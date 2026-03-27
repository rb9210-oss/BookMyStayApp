/**
 * Book My Stay Application - Use Case 8
 * Demonstrates booking history tracking and reporting.
 *
 * Key Focus:
 * - Historical tracking using List
 * - Ordered storage (chronological)
 * - Reporting without modifying data
 *
 * @author YourName
 * @version 1.0
 */

import java.util.*;

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

    @Override
    public String toString() {
        return "ReservationID: " + reservationId +
                " | Guest: " + guestName +
                " | Room: " + roomType;
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

// ---------- Booking History ----------
class BookingHistory {

    // List preserves insertion order
    private List<Reservation> history = new ArrayList<>();

    // Add confirmed booking
    public void addReservation(Reservation r) {
        history.add(r);
    }

    // Retrieve all bookings
    public List<Reservation> getAllReservations() {
        return history;
    }
}

// ---------- Booking Service ----------
class BookingService {

    private RoomInventory inventory;
    private BookingHistory history;

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void processBookings(BookingQueue queue) {

        System.out.println("\n====== Processing Bookings ======");

        while (!queue.isEmpty()) {
            Reservation r = queue.getNext();
            String type = r.getRoomType();

            if (inventory.getAvailability(type) > 0) {

                inventory.decrement(type);

                // Store in history after confirmation
                history.addReservation(r);

                System.out.println("Booking Confirmed → " + r);

            } else {
                System.out.println("Booking Failed → " + r.getGuestName());
            }
        }
    }
}

// ---------- Reporting Service ----------
class BookingReportService {

    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    // Display all bookings
    public void displayAllBookings() {
        System.out.println("\n====== Booking History ======");

        for (Reservation r : history.getAllReservations()) {
            System.out.println(r);
        }

        System.out.println("=============================");
    }

    // Summary report
    public void generateSummaryReport() {

        System.out.println("\n====== Booking Summary ======");

        Map<String, Integer> countMap = new HashMap<>();

        for (Reservation r : history.getAllReservations()) {
            countMap.put(r.getRoomType(),
                    countMap.getOrDefault(r.getRoomType(), 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue() + " bookings");
        }

        System.out.println("=============================");
    }
}

// ---------- Main Application ----------
public class BookMyStayApp {

    public static void main(String[] args) {

        // Setup
        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        BookingQueue queue = new BookingQueue();

        // Add booking requests
        queue.addRequest(new Reservation("R1", "Alice", "Single Room"));
        queue.addRequest(new Reservation("R2", "Bob", "Double Room"));
        queue.addRequest(new Reservation("R3", "Charlie", "Single Room"));

        // Process bookings
        BookingService bookingService = new BookingService(inventory, history);
        bookingService.processBookings(queue);

        // Reporting
        BookingReportService reportService = new BookingReportService(history);

        reportService.displayAllBookings();
        reportService.generateSummaryReport();
    }
}