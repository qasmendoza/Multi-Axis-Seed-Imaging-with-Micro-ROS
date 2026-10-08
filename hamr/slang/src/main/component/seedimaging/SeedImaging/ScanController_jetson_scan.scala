// #Sireum

package seedimaging.SeedImaging

import org.sireum._
import seedimaging._
import org.sireum.S32._

// This file will not be overwritten if HAMR codegen is rerun
object ScanController_jetson_scan {

  // BEGIN STATE VARS
  var pending: Base_Types.Boolean = Base_Types.Boolean_example()

  var awaitAxis: Base_Types.Integer_32 = Base_Types.Integer_32_example()

  var tiltEstimate: Base_Types.Integer_32 = Base_Types.Integer_32_example()

  var planIndex: Base_Types.Integer_32 = Base_Types.Integer_32_example()

  var planEnd: Base_Types.Integer_32 = Base_Types.Integer_32_example()

  var halted: Base_Types.Boolean = Base_Types.Boolean_example()
  // END STATE VARS

  // BEGIN FUNCTIONS
  @strictpure def cmdSent(z: Base_Types.Boolean, x: Base_Types.Boolean, y: Base_Types.Boolean): Base_Types.Boolean = z | x |
    y

  @strictpure def isAxis(a: Base_Types.Integer_32): Base_Types.Boolean = s32"0" <= a &
    a <= s32"2"
  // END FUNCTIONS

  //------------------------------------------------------------------------
  //  Scan plan (implementation data -- the contracts hold for ANY plan).
  //  Move k is (planAxis(k), planSteps(k)): axis 0 = Z turntable, 1 = X tilt,
  //  2 = Y reserved; steps are relative full steps (1.8 deg each).
  //
  //    self-test  (start data = 1)  k = 0 .. 5
  //      k = 0      Z +200   one turntable revolution
  //      k = 1      X  +25   tilt to +45 deg
  //      k = 2      X  -50   tilt to -45 deg
  //      k = 3      X  +25   back to level
  //      k = 4      Y +200   one revolution of the reserved axis
  //      k = 5      Z -200   turntable back
  //    imaging scan  (start data = 2)  k = 6 .. 23
  //      k = 6..13  Z  +25   ring 1: 8 views, 45 deg apart, tilt level
  //      k = 14     X  +25   tilt to +45 deg
  //      k = 15..22 Z  +25   ring 2: 8 views at +45 deg tilt
  //      k = 23     X  -25   back to level
  //    anything else: the self-test followed by the scan  (k = 0 .. 23)
  //------------------------------------------------------------------------
  @strictpure def SELF_TEST_END: S32 = s32"6"

  @strictpure def PLAN_LENGTH: S32 = s32"24"

  @strictpure def planAxis(k: S32): S32 =
    if (k == s32"1" | k == s32"2" | k == s32"3" | k == s32"14" | k == s32"23") s32"1"
    else if (k == s32"4") s32"2"
    else s32"0"

  @strictpure def planSteps(k: S32): S32 =
    if (k == s32"0" | k == s32"4") s32"200"
    else if (k == s32"2") s32"-50"
    else if (k == s32"5") s32"-200"
    else if (k == s32"23") s32"-25"
    else s32"25"

  def initialise(api: ScanController_Initialization_Api): Unit = {
    Contract(
      Requires(
        // BEGIN INITIALIZES REQUIRES
        // assume AADL_Requirement
        //   All outgoing event ports must be empty
        api.zCmd.isEmpty,
        api.xCmd.isEmpty,
        api.yCmd.isEmpty
        // END INITIALIZES REQUIRES
      ),
      Modifies(
        api, // the put_ calls below write the api's outgoing port spec vars
        // BEGIN INITIALIZES MODIFIES
        pending,
        awaitAxis,
        tiltEstimate,
        planIndex,
        planEnd,
        halted
        // END INITIALIZES MODIFIES
      ),
      Ensures(
        // BEGIN INITIALIZES ENSURES
        // guarantee SI_HOST_1_startIdle
        //   SI-HOST-1: The scan controller starts idle: no move in flight, tilt assumed level, plan at its first move.
        !pending &
          awaitAxis == s32"0" &
          tiltEstimate == s32"0" &
          planIndex == s32"0" &
          planEnd == s32"0" &
          !halted
        // END INITIALIZES ENSURES
      )
    )
    // SI-HOST-1: idle, tilt assumed level, no plan selected yet
    pending = F
    awaitAxis = s32"0"
    tiltEstimate = s32"0"
    planIndex = s32"0"
    planEnd = s32"0"
    halted = F
  }

