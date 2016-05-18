import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

/**
 * Displays a window that shows 25 copies of the string "Java!" in
 * random colors, fonts, and positions.  The content of the window
 * is an object of type RandomStringsPanel.  When the user clicks
 * the window, the content of the window is repainted, with the 
 * strings in newly selected random colors, fonts, and positions.
 */
public class ClickableRandomStringsApp {
   
   public static void main(String[] args) {
      JFrame window = new JFrame("Random Strings");
      RandomStringsPanel content = new RandomStringsPanel();
      content.addMouseListener( new RepaintOnClick() );
      window.setContentPane(content);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.setLocation(100,75);
      window.setSize(300,240);
      window.setVisible(true);
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
