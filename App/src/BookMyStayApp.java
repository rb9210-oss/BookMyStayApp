/**
 * Book My Stay Application - Use Case 2
 * Demonstrates abstraction, inheritance, polymorphism,
 * and static room availability.
 *
 * @author YourName
 * @version 1.0
 */

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

    // Abstract method
    public abstract void displayDetails();
}

// Single Room class
class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 1, 2000.0);
    }

    @Override
    public void displayDetails() {
        System.out.println("Room Type: " + roomType);
        System.out.println("Beds: " + beds);
        System.out.println("Price: ₹" + price);
    }
}

// Double Room class
class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 2, 3500.0);
    }

    @Override
    public void displayDetails() {
        System.out.println("Room Type: " + roomType);
        System.out.println("Beds: " + beds);
        System.out.println("Price: ₹" + price);
    }
}

// Suite Room class
class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 3, 6000.0);
    }

    @Override
    public void displayDetails() {
        System.out.println("Room Type: " + roomType);
        System.out.println("Beds: " + beds);
        System.out.println("Price: ₹" + price);
    }
}

// Main Application Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // Polymorphic Room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static Availability
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        System.out.println("========== Room Availability ==========");

        // Single Room
        single.displayDetails();
        System.out.println("Available: " + singleAvailable);
        System.out.println("--------------------------------------");

        // Double Room
        doubleRoom.displayDetails();
        System.out.println("Available: " + doubleAvailable);
        System.out.println("--------------------------------------");

        // Suite Room
        suite.displayDetails();
        System.out.println("Available: " + suiteAvailable);
        System.out.println("--------------------------------------");

        System.out.println("=======================================");
    }
}