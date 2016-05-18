import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A simple GUI program that creates and opens a JFrame containing
 * the message "Hello World" and an "OK" button.  When the user clicks
 * the OK button, the program ends.  This version uses anonymous
 * classes to define the message display panel and the action listener
 * object.  Compare to HelloWorldGUI2, which uses nested classes.
 */
public class HelloWorldGUI4 {
      
   /**
    * The main program creates a window containing a display panel
    * and a button that will end the program when the user clicks it.
    */
   public static void main(String[] args) {
      
      JPanel displayPanel = new JPanel() {
             // An anonymous subclass of JPanel that displays "Hello World!".
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawString( "Hello World!", 20, 30 );
         }
      };

      JButton okButton = new JButton("OK");
      okButton.addActionListener( new ActionListener() {
             // An anonymous class that defines the listener object.
         public void actionPerformed(ActionEvent e) {
            System.exit(0);
         }
      });

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
