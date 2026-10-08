# Content between markers will be preserved if codegen is rerun

# The micro-ROS half of the system, valid for a host deployment only.  On an
# embedded target these nodes are flashed rather than launched, and the agent
# may run elsewhere; in that case launch SeedImagingSystem_Instance_ros2.launch.py alone.
#
# The agent's transport must agree with RMW_UXRCE_TRANSPORT and the
# RMW_UXRCE_DEFAULT_UDP_* settings in microros_apps/colcon.meta.  Those live in
# a preserved block there, so changing them does not update the command below.

from launch import LaunchDescription
from launch.actions import DeclareLaunchArgument, ExecuteProcess
from launch.substitutions import LaunchConfiguration
from launch_ros.actions import Node

# LAUNCH PREAMBLE - additions within these tags will be preserved when re-running Codegen
# Add imports and helper functions here, e.g.
#     from launch.actions import TimerAction
#
#     def delayed(action, seconds):
#         return TimerAction(period=float(seconds), actions=[action])
# LAUNCH PREAMBLE - additions within these tags will be preserved when re-running Codegen


def generate_launch_description():
    ld = LaunchDescription()

    # micro-ROS agent: bridges rmw_microxrcedds nodes to the ROS2 DDS world.
    # Invoked through `ros2 run` because the binary lives in the package's lib
    # directory rather than on PATH.
    ld.add_action(ExecuteProcess(
        cmd=["ros2", "run", "micro_ros_agent", "micro_ros_agent", "udp4", "--port", "8888"],
        output="screen"))

    # LAUNCH ARGUMENTS - additions within these tags will be preserved when re-running Codegen
    # Declare launch arguments here, e.g.
    #     log_file = LaunchConfiguration("log_file")
    #     ld.add_action(DeclareLaunchArgument("log_file", default_value="uros-demo.txt"))
    # then reference them from a node's config block:
    #     parameters=[{"log_file_name": log_file}],
    # LAUNCH ARGUMENTS - additions within these tags will be preserved when re-running Codegen

    esp32_stepper_node = Node(
        package="seed_imaging_microros_pkg",
        executable="esp32_stepper_exe",
        additional_env={"RMW_IMPLEMENTATION": "rmw_microxrcedds"},
        # LAUNCH CONFIG esp32_stepper - additions within these tags will be preserved when re-running Codegen
        # LAUNCH CONFIG esp32_stepper - additions within these tags will be preserved when re-running Codegen
    )
    # LAUNCH OVERRIDE esp32_stepper - additions within these tags will be preserved when re-running Codegen
    # Rebind esp32_stepper_node here to replace the declaration above, e.g.
    #     esp32_stepper_node = Node(package="...", executable="...")
    # LAUNCH OVERRIDE esp32_stepper - additions within these tags will be preserved when re-running Codegen
    ld.add_action(esp32_stepper_node)

    return ld
