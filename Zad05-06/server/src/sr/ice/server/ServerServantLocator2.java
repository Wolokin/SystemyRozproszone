package sr.ice.server;

import com.zeroc.Ice.Current;
import com.zeroc.Ice.Object;
import com.zeroc.Ice.ServantLocator;
import com.zeroc.Ice.UserException;
import sr.ice.server.servants.bulbulator.BulbulatorI;
import sr.ice.server.servants.fridges.AutomaticFridgeI;
import sr.ice.server.servants.light_controllers.PremiumLightControllerI;

import java.util.HashMap;
import java.util.Map;

public class ServerServantLocator2 implements ServantLocator {
    private final Map<String, Object> servants = new HashMap<>();

    @Override
    public LocateResult locate(Current current) throws UserException {
        String name = current.id.name;
        if (servants.containsKey(name)) {
            System.out.println("Accessing existing servant " + servants.get(name) + " for " + name);
            return new LocateResult(servants.get(name), null);
        }

        System.out.println("Creating new servant for " + name);
        Object servant;
        switch (name) {
            case "Bulbulator2" -> servant = new BulbulatorI();
            case "AutomaticFridge" -> servant = new AutomaticFridgeI();
            case "PremiumLightController" -> servant = new PremiumLightControllerI();
            default -> {
                return new LocateResult();
            }
        }

        servants.put(name, servant);
        return new LocateResult(servant, null);
    }

    @Override
    public void finished(Current current, Object object, java.lang.Object o) throws UserException {

    }

    @Override
    public void deactivate(String s) {

    }
}
