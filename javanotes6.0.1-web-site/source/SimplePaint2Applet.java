import javax.swing.JApplet;

/**
 * Applet simply shows a content pane of type SimplePaint2,
 * with its associated menu bar.
 */
public class SimplePaint2Applet extends JApplet {
   
   public void init() {
      SimplePaint2 content = new SimplePaint2();
      setContentPane( content );
      setJMenuBar( content.createMenuBar() );
   }

}
