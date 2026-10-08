# SysML v2 integration: verification report

**Project:** Multi-Axis Seed Imaging with Micro-ROS: A SysML v2 Model-Based Architecture Approach
**Scope:** the ROS 2 ↔ micro-ROS ↔ 3-stepper control chain (what the prelim asks for). The camera is outside this model's scope, as listed in §6.
**Toolchain:** Sireum HAMR 4.20260810.80aad0c2 · ROS 2 Humble · micro-ROS (humble) · esp32 core 2.0.17 · micro_ros_arduino 2.0.8-humble

---

## 1. Summary

| # | Question | Result | Evidence |
|---|---|---|---|
| 1 | Does the earlier model (`MicroROS_Schematic.sysml`) work in HAMR? | **No.** It does not load, and it cannot generate code for the real platform. | §2, `docs/audit/old_model_audit_log.txt` |
| 2 | Is the new model well-formed (SysML v2 + GUMBO)? | **Yes** | HAMR Type Checking (`sireum hamr sysml tipe`) → `Well-formed!` |
| 2a | Do the integration constraints match across every connection? | **Yes.** The ESP32's tilt-ack guarantee (SI-MCU-16) implies the Jetson's tilt-ack assumption (SI-HOST-A1). A seeded mismatch is caught. | HAMR SysMLv2 Logika Checking (`sireum hamr sysml logika`) → `Integration constraints verified!`; `bin/seeded_bug_demo.sh model` |
| 3 | Does HAMR generate the ROS 2 and micro-ROS code from it? | **Yes.** C++ ROS 2 node, C micro-ROS node, launch files and colcon.meta. Regeneration is idempotent and keeps user code. | `bin/hamr.sh`; a second run changes nothing |
| 4 | Do the contracts type-check as code? | **Yes** | `sireum proyek tipe` → `Programs are well-typed!` |
| 5 | Does the component logic satisfy every contract? | **Yes, proved.** Logika verifies both components against all 37 guarantees. | `sireum proyek logika` → `Logika verified!` |
| 6 | Do the contracts catch real bugs? | **Yes.** Seeded bugs fail the proof and the tests. | §4.3, `bin/seeded_bug_demo.sh`, `docs/evidence/seeded_bug_demo_output.txt` |
| 6a | Are the components tested the way the HAMR tutorials teach (manual unit tests, manual GUMBOX tests, property-based GUMBOX tests)? | **Yes.** 16 manual unit tests, 59 manual GUMBOX tests (every clause by name, plus precondition-rejection tests), 2,820 property-based GUMBOX tests. All pass. | `bin/verify.sh` step 5; §4.2, §7 |
| 7 | Does the deployed C / C++ logic satisfy the contracts? | **Yes.** About 17.8 million contract checks, 0 failures. | `tests/host/run_host_tests.sh` |
| 8 | Does the generated system run end to end (ROS 2 → agent → micro-ROS → 3 axes)? | **Yes (host simulation).** 34 of 34 checks pass. | `tests/e2e/run_e2e_sim.sh` |
| 9 | Does the ESP32 firmware build from the generated node? | **Yes.** 434 KB flash (13%), 57 KB RAM (17%), no warnings. | arduino-cli, `espressif:esp32:nano_nora`; `docs/evidence/firmware_compile_output.txt` |
| 10 | Do the runbook's demo commands work, and does the Jetson half build from a clean copy? | **Yes (host simulation).** Every Part A/B command gives the documented output. A clean copy of `hamr/ros2` builds with only ROS 2 Humble installed, using `--packages-skip seed_imaging_microros_pkg`. The serial-agent launch file starts, and stops cleanly on Ctrl-C. | `docs/evidence/runbook_dry_run_output.txt` |
| 11 | Does it run on the physical Jetson + ESP32 + motors? | **Not yet verified.** It has to be run on the bench. | `docs/PRELIM_DEMO_RUNBOOK.md` §2; the same e2e checker runs on the hardware |

---

## 2. Audit of the earlier model (`MicroROS_Schematic.sysml`)

HAMR stops at the first layer of errors. The audit script therefore applies the minimal fix to a scratch copy, re-runs the checker, and repeats (`docs/audit/audit_old_model.py`). The original file was not modified.

