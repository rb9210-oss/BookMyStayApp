/**
 * Book My Stay Application - Use Case 10
 * Demonstrates booking cancellation with rollback using Stack.
 *
 * Key Focus:
 * - State reversal (undo booking)
 * - Stack (LIFO) for rollback
 * - Inventory restoration
 * - Safe cancellation validation
 *
 * @author YourName
 * @version 1.0
 */

import java.util.*;

// ---------- Custom Exception ----------
class CancellationException extends Exception {
    public CancellationException(String message) {
        super(message);
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

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType;
    }
}

// ---------- Inventory ----------
class RoomInventory {
    private Map<String, Integer> availabilityMap = new HashMap<>();

    public RoomInventory() {
        availabilityMap.put("Single Room", 1);
        availabilityMap.put("Double Room", 1);
    }

    public int getAvailability(String type) {
        return availabilityMap.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        availabilityMap.put(type, getAvailability(type) - 1);
    }

    public void increment(String type) {
        availabilityMap.put(type, getAvailability(type) + 1);
    }

    public void displayInventory() {
        System.out.println("\nInventory:");
        for (Map.Entry<String, Integer> e : availabilityMap.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

// ---------- Booking History ----------
class BookingHistory {
    private Map<String, Reservation> confirmed = new HashMap<>();

    public void add(Reservation r) {
        confirmed.put(r.getReservationId(), r);
    }

    public Reservation get(String id) {
        return confirmed.get(id);
    }

    public void remove(String id) {
        confirmed.remove(id);
    }

    public void display() {
        System.out.println("\nConfirmed Bookings:");
        for (Reservation r : confirmed.values()) {
            System.out.println(r);
        }
    }
}

// ---------- Booking Service ----------
class BookingService {

    private RoomInventory inventory;
    private BookingHistory history;

    // Track allocations
    private Map<String, String> reservationToRoomId = new HashMap<>();

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void book(Reservation r) {

        if (inventory.getAvailability(r.getRoomType()) > 0) {

            String roomId = generateRoomId(r.getRoomType());

            reservationToRoomId.put(r.getReservationId(), roomId);
            inventory.decrement(r.getRoomType());
            history.add(r);

            System.out.println("Booked → " + r + " | Room ID: " + roomId);

        } else {
            System.out.println("Booking Failed → " + r.getGuestName());
        }
    }

    public String getRoomId(String reservationId) {
        return reservationToRoomId.get(reservationId);
    }

    public void removeReservation(String reservationId) {
        reservationToRoomId.remove(reservationId);
    }

    private String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-"
                + UUID.randomUUID().toString().substring(0, 5);
    }
}

// ---------- Cancellation Service ----------
class CancellationService {

    private RoomInventory inventory;
    private BookingHistory history;
    private BookingService bookingService;

    // Stack for rollback tracking (LIFO)
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(RoomInventory inventory,
                               BookingHistory history,
                               BookingService bookingService) {
        this.inventory = inventory;
        this.history = history;
        this.bookingService = bookingService;
    }

    public void cancel(String reservationId) {

        try {
            // Validate existence
            Reservation r = history.get(reservationId);

            if (r == null) {
                throw new CancellationException("Reservation not found!");
            }

            // Get allocated room ID
            String roomId = bookingService.getRoomId(reservationId);

            if (roomId == null) {
                throw new CancellationException("No allocated room found!");
            }

            // Push to rollback stack
            rollbackStack.push(roomId);

            // Restore inventory
            inventory.increment(r.getRoomType());

            // Remove booking
            history.remove(reservationId);
            bookingService.removeReservation(reservationId);

            System.out.println("Cancellation Successful → " + reservationId
                    + " | Released Room ID: " + roomId);

        } catch (CancellationException e) {
            System.out.println("Cancellation Failed → " + e.getMessage());
        }
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack (Recent Releases): " + rollbackStack);
    }
}

// ---------- Main Application ----------
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        BookingService bookingService = new BookingService(inventory, history);
        CancellationService cancelService =
                new CancellationService(inventory, history, bookingService);

        // Bookings
        bookingService.book(new Reservation("R1", "Alice", "Single Room"));
        bookingService.book(new Reservation("R2", "Bob", "Double Room"));

        history.display();
        inventory.displayInventory();

        // Cancellation
        cancelService.cancel("R1");   // valid
        cancelService.cancel("R3");   // invalid

        history.display();
        inventory.displayInventory();
        cancelService.showRollbackStack();
    }
}