import java.io.*;
import java.util.Scanner;

/**
 * A simple input utility class for reading from files.
 * Similar to Princeton's StdIn library.
 */
public class StdIn {
    private static Scanner scanner;
    
    /**
     * Sets the file to read from.
     * @param fileName the name of the file to read
     */
    public static void setFile(String fileName) {
        try {
            scanner = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + fileName);
            e.printStackTrace();
        }
    }
    
    /**
     * Checks if there is more input available.
     * @return true if there is more input, false otherwise
     */
    public static boolean isEmpty() {
        return scanner == null || !scanner.hasNextLine();
    }
    
    /**
     * Reads the next line from the input.
     * @return the next line as a String
     */
    public static String readLine() {
        if (scanner == null) {
            throw new IllegalStateException("No file has been set. Call setFile() first.");
        }
        return scanner.nextLine();
    }
    
    /**
     * Closes the scanner.
     */
    public static void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
