# ESP32 firmware (Arduino Nano ESP32)

The firmware is the HAMR-generated micro-ROS node `esp32_stepper` (StepperController) from `sysmlv2/SeedImaging.sysml`. The sketch adds only what is specific to the board:

| File | Role |
|---|---|
| `SeedImagingStepperNode/` | Arduino library. It holds the generated node (`esp32_stepper_base_src.c/.h`), its user code (`esp32_stepper_src.c`), the decision logic (`stepper_logic.h`) and the model's constants (`si_model_constants.h`). **Do not edit it by hand.** It is refreshed by `sync_from_hamr.sh`, which `bin/hamr.sh` calls. |
| `SeedImagingStepper/SeedImagingStepper.ino` | `setup()` / `loop()`: serial transport, wait for the agent, init, spin, reboot if the agent is lost |
| `SeedImagingStepper/board.cpp` | Pin map (the board as built, `ROS2_Stepper_Build_Guide_2.docx` §1.2), GPIO hooks, logging off, safe state before reboot |
| `MotorTest/MotorTest.ino` | Plain motor test without ROS 2 (build guide §2): type `z`, `x` or `y` in the Serial Monitor and that motor turns 10 steps and back. Same pin table and step pattern as the firmware. |

Compile check (arduino-cli, esp32 core 2.0.17, `micro_ros_arduino` 2.0.8-humble): **434,421 bytes of flash (13%), 56,572 bytes of RAM (17%), no warnings.** `MotorTest`: 286,849 bytes of flash, no warnings.

**Pin map (as built):** Motor 1 board = Z turntable = D13, D12, A0, A1 · Motor 2 board = X tilt = D8, D9, D10, D11 · Motor 3 board = Y = D7, D6, D5, D4 (IN1 to IN4 in that order). B0 is never used. If `MotorTest` shows the wrong motor on a letter, swap the rows in `board.cpp` and `MotorTest.ino`.

## Flash it (Arduino IDE on the Mac)

1. Once, if part 1 is not already working on this Mac: add the `micro_ros_arduino` **v2.0.8-humble** zip (Sketch ▸ Include Library ▸ Add .ZIP Library), then copy its `esp32` folder to `esp32s3`: `cd ~/Documents/Arduino/libraries/micro_ros_arduino*/src && cp -R esp32 esp32s3`.
2. Copy the folder `SeedImagingStepperNode` into `~/Documents/Arduino/libraries/`.
3. Open `SeedImagingStepper/SeedImagingStepper.ino`. Select **Tools ▸ Board ▸ Arduino ESP32 Boards ▸ Arduino Nano ESP32** (Arduino's 2.0.18 package, not the esp32 3.3.12 one; the compile check used the 2.0 line) and the `/dev/cu.usbmodem…` port.
4. Upload. Close the Serial Monitor afterwards: the USB serial link carries micro-ROS traffic.
5. Plug the ESP32 into the Jetson and start the agent: `sudo docker run -it --rm -v /dev:/dev --privileged --net=host microros/micro-ros-agent:humble serial --dev /dev/ttyACM0 -v4` (or `seed_imaging_hardware.launch.py` if the agent is built from source). About a second later, `ros2 node list` shows `/esp32_stepper`.

## Behaviour worth knowing before the demo

* **One step = 1.8°, so 200 steps = one revolution.** The old test sketch in `StepByStep_SeedImaging_Guide.docx` (Step 7) wrote all four phases per loop iteration. Its `stepMotor(..., 200)` therefore made **800** steps (4 revolutions). The firmware here makes exactly the commanded number of steps.
* **Speed and settle time come from the model:** 5 ms per step (200 steps/s) and 80 ms settle before the acknowledgement. A maximal command (400 steps) takes 2.08 s.
* **Coils are released after every move.** The L298N has no current limiting, so this keeps the drivers cool. The tilt axis therefore has no holding torque between moves. If the tray back-drives the tilt motor, tell me and we can hold the X coils instead.
* **Out-of-limit commands do nothing and are still acknowledged:** more than 400 steps on any axis, or a tilt beyond ±45°. The ack carries the unchanged position.
* **If the agent goes away,** the ESP32 turns every coil off, reboots, and waits for the agent again.
* **D13 is also the on-board LED,** so it flickers while the Z axis (Motor 1 board) moves. That is harmless.
* **If a motor buzzes but does not turn,** its coil wires are crossed: with 12 V unplugged, swap OUT3 and OUT4 on that board (`ROS2_Stepper_Build_Guide_2.docx` §2).
