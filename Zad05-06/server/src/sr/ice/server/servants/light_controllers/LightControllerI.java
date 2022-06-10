package sr.ice.server.servants.light_controllers;

import SmartHome.*;
import com.zeroc.Ice.Current;
import sr.ice.server.servants.SmartDeviceI;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LightControllerI extends SmartDeviceI implements ILightController {
    private final LightController lightController;

    public LightControllerI() {
        super(new LightController(new DeviceMetadata("LightController", ThreadLocalRandom.current().nextInt(SERIAL_MAX)),
                new LightControllerData(LightMode.off, new LightMode[]{LightMode.off, LightMode.normal})));
        lightController = (LightController) this.device;
    }

    protected LightControllerI(LightController lightController) {
        super(lightController);
        this.lightController = lightController;
    }

    @Override
    public String flash(Current current) {
        return "performing unimpressive flash";
    }

    @Override
    protected List<BaseReading> getDataHelper() {
        List<BaseReading> readings = super.getDataHelper();
        readings.add(lightController.lightControllerData);
        return readings;
    }

    @Override
    protected boolean changeSetting(BaseSetting setting) throws InvalidSettingValueException {
        if (setting.ice_id().equals(LightModeSetting.ice_staticId())) {
            LightMode newMode = ((LightModeSetting) setting).newMode;
            if (Arrays.stream(lightController.lightControllerData.supportedModes).noneMatch(newMode::equals)) {
                throw new InvalidSettingValueException("Unsupported light mode!");
            }
            System.out.println("Changing " + setting + " of " + this);
            lightController.lightControllerData.activeMode = newMode;
            return true;
        }
        return super.changeSetting(setting);
    }
}
