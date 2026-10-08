package seedimaging.SeedImaging

import org.sireum._
import seedimaging.SeedImaging._
import seedimaging.std_msgs

// This file will not be overwritten if HAMR codegen is rerun

// Manual unit tests (HAMR "Thread Component Manual Unit Testing").
//
// Each test puts one event on an input port per dispatch, runs the entry point, and
// compares the command ports with the moves worked out by hand from the requirements
// and the scan plan.  Several dispatches in a row check that the plan advances only
// when the awaited acknowledgement arrives.
class ScanController_jetson_scan_Test extends ScanController_jetson_scan_ScalaTest {

  def msg(v: Int): std_msgs.Int32 = std_msgs.Int32(S32(v))

  def move(v: Int): Option[std_msgs.Int32] = Some(std_msgs.Int32(S32(v)))

  val nothing: Option[std_msgs.Int32] = None()

  val noMove = (nothing, nothing, nothing)

  // ART's test mode keeps port values from one dispatch to the next, and
  // BeforeEntrypoint() re-runs initialise.  So between two dispatches of one test the
  // ports are reset and the component's GUMBO state is put back.
  def freshPorts(): Unit = {
    val c = ScanController_jetson_scan
    val (pending, awaitAxis, tilt, index, end, halted) = (c.pending, c.awaitAxis, c.tiltEstimate, c.planIndex, c.planEnd, c.halted)
    AfterEntrypoint()
    BeforeEntrypoint()
    c.pending = pending
    c.awaitAxis = awaitAxis
    c.tiltEstimate = tilt
    c.planIndex = index
    c.planEnd = end
    c.halted = halted
  }

  // One dispatch: 's' = start request, 'z' / 'x' / 'y' = acknowledgement.
  // Returns the commands published on (zCmd, xCmd, yCmd).
  def event(kind: Char, value: Int): (Option[std_msgs.Int32], Option[std_msgs.Int32], Option[std_msgs.Int32]) = {
    freshPorts()
    kind match {
      case 's' => put_start(msg(value))
      case 'z' => put_zPos(msg(value))
      case 'x' => put_xPos(msg(value))
      case _ => put_yPos(msg(value))
    }
    testCompute()
    return (get_zCmd(), get_xCmd(), get_yCmd())
  }

  test("SI-HOST-1: after initialise, a stray acknowledgement starts nothing") {
    testInitialise()
    assert(event('z', 0) == noMove)
  }

  test("SI-HOST-11, SI-HOST-12: start 1 issues the first self-test move, Z +200") {
    testInitialise()
    assert(event('s', 1) == ((move(200), nothing, nothing)))
  }

  test("SI-HOST-11: a second start while a move is in flight is ignored") {
    testInitialise()
    assert(event('s', 1) == ((move(200), nothing, nothing)))
    assert(event('s', 1) == noMove)
  }

  test("SI-HOST-13: a stray Z acknowledgement while X is awaited issues nothing") {
    testInitialise()
    assert(event('s', 1) == ((move(200), nothing, nothing)))
    assert(event('z', 0) == ((nothing, move(25), nothing)))   // Z acknowledged -> X +25
    assert(event('z', 7) == noMove)                             // X is awaited, not Z
  }

  test("Self-test plan (start 1): six moves in order, each after the previous acknowledgement") {
    testInitialise()
    assert(event('s', 1) == ((move(200), nothing, nothing)))    // Z one turn
    assert(event('z', 0) == ((nothing, move(25), nothing)))     // tilt +45 deg
    assert(event('x', 25) == ((nothing, move(-50), nothing)))   // tilt -45 deg
    assert(event('x', -25) == ((nothing, move(25), nothing)))   // back to level
    assert(event('x', 0) == ((nothing, nothing, move(200))))    // Y one turn
    assert(event('y', 0) == ((move(-200), nothing, nothing)))   // Z back
    assert(event('z', 0) == noMove)                             // plan complete
    assert(event('s', 2) == ((move(25), nothing, nothing)))     // a new plan may start
  }

  test("SI-HOST-4, SI-HOST-8: a tilt move that would pass +45 deg halts the plan for good") {
    testInitialise()
    assert(event('x', 25) == noMove)                            // tilt left at +45 deg by hand
    assert(event('s', 1) == ((move(200), nothing, nothing)))    // Z +200 is still safe
    assert(event('z', 0) == noMove)                             // next X +25 would reach +90 deg: halt
    assert(event('s', 1) == noMove)                             // halted: start ignored
    assert(event('x', 0) == noMove)                             // halted: stays halted
  }
}
