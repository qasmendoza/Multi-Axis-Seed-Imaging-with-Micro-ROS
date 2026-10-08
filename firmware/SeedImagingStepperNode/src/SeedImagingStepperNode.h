#ifndef SEED_IMAGING_STEPPER_NODE_H
#define SEED_IMAGING_STEPPER_NODE_H

// Entry header for the Arduino sketch: the HAMR-generated micro-ROS node
// (esp32_stepper_base_t, esp32_stepper_base_init, esp32_stepper_initialize)
// and the stepper decision logic.  The generated sources are C.

#ifdef __cplusplus
extern "C" {
#endif

#include "seed_imaging_microros_pkg/user_headers/esp32_stepper_src.h"
#include "seed_imaging_microros_pkg/user_headers/stepper_logic.h"

// GPIO hooks the node calls; the sketch defines them (extern "C")
void stepper_hw_init(void);
void stepper_hw_write(si_axis_t axis, const uint8_t in[4]);
void stepper_hw_delay_ms(uint32_t ms);

#ifdef __cplusplus
}
#endif

#endif  // SEED_IMAGING_STEPPER_NODE_H
