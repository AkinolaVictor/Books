import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;


/**
 * This panel lets the user place colored rectangles on a canvas
 * by clicking the canvas.  The color of the rectangle is given
 * by a color palette that lies along the bottom of the applet.
 * Alt-clicking (or clicking with middle mouse button) on a rectangle
 * will delete it.  Shift-clicking a rectangle will move it out in
 * front of all the other rectangles.  The user can right-click-and-drag 
 * to move the rectangles around.  The rectangles are restricted so that 
 * they cannot be moved entirely off the canvas.
 * 
 * The main point of this class is to demonstrate the use of an
 * ArrayList.
 * 
 * This class contains a main() routine so that it can be run as
 * a standalone application.  The main routine simply opens a
 * window that uses a SimpleDrawRects panel as its content pane.
 * 
 * This program depends on RainbowPalette.java.
 */
public class SimpleDrawRects extends JPanel {
   
   /**
    * When this class is run as an application, this main() routine opens
    * a frame that sues a SimpleDrawRects panel as its content pane.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("SimpleDrawRects");
      SimpleDrawRects content = new SimpleDrawRects();
      window.setContentPane(content);
      window.pack();
      window.setLocation(100,100);
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setResizable(false);  
      window.setVisible(true);
   }
   

   /**
    * A palette of colors that appears at the bottom of the applet.  The
    * RainbowPalette class is non-standard, and is defined in a separate file..
    */
   private RainbowPalette colorInput; 
   

   /**
    * Constructor sets up the panel, with drawing area and palette.
    */
   public SimpleDrawRects() {
      setBackground(Color.GRAY);
      setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
      setLayout(new BorderLayout(2,2));
      RectPanel canvas = new RectPanel();
      colorInput = new RainbowPalette();
      add(canvas, BorderLayout.CENTER);
      add(colorInput, BorderLayout.SOUTH);
   }
   
