
import java.awt.*;
import javax.swing.*;

/**
 * A simplified version of QuickSortThreadDemo, with no user control.
 * The applet shows a visualization of the quicksort algorithm, where
 * the gaol is to sort a sequence of colored bars into spectral order
 * from red to violet.  After the sort completes, the array is randomized
 * and another sort performed.
 */
public class LittleQuicksortDemo extends JApplet {
   
   public void init() {
      for (int i = 0; i < ARRAY_SIZE; i++) {
         palette[i] = Color.getHSBColor((i*230)/(ARRAY_SIZE*255.0F), 1, 1);
         hue[i] = i;
      }
      display = new Display();
      setContentPane(display);
      new Runner().start();
   }
   
   
   
   private final static int ARRAY_SIZE = 100;  // The number of colored bars.
   
   private int[] hue = new int[ARRAY_SIZE];  // The array that will be sorted.
   private Color[] palette = new Color[ARRAY_SIZE]; // Colors in spectral order.
   private Display display;     // The panel that displays the colored bars.
   
   
   /**
    * A panel of type Display shows the colored bars that are being sorted.
    * The current pivot, if any, is shown in black.  A 3-pixel gray border is
    * left around the bars.
    */
   private class Display extends JPanel {
      Display() {
         setPreferredSize(new Dimension(606,206));
         setBackground(Color.GRAY);
         setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
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
    * This method is called frequently by the thread that is running
    * the recursion, in order to insert delays.  It calls the repaint()
    * method of the display to allow the user to see what is going on;
    * the delay will give the system a chance to actually update the display.
    * @param millis  The number of milliseconds to sleep.
    */
   private void delay(int millis) {
      display.repaint();
      try {
         Thread.sleep(millis);
      }
      catch (InterruptedException e) {
      }
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
      delay(200);
      while (true) {
         while (hi > lo  && hue[hi] > pivot)
            hi--;
         if (hi == lo)
            break;
         hue[lo] = hue[hi]; // Move hue[hi] into empty space.
         hue[hi]  = -1;     // Mark location hi as empty.
         delay(200);
         while (lo < hi && hue[lo] < pivot)
            lo++;
         if (hi == lo)
            break;
         hue[hi] = hue[lo];  // Move hue[lo] into empty space.
         hue[lo] = -1;       // Mark location lo as empty.
         delay(200);
      }
      hue[lo] = pivot;  // Move pivot item into the empty space.
      delay(200);
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
         delay(3000);
         while (true) {
            delay(2000);
            for (int i = hue.length-1; i > 0; i--) { // Randomize array.
               int r = (int)((i+1)*Math.random());
               int temp = hue[r];
               hue[r] = hue[i];
               hue[i] = temp;
            }
            delay(1000);  // Wait one second before starting the sort.
            quickSort(0,hue.length-1);  // Sort the whole array.
         }
      }
   }

} // end QuicksortThreadDemo
