/**
 * Book My Stay Application
 *
 * This class serves as the entry point for the Hotel Booking Management System.
 * It demonstrates the basic structure of a Java application including
 * the main method and console output.
 *
 * The application prints a welcome message along with the system name
 * and version information.
 *
 * @author YourName
 * @version 1.0
 */
public class BookMyStayApp {

    /**
     * Main method - Entry point of the application.
     * The JVM starts execution from here.
     *
     * @param args Command-line arguments (not used here)
     */
    public static void main(String[] args) {

        // Application Name and Version
        String appName = "Book My Stay - Hotel Booking System";
        String version = "Version 1.0";

        // Welcome Message
        System.out.println("======================================");
        System.out.println(" Welcome to " + appName);
        System.out.println(" " + version);
        System.out.println("======================================");

        // Closing Message
        System.out.println("Application started successfully.");
        System.out.println("Thank you for using Book My Stay!");
    }
}
