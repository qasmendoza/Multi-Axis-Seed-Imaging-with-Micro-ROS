#!/bin/bash
# ============================================================================
#  Seeded-bug demo (the HAMR GUMBO / GUMBOX exercises: "seed a bug, watch the
#  contracts catch it").  Works on a scratch copy; your project is not changed.
#
#    bin/seeded_bug_demo.sh esp32    drop the +/-45 deg check in the ESP32 StepperController
#    bin/seeded_bug_demo.sh jetson   let the Jetson ScanController accept a start while busy
#    bin/seeded_bug_demo.sh model    narrow the Jetson's tilt-ack integration assumption
#                                    to +/-20 deg so it no longer matches the ESP32's guarantee
#
#  esp32 / jetson: the component's GUMBOX tests (manual + property-based) and its
#  Logika proof must FAIL.  model: HAMR SysMLv2 Logika checking must FAIL.
#  Exit code 0 = the seeded bug was caught, 1 = it slipped through.
#  Requires SIREUM_HOME (Sireum 4.20260810.80aad0c2) and python3.
# ============================================================================
set -u
ROOT=$(cd "$(dirname "$0")/.." && pwd)
: "${SIREUM_HOME:?Please set SIREUM_HOME to your Sireum installation}"
SIREUM="$SIREUM_HOME/bin/sireum"
WHAT=${1:-}
TMP=$(mktemp -d "${TMPDIR:-/tmp}/seeded_bug.XXXXXX")
trap 'rm -rf "$TMP"' EXIT
COMP=src/main/component/seedimaging/SeedImaging

# replace exactly one occurrence of $2 by $3 in file $1
swap() {
  python3 - "$1" "$2" "$3" <<'EOF'
import sys
path, old, new = sys.argv[1], sys.argv[2], sys.argv[3]
text = open(path).read()
if text.count(old) != 1:
    sys.exit("seeded_bug_demo: expected text not found in " + path)
open(path, "w").write(text.replace(old, new))
EOF
}

run_component() {   # $1 = component file, $2 = test classes (space separated)
  echo "== GUMBOX tests (manual + property-based), expected to FAIL =="
  (cd "$TMP/slang" && "$SIREUM" proyek test . $2) > "$TMP/test.log" 2>&1
  TEST_RC=$?
  grep -v '"title" : "Art"' "$TMP/test.log" | perl -pe 's/\e\[[0-9;]*m//g' \
    | grep -E '\*\*\* FAILED|Tests: succeeded' | head -12
  echo
  echo "== Logika proof of $1, expected to FAIL =="
  (cd "$TMP/slang" && "$SIREUM" proyek logika --par --timeout 120 . "$COMP/$1") > "$TMP/logika.log" 2>&1
  LOGIKA_RC=$?
  grep -v 'JAVA_TOOL_OPTIONS' "$TMP/logika.log" | grep -E 'Error|error|Could not|Logika verified' | head -8
  echo
  if [ $TEST_RC -ne 0 ] && [ $LOGIKA_RC -ne 0 ]; then
    echo "RESULT: the seeded bug was caught by the GUMBOX tests AND by Logika."
    exit 0
  fi
  echo "RESULT: the seeded bug was NOT caught (tests exit $TEST_RC, Logika exit $LOGIKA_RC)."
  exit 1
}

case "$WHAT" in
  esp32)
    cp -R "$ROOT/hamr/slang" "$TMP/slang" && rm -rf "$TMP/slang/out"
    F="$TMP/slang/$COMP/StepperController_esp32_stepper.scala"
    swap "$F" \
'    // if (SeedImaging.GUMBO__Library.isBoundedMove(steps)) {   // seeded bug
    if (SeedImaging.GUMBO__Library.isSafeTiltMove(xPosition, steps)) {' \
'    if (SeedImaging.GUMBO__Library.isBoundedMove(steps)) {   // seeded bug
    // if (SeedImaging.GUMBO__Library.isSafeTiltMove(xPosition, steps)) {'
    echo "Seeded bug (scratch copy only): the ESP32 accepts any bounded tilt move, even past +/-45 deg."
    echo
    run_component StepperController_esp32_stepper.scala \
      "seedimaging.SeedImaging.StepperController_esp32_stepper_GumboX_Manual_Tests seedimaging.SeedImaging.StepperController_esp32_stepper_GumboX_UnitTests"
    ;;
  jetson)
    cp -R "$ROOT/hamr/slang" "$TMP/slang" && rm -rf "$TMP/slang/out"
    F="$TMP/slang/$COMP/ScanController_jetson_scan.scala"
    swap "$F" \
'    // if (!halted) {   // seeded bug
    if (!pending & !halted) {' \
'    if (!halted) {   // seeded bug
    // if (!pending & !halted) {'
    echo "Seeded bug (scratch copy only): the Jetson starts a plan even while a move is in flight."
    echo
    run_component ScanController_jetson_scan.scala \
      "seedimaging.SeedImaging.ScanController_jetson_scan_GumboX_Manual_Tests seedimaging.SeedImaging.ScanController_jetson_scan_GumboX_UnitTests"
    ;;
  model)
    cp -R "$ROOT/sysmlv2" "$TMP/sysmlv2"
    swap "$TMP/sysmlv2/SeedImaging.sysml" \
'            assume SI_HOST_A1_tiltAckInRange "Tilt acknowledgements lie within +/-45 deg.":
              SeedImaging::GUMBO__Library::isTiltSafe(xPos.data);' \
'            assume SI_HOST_A1_tiltAckInRange "Tilt acknowledgements lie within +/-45 deg.":
              -20 [s32] <= xPos.data & xPos.data <= 20 [s32];'
    echo "Seeded bug (scratch copy only): the Jetson now assumes tilt acks within +/-20 steps,"
    echo "but the ESP32 only guarantees +/-25 steps (SI-MCU-16)."
    echo
    echo "== HAMR SysMLv2 Logika checking of the connections, expected to FAIL =="
    (cd "$TMP/sysmlv2" && "$SIREUM" hamr sysml logika --sourcepath ".:aadl-lib") > "$TMP/model.log" 2>&1
    RC=$?
    grep -v 'JAVA_TOOL_OPTIONS' "$TMP/model.log" | grep -E 'Checking|Could not|verified' | sed "s|$TMP/||"
    echo
    if [ $RC -ne 0 ]; then
      echo "RESULT: the seeded integration-constraint error was caught by HAMR SysMLv2 Logika checking."
      exit 0
    fi
    echo "RESULT: the seeded error was NOT caught (exit $RC)."
    exit 1
    ;;
  *)
    echo "usage: bin/seeded_bug_demo.sh esp32 | jetson | model"
    exit 2
    ;;
esac
