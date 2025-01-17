
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator; 

/*
 * 
 * This class contains the main logic of the system.
 * 
 *  It keeps track of all users, drivers and service requests (RIDE or DELIVERY)
 * 
 */
public class TMUberSystemManager
{
  private Map<String, User>   users;
  private ArrayList<Driver> drivers;

  private Queue<TMUberService>[] serviceRequests;
  private ArrayList<User> listUsers;

  public double totalRevenue; // Total revenues accumulated via rides and deliveries
  
  // Rates per city block
  private static final double DELIVERYRATE = 1.2;
  private static final double RIDERATE = 1.5;
  
  // Portion of a ride/delivery cost paid to the driver
  private static final double PAYRATE = 0.1;

  // These variables are used to generate user account and driver ids
  int userAccountId = 900;
  int driverId = 700;

  public TMUberSystemManager()
  {
    // Using treemap so it is automatically sorted by userId
    users   = new TreeMap<>();
    drivers = new ArrayList<Driver>();

    // Convering map to an arraylist
    listUsers = new ArrayList<>(users.values());
    // Creating Queue object for each zone
    serviceRequests = (Queue<TMUberService>[]) new Queue[4];
    for (int i = 0; i < serviceRequests.length; i++) {
        serviceRequests[i] = new LinkedList<TMUberService>();
    }
    totalRevenue = 0;
  }

  void setUsers(ArrayList<User> userList){

    // If there are already registered users, shift the loaded users since it always starts at accountId 9000
    if(users.size()>=1){
      int shift = users.size();
      for (User user : userList) {
        // Change Id to shift how many users are already registered
        int newId = Integer.parseInt(user.getAccountId().substring(2))+shift;
        
        user.setAccountId("900"+String.valueOf(newId));
        users.put(user.getAccountId(), user);

        // adding to listusers so it adds in the proper order 
        listUsers.add(user);
      }
    } else{
      for (User user : userList) {
        users.put(user.getAccountId(), user);
        listUsers.add(user);
      }
    }
    
    
  }

  void setDrivers(ArrayList<Driver> driverList){
    if(drivers.size()>=1){
      int shift = drivers.size();
      for (Driver driver : driverList) {
        int newId = Integer.parseInt(driver.getId().substring(2))+shift;
        driver.setId("700"+String.valueOf(newId));
        drivers.add(driver);
      }
    } else{
      for (Driver driver : driverList) {
        drivers.add(driver);
      }
    }
  }
  // General string variable used to store an error message when something is invalid 
  // (e.g. user does not exist, invalid address etc.)  
  // The methods below will set this errMsg string and then return false
  String errMsg = "";

  // public String getErrorMessage()
  // {
  //   return errMsg;
  // }
  
  // Generate a new user account id
  private String generateUserAccountId()
  {
    return "" + userAccountId + users.size();
  }
  
  // Generate a new driver id
  private String generateDriverId()
  {
    return "" + driverId + drivers.size();
  }

  // Given user account id, find user in list of users
  public User getUser(String accountId)
  {
    return users.get(accountId);
  }
  
  // Check for duplicate user
  private void userExists(String accountId) throws UserExistsException
  {
    // Iterate through the entries of the map
    for (Map.Entry<String, User> entry : users.entrySet()) {
      // Check if the accountId matches the key
      if (entry.getKey().equals(accountId)) {
          // If match is found return user exists exception
          throw new UserExistsException("User Already Exists in System ");
      }
    }
  }
  
