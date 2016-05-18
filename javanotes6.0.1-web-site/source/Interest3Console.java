
/**
 * This is an example of simulating a TextIO program in an applet.
 * The applet is defined as a subclass of TextIOApplet, and the
 * main() routine of the program that is to be simulated becomes the
 * runProgram() method in this class.   Any other static members of
 * the program class would become NON-STATIC members of this class.
 * When the identifiers TextIO and System appear, they do not have
 * their usual meaning.  Instead, they perform I/O operations in
 * the input/output area of the applet.
 */
public class Interest3Console extends TextIOApplet {
   
   protected String getDefaultAppletTitle() {
      return "Sample program \"Interest3\"";
   }

   protected void runProgram() {

        /*
            This implements a simple program that will compute the amount of 
            interest that is earned on an investment over a period of 5 years.  The 
            initial amount of the investment and the interest rate are input by the 
            user.  The value of the investment at the end of each year is output.
         */

         double principal;  // The value of the investment.
         double rate;       // The annual interest rate.
         
         /* Get the initial investment and interest rate from the user. */
         
         TextIO.put("Enter the initial investment: ");
         principal = TextIO.getlnDouble();
         
         TextIO.putln();
         TextIO.putln("Enter the annual interest rate.");
         TextIO.put("Enter a decimal, not a percentage: ");
         rate = TextIO.getlnDouble();
         TextIO.putln();

         /* Simulate the investment for 5 years. */
         
         int years;  // Counts the number of years that have passed.
         
         years = 0;
         while (years < 5) {
            double interest;  // Interest for this year.
            interest = principal * rate;
            principal = principal + interest;     // Add it to principal.
            years = years + 1;    // Count the current year.
            System.out.print("The value of the investment after ");
            System.out.print(years);
            System.out.print(" years is $");
            System.out.printf("%1.2f", principal);
            System.out.println();
         } // end of while loop

   }

}
