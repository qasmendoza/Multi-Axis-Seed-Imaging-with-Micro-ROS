# Content between markers will be preserved if codegen is rerun

from launch import LaunchDescription
from launch.actions import DeclareLaunchArgument
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

    # LAUNCH ARGUMENTS - additions within these tags will be preserved when re-running Codegen
    # Declare launch arguments here, e.g.
    #     log_file = LaunchConfiguration("log_file")
    #     ld.add_action(DeclareLaunchArgument("log_file", default_value="uros-demo.txt"))
    # then reference them from a node's config block:
    #     parameters=[{"log_file_name": log_file}],
    # LAUNCH ARGUMENTS - additions within these tags will be preserved when re-running Codegen

    jetson_scan_node = Node(
        package="seed_imaging_cpp_pkg",
        executable="jetson_scan_exe",
        # LAUNCH CONFIG jetson_scan - additions within these tags will be preserved when re-running Codegen
        # LAUNCH CONFIG jetson_scan - additions within these tags will be preserved when re-running Codegen
    )
    # LAUNCH OVERRIDE jetson_scan - additions within these tags will be preserved when re-running Codegen
    # Rebind jetson_scan_node here to replace the declaration above, e.g.
    #     jetson_scan_node = Node(package="...", executable="...")
    # LAUNCH OVERRIDE jetson_scan - additions within these tags will be preserved when re-running Codegen
    ld.add_action(jetson_scan_node)

    return ld