 // Check for duplicate driver
 private void driverExists(Driver driver) throws DriverExistsException
 {

   for (int i = 0; i < drivers.size(); i++)
   {
    // throw driver exists
     if (drivers.get(i).equals(driver))
       throw new DriverExistsException("Driver Already Exists in System");
   }

 }
  
 
 // Given a user, check if user ride/delivery request already exists in service requests
 private void existingRequest(TMUberService req)
 {
   // Iterating through the queues 
   for (Queue<TMUberService> queue : serviceRequests) {
    // Iterating through the services queue
    for (TMUberService service : queue) {
      // throw diff exceptions based on service type
      if (service.equals(req) && service.getServiceType().equals("DELIVERY")) {
        throw new DelExistsException("User Already Has Delivery Request at Restaurant with this Food Order"); // Service exists in this queue
      } else if(service.equals(req) && service.getServiceType().equals("RIDE")){
        throw new RideExistsException("User Already Has Ride Request");
      }
    }
  }
 }

  
  // Calculate the cost of a ride or of a delivery based on distance 
  private double getDeliveryCost(int distance)
  {
    return distance * DELIVERYRATE;
  }

  private double getRideCost(int distance)
  {
    return distance * RIDERATE;
  }

  // Go through all drivers and see if one is available
  // Choose the first available driver
  // private Driver getAvailableDriver()
  // {
  //   for (int i = 0; i < drivers.size(); i++)
  //   {
  //     Driver driver = drivers.get(i);
  //     if (driver.getStatus() == Driver.Status.AVAILABLE)
  //       return driver;
  //   }
  //   return null;
  // }

  // get Driver based on driverID
  public Driver getDriver(String accountId){
    for(Driver d: drivers){
      if(d.getId().equals(accountId)){
        return d;
      } 
    }
    return null;
  }


  // pick up based on driver in the zone
  void pickup(String driverId){
    // Find the Driver object using the driverId
    Driver driver = getDriver(driverId);

    // If driver not found, throw exception
    if (driver == null) {
      errMsg = "Driver not found with ID: "+ driverId;
      throw new DriverNotFoundException(errMsg);
    }

    // Get the driver's current address to find the zone
    String currentAddress = driver.getAddress();
    int zone = CityMap.getCityZone(currentAddress);

    // Get the queue for the driver's zone
    Queue<TMUberService> zoneQueue = serviceRequests[zone];

    // Check if any requests in this zone
    if (zoneQueue.isEmpty()) {
      errMsg = "No Service Request in Zone " + zone;
      throw new NoServiceRequestException(errMsg);
    }

    // Check if driver already has picked someone up
    if(driver.getStatus() == Driver.Status.DRIVING){
      errMsg = "Driver already has active request";
      throw new DriverExistsException(errMsg); 
    }
    // Remove the TMUberService object from the front of the queue, and save it to a variable
    TMUberService serviceRequest = zoneQueue.remove();

    // Set the new service variable in the Driver object
    driver.setService(serviceRequest);

    // Set driver status
    driver.setStatus(Driver.Status.DRIVING);

    // Set the driver address and zone to the From address for this service request
    driver.setAddress(serviceRequest.getFrom());
    driver.setZone(serviceRequest.getFrom());
  }

  void driveTo(String driverId, String address){
   // Find the Driver object using the driverId
   Driver driver = getDriver(driverId);

   // If driver not found, throw exception
   if (driver == null) {
    errMsg = "Driver not found with ID: "+ driverId;
    throw new DriverNotFoundException(errMsg);
   }

   if (!CityMap.validAddress(address)){
    throw new AddressException("Invalid Address");
   }
   // Set the drivers address to the proper address
   if (driver.getStatus() == Driver.Status.AVAILABLE){
    driver.setAddress(address);
    driver.setZone(driver.getAddress());
    System.out.print("Driver "+driverId+" Now in Zone "+driver.getZone());
   }
  }

  // Print Information (printInfo()) about all registered users in the system
  public void listAllUsers()
  {
    System.out.println();
    int index = 1;
    for (int i = 0; i<listUsers.size(); i++){
      System.out.printf("%-2s. ", index++);
      listUsers.get(i).printInfo();
      System.out.println();
    }
  }

  // Print Information (printInfo()) about all registered drivers in the system
  public void listAllDrivers()
  {
    System.out.println("");
    
    for (int i = 0; i < drivers.size(); i++)
    {
      //System.out.println();
      int index = i + 1;
      System.out.printf("%-2s. ", index);
      //System.out.println();
      drivers.get(i).printInfo(); 
      if(i<drivers.size()-1){
        System.out.println();
      }
    }
  }

