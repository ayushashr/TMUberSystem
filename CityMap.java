import java.util.Arrays;
import java.util.Scanner;

// The city consists of a grid of 9 X 9 City Blocks

// Streets are west-east (1st street to 9th street)
// Avenues are south-north (1st avenue to 9th avenue)

// Example 1 of Interpreting an address:  "34 4th Street"
// A valid address always has 3 parts.
// Part 1: Street/Avenue residence numbers are always 2 digits (e.g. 34).
// Part 2: Must be 'n'th or 1st or 2nd or 3rd (e.g. n = 4)
// Part 3: Must be "Street" or "Avenue" (case insensitive)

// Use the first digit of the residence number (e.g. 3 of the number 34) to determine the avenue.
// For distance calculation you need to identify the the specific city block - in this example 
// it is city block (3, 4) (3rd avenue and 4th street)

// Example 2 of Interpreting an address:  "51 7th Avenue"
// Use the first digit of the residence number (i.e. 5 of the number 51) to determine street.
// For distance calculation you need to identify the the specific city block - 
// in this example it is city block (7, 5) (7th avenue and 5th street)
//
// Distance in city blocks between (3, 4) and (7, 5) is then == 5 city blocks

public class CityMap
{
  // Checks for string consisting of all digits
  // An easier solution would use String method matches()
  private static boolean allDigits(String s)
  {
    for (int i = 0; i < s.length(); i++)
      if (!Character.isDigit(s.charAt(i)))
        return false;
    return  true;
  }

  // Get all parts of address string
  // An easier solution would use String method split()
  private static String[] getParts(String address)
  {
    String parts[] = new String[3];
    
    if (address == null || address.length() == 0)
    {
      parts = new String[0];
      return parts;
    }
         
    int numParts = 0;
    Scanner sc = new Scanner(address);
    while (sc.hasNext())
    {
      if (numParts >= 3)
        parts = Arrays.copyOf(parts, parts.length+1);

      parts[numParts] = sc.next();
      numParts++;
    }
    if (numParts == 1)
      parts = Arrays.copyOf(parts, 1);
    else if (numParts == 2)
      parts = Arrays.copyOf(parts, 2);
    sc.close(); 
    return parts;
    
  }

  // Checks for a valid address
  public static boolean validAddress(String address)
  {
    int[] block = {-1, -1};

    //String[] parts = address.split(" ");
    String[] parts = getParts(address);
    
    if (parts.length != 3)
      return false;
    
    boolean streetType = false; 
    
    // "street" or "avenue" check
    if (!parts[2].equalsIgnoreCase("street") && !parts[2].equalsIgnoreCase("avenue"))
      return false;
    // which is it?
    if (parts[2].equalsIgnoreCase("street"))
      streetType = true;

    // All digits and digit count == 2
    if (!allDigits(parts[0]) || parts[0].length() != 2)
      return false;

    // Get first digit of street number
    int num1 = Integer.parseInt(parts[0])/10;
    if (num1 == 0) return false;
   
    // Must be 'n'th or 1st or 2nd or 3rd
    String suffix = parts[1].substring(1);
    if (parts[1].length() != 3) 
      return false;
   
    if (!suffix.equals("th") && !parts[1].equals("1st") &&
        !parts[1].equals("2nd") && !parts[1].equals("3rd"))
      return false;

    String digitStr = parts[1].substring(0, 1);
    if (!allDigits(digitStr))
      return false;
    int num2 = Integer.parseInt(digitStr);
    if (num2 == 0)
      return false;
    if (streetType)
    {
      block[0] = num2;
      block[1] = num1;
    }
    else
    {
      block[0] = num1;
      block[1] = num2;
    }
    return true;
  }

  // Computes the city block from address string
  public static int[] getCityBlock(String address)
  {
    int[] block = {-1, -1};
    boolean streetType = false; 

    //String[] parts = address.split(" ");
    String[] parts = getParts(address);

    if (parts[2].equalsIgnoreCase("street"))
      streetType = true;

    // Get first digit of street number
    int num1 = Integer.parseInt(parts[0])/10;
    int num2 = Integer.parseInt(parts[1].substring(0, 1));
    if (streetType)
    {
      block[0] = num1;
      block[1] = num2;
    }
    else
    {
      block[0] = num2;
      block[1] = num1;
    }
    return block;
  }
  
  // Calculates the distance in city blocks between from address and to address
  public static int getDistance(String from, String to)
  {
    int[] fromblock = {0, 0};
    int[] toblock   = {0, 0};

    if (validAddress(from) && validAddress(to))
    {
      fromblock = getCityBlock(from);
      toblock   = getCityBlock(to);
    }
    return Math.abs(toblock[0] - fromblock[0]) + Math.abs(toblock[1] - fromblock[1]) ;
  }

  public static int getCityZone(String address){
    if(!validAddress(address)){
      return -1;
    }
    int[] block = getCityBlock(address);

    
    int x = block[0];
    int y= block[1];
    

    // (Ex (2,3))
    /*
     * zone 0, 1, 2, 3. Zone 0 extends from 
     * 1st avenue to 5th avenue and 6th to 9th street. Zone 1 extends from 6th avenue to 9th
     * avenue and 6th to 9th street. Zone 2 extends from 6th avenue to 9th avenue and 1st
     * to 5th street. Zone 3 extends from 1st avenue to 5th avenue and 1st to 5th street. 
     */

    
    if ((x>=1 && x<=5) && (y>= 6 && y<= 9)){
      return 0;
    } else if(((x>=6 && x<=9) && (y>= 6 && y<= 9))){
      return 1;
    } else if((x>=6 && x<=9) && (y>= 1 && y<= 5)){
      return 2;
    } else if((x>=1 && x<=5) && (y>= 1 && y<= 5)){
      return 3;
    }
    return -1;
  }
}
