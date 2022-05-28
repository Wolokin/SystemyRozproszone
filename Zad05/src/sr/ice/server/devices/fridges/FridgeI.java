package sr.ice.server.devices.fridges;

import SmartHome.*;
import com.zeroc.Ice.Current;
import sr.ice.server.devices.SmartDeviceI;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FridgeI extends SmartDeviceI implements IFridge {
    private final Fridge fridge;

    public FridgeI() {
        super(new Fridge(new DeviceMetadata("Fridge", ThreadLocalRandom.current().nextInt(SERIAL_MAX)), new FridgeData()));
        fridge = (Fridge) this.device;
    }

    protected FridgeI(Fridge fridge) {
        super(fridge);
        this.fridge = fridge;
    }

    @Override
    public String performSelfCheck(Current current) {
        return "Everything seems to be working...";
    }

    @Override
    protected List<BaseReading> getDataHelper() {
        List<BaseReading> readings =  super.getDataHelper();
        readings.add(fridge.fridgeData);
        return readings;
    }

    @Override
    protected boolean changeSetting(BaseSetting setting) throws InvalidSettingValueException {
        if(setting.ice_id().equals(TargetTemperatureSetting.ice_staticId())) {
            short newTargetTemperature = ((TargetTemperatureSetting) setting).newTargetTemperature;
            if(fridge.fridgeData.minTargetTemperature > newTargetTemperature
                    || fridge.fridgeData.maxTargetTemperature < newTargetTemperature) {
                throw new InvalidSettingValueException("Temperature out of range!");
            }
            fridge.fridgeData.targetTemperature = newTargetTemperature;
            return true;
        }
        return super.changeSetting(setting);
    }
}
