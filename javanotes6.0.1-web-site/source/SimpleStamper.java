import javax.swing.JFrame;

/**
 * A stand-alone application that opens a window containing a
 * SimpleStamperPanel.  When the user clicks the panel, a red
 * rectangle is added to the window.  When the user right-clicks,
 * a blue oval is added.  When the user shift-clicks, the drawing
 * is cleared.  The program ends when the user closes the window
 * by clicking its close box. 
 */
public class SimpleStamper {

   public static void main(String[] args) {
         JFrame window = new JFrame("Simple Stamper");
         SimpleStamperPanel content = new SimpleStamperPanel();
         window.setContentPane(content);
         window.setSize(350,250);
         window.setLocation(100,100);
         window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
         window.setVisible(true);
      }

}
