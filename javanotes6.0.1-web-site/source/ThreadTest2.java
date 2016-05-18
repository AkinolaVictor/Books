/**
 * This program counts the number of prime integers between 3000001 and 6000000.
 * The work is divided among one to five threads.  The number of threads is
 * chosen by the user.
 */
public class ThreadTest2 {
   
   /**
    * The starting point for the range of integers that are tested for primality.
    * The range is from (start+1) to (2*start).  Note the value of start is chosen
    * to be divisible by 2, 3, 4, and 5 to make it easy to divide up the range
    * among the threads.
    */
   private static final int START = 3000000;
   
   /**
    * The total number of primes found.  Each thread counts the number of primes in
    * a different range of integers.  After it finishes counting, it adds its count
    * to the total.
    */
   private static int total;
   
   /**
    * Adds x to total.  This method is synchronized so that it can be safely used by
    * several different threads.
    */
   synchronized private static void addToTotal(int x) {
      total = total + x;
      System.out.println(total + " primes found so far.");
   }
   
   /**
    * A Thread belonging to this class will count primes in a specified range
    * of integers.  The range is from min to max, inclusive, where min and max
    * are given as parameters to the constructor.  After counting, the thread
    * outputs a message about the number of primes that it has found, and it
    * adds its count to the overall total by calling the addToTotal(int) method.
    */
   private static class CountPrimesThread extends Thread {
      int count = 0;
      int min, max;
      public CountPrimesThread(int min, int max) {
         this.min = min;
         this.max = max;
      }
      public void run() {
         count = countPrimes(min,max);
         System.out.println("There are " + count + 
                                 " primes between " + min + " and " + max);
         addToTotal(count);
      }
   }
   
   /**
    * Counts the primes in the range from (START+1) to (2*START), using a specified number
    * of threads.  The total elapsed time is printed.
    * @param numberOfThreads
    */
   private static void countPrimesWithThreads(int numberOfThreads) {
      int increment = START/numberOfThreads;
      System.out.println("\nCounting primes between " + (START+1) + " and " 
            + (2*START) + " using " + numberOfThreads + " threads...\n");
      long startTime = System.currentTimeMillis();
      CountPrimesThread[] worker = new CountPrimesThread[numberOfThreads];
      for (int i = 0; i < numberOfThreads; i++)
         worker[i] = new CountPrimesThread(  START+i*increment+1, START+(i+1)*increment );
      total = 0;
      for (int i = 0; i < numberOfThreads; i++)
         worker[i].start();
      for (int i = 0; i < numberOfThreads; i++) {
         while (worker[i].isAlive()) {
            try {
               worker[i].join();
            }
            catch (InterruptedException e) {
            }
         }
      }
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println("\nThe number of primes is " + total + ".");
      System.out.println("\nTotal elapsed time:  " + (elapsedTime/1000.0) + " seconds.\n");
   }
   
   /**
    * Gets the number of threads from the user and counts primes using that many threads.
    */
   public static void main(String[] args) {
      int processors = Runtime.getRuntime().availableProcessors();
      if (processors == 1)
         System.out.println("Your computer has only 1 available processor.\n");
      else
         System.out.println("Your computer has " + processors + " available processors.\n");
      int numberOfThreads = 0;
      while (numberOfThreads < 1 || numberOfThreads > 5) {
         System.out.print("How many threads do you want to use  (from 1 to 5) ?  ");
         numberOfThreads = TextIO.getlnInt();
         if (numberOfThreads < 1 || numberOfThreads > 5)
            System.out.println("Please enter 1, 2, 3, 4, or 5 !");
      }
      countPrimesWithThreads(numberOfThreads);
   }
   
   /**
    * Count the primes between min and max, inclusive.
    */
   private static int countPrimes(int min, int max) {
      int count = 0;
      for (int i = min; i <= max; i++)
         if (isPrime(i))
            count++;
      return count;
   }
   
   /**
    * Test whether x is a prime number.
    * x is assumed to be greater than 1.
    */
   private static boolean isPrime(int x) {
      int top = (int)Math.sqrt(x);
      for (int i = 2; i <= top; i++)
         if ( x % i == 0 )
            return false;
      return true;
   }
   
}