  def handle_start(api: ScanController_Operational_Api, value: std_msgs.Int32): Unit = {
    Contract(
      Requires(
        // BEGIN COMPUTE REQUIRES start
        // assume HAMR-Guarantee built-in
        //   The spec var corresponding to the handled event must be non-empty and
        //   the passed in payload must be the same as the spec var's value
        api.start.nonEmpty &&
        api.start.get == value,
        // assume AADL_Requirement
        //   All outgoing event ports must be empty
        api.zCmd.isEmpty,
        api.xCmd.isEmpty,
        api.yCmd.isEmpty,
        // assume SI_HOST_A2_stateInRange
        //   The controller's state is within range when an event arrives.
        SeedImaging.GUMBO__Library.isTiltSafe(In(tiltEstimate)) & ScanController_jetson_scan.isAxis(In(awaitAxis)) &
          s32"0" <= In(planIndex) &
          In(planIndex) <= In(planEnd) &
          In(planEnd) <= SeedImaging.GUMBO__Library.MAX_SCAN_MOVES()
        // END COMPUTE REQUIRES start
      ),
      Modifies(
        api, // the put_ calls below write the api's outgoing port spec vars
        // BEGIN COMPUTE MODIFIES start
        pending,
        awaitAxis,
        tiltEstimate,
        planIndex,
        planEnd,
        halted
        // END COMPUTE MODIFIES start
      ),
      Ensures(
        // BEGIN COMPUTE ENSURES start
        // guarantee SI_HOST_2_oneCommandPerDispatch
        //   SI-HOST-2: At most one move command is issued per dispatch.
        (api.zCmd.nonEmpty __>:
          api.xCmd.isEmpty & api.yCmd.isEmpty) &
          (api.xCmd.nonEmpty __>: api.yCmd.isEmpty),
        // guarantee SI_HOST_3_oneMoveInFlight
        //   SI-HOST-3: While a move is unacknowledged, no new move is issued unless this very event is its acknowledgement.
        In(pending) & !(api.zPos.nonEmpty &
          In(awaitAxis) == s32"0" |
          api.xPos.nonEmpty &
            In(awaitAxis) == s32"1" |
          api.yPos.nonEmpty &
            In(awaitAxis) == s32"2") __>:
          api.zCmd.isEmpty & api.xCmd.isEmpty &
            api.yCmd.isEmpty,
        // guarantee SI_HOST_4_tiltCommandsSafe
        //   SI-HOST-4: A tilt command is only issued if it is bounded and its target stays within +/-45 deg.
        api.xCmd.isEmpty || SeedImaging.GUMBO__Library.isSafeTiltMove(tiltEstimate, api.xCmd.get.data),
        // guarantee SI_HOST_6_awaitCommandedAxis
        //   SI-HOST-6: Issuing a move makes it the move in flight, on the commanded axis.
        (api.zCmd.nonEmpty __>:
          pending &
            awaitAxis == s32"0") &
          (api.xCmd.nonEmpty __>:
            pending &
              awaitAxis == s32"1") &
          (api.yCmd.nonEmpty __>:
            pending &
              awaitAxis == s32"2"),
        // guarantee SI_HOST_7_stateInRange
        //   SI-HOST-7: The awaited axis and the plan position stay within range (a plan never exceeds MAX_SCAN_MOVES moves).
        ScanController_jetson_scan.isAxis(awaitAxis) &
          s32"0" <= planIndex &
          planIndex <= planEnd &
          planEnd <= SeedImaging.GUMBO__Library.MAX_SCAN_MOVES(),
        // guarantee SI_HOST_8_haltIsFinal
        //   SI-HOST-8: Once halted, the scan controller stays halted and issues no further moves.
        In(halted) __>:
          halted & api.zCmd.isEmpty &
            api.xCmd.isEmpty &
            api.yCmd.isEmpty,
        // guarantee SI_HOST_9_noMoveNoPending
        //   SI-HOST-9: If no move is issued, a move is pending only if one already was.
        !(ScanController_jetson_scan.cmdSent(api.zCmd.nonEmpty, api.xCmd.nonEmpty, api.yCmd.nonEmpty)) __>:
          pending __>: In(pending),
        // guarantee SI_HOST_10_tiltEstimateInRange
        //   SI-HOST-10: The tilt estimate always lies within +/-45 deg.
        SeedImaging.GUMBO__Library.isTiltSafe(tiltEstimate),
        // guarantees SI_HOST_11_startIgnoredWhenBusy
        //   SI-HOST-11: A start request while a move is in flight, or after a halt, changes nothing.
        In(pending) | In(halted) __>:
          api.zCmd.isEmpty & api.xCmd.isEmpty &
            api.yCmd.isEmpty &
            pending == In(pending) &
            awaitAxis == In(awaitAxis) &
            planIndex == In(planIndex) &
            planEnd == In(planEnd) &
            halted == In(halted),
        // guarantees SI_HOST_5_startKeepsTilt
        //   SI-HOST-5: The tilt estimate changes only on a tilt acknowledgement.
        tiltEstimate == In(tiltEstimate),
        // guarantees SI_HOST_12_startLaunchesPlan
        //   SI-HOST-12: A start request while idle launches the selected plan: its first move is issued, or the scan halts.
        !(In(pending)) & !(In(halted)) __>:
          ScanController_jetson_scan.cmdSent(api.zCmd.nonEmpty, api.xCmd.nonEmpty, api.yCmd.nonEmpty) | halted
        // END COMPUTE ENSURES start
      )
    )
    // SI-HOST-11 / SI-HOST-12: only an idle, non-halted controller starts a plan
    // SEEDED BUG (kept as a comment, as in the HAMR GUMBOX exercise): use the line below
    // instead of the next one and a start is accepted while a move is still in flight.
    // The GUMBOX tests and Logika then fail on SI-HOST-3 / SI-HOST-11.
    // if (!halted) {   // seeded bug
    if (!pending & !halted) {
      val selection: S32 = value.data
      if (selection == s32"1") {          // axis self-test
        planIndex = s32"0"
        planEnd = SELF_TEST_END
      } else if (selection == s32"2") {   // imaging scan
        planIndex = SELF_TEST_END
        planEnd = PLAN_LENGTH
      } else {                            // self-test followed by the scan
        planIndex = s32"0"
        planEnd = PLAN_LENGTH
      }
      if (!halted & planIndex < planEnd) {
        // issue the next move of the plan (SI-HOST-2/4/6/7/14/16/18)
        val axis: S32 = planAxis(planIndex)
        val steps: S32 = planSteps(planIndex)
        if (axis == s32"1") {
          if (SeedImaging.GUMBO__Library.isSafeTiltMove(tiltEstimate, steps)) {
            api.put_xCmd(std_msgs.Int32(steps))
            pending = T
            awaitAxis = s32"1"
            planIndex = planIndex + s32"1"
          } else {
            halted = T             // SI-HOST-4 / SI-HOST-8: never ask for an unsafe tilt
          }
        } else if (axis == s32"2") {
          api.put_yCmd(std_msgs.Int32(steps))
          pending = T
          awaitAxis = s32"2"
          planIndex = planIndex + s32"1"
        } else {
          api.put_zCmd(std_msgs.Int32(steps))
          pending = T
          awaitAxis = s32"0"
          planIndex = planIndex + s32"1"
        }
      }
    }
  }

