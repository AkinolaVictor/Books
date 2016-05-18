/** 
 * This program will convert measurements expressed in inches,
 * feet, yards, or miles into each of the possible units of
 * measure.  The measurement is input by the user, followed by
 * the unit of measure.  For example:  "17 feet", "1 inch",
 * "2.73 mi".  Several measurements can be combined on
 * one line of input.  For example:  "2 miles 5 yards 1 inch".
 * In this case, the measurements are combined into one total.
 * Abbreviations in, ft, yd, and mi are accepted.  Negative 
 * measurements are not allowed.
 * 
 * The program will continue to read and convert measurements
 * until the user enters an empty line.
 */
public class LengthConverter2 {

    public static void main(String[] args) {
      
      double inches, feet, yards, miles;  // Measurement expressed in each
                                          //   possible unit of measure.
      
      System.out.println("Enter measurements in inches, feet, yards, or miles.");
      System.out.println("For example:  1 inch   17 feet   2.73 miles");
      System.out.println("You can use abbreviations:   in   ft  yd   mi");
      System.out.println("You can combine measurements, as in:  1 mile 270 yards 17 inches");
      System.out.println("I will convert your input into each of the four units of measure.");
      
      while (true) {
         
         /* Get the user's input, and convert it to inches. */
         
         System.out.println();
         System.out.println();
         System.out.println("Enter your measurement, or press return to end:");
         
         skipBlanks();  
         
         if (TextIO.peek() == '\n')  // End if there is nothing on the line.
            break;
            
         inches = readMeasurement();  // If value is < 0, then user's input was illegal.
         
         if (inches >= 0) {
            
            /* Convert the measurement in inches to feet, yards, and miles. */
         
            feet = inches / 12;
            yards = inches / 36;
            miles = inches / (12*5280);
          
            /* Output the measurement in terms of each unit of measure. */
         
            System.out.println();
            System.out.println("That's equivalent to:");
            System.out.printf("%12.5g", inches);
            System.out.println(" inches");
            System.out.printf("%12.5g", feet);
            System.out.println(" feet");
            System.out.printf("%12.5g", yards);
            System.out.println(" yards");
            System.out.printf("%12.5g", miles);
            System.out.println(" miles");
            
         } // end else
         
         TextIO.getln();  // Discard the rest of the input line before
                          // getting the next line.
      
      } // end while
      
      System.out.println();
      System.out.println("OK!  Bye for now.");
      
   } // end main()
    
   
   /**
    * Reads past any blanks and tabs in the input.
    * Postcondition:  The next character in the input is an
    *                 end-of-line or a non-blank character.
    */
   static void skipBlanks() {
      char ch;
      ch = TextIO.peek();
      while (ch == ' ' || ch == '\t') {
         ch = TextIO.getAnyChar();
         ch = TextIO.peek();
      }
   }
   

   /**
    * Reads the user's input measurement from one line of input.
    * Precondition:  The input line is not empty.
    * Postcondition:  If the user's input is legal, the measurement
    *                 is converted to inches and returned.  If the
    *                 input is not legal, the value -1 is returned.
    *                 The end-of-line is NOT read by this routine.
    */
   static double readMeasurement() {

      double inches;  // Total number of inches in user's measurement.
      
      double measurement;  // One measurement, such as the 12 in "12 miles"
      String units;   // The units specified for the measurement, such as "miles"
      
      char ch;  // Used to peek at next character in the user's input.

      inches = 0;  // No inches have yet been read.

      skipBlanks();
      ch = TextIO.peek();
      
      /* As long as there is more input on the line, read a measurement and
         add the equivalent number of inches to the variable, inches.  If an
         error is detected during the loop, end the subroutine immediately
         by returning -1. */

      while (ch != '\n') {
      
          /* Get the next measurement and the units.  Before reading
             anything, make sure that a legal value is there to read. */
      
          if ( ! Character.isDigit(ch) ) {
              TextIO.putln("Error:  Expected to find a number, but found " + ch);
              return -1;
          }
          measurement = TextIO.getDouble();
          skipBlanks();
          if (TextIO.peek() == '\n') {
              TextIO.putln("Error:  Missing unit of measure at end of line.");
              return -1;
          }
          units = TextIO.getWord();
          units = units.toLowerCase();
          
          /* Convert the measurement to inches and add it to the total. */
          
          if (units.equals("inch") || units.equals("inches") || units.equals("in"))
              inches += measurement;
          else if (units.equals("foot") || units.equals("feet") || units.equals("ft"))
              inches += measurement * 12;
          else if (units.equals("yard") || units.equals("yards") || units.equals("yd"))
              inches += measurement * 36;
          else if (units.equals("mile") || units.equals("miles") || units.equals("mi"))
              inches += measurement * 12 * 5280;
          else {
              TextIO.putln("Error: \"" + units + "\" is not a legal unit of measure.");
              return -1;
          }
        
          /* Look ahead to see whether the next thing on the line is the end-of-line. */
         
          skipBlanks();
          ch = TextIO.peek();
          
      }  // end while
      
      return inches;
      
   } // end readMeasurement()
   

} // end class LengthConverter
