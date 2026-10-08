// ============================================================================
//  MotorTest -- plain motor test for the Arduino Nano ESP32 (no ROS 2)
//
//  Build guide, section 2.  Proves the wiring and the motors work before
//  micro-ROS is involved, and shows which physical motor is on which board.
//
//  Upload, open Tools > Serial Monitor at 115200 baud, type one letter, Enter:
//    z   Motor 1 board (Z turntable): 10 steps forward, then 10 steps back
//    x   Motor 2 board (X tilt)     : 10 steps forward, then 10 steps back
//    y   Motor 3 board (Y reserved) : 10 steps forward, then 10 steps back
//  10 steps = 18 degrees, small enough for the tilt.  The coils are switched
//  off after every move, so the L298N boards stay cool.
//
//  The pin table, step pattern and speed are the same as in the real firmware
//  (SeedImagingStepper/board.cpp and the generated stepper_logic.h).  If the
//  wrong motor moves, swap the rows here AND in board.cpp.
// ============================================================================

static const uint8_t AXIS_PINS[3][4] = {
  { D13, D12, A0,  A1  },   // z: Z turntable  (Motor 1 board)
  { D8,  D9,  D10, D11 },   // x: X tilt       (Motor 2 board)
  { D7,  D6,  D5,  D4  },   // y: Y reserved   (Motor 3 board)
};

static const char *const AXIS_NAME[3] = {
  "z = Motor 1 board, Z turntable (D13 D12 A0 A1)",
  "x = Motor 2 board, X tilt (D8 D9 D10 D11)",
  "y = Motor 3 board, Y reserved (D7 D6 D5 D4)",
};

// full-step pattern (IN1, IN2, IN3, IN4): two coils on in every phase
static const uint8_t PHASES[4][4] = {
  { 1, 0, 1, 0 },
  { 0, 1, 1, 0 },
  { 0, 1, 0, 1 },
  { 1, 0, 0, 1 },
};
static const uint8_t COILS_OFF[4] = { 0, 0, 0, 0 };

static const int TEST_STEPS = 10;   // 18 degrees
static const int STEP_MS = 5;       // 200 steps per second, as in the firmware
static const int SETTLE_MS = 80;

static uint8_t phase[3] = { 0, 0, 0 };

static void writeCoils(int axis, const uint8_t in[4])
{
  for (int i = 0; i < 4; i++) {
    digitalWrite(AXIS_PINS[axis][i], in[i] ? HIGH : LOW);
  }
}

static void moveSteps(int axis, int steps)
{
  const int count = steps >= 0 ? steps : -steps;
  for (int n = 0; n < count; n++) {
    phase[axis] = (uint8_t) ((phase[axis] + (steps >= 0 ? 1 : 3)) & 3);
    writeCoils(axis, PHASES[phase[axis]]);
    delay(STEP_MS);
  }
  delay(SETTLE_MS);
  writeCoils(axis, COILS_OFF);
}

void setup()
{
  for (int a = 0; a < 3; a++) {
    for (int i = 0; i < 4; i++) {
      pinMode(AXIS_PINS[a][i], OUTPUT);
    }
    writeCoils(a, COILS_OFF);
  }
  Serial.begin(115200);
  delay(1500);
  Serial.println("MotorTest ready. Type z, x or y and press Enter.");
}

void loop()
{
  if (Serial.available() <= 0) {
    return;
  }
  const char c = (char) tolower(Serial.read());
  const int axis = c == 'z' ? 0 : (c == 'x' ? 1 : (c == 'y' ? 2 : -1));
  if (axis < 0) {
    return;                         // Enter, spaces and other keys are ignored
  }
  Serial.print("Moving ");
  Serial.println(AXIS_NAME[axis]);
  moveSteps(axis, TEST_STEPS);
  delay(300);
  moveSteps(axis, -TEST_STEPS);
  Serial.println("  done: 10 steps forward and 10 back, coils off");
}
