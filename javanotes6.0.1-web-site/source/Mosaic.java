
import java.awt.*;
import java.awt.event.*;


/**
 *  The class Mosaic makes available a window made up of a grid
 *  of colored rectangles.  Routines are provided for opening and
 *  closing the window and for setting and testing the color of rectangles
 *  in the grid.
 *
 *  Each rectangle in the grid has a color.  The color can be
 *  specified by red, green, and blue amounts in the range from
 *  0 to 255.  It can also be given as an object belonging
 *  to the class Color.
 *  
 *  (Technical note:  Some of the methods in this class are declared to
 *  be "synchronized".  Synchronization is sometimes required when there
 *  are several threads running at the same time.  In this case, there
 *  can be both the thread that runs the main routine of a program and
 *  the thread that manages the GUI.  Synchronization can be necessary
 *  if the window is closed by the GUI while it is being used by
 *  the main thread.)
 */

public class Mosaic {

   private static Frame window;         // A mosaic window, null if no window is open.
   private static MosaicCanvas canvas;  // A component that actually manages and displays the rectangles.


   /** 
    * Open a mosaic window with a 20-by-20 grid of squares, where each
    * square is 15 pixel on a side.
    */
   public static void open() {
      open(20,20,15,15);
   }

   
   /**
    * Opens a mosaic window containing a specified number of rows and
    * a specified number of columns of square.  Each square is 15 pixels
    * on a side.
    */
   public static void open(int rows, int columns) {
      open(rows,columns,15,15);
   }

   
   /**
    * Opens a "mosaic" window on the screen.  If another mosaic window was
    * already open, that one is closed and a new one is created.
    *
    * Precondition:   The parameters rows, cols, w, and h are positive integers.
    * Postcondition:  A window is open on the screen that can display rows and
    *                   columns of colored rectangles.  Each rectangle is w pixels
    *                   wide and h pixels high.  The number of rows is given by
    *                   the first parameter and the number of columns by the
    *                   second.  Initially, all rectangles are black.
    * Note:  The rows are numbered from 0 to rows - 1, and the columns are 
    * numbered from 0 to cols - 1.
    */
   synchronized public static void open(int rows, int columns, int blockWidth, int blockHeight) {
      if (window != null)
         window.dispose();
      canvas = new MosaicCanvas(rows,columns,blockWidth,blockHeight);
      window = new Frame("Mosaic Window");
      window.add("Center",canvas);
      window.addWindowListener(
            new WindowAdapter() {  // close the window when the user clicks its close box
               public void windowClosing(WindowEvent evt) {
                  close();
               }
            });
      window.pack();
      window.setVisible(true);
   }

   
   /**
    * Close the mosaic window, if one is open.
    */
   synchronized public static void close() {
      if (window != null) {
         window.dispose();
         window = null;
         canvas = null;
      }
   }

   
   /**
    * Tests whether the mosaic window is currently open.
    *
    * Precondition:   None.
    * Postcondition:  The return value is true if the window is open when this
    *                   function is called, and it is false if the window is
    *                   closed.
    */
   synchronized public static boolean isOpen() {
      return (window != null);
   }

   
   /**
    * Inserts a delay in the program (to regulate the speed at which the colors
    * are changed, for example).
    *
    * Precondition:   milliseconds is a positive integer.
    * Postcondition:  The program has paused for at least the specified number
    *                   of milliseconds, where one second is equal to 1000
    *                   milliseconds.
    */
   public static void delay(int milliseconds) {
      if (milliseconds > 0) {
         try { Thread.sleep(milliseconds); }
         catch (InterruptedException e) { }
      }
   }

   
   /**
    * Gets the color of one of the rectangles in the mosaic.
    * 
    * Precondition:   row and col are in the valid range of row and column numbers.
    * Postcondition:  The color of the specified rectangle is returned as
    *                 object of type color.
    */
   public static Color getColor(int row, int col) {
      if (canvas == null)
         return Color.black;
      return canvas.getColor(row, col);
   }

   
   /**
    * Gets the red component of the color of one of the rectangles.
    *
    * Precondition:   row and col are in the valid range of row and column numbers.
    * Postcondition:  The red component of the color of the specified rectangle is
    *                   returned as an integer in the range 0 to 255 inclusive.
    */
   public static int getRed(int row, int col) {
      if (canvas == null)
         return 0;
      return canvas.getRed(row, col);
   }

   
   /**
    * Like getRed, but returns the green component of the color.
    */
   public static int getGreen(int row, int col) {
      if (canvas == null)
         return 0;
      return canvas.getGreen(row, col);
   }
   
   
   /**
    * Like getRed, but returns the blue component of the color.
    */
   public static int getBlue(int row, int col) {
      if (canvas == null)
         return 0;
      return canvas.getBlue(row, col);
   }

   
   /**
    * Sets the color of one of the rectangles in the window.
    *
    * Precondition:   row and col are in the valid range of row and column numbers.
    * Postcondition:  The color of the rectangle in row number row and column
    *                 number col has been set to the color specified by c.
    *                 If c is null, the color of the rectangle is set to black.
    */
   public static void setColor(int row, int col, Color c) {
      if (canvas == null)
         return;
      canvas.setColor(row,col,c);
   }

   
   /**
    * Sets the color of one of the rectangles in the window.
    *
    * Precondition:   row and col are in the valid range of row and column numbers,
    *                   and r, g, and b are in the range 0 to 255, inclusive.
    * Postcondition:  The color of the rectangle in row number row and column
    *                   number col has been set to the color specified by r, g,
    *                   and b.  r gives the amount of red in the color with 0 
    *                   representing no red and 255 representing the maximum 
    *                   possible amount of red.  The larger the value of r, the 
    *                   more red in the color.  g and b work similarly for the 
    *                   green and blue color components.
    */
   public static void setColor(int row, int col, int red, int green, int blue) {
      if (canvas == null)
         return;
      canvas.setColor(row,col,red,green,blue);
   }

   
   /**
    * Fills the entire mosaic with a specified color.  If c is null, the mosaic
    * is filled with black.
    */
   public static void fill(Color c) {
      canvas.fill(c);
   }

   
   /**
    * Fills the entire mosaic with a color that is specified by giving its
    * red, green, and blue components (numbers in the range 0 to 255).
    */
   public static void fill(int red, int green, int blue) {
      if (canvas == null)
         return;
      canvas.fill(red,green,blue);
   }
   
   
   /**
    * Fill the entire mosaic window with random colors by setting
    * the color of each rectangle to a randomly selected red/blue/green
    * values.
    */
   public static void fillRandomly() {
      canvas.fillRandomly();
   }
   

}  // end of class Mosaic