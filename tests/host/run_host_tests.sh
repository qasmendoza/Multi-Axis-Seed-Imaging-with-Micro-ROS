#!/bin/bash
# Contract checks of the deployed C / C++ decision logic.  Needs only gcc/g++.
set -e
HERE=$(cd "$(dirname "$0")" && pwd)
ROOT=$(cd "$HERE/../.." && pwd)
OUT=${TMPDIR:-/tmp}/seed_imaging_host_tests
mkdir -p "$OUT"
gcc -std=c99 -O2 -Wall -Wextra -Werror -I"$ROOT/hamr/ros2/microros_apps/seed_imaging_microros_pkg/include" \
    "$HERE/test_stepper_logic.c" -o "$OUT/test_stepper_logic"
g++ -std=c++17 -O2 -Wall -Wextra -Werror -I"$ROOT/hamr/ros2/src/seed_imaging_cpp_pkg/include" \
    "$HERE/test_scan_logic.cpp" -o "$OUT/test_scan_logic"
"$OUT/test_stepper_logic"
"$OUT/test_scan_logic"
