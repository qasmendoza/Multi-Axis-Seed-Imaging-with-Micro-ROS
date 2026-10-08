#ifndef SEED_IMAGING_SCAN_LOGIC_HPP
#define SEED_IMAGING_SCAN_LOGIC_HPP

// ============================================================================
//  Decision logic of the Jetson ROS 2 node (ScanController).
//
//  C++ transcription of the Slang reference implementation in
//    hamr/slang/src/main/component/seedimaging/SeedImaging/ScanController_jetson_scan.scala
//  which Logika proves against every GUMBO clause SI-HOST-1 .. SI-HOST-18 of
//    sysmlv2/SeedImaging.sysml
//  Checked again by tests/host/test_scan_logic.cpp.
//
//  Header-only, no ROS dependency.  Hand-written; HAMR codegen does not touch it.
// ============================================================================

#include <cstdint>

// constants of the model's GUMBO library, generated from sysmlv2/SeedImaging.sysml
// by bin/gen_model_constants.py
#include "seed_imaging_cpp_pkg/user_headers/si_model_constants.h"

namespace seed_imaging {

constexpr int32_t MAX_MOVE_STEPS   = SI_MAX_MOVE_STEPS;    // MAX_MOVE_STEPS()
constexpr int32_t TILT_LIMIT_STEPS = SI_TILT_LIMIT_STEPS;  // TILT_LIMIT_STEPS()  (+/-45 deg)
constexpr int32_t MAX_SCAN_MOVES   = SI_MAX_SCAN_MOVES;    // MAX_SCAN_MOVES()

constexpr bool isBoundedMove(int32_t steps) { return -MAX_MOVE_STEPS <= steps && steps <= MAX_MOVE_STEPS; }
constexpr bool isTiltSafe(int32_t pos) { return -TILT_LIMIT_STEPS <= pos && pos <= TILT_LIMIT_STEPS; }
constexpr bool isSafeTiltMove(int32_t tilt, int32_t steps) {
    return isBoundedMove(steps) && isTiltSafe(tilt) && isTiltSafe(tilt + steps);
}

enum Axis : int32_t { AXIS_Z = 0, AXIS_X = 1, AXIS_Y = 2 };

// ---- scan plan (implementation data; the contracts hold for any plan) -------
//    self-test  (start data = 1)  k = 0 .. 5
//      Z +200, X +25, X -50, X +25, Y +200, Z -200
//    imaging scan  (start data = 2)  k = 6 .. 23
//      8 x Z +25 (level), X +25, 8 x Z +25 (at +45 deg), X -25
//    anything else: the self-test followed by the scan  (k = 0 .. 23)
constexpr int32_t SELF_TEST_END = 6;
constexpr int32_t PLAN_LENGTH   = 24;

constexpr int32_t planAxis(int32_t k) {
    return (k == 1 || k == 2 || k == 3 || k == 14 || k == 23) ? AXIS_X : (k == 4 ? AXIS_Y : AXIS_Z);
}
constexpr int32_t planSteps(int32_t k) {
    return (k == 0 || k == 4) ? 200 : (k == 2 ? -50 : (k == 5 ? -200 : (k == 23 ? -25 : 25)));
}

// A command the node must publish in this dispatch (at most one: SI-HOST-2)
struct Command {
    bool present = false;
    int32_t axis = AXIS_Z;
    int32_t steps = 0;
};

// ---- GUMBO state + the four entry points --------------------------------------
struct ScanLogic {
    bool pending = false;        // a move was sent and is not yet acknowledged
    int32_t awaitAxis = AXIS_Z;  // axis of the move in flight
    int32_t tiltEstimate = 0;    // last tilt position reported by the ESP32
    int32_t planIndex = 0;       // next move of the plan
    int32_t planEnd = 0;         // end (exclusive) of the plan being run
    bool halted = false;         // stopped for good: a move would be unsafe

    // SI-HOST-1
    void initialise() {
        pending = false; awaitAxis = AXIS_Z; tiltEstimate = 0;
        planIndex = 0; planEnd = 0; halted = false;
    }

    // SI-HOST-11 / SI-HOST-12
    Command onStart(int32_t selection) {
        Command c;
        if (!pending && !halted) {
            if (selection == 1) { planIndex = 0; planEnd = SELF_TEST_END; }
            else if (selection == 2) { planIndex = SELF_TEST_END; planEnd = PLAN_LENGTH; }
            else { planIndex = 0; planEnd = PLAN_LENGTH; }
            c = issueNext();
        }
        return c;
    }

    // SI-HOST-5/10 (tilt only), SI-HOST-13..18
    Command onAck(int32_t axis, int32_t position) {
        Command c;
        if (axis == AXIS_X) {
            tiltEstimate = position;
        }
        if (pending && awaitAxis == axis) {
            pending = false;
            c = issueNext();
        }
        return c;
    }

    bool finished() const { return !pending && !halted && planIndex >= planEnd; }

private:
    // SI-HOST-2/4/6/7 : the next move of the plan, if any
    Command issueNext() {
        Command c;
        if (!halted && planIndex < planEnd) {
            const int32_t axis = planAxis(planIndex);
            const int32_t steps = planSteps(planIndex);
            if (axis == AXIS_X && !isSafeTiltMove(tiltEstimate, steps)) {
                halted = true;                  // SI-HOST-4 / SI-HOST-8
                return c;
            }
            c.present = true;
            c.axis = axis;
            c.steps = steps;
            pending = true;
            awaitAxis = axis;
            planIndex = planIndex + 1;
        }
        return c;
    }
};

}  // namespace seed_imaging

#endif  // SEED_IMAGING_SCAN_LOGIC_HPP
