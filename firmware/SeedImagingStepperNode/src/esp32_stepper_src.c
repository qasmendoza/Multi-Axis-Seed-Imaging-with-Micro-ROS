#include "seed_imaging_microros_pkg/user_headers/esp32_stepper_src.h"
#include "seed_imaging_microros_pkg/user_headers/stepper_logic.h"

// This file will not be overwritten if HAMR codegen is rerun

// ============================================================================
//  StepperController -- behaviour of the ESP32 micro-ROS node
//
//  HAMR generated everything around this file from sysmlv2/SeedImaging.sysml:
//  the node, the subscriptions /stepper/{z,x,y}/cmd, the publishers
//  /stepper/{z,x,y}/pos, the rclc executor, and the put_* API.  This file only
//  supplies the entry points.  The decision logic lives in stepper_logic.h and
//  mirrors the Logika-verified Slang reference implementation.
//
//  The same source builds two ways:
//    * ARDUINO defined  -> Arduino Nano ESP32 firmware; the four GPIO routines
//                          below are provided by firmware/SeedImagingStepper.ino
//    * otherwise        -> host (Linux) micro-ROS build used for simulation; the
//                          GPIO routines are simulated and every coil write is
//                          checked against the no-shoot-through rule
// ============================================================================

#if defined(ARDUINO)
// provided by the Arduino sketch (real GPIO: D2..D5, D6..D9, D10..D13)
extern void stepper_hw_init(void);
extern void stepper_hw_write(si_axis_t axis, const uint8_t in[4]);
extern void stepper_hw_delay_ms(uint32_t ms);
#else
#include <time.h>

static unsigned long g_coil_writes = 0;

static void stepper_hw_init(void) { }

static void stepper_hw_write(si_axis_t axis, const uint8_t in[4])
{
    (void) axis;
    // simulated L298N input: never both inputs of one H-bridge high
    if ((in[0] && in[1]) || (in[2] && in[3])) {
        LOG_ERROR("SHOOT-THROUGH pattern %d%d%d%d on axis %d", in[0], in[1], in[2], in[3], (int) axis);
    }
    g_coil_writes++;
}

static void stepper_hw_delay_ms(uint32_t ms)
{
    struct timespec ts;
    ts.tv_sec = (time_t) (ms / 1000u);
    ts.tv_nsec = (long) (ms % 1000u) * 1000000L;
    nanosleep(&ts, NULL);
}
#endif

// ---- GUMBO state (zPosition, xPosition, yPosition) + per-axis drive phase ----
static si_positions_t g_pos;
static uint8_t g_phase[3];

static const uint8_t COILS_OFF[4] = { 0, 0, 0, 0 };

static inline const char * axis_name(si_axis_t axis)
{
    return axis == SI_AXIS_Z ? "Z turntable" : (axis == SI_AXIS_X ? "X tilt" : "Y reserved");
}

// Physically travel `steps` full steps (sign = direction) on one axis:
// 5 ms per step, hold 80 ms to settle, then release the coils (the L298N has
// no current limiting, so the coils are not left energised between moves).
static void drive(si_axis_t axis, int32_t steps)
{
    const int32_t direction = steps >= 0 ? 1 : -1;
    int32_t remaining = steps >= 0 ? steps : -steps;
    while (remaining > 0) {
        g_phase[axis] = si_next_phase(g_phase[axis], direction);
        stepper_hw_write(axis, SI_FULL_STEP_PHASES[g_phase[axis]]);
        stepper_hw_delay_ms(SI_STEP_INTERVAL_MS);
        remaining--;
    }
    if (steps != 0) {
        stepper_hw_delay_ms(SI_SETTLE_MS);
    }
    stepper_hw_write(axis, COILS_OFF);
}

// One dispatch: decide (verified logic), move, acknowledge on the axis' topic.
static void handle_command(esp32_stepper_base_t * self, si_axis_t axis, int32_t steps)
{
    bool accepted = false;
    const int32_t travel = si_decide_move(&g_pos, axis, steps, &accepted);

    if (accepted) {
        drive(axis, travel);
        LOG_INFO("%s: moved %ld steps -> position %ld", axis_name(axis), (long) travel,
                 (long) (axis == SI_AXIS_Z ? g_pos.z : (axis == SI_AXIS_X ? g_pos.x : g_pos.y)));
    } else {
        LOG_WARN("%s: command %ld rejected (outside the model's limits), no motion", axis_name(axis), (long) steps);
    }

    // SI-MCU-7 / 11 / 15: every command is acknowledged once, on its own axis only
    std_msgs__msg__Int32 ack;
    switch (axis) {
    case SI_AXIS_Z: ack.data = g_pos.z; put_zPos(self, &ack); break;
    case SI_AXIS_X: ack.data = g_pos.x; put_xPos(self, &ack); break;
    case SI_AXIS_Y:
    default:        ack.data = g_pos.y; put_yPos(self, &ack); break;
    }
}

//=================================================
//  I n i t i a l i z e    E n t r y    P o i n t
//=================================================
void esp32_stepper_initialize(esp32_stepper_base_t * self)
{
    (void) self;
    // SI-MCU-1: all three axes are taken to be at home at power-up
    g_pos.z = 0;
    g_pos.x = 0;
    g_pos.y = 0;
    g_phase[SI_AXIS_Z] = g_phase[SI_AXIS_X] = g_phase[SI_AXIS_Y] = 0;

    stepper_hw_init();
    stepper_hw_write(SI_AXIS_Z, COILS_OFF);
    stepper_hw_write(SI_AXIS_X, COILS_OFF);
    stepper_hw_write(SI_AXIS_Y, COILS_OFF);

    LOG_INFO("StepperController ready: Z, X and Y at home, coils released");
}

//=================================================
//  C o m p u t e    E n t r y    P o i n t
//=================================================
void esp32_stepper_handle_zCmd(esp32_stepper_base_t * self, const std_msgs__msg__Int32 * msg)
{
    handle_command(self, SI_AXIS_Z, msg->data);
}

void esp32_stepper_handle_xCmd(esp32_stepper_base_t * self, const std_msgs__msg__Int32 * msg)
{
    handle_command(self, SI_AXIS_X, msg->data);
}

void esp32_stepper_handle_yCmd(esp32_stepper_base_t * self, const std_msgs__msg__Int32 * msg)
{
    handle_command(self, SI_AXIS_Y, msg->data);
}
