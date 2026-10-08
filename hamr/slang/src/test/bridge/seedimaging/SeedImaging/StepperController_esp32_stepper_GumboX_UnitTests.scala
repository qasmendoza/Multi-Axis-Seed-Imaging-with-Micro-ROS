package seedimaging.SeedImaging

import org.sireum._
import seedimaging.GumboXUtil.GumboXResult
import seedimaging.util.{Container, UnitTestConfigurationBatch}
import seedimaging.SeedImaging.StepperController_esp32_stepper_UnitTestConfiguration_Util._
import seedimaging.{Config_S32, RandomLib}

// This file will not be overwritten if HAMR codegen is rerun

// Property-based GUMBOX tests (HAMR "Automated GUMBOX Property-Based Testing").
//
// SlangCheck generates the test vectors and the HAMR-generated GUMBOX oracle judges
// every result.  The default configurations draw every S32 from its full range, so
// almost no vector satisfies the compute assumption SI_MCU_A1 (positions in range)
// and the tests would pass without testing anything.  The configurations below draw
// the positions from their valid ranges instead, and failOnUnsatPreconditions = T
// makes a test fail if it cannot find a vector that satisfies the precondition.
class StepperController_esp32_stepper_GumboX_UnitTests extends StepperController_esp32_stepper_GumboX_TestHarness_ScalaTest {

  // set verbose to T to see pre/post state values and generated unit tests
  // that can be copied/pasted to replay a test
  val verbose: B = F

  // T: a test fails when no generated vector satisfies the entry point's precondition
  val failOnUnsatPreconditions: B = T

  // SlangCheck generator whose S32 values lie in [lo, hi]
  def inRange(lo: Int, hi: Int): RandomLib = {
    val r = freshRandomLib
    r.set_Config_S32(Config_S32(Some(S32(lo)), Some(S32(hi)), Z(100), F, (v: S32) => T))
    return r
  }

  def configs: MSZ[UnitTestConfigurationBatch] = {
    return MSZ(
      defaultInitializeConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions,
        name = "PBT_initialize", numTests = Z(10)),

      // pre-state = the component's own state; one command in [-450, 450]
      defaultComputeConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions,
        name = "PBT_compute_commands_in_range", numTests = Z(300),
        profile = StepperController_esp32_stepper_Profile_P(
          name = "commands_in_range",
          api_xCmd = inRange(-450, 450),
          api_yCmd = inRange(-450, 450),
          api_zCmd = inRange(-450, 450))),

      // random pre-state within the compute assumption SI_MCU_A1; one command in [-450, 450]
      defaultComputewLConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions,
        name = "PBT_computewL_positions_and_commands_in_range", numTests = Z(1000),
        profile = StepperController_esp32_stepper_Profile_PS(
          name = "positions_and_commands_in_range",
          In_xPosition = inRange(-25, 25),
          In_yPosition = inRange(0, 199),
          In_zPosition = inRange(0, 199),
          api_xCmd = inRange(-450, 450),
          api_yCmd = inRange(-450, 450),
          api_zCmd = inRange(-450, 450))),

      // random pre-state within SI_MCU_A1; commands from the full S32 range (overflow edge)
      defaultComputewLConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions,
        name = "PBT_computewL_full_range_commands", numTests = Z(200),
        profile = StepperController_esp32_stepper_Profile_PS(
          name = "full_range_commands",
          In_xPosition = inRange(-25, 25),
          In_yPosition = inRange(0, 199),
          In_zPosition = inRange(0, 199),
          api_xCmd = freshRandomLib,
          api_yCmd = freshRandomLib,
          api_zCmd = freshRandomLib))
    )
  }


  for (c <- configs) {
    def next: Option[Container] = {
      try {
        c.profile.next match {
          case (cp: StepperController_esp32_stepper_PreState_Container) =>
            // only allow one incoming event
            if (ops.ISZOps(ISZ(cp.api_zCmd.nonEmpty, cp.api_xCmd.nonEmpty, cp.api_yCmd.nonEmpty)).filter(p => p).size == 1)
              return Some(cp)
            else return None()
          case c =>
            return Some(c)
        }
      } catch {
        case e: AssertionError => // SlangCheck was unable to satisfy a datatype's filter
          return None()
      }
    }

    for (i <- 0 until c.numTests) {
      val testName = s"${c.name}_$i"

      this.registerTest(testName) {
        var retry: B = T

        var j: Z = 0
        while (j < c.numTestVectorGenRetries && retry) {
          next match {
            case Some(o) =>

              if (verbose && j > 1) {
                println(s"Retry $j:")
              }

              val results = c.test(o)

              if (verbose) {
                c.genReplay(o, testName, results) match {
                  case Some(s) => println(s)
                  case _ =>
                }
              }

              results match {
                case GumboXResult.Pre_Condition_Unsat =>
                case GumboXResult.Post_Condition_Fail =>
                  fail("Post condition did not hold")
                  retry = F
                case GumboXResult.Post_Condition_Pass =>
                  if (verbose) {
                    println("Success!")
                  }
                  retry = F
              }
            case _ =>

          }
          j = j + 1
        }

        if (retry) {
          if (c.failOnUnsatPreconditions) {
            fail("Unable to satisfy precondition")
          } else if (verbose) {
            cprintln(T, "Unable to satisfy precondition")
          }
        }
      }
    }
  }

  def configsToJson: String = {
    return st"[ ${(for (c <- configs) yield s"\"${c.name}|${c.description}\"", ", ")} ]".render
  }
}
