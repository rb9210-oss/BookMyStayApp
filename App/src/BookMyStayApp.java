/**
 * Book My Stay Application - Use Case 9
 * Demonstrates validation, custom exceptions, and fail-fast design.
 *
 * Key Focus:
 * - Input validation
 * - Custom exceptions
 * - Prevent invalid state changes
 * - Graceful error handling
 *
 * @author YourName
 * @version 1.0
 */

import java.util.*;

// ---------- Custom Exceptions ----------
class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class NoAvailabilityException extends Exception {
    public NoAvailabilityException(String message) {
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
        availabilityMap.put("Single Room", 2);
        availabilityMap.put("Double Room", 1);
        availabilityMap.put("Suite Room", 1);
    }

    public boolean isValidRoomType(String type) {
        return availabilityMap.containsKey(type);
    }

    public int getAvailability(String type) {
        return availabilityMap.getOrDefault(type, 0);
    }

    public void decrement(String type) throws NoAvailabilityException {
        int current = getAvailability(type);

        if (current <= 0) {
            throw new NoAvailabilityException("No rooms available for " + type);
        }

        availabilityMap.put(type, current - 1);
    }
}

// ---------- Booking History ----------
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }
}

// ---------- Validator ----------
class BookingValidator {

    private RoomInventory inventory;

    public BookingValidator(RoomInventory inventory) {
        this.inventory = inventory;
    }

    // Validate booking input
    public void validate(Reservation r)
            throws InvalidRoomTypeException, NoAvailabilityException {

        // Check room type
        if (!inventory.isValidRoomType(r.getRoomType())) {
            throw new InvalidRoomTypeException(
                    "Invalid room type: " + r.getRoomType());
        }

        // Check availability
        if (inventory.getAvailability(r.getRoomType()) <= 0) {
            throw new NoAvailabilityException(
                    "No availability for: " + r.getRoomType());
        }
    }
}

// ---------- Booking Service ----------
class BookingService {

    private RoomInventory inventory;
    private BookingHistory history;
    private BookingValidator validator;

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
        this.validator = new BookingValidator(inventory);
    }

    public void processBooking(Reservation r) {

        try {
            // Validate first (Fail-Fast)
            validator.validate(r);

            // Allocation (safe)
            inventory.decrement(r.getRoomType());

            // Store in history
            history.addReservation(r);

            System.out.println("Booking Confirmed → " + r);

        } catch (InvalidRoomTypeException | NoAvailabilityException e) {
            // Graceful failure
            System.out.println("Booking Failed → " + r.getGuestName()
                    + " | Reason: " + e.getMessage());
        }
    }
}

// ---------- Main Application ----------
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        BookingService service = new BookingService(inventory, history);

        // Test cases (valid + invalid)
        List<Reservation> requests = Arrays.asList(
                new Reservation("R1", "Alice", "Single Room"),   // valid
                new Reservation("R2", "Bob", "Luxury Room"),     // invalid type
                new Reservation("R3", "Charlie", "Single Room"), // valid
                new Reservation("R4", "David", "Single Room")    // no availability
        );

        for (Reservation r : requests) {
            service.processBooking(r);
        }

        // Show history
        System.out.println("\n====== Booking History ======");
        for (Reservation r : history.getAll()) {
            System.out.println(r);
        }
    }
}