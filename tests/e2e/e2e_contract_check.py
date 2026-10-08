#!/usr/bin/env python3
"""
End-to-end check of the HAMR-generated ROS 2 + micro-ROS system.

Runs against the live system (host simulation or the real Jetson + ESP32):
  * part 1 drives each motor directly over ROS 2 -> micro-ROS and checks every
    acknowledgement against the StepperController contracts (SI-MCU-*)
  * part 2 starts the self-test and the imaging scan through /scan/start and
    checks the observed traffic against the ScanController contracts (SI-HOST-*)

Usage (with the system already running):
    python3 e2e_contract_check.py            # everything
    python3 e2e_contract_check.py --manual   # part 1 only
"""
import sys
import time
import threading

import rclpy
from rclpy.node import Node
from std_msgs.msg import Int32

STEPS_PER_REV = 200
MAX_MOVE = 400
TILT_LIMIT = 25

SELF_TEST = [("z", 200), ("x", 25), ("x", -50), ("x", 25), ("y", 200), ("z", -200)]
SCAN = [("z", 25)] * 8 + [("x", 25)] + [("z", 25)] * 8 + [("x", -25)]


def wrap(p):
    return ((p % STEPS_PER_REV) + STEPS_PER_REV) % STEPS_PER_REV


class Recorder(Node):
    def __init__(self):
        super().__init__("e2e_contract_check")
        self.lock = threading.Lock()
        self.events = []          # (time, kind, axis, value)  kind = 'cmd' | 'pos'
        self.pubs = {}
        for ax in ("z", "x", "y"):
            self.create_subscription(Int32, f"/stepper/{ax}/cmd", self._cb("cmd", ax), 10)
            self.create_subscription(Int32, f"/stepper/{ax}/pos", self._cb("pos", ax), 10)
            self.pubs[ax] = self.create_publisher(Int32, f"/stepper/{ax}/cmd", 10)
        self.start_pub = self.create_publisher(Int32, "/scan/start", 10)

    def _cb(self, kind, ax):
        def cb(msg):
            with self.lock:
                self.events.append((time.monotonic(), kind, ax, msg.data))
        return cb

    def snapshot(self):
        with self.lock:
            return list(self.events)


def spin_until(node, pred, timeout):
    end = time.monotonic() + timeout
    while time.monotonic() < end:
        rclpy.spin_once(node, timeout_sec=0.05)
        if pred():
            return True
    return False


def wait_for_graph(node, timeout=30.0):
    def ready():
        return (node.count_subscribers("/stepper/z/cmd") >= 2 and   # esp32_stepper + this recorder
                node.count_subscribers("/stepper/x/cmd") >= 2 and
                node.count_subscribers("/stepper/y/cmd") >= 2 and
                node.count_publishers("/stepper/z/pos") >= 1)
    return spin_until(node, ready, timeout)


class Checker:
    def __init__(self):
        self.failures = []
        self.passes = 0

    def check(self, cond, what):
        if cond:
            self.passes += 1
            print(f"  PASS  {what}")
        else:
            self.failures.append(what)
            print(f"  FAIL  {what}")


def move_and_wait(node, ax, steps, timeout=15.0):
    """Publish one relative move and return the acknowledged position (or None)."""
    before = len(node.snapshot())
    node.pubs[ax].publish(Int32(data=steps))
    got = []

    def acked():
        acks = [e for e in node.snapshot()[before:] if e[1] == "pos" and e[2] == ax]
        if acks:
            got.append(acks[0][3])
            return True
        return False
    return got[0] if spin_until(node, acked, timeout) else None


