package sr.ice.server.servants.door;

import SmartHome.Door;
import SmartHome.IDoor;
import com.zeroc.Ice.Current;

public class DoorI implements IDoor {
    @Override
    public String close(Current current) {
        // Connect to the door
        Door door = new Door(current.id.name, Math.random() < 0.5);
        // Close the door
        door.isClosed = true;

        System.out.println("Servant " + this + " closed the " + current.id.name);
        return current.id.name + " closed";
    }

    @Override
    public String open(Current current) {
        // Connect to the door
        Door door = new Door(current.id.name, Math.random() < 0.5);
        // Open the door
        door.isClosed = false;

        System.out.println("Servant " + this + " opened the " + current.id.name);
        return current.id.name + " opened";
    }

//    @Override
//    public void ice_ping(Current current) {
//        IDoor.super.ice_ping(current);
//    }
}
