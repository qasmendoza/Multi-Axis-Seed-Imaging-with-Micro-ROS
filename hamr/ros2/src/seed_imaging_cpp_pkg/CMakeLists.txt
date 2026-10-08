cmake_minimum_required(VERSION 3.8)
project(seed_imaging_cpp_pkg)

# Content between markers will be preserved if codegen is rerun

if(CMAKE_COMPILER_IS_GNUCXX OR CMAKE_CXX_COMPILER_ID MATCHES "Clang")
    add_compile_options(-Wall -Wextra -Wpedantic)
endif()

find_package(ament_cmake REQUIRED)
find_package(rclcpp REQUIRED)
find_package(seed_imaging_cpp_pkg_interfaces REQUIRED)
find_package(std_msgs REQUIRED)

# Additions within these tags will be preserved when re-running Codegen

# Additions within these tags will be preserved when re-running Codegen

include_directories(include)

add_executable(jetson_scan_exe src/base_code/jetson_scan_runner.cpp src/user_code/jetson_scan_src.cpp src/base_code/jetson_scan_base_src.cpp)
ament_target_dependencies(jetson_scan_exe rclcpp seed_imaging_cpp_pkg_interfaces std_msgs)

install(TARGETS
    jetson_scan_exe
    DESTINATION lib/${PROJECT_NAME}
)

ament_package()
