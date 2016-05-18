import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.awt.image.BufferedImage;

/**
 * This demo program uses several threads to compute an image "in the background".
 * It is a modification of the BackgroundComputationDemo, which used only one thread.
 * 
 * As rows of pixels in the image are computed, they are copied to the
 * screen.  (The image is a small piece of the famous Mandelbrot set, which
 * is used just because it takes some time to compute.  There is no need
 * to understand what the image means.)  The user starts the computation by
 * clicking a "Start" button.  A pop-up menu allows the user to select the
 * number of threads to be used.  The specified number of threads is created 
 * and each thread is assigned a region in the image.  The threads are run
 * at lower priority, which will make sure that the GUI thread will get a
 * chance to run to repaint the display as necessary.
 */
public class MultiprocessingDemo1 extends JPanel {

   /**
    * This main routine just shows a panel of type MultiprocessingDemo1.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Multiprocessing Demo 1");
      MultiprocessingDemo1 content = new MultiprocessingDemo1();
      window.setContentPane(content);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.pack();
      window.setResizable(false);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth()) / 2,
            (screenSize.height - window.getHeight()) / 2 );
      window.setVisible(true);
   }
   
   
   private Runner[] workers;  // the threads that compute the image
   
   private volatile boolean running;  // used to signal the thread to abort
   
   private volatile int threadsCompleted; // how many threads have finished running?
   
   private JButton startButton; // button the user can click to start or abort the thread
   
   private JComboBox threadCountSelect;  // for specifying the number of threads to be used
   
   private BufferedImage image; // contains the image that is computed by this program

   
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
             * there are two threads that compete for access to the image:
             * the thread that computes the image and the thread that does the
             * painting.  The two threads both synchronize on the image object,
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
   public MultiprocessingDemo1() {
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
    * threads as the user has specified, and assigns a different part
    * of the image to each thread.  The threads are run at lower
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
      int threadCount = threadCountSelect.getSelectedIndex() + 1;
      workers = new Runner[threadCount];
      int rowsPerThread;  // How many rows of pixels should each thread compute?
      rowsPerThread  = height / threadCount;
      running = true;  // Set the signal before starting the threads!
      threadsCompleted = 0;  // Records how many of the threads have terminated.
      for (int i = 0; i < threadCount; i++) {
         int startRow;  // first row computed by thread number i
         int endRow;    // last row computed by thread number i
            // Create and start a thread to compute the rows of the image from
            // startRow to endRow.  Note that we have to make sure that
            // the endRow for the last thread is the bottom row of the image.
         startRow = rowsPerThread*i;
         if (i == threadCount-1)
            endRow = height-1;
         else
            endRow = rowsPerThread*(i+1) - 1;
         workers[i] = new Runner(startRow, endRow);
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
    * This class defines the thread that does the computation.  The
    * run method computes the image one pixel at a time.  After computing
    * the colors for each row of pixels, the colors are copied into the
    * image, and the part of the displays that shows that row is repainted.
    * (Since the thread runs in the background, at lower priority than
    * the event-handling thread, the event-handling thread wakes up
    * immediately to repaint the display.)
    */
   private class Runner extends Thread {
      double xmin, xmax, ymin, ymax;
      int maxIterations;
      int[] rgb;
      int[] palette;
      int width, height;
      int startRow, endRow;
      Runner(int startRow, int endRow) {
         this.startRow = startRow;
         this.endRow = endRow;
         width = image.getWidth();
         height = image.getHeight();
         rgb = new int[width];
         palette = new int[256];
         for (int i = 0; i < 256; i++)
            palette[i] = Color.getHSBColor(i/255F, 1, 1).getRGB();
         xmin = -1.6744096740931858;
         xmax = -1.674409674093473;
         ymin = 4.716540768697223E-5;
         ymax = 4.716540790246652E-5;
         maxIterations = 10000;
      }
      public void run() {
         try {
            double x, y;
            double dx, dy;
            dx = (xmax-xmin)/(width-1);
            dy = (ymax-ymin)/(height-1);
            for (int row = startRow; row <= endRow; row++) {  // Compute one row of pixels.
               y = ymax - dy*row;
               for (int col = 0; col < width; col++) {
                  x = xmin + dx*col;
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
                     rgb[col] = 0;
                  else
                     rgb[col] = palette[count%palette.length];
               }
               if (! running) {  // Check for the signal to abort the computation.
                  return;
               }
               synchronized(image) {
                  /* Add the newly computed row of pixel colors to the image.  This is
                   * synchronized because this thread and the thread that paints the
                   * display might both try to access the image simultaneously.
                   */
                  image.setRGB(0,row, width, 1, rgb, 0, width);
               }
               display.repaint(0,row,width,1); // Repaint just the newly computed row.
            }
         }
         finally {
            threadFinished(); // make sure this is called when the thread finishes for any reason.
         }
      }
   }
   
}
