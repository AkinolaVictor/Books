import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.awt.image.BufferedImage;

/**
 * This demo program uses a thread to compute an image "in the background".
 * As rows of pixels in the image are computed, they are copied to the
 * screen.  (The image is a small piece of the famous Mandelbrot set, which
 * is used just because it takes some time to compute.  There is no need
 * to understand what the image means.)  The user starts the computation by
 * clicking a "Start" button.  A separate thread is created and is run at
 * a lower priority, which will make sure that the GUI thread will get a
 * chance to run to repaint the display as necessary.
 */
public class BackgroundComputationDemo extends JPanel {

   /**
    * This main routine just shows a panel of type BackgroundComputationDemo.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Demo: Background Computation in a Thread");
      BackgroundComputationDemo content = new BackgroundComputationDemo();
      window.setContentPane(content);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.pack();
      window.setResizable(false);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth()) / 2,
            (screenSize.height - window.getHeight()) / 2 );
      window.setVisible(true);
   }
   
   
   private Runner runner;  // the thread that computes the image
   
   private volatile boolean running;  // used to signal the thread to abort
   
   private JButton startButton; // button the user can click to start or abort the thread
   
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
    * Constructor creates a panel to hold the display, with a "Start" button below it.
    */
   public BackgroundComputationDemo() {
      display.setPreferredSize(new Dimension(800,600));
      display.setBackground(Color.LIGHT_GRAY);
      setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      setLayout(new BorderLayout());
      add(display, BorderLayout.CENTER);
      startButton = new JButton("Start");
      JPanel bottom = new JPanel();
      bottom.add(startButton);
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
    * while no thread is running.  It starts a new thread and
    * sets the signaling variable, running, to true;  Also changes
    * the text on the Start button to "Abort".
    * Note that the priority of the thread is set to be one less
    * than the priority of the thread that calls this method, that
    * is of Swing's event-handling thread.  This means that the event-handling
    * thread is run in preference to the computation thread.  When there is an
    * event to be handled, such as painting the display or reacting to a
    * button click, the event-handling thread will wake up to handle the
    * event.
    */
   private void start() {
      startButton.setEnabled(false);
      int width = display.getWidth() + 2;
      int height = display.getHeight() + 2;
      if (image == null)
         image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.getGraphics(); // fill image with gray
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(0,0,width,height);
      g.dispose();
      display.repaint();
      startButton.setText("Abort");
      runner = new Runner();
      try {
         runner.setPriority( Thread.currentThread().getPriority() - 1 );
      }
      catch (Exception e) {
      }
      running = true;  // Set the signal before starting the thread!
      runner.start();
   }
   
   
   /**
    * This method is called when the user clicks the button while
    * a thread is running.  A signal is sent to the thread to terminate,
    * by setting the value of the signaling variable, running, to false.
    */
   private void stop() {
      startButton.setEnabled(false);
      running = false;
      runner = null;
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
      Runner() {
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
            startButton.setEnabled(true);
            double x, y;
            double dx, dy;
            dx = (xmax-xmin)/(width-1);
            dy = (ymax-ymin)/(height-1);
            for (int row = 0; row < height; row++) {  // Compute one row of pixels.
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
            startButton.setText("Start Again");
            startButton.setEnabled(true);
            running = false; // Make sure running is false after the thread ends.
         }
      }
   }
   
}
