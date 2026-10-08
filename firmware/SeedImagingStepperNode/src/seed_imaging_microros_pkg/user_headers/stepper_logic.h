#ifndef SEED_IMAGING_STEPPER_LOGIC_H
#define SEED_IMAGING_STEPPER_LOGIC_H

// ============================================================================
//  Stepper decision logic of the ESP32 micro-ROS node (StepperController).
//
//  This is the C transcription of the Slang reference implementation in
//    hamr/slang/src/main/component/seedimaging/SeedImaging/StepperController_esp32_stepper.scala
//  which Logika proves against every GUMBO clause SI-MCU-1 .. SI-MCU-16 of
//    sysmlv2/SeedImaging.sysml
//  The same logic is exercised by tests/host/test_stepper_logic.c.
//
//  Pure C99, no ROS / Arduino dependencies: it compiles for the host
//  (micro-ROS host build, unit tests) and for the Arduino Nano ESP32.
//  This file is hand-written; HAMR codegen does not touch it.
// ============================================================================

#include <stdbool.h>
#include <stdint.h>

// Constants of the model's GUMBO library -- STEPS_PER_REV (200), MAX_MOVE_STEPS
// (400), TILT_LIMIT_STEPS (25 = 45 deg), STEP_INTERVAL_MS (5), SETTLE_MS (80) --
// generated from sysmlv2/SeedImaging.sysml by bin/gen_model_constants.py
#include "seed_imaging_microros_pkg/user_headers/si_model_constants.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum { SI_AXIS_Z = 0, SI_AXIS_X = 1, SI_AXIS_Y = 2 } si_axis_t;

// GUMBO state of StepperController: zPosition, xPosition, yPosition
typedef struct {
    int32_t z;   // turntable, steps in [0, 200)
    int32_t x;   // tilt, steps from level, in [-25, 25]
    int32_t y;   // reserved rotary axis, steps in [0, 200)
} si_positions_t;

// ---- GUMBO library predicates, 1:1 ------------------------------------------
static inline bool si_is_bounded_move(int32_t steps) {
    return (-SI_MAX_MOVE_STEPS <= steps) && (steps <= SI_MAX_MOVE_STEPS);
}

static inline bool si_is_tilt_safe(int32_t pos) {
    return (-SI_TILT_LIMIT_STEPS <= pos) && (pos <= SI_TILT_LIMIT_STEPS);
}

static inline bool si_is_rotary_index(int32_t pos) {
    return (0 <= pos) && (pos < SI_STEPS_PER_REV);
}

// C's % truncates toward zero exactly like Slang's, so the formula carries over
static inline int32_t si_wrap_rotary(int32_t pos) {
    return ((pos % SI_STEPS_PER_REV) + SI_STEPS_PER_REV) % SI_STEPS_PER_REV;
}

// && short-circuits like GUMBO 'and': the sum is only formed for bounded steps
static inline bool si_is_safe_tilt_move(int32_t tilt, int32_t steps) {
    return si_is_bounded_move(steps) && si_is_tilt_safe(tilt) && si_is_tilt_safe(tilt + steps);
}

// ---- the decision made for one incoming command ------------------------------
//  Updates *pos exactly as the verified Slang handler does and returns the
//  number of (signed) steps the motor must physically travel: 0 when the
//  command is rejected.  *accepted tells the two cases apart for logging.
static inline int32_t si_decide_move(si_positions_t * pos, si_axis_t axis, int32_t steps, bool * accepted)
{
    *accepted = false;
    switch (axis) {
    case SI_AXIS_Z:                                   // SI-MCU-4 / SI-MCU-5 / SI-MCU-6
        if (si_is_bounded_move(steps)) {
            pos->z = si_wrap_rotary(pos->z + steps);
            *accepted = true;
            return steps;
        }
        return 0;
    case SI_AXIS_X:                                   // SI-MCU-8 / SI-MCU-9 / SI-MCU-10
        if (si_is_safe_tilt_move(pos->x, steps)) {
            pos->x = pos->x + steps;
            *accepted = true;
            return steps;
        }
        return 0;
    case SI_AXIS_Y:                                   // SI-MCU-12 / SI-MCU-13 / SI-MCU-14
    default:
        if (si_is_bounded_move(steps)) {
            pos->y = si_wrap_rotary(pos->y + steps);
            *accepted = true;
            return steps;
        }
        return 0;
    }
}

// ---- full-step drive sequence for one L298N (IN1, IN2, IN3, IN4) -----------
//  Two coils energised in every phase (full torque).  Each row keeps the two
//  inputs of one H-bridge complementary, so IN1/IN2 and IN3/IN4 are never both
//  high: no shoot-through (the MR_HLR_10 requirement of the earlier model).
static const uint8_t SI_FULL_STEP_PHASES[4][4] = {
    { 1, 0, 1, 0 },
    { 0, 1, 1, 0 },
    { 0, 1, 0, 1 },
    { 1, 0, 0, 1 },
};

// next phase index in the given direction (+1 forward, -1 reverse)
static inline uint8_t si_next_phase(uint8_t phase, int32_t direction) {
    return (uint8_t) ((phase + (direction >= 0 ? 1u : 3u)) & 3u);
}

#ifdef __cplusplus
}
#endif

#endif  // SEED_IMAGING_STEPPER_LOGIC_H
