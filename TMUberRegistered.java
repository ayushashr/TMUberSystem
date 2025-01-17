import java.util.ArrayList;
import java.io.File;  // Import the File class
import java.io.IOException;
import java.util.Scanner;

public class TMUberRegistered
{
    // These variables are used to generate user account and driver ids
    private static int firstUserAccountID = 900;
    private static int firstDriverId = 700;

    // Generate a new user account id
    public static String generateUserAccountId(ArrayList<User> current)
    {
        return "" + firstUserAccountID + current.size();
    }

    // Generate a new driver id
    public static String generateDriverId(ArrayList<Driver> current)
    {
        return "" + firstDriverId + current.size();
    }

    // Database of Preregistered users
    // In Assignment 2 these will be loaded from a file
    // The test scripts and test outputs included with the skeleton code use these
    // users and drivers below. You may want to work with these to test your code (i.e. check your output with the
    // sample output provided). 
    public static ArrayList<User> loadPreregisteredUsers(String filename) throws IOException {
        ArrayList<User> users = new ArrayList<>();
        File userFile = new File(filename);
        Scanner scanner = new Scanner(userFile);
        while (scanner.hasNextLine()) {
            String id = generateUserAccountId(users);
            String name = scanner.nextLine();
            String address = scanner.nextLine();
            double wallet = Double.parseDouble(scanner.nextLine());

            users.add(new User(id, name, address, wallet));
        }
        scanner.close();
        return users;
    }

    // Database of Preregistered users
    // In Assignment 2 these will be loaded from a file
    public static ArrayList<Driver> loadPreregisteredDrivers(String filename) throws IOException {
        ArrayList<Driver> drivers = new ArrayList<>();
        File driverFile = new File(filename);
        Scanner scanner = new Scanner(driverFile);
        while (scanner.hasNextLine()) {
            String id = generateDriverId(drivers);
            String name = scanner.nextLine();
            String carModel = scanner.nextLine();
            String carLicense = scanner.nextLine();
            String address = scanner.nextLine();
            drivers.add(new Driver(id, name, carModel, carLicense, address));
        }
        scanner.close();
        return drivers;
    }
}

