// #Sireum

package seedimaging

import org.sireum._
import org.sireum.Random.Gen64

/*
GENERATED FROM

Int32.scala

Base_Types.scala

GUMBO__Library.scala

ScanController_jetson_scan_Containers.scala

StepperController_esp32_stepper_Containers.scala

Container.scala

DataContent.scala

Aux_Types.scala

*/

@enum object _artDataContent_DataTypeId {
   "_artEmpty_Id"
   "Base_TypesBits_Payload_Id"
   "Base_TypesBoolean_Payload_Id"
   "Base_TypesCharacter_Payload_Id"
   "Base_TypesFloat_32_Payload_Id"
   "Base_TypesFloat_64_Payload_Id"
   "Base_TypesFloat_Payload_Id"
   "Base_TypesInteger_16_Payload_Id"
   "Base_TypesInteger_32_Payload_Id"
   "Base_TypesInteger_64_Payload_Id"
   "Base_TypesInteger_8_Payload_Id"
   "Base_TypesInteger_Payload_Id"
   "Base_TypesString_Payload_Id"
   "Base_TypesUnsigned_16_Payload_Id"
   "Base_TypesUnsigned_32_Payload_Id"
   "Base_TypesUnsigned_64_Payload_Id"
   "Base_TypesUnsigned_8_Payload_Id"
   "SeedImagingScanController_jetson_scan_PostState_Container_P_Id"
   "SeedImagingScanController_jetson_scan_PostState_Container_PS_Id"
   "SeedImagingScanController_jetson_scan_PreState_Container_P_Id"
   "SeedImagingScanController_jetson_scan_PreState_Container_PS_Id"
   "SeedImagingStepperController_esp32_stepper_PostState_Container_P_Id"
   "SeedImagingStepperController_esp32_stepper_PostState_Container_PS_Id"
   "SeedImagingStepperController_esp32_stepper_PreState_Container_P_Id"
   "SeedImagingStepperController_esp32_stepper_PreState_Container_PS_Id"
   "std_msgsInt32_Payload_Id"
   "utilEmptyContainer_Id"
}

@enum object SeedImagingScanController_jetson_scan_PreState_Container_DataTypeId {
   "SeedImagingScanController_jetson_scan_PreState_Container_P_Id"
   "SeedImagingScanController_jetson_scan_PreState_Container_PS_Id"
}

@enum object SeedImagingScanController_jetson_scan_PostState_Container_DataTypeId {
   "SeedImagingScanController_jetson_scan_PostState_Container_P_Id"
   "SeedImagingScanController_jetson_scan_PostState_Container_PS_Id"
}

@enum object SeedImagingStepperController_esp32_stepper_PreState_Container_DataTypeId {
   "SeedImagingStepperController_esp32_stepper_PreState_Container_P_Id"
   "SeedImagingStepperController_esp32_stepper_PreState_Container_PS_Id"
}

@enum object SeedImagingStepperController_esp32_stepper_PostState_Container_DataTypeId {
   "SeedImagingStepperController_esp32_stepper_PostState_Container_P_Id"
   "SeedImagingStepperController_esp32_stepper_PostState_Container_PS_Id"
}

@enum object utilContainer_DataTypeId {
   "SeedImagingScanController_jetson_scan_PostState_Container_P_Id"
   "SeedImagingScanController_jetson_scan_PostState_Container_PS_Id"
   "SeedImagingScanController_jetson_scan_PreState_Container_P_Id"
   "SeedImagingScanController_jetson_scan_PreState_Container_PS_Id"
   "SeedImagingStepperController_esp32_stepper_PostState_Container_P_Id"
   "SeedImagingStepperController_esp32_stepper_PostState_Container_PS_Id"
   "SeedImagingStepperController_esp32_stepper_PreState_Container_P_Id"
   "SeedImagingStepperController_esp32_stepper_PreState_Container_PS_Id"
   "utilEmptyContainer_Id"
}

