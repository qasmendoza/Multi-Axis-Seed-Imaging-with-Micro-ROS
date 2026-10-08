#!/bin/bash
# Copies the HAMR-generated micro-ROS node (plus its hand-written user code) from
#   hamr/ros2/microros_apps/seed_imaging_microros_pkg
# into the Arduino library SeedImagingStepperNode.  Run it after every
#   sireum hamr sysml codegen --platform ros2 ...
# so the firmware always carries exactly what the model generated.
set -e
HERE=$(cd "$(dirname "$0")" && pwd)
PKG="$HERE/../hamr/ros2/microros_apps/seed_imaging_microros_pkg"
LIB="$HERE/SeedImagingStepperNode/src"
mkdir -p "$LIB/seed_imaging_microros_pkg/base_headers" "$LIB/seed_imaging_microros_pkg/user_headers"
cp "$PKG/include/seed_imaging_microros_pkg/base_headers/"*.h "$LIB/seed_imaging_microros_pkg/base_headers/"
cp "$PKG/include/seed_imaging_microros_pkg/user_headers/"*.h "$LIB/seed_imaging_microros_pkg/user_headers/"
cp "$PKG/src/base_code/esp32_stepper_base_src.c" "$LIB/"
cp "$PKG/src/user_code/esp32_stepper_src.c" "$LIB/"
# esp32_stepper_runner.c (the host main()) is deliberately NOT copied: the sketch replaces it
echo "synced HAMR node -> $LIB"
ls -R "$LIB"
