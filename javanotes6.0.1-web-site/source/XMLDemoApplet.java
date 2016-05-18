import javax.swing.JApplet;

/**
 *  An applet that simply shows a content pane
 *  of type XMLDemo.
 */
public class XMLDemoApplet extends JApplet {
   public void init() {
      setContentPane(new XMLDemo());
   }
}
