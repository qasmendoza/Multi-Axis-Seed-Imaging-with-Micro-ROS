# System Architecture

Every heading below links into [`sysmlv2/SeedImaging.sysml`](../sysmlv2/SeedImaging.sysml)
at the element it describes.

---

## What the system does

A seed sits on a turntable. A camera observes it while two rotational axes change the
viewing direction and a third axis positions the seed. Images are later carved into a
visual hull to estimate volume non-destructively.

This repository covers the motion and control architecture. The imaging path is not yet
part of the model.

---

## Top level

[`part def SeedImagingSystem`](../sysmlv2/SeedImaging.sysml#L99) holds two processors,
two processes, six connections, and two deployment bindings.

| Element | Line | Role |
| --- | --- | --- |
| [`part orin : JetsonOrinNano`](../sysmlv2/SeedImaging.sysml#L102) | 102 | Host processor |
| [`part nano : ArduinoNanoESP32`](../sysmlv2/SeedImaging.sysml#L103) | 103 | MCU processor |
| [`part jetson : JetsonSoftware`](../sysmlv2/SeedImaging.sysml#L106) | 106 | ROS 2 process |
| [`part esp32 : Esp32Firmware`](../sysmlv2/SeedImaging.sysml#L107) | 107 | micro-ROS process |

Processor definitions: [`JetsonOrinNano`](../sysmlv2/SeedImaging.sysml#L126) and
[`ArduinoNanoESP32`](../sysmlv2/SeedImaging.sysml#L127).

### Connections

Commands flow host to MCU; acknowledgements flow back.

| Connection | Line | Direction |
| --- | --- | --- |
| [`zCmd`](../sysmlv2/SeedImaging.sysml#L110) | 110 | `jetson.zCmd` → `esp32.zCmd` |
| [`xCmd`](../sysmlv2/SeedImaging.sysml#L111) | 111 | `jetson.xCmd` → `esp32.xCmd` |
| [`yCmd`](../sysmlv2/SeedImaging.sysml#L112) | 112 | `jetson.yCmd` → `esp32.yCmd` |
| [`zPos`](../sysmlv2/SeedImaging.sysml#L115) | 115 | `esp32.zPos` → `jetson.zPos` |
| [`xPos`](../sysmlv2/SeedImaging.sysml#L116) | 116 | `esp32.xPos` → `jetson.xPos` |
| [`yPos`](../sysmlv2/SeedImaging.sysml#L117) | 117 | `esp32.yPos` → `jetson.yPos` |

The `xPos` connection is the one carrying an integration constraint pair. See
[Integration-Constraints.md](Integration-Constraints.md).

### Deployment

| Binding | Line |
| --- | --- |
| [`allocation jetsonOnOrin`](../sysmlv2/SeedImaging.sysml#L120) | 120 |
| [`allocation firmwareOnNano`](../sysmlv2/SeedImaging.sysml#L122) | 122 |

Both are `Deployment_Properties::Actual_Processor_Binding`. They determine which HAMR
back-end translation applies to each process.

---

## Processes and threads

[`part def JetsonSoftware :> Process`](../sysmlv2/SeedImaging.sysml#L134) contains
[`part scan : ScanController`](../sysmlv2/SeedImaging.sysml#L142) and the six connections
at [lines 144–149](../sysmlv2/SeedImaging.sysml#L144) that route its ports to the process
boundary.

[`part def Esp32Firmware :> Process`](../sysmlv2/SeedImaging.sysml#L153) contains
[`part stepper : StepperController`](../sysmlv2/SeedImaging.sysml#L161) and the matching
connections at [lines 163–168](../sysmlv2/SeedImaging.sysml#L163).

### ScanController

[`part def ScanController :> Thread`](../sysmlv2/SeedImaging.sysml#L317)

Sporadic dispatch, `Ros_Node_Kind = ros2`. Owns the scan plan: it issues one move at a
time, waits for the acknowledgement, and advances.

Ports: `start` (`/scan/start`), out `zCmd` `xCmd` `yCmd`, in `zPos` `xPos` `yPos`.

`start` is **intentionally unconnected in the model**. The operator is off-model; the
`Ros_Topic_Name` attribute is what binds it at deployment.

### StepperController

[`part def StepperController :> Thread`](../sysmlv2/SeedImaging.sysml#L181)

Sporadic dispatch, `Ros_Node_Kind = microRos`,
`Compute_Execution_Time = 0 [ms] .. 2080 [ms]`. The bound is derived in the model from a
maximal move: 400 steps at 5 ms plus an 80 ms settle.

Ports: in `zCmd` `xCmd` `yCmd`, out `zPos` `xPos` `yPos`, all `EventDataPort` carrying
`std_msgs::Int32`.

---

## Topics

Topic names are carried on the ports as `Ros_Topic_Name` attributes, so the model is the
single source of truth for the ROS interface.

| Topic | Type | Direction | Meaning |
| --- | --- | --- | --- |
| `/scan/start` | `std_msgs::Int32` | operator → Jetson | 1 self-test, 2 imaging scan, other = both |
| `/stepper/z/cmd` | `std_msgs::Int32` | Jetson → ESP32 | turntable, **relative** steps, sign is direction |
| `/stepper/x/cmd` | `std_msgs::Int32` | Jetson → ESP32 | tilt, relative steps |
| `/stepper/y/cmd` | `std_msgs::Int32` | Jetson → ESP32 | Y axis, relative steps |
| `/stepper/z/pos` | `std_msgs::Int32` | ESP32 → Jetson | turntable position after the command |
| `/stepper/x/pos` | `std_msgs::Int32` | ESP32 → Jetson | tilt position after the command |
| `/stepper/y/pos` | `std_msgs::Int32` | ESP32 → Jetson | Y position after the command |

Commands are **relative**; positions are **absolute**. An out-of-range command is
**refused, not clamped**: the axis does not move and the current position is still
acknowledged. Clamping would silently move an axis somewhere that was not requested.

---

## The GUMBO library

Four shared predicates, used across both components' contracts:

| Function | Line | Meaning |
| --- | --- | --- |
| [`isBoundedMove`](../sysmlv2/SeedImaging.sysml#L75) | 75 | `|steps| <= MAX_MOVE_STEPS` |
| [`isTiltSafe`](../sysmlv2/SeedImaging.sysml#L78) | 78 | tilt position within ±`TILT_LIMIT_STEPS` |
| [`isRotaryIndex`](../sysmlv2/SeedImaging.sysml#L82) | 82 | position in `[0, STEPS_PER_REV)` |
| [`isSafeTiltMove`](../sysmlv2/SeedImaging.sysml#L91) | 91 | bounded **and** the target stays within ±45° |

Centralising these means a physical limit is stated once and referenced by both the MCU
guarantee and the host assumption, which is what makes the integration constraint on
`xPos` meaningful rather than coincidental.

---

## Constants

Declared in the model, exported to C/C++ by `bin/gen_model_constants.py`, and checked for
drift as step 6 of `bin/verify.sh`.

| Constant | Value | Meaning |
| --- | --- | --- |
| `STEPS_PER_REV` | 200 | NEMA 17 full steps per revolution |
| `MAX_MOVE_STEPS` | 400 | largest accepted single move |
| `TILT_LIMIT_STEPS` | 25 | ±25 steps = ±45°, the RPE 2 bracket-occlusion limit |
| `MAX_SCAN_MOVES` | 1000 | upper bound on a scan plan |
| `STEP_INTERVAL_MS` | 5 | inter-step delay |
| `SETTLE_MS` | 80 | post-move settle before acknowledging |

---

## Axes

| Axis | Model state | Range | Purpose |
| --- | --- | --- | --- |
| Z | `zPosition` | `[0, 200)`, wraps | Turntable: azimuth |
| X | `xPosition` | `[-25, 25]`, bounded | Tilt: elevation |
| Y | `yPosition` | `[0, 200)`, wraps | Reserved; intended for platform height |

Z and X between them reach every viewing direction, since a viewing direction is two
angles. Y is a positioning axis and contributes no viewpoint coverage; its purpose is to
make the effect of platform height on computed volume measurable rather than assumed.

X is the only bounded axis. Past ±45° the tilt bracket occludes the camera field of view,
a finding from the second RPE presentation that is annotated as such in the model and
enforced by [`SI-MCU-9`](../sysmlv2/SeedImaging.sysml#L277).
