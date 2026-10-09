# Seed Imaging System: SysML v2 / HAMR

A three-axis seed imaging platform developed with HAMR. One SysML v2 model describes
both processors, the two threads, the connections between them, and the behavioural
contracts in GUMBO. HAMR generates the ROS 2 (C++) node for the Jetson, the micro-ROS
(C) node for the ESP32, and a Slang reference implementation used for Logika
verification and GUMBOX testing.


---

## Start here

| Document | Covers |
| --- | --- |
| [docs/Architecture.md](docs/Architecture.md) | System structure, with links into the model |
| [docs/Components.md](docs/Components.md) | Each component: purpose, contracts, implementation language |
| [docs/Integration-Constraints.md](docs/Integration-Constraints.md) | The GUMBO integration constraints and what they establish |
| [docs/Platform.md](docs/Platform.md) | Boards, wiring, and how they are connected |
| [docs/Running.md](docs/Running.md) | Running the HAMR-generated system with application code on the boards |
| [docs/Testing.md](docs/Testing.md) | Verification and testing, with worked examples |
| [docs/Noteworthy.md](docs/Noteworthy.md) | Findings about the models, contracts, and tooling |
| [docs/Demo.md](docs/Demo.md) | The demonstration and how to reproduce it |

[Checklist.md](Checklist.md) maps each item requested on 7 October 2026 to where it is
answered.

---

## The model

[`sysmlv2/SeedImaging.sysml`](sysmlv2/SeedImaging.sysml): the system, its two threads,
their ports, and 40 GUMBO clauses.

HAMR is invoked twice, as recorded in the directives at the top of the model:

```
//@ HAMR: --platform ros2 --ros2-output-workspace-dir ../hamr/ros2 \
          --ros2-nodes-language Cpp --ros2-launch-language Xml
//@ HAMR: --platform JVM  --slang-output-dir ../hamr/slang --package-name seedimaging
```

| Run | Output | Role |
| --- | --- | --- |
| `--platform ros2` | `hamr/ros2` | **Deployed.** ROS 2 (rclcpp, C++) for the Jetson; micro-ROS (rclc, C) library for the ESP32 |
| `--platform JVM` | `hamr/slang` | Slang reference implementation, Logika proofs, GUMBOX test harnesses |

---

## Current state

**Verified.** Model type checks; SysMLv2 Logika discharges the integration constraints;
the generated Slang type checks and passes Logika; 2,895 tests pass; model constants
match the generated C/C++; 17,758,984 contract checks over the deployed C/C++ logic
report no failures. Three seeded faults are each caught, by three different mechanisms.
Regeneration is byte-identical to what is deployed.

**Running.** ROS 2 Humble on a Jetson Orin Nano, micro-ROS over USB serial to an Arduino
Nano ESP32, three NEMA 17 steppers. A commanded 100-step move measures 180° on the
hardware, matching `STEPS_PER_REV = 200`.

**Not yet done.** No camera is integrated, so no imaging or volume carving has been
performed. The accuracy requirement derived in
[`sysmlv2/SeedImaging_AccuracyChain.sysml`](sysmlv2/SeedImaging_AccuracyChain.sysml) is
not met by the available optics. See [docs/Noteworthy.md](docs/Noteworthy.md) for the
Slang/C++ verification gap, which is open work rather than a completed claim.

---

## Toolchain

| | |
| --- | --- |
| Sireum HAMR | 4.20260810.80aad0c2 |
| AADL libraries | `sysmlv2/aadl-lib`, 4.20260810 |
| ROS 2 | Humble |
| micro-ROS | humble, `micro_ros_arduino` 2.0.8-humble |
| ESP32 core | Espressif 2.0.17 (compile-checked); flashed with Arduino ESP32 Boards 2.0 line |
| Host OS | Ubuntu 22.04 on the Jetson; macOS on the development machine |

---

## Repository layout

```
sysmlv2/            SysML v2 models and the AADL libraries
sysml-trace/        satisfy relations, held off the HAMR source path
hamr/ros2/          generated ROS 2 (C++) and micro-ROS (C), deployed
hamr/slang/         generated Slang, Logika contracts, GUMBOX harnesses
firmware/           ESP32 sketch, generated node library, and a no-ROS motor test
bin/                hamr.sh, verify.sh, seeded_bug_demo.sh and helpers
docs/               the documents listed above
docs/evidence/      captured tool output
tests/              end-to-end checks
```