  // Print Information (printInfo()) about all current service requests
  public void listAllServiceRequests()
  {
    // Iterate through the queues
    for(int i = 0; i<4;i++){
      int index = 1;
      System.out.println("");
      System.out.println("ZONE "+i);
      System.out.println("======");

      // Iterate through the service requests in each zone (i)
      for (TMUberService service : serviceRequests[i]) {
        System.out.println("");
        System.out.print(index + ". ");
        for (int j = 0; j < 60; j++) {
            System.out.print("-");
        }
        service.printInfo();
        System.out.println("");
        index++;
      }
      
    }
  }

  // Add a new user to the system
  public void registerNewUser(String name, String address, double wallet)
  {
    // Check to ensure name is valid
    if (name == null || name.equals(""))
    {
      errMsg = "Invalid User Name " + name;
      throw new InvalidUserNameException("Invalid User Name " + name);
    }
    // Check to ensure address is valid
    if (!CityMap.validAddress(address))
    {
      errMsg = "Invalid User Address " + address;
      throw new InvalidUserAddressException("Invalid User Address " + address);
    }
    // Check to ensure wallet amount is valid
    if (wallet < 0)
    {
      errMsg = "Invalid Money in Wallet";
      throw new MoneyInWalletException("Invalid Money in Wallet");
    }
    User user = new User(generateUserAccountId(), name, address, wallet);

    // Check for duplicate user
    // Exception is thrown in userExists method
    userExists(user.getAccountId());

    users.put(user.getAccountId(), user);
    listUsers.add(user);
    
  }

  // Add a new driver to the system
  public void registerNewDriver(String name, String carModel, String carLicencePlate, String address)
  {
    // Check to ensure name is valid
    if (name == null || name.equals(""))
    {
      errMsg = "Invalid Driver Name " + name;
      throw new InvalidDriverNameException(errMsg);
    }
    // Check to ensure car models is valid
    if (carModel == null || carModel.equals(""))
    {
      errMsg = "Invalid Car Model " + carModel;
      throw new CarModelException(errMsg);
    }
    // Check to ensure car licence plate is valid
    // i.e. not null or empty string
    if (carLicencePlate == null || carLicencePlate.equals(""))
    {
      errMsg = "Invalid Car Licence Plate " + carLicencePlate;
      throw new LicensePlateException(errMsg);
    }

    // Check to ensure drivers address is valid
    if (!CityMap.validAddress(address)){
      throw new AddressException("Invalid Address: " + address);
    }
    
    // Check for duplicate driver. If not a duplicate, add the driver to the drivers list
    Driver driver = new Driver(generateDriverId(), name, carModel, carLicencePlate, address);
    driverExists(driver);
    drivers.add(driver);  
  }

  // Request a ride. User wallet will be reduced when drop off happens
  public void requestRide(String accountId, String from, String to)
  {
    // Check valid user account
    User user = getUser(accountId);
    if (user == null)
    {
      errMsg = "User Account Not Found " + accountId;
      throw new UserNotFoundException(errMsg);
    }
    // Check for a valid from and to addresses
    if (!CityMap.validAddress(from))
    {
      errMsg = "Invalid Address " + from;
      throw new AddressException(errMsg);
    }
    if (!CityMap.validAddress(to))
    {
      errMsg = "Invalid Address " + to;
      throw new AddressException(errMsg);
    }
    // Get the distance for this ride
    int distance = CityMap.getDistance(from, to);         // city blocks
    int requestZone = CityMap.getCityZone(from);
    // Distance == 0 or == 1 is not accepted - walk!
    if (distance <= 1)
    {
      errMsg = "Insufficient Travel Distance";
      throw new DistanceException(errMsg);
    }
    // Check if user has enough money in wallet for this trip
    double cost = getRideCost(distance);
    if (user.getWallet() < cost)
    {
      errMsg = "Insufficient Funds";
      throw new InvalidFundsException(errMsg);
    }

    // Create the request
    TMUberRide req = new TMUberRide(from, to, user, distance, cost);
    
    // Check if existing ride request for this user - only one ride request per user at a time
    existingRequest(req);
    // Add to appropriate queue based on zone #
    serviceRequests[requestZone].add(req);
    user.addRide();
  }

