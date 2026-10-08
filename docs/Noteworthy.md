# Noteworthy

Things found while building this that seem worth recording: tooling behaviour, modelling
decisions forced by the tools, and one open gap. Reported factually, with versions and
reproducers, since several are observations about HAMR 4.20260810 rather than about this
system.

---

## Generated property-based tests can pass vacuously

**Probably the most consequential finding here.**

The generated `*_GumboX_UnitTests` suites draw inputs with SlangCheck. In the default
configuration the generators draw across the full range of the declared type, and
`failOnUnsatPreconditions` defaults to `F`.

For a component whose contracts are guarded by preconditions over a narrow range — tilt in
`[-25, 25]`, rotary positions in `[0, 200)` — nearly every full-range draw fails the
precondition. A vector that fails its precondition is discharged vacuously. The suite
reports a large number of passing tests while having exercised the contract almost not at
all.

The failure mode is silent. Nothing in the output distinguishes a suite that verified
2,820 meaningful cases from one that verified almost none.

**What we changed.** Narrowed the SlangCheck generators to the in-range values the
preconditions admit, and set `failOnUnsatPreconditions = T` so an unsatisfied precondition
is an error rather than a pass.

**Why it matters.** The seeded-bug runs show the difference. With in-range generators,
`bin/seeded_bug_demo.sh esp32` fails eight property-based vectors alongside the manual
tests. With the defaults, the same injected fault would be far less likely to be sampled
into the region where it manifests.

We think this is a usability issue rather than a defect — the defaults are reasonable for
components with wide-open preconditions — but the silence is the problem. A test count
that is large and meaningless looks exactly like a test count that is large and meaningful.

---

## The type checker crashes when `satisfy` relations are in scope

HAMR 4.20260810 does not report a diagnostic for `satisfy` relations on the sourcepath. It
raises an unhandled exception.

### Reproducer

```
$ sireum hamr sysml tipe --sourcepath ./sysmlv2
Well-formed!

$ sireum hamr sysml tipe --sourcepath ./sysmlv2:./sysml-trace
java.lang.Error: Invalid 'None' operation 'get'.
  at org.sireum.None.get(Option.scala:183)
  at org.sireum.hamr.sysml.stipe.TypeChecker.$anonfun$updateMembers$1(TypeChecker.scala:500)
  at org.sireum.hamr.sysml.stipe.TypeChecker.updateMembers(TypeChecker.scala:481)
  at org.sireum.hamr.sysml.stipe.TypeChecker.checkPackage(TypeChecker.scala:183)
  at org.sireum.hamr.sysml.FrontEnd$.typeCheck(FrontEnd.scala:56)
```

`sysml-trace/` contains one file holding only `satisfy` relations between requirements and
model elements. Adding it to the sourcepath is the only difference between the two runs.

Sireum 4.20260810.80aad0c2, macOS arm64. Full output in
[`evidence/typecheck_sourcepath.txt`](evidence/typecheck_sourcepath.txt).

A stack trace rather than a diagnostic suggests a defect rather than a rejected model.

### Why I think it mattered

`bin/verify.sh` scopes the sourcepath to `sysmlv2` with `aadl-lib`, so the project's own
verification was never affected. The **CodeIVE "SysMLv2 Type Check" command passes the
workspace root** as sourcepath, so it hit the crash and could not produce a result on this
project, even though the model is well formed.

The symptom is unhelpful from inside the IDE. The command fails with
`terminated with exit code: 1` in a terminal tab that is not open by default, and nothing
names the sourcepath or the file responsible.

### Workaround adopted

The trace file is stored as `sysml-trace/SeedImaging_Trace.sysml.txt`. HAMR discovers model
sources by the `.sysml` extension, so the file is no longer picked up as source. It remains
version-controlled and readable, and `bin/trace_matrix.py` is unaffected — it builds the
traceability matrix from `SeedImaging_Requirements.sysml` and `SeedImaging.sysml` and never
reads the trace file.

After the change:

