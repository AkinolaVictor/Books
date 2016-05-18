
import javax.swing.JApplet;

/**
 * A RandomStringsApplet displays 25 copies of a string, using random colors,
 * fonts, and positions for the copies.  The message can be specified as the
 * value of an applet param with name "message."  If no param with name
 * "message" is present, then the default message "Java!" is displayed.
 * The actual content of the applet is an object of type RandomStringsPanel.
 */
public class RandomStringsApplet extends JApplet {
   
   public void init() {
      String message = getParameter("message");
      RandomStringsPanel content = new RandomStringsPanel(message);
      setContentPane(content);
   }

}
