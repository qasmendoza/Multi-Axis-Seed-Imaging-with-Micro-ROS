#ifndef SEED_IMAGING_BOARD_H
#define SEED_IMAGING_BOARD_H

// Board-specific glue for the Arduino Nano ESP32 (kept out of the .ino so the
// Arduino prototype generator never has to guess these signatures).

// switches rcutils logging off: the USB serial link carries XRCE-DDS traffic
void silenceNodeLogging();

// safe state (all coils off), then reboot; setup() waits for the agent again
void rebootWhenAgentLost();

#endif  // SEED_IMAGING_BOARD_H
