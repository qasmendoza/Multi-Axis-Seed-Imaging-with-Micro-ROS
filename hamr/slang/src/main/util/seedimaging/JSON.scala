// #Sireum
// @formatter:off

// This file is auto-generated from Int32.scala, Base_Types.scala, GUMBO__Library.scala, ScanController_jetson_scan_Containers.scala, StepperController_esp32_stepper_Containers.scala, Container.scala, DataContent.scala, Aux_Types.scala

package seedimaging

import org.sireum._
import org.sireum.Json.Printer._

object JSON {

  object Printer {

    @pure def printstd_msgsInt32(o: std_msgs.Int32): ST = {
      return printObject(ISZ(
        ("type", st""""std_msgs.Int32""""),
        ("data", printS32(o.data))
      ))
    }

    @pure def printstd_msgsInt32_Payload(o: std_msgs.Int32_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""std_msgs.Int32_Payload""""),
        ("value", printstd_msgsInt32(o.value))
      ))
    }

    @pure def printBase_TypesBoolean_Payload(o: Base_Types.Boolean_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Boolean_Payload""""),
        ("value", printB(o.value))
      ))
    }

    @pure def printBase_TypesInteger_Payload(o: Base_Types.Integer_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Integer_Payload""""),
        ("value", printZ(o.value))
      ))
    }

    @pure def printBase_TypesInteger_8_Payload(o: Base_Types.Integer_8_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Integer_8_Payload""""),
        ("value", printS8(o.value))
      ))
    }

    @pure def printBase_TypesInteger_16_Payload(o: Base_Types.Integer_16_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Integer_16_Payload""""),
        ("value", printS16(o.value))
      ))
    }

    @pure def printBase_TypesInteger_32_Payload(o: Base_Types.Integer_32_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Integer_32_Payload""""),
        ("value", printS32(o.value))
      ))
    }

    @pure def printBase_TypesInteger_64_Payload(o: Base_Types.Integer_64_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Integer_64_Payload""""),
        ("value", printS64(o.value))
      ))
    }

    @pure def printBase_TypesUnsigned_8_Payload(o: Base_Types.Unsigned_8_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Unsigned_8_Payload""""),
        ("value", printU8(o.value))
      ))
    }

    @pure def printBase_TypesUnsigned_16_Payload(o: Base_Types.Unsigned_16_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Unsigned_16_Payload""""),
        ("value", printU16(o.value))
      ))
    }

    @pure def printBase_TypesUnsigned_32_Payload(o: Base_Types.Unsigned_32_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Unsigned_32_Payload""""),
        ("value", printU32(o.value))
      ))
    }

    @pure def printBase_TypesUnsigned_64_Payload(o: Base_Types.Unsigned_64_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Unsigned_64_Payload""""),
        ("value", printU64(o.value))
      ))
    }

    @pure def printBase_TypesFloat_Payload(o: Base_Types.Float_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Float_Payload""""),
        ("value", printR(o.value))
      ))
    }

    @pure def printBase_TypesFloat_32_Payload(o: Base_Types.Float_32_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Float_32_Payload""""),
        ("value", printF32(o.value))
      ))
    }

    @pure def printBase_TypesFloat_64_Payload(o: Base_Types.Float_64_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Float_64_Payload""""),
        ("value", printF64(o.value))
      ))
    }

    @pure def printBase_TypesCharacter_Payload(o: Base_Types.Character_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Character_Payload""""),
        ("value", printC(o.value))
      ))
    }

    @pure def printBase_TypesString_Payload(o: Base_Types.String_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.String_Payload""""),
        ("value", printString(o.value))
      ))
    }

    @pure def printBase_TypesBits_Payload(o: Base_Types.Bits_Payload): ST = {
      return printObject(ISZ(
        ("type", st""""Base_Types.Bits_Payload""""),
        ("value", printISZ(T, o.value, printB _))
      ))
    }

    @pure def printSeedImagingScanController_jetson_scan_PreState_Container(o: SeedImaging.ScanController_jetson_scan_PreState_Container): ST = {
      o match {
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_P => return printSeedImagingScanController_jetson_scan_PreState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_PS => return printSeedImagingScanController_jetson_scan_PreState_Container_PS(o)
      }
    }

    @pure def printSeedImagingScanController_jetson_scan_PreState_Container_P(o: SeedImaging.ScanController_jetson_scan_PreState_Container_P): ST = {
      return printObject(ISZ(
        ("type", st""""SeedImaging.ScanController_jetson_scan_PreState_Container_P""""),
        ("api_start", printOption(F, o.api_start, printstd_msgsInt32 _)),
        ("api_xPos", printOption(F, o.api_xPos, printstd_msgsInt32 _)),
        ("api_yPos", printOption(F, o.api_yPos, printstd_msgsInt32 _)),
        ("api_zPos", printOption(F, o.api_zPos, printstd_msgsInt32 _))
      ))
    }

    @pure def printSeedImagingScanController_jetson_scan_PreState_Container_PS(o: SeedImaging.ScanController_jetson_scan_PreState_Container_PS): ST = {
      return printObject(ISZ(
        ("type", st""""SeedImaging.ScanController_jetson_scan_PreState_Container_PS""""),
        ("In_awaitAxis", printS32(o.In_awaitAxis)),
        ("In_halted", printB(o.In_halted)),
        ("In_pending", printB(o.In_pending)),
        ("In_planEnd", printS32(o.In_planEnd)),
        ("In_planIndex", printS32(o.In_planIndex)),
        ("In_tiltEstimate", printS32(o.In_tiltEstimate)),
        ("api_start", printOption(F, o.api_start, printstd_msgsInt32 _)),
        ("api_xPos", printOption(F, o.api_xPos, printstd_msgsInt32 _)),
        ("api_yPos", printOption(F, o.api_yPos, printstd_msgsInt32 _)),
        ("api_zPos", printOption(F, o.api_zPos, printstd_msgsInt32 _))
      ))
    }

    @pure def printSeedImagingScanController_jetson_scan_PostState_Container(o: SeedImaging.ScanController_jetson_scan_PostState_Container): ST = {
      o match {
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_P => return printSeedImagingScanController_jetson_scan_PostState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_PS => return printSeedImagingScanController_jetson_scan_PostState_Container_PS(o)
      }
    }

    @pure def printSeedImagingScanController_jetson_scan_PostState_Container_P(o: SeedImaging.ScanController_jetson_scan_PostState_Container_P): ST = {
      return printObject(ISZ(
        ("type", st""""SeedImaging.ScanController_jetson_scan_PostState_Container_P""""),
        ("api_xCmd", printOption(F, o.api_xCmd, printstd_msgsInt32 _)),
        ("api_yCmd", printOption(F, o.api_yCmd, printstd_msgsInt32 _)),
        ("api_zCmd", printOption(F, o.api_zCmd, printstd_msgsInt32 _))
      ))
    }

    @pure def printSeedImagingScanController_jetson_scan_PostState_Container_PS(o: SeedImaging.ScanController_jetson_scan_PostState_Container_PS): ST = {
      return printObject(ISZ(
        ("type", st""""SeedImaging.ScanController_jetson_scan_PostState_Container_PS""""),
        ("awaitAxis", printS32(o.awaitAxis)),
        ("halted", printB(o.halted)),
        ("pending", printB(o.pending)),
        ("planEnd", printS32(o.planEnd)),
        ("planIndex", printS32(o.planIndex)),
        ("tiltEstimate", printS32(o.tiltEstimate)),
        ("api_xCmd", printOption(F, o.api_xCmd, printstd_msgsInt32 _)),
        ("api_yCmd", printOption(F, o.api_yCmd, printstd_msgsInt32 _)),
        ("api_zCmd", printOption(F, o.api_zCmd, printstd_msgsInt32 _))
      ))
    }

    @pure def printSeedImagingStepperController_esp32_stepper_PreState_Container(o: SeedImaging.StepperController_esp32_stepper_PreState_Container): ST = {
      o match {
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_P => return printSeedImagingStepperController_esp32_stepper_PreState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_PS => return printSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o)
      }
    }

    @pure def printSeedImagingStepperController_esp32_stepper_PreState_Container_P(o: SeedImaging.StepperController_esp32_stepper_PreState_Container_P): ST = {
      return printObject(ISZ(
        ("type", st""""SeedImaging.StepperController_esp32_stepper_PreState_Container_P""""),
        ("api_xCmd", printOption(F, o.api_xCmd, printstd_msgsInt32 _)),
        ("api_yCmd", printOption(F, o.api_yCmd, printstd_msgsInt32 _)),
        ("api_zCmd", printOption(F, o.api_zCmd, printstd_msgsInt32 _))
      ))
    }

    @pure def printSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o: SeedImaging.StepperController_esp32_stepper_PreState_Container_PS): ST = {
      return printObject(ISZ(
        ("type", st""""SeedImaging.StepperController_esp32_stepper_PreState_Container_PS""""),
        ("In_xPosition", printS32(o.In_xPosition)),
        ("In_yPosition", printS32(o.In_yPosition)),
        ("In_zPosition", printS32(o.In_zPosition)),
        ("api_xCmd", printOption(F, o.api_xCmd, printstd_msgsInt32 _)),
        ("api_yCmd", printOption(F, o.api_yCmd, printstd_msgsInt32 _)),
        ("api_zCmd", printOption(F, o.api_zCmd, printstd_msgsInt32 _))
      ))
    }

    @pure def printSeedImagingStepperController_esp32_stepper_PostState_Container(o: SeedImaging.StepperController_esp32_stepper_PostState_Container): ST = {
      o match {
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_P => return printSeedImagingStepperController_esp32_stepper_PostState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_PS => return printSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o)
      }
    }

    @pure def printSeedImagingStepperController_esp32_stepper_PostState_Container_P(o: SeedImaging.StepperController_esp32_stepper_PostState_Container_P): ST = {
      return printObject(ISZ(
        ("type", st""""SeedImaging.StepperController_esp32_stepper_PostState_Container_P""""),
        ("api_xPos", printOption(F, o.api_xPos, printstd_msgsInt32 _)),
        ("api_yPos", printOption(F, o.api_yPos, printstd_msgsInt32 _)),
        ("api_zPos", printOption(F, o.api_zPos, printstd_msgsInt32 _))
      ))
    }

    @pure def printSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o: SeedImaging.StepperController_esp32_stepper_PostState_Container_PS): ST = {
      return printObject(ISZ(
        ("type", st""""SeedImaging.StepperController_esp32_stepper_PostState_Container_PS""""),
        ("xPosition", printS32(o.xPosition)),
        ("yPosition", printS32(o.yPosition)),
        ("zPosition", printS32(o.zPosition)),
        ("api_xPos", printOption(F, o.api_xPos, printstd_msgsInt32 _)),
        ("api_yPos", printOption(F, o.api_yPos, printstd_msgsInt32 _)),
        ("api_zPos", printOption(F, o.api_zPos, printstd_msgsInt32 _))
      ))
    }

    @pure def printutilContainer(o: util.Container): ST = {
      o match {
        case o: util.EmptyContainer => return printutilEmptyContainer(o)
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_P => return printSeedImagingScanController_jetson_scan_PreState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_PS => return printSeedImagingScanController_jetson_scan_PreState_Container_PS(o)
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_P => return printSeedImagingScanController_jetson_scan_PostState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_PS => return printSeedImagingScanController_jetson_scan_PostState_Container_PS(o)
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_P => return printSeedImagingStepperController_esp32_stepper_PreState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_PS => return printSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o)
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_P => return printSeedImagingStepperController_esp32_stepper_PostState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_PS => return printSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o)
      }
    }

    @pure def printutilEmptyContainer(o: util.EmptyContainer): ST = {
      return printObject(ISZ(
        ("type", st""""util.EmptyContainer"""")
      ))
    }

    @pure def print_artDataContent(o: art.DataContent): ST = {
      o match {
        case o: art.Empty => return print_artEmpty(o)
        case o: Base_Types.Boolean_Payload => return printBase_TypesBoolean_Payload(o)
        case o: Base_Types.Integer_Payload => return printBase_TypesInteger_Payload(o)
        case o: Base_Types.Integer_8_Payload => return printBase_TypesInteger_8_Payload(o)
        case o: Base_Types.Integer_16_Payload => return printBase_TypesInteger_16_Payload(o)
        case o: Base_Types.Integer_32_Payload => return printBase_TypesInteger_32_Payload(o)
        case o: Base_Types.Integer_64_Payload => return printBase_TypesInteger_64_Payload(o)
        case o: Base_Types.Unsigned_8_Payload => return printBase_TypesUnsigned_8_Payload(o)
        case o: Base_Types.Unsigned_16_Payload => return printBase_TypesUnsigned_16_Payload(o)
        case o: Base_Types.Unsigned_32_Payload => return printBase_TypesUnsigned_32_Payload(o)
        case o: Base_Types.Unsigned_64_Payload => return printBase_TypesUnsigned_64_Payload(o)
        case o: Base_Types.Float_Payload => return printBase_TypesFloat_Payload(o)
        case o: Base_Types.Float_32_Payload => return printBase_TypesFloat_32_Payload(o)
        case o: Base_Types.Float_64_Payload => return printBase_TypesFloat_64_Payload(o)
        case o: Base_Types.Character_Payload => return printBase_TypesCharacter_Payload(o)
        case o: Base_Types.String_Payload => return printBase_TypesString_Payload(o)
        case o: Base_Types.Bits_Payload => return printBase_TypesBits_Payload(o)
        case o: util.EmptyContainer => return printutilEmptyContainer(o)
        case o: std_msgs.Int32_Payload => return printstd_msgsInt32_Payload(o)
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_P => return printSeedImagingScanController_jetson_scan_PreState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_PS => return printSeedImagingScanController_jetson_scan_PreState_Container_PS(o)
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_P => return printSeedImagingScanController_jetson_scan_PostState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_PS => return printSeedImagingScanController_jetson_scan_PostState_Container_PS(o)
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_P => return printSeedImagingStepperController_esp32_stepper_PreState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_PS => return printSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o)
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_P => return printSeedImagingStepperController_esp32_stepper_PostState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_PS => return printSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o)
      }
    }

    @pure def print_artEmpty(o: art.Empty): ST = {
      return printObject(ISZ(
        ("type", st""""art.Empty"""")
      ))
    }

  }

  @record class Parser(val input: String) {
    val parser: Json.Parser = Json.Parser.create(input)

    def errorOpt: Option[Json.ErrorMsg] = {
      return parser.errorOpt
    }

    def parsestd_msgsInt32(): std_msgs.Int32 = {
      val r = parsestd_msgsInt32T(F)
      return r
    }

    def parsestd_msgsInt32T(typeParsed: B): std_msgs.Int32 = {
      if (!typeParsed) {
        parser.parseObjectType("std_msgs.Int32")
      }
      parser.parseObjectKey("data")
      val data = parser.parseS32()
      parser.parseObjectNext()
      return std_msgs.Int32(data)
    }

    def parsestd_msgsInt32_Payload(): std_msgs.Int32_Payload = {
      val r = parsestd_msgsInt32_PayloadT(F)
      return r
    }

    def parsestd_msgsInt32_PayloadT(typeParsed: B): std_msgs.Int32_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("std_msgs.Int32_Payload")
      }
      parser.parseObjectKey("value")
      val value = parsestd_msgsInt32()
      parser.parseObjectNext()
      return std_msgs.Int32_Payload(value)
    }

    def parseBase_TypesBoolean_Payload(): Base_Types.Boolean_Payload = {
      val r = parseBase_TypesBoolean_PayloadT(F)
      return r
    }

    def parseBase_TypesBoolean_PayloadT(typeParsed: B): Base_Types.Boolean_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Boolean_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseB()
      parser.parseObjectNext()
      return Base_Types.Boolean_Payload(value)
    }

    def parseBase_TypesInteger_Payload(): Base_Types.Integer_Payload = {
      val r = parseBase_TypesInteger_PayloadT(F)
      return r
    }

    def parseBase_TypesInteger_PayloadT(typeParsed: B): Base_Types.Integer_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Integer_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseZ()
      parser.parseObjectNext()
      return Base_Types.Integer_Payload(value)
    }

    def parseBase_TypesInteger_8_Payload(): Base_Types.Integer_8_Payload = {
      val r = parseBase_TypesInteger_8_PayloadT(F)
      return r
    }

    def parseBase_TypesInteger_8_PayloadT(typeParsed: B): Base_Types.Integer_8_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Integer_8_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseS8()
      parser.parseObjectNext()
      return Base_Types.Integer_8_Payload(value)
    }

    def parseBase_TypesInteger_16_Payload(): Base_Types.Integer_16_Payload = {
      val r = parseBase_TypesInteger_16_PayloadT(F)
      return r
    }

    def parseBase_TypesInteger_16_PayloadT(typeParsed: B): Base_Types.Integer_16_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Integer_16_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseS16()
      parser.parseObjectNext()
      return Base_Types.Integer_16_Payload(value)
    }

    def parseBase_TypesInteger_32_Payload(): Base_Types.Integer_32_Payload = {
      val r = parseBase_TypesInteger_32_PayloadT(F)
      return r
    }

    def parseBase_TypesInteger_32_PayloadT(typeParsed: B): Base_Types.Integer_32_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Integer_32_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseS32()
      parser.parseObjectNext()
      return Base_Types.Integer_32_Payload(value)
    }

    def parseBase_TypesInteger_64_Payload(): Base_Types.Integer_64_Payload = {
      val r = parseBase_TypesInteger_64_PayloadT(F)
      return r
    }

    def parseBase_TypesInteger_64_PayloadT(typeParsed: B): Base_Types.Integer_64_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Integer_64_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseS64()
      parser.parseObjectNext()
      return Base_Types.Integer_64_Payload(value)
    }

    def parseBase_TypesUnsigned_8_Payload(): Base_Types.Unsigned_8_Payload = {
      val r = parseBase_TypesUnsigned_8_PayloadT(F)
      return r
    }

    def parseBase_TypesUnsigned_8_PayloadT(typeParsed: B): Base_Types.Unsigned_8_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Unsigned_8_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseU8()
      parser.parseObjectNext()
      return Base_Types.Unsigned_8_Payload(value)
    }

    def parseBase_TypesUnsigned_16_Payload(): Base_Types.Unsigned_16_Payload = {
      val r = parseBase_TypesUnsigned_16_PayloadT(F)
      return r
    }

    def parseBase_TypesUnsigned_16_PayloadT(typeParsed: B): Base_Types.Unsigned_16_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Unsigned_16_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseU16()
      parser.parseObjectNext()
      return Base_Types.Unsigned_16_Payload(value)
    }

    def parseBase_TypesUnsigned_32_Payload(): Base_Types.Unsigned_32_Payload = {
      val r = parseBase_TypesUnsigned_32_PayloadT(F)
      return r
    }

    def parseBase_TypesUnsigned_32_PayloadT(typeParsed: B): Base_Types.Unsigned_32_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Unsigned_32_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseU32()
      parser.parseObjectNext()
      return Base_Types.Unsigned_32_Payload(value)
    }

    def parseBase_TypesUnsigned_64_Payload(): Base_Types.Unsigned_64_Payload = {
      val r = parseBase_TypesUnsigned_64_PayloadT(F)
      return r
    }

    def parseBase_TypesUnsigned_64_PayloadT(typeParsed: B): Base_Types.Unsigned_64_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Unsigned_64_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseU64()
      parser.parseObjectNext()
      return Base_Types.Unsigned_64_Payload(value)
    }

    def parseBase_TypesFloat_Payload(): Base_Types.Float_Payload = {
      val r = parseBase_TypesFloat_PayloadT(F)
      return r
    }

    def parseBase_TypesFloat_PayloadT(typeParsed: B): Base_Types.Float_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Float_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseR()
      parser.parseObjectNext()
      return Base_Types.Float_Payload(value)
    }

    def parseBase_TypesFloat_32_Payload(): Base_Types.Float_32_Payload = {
      val r = parseBase_TypesFloat_32_PayloadT(F)
      return r
    }

    def parseBase_TypesFloat_32_PayloadT(typeParsed: B): Base_Types.Float_32_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Float_32_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseF32()
      parser.parseObjectNext()
      return Base_Types.Float_32_Payload(value)
    }

    def parseBase_TypesFloat_64_Payload(): Base_Types.Float_64_Payload = {
      val r = parseBase_TypesFloat_64_PayloadT(F)
      return r
    }

    def parseBase_TypesFloat_64_PayloadT(typeParsed: B): Base_Types.Float_64_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Float_64_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseF64()
      parser.parseObjectNext()
      return Base_Types.Float_64_Payload(value)
    }

    def parseBase_TypesCharacter_Payload(): Base_Types.Character_Payload = {
      val r = parseBase_TypesCharacter_PayloadT(F)
      return r
    }

    def parseBase_TypesCharacter_PayloadT(typeParsed: B): Base_Types.Character_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Character_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseC()
      parser.parseObjectNext()
      return Base_Types.Character_Payload(value)
    }

    def parseBase_TypesString_Payload(): Base_Types.String_Payload = {
      val r = parseBase_TypesString_PayloadT(F)
      return r
    }

    def parseBase_TypesString_PayloadT(typeParsed: B): Base_Types.String_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.String_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseString()
      parser.parseObjectNext()
      return Base_Types.String_Payload(value)
    }

    def parseBase_TypesBits_Payload(): Base_Types.Bits_Payload = {
      val r = parseBase_TypesBits_PayloadT(F)
      return r
    }

    def parseBase_TypesBits_PayloadT(typeParsed: B): Base_Types.Bits_Payload = {
      if (!typeParsed) {
        parser.parseObjectType("Base_Types.Bits_Payload")
      }
      parser.parseObjectKey("value")
      val value = parser.parseISZ(parser.parseB _)
      parser.parseObjectNext()
      return Base_Types.Bits_Payload(value)
    }

    def parseSeedImagingScanController_jetson_scan_PreState_Container(): SeedImaging.ScanController_jetson_scan_PreState_Container = {
      val t = parser.parseObjectTypes(ISZ("SeedImaging.ScanController_jetson_scan_PreState_Container_P", "SeedImaging.ScanController_jetson_scan_PreState_Container_PS"))
      t.native match {
        case "SeedImaging.ScanController_jetson_scan_PreState_Container_P" => val r = parseSeedImagingScanController_jetson_scan_PreState_Container_PT(T); return r
        case "SeedImaging.ScanController_jetson_scan_PreState_Container_PS" => val r = parseSeedImagingScanController_jetson_scan_PreState_Container_PST(T); return r
        case _ => val r = parseSeedImagingScanController_jetson_scan_PreState_Container_PST(T); return r
      }
    }

    def parseSeedImagingScanController_jetson_scan_PreState_Container_P(): SeedImaging.ScanController_jetson_scan_PreState_Container_P = {
      val r = parseSeedImagingScanController_jetson_scan_PreState_Container_PT(F)
      return r
    }

    def parseSeedImagingScanController_jetson_scan_PreState_Container_PT(typeParsed: B): SeedImaging.ScanController_jetson_scan_PreState_Container_P = {
      if (!typeParsed) {
        parser.parseObjectType("SeedImaging.ScanController_jetson_scan_PreState_Container_P")
      }
      parser.parseObjectKey("api_start")
      val api_start = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_xPos")
      val api_xPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_yPos")
      val api_yPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_zPos")
      val api_zPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      return SeedImaging.ScanController_jetson_scan_PreState_Container_P(api_start, api_xPos, api_yPos, api_zPos)
    }

    def parseSeedImagingScanController_jetson_scan_PreState_Container_PS(): SeedImaging.ScanController_jetson_scan_PreState_Container_PS = {
      val r = parseSeedImagingScanController_jetson_scan_PreState_Container_PST(F)
      return r
    }

    def parseSeedImagingScanController_jetson_scan_PreState_Container_PST(typeParsed: B): SeedImaging.ScanController_jetson_scan_PreState_Container_PS = {
      if (!typeParsed) {
        parser.parseObjectType("SeedImaging.ScanController_jetson_scan_PreState_Container_PS")
      }
      parser.parseObjectKey("In_awaitAxis")
      val In_awaitAxis = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("In_halted")
      val In_halted = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("In_pending")
      val In_pending = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("In_planEnd")
      val In_planEnd = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("In_planIndex")
      val In_planIndex = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("In_tiltEstimate")
      val In_tiltEstimate = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("api_start")
      val api_start = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_xPos")
      val api_xPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_yPos")
      val api_yPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_zPos")
      val api_zPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      return SeedImaging.ScanController_jetson_scan_PreState_Container_PS(In_awaitAxis, In_halted, In_pending, In_planEnd, In_planIndex, In_tiltEstimate, api_start, api_xPos, api_yPos, api_zPos)
    }

    def parseSeedImagingScanController_jetson_scan_PostState_Container(): SeedImaging.ScanController_jetson_scan_PostState_Container = {
      val t = parser.parseObjectTypes(ISZ("SeedImaging.ScanController_jetson_scan_PostState_Container_P", "SeedImaging.ScanController_jetson_scan_PostState_Container_PS"))
      t.native match {
        case "SeedImaging.ScanController_jetson_scan_PostState_Container_P" => val r = parseSeedImagingScanController_jetson_scan_PostState_Container_PT(T); return r
        case "SeedImaging.ScanController_jetson_scan_PostState_Container_PS" => val r = parseSeedImagingScanController_jetson_scan_PostState_Container_PST(T); return r
        case _ => val r = parseSeedImagingScanController_jetson_scan_PostState_Container_PST(T); return r
      }
    }

    def parseSeedImagingScanController_jetson_scan_PostState_Container_P(): SeedImaging.ScanController_jetson_scan_PostState_Container_P = {
      val r = parseSeedImagingScanController_jetson_scan_PostState_Container_PT(F)
      return r
    }

    def parseSeedImagingScanController_jetson_scan_PostState_Container_PT(typeParsed: B): SeedImaging.ScanController_jetson_scan_PostState_Container_P = {
      if (!typeParsed) {
        parser.parseObjectType("SeedImaging.ScanController_jetson_scan_PostState_Container_P")
      }
      parser.parseObjectKey("api_xCmd")
      val api_xCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_yCmd")
      val api_yCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_zCmd")
      val api_zCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      return SeedImaging.ScanController_jetson_scan_PostState_Container_P(api_xCmd, api_yCmd, api_zCmd)
    }

    def parseSeedImagingScanController_jetson_scan_PostState_Container_PS(): SeedImaging.ScanController_jetson_scan_PostState_Container_PS = {
      val r = parseSeedImagingScanController_jetson_scan_PostState_Container_PST(F)
      return r
    }

    def parseSeedImagingScanController_jetson_scan_PostState_Container_PST(typeParsed: B): SeedImaging.ScanController_jetson_scan_PostState_Container_PS = {
      if (!typeParsed) {
        parser.parseObjectType("SeedImaging.ScanController_jetson_scan_PostState_Container_PS")
      }
      parser.parseObjectKey("awaitAxis")
      val awaitAxis = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("halted")
      val halted = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("pending")
      val pending = parser.parseB()
      parser.parseObjectNext()
      parser.parseObjectKey("planEnd")
      val planEnd = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("planIndex")
      val planIndex = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("tiltEstimate")
      val tiltEstimate = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("api_xCmd")
      val api_xCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_yCmd")
      val api_yCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_zCmd")
      val api_zCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      return SeedImaging.ScanController_jetson_scan_PostState_Container_PS(awaitAxis, halted, pending, planEnd, planIndex, tiltEstimate, api_xCmd, api_yCmd, api_zCmd)
    }

    def parseSeedImagingStepperController_esp32_stepper_PreState_Container(): SeedImaging.StepperController_esp32_stepper_PreState_Container = {
      val t = parser.parseObjectTypes(ISZ("SeedImaging.StepperController_esp32_stepper_PreState_Container_P", "SeedImaging.StepperController_esp32_stepper_PreState_Container_PS"))
      t.native match {
        case "SeedImaging.StepperController_esp32_stepper_PreState_Container_P" => val r = parseSeedImagingStepperController_esp32_stepper_PreState_Container_PT(T); return r
        case "SeedImaging.StepperController_esp32_stepper_PreState_Container_PS" => val r = parseSeedImagingStepperController_esp32_stepper_PreState_Container_PST(T); return r
        case _ => val r = parseSeedImagingStepperController_esp32_stepper_PreState_Container_PST(T); return r
      }
    }

    def parseSeedImagingStepperController_esp32_stepper_PreState_Container_P(): SeedImaging.StepperController_esp32_stepper_PreState_Container_P = {
      val r = parseSeedImagingStepperController_esp32_stepper_PreState_Container_PT(F)
      return r
    }

    def parseSeedImagingStepperController_esp32_stepper_PreState_Container_PT(typeParsed: B): SeedImaging.StepperController_esp32_stepper_PreState_Container_P = {
      if (!typeParsed) {
        parser.parseObjectType("SeedImaging.StepperController_esp32_stepper_PreState_Container_P")
      }
      parser.parseObjectKey("api_xCmd")
      val api_xCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_yCmd")
      val api_yCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_zCmd")
      val api_zCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      return SeedImaging.StepperController_esp32_stepper_PreState_Container_P(api_xCmd, api_yCmd, api_zCmd)
    }

    def parseSeedImagingStepperController_esp32_stepper_PreState_Container_PS(): SeedImaging.StepperController_esp32_stepper_PreState_Container_PS = {
      val r = parseSeedImagingStepperController_esp32_stepper_PreState_Container_PST(F)
      return r
    }

    def parseSeedImagingStepperController_esp32_stepper_PreState_Container_PST(typeParsed: B): SeedImaging.StepperController_esp32_stepper_PreState_Container_PS = {
      if (!typeParsed) {
        parser.parseObjectType("SeedImaging.StepperController_esp32_stepper_PreState_Container_PS")
      }
      parser.parseObjectKey("In_xPosition")
      val In_xPosition = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("In_yPosition")
      val In_yPosition = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("In_zPosition")
      val In_zPosition = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("api_xCmd")
      val api_xCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_yCmd")
      val api_yCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_zCmd")
      val api_zCmd = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      return SeedImaging.StepperController_esp32_stepper_PreState_Container_PS(In_xPosition, In_yPosition, In_zPosition, api_xCmd, api_yCmd, api_zCmd)
    }

    def parseSeedImagingStepperController_esp32_stepper_PostState_Container(): SeedImaging.StepperController_esp32_stepper_PostState_Container = {
      val t = parser.parseObjectTypes(ISZ("SeedImaging.StepperController_esp32_stepper_PostState_Container_P", "SeedImaging.StepperController_esp32_stepper_PostState_Container_PS"))
      t.native match {
        case "SeedImaging.StepperController_esp32_stepper_PostState_Container_P" => val r = parseSeedImagingStepperController_esp32_stepper_PostState_Container_PT(T); return r
        case "SeedImaging.StepperController_esp32_stepper_PostState_Container_PS" => val r = parseSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T); return r
        case _ => val r = parseSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T); return r
      }
    }

    def parseSeedImagingStepperController_esp32_stepper_PostState_Container_P(): SeedImaging.StepperController_esp32_stepper_PostState_Container_P = {
      val r = parseSeedImagingStepperController_esp32_stepper_PostState_Container_PT(F)
      return r
    }

    def parseSeedImagingStepperController_esp32_stepper_PostState_Container_PT(typeParsed: B): SeedImaging.StepperController_esp32_stepper_PostState_Container_P = {
      if (!typeParsed) {
        parser.parseObjectType("SeedImaging.StepperController_esp32_stepper_PostState_Container_P")
      }
      parser.parseObjectKey("api_xPos")
      val api_xPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_yPos")
      val api_yPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_zPos")
      val api_zPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      return SeedImaging.StepperController_esp32_stepper_PostState_Container_P(api_xPos, api_yPos, api_zPos)
    }

    def parseSeedImagingStepperController_esp32_stepper_PostState_Container_PS(): SeedImaging.StepperController_esp32_stepper_PostState_Container_PS = {
      val r = parseSeedImagingStepperController_esp32_stepper_PostState_Container_PST(F)
      return r
    }

    def parseSeedImagingStepperController_esp32_stepper_PostState_Container_PST(typeParsed: B): SeedImaging.StepperController_esp32_stepper_PostState_Container_PS = {
      if (!typeParsed) {
        parser.parseObjectType("SeedImaging.StepperController_esp32_stepper_PostState_Container_PS")
      }
      parser.parseObjectKey("xPosition")
      val xPosition = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("yPosition")
      val yPosition = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("zPosition")
      val zPosition = parser.parseS32()
      parser.parseObjectNext()
      parser.parseObjectKey("api_xPos")
      val api_xPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_yPos")
      val api_yPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      parser.parseObjectKey("api_zPos")
      val api_zPos = parser.parseOption(parsestd_msgsInt32 _)
      parser.parseObjectNext()
      return SeedImaging.StepperController_esp32_stepper_PostState_Container_PS(xPosition, yPosition, zPosition, api_xPos, api_yPos, api_zPos)
    }

    def parseutilContainer(): util.Container = {
      val t = parser.parseObjectTypes(ISZ("util.EmptyContainer", "SeedImaging.ScanController_jetson_scan_PreState_Container_P", "SeedImaging.ScanController_jetson_scan_PreState_Container_PS", "SeedImaging.ScanController_jetson_scan_PostState_Container_P", "SeedImaging.ScanController_jetson_scan_PostState_Container_PS", "SeedImaging.StepperController_esp32_stepper_PreState_Container_P", "SeedImaging.StepperController_esp32_stepper_PreState_Container_PS", "SeedImaging.StepperController_esp32_stepper_PostState_Container_P", "SeedImaging.StepperController_esp32_stepper_PostState_Container_PS"))
      t.native match {
        case "util.EmptyContainer" => val r = parseutilEmptyContainerT(T); return r
        case "SeedImaging.ScanController_jetson_scan_PreState_Container_P" => val r = parseSeedImagingScanController_jetson_scan_PreState_Container_PT(T); return r
        case "SeedImaging.ScanController_jetson_scan_PreState_Container_PS" => val r = parseSeedImagingScanController_jetson_scan_PreState_Container_PST(T); return r
        case "SeedImaging.ScanController_jetson_scan_PostState_Container_P" => val r = parseSeedImagingScanController_jetson_scan_PostState_Container_PT(T); return r
        case "SeedImaging.ScanController_jetson_scan_PostState_Container_PS" => val r = parseSeedImagingScanController_jetson_scan_PostState_Container_PST(T); return r
        case "SeedImaging.StepperController_esp32_stepper_PreState_Container_P" => val r = parseSeedImagingStepperController_esp32_stepper_PreState_Container_PT(T); return r
        case "SeedImaging.StepperController_esp32_stepper_PreState_Container_PS" => val r = parseSeedImagingStepperController_esp32_stepper_PreState_Container_PST(T); return r
        case "SeedImaging.StepperController_esp32_stepper_PostState_Container_P" => val r = parseSeedImagingStepperController_esp32_stepper_PostState_Container_PT(T); return r
        case "SeedImaging.StepperController_esp32_stepper_PostState_Container_PS" => val r = parseSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T); return r
        case _ => val r = parseSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T); return r
      }
    }

    def parseutilEmptyContainer(): util.EmptyContainer = {
      val r = parseutilEmptyContainerT(F)
      return r
    }

    def parseutilEmptyContainerT(typeParsed: B): util.EmptyContainer = {
      if (!typeParsed) {
        parser.parseObjectType("util.EmptyContainer")
      }
      return util.EmptyContainer()
    }

    def parse_artDataContent(): art.DataContent = {
      val t = parser.parseObjectTypes(ISZ("art.Empty", "Base_Types.Boolean_Payload", "Base_Types.Integer_Payload", "Base_Types.Integer_8_Payload", "Base_Types.Integer_16_Payload", "Base_Types.Integer_32_Payload", "Base_Types.Integer_64_Payload", "Base_Types.Unsigned_8_Payload", "Base_Types.Unsigned_16_Payload", "Base_Types.Unsigned_32_Payload", "Base_Types.Unsigned_64_Payload", "Base_Types.Float_Payload", "Base_Types.Float_32_Payload", "Base_Types.Float_64_Payload", "Base_Types.Character_Payload", "Base_Types.String_Payload", "Base_Types.Bits_Payload", "util.EmptyContainer", "std_msgs.Int32_Payload", "SeedImaging.ScanController_jetson_scan_PreState_Container_P", "SeedImaging.ScanController_jetson_scan_PreState_Container_PS", "SeedImaging.ScanController_jetson_scan_PostState_Container_P", "SeedImaging.ScanController_jetson_scan_PostState_Container_PS", "SeedImaging.StepperController_esp32_stepper_PreState_Container_P", "SeedImaging.StepperController_esp32_stepper_PreState_Container_PS", "SeedImaging.StepperController_esp32_stepper_PostState_Container_P", "SeedImaging.StepperController_esp32_stepper_PostState_Container_PS"))
      t.native match {
        case "art.Empty" => val r = parse_artEmptyT(T); return r
        case "Base_Types.Boolean_Payload" => val r = parseBase_TypesBoolean_PayloadT(T); return r
        case "Base_Types.Integer_Payload" => val r = parseBase_TypesInteger_PayloadT(T); return r
        case "Base_Types.Integer_8_Payload" => val r = parseBase_TypesInteger_8_PayloadT(T); return r
        case "Base_Types.Integer_16_Payload" => val r = parseBase_TypesInteger_16_PayloadT(T); return r
        case "Base_Types.Integer_32_Payload" => val r = parseBase_TypesInteger_32_PayloadT(T); return r
        case "Base_Types.Integer_64_Payload" => val r = parseBase_TypesInteger_64_PayloadT(T); return r
        case "Base_Types.Unsigned_8_Payload" => val r = parseBase_TypesUnsigned_8_PayloadT(T); return r
        case "Base_Types.Unsigned_16_Payload" => val r = parseBase_TypesUnsigned_16_PayloadT(T); return r
        case "Base_Types.Unsigned_32_Payload" => val r = parseBase_TypesUnsigned_32_PayloadT(T); return r
        case "Base_Types.Unsigned_64_Payload" => val r = parseBase_TypesUnsigned_64_PayloadT(T); return r
        case "Base_Types.Float_Payload" => val r = parseBase_TypesFloat_PayloadT(T); return r
        case "Base_Types.Float_32_Payload" => val r = parseBase_TypesFloat_32_PayloadT(T); return r
        case "Base_Types.Float_64_Payload" => val r = parseBase_TypesFloat_64_PayloadT(T); return r
        case "Base_Types.Character_Payload" => val r = parseBase_TypesCharacter_PayloadT(T); return r
        case "Base_Types.String_Payload" => val r = parseBase_TypesString_PayloadT(T); return r
        case "Base_Types.Bits_Payload" => val r = parseBase_TypesBits_PayloadT(T); return r
        case "util.EmptyContainer" => val r = parseutilEmptyContainerT(T); return r
        case "std_msgs.Int32_Payload" => val r = parsestd_msgsInt32_PayloadT(T); return r
        case "SeedImaging.ScanController_jetson_scan_PreState_Container_P" => val r = parseSeedImagingScanController_jetson_scan_PreState_Container_PT(T); return r
        case "SeedImaging.ScanController_jetson_scan_PreState_Container_PS" => val r = parseSeedImagingScanController_jetson_scan_PreState_Container_PST(T); return r
        case "SeedImaging.ScanController_jetson_scan_PostState_Container_P" => val r = parseSeedImagingScanController_jetson_scan_PostState_Container_PT(T); return r
        case "SeedImaging.ScanController_jetson_scan_PostState_Container_PS" => val r = parseSeedImagingScanController_jetson_scan_PostState_Container_PST(T); return r
        case "SeedImaging.StepperController_esp32_stepper_PreState_Container_P" => val r = parseSeedImagingStepperController_esp32_stepper_PreState_Container_PT(T); return r
        case "SeedImaging.StepperController_esp32_stepper_PreState_Container_PS" => val r = parseSeedImagingStepperController_esp32_stepper_PreState_Container_PST(T); return r
        case "SeedImaging.StepperController_esp32_stepper_PostState_Container_P" => val r = parseSeedImagingStepperController_esp32_stepper_PostState_Container_PT(T); return r
        case "SeedImaging.StepperController_esp32_stepper_PostState_Container_PS" => val r = parseSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T); return r
        case _ => val r = parseSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T); return r
      }
    }

    def parse_artEmpty(): art.Empty = {
      val r = parse_artEmptyT(F)
      return r
    }

    def parse_artEmptyT(typeParsed: B): art.Empty = {
      if (!typeParsed) {
        parser.parseObjectType("art.Empty")
      }
      return art.Empty()
    }

    def eof(): B = {
      val r = parser.eof()
      return r
    }

  }

  def to[T](s: String, f: Parser => T): Either[T, Json.ErrorMsg] = {
    val parser = Parser(s)
    val r = f(parser)
    parser.eof()
    parser.errorOpt match {
      case Some(e) => return Either.Right(e)
      case _ => return Either.Left(r)
    }
  }

  def fromstd_msgsInt32(o: std_msgs.Int32, isCompact: B): String = {
    val st = Printer.printstd_msgsInt32(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def tostd_msgsInt32(s: String): Either[std_msgs.Int32, Json.ErrorMsg] = {
    def fstd_msgsInt32(parser: Parser): std_msgs.Int32 = {
      val r = parser.parsestd_msgsInt32()
      return r
    }
    val r = to(s, fstd_msgsInt32 _)
    return r
  }

  def fromstd_msgsInt32_Payload(o: std_msgs.Int32_Payload, isCompact: B): String = {
    val st = Printer.printstd_msgsInt32_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def tostd_msgsInt32_Payload(s: String): Either[std_msgs.Int32_Payload, Json.ErrorMsg] = {
    def fstd_msgsInt32_Payload(parser: Parser): std_msgs.Int32_Payload = {
      val r = parser.parsestd_msgsInt32_Payload()
      return r
    }
    val r = to(s, fstd_msgsInt32_Payload _)
    return r
  }

  def fromBase_TypesBoolean_Payload(o: Base_Types.Boolean_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesBoolean_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesBoolean_Payload(s: String): Either[Base_Types.Boolean_Payload, Json.ErrorMsg] = {
    def fBase_TypesBoolean_Payload(parser: Parser): Base_Types.Boolean_Payload = {
      val r = parser.parseBase_TypesBoolean_Payload()
      return r
    }
    val r = to(s, fBase_TypesBoolean_Payload _)
    return r
  }

  def fromBase_TypesInteger_Payload(o: Base_Types.Integer_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesInteger_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesInteger_Payload(s: String): Either[Base_Types.Integer_Payload, Json.ErrorMsg] = {
    def fBase_TypesInteger_Payload(parser: Parser): Base_Types.Integer_Payload = {
      val r = parser.parseBase_TypesInteger_Payload()
      return r
    }
    val r = to(s, fBase_TypesInteger_Payload _)
    return r
  }

  def fromBase_TypesInteger_8_Payload(o: Base_Types.Integer_8_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesInteger_8_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesInteger_8_Payload(s: String): Either[Base_Types.Integer_8_Payload, Json.ErrorMsg] = {
    def fBase_TypesInteger_8_Payload(parser: Parser): Base_Types.Integer_8_Payload = {
      val r = parser.parseBase_TypesInteger_8_Payload()
      return r
    }
    val r = to(s, fBase_TypesInteger_8_Payload _)
    return r
  }

  def fromBase_TypesInteger_16_Payload(o: Base_Types.Integer_16_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesInteger_16_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesInteger_16_Payload(s: String): Either[Base_Types.Integer_16_Payload, Json.ErrorMsg] = {
    def fBase_TypesInteger_16_Payload(parser: Parser): Base_Types.Integer_16_Payload = {
      val r = parser.parseBase_TypesInteger_16_Payload()
      return r
    }
    val r = to(s, fBase_TypesInteger_16_Payload _)
    return r
  }

  def fromBase_TypesInteger_32_Payload(o: Base_Types.Integer_32_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesInteger_32_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesInteger_32_Payload(s: String): Either[Base_Types.Integer_32_Payload, Json.ErrorMsg] = {
    def fBase_TypesInteger_32_Payload(parser: Parser): Base_Types.Integer_32_Payload = {
      val r = parser.parseBase_TypesInteger_32_Payload()
      return r
    }
    val r = to(s, fBase_TypesInteger_32_Payload _)
    return r
  }

  def fromBase_TypesInteger_64_Payload(o: Base_Types.Integer_64_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesInteger_64_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesInteger_64_Payload(s: String): Either[Base_Types.Integer_64_Payload, Json.ErrorMsg] = {
    def fBase_TypesInteger_64_Payload(parser: Parser): Base_Types.Integer_64_Payload = {
      val r = parser.parseBase_TypesInteger_64_Payload()
      return r
    }
    val r = to(s, fBase_TypesInteger_64_Payload _)
    return r
  }

  def fromBase_TypesUnsigned_8_Payload(o: Base_Types.Unsigned_8_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesUnsigned_8_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesUnsigned_8_Payload(s: String): Either[Base_Types.Unsigned_8_Payload, Json.ErrorMsg] = {
    def fBase_TypesUnsigned_8_Payload(parser: Parser): Base_Types.Unsigned_8_Payload = {
      val r = parser.parseBase_TypesUnsigned_8_Payload()
      return r
    }
    val r = to(s, fBase_TypesUnsigned_8_Payload _)
    return r
  }

  def fromBase_TypesUnsigned_16_Payload(o: Base_Types.Unsigned_16_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesUnsigned_16_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesUnsigned_16_Payload(s: String): Either[Base_Types.Unsigned_16_Payload, Json.ErrorMsg] = {
    def fBase_TypesUnsigned_16_Payload(parser: Parser): Base_Types.Unsigned_16_Payload = {
      val r = parser.parseBase_TypesUnsigned_16_Payload()
      return r
    }
    val r = to(s, fBase_TypesUnsigned_16_Payload _)
    return r
  }

  def fromBase_TypesUnsigned_32_Payload(o: Base_Types.Unsigned_32_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesUnsigned_32_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesUnsigned_32_Payload(s: String): Either[Base_Types.Unsigned_32_Payload, Json.ErrorMsg] = {
    def fBase_TypesUnsigned_32_Payload(parser: Parser): Base_Types.Unsigned_32_Payload = {
      val r = parser.parseBase_TypesUnsigned_32_Payload()
      return r
    }
    val r = to(s, fBase_TypesUnsigned_32_Payload _)
    return r
  }

  def fromBase_TypesUnsigned_64_Payload(o: Base_Types.Unsigned_64_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesUnsigned_64_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesUnsigned_64_Payload(s: String): Either[Base_Types.Unsigned_64_Payload, Json.ErrorMsg] = {
    def fBase_TypesUnsigned_64_Payload(parser: Parser): Base_Types.Unsigned_64_Payload = {
      val r = parser.parseBase_TypesUnsigned_64_Payload()
      return r
    }
    val r = to(s, fBase_TypesUnsigned_64_Payload _)
    return r
  }

  def fromBase_TypesFloat_Payload(o: Base_Types.Float_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesFloat_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesFloat_Payload(s: String): Either[Base_Types.Float_Payload, Json.ErrorMsg] = {
    def fBase_TypesFloat_Payload(parser: Parser): Base_Types.Float_Payload = {
      val r = parser.parseBase_TypesFloat_Payload()
      return r
    }
    val r = to(s, fBase_TypesFloat_Payload _)
    return r
  }

  def fromBase_TypesFloat_32_Payload(o: Base_Types.Float_32_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesFloat_32_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesFloat_32_Payload(s: String): Either[Base_Types.Float_32_Payload, Json.ErrorMsg] = {
    def fBase_TypesFloat_32_Payload(parser: Parser): Base_Types.Float_32_Payload = {
      val r = parser.parseBase_TypesFloat_32_Payload()
      return r
    }
    val r = to(s, fBase_TypesFloat_32_Payload _)
    return r
  }

  def fromBase_TypesFloat_64_Payload(o: Base_Types.Float_64_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesFloat_64_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesFloat_64_Payload(s: String): Either[Base_Types.Float_64_Payload, Json.ErrorMsg] = {
    def fBase_TypesFloat_64_Payload(parser: Parser): Base_Types.Float_64_Payload = {
      val r = parser.parseBase_TypesFloat_64_Payload()
      return r
    }
    val r = to(s, fBase_TypesFloat_64_Payload _)
    return r
  }

  def fromBase_TypesCharacter_Payload(o: Base_Types.Character_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesCharacter_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesCharacter_Payload(s: String): Either[Base_Types.Character_Payload, Json.ErrorMsg] = {
    def fBase_TypesCharacter_Payload(parser: Parser): Base_Types.Character_Payload = {
      val r = parser.parseBase_TypesCharacter_Payload()
      return r
    }
    val r = to(s, fBase_TypesCharacter_Payload _)
    return r
  }

  def fromBase_TypesString_Payload(o: Base_Types.String_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesString_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesString_Payload(s: String): Either[Base_Types.String_Payload, Json.ErrorMsg] = {
    def fBase_TypesString_Payload(parser: Parser): Base_Types.String_Payload = {
      val r = parser.parseBase_TypesString_Payload()
      return r
    }
    val r = to(s, fBase_TypesString_Payload _)
    return r
  }

  def fromBase_TypesBits_Payload(o: Base_Types.Bits_Payload, isCompact: B): String = {
    val st = Printer.printBase_TypesBits_Payload(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toBase_TypesBits_Payload(s: String): Either[Base_Types.Bits_Payload, Json.ErrorMsg] = {
    def fBase_TypesBits_Payload(parser: Parser): Base_Types.Bits_Payload = {
      val r = parser.parseBase_TypesBits_Payload()
      return r
    }
    val r = to(s, fBase_TypesBits_Payload _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PreState_Container(o: SeedImaging.ScanController_jetson_scan_PreState_Container, isCompact: B): String = {
    val st = Printer.printSeedImagingScanController_jetson_scan_PreState_Container(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingScanController_jetson_scan_PreState_Container(s: String): Either[SeedImaging.ScanController_jetson_scan_PreState_Container, Json.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PreState_Container(parser: Parser): SeedImaging.ScanController_jetson_scan_PreState_Container = {
      val r = parser.parseSeedImagingScanController_jetson_scan_PreState_Container()
      return r
    }
    val r = to(s, fSeedImagingScanController_jetson_scan_PreState_Container _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PreState_Container_P(o: SeedImaging.ScanController_jetson_scan_PreState_Container_P, isCompact: B): String = {
    val st = Printer.printSeedImagingScanController_jetson_scan_PreState_Container_P(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingScanController_jetson_scan_PreState_Container_P(s: String): Either[SeedImaging.ScanController_jetson_scan_PreState_Container_P, Json.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PreState_Container_P(parser: Parser): SeedImaging.ScanController_jetson_scan_PreState_Container_P = {
      val r = parser.parseSeedImagingScanController_jetson_scan_PreState_Container_P()
      return r
    }
    val r = to(s, fSeedImagingScanController_jetson_scan_PreState_Container_P _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PreState_Container_PS(o: SeedImaging.ScanController_jetson_scan_PreState_Container_PS, isCompact: B): String = {
    val st = Printer.printSeedImagingScanController_jetson_scan_PreState_Container_PS(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingScanController_jetson_scan_PreState_Container_PS(s: String): Either[SeedImaging.ScanController_jetson_scan_PreState_Container_PS, Json.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PreState_Container_PS(parser: Parser): SeedImaging.ScanController_jetson_scan_PreState_Container_PS = {
      val r = parser.parseSeedImagingScanController_jetson_scan_PreState_Container_PS()
      return r
    }
    val r = to(s, fSeedImagingScanController_jetson_scan_PreState_Container_PS _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PostState_Container(o: SeedImaging.ScanController_jetson_scan_PostState_Container, isCompact: B): String = {
    val st = Printer.printSeedImagingScanController_jetson_scan_PostState_Container(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingScanController_jetson_scan_PostState_Container(s: String): Either[SeedImaging.ScanController_jetson_scan_PostState_Container, Json.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PostState_Container(parser: Parser): SeedImaging.ScanController_jetson_scan_PostState_Container = {
      val r = parser.parseSeedImagingScanController_jetson_scan_PostState_Container()
      return r
    }
    val r = to(s, fSeedImagingScanController_jetson_scan_PostState_Container _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PostState_Container_P(o: SeedImaging.ScanController_jetson_scan_PostState_Container_P, isCompact: B): String = {
    val st = Printer.printSeedImagingScanController_jetson_scan_PostState_Container_P(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingScanController_jetson_scan_PostState_Container_P(s: String): Either[SeedImaging.ScanController_jetson_scan_PostState_Container_P, Json.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PostState_Container_P(parser: Parser): SeedImaging.ScanController_jetson_scan_PostState_Container_P = {
      val r = parser.parseSeedImagingScanController_jetson_scan_PostState_Container_P()
      return r
    }
    val r = to(s, fSeedImagingScanController_jetson_scan_PostState_Container_P _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PostState_Container_PS(o: SeedImaging.ScanController_jetson_scan_PostState_Container_PS, isCompact: B): String = {
    val st = Printer.printSeedImagingScanController_jetson_scan_PostState_Container_PS(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingScanController_jetson_scan_PostState_Container_PS(s: String): Either[SeedImaging.ScanController_jetson_scan_PostState_Container_PS, Json.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PostState_Container_PS(parser: Parser): SeedImaging.ScanController_jetson_scan_PostState_Container_PS = {
      val r = parser.parseSeedImagingScanController_jetson_scan_PostState_Container_PS()
      return r
    }
    val r = to(s, fSeedImagingScanController_jetson_scan_PostState_Container_PS _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PreState_Container(o: SeedImaging.StepperController_esp32_stepper_PreState_Container, isCompact: B): String = {
    val st = Printer.printSeedImagingStepperController_esp32_stepper_PreState_Container(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingStepperController_esp32_stepper_PreState_Container(s: String): Either[SeedImaging.StepperController_esp32_stepper_PreState_Container, Json.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PreState_Container(parser: Parser): SeedImaging.StepperController_esp32_stepper_PreState_Container = {
      val r = parser.parseSeedImagingStepperController_esp32_stepper_PreState_Container()
      return r
    }
    val r = to(s, fSeedImagingStepperController_esp32_stepper_PreState_Container _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PreState_Container_P(o: SeedImaging.StepperController_esp32_stepper_PreState_Container_P, isCompact: B): String = {
    val st = Printer.printSeedImagingStepperController_esp32_stepper_PreState_Container_P(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingStepperController_esp32_stepper_PreState_Container_P(s: String): Either[SeedImaging.StepperController_esp32_stepper_PreState_Container_P, Json.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PreState_Container_P(parser: Parser): SeedImaging.StepperController_esp32_stepper_PreState_Container_P = {
      val r = parser.parseSeedImagingStepperController_esp32_stepper_PreState_Container_P()
      return r
    }
    val r = to(s, fSeedImagingStepperController_esp32_stepper_PreState_Container_P _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o: SeedImaging.StepperController_esp32_stepper_PreState_Container_PS, isCompact: B): String = {
    val st = Printer.printSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingStepperController_esp32_stepper_PreState_Container_PS(s: String): Either[SeedImaging.StepperController_esp32_stepper_PreState_Container_PS, Json.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PreState_Container_PS(parser: Parser): SeedImaging.StepperController_esp32_stepper_PreState_Container_PS = {
      val r = parser.parseSeedImagingStepperController_esp32_stepper_PreState_Container_PS()
      return r
    }
    val r = to(s, fSeedImagingStepperController_esp32_stepper_PreState_Container_PS _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PostState_Container(o: SeedImaging.StepperController_esp32_stepper_PostState_Container, isCompact: B): String = {
    val st = Printer.printSeedImagingStepperController_esp32_stepper_PostState_Container(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingStepperController_esp32_stepper_PostState_Container(s: String): Either[SeedImaging.StepperController_esp32_stepper_PostState_Container, Json.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PostState_Container(parser: Parser): SeedImaging.StepperController_esp32_stepper_PostState_Container = {
      val r = parser.parseSeedImagingStepperController_esp32_stepper_PostState_Container()
      return r
    }
    val r = to(s, fSeedImagingStepperController_esp32_stepper_PostState_Container _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PostState_Container_P(o: SeedImaging.StepperController_esp32_stepper_PostState_Container_P, isCompact: B): String = {
    val st = Printer.printSeedImagingStepperController_esp32_stepper_PostState_Container_P(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingStepperController_esp32_stepper_PostState_Container_P(s: String): Either[SeedImaging.StepperController_esp32_stepper_PostState_Container_P, Json.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PostState_Container_P(parser: Parser): SeedImaging.StepperController_esp32_stepper_PostState_Container_P = {
      val r = parser.parseSeedImagingStepperController_esp32_stepper_PostState_Container_P()
      return r
    }
    val r = to(s, fSeedImagingStepperController_esp32_stepper_PostState_Container_P _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o: SeedImaging.StepperController_esp32_stepper_PostState_Container_PS, isCompact: B): String = {
    val st = Printer.printSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toSeedImagingStepperController_esp32_stepper_PostState_Container_PS(s: String): Either[SeedImaging.StepperController_esp32_stepper_PostState_Container_PS, Json.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PostState_Container_PS(parser: Parser): SeedImaging.StepperController_esp32_stepper_PostState_Container_PS = {
      val r = parser.parseSeedImagingStepperController_esp32_stepper_PostState_Container_PS()
      return r
    }
    val r = to(s, fSeedImagingStepperController_esp32_stepper_PostState_Container_PS _)
    return r
  }

  def fromutilContainer(o: util.Container, isCompact: B): String = {
    val st = Printer.printutilContainer(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toutilContainer(s: String): Either[util.Container, Json.ErrorMsg] = {
    def futilContainer(parser: Parser): util.Container = {
      val r = parser.parseutilContainer()
      return r
    }
    val r = to(s, futilContainer _)
    return r
  }

  def fromutilEmptyContainer(o: util.EmptyContainer, isCompact: B): String = {
    val st = Printer.printutilEmptyContainer(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def toutilEmptyContainer(s: String): Either[util.EmptyContainer, Json.ErrorMsg] = {
    def futilEmptyContainer(parser: Parser): util.EmptyContainer = {
      val r = parser.parseutilEmptyContainer()
      return r
    }
    val r = to(s, futilEmptyContainer _)
    return r
  }

  def from_artDataContent(o: art.DataContent, isCompact: B): String = {
    val st = Printer.print_artDataContent(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_artDataContent(s: String): Either[art.DataContent, Json.ErrorMsg] = {
    def f_artDataContent(parser: Parser): art.DataContent = {
      val r = parser.parse_artDataContent()
      return r
    }
    val r = to(s, f_artDataContent _)
    return r
  }

  def from_artEmpty(o: art.Empty, isCompact: B): String = {
    val st = Printer.print_artEmpty(o)
    if (isCompact) {
      return st.renderCompact
    } else {
      return st.render
    }
  }

  def to_artEmpty(s: String): Either[art.Empty, Json.ErrorMsg] = {
    def f_artEmpty(parser: Parser): art.Empty = {
      val r = parser.parse_artEmpty()
      return r
    }
    val r = to(s, f_artEmpty _)
    return r
  }

}