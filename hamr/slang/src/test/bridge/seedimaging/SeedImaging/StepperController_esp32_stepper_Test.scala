package seedimaging.SeedImaging

import org.sireum._
import seedimaging.SeedImaging._
import seedimaging.std_msgs

// This file will not be overwritten if HAMR codegen is rerun

// Manual unit tests (HAMR "Thread Component Manual Unit Testing").
//
// Each test puts values on the input ports, runs the entry point, and compares the
// output ports with values worked out by hand from the requirements.  Unlike the
// GUMBOX tests, a test here can run several dispatches in a row, so it also checks
// that the positions carry over from one command to the next.
class StepperController_esp32_stepper_Test extends StepperController_esp32_stepper_ScalaTest {

  def cmd(v: Int): std_msgs.Int32 = std_msgs.Int32(S32(v))

  def pos(v: Int): Option[std_msgs.Int32] = Some(std_msgs.Int32(S32(v)))

  val nothing: Option[std_msgs.Int32] = None()

  // ART's test mode keeps port values from one dispatch to the next, and
  // BeforeEntrypoint() re-runs initialise.  So between two dispatches of one test the
  // ports are reset and the component's GUMBO state (the positions) is put back.
  def freshPorts(): Unit = {
    val z = StepperController_esp32_stepper.zPosition
    val x = StepperController_esp32_stepper.xPosition
    val y = StepperController_esp32_stepper.yPosition
    AfterEntrypoint()
    BeforeEntrypoint()
    StepperController_esp32_stepper.zPosition = z
    StepperController_esp32_stepper.xPosition = x
    StepperController_esp32_stepper.yPosition = y
  }

  // One dispatch: put one command on its port, run compute, read the three ack ports.
  def send(axis: Char, steps: Int): (Option[std_msgs.Int32], Option[std_msgs.Int32], Option[std_msgs.Int32]) = {
    freshPorts()
    axis match {
      case 'z' => put_zCmd(cmd(steps))
      case 'x' => put_xCmd(cmd(steps))
      case _ => put_yCmd(cmd(steps))
    }
    testCompute()
    return (get_zPos(), get_xPos(), get_yPos())
  }

  test("SI-MCU-1: after initialise, a 0-step query on each axis answers 0") {
    testInitialise()
    assert(send('z', 0) == ((pos(0), nothing, nothing)))
    assert(send('x', 0) == ((nothing, pos(0), nothing)))
    assert(send('y', 0) == ((nothing, nothing, pos(0))))
  }

  test("SI-MCU-4, SI-MCU-7: Z +50 from home answers 50 on zPos only") {
    testInitialise()
    assert(send('z', 50) == ((pos(50), nothing, nothing)))
  }

  test("SI-MCU-3: Z counts modulo one revolution (0 - 1 = 199, then 199 + 1 = 0)") {
    testInitialise()
    assert(send('z', -1) == ((pos(199), nothing, nothing)))
    assert(send('z', 1) == ((pos(0), nothing, nothing)))
  }

  test("SI-MCU-5: Z +401 is rejected, the position stays and is still acknowledged") {
    testInitialise()
    assert(send('z', 30) == ((pos(30), nothing, nothing)))
    assert(send('z', 401) == ((pos(30), nothing, nothing)))
  }

  test("SI-MCU-8, SI-MCU-11: X +25 (to +45 deg) answers 25 on xPos only") {
    testInitialise()
    assert(send('x', 25) == ((nothing, pos(25), nothing)))
  }

  test("SI-MCU-9, SI-MCU-2: X +1 past +45 deg is rejected, tilt stays 25") {
    testInitialise()
    assert(send('x', 25) == ((nothing, pos(25), nothing)))
    assert(send('x', 1) == ((nothing, pos(25), nothing)))
    assert(send('x', -25) == ((nothing, pos(0), nothing)))
  }

  test("SI-MCU-12, SI-MCU-15: Y -1 from home wraps to 199 on yPos only") {
    testInitialise()
    assert(send('y', -1) == ((nothing, nothing, pos(199))))
  }

  test("SI-MCU-13: Y +1000 is rejected, the position stays") {
    testInitialise()
    assert(send('y', 100) == ((nothing, nothing, pos(100))))
    assert(send('y', 1000) == ((nothing, nothing, pos(100))))
  }

  test("SI-MCU-6, SI-MCU-10, SI-MCU-14: a move on one axis leaves the other two where they are") {
    testInitialise()
    assert(send('x', 10) == ((nothing, pos(10), nothing)))
    assert(send('y', 20) == ((nothing, nothing, pos(20))))
    assert(send('z', 30) == ((pos(30), nothing, nothing)))
    // 0-step queries show that X and Y did not move while Z did
    assert(send('x', 0) == ((nothing, pos(10), nothing)))
    assert(send('y', 0) == ((nothing, nothing, pos(20))))
  }

  test("Demo sequence from the prelim runbook (Part B): 50, 25, 100, 10 rejected, -25") {
    testInitialise()
    assert(send('z', 50) == ((pos(50), nothing, nothing)))
    assert(send('x', 25) == ((nothing, pos(25), nothing)))
    assert(send('y', 100) == ((nothing, nothing, pos(100))))
    assert(send('x', 10) == ((nothing, pos(25), nothing)))
    assert(send('x', -25) == ((nothing, pos(0), nothing)))
  }
}
