package sr.ice.server.devices.light_controllers;

import SmartHome.*;
import com.zeroc.Ice.Current;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PremiumLightControllerI extends LightControllerI implements IPremiumLightController {
    private final PremiumLightController premiumLightController;

    public PremiumLightControllerI() {
        super(new PremiumLightController(new DeviceMetadata("PremiumLightController", ThreadLocalRandom.current().nextInt(SERIAL_MAX)), new LightControllerData(), new PremiumLightControllerData(Map.of(0, true, 1, true, 2, false, 3, false))));
        premiumLightController = (PremiumLightController) this.device;
    }

    @Override
    public String flash(Current current) {
        return "A very premium flash, fills you with determination";
    }

    @Override
    public String colorfulFlash(int color, Current current) {
        return "Flashing with color "+color;
    }

    @Override
    protected List<BaseReading> getDataHelper() {
        List<BaseReading> readings =  super.getDataHelper();
        readings.add(premiumLightController.premiumLightControllerData);
        return readings;
    }

    @Override
    protected boolean changeSetting(BaseSetting setting) throws InvalidSettingValueException {
        if(setting.ice_id().equals(LampStatesSetting.ice_staticId())) {
            Map<Integer, Boolean> newStates = ((LampStatesSetting) setting).newStates;
            for(Map.Entry<Integer, Boolean> entry : newStates.entrySet()) {
                if(!premiumLightController.premiumLightControllerData.lampStates.containsKey(entry.getKey())) {
                    throw new InvalidSettingValueException("There is no lamp with id "+entry.getKey());
                }
                premiumLightController.premiumLightControllerData.lampStates.put(entry.getKey(),entry.getValue());
            }
            return true;
        }
        return super.changeSetting(setting);
    }
}
