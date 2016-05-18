import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A simple applet that can display the messages "Hello World"
 * and "Goodbye World".  The applet contains a button, and it
 * switches from one message to the other when the button is
 * clicked.
 */
public class HelloWorldApplet extends JApplet {
   
   /**
    * This variables stores the message that is currently displayed
    * in the applet.  The message is changed when the user clicks
    * the button in the applet.
    */
   private String currentMessage = "Hello World!";
   
   /**
    * The message is drawn on this panel.
    */
   private MessageDisplay displayPanel;
   
   /**
    * An object of type MessageDisplay displays the applet's current
    * message, as stored in the instance variable currentMessage.
    */
   private class MessageDisplay extends JPanel {
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         g.drawString(currentMessage, 20, 30);
      }
   }
   
   /**
    * An object of type ButtonHandler can "listen" for ActionEvents
    * (the type of event that is generated when the user clicks a
    * button).  When the event occurs, the ButtonHandler responds
    * by changing the applet's message from "Hello World!" to
    * "Goodbye World!" or vice versa.
    */
   private class ButtonHandler implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         if (currentMessage.equals("Hello World!"))
            currentMessage = "Goodbye World!";
         else
            currentMessage = "Hello World!";
         displayPanel.repaint(); // Paint display panel with new message.
      }
   }
   
   /**
    * The applet's init() method creates the button and display panel and
    * adds them to the applet, and it sets up a listener to respond to
    * clicks on the button.
    */
   public void init() {
      
      displayPanel = new MessageDisplay();
      JButton changeMessageButton = new JButton("Change Message");
      ButtonHandler listener = new ButtonHandler();
      changeMessageButton.addActionListener(listener);

      JPanel content = new JPanel();
      content.setLayout(new BorderLayout());
      content.add(displayPanel, BorderLayout.CENTER);
      content.add(changeMessageButton, BorderLayout.SOUTH);

      setContentPane(content);
   }
   
}
