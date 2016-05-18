/**
 * This class implements a simple program that will compute
 * the amount of interest that is earned on an investment over
 * a period of one year.  The initial amount of the investment
 * and the interest rate are input by the user.  The value of
 * the investment at the end of the year is output.  The
 * rate must be input as a decimal, not a percentage (for
 * example, 0.05 rather than 5).
 *
 * This is a version of the Interest2 program that uses a Scanner
 * rather than TextIO for input.
 */

import java.util.Scanner;  // Make the Scanner class available.

public class Interest2WithScanner {
   
   public static void main(String[] args) {
      
      Scanner stdin = new Scanner( System.in );  // Create the Scanner.
      
      double principal;  // The value of the investment.
      double rate;       // The annual interest rate.
      double interest;   // The interest earned during the year.
      
      System.out.print("Enter the initial investment: ");
      principal = stdin.nextDouble();
      
      System.out.print("Enter the annual interest rate (decimal, not percentage!): ");
      rate = stdin.nextDouble();
      
      interest = principal * rate;       // Compute this year's interest.
      principal = principal + interest;  // Add it to principal.
      
      System.out.print("The value of the investment after one year is $");
      System.out.println(principal);
      
   } // end of main()
   
} // end of class Interest2With Scanner
