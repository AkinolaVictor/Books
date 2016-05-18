import javax.swing.JApplet;

/**
 * A SubKillerApplet lets the user play a simple game, using the arrow
 * keys.  The applet uses a SubKillerPanel as its content pane, and that
 * does all the work.
 */
public class SubKillerApplet extends JApplet {

   public void init() {
      setContentPane( new SubKillerPanel() );
   }

}
