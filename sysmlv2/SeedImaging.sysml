//@ HAMR: --platform ros2 --ros2-output-workspace-dir ../hamr/ros2 --ros2-nodes-language Cpp --ros2-launch-language Xml
//@ HAMR: --platform JVM --slang-output-dir ../hamr/slang --package-name seedimaging

/*============================================================================
    Project : Multi-Axis Seed Imaging with Micro-ROS
    Model   : SysML v2 architecture model (HAMR / AADL profile) of the
              ROS 2  <->  micro-ROS  <->  3 x stepper motor control chain.

    This model is the single source of truth for the software architecture.
    From it HAMR generates:

      --platform ros2  ->  hamr/ros2
          * ROS 2 (rclcpp, C++) package for the Jetson node      (ScanController)
          * micro-ROS (rclc, C) package for the ESP32 node       (StepperController)
          * launch files, and the colcon.meta that sizes micro-ROS from the model

      --platform JVM   ->  hamr/slang
          * a Slang reference implementation of the same components with every
            GUMBO clause below turned into a Logika contract (formal proof) and
            a GUMBOX executable check (contract-based testing)

    Physical system (as built: ROS2_Stepper_Build_Guide, section 1.2):

      Jetson Orin Nano  --USB-C, XRCE-DDS over serial-->  Arduino Nano ESP32
      ESP32 D13,D12,A0,A1 -> L298N #1 IN1..IN4 -> NEMA 17 #1   Z  turntable
      ESP32 D8,D9,D10,D11 -> L298N #2 IN1..IN4 -> NEMA 17 #2   X  tilt
      ESP32 D7,D6,D5,D4   -> L298N #3 IN1..IN4 -> NEMA 17 #3   Y  reserved rotary

    Units: positions and commands are FULL STEPS of 1.8 deg (200 steps = one
    revolution).  Commands are RELATIVE moves; the sign is the direction.

    Requirement IDs used in the GUMBO clause names (SI_...) are the ones
    defined in SeedImaging_Requirements.sysml.
  ============================================================================*/

