package edu.hws.eck.mdb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A MandelbrotPanel contains a MandelbrotDisplay and a status bar.  The display
 * computes and displays a visualization of the Mandelbrot Set.  The status bar is
 * a JLabel that is used to display information that the user might be interested
 * in.  A mouse listener is installed on the display that enables the user to
 * zoom in and out on the image.  Nothing is done to stop the user from zooming
 * in beyond the limited accuracy of numbers of type double -- when this happens,
 * the picture will first become "blocky" and with even further zooms will become
 * meaningless.
 * 
 * <p>Mouse actions on the display:
 * <ul>
 * <li>Moving or dragging the mouse shows image coordinates corresponding to mouse location.
 * <li>Clicking the mouse zooms in on or out from a point.  If shift or meta is down when the
 * mouse was pressed, or if the right mouse button is used, then zoom out by a factor of 2;
 * otherwise, zoom in by a factor of 2.  If alt is down when the mouse is pressed, of if
 * the middle mouse button is used, then the point that was clicked is moved to the center
 * of the image; otherwise, it stays where it is and the rest of the picture moves toward
 * that point or away from it.
 * <li>Dragging the mouse will draw a box around a region of the image.  The box is forced
 * to have the same aspect ratio (shape) as the display.  The box is not drawn if it would
 * be too narrow.  If the box is there when the mouse is released, then the image is
 * zoomed.  With no modifier keys and using the left mouse button, the inside of the
 * box is zoomed to fill the entire display (this zooms in on the image, which is usually
 * what you want).  If the shift or meta key is down, or if the right mouse button is used, 
 * then the entire display is shrunk down into the box (this zooms out).
 * </ul>
 */
public class MandelbrotPanel extends JPanel {
   
   private MandelbrotDisplay display;
   private JLabel statusBar;

   /**
    * Create a panel containing a MandelbrotDisplay and the label that
    * is used as a status bar.  Also add a mouse listener of type
    * MouseHandler (defined in this class).  A ComponentListener
    * is added that will respond to a change in the size of the
    * display by reporting the new size in the status bar.
    */
   public MandelbrotPanel() {
      setLayout(new BorderLayout());
      display = new MandelbrotDisplay();
      statusBar = new JLabel(I18n.tr("Idle"));
      add(display,BorderLayout.CENTER);
      add(statusBar,BorderLayout.SOUTH);
      MouseHandler mouser = new MouseHandler();
      display.addMouseListener(mouser);
      display.addMouseMotionListener(mouser);
      display.addComponentListener( new ComponentAdapter() {
         public void componentResized(ComponentEvent evt) {
               // Called when the display changes size; report the new
               // size in the status bar.  Note that this is also
               // called when the display gets its initial size, so
               // when the program opens, the image size will be shown
               // in the status bar.
            String w = "" + display.getWidth();
            String h = "" + display.getHeight();
            statusBar.setText(I18n.tr("status.imageSize",w,h));
         }
      });
   }
   
   
   /**
    * Returns the MandelbrotDisplay that is contained in this panel.
    */
   public MandelbrotDisplay getDisplay() {
      return display;
   }
   
   /**
    * Zoom in on or out from a point in the image.
    * @param x x-coordinate of the point at the center of the zoom
    * @param y y-coordinate of the point at the center of the zoom
    * @param factor magnification or shrinking factor.  If factor is
    *   greater than 1, zoom out.  If factor is less than 1, zoom in.
    *   For example, factor=0.5 shrinks the x,y ranges in the image
    *   to half their previous size.
    * @param movePointToCenter if true, then the image point at pixel
    *   position (x,y) is moved to the center pixel of the image after
    *   the zoom; if false, the point is not moved so that the pixel
    *   at (x,y) represents the same point after the zoom as before
    *   and all the other points move towards or away from that one.
    */
   public void zoom(int x, int y, double factor, boolean movePointToCenter) {
      double xmin = display.getXmin();
      double xmax = display.getXmax();
      double ymin = display.getYmin();
      double ymax = display.getYmax();
      double newWidth = factor*(xmax-xmin);
      double newHeight = factor*(ymax-ymin);
      double centerX = xmin + ((double)x)/display.getWidth()*(xmax-xmin);
      double centerY = ymax - ((double)y)/display.getHeight()*(ymax-ymin);
      if (movePointToCenter) {
         display.setLimits(centerX-newWidth/2,centerX+newWidth/2,
               centerY-newHeight/2,centerY+newHeight/2);
      }
      else {
         double newXmin = centerX - newWidth*(centerX-xmin)/(xmax-xmin);
         double newYmin = centerY - newHeight*(centerY-ymin)/(ymax-ymin);
         display.setLimits(newXmin,newXmin+newWidth,newYmin,newYmin+newHeight);
      }
   }
   