  // Request a food delivery. User wallet will be reduced when drop off happens
  public void requestDelivery(String accountId, String from, String to, String restaurant, String foodOrderId)
  {
    // Check for valid user account
    User user = getUser(accountId);
    if (user == null) {
      errMsg = "User Account Not Found " + accountId;
      throw new UserNotFoundException(errMsg);
    }

  // Check for valid from and to address
    if (!CityMap.validAddress(from)) {
      errMsg = "Invalid Address " + from;
      throw new AddressException(errMsg);
    }
    if (!CityMap.validAddress(to)) {
      errMsg = "Invalid Address " + to;
      throw new AddressException(errMsg);
    }

  // Get the distance to travel
    int distance = CityMap.getDistance(from, to); // city blocks
    int requestZone = CityMap.getCityZone(from);

    
    if (distance <= 1) {
      errMsg = "Insufficient Travel Distance";
      throw new DistanceException(errMsg);
    }

  // Check if user has enough money in wallet for this delivery
    double cost = getDeliveryCost(distance);
    if (user.getWallet() < cost) {
      errMsg = "Insufficient Funds";
      throw new InvalidFundsException(errMsg);
    }

    TMUberDelivery delivery = new TMUberDelivery(from, to, user, distance, cost, restaurant, foodOrderId); 
    // Check if existing delivery request for this user for this restaurant and food order #
    existingRequest(delivery);
    serviceRequests[requestZone].add(delivery);
    user.addDelivery();
  }


  // Cancel an existing service request. 
  // parameter request is the index in the serviceRequests array list
  public void cancelServiceRequest(int reqnum, int zone)
  {
    // check if valid zone input
    if (zone < 0 || zone > 3) {
      throw new InvalidZoneException("Invalid Zone #");
    }
    // Check if valid driverID
    Queue<TMUberService> zoneService = serviceRequests[zone];
    if (reqnum>zoneService.size() ||reqnum<=0){
      throw new InvalidReqNumException("Invalid Request #");
    }


    // Use an iterator to go through services in the zone
    Iterator<TMUberService> it = zoneService.iterator();
    // Index acts as the pointer 
    int index = 1;
    while (it.hasNext()) {
      TMUberService service = it.next();

      // Once pointer is at the right request 
      if(index == reqnum){
        User u = service.getUser();
        if (service.getServiceType().equals("DELIVERY")){
          u.decrementDelivery();
        } else if (service.getServiceType().equals("RIDE")){
          u.decrementRide();
        }
        System.out.println("Service request for " + u.getName() + " cancelled");
        // Remove it using iterator 
        it.remove();
        // Stop iterating
        break;
      }
      // move pointer
      index++;
    }
  }
  
  // Drop off a ride or a delivery. This completes a service.
  // parameter request is the index in the serviceRequests array list
  public void dropOff(String driverId)
  {
    Driver driver= getDriver(driverId);
    if (driver == null) {
      errMsg = "Invalid Driver Id";
      throw new DriverNotFoundException(errMsg);
    }
    TMUberService service = driver.getService();
    if(service == null){
      errMsg = "Driver "+driverId+" has no active requests";
      throw new NoServiceRequestException(errMsg);
    }
    User user = service.getUser();
    if (service.getServiceType().equals("DELIVERY")){
      user.decrementDelivery();
    } else if (service.getServiceType().equals("RIDE")){
      user.decrementRide();
    }
    totalRevenue += service.getCost();          // add service cost to revenues
    driver.pay(service.getCost()*PAYRATE);      // pay the driver
    totalRevenue -= service.getCost()*PAYRATE;  // deduct driver fee from total revenues
    driver.setStatus(Driver.Status.AVAILABLE);  // driver is now available again
    driver.setService(null);                    // no service for driver
    driver.setAddress(service.getTo());         // setaAddress to the To of the requst because driver is there now
    driver.setZone(driver.getAddress());        // Change zone accordingly
    user.payForService(service.getCost());      // user pays for ride or delivery
  }


