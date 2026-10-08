#include <Arduino.h>
#include <rcutils/logging.h>
#include <SeedImagingStepperNode.h>
#include "board.h"

// ---- pin map: the board as built (ROS2_Stepper_Build_Guide, section 1.2) -----
//   Motor 1 board (L298N #1)  Z turntable   IN1..IN4 = D13, D12, A0, A1
//   Motor 2 board (L298N #2)  X tilt        IN1..IN4 = D8,  D9,  D10, D11
//   Motor 3 board (L298N #3)  Y reserved    IN1..IN4 = D7,  D6,  D5,  D4
//   B0 is never used: it is a start-up pin.  D13 also drives the on-board LED.
//   Rows are in si_axis_t order (Z, X, Y).  If the wrong motor moves, swap rows
//   here (and in MotorTest.ino), not wires.
static const uint8_t AXIS_PINS[3][4] = {
  { D13, D12, A0,  A1  },   // Z turntable  (Motor 1 board)
  { D8,  D9,  D10, D11 },   // X tilt       (Motor 2 board)
  { D7,  D6,  D5,  D4  },   // Y reserved   (Motor 3 board)
};

// ---- GPIO hooks called by the HAMR-generated node (C linkage) ----------------
extern "C" void stepper_hw_init(void)
{
  for (int a = 0; a < 3; a++) {
    for (int i = 0; i < 4; i++) {
      pinMode(AXIS_PINS[a][i], OUTPUT);
      digitalWrite(AXIS_PINS[a][i], LOW);        // coils de-energised
    }
  }
}

extern "C" void stepper_hw_write(si_axis_t axis, const uint8_t in[4])
{
  for (int i = 0; i < 4; i++) {
    digitalWrite(AXIS_PINS[axis][i], in[i] ? HIGH : LOW);
  }
}

extern "C" void stepper_hw_delay_ms(uint32_t ms)
{
  delay(ms);
}

// ---- logging off ---------------------------------------------------------------
static void silent_log(const rcutils_log_location_t *, int, const char *,
                       rcutils_time_point_value_t, const char *, va_list *)
{
}

void silenceNodeLogging()
{
  rcutils_ret_t ret = rcutils_logging_initialize();
  (void) ret;
  rcutils_logging_set_output_handler(silent_log);
}

// ---- agent loss ------------------------------------------------------------------
void rebootWhenAgentLost()
{
  stepper_hw_init();                             // safe state first: all coils off
  delay(100);
  ESP.restart();
}
