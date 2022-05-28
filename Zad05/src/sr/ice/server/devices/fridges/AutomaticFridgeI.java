package sr.ice.server.devices.fridges;

import SmartHome.*;
import com.zeroc.Ice.Current;
import sr.ice.server.devices.SmartDeviceI;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AutomaticFridgeI extends FridgeI implements IAutomaticFridge {
    private final AutomaticFridge automaticFridge;

    public AutomaticFridgeI() {
        super(new AutomaticFridge(new DeviceMetadata("AutomaticFridge", ThreadLocalRandom.current().nextInt(SERIAL_MAX)), new FridgeData(), new AutomaticFridgeData(new String[]{"Milk", "Meat", "Eggs"})));
        automaticFridge = (AutomaticFridge) this.device;
    }

    @Override
    public String[] findMissingFood(String[] requiredFood, Current current) {
        Set<String> presentFood = new HashSet<>(Arrays.asList(automaticFridge.automaticFridgeData.presentFood));
        List<String> missingFood = new ArrayList<>();
        for(String food : requiredFood) {
            if(!presentFood.contains(food)) {
                missingFood.add(food);
            }
        }
        return missingFood.toArray(new String[0]);
    }

    @Override
    public String performSelfCheck(Current current) {
        return "Self check completed successfully, no issues detected. However, your other fridge seems to have a malfunctioning fridge light";
    }


    @Override
    protected List<BaseReading> getDataHelper() {
        List<BaseReading> readings =  super.getDataHelper();
        readings.add(automaticFridge.automaticFridgeData);
        return readings;
    }

    @Override
    protected boolean changeSetting(BaseSetting setting) throws InvalidSettingValueException {
        return super.changeSetting(setting);
    }
}