  // Sort users by name using the user arraylist
  public void sortByUserName()
  {
    Collections.sort(listUsers, new NameComparator());
    listAllUsers();
  }

  private class NameComparator implements Comparator<User>
  {
    public int compare(User a, User b)
    {
      return a.getName().compareTo(b.getName());
    }
  }

  // Sort users by number amount in wallet
  public void sortByWallet()
  {
    Collections.sort(listUsers, new UserWalletComparator());
    listAllUsers();
  }

  private class UserWalletComparator implements Comparator<User>
  {
    public int compare(User a, User b)
    {
      if (a.getWallet() > b.getWallet()) return 1;
      if (a.getWallet() < b.getWallet()) return -1; 
      return 0;
    }
  }

  // Sort trips (rides or deliveries) by distance
  // class TMUberService must implement Comparable
  // public void sortByDistance()
  // {
  //   Collections.sort(serviceRequests);
  //   listAllServiceRequests();
  // }
  
}

// CUSTOM EXCEPTIONS
class InvalidUserNameException extends RuntimeException { 
  public InvalidUserNameException(){}
  public InvalidUserNameException(String message) {
      super(message);
  } 
}
class DriverNotFoundException extends RuntimeException {
  DriverNotFoundException(){}
  public DriverNotFoundException(String message) {
      super(message);
  }
}

class NoServiceRequestException extends RuntimeException {
  NoServiceRequestException(){}
  public NoServiceRequestException(String message) {
      super(message);
  }
}

class InvalidUserAddressException extends RuntimeException { 
  public InvalidUserAddressException(){}
  public InvalidUserAddressException(String message) {
      super(message);
  } 
}

class MoneyInWalletException extends RuntimeException { 
  public MoneyInWalletException(){}
  public MoneyInWalletException(String message) {
      super(message);
  } 
}

class UserExistsException extends RuntimeException { 
  UserExistsException(){}
  public UserExistsException(String message) {
      super(message);
  } 
}

class InvalidDriverNameException extends RuntimeException { 
  InvalidDriverNameException(){}
  public InvalidDriverNameException(String message) {
      super(message);
  } 
}

class LicensePlateException extends RuntimeException { 
  LicensePlateException(){}
  public LicensePlateException(String message) {
      super(message);
  } 
}

class CarModelException extends RuntimeException { 
  CarModelException(){}
  public CarModelException(String message) {
      super(message);
  } 
}

class DriverExistsException extends RuntimeException { 
  DriverExistsException(){}
  public DriverExistsException(String message) {
      super(message);
  } 
}

class UserNotFoundException extends RuntimeException { 
  UserNotFoundException(){}
  public UserNotFoundException(String message) {
      super(message);
  } 
}

class RideExistsException extends RuntimeException { 
  RideExistsException(){}
  public RideExistsException(String message) {
      super(message);
  } 
}

class DelExistsException extends RuntimeException { 
  DelExistsException(){}
  public DelExistsException(String message) {
      super(message);
  } 
}

class DistanceException extends RuntimeException { 
  DistanceException(){}
  public DistanceException(String message) {
      super(message);
  } 
}

class AddressException extends RuntimeException { 
  AddressException(){}
  public AddressException(String message) {
      super(message);
  } 
}

class InvalidFundsException extends RuntimeException {
  InvalidFundsException(){} 
  public InvalidFundsException(String message) {
      super(message);
  } 
}

class NoDriversException extends RuntimeException { 
  NoDriversException(){}
  public NoDriversException(String message) {
      super(message);
  } 
}

class InvalidReqNumException extends RuntimeException { 
  InvalidReqNumException(){}
  public InvalidReqNumException(String message) {
      super(message);
  } 
}

class InvalidZoneException extends RuntimeException {
  InvalidZoneException(){}
  public InvalidZoneException(String message){
    super(message);
  }
}