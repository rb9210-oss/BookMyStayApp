/**
 * Book My Stay Application - Use Case 6
 * Demonstrates booking confirmation, room allocation,
 * uniqueness using Set, and inventory synchronization.
 *
 * Key Focus:
 * - FIFO queue processing
 * - Unique room ID assignment
 * - Prevent double booking
 * - Immediate inventory update
 *
 * @author YourName
 * @version 1.0
 */

import java.util.*;

// Abstract Room
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
}

// Room Types
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 2000.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 3500.0);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 6000.0);
    }
}

// Inventory Service
class RoomInventory {
    private Map<String, Integer> availabilityMap;

    public RoomInventory() {
        availabilityMap = new HashMap<>();
        availabilityMap.put("Single Room", 2);
        availabilityMap.put("Double Room", 1);
        availabilityMap.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return availabilityMap.getOrDefault(roomType, 0);
    }

    public void decrement(String roomType) {
        availabilityMap.put(roomType, getAvailability(roomType) - 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> e : availabilityMap.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

// Reservation (Request)
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// Booking Queue (FIFO)
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Booking Service (Core Logic)
class BookingService {

    private RoomInventory inventory;

    // Track allocated room IDs (global uniqueness)
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Map room type → allocated IDs
    private Map<String, Set<String>> allocationMap = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    // Process queue
    public void processBookings(BookingQueue queue) {

        System.out.println("\n====== Processing Bookings ======");

        while (!queue.isEmpty()) {
            Reservation req = queue.getNextRequest();
            String type = req.getRoomType();

            System.out.println("\nProcessing request for " + req.getGuestName());

            // Check availability
            if (inventory.getAvailability(type) > 0) {

                // Generate unique room ID
                String roomId = generateRoomId(type);

                // Ensure uniqueness
                if (!allocatedRoomIds.contains(roomId)) {

                    // Record allocation
                    allocatedRoomIds.add(roomId);

                    allocationMap.putIfAbsent(type, new HashSet<>());
                    allocationMap.get(type).add(roomId);

                    // Update inventory (atomic step)
                    inventory.decrement(type);

                    // Confirm booking
                    System.out.println("Booking Confirmed!");
                    System.out.println("Guest: " + req.getGuestName());
                    System.out.println("Room Type: " + type);
                    System.out.println("Allocated Room ID: " + roomId);

                } else {
                    System.out.println("Error: Duplicate room ID detected!");
                }

            } else {
                System.out.println("Booking Failed for " + req.getGuestName()
                        + " → No rooms available for " + type);
            }
        }

        System.out.println("\n================================");
    }

    // Simple unique ID generator
    private String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);
    }

    // Display allocations
    public void displayAllocations() {
        System.out.println("\n====== Allocated Rooms ======");
        for (Map.Entry<String, Set<String>> entry : allocationMap.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
        System.out.println("=============================");
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        // Inventory
        RoomInventory inventory = new RoomInventory();

        // Queue
        BookingQueue queue = new BookingQueue();

        // Add requests
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Single Room"));
        queue.addRequest(new Reservation("Charlie", "Single Room")); // will fail
        queue.addRequest(new Reservation("David", "Suite Room"));

        // Booking service
        BookingService service = new BookingService(inventory);

        // Process bookings
        service.processBookings(queue);

        // Show final state
        inventory.displayInventory();
        service.displayAllocations();
    }
}