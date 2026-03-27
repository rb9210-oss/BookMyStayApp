/**
 * Book My Stay Application - Use Case 7
 * Demonstrates add-on services using Map + List,
 * without modifying booking or inventory logic.
 *
 * Key Focus:
 * - One-to-many relationship (Reservation → Services)
 * - Composition over inheritance
 * - Cost aggregation
 *
 * @author YourName
 * @version 1.0
 */

import java.util.*;

// ---------- Core Room Classes ----------
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

// ---------- Inventory ----------
class RoomInventory {
    private Map<String, Integer> availabilityMap = new HashMap<>();

    public RoomInventory() {
        availabilityMap.put("Single Room", 2);
        availabilityMap.put("Double Room", 1);
        availabilityMap.put("Suite Room", 1);
    }

    public int getAvailability(String type) {
        return availabilityMap.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        availabilityMap.put(type, getAvailability(type) - 1);
    }
}

// ---------- Reservation ----------
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// ---------- Booking Queue ----------
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNext() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// ---------- Booking Service ----------
class BookingService {

    private RoomInventory inventory;
    private Set<String> allocatedRooms = new HashSet<>();
    private Map<String, String> reservationToRoomId = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void processBookings(BookingQueue queue) {
        while (!queue.isEmpty()) {
            Reservation r = queue.getNext();
            String type = r.getRoomType();

            if (inventory.getAvailability(type) > 0) {

                String roomId = generateRoomId(type);

                if (!allocatedRooms.contains(roomId)) {
                    allocatedRooms.add(roomId);
                    reservationToRoomId.put(r.getReservationId(), roomId);

                    inventory.decrement(type);

                    System.out.println("Booking Confirmed → " + r.getGuestName()
                            + " | Room ID: " + roomId);
                }

            } else {
                System.out.println("Booking Failed → " + r.getGuestName());
            }
        }
    }

    private String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-"
                + UUID.randomUUID().toString().substring(0, 5);
    }

    public Set<String> getReservationIds() {
        return reservationToRoomId.keySet();
    }
}

// ---------- Add-On Service ----------
class AddOnService {
    private String serviceName;
    private double price;

    public AddOnService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String toString() {
        return serviceName + " (₹" + price + ")";
    }
}

// ---------- Add-On Service Manager ----------
class AddOnServiceManager {

    // Reservation ID → List of Services
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add service to reservation
    public void addService(String reservationId, AddOnService service) {
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);

        System.out.println("Added Service → " + service.getServiceName()
                + " to Reservation " + reservationId);
    }

    // Calculate total cost
    public double calculateTotalCost(String reservationId) {
        List<AddOnService> services = serviceMap.getOrDefault(reservationId, new ArrayList<>());
        double total = 0;

        for (AddOnService s : services) {
            total += s.getPrice();
        }
        return total;
    }

    // Display services
    public void displayServices(String reservationId) {
        System.out.println("\nServices for Reservation " + reservationId);

        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No services selected.");
            return;
        }

        for (AddOnService s : services) {
            System.out.println("- " + s);
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotalCost(reservationId));
    }
}

// ---------- Main Application ----------
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();

        // Add booking requests
        queue.addRequest(new Reservation("R1", "Alice", "Single Room"));
        queue.addRequest(new Reservation("R2", "Bob", "Double Room"));

        // Process bookings
        BookingService bookingService = new BookingService(inventory);
        bookingService.processBookings(queue);

        // Add-On Service Manager
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Create services
        AddOnService breakfast = new AddOnService("Breakfast", 300);
        AddOnService wifi = new AddOnService("WiFi", 100);
        AddOnService pickup = new AddOnService("Airport Pickup", 800);

        // Attach services to reservation
        serviceManager.addService("R1", breakfast);
        serviceManager.addService("R1", wifi);
        serviceManager.addService("R2", pickup);

        // Display services
        serviceManager.displayServices("R1");
        serviceManager.displayServices("R2");
    }
}