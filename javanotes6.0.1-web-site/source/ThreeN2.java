/**
 * A program that computes and displays severl 3N+1 sequences.  Starting
 * values for the sequences are input by the user.  Terms in a sequence 
 * are printed in columns, with five terms on each line of output.
 * After a sequence has been displayed, the number of terms in that 
 * sequence is reported to the user.
 */

public class ThreeN2 {
          
   
   public static void main(String[] args) {

      TextIO.putln("This program will print out 3N+1 sequences");
      TextIO.putln("for starting values that you specify.");
      TextIO.putln();
      
      int K;   // Starting point for sequence, specified by the user.
      do {
         TextIO.putln("Enter a starting value;");
         TextIO.put("To end the program, enter 0: ");
         K = TextIO.getInt();   // get starting value from user
         if (K > 0)             // print sequence, but only if K is > 0
            print3NSequence(K);
      } while (K > 0);          // continue only if K &gt; 0
 
   } // end main
 

   /**
    * print3NSequence prints a 3N+1 sequence to standard output, using
    * startingValue as the initial value of N.  It also prints the number 
    * of terms in the sequence. The value of the parameter, startingValue, 
    * must  be a positive integer.
    */
   static void print3NSequence(int startingValue) {
  
      int N;       // One of the terms in the sequence.
      int count;   // The number of terms found.
      int onLine;  // The number of terms that have been output
                   //     so far on the current line.
      
      N = startingValue;   // Start the sequence with startingValue;
      count = 1;           // We have one term so far.
   
      TextIO.putln("The 3N+1 sequence starting from " + N);
      TextIO.putln();
      TextIO.put(N, 8);  // Print initial term, using 8 characters.
      onLine = 1;        // There's now 1 term on current output line.
   
      while (N > 1) {
          N = nextN(N);  // compute next term
          count++;   // count this term
          if (onLine == 5) {  // If current output line is full
             TextIO.putln();  // ...then output a carriage return
             onLine = 0;      // ...and note that there are no terms 
                              //               on the new line.
          }
          TextIO.putf("%8d", N);  // Print this term in an 8-char column.
          onLine++;   // Add 1 to the number of terms on this line.
      }
   
      TextIO.putln();  // end current line of output
      TextIO.putln();  // and then add a blank line
      TextIO.putln("There were " + count + " terms in the sequence.");
   
   }  // end of Print3NSequence
   
   
   /**
    * nextN computes and returns the next term in a 3N+1 sequence,
    * given that the current term is currentN.
    */
   static int nextN(int currentN) {
       if (currentN % 2 == 1)
          return 3 * currentN + 1;
       else
          return currentN / 2;
   }  // end of nextN()
   
} // end of class ThreeN2