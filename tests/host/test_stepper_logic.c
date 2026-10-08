// Contract checks of the ESP32 decision logic that is actually flashed
// (hamr/ros2/microros_apps/seed_imaging_microros_pkg/.../stepper_logic.h).
//
// Every GUMBO clause SI-MCU-2 .. SI-MCU-15 of sysmlv2/SeedImaging.sysml is
// evaluated as an oracle, exhaustively over every in-range position of the
// commanded axis and every command in [-450, 450] plus the int32 extremes.
// The drive sequence is checked for the no-shoot-through rule.
//
//   gcc -std=c99 -Wall -Wextra -I<pkg>/include test_stepper_logic.c && ./a.out
#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include "seed_imaging_microros_pkg/user_headers/stepper_logic.h"

static long checks = 0, failures = 0;
#define CHECK(cond, ...) do { checks++; if (!(cond)) { failures++; if (failures <= 20) { printf("FAIL: "); printf(__VA_ARGS__); printf("\n"); } } } while (0)

// GUMBO oracle for one handled command (pre -> post)
static void oracle(si_positions_t pre, si_axis_t axis, int32_t steps, si_positions_t post, bool accepted)
{
    // general guarantees, every dispatch
    CHECK(si_is_tilt_safe(post.x), "SI-MCU-2 tilt %d", post.x);
    CHECK(si_is_rotary_index(post.z) && si_is_rotary_index(post.y), "SI-MCU-3 z=%d y=%d", post.z, post.y);

    if (axis == SI_AXIS_Z) {
        if (si_is_bounded_move(steps)) CHECK(post.z == si_wrap_rotary(pre.z + steps) && accepted, "SI-MCU-4 z %d%+d -> %d", pre.z, steps, post.z);
        else                           CHECK(post.z == pre.z && !accepted, "SI-MCU-5 z %d%+d -> %d", pre.z, steps, post.z);
        CHECK(post.x == pre.x && post.y == pre.y, "SI-MCU-6");
    } else if (axis == SI_AXIS_X) {
        if (si_is_safe_tilt_move(pre.x, steps)) CHECK(post.x == pre.x + steps && accepted, "SI-MCU-8 x %d%+d -> %d", pre.x, steps, post.x);
        else                                    CHECK(post.x == pre.x && !accepted, "SI-MCU-9 x %d%+d -> %d", pre.x, steps, post.x);
        CHECK(post.z == pre.z && post.y == pre.y, "SI-MCU-10");
    } else {
        if (si_is_bounded_move(steps)) CHECK(post.y == si_wrap_rotary(pre.y + steps) && accepted, "SI-MCU-12 y %d%+d -> %d", pre.y, steps, post.y);
        else                           CHECK(post.y == pre.y && !accepted, "SI-MCU-13 y %d%+d -> %d", pre.y, steps, post.y);
        CHECK(post.z == pre.z && post.x == pre.x, "SI-MCU-14");
    }
}

static void run(si_positions_t pre, si_axis_t axis, int32_t steps)
{
    si_positions_t post = pre;
    bool accepted = false;
    int32_t travel = si_decide_move(&post, axis, steps, &accepted);
    CHECK(travel == (accepted ? steps : 0), "travel %d for %d", travel, steps);
    oracle(pre, axis, steps, post, accepted);
}

int main(void)
{
    const int32_t extremes[] = { INT32_MIN, INT32_MIN + 1, -1000000, -401, 401, 1000000, INT32_MAX - 1, INT32_MAX };
    srand(20260924);

    // exhaustive over the commanded axis' position x command in [-450, 450] + extremes
    for (int axis = 0; axis < 3; axis++) {
        const int lo = axis == SI_AXIS_X ? -SI_TILT_LIMIT_STEPS : 0;
        const int hi = axis == SI_AXIS_X ?  SI_TILT_LIMIT_STEPS : SI_STEPS_PER_REV - 1;
        for (int p = lo; p <= hi; p++) {
            for (int s = -450; s <= 450 + (int) (sizeof extremes / sizeof extremes[0]); s++) {
                int32_t steps = s <= 450 ? s : extremes[s - 451];
                si_positions_t pre;
                pre.z = axis == SI_AXIS_Z ? p : rand() % 200;
                pre.x = axis == SI_AXIS_X ? p : rand() % 51 - 25;
                pre.y = axis == SI_AXIS_Y ? p : rand() % 200;
                run(pre, (si_axis_t) axis, steps);
            }
        }
    }

    // drive sequence: exactly two coils per phase, never both inputs of one bridge
    for (int ph = 0; ph < 4; ph++) {
        const uint8_t * in = SI_FULL_STEP_PHASES[ph];
        CHECK(!(in[0] && in[1]) && !(in[2] && in[3]), "shoot-through in phase %d", ph);
        CHECK(in[0] + in[1] == 1 && in[2] + in[3] == 1, "phase %d does not energise both coils", ph);
        // consecutive phases flip exactly one coil (one 1.8 deg step)
        const uint8_t * nx = SI_FULL_STEP_PHASES[si_next_phase((uint8_t) ph, 1)];
        int flipsA = in[0] != nx[0], flipsB = in[2] != nx[2];
        CHECK(flipsA + flipsB == 1, "phase %d -> next flips %d coils", ph, flipsA + flipsB);
        CHECK(si_next_phase(si_next_phase((uint8_t) ph, 1), -1) == ph, "reverse of forward is identity at %d", ph);
    }

    printf("stepper_logic: %ld checks, %ld failures\n", checks, failures);
    return failures == 0 ? 0 : 1;
}
