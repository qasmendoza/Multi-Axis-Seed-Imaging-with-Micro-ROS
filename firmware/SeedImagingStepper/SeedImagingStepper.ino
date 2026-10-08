// ============================================================================
//  SeedImagingStepper -- Arduino Nano ESP32 firmware
//
//  The micro-ROS node itself (subscriptions /stepper/{z,x,y}/cmd, publishers
//  /stepper/{z,x,y}/pos, executor, contract-checked decision logic) is the
//  HAMR-generated StepperController from sysmlv2/SeedImaging.sysml, packaged as
//  the Arduino library SeedImagingStepperNode.  This sketch only adds what is
//  specific to the board (board.cpp): the GPIO pin map of the board as built
//  (ROS2_Stepper_Build_Guide, section 1.2), the serial (USB-C) micro-ROS
//  transport, and agent-loss handling.
//
//  Board : Tools > Board > Arduino ESP32 Boards > Arduino Nano ESP32  (2.0.x core)
//  Libs  : micro_ros_arduino (humble, with the esp32 -> esp32s3 folder copy)
//          SeedImagingStepperNode (this project's firmware/ folder)
//  Agent : sudo docker run -it --rm -v /dev:/dev --privileged --net=host
//            microros/micro-ros-agent:humble serial --dev /dev/ttyACM0 -v4
// ============================================================================

#include <micro_ros_arduino.h>
#include <rmw_microros/rmw_microros.h>
#include <SeedImagingStepperNode.h>
#include "board.h"

static esp32_stepper_base_t node;

void setup()
{
  stepper_hw_init();                             // coils off before anything else

  set_microros_transports();                     // Serial over USB-C, 115200 baud
  silenceNodeLogging();

  // wait until the micro-ROS agent on the Jetson answers
  while (rmw_uros_ping_agent(100, 1) != RMW_RET_OK) {
    delay(500);
  }

  // HAMR-generated initialisation: node, 3 subscriptions, 3 publishers, executor
  if (esp32_stepper_base_init(&node) != RCL_RET_OK) {
    rebootWhenAgentLost();
  }
  esp32_stepper_initialize(&node);               // SI-MCU-1: all axes at home
}

void loop()
{
  // one executor pass: an arriving command runs its (blocking) move handler here
  rclc_executor_spin_some(&node.executor, RCL_MS_TO_NS(10));

  static uint32_t lastPing = 0;
  if (millis() - lastPing > 1000) {
    lastPing = millis();
    if (rmw_uros_ping_agent(100, 3) != RMW_RET_OK) {
      rebootWhenAgentLost();
    }
  }
}
