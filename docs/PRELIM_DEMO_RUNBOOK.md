# Prelim demo runbook

The adviser asked for three things:

1. The ROS ↔ micro-ROS connection works.
2. The 3 stepper motors are controlled through it.
3. **Most important: SysML v2 integration.**

The one sentence that ties them together:

> *The SysML v2 model is the single source of truth. HAMR type-checks it, proves the component logic against its contracts, and generates the ROS 2 and micro-ROS code that now moves the three motors. The same contracts are then checked on the running hardware.*

Every command in §2 and §3 was dry-run against the simulation (generated nodes, real micro-ROS agent over UDP); the output is in `docs/evidence/runbook_dry_run_output.txt`.

---

## 1. Before prelim day (do this once, then dry-run it)

### Mac (model and proofs)
Sireum must be build **4.20260810.80aad0c2**, because `sysmlv2/aadl-lib` and the generated code match it. Check an existing install first:
```bash
~/Applications/Sireum/bin/sireum | grep Build        # "Build 4.20260810.80aad0c2" = nothing to install
```
Otherwise install that release **next to** the current one. The installer deletes whatever folder `DIR` names, so give it a new folder:
```bash
DISTRO=codeive SIREUM_V=4.20260810.80aad0c2 DIR=$HOME/Applications/Sireum-4.20260810/Sireum \
  sh -c "$(curl -fsSL https://github.com/sireum/kekinian/releases/download/4.20260810.80aad0c2/install.cmd)"
```
Then run every check:
```bash
export SIREUM_HOME=$HOME/Applications/Sireum-4.20260810/Sireum   # (or ~/Applications/Sireum if it already is that build) add to ~/.zshrc
cd SeedImaging_SysMLv2
bin/verify.sh | tee docs/verify_output.txt     # 2-5 min (the first run also downloads Scala libraries); must end with ALL CHECKS PASSED
```
Save a screenshot of the Logika and test output as a backup slide, in case the room has no time for a live run.
Quit any open CodeIVE, start this one with `open "$SIREUM_HOME/bin/mac/vscodium/CodeIVE.app"`, open the `sysmlv2/` folder, and check that `SeedImaging.sysml` shows no errors.

### Mac (firmware)
Follow `firmware/README.md` (or `ROS2_Stepper_Build_Guide_2.docx` §2 and §5): run `firmware/MotorTest` once to check that `z`, `x` and `y` move the turntable, the tilt and the Y motor, then copy the library, open the sketch, set the board to **Arduino Nano ESP32**, and upload. The pin map matches the board as built (Motor 1 board D13 D12 A0 A1 = Z, Motor 2 board D8–D11 = X, Motor 3 board D7 D6 D5 D4 = Y).

### Jetson (ROS 2 side)
```bash
# copy the whole SeedImaging_SysMLv2 folder (about 10 MB) to the Jetson, then:
source /opt/ros/humble/setup.bash
sudo docker image ls microros/micro-ros-agent   # the agent image from part 1 (humble) must be listed
cd SeedImaging_SysMLv2/hamr/ros2
colcon build --packages-skip seed_imaging_microros_pkg
echo "source $PWD/install/setup.bash" >> ~/.bashrc
```
The agent runs in Docker, as in `ROS2_Stepper_Build_Guide_2.docx` (§3.2). If you ever build the agent from source instead, source its workspace before `colcon build`; then `seed_imaging_hardware.launch.py` can start the agent and the ScanController together.
`--packages-skip` matters. `microros_apps/` holds the host build of the ESP32 node, which needs `rclc`. On a plain ROS 2 Humble install, a bare `colcon build` fails on it and aborts the Jetson node too (checked on a clean copy). The ESP32 runs that node from the flashed firmware, so the Jetson does not need it.
Run the `ros2 …` commands below in terminals that have this setup sourced, and the Python checker from the `SeedImaging_SysMLv2` folder.

---

## 2. Bench bring-up on the day

Power-on order, as in `ROS2_Stepper_Build_Guide_2.docx` (§6): **Jetson → ESP32 USB-C → 12 V supply.** Level the tilt by hand before the 12 V goes on: the ESP32 counts every axis from where it is at start-up.

```bash
# Terminal 1: the micro-ROS agent on the ESP32's serial port (ls /dev/ttyACM* first)
sudo docker run -it --rm -v /dev:/dev --privileged --net=host microros/micro-ros-agent:humble serial --dev /dev/ttyACM0 -v4
```
`session established`, then `participant created` and a few `topic created` / `datawriter created` lines, mean the ESP32 has connected. If nothing appears within 10 s, press RST on the ESP32 once.

