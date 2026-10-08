// #Sireum

package seedimaging.SeedImaging

import org.sireum._
import seedimaging._
import org.sireum.S32._

// This file will not be overwritten if HAMR codegen is rerun
object StepperController_esp32_stepper {

  // BEGIN STATE VARS
  var zPosition: Base_Types.Integer_32 = Base_Types.Integer_32_example()

  var xPosition: Base_Types.Integer_32 = Base_Types.Integer_32_example()

  var yPosition: Base_Types.Integer_32 = Base_Types.Integer_32_example()
  // END STATE VARS

  def initialise(api: StepperController_Initialization_Api): Unit = {
    Contract(
      Requires(
        // BEGIN INITIALIZES REQUIRES
        // assume AADL_Requirement
        //   All outgoing event ports must be empty
        api.zPos.isEmpty,
        api.xPos.isEmpty,
        api.yPos.isEmpty
        // END INITIALIZES REQUIRES
      ),
      Modifies(
        api, // the put_ calls below write the api's outgoing port spec vars
        // BEGIN INITIALIZES MODIFIES
        zPosition,
        xPosition,
        yPosition
        // END INITIALIZES MODIFIES
      ),
      Ensures(
        // BEGIN INITIALIZES ENSURES
        // guarantee SI_MCU_1_homeAtStartup
        //   SI-MCU-1: At power-up all three axes are taken to be at home (0 steps).
        zPosition == s32"0" &
          xPosition == s32"0" &
          yPosition == s32"0"
        // END INITIALIZES ENSURES
      )
    )
    // SI-MCU-1: all three axes are taken to be at home at power-up
    zPosition = s32"0"
    xPosition = s32"0"
    yPosition = s32"0"
  }

  def handle_zCmd(api: StepperController_Operational_Api, value: std_msgs.Int32): Unit = {
    Contract(
      Requires(
        // BEGIN COMPUTE REQUIRES zCmd
        // assume HAMR-Guarantee built-in
        //   The spec var corresponding to the handled event must be non-empty and
        //   the passed in payload must be the same as the spec var's value
        api.zCmd.nonEmpty &&
        api.zCmd.get == value,
        // assume AADL_Requirement
        //   All outgoing event ports must be empty
        api.zPos.isEmpty,
        api.xPos.isEmpty,
        api.yPos.isEmpty,
        // assume SI_MCU_A1_positionsInRange
        //   Positions are within their ranges when a command arrives.
        SeedImaging.GUMBO__Library.isRotaryIndex(In(zPosition)) & SeedImaging.GUMBO__Library.isTiltSafe(In(xPosition)) &
          SeedImaging.GUMBO__Library.isRotaryIndex(In(yPosition))
        // END COMPUTE REQUIRES zCmd
      ),
      Modifies(
        api, // the put_ calls below write the api's outgoing port spec vars
        // BEGIN COMPUTE MODIFIES zCmd
        zPosition,
        xPosition,
        yPosition
        // END COMPUTE MODIFIES zCmd
      ),
      Ensures(
        // BEGIN COMPUTE ENSURES zCmd
        // guarantee SI_MCU_2_tiltNeverBeyond45
        //   SI-MCU-2: The tilt axis never leaves +/-45 deg (+/-25 steps).
        SeedImaging.GUMBO__Library.isTiltSafe(xPosition),
        // guarantee SI_MCU_3_rotaryModuloOneRev
        //   SI-MCU-3: Turntable and Y positions are kept modulo one revolution.
        SeedImaging.GUMBO__Library.isRotaryIndex(zPosition) & SeedImaging.GUMBO__Library.isRotaryIndex(yPosition),
        // guarantees SI_MCU_4_zExactMove
        //   SI-MCU-4: A bounded turntable command rotates the turntable by exactly the commanded steps.
        SeedImaging.GUMBO__Library.isBoundedMove(api.zCmd.get.data) __>:
          zPosition == SeedImaging.GUMBO__Library.wrapRotary(In(zPosition) + api.zCmd.get.data),
        // guarantees SI_MCU_5_zRejectOversize
        //   SI-MCU-5: An oversize turntable command is rejected: no motion.
        !(SeedImaging.GUMBO__Library.isBoundedMove(api.zCmd.get.data)) __>:
          zPosition == In(zPosition),
        // guarantees SI_MCU_6_zAxisIsolation
        //   SI-MCU-6: A turntable command never moves the tilt or Y axis.
        xPosition == In(xPosition) &
          yPosition == In(yPosition),
        // guarantees SI_MCU_7_zAcknowledged
        //   SI-MCU-7: Every turntable command is acknowledged once, on zPos only, with the new turntable position.
        api.zPos.nonEmpty &&
          api.zPos.get.data == zPosition &
          api.xPos.isEmpty &
          api.yPos.isEmpty
        // END COMPUTE ENSURES zCmd
      )
    )
    // Reference behaviour of the ESP32 micro-ROS node for a turntable command.
    // (The C realisation pulses the Z coils |steps| times, 5 ms apart, releases
    //  them, and then publishes the acknowledgement.)
    val steps: S32 = value.data
    if (SeedImaging.GUMBO__Library.isBoundedMove(steps)) {
      zPosition = SeedImaging.GUMBO__Library.wrapRotary(zPosition + steps)
    }
    api.put_zPos(std_msgs.Int32(zPosition))
  }