| Stage | What HAMR reports | Cause | How the new model avoids it |
|---|---|---|---|
| 0 | **42 parse errors** (lines 109–117, 191–199). The file does not load in CodeIVE / HAMR. | `state` is a reserved word in SysML v2 and GUMBO. It is used as a field name (`PowerSample_i.state`). | No reserved words used as names |
| 1 | `In-to-In connection requires destination component Motor_Z to be a direct subcomponent of source component L298N_Z` (×3, lines 503–507) | `L298N_*.coilDriveIn → Motor_*.coilDriveIn` connects an *input* to an *input* | Drivers and motors are hardware. They are no longer modeled as software threads; the GPIO pin map lives in the firmware. |
| 2 | `There are multiple instances of L298N_Driver_i, currently only handling single instances` | A thread definition that carries GUMBO contracts may be instantiated **only once** in HAMR 4.20260810 (the model had 3 drivers and 3 motors). | One StepperController that owns all three axes |
| 4 | `Could not resolve 'coilResistance_mOhm'` (MR_HLR_12/13) | GUMBO can only talk about ports and GUMBO state, not about component attributes | Contracts only use ports and state |
| 5 | `Incompatible types for binary operation 'U32' >= 'Z'` (and `U32 == Z` in MR_HLR_4) | Untyped integer literals compared with `Unsigned_32` fields | Typed literals (`25 [s32]`) and named constants |
| 6 | `Well-formed!` after all of the above | | |
| 7 | Codegen for the model's own target, `--platform Microkit`: **`Model must contain exactly one actual bound processor, found 2`** | The model targets **seL4 Microkit** (the CIS 855 platform), but the system is a Jetson running Linux/ROS 2 plus an ESP32 running FreeRTOS/micro-ROS. HAMR can never generate this system from it. | Targets HAMR's **`ros2`** platform. The ESP32 thread is marked `Ros_Node_Kind = microRos`. |

Further problems that HAMR does not flag:

* The camera contract requires 1920×1080 frames, but the DFK 37BUX287 in `StepByStep_SeedImaging_Guide.docx` delivers 720×540.
* The camera thread's period (33 ms) is shorter than its execution time (100 ms).
* The `assume … : (not HasEvent(p)) | HasEvent(p)` clauses are always true, so they assume nothing.
* MR_HLR_11's text talks about ENA/ENB jumpers, but its formula checks the IN lines.
* Two threads write the same process output (`CameraDriver_seL4_i`).
* The model names a "Jetson Nano"; the hardware is a Jetson Orin Nano.

## 3. The new model

`sysmlv2/SeedImaging.sysml`, using the HAMR SysML v2 / AADL profile:

* **System** `SeedImagingSystem` has two processors, `JetsonOrinNano` and `ArduinoNanoESP32`. Two processes are bound to them: `JetsonSoftware` and `Esp32Firmware`. There are 6 connections: commands and acknowledgements for Z, X and Y.
* **Thread `StepperController`** (`Ros_Node_Kind = microRos`, sporadic, `Compute_Execution_Time = 0 .. 2080 ms`): three command ports and three acknowledgement ports. `Ros_Topic_Name` pins them to `/stepper/{z,x,y}/{cmd,pos}`. Its 16 guarantees cover home at start-up, the ±45° tilt limit, rotary positions modulo one revolution, exact moves, rejection of oversize or unsafe moves, axis isolation, and exactly one acknowledgement per command. It also has one integration guarantee: every tilt acknowledgement is within ±45°.
* **Thread `ScanController`** (`Ros_Node_Kind = ros2`, sporadic): `/scan/start` plus the three acknowledgement inputs, and the three command outputs. Its 21 guarantees cover at most one command per dispatch, one move in flight, tilt commands that are always safe, stray acknowledgements that are ignored, halt as a final state, and plan progress.
* **Message types** are *platform-provided* `std_msgs::Int32`. HAMR uses the stock ROS type, so the precompiled `micro_ros_arduino` works unchanged and the operator can drive the system with `ros2 topic pub`.
* **The GUMBO library holds the constants** (200 steps/rev, 400 steps/command, ±25-step tilt, 5 ms/step, 80 ms settle). `bin/gen_model_constants.py` exports them to the C / C++ code, and `bin/verify.sh` checks they are in sync. The code therefore enforces exactly the limits the proofs use.
* **Requirements:** 40 SysML v2 requirement definitions: 6 system (SI-SYS), 16 ESP32 (SI-MCU) and 18 Jetson (SI-HOST). Every SI-MCU / SI-HOST requirement is a GUMBO clause with the same ID. `docs/Traceability.md` is generated from the model and reports 0 missing and 0 orphan clauses.