```bash
# Terminal 2: the generated ScanController (the ROS 2 half)
ros2 launch seed_imaging_cpp_pkg_bringup SeedImagingSystem_Instance_ros2.launch.py
```
Lines tagged `[jetson_scan_exe-1]` come from the generated ScanController, which logs every acknowledgement.
(Agent built from source instead of Docker: `ros2 launch seed_imaging_cpp_pkg_bringup seed_imaging_hardware.launch.py` starts both in Terminal 1; add `dev:=/dev/ttyACM1` if needed.)

```bash
# Terminal 3
ros2 node list            # expect /esp32_stepper and /jetson_scan
```
Run this check well before Part A. The first `ros2` command starts the ROS 2 daemon, and for a second or two after that, `ros2 topic list` can come back incomplete and `ros2 topic info` can say `Unknown topic`. The dry run hit this once. If it happens, run the command again.

---

## 3. The demo (~10 minutes)

### Part A: the ROS 2 ↔ micro-ROS connection (1 min)
```bash
ros2 node list
ros2 topic list                                  # /stepper/{z,x,y}/{cmd,pos}, /scan/start
ros2 topic info /stepper/z/cmd -v                # publisher jetson_scan, subscriber esp32_stepper
ros2 topic echo /stepper/x/pos                   # Terminal 4: keep it open
```
Say: *"`esp32_stepper` is a micro-ROS node running on the ESP32. The agent bridges it into the ROS 2 graph over USB serial."*

### Part B: the 3 steppers under ROS 2 control (3 min)
```bash
ros2 topic pub --once /stepper/z/cmd std_msgs/msg/Int32 "{data: 50}"    # turntable: quarter turn       -> ack 50
ros2 topic pub --once /stepper/x/cmd std_msgs/msg/Int32 "{data: 25}"    # tilt to +45 deg               -> ack 25
ros2 topic pub --once /stepper/y/cmd std_msgs/msg/Int32 "{data: 100}"   # Y axis: half turn             -> ack 100
ros2 topic pub --once /stepper/x/cmd std_msgs/msg/Int32 "{data: 10}"    # would reach +63 deg: REJECTED -> no motion, ack stays 25
ros2 topic pub --once /stepper/x/cmd std_msgs/msg/Int32 "{data: -25}"   # back to level                 -> ack 0
ros2 topic pub --once /scan/start    std_msgs/msg/Int32 "{data: 1}"     # automatic self-test of all three motors
```
Each acknowledgement is the axis position after the move. Z and Y count 0–199 per revolution, so a full turn comes back to the same number. `--once` waits until the ESP32's subscription is matched, so a command is not lost.
Say for the rejected command: *"That is requirement SI-MCU-9 from the model. The ESP32 refuses any tilt beyond ±45°."*
The self-test runs six moves: Z one turn, tilt +45°, tilt −45°, back to level, Y one turn, Z back. Then `{data: 2}` runs the 18-move imaging scan: two rings of 8 views, at level and at +45°. Terminal 2 shows `plan move k/n` and each acknowledgement.
Put the tilt back to level before `/scan/start`. A plan whose next tilt move would pass ±45° stops instead of moving (SI-HOST-4), and that stop is final (§5).

### Part C: SysML v2 integration (5 min). This is the part to spend time on.
1. **The model** (CodeIVE, `sysmlv2/SeedImaging.sysml`). Show:
   * the system with two processors and two processes, each bound to its processor
   * `ScanController` with `Ros_Node_Kind = ros2`, and `StepperController` with `Ros_Node_Kind = microRos`
   * `Ros_Topic_Name = "/stepper/x/cmd"`, the topic you just used
   * one contract, e.g. `SI_MCU_2_tiltNeverBeyond45` or `SI_HOST_3_oneMoveInFlight`
2. **The model checks itself.** In CodeIVE, open the command palette (View ▸ Command Palette), type `HAMR`, and run:
   * **HAMR Type Checking** → no problems (command line: `bin/hamr.sh tipe` → `Well-formed!`)
   * **HAMR SysMLv2 Logika Checking** → `Integration constraints verified!`. The ESP32's promise about tilt acks (SI-MCU-16) covers what the Jetson assumes (SI-HOST-A1) on the `xPos` connection.
3. **The code comes from the model.**
   ```bash
   grep -rn '"/stepper/x/cmd"' hamr/ros2      # the same topic, in the generated rclcpp and rclc code
   ```
   Point out what was generated and what was hand-written. Generated: nodes, topics, types, executor, launch files, middleware sizing. Hand-written: only the marked user code. The model's limits reach the C code through `si_model_constants.h`.
