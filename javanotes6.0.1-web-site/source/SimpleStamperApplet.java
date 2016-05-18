import javax.swing.JApplet;

/**
 * An applet that shows a SimpleStamperPanel.  When the user clicks the 
 * panel, a red rectangle is added to the window.  When the user right-clicks,
 * a blue oval is added.  When the user shift-clicks, the drawing is cleared. 
 */
public class SimpleStamperApplet extends JApplet {

   public void init() {
      SimpleStamperPanel content = new SimpleStamperPanel();
      setContentPane(content);
   }
}
