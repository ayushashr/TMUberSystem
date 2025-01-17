import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException; 
import java.io.FileNotFoundException;

// Simulation of a Simple Command-line based Uber App 

// This system supports "ride sharing" service and a delivery service

public class TMUberUI
{
  public static void main(String[] args)
  {
    // Create the System Manager - the main system code is in here 

    TMUberSystemManager tmuber = new TMUberSystemManager();
    
    Scanner scanner = new Scanner(System.in);
    System.out.print(">");

    // Process keyboard actions
    while (scanner.hasNextLine())
    {

      String action = scanner.nextLine();
      try{
        if (action == null || action.equals("")) 
        {
          System.out.print("\n>");
          continue;
        }
        // Quit the App
        else if (action.equalsIgnoreCase("Q") || action.equalsIgnoreCase("QUIT"))
          
          return;
        // Print all the registered drivers
        else if (action.equalsIgnoreCase("LOADUSERS")) {
          String filename = "";
          System.out.print("Users File: ");
          filename = scanner.nextLine();
          try {
            ArrayList<User> userList = TMUberRegistered.loadPreregisteredUsers(filename);
            tmuber.setUsers(userList);
            System.out.println("Users Loaded");
          } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
              System.out.println("Users File: "+filename+" Not Found");
            } else {
              System.out.println("Exiting the program.");
              System.exit(1);
            }
          }

        } else if (action.equalsIgnoreCase("LOADDRIVERS")) {
          String filename = "";
          System.out.print("Drivers File: ");
          filename = scanner.nextLine();
          try {
            ArrayList<Driver> driverList = TMUberRegistered.loadPreregisteredDrivers(filename);
            tmuber.setDrivers(driverList);
            System.out.println("Drivers Loaded");
          } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
              System.out.println("Drivers File: "+filename+" Not Found");
            } else {
              System.out.println("Exiting the program.");
              System.exit(1);
            }
          }
          
        } else if (action.equalsIgnoreCase("DRIVERS"))  // List all drivers
        {
          tmuber.listAllDrivers(); 
        }
        // Print all the registered users
        else if (action.equalsIgnoreCase("USERS"))  // List all users
        {
          tmuber.listAllUsers(); 
        }
        // Print all current ride requests or delivery requests
        else if (action.equalsIgnoreCase("REQUESTS"))  // List all requests
        {
          tmuber.listAllServiceRequests(); 
          
        }
        // Register a new driver
        else if (action.equalsIgnoreCase("REGDRIVER")) 
        {
          String name = "";
          System.out.print("Name: ");
          if (scanner.hasNextLine())
          {
            name = scanner.nextLine();
          }
          String carModel = "";
          System.out.print("Car Model: ");
          if (scanner.hasNextLine())
          {
            carModel = scanner.nextLine();
          }
          String license = "";
          System.out.print("Car License: ");
          if (scanner.hasNextLine())
          {
            license = scanner.nextLine();
          }

          String address = "";
          System.out.print("Address: ");
          if (scanner.hasNextLine())
          {
            address = scanner.nextLine();
          }

          tmuber.registerNewDriver(name, carModel, license, address);
          System.out.printf("Driver: %-15s Car Model: %-15s License Plate: %-10s", name, carModel, license);

        }
        // Register a new user
        else if (action.equalsIgnoreCase("REGUSER")) 
        {
          String name = "";
          System.out.print("Name: ");
          if (scanner.hasNextLine())
          {
            name = scanner.nextLine();
          }
          String address = "";
          System.out.print("Address: ");
          if (scanner.hasNextLine())
          {
            address = scanner.nextLine();
          }
          double wallet = 0.0;
          System.out.print("Wallet: ");
          if (scanner.hasNextDouble())
          {
            wallet = scanner.nextDouble();
            scanner.nextLine(); // consume nl
          }

          tmuber.registerNewUser(name, address, wallet);
          System.out.printf("User: %-15s Address: %-15s Wallet: %2.2f", name, address, wallet);

          
        }
        // Request a ride
        else if (action.equalsIgnoreCase("REQRIDE")) 
        {
          String account = "";
          System.out.print("User Account Id: ");
          if (scanner.hasNextLine())
          {
            account = scanner.nextLine();
          }
          String from = "";
          System.out.print("From Address: ");
          if (scanner.hasNextLine())
          {
            from = scanner.nextLine();
          }
          String to = "";
          System.out.print("To Address: ");
          if (scanner.hasNextLine())
          {
            to = scanner.nextLine();
          }
          tmuber.requestRide(account, from, to);
          User user = tmuber.getUser(account);
          System.out.printf("\nRIDE for: %-15s From: %-15s To: %-15s", user.getName(), from, to);
        }
        // Request a food delivery
        else if (action.equalsIgnoreCase("REQDLVY")) 
        {
          String account = "";
          System.out.print("User Account Id: ");
          if (scanner.hasNextLine())
          {
            account = scanner.nextLine();
          }
          String from = "";
          System.out.print("From Address: ");
          if (scanner.hasNextLine())
          {
            from = scanner.nextLine();
          }
          String to = "";
          System.out.print("To Address: ");
          if (scanner.hasNextLine())
          {
            to = scanner.nextLine();
          }
          String restaurant = "";
          System.out.print("Restaurant: ");
          if (scanner.hasNextLine())
          {
            restaurant = scanner.nextLine();
          }
          String foodOrder = "";
          System.out.print("Food Order #: ");
          if (scanner.hasNextLine())
          {
            foodOrder = scanner.nextLine();
          }
          tmuber.requestDelivery(account, from, to, restaurant, foodOrder);
          User user = tmuber.getUser(account);
          System.out.printf("\nDELIVERY for: %-15s From: %-15s To: %-15s", user.getName(), from, to);  

        } else if (action.equalsIgnoreCase("PICKUP")){
          String driverId = "";
          System.out.print("Driver Id: ");
          if (scanner.hasNextLine()){
            driverId = scanner.nextLine();
          }

          tmuber.pickup(driverId);
          Driver driver = tmuber.getDriver(driverId);
          System.out.println("Driver "+driverId+" Picking Up in Zone "+ driver.getZone());

        // Sort users by name
        } else if (action.equalsIgnoreCase("SORTBYNAME")) 
        {
          tmuber.sortByUserName();
        }
        // Sort users by number of ride they have had
        else if (action.equalsIgnoreCase("SORTBYWALLET")) 
        {
          tmuber.sortByWallet();
        }
        // Sort current service requests (ride or delivery) by distance
        // else if (action.equalsIgnoreCase("SORTBYDIST")) 
        // {
        //   tmuber.sortByDistance();
        // }
        // Cancel a current service (ride or delivery) request
        else if (action.equalsIgnoreCase("CANCELREQ")) 
        {
          int zone=-1;
          System.out.print("Zone: ");
          if (scanner.hasNext())
          {
            zone = scanner.nextInt();
            scanner.nextLine(); // consume nl character
          }

          int reqnum =-1;
          System.out.print("Request #: ");
          if (scanner.hasNext())
          {
            reqnum = scanner.nextInt();
            scanner.nextLine(); // consume nl character
          }

          tmuber.cancelServiceRequest(reqnum, zone);

        }
        // Drop-off the user or the food delivery to the destination address
        else if (action.equalsIgnoreCase("DROPOFF")) 
        {
          String driverId = "";
          System.out.print("Driver Id: ");
          if (scanner.hasNext())
          {
            driverId = scanner.next();
            scanner.nextLine(); // consume nl
          }
          tmuber.dropOff(driverId);
          System.out.println("Driver " + driverId + " Dropping Off");
        }
        // Get the Current Total Revenues
        else if (action.equalsIgnoreCase("REVENUES")) 
        {
          System.out.println("Total Revenue: " + tmuber.totalRevenue);
        }
        // Unit Test of Valid City Address 
        else if (action.equalsIgnoreCase("ADDR")) 
        {
          String address = "";
          System.out.print("Address: ");
          if (scanner.hasNextLine())
          {
            address = scanner.nextLine();
          }
          System.out.print(address);
          if (CityMap.validAddress(address))
            System.out.println("\nValid Address"); 
          else
            System.out.println("\nBad Address"); 
        }
        // Unit Test of CityMap Distance Method
        else if (action.equalsIgnoreCase("DIST")) 
        {
          String from = "";
          System.out.print("From: ");
          if (scanner.hasNextLine())
          {
            from = scanner.nextLine();
          }
          String to = "";
          System.out.print("To: ");
          if (scanner.hasNextLine())
          {
            to = scanner.nextLine();
          }
          System.out.print("\nFrom: " + from + " To: " + to);
          System.out.println("\nDistance: " + CityMap.getDistance(from, to) + " City Blocks");

        } else if (action.equalsIgnoreCase("DRIVETO")){
          String driverId ="";
          String address = "";
          System.out.print("Driver Id: ");

          if (scanner.hasNextLine()){
            driverId = scanner.nextLine();
          }

          System.out.print("Address: ");
          if (scanner.hasNextLine()){
            address = scanner.nextLine();
          }
          tmuber.driveTo(driverId, address);
        }
      } catch(Exception e){
        System.out.println(e.getMessage());
      }
      System.out.print("\n>");
    }
  }
}