```
$ sireum hamr sysml tipe --sourcepath .
Well-formed!
```

All seven checks in `bin/verify.sh` still pass, the CodeIVE type check reports
`Well-formed!`, and the traceability matrix still builds with 40 requirements and 40
clauses, none missing or untested.

### Cost of the workaround

The `satisfy` relations are not type checked against the model they refer to. A renamed
requirement or model element would not be caught automatically. The traceability matrix
catches a divergence between the requirements file and the GUMBO clauses, but not one
inside the trace file itself.

### Open question

Where should traceability relations live so that they are version-controlled with the
project, visible to a reader, and outside the HAMR sourcepath? The current answer — a
non-`.sysml` extension — works but reads as a workaround rather than a convention.

---

## Three obstacles between a correct install and a working CodeIVE

Recorded because none produced an error message naming its cause, and together they cost
most of a day.

### The install path can exceed the Unix socket limit

The runbook advises installing side by side so an existing Sireum is not destroyed, which
gives a path like `~/Applications/Sireum-4.20260810/Sireum`. CodeIVE then builds an IPC
socket path under `codium-portable-data/user-data/` that exceeds the 103-character limit
for Unix domain sockets on macOS:

```
WARNING: IPC handle ".../codium-portable-data/user-data/1.12-main.sock"
         is longer than 103 chars, try a shorter --user-data-dir
Error: listen EINVAL: invalid argument
```

The application does not start. Launching with `open` produces **no output at all** — the
error is only visible when the binary is run directly from a terminal.

`--user-data-dir` does not help: this build runs in portable mode and pins its data
directory beside the application. A symlink does not help either, since the path is
resolved before the socket is created. The installation has to physically live somewhere
shorter. Ours is at `~/S`.

### The HAMR commands resolve `sireum` from a path that cannot be overridden

With the installation moved, CodeIVE's HAMR tasks continued to invoke the *previous*
installation:

```
Executing task: /Users/<user>/Sireum/bin/sireum hamr sysml tipe ...
terminated with exit code: 1
```

Setting `SIREUM_HOME` in the shell had no effect. Neither did `launchctl setenv`, which is
normally how a GUI application on macOS is given an environment variable. We did not find a
setting that changes it.

The workaround was to make the path the extension uses point at the correct build:

```
mv ~/Sireum ~/Sireum-old
ln -s ~/S ~/Sireum
```

This is worth flagging because the failure is silent about its cause. A stale installation
at the expected path produces `exit code: 1` with no indication that a different Sireum
version is being run.

### A failed task hides its own output

When a HAMR command fails, the editor surfaces only:

```
The terminal process "..." failed to launch (exit code: 1).
```

The underlying output lives in a task-specific terminal that is not focused by default. It
has to be selected from the list on the right of the terminal panel. Until it is, there is
no way to tell a crashed type check from a model error.

---

## A contract-bearing thread can be instantiated only once

HAMR 4.20260810 rejects a model in which a thread definition carrying GUMBO contracts is
instantiated more than once:

```
There are multiple instances of L298N_Driver_i, currently only handling single instances
```

**Effect on the design.** The natural decomposition is one driver component per motor,
instantiated three times. That is not expressible with contracts attached, so the model has
a single `StepperController` owning all three axes.

