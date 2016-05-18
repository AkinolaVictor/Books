import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
* This demo program divides up a large computation into a fairly
* large number of smaller tasks.  The computation is to compute
* an image, and each task computes one row of pixels in the image.
* The tasks are placed into a thread-safe queue.  Several "worker" 
* threads remove tasks from the queue and carry them out.  When
* all the tasks have completed, the worker threads terminate.
* The number of worker threads is specified by the user.
* (The image is a small piece of the famous Mandelbrot set,
*  which is used just because it takes some time to compute.  
* There is no need to understand what the image means.)  
*/
public class MultiprocessingDemo2 extends JPanel {

   /**
    * This main routine just shows a panel of type MultiprocessingDemo2.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Multiprocessing Demo 2");
      MultiprocessingDemo2 content = new MultiprocessingDemo2();
      window.setContentPane(content);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.pack();
      window.setResizable(false);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth()) / 2,
            (screenSize.height - window.getHeight()) / 2 );
      window.setVisible(true);
   }
   
   
   private WorkerThread[] workers;  // the threads that compute the image
   
   private ConcurrentLinkedQueue<Runnable> taskQueue;  // holds individual tasks.
   
   private volatile int threadsCompleted; // how many tasks have finished?
   
   private volatile boolean running;  // used to signal the thread to abort
   
   private JButton startButton; // button the user can click to start or abort the thread
   
   private JComboBox threadCountSelect;  // for specifying the number of threads to be used
   
   private BufferedImage image; // contains the image that is computed by this program
   
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
             * painting.  These threads all synchronize on the image object,
             * although any object could be used.
             */
            synchronized(image) {
               g.drawImage(image,0,0,null);
            }
         }
      }
   };
   
   
   /**
    * Constructor creates a panel to hold the display, with a "Start" button 
    * and a pop-up menu for selecting the number of threads below it.
    */
   public MultiprocessingDemo2() {
      display.setPreferredSize(new Dimension(800,600));
      display.setBackground(Color.LIGHT_GRAY);
      setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      setLayout(new BorderLayout());
      add(display, BorderLayout.CENTER);
      JPanel bottom = new JPanel();
      startButton = new JButton("Start");
      bottom.add(startButton);
      threadCountSelect = new JComboBox();
      threadCountSelect.addItem("Use 1 thread.");
      threadCountSelect.addItem("Use 2 threads.");
      threadCountSelect.addItem("Use 3 threads.");
      threadCountSelect.addItem("Use 4 threads.");
      threadCountSelect.addItem("Use 5 threads.");
      threadCountSelect.setSelectedIndex(1);
      bottom.add(threadCountSelect);
      bottom.setBackground(Color.WHITE);
      add(bottom,BorderLayout.SOUTH);
      palette = new int[256];
      for (int i = 0; i < 256; i++)
         palette[i] = Color.getHSBColor(i/255F, 1, 1).getRGB();
      startButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (running)
               stop();
            else
               start();
         }
      });
   }
   
   
   /**
    * This method is called when the user clicks the Start button,
    * while no computation is in progress.  It starts as many new
    * threads as the user has specified.  It creates one 
    * MandelbrotTask object for each row of the image and places
    * all the tasks into a queue.  The threads will remove tasks
    * from the queue to process them.  The threads are run at lower
    * priority than the event-handling thread, in order to keep the
    * GUI responsive. 
    */
   private void start() {
      startButton.setText("Abort"); // change name while computation is in progress
      threadCountSelect.setEnabled(false); // will be re-enabled when all threads finish
      int width = display.getWidth() + 2;
      int height = display.getHeight() + 2;
      if (image == null)
         image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.getGraphics();  // fill image with gray
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(0,0,width,height);
      g.dispose();
      display.repaint();
      
      taskQueue = new ConcurrentLinkedQueue<Runnable>();
      
      double xmin = -1.6744096740931858;
      double xmax = -1.674409674093473;
      double ymin = 4.716540768697223E-5;
      double ymax = 4.716540790246652E-5;
      int maxIterations = 10000;
      double dx = (xmax-xmin)/(width-1);
      double dy = (ymax-ymin)/(height-1);
      for (int row = 0; row < height; row++) {
         double y = ymax - row*dy;
         MandelbrotTask task = new MandelbrotTask(row, width, maxIterations, xmin, y, dx);
         taskQueue.add(task);
      }
      
      int threadCount = threadCountSelect.getSelectedIndex() + 1;
      workers = new WorkerThread[threadCount];
      running = true;  // Set the signal before starting the threads!
      threadsCompleted = 0;  // Records how many of the threads have terminated.
      for (int i = 0; i < threadCount; i++) {
         workers[i] = new WorkerThread();
         try {
            workers[i].setPriority( Thread.currentThread().getPriority() - 1 );
         }
         catch (Exception e) {
         }
         workers[i].start();
      }
   }
   
   
   /**
    * This method is called when the user clicks the button while
    * a thread is running.  A signal is sent to the thread to terminate,
    * by setting the value of the signaling variable, running, to false.
    */
   private void stop() {
      startButton.setEnabled(false);  // will be re-enabled when all threads finish
      running = false;
   }
   
   
   /**
    * This method is called by each thread when it terminates.  We keep track
    * of the number of threads that have terminated, so that when they have
    * all finished, we can put the program into the correct state, such as
    * changing the name of the button to "Start Again" and re-enabling the
    * pop-up menu.
    */
   synchronized private void threadFinished() {
      threadsCompleted++;
      if (threadsCompleted == workers.length) { // all threads have finished
         startButton.setText("Start Again");
         startButton.setEnabled(true);
         running = false; // Make sure running is false after the thread ends.
         workers = null;
         threadCountSelect.setEnabled(true); // re-enable pop-up menu
      }
   }
   

   /**
    * An object of type MandelbrotTask represents the task of computing one row
    * of pixels in an image of the Mandelbrot set.  The task has a run() method
    * that does the actual computation and also applies the colors that it has
    * computed to the image on the screen.
    */
   private class MandelbrotTask implements Runnable {
      int rowNumber;  // Which row of pixels does this task compute?
      double xmin;    // The x-value for the first pixel in the row.
      double y;       // The y-value for all the pixels in the row.
      double dx;      // The change in x-value from one pixel to the next.
      int width;      // The number of pixels in the row.
      int maxIterations;  // The maximum count in the Mandelbrot algorithm.
      MandelbrotTask( int rowNumber, int width, int maxIterations, double xmin, double y, double dx) {
         this.rowNumber = rowNumber;
         this.maxIterations = maxIterations;
         this.xmin = xmin;
         this.y = y;
         this.dx = dx;
         this.width = width;
      }
      public void run() {
         int[] rgb= new int[width];     // The colors computed for the pixels.
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
         synchronized(image) {
            /* Add the newly computed row of pixel colors to the image.  This is
             * synchronized because this thread and the thread that paints the
             * display might both try to access the image simultaneously.
             */
            image.setRGB(0,rowNumber, width, 1, rgb, 0, width);
         }
         display.repaint(0,rowNumber,width,1); // Repaint just the newly computed row.
      }
   }
   

   /**
    * This class defines the worker threads that carry out the tasks. 
    * A WorkerThread runs in a loop in which it retrieves a task from the 
    * taskQueue and calls the run() method in that task.  The thread 
    * terminates when the queue is empty.  (Note that for this to work 
    * properly, all the tasks must be placed into the queue before the
    * thread is started.  If the queue is empty when the thread starts,
    * the thread will simply exit immediately.)  The thread also terminates
    * if the signal variable, running, is set to false.  Just before it
    * terminates, the thread calls the threadFinished() method.
    */
   private class WorkerThread extends Thread {
      public void run() {
         try {
            while (running) {
               Runnable task = taskQueue.poll();
               if (task == null)
                  break;
               task.run();
            }
         }
         finally {
            threadFinished();
         }
      }
   }
   
   
}
