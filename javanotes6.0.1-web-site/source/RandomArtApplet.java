import javax.swing.JApplet;

/**
 * This applet shows a RandomArtPanel as its content pane.  The panel shows
 * a new random drawing every four seconds. 
 */
public class RandomArtApplet extends JApplet {

   public void init() {
      setContentPane( new RandomArtPanel() );
   }

}
