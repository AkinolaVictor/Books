/**  
 * This program prints out a 3N+1 sequence starting from a positive 
 * integer specified by the user.  It also counts the number of 
 * terms in the sequence, and prints out that number.
 */
 public class ThreeN1 {
 
      public static void main(String[] args) {                
        
        int N;       // for computing terms in the sequence
        int counter; // for counting the terms
        
        TextIO.put("Starting point for sequence: ");
        N = TextIO.getlnInt();
        while (N <= 0) {
           TextIO.put("The starting point must be positive. "
                              + " Please try again: ");
           N = TextIO.getlnInt();
        }
        // At this point, we know that N &gt; 0
        
        counter = 0;
        while (N != 1) {
            if (N % 2 == 0)
               N = N / 2;
            else
               N = 3 * N + 1;
            TextIO.putln(N);
            counter = counter + 1;
        }
        
        TextIO.putln();
        TextIO.put("There were ");
        TextIO.put(counter);
        TextIO.putln(" terms in the sequence.");
                           
     }  // end of main()
 
 }  // end of class ThreeN1
 