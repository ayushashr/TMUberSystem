/*
 * 
 * This class simulates a food delivery service for a simple Uber app
 * 
 * A TMUberDelivery is-a TMUberService with some extra functionality
 */
public class TMUberDelivery extends TMUberService
{
  public static final String TYPENAME = "DELIVERY";
 
  private String restaurant; 
  private String foodOrderId;
      
  public TMUberDelivery(String from, String to, User user, int distance, double cost,
                        String restaurant, String order)
  {
    super(from, to, user, distance, cost, TMUberDelivery.TYPENAME);
    this.restaurant = restaurant;
    this.foodOrderId = order;
  }
 
  
  public String getServiceType()
  {
    return TYPENAME;
  }
  
  public String getRestaurant()
  {
    return restaurant;
  }

  public void setRestaurant(String restaurant)
  {
    this.restaurant = restaurant;
  }

  public String getFoodOrderId()
  {
    return foodOrderId;
  }

  public void setFoodOrderId(String foodOrderId)
  {
    this.foodOrderId = foodOrderId;
  }
  /*
   * Two Delivery Requests are equal if they are equal in terms of TMUberServiceRequest
   * and restaurant and food order id
   */
  public boolean equals(Object other)
  {
    // First check to see if other is a Delivery type
    // Cast other to a TMUService reference and check type
    // If not a delivery, return false
    TMUberService req = (TMUberService)other;
    if (!req.getServiceType().equals(TMUberDelivery.TYPENAME))
      return false;
    
    // Now check if this delivery and other delivery are equal
    TMUberDelivery delivery = (TMUberDelivery)other;
    return super.equals(other) && delivery.getRestaurant().equals(restaurant) && 
                                  delivery.getFoodOrderId().equals(foodOrderId);
  }
  /*
   * Print Information about a Delivery Request
   */
  public void printInfo()
  {
    super.printInfo();
    System.out.printf("\nRestaurant: %-9s Food Order #: %-3s", restaurant, foodOrderId); 
  }
}
