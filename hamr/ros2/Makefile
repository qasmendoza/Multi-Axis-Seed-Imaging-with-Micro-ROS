# This file will not be overwritten if HAMR codegen is rerun

MICROROS_WS ?= $(error MICROROS_WS is not set. Export it to the path of your micro-ROS firmware workspace.)
ROS2_PKG       := seed_imaging_cpp_pkg
INTERFACES_PKG := seed_imaging_cpp_pkg_interfaces
MICROROS_PKG   := seed_imaging_microros_pkg

# Extra rcl arguments appended to every `ros2 run` below, e.g.
#   make run ROS_ARGS="--ros-args -p log_file_name:=uros-demo.txt"
# Node parameters must be declared by the node before a value here takes effect.
ROS_ARGS ?=

SOURCE_BASE  := source /opt/ros/$${ROS_DISTRO}/setup.bash && source $(MICROROS_WS)/install/setup.bash
SOURCE_LOCAL := source $(CURDIR)/install/setup.bash

.PHONY: all build build-microros build-ros2 microros-config run launch launch-py launch-xml stop clean check-ros2

all: run

build: check-ros2 build-microros build-ros2

# Applies microros_apps/colcon.meta to the firmware workspace and rebuilds the
# micro-ROS stack with it.  This is NOT part of `make build`: colcon.meta configures
# the micro-ROS middleware (rcl, rmw_microxrcedds, ...), which lives in MICROROS_WS
# and is built once, not the application packages built from here.
#
# Run this once after generating, and again whenever colcon.meta changes -- notably
# after adding ports, since the RMW_UXRCE_MAX_* pool sizes are derived from the model
# and undersized pools fail entity creation without a diagnostic.  On embedded targets
# it additionally restores RCL_COMMAND_LINE_ENABLED, which micro_ros_setup disables
# there; until then the rcl arguments in each node's node_options block are silently
# ignored.  The host config does not disable it, so that part is moot on host.
#
# It goes to $(MICROROS_WS)/src/colcon.meta, which is where the host firmware build
# reads it from: micro_ros_setup's host build.sh runs `colcon build --metas src`, and
# its create.sh seeds that same path with the stock profile.  A colcon.meta placed at
# the workspace root is read by nothing.
#
# MICROROS_WS is shared across projects and the stock profile is already at that path,
# so any existing colcon.meta is backed up to colcon.meta.bak rather than discarded.
# The backup is only taken when what is there is not already our own copy, otherwise
# a second run of this target would overwrite the stock backup with the project profile.
#
# --cmake-force-configure is forwarded to colcon (build_firmware.sh shifts past -- and
# the host config passes the remaining "$$@" to each colcon build).  Without it a changed
# colcon.meta can leave the existing CMake caches in place, so the target reports success
# while the new settings never take effect -- the same silent-failure shape as an ignored
# remap rule.  Note the doubled --: `ros2 run` parses its own command line with argparse,
# which swallows the first --, so a single one never reaches build_firmware.sh and its
# getopts rejects --cmake-force-configure as an illegal option.
microros-config: check-ros2
	@if [ -f "$(MICROROS_WS)/src/colcon.meta" ] && ! cmp -s "$(MICROROS_WS)/src/colcon.meta" microros_apps/colcon.meta; then \
	  cp "$(MICROROS_WS)/src/colcon.meta" "$(MICROROS_WS)/src/colcon.meta.bak"; \
	  echo "Backed up existing src/colcon.meta to src/colcon.meta.bak"; \
	fi
	cp microros_apps/colcon.meta $(MICROROS_WS)/src/colcon.meta
	cd $(MICROROS_WS) && bash -c "$(SOURCE_BASE); ros2 run micro_ros_setup build_firmware.sh -- -- --cmake-force-configure"
	@echo "Firmware rebuilt with microros_apps/colcon.meta. Re-run 'make build' to rebuild the app packages against it."

check-ros2:
	@test -n "$${ROS_DISTRO}" || { echo "ERROR: ROS_DISTRO is not set. Source a ROS2 installation first (e.g., source /opt/ros/jazzy/setup.bash)."; exit 1; }
	@test -f "/opt/ros/$${ROS_DISTRO}/setup.bash" || { echo "ERROR: /opt/ros/$${ROS_DISTRO}/setup.bash not found."; exit 1; }