The two component assumptions are state invariants that discharge themselves. Each is exactly the conjunction of guarantees that `initialize` establishes and every dispatch re-establishes:

* SI-MCU-A1 = SI-MCU-3 ∧ SI-MCU-2
* SI-HOST-A2 = SI-HOST-7 ∧ SI-HOST-10

The integration assumption SI-HOST-A1 is exactly the ESP32's integration guarantee SI-MCU-16 on the same `xPos` connection.

## 4. Verification evidence

### 4.1 Formal proof (Logika)
HAMR's JVM target turns every GUMBO clause into a Slang contract. The Slang reference implementations of both components were **proved** against all of them:

```
sireum proyek logika --par . StepperController_esp32_stepper.scala ScanController_jetson_scan.scala
Logika verified!
```

The C (`stepper_logic.h`) and C++ (`scan_logic.hpp`) code that actually runs is a line-for-line transcription of the proved Slang. §4.2 checks the transcription again.

### 4.2 Tests with the contracts as oracle

The Slang tests follow the three kinds the HAMR tutorials teach. All run in `bin/verify.sh` step 5 (`sireum proyek test`).

| Suite | What runs | Cases | Result |
|---|---|---|---|
| Manual unit tests (`*_Test.scala`) | Put inputs on the ports, run the entry point, compare the outputs with hand-worked values; several dispatches in a row (e.g. the whole self-test plan) | 10 (ESP32) + 6 (Jetson) | pass |
| Manual GUMBOX tests (`*_GumboX_Manual_Tests.scala`) | Hand-built test vectors; the HAMR-generated GUMBOX oracle judges every clause. One or more tests per clause, named after it (`compute_GUMBOX_manual_SI_MCU_9`, …). "Failing" tests break an assumption (SI-MCU-A1, SI-HOST-A1, SI-HOST-A2) and expect `Pre_Condition_Unsat` | 33 (ESP32) + 26 (Jetson) | pass |
| Property-based GUMBOX tests (`*_GumboX_UnitTests.scala`) | SlangCheck generates the vectors, drawn from each value's valid range; `failOnUnsatPreconditions = T`, so a test cannot pass without running | 1,520 (ESP32) + 1,300 (Jetson) | pass |
| `test_stepper_logic.c` | **the C logic flashed to the ESP32**; exhaustive over every in-range position × every command in [−450, 450] plus int32 extremes; no-shoot-through property of the drive sequence | 2,049,811 checks | 0 failures |
| `test_scan_logic.cpp` | **the C++ logic running on the Jetson**; every in-range controller state × every event; replays of all three plans | 15,709,173 checks | 0 failures |
| e2e (`tests/e2e`) | **the generated nodes on ROS 2 Humble with the real micro-ROS agent** (host simulation). Direct commands to each axis, then the self-test and scan plans, all checked against the contracts. | 34 checks | 34 pass |

### 4.3 The checks catch real bugs (seeded bugs)

As in the HAMR GUMBOX exercise, each component keeps its seeded bug as a comment next to the line it replaces. `bin/seeded_bug_demo.sh` applies the bug to a scratch copy and runs the checks; the project itself is not changed (`docs/evidence/seeded_bug_demo_output.txt`).

| Seeded bug | Caught by |
|---|---|
| `esp32`: the ESP32 forgets the ±45° check (only checks the 400-step bound) | Logika (`put_xPos` precondition, i.e. the outgoing integration constraint SI-MCU-16); manual GUMBOX tests SI_MCU_2 and SI_MCU_9 (4 failures); property-based GUMBOX tests; `test_stepper_logic.c` (38,250 failures) |
| `jetson`: the Jetson accepts a start while a move is in flight | Logika (postcondition of `handle_start`); manual GUMBOX test SI_HOST_11; property-based GUMBOX tests |
| `model`: the Jetson's tilt-ack assumption narrowed to ±20 steps | HAMR SysMLv2 Logika Checking: `Could not deduce that the integration constraints of SeedImagingSystem_Instance.xPos holds` |