  def handle_xCmd(api: StepperController_Operational_Api, value: std_msgs.Int32): Unit = {
    Contract(
      Requires(
        // BEGIN COMPUTE REQUIRES xCmd
        // assume HAMR-Guarantee built-in
        //   The spec var corresponding to the handled event must be non-empty and
        //   the passed in payload must be the same as the spec var's value
        api.xCmd.nonEmpty &&
        api.xCmd.get == value,
        // assume AADL_Requirement
        //   All outgoing event ports must be empty
        api.zPos.isEmpty,
        api.xPos.isEmpty,
        api.yPos.isEmpty,
        // assume SI_MCU_A1_positionsInRange
        //   Positions are within their ranges when a command arrives.
        SeedImaging.GUMBO__Library.isRotaryIndex(In(zPosition)) & SeedImaging.GUMBO__Library.isTiltSafe(In(xPosition)) &
          SeedImaging.GUMBO__Library.isRotaryIndex(In(yPosition))
        // END COMPUTE REQUIRES xCmd
      ),
      Modifies(
        api, // the put_ calls below write the api's outgoing port spec vars
        // BEGIN COMPUTE MODIFIES xCmd
        zPosition,
        xPosition,
        yPosition
        // END COMPUTE MODIFIES xCmd
      ),
      Ensures(
        // BEGIN COMPUTE ENSURES xCmd
        // guarantee SI_MCU_2_tiltNeverBeyond45
        //   SI-MCU-2: The tilt axis never leaves +/-45 deg (+/-25 steps).
        SeedImaging.GUMBO__Library.isTiltSafe(xPosition),
        // guarantee SI_MCU_3_rotaryModuloOneRev
        //   SI-MCU-3: Turntable and Y positions are kept modulo one revolution.
        SeedImaging.GUMBO__Library.isRotaryIndex(zPosition) & SeedImaging.GUMBO__Library.isRotaryIndex(yPosition),
        // guarantees SI_MCU_8_xExactMove
        //   SI-MCU-8: A tilt command that is bounded and lands within +/-45 deg moves the tilt by exactly the commanded steps.
        SeedImaging.GUMBO__Library.isSafeTiltMove(In(xPosition), api.xCmd.get.data) __>:
          xPosition == In(xPosition) + api.xCmd.get.data,
        // guarantees SI_MCU_9_xRejectUnsafe
        //   SI-MCU-9: A tilt command that is oversize or would leave +/-45 deg is rejected: no motion.
        !(SeedImaging.GUMBO__Library.isSafeTiltMove(In(xPosition), api.xCmd.get.data)) __>:
          xPosition == In(xPosition),
        // guarantees SI_MCU_10_xAxisIsolation
        //   SI-MCU-10: A tilt command never moves the turntable or Y axis.
        zPosition == In(zPosition) &
          yPosition == In(yPosition),
        // guarantees SI_MCU_11_xAcknowledged
        //   SI-MCU-11: Every tilt command is acknowledged once, on xPos only, with the new tilt position.
        api.xPos.nonEmpty &&
          api.xPos.get.data == xPosition &
          api.zPos.isEmpty &
          api.yPos.isEmpty
        // END COMPUTE ENSURES xCmd
      )
    )
    // Reference behaviour for a tilt command: execute it only if it is bounded
    // and lands within +/-45 deg; otherwise reject it (no motion).  Either way
    // acknowledge with the (possibly unchanged) tilt position.
    val steps: S32 = value.data
    // SEEDED BUG (kept as a comment, as in the HAMR GUMBOX exercise): use the line below
    // instead of the next one and the +/-45 deg check is gone.  The GUMBOX tests and
    // Logika then fail on SI-MCU-9 / SI-MCU-16; bin/seeded_bug_demo.sh shows this on a
    // scratch copy without editing this file.
    // if (SeedImaging.GUMBO__Library.isBoundedMove(steps)) {   // seeded bug
    if (SeedImaging.GUMBO__Library.isSafeTiltMove(xPosition, steps)) {
      xPosition = xPosition + steps
    }
    api.put_xPos(std_msgs.Int32(xPosition))
  }