package SeedImaging {

    // HAMR_AADL_Preview must be imported BEFORE HAMR: it supplies the port defs
    // that carry the Ros_Topic_Name attribute used on the stepper's ports.
    private import HAMR_AADL_Preview::*;
    private import HAMR::*;

    //==========================================================================
    //  Shared GUMBO specification library (constants + predicates)
    //==========================================================================
    language "GUMBO" /*{
      library
        functions
          // ---- constants: the single source of the limits used by the code ----
          // (bin/gen_model_constants.py exports them to the C / C++ implementation)

          // 1.8 deg full steps: 200 steps per revolution
          def STEPS_PER_REV(): Base_Types::Integer_32 := 200 [s32];

          // Largest move accepted in one command: 2 revolutions
          def MAX_MOVE_STEPS(): Base_Types::Integer_32 := 400 [s32];

          // Tilt soft limit: +/-25 steps = +/-45 deg from level (the RPE 2 limit
          // that avoids bracket occlusion and keeps >90% surface coverage)
          def TILT_LIMIT_STEPS(): Base_Types::Integer_32 := 25 [s32];

          // Upper bound on the number of moves one scan plan may issue
          def MAX_SCAN_MOVES(): Base_Types::Integer_32 := 1000 [s32];

          // Drive timing (RPE 2): 200 steps/s, then 80 ms settle before acknowledging
          def STEP_INTERVAL_MS(): Base_Types::Integer_32 := 5 [s32];
          def SETTLE_MS(): Base_Types::Integer_32 := 80 [s32];

          // Worst-case duration of one StepperController dispatch (a maximal move):
          // 400 x 5 ms + 80 ms = 2080 ms; recorded as its Compute_Execution_Time
          def MAX_MOVE_TIME_MS(): Base_Types::Integer_32 :=
            MAX_MOVE_STEPS() * STEP_INTERVAL_MS() + SETTLE_MS();

          // ---- predicates -------------------------------------------------------
          def isBoundedMove(steps: Base_Types::Integer_32): Base_Types::Boolean :=
            (-MAX_MOVE_STEPS() <= steps) & (steps <= MAX_MOVE_STEPS());

          def isTiltSafe(pos: Base_Types::Integer_32): Base_Types::Boolean :=
            (-TILT_LIMIT_STEPS() <= pos) & (pos <= TILT_LIMIT_STEPS());

          // position of a continuous-rotation axis, kept modulo one revolution
          def isRotaryIndex(pos: Base_Types::Integer_32): Base_Types::Boolean :=
            (0 [s32] <= pos) & (pos < STEPS_PER_REV());

          // pos mod 200, mapped into [0, 200) also for negative pos
          def wrapRotary(pos: Base_Types::Integer_32): Base_Types::Integer_32 :=
            ((pos % STEPS_PER_REV()) + STEPS_PER_REV()) % STEPS_PER_REV();

          // a tilt move is executed only if it is bounded AND lands within +/-45 deg
          // ('and' short-circuits, so the sum is only formed for bounded steps)
          def isSafeTiltMove(tilt: Base_Types::Integer_32, steps: Base_Types::Integer_32): Base_Types::Boolean :=
            isBoundedMove(steps) and isTiltSafe(tilt) and isTiltSafe(tilt + steps);
    }*/

    //==========================================================================
    //  T o p - l e v e l   s y s t e m
    //==========================================================================

    part def SeedImagingSystem :> System {

        // ---- execution platforms ------------------------------------------
        part orin : JetsonOrinNano;          // Ubuntu 22.04 + ROS 2 Humble + micro-ROS agent
        part nano : ArduinoNanoESP32;        // FreeRTOS (Arduino core) + micro_ros_arduino

        // ---- software ----------------------------------------------------
        part jetson : JetsonSoftware;        // ROS 2 side
        part esp32  : Esp32Firmware;         // micro-ROS side

        // ---- ROS 2 -> micro-ROS : relative move commands (one topic per axis)
        connection zCmd : PortConnection connect jetson.zCmd to esp32.zCmd;
        connection xCmd : PortConnection connect jetson.xCmd to esp32.xCmd;
        connection yCmd : PortConnection connect jetson.yCmd to esp32.yCmd;

        // ---- micro-ROS -> ROS 2 : position acknowledgements
        connection zPos : PortConnection connect esp32.zPos to jetson.zPos;
        connection xPos : PortConnection connect esp32.xPos to jetson.xPos;
        connection yPos : PortConnection connect esp32.yPos to jetson.yPos;

        // ---- deployment ----------------------------------------------------
        allocation jetsonOnOrin : Deployment_Properties::Actual_Processor_Binding
            allocate jetson to orin;
        allocation firmwareOnNano : Deployment_Properties::Actual_Processor_Binding
            allocate esp32 to nano;
    }

    part def JetsonOrinNano :> Processor;
    part def ArduinoNanoESP32 :> Processor;

    //==========================================================================
    //  P r o c e s s e s
    //==========================================================================

    // Everything that runs on the Jetson as ordinary ROS 2 (rclcpp) nodes
    part def JetsonSoftware :> Process {
        out port zCmd : EventDataPort { out :>> type : std_msgs::Int32; }
        out port xCmd : EventDataPort { out :>> type : std_msgs::Int32; }
        out port yCmd : EventDataPort { out :>> type : std_msgs::Int32; }
        in  port zPos : EventDataPort { in  :>> type : std_msgs::Int32; }
        in  port xPos : EventDataPort { in  :>> type : std_msgs::Int32; }
        in  port yPos : EventDataPort { in  :>> type : std_msgs::Int32; }

        part scan : ScanController;

        connection c1 : PortConnection connect scan.zCmd to zCmd;
        connection c2 : PortConnection connect scan.xCmd to xCmd;
        connection c3 : PortConnection connect scan.yCmd to yCmd;
        connection c4 : PortConnection connect zPos to scan.zPos;
        connection c5 : PortConnection connect xPos to scan.xPos;
        connection c6 : PortConnection connect yPos to scan.yPos;
    }

    // Everything that runs on the Arduino Nano ESP32 as a micro-ROS (rclc) node
    part def Esp32Firmware :> Process {
        in  port zCmd : EventDataPort { in  :>> type : std_msgs::Int32; }
        in  port xCmd : EventDataPort { in  :>> type : std_msgs::Int32; }
        in  port yCmd : EventDataPort { in  :>> type : std_msgs::Int32; }
        out port zPos : EventDataPort { out :>> type : std_msgs::Int32; }
        out port xPos : EventDataPort { out :>> type : std_msgs::Int32; }
        out port yPos : EventDataPort { out :>> type : std_msgs::Int32; }

        part stepper : StepperController;

        connection c1 : PortConnection connect zCmd to stepper.zCmd;
        connection c2 : PortConnection connect xCmd to stepper.xCmd;
        connection c3 : PortConnection connect yCmd to stepper.yCmd;
        connection c4 : PortConnection connect stepper.zPos to zPos;
        connection c5 : PortConnection connect stepper.xPos to xPos;
        connection c6 : PortConnection connect stepper.yPos to yPos;
    }

    //==========================================================================
    //  T h r e a d s   ( = ROS nodes )
    //==========================================================================

    //--------------------------------------------------------------------------
    //  StepperController -- micro-ROS node on the Arduino Nano ESP32.
    //  Sporadic: each arriving command is one dispatch.  The handler drives the
    //  axis' four L298N inputs through the full-step sequence (5 ms per step),
    //  releases the coils, and acknowledges with the axis' new position.
    //--------------------------------------------------------------------------
    part def StepperController :> Thread {

        // commands: relative full steps, sign = direction
        in port zCmd : EventDataPort {
            in :>> type : std_msgs::Int32;
            attribute :>> Ros_Topic_Name = "/stepper/z/cmd";
        }
        in port xCmd : EventDataPort {
            in :>> type : std_msgs::Int32;
            attribute :>> Ros_Topic_Name = "/stepper/x/cmd";
        }
        in port yCmd : EventDataPort {
            in :>> type : std_msgs::Int32;
            attribute :>> Ros_Topic_Name = "/stepper/y/cmd";
        }

        // acknowledgements: the axis' position after the command was handled
        out port zPos : EventDataPort {
            out :>> type : std_msgs::Int32;
            attribute :>> Ros_Topic_Name = "/stepper/z/pos";
        }
        out port xPos : EventDataPort {
            out :>> type : std_msgs::Int32;
            attribute :>> Ros_Topic_Name = "/stepper/x/pos";
        }
        out port yPos : EventDataPort {
            out :>> type : std_msgs::Int32;
            attribute :>> Ros_Topic_Name = "/stepper/y/pos";
        }

        attribute :>> Dispatch_Protocol = Supported_Dispatch_Protocols::Sporadic;
        // worst case = a maximal move: MAX_MOVE_TIME_MS() = 400 x 5 ms + 80 ms
        attribute :>> Compute_Execution_Time = 0 [ms] .. 2080 [ms] as Compute_Execution_Time;
        attribute :>> Ros_Node_Kind = microRos;

        language "GUMBO" /*{
          state
            zPosition: Base_Types::Integer_32;   // turntable, steps in [0, 200)
            xPosition: Base_Types::Integer_32;   // tilt, steps from level, in [-25, 25]
            yPosition: Base_Types::Integer_32;   // reserved rotary axis, steps in [0, 200)

          integration
            // what the ESP32 promises about every tilt acknowledgement it publishes
            guarantee SI_MCU_16_tiltAckInRange "SI-MCU-16: Every published tilt position lies within +/-45 deg.":
              SeedImaging::GUMBO__Library::isTiltSafe(xPos.data);

          initialize
            modifies zPosition, xPosition, yPosition;

            guarantee SI_MCU_1_homeAtStartup "SI-MCU-1: At power-up all three axes are taken to be at home (0 steps).":
              (zPosition == 0 [s32]) & (xPosition == 0 [s32]) & (yPosition == 0 [s32]);

          compute
            modifies zPosition, xPosition, yPosition;

            // state invariant: established by initialize (all 0) and re-established
            // by every dispatch (SI-MCU-2, SI-MCU-3), so it may be assumed here
            assume SI_MCU_A1_positionsInRange "Positions are within their ranges when a command arrives.":
              SeedImaging::GUMBO__Library::isRotaryIndex(zPosition) &
              SeedImaging::GUMBO__Library::isTiltSafe(xPosition) &
              SeedImaging::GUMBO__Library::isRotaryIndex(yPosition);

            // ---- properties that hold after EVERY dispatch -----------------
            guarantee SI_MCU_2_tiltNeverBeyond45 "SI-MCU-2: The tilt axis never leaves +/-45 deg (+/-25 steps).":
              SeedImaging::GUMBO__Library::isTiltSafe(xPosition);

            guarantee SI_MCU_3_rotaryModuloOneRev "SI-MCU-3: Turntable and Y positions are kept modulo one revolution.":
              SeedImaging::GUMBO__Library::isRotaryIndex(zPosition) &
              SeedImaging::GUMBO__Library::isRotaryIndex(yPosition);

            // ---- Z : turntable ------------------------------------------------
            handle zCmd:
              modifies zPosition, xPosition, yPosition;

              guarantee SI_MCU_4_zExactMove "SI-MCU-4: A bounded turntable command rotates the turntable by exactly the commanded steps.":
                '->:'(SeedImaging::GUMBO__Library::isBoundedMove(zCmd.data),
                      zPosition == SeedImaging::GUMBO__Library::wrapRotary(In(zPosition) + zCmd.data));

              guarantee SI_MCU_5_zRejectOversize "SI-MCU-5: An oversize turntable command is rejected: no motion.":
                '->:'(not SeedImaging::GUMBO__Library::isBoundedMove(zCmd.data),
                      zPosition == In(zPosition));

              guarantee SI_MCU_6_zAxisIsolation "SI-MCU-6: A turntable command never moves the tilt or Y axis.":
                (xPosition == In(xPosition)) & (yPosition == In(yPosition));

              guarantee SI_MCU_7_zAcknowledged "SI-MCU-7: Every turntable command is acknowledged once, on zPos only, with the new turntable position.":
                MustSend(zPos) and (zPos.data == zPosition) & NoSend(xPos) & NoSend(yPos);

            // ---- X : tilt -----------------------------------------------------
            handle xCmd:
              modifies zPosition, xPosition, yPosition;

              guarantee SI_MCU_8_xExactMove "SI-MCU-8: A tilt command that is bounded and lands within +/-45 deg moves the tilt by exactly the commanded steps.":
                '->:'(SeedImaging::GUMBO__Library::isSafeTiltMove(In(xPosition), xCmd.data),
                      xPosition == In(xPosition) + xCmd.data);

              guarantee SI_MCU_9_xRejectUnsafe "SI-MCU-9: A tilt command that is oversize or would leave +/-45 deg is rejected: no motion.":
                '->:'(not SeedImaging::GUMBO__Library::isSafeTiltMove(In(xPosition), xCmd.data),
                      xPosition == In(xPosition));

              guarantee SI_MCU_10_xAxisIsolation "SI-MCU-10: A tilt command never moves the turntable or Y axis.":
                (zPosition == In(zPosition)) & (yPosition == In(yPosition));

              guarantee SI_MCU_11_xAcknowledged "SI-MCU-11: Every tilt command is acknowledged once, on xPos only, with the new tilt position.":
                MustSend(xPos) and (xPos.data == xPosition) & NoSend(zPos) & NoSend(yPos);

            // ---- Y : reserved rotary axis -----------------------------------
            handle yCmd:
              modifies zPosition, xPosition, yPosition;

              guarantee SI_MCU_12_yExactMove "SI-MCU-12: A bounded Y command rotates the Y axis by exactly the commanded steps.":
                '->:'(SeedImaging::GUMBO__Library::isBoundedMove(yCmd.data),
                      yPosition == SeedImaging::GUMBO__Library::wrapRotary(In(yPosition) + yCmd.data));

              guarantee SI_MCU_13_yRejectOversize "SI-MCU-13: An oversize Y command is rejected: no motion.":
                '->:'(not SeedImaging::GUMBO__Library::isBoundedMove(yCmd.data),
                      yPosition == In(yPosition));

              guarantee SI_MCU_14_yAxisIsolation "SI-MCU-14: A Y command never moves the turntable or tilt axis.":
                (zPosition == In(zPosition)) & (xPosition == In(xPosition));

              guarantee SI_MCU_15_yAcknowledged "SI-MCU-15: Every Y command is acknowledged once, on yPos only, with the new Y position.":
                MustSend(yPos) and (yPos.data == yPosition) & NoSend(zPos) & NoSend(xPos);
        }*/
    }

    //--------------------------------------------------------------------------
    //  ScanController -- ROS 2 node on the Jetson Orin Nano.
    //  Sporadic (event driven): an operator 'start' request launches the scan
    //  plan, and every acknowledgement from the ESP32 for the move in flight
    //  releases the next move.  So it never has more than one move in flight
    //  (the ESP32 keeps only the newest command per topic), it ignores stray
    //  acknowledgements (e.g. from a manual command typed during the demo), it
    //  never asks for a tilt beyond +/-45 deg, and it halts for good if the plan
    //  would require an unsafe tilt.
    //--------------------------------------------------------------------------
    part def ScanController :> Thread {

        // operator trigger, carrying the plan to run:
        //   ros2 topic pub --once /scan/start std_msgs/msg/Int32 "{data: 1}"
        //   1 = axis self-test (each motor moves and returns), 2 = imaging scan,
        //   anything else = self-test followed by the scan
        // (intentionally unconnected in the model -- the operator is off-model; the
        //  topic name is what binds it at deployment)
        in port start : EventDataPort {
            in :>> type : std_msgs::Int32;
            attribute :>> Ros_Topic_Name = "/scan/start";
        }

        out port zCmd : EventDataPort { out :>> type : std_msgs::Int32; }
        out port xCmd : EventDataPort { out :>> type : std_msgs::Int32; }
        out port yCmd : EventDataPort { out :>> type : std_msgs::Int32; }
        in  port zPos : EventDataPort { in  :>> type : std_msgs::Int32; }
        in  port xPos : EventDataPort { in  :>> type : std_msgs::Int32; }
        in  port yPos : EventDataPort { in  :>> type : std_msgs::Int32; }

        attribute :>> Dispatch_Protocol = Supported_Dispatch_Protocols::Sporadic;
        attribute :>> Ros_Node_Kind = ros2;

        language "GUMBO" /*{
          state
            pending: Base_Types::Boolean;            // a move was sent and is not yet acknowledged
            awaitAxis: Base_Types::Integer_32;       // axis of the move in flight: 0 = Z, 1 = X, 2 = Y
            tiltEstimate: Base_Types::Integer_32;    // last tilt position reported by the ESP32
            planIndex: Base_Types::Integer_32;       // index of the next move of the scan plan
            planEnd: Base_Types::Integer_32;         // end (exclusive) of the plan being run
            halted: Base_Types::Boolean;             // scan stopped for good: a move would be unsafe

          functions
            def cmdSent(z: Base_Types::Boolean, x: Base_Types::Boolean, y: Base_Types::Boolean): Base_Types::Boolean :=
              z | x | y;

            def isAxis(a: Base_Types::Integer_32): Base_Types::Boolean :=
              (0 [s32] <= a) & (a <= 2 [s32]);

          integration
            // relies on the ESP32's SI-MCU-16 (the two meet at the xPos connection)
            assume SI_HOST_A1_tiltAckInRange "Tilt acknowledgements lie within +/-45 deg.":
              SeedImaging::GUMBO__Library::isTiltSafe(xPos.data);

          initialize
            modifies pending, awaitAxis, tiltEstimate, planIndex, planEnd, halted;

            guarantee SI_HOST_1_startIdle "SI-HOST-1: The scan controller starts idle: no move in flight, tilt assumed level, plan at its first move.":
              (not pending) & (awaitAxis == 0 [s32]) & (tiltEstimate == 0 [s32]) &
              (planIndex == 0 [s32]) & (planEnd == 0 [s32]) & (not halted);

          compute
            modifies pending, awaitAxis, tiltEstimate, planIndex, planEnd, halted;

            // state invariant: established by initialize and re-established by every
            // dispatch (SI-HOST-8, SI-HOST-10, SI-HOST-7)
            assume SI_HOST_A2_stateInRange "The controller's state is within range when an event arrives.":
              SeedImaging::GUMBO__Library::isTiltSafe(tiltEstimate) & isAxis(awaitAxis) &
              (0 [s32] <= planIndex) & (planIndex <= planEnd) &
              (planEnd <= SeedImaging::GUMBO__Library::MAX_SCAN_MOVES());

            // ---- properties that hold after EVERY dispatch -----------------
            guarantee SI_HOST_2_oneCommandPerDispatch "SI-HOST-2: At most one move command is issued per dispatch.":
              ('->:'(MustSend(zCmd), NoSend(xCmd) & NoSend(yCmd))) &
              ('->:'(MustSend(xCmd), NoSend(yCmd)));

            guarantee SI_HOST_3_oneMoveInFlight "SI-HOST-3: While a move is unacknowledged, no new move is issued unless this very event is its acknowledgement.":
              '->:'(In(pending) &
                      (not ((HasEvent(zPos) & (In(awaitAxis) == 0 [s32])) |
                            (HasEvent(xPos) & (In(awaitAxis) == 1 [s32])) |
                            (HasEvent(yPos) & (In(awaitAxis) == 2 [s32])))),
                    NoSend(zCmd) & NoSend(xCmd) & NoSend(yCmd));

            // written with 'or' (short-circuit) so xCmd.data is only read when a tilt
            // command was actually sent
            guarantee SI_HOST_4_tiltCommandsSafe "SI-HOST-4: A tilt command is only issued if it is bounded and its target stays within +/-45 deg.":
              NoSend(xCmd) or SeedImaging::GUMBO__Library::isSafeTiltMove(tiltEstimate, xCmd.data);

            guarantee SI_HOST_6_awaitCommandedAxis "SI-HOST-6: Issuing a move makes it the move in flight, on the commanded axis.":
              ('->:'(MustSend(zCmd), pending & (awaitAxis == 0 [s32]))) &
              ('->:'(MustSend(xCmd), pending & (awaitAxis == 1 [s32]))) &
              ('->:'(MustSend(yCmd), pending & (awaitAxis == 2 [s32])));

            guarantee SI_HOST_7_stateInRange "SI-HOST-7: The awaited axis and the plan position stay within range (a plan never exceeds MAX_SCAN_MOVES moves).":
              isAxis(awaitAxis) &
              (0 [s32] <= planIndex) & (planIndex <= planEnd) &
              (planEnd <= SeedImaging::GUMBO__Library::MAX_SCAN_MOVES());

            guarantee SI_HOST_8_haltIsFinal "SI-HOST-8: Once halted, the scan controller stays halted and issues no further moves.":
              '->:'(In(halted), halted & NoSend(zCmd) & NoSend(xCmd) & NoSend(yCmd));

            guarantee SI_HOST_9_noMoveNoPending "SI-HOST-9: If no move is issued, a move is pending only if one already was.":
              '->:'(not cmdSent(MustSend(zCmd), MustSend(xCmd), MustSend(yCmd)),
                    '->:'(pending, In(pending)));

            guarantee SI_HOST_10_tiltEstimateInRange "SI-HOST-10: The tilt estimate always lies within +/-45 deg.":
              SeedImaging::GUMBO__Library::isTiltSafe(tiltEstimate);

            // ---- operator start request ---------------------------------------
            handle start:
              modifies pending, awaitAxis, tiltEstimate, planIndex, planEnd, halted;

              guarantee SI_HOST_11_startIgnoredWhenBusy "SI-HOST-11: A start request while a move is in flight, or after a halt, changes nothing.":
                '->:'(In(pending) | In(halted),
                      NoSend(zCmd) & NoSend(xCmd) & NoSend(yCmd) &
                      (pending == In(pending)) & (awaitAxis == In(awaitAxis)) &
                      (planIndex == In(planIndex)) & (planEnd == In(planEnd)) & (halted == In(halted)));

              guarantee SI_HOST_5_startKeepsTilt "SI-HOST-5: The tilt estimate changes only on a tilt acknowledgement.":
                tiltEstimate == In(tiltEstimate);

              guarantee SI_HOST_12_startLaunchesPlan "SI-HOST-12: A start request while idle launches the selected plan: its first move is issued, or the scan halts.":
                '->:'((not In(pending)) & (not In(halted)),
                      cmdSent(MustSend(zCmd), MustSend(xCmd), MustSend(yCmd)) | halted);

            // ---- acknowledgements from the ESP32 ------------------------------
            handle zPos:
              modifies pending, awaitAxis, tiltEstimate, planIndex, planEnd, halted;

              guarantee SI_HOST_5_zKeepsTilt "SI-HOST-5: The tilt estimate changes only on a tilt acknowledgement.":
                tiltEstimate == In(tiltEstimate);

              guarantee SI_HOST_13_zStrayAckIgnored "SI-HOST-13: A turntable acknowledgement that is not awaited changes nothing and issues nothing.":
                '->:'(not (In(pending) & (In(awaitAxis) == 0 [s32])),
                      NoSend(zCmd) & NoSend(xCmd) & NoSend(yCmd) &
                      (pending == In(pending)) & (awaitAxis == In(awaitAxis)) &
                      (planIndex == In(planIndex)) & (planEnd == In(planEnd)) & (halted == In(halted)));

              guarantee SI_HOST_14_zAckAdvancesPlan "SI-HOST-14: The awaited turntable acknowledgement completes the move; the next move (if any) advances the plan by one.":
                '->:'(In(pending) & (In(awaitAxis) == 0 [s32]),
                      ('->:'(cmdSent(MustSend(zCmd), MustSend(xCmd), MustSend(yCmd)), planIndex == In(planIndex) + 1 [s32])) &
                      ('->:'(not cmdSent(MustSend(zCmd), MustSend(xCmd), MustSend(yCmd)), (not pending) & (planIndex == In(planIndex)))) &
                      (planEnd == In(planEnd)));

            handle xPos:
              modifies pending, awaitAxis, tiltEstimate, planIndex, planEnd, halted;

              guarantee SI_HOST_5_xTracksTilt "SI-HOST-5: A tilt acknowledgement always updates the tilt estimate to the reported position.":
                tiltEstimate == xPos.data;

              guarantee SI_HOST_15_xStrayAckIgnored "SI-HOST-15: A tilt acknowledgement that is not awaited only updates the tilt estimate.":
                '->:'(not (In(pending) & (In(awaitAxis) == 1 [s32])),
                      NoSend(zCmd) & NoSend(xCmd) & NoSend(yCmd) &
                      (pending == In(pending)) & (awaitAxis == In(awaitAxis)) &
                      (planIndex == In(planIndex)) & (planEnd == In(planEnd)) & (halted == In(halted)));

              guarantee SI_HOST_16_xAckAdvancesPlan "SI-HOST-16: The awaited tilt acknowledgement completes the move; the next move (if any) advances the plan by one.":
                '->:'(In(pending) & (In(awaitAxis) == 1 [s32]),
                      ('->:'(cmdSent(MustSend(zCmd), MustSend(xCmd), MustSend(yCmd)), planIndex == In(planIndex) + 1 [s32])) &
                      ('->:'(not cmdSent(MustSend(zCmd), MustSend(xCmd), MustSend(yCmd)), (not pending) & (planIndex == In(planIndex)))) &
                      (planEnd == In(planEnd)));

            handle yPos:
              modifies pending, awaitAxis, tiltEstimate, planIndex, planEnd, halted;

              guarantee SI_HOST_5_yKeepsTilt "SI-HOST-5: The tilt estimate changes only on a tilt acknowledgement.":
                tiltEstimate == In(tiltEstimate);

              guarantee SI_HOST_17_yStrayAckIgnored "SI-HOST-17: A Y acknowledgement that is not awaited changes nothing and issues nothing.":
                '->:'(not (In(pending) & (In(awaitAxis) == 2 [s32])),
                      NoSend(zCmd) & NoSend(xCmd) & NoSend(yCmd) &
                      (pending == In(pending)) & (awaitAxis == In(awaitAxis)) &
                      (planIndex == In(planIndex)) & (planEnd == In(planEnd)) & (halted == In(halted)));

              guarantee SI_HOST_18_yAckAdvancesPlan "SI-HOST-18: The awaited Y acknowledgement completes the move; the next move (if any) advances the plan by one.":
                '->:'(In(pending) & (In(awaitAxis) == 2 [s32]),
                      ('->:'(cmdSent(MustSend(zCmd), MustSend(xCmd), MustSend(yCmd)), planIndex == In(planIndex) + 1 [s32])) &
                      ('->:'(not cmdSent(MustSend(zCmd), MustSend(xCmd), MustSend(yCmd)), (not pending) & (planIndex == In(planIndex)))) &
                      (planEnd == In(planEnd)));
        }*/
    }
}
