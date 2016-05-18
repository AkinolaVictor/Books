import javax.swing.JApplet;

/**
 * An applet that simply shows a content pane of type SimpleDrawRects.
 * The applet also depends on the class RainbowPalette.  The width of
 * the applet must be 266 to accommodate the size of the palette.
 */
public class SimpleDrawRectsApplet extends JApplet{

   public void init() {
      setContentPane( new SimpleDrawRects() );
   }
   
}
