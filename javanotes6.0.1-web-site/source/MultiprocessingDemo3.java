import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.LinkedBlockingQueue;

/**
* This demo program divides up a large computation into a fairly
* large number of smaller tasks.  The computation is to compute
* an image, and each task computes one row of pixels in the image.
* 
* A thread pool is created at the beginning of the program, with
* one thread for each available processor.  The threads remove
* tasks from a blocking queue and execute them.  The threads never
* terminate (until the program ends).  To start a computation, tasks
* are created and added to the blocking queue.  As soon as the 
* first tasks are added to the queue, the threads "wake up" and
* start working on them.
* 
* (The image is a small piece of the famous Mandelbrot set,
*  which is used just because it takes some time to compute.  
* There is no need to understand what the image means.)  
*/
public class MultiprocessingDemo3 extends JPanel {

   /**
    * This main routine just shows a panel of type MultiprocessingDemo2.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Multiprocessing Demo 3");
      MultiprocessingDemo3 content = new MultiprocessingDemo3();
      window.setContentPane(content);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.pack();
      window.setResizable(false);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth()) / 2,
            (screenSize.height - window.getHeight()) / 2 );
      window.setVisible(true);
   }
   
   
   private WorkerThread[] workers;  // The thread pool.  Note:  The threads are created in
                                    // the start() method, the first time the user clicks the
                                    // "Start" button.  They continue to exist until the
                                    // program ends.
   
   private LinkedBlockingQueue<Runnable> taskQueue;  // The queue that holds individual tasks;
   
   private volatile int jobNumber;  // Job number of the current computation job.
                                    // This is incremented after the job has completed.
                                    // Note that any left-over tasks from a job are ignored
                                    // when the finally complete.  This can happen when the
                                    // user aborts a computation, since if a thread is working
                                    // on a task when that happens, it will continue to run
                                    // that task until the task completes, so the task can
                                    // complete after the job of which it is a part has been
                                    // aborted.  (Note:  The task itself could check the
                                    // job number as it is running and terminate early if the
                                    // job number has changed.  This would make sense if the
                                    // task's computation were very long.)

   private int taskCount;  // The number of tasks that make up one computation job.
   private volatile int tasksCompleted; // How many tasks in the current job have finished?
   
   private volatile boolean jobInProgress; // Set to true when a job starts, false when it ends.
   
   private JButton startButton; // Button the user can click to start or abort the thread.
   private BufferedImage image; // Contains the image that is computed by this program.
   int[] palette;  // Holds a spectrum of RGB color values; used in computing pixel colors.

   
   /**
    * The display is a JPanel that shows the image.  The part of the image that has
    * not yet been computed is gray.  If the image has not yet been created, the
    * entire display is filled with gray.
    */
   private JPanel display = new JPanel() {
      protected void paintComponent(Graphics g) {
         if (image == null)
            super.paintComponent(g);  // fill with background color, gray
         else {
            /* Copy the image onto the display.  This is synchronized because
             * there are several threads that compete for access to the image:
             * the threads that compute the image and the thread that does the
             * painting.  All synchronization is done on the main class object,
             * referred to here as MultiprocessingDemo3.this.
             */
            synchronized(MultiprocessingDemo3.this) {
               g.drawImage(image,0,0,null);
            }
         }
      }
   };
   
   
   /**
    * Constructor creates a panel to hold the display, with a "Start" button 
    * and a pop-up menu for selecting the number of threads below it.
    */
   public MultiprocessingDemo3() {
      display.setPreferredSize(new Dimension(800,600));
      display.setBackground(Color.LIGHT_GRAY);
      setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      setLayout(new BorderLayout());
      add(display, BorderLayout.CENTER);
      JPanel bottom = new JPanel();
      startButton = new JButton("Start");
      bottom.add(startButton);
      bottom.setBackground(Color.WHITE);
      add(bottom,BorderLayout.SOUTH);
      palette = new int[256];
      for (int i = 0; i < 256; i++)
         palette[i] = Color.getHSBColor(i/255F, 1, 1).getRGB();
      startButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (jobInProgress)
               stop();
            else
               start();
         }
      });
      taskQueue = new LinkedBlockingQueue<Runnable>();
   }
   
   
   /**
    * This method is called when the user clicks the Start button,
    * while no computation is in progress.  It clears the image
    * and sets up the computation of a new image.  The first time
    * that it is called, it is also responsible for creating the
    * image and the thread pool.
    */
   synchronized private void start() {
      startButton.setText("Abort"); // change name while computation is in progress
      int width = display.getWidth() + 2;
      int height = display.getHeight() + 2;
      if (image == null) { // create the image and the thread pool
         image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
         int processors = Runtime.getRuntime().availableProcessors();
         workers = new WorkerThread[processors];
         for (int i = 0; i < processors; i++) {
            workers[i] = new WorkerThread();
         }
      }
      Graphics g = image.getGraphics();  // fill image with gray
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(0,0,width,height);
      g.dispose();
      display.repaint();
      
      double xmin = -1.6744096740931858;
      double xmax = -1.674409674093473;
      double ymin = 4.716540768697223E-5;
      double ymax = 4.716540790246652E-5;
      int maxIterations = 10000;
      double dx = (xmax-xmin)/(width-1);
      double dy = (ymax-ymin)/(height-1);
      for (int row = 0; row < height; row++) { // Add tasks for current job to job queue.
         double y = ymax - row*dy;
         MandelbrotTask task = new MandelbrotTask(jobNumber, row, width, maxIterations, xmin, y, dx);
         taskQueue.add(task);
      }
      tasksCompleted = 0;
      taskCount = height;
      jobInProgress = true;
   }
   
   
   /**
    * This method is called when the user clicks the button while
    * a thread is running.  It is also called by the taskFinished()
    * method when all the tasks that make up a job have been
    * completed.  The responsibility of this method is to
    * finish the current job by incrementing the jobNumber and
    * discarding any tasks from the current job that are still in
    * the queue (in case the job is ending because the user has
    * aborted it before it finished).
    */
   synchronized private void stop() {
      startButton.setText("Start Again");
      taskQueue.clear();
      jobNumber++;
      jobInProgress = false;
   }
   
   
   /**
    * This method is called by each thread when it terminates.  We keep track
    * of the number of threads that have terminated, so that when they have
    * all finished, we can put the program into the correct state, such as
    * changing the name of the button to "Start Again" and re-enabling the
    * pop-up menu.
    */
   synchronized private void taskFinished(MandelbrotTask task) {
      if (task.jobNumber != jobNumber) {
         System.out.println("Dropping results from previous job."); // for testing
         return;
      }
      tasksCompleted++;
      image.setRGB(0,task.rowNumber, task.width, 1, task.rgb, 0, task.width);
      display.repaint(0,task.rowNumber,task.width,1); // Repaint just the newly computed row.
      if (tasksCompleted == taskCount) { // all threads have finished
         stop();
      }
   }
   

   /**
    * An object of type MandelbrotTask represents the task of computing one row
    * of pixels in an image of the Mandelbrot set.  The task has a run() method
    * that does the actual computation.  It also calls the taskFinished() method
    * before terminating.
    */
   private class MandelbrotTask implements Runnable {
      int jobNumber;  // Which job is this task part of?
      int rowNumber;  // Which row of pixels does this task compute?
      double xmin;    // The x-value for the first pixel in the row.
      double y;       // The y-value for all the pixels in the row.
      double dx;      // The change in x-value from one pixel to the next.
      int width;      // The number of pixels in the row.
      int maxIterations;  // The maximum count in the Mandelbrot algorithm.
      int[] rgb;      // The pixel colors computed by this task;
      MandelbrotTask( int jobNumber, int rowNumber, int width, int maxIterations, double xmin, double y, double dx) {
         this.jobNumber = jobNumber;
         this.rowNumber = rowNumber;
         this.maxIterations = maxIterations;
         this.xmin = xmin;
         this.y = y;
         this.dx = dx;
         this.width = width;
      }
      public void run() {
         rgb= new int[width];     // The colors computed for the pixels.
         for (int i = 0; i < rgb.length; i++) {
            double x = xmin + i * dx;
            int count = 0;
            double xx = x;
            double yy = y;
            while (count < maxIterations && (xx*xx + yy*yy) < 4) {
               count++;
               double newxx = xx*xx - yy*yy + x;
               yy = 2*xx*yy + y;
               xx = newxx; 
            }
            if (count == maxIterations)
               rgb[i] = 0;
            else
               rgb[i] = palette[count % 256];
         }
         taskFinished(this);
      }
   }
   

   /**
    * This class defines the worker threads that make up the thread pool.
    * A WorkerThread runs in a loop in which it retrieves a task from the 
    * taskQueue and calls the run() method in that task.  Note that if
    * the queue is empty, the thread blocks until a task becomes available
    * in the queue.  The constructor starts the thread, so there is no
    * need for the main program to do so.  The thread will run at a priority
    * that is one less than the priority of the thread that calls the
    * constructor.
    * 
    * A WorkerThread is designed to run in an infinite loop.  It will
    * end only when the Java virtual machine exits. (This assumes that
    * the tasks that are executed don't throw exceptions, which is true
    * in this program.)  The constructor sets the thread to run as
    * a daemon thread; the Java virtual machine will exit when the
    * only threads are daemon threads.  (In this program, this is not
    * necessary since the virtual machine is set to exit when the
    * window is closed.  In a multi-window program, however, that would
    * not be the case and it would be important for the threads to be
    * daemon threads.)
    */
   private class WorkerThread extends Thread {
      WorkerThread() {
         try {
            setPriority( Thread.currentThread().getPriority() - 1);
         }
         catch (Exception e) {
         }
         try {
            setDaemon(true);
         }
         catch (Exception e) {
         }
         start();
      }
      public void run() {
         while (true) {
            try {
               Runnable task = taskQueue.take();
               task.run();
            }
            catch (InterruptedException e) {
            }
         }
      }
   }
   
   
}
