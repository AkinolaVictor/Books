package netgame.chat;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import netgame.common.*;

/* This class is a demo of the "netgame" package.  It's not exactly a game, but
 * it uses the netgame infrastructure of Hub + Clients to send and receive
 * messages in the chat room.  The chat room server is just a netgame Hub.
 * A ChatRoomWindow has a subclass that represents a Client for that Hub.
 */

/**
 * This class represents a client for a "chat room" application.  The chat
 * room is hosted by a server running on some computer.  The user of this
 * program must know the host name (or IP address) of the computer that
 * hosts the chat room.  When this program is run, it asks for that
 * information.  Then, it opens a window that has an input box where the
 * user can enter messages to be sent to the chat room.  The message is 
 * sent when the user presses return in the input box or when the
 * user clicks a Send button.  There is also a text area that shows 
 * a transcript of all messages from participants in the chat room.
 * <p>Participants in the chat room are represented only by ID numbers
 * that are assigned to them by the server when they connect.
 */
public class ChatRoomWindow extends JFrame {
   
   private final static int PORT = 37829; // The ChatRoom port number; can't be 
                                          // changed here unless the ChatRoomServer
                                          // program is also changed.

   /**
    * Gets the host name (or IP address) of the chat room server from the
    * user and opens a ChatRoomWindow.  The program ends when the user
    * closes the window.
    */
   public static void main(String[] args) {
      String host = JOptionPane.showInputDialog(
                   "Enter the host name of the\ncomputer that hosts the chat room:");
      if (host == null || host.trim().length() == 0)
         return;
      ChatRoomWindow window = new ChatRoomWindow(host);
      window.setLocation(200,100);
      window.setVisible(true);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }
   
   
   /**
    * A ChatClient connects to the Hub and is used to send messages to
    * and receive messages from a Hub.  Messages received from the
    * Hub will be of type ForwardedMessage and will contain the
    * ID number of the sender and the string that was sent by
    * that user.
    */
   private class ChatClient extends Client {

      /**
       * Opens a connection the chat room server on a specified computer.
       */
      ChatClient(String host) throws IOException {
         super(host, PORT);
      }

      /**
       * Responds when a message is received from the server.  It should be
       * a ForwardedMessage representing something that one of the participants
       * in the chat room is saying.  The message is simply added to the
       * transcript, along with the ID number of the sender.
       */
      protected void messageReceived(Object message) {
         if (message instanceof ForwardedMessage) {  // (no other message types are expected)
            ForwardedMessage bm = (ForwardedMessage)message;
            addToTranscript("#" + bm.senderID + " SAYS:  " + bm.message);
         }
      }

      /**
       * Called when the connection to the client is shut down because of some
       * error message.  (This will happen if the server program is terminated.)
       */
      protected void connectionClosedByError(String message) {
         addToTranscript("Sorry, communication has shut down due to an error:\n     " + message);
         sendButton.setEnabled(false);
         messageInput.setEnabled(false);
         messageInput.setEditable(false);
         messageInput.setText("");
         connected = false;
         connection = null;
      }

      /**
       * Posts a message to the transcript when someone leaves the chat room.
       */
      protected void playerConnected(int newPlayerID) {
         addToTranscript("Someone new has joined the chat room, with ID number " + newPlayerID);
      }

      /**
       * Posts a message to the transcript when someone leaves the chat room.
       */
      protected void playerDisconnected(int departingPlayerID) {
         addToTranscript("The person with ID number " + departingPlayerID + " has left the chat room");
      }

   } // end nested class ChatClient

   
   
   private JTextField messageInput;   // For entering messages to be sent to the chat room
   private JButton sendButton;        // Sends the contents of the messageInput.
   private JButton quitButton;        // Leaves the chat room cleanly, by sending a DisconnectMessage

   private JTextArea transcript;      // Contains all messages sent by chat room participant, as well
                                      // as a few additional status messages, such as when a new user arrives.
   
   private ChatClient connection;      // Represents the connection to the Hub; used to send messages;
                                       // also receives and processes messages from the Hub.
   
