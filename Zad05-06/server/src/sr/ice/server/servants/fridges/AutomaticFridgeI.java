package sr.ice.server.servants.fridges;

import SmartHome.*;
import com.zeroc.Ice.Current;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AutomaticFridgeI extends FridgeI implements IAutomaticFridge {
    private final AutomaticFridge automaticFridge;

    public AutomaticFridgeI() {
        super(new AutomaticFridge(new DeviceMetadata("AutomaticFridge",
                ThreadLocalRandom.current().nextInt(SERIAL_MAX)), new FridgeData(),
                new AutomaticFridgeData(new String[]{"Milk", "Ham", "Eggs"})));
        automaticFridge = (AutomaticFridge) this.device;
    }

    @Override
    public String[] findMissingFood(String[] requiredFood, Current current) {
        Set<String> presentFood =
                new HashSet<>(Arrays.stream(automaticFridge.automaticFridgeData.presentFood).map(String::toLowerCase).toList());
        System.out.println(presentFood);
        System.out.println(Arrays.toString(requiredFood));
        List<String> missingFood = new ArrayList<>();
        for (String food : requiredFood) {
            if (!presentFood.contains(food)) {
                missingFood.add(food);
            }
        }
        return missingFood.toArray(new String[0]);
    }

    @Override
    public String performSelfCheck(Current current) {
        return "Self check completed successfully, no issues detected. However, your other fridge seems to have a " +
                "malfunctioning self-check module";
    }


    @Override
    protected List<BaseReading> getDataHelper() {
        List<BaseReading> readings = super.getDataHelper();
        readings.add(automaticFridge.automaticFridgeData);
        return readings;
    }

    @Override
    protected boolean changeSetting(BaseSetting setting) throws InvalidSettingValueException {
        return super.changeSetting(setting);
    }
}
