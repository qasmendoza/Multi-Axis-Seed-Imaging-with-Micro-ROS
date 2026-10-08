// #Sireum
// @formatter:off

// This file is auto-generated from Int32.scala, Base_Types.scala, GUMBO__Library.scala, ScanController_jetson_scan_Containers.scala, StepperController_esp32_stepper_Containers.scala, Container.scala, DataContent.scala, Aux_Types.scala

package seedimaging

import org.sireum._

object MsgPack {

  object Constants {

    val std_msgsInt32: Z = -32

    val std_msgsInt32_Payload: Z = -31

    val Base_TypesBoolean_Payload: Z = -30

    val Base_TypesInteger_Payload: Z = -29

    val Base_TypesInteger_8_Payload: Z = -28

    val Base_TypesInteger_16_Payload: Z = -27

    val Base_TypesInteger_32_Payload: Z = -26

    val Base_TypesInteger_64_Payload: Z = -25

    val Base_TypesUnsigned_8_Payload: Z = -24

    val Base_TypesUnsigned_16_Payload: Z = -23

    val Base_TypesUnsigned_32_Payload: Z = -22

    val Base_TypesUnsigned_64_Payload: Z = -21

    val Base_TypesFloat_Payload: Z = -20

    val Base_TypesFloat_32_Payload: Z = -19

    val Base_TypesFloat_64_Payload: Z = -18

    val Base_TypesCharacter_Payload: Z = -17

    val Base_TypesString_Payload: Z = -16

    val Base_TypesBits_Payload: Z = -15

    val SeedImagingScanController_jetson_scan_PreState_Container_P: Z = -14

    val SeedImagingScanController_jetson_scan_PreState_Container_PS: Z = -13

    val SeedImagingScanController_jetson_scan_PostState_Container_P: Z = -12

    val SeedImagingScanController_jetson_scan_PostState_Container_PS: Z = -11

    val SeedImagingStepperController_esp32_stepper_PreState_Container_P: Z = -10

    val SeedImagingStepperController_esp32_stepper_PreState_Container_PS: Z = -9

    val SeedImagingStepperController_esp32_stepper_PostState_Container_P: Z = -8

    val SeedImagingStepperController_esp32_stepper_PostState_Container_PS: Z = -7

    val utilEmptyContainer: Z = -6

    val _artEmpty: Z = -5

  }

  object Writer {

    @record class Default(val writer: MessagePack.Writer.Impl) extends Writer

  }

  @msig trait Writer {

    def writer: MessagePack.Writer

    def writestd_msgsInt32(o: std_msgs.Int32): Unit = {
      writer.writeZ(Constants.std_msgsInt32)
      writer.writeS32(o.data)
    }

    def writestd_msgsInt32_Payload(o: std_msgs.Int32_Payload): Unit = {
      writer.writeZ(Constants.std_msgsInt32_Payload)
      writestd_msgsInt32(o.value)
    }

    def writeBase_TypesBoolean_Payload(o: Base_Types.Boolean_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesBoolean_Payload)
      writer.writeB(o.value)
    }

