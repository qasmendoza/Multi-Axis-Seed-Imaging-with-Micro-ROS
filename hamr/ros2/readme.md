<!-- This file will not be overwritten if HAMR codegen is rerun -->

# seed_imaging — Mixed ROS2 / microROS Workspace

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Manual Steps](#manual-steps)
  - [Build](#build)
  - [Run](#run)

| Node | Package | Type | Dispatch |
|---|---|---|---|
| `jetson_scan_exe` | `seed_imaging_cpp_pkg` | ROS2 (rclcpp) | Sporadic |
| `esp32_stepper_exe` | `seed_imaging_microros_pkg` | microROS (rclc + rmw_microxrcedds) | Sporadic |

The microROS node(s) communicate via a micro-XRCE-DDS agent that bridges them to the ROS2 DDS bus.

## Prerequisites

- [ROS2 Humble](https://docs.ros.org/en/humble/Installation.html)
- micro-ROS Firmware Workspace (one-time setup)

  microROS nodes require a firmware workspace containing the micro-ROS client stack and agent.
  This workspace is built once and shared across all your microROS projects — set `MICROROS_WS`
  to a stable location outside any individual project and reuse it everywhere.

  **Step 1 — choose a location** (edit this, then add it to your shell profile):

  ```bash
  export MICROROS_WS=/path/to/microros_ws
  ```

  **Step 2 — build the firmware workspace** (copy-paste as-is once `MICROROS_WS` is set):

  ```bash
  mkdir -p $MICROROS_WS && cd $MICROROS_WS
  source /opt/ros/$ROS_DISTRO/setup.bash

  # 1. Add micro_ros_setup and build it
  git clone -b $ROS_DISTRO https://github.com/micro-ROS/micro_ros_setup.git src/micro_ros_setup
  colcon build --packages-select micro_ros_setup
  source install/setup.bash

  # 2. Download the micro-ROS client stack
  ros2 run micro_ros_setup create_firmware_ws.sh host

  # 3. Ignore packages with known build failures that are not needed
  touch src/ros2/example_interfaces/COLCON_IGNORE
  touch src/uros/micro-ROS-demos/COLCON_IGNORE

  # 4. Build the full micro-ROS stack (takes a while, but only done once)
  ros2 run micro_ros_setup build_firmware.sh
  source install/setup.bash

  # 5. Build the micro-XRCE-DDS agent
  ros2 run micro_ros_setup create_agent_ws.sh
  ros2 run micro_ros_setup build_agent.sh
  source install/setup.bash
  ```

## Quick Start

Run from this directory with `MICROROS_WS` set.

| Target | Description |
|---|---|
| `make build` | Build the micro-ROS app in `MICROROS_WS` and the ROS2 packages here |
| `make launch` | Build, then bring the system up with `ros2 launch` (Python launch file) |
| `make launch-py` | Same as `make launch` |
| `make launch-xml` | Build, then bring the system up using the XML launch file instead |
| `make` | Build everything and launch all nodes in separate terminals |
| `make run` | Same as `make` |
| `make stop` | Kill all running nodes |
| `make clean` | Remove local build artifacts and copied packages from `MICROROS_WS` |
| `make microros-config` | Apply `microros_apps/colcon.meta` to `MICROROS_WS` and rebuild the micro-ROS stack (see below) |

`make launch` and `make run` are alternatives: the launch targets start everything from
one launch file in the foreground, `run` opens a terminal per node.  Set launch
arguments with `LAUNCH_ARGS`, and pick a different launch file with `LAUNCH_FILE`:

```bash
make launch LAUNCH_ARGS="log_file:=run1.txt"
make launch LAUNCH_FILE=SeedImagingSystem_Instance_ros2
```

## Firmware Configuration

`microros_apps/colcon.meta` holds the build configuration the generated nodes need
from the micro-ROS middleware -- `rmw_microxrcedds`, `microxrcedds_client` and the
typesupport packages.  Those live in the firmware workspace and were built by step 4
above, not by `make build`, which builds only the application packages.

To have any effect it must be installed into that workspace, as
`$MICROROS_WS/src/colcon.meta` -- the path `micro_ros_setup`'s host `build.sh` reads,
via `colcon build --metas src`.  Sitting in `microros_apps/` it does nothing.
Installing it is therefore a separate step:

```bash
make microros-config
```

Run it once after generating, and again whenever `colcon.meta` changes.  Settings that
fail quietly if it is never applied:

- `RMW_UXRCE_MAX_PUBLISHERS` / `RMW_UXRCE_MAX_SUBSCRIPTIONS` -- these are derived from
  the model's port counts and regenerated on every codegen run.  The publisher count
  carries one extra beyond the model's ports when rcl logging is on, for the `/rosout`
  publisher `rcl_node_init` creates per node.  If the firmware's pools are smaller than
  the nodes need, entity creation fails: `rcl_publisher_init` reports `error not set`,
  because the rmw returns NULL without setting an error.  Re-apply after adding ports.
  `RMW_UXRCE_TRANSPORT` and the agent address are in the same package, so they apply on
  every target, host included.
- `RCL_COMMAND_LINE_ENABLED=ON` -- **embedded targets only.**  It restores the rcl
  argument parsing that `micro_ros_setup` disables in its cross-compiled configs;
  without it the rcl arguments in each node's `node_options` block (topic remap rules
  in particular) are parsed by nothing.  The option defaults to ON and the host config
  does not touch it, so on host the rules are parsed regardless -- and if `rcl` is not
  rebuilt in the firmware workspace at all, the nodes simply link the ROS
  distribution's `librcl`.  The same reasoning covers `RCL_LOGGING_ENABLED` and
  `RCL_LOGGING_IMPLEMENTATION`.

Entries outside the marked blocks in `colcon.meta` are derived from the model and are
overwritten on each run; the marked blocks (build profile, transport and tuning) are
preserved.  To override a derived value, restate its `-D` flag inside a marked block --
colcon passes `cmake-args` through in order and CMake takes the last occurrence.

`create_firmware_ws.sh` seeds that same path with micro-ROS's stock profile, and
`MICROROS_WS` is shared across projects, so `make microros-config` renames any
`colcon.meta` already there to `colcon.meta.bak` rather than discarding it.  The stock
profile it displaces sizes the `RMW_UXRCE_MAX_*` pools generously and enables
`UCLIENT_PROFILE_MULTITHREAD`; the generated one sizes the pools from your model and
turns multithreading off, since a generated node drives its XRCE session from a single
thread.  If you maintain your own firmware configuration, merge the two rather than
letting one replace the other.

### On a host workspace the `rcl` entry is inert

A firmware workspace created for the **host** platform
(`create_firmware_ws.sh host generic`) does not check out `rcl` at all -- on host,
micro-ROS is `rmw_microxrcedds` and `rclc` layered over the ROS 2 distribution's own
`rcl`, so there is no micro-ROS `rcl` to configure.  The `rcl` entry below therefore
matches no package and is silently inert on host; `find_package(rcl)` keeps resolving
to `/opt/ros/$ROS_DISTRO`.  The other entries -- `microxrcedds_client`,
`rmw_microxrcedds`, and the typesupport packages -- do apply, since those are checked
out and built there.

This is usually invisible, because the distribution's `rcl` is built with both
argument parsing and logging enabled -- the very things the flags above turn on.  So
remap rules and `/rosout` routing work on host whether or not this step is ever run.
They stop working the moment the same model is deployed to an embedded target, where
the micro-ROS `rcl` fork is used and ships with both features off.  Applying the
configuration matters there, and an embedded workspace
(`create_firmware_ws.sh <rtos> <board>`) does check `rcl` out, under
`firmware/mcu_ws`.

One setting to revisit when moving off host: `RCL_LOGGING_IMPLEMENTATION` is emitted
as `rcl_logging_spdlog`, which suits a host build.  Embedded targets generally want
`rcl_logging_noop`.

## Manual Steps

The Makefile targets automate the following steps.

### Build

Run from this directory. Requires `MICROROS_WS` to be set to the firmware workspace above.

```bash
source /opt/ros/$ROS_DISTRO/setup.bash && source $MICROROS_WS/install/setup.bash

# Copy the interfaces and microROS app into the firmware workspace and build them
cp -r src/seed_imaging_cpp_pkg_interfaces $MICROROS_WS/src/
cp -r microros_apps/seed_imaging_microros_pkg $MICROROS_WS/src/
cd $MICROROS_WS && colcon build --packages-select seed_imaging_cpp_pkg_interfaces seed_imaging_microros_pkg

# Build the ROS2 packages from this workspace
cd - && colcon build
source install/setup.bash
```

### Run

Each terminal requires:

```bash
source /opt/ros/$ROS_DISTRO/setup.bash && source $MICROROS_WS/install/setup.bash
```

Terminals running ROS2 nodes also need:

```bash
source <path-to-this-workspace>/install/setup.bash
```

Start the agent before the microROS node(s).

```bash
# Terminal 1 — micro-XRCE-DDS agent (must start first)
ros2 run micro_ros_agent micro_ros_agent udp4 --port 8888

# esp32_stepper — microROS node
RMW_IMPLEMENTATION=rmw_microxrcedds ros2 run seed_imaging_microros_pkg esp32_stepper_exe

# jetson_scan — ROS2 node
ros2 run seed_imaging_cpp_pkg jetson_scan_exe
```
