import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Reads numbers from a file named data.dat and writes them to a file
 * named result.dat in reverse order.  The input file should contain
 * exactly one real number per line.
 */
public class ReverseFileWithScanner {

   public static void main(String[] args) {

      Scanner data;     // Character input stream for reading data.
      PrintWriter result;  // Character output stream for writing data.

      ArrayList<Double> numbers;  // An ArrayList for holding the data.

      numbers = new ArrayList<Double>();

      try {  // Create the input stream.
         data = new Scanner(new File("data.dat"));
      }
      catch (FileNotFoundException e) {
         System.out.println("Can't find file data.dat!");
         return;  // End the program by returning from main().
      }

      try {  // Create the output stream.
         result = new PrintWriter(new FileWriter("result.dat"));
      }
      catch (IOException e) {
         System.out.println("Can't open file result.dat!");
         System.out.println("Error: " + e);
         data.close();  // Close the input file.
         return;        // End the program.
      }

      while ( data.hasNextDouble() ) {  // Read until end-of-file.
         double inputNumber = data.nextDouble();
         numbers.add( inputNumber );
      }

      // Output the numbers in reverse order.

      for (int i = numbers.size()-1; i >= 0; i--)
         result.println(numbers.get(i));

      System.out.println("Done!");

      data.close();
      result.close();

   }  // end of main()

} // end of class