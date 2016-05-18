import javax.swing.JFrame;

/**
 * This stand-alone application opens window that contains a
 * SimpleMouseTrackPanel.  The panel displays information about
 * mouse events on the panel.  The program ends when the user
 * closes the window.
 */
public class SimpleTrackMouse {
   
   public static void main(String[] args) {
      JFrame window = new JFrame("Simple Mouse Tracker");
      SimpleTrackMousePanel content = new SimpleTrackMousePanel();
      window.setContentPane(content);
      window.setSize(500,400);
      window.setLocation(100,100);
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
}
