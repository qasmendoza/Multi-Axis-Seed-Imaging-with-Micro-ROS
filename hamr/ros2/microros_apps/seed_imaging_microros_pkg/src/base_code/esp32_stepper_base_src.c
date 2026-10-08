#include "seed_imaging_microros_pkg/base_headers/esp32_stepper_base_src.h"

// Content between markers will be preserved if codegen is rerun

// Forward declarations of user compute entry points
void esp32_stepper_handle_zCmd(esp32_stepper_base_t * self, const std_msgs__msg__Int32 * msg);
void esp32_stepper_handle_xCmd(esp32_stepper_base_t * self, const std_msgs__msg__Int32 * msg);
void esp32_stepper_handle_yCmd(esp32_stepper_base_t * self, const std_msgs__msg__Int32 * msg);

// Static instance pointer for subscription callback context (heap-free, MCU-compatible)
static esp32_stepper_base_t * g_self = NULL;

// Logger name used by the LOG_* macros; updated to the node's actual logger
// name once the node has been initialized
const char * esp32_stepper_logger_name = "esp32_stepper";

// NODE OPTIONS - additions within these tags will be preserved when re-running Codegen
// Add rcl arguments after "--ros-args", e.g. a remap rule binding one of this
// node's topics to a preexisting node's topic:
//     "-r", "some_port:=/some/other/topic"
// Write the match side of a remap rule relative (no leading '/') so that it keeps
// matching if the node is later placed in a namespace.
static const char * const node_options[] = {
    "--ros-args"
};
// NODE OPTIONS - additions within these tags will be preserved when re-running Codegen

// USER DECLARATIONS - additions within these tags will be preserved when re-running Codegen
// Storage for message fields codegen could not size from the model, e.g. a sequence
// or string field of a platform-provided type whose mirror declares no dimensions:
//     static float joy_axes_buf[8];
// USER DECLARATIONS - additions within these tags will be preserved when re-running Codegen

static void esp32_stepper_sendOutputs(esp32_stepper_base_t * self)
{
    if (self->esp32_stepper_zPos_out_hasValue) {
        rcl_ret_t ret = rcl_publish(&self->esp32_stepper_zPos_publisher, &self->esp32_stepper_zPos_out, NULL);
        if (ret != RCL_RET_OK) {
            LOG_ERROR("Failed to publish zPos");
        }
        self->esp32_stepper_zPos_out_hasValue = false;
    }
    if (self->esp32_stepper_xPos_out_hasValue) {
        rcl_ret_t ret = rcl_publish(&self->esp32_stepper_xPos_publisher, &self->esp32_stepper_xPos_out, NULL);
        if (ret != RCL_RET_OK) {
            LOG_ERROR("Failed to publish xPos");
        }
        self->esp32_stepper_xPos_out_hasValue = false;
    }
    if (self->esp32_stepper_yPos_out_hasValue) {
        rcl_ret_t ret = rcl_publish(&self->esp32_stepper_yPos_publisher, &self->esp32_stepper_yPos_out, NULL);
        if (ret != RCL_RET_OK) {
            LOG_ERROR("Failed to publish yPos");
        }
        self->esp32_stepper_yPos_out_hasValue = false;
    }
}

//=================================================
//  S u b s c r i p t i o n   C a l l b a c k s
//=================================================

static void esp32_stepper_zCmd_subscription_callback(const void * msgin)
{
    const std_msgs__msg__Int32 * msg = (const std_msgs__msg__Int32 *) msgin;
    if (g_self != NULL) {
        esp32_stepper_handle_zCmd(g_self, msg);
        esp32_stepper_sendOutputs(g_self);
    }
}

static void esp32_stepper_xCmd_subscription_callback(const void * msgin)
{
    const std_msgs__msg__Int32 * msg = (const std_msgs__msg__Int32 *) msgin;
    if (g_self != NULL) {
        esp32_stepper_handle_xCmd(g_self, msg);
        esp32_stepper_sendOutputs(g_self);
    }
}

static void esp32_stepper_yCmd_subscription_callback(const void * msgin)
{
    const std_msgs__msg__Int32 * msg = (const std_msgs__msg__Int32 *) msgin;
    if (g_self != NULL) {
        esp32_stepper_handle_yCmd(g_self, msg);
        esp32_stepper_sendOutputs(g_self);
    }
}

//=================================================
//  I n i t i a l i z a t i o n
//=================================================

