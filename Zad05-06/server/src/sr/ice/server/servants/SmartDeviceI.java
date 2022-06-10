package sr.ice.server.servants;

import SmartHome.*;
import com.zeroc.Ice.Current;

import java.util.ArrayList;
import java.util.List;

public class SmartDeviceI implements ISmartDevice {
    public static final int SERIAL_MAX = 1000000;
    protected final SmartDevice device;

    public SmartDeviceI(SmartDevice device) {
        this.device = device;
        System.out.println("Creating new device: "+this.device.deviceData.deviceName);
    }

    protected List<BaseReading> getDataHelper() {
        ArrayList<BaseReading> list = new ArrayList<>();
        list.add(device.deviceData);
        return list;
    }

    protected boolean changeSetting(BaseSetting setting) throws InvalidSettingValueException {
        if (setting.ice_id().equals(NameSetting.ice_staticId())) {
            System.out.println("Changing " + setting + " of " + this);
            this.device.deviceData.deviceName = ((NameSetting) setting).newName;
            return true;
        }
        return false;
    }

    @Override
    public BaseReading[] getData(Current current) {
        return getDataHelper().toArray(new BaseReading[0]);
    }

    @Override
    public void changeSettings(BaseSetting[] settings, Current current) throws InvalidSettingValueException,
            UnsupportedSettingException {
        for (BaseSetting setting : settings) {
            if (!changeSetting(setting)) {
                throw new UnsupportedSettingException("Setting of type " + setting.ice_id() + " is not supported by " + this.ice_id(current));
            }
        }
    }
}
