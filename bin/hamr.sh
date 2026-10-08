#!/bin/bash
# ============================================================================
#  Model -> code.  Type-checks the SysML v2 model and regenerates both targets
#  from the //@ HAMR: lines at the top of sysmlv2/SeedImaging.sysml:
#     --platform ros2  ->  hamr/ros2   (ROS 2 C++ node, micro-ROS C node, launch)
#     --platform JVM   ->  hamr/slang  (Slang reference + Logika contracts + GUMBOX)
#  then exports the model's constants to the C / C++ code and copies the
#  regenerated micro-ROS node into the Arduino library.
#
#  Hand-written code survives regeneration (HAMR only rewrites its own files and
#  the regions between its markers).
#
#  Code generation itself is sysmlv2/bin/run-hamr.cmd (the HAMR tutorials' script);
#  this wrapper adds the type-check gate, the constants export and the firmware sync.
#
#  Requires: Sireum 4.20260810.80aad0c2 (SIREUM_HOME set)
#     bin/hamr.sh            type check + regenerate everything
#     bin/hamr.sh tipe       type check only
# ============================================================================
set -e
ROOT=$(cd "$(dirname "$0")/.." && pwd)
: "${SIREUM_HOME:?Please set SIREUM_HOME to your Sireum installation}"
SIREUM="$SIREUM_HOME/bin/sireum"
cd "$ROOT/sysmlv2"

echo "== HAMR type check (SysML v2 + GUMBO) =="
OUT=$("$SIREUM" hamr sysml tipe --sourcepath ".:aadl-lib" 2>&1); echo "$OUT"
echo "$OUT" | grep -q "Well-formed!" || { echo "model has errors -- nothing regenerated"; exit 1; }
[ "${1:-}" = "tipe" ] && exit 0

echo "== HAMR codegen (sysmlv2/bin/run-hamr.cmd): ROS 2 + micro-ROS -> hamr/ros2, Slang (JVM) -> hamr/slang =="
"$ROOT/sysmlv2/bin/run-hamr.cmd" ros2 JVM

echo "== export the model's constants (limits, timing) to the C / C++ code =="
python3 "$ROOT/bin/gen_model_constants.py"

echo "== sync the regenerated micro-ROS node into the Arduino library =="
"$ROOT/firmware/sync_from_hamr.sh" > /dev/null
echo "done"
