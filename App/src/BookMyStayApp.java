/**
 * Book My Stay Application - Use Case 11
 * Demonstrates thread-safe booking using synchronization.
 *
 * Key Focus:
 * - Multi-threading (concurrent booking)
 * - Race condition prevention
 * - Synchronized critical sections
 *
 * @author YourName
 * @version 1.0
 */

import java.util.*;

// ---------- Reservation ----------
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// ---------- Thread-Safe Inventory ----------
class RoomInventory {

    private Map<String, Integer> availabilityMap = new HashMap<>();

    public RoomInventory() {
        availabilityMap.put("Single Room", 2);
    }

    // Critical section (synchronized)
    public synchronized boolean allocateRoom(String roomType) {

        int available = availabilityMap.getOrDefault(roomType, 0);

        if (available > 0) {
            // Simulate delay (to expose race condition if not synchronized)
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            availabilityMap.put(roomType, available - 1);

            System.out.println(Thread.currentThread().getName()
                    + " → Booking Confirmed | Remaining: " + (available - 1));

            return true;
        } else {
            System.out.println(Thread.currentThread().getName()
                    + " → Booking Failed (No Availability)");

            return false;
        }
    }
}

// ---------- Shared Booking Queue ----------
class BookingQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void addRequest(Reservation r) {
        queue.offer(r);
    }

    public synchronized Reservation getNext() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}

// ---------- Concurrent Booking Processor ----------
class BookingProcessor implements Runnable {

    private BookingQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingQueue queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {

            Reservation r;

            // Synchronized queue access
            synchronized (queue) {
                if (queue.isEmpty()) {
                    break;
                }
                r = queue.getNext();
            }

            if (r != null) {
                inventory.allocateRoom(r.getRoomType());
            }
        }
    }
}

// ---------- Main Application ----------
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();

        // Simulate multiple guest requests
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Single Room"));
        queue.addRequest(new Reservation("Charlie", "Single Room")); // extra

        // Create multiple threads
        Thread t1 = new Thread(new BookingProcessor(queue, inventory), "Thread-1");
        Thread t2 = new Thread(new BookingProcessor(queue, inventory), "Thread-2");
        Thread t3 = new Thread(new BookingProcessor(queue, inventory), "Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nAll booking requests processed safely.");
    }
}