   /**
    * Display the coordinates of the image point that corresponds
    * to pixel coordinates (x,y).
    */
   private void doShowCoordsInStatusBar(int x, int y) {
      double xmin = display.getXmin();
      double xmax = display.getXmax();
      double ymin = display.getYmin();
      double ymax = display.getYmax();
      double width = display.getWidth();
      double height = display.getHeight();
      double xCoord = xmin + x/width*(xmax-xmin);
      double yCoord = ymax - y/height*(ymax-ymin);
        // The next 10 lines try to avoid more digits after the decimal
        // points than makes sense.  If it succeeds the coordinates
        // that are shown should differ only in their last few digits.
      double diff = xmax - xmin;
      int scale = 4;
      if (diff > 0) {
         while (diff < 1) {
            scale++;
            diff *= 10;
         }
      }
      String xStr = String.format("%1." + scale + "f", xCoord);
      String yStr = String.format("%1." + scale + "f", yCoord);
      statusBar.setText(I18n.tr("status.mouseCoords",xStr,yStr));
   }
    
   
   /**
    * Defines the listener that responds to user mouse actions on the display.
    * Note that the (x,y) coordinates for the events refer to the display, since
    * the listeners are registered to respond to events on the display, not on
    * this panel.  Mouse drags and clicks are used for zooming the image.  Dragging
    * and mouse motion also show the current mouse coordinates in the status bar.
    */
   private class MouseHandler implements MouseListener, MouseMotionListener {
      
      int startX, startY;  // Location of mousePressed event.
      
      boolean dragging;  // True if a drag operation is in progress.

      boolean zoomOut;   // True if the action will be a zoom out rather than
                         // a zoom in.  This is set to true if the shift key
                         // or meta key is down for the mousePressed action.
                         // (Also true if the right-mouse button is used.)
      
      boolean moved;     // During a drag operation, this becomes true if
                         // the mouse actually moves at least a few pixels.
                         // If so, the mouse action is interpreted as a
                         // click rather than a drag.

      boolean movePointToCenter;   // True if the click point for a click
                                   // operation should be moved to the center
                                   // of the image; false if it should not
                                   // be moved.  This is set in the mousePressed
                                   // routine to be true if the alt/option key
                                   // is down (or the middle mouse button is used).

      public void mousePressed(MouseEvent evt) {
         doShowCoordsInStatusBar(startX,startY);
         dragging = false;
         if (display.getStatus() == MandelbrotDisplay.STATUS_OUT_OF_MEMORY) {
               // If there is not enough memory to have an image in the display,
               // don't try to zoom it!
            return;
         }
         startX = evt.getX();
         startY = evt.getY();
         zoomOut = evt.isShiftDown() || evt.isMetaDown();
         dragging = true;
         moved = false;
         movePointToCenter = evt.isAltDown();
      }

      
      public void mouseReleased(MouseEvent evt) {
         if (!dragging)
            return;
         if (moved)  // If moved is true, this is a drag operation, otherwise, a click.
            display.applyZoom(zoomOut);  // zoom into or out of zoom rect that user has drawn
         else if (zoomOut)
            zoom(startX,startY,2,movePointToCenter); // zoom out from point
         else
            zoom(startX,startY,0.5,movePointToCenter); // zoom in on point
         dragging = false;
      }

      public void mouseDragged(MouseEvent evt) {
         int x = evt.getX();
         int y = evt.getY();
         doShowCoordsInStatusBar(x,y);
         if (!dragging)
            return;
         int width = Math.abs(x-startX);
         int height = Math.abs(y-startY);
         if (Math.abs(width) < 3 || Math.abs(height) < 3) {
               // Too close to start point to have a zoom box.
            if (!display.drawZoomBox(null)) {
                  // display.drawZoomBox() returns false if there is
                  // some error, such as not having an image, that
                  // prevents having a zoom box.  This is unlikely to
                  // happen, but if it does, we should cancel the zoom.
               dragging = false;
            }
            return;
         }
         moved = true;  // Mouse has moved more than 2 pixels away from start position.
            // During a draw operation, a zoom box is drawn with one corner at the
            // mouse's starting position.  When the mouse is release, the image
            // is zoomed out form this box or into this box.
            // The next 6 lines adjust the shape of the zoom box so that it matches
            // the shape of the window.  This is so that zooming will use the same
            // magnification factor in both directions.
         double aspect = (double)width/height;
         double imageAspect = (double)display.getWidth()/display.getHeight();
         if (aspect < imageAspect)
            width = (int)(width*imageAspect/aspect+0.49);
         else if (aspect > imageAspect)
            height = (int)(height*aspect/imageAspect+0.49);
            // The next 9 lines compute the upper left corner of the rectangle,
            // so that it has one corner at the start position of the mouse;
            // width and height represent the size of the zoom rect.
         int x1,y1;
         if (x < startX)
            x1 = startX - width;
         else
            x1 = startX;
         if (y < startY)
            y1 = startY - height;
         else
            y1 = startY;
         Rectangle rect = new Rectangle(x1,y1,width,height);
         if (!display.drawZoomBox(rect))
            dragging = false;
      }

      public void mouseMoved(MouseEvent evt) { 
         doShowCoordsInStatusBar(evt.getX(),evt.getY());
      }
      
      public void mouseExited(MouseEvent evt) { 
            // When mouse moves out of the display, get rid of the coordinate
            // display in the status bar.
         statusBar.setText("Idle");
      }
      
      public void mouseEntered(MouseEvent evt) { }
      public void mouseClicked(MouseEvent evt) { }
      
   } // end nested class MouseHandler
   
   
} // end class MandelbrotPanel
