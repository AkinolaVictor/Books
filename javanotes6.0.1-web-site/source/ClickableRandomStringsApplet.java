
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JApplet;

/**
 * A RandomStringsApplet displays 25 copies of a string, using random colors,
 * fonts, and positions for the copies.  The message can be specified as the
 * value of an applet param with name "message."  If no param with name
 * "message" is present, then the default message "Java!" is displayed.
 * The actual content of the applet is an object of type RandomStringsPanel.
 * When the user clicks the applet, it is repainted (with strings that have
 * newly selected random colors, fonts, and positions).
 */
public class ClickableRandomStringsApplet extends JApplet {
   
   public void init() {
      String message = getParameter("message");
      RandomStringsPanel content = new RandomStringsPanel(message);
      content.addMouseListener( new RepaintOnClick() );
      setContentPane(content);
   }

   private static class RepaintOnClick implements MouseListener {

      public void mousePressed(MouseEvent evt) {
         Component source = (Component)evt.getSource();
         source.repaint();
      }

      public void mouseClicked(MouseEvent evt) { }
      public void mouseReleased(MouseEvent evt) { }
      public void mouseEntered(MouseEvent evt) { }
      public void mouseExited(MouseEvent evt) { }

   }

}
