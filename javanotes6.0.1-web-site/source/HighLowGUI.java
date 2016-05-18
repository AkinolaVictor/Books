import javax.swing.JFrame;

/**
 * This program is a GUI card game.  The main program simply shows
 * a window containing a panel of type HighLowGUIPanel.  In addition
 * to that class, the program also depends on the classes Deck, Hand,
 * and Card.
 */
public class HighLowGUI {

   /**
    * The main routine simply opens a window that shows a HighLowGUIl.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("HighLowGUI");
      HighLowGUIPanel content = new HighLowGUIPanel();
      window.setContentPane(content);
      window.pack();  // Set size of window to preferred size of its contents.
      window.setResizable(false);  // User can't change the window's size.
      window.setLocation(100,100);
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
}