   private volatile boolean connected; // This is true while the client is connected to the hub.
   
   
   /**
    * Constructor creates the window and starts the process of connecting
    * to the hub; the actual connection is done in a separate thread.
    * @param host  The IP address or host name of the computer where the server is running.
    */
   private ChatRoomWindow(final String host) {
      super("Chat Room");
      setBackground(Color.BLACK);
      setLayout(new BorderLayout(2,2));
      transcript = new JTextArea(30,60);
      transcript.setLineWrap(true);
      transcript.setWrapStyleWord(true);
      transcript.setMargin(new Insets(5,5,5,5));
      transcript.setEditable(false);
      add(new JScrollPane(transcript), BorderLayout.CENTER);
      sendButton = new JButton("send");
      quitButton = new JButton("quit");
      messageInput = new JTextField(40);
      messageInput.setMargin(new Insets(3,3,3,3));
      ActionHandler ah = new ActionHandler();
      sendButton.addActionListener(ah);
      quitButton.addActionListener(ah);
      messageInput.addActionListener(ah);
      sendButton.setEnabled(false);
      messageInput.setEditable(false);
      messageInput.setEnabled(false);
      JPanel bottom = new JPanel();
      bottom.setBackground(Color.LIGHT_GRAY);
      bottom.add(new JLabel("You say:"));
      bottom.add(messageInput);
      bottom.add(sendButton);
      bottom.add(Box.createHorizontalStrut(30));
      bottom.add(quitButton);
      add(bottom,BorderLayout.SOUTH);
      pack();
      addWindowListener( new WindowAdapter() { // calls doQuit if user closes window
         public void windowClosing(WindowEvent e) {
            doQuit();
         }
      });
      new Thread() {
             // This is a thread that opens the connection to the server.  Since
             // that operation can block, it's not done directly in the constructor.
             // Once the connection is established, the user interface elements are
             // enabled so the user can send messages.  The Thread dies after
             // the connection is established or after an error occurs.
         public void run() {
            try {
               addToTranscript("Connecting to " + host + " ...");
               connection = new ChatClient(host);
               connected = true;
               messageInput.setEditable(true);
               messageInput.setEnabled(true);
               sendButton.setEnabled(true);
               messageInput.requestFocus();
            }
            catch (IOException e) {
               addToTranscript("Connection attempt failed.");
               addToTranscript("Error: " + e);
            }
         }
      }.start();
   }
   
   
   /**
    * Adds a string to the transcript area, followed by a blank line.
    */
   private void addToTranscript(String message) {
      transcript.append(message);
      transcript.append("\n\n");
           // The following line is a nasty kludge that was the only way I could find to force
           // the transcript to scroll so that the text that was just added is visible in
           // the window.  Without this, text can be added below the bottom of the visible area
           // of the transcript.
      transcript.setCaretPosition(transcript.getDocument().getLength());
   }
   
   
   /**
    * Called when the user clicks the Quit button or closes
    * the window by clicking its close box.
    */
   private void doQuit() {
      if (connected)
         connection.disconnect();  // Sends a DisconnectMessage to the server.
      dispose();
      try {
         Thread.sleep(1000); // Time for DisconnectMessage to actually be sent.
      }
      catch (InterruptedException e) {
      }
      System.exit(0);
   }

   
   /**
    * Defines the object that handles all ActionEvents for the program.
    */
   private class ActionHandler implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         Object src = evt.getSource();
         if (src == quitButton) {  // Disconnect from the server and end the program.
            doQuit();
         }
         else if (src == sendButton || src == messageInput) {
               // Send the string entered by the user as a message
               // to the Hub, using the ChatClient that handles communication
               // for this ChatRoomWindow.  Note that the string is not added
               // to the transcript here.  It will get added after the Hub
               // receives the message and broadcasts it to all clients,
               // including this one.
            String message = messageInput.getText();
            if (message.trim().length() == 0)
               return;
            connection.send(message);
            messageInput.selectAll();
            messageInput.requestFocus();
         }
      }
   }
   

}
