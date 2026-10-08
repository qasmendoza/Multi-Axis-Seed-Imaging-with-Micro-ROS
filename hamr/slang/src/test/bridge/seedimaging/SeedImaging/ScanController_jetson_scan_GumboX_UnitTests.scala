package seedimaging.SeedImaging

import org.sireum._
import seedimaging.GumboXUtil.GumboXResult
import seedimaging.util.{Container, UnitTestConfigurationBatch}
import seedimaging.SeedImaging.ScanController_jetson_scan_UnitTestConfiguration_Util._
import seedimaging.{Config_S32, RandomLib}

// This file will not be overwritten if HAMR codegen is rerun

// Property-based GUMBOX tests (HAMR "Automated GUMBOX Property-Based Testing").
//
// SlangCheck generates the test vectors and the HAMR-generated GUMBOX oracle judges
// every result.  The default configurations draw every S32 from its full range, so
// almost no vector satisfies the preconditions (the state invariant SI_HOST_A2 and
// the tilt-ack integration constraint SI_HOST_A1) and the tests would pass without
// testing anything.  The configurations below draw each value from its valid range
// instead, and failOnUnsatPreconditions = T makes a test fail if it cannot find a
// vector that satisfies the precondition.
class ScanController_jetson_scan_GumboX_UnitTests extends ScanController_jetson_scan_GumboX_TestHarness_ScalaTest {

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

      // pre-state = the component's own state; one event with a value in range
      defaultComputeConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions,
        name = "PBT_compute_events_in_range", numTests = Z(300), numTestVectorGenRetries = Z(1000),
        profile = ScanController_jetson_scan_Profile_P(
          name = "events_in_range",
          api_start = inRange(-1, 3),
          api_xPos = inRange(-25, 25),
          api_yPos = inRange(0, 199),
          api_zPos = inRange(0, 199))),

      // random pre-state (awaited axis, flags, plan position, tilt estimate) and one event
      defaultComputewLConfig(verbose = verbose, failOnUnsatPreconditions = failOnUnsatPreconditions,
        name = "PBT_computewL_state_and_events_in_range", numTests = Z(1000), numTestVectorGenRetries = Z(1000),
        profile = ScanController_jetson_scan_Profile_PS(
          name = "state_and_events_in_range",
          In_awaitAxis = inRange(0, 2),
          In_halted = freshRandomLib,
          In_pending = freshRandomLib,
          In_planEnd = inRange(0, 30),
          In_planIndex = inRange(0, 30),
          In_tiltEstimate = inRange(-25, 25),
          api_start = inRange(-1, 3),
          api_xPos = inRange(-25, 25),
          api_yPos = inRange(0, 199),
          api_zPos = inRange(0, 199)))
    )
  }


  for (c <- configs) {
    def next: Option[Container] = {
      try {
        c.profile.next match {
          case (cp: ScanController_jetson_scan_PreState_Container) =>
            // only allow one incoming event (sporadic thread: one event per dispatch)
            if (ops.ISZOps(ISZ(cp.api_start.nonEmpty, cp.api_zPos.nonEmpty, cp.api_xPos.nonEmpty, cp.api_yPos.nonEmpty)).filter(p => p).size == 1)
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