  def handle_yCmd(api: StepperController_Operational_Api, value: std_msgs.Int32): Unit = {
    Contract(
      Requires(
        // BEGIN COMPUTE REQUIRES yCmd
        // assume HAMR-Guarantee built-in
        //   The spec var corresponding to the handled event must be non-empty and
        //   the passed in payload must be the same as the spec var's value
        api.yCmd.nonEmpty &&
        api.yCmd.get == value,
        // assume AADL_Requirement
        //   All outgoing event ports must be empty
        api.zPos.isEmpty,
        api.xPos.isEmpty,
        api.yPos.isEmpty,
        // assume SI_MCU_A1_positionsInRange
        //   Positions are within their ranges when a command arrives.
        SeedImaging.GUMBO__Library.isRotaryIndex(In(zPosition)) & SeedImaging.GUMBO__Library.isTiltSafe(In(xPosition)) &
          SeedImaging.GUMBO__Library.isRotaryIndex(In(yPosition))
        // END COMPUTE REQUIRES yCmd
      ),
      Modifies(
        api, // the put_ calls below write the api's outgoing port spec vars
        // BEGIN COMPUTE MODIFIES yCmd
        zPosition,
        xPosition,
        yPosition
        // END COMPUTE MODIFIES yCmd
      ),
      Ensures(
        // BEGIN COMPUTE ENSURES yCmd
        // guarantee SI_MCU_2_tiltNeverBeyond45
        //   SI-MCU-2: The tilt axis never leaves +/-45 deg (+/-25 steps).
        SeedImaging.GUMBO__Library.isTiltSafe(xPosition),
        // guarantee SI_MCU_3_rotaryModuloOneRev
        //   SI-MCU-3: Turntable and Y positions are kept modulo one revolution.
        SeedImaging.GUMBO__Library.isRotaryIndex(zPosition) & SeedImaging.GUMBO__Library.isRotaryIndex(yPosition),
        // guarantees SI_MCU_12_yExactMove
        //   SI-MCU-12: A bounded Y command rotates the Y axis by exactly the commanded steps.
        SeedImaging.GUMBO__Library.isBoundedMove(api.yCmd.get.data) __>:
          yPosition == SeedImaging.GUMBO__Library.wrapRotary(In(yPosition) + api.yCmd.get.data),
        // guarantees SI_MCU_13_yRejectOversize
        //   SI-MCU-13: An oversize Y command is rejected: no motion.
        !(SeedImaging.GUMBO__Library.isBoundedMove(api.yCmd.get.data)) __>:
          yPosition == In(yPosition),
        // guarantees SI_MCU_14_yAxisIsolation
        //   SI-MCU-14: A Y command never moves the turntable or tilt axis.
        zPosition == In(zPosition) &
          xPosition == In(xPosition),
        // guarantees SI_MCU_15_yAcknowledged
        //   SI-MCU-15: Every Y command is acknowledged once, on yPos only, with the new Y position.
        api.yPos.nonEmpty &&
          api.yPos.get.data == yPosition &
          api.zPos.isEmpty &
          api.xPos.isEmpty
        // END COMPUTE ENSURES yCmd
      )
    )
    // Reference behaviour for a command to the reserved rotary (Y) axis
    val steps: S32 = value.data
    if (SeedImaging.GUMBO__Library.isBoundedMove(steps)) {
      yPosition = SeedImaging.GUMBO__Library.wrapRotary(yPosition + steps)
    }
    api.put_yPos(std_msgs.Int32(yPosition))
  }

  def finalise(api: StepperController_Operational_Api): Unit = { }
}