### 4.4 Things the tools taught us (worth knowing when you extend the model)

* In HAMR 4.20260810, `sireum hamr sysml tipe` resolves names in GUMBO but **does not reject mistyped equalities** (`x == true` passes). The real type check is `sireum proyek tipe` on the generated Slang, and the real semantic check is Logika. `bin/verify.sh` runs both.
* `'->:'(p, q)` should not guard a `.get` in the consequent. Logika does not use the antecedent as a guard there, so write `NoSend(p) or f(p.data)` instead.
* A platform-provided `std_msgs::Empty` (no fields) crashes the JVM GUMBOX generator (`AadlTypes.getTypeByPath`). `/scan/start` therefore carries an `Int32`, which also selects the plan.
* `satisfy` relations are outside HAMR's supported subset: the type checker crashes on them. They live in `sysml-trace/`, outside HAMR's source path.
* The generated ROS 2 callback group is `Reentrant`, so the Jetson node's user code serialises its handlers with a mutex (AADL sporadic semantics).

## 5. Generated artefacts

| From the model | Where |
|---|---|
| ROS 2 node `jetson_scan` (rclcpp, sporadic handlers `handle_start/zPos/xPos/yPos`) | `hamr/ros2/src/seed_imaging_cpp_pkg` |
| micro-ROS node `esp32_stepper` (rclc; 3 subscriptions, 3 publishers, executor, `put_*` API) | `hamr/ros2/microros_apps/seed_imaging_microros_pkg` and `firmware/SeedImagingStepperNode` |
| Launch: whole system on a host, ROS 2 half only, micro-ROS half | `hamr/ros2/src/seed_imaging_cpp_pkg_bringup/launch` (plus the hand-written `seed_imaging_hardware.launch.py`) |
| micro-ROS middleware sizing (3 publishers, 3 subscriptions) | `hamr/ros2/microros_apps/colcon.meta` |
| Slang reference, Logika contracts, GUMBOX harness | `hamr/slang` |

Hand-written code lives only in HAMR's preserved places: `user_code/`, `user_headers/`, the `component/` bodies, and the extra test suites. It survives regeneration, and a second `bin/hamr.sh` run changes nothing.

## 6. Limits and open items

1. **Hardware run pending.** The firmware builds and links against the precompiled `micro_ros_arduino` using the `esp32` → `esp32s3` copy. The same node code runs in simulation. The actual run on the Jetson + ESP32 + motors still has to be done; `tests/e2e/e2e_contract_check.py` is the acceptance test there.
2. **Transport:** the simulation uses micro-ROS over UDP. The hardware uses USB serial, with the same node code and a different transport (`set_microros_transports()` / `micro_ros_agent serial`). With a pseudo-terminal standing in for the ESP32, the serial agent from `seed_imaging_hardware.launch.py` opens the port and shuts down cleanly. A real serial session with the board has not been run.
3. **Camera, contour detection, volume carving** are not in this model yet. The next step is a `CameraCapture` ROS 2 node triggered by the acknowledgements.
4. **Tilt holding torque:** coils are released after each move. Check on the bench that the tilt stage does not back-drive.
5. **Timing** is recorded in the model (`Compute_Execution_Time` 0..2080 ms; 5 ms/step; 80 ms settle), but HAMR's ROS 2 target does not analyse schedulability. Measured settle time and trigger latency (RPE 2 plan) remain experiments.

## 7. Conformance with the HAMR CodeIVE guide and the GUMBO / GUMBOX exercises