def home_all(node):
    """A 0-step move is always accepted and answered with the current position
    (SI-MCU-4/8/12 with steps = 0), so it doubles as a position query.  Bring
    every axis back to 0 so the scenario below starts from home."""
    print("\n=== Homing: query each axis with a 0-step move, then return it to 0 ===")
    pos = {}
    for ax in ("z", "x", "y"):
        cur = move_and_wait(node, ax, 0)
        if cur is None:
            print(f"  no answer from /stepper/{ax}/pos -- is the ESP32 node running?")
            sys.exit(2)
        if cur != 0:
            back = -cur if (ax == "x" or cur <= STEPS_PER_REV // 2) else STEPS_PER_REV - cur
            cur = move_and_wait(node, ax, back)
        print(f"  {ax}: at {cur}")
        pos[ax] = cur
    spin_until(node, lambda: False, 0.5)
    return pos


def manual_part(node, chk, pos):
    print("\n=== Part 1: ROS 2 -> micro-ROS, each motor commanded directly ===")
    cases = [
        ("z", 50,   "Z +50"),
        ("x", 25,   "X +25 (to +45 deg)"),
        ("x", 1,    "X +1 beyond +45 deg -> rejected"),
        ("x", -25,  "X -25 (back to level)"),
        ("y", -1,   "Y -1 (wraps to 199)"),
        ("z", 401,  "Z +401 oversize -> rejected"),
        ("z", -50,  "Z -50 (back home)"),
        ("y", 1,    "Y +1 (back home)"),
    ]
    for ax, steps, label in cases:
        before = len(node.snapshot())
        node.pubs[ax].publish(Int32(data=steps))
        ok = spin_until(node, lambda: any(e[1] == "pos" and e[2] == ax for e in node.snapshot()[before:]), 10.0)
        acks = [e for e in node.snapshot()[before:] if e[1] == "pos"]
        # expected position from the model's contracts
        cur = pos[ax]
        if ax == "x":
            accepted = abs(steps) <= MAX_MOVE and abs(cur) <= TILT_LIMIT and abs(cur + steps) <= TILT_LIMIT
            expect = cur + steps if accepted else cur
        else:
            accepted = abs(steps) <= MAX_MOVE
            expect = wrap(cur + steps) if accepted else cur
        pos[ax] = expect
        chk.check(ok, f"{label}: acknowledgement received")
        if ok:
            chk.check(len(acks) == 1 and acks[0][2] == ax,
                      f"{label}: exactly one ack, on /stepper/{ax}/pos only (SI-MCU-7/11/15)")
            chk.check(acks[0][3] == expect, f"{label}: position {acks[0][3]} == expected {expect}")
        spin_until(node, lambda: False, 0.3)   # let stray acks reach the scan controller


def plan_part(node, chk, pos, selection, plan, label):
    print(f"\n=== Part 2: /scan/start {selection} ({label}, {len(plan)} moves) ===")
    before = len(node.snapshot())
    node.start_pub.publish(Int32(data=selection))
    t0 = time.monotonic()
    # also try to start again while the plan runs: must be ignored (SI-HOST-11)
    spin_until(node, lambda: False, 0.5)
    node.start_pub.publish(Int32(data=selection))

    def done():
        ev = [e for e in node.snapshot()[before:]]
        return sum(1 for e in ev if e[1] == "pos") >= len(plan)
    ok = spin_until(node, done, 120.0)
    spin_until(node, lambda: False, 1.0)       # make sure nothing else follows
    ev = node.snapshot()[before:]
    cmds = [(e[2], e[3]) for e in ev if e[1] == "cmd"]
    acks = [(e[2], e[3]) for e in ev if e[1] == "pos"]
    chk.check(ok, f"{label}: all {len(plan)} moves acknowledged ({time.monotonic() - t0:.1f} s)")
    chk.check(cmds == plan, f"{label}: commands follow the plan exactly, no extra move from the 2nd start")

    # one move in flight (SI-HOST-3/6): move k+1 is only commanded once move k is
    # acknowledged.  This observer receives /cmd and /pos over different paths
    # (DDS vs. the micro-ROS agent), so their arrival order can be skewed by a few
    # ms; a genuine violation would show up as a whole move-time (>= 125 ms) early.
    cmd_t = [e[0] for e in ev if e[1] == "cmd"]
    ack_t = [e[0] for e in ev if e[1] == "pos"]
    skew = 0.0
    in_flight_ok = len(cmd_t) == len(ack_t) == len(plan)
    if in_flight_ok:
        for k in range(len(plan)):
            if ack_t[k] < cmd_t[k] - 0.005:
                in_flight_ok = False
            if k + 1 < len(plan):
                skew = max(skew, ack_t[k] - cmd_t[k + 1])
                if cmd_t[k + 1] < ack_t[k] - 0.020:
                    in_flight_ok = False
    chk.check(in_flight_ok, f"{label}: one move in flight at a time (largest observer skew {skew * 1000:.1f} ms)")

    # acknowledged positions match the model (SI-MCU-4/8/12) and tilt stays in +/-45 deg
    exp_ok = True
    for (ax, steps), (aax, apos) in zip(plan, acks):
        cur = pos[ax]
        expect = cur + steps if ax == "x" else wrap(cur + steps)
        pos[ax] = expect
        if aax != ax or apos != expect:
            exp_ok = False
            print(f"        mismatch: {ax}{steps:+d} -> ack {aax}={apos}, expected {expect}")
    chk.check(exp_ok, f"{label}: every acknowledged position matches the model")
    chk.check(all(abs(p) <= TILT_LIMIT for a, p in acks if a == "x"), f"{label}: tilt never beyond +/-45 deg (SI-MCU-2)")


def main():
    manual_only = "--manual" in sys.argv
    rclpy.init()
    node = Recorder()
    chk = Checker()
    print("waiting for the ROS 2 graph (micro-ROS node + scan controller) ...")
    if not wait_for_graph(node):
        print("FAIL: the micro-ROS node did not appear on /stepper/*/cmd")
        sys.exit(2)
    spin_until(node, lambda: False, 1.0)
    pos = home_all(node)
    manual_part(node, chk, pos)
    if not manual_only:
        plan_part(node, chk, pos, 1, SELF_TEST, "self-test")
        plan_part(node, chk, pos, 2, SCAN, "imaging scan")
    print(f"\n{chk.passes} checks passed, {len(chk.failures)} failed")
    node.destroy_node()
    rclpy.shutdown()
    sys.exit(0 if not chk.failures else 1)


if __name__ == "__main__":
    main()
