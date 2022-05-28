
#ifndef SMART_ICE
#define SMART_ICE

module SmartHome
{
  class BaseSetting {};
  class BaseReading {};

  sequence<BaseSetting> Settings;
  sequence<BaseReading> Readings;

//  class DeviceData {
//    Readings readings;
//    Settings settings;
//  };

  exception UnsupportedSettingException { string reason; };
  exception InvalidSettingValueException { string reason; };

  class NameSetting extends BaseSetting {
    string newName;
  };
  class DeviceMetadata extends BaseReading {
    string deviceName;
    int serialNo;
  };
  interface ISmartDevice {
    idempotent Readings getData();
    idempotent void changeSettings(Settings settings) throws UnsupportedSettingException, InvalidSettingValueException;
  };
  class SmartDevice {
    DeviceMetadata deviceData;
  };


  class BulbulatorData extends BaseReading {
    short minIntensity = 1;
    short maxIntensity = 10;
    short currentIntensity = 3;
  };
  class BulbingIntensitySetting extends BaseSetting {
    short intensity;
  };
  interface IBulbulator extends ISmartDevice {
    string bulbul(bool capital);
  };
  class Bulbulator extends SmartDevice {
    BulbulatorData bulbulatorData;
  };


  class FridgeData extends BaseReading {
    short currentTemperature = 4;
    short targetTemperature = 2;
    short minTargetTemperature = -20;
    short maxTargetTemperature = -20;
  };
  class TargetTemperatureSetting extends BaseSetting {
    short newTargetTemperature;
  };
  interface IFridge extends ISmartDevice {
    string performSelfCheck();
  };
  class Fridge extends SmartDevice {
    FridgeData fridgeData;
  };


  sequence<string> Food;
  class AutomaticFridgeData extends BaseReading {
    Food presentFood;
  };
  interface IAutomaticFridge extends IFridge {
    Food findMissingFood(Food requiredFood);
  };
  class AutomaticFridge extends Fridge {
    AutomaticFridgeData automaticFridgeData;
  };


  enum LightMode {
    off, normal, musicSync, videoSync
  };
  sequence<LightMode> SupportedModes;
  class LightModeSetting extends BaseSetting {
    LightMode newMode;
  };
  class LightControllerData extends BaseReading {
    LightMode activeMode;
    SupportedModes supportedModes;
  };
  interface ILightController extends ISmartDevice {
    string flash();
  };
  class LightController extends SmartDevice {
    LightControllerData lightControllerData;
  };


  dictionary<int, bool> LampIdToStates;
  class PremiumLightControllerData extends BaseReading {
    LampIdToStates lampStates;
  };
  class LampStatesSetting extends BaseSetting {
    LampIdToStates newStates;
  };
  interface IPremiumLightController extends ILightController {
    string colorfulFlash(int color);
  };
  class PremiumLightController extends LightController {
    PremiumLightControllerData premiumLightControllerData;
  };

};

#endif
