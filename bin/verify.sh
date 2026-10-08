#!/bin/bash
# ============================================================================
#  Everything that can be verified without hardware, in one go.  The steps follow
#  the HAMR GUMBO tutorials (model -> contracts -> tests -> verification):
#    1. HAMR Type Checking of the SysML v2 model + GUMBO     (sireum hamr sysml tipe)
#    2. HAMR SysMLv2 Logika Checking: integration constraints on every connection
#                                                            (sireum hamr sysml logika)
#    3. Slang type check of the generated code and contracts (sireum proyek tipe)
#    4. Logika proof: component code vs. every GUMBO clause  (sireum proyek logika)
#    5. Tests, all three kinds from the tutorials            (sireum proyek test)
#         manual unit tests, manual GUMBOX tests, property-based GUMBOX tests
#    6. the C / C++ code carries the model's constants (limits, timing)
#    7. contract checks of the deployed C / C++ logic (gcc / g++)
#  Requires SIREUM_HOME (Sireum 4.20260810.80aad0c2), python3 and gcc/g++.
# ============================================================================
set -e
ROOT=$(cd "$(dirname "$0")/.." && pwd)
: "${SIREUM_HOME:?Please set SIREUM_HOME to your Sireum installation}"
SIREUM="$SIREUM_HOME/bin/sireum"
COMP=src/main/component/seedimaging/SeedImaging
PKG=seedimaging.SeedImaging

step() { echo; echo "==================== $* ===================="; }

step "1/7 HAMR Type Checking of sysmlv2/SeedImaging.sysml"
OUT=$(cd "$ROOT/sysmlv2" && "$SIREUM" hamr sysml tipe --sourcepath ".:aadl-lib" 2>&1); echo "$OUT"
echo "$OUT" | grep -q "Well-formed!" || { echo "model has errors"; exit 1; }

step "2/7 HAMR SysMLv2 Logika Checking: integration constraints of the connections"
set +e
OUT=$(cd "$ROOT/sysmlv2" && "$SIREUM" hamr sysml logika --sourcepath ".:aadl-lib" 2>&1); RC=$?
set -e
echo "$OUT" | grep -v 'JAVA_TOOL_OPTIONS' || true
[ $RC -eq 0 ] || { echo "integration constraints do not hold"; exit 1; }

step "3/7 Slang type check of the generated code and contracts"
(cd "$ROOT/hamr/slang" && "$SIREUM" proyek tipe .)

step "4/7 Logika: StepperController (ESP32) and ScanController (Jetson)"
(cd "$ROOT/hamr/slang" && "$SIREUM" proyek logika --par --timeout 120 . \
    $COMP/StepperController_esp32_stepper.scala $COMP/ScanController_jetson_scan.scala)

step "5/7 Tests: manual unit tests, manual GUMBOX tests, property-based GUMBOX tests"
LOG=${TMPDIR:-/tmp}/seed_imaging_tests.log
set +e
(cd "$ROOT/hamr/slang" && "$SIREUM" proyek test . \
    $PKG.StepperController_esp32_stepper_Test          $PKG.ScanController_jetson_scan_Test \
    $PKG.StepperController_esp32_stepper_GumboX_Manual_Tests $PKG.ScanController_jetson_scan_GumboX_Manual_Tests \
    $PKG.StepperController_esp32_stepper_GumboX_UnitTests   $PKG.ScanController_jetson_scan_GumboX_UnitTests) > "$LOG" 2>&1
RC=$?
set -e
# leave out the runtime's per-test log lines and the one-line-per-vector property-based tests
grep -v -E '"title" : "Art"|JAVA_TOOL_OPTIONS|^(.\[3[0-9]m)?- PBT_[A-Za-z_]+_[0-9]+(.\[0m)?$' "$LOG" || true
echo "Property-based GUMBOX tests (SlangCheck vectors, one test each):"
for c in PBT_initialize PBT_compute_commands_in_range PBT_computewL_positions_and_commands_in_range \
         PBT_computewL_full_range_commands PBT_compute_events_in_range PBT_computewL_state_and_events_in_range; do
  echo "  $c: $(grep -c -E -- "- ${c}_[0-9]+" "$LOG") run"
done
[ $RC -eq 0 ] || { echo "tests failed (full log: $LOG)"; exit 1; }

step "6/7 the C / C++ code uses the model's constants"
python3 "$ROOT/bin/gen_model_constants.py" --check

step "7/7 contract checks of the deployed C / C++ logic"
"$ROOT/tests/host/run_host_tests.sh"

echo; echo "ALL CHECKS PASSED"