Checked against the HAMR documentation page [Editing and Code Generation Using SysMLv2 Models (CodeIVE)](https://hamr.sireum.org/hamr-doc/sysmlv2-codeive/) and the tutorial [HAMR Exercise: Adding a GUMBO Contract Clause](https://github.com/santoslab/hamr-tutorials/tree/main/HAMR-SysMLv2-Rust-Tutorials-Exercises/Ex-Simple-Isolette-DT-add-GUMBO) (Part 1 model, Part 2 GUMBOX testing, Part 3 verification). The tutorials use Rust and Verus on seL4. This project uses HAMR's Slang (JVM) target for contracts, tests and proofs (Logika instead of Verus), and HAMR's ROS 2 target for the deployed code. "Revised" marks what was added or changed to meet the item.

| # | Guide item | This project | Status |
|---|---|---|---|
| 1 | Models in a `sysmlv2` folder, opened as the CodeIVE workspace root | `sysmlv2/` | Revised (was `sysml/`) |
| 2 | `aadl-lib` with the AADL and HAMR libraries | `sysmlv2/aadl-lib/` (4.20260810, `VERSION.txt`) | Met |
| 3 | Model imports the HAMR library | `private import HAMR::*;` (+ `HAMR_AADL_Preview` for topic names) | Met |
| 4 | `//@ HAMR:` configuration lines with `--platform` and an output folder; code goes to `hamr/` | ros2 → `hamr/ros2`, JVM → `hamr/slang` | Met |
| 5 | "HAMR Type Checking" reports no problems | `Well-formed!` (`verify.sh` step 1) | Met |
| 6 | "HAMR SysML CodeGen", plus the tutorials' `sysmlv2/bin/run-hamr.cmd` script | `sysmlv2/bin/run-hamr.cmd` (called by `bin/hamr.sh`); regeneration changes nothing | Revised (script added) |
| 7 | Requirements with IDs; each GUMBO clause named after its requirement, with a description | 40 requirements; 37 guarantees + 3 assumptions; `docs/Traceability.md`: 0 missing, 0 orphan | Met |
| 8 | Integration constraints: `guarantee` on an outgoing port, `assume` on an incoming port | SI-MCU-16 on the ESP32's `xPos`, SI-HOST-A1 on the Jetson's `xPos` | Met |
| 9 | Initialize and compute clauses (general and per handler), GUMBO state and functions | SI-MCU-1, SI-HOST-1; `handle` clauses for every event port | Met |
| 10 | "HAMR SysMLv2 Logika Checking": every connection's integration constraints match; a seeded mismatch is caught | `Integration constraints verified!` (`verify.sh` step 2); `seeded_bug_demo.sh model` | Revised (step added) |
| 11 | Codegen weaves the contracts into the component code between markers and regenerates the GUMBOX file | Slang components carry the Logika contracts between `BEGIN`/`END` markers; `*_GumboX.scala` regenerated | Met |
| 12 | Manual unit tests with explicit expected outputs | `*_Test.scala`: 16 tests (they held only HAMR's empty examples before) | Revised |
| 13 | Manual GUMBOX tests per requirement, using the generated harness | `*_GumboX_Manual_Tests.scala`: 59 tests, every clause by name | Revised (renamed, one test per clause) |
| 14 | Follow the oracle call tree down to the clause | `testComputeCBwL` → `compute_CEP_Post` → `compute_CEP_Handler_xCmd_Guar` → `compute_handle_xCmd_SI_MCU_9_xRejectUnsafe_guarantee` (in `*_GumboX.scala`) | Met (documented in the test files and the runbook) |
| 15 | Seeded bug caught by the GUMBOX tests, then kept as a comment | Commented seeded bugs in both components; `seeded_bug_demo.sh esp32` / `jetson` | Revised (added) |
| 16 | Outgoing integration constraint (`I_Guar`) is part of the postcondition | `I_Guar_xPos` (SI-MCU-16); the seeded ESP32 bug violates it | Met |
| 17 | Incoming integration constraint (`I_Assm`) is part of the precondition; a "failing" test expects the harness to reject the vector | `compute_GUMBOX_manual_failing_SI_HOST_A1` expects `Pre_Condition_Unsat` (Slang's `RejectedPrecondition`); same for SI-MCU-A1 and SI-HOST-A2 | Revised (added) |
| 18 | Property-based GUMBOX tests (automated random vectors) | `*_GumboX_UnitTests.scala`: 2,820 SlangCheck vectors from valid ranges, `failOnUnsatPreconditions = T` | Revised (the generated defaults drew full-range values that never met the preconditions, and were not run) |
| 19 | Run single tests in the IDE and all tests from the command line | All tests: `sireum proyek test hamr/slang` (`verify.sh` step 5). Single tests: `sireum proyek ive hamr/slang`, then open the folder in Sireum IVE | Met (IVE, since the Slang tests are ScalaTest) |
| 20 | Verify the component code against the contracts; the seeded bug makes verification fail | Logika: `Logika verified!`; with a seeded bug Logika fails (§4.3) | Met (Logika in place of Verus) |

