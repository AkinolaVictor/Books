import javax.swing.JFrame;

/**
 * A simple GUI program that creates and opens a JFrame containing
 * the message "Hello World" and a "Change Message" button.  When the
 * user clicks the OK button, the message switches between "Hello World"
 * and "Goodbye World".  The program ends when the user clicks the window's
 * close box.  The content of the window is an object of type
 * HelloWorldPanel.
 */

public class HelloWorldGUI3 {
   
   public static void main(String[] args) {
      JFrame window = new JFrame("GUI Test");
      HelloWorldPanel content = new HelloWorldPanel();
      window.setContentPane(content);
      window.setSize(250,100);
      window.setLocation(100,100);
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
}