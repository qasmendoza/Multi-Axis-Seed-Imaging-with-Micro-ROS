#include "seed_imaging_cpp_pkg/base_headers/jetson_scan_base_src.hpp"

// Content between markers will be preserved if codegen is rerun

class jetson_scan : public jetson_scan_base
{
public:
    jetson_scan();

private:
    //=================================================
    //  I n i t i a l i z e    E n t r y    P o i n t
    //=================================================
    void initialize();

    //=================================================
    //  C o m p u t e    E n t r y    P o i n t
    //=================================================
    void handle_start(const std_msgs::msg::Int32::SharedPtr msg);
    void handle_zPos(const std_msgs::msg::Int32::SharedPtr msg);
    void handle_xPos(const std_msgs::msg::Int32::SharedPtr msg);
    void handle_yPos(const std_msgs::msg::Int32::SharedPtr msg);

    //=================================================
    //  Include any additional declarations here
    //=================================================
    // Additions within these tags will be preserved when re-running Codegen
    // publishes the (at most one) move decided in a dispatch on its axis' topic
    void publishMove(int32_t axis, int32_t steps);
    // Additions within these tags will be preserved when re-running Codegen
};