  def handle_zPos(api: ScanController_Operational_Api, value: std_msgs.Int32): Unit = {
    Contract(
      Requires(
        // BEGIN COMPUTE REQUIRES zPos
        // assume HAMR-Guarantee built-in
        //   The spec var corresponding to the handled event must be non-empty and
        //   the passed in payload must be the same as the spec var's value
        api.zPos.nonEmpty &&
        api.zPos.get == value,
        // assume AADL_Requirement
        //   All outgoing event ports must be empty
        api.zCmd.isEmpty,
        api.xCmd.isEmpty,
        api.yCmd.isEmpty,
        // assume SI_HOST_A2_stateInRange
        //   The controller's state is within range when an event arrives.
        SeedImaging.GUMBO__Library.isTiltSafe(In(tiltEstimate)) & ScanController_jetson_scan.isAxis(In(awaitAxis)) &
          s32"0" <= In(planIndex) &
          In(planIndex) <= In(planEnd) &
          In(planEnd) <= SeedImaging.GUMBO__Library.MAX_SCAN_MOVES()
        // END COMPUTE REQUIRES zPos
      ),
      Modifies(
        api, // the put_ calls below write the api's outgoing port spec vars
        // BEGIN COMPUTE MODIFIES zPos
        pending,
        awaitAxis,
        tiltEstimate,
        planIndex,
        planEnd,
        halted
        // END COMPUTE MODIFIES zPos
      ),
      Ensures(
        // BEGIN COMPUTE ENSURES zPos
        // guarantee SI_HOST_2_oneCommandPerDispatch
        //   SI-HOST-2: At most one move command is issued per dispatch.
        (api.zCmd.nonEmpty __>:
          api.xCmd.isEmpty & api.yCmd.isEmpty) &
          (api.xCmd.nonEmpty __>: api.yCmd.isEmpty),
        // guarantee SI_HOST_3_oneMoveInFlight
        //   SI-HOST-3: While a move is unacknowledged, no new move is issued unless this very event is its acknowledgement.
        In(pending) & !(api.zPos.nonEmpty &
          In(awaitAxis) == s32"0" |
          api.xPos.nonEmpty &
            In(awaitAxis) == s32"1" |
          api.yPos.nonEmpty &
            In(awaitAxis) == s32"2") __>:
          api.zCmd.isEmpty & api.xCmd.isEmpty &
            api.yCmd.isEmpty,
        // guarantee SI_HOST_4_tiltCommandsSafe
        //   SI-HOST-4: A tilt command is only issued if it is bounded and its target stays within +/-45 deg.
        api.xCmd.isEmpty || SeedImaging.GUMBO__Library.isSafeTiltMove(tiltEstimate, api.xCmd.get.data),
        // guarantee SI_HOST_6_awaitCommandedAxis
        //   SI-HOST-6: Issuing a move makes it the move in flight, on the commanded axis.
        (api.zCmd.nonEmpty __>:
          pending &
            awaitAxis == s32"0") &
          (api.xCmd.nonEmpty __>:
            pending &
              awaitAxis == s32"1") &
          (api.yCmd.nonEmpty __>:
            pending &
              awaitAxis == s32"2"),
        // guarantee SI_HOST_7_stateInRange
        //   SI-HOST-7: The awaited axis and the plan position stay within range (a plan never exceeds MAX_SCAN_MOVES moves).
        ScanController_jetson_scan.isAxis(awaitAxis) &
          s32"0" <= planIndex &
          planIndex <= planEnd &
          planEnd <= SeedImaging.GUMBO__Library.MAX_SCAN_MOVES(),
        // guarantee SI_HOST_8_haltIsFinal
        //   SI-HOST-8: Once halted, the scan controller stays halted and issues no further moves.
        In(halted) __>:
          halted & api.zCmd.isEmpty &
            api.xCmd.isEmpty &
            api.yCmd.isEmpty,
        // guarantee SI_HOST_9_noMoveNoPending
        //   SI-HOST-9: If no move is issued, a move is pending only if one already was.
        !(ScanController_jetson_scan.cmdSent(api.zCmd.nonEmpty, api.xCmd.nonEmpty, api.yCmd.nonEmpty)) __>:
          pending __>: In(pending),
        // guarantee SI_HOST_10_tiltEstimateInRange
        //   SI-HOST-10: The tilt estimate always lies within +/-45 deg.
        SeedImaging.GUMBO__Library.isTiltSafe(tiltEstimate),
        // guarantees SI_HOST_5_zKeepsTilt
        //   SI-HOST-5: The tilt estimate changes only on a tilt acknowledgement.
        tiltEstimate == In(tiltEstimate),
        // guarantees SI_HOST_13_zStrayAckIgnored
        //   SI-HOST-13: A turntable acknowledgement that is not awaited changes nothing and issues nothing.
        !(In(pending) &
           In(awaitAxis) == s32"0") __>:
          api.zCmd.isEmpty & api.xCmd.isEmpty &
            api.yCmd.isEmpty &
            pending == In(pending) &
            awaitAxis == In(awaitAxis) &
            planIndex == In(planIndex) &
            planEnd == In(planEnd) &
            halted == In(halted),
        // guarantees SI_HOST_14_zAckAdvancesPlan
        //   SI-HOST-14: The awaited turntable acknowledgement completes the move; the next move (if any) advances the plan by one.
        In(pending) &
          In(awaitAxis) == s32"0" __>:
          (ScanController_jetson_scan.cmdSent(api.zCmd.nonEmpty, api.xCmd.nonEmpty, api.yCmd.nonEmpty) __>:
            planIndex == In(planIndex) + s32"1") &
            (!(ScanController_jetson_scan.cmdSent(api.zCmd.nonEmpty, api.xCmd.nonEmpty, api.yCmd.nonEmpty)) __>:
              !pending &
                planIndex == In(planIndex)) &
            planEnd == In(planEnd)
        // END COMPUTE ENSURES zPos
      )
    )
    // SI-HOST-13 / SI-HOST-14: only the awaited acknowledgement releases the next move
    if (pending & awaitAxis == s32"0") {
      pending = F
      if (!halted & planIndex < planEnd) {
        // issue the next move of the plan (SI-HOST-2/4/6/7/14/16/18)
        val axis: S32 = planAxis(planIndex)
        val steps: S32 = planSteps(planIndex)
        if (axis == s32"1") {
          if (SeedImaging.GUMBO__Library.isSafeTiltMove(tiltEstimate, steps)) {
            api.put_xCmd(std_msgs.Int32(steps))
            pending = T
            awaitAxis = s32"1"
            planIndex = planIndex + s32"1"
          } else {
            halted = T             // SI-HOST-4 / SI-HOST-8: never ask for an unsafe tilt
          }
        } else if (axis == s32"2") {
          api.put_yCmd(std_msgs.Int32(steps))
          pending = T
          awaitAxis = s32"2"
          planIndex = planIndex + s32"1"
        } else {
          api.put_zCmd(std_msgs.Int32(steps))
          pending = T
          awaitAxis = s32"0"
          planIndex = planIndex + s32"1"
        }
      }
    }
  }

