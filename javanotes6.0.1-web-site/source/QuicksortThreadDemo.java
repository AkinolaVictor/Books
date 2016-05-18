
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A panel that can show a demonstration of Quicksort in action.
 * The items to be sorted are the hues of a set of vertical bars.
 * When sorted, the bars form a spectrum from red to violet.
 * Initially, the bars are sorted.  There is a Start button.  When
 * the user clicks this button, the order of the bars is randomized
 * and then Quicksort is applied.  During the sort, a black bar
 * marks the location of an empty space from which the "pivot" element
 * has been removed.  The user can abort the sort by clicking the
 * button again.
 * <p>The main point of this program is to demonstrate threads, with
 * very simple inter-thread communication.  The recursive Quicksort
 * algorithm is run in a separate thread.  The abort operation is
 * implemented by setting the value of a volatile variable that
 * is checked periodically by the thread.  When the user aborts
 * the sort before it finishes, the value of the variable changes;
 * the thread sees the change and exits.
 * <p>This class contains a main() routine that allows the demo to
 * be run as a stand-alone application.
 */
public class QuicksortThreadDemo extends JPanel {
   
   /**
    * This main routine just shows a panel of type QuicksortThreadDemo.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Demo: Recursion in a Thread");
      QuicksortThreadDemo content = new QuicksortThreadDemo();
      window.setContentPane(content);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth()) / 2,
            (screenSize.height - window.getHeight()) / 2 );
      window.setVisible(true);
   }
   
   private final static int ARRAY_SIZE = 150;  // The number of colored bars.
   
   private int[] hue = new int[ARRAY_SIZE];  // The array that will be sorted.
   private Color[] palette = new Color[ARRAY_SIZE]; // Colors in spectral order.
   private Display display;     // The panel that displays the colored bars.
   private JButton startButton; // The button that starts and stops the demo.
   
   private Runner runner; // The thread that runs the recursion.

   private volatile boolean running;  // Set to true while recursion is running;
                                      // This is set to true as a signal to the
                                      // thread to abort.
    

   
   /**
    * When the user aborts the recursion before it finishes, an exception of
    * this type is thrown to end the recursion cleanly.
    */
   private class ThreadTerminationException extends RuntimeException {
   }
   
   
   /**
    * A subpanel of type Display shows the colored bars that are being sorted.
    * The current pivot, if any, is shown in black.  A 3-pixel gray border is
    * left around the bars.
    */
   private class Display extends JPanel {
      Display() {
         setPreferredSize(new Dimension(606,206));
         setBackground(Color.GRAY);
      }
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         double barWidth = (double)(getWidth() - 6) / hue.length;
         int h = getHeight() - 6;
         for (int i = 0; i < hue.length; i++) {
            int x1 = 3 + (int)(i*barWidth + 0.49);
            int x2 = 3 + (int)((i+1)*barWidth + 0.49);
            int w = x2 - x1;
            if (hue[i] == -1)
               g.setColor(Color.BLACK);
            else
               g.setColor(palette[hue[i]]);
            g.fillRect(x1,3,w,h);
         }
      }
   }
   
   
   /**
    * The constructor sets up the panel, containing the Display and the
    * Start button below it.
    */
   public QuicksortThreadDemo() {
      for (int i = 0; i < ARRAY_SIZE; i++) {
         palette[i] = Color.getHSBColor((i*230)/(ARRAY_SIZE*255.0F), 1, 1);
         hue[i] = i;
      }
      setLayout(new BorderLayout());
      display = new Display();
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
    * the text on the Start button to "Finish".
    */
   private void start() {
      startButton.setText("Finish");
      runner = new Runner();
      running = true;  // Set the signal before starting the thread!
      runner.start();
   }
   
   
   /**
    * This method is called when the user clicks the button while
    * a thread is running.  A signal is sent to the thread to terminate,
    * by setting the value of the signaling variable, running, to false.
    */
   private void stop() {

      /* Set the value of the signaling variable to false as a signal
       * to the thread to terminate.
       */

      running = false; 
      
      /* Wake the thread, in case it is sleeping, to get a more
       * immediate reaction to the signal.
       */
      
      runner.interrupt(); 
      
      /* Wait for the thread to stop before setting runner = null.
       * One second should be plenty of time for this to happen, but
       * in case something goes wrong, it's better not to 
       */
      
      try {
         runner.join(1000);  // Wait for thread to stop.  One second should be plenty of time.
      }
      catch (InterruptedException e) {
      }
      
      runner = null;
   }
   
   
   /**
    * This method is called frequently by the thread that is running
    * the recursion, in order to insert delays.  It calls the repaint()
    * method of the display to allow the user to see what is going on;
    * the delay will give the system a chance to actually update the display.
    * Since this method is called regularly while the recursion is in 
    * progress, it is also used as a convenient place to check the value
    * of the signaling variable, running.  If the value of running has
    * been set to false, this method throws an exception of type
    * ThreadTerminatedException.  This exception will cause all active
    * levels of the recursion to be terminated.  It is caught in the
    * run() method of the thread.
    * @param millis  The number of milliseconds to sleep.
    */
   private void delay(int millis) {
      if (! running)
         throw new ThreadTerminationException();
      display.repaint();
      try {
         Thread.sleep(millis);
      }
      catch (InterruptedException e) {
      }
      if (! running)
         throw new ThreadTerminationException();
   }
   
   
   /**
    * The basic non-recursive QuickSortStep algorithm, which
    * uses hue[lo] as a "pivot" and rearranges elements of the 
    * hue array from positions lo through hi so that positions
    * the pivot value is in its correct location, with smaller
    * items to the left and bigger items to the right.  The
    * position of the pivot is returned.  In this version,
    * we conceptually remove the pivot from the array, leaving
    * an empty space.  The space is marked by a -1, and it moves
    * around as the algorithm proceeds.  It is shown as a black
    * bar in the display. Every time a change is made, the
    * delay() method is called to insert a 1/10 second delay
    * to let the user see the change.
    */
   private int quickSortStep(int lo, int hi) {
      int pivot = hue[lo];  // Save pivot item.
      hue[lo] = -1;  // Mark location lo as empty.
      delay(100);
      while (true) {
         while (hi > lo  && hue[hi] > pivot)
            hi--;
         if (hi == lo)
            break;
         hue[lo] = hue[hi]; // Move hue[hi] into empty space.
         hue[hi]  = -1;     // Mark location hi as empty.
         delay(100);
         while (lo < hi && hue[lo] < pivot)
            lo++;
         if (hi == lo)
            break;
         hue[hi] = hue[lo];  // Move hue[lo] into empty space.
         hue[lo] = -1;       // Mark location lo as empty.
         delay(100);
      }
      hue[lo] = pivot;  // Move pivot item into the empty space.
      delay(100);
      return lo;
   }
   
   
   /**
    * The recursive quickSort algorithm, for sorting the hue
    * array from positions lo through hi into increasing order.
    * Most of the actual work is done in quickSortStep().
    */
   private void quickSort(int lo, int hi) {
      if (hi <= lo)
         return;
      int mid = quickSortStep(lo, hi);
      quickSort(lo, mid-1);
      quickSort(mid+1, hi);
   }
   
   
   /**
    * This class defines the treads that run the recursive
    * QuickSort algorithm.  The thread begins by randomizing the
    * array.  It then calls quickSort() to sort the entire array.
    * If quickSort() is aborted by a ThreadTerminationExcpetion,
    * which would be caused by the user clicking the Finish button,
    * then the thread will restore the array to sorted order before
    * terminating, so that whether or not the quickSort is aborted,
    * the array ends up sorted.
    */
   private class Runner extends Thread {
      public void run() {
         try {
            for (int i = hue.length-1; i > 0; i--) { // Randomize array.
               int r = (int)((i+1)*Math.random());
               int temp = hue[r];
               hue[r] = hue[i];
               hue[i] = temp;
            }
            delay(1000);  // Wait one second before starting the sort.
            quickSort(0,hue.length-1);  // Sort the whole array.
         }
         catch (ThreadTerminationException e) { // User aborted quickSort.
            for (int i = 0; i < hue.length; i++)
               hue[i] = i;
         }
         finally {// Make sure running is false and button label is correct. 
            running = false; 
            startButton.setText("Start");
            display.repaint();
         }
      }
   }

} // end QuicksortThreadDemo
