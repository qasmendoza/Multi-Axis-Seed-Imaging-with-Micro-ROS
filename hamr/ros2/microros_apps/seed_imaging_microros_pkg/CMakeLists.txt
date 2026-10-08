cmake_minimum_required(VERSION 3.8)
project(seed_imaging_microros_pkg)

# Content between markers will be preserved if codegen is rerun

if(CMAKE_COMPILER_IS_GNUCXX OR CMAKE_CXX_COMPILER_ID MATCHES "Clang")
    add_compile_options(-Wall -Wextra -Wpedantic)
endif()

find_package(ament_cmake REQUIRED)
find_package(rclc REQUIRED)
find_package(rcutils REQUIRED)
find_package(seed_imaging_cpp_pkg_interfaces REQUIRED)
find_package(std_msgs REQUIRED)

# Additions within these tags will be preserved when re-running Codegen

# Additions within these tags will be preserved when re-running Codegen

include_directories(include)

add_executable(esp32_stepper_exe src/base_code/esp32_stepper_runner.c src/user_code/esp32_stepper_src.c src/base_code/esp32_stepper_base_src.c)
ament_target_dependencies(esp32_stepper_exe rclc rcutils seed_imaging_cpp_pkg_interfaces std_msgs)

install(TARGETS
    esp32_stepper_exe
    DESTINATION lib/${PROJECT_NAME}
)

ament_package()