   //-------------------------- Nested classes -------------------
   
   
   /**
    * An object of type ColoredRect holds the data for one colored rectangle.
    */
   private static class ColoredRect {
      int x, y, width, height;   // Location and size of rect.
      Color color;               // Color of rect.
   }
   
   
   /**
    * This class defined the drawing surface that is used for displaying
    * the rectangles.  It also listens for mouse events on itself and
    * responds to them.  The user adds a rectangle by (left) clicking on
    * the panel, can delete a rectangle by Alt-clicking, can move a 
    * rectangle to the front by shift-clicking it, and can drag a rectangle
    * by right-clicking it.
    */
   private class RectPanel extends JPanel
                         implements MouseListener, MouseMotionListener {
      
      /**
       * The colored rectangles are represented by objects of type ColoredRect 
       * that are stored in this ArrayList.
       */
      private ArrayList rects;
      
      
      /* Variables for implementing dragging. */
      
      private boolean dragging;      // This is true when dragging is in progress.
      private ColoredRect dragRect;  // The rect that is being dragged.
      private int offsetx, offsety;  // The distance from the upper left corner of
                                     // the dragRect to the point where the user 
                                     // clicked the rect.  This offset is maintained \
                                     // as the rect is dragged.
      
      /**
       * Constructor creates an ArrayList to hold the rectangle data and
       * sets up mouse listening.  Background color is set to white,
       * and preferred size to 262-by-262.
       */
      RectPanel() {
         setBackground(Color.WHITE);
         setPreferredSize(new Dimension(262,262));
         rects = new ArrayList();
         addMouseListener(this);
         addMouseMotionListener(this);
      }
      
      /**
       * Find the topmost rect that contains the point (x,y). Return null 
       * if no rect contains that point.  The rects in the ArrayList are 
       * considered in reverse order so that if one lies on top of another, 
       * the one on top is seen first and is returned.
       */
      ColoredRect findRect(int x, int y) {
         for (int i = rects.size() - 1;  i >= 0;  i--) {
            ColoredRect rect = (ColoredRect)rects.get(i);
            if ( x >= rect.x && x < rect.x + rect.width
                  && y >= rect.y && y < rect.y + rect.height )
               return rect;  // (x,y) is inside this rect.
         }
         return null;
      }
      
      /**
       * If rect != null, move it out in front of the other rects by moving 
       * it to the last position in the ArrayList.
       */
      void bringToFront(ColoredRect rect) {
         if (rect != null) {
            rects.remove(rect);  // Remove rect from current position.
            rects.add(rect);     // Put rect in the ArrayList in last position.
            repaint();
         }
      }
      
      /**
       * If rect != null, remove it from the ArrayList and from the screen.
       */
      void deleteRect(ColoredRect rect) {
         if (rect != null) {
            rects.remove(rect);
            repaint();
         }
      }
      
      /**
       * Draw all the rects in the ArrayList.  NOTE: A black border is drawn
       * around each rectangle.
       */
      public void paintComponent(Graphics g) {
         super.paintComponent(g);  // Fills with background color, white.
         for (int i = 0; i < rects.size(); i++) {
            ColoredRect rect = (ColoredRect)rects.get(i);
            g.setColor(rect.color);
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
            g.setColor(Color.BLACK);
            g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
         }
      }
      
      /**
       * Responds when the user clicks on the panel.
       */
      public void mousePressed(MouseEvent evt) { 
         
         if (dragging)  // If dragging is already in progress, just return.
            return;
         
         if (evt.isMetaDown() || evt.isControlDown()) {
               // User right-clicked (or Command-clicked or control-clicked).  
               // Start dragging the rect that the user clicked (if any).
            dragRect = findRect( evt.getX(), evt.getY() );
            if (dragRect != null) {
               dragging = true;   // Begin a drag operation.
               offsetx = evt.getX() - dragRect.x;
               offsety = evt.getY() - dragRect.y;
            }
         }
         else if (evt.isShiftDown()) {
               // User shift-clicked.  More the rect that the user
               // clicked (if any) to the front.  Note that findRect()
               // might return null, but bringToFront() accounts for that.
            bringToFront( findRect( evt.getX(), evt.getY() ) );
         }
         else if (evt.isAltDown()) {
               // User alt-clicked or middle-clicked.  Delete the rect
               // that the user clicked (if any).
            deleteRect( findRect( evt.getX(), evt.getY() ) );
         }
         else {
               // This is a simple left-click.  Make a new
               // rectangle and add it to the canvas.  Every rectangle is
               // 60 pixels wide and 30 pixels tall.  The point where the
               // user clicked is at the center of the rectangle.  It's
               // color is the selected color in the colorInput palette.
            ColoredRect rect = new ColoredRect();
            rect.x = evt.getX() - 30;
            rect.y = evt.getY() - 15;
            rect.width = 60;
            rect.height = 30;
            rect.color = colorInput.getSelectedColor();
            rects.add(rect);
            repaint();
         }
         
      } // end mousePressed()
      
      /**
       * Called when the user releases the mouse button.
       * End the current drag operation, if one is in progress.
       */
      public void mouseReleased(MouseEvent evt) { 
         if (dragging == false)
            return;
         dragRect = null;
         dragging = false;
      }
      
      /**
       * Called when the user drags the mouse.  Continue the drag operation if 
       * one is in progress.  Move the rect that is being dragged to the current
       * mouse position.  But clamp it so that it can't be more than halfway 
       * off the screen.
       */
      public void mouseDragged(MouseEvent evt) { 

         if (dragging == false)
            return;
         
         dragRect.x = evt.getX() - offsetx;  // Get new position of rect.
         dragRect.y = evt.getY() - offsety;
         
         /* Clamp (x,y) to a permitted range, as described above. */
         
         if (dragRect.x < - dragRect.width / 2)
            dragRect.x = - dragRect.width / 2;
         else if (dragRect.x + dragRect.width/2 > getWidth())
            dragRect.x = getWidth() - dragRect.width / 2;
         if (dragRect.y < - dragRect.height / 2)
            dragRect.y = - dragRect.height / 2;
         else if (dragRect.y + dragRect.height/2 > getHeight())
            dragRect.y = getHeight() - dragRect.height / 2;
         
         /* Redraw the panel, with the rect in its new position. */
         
         repaint();
         
      }  // end mouseDragged()
      
      public void mouseClicked(MouseEvent evt) { }
      public void mouseEntered(MouseEvent evt) { }
      public void mouseExited(MouseEvent evt) { }
      public void mouseMoved(MouseEvent evt) { }
      
   }  // end nested class RectPanel
   
   
}  // end class SimpleDrawRects

