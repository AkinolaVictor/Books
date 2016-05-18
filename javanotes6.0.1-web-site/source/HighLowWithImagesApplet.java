import javax.swing.JApplet;

/**
 * An applet the simply displays a panel of type HighLowWithImages.
 * The applet should be 395 pixels wide and about 245 pixels high.
 * In addition to HighLowWithImages, this applet depends on the
 * classes Deck, Hand, and Card.  The image file cards.png is also
 * required, as a resource file in the program.
 */
public class HighLowWithImagesApplet extends JApplet {

   public void init() {
      setContentPane( new HighLowWithImages() );
   }
   
}
