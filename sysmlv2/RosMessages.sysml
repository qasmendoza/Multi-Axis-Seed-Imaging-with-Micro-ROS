/*============================================================================
    Platform-provided ROS 2 message types used by the Seed Imaging system.

    HAMR structural convention: SysML package name = ROS package name, and
    part def name = message name.  So std_msgs::Int32 below *denotes* the
    stock ROS 2 message std_msgs/msg/Int32 -- HAMR emits no .msg file for it
    and the generated code uses the native type directly.

    Why stock messages: the ESP32 runs the precompiled micro_ros_arduino
    library, which already contains std_msgs.  A custom message would force a
    rebuild of that library.  Stock types also let anyone drive the motors from
    the command line, e.g.

        ros2 topic pub --once /stepper/z/cmd std_msgs/msg/Int32 "{data: 50}"

    The mirror field `data` is what lets the GUMBO contracts in
    SeedImaging.sysml talk about the value that is carried
    (e.g. zCmd.data, xPos.data).
  ============================================================================*/

package std_msgs {
    private import HAMR::*;

    // std_msgs/msg/Int32  --  native definition:  int32 data
    part def Int32 :> Data {
        attribute :>> Type_Provenance = Provenances::Platform_Provided;

        part data : Base_Types::Integer_32;
    }

    // std_msgs/msg/Empty  --  no fields; used as a pure trigger
    // (ros2 topic pub --once /scan/start std_msgs/msg/Empty "{}")
    part def Empty :> Data {
        attribute :>> Type_Provenance = Provenances::Platform_Provided;
    }
}
