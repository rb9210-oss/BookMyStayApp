/**
 * Book My Stay Application - Use Case 12
 * Demonstrates persistence using serialization and recovery from file.
 *
 * Key Focus:
 * - Serialization & Deserialization
 * - File-based persistence
 * - System recovery after restart
 * - Failure-safe loading
 *
 * @author YourName
 * @version 1.0
 */

import java.io.*;
import java.util.*;

// ---------- Reservation ----------
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

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
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> availabilityMap = new HashMap<>();

    public RoomInventory() {
        availabilityMap.put("Single Room", 2);
        availabilityMap.put("Double Room", 1);
    }

    public int getAvailability(String type) {
        return availabilityMap.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        availabilityMap.put(type, getAvailability(type) - 1);
    }

    public void display() {
        System.out.println("\nInventory:");
        for (Map.Entry<String, Integer> e : availabilityMap.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

// ---------- Booking History ----------
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }

    public void display() {
        System.out.println("\nBooking History:");
        for (Reservation r : history) {
            System.out.println(r);
        }
    }
}

// ---------- Persistence Service ----------
class PersistenceService {

    private static final String FILE_NAME = "bookmyStay.dat";

    // Save state
    public static void save(RoomInventory inventory, BookingHistory history) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(inventory);
            oos.writeObject(history);

            System.out.println("\nSystem state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load state
    public static Object[] load() {

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            RoomInventory inventory = (RoomInventory) ois.readObject();
            BookingHistory history = (BookingHistory) ois.readObject();

            System.out.println("System state restored successfully.");
            return new Object[]{inventory, history};

        } catch (FileNotFoundException e) {
            System.out.println("No saved data found. Starting fresh...");
        } catch (Exception e) {
            System.out.println("Error loading data. Starting with clean state...");
        }

        // fallback (safe recovery)
        return new Object[]{new RoomInventory(), new BookingHistory()};
    }
}

// ---------- Main Application ----------
public class BookMyStayApp {

    public static void main(String[] args) {

        // Load existing state (if any)
        Object[] data = PersistenceService.load();
        RoomInventory inventory = (RoomInventory) data[0];
        BookingHistory history = (BookingHistory) data[1];

        // Simulate bookings
        Reservation r1 = new Reservation("R1", "Alice", "Single Room");
        Reservation r2 = new Reservation("R2", "Bob", "Double Room");

        if (inventory.getAvailability(r1.getRoomType()) > 0) {
            inventory.decrement(r1.getRoomType());
            history.add(r1);
        }

        if (inventory.getAvailability(r2.getRoomType()) > 0) {
            inventory.decrement(r2.getRoomType());
            history.add(r2);
        }

        // Display current state
        inventory.display();
        history.display();

        // Save before shutdown
        PersistenceService.save(inventory, history);
    }
}