import javax.swing.JFrame;

/**
 * When this class is run as a stand-alone application, it opens a window
 * that contains a RandomArtPanel as its content pane.  The panel shows
 * a new random drawing every four seconds.
 */
public class RandomArt {

   public static void main(String[] args) {
      JFrame window = new JFrame("Random Art ??");
      RandomArtPanel content = new RandomArtPanel();
      window.setContentPane(content);
      window.setSize(400,400);
      window.setLocation(100,100);
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }

}
