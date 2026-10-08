package seedimaging.SeedImaging

import org.sireum._
import seedimaging.GumboXUtil.GumboXResult
import seedimaging.std_msgs

// Manual GUMBOX tests for the ESP32 StepperController (HAMR "Manual GUMBOX Testing").
//
// Each test builds one test vector by hand: the pre-state (the three axis positions,
// i.e. the GUMBO state variables) and one incoming command.  testComputeCBwL runs the
// component and then evaluates the HAMR-generated GUMBOX oracle, which checks every
// GUMBO clause of the model:
//   testComputeCBwL -> compute_CEP_Pre  (SI_MCU_A1: I-Assm / compute assume)
//                   -> compute_CEP_Post -> compute_CEP_T_Guar        (SI_MCU_2, SI_MCU_3)
//                                       -> compute_CEP_Handler_*_Guar (SI_MCU_4 .. SI_MCU_15)
//                                       -> I_Guar_xPos                (SI_MCU_16)
// Each test is named after the clause it targets; the oracle still checks all of them.
// The "failing" tests give a pre-state that breaks the compute assumption and expect
// the harness to reject it (Pre_Condition_Unsat) instead of running the component.
//
// Property-based GUMBOX tests (random test vectors) are in
// StepperController_esp32_stepper_GumboX_UnitTests.scala.
//
// This file is hand-written; HAMR code generation does not touch it.
class StepperController_esp32_stepper_GumboX_Manual_Tests extends StepperController_esp32_stepper_GumboX_TestHarness_ScalaTest {

  // set to T to print the pre/post state of every test vector
  val verbose: B = F

  def cmd(v: Int): Option[std_msgs.Int32] = Some(std_msgs.Int32(S32(v)))

  val none: Option[std_msgs.Int32] = None()

  // (clause, description, zPosition, xPosition, yPosition, zCmd, xCmd, yCmd)
  val passingVectors: scala.Seq[(Predef.String, Predef.String, Int, Int, Int, Option[std_msgs.Int32], Option[std_msgs.Int32], Option[std_msgs.Int32])] = scala.Seq(
    ("SI_MCU_2",  "tilt at +45 deg, +1 more is refused: tilt never passes +45 deg",  0,  25,   0, none, cmd(1),   none),
    ("SI_MCU_3",  "Z wrap 199 + 1 -> 0",                                    199,   0,   0, cmd(1),    none, none),
    ("SI_MCU_3",  "Z wrap 0 - 1 -> 199",                                      0,   0,   0, cmd(-1),   none, none),
    ("SI_MCU_3",  "Z one revolution (+200) returns to 0",                     0,   0,   0, cmd(200),  none, none),
    ("SI_MCU_4",  "Z +1 from home",                                           0,   0,   0, cmd(1),    none, none),
    ("SI_MCU_4",  "Z largest bounded move +400",                             57,   0,   0, cmd(400),  none, none),
    ("SI_MCU_4",  "Z largest bounded move -400",                             57,   0,   0, cmd(-400), none, none),
    ("SI_MCU_5",  "Z oversize +401 is refused",                              57,   3,   9, cmd(401),  none, none),
    ("SI_MCU_5",  "Z oversize -401 is refused",                              57,   3,   9, cmd(-401), none, none),
    ("SI_MCU_5",  "Z extreme S32.Max is refused (no overflow)",              10,   0,   0, cmd(Int.MaxValue), none, none),
    ("SI_MCU_5",  "Z extreme S32.Min is refused (no overflow)",              10,   0,   0, cmd(Int.MinValue), none, none),
    ("SI_MCU_6",  "Z move with X = 3, Y = 9 leaves X and Y unchanged",       57,   3,   9, cmd(10),   none, none),
    ("SI_MCU_7",  "Z 0-step query is acknowledged with the position",        12,   7,  33, cmd(0),    none, none),
    ("SI_MCU_8",  "X level to +45 deg",                                       0,   0,   0, none, cmd(25),  none),
    ("SI_MCU_8",  "X level to -45 deg",                                       0,   0,   0, none, cmd(-25), none),
    ("SI_MCU_8",  "X +45 to -45 deg in one move",                             0,  25,   0, none, cmd(-50), none),
    ("SI_MCU_9",  "X at +45 deg, +1 is refused",                              0,  25,   0, none, cmd(1),   none),
    ("SI_MCU_9",  "X at -45 deg, -1 is refused",                              0, -25,   0, none, cmd(-1),  none),
    ("SI_MCU_9",  "X level, +26 is refused",                                  0,   0,   0, none, cmd(26),  none),
    ("SI_MCU_9",  "X oversize +401 is refused",                               0,   0,   0, none, cmd(401), none),
    ("SI_MCU_9",  "X extreme S32.Max is refused (no overflow)",               0,  20,   0, none, cmd(Int.MaxValue), none),
    ("SI_MCU_9",  "X extreme S32.Min is refused (no overflow)",               0, -20,   0, none, cmd(Int.MinValue), none),
    ("SI_MCU_10", "X move with Z = 57, Y = 150 leaves Z and Y unchanged",    57,   0, 150, none, cmd(5),   none),
    ("SI_MCU_11", "X 0-step query is acknowledged with the position",        12,   7,  33, none, cmd(0),   none),
    ("SI_MCU_12", "Y +200 wraps to the same position",                        5,   0, 150, none, none, cmd(200)),
    ("SI_MCU_12", "Y -400 wraps to the same position",                        5,   0, 150, none, none, cmd(-400)),
    ("SI_MCU_13", "Y oversize +1000 is refused",                              5,   0, 150, none, none, cmd(1000)),
    ("SI_MCU_14", "Y move with Z = 5, X = -10 leaves Z and X unchanged",      5, -10, 150, none, none, cmd(3)),
    ("SI_MCU_15", "Y 0-step query is acknowledged with the position",         5,   0, 150, none, none, cmd(0)),
    ("SI_MCU_16", "X -20, -5 more: the published tilt -25 is within +/-45 deg", 0, -20,  0, none, cmd(-5),  none)
  )

  for ((clause, what, z, x, y, zc, xc, yc) <- passingVectors) {
    test(s"compute_GUMBOX_manual_$clause: $what") {
      val r = testComputeCBwL(S32(x), S32(y), S32(z), xc, yc, zc)
      assert(r == GumboXResult.Post_Condition_Pass, s"$clause $what -> $r")
    }
  }

  test("initialize_GUMBOX_manual_SI_MCU_1: initialise puts all axes at home") {
    val r = testInitialiseCB()
    assert(r == GumboXResult.Post_Condition_Pass, s"$r")
  }

  // pre-states that break the compute assumption SI_MCU_A1: the harness must refuse them
  test("compute_GUMBOX_manual_failing_SI_MCU_A1: tilt pre-state +30 (beyond +/-45 deg) is rejected") {
    val r = testComputeCBwL(S32(30), S32(0), S32(0), cmd(0), none, none)
    assert(r == GumboXResult.Pre_Condition_Unsat, s"$r")
  }

  test("compute_GUMBOX_manual_failing_SI_MCU_A1: Z pre-state 200 (not a rotary index) is rejected") {
    val r = testComputeCBwL(S32(0), S32(0), S32(200), none, none, cmd(0))
    assert(r == GumboXResult.Pre_Condition_Unsat, s"$r")
  }
}
