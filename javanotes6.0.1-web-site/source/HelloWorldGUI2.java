import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A simple GUI program that creates and opens a JFrame containing
 * the message "Hello World" and an "OK" button.  When the user clicks
 * the OK button, the program ends.
 */
public class HelloWorldGUI2 {
   
    /**
     * An object of type HelloWorldDisplay is a JPanel that displays
     * the message "Hello World!".
     */
   private static class HelloWorldDisplay extends JPanel {
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         g.drawString( "Hello World!", 20, 30 );
      }
   }
   
   /**
    * An object of type ButtonHandler can "listen" for ActionEvents
    * (the type of event that is generated when the user clicks a
    * button).  When the event occurs, the ButtonHandler responds
    * by exiting the program.
    */
   private static class ButtonHandler implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         System.exit(0);
      }
   }
   
   /**
    * The main program creates a window containing a HelloWorldDisplay
    * and a button.  A ButtonHandler is created that will end the program
    * when the user clicks the button.
    */
   public static void main(String[] args) {
      
      HelloWorldDisplay displayPanel = new HelloWorldDisplay();
      JButton okButton = new JButton("OK");
      ButtonHandler listener = new ButtonHandler();
      okButton.addActionListener(listener);

      JPanel content = new JPanel();
      content.setLayout(new BorderLayout());
      content.add(displayPanel, BorderLayout.CENTER);
      content.add(okButton, BorderLayout.SOUTH);

      JFrame window = new JFrame("GUI Test");
      window.setContentPane(content);
      window.setSize(250,100);
      window.setLocation(100,100);
      window.setVisible(true);

   }
   
}