# Run under `env -i` so only the ROS2 distribution and MICROROS_WS are on the
# environment.  colcon writes whatever is on AMENT_PREFIX_PATH/COLCON_PREFIX_PATH at
# build time into the generated setup scripts as a prefix chain, and MICROROS_WS is
# shared across projects -- so building into it with another workspace sourced leaves
# that workspace chained into MICROROS_WS permanently, for every project that uses it
# afterwards.  Clearing just one of the two variables is not enough; both seed the chain.
build-microros:
	cp -r src/$(INTERFACES_PKG) $(MICROROS_WS)/src/
	cp -r microros_apps/$(MICROROS_PKG) $(MICROROS_WS)/src/
	cd $(MICROROS_WS) && env -i HOME="$$HOME" PATH="$$PATH" ROS_DISTRO="$${ROS_DISTRO}" \
	  bash --noprofile --norc -c "$(SOURCE_BASE); colcon build --packages-select $(INTERFACES_PKG) $(MICROROS_PKG)"

build-ros2:
	bash -c "$(SOURCE_BASE); colcon build; $(SOURCE_LOCAL)"

run: build
	gnome-terminal --title="[agent] micro-XRCE-DDS" -- bash -c "$(SOURCE_BASE); ros2 run micro_ros_agent micro_ros_agent udp4 --port 8888; exec bash"
	sleep 2
	gnome-terminal --title="[microROS] esp32_stepper_exe" -- bash -c "$(SOURCE_BASE); RMW_IMPLEMENTATION=rmw_microxrcedds ros2 run $(MICROROS_PKG) esp32_stepper_exe $(ROS_ARGS); exec bash"
	gnome-terminal --title="[ROS2] jetson_scan_exe" -- bash -c "$(SOURCE_BASE); $(SOURCE_LOCAL); ros2 run $(ROS2_PKG) jetson_scan_exe $(ROS_ARGS); exec bash"
	@echo "Nodes launched. Run 'make stop' to kill them."

# Arguments passed to `ros2 launch`, e.g.
#   make launch LAUNCH_ARGS="log_file:=run1.txt joystick_dev:=/dev/input/by-id/<pad>-joystick"
# `ros2 launch --show-args $(BRINGUP_PKG) $(LAUNCH_FILE).launch.py` lists what the file
# accepts.  Unlike ROS_ARGS above these are launch arguments, not rcl arguments: they are
# declared by the launch file and reach a node through its parameters.
#
# Device arguments take a by-id path rather than /dev/input/jsN, which is assigned in
# connection order and moves across replugs; `ls /dev/input/by-id` lists them.  Every
# argument also needs a non-empty value -- `name:=` is rejected as malformed -- so an
# argument whose value should be empty has to be changed in the launch file instead.
LAUNCH_ARGS ?=

# Which launch file to run.  SeedImagingSystem_Instance brings up the whole system on this host;
# SeedImagingSystem_Instance_ros2 brings up only the ROS2 half, for when the micro-ROS nodes run on
# hardware and the agent is reachable elsewhere.
LAUNCH_FILE ?= SeedImagingSystem_Instance

BRINGUP_PKG := $(ROS2_PKG)_bringup

# Both formats are generated from the same model and launch the same system; pick
# whichever you prefer to hand-edit.
launch: launch-py

launch-py: build
	bash -c "$(SOURCE_BASE); $(SOURCE_LOCAL); ros2 launch $(BRINGUP_PKG) $(LAUNCH_FILE).launch.py $(LAUNCH_ARGS)"

launch-xml: build
	bash -c "$(SOURCE_BASE); $(SOURCE_LOCAL); ros2 launch $(BRINGUP_PKG) $(LAUNCH_FILE).launch.xml $(LAUNCH_ARGS)"

stop:
	-pkill -f 'micro_ros_agent udp4' 2>/dev/null || true
	-pkill -f 'ros2 run $(MICROROS_PKG) esp32_stepper_exe' 2>/dev/null || true
	-pkill -f 'ros2 run $(ROS2_PKG) jetson_scan_exe' 2>/dev/null || true

clean:
	rm -rf build install log
	rm -rf $(MICROROS_WS)/src/$(INTERFACES_PKG) $(MICROROS_WS)/src/$(MICROROS_PKG)
	rm -rf $(MICROROS_WS)/build/$(INTERFACES_PKG) $(MICROROS_WS)/build/$(MICROROS_PKG)
	rm -rf $(MICROROS_WS)/install/$(INTERFACES_PKG) $(MICROROS_WS)/install/$(MICROROS_PKG)
