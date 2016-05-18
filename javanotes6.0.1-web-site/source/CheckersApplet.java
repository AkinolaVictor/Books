import javax.swing.JApplet;


/**
 * An applet that simply shows an object of type checkers
 * as its content pane.  The applet size should be 350-by-250.
 */
public class CheckersApplet extends JApplet {

   public void init() {
      setContentPane( new Checkers() );
   }
}
