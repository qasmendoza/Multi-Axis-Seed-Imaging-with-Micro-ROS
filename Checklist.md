# Request Checklist

Each item requested on 7 October 2026, and where it is answered.

---

## Artifacts

| Requested | Where | Status |
| --- | --- | --- |
| SysML v2 models | [`sysmlv2/`](sysmlv2/) | |
| Models pass **SysMLv2 Type Check** in CodeIVE | screenshot in [`docs/evidence/`](docs/evidence/); step 1 of `bin/verify.sh` reports `Well-formed!` | |
| HAMR code generation for ROS 2 / micro-ROS works | [`hamr/ros2`](hamr/ros2); directives at the top of [`SeedImaging.sysml`](sysmlv2/SeedImaging.sysml#L1); `bin/hamr.sh` | |
| Implementation of each component, with testing examples | [docs/Components.md](docs/Components.md), [docs/Testing.md](docs/Testing.md) | |

## Documentation

| Requested | Where |
| --- | --- |
| System architecture, with hot links to model elements | [docs/Architecture.md](docs/Architecture.md) |
| Purpose of each component | [docs/Components.md](docs/Components.md) |
| Purpose of the contracts for each component | [docs/Components.md](docs/Components.md#what-the-contracts-are-for) |
| What language each component is implemented in | [docs/Components.md](docs/Components.md#implementation-languages-at-a-glance) |
| Summary of the GUMBO integration constraints | [docs/Integration-Constraints.md](docs/Integration-Constraints.md) |
| Target platform: boards, setup, connections | [docs/Platform.md](docs/Platform.md) |
| How to run the HAMR system with developer code on the boards | [docs/Running.md](docs/Running.md) |
| Anything interesting done with models, contracts, verification | [docs/Noteworthy.md](docs/Noteworthy.md) |

## Demo

| Requested | Where |
| --- | --- |
| A demo of some aspect of the system | [docs/Demo.md](docs/Demo.md), recordings in [`docs/evidence/`](docs/evidence/) |

---

## Two things stated rather than claimed

**No camera is integrated.** No imaging or volume carving has been performed, and no
accuracy result is claimed. The repository covers the modelling, verification and motion
control.

**Step 7 of `bin/verify.sh` is testing, not proof.** Logika proves properties of the
generated Slang; the boards run generated C and C++. Step 7 compiles and exhaustively
executes the deployed logic against the same contracts over a bounded space. It is a
weaker claim than the one made about the Slang, and it is written up as open work in
[docs/Noteworthy.md](docs/Noteworthy.md#the-slangcpp-verification-gap).

---

## Before this is handed over

- [ ] Run **SysMLv2 Type Check** in CodeIVE; capture the result to `docs/evidence/`
- [ ] Re-run `bin/verify.sh` and `bin/seeded_bug_demo.sh` on a clean checkout; refresh evidence
- [ ] Confirm every `#L` anchor still resolves after any model edit
- [ ] Add the two demo recordings to `docs/evidence/`
- [ ] Tag the state under review: `git tag -a rpe3-review -m "State submitted for RPE review"`
