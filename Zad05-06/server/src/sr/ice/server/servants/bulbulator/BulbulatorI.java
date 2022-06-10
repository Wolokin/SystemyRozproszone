package sr.ice.server.servants.bulbulator;

import SmartHome.*;
import com.zeroc.Ice.Current;
import sr.ice.server.servants.SmartDeviceI;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BulbulatorI extends SmartDeviceI implements IBulbulator {
    private final Bulbulator bulbulator;

    public BulbulatorI() {
        super(new Bulbulator(new DeviceMetadata("Bulbulator", ThreadLocalRandom.current().nextInt(SERIAL_MAX)),
                new BulbulatorData()));
        bulbulator = (Bulbulator) this.device;
    }

    @Override
    protected List<BaseReading> getDataHelper() {
        List<BaseReading> readings = super.getDataHelper();
        readings.add(bulbulator.bulbulatorData);
        return readings;
    }

    @Override
    protected boolean changeSetting(BaseSetting setting) throws InvalidSettingValueException {
        if (setting.ice_id().equals(CurrentIntensitySetting.ice_staticId())) {
            short newIntensity = ((CurrentIntensitySetting) setting).intensity;
            if (bulbulator.bulbulatorData.minIntensity > newIntensity
                    || bulbulator.bulbulatorData.maxIntensity < newIntensity) {
                throw new InvalidSettingValueException("Intensity out of range!");
            }
            System.out.println("Changing " + setting + " of " + this);
            bulbulator.bulbulatorData.currentIntensity = newIntensity;
            return true;
        }
        return super.changeSetting(setting);
    }


    @Override
    public String bulbul(boolean capital, Current current) {
        String s = capital ? "BUL" : "bul";
        return s.repeat(bulbulator.bulbulatorData.currentIntensity);
    }

}
