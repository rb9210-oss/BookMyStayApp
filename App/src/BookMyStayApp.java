/**
 * Book My Stay Application - Use Case 4
 * Demonstrates read-only search functionality with proper separation of concerns.
 *
 * Key Focus:
 * - Read-only access (no state modification)
 * - Filtering available rooms
 * - Separation of search and inventory logic
 *
 * @author YourName
 * @version 1.0
 */

import java.util.*;

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

// Room Types
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 2000.0);
    }

    public void displayDetails() {
        System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 3500.0);
    }

    public void displayDetails() {
        System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 6000.0);
    }

    public void displayDetails() {
        System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

// Centralized Inventory (Same as Use Case 3)
class RoomInventory {

    private Map<String, Integer> availabilityMap;

    public RoomInventory() {
        availabilityMap = new HashMap<>();
        availabilityMap.put("Single Room", 5);
        availabilityMap.put("Double Room", 3);
        availabilityMap.put("Suite Room", 0); // Example: no suites available
    }

    // Read-only access
    public int getAvailability(String roomType) {
        return availabilityMap.getOrDefault(roomType, 0);
    }

    // No update method used in search use case
}

// Search Service (Read-Only Logic)
class SearchService {

    private RoomInventory inventory;
    private List<Room> rooms;

    public SearchService(RoomInventory inventory, List<Room> rooms) {
        this.inventory = inventory;
        this.rooms = rooms;
    }

    // Search available rooms (read-only)
    public void searchAvailableRooms() {

        System.out.println("\n====== Available Rooms ======");

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.getRoomType());

            // Filter: only show available rooms
            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available);
                System.out.println("----------------------------");
            }
        }

        System.out.println("============================");
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        // Room domain objects
        List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        // Centralized inventory
        RoomInventory inventory = new RoomInventory();

        // Search service (read-only)
        SearchService searchService = new SearchService(inventory, rooms);

        // Guest performs search
        searchService.searchAvailableRooms();
    }
}