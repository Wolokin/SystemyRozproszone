package sr.ice.server;

import com.zeroc.Ice.Current;
import com.zeroc.Ice.Object;
import com.zeroc.Ice.ServantLocator;
import com.zeroc.Ice.UserException;
import sr.ice.server.servants.bulbulator.BulbulatorI;
import sr.ice.server.servants.door.DoorI;
import sr.ice.server.servants.fridges.FridgeI;
import sr.ice.server.servants.light_controllers.LightControllerI;

import java.util.HashMap;
import java.util.Map;

public class ServerServantLocator1 implements ServantLocator {
    private final Map<String, Object> servants = new HashMap<>();

    @Override
    public LocateResult locate(Current current) throws UserException {
        String name = current.id.name;

        // Shared servant
        if (name.startsWith("Door")) {
            if (!servants.containsKey("Door")) {
                DoorI servant = new DoorI();
                System.out.println("Creating new servant "+servant+" for " + name);
                servants.put("Door", servant);
                return new LocateResult(servant, null);
            } else {
                System.out.println("Accessing existing servant " + servants.get("Door") + " for " + name);
                return new LocateResult(servants.get("Door"), null);
            }
        }

        // Exclusive servant
        if (servants.containsKey(name)) {
            System.out.println("Accessing existing servant " + servants.get(name) + " for " + name);
            return new LocateResult(servants.get(name), null);
        }

        Object servant;
        switch (name) {
            case "Bulbulator1" -> servant = new BulbulatorI();
            case "Fridge" -> servant = new FridgeI();
            case "LightController" -> servant = new LightControllerI();
            default -> {
                return new LocateResult();
            }
        }
        System.out.println("Creating new servant "+servant+" for " + name);

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
