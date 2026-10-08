#!/usr/bin/env python3
"""
Builds docs/Traceability.md from the model itself:

  requirement (SeedImaging_Requirements.sysml)
    -> GUMBO clause(s) that formalise it (SeedImaging.sysml), per component / entry point
    -> code that realises it (generated + hand-written files)
    -> evidence that checks it (Logika, manual unit tests, manual and property-based
       GUMBOX tests, host C/C++ tests, end-to-end check), with the named tests per clause

Run from anywhere:  python3 bin/trace_matrix.py
"""
import os
import re

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
REQ = os.path.join(ROOT, "sysmlv2", "SeedImaging_Requirements.sysml")
MODEL = os.path.join(ROOT, "sysmlv2", "SeedImaging.sysml")
OUT = os.path.join(ROOT, "docs", "Traceability.md")
TESTS = os.path.join(ROOT, "hamr", "slang", "src", "test", "bridge", "seedimaging", "SeedImaging")

req_text = open(REQ).read()
reqs = []   # (id, name, doc)
for m in re.finditer(r"requirement def <'([^']+)'>\s+(\w+)\s*\{\s*doc\s*/\*(.*?)\*/", req_text, re.S):
    rid, name, doc = m.group(1), m.group(2), m.group(3)
    doc = " ".join(l.strip().lstrip("*").strip() for l in doc.strip().splitlines())
    reqs.append((rid, name, doc))

model = open(MODEL).read()
# locate each thread's GUMBO block and its entry-point sections
clauses = {}   # id -> list of (component, section, clause name)
for tm in re.finditer(r"part def (\w+) :> Thread \{(.*?)\n    \}", model, re.S):
    comp, body = tm.group(1), tm.group(2)
    section = "?"
    for line in body.splitlines():
        s = line.strip()
        if s.startswith("integration"):
            section = "integration"
        elif s.startswith("initialize"):
            section = "initialize"
        elif s.startswith("compute"):
            section = "compute (every dispatch)"
        elif s.startswith("handle "):
            section = "handle " + s.split()[1].rstrip(":")
        cm = re.match(r'(guarantee|assume)\s+(\w+)\s+"([A-Z]+-[A-Z]+-[A0-9]+)', s)
        if cm:
            clauses.setdefault(cm.group(3), []).append((comp, section, cm.group(2), cm.group(1)))
            continue
        am = re.match(r'assume\s+(SI_(MCU|HOST)_A(\d+)\w*)\s+"', s)
        if am:
            aid = f"SI-{am.group(2)}-A{am.group(3)}"
            clauses.setdefault(aid, []).append((comp, section, am.group(1), "assume"))

# tests named after a clause: manual GUMBOX tests (vector tables and test titles) and
# manual unit tests (requirement IDs in the test titles)
gumbox_tests, unit_tests = {}, {}
for comp, stem in (("StepperController", "StepperController_esp32_stepper"), ("ScanController", "ScanController_jetson_scan")):
    g = open(os.path.join(TESTS, stem + "_GumboX_Manual_Tests.scala")).read()
    for m in re.finditer(r'\("SI_(MCU|HOST)_(\d+|A\d)",', g):
        rid = f"SI-{m.group(1)}-{m.group(2)}"
        gumbox_tests[rid] = gumbox_tests.get(rid, 0) + 1
    for m in re.finditer(r'test\("(?:compute|initialize)_GUMBOX_manual_(?:failing_)?SI_(MCU|HOST)_(\d+|A\d)', g):
        rid = f"SI-{m.group(1)}-{m.group(2)}"
        gumbox_tests[rid] = gumbox_tests.get(rid, 0) + 1
    u = open(os.path.join(TESTS, stem + "_Test.scala")).read()
    for t in re.finditer(r'test\("([^"]+)"\)', u):
        for m in re.finditer(r"SI-(MCU|HOST)-(\d+)", t.group(1)):
            rid = f"SI-{m.group(1)}-{m.group(2)}"
            unit_tests[rid] = unit_tests.get(rid, 0) + 1

def tests_for(rid):
    parts = []
    if gumbox_tests.get(rid):
        parts.append(f"{gumbox_tests[rid]} manual GUMBOX")
    if unit_tests.get(rid):
        parts.append(f"{unit_tests[rid]} manual unit")
    return ", ".join(parts) if parts else "**none**"

IMPL = {
    "StepperController": ("micro-ROS node `esp32_stepper` (HAMR-generated) - "
                          "`hamr/ros2/microros_apps/.../esp32_stepper_src.c`, `stepper_logic.h`; "
                          "Slang reference `StepperController_esp32_stepper.scala`"),
    "ScanController": ("ROS 2 node `jetson_scan` (HAMR-generated) - "
                       "`hamr/ros2/src/seed_imaging_cpp_pkg/.../jetson_scan_src.cpp`, `scan_logic.hpp`; "
                       "Slang reference `ScanController_jetson_scan.scala`"),
}
EVIDENCE = {
    "StepperController": ("Logika proof; every clause is also checked by the GUMBOX oracle in each manual GUMBOX test and "
                          "in the 1,520 property-based GUMBOX tests; `test_stepper_logic.c` (2.0 M checks); e2e part 1-2"),
    "ScanController": ("Logika proof; every clause is also checked by the GUMBOX oracle in each manual GUMBOX test and "
                       "in the 1,300 property-based GUMBOX tests; `test_scan_logic.cpp` (15.7 M checks); e2e part 2"),
}
SYS_REALISED = {
    "SI-SYS-1": "architecture: connections `zCmd`/`xCmd`/`yCmd` + `Ros_Topic_Name` on the stepper ports -> generated publishers/subscriptions `/stepper/{z,x,y}/cmd`",
    "SI-SYS-2": "architecture: connections `zPos`/`xPos`/`yPos` -> generated topics `/stepper/{z,x,y}/pos`",
    "SI-SYS-3": "SI-MCU-2, -8, -9, -16 (ESP32) and SI-HOST-4, -10, -A1 (Jetson)",
    "SI-SYS-4": "SI-HOST-2, -3, -6, -9, -13 .. -18",
    "SI-SYS-5": "SI-HOST-1, -7, -11, -12, -14, -16, -18",
    "SI-SYS-6": "SI-MCU-5, -9, -13, SI-HOST-8; coils released after every move and before reboot (firmware)",
}

