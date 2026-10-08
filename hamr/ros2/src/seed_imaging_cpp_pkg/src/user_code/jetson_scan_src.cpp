#include "seed_imaging_cpp_pkg/user_headers/jetson_scan_src.hpp"
#include "seed_imaging_cpp_pkg/user_headers/scan_logic.hpp"

#include <mutex>

// This file will not be overwritten if HAMR codegen is rerun

// ============================================================================
//  ScanController -- behaviour of the Jetson ROS 2 node
//
//  HAMR generated the node around this file from sysmlv2/SeedImaging.sysml:
//  the subscriptions /scan/start and /stepper/{z,x,y}/pos, the publishers
//  /stepper/{z,x,y}/cmd, and the put_* API.  This file only supplies the entry
//  points.  The decisions are made by seed_imaging::ScanLogic (scan_logic.hpp),
//  a transcription of the Logika-verified Slang reference implementation.
//
//  Operator:
//    ros2 topic pub --once /scan/start std_msgs/msg/Int32 "{data: 1}"   # self-test
//    ros2 topic pub --once /scan/start std_msgs/msg/Int32 "{data: 2}"   # imaging scan
//    ros2 topic pub --once /scan/start std_msgs/msg/Int32 "{data: 0}"   # both
// ============================================================================

namespace {
// GUMBO state of the component (one node per process)
seed_imaging::ScanLogic g_logic;
// The generated callback group is Reentrant, so ROS may run two handlers at the
// same time.  One dispatch at a time is the AADL sporadic semantics the
// contracts assume, so every entry point takes this lock.
std::mutex g_dispatch;

const char * axisName(int32_t axis)
{
    return axis == seed_imaging::AXIS_Z ? "Z turntable" : (axis == seed_imaging::AXIS_X ? "X tilt" : "Y reserved");
}
}  // namespace

void jetson_scan::publishMove(int32_t axis, int32_t steps)
{
    std_msgs::msg::Int32 cmd;
    cmd.data = steps;
    switch (axis) {
    case seed_imaging::AXIS_Z: put_zCmd(cmd); break;
    case seed_imaging::AXIS_X: put_xCmd(cmd); break;
    default:                   put_yCmd(cmd); break;
    }
    LOG_INFO("plan move %d/%d: %s %+d steps", (int) g_logic.planIndex, (int) g_logic.planEnd,
             axisName(axis), (int) steps);
}

//=================================================
//  I n i t i a l i z e    E n t r y    P o i n t
//=================================================
void jetson_scan::initialize()
{
    std::lock_guard<std::mutex> lock(g_dispatch);
    g_logic.initialise();                                   // SI-HOST-1
    LOG_INFO("ScanController ready. Start a plan with: "
             "ros2 topic pub --once /scan/start std_msgs/msg/Int32 \"{data: 1}\"  "
             "(1 = self-test, 2 = imaging scan, other = both)");
}

//=================================================
//  C o m p u t e    E n t r y    P o i n t
//=================================================
void jetson_scan::handle_start(const std_msgs::msg::Int32::SharedPtr msg)
{
    std::lock_guard<std::mutex> lock(g_dispatch);
    const bool busy = g_logic.pending || g_logic.halted;
    const seed_imaging::Command c = g_logic.onStart(msg->data);
    if (busy) {
        LOG_WARN("start ignored: %s", g_logic.halted ? "scan is halted" : "a move is still in flight");
        return;
    }
    LOG_INFO("start: running plan %d (moves %d..%d)", (int) msg->data, (int) (g_logic.planIndex - (c.present ? 1 : 0)),
             (int) (g_logic.planEnd - 1));
    if (c.present) {
        publishMove(c.axis, c.steps);
    } else if (g_logic.halted) {
        LOG_ERROR("scan halted: the next tilt move would leave +/-45 deg (estimate %d)", (int) g_logic.tiltEstimate);
    }
}

static void onAck(jetson_scan & node, rclcpp::Logger logger, int32_t axis, int32_t position,
                  void (jetson_scan::*publish)(int32_t, int32_t))
{
    std::lock_guard<std::mutex> lock(g_dispatch);
    const bool awaited = g_logic.pending && g_logic.awaitAxis == axis;
    const seed_imaging::Command c = g_logic.onAck(axis, position);
    if (!awaited) {
        RCLCPP_INFO(logger, "%s at %d (not a move of this plan; ignored)", axisName(axis), (int) position);
        return;
    }
    RCLCPP_INFO(logger, "%s acknowledged at position %d", axisName(axis), (int) position);
    if (c.present) {
        (node.*publish)(c.axis, c.steps);
    } else if (g_logic.halted) {
        RCLCPP_ERROR(logger, "scan halted: the next tilt move would leave +/-45 deg (estimate %d)",
                     (int) g_logic.tiltEstimate);
    } else if (g_logic.finished()) {
        RCLCPP_INFO(logger, "plan complete (%d moves). Ready for the next start request.", (int) g_logic.planEnd);
    }
}

void jetson_scan::handle_zPos(const std_msgs::msg::Int32::SharedPtr msg)
{
    onAck(*this, this->get_logger(), seed_imaging::AXIS_Z, msg->data, &jetson_scan::publishMove);
}

void jetson_scan::handle_xPos(const std_msgs::msg::Int32::SharedPtr msg)
{
    onAck(*this, this->get_logger(), seed_imaging::AXIS_X, msg->data, &jetson_scan::publishMove);
}

void jetson_scan::handle_yPos(const std_msgs::msg::Int32::SharedPtr msg)
{
    onAck(*this, this->get_logger(), seed_imaging::AXIS_Y, msg->data, &jetson_scan::publishMove);
}
