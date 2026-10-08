package seedimaging.SeedImaging

import org.sireum._
import seedimaging.GumboXUtil.GumboXResult
import seedimaging.std_msgs

// Manual GUMBOX tests for the Jetson ScanController (HAMR "Manual GUMBOX Testing").
//
// Each test builds one test vector by hand: the pre-state (the GUMBO state variables
// awaitAxis, halted, pending, planEnd, planIndex, tiltEstimate) and exactly one
// incoming event (a start request or an acknowledgement).  testComputeCBwL runs the
// component and then evaluates the HAMR-generated GUMBOX oracle, which checks every
// GUMBO clause of the model:
//   testComputeCBwL -> compute_CEP_Pre  (I_Assm_xPos = SI_HOST_A1, compute assume SI_HOST_A2)
//                   -> compute_CEP_Post -> compute_CEP_T_Guar           (SI_HOST_2 .. 4, 6 .. 10)
//                                       -> compute_CEP_Handler_*_Guar   (SI_HOST_5, 11 .. 18)
// Each test is named after the clause it targets; the oracle still checks all of them.
// The "failing" tests break an assumption and expect the harness to reject the vector
// (Pre_Condition_Unsat) instead of running the component.
//
// Property-based GUMBOX tests (random test vectors) are in
// ScanController_jetson_scan_GumboX_UnitTests.scala.
//
// This file is hand-written; HAMR code generation does not touch it.
class ScanController_jetson_scan_GumboX_Manual_Tests extends ScanController_jetson_scan_GumboX_TestHarness_ScalaTest {

  // set to T to print the pre/post state of every test vector
  val verbose: B = F

  def msg(v: Int): Option[std_msgs.Int32] = Some(std_msgs.Int32(S32(v)))

  val none: Option[std_msgs.Int32] = None()

  // one event per dispatch: 's' = start, 'z' / 'x' / 'y' = acknowledgement
  def run(await: Int, halted: Boolean, pending: Boolean, planEnd: Int, planIndex: Int, tilt: Int,
          event: Char, value: Int): GumboXResult.Type = {
    val start = if (event == 's') msg(value) else none
    val zAck = if (event == 'z') msg(value) else none
    val xAck = if (event == 'x') msg(value) else none
    val yAck = if (event == 'y') msg(value) else none
    testComputeCBwL(S32(await), B(halted), B(pending), S32(planEnd), S32(planIndex), S32(tilt),
                    start, xAck, yAck, zAck)
  }

  // (clause, description, awaitAxis, halted, pending, planEnd, planIndex, tiltEstimate, event, value)
  val passingVectors: scala.Seq[(Predef.String, Predef.String, Int, Boolean, Boolean, Int, Int, Int, Char, Int)] = scala.Seq(
    ("SI_HOST_2",  "start 0 publishes exactly one command",                    0, false, false,  0,  0,   0, 's', 0),
    ("SI_HOST_3",  "awaited Z ack: the next move goes out only now",           0, false, true,   6,  1,   0, 'z', 0),
    ("SI_HOST_4",  "awaited X ack at +25, next X +25 would pass +45 deg: halt", 1, false, true,  24, 14,   0, 'x', 25),
    ("SI_HOST_5",  "start keeps the tilt estimate (-10)",                      0, false, false,  0,  0, -10, 's', 2),
    ("SI_HOST_5",  "X ack updates the tilt estimate to the reported tilt",     0, false, false,  6,  6,   0, 'x', -10),
    ("SI_HOST_6",  "the controller then awaits the axis it commanded",         0, false, false,  0,  0,   0, 's', 1),
    ("SI_HOST_7",  "last plan move: the plan index stays within the plan",     1, false, true,  24, 23,   0, 'x', 0),
    ("SI_HOST_8",  "halted, awaited-looking ack: stays halted",                0, true,  false, 24, 14,  25, 'z', 0),
    ("SI_HOST_9",  "last move acknowledged: plan complete, nothing pending",   1, false, true,  24, 24,  25, 'x', 0),
    ("SI_HOST_10", "X ack at -25 keeps the tilt estimate within +/-45 deg",    0, false, false,  6,  6,   0, 'x', -25),
    ("SI_HOST_11", "move in flight: start is ignored",                         0, false, true,   6,  1,   0, 's', 1),
    ("SI_HOST_11", "halted: start is ignored",                                 1, true,  false, 24, 14,  25, 's', 2),
    ("SI_HOST_12", "idle, start 1: first self-test move (Z +200)",             0, false, false,  0,  0,   0, 's', 1),
    ("SI_HOST_12", "idle, start 2: first scan move (Z +25)",                   0, false, false,  0,  0,   0, 's', 2),
    ("SI_HOST_12", "idle after a finished plan: start again",                  1, false, false, 24, 24,   0, 's', 1),
    ("SI_HOST_13", "stray Z ack while X is awaited: ignored",                  1, false, true,   6,  2,   0, 'z', 7),
    ("SI_HOST_14", "awaited Z ack: next move issued",                          0, false, true,   6,  1,   0, 'z', 0),
    ("SI_HOST_15", "stray X ack while idle: only the estimate moves",          0, false, false,  6,  6,   0, 'x', -10),
    ("SI_HOST_16", "awaited X ack at +25, next X -50: allowed",                1, false, true,   6,  2,   0, 'x', 25),
    ("SI_HOST_17", "stray Y ack while Z is awaited: ignored",                  0, false, true,   6,  1,   0, 'y', 3),
    ("SI_HOST_18", "awaited Y ack: next move issued",                          2, false, true,   6,  5,   0, 'y', 0)
  )

  for ((clause, what, await, halted, pending, end, idx, tilt, ev, v) <- passingVectors) {
    test(s"compute_GUMBOX_manual_$clause: $what") {
      val r = run(await, halted, pending, end, idx, tilt, ev, v)
      assert(r == GumboXResult.Post_Condition_Pass, s"$clause $what -> $r")
    }
  }

  test("initialize_GUMBOX_manual_SI_HOST_1: initialise starts idle") {
    val r = testInitialiseCB()
    assert(r == GumboXResult.Post_Condition_Pass, s"$r")
  }

  // incoming integration constraint SI_HOST_A1 (I_Assm_xPos) is part of the precondition
  test("compute_GUMBOX_manual_failing_SI_HOST_A1: X ack of +30 (beyond +/-45 deg) is rejected") {
    assert(run(0, false, false, 6, 6, 0, 'x', 30) == GumboXResult.Pre_Condition_Unsat)
  }

  // compute assumption SI_HOST_A2 (the state invariant) is part of the precondition
  test("compute_GUMBOX_manual_failing_SI_HOST_A2: plan index past the plan end is rejected") {
    assert(run(0, false, false, 6, 7, 0, 'z', 0) == GumboXResult.Pre_Condition_Unsat)
  }

  test("compute_GUMBOX_manual_failing_SI_HOST_A2: awaited axis 3 (not an axis) is rejected") {
    assert(run(3, false, true, 6, 1, 0, 'z', 0) == GumboXResult.Pre_Condition_Unsat)
  }

  test("compute_GUMBOX_manual_failing_SI_HOST_A2: tilt estimate +26 is rejected") {
    assert(run(0, false, false, 6, 6, 26, 'z', 0) == GumboXResult.Pre_Condition_Unsat)
  }
}