  def handle_xPos(api: ScanController_Operational_Api, value: std_msgs.Int32): Unit = {
    Contract(
      Requires(
        // BEGIN COMPUTE REQUIRES xPos
        // assume HAMR-Guarantee built-in
        //   The spec var corresponding to the handled event must be non-empty and
        //   the passed in payload must be the same as the spec var's value
        api.xPos.nonEmpty &&
        api.xPos.get == value,
        // assume AADL_Requirement
        //   All outgoing event ports must be empty
        api.zCmd.isEmpty,
        api.xCmd.isEmpty,
        api.yCmd.isEmpty,
        // assume SI_HOST_A2_stateInRange
        //   The controller's state is within range when an event arrives.
        SeedImaging.GUMBO__Library.isTiltSafe(In(tiltEstimate)) & ScanController_jetson_scan.isAxis(In(awaitAxis)) &
          s32"0" <= In(planIndex) &
          In(planIndex) <= In(planEnd) &
          In(planEnd) <= SeedImaging.GUMBO__Library.MAX_SCAN_MOVES()
        // END COMPUTE REQUIRES xPos
      ),
      Modifies(
        api, // the put_ calls below write the api's outgoing port spec vars
        // BEGIN COMPUTE MODIFIES xPos
        pending,
        awaitAxis,
        tiltEstimate,
        planIndex,
        planEnd,
        halted
        // END COMPUTE MODIFIES xPos
      ),
      Ensures(
        // BEGIN COMPUTE ENSURES xPos
        // guarantee SI_HOST_2_oneCommandPerDispatch
        //   SI-HOST-2: At most one move command is issued per dispatch.
        (api.zCmd.nonEmpty __>:
          api.xCmd.isEmpty & api.yCmd.isEmpty) &
          (api.xCmd.nonEmpty __>: api.yCmd.isEmpty),
        // guarantee SI_HOST_3_oneMoveInFlight
        //   SI-HOST-3: While a move is unacknowledged, no new move is issued unless this very event is its acknowledgement.
        In(pending) & !(api.zPos.nonEmpty &
          In(awaitAxis) == s32"0" |
          api.xPos.nonEmpty &
            In(awaitAxis) == s32"1" |
          api.yPos.nonEmpty &
            In(awaitAxis) == s32"2") __>:
          api.zCmd.isEmpty & api.xCmd.isEmpty &
            api.yCmd.isEmpty,
        // guarantee SI_HOST_4_tiltCommandsSafe
        //   SI-HOST-4: A tilt command is only issued if it is bounded and its target stays within +/-45 deg.
        api.xCmd.isEmpty || SeedImaging.GUMBO__Library.isSafeTiltMove(tiltEstimate, api.xCmd.get.data),
        // guarantee SI_HOST_6_awaitCommandedAxis
        //   SI-HOST-6: Issuing a move makes it the move in flight, on the commanded axis.
        (api.zCmd.nonEmpty __>:
          pending &
            awaitAxis == s32"0") &
          (api.xCmd.nonEmpty __>:
            pending &
              awaitAxis == s32"1") &
          (api.yCmd.nonEmpty __>:
            pending &
              awaitAxis == s32"2"),
        // guarantee SI_HOST_7_stateInRange
        //   SI-HOST-7: The awaited axis and the plan position stay within range (a plan never exceeds MAX_SCAN_MOVES moves).
        ScanController_jetson_scan.isAxis(awaitAxis) &
          s32"0" <= planIndex &
          planIndex <= planEnd &
          planEnd <= SeedImaging.GUMBO__Library.MAX_SCAN_MOVES(),
        // guarantee SI_HOST_8_haltIsFinal
        //   SI-HOST-8: Once halted, the scan controller stays halted and issues no further moves.
        In(halted) __>:
          halted & api.zCmd.isEmpty &
            api.xCmd.isEmpty &
            api.yCmd.isEmpty,
        // guarantee SI_HOST_9_noMoveNoPending
        //   SI-HOST-9: If no move is issued, a move is pending only if one already was.
        !(ScanController_jetson_scan.cmdSent(api.zCmd.nonEmpty, api.xCmd.nonEmpty, api.yCmd.nonEmpty)) __>:
          pending __>: In(pending),
        // guarantee SI_HOST_10_tiltEstimateInRange
        //   SI-HOST-10: The tilt estimate always lies within +/-45 deg.
        SeedImaging.GUMBO__Library.isTiltSafe(tiltEstimate),
        // guarantees SI_HOST_5_xTracksTilt
        //   SI-HOST-5: A tilt acknowledgement always updates the tilt estimate to the reported position.
        tiltEstimate == api.xPos.get.data,
        // guarantees SI_HOST_15_xStrayAckIgnored
        //   SI-HOST-15: A tilt acknowledgement that is not awaited only updates the tilt estimate.
        !(In(pending) &
           In(awaitAxis) == s32"1") __>:
          api.zCmd.isEmpty & api.xCmd.isEmpty &
            api.yCmd.isEmpty &
            pending == In(pending) &
            awaitAxis == In(awaitAxis) &
            planIndex == In(planIndex) &
            planEnd == In(planEnd) &
            halted == In(halted),
        // guarantees SI_HOST_16_xAckAdvancesPlan
        //   SI-HOST-16: The awaited tilt acknowledgement completes the move; the next move (if any) advances the plan by one.
        In(pending) &
          In(awaitAxis) == s32"1" __>:
          (ScanController_jetson_scan.cmdSent(api.zCmd.nonEmpty, api.xCmd.nonEmpty, api.yCmd.nonEmpty) __>:
            planIndex == In(planIndex) + s32"1") &
            (!(ScanController_jetson_scan.cmdSent(api.zCmd.nonEmpty, api.xCmd.nonEmpty, api.yCmd.nonEmpty)) __>:
              !pending &
                planIndex == In(planIndex)) &
            planEnd == In(planEnd)
        // END COMPUTE ENSURES xPos
      )
    )
    // SI-HOST-5 / SI-HOST-10: every tilt acknowledgement updates the estimate.
    // get_xPos() carries the integration assumption SI-HOST-A1 (ack within +/-45 deg).
    val ack: Option[std_msgs.Int32] = api.get_xPos()
    tiltEstimate = value.data

    // SI-HOST-15 / SI-HOST-16: only the awaited acknowledgement releases the next move
    if (pending & awaitAxis == s32"1") {
      pending = F
      if (!halted & planIndex < planEnd) {
        // issue the next move of the plan (SI-HOST-2/4/6/7/14/16/18)
        val axis: S32 = planAxis(planIndex)
        val steps: S32 = planSteps(planIndex)
        if (axis == s32"1") {
          if (SeedImaging.GUMBO__Library.isSafeTiltMove(tiltEstimate, steps)) {
            api.put_xCmd(std_msgs.Int32(steps))
            pending = T
            awaitAxis = s32"1"
            planIndex = planIndex + s32"1"
          } else {
            halted = T             // SI-HOST-4 / SI-HOST-8: never ask for an unsafe tilt
          }
        } else if (axis == s32"2") {
          api.put_yCmd(std_msgs.Int32(steps))
          pending = T
          awaitAxis = s32"2"
          planIndex = planIndex + s32"1"
        } else {
          api.put_zCmd(std_msgs.Int32(steps))
          pending = T
          awaitAxis = s32"0"
          planIndex = planIndex + s32"1"
        }
      }
    }
  }