lines = []
lines.append("# Requirements traceability\n")
lines.append("Generated by `bin/trace_matrix.py` from `sysmlv2/SeedImaging_Requirements.sysml` and "
             "`sysmlv2/SeedImaging.sysml`. Re-run it after you change either file.\n")
lines.append("## System requirements\n")
lines.append("| ID | Requirement | Realised / refined by | End-to-end evidence |")
lines.append("|---|---|---|---|")
for rid, name, doc in reqs:
    if rid.startswith("SI-SYS"):
        short = doc.split(".")[0] + "."
        lines.append(f"| {rid} | {short} | {SYS_REALISED.get(rid, '')} | `tests/e2e/e2e_contract_check.py` |")
lines.append("")
for comp_prefix, comp in (("SI-MCU", "StepperController"), ("SI-HOST", "ScanController")):
    title = "StepperController (ESP32, micro-ROS)" if comp == "StepperController" else "ScanController (Jetson, ROS 2)"
    lines.append(f"## {title}\n")
    lines.append(f"Implemented by: {IMPL[comp]}.  ")
    lines.append(f"Checked by: {EVIDENCE[comp]}.\n")
    lines.append("| ID | Requirement | GUMBO clause(s) in the model | Entry point(s) | Tests named after it |")
    lines.append("|---|---|---|---|---|")
    for rid, name, doc in reqs:
        if not rid.startswith(comp_prefix):
            continue
        cl = clauses.get(rid, [])
        names = ", ".join(sorted({f"`{c[2]}`" for c in cl})) or "**missing**"
        where = ", ".join(sorted({c[1] for c in cl})) or "-"
        lines.append(f"| {rid} | {doc} | {names} | {where} | {tests_for(rid)} |")
    lines.append("")

# assumptions (not requirements, but part of the contracts)
lines.append("## Assumptions the contracts rely on\n")
lines.append("| Clause | Component | Section | Discharged by | Tests named after it |")
lines.append("|---|---|---|---|---|")
for rid, cl in sorted(clauses.items()):
    for comp, section, cname, kind in cl:
        if kind == "assume":
            by = {"SI-HOST-A1": "SI-MCU-16 (integration guarantee on the same xPos connection)",
                  "SI-HOST-A2": "state invariant: SI-HOST-1 establishes it, SI-HOST-7/10 re-establish it",
                  "SI-MCU-A1": "state invariant: SI-MCU-1 establishes it, SI-MCU-2/3 re-establish it"}.get(rid, "")
            lines.append(f"| `{cname}` | {comp} | {section} | {by} | {tests_for(rid)} (precondition rejected) |")
lines.append("")

missing = [rid for rid, _, _ in reqs if not rid.startswith("SI-SYS") and rid not in clauses]
untested = [rid for rid in list(r[0] for r in reqs if not r[0].startswith("SI-SYS")) + ["SI-MCU-A1", "SI-HOST-A1", "SI-HOST-A2"]
            if not gumbox_tests.get(rid)]
extra = [rid for rid in clauses if not rid.endswith(("A1", "A2")) and rid not in {r[0] for r in reqs}]
lines.append("## Consistency\n")
lines.append(f"* requirements: {len(reqs)} ({sum(1 for r in reqs if r[0].startswith('SI-SYS'))} system, "
             f"{sum(1 for r in reqs if r[0].startswith('SI-MCU'))} ESP32, {sum(1 for r in reqs if r[0].startswith('SI-HOST'))} Jetson)")
lines.append(f"* GUMBO guarantees carrying a requirement ID: {sum(len(v) for k, v in clauses.items() if '-A' not in k)}; assumptions: {sum(len(v) for k, v in clauses.items() if '-A' in k)}")
lines.append(f"* component requirements without a GUMBO clause: {', '.join(missing) if missing else 'none'}")
lines.append(f"* GUMBO clauses without a requirement: {', '.join(extra) if extra else 'none'}")
lines.append(f"* requirements and assumptions without a manual GUMBOX test named after them: {', '.join(untested) if untested else 'none'}")
lines.append("")
os.makedirs(os.path.dirname(OUT), exist_ok=True)
open(OUT, "w").write("\n".join(lines))
print(f"wrote {OUT}: {len(reqs)} requirements, {sum(len(v) for v in clauses.values())} clauses; "
      f"missing={missing} extra={extra} untested={untested}")
