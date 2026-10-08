# Target Platform

The boards, how they are set up, and how they are connected.

---

## Boards

### NVIDIA Jetson Orin Nano Developer Kit

| | |
| --- | --- |
| Model element | [`part orin : JetsonOrinNano`](../sysmlv2/SeedImaging.sysml#L102) |
| OS | Ubuntu 22.04 |
| Middleware | ROS 2 Humble |
| Runs | `jetson_scan`, the generated ROS 2 node, plus the micro-ROS agent |
| Bound by | [`allocation jetsonOnOrin`](../sysmlv2/SeedImaging.sysml#L120) |

The micro-ROS agent runs in Docker from `microros/micro-ros-agent:humble`. There is no
source-built agent on this machine, which is why the ROS 2 half is launched on its own
rather than through a combined hardware launch file.

### Arduino Nano ESP32

| | |
| --- | --- |
| Model element | [`part nano : ArduinoNanoESP32`](../sysmlv2/SeedImaging.sysml#L103) |
| MCU | ESP32-S3 |
| RTOS | FreeRTOS, via the Arduino core |
| Library | `micro_ros_arduino` 2.0.8-humble |
| Runs | `esp32_stepper`, the generated micro-ROS node |
| Bound by | [`allocation firmwareOnNano`](../sysmlv2/SeedImaging.sysml#L122) |

Firmware is compile-checked against Espressif core 2.0.17 and flashed using
**Arduino ESP32 Boards → Arduino Nano ESP32** (the 2.0 line). Build footprint with the
as-built pin map: 434,421 B flash, 56,572 B RAM.

### Motors and drivers

Three NEMA 17 bipolar steppers, 200 full steps per revolution, driven through L298N
H-bridge modules on a shared 12 V rail.

---

## Connections

```
  operator
     │  ros2 topic pub /scan/start
     ▼
┌─────────────────────┐
│ Jetson Orin Nano    │
│  jetson_scan (C++)  │
│  micro-ROS agent    │
└──────────┬──────────┘
           │ USB serial, /dev/ttyACM0, XRCE-DDS
┌──────────▼──────────┐
│ Arduino Nano ESP32  │
│  esp32_stepper (C)  │
└──────────┬──────────┘
           │ IN1-IN4 per driver
┌──────────▼──────────┐      ┌──────────────┐
│ 3 × L298N           │◄─────┤ 12 V supply  │
└──────────┬──────────┘      └──────────────┘
           │
   3 × NEMA 17
```

The Jetson-to-ESP32 link is the transport for all six modelled connections. Topic names
on the ports are what bind the model's connections to the ROS graph.

---

## Wiring

As built. Four signal pins per motor, ESP32 to the driver's IN1–IN4.

| Driver | Axis | IN1 | IN2 | IN3 | IN4 |
| --- | --- | --- | --- | --- | --- |
| 1 | Z, turntable | D13 | D12 | A0 | A1 |
| 2 | X, tilt | D8 | D9 | D10 | D11 |
| 3 | Y, reserved | D7 | D6 | D5 | D4 |

**B0 is deliberately unused.** It is a boot-strapping pin on the ESP32-S3 and driving it
prevents normal startup.

**The `+5V` terminal on each L298N must be empty.** Wiring it back to the ESP32 feeds
current into the board and causes it to brown out and reset continuously. The diagnostic
is a red LED lit on a driver while the 12 V supply is disconnected.

Grounds are common between the ESP32, all three drivers, and the 12 V supply. Never
disconnect the shared ground while the 12 V rail is live.

The board-to-axis assignment above is physical and was established by running
`firmware/MotorTest` and labelling the motors by which key moved them. If motors are
re-seated, that mapping must be re-established before the pin table can be trusted.

---

## Setup

### Jetson

ROS 2 Humble, Docker, and the agent image:

```
sudo docker pull microros/micro-ros-agent:humble
```

Only one agent may run at a time. Two agents will both claim `/dev/ttyACM0` and fight
over it, producing a connection that appears and disappears roughly once per second.
Check before starting:

```
sudo docker ps
```

Connect the ESP32 directly to a port on the Jetson rather than through a hub.

### Development machine

Sireum HAMR `4.20260810.80aad0c2`, installed side by side so an existing installation is
not destroyed:

```
DISTRO=codeive SIREUM_V=4.20260810.80aad0c2 DIR=$HOME/Applications/Sireum-4.20260810/Sireum \
  sh -c "$(curl -fsSL https://github.com/sireum/kekinian/releases/download/4.20260810.80aad0c2/install.cmd)"
```

```
export SIREUM_HOME=$HOME/Applications/Sireum-4.20260810/Sireum
```

The `DISTRO=codeive` distribution includes CodeIVE, used for the SysMLv2 type check.

Arduino IDE with the Arduino ESP32 Boards package and `micro_ros_arduino`.

---

## Operating notes

**Positions reset on every ESP32 boot.** There are no homing switches, so
[`SI-MCU-1`](../sysmlv2/SeedImaging.sysml#L230) — all axes at home at power-up — is an
assumption about the deployment rather than a sensed fact. Level the tilt by hand before
resetting, or the ±45° limit is enforced about the wrong zero.

This is a known weakness and the honest reading of that clause: the contract is sound,
but what makes it true in the physical system is an operating procedure rather than the
hardware.

**Power-on order.** 12 V first, with the driver chips checked by hand for heat over the
first fifteen seconds, then USB, then the agent, then the ROS 2 node.

**Thermal.** The L298N has no current limiting, so the motors and driver chips run at full
current whenever energised. Continuous stepping without pause is outside what this bench
setup is intended for.
