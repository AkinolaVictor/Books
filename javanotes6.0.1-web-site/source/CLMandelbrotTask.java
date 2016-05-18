
/**
 * This class is part of a part of a demonstration of distributed computing.
 * It is to be used with CLMandelbrotWorker.java and CLMandelbrotMaster.java.
 * This class must be present on both the master computer (along with
 * CLMandelbrotMaster) and on the worker computers.
 * 
 * CLMandelbrotTask is a simple container that holds the data for one "task"
 * which consists of computing one row of data for a Mandelbrot image.  It
 * also contains the output of the task, and a method for doing the computation.
 * CLMandelbrotMaster computes the image by creating one CLMandelbrotTask for
 * each row in the image.  The tasks are sent over a network to CLMandelbrotWorkers
 * to be computed, and the results are returned to CLMandelbrotMaster where
 * all the results are combined to produce the entire image.
 */
public class CLMandelbrotTask {

   public int id;             // Identifies this task.  Each task that is 
                              // part of the overall computation has a
                              // different id.
   
   public int maxIterations;  // Input for the computation.
   public double y;    
   public double xmin;
   public double dx;
   public int count;
   
   public int[] results;      // Holds the results of the computation after
                              //  compute() has been executed.
   

   /**
    * Performs the task represented by this data.  Used the values
    * of maxIterations, y, xmin, dx, and count.  Creates the result
    * array and fills it with computed data.  For the purposes of
    * this demonstration, it is not important to understand the
    * computation performed by this task.
    */
   public void compute() {
      results = new int[count];
      for (int i = 0; i < count; i++)
         results[i] = countIterations(xmin + i*dx,y);
   }
   
   
   /**
    * Called by compute() to compute each entry in the results array.
    */
   private int countIterations(double startx, double starty) {
      int ct = 0;
      double x = startx;
      double y = starty;
      while (ct < maxIterations && x*x + y*y < 5) {
             double new_x = x*x - y*y + startx;
             y = 2*x*y + starty;
             x = new_x;
             ct++;
      }
      return ct;
   }

}
