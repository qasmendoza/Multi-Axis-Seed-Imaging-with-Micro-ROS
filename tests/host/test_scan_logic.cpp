// Contract checks of the Jetson decision logic that actually runs
// (hamr/ros2/src/seed_imaging_cpp_pkg/.../scan_logic.hpp).
//
// Every GUMBO clause SI-HOST-2 .. SI-HOST-18 of sysmlv2/SeedImaging.sysml is
// evaluated as an oracle over every in-range controller state and every event:
// start (plan 0/1/2/other) and acknowledgements on Z, X, Y.
//
//   g++ -std=c++17 -Wall -Wextra -I<pkg>/include test_scan_logic.cpp && ./a.out
#include <cstdio>
#include <vector>
#include "seed_imaging_cpp_pkg/user_headers/scan_logic.hpp"

using namespace seed_imaging;

static long checks = 0, failures = 0;
#define CHECK(cond, ...) do { checks++; if (!(cond)) { failures++; if (failures <= 20) { std::printf("FAIL: "); std::printf(__VA_ARGS__); std::printf("\n"); } } } while (0)

static bool isAxis(int32_t a) { return 0 <= a && a <= 2; }

enum Kind { START, ACK };

static void oracle(const ScanLogic & pre, const ScanLogic & post, Kind kind, int32_t axis, int32_t value, const Command & c)
{
    const bool sentZ = c.present && c.axis == AXIS_Z, sentX = c.present && c.axis == AXIS_X, sentY = c.present && c.axis == AXIS_Y;
    const bool sent = c.present;
    const bool awaitedAck = kind == ACK && pre.pending && pre.awaitAxis == axis;

    // general guarantees
    CHECK(!(sentZ && (sentX || sentY)) && !(sentX && sentY), "SI-HOST-2");
    CHECK(!(pre.pending && !awaitedAck) || !sent, "SI-HOST-3 kind=%d axis=%d", kind, axis);
    CHECK(!sentX || isSafeTiltMove(post.tiltEstimate, c.steps), "SI-HOST-4 tilt %d %+d", post.tiltEstimate, c.steps);
    CHECK(!sentZ || (post.pending && post.awaitAxis == AXIS_Z), "SI-HOST-6 z");
    CHECK(!sentX || (post.pending && post.awaitAxis == AXIS_X), "SI-HOST-6 x");
    CHECK(!sentY || (post.pending && post.awaitAxis == AXIS_Y), "SI-HOST-6 y");
    CHECK(isAxis(post.awaitAxis) && 0 <= post.planIndex && post.planIndex <= post.planEnd && post.planEnd <= MAX_SCAN_MOVES, "SI-HOST-7");
    CHECK(!pre.halted || (post.halted && !sent), "SI-HOST-8");
    CHECK(sent || !post.pending || pre.pending, "SI-HOST-9");
    CHECK(isTiltSafe(post.tiltEstimate), "SI-HOST-10");

    // SI-HOST-5: tilt estimate
    if (kind == ACK && axis == AXIS_X) CHECK(post.tiltEstimate == value, "SI-HOST-5 track");
    else                               CHECK(post.tiltEstimate == pre.tiltEstimate, "SI-HOST-5 keep");

    const bool unchanged = !sent && post.pending == pre.pending && post.awaitAxis == pre.awaitAxis &&
                           post.planIndex == pre.planIndex && post.planEnd == pre.planEnd && post.halted == pre.halted;
    if (kind == START) {
        if (pre.pending || pre.halted) CHECK(unchanged, "SI-HOST-11");
        else                           CHECK(sent || post.halted, "SI-HOST-12");
    } else {
        if (!awaitedAck) {
            CHECK(unchanged, "SI-HOST-13/15/17 stray ack axis %d", axis);
        } else {
            if (sent) CHECK(post.planIndex == pre.planIndex + 1, "SI-HOST-14/16/18 advance");
            else      CHECK(!post.pending && post.planIndex == pre.planIndex, "SI-HOST-14/16/18 no move");
            CHECK(post.planEnd == pre.planEnd, "SI-HOST-14/16/18 planEnd");
        }
    }
}

int main()
{
    const int32_t ends[] = { 0, SELF_TEST_END, PLAN_LENGTH };
    const int32_t selections[] = { 0, 1, 2, 7, -3 };
    long states = 0;
    for (int pending = 0; pending < 2; pending++)
    for (int32_t await = 0; await <= 2; await++)
    for (int32_t tilt = -TILT_LIMIT_STEPS; tilt <= TILT_LIMIT_STEPS; tilt++)
    for (int32_t end : ends)
    for (int32_t idx = 0; idx <= end; idx++)
    for (int halted = 0; halted < 2; halted++) {
        ScanLogic pre;
        pre.pending = pending; pre.awaitAxis = await; pre.tiltEstimate = tilt;
        pre.planIndex = idx; pre.planEnd = end; pre.halted = halted;
        states++;
        for (int32_t sel : selections) {
            ScanLogic post = pre;
            Command c = post.onStart(sel);
            oracle(pre, post, START, 0, sel, c);
        }
        for (int32_t axis = 0; axis <= 2; axis++) {
            std::vector<int32_t> values;
            if (axis == AXIS_X) for (int32_t v = -TILT_LIMIT_STEPS; v <= TILT_LIMIT_STEPS; v++) values.push_back(v);
            else values = { 0, 1, 25, 199 };
            for (int32_t v : values) {
                ScanLogic post = pre;
                Command c = post.onAck(axis, v);
                oracle(pre, post, ACK, axis, v, c);
            }
        }
    }

    // the concrete plans: replay them against an ideal ESP32 and check they finish
    for (int32_t sel : { 1, 2, 0 }) {
        ScanLogic s; s.initialise();
        int32_t z = 0, x = 0, y = 0, moves = 0;
        Command c = s.onStart(sel);
        while (c.present && moves < 100) {
            moves++;
            int32_t ackAxis = c.axis, ackVal;
            if (c.axis == AXIS_Z) { z = ((z + c.steps) % 200 + 200) % 200; ackVal = z; }
            else if (c.axis == AXIS_X) { x = x + c.steps; ackVal = x; }
            else { y = ((y + c.steps) % 200 + 200) % 200; ackVal = y; }
            CHECK(isTiltSafe(x), "plan %d drives tilt to %d", sel, x);
            c = s.onAck(ackAxis, ackVal);
        }
        const int32_t expected = sel == 1 ? SELF_TEST_END : (sel == 2 ? PLAN_LENGTH - SELF_TEST_END : PLAN_LENGTH);
        CHECK(s.finished() && moves == expected && z == 0 && x == 0 && y == 0,
              "plan %d: %d moves, finished=%d, ends at z=%d x=%d y=%d", sel, moves, s.finished(), z, x, y);
    }

    std::printf("scan_logic: %ld states, %ld checks, %ld failures\n", states, checks, failures);
    return failures == 0 ? 0 : 1;
}
