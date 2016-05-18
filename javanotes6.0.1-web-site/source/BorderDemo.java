
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * A simple applet that shows six different types of border.  The
 * borders are used on labels which are contained in a panel that
 * itself uses an EmptyBorder.
 */
public class BorderDemo extends JApplet {

   private JPanel content;  // The panel that will be the content panel of the applet.

   public void init() {
      content = new JPanel();
      content.setBackground(Color.LIGHT_GRAY);
      content.setLayout( new GridLayout(0,1,7,7) );
      content.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
      make(BorderFactory.createLineBorder(Color.RED,2),
            "BorderFactory.createLineBorder(Color.RED,2)");
      make(BorderFactory.createMatteBorder(2,2,5,5,Color.RED),
              "BorderFactory.createMatteBorder(2,2,5,5,Color.RED)");
      make(BorderFactory.createEtchedBorder(),
              "BorderFactory.createEtchedBorder()");
      make(BorderFactory.createRaisedBevelBorder(),
              "BorderFactory.createRaisedBevelBorder()");
      make(BorderFactory.createLoweredBevelBorder(),
              "BorderFactory.createLoweredBevelBorder()");
      make(BorderFactory.createTitledBorder("Title Goes Here"),
              "BorderFactory.createTitledBorder(\"Title Goes Here\")");
      setContentPane(content);
   }

   /**
    * Make a label showing the string and with the specified border.
    * The label will be opaque and will have a light gray background.
    * The label is added to the content pane.
    */
   void make(Border border, String command) {
      // 
      JLabel label = new JLabel(command, JLabel.CENTER);
      label.setBackground(Color.LIGHT_GRAY);
      label.setOpaque(true);
      label.setBorder(border);
      content.add(label);
   }


}