    def writeBase_TypesInteger_Payload(o: Base_Types.Integer_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesInteger_Payload)
      writer.writeZ(o.value)
    }

    def writeBase_TypesInteger_8_Payload(o: Base_Types.Integer_8_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesInteger_8_Payload)
      writer.writeS8(o.value)
    }

    def writeBase_TypesInteger_16_Payload(o: Base_Types.Integer_16_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesInteger_16_Payload)
      writer.writeS16(o.value)
    }

    def writeBase_TypesInteger_32_Payload(o: Base_Types.Integer_32_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesInteger_32_Payload)
      writer.writeS32(o.value)
    }

    def writeBase_TypesInteger_64_Payload(o: Base_Types.Integer_64_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesInteger_64_Payload)
      writer.writeS64(o.value)
    }

    def writeBase_TypesUnsigned_8_Payload(o: Base_Types.Unsigned_8_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesUnsigned_8_Payload)
      writer.writeU8(o.value)
    }

    def writeBase_TypesUnsigned_16_Payload(o: Base_Types.Unsigned_16_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesUnsigned_16_Payload)
      writer.writeU16(o.value)
    }

    def writeBase_TypesUnsigned_32_Payload(o: Base_Types.Unsigned_32_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesUnsigned_32_Payload)
      writer.writeU32(o.value)
    }

    def writeBase_TypesUnsigned_64_Payload(o: Base_Types.Unsigned_64_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesUnsigned_64_Payload)
      writer.writeU64(o.value)
    }

    def writeBase_TypesFloat_Payload(o: Base_Types.Float_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesFloat_Payload)
      writer.writeR(o.value)
    }

    def writeBase_TypesFloat_32_Payload(o: Base_Types.Float_32_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesFloat_32_Payload)
      writer.writeF32(o.value)
    }

    def writeBase_TypesFloat_64_Payload(o: Base_Types.Float_64_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesFloat_64_Payload)
      writer.writeF64(o.value)
    }

    def writeBase_TypesCharacter_Payload(o: Base_Types.Character_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesCharacter_Payload)
      writer.writeC(o.value)
    }

    def writeBase_TypesString_Payload(o: Base_Types.String_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesString_Payload)
      writer.writeString(o.value)
    }

    def writeBase_TypesBits_Payload(o: Base_Types.Bits_Payload): Unit = {
      writer.writeZ(Constants.Base_TypesBits_Payload)
      writer.writeISZ(o.value, writer.writeB _)
    }

    def writeSeedImagingScanController_jetson_scan_PreState_Container(o: SeedImaging.ScanController_jetson_scan_PreState_Container): Unit = {
      o match {
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_P => writeSeedImagingScanController_jetson_scan_PreState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_PS => writeSeedImagingScanController_jetson_scan_PreState_Container_PS(o)
      }
    }

    def writeSeedImagingScanController_jetson_scan_PreState_Container_P(o: SeedImaging.ScanController_jetson_scan_PreState_Container_P): Unit = {
      writer.writeZ(Constants.SeedImagingScanController_jetson_scan_PreState_Container_P)
      writer.writeOption(o.api_start, writestd_msgsInt32 _)
      writer.writeOption(o.api_xPos, writestd_msgsInt32 _)
      writer.writeOption(o.api_yPos, writestd_msgsInt32 _)
      writer.writeOption(o.api_zPos, writestd_msgsInt32 _)
    }

    def writeSeedImagingScanController_jetson_scan_PreState_Container_PS(o: SeedImaging.ScanController_jetson_scan_PreState_Container_PS): Unit = {
      writer.writeZ(Constants.SeedImagingScanController_jetson_scan_PreState_Container_PS)
      writer.writeS32(o.In_awaitAxis)
      writer.writeB(o.In_halted)
      writer.writeB(o.In_pending)
      writer.writeS32(o.In_planEnd)
      writer.writeS32(o.In_planIndex)
      writer.writeS32(o.In_tiltEstimate)
      writer.writeOption(o.api_start, writestd_msgsInt32 _)
      writer.writeOption(o.api_xPos, writestd_msgsInt32 _)
      writer.writeOption(o.api_yPos, writestd_msgsInt32 _)
      writer.writeOption(o.api_zPos, writestd_msgsInt32 _)
    }

    def writeSeedImagingScanController_jetson_scan_PostState_Container(o: SeedImaging.ScanController_jetson_scan_PostState_Container): Unit = {
      o match {
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_P => writeSeedImagingScanController_jetson_scan_PostState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_PS => writeSeedImagingScanController_jetson_scan_PostState_Container_PS(o)
      }
    }

    def writeSeedImagingScanController_jetson_scan_PostState_Container_P(o: SeedImaging.ScanController_jetson_scan_PostState_Container_P): Unit = {
      writer.writeZ(Constants.SeedImagingScanController_jetson_scan_PostState_Container_P)
      writer.writeOption(o.api_xCmd, writestd_msgsInt32 _)
      writer.writeOption(o.api_yCmd, writestd_msgsInt32 _)
      writer.writeOption(o.api_zCmd, writestd_msgsInt32 _)
    }

    def writeSeedImagingScanController_jetson_scan_PostState_Container_PS(o: SeedImaging.ScanController_jetson_scan_PostState_Container_PS): Unit = {
      writer.writeZ(Constants.SeedImagingScanController_jetson_scan_PostState_Container_PS)
      writer.writeS32(o.awaitAxis)
      writer.writeB(o.halted)
      writer.writeB(o.pending)
      writer.writeS32(o.planEnd)
      writer.writeS32(o.planIndex)
      writer.writeS32(o.tiltEstimate)
      writer.writeOption(o.api_xCmd, writestd_msgsInt32 _)
      writer.writeOption(o.api_yCmd, writestd_msgsInt32 _)
      writer.writeOption(o.api_zCmd, writestd_msgsInt32 _)
    }

    def writeSeedImagingStepperController_esp32_stepper_PreState_Container(o: SeedImaging.StepperController_esp32_stepper_PreState_Container): Unit = {
      o match {
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_P => writeSeedImagingStepperController_esp32_stepper_PreState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_PS => writeSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o)
      }
    }

    def writeSeedImagingStepperController_esp32_stepper_PreState_Container_P(o: SeedImaging.StepperController_esp32_stepper_PreState_Container_P): Unit = {
      writer.writeZ(Constants.SeedImagingStepperController_esp32_stepper_PreState_Container_P)
      writer.writeOption(o.api_xCmd, writestd_msgsInt32 _)
      writer.writeOption(o.api_yCmd, writestd_msgsInt32 _)
      writer.writeOption(o.api_zCmd, writestd_msgsInt32 _)
    }

    def writeSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o: SeedImaging.StepperController_esp32_stepper_PreState_Container_PS): Unit = {
      writer.writeZ(Constants.SeedImagingStepperController_esp32_stepper_PreState_Container_PS)
      writer.writeS32(o.In_xPosition)
      writer.writeS32(o.In_yPosition)
      writer.writeS32(o.In_zPosition)
      writer.writeOption(o.api_xCmd, writestd_msgsInt32 _)
      writer.writeOption(o.api_yCmd, writestd_msgsInt32 _)
      writer.writeOption(o.api_zCmd, writestd_msgsInt32 _)
    }

    def writeSeedImagingStepperController_esp32_stepper_PostState_Container(o: SeedImaging.StepperController_esp32_stepper_PostState_Container): Unit = {
      o match {
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_P => writeSeedImagingStepperController_esp32_stepper_PostState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_PS => writeSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o)
      }
    }

    def writeSeedImagingStepperController_esp32_stepper_PostState_Container_P(o: SeedImaging.StepperController_esp32_stepper_PostState_Container_P): Unit = {
      writer.writeZ(Constants.SeedImagingStepperController_esp32_stepper_PostState_Container_P)
      writer.writeOption(o.api_xPos, writestd_msgsInt32 _)
      writer.writeOption(o.api_yPos, writestd_msgsInt32 _)
      writer.writeOption(o.api_zPos, writestd_msgsInt32 _)
    }

    def writeSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o: SeedImaging.StepperController_esp32_stepper_PostState_Container_PS): Unit = {
      writer.writeZ(Constants.SeedImagingStepperController_esp32_stepper_PostState_Container_PS)
      writer.writeS32(o.xPosition)
      writer.writeS32(o.yPosition)
      writer.writeS32(o.zPosition)
      writer.writeOption(o.api_xPos, writestd_msgsInt32 _)
      writer.writeOption(o.api_yPos, writestd_msgsInt32 _)
      writer.writeOption(o.api_zPos, writestd_msgsInt32 _)
    }

    def writeutilContainer(o: util.Container): Unit = {
      o match {
        case o: util.EmptyContainer => writeutilEmptyContainer(o)
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_P => writeSeedImagingScanController_jetson_scan_PreState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_PS => writeSeedImagingScanController_jetson_scan_PreState_Container_PS(o)
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_P => writeSeedImagingScanController_jetson_scan_PostState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_PS => writeSeedImagingScanController_jetson_scan_PostState_Container_PS(o)
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_P => writeSeedImagingStepperController_esp32_stepper_PreState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_PS => writeSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o)
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_P => writeSeedImagingStepperController_esp32_stepper_PostState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_PS => writeSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o)
      }
    }

    def writeutilEmptyContainer(o: util.EmptyContainer): Unit = {
      writer.writeZ(Constants.utilEmptyContainer)
    }

    def write_artDataContent(o: art.DataContent): Unit = {
      o match {
        case o: art.Empty => write_artEmpty(o)
        case o: Base_Types.Boolean_Payload => writeBase_TypesBoolean_Payload(o)
        case o: Base_Types.Integer_Payload => writeBase_TypesInteger_Payload(o)
        case o: Base_Types.Integer_8_Payload => writeBase_TypesInteger_8_Payload(o)
        case o: Base_Types.Integer_16_Payload => writeBase_TypesInteger_16_Payload(o)
        case o: Base_Types.Integer_32_Payload => writeBase_TypesInteger_32_Payload(o)
        case o: Base_Types.Integer_64_Payload => writeBase_TypesInteger_64_Payload(o)
        case o: Base_Types.Unsigned_8_Payload => writeBase_TypesUnsigned_8_Payload(o)
        case o: Base_Types.Unsigned_16_Payload => writeBase_TypesUnsigned_16_Payload(o)
        case o: Base_Types.Unsigned_32_Payload => writeBase_TypesUnsigned_32_Payload(o)
        case o: Base_Types.Unsigned_64_Payload => writeBase_TypesUnsigned_64_Payload(o)
        case o: Base_Types.Float_Payload => writeBase_TypesFloat_Payload(o)
        case o: Base_Types.Float_32_Payload => writeBase_TypesFloat_32_Payload(o)
        case o: Base_Types.Float_64_Payload => writeBase_TypesFloat_64_Payload(o)
        case o: Base_Types.Character_Payload => writeBase_TypesCharacter_Payload(o)
        case o: Base_Types.String_Payload => writeBase_TypesString_Payload(o)
        case o: Base_Types.Bits_Payload => writeBase_TypesBits_Payload(o)
        case o: util.EmptyContainer => writeutilEmptyContainer(o)
        case o: std_msgs.Int32_Payload => writestd_msgsInt32_Payload(o)
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_P => writeSeedImagingScanController_jetson_scan_PreState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PreState_Container_PS => writeSeedImagingScanController_jetson_scan_PreState_Container_PS(o)
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_P => writeSeedImagingScanController_jetson_scan_PostState_Container_P(o)
        case o: SeedImaging.ScanController_jetson_scan_PostState_Container_PS => writeSeedImagingScanController_jetson_scan_PostState_Container_PS(o)
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_P => writeSeedImagingStepperController_esp32_stepper_PreState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PreState_Container_PS => writeSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o)
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_P => writeSeedImagingStepperController_esp32_stepper_PostState_Container_P(o)
        case o: SeedImaging.StepperController_esp32_stepper_PostState_Container_PS => writeSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o)
      }
    }

    def write_artEmpty(o: art.Empty): Unit = {
      writer.writeZ(Constants._artEmpty)
    }

    def result: ISZ[U8] = {
      return writer.result
    }

  }

  object Reader {

    @record class Default(val reader: MessagePack.Reader.Impl) extends Reader {
      def errorOpt: Option[MessagePack.ErrorMsg] = {
        return reader.errorOpt
      }
    }

  }

  @msig trait Reader {

    def reader: MessagePack.Reader

    def readstd_msgsInt32(): std_msgs.Int32 = {
      val r = readstd_msgsInt32T(F)
      return r
    }

    def readstd_msgsInt32T(typeParsed: B): std_msgs.Int32 = {
      if (!typeParsed) {
        reader.expectZ(Constants.std_msgsInt32)
      }
      val data = reader.readS32()
      return std_msgs.Int32(data)
    }

    def readstd_msgsInt32_Payload(): std_msgs.Int32_Payload = {
      val r = readstd_msgsInt32_PayloadT(F)
      return r
    }

    def readstd_msgsInt32_PayloadT(typeParsed: B): std_msgs.Int32_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.std_msgsInt32_Payload)
      }
      val value = readstd_msgsInt32()
      return std_msgs.Int32_Payload(value)
    }

    def readBase_TypesBoolean_Payload(): Base_Types.Boolean_Payload = {
      val r = readBase_TypesBoolean_PayloadT(F)
      return r
    }

    def readBase_TypesBoolean_PayloadT(typeParsed: B): Base_Types.Boolean_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesBoolean_Payload)
      }
      val value = reader.readB()
      return Base_Types.Boolean_Payload(value)
    }

    def readBase_TypesInteger_Payload(): Base_Types.Integer_Payload = {
      val r = readBase_TypesInteger_PayloadT(F)
      return r
    }

    def readBase_TypesInteger_PayloadT(typeParsed: B): Base_Types.Integer_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesInteger_Payload)
      }
      val value = reader.readZ()
      return Base_Types.Integer_Payload(value)
    }

    def readBase_TypesInteger_8_Payload(): Base_Types.Integer_8_Payload = {
      val r = readBase_TypesInteger_8_PayloadT(F)
      return r
    }

    def readBase_TypesInteger_8_PayloadT(typeParsed: B): Base_Types.Integer_8_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesInteger_8_Payload)
      }
      val value = reader.readS8()
      return Base_Types.Integer_8_Payload(value)
    }

    def readBase_TypesInteger_16_Payload(): Base_Types.Integer_16_Payload = {
      val r = readBase_TypesInteger_16_PayloadT(F)
      return r
    }

    def readBase_TypesInteger_16_PayloadT(typeParsed: B): Base_Types.Integer_16_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesInteger_16_Payload)
      }
      val value = reader.readS16()
      return Base_Types.Integer_16_Payload(value)
    }

    def readBase_TypesInteger_32_Payload(): Base_Types.Integer_32_Payload = {
      val r = readBase_TypesInteger_32_PayloadT(F)
      return r
    }

    def readBase_TypesInteger_32_PayloadT(typeParsed: B): Base_Types.Integer_32_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesInteger_32_Payload)
      }
      val value = reader.readS32()
      return Base_Types.Integer_32_Payload(value)
    }

    def readBase_TypesInteger_64_Payload(): Base_Types.Integer_64_Payload = {
      val r = readBase_TypesInteger_64_PayloadT(F)
      return r
    }

    def readBase_TypesInteger_64_PayloadT(typeParsed: B): Base_Types.Integer_64_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesInteger_64_Payload)
      }
      val value = reader.readS64()
      return Base_Types.Integer_64_Payload(value)
    }

    def readBase_TypesUnsigned_8_Payload(): Base_Types.Unsigned_8_Payload = {
      val r = readBase_TypesUnsigned_8_PayloadT(F)
      return r
    }

    def readBase_TypesUnsigned_8_PayloadT(typeParsed: B): Base_Types.Unsigned_8_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesUnsigned_8_Payload)
      }
      val value = reader.readU8()
      return Base_Types.Unsigned_8_Payload(value)
    }

    def readBase_TypesUnsigned_16_Payload(): Base_Types.Unsigned_16_Payload = {
      val r = readBase_TypesUnsigned_16_PayloadT(F)
      return r
    }

    def readBase_TypesUnsigned_16_PayloadT(typeParsed: B): Base_Types.Unsigned_16_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesUnsigned_16_Payload)
      }
      val value = reader.readU16()
      return Base_Types.Unsigned_16_Payload(value)
    }

    def readBase_TypesUnsigned_32_Payload(): Base_Types.Unsigned_32_Payload = {
      val r = readBase_TypesUnsigned_32_PayloadT(F)
      return r
    }

    def readBase_TypesUnsigned_32_PayloadT(typeParsed: B): Base_Types.Unsigned_32_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesUnsigned_32_Payload)
      }
      val value = reader.readU32()
      return Base_Types.Unsigned_32_Payload(value)
    }

    def readBase_TypesUnsigned_64_Payload(): Base_Types.Unsigned_64_Payload = {
      val r = readBase_TypesUnsigned_64_PayloadT(F)
      return r
    }

    def readBase_TypesUnsigned_64_PayloadT(typeParsed: B): Base_Types.Unsigned_64_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesUnsigned_64_Payload)
      }
      val value = reader.readU64()
      return Base_Types.Unsigned_64_Payload(value)
    }

    def readBase_TypesFloat_Payload(): Base_Types.Float_Payload = {
      val r = readBase_TypesFloat_PayloadT(F)
      return r
    }

    def readBase_TypesFloat_PayloadT(typeParsed: B): Base_Types.Float_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesFloat_Payload)
      }
      val value = reader.readR()
      return Base_Types.Float_Payload(value)
    }

    def readBase_TypesFloat_32_Payload(): Base_Types.Float_32_Payload = {
      val r = readBase_TypesFloat_32_PayloadT(F)
      return r
    }

    def readBase_TypesFloat_32_PayloadT(typeParsed: B): Base_Types.Float_32_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesFloat_32_Payload)
      }
      val value = reader.readF32()
      return Base_Types.Float_32_Payload(value)
    }

    def readBase_TypesFloat_64_Payload(): Base_Types.Float_64_Payload = {
      val r = readBase_TypesFloat_64_PayloadT(F)
      return r
    }

    def readBase_TypesFloat_64_PayloadT(typeParsed: B): Base_Types.Float_64_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesFloat_64_Payload)
      }
      val value = reader.readF64()
      return Base_Types.Float_64_Payload(value)
    }

    def readBase_TypesCharacter_Payload(): Base_Types.Character_Payload = {
      val r = readBase_TypesCharacter_PayloadT(F)
      return r
    }

    def readBase_TypesCharacter_PayloadT(typeParsed: B): Base_Types.Character_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesCharacter_Payload)
      }
      val value = reader.readC()
      return Base_Types.Character_Payload(value)
    }

    def readBase_TypesString_Payload(): Base_Types.String_Payload = {
      val r = readBase_TypesString_PayloadT(F)
      return r
    }

    def readBase_TypesString_PayloadT(typeParsed: B): Base_Types.String_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesString_Payload)
      }
      val value = reader.readString()
      return Base_Types.String_Payload(value)
    }

    def readBase_TypesBits_Payload(): Base_Types.Bits_Payload = {
      val r = readBase_TypesBits_PayloadT(F)
      return r
    }

    def readBase_TypesBits_PayloadT(typeParsed: B): Base_Types.Bits_Payload = {
      if (!typeParsed) {
        reader.expectZ(Constants.Base_TypesBits_Payload)
      }
      val value = reader.readISZ(reader.readB _)
      return Base_Types.Bits_Payload(value)
    }

    def readSeedImagingScanController_jetson_scan_PreState_Container(): SeedImaging.ScanController_jetson_scan_PreState_Container = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants.SeedImagingScanController_jetson_scan_PreState_Container_P => val r = readSeedImagingScanController_jetson_scan_PreState_Container_PT(T); return r
        case Constants.SeedImagingScanController_jetson_scan_PreState_Container_PS => val r = readSeedImagingScanController_jetson_scan_PreState_Container_PST(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of SeedImaging.ScanController_jetson_scan_PreState_Container.")
          val r = readSeedImagingScanController_jetson_scan_PreState_Container_PST(T)
          return r
      }
    }

    def readSeedImagingScanController_jetson_scan_PreState_Container_P(): SeedImaging.ScanController_jetson_scan_PreState_Container_P = {
      val r = readSeedImagingScanController_jetson_scan_PreState_Container_PT(F)
      return r
    }

    def readSeedImagingScanController_jetson_scan_PreState_Container_PT(typeParsed: B): SeedImaging.ScanController_jetson_scan_PreState_Container_P = {
      if (!typeParsed) {
        reader.expectZ(Constants.SeedImagingScanController_jetson_scan_PreState_Container_P)
      }
      val api_start = reader.readOption(readstd_msgsInt32 _)
      val api_xPos = reader.readOption(readstd_msgsInt32 _)
      val api_yPos = reader.readOption(readstd_msgsInt32 _)
      val api_zPos = reader.readOption(readstd_msgsInt32 _)
      return SeedImaging.ScanController_jetson_scan_PreState_Container_P(api_start, api_xPos, api_yPos, api_zPos)
    }

    def readSeedImagingScanController_jetson_scan_PreState_Container_PS(): SeedImaging.ScanController_jetson_scan_PreState_Container_PS = {
      val r = readSeedImagingScanController_jetson_scan_PreState_Container_PST(F)
      return r
    }

    def readSeedImagingScanController_jetson_scan_PreState_Container_PST(typeParsed: B): SeedImaging.ScanController_jetson_scan_PreState_Container_PS = {
      if (!typeParsed) {
        reader.expectZ(Constants.SeedImagingScanController_jetson_scan_PreState_Container_PS)
      }
      val In_awaitAxis = reader.readS32()
      val In_halted = reader.readB()
      val In_pending = reader.readB()
      val In_planEnd = reader.readS32()
      val In_planIndex = reader.readS32()
      val In_tiltEstimate = reader.readS32()
      val api_start = reader.readOption(readstd_msgsInt32 _)
      val api_xPos = reader.readOption(readstd_msgsInt32 _)
      val api_yPos = reader.readOption(readstd_msgsInt32 _)
      val api_zPos = reader.readOption(readstd_msgsInt32 _)
      return SeedImaging.ScanController_jetson_scan_PreState_Container_PS(In_awaitAxis, In_halted, In_pending, In_planEnd, In_planIndex, In_tiltEstimate, api_start, api_xPos, api_yPos, api_zPos)
    }

    def readSeedImagingScanController_jetson_scan_PostState_Container(): SeedImaging.ScanController_jetson_scan_PostState_Container = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants.SeedImagingScanController_jetson_scan_PostState_Container_P => val r = readSeedImagingScanController_jetson_scan_PostState_Container_PT(T); return r
        case Constants.SeedImagingScanController_jetson_scan_PostState_Container_PS => val r = readSeedImagingScanController_jetson_scan_PostState_Container_PST(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of SeedImaging.ScanController_jetson_scan_PostState_Container.")
          val r = readSeedImagingScanController_jetson_scan_PostState_Container_PST(T)
          return r
      }
    }

    def readSeedImagingScanController_jetson_scan_PostState_Container_P(): SeedImaging.ScanController_jetson_scan_PostState_Container_P = {
      val r = readSeedImagingScanController_jetson_scan_PostState_Container_PT(F)
      return r
    }

    def readSeedImagingScanController_jetson_scan_PostState_Container_PT(typeParsed: B): SeedImaging.ScanController_jetson_scan_PostState_Container_P = {
      if (!typeParsed) {
        reader.expectZ(Constants.SeedImagingScanController_jetson_scan_PostState_Container_P)
      }
      val api_xCmd = reader.readOption(readstd_msgsInt32 _)
      val api_yCmd = reader.readOption(readstd_msgsInt32 _)
      val api_zCmd = reader.readOption(readstd_msgsInt32 _)
      return SeedImaging.ScanController_jetson_scan_PostState_Container_P(api_xCmd, api_yCmd, api_zCmd)
    }

    def readSeedImagingScanController_jetson_scan_PostState_Container_PS(): SeedImaging.ScanController_jetson_scan_PostState_Container_PS = {
      val r = readSeedImagingScanController_jetson_scan_PostState_Container_PST(F)
      return r
    }

    def readSeedImagingScanController_jetson_scan_PostState_Container_PST(typeParsed: B): SeedImaging.ScanController_jetson_scan_PostState_Container_PS = {
      if (!typeParsed) {
        reader.expectZ(Constants.SeedImagingScanController_jetson_scan_PostState_Container_PS)
      }
      val awaitAxis = reader.readS32()
      val halted = reader.readB()
      val pending = reader.readB()
      val planEnd = reader.readS32()
      val planIndex = reader.readS32()
      val tiltEstimate = reader.readS32()
      val api_xCmd = reader.readOption(readstd_msgsInt32 _)
      val api_yCmd = reader.readOption(readstd_msgsInt32 _)
      val api_zCmd = reader.readOption(readstd_msgsInt32 _)
      return SeedImaging.ScanController_jetson_scan_PostState_Container_PS(awaitAxis, halted, pending, planEnd, planIndex, tiltEstimate, api_xCmd, api_yCmd, api_zCmd)
    }

    def readSeedImagingStepperController_esp32_stepper_PreState_Container(): SeedImaging.StepperController_esp32_stepper_PreState_Container = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants.SeedImagingStepperController_esp32_stepper_PreState_Container_P => val r = readSeedImagingStepperController_esp32_stepper_PreState_Container_PT(T); return r
        case Constants.SeedImagingStepperController_esp32_stepper_PreState_Container_PS => val r = readSeedImagingStepperController_esp32_stepper_PreState_Container_PST(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of SeedImaging.StepperController_esp32_stepper_PreState_Container.")
          val r = readSeedImagingStepperController_esp32_stepper_PreState_Container_PST(T)
          return r
      }
    }

    def readSeedImagingStepperController_esp32_stepper_PreState_Container_P(): SeedImaging.StepperController_esp32_stepper_PreState_Container_P = {
      val r = readSeedImagingStepperController_esp32_stepper_PreState_Container_PT(F)
      return r
    }

    def readSeedImagingStepperController_esp32_stepper_PreState_Container_PT(typeParsed: B): SeedImaging.StepperController_esp32_stepper_PreState_Container_P = {
      if (!typeParsed) {
        reader.expectZ(Constants.SeedImagingStepperController_esp32_stepper_PreState_Container_P)
      }
      val api_xCmd = reader.readOption(readstd_msgsInt32 _)
      val api_yCmd = reader.readOption(readstd_msgsInt32 _)
      val api_zCmd = reader.readOption(readstd_msgsInt32 _)
      return SeedImaging.StepperController_esp32_stepper_PreState_Container_P(api_xCmd, api_yCmd, api_zCmd)
    }

    def readSeedImagingStepperController_esp32_stepper_PreState_Container_PS(): SeedImaging.StepperController_esp32_stepper_PreState_Container_PS = {
      val r = readSeedImagingStepperController_esp32_stepper_PreState_Container_PST(F)
      return r
    }

    def readSeedImagingStepperController_esp32_stepper_PreState_Container_PST(typeParsed: B): SeedImaging.StepperController_esp32_stepper_PreState_Container_PS = {
      if (!typeParsed) {
        reader.expectZ(Constants.SeedImagingStepperController_esp32_stepper_PreState_Container_PS)
      }
      val In_xPosition = reader.readS32()
      val In_yPosition = reader.readS32()
      val In_zPosition = reader.readS32()
      val api_xCmd = reader.readOption(readstd_msgsInt32 _)
      val api_yCmd = reader.readOption(readstd_msgsInt32 _)
      val api_zCmd = reader.readOption(readstd_msgsInt32 _)
      return SeedImaging.StepperController_esp32_stepper_PreState_Container_PS(In_xPosition, In_yPosition, In_zPosition, api_xCmd, api_yCmd, api_zCmd)
    }

    def readSeedImagingStepperController_esp32_stepper_PostState_Container(): SeedImaging.StepperController_esp32_stepper_PostState_Container = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants.SeedImagingStepperController_esp32_stepper_PostState_Container_P => val r = readSeedImagingStepperController_esp32_stepper_PostState_Container_PT(T); return r
        case Constants.SeedImagingStepperController_esp32_stepper_PostState_Container_PS => val r = readSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of SeedImaging.StepperController_esp32_stepper_PostState_Container.")
          val r = readSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T)
          return r
      }
    }

    def readSeedImagingStepperController_esp32_stepper_PostState_Container_P(): SeedImaging.StepperController_esp32_stepper_PostState_Container_P = {
      val r = readSeedImagingStepperController_esp32_stepper_PostState_Container_PT(F)
      return r
    }

    def readSeedImagingStepperController_esp32_stepper_PostState_Container_PT(typeParsed: B): SeedImaging.StepperController_esp32_stepper_PostState_Container_P = {
      if (!typeParsed) {
        reader.expectZ(Constants.SeedImagingStepperController_esp32_stepper_PostState_Container_P)
      }
      val api_xPos = reader.readOption(readstd_msgsInt32 _)
      val api_yPos = reader.readOption(readstd_msgsInt32 _)
      val api_zPos = reader.readOption(readstd_msgsInt32 _)
      return SeedImaging.StepperController_esp32_stepper_PostState_Container_P(api_xPos, api_yPos, api_zPos)
    }

    def readSeedImagingStepperController_esp32_stepper_PostState_Container_PS(): SeedImaging.StepperController_esp32_stepper_PostState_Container_PS = {
      val r = readSeedImagingStepperController_esp32_stepper_PostState_Container_PST(F)
      return r
    }

    def readSeedImagingStepperController_esp32_stepper_PostState_Container_PST(typeParsed: B): SeedImaging.StepperController_esp32_stepper_PostState_Container_PS = {
      if (!typeParsed) {
        reader.expectZ(Constants.SeedImagingStepperController_esp32_stepper_PostState_Container_PS)
      }
      val xPosition = reader.readS32()
      val yPosition = reader.readS32()
      val zPosition = reader.readS32()
      val api_xPos = reader.readOption(readstd_msgsInt32 _)
      val api_yPos = reader.readOption(readstd_msgsInt32 _)
      val api_zPos = reader.readOption(readstd_msgsInt32 _)
      return SeedImaging.StepperController_esp32_stepper_PostState_Container_PS(xPosition, yPosition, zPosition, api_xPos, api_yPos, api_zPos)
    }

    def readutilContainer(): util.Container = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants.utilEmptyContainer => val r = readutilEmptyContainerT(T); return r
        case Constants.SeedImagingScanController_jetson_scan_PreState_Container_P => val r = readSeedImagingScanController_jetson_scan_PreState_Container_PT(T); return r
        case Constants.SeedImagingScanController_jetson_scan_PreState_Container_PS => val r = readSeedImagingScanController_jetson_scan_PreState_Container_PST(T); return r
        case Constants.SeedImagingScanController_jetson_scan_PostState_Container_P => val r = readSeedImagingScanController_jetson_scan_PostState_Container_PT(T); return r
        case Constants.SeedImagingScanController_jetson_scan_PostState_Container_PS => val r = readSeedImagingScanController_jetson_scan_PostState_Container_PST(T); return r
        case Constants.SeedImagingStepperController_esp32_stepper_PreState_Container_P => val r = readSeedImagingStepperController_esp32_stepper_PreState_Container_PT(T); return r
        case Constants.SeedImagingStepperController_esp32_stepper_PreState_Container_PS => val r = readSeedImagingStepperController_esp32_stepper_PreState_Container_PST(T); return r
        case Constants.SeedImagingStepperController_esp32_stepper_PostState_Container_P => val r = readSeedImagingStepperController_esp32_stepper_PostState_Container_PT(T); return r
        case Constants.SeedImagingStepperController_esp32_stepper_PostState_Container_PS => val r = readSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of util.Container.")
          val r = readSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T)
          return r
      }
    }

    def readutilEmptyContainer(): util.EmptyContainer = {
      val r = readutilEmptyContainerT(F)
      return r
    }

    def readutilEmptyContainerT(typeParsed: B): util.EmptyContainer = {
      if (!typeParsed) {
        reader.expectZ(Constants.utilEmptyContainer)
      }
      return util.EmptyContainer()
    }

    def read_artDataContent(): art.DataContent = {
      val i = reader.curr
      val t = reader.readZ()
      t match {
        case Constants._artEmpty => val r = read_artEmptyT(T); return r
        case Constants.Base_TypesBoolean_Payload => val r = readBase_TypesBoolean_PayloadT(T); return r
        case Constants.Base_TypesInteger_Payload => val r = readBase_TypesInteger_PayloadT(T); return r
        case Constants.Base_TypesInteger_8_Payload => val r = readBase_TypesInteger_8_PayloadT(T); return r
        case Constants.Base_TypesInteger_16_Payload => val r = readBase_TypesInteger_16_PayloadT(T); return r
        case Constants.Base_TypesInteger_32_Payload => val r = readBase_TypesInteger_32_PayloadT(T); return r
        case Constants.Base_TypesInteger_64_Payload => val r = readBase_TypesInteger_64_PayloadT(T); return r
        case Constants.Base_TypesUnsigned_8_Payload => val r = readBase_TypesUnsigned_8_PayloadT(T); return r
        case Constants.Base_TypesUnsigned_16_Payload => val r = readBase_TypesUnsigned_16_PayloadT(T); return r
        case Constants.Base_TypesUnsigned_32_Payload => val r = readBase_TypesUnsigned_32_PayloadT(T); return r
        case Constants.Base_TypesUnsigned_64_Payload => val r = readBase_TypesUnsigned_64_PayloadT(T); return r
        case Constants.Base_TypesFloat_Payload => val r = readBase_TypesFloat_PayloadT(T); return r
        case Constants.Base_TypesFloat_32_Payload => val r = readBase_TypesFloat_32_PayloadT(T); return r
        case Constants.Base_TypesFloat_64_Payload => val r = readBase_TypesFloat_64_PayloadT(T); return r
        case Constants.Base_TypesCharacter_Payload => val r = readBase_TypesCharacter_PayloadT(T); return r
        case Constants.Base_TypesString_Payload => val r = readBase_TypesString_PayloadT(T); return r
        case Constants.Base_TypesBits_Payload => val r = readBase_TypesBits_PayloadT(T); return r
        case Constants.utilEmptyContainer => val r = readutilEmptyContainerT(T); return r
        case Constants.std_msgsInt32_Payload => val r = readstd_msgsInt32_PayloadT(T); return r
        case Constants.SeedImagingScanController_jetson_scan_PreState_Container_P => val r = readSeedImagingScanController_jetson_scan_PreState_Container_PT(T); return r
        case Constants.SeedImagingScanController_jetson_scan_PreState_Container_PS => val r = readSeedImagingScanController_jetson_scan_PreState_Container_PST(T); return r
        case Constants.SeedImagingScanController_jetson_scan_PostState_Container_P => val r = readSeedImagingScanController_jetson_scan_PostState_Container_PT(T); return r
        case Constants.SeedImagingScanController_jetson_scan_PostState_Container_PS => val r = readSeedImagingScanController_jetson_scan_PostState_Container_PST(T); return r
        case Constants.SeedImagingStepperController_esp32_stepper_PreState_Container_P => val r = readSeedImagingStepperController_esp32_stepper_PreState_Container_PT(T); return r
        case Constants.SeedImagingStepperController_esp32_stepper_PreState_Container_PS => val r = readSeedImagingStepperController_esp32_stepper_PreState_Container_PST(T); return r
        case Constants.SeedImagingStepperController_esp32_stepper_PostState_Container_P => val r = readSeedImagingStepperController_esp32_stepper_PostState_Container_PT(T); return r
        case Constants.SeedImagingStepperController_esp32_stepper_PostState_Container_PS => val r = readSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T); return r
        case _ =>
          reader.error(i, s"$t is not a valid type of art.DataContent.")
          val r = readSeedImagingStepperController_esp32_stepper_PostState_Container_PST(T)
          return r
      }
    }

    def read_artEmpty(): art.Empty = {
      val r = read_artEmptyT(F)
      return r
    }

    def read_artEmptyT(typeParsed: B): art.Empty = {
      if (!typeParsed) {
        reader.expectZ(Constants._artEmpty)
      }
      return art.Empty()
    }

  }

  def to[T](data: ISZ[U8], f: Reader => T): Either[T, MessagePack.ErrorMsg] = {
    val rd = Reader.Default(MessagePack.reader(data))
    rd.reader.init()
    val r = f(rd)
    rd.errorOpt match {
      case Some(e) => return Either.Right(e)
      case _ => return Either.Left(r)
    }
  }

  def fromstd_msgsInt32(o: std_msgs.Int32, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writestd_msgsInt32(o)
    return w.result
  }

  def tostd_msgsInt32(data: ISZ[U8]): Either[std_msgs.Int32, MessagePack.ErrorMsg] = {
    def fstd_msgsInt32(reader: Reader): std_msgs.Int32 = {
      val r = reader.readstd_msgsInt32()
      return r
    }
    val r = to(data, fstd_msgsInt32 _)
    return r
  }

  def fromstd_msgsInt32_Payload(o: std_msgs.Int32_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writestd_msgsInt32_Payload(o)
    return w.result
  }

  def tostd_msgsInt32_Payload(data: ISZ[U8]): Either[std_msgs.Int32_Payload, MessagePack.ErrorMsg] = {
    def fstd_msgsInt32_Payload(reader: Reader): std_msgs.Int32_Payload = {
      val r = reader.readstd_msgsInt32_Payload()
      return r
    }
    val r = to(data, fstd_msgsInt32_Payload _)
    return r
  }

  def fromBase_TypesBoolean_Payload(o: Base_Types.Boolean_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesBoolean_Payload(o)
    return w.result
  }

  def toBase_TypesBoolean_Payload(data: ISZ[U8]): Either[Base_Types.Boolean_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesBoolean_Payload(reader: Reader): Base_Types.Boolean_Payload = {
      val r = reader.readBase_TypesBoolean_Payload()
      return r
    }
    val r = to(data, fBase_TypesBoolean_Payload _)
    return r
  }

  def fromBase_TypesInteger_Payload(o: Base_Types.Integer_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesInteger_Payload(o)
    return w.result
  }

  def toBase_TypesInteger_Payload(data: ISZ[U8]): Either[Base_Types.Integer_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesInteger_Payload(reader: Reader): Base_Types.Integer_Payload = {
      val r = reader.readBase_TypesInteger_Payload()
      return r
    }
    val r = to(data, fBase_TypesInteger_Payload _)
    return r
  }

  def fromBase_TypesInteger_8_Payload(o: Base_Types.Integer_8_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesInteger_8_Payload(o)
    return w.result
  }

  def toBase_TypesInteger_8_Payload(data: ISZ[U8]): Either[Base_Types.Integer_8_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesInteger_8_Payload(reader: Reader): Base_Types.Integer_8_Payload = {
      val r = reader.readBase_TypesInteger_8_Payload()
      return r
    }
    val r = to(data, fBase_TypesInteger_8_Payload _)
    return r
  }

  def fromBase_TypesInteger_16_Payload(o: Base_Types.Integer_16_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesInteger_16_Payload(o)
    return w.result
  }

  def toBase_TypesInteger_16_Payload(data: ISZ[U8]): Either[Base_Types.Integer_16_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesInteger_16_Payload(reader: Reader): Base_Types.Integer_16_Payload = {
      val r = reader.readBase_TypesInteger_16_Payload()
      return r
    }
    val r = to(data, fBase_TypesInteger_16_Payload _)
    return r
  }

  def fromBase_TypesInteger_32_Payload(o: Base_Types.Integer_32_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesInteger_32_Payload(o)
    return w.result
  }

  def toBase_TypesInteger_32_Payload(data: ISZ[U8]): Either[Base_Types.Integer_32_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesInteger_32_Payload(reader: Reader): Base_Types.Integer_32_Payload = {
      val r = reader.readBase_TypesInteger_32_Payload()
      return r
    }
    val r = to(data, fBase_TypesInteger_32_Payload _)
    return r
  }

  def fromBase_TypesInteger_64_Payload(o: Base_Types.Integer_64_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesInteger_64_Payload(o)
    return w.result
  }

  def toBase_TypesInteger_64_Payload(data: ISZ[U8]): Either[Base_Types.Integer_64_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesInteger_64_Payload(reader: Reader): Base_Types.Integer_64_Payload = {
      val r = reader.readBase_TypesInteger_64_Payload()
      return r
    }
    val r = to(data, fBase_TypesInteger_64_Payload _)
    return r
  }

  def fromBase_TypesUnsigned_8_Payload(o: Base_Types.Unsigned_8_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesUnsigned_8_Payload(o)
    return w.result
  }

  def toBase_TypesUnsigned_8_Payload(data: ISZ[U8]): Either[Base_Types.Unsigned_8_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesUnsigned_8_Payload(reader: Reader): Base_Types.Unsigned_8_Payload = {
      val r = reader.readBase_TypesUnsigned_8_Payload()
      return r
    }
    val r = to(data, fBase_TypesUnsigned_8_Payload _)
    return r
  }

  def fromBase_TypesUnsigned_16_Payload(o: Base_Types.Unsigned_16_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesUnsigned_16_Payload(o)
    return w.result
  }

  def toBase_TypesUnsigned_16_Payload(data: ISZ[U8]): Either[Base_Types.Unsigned_16_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesUnsigned_16_Payload(reader: Reader): Base_Types.Unsigned_16_Payload = {
      val r = reader.readBase_TypesUnsigned_16_Payload()
      return r
    }
    val r = to(data, fBase_TypesUnsigned_16_Payload _)
    return r
  }

  def fromBase_TypesUnsigned_32_Payload(o: Base_Types.Unsigned_32_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesUnsigned_32_Payload(o)
    return w.result
  }

  def toBase_TypesUnsigned_32_Payload(data: ISZ[U8]): Either[Base_Types.Unsigned_32_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesUnsigned_32_Payload(reader: Reader): Base_Types.Unsigned_32_Payload = {
      val r = reader.readBase_TypesUnsigned_32_Payload()
      return r
    }
    val r = to(data, fBase_TypesUnsigned_32_Payload _)
    return r
  }

  def fromBase_TypesUnsigned_64_Payload(o: Base_Types.Unsigned_64_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesUnsigned_64_Payload(o)
    return w.result
  }

  def toBase_TypesUnsigned_64_Payload(data: ISZ[U8]): Either[Base_Types.Unsigned_64_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesUnsigned_64_Payload(reader: Reader): Base_Types.Unsigned_64_Payload = {
      val r = reader.readBase_TypesUnsigned_64_Payload()
      return r
    }
    val r = to(data, fBase_TypesUnsigned_64_Payload _)
    return r
  }

  def fromBase_TypesFloat_Payload(o: Base_Types.Float_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesFloat_Payload(o)
    return w.result
  }

  def toBase_TypesFloat_Payload(data: ISZ[U8]): Either[Base_Types.Float_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesFloat_Payload(reader: Reader): Base_Types.Float_Payload = {
      val r = reader.readBase_TypesFloat_Payload()
      return r
    }
    val r = to(data, fBase_TypesFloat_Payload _)
    return r
  }

  def fromBase_TypesFloat_32_Payload(o: Base_Types.Float_32_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesFloat_32_Payload(o)
    return w.result
  }

  def toBase_TypesFloat_32_Payload(data: ISZ[U8]): Either[Base_Types.Float_32_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesFloat_32_Payload(reader: Reader): Base_Types.Float_32_Payload = {
      val r = reader.readBase_TypesFloat_32_Payload()
      return r
    }
    val r = to(data, fBase_TypesFloat_32_Payload _)
    return r
  }

  def fromBase_TypesFloat_64_Payload(o: Base_Types.Float_64_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesFloat_64_Payload(o)
    return w.result
  }

  def toBase_TypesFloat_64_Payload(data: ISZ[U8]): Either[Base_Types.Float_64_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesFloat_64_Payload(reader: Reader): Base_Types.Float_64_Payload = {
      val r = reader.readBase_TypesFloat_64_Payload()
      return r
    }
    val r = to(data, fBase_TypesFloat_64_Payload _)
    return r
  }

  def fromBase_TypesCharacter_Payload(o: Base_Types.Character_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesCharacter_Payload(o)
    return w.result
  }

  def toBase_TypesCharacter_Payload(data: ISZ[U8]): Either[Base_Types.Character_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesCharacter_Payload(reader: Reader): Base_Types.Character_Payload = {
      val r = reader.readBase_TypesCharacter_Payload()
      return r
    }
    val r = to(data, fBase_TypesCharacter_Payload _)
    return r
  }

  def fromBase_TypesString_Payload(o: Base_Types.String_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesString_Payload(o)
    return w.result
  }

  def toBase_TypesString_Payload(data: ISZ[U8]): Either[Base_Types.String_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesString_Payload(reader: Reader): Base_Types.String_Payload = {
      val r = reader.readBase_TypesString_Payload()
      return r
    }
    val r = to(data, fBase_TypesString_Payload _)
    return r
  }

  def fromBase_TypesBits_Payload(o: Base_Types.Bits_Payload, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeBase_TypesBits_Payload(o)
    return w.result
  }

  def toBase_TypesBits_Payload(data: ISZ[U8]): Either[Base_Types.Bits_Payload, MessagePack.ErrorMsg] = {
    def fBase_TypesBits_Payload(reader: Reader): Base_Types.Bits_Payload = {
      val r = reader.readBase_TypesBits_Payload()
      return r
    }
    val r = to(data, fBase_TypesBits_Payload _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PreState_Container(o: SeedImaging.ScanController_jetson_scan_PreState_Container, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingScanController_jetson_scan_PreState_Container(o)
    return w.result
  }

  def toSeedImagingScanController_jetson_scan_PreState_Container(data: ISZ[U8]): Either[SeedImaging.ScanController_jetson_scan_PreState_Container, MessagePack.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PreState_Container(reader: Reader): SeedImaging.ScanController_jetson_scan_PreState_Container = {
      val r = reader.readSeedImagingScanController_jetson_scan_PreState_Container()
      return r
    }
    val r = to(data, fSeedImagingScanController_jetson_scan_PreState_Container _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PreState_Container_P(o: SeedImaging.ScanController_jetson_scan_PreState_Container_P, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingScanController_jetson_scan_PreState_Container_P(o)
    return w.result
  }

  def toSeedImagingScanController_jetson_scan_PreState_Container_P(data: ISZ[U8]): Either[SeedImaging.ScanController_jetson_scan_PreState_Container_P, MessagePack.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PreState_Container_P(reader: Reader): SeedImaging.ScanController_jetson_scan_PreState_Container_P = {
      val r = reader.readSeedImagingScanController_jetson_scan_PreState_Container_P()
      return r
    }
    val r = to(data, fSeedImagingScanController_jetson_scan_PreState_Container_P _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PreState_Container_PS(o: SeedImaging.ScanController_jetson_scan_PreState_Container_PS, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingScanController_jetson_scan_PreState_Container_PS(o)
    return w.result
  }

  def toSeedImagingScanController_jetson_scan_PreState_Container_PS(data: ISZ[U8]): Either[SeedImaging.ScanController_jetson_scan_PreState_Container_PS, MessagePack.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PreState_Container_PS(reader: Reader): SeedImaging.ScanController_jetson_scan_PreState_Container_PS = {
      val r = reader.readSeedImagingScanController_jetson_scan_PreState_Container_PS()
      return r
    }
    val r = to(data, fSeedImagingScanController_jetson_scan_PreState_Container_PS _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PostState_Container(o: SeedImaging.ScanController_jetson_scan_PostState_Container, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingScanController_jetson_scan_PostState_Container(o)
    return w.result
  }

  def toSeedImagingScanController_jetson_scan_PostState_Container(data: ISZ[U8]): Either[SeedImaging.ScanController_jetson_scan_PostState_Container, MessagePack.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PostState_Container(reader: Reader): SeedImaging.ScanController_jetson_scan_PostState_Container = {
      val r = reader.readSeedImagingScanController_jetson_scan_PostState_Container()
      return r
    }
    val r = to(data, fSeedImagingScanController_jetson_scan_PostState_Container _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PostState_Container_P(o: SeedImaging.ScanController_jetson_scan_PostState_Container_P, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingScanController_jetson_scan_PostState_Container_P(o)
    return w.result
  }

  def toSeedImagingScanController_jetson_scan_PostState_Container_P(data: ISZ[U8]): Either[SeedImaging.ScanController_jetson_scan_PostState_Container_P, MessagePack.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PostState_Container_P(reader: Reader): SeedImaging.ScanController_jetson_scan_PostState_Container_P = {
      val r = reader.readSeedImagingScanController_jetson_scan_PostState_Container_P()
      return r
    }
    val r = to(data, fSeedImagingScanController_jetson_scan_PostState_Container_P _)
    return r
  }

  def fromSeedImagingScanController_jetson_scan_PostState_Container_PS(o: SeedImaging.ScanController_jetson_scan_PostState_Container_PS, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingScanController_jetson_scan_PostState_Container_PS(o)
    return w.result
  }

  def toSeedImagingScanController_jetson_scan_PostState_Container_PS(data: ISZ[U8]): Either[SeedImaging.ScanController_jetson_scan_PostState_Container_PS, MessagePack.ErrorMsg] = {
    def fSeedImagingScanController_jetson_scan_PostState_Container_PS(reader: Reader): SeedImaging.ScanController_jetson_scan_PostState_Container_PS = {
      val r = reader.readSeedImagingScanController_jetson_scan_PostState_Container_PS()
      return r
    }
    val r = to(data, fSeedImagingScanController_jetson_scan_PostState_Container_PS _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PreState_Container(o: SeedImaging.StepperController_esp32_stepper_PreState_Container, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingStepperController_esp32_stepper_PreState_Container(o)
    return w.result
  }

  def toSeedImagingStepperController_esp32_stepper_PreState_Container(data: ISZ[U8]): Either[SeedImaging.StepperController_esp32_stepper_PreState_Container, MessagePack.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PreState_Container(reader: Reader): SeedImaging.StepperController_esp32_stepper_PreState_Container = {
      val r = reader.readSeedImagingStepperController_esp32_stepper_PreState_Container()
      return r
    }
    val r = to(data, fSeedImagingStepperController_esp32_stepper_PreState_Container _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PreState_Container_P(o: SeedImaging.StepperController_esp32_stepper_PreState_Container_P, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingStepperController_esp32_stepper_PreState_Container_P(o)
    return w.result
  }

  def toSeedImagingStepperController_esp32_stepper_PreState_Container_P(data: ISZ[U8]): Either[SeedImaging.StepperController_esp32_stepper_PreState_Container_P, MessagePack.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PreState_Container_P(reader: Reader): SeedImaging.StepperController_esp32_stepper_PreState_Container_P = {
      val r = reader.readSeedImagingStepperController_esp32_stepper_PreState_Container_P()
      return r
    }
    val r = to(data, fSeedImagingStepperController_esp32_stepper_PreState_Container_P _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o: SeedImaging.StepperController_esp32_stepper_PreState_Container_PS, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingStepperController_esp32_stepper_PreState_Container_PS(o)
    return w.result
  }

  def toSeedImagingStepperController_esp32_stepper_PreState_Container_PS(data: ISZ[U8]): Either[SeedImaging.StepperController_esp32_stepper_PreState_Container_PS, MessagePack.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PreState_Container_PS(reader: Reader): SeedImaging.StepperController_esp32_stepper_PreState_Container_PS = {
      val r = reader.readSeedImagingStepperController_esp32_stepper_PreState_Container_PS()
      return r
    }
    val r = to(data, fSeedImagingStepperController_esp32_stepper_PreState_Container_PS _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PostState_Container(o: SeedImaging.StepperController_esp32_stepper_PostState_Container, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingStepperController_esp32_stepper_PostState_Container(o)
    return w.result
  }

  def toSeedImagingStepperController_esp32_stepper_PostState_Container(data: ISZ[U8]): Either[SeedImaging.StepperController_esp32_stepper_PostState_Container, MessagePack.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PostState_Container(reader: Reader): SeedImaging.StepperController_esp32_stepper_PostState_Container = {
      val r = reader.readSeedImagingStepperController_esp32_stepper_PostState_Container()
      return r
    }
    val r = to(data, fSeedImagingStepperController_esp32_stepper_PostState_Container _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PostState_Container_P(o: SeedImaging.StepperController_esp32_stepper_PostState_Container_P, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingStepperController_esp32_stepper_PostState_Container_P(o)
    return w.result
  }

  def toSeedImagingStepperController_esp32_stepper_PostState_Container_P(data: ISZ[U8]): Either[SeedImaging.StepperController_esp32_stepper_PostState_Container_P, MessagePack.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PostState_Container_P(reader: Reader): SeedImaging.StepperController_esp32_stepper_PostState_Container_P = {
      val r = reader.readSeedImagingStepperController_esp32_stepper_PostState_Container_P()
      return r
    }
    val r = to(data, fSeedImagingStepperController_esp32_stepper_PostState_Container_P _)
    return r
  }

  def fromSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o: SeedImaging.StepperController_esp32_stepper_PostState_Container_PS, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeSeedImagingStepperController_esp32_stepper_PostState_Container_PS(o)
    return w.result
  }

  def toSeedImagingStepperController_esp32_stepper_PostState_Container_PS(data: ISZ[U8]): Either[SeedImaging.StepperController_esp32_stepper_PostState_Container_PS, MessagePack.ErrorMsg] = {
    def fSeedImagingStepperController_esp32_stepper_PostState_Container_PS(reader: Reader): SeedImaging.StepperController_esp32_stepper_PostState_Container_PS = {
      val r = reader.readSeedImagingStepperController_esp32_stepper_PostState_Container_PS()
      return r
    }
    val r = to(data, fSeedImagingStepperController_esp32_stepper_PostState_Container_PS _)
    return r
  }

  def fromutilContainer(o: util.Container, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeutilContainer(o)
    return w.result
  }

  def toutilContainer(data: ISZ[U8]): Either[util.Container, MessagePack.ErrorMsg] = {
    def futilContainer(reader: Reader): util.Container = {
      val r = reader.readutilContainer()
      return r
    }
    val r = to(data, futilContainer _)
    return r
  }

  def fromutilEmptyContainer(o: util.EmptyContainer, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.writeutilEmptyContainer(o)
    return w.result
  }

  def toutilEmptyContainer(data: ISZ[U8]): Either[util.EmptyContainer, MessagePack.ErrorMsg] = {
    def futilEmptyContainer(reader: Reader): util.EmptyContainer = {
      val r = reader.readutilEmptyContainer()
      return r
    }
    val r = to(data, futilEmptyContainer _)
    return r
  }

  def from_artDataContent(o: art.DataContent, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_artDataContent(o)
    return w.result
  }

  def to_artDataContent(data: ISZ[U8]): Either[art.DataContent, MessagePack.ErrorMsg] = {
    def f_artDataContent(reader: Reader): art.DataContent = {
      val r = reader.read_artDataContent()
      return r
    }
    val r = to(data, f_artDataContent _)
    return r
  }

  def from_artEmpty(o: art.Empty, pooling: B): ISZ[U8] = {
    val w = Writer.Default(MessagePack.writer(pooling))
    w.write_artEmpty(o)
    return w.result
  }

  def to_artEmpty(data: ISZ[U8]): Either[art.Empty, MessagePack.ErrorMsg] = {
    def f_artEmpty(reader: Reader): art.Empty = {
      val r = reader.read_artEmpty()
      return r
    }
    val r = to(data, f_artEmpty _)
    return r
  }

}