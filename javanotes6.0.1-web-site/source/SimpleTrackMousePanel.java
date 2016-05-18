
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A SimpleTrackMousePanel is a panel that displays information about mouse
 * events on the panel, including the type of event, the position of the mouse,
 * and a list of modifier keys that were down when the event occurred.
 */
public class SimpleTrackMousePanel extends JPanel {
   
   private String eventType = null;     // If non-null, gives the type of the most recent mouse event.
   private String modifierKeys = "";    // If non-empty, gives special keys that are held down.
   private int mouseX, mouseY;          // Position of mouse (at most recent mouse event).
   

   /**
    * Constructor creates a mouse listener object and sets it to listen for
    * mouse events and mouse motion events on the panel.
    */
   public SimpleTrackMousePanel() { 
      // Set background color and arrange for the applet to listen for mouse events.
      setBackground(Color.WHITE);
      MouseHandler listener = new MouseHandler();
      addMouseListener(listener);
      addMouseMotionListener(listener);
   }
   
   
   /**
    * Records information about a mouse event on the panel.  This method is called
    * by the mouse handler object whenever a mouse event occurs.
    * @param evt the MouseEvent object for the event.
    * @param eventType a description of the type of event, such as "mousePressed".
    */
   private void setInfo(MouseEvent evt, String eventType) {
      this.eventType = eventType;
      mouseX = evt.getX();
      mouseY = evt.getY();
      modifierKeys = "";
      if (evt.isShiftDown())
         modifierKeys += "Shift  ";
      if (evt.isControlDown())
         modifierKeys += "Control  ";
      if (evt.isMetaDown())
         modifierKeys += "Meta  ";
      if (evt.isAltDown())
         modifierKeys += "Alt";
      repaint();
   }

   
   /**
    * The paintComponent() method displays information about the most recent
    * mouse event on the panel (as set by the setInfo() method).
    */
   public void paintComponent(Graphics g) {

      super.paintComponent(g);  // Fills panel with background color.

      g.setColor(Color.BLUE);   // Draw a 2-pixel blue border.
      g.drawRect(0 ,0, getSize().width - 1 ,getSize().height - 1);  
      g.drawRect(1 ,1, getSize().width - 3 ,getSize().height - 3);  

      if (eventType == null) {
            // If eventType is null, no mouse event has yet occurred 
            // on the panel, so don't display any information.
         return;
      }

      g.setColor(Color.RED);  // Display information about the mouse event.
      g.drawString("Mouse event type:  " + eventType, 6, 18);
      if (modifierKeys.length() > 0)
         g.drawString("Modifier keys:     " + modifierKeys, 6, 34);
      g.setColor(Color.BLACK);
      g.drawString("(" + mouseX + "," + mouseY + ")", mouseX, mouseY);

   }  
   

   /**
    * An object belonging to class MouseHandler listens for mouse events
    * on the panel.  (Listening is set up in the constructor for the
    * SimpleTrackMousePanel class.)  When a mouse event occurs, the listener
    * simply calls the setInfo() method in the SimpleMouseTrackPanel class
    * with information about the mouse event that has occurred.
    */
   private class MouseHandler implements MouseListener, MouseMotionListener {
      
      public void mousePressed(MouseEvent evt) {
         setInfo(evt, "mousePressed");
      }
      
      public void mouseReleased(MouseEvent evt) {
         setInfo(evt, "mouseReleased");
      }
      
      public void mouseClicked(MouseEvent evt) {
         setInfo(evt, "mouseClicked");
      }
      
      public void mouseEntered(MouseEvent evt) {
         setInfo(evt, "mouseEntered");
      }
      
      public void mouseExited(MouseEvent evt) {
         setInfo(evt, "mouseExited");
      }
      
      public void mouseMoved(MouseEvent evt) {
         setInfo(evt, "mouseMoved");
      }
      
      public void mouseDragged(MouseEvent evt) {
         setInfo(evt, "mouseDragged");
      }

   }  // end nested class MouseHandler

}  // end of class SimpleMouseTracker

