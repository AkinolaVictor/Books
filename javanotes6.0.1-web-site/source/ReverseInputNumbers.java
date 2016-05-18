/**
 * This program reads some positive integers from the user and
 * then prints them in reverse order.  The numbers are stored
 * in an array.
 */
 public class ReverseInputNumbers {

   public static void main(String[] args) {
   
     int[] numbers;  // An array for storing the input values.
     int numCount;   // The number of numbers saved in the array.
     int num;        // One of the numbers input by the user.
     
     numbers = new int[100];   // Space for 100 ints.
     numCount = 0;             // No numbers have been saved yet.
     
     TextIO.putln("Enter up to 100 positive integers; enter 0 to end.");
     
     while (true) {   // Get the numbers and put them in the array.
        TextIO.put("? ");
        num = TextIO.getlnInt();
        if (num <= 0)
           break;
        numbers[numCount] = num;
        numCount++;
     }
     
     TextIO.putln("\nYour numbers in reverse order are:\n");
     
     for (int i = numCount - 1; i >= 0; i--) {
         TextIO.putln( numbers[i] );
     }
     
   } // end main();
   
}  // end class ReverseInputNumbers
