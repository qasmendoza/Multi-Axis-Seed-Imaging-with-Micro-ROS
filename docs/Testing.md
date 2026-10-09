# Verification and Testing

What is checked, how, and what each check does and does not establish.

---

## The seven checks

`bin/verify.sh` runs all of them. Output from 30 September 2026, Sireum HAMR
4.20260810.80aad0c2, macOS arm64, 2 min 3 s. Captured in
[`docs/evidence/verify_output.txt`](evidence/verify_output.txt).

| # | Check | Tool | Result |
| --- | --- | --- | --- |
| 1 | Model type checking | HAMR | `Well-formed!` |
| 2 | Integration constraints on `xPos` | SysMLv2 Logika | `Integration constraints verified!` |
| 3 | Type check of generated Slang | `proyek tipe` | `Programs are well-typed!` |
| 4 | Both components proved | Logika | `Logika verified!`, 26.4 s |
| 5 | Unit, manual GUMBOX, property-based | ScalaTest | 2,895 passed, 0 failed |
| 6 | C/C++ constants match the model | `gen_model_constants.py` | in sync |
| 7 | Contract checks over deployed C/C++ | gcc/g++ harness | 17,758,984 checks, 0 failures |

Checks 1–2 are on the model. Checks 3–5 are on the Slang. Checks 6–7 are on the C/C++
that ships. The gap between 4 and 7 is discussed in
[Noteworthy.md](Noteworthy.md#the-slangcpp-verification-gap).

---

## Test suite composition

2,895 tests in 12 suites.

| Kind | Count | File pattern |
| --- | --- | --- |
| Manual unit tests | 16 | `*_Test.scala` |
| Manual GUMBOX tests | 59 | `*_GumboX_Manual_Tests.scala` |
| Property-based GUMBOX | 2,820 | `*_GumboX_UnitTests.scala` |

### Manual GUMBOX tests are named for the clause they exercise

Every clause has at least one test whose name carries the clause ID, so a failure
identifies the property rather than a line number.

```
compute_GUMBOX_manual_SI_MCU_2: tilt at +45 deg, +1 more is refused: tilt never passes +45 deg
compute_GUMBOX_manual_SI_MCU_3: Z wrap 199 + 1 -> 0
compute_GUMBOX_manual_SI_MCU_5: Z extreme S32.Max is refused (no overflow)
compute_GUMBOX_manual_SI_MCU_9: X level, +26 is refused
compute_GUMBOX_manual_SI_MCU_10: X move with Z = 57, Y = 150 leaves Z and Y unchanged
compute_GUMBOX_manual_SI_HOST_11: move in flight: start is ignored
compute_GUMBOX_manual_SI_HOST_13: stray Z ack while X is awaited: ignored
```

Assumptions are tested for their negative case too, by name:

```
compute_GUMBOX_manual_failing_SI_MCU_A1: tilt pre-state +30 (beyond +/-45 deg) is rejected
compute_GUMBOX_manual_failing_SI_HOST_A1: X ack of +30 (beyond +/-45 deg) is rejected
compute_GUMBOX_manual_failing_SI_HOST_A2: awaited axis 3 (not an axis) is rejected
```

These confirm the precondition actually constrains the input space, rather than being
vacuously satisfiable.

### Property-based GUMBOX

Six generated suites, 2,820 vectors:

```
PBT_initialize                                  20
PBT_compute_commands_in_range                  300
PBT_computewL_positions_and_commands_in_range 1000
PBT_computewL_full_range_commands              200
PBT_compute_events_in_range                    300
PBT_computewL_state_and_events_in_range       1000
```

These use SlangCheck generators configured to produce in-range values, with
`failOnUnsatPreconditions = T`. **The default configuration does not do this**, and the
consequence is significant enough that it is written up separately in
[Noteworthy.md](Noteworthy.md#generated-property-based-tests-can-pass-vacuously).

---

## Fault injection

A passing suite only means something if it can fail. `bin/seeded_bug_demo.sh` injects a
deliberate fault into a scratch copy and reports what catches it. Three cases, three
different mechanisms.

### `esp32`: the MCU accepts an unsafe tilt move

Caught twice, independently.

```
== GUMBOX tests, expected to FAIL ==
- compute_GUMBOX_manual_SI_MCU_2: tilt at +45 deg, +1 more is refused *** FAILED ***
- compute_GUMBOX_manual_SI_MCU_9: X at +45 deg, +1 is refused *** FAILED ***
- compute_GUMBOX_manual_SI_MCU_9: X level, +26 is refused *** FAILED ***
- PBT_compute_commands_in_range_0 *** FAILED ***
  (plus seven further property-based vectors)

== Logika proof of StepperController_esp32_stepper.scala, expected to FAIL ==
  - [191, 5] Could not deduce that the pre-condition of
    seedimaging.SeedImaging.StepperController_Operational_Api#put_xPos holds

RESULT: the seeded bug was caught by the GUMBOX tests AND by Logika.
```

The Logika failure is the more interesting of the two. It is not the compute guarantee
that fails first; it is the precondition of `put_xPos`, the operational API call that
publishes the acknowledgement. Weakening the tilt check means the component can no longer
establish `SI-MCU-16` at the point it writes to the port.

### `jetson`: the host starts a plan while a move is in flight

```
- compute_GUMBOX_manual_SI_HOST_11: move in flight: start is ignored *** FAILED ***
  (plus eleven property-based vectors)

== Logika proof of ScanController_jetson_scan.scala, expected to FAIL ==
  - [160, 38] Could not deduce that the postcondition holds

RESULT: the seeded bug was caught by the GUMBOX tests AND by Logika.
```

### `model`: the two sides disagree about the tilt range

The host is weakened to assume ±20 steps while the MCU still guarantees ±25.

```
== HAMR SysMLv2 Logika checking of the connections, expected to FAIL ==
Checking integration constraints of SeedImagingSystem_Instance.xPos
  - [116, 8] Could not deduce that the integration constraints of
    SeedImagingSystem_Instance.xPos holds

RESULT: the seeded integration-constraint error was caught by
        HAMR SysMLv2 Logika checking.
```

**Neither component is faulty in isolation here.** Both still pass their own unit tests
and their own compute contracts. The error exists only in the relationship between a
guarantee and an assumption, and it is caught before code generation. Of the three cases
this is the one that could not be found by testing either component alone.

Each scratch copy keeps the original line as a comment beside the seeded replacement, and
`bin/verify.sh` returns to all-passing afterwards.

---

## Provenance

The deployed artefacts are regenerated from the model and compared.

```
$ bin/hamr.sh
$ diff -r <backup>/firmware firmware
$ diff -r -x out -x Makefile <backup>/hamr hamr
```

Both silent. Captured in
[`docs/evidence/regeneration_proof.txt`](evidence/regeneration_proof.txt).

---

## Hardware checks

| Check | Result |
| --- | --- |
| Both nodes register | `/esp32_stepper` and `/jetson_scan` |
| All seven topics appear | `/scan/start`, `/stepper/{z,x,y}/{cmd,pos}` |
| Step scaling | 100 commanded steps measured 180°, consistent with `STEPS_PER_REV = 200` |
| Self-test plan | all three axes move in plan order |
| Tilt limit | `+25` → +45°, `-50` → sweep to −45°, `-1` refused, position holds |
| Firmware size | 434,421 B flash, 56,572 B RAM |

The tilt-limit sequence is the only hardware observation that directly exercises a
contract. It is recorded in [Demo.md](Demo.md).

**What this is not.** These are observations, not a validation campaign. No statistical
characterisation of positioning accuracy has been performed, and the ±45° limit has been
observed to hold rather than measured against an external reference.
