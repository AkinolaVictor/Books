import javax.swing.JApplet;

/**
 * An applet the simply displays a panel of type HighLowGUIPanel.
 * The applet should be 376 pixels wide and about 220 pixels high.
 * In addition to HighLowGUIPanel, this applet depends on the
 * classes Deck, Hand, and Card.
 */
public class HighLowGUIApplet extends JApplet {

   public void init() {
      setContentPane( new HighLowGUIPanel() );
   }
   
}
