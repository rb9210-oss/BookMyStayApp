/**
 * Book My Stay Application - Use Case 3
 * Demonstrates centralized inventory using HashMap.
 *
 * Key Focus:
 * - Single source of truth
 * - Encapsulation of inventory logic
 * - Scalable design
 *
 * @author YourName
 * @version 1.0
 */

import java.util.HashMap;
import java.util.Map;

// Abstract Room class
abstract class Room {
    protected String roomType;
    protected int beds;
    protected double price;

    public Room(String roomType, int beds, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public abstract void displayDetails();
}

// Single Room
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 2000.0);
    }

    @Override
    public void displayDetails() {
        System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

// Double Room
class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 3500.0);
    }

    @Override
    public void displayDetails() {
        System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

// Suite Room
class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 6000.0);
    }

    @Override
    public void displayDetails() {
        System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

// Inventory Class (Centralized State Management)
class RoomInventory {

    private Map<String, Integer> availabilityMap;

    // Constructor initializes inventory
    public RoomInventory() {
        availabilityMap = new HashMap<>();

        // Initial room availability
        availabilityMap.put("Single Room", 5);
        availabilityMap.put("Double Room", 3);
        availabilityMap.put("Suite Room", 2);
    }

    // Get availability
    public int getAvailability(String roomType) {
        return availabilityMap.getOrDefault(roomType, 0);
    }

    // Update availability (controlled)
    public void updateAvailability(String roomType, int count) {
        if (availabilityMap.containsKey(roomType)) {
            availabilityMap.put(roomType, count);
        } else {
            System.out.println("Room type not found!");
        }
    }

    // Display all inventory
    public void displayInventory() {
        System.out.println("\n====== Current Room Inventory ======");
        for (Map.Entry<String, Integer> entry : availabilityMap.entrySet()) {
            System.out.println(entry.getKey() + " → Available: " + entry.getValue());
        }
        System.out.println("====================================");
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        // Room objects (Domain)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Centralized Inventory
        RoomInventory inventory = new RoomInventory();

        System.out.println("========= Room Details =========");
        single.displayDetails();
        doubleRoom.displayDetails();
        suite.displayDetails();

        // Display Inventory
        inventory.displayInventory();

        // Example update (simulate booking)
        System.out.println("\nBooking 1 Single Room...");
        int current = inventory.getAvailability("Single Room");
        inventory.updateAvailability("Single Room", current - 1);

        // Display updated inventory
        inventory.displayInventory();
    }
}