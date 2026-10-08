/*============================================================================
    Requirements of the Seed Imaging control chain (Jetson ROS 2 node  <->
    micro-ROS node on the Arduino Nano ESP32  <->  3 stepper axes).

    SI-SYS-*   system requirements (what the prelim demo shows)
    SI-MCU-*   StepperController (ESP32, micro-ROS) -- formalised as the GUMBO
               clauses of the same name in SeedImaging.sysml
    SI-HOST-*  ScanController (Jetson, ROS 2)        -- ditto

    Each SI-MCU / SI-HOST requirement is a GUMBO clause, so it is
      * type-checked with the model (sireum hamr sysml tipe),
      * proved by Logika on the Slang reference implementation,
      * executed as an oracle by the GUMBOX tests, the host C/C++ tests and
        the end-to-end check (tests/).
    The satisfy relations to the architecture are in
    ../sysml-trace/SeedImaging_Trace.sysml.
  ============================================================================*/

package SeedImaging_Requirements {

    //==========================================================================
    //  System requirements
    //==========================================================================

    requirement def <'SI-SYS-1'> ThreeAxisRosControl {
        doc /* The Jetson shall command each of the three stepper axes (Z turntable,
             * X tilt, Y reserved) through ROS 2 and micro-ROS, one topic per axis:
             * /stepper/z/cmd, /stepper/x/cmd, /stepper/y/cmd (std_msgs/Int32,
             * relative full steps, sign = direction).
             * Realised by: the model's connections and Ros_Topic_Name properties;
             * refined by SI-MCU-4, -8, -12. */
    }

    requirement def <'SI-SYS-2'> PositionFeedback {
        doc /* Every command shall be answered with the axis' resulting position on
             * /stepper/z/pos, /stepper/x/pos, /stepper/y/pos.
             * Refined by SI-MCU-7, -11, -15, -16. */
    }

    requirement def <'SI-SYS-3'> TiltLimit {
        doc /* The tilt stage shall never be driven beyond +/-45 deg from level
             * (+/-25 full steps; RPE 2 mitigation for bracket occlusion).
             * Refined by SI-MCU-2, -8, -9 (ESP32) and SI-HOST-4, -10 (Jetson):
             * the limit is enforced on both sides of the link. */
    }

    requirement def <'SI-SYS-4'> NoLostCommands {
        doc /* No move command shall be lost: the ESP32 keeps only the newest command
             * per topic (AADL Queue_Size 1), so the Jetson shall have at most one
             * move in flight.  Refined by SI-HOST-2, -3, -6, -9, -13 .. -18. */
    }

    requirement def <'SI-SYS-5'> ScanPlan {
        doc /* On an operator request (/scan/start) the Jetson shall run the selected
             * plan -- axis self-test, imaging scan (two rings of 8 views, 45 deg
             * apart, at level and at +45 deg tilt), or both -- one move after the
             * other.  Refined by SI-HOST-1, -7, -11, -12, -14, -16, -18. */
    }

    requirement def <'SI-SYS-6'> FailSafe {
        doc /* Out-of-limit commands shall be rejected without motion, a plan that
             * would need an unsafe tilt shall halt for good, and the ESP32 shall
             * de-energise the coils after every move and before rebooting when the
             * agent is lost.  Refined by SI-MCU-5, -9, -13 and SI-HOST-8. */
    }

    //==========================================================================
    //  StepperController (Arduino Nano ESP32, micro-ROS)
    //==========================================================================

    requirement def <'SI-MCU-1'> HomeAtStartup {
        doc /* At power-up all three axes are taken to be at home (0 steps). */
    }
    requirement def <'SI-MCU-2'> TiltNeverBeyond45 {
        doc /* The tilt axis never leaves +/-45 deg (+/-25 steps). */
    }
    requirement def <'SI-MCU-3'> RotaryModuloOneRevolution {
        doc /* Turntable and Y positions are kept modulo one revolution (0 .. 199). */
    }
    requirement def <'SI-MCU-4'> TurntableExactMove {
        doc /* A bounded turntable command (|steps| <= 400) rotates the turntable by
             * exactly the commanded steps. */
    }
    requirement def <'SI-MCU-5'> TurntableRejectOversize {
        doc /* An oversize turntable command is rejected: no motion. */
    }
    requirement def <'SI-MCU-6'> TurntableAxisIsolation {
        doc /* A turntable command never moves the tilt or Y axis. */
    }
    requirement def <'SI-MCU-7'> TurntableAcknowledged {
        doc /* Every turntable command is acknowledged once, on zPos only, with the
             * new turntable position. */
    }
    requirement def <'SI-MCU-8'> TiltExactMove {
        doc /* A tilt command that is bounded and lands within +/-45 deg moves the tilt
             * by exactly the commanded steps. */
    }
    requirement def <'SI-MCU-9'> TiltRejectUnsafe {
        doc /* A tilt command that is oversize or would leave +/-45 deg is rejected:
             * no motion. */
    }
    requirement def <'SI-MCU-10'> TiltAxisIsolation {
        doc /* A tilt command never moves the turntable or Y axis. */
    }
    requirement def <'SI-MCU-11'> TiltAcknowledged {
        doc /* Every tilt command is acknowledged once, on xPos only, with the new tilt
             * position. */
    }
    requirement def <'SI-MCU-12'> YExactMove {
        doc /* A bounded Y command rotates the Y axis by exactly the commanded steps. */
    }
    requirement def <'SI-MCU-13'> YRejectOversize {
        doc /* An oversize Y command is rejected: no motion. */
    }
    requirement def <'SI-MCU-14'> YAxisIsolation {
        doc /* A Y command never moves the turntable or tilt axis. */
    }
    requirement def <'SI-MCU-15'> YAcknowledged {
        doc /* Every Y command is acknowledged once, on yPos only, with the new Y
             * position. */
    }
    requirement def <'SI-MCU-16'> TiltAckInRange {
        doc /* Every published tilt position lies within +/-45 deg (integration
             * guarantee on xPos, matched by the Jetson's integration assumption
             * SI-HOST-A1 on the same connection). */
    }

    //==========================================================================
    //  ScanController (Jetson Orin Nano, ROS 2)
    //==========================================================================

    requirement def <'SI-HOST-1'> StartIdle {
        doc /* The scan controller starts idle: no move in flight, tilt assumed level,
             * no plan selected. */
    }
    requirement def <'SI-HOST-2'> OneCommandPerDispatch {
        doc /* At most one move command is issued per dispatch. */
    }
    requirement def <'SI-HOST-3'> OneMoveInFlight {
        doc /* While a move is unacknowledged, no new move is issued unless the event
             * being handled is its acknowledgement. */
    }
    requirement def <'SI-HOST-4'> TiltCommandsSafe {
        doc /* A tilt command is only issued if it is bounded and its target stays
             * within +/-45 deg. */
    }
    requirement def <'SI-HOST-5'> TrackTilt {
        doc /* The tilt estimate changes only on a tilt acknowledgement, and then
             * becomes the reported position. */
    }
    requirement def <'SI-HOST-6'> AwaitCommandedAxis {
        doc /* Issuing a move makes it the move in flight, on the commanded axis. */
    }
    requirement def <'SI-HOST-7'> StateInRange {
        doc /* The awaited axis and the plan position stay within range; a plan never
             * exceeds MAX_SCAN_MOVES moves. */
    }
    requirement def <'SI-HOST-8'> HaltIsFinal {
        doc /* Once halted, the scan controller stays halted and issues no moves. */
    }
    requirement def <'SI-HOST-9'> NoMoveNoPending {
        doc /* If no move is issued, a move is pending only if one already was. */
    }
    requirement def <'SI-HOST-10'> TiltEstimateInRange {
        doc /* The tilt estimate always lies within +/-45 deg. */
    }
    requirement def <'SI-HOST-11'> StartIgnoredWhenBusy {
        doc /* A start request while a move is in flight, or after a halt, changes
             * nothing. */
    }
    requirement def <'SI-HOST-12'> StartLaunchesPlan {
        doc /* A start request while idle launches the selected plan: its first move is
             * issued, or the scan halts. */
    }
    requirement def <'SI-HOST-13'> TurntableStrayAckIgnored {
        doc /* A turntable acknowledgement that is not awaited changes nothing. */
    }
    requirement def <'SI-HOST-14'> TurntableAckAdvancesPlan {
        doc /* The awaited turntable acknowledgement completes the move; the next move
             * (if any) advances the plan by one. */
    }
    requirement def <'SI-HOST-15'> TiltStrayAckIgnored {
        doc /* A tilt acknowledgement that is not awaited only updates the estimate. */
    }
    requirement def <'SI-HOST-16'> TiltAckAdvancesPlan {
        doc /* The awaited tilt acknowledgement completes the move; the next move
             * (if any) advances the plan by one. */
    }
    requirement def <'SI-HOST-17'> YStrayAckIgnored {
        doc /* A Y acknowledgement that is not awaited changes nothing. */
    }
    requirement def <'SI-HOST-18'> YAckAdvancesPlan {
        doc /* The awaited Y acknowledgement completes the move; the next move (if any)
             * advances the plan by one. */
    }
}
