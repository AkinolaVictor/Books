/**
 * This program reads some positive integers from the user and
 * then prints them in reverse order.  It uses the non-built-in
 * class DynamincArrayOfInt to store the numbers, so that there is
 * no preset limit on the number of numbers that the program can
 * handle.  Note that the DynamicArrayOfInt class, for demonstration
 * purposes, will print a message every time the size of the array
 * that it uses increases.  You will see these messages when you
 * run the program.
 */
public class ReverseWithDynamicArray {

   public static void main(String[] args) {
  
      DynamicArrayOfInt numbers;  // To hold the input numbers.
      int numCount;  // The number of numbers stored in the array.
      int num;    // One of the numbers input by the user.
    
      numbers = new DynamicArrayOfInt();
      numCount = 0;
    
      TextIO.putln("Enter some positive integers; Enter 0 to end");
      while (true) {  // Get numbers and put them in the dynamic array.
         TextIO.put("? ");
         num = TextIO.getlnInt();
         if (num <= 0)
            break;
         numbers.put(numCount, num);  // Store num in the dynamic array.
         numCount++;
      }
    
      TextIO.putln("\nYour numbers in reverse order are:\n");
    
      for (int i = numCount - 1; i >= 0; i--) {
          TextIO.putln( numbers.get(i) );  // Print the i-th number.
      }
    
   } // end main();
  
}  // end class ReverseWithDynamicArray