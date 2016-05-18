
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This applet simulates a network chat, in which the user chats with
 * someone over a network.  Here, there is no network.  The partner is
 * simulated by a thread that emits strings at random.  The point
 * of the simulation is to show how to use a separate thread for receiving
 * information.

 * The applet has an input box where the user enters messages.  When
 * the user presses return, the message is posted to a text area.  In
 * a real chat program, it would be transmitted over the network.
 * When a (simulated) message is received from the other side, it is
 * also posted to this text area.  

 * The partner thread is started the first time the user sends
 * a message.
 */

public class ChatSimulation extends JApplet implements ActionListener, Runnable {

   private static String[] incomingMessages = {  // Simulated messages from chat partner.
      "Say, how about those Mets.",
      "My favorite color is gray.  What's yours?",
      "In a World without Walls, who needs Windows?  "
      + "In a World without Fences, who needs Gates?",
      "An empty vessel makes the most noise.",
      "Can you tell me what a Thread is?",
      "Do you always follow style rules when you program?",
      "Sometimes, I really, really hate computers.",
      "Do you remember the Pythagorean theorem?",
      "Have you tried the GIMP image processing program?",
      "I was thinking, How come wrong numbers are never busy?",
      "Do you know Murphy's Law?  You should.",
      "Are you getting bored yet?",
      "Everything should be made as simple as possible -- but not simpler.",
      "The revolution will not be televised."
   };

   private JTextArea transcript;  // A text area that shows transmitted and
                                  // received messages.

   private JTextField input;  // A text-input box where the use enters
                              // outgoing messages.

   private Thread runner;    // The thread the simulated the incoming messages.

   private boolean running;  // This is set to true when the thread is created.
                             // when it becomes false, the thread should exit.
                             // It is set to false in the applet's stop() method.

   /**
    * Initialize the applet.
    */
   public void init() {
      JPanel content = new JPanel();
      content.setBackground(Color.LIGHT_GRAY);
      content.setLayout(new BorderLayout(3,3));
      content.setBorder(BorderFactory.createLineBorder(Color.GRAY,3));
      transcript = new JTextArea();
      transcript.setEditable(false);
      content.add(new JScrollPane(transcript),BorderLayout.CENTER);
      JPanel bottom = new JPanel();
      bottom.setBackground(Color.LIGHT_GRAY);
      bottom.setLayout(new BorderLayout(3,3));
      content.add(bottom,BorderLayout.SOUTH);
      input = new JTextField();
      input.setBackground(Color.WHITE);
      input.addActionListener(this);
      bottom.add(input,BorderLayout.CENTER);
      JButton send = new JButton("Send");
      send.addActionListener(this);
      bottom.add(send,BorderLayout.EAST);
      bottom.add(new JLabel("Text to send:"),BorderLayout.WEST);
      setContentPane(content);
   }

   
   /**
    *  This will be called when the user clicks the "Send" button or presses 
    *  return in the text-input box.  It posts the contents of the input box 
    *  to the transcript.  If the thread is not running, it creates and starts 
    *  a new thread.
    */
   public void actionPerformed(ActionEvent evt) {
      postMessage("SENT: " + input.getText());
      input.selectAll();
      input.requestFocus();
      if (running == false) {
         runner = new Thread(this);
         running = true;
         runner.start();
      }
   }
   

   /**
    * Add a line of text to the transcript area.
    * @param message text to be added; two line feeds is added at the end.
    */
   private void postMessage(String message) {
      transcript.append(message + "\n\n");
         // The following line is a nasty kludge that was the only way I could find to force
         // the transcript to scroll so that the text that was just added is visible in
         // the window.  Without this, text can be added below the bottom of the visible area
         // of the transcript.
      transcript.setCaretPosition(transcript.getDocument().getLength());
   }


   /**
    * The run method just posts messages to the transcript
    * at random intervals of 2 to 10 seconds.
    */
   public void run() {
      // 
      postMessage("RECEIVED: Hey, hello there! Nice to chat with you.");
      while (running) {
         try {
               // Wait a random time from 2000 to 10000 milliseconds.
            Thread.sleep( 2000 + (int)(8000*Math.random()) );
         }
         catch (InterruptedException e) {
         }
         int msgNum = (int)(Math.random() * incomingMessages.length);
         postMessage("RECEIVED: " + incomingMessages[msgNum]);
      }
   }
   

   /**
    * When applet is stopped, make sure that the thread will
    * terminate by setting running to false.
    */
   public void stop() {
      running = false;
      runner = null;
   }


} // end class ChatSimulation

