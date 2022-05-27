
#ifndef SMART_ICE
#define SMART_ICE

module SmartHome
{
  class BaseSetting {};
  class BaseReading {};

  sequence<BaseSetting> Settings;
  sequence<BaseReading> Readings;

  class DeviceData {
    Readings readings;
    Settings settings;
  };

  exception UnsupportedSettingException { string reason; };
  exception InvalidSettingValueException { string reason; };

  class NameSetting extends BaseSetting {
    string newName;
  };
  class DeviceMetadata extends BaseReading {
    string deviceName;
    string serialNo;
  };
  interface ISmartDevice {
    idempotent DeviceData getData();
    idempotent void changeSettings(Settings settings) throws UnsupportedSettingException, InvalidSettingValueException;
  };
  class SmartDevice implements ISmartDevice{
    DeviceData deviceData;
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
  class Bulbulator extends SmartDevice implements IBulbulator {
    BulbulatorData bulbulatorData;
  };


  interface IFridge extends ISmartDevice {
    string performSelfCheck();
  };
  class FridgeData extends BaseReading {
    short currentTemperature = 4;
    short targetTemperature = 2;
  };
  class Fridge extends SmartDevice implements IFridge {
    FridgeData fridgeData;
  };
  class TargetTemperatureSetting extends BaseSetting {
    short newTargetTemperature;
  };


  sequence<string> Food;
  interface IAutomaticFridge extends IFridge {
    Food findMissingFood(Food requiredFood);
  };
  class AutomaticFridgeData extends BaseReading {
    bool isDoorClosed;
    Food presentFood;
  };
  class AutomaticFridge extends Fridge implements IAutomaticFridge {
    AutomaticFridgeData automaticFridgeData;
  };


  enum LightMode {
    off, normal, musicSync, videoSync
  };
  sequence<LightMode> SupportedModes;
  interface ILightController extends ISmartDevice {
    string flash();
  };
  class LightControllerData extends BaseReading {
    LightMode activeMode;
    SupportedModes supportedModes;
  };
  class LightController extends SmartDevice implements ILightController {
    LightControllerData lightControllerData;
  };


  interface IPremiumLightController extends ILightController {
    string colorfulFlash(int color);
  };
  class PremiumLightController extends LightController implements IPremiumLightController {
  };

};

#endif