**Not purely a cost.** The axis-isolation clauses
[`SI-MCU-6`](../sysmlv2/SeedImaging.sysml#L263),
[`SI-MCU-10`](../sysmlv2/SeedImaging.sysml#L281) and
[`SI-MCU-14`](../sysmlv2/SeedImaging.sysml#L299) exist precisely because one component owns
all three axes: they state that a command on one axis leaves the others untouched. With
three separate instances that property would be structural and unstated. Here it is
explicit and proved.

Worth recording as a case where a tool limitation produced a model that says more than the
natural decomposition would have.

---

## The Slang/C++ verification gap

Logika proves properties of the generated **Slang**. The boards run the generated **C and
C++**. Step 7 of `bin/verify.sh` bridges that by compiling the deployed C/C++ logic with
gcc/g++ and executing it against the same contracts over a bounded input and state space —
2,049,811 checks for the stepper logic, 20,196 states and 15,709,173 checks for the scan
logic.

That is bounded exhaustive **testing**, not a proof, and it is a weaker claim than the one
made about the Slang.

The seeded-bug output makes the asymmetry visible: the injected ESP32 fault is caught by
GUMBOX tests and by a Logika precondition, both on the Slang side. Step 7 is the only check
that looks at what actually ships.

A Clang-based analysis of the generated C/C++ — the static analyzer, CBMC, or symbolic
execution — would give the deployed code the standing the reference implementation already
has. This is open work and is noted here as a limitation rather than a result.

---

## Other 4.20260810 observations

| Observation | Effect |
| --- | --- |
| `state` and `from` are reserved | Cannot be used as identifiers; affected naming of GUMBO state variables |
| `Empty` crashes JVM code generation | Avoided in the model |
| ART test mode: port values persist across dispatches, and `BeforeEntrypoint()` re-runs `initialize` | Manual unit tests save and restore GUMBO state around it, via a `freshPorts()` helper |
| Remote file tooling refuses to write files named `Makefile` | `bin/hamr.sh` regenerates `hamr/ros2/Makefile` identically, which is why `diff` excludes it |
| Logika verification time varies between runs on an unchanged model | 23 s and 26 s observed; quoted approximately in [Testing.md](Testing.md) |

The ART test-mode behaviour is the one most likely to mislead. A test that depends on a
port being empty at the start of a dispatch will pass or fail depending on what the
previous test left there.

---

## Micro-ROS on an ESP32-S3 as a HAMR target

`Ros_Node_Kind = microRos` on a thread, with `--platform ros2` and
`--ros2-nodes-language Cpp`, produces a micro-ROS (rclc, C) node for the MCU alongside the
rclcpp C++ node for the host, from one model.

Two things that made this work in practice are not in the model and are worth recording.

The generated library must be placed under a directory named for the chip family. The
Arduino Nano ESP32 is an ESP32-S3, and `micro_ros_arduino` ships precompiled archives under
`esp32`. Without an `esp32s3` copy the sketch compiles and then fails at link with
undefined micro-ROS symbols — a confusing failure, since nothing points at the directory
name.

`Compute_Execution_Time = 0 [ms] .. 2080 [ms]` is derived in the model from
`MAX_MOVE_STEPS × STEP_INTERVAL_MS + SETTLE_MS`. On an MCU with no preemption in the
application path, a worst-case bound that long is a real scheduling constraint rather than
bookkeeping. We have not investigated what the AADL run-time services assume about
execution times of that magnitude on this class of target.

---

## Contracts that encode a physical finding

The ±45° tilt limit is not a software convention. It comes from an earlier experimental
finding: past that angle the tilt-axis mounting bracket occludes the camera field of view
for seeds under 3 mm. That is annotated in the model at
[`TILT_LIMIT_STEPS`](../sysmlv2/SeedImaging.sysml#L91) and enforced by
[`SI-MCU-9`](../sysmlv2/SeedImaging.sysml#L277).

The chain runs from a bench observation, to a model constant, to a GUMBO guarantee, to a
Logika proof, to a named test, to firmware that refuses the command on hardware. It is the
one place in this system where a physical constraint is traceable end to end, and the
demonstration in [Demo.md](Demo.md) is built around showing that chain.

---

## Open questions

Noted as questions rather than claims.

1. What would a HAMR back-end for bare-metal ARM Cortex-M require, and what do the AADL
   run-time services assume that does not hold on MCU-class targets?
2. Can the C/C++ side be given the same verification standing as the Slang reference
   implementation, and what would that take?
3. Should property-based test generation derive its generators from the preconditions
   rather than from the declared types?
4. Where should `satisfy` relations live so they are version-controlled with the project
   but outside the HAMR sourcepath?

The third and fourth are the ones we would most like an opinion on.