rcl_ret_t esp32_stepper_base_init(esp32_stepper_base_t * self)
{
    g_self = self;

    self->allocator = rcl_get_default_allocator();

    rcl_init_options_t init_options = rcl_get_zero_initialized_init_options();
    RCL_CHECK(rcl_init_options_init(&init_options, self->allocator));

    RCL_CHECK(rclc_support_init_with_options(
        &self->support,
        (int) (sizeof(node_options) / sizeof(node_options[0])), node_options,
        &init_options, &self->allocator));

    RCL_CHECK(rclc_node_init_default(&self->node, "esp32_stepper", "", &self->support));

    // Retrieve the node's registered logger name for use by the LOG_* macros
    const char * logger_name = rcl_node_get_logger_name(&self->node);
    if (logger_name != NULL) {
        esp32_stepper_logger_name = logger_name;
    }

    // Setting up connections
    RCL_CHECK(rclc_publisher_init_default(
        &self->esp32_stepper_zPos_publisher,
        &self->node,
        ROSIDL_GET_MSG_TYPE_SUPPORT(std_msgs, msg, Int32),
        "/stepper/z/pos"));

    RCL_CHECK(rclc_publisher_init_default(
        &self->esp32_stepper_xPos_publisher,
        &self->node,
        ROSIDL_GET_MSG_TYPE_SUPPORT(std_msgs, msg, Int32),
        "/stepper/x/pos"));

    RCL_CHECK(rclc_publisher_init_default(
        &self->esp32_stepper_yPos_publisher,
        &self->node,
        ROSIDL_GET_MSG_TYPE_SUPPORT(std_msgs, msg, Int32),
        "/stepper/y/pos"));

    // Setting up subscriptions
    RCL_CHECK(rclc_subscription_init_default(
        &self->esp32_stepper_zCmd_subscription,
        &self->node,
        ROSIDL_GET_MSG_TYPE_SUPPORT(std_msgs, msg, Int32),
        "/stepper/z/cmd"));

    RCL_CHECK(rclc_subscription_init_default(
        &self->esp32_stepper_xCmd_subscription,
        &self->node,
        ROSIDL_GET_MSG_TYPE_SUPPORT(std_msgs, msg, Int32),
        "/stepper/x/cmd"));

    RCL_CHECK(rclc_subscription_init_default(
        &self->esp32_stepper_yCmd_subscription,
        &self->node,
        ROSIDL_GET_MSG_TYPE_SUPPORT(std_msgs, msg, Int32),
        "/stepper/y/cmd"));


    // Staged outputs start empty
    self->esp32_stepper_zPos_out_hasValue = false;
    self->esp32_stepper_xPos_out_hasValue = false;
    self->esp32_stepper_yPos_out_hasValue = false;


    // USER INIT - additions within these tags will be preserved when re-running Codegen
    // Attach storage declared above to the corresponding message fields, e.g.:
    //     self->proc_ttj_joy_msg.axes.data = joy_axes_buf;
    //     self->proc_ttj_joy_msg.axes.capacity = 8;
    //     self->proc_ttj_joy_msg.axes.size = 0;
    // USER INIT - additions within these tags will be preserved when re-running Codegen

    RCL_CHECK(rclc_executor_init(&self->executor, &self->support.context, 3, &self->allocator));
    RCL_CHECK(rclc_executor_add_subscription(&self->executor, &self->esp32_stepper_zCmd_subscription, &self->esp32_stepper_zCmd_msg, esp32_stepper_zCmd_subscription_callback, ON_NEW_DATA));
    RCL_CHECK(rclc_executor_add_subscription(&self->executor, &self->esp32_stepper_xCmd_subscription, &self->esp32_stepper_xCmd_msg, esp32_stepper_xCmd_subscription_callback, ON_NEW_DATA));
    RCL_CHECK(rclc_executor_add_subscription(&self->executor, &self->esp32_stepper_yCmd_subscription, &self->esp32_stepper_yCmd_msg, esp32_stepper_yCmd_subscription_callback, ON_NEW_DATA));

    return RCL_RET_OK;
}

void esp32_stepper_base_spin(esp32_stepper_base_t * self)
{
    rclc_executor_spin(&self->executor);
}

//=================================================
//  C o m m u n i c a t i o n
//=================================================

void put_zPos(esp32_stepper_base_t * self, std_msgs__msg__Int32 * msg)
{
    self->esp32_stepper_zPos_out = *msg;
    self->esp32_stepper_zPos_out_hasValue = true;
}

void put_xPos(esp32_stepper_base_t * self, std_msgs__msg__Int32 * msg)
{
    self->esp32_stepper_xPos_out = *msg;
    self->esp32_stepper_xPos_out_hasValue = true;
}

void put_yPos(esp32_stepper_base_t * self, std_msgs__msg__Int32 * msg)
{
    self->esp32_stepper_yPos_out = *msg;
    self->esp32_stepper_yPos_out_hasValue = true;
}