  def handle_yPos(api: ScanController_Operational_Api, value: std_msgs.Int32): Unit = {
    Contract(
      Requires(
        // BEGIN COMPUTE REQUIRES yPos
        // assume HAMR-Guarantee built-in
        //   The spec var corresponding to the handled event must be non-empty and
        //   the passed in payload must be the same as the spec var's value
        api.yPos.nonEmpty &&
        api.yPos.get == value,
        // assume AADL_Requirement
        //   All outgoing event ports must be empty
        api.zCmd.isEmpty,
        api.xCmd.isEmpty,
        api.yCmd.isEmpty,
        // assume SI_HOST_A2_stateInRange
        //   The controller's state is within range when an event arrives.
        SeedImaging.GUMBO__Library.isTiltSafe(In(tiltEstimate)) & ScanController_jetson_scan.isAxis(In(awaitAxis)) &
          s32"0" <= In(planIndex) &
          In(planIndex) <= In(planEnd) &
          In(planEnd) <= SeedImaging.GUMBO__Library.MAX_SCAN_MOVES()
        // END COMPUTE REQUIRES yPos
      ),
      Modifies(
        api, // the put_ calls below write the api's outgoing port spec vars
        // BEGIN COMPUTE MODIFIES yPos
        pending,
        awaitAxis,
        tiltEstimate,
        planIndex,
        planEnd,
        halted
        // END COMPUTE MODIFIES yPos
      ),
      Ensures(
        // BEGIN COMPUTE ENSURES yPos
        // guarantee SI_HOST_2_oneCommandPerDispatch
        //   SI-HOST-2: At most one move command is issued per dispatch.
        (api.zCmd.nonEmpty __>:
          api.xCmd.isEmpty & api.yCmd.isEmpty) &
          (api.xCmd.nonEmpty __>: api.yCmd.isEmpty),
        // guarantee SI_HOST_3_oneMoveInFlight
        //   SI-HOST-3: While a move is unacknowledged, no new move is issued unless this very event is its acknowledgement.
        In(pending) & !(api.zPos.nonEmpty &
          In(awaitAxis) == s32"0" |
          api.xPos.nonEmpty &
            In(awaitAxis) == s32"1" |
          api.yPos.nonEmpty &
            In(awaitAxis) == s32"2") __>:
          api.zCmd.isEmpty & api.xCmd.isEmpty &
            api.yCmd.isEmpty,
        // guarantee SI_HOST_4_tiltCommandsSafe
        //   SI-HOST-4: A tilt command is only issued if it is bounded and its target stays within +/-45 deg.
        api.xCmd.isEmpty || SeedImaging.GUMBO__Library.isSafeTiltMove(tiltEstimate, api.xCmd.get.data),
        // guarantee SI_HOST_6_awaitCommandedAxis
        //   SI-HOST-6: Issuing a move makes it the move in flight, on the commanded axis.
        (api.zCmd.nonEmpty __>:
          pending &
            awaitAxis == s32"0") &
          (api.xCmd.nonEmpty __>:
            pending &
              awaitAxis == s32"1") &
          (api.yCmd.nonEmpty __>:
            pending &
              awaitAxis == s32"2"),
        // guarantee SI_HOST_7_stateInRange
        //   SI-HOST-7: The awaited axis and the plan position stay within range (a plan never exceeds MAX_SCAN_MOVES moves).
        ScanController_jetson_scan.isAxis(awaitAxis) &
          s32"0" <= planIndex &
          planIndex <= planEnd &
          planEnd <= SeedImaging.GUMBO__Library.MAX_SCAN_MOVES(),
        // guarantee SI_HOST_8_haltIsFinal
        //   SI-HOST-8: Once halted, the scan controller stays halted and issues no further moves.
        In(halted) __>:
          halted & api.zCmd.isEmpty &
            api.xCmd.isEmpty &
            api.yCmd.isEmpty,
        // guarantee SI_HOST_9_noMoveNoPending
        //   SI-HOST-9: If no move is issued, a move is pending only if one already was.
        !(ScanController_jetson_scan.cmdSent(api.zCmd.nonEmpty, api.xCmd.nonEmpty, api.yCmd.nonEmpty)) __>:
          pending __>: In(pending),
        // guarantee SI_HOST_10_tiltEstimateInRange
        //   SI-HOST-10: The tilt estimate always lies within +/-45 deg.
        SeedImaging.GUMBO__Library.isTiltSafe(tiltEstimate),
        // guarantees SI_HOST_5_yKeepsTilt
        //   SI-HOST-5: The tilt estimate changes only on a tilt acknowledgement.
        tiltEstimate == In(tiltEstimate),
        // guarantees SI_HOST_17_yStrayAckIgnored
        //   SI-HOST-17: A Y acknowledgement that is not awaited changes nothing and issues nothing.
        !(In(pending) &
           In(awaitAxis) == s32"2") __>:
          api.zCmd.isEmpty & api.xCmd.isEmpty &
            api.yCmd.isEmpty &
            pending == In(pending) &
            awaitAxis == In(awaitAxis) &
            planIndex == In(planIndex) &
            planEnd == In(planEnd) &
            halted == In(halted),
        // guarantees SI_HOST_18_yAckAdvancesPlan
        //   SI-HOST-18: The awaited Y acknowledgement completes the move; the next move (if any) advances the plan by one.
        In(pending) &
          In(awaitAxis) == s32"2" __>:
          (ScanController_jetson_scan.cmdSent(api.zCmd.nonEmpty, api.xCmd.nonEmpty, api.yCmd.nonEmpty) __>:
            planIndex == In(planIndex) + s32"1") &
            (!(ScanController_jetson_scan.cmdSent(api.zCmd.nonEmpty, api.xCmd.nonEmpty, api.yCmd.nonEmpty)) __>:
              !pending &
                planIndex == In(planIndex)) &
            planEnd == In(planEnd)
        // END COMPUTE ENSURES yPos
      )
    )
    // SI-HOST-17 / SI-HOST-18: only the awaited acknowledgement releases the next move
    if (pending & awaitAxis == s32"2") {
      pending = F
      if (!halted & planIndex < planEnd) {
        // issue the next move of the plan (SI-HOST-2/4/6/7/14/16/18)
        val axis: S32 = planAxis(planIndex)
        val steps: S32 = planSteps(planIndex)
        if (axis == s32"1") {
          if (SeedImaging.GUMBO__Library.isSafeTiltMove(tiltEstimate, steps)) {
            api.put_xCmd(std_msgs.Int32(steps))
            pending = T
            awaitAxis = s32"1"
            planIndex = planIndex + s32"1"
          } else {
            halted = T             // SI-HOST-4 / SI-HOST-8: never ask for an unsafe tilt
          }
        } else if (axis == s32"2") {
          api.put_yCmd(std_msgs.Int32(steps))
          pending = T
          awaitAxis = s32"2"
          planIndex = planIndex + s32"1"
        } else {
          api.put_zCmd(std_msgs.Int32(steps))
          pending = T
          awaitAxis = s32"0"
          planIndex = planIndex + s32"1"
        }
      }
    }
  }

  def finalise(api: ScanController_Operational_Api): Unit = { }
}