4. **Proof and tests.** Show `docs/verify_output.txt` from §1 (a reference run is in `docs/evidence/verify_output.txt`), or run `bin/verify.sh` if there is time:
   * `Logika verified!`: the component logic is *proved* against every contract.
   * The three kinds of tests from the HAMR tutorials, 2,895 in all: 16 manual unit tests, 59 manual GUMBOX tests (one or more per contract clause, named after it), and 2,820 property-based GUMBOX tests. Then ~17.8 M contract checks of the deployed C/C++ logic.
   * **GUMBOX oracle, in one click path:** open `hamr/slang/src/main/bridge/seedimaging/SeedImaging/StepperController_esp32_stepper_GumboX.scala` and show `compute_handle_xCmd_SI_MCU_9_xRejectUnsafe_guarantee`. That is the model's SI-MCU-9 clause, generated as executable code that every GUMBOX test checks.
   * **Seeded bug (optional, about 2 min):** `bin/seeded_bug_demo.sh esp32` drops the ±45° check in a scratch copy. The GUMBOX tests and Logika both fail, and the script ends with `RESULT: the seeded bug was caught`. The bug is kept as a comment in `StepperController_esp32_stepper.scala`. `bin/seeded_bug_demo.sh model` does the same for a mismatched integration constraint. A recorded run is in `docs/evidence/seeded_bug_demo_output.txt`.
5. **Close the loop on the hardware.**
   ```bash
   python3 tests/e2e/e2e_contract_check.py     # 34 checks: live hardware vs. the model's contracts
   ```
   It first returns all three axes to 0, then drives each axis and runs both plans (about 30 s). `jetson_scan` must not be halted.
   Say: *"The same contracts that Logika proved are now checked against the running motors."*

---

## 4. Questions to expect

| Question | Answer |
|---|---|
| Is the code really generated from the model? | Yes. The node structure, topics, message types, dispatch semantics, launch files and micro-ROS entity pools all come from HAMR. Only the decision logic is hand-written, inside HAMR's preserved regions, and that logic is proved against the model's contracts. |
| How do you know the C code matches the proved Slang? | It is a line-for-line transcription. It is then checked exhaustively against the same contracts (17.8 M checks) and again end to end on ROS 2 / hardware. |
| Why `std_msgs/Int32` and not custom messages? | The precompiled `micro_ros_arduino` already contains `std_msgs`; custom messages would mean rebuilding it. HAMR's *platform-provided types* keep the model typed (`zCmd.data`), and any ROS user can drive the motors from the CLI. |
| What happened to the first model? | HAMR could not load it (reserved word `state`), and its Microkit target cannot generate a two-processor Jetson + ESP32 system. The audit is in `docs/SysMLv2_Verification_Report.md` §2. |
| Why HAMR instead of Cameo / the Pilot Implementation? | HAMR makes the SysML v2 model *executable*: it generates code for ROS 2 and micro-ROS and verifies contracts. That is the "integration" part. Standard SysML v2 requirements and `satisfy` relations are kept alongside (`sysml-trace/`). |
| Timing? | Step interval (5 ms), settle (80 ms) and the worst-case move time (2080 ms, as `Compute_Execution_Time`) are in the model. Measuring settle time and trigger latency is the RPE evaluation plan. |
| What is next? | Add the camera node to the model (triggered by the acknowledgements), then contour detection and volume carving as ROS 2 nodes, all generated the same way. |

---

## 5. If something goes wrong

| Symptom | Fix |
|---|---|
| `colcon build` fails on `seed_imaging_microros_pkg` (`rclc` not found) | Build with `--packages-skip seed_imaging_microros_pkg` (§1). |
| `package 'micro_ros_agent' not found` when launching | That is `seed_imaging_hardware.launch.py`, which needs a source-built agent. With the Docker agent, launch `SeedImagingSystem_Instance_ros2.launch.py` instead (§2). |
| `/esp32_stepper` does not appear | Run `ls /dev/ttyACM*` and give the agent the right `--dev`. Replug the USB-C cable (it must be a data cable), or try another USB port on the Jetson. Press RST on the ESP32: it waits for the agent and connects within about a second. Close any serial monitor that holds the port. |
| A motor hums but doesn't turn | Its coil wires are crossed. With 12 V unplugged, swap OUT3 and OUT4 on that board (`ROS2_Stepper_Build_Guide_2.docx` §2). |
| A motor turns the wrong way | Swap the two wires of one coil. |
| The wrong motor moves (e.g. `/stepper/z/cmd` tilts the tray) | The board-to-axis order differs from the pin map. Swap the rows in `firmware/SeedImagingStepper/board.cpp` (and `MotorTest.ino`), upload again. |
| `scan halted: the next tilt move would leave +/-45 deg`, then `start ignored: scan is halted` | A manual command left the tilt off level (e.g. at +45°) before the plan started. The halt is final by design (SI-HOST-8). Level the tilt with a manual X command, then restart the launch (Ctrl-C and relaunch). |
| `start ignored: a move is still in flight` | Wait for the acknowledgement, or restart the launch. |
| No hardware at all | Run the simulation from `tests/e2e/run_e2e_sim.sh`. It needs a micro-ROS host workspace (`MICROROS_WS`, see `hamr/ros2/readme.md`). |
