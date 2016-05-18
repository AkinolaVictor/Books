import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.net.*;


/**
 * Opens a window that can be used for a two-way network chat.
 * The window can "listen" for a connection request on a port
 * that is specified by the user.  It can request a connection
 * to another GUIChat window on a specified computer and port.
 * The window has an input box where the user can enter
 * messages to be sent over the connection.  A connection
 * can be closed by clicking a button in the window or by
 * closing the window.   It is possible to open additional 
 * windows to support simultaneous chats (or to test the program 
 * by opening a connection from one window to another).
 * This class contains a main() routine, so it can be run as
 * a stand-alone application.
 */
public class GUIChat extends JFrame {
   
   /**
    * Possible states of the thread that handles the network connection.
    */
   private enum ConnectionState { LISTENING, CONNECTING, CONNECTED, CLOSED }
   
   /**
    * Default port number.  This is the initial content of input boxes in
    * the window that specify the port number for the connection. 
    */
   private static String defaultPort = "1501";
   
   /**
    * Default host name.  This is the initial content of the input box that
    * specifies the name of the computer to which a connections request
    * will be sent.
    */
   private static String defaultHost = "localhost";
         
   /**
    * Used to keep track of where on the screen the previous window
    * was opened, so that the next window can be placed at a 
    * different position.
    */
   private static Point previousWindowLocation;
   
   /**
    * The number of windows that are currently open.  If this drops to
    * zero, then the program is terminated by calling System.exit();
    */
   private static int openWindowCount;
   
   /**
    * The number of windows that have been created.  This is used
    * in the title bar of the second and subsequent windows.
    */
   private static int windowsCreated;
   
   /**
    * The thread that handles the connection; defined by a nested class.
    */
   private ConnectionHandler connection;
   
   
   /**
    * The main() routine makes it possible to run this class as an
    * application; it just creates a GUIChat window and makes it visible.
    */
   public static void main(String[] args) {
      GUIChat window = new GUIChat();
      window.setVisible(true);
   }
   
      
   /**
    * Control buttons that appear in the window.
    */
   private JButton newButton, listenButton, connectButton, closeButton, 
                         clearButton, quitButton, saveButton, sendButton;
   
   /**
    * Input boxes for connection information (port numbers and host names).
    */
   private JTextField listeningPortInput, remotePortInput, remoteHostInput;

   /**
    * Input box for messages that will be sent to the other side of the
    * network connection.
    */
   private JTextField messageInput;
   
   /**
    * Contains a transcript of messages sent and received, along with
    * information about the progress and state of the connection.
    */
   private JTextArea transcript;
   
   
   /**
    * Constructor creates a window with a default title.  The
    * constructor does not make the window visible.
    */
   public GUIChat() {
      this( windowsCreated == 0 ? "Chat Window" :
                           "Chat Window #" + (windowsCreated+1) );
   }
   
   /**
    * Constructor creates a window with a specified title.  The
    * constructor does not make the window visible.
    */
   public GUIChat(String title) {
      
      super(title);
      
      ActionListener actionHandler = new ActionHandler();
      newButton = new JButton("New");
      newButton.addActionListener(actionHandler);
      listenButton = new JButton("Listen on port:");
      listenButton.addActionListener(actionHandler);
      connectButton = new JButton("Connect to:");
      connectButton.addActionListener(actionHandler);
      closeButton = new JButton("Disconnect");
      closeButton.addActionListener(actionHandler);
      closeButton.setEnabled(false);
      clearButton = new JButton("Clear Transcript");
      clearButton.addActionListener(actionHandler);
      sendButton = new JButton("Send");
      sendButton.addActionListener(actionHandler);
      sendButton.setEnabled(false);
      saveButton = new JButton("Save Transcript");
      saveButton.addActionListener(actionHandler);
      quitButton = new JButton("Quit");
      quitButton.addActionListener(actionHandler);
      messageInput = new JTextField();
      messageInput.addActionListener(actionHandler);
      messageInput.setEditable(false);
      transcript = new JTextArea(20,60);
      transcript.setLineWrap(true);
      transcript.setWrapStyleWord(true);
      transcript.setEditable(false);
      listeningPortInput = new JTextField(defaultPort,5);
      remotePortInput = new JTextField(defaultPort,5);
      remoteHostInput = new JTextField(defaultHost,18);
      
      JPanel content = new JPanel();
      content.setLayout(new BorderLayout(3,3));
      content.setBackground(Color.GRAY);
      JPanel topPanel = new JPanel();
      topPanel.setLayout(new GridLayout(2,1,3,3));
      topPanel.setBackground(Color.GRAY);
      JPanel buttonBar = new JPanel();
      buttonBar.setLayout(new FlowLayout(FlowLayout.CENTER,3,3));
      JPanel connectBar = new JPanel();
      connectBar.setLayout(new FlowLayout(FlowLayout.CENTER,3,3));
      JPanel inputBar = new JPanel();
      inputBar.setLayout(new BorderLayout(3,3));
      inputBar.setBackground(Color.GRAY);
      
      content.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
      content.add(topPanel, BorderLayout.NORTH);
      topPanel.add(connectBar);
      topPanel.add(buttonBar);
      content.add(inputBar, BorderLayout.SOUTH);
      content.add(new JScrollPane(transcript));
      buttonBar.add(newButton);
      buttonBar.add(quitButton);
      buttonBar.add(saveButton);
      buttonBar.add(clearButton);
      buttonBar.add(closeButton);
      connectBar.add(listenButton);
      connectBar.add(listeningPortInput);
      connectBar.add(Box.createHorizontalStrut(12));
      connectBar.add(connectButton);
      connectBar.add(remoteHostInput);
      connectBar.add(new JLabel("port:"));
      connectBar.add(remotePortInput);
      inputBar.add(new JLabel("Your Message:"), BorderLayout.WEST);
      inputBar.add(messageInput, BorderLayout.CENTER);
      inputBar.add(sendButton, BorderLayout.EAST);
      
      setContentPane(content);
      
      pack();
      if (previousWindowLocation == null)
         previousWindowLocation = new Point(40,80);
      else {
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         previousWindowLocation.x += 50;
         if (previousWindowLocation.x + getWidth() > screenSize.width)
            previousWindowLocation.x = 10;
         previousWindowLocation.y += 30;
         if (previousWindowLocation.y + getHeight() > screenSize.height)
            previousWindowLocation.y = 50;
      }
      setLocation(previousWindowLocation);
      
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      openWindowCount++;
      windowsCreated++;

      addWindowListener( new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            if (connection != null && 
                  connection.getConnectionState() != ConnectionState.CLOSED) {
               connection.close();
            }
            openWindowCount--;
            if (openWindowCount == 0) {
               try {
                  System.exit(0);
               }
               catch (SecurityException e) {
               }
            }
         }
      });
      
   } // end constructor
   
   
   /**
    * Defines responses to buttons, and when the user presses return
    * in the message input box.
    */
   private class ActionHandler implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         Object source = evt.getSource();
         if (source == newButton) {
            GUIChat window = new GUIChat();
            window.setVisible(true);
         }
         else if (source == listenButton) {
            if (connection == null || 
                  connection.getConnectionState() == ConnectionState.CLOSED) {
               String portString = listeningPortInput.getText();
               int port;
               try {
                  port = Integer.parseInt(portString);
                  if (port < 0 || port > 65535)
                     throw new NumberFormatException();
               }
               catch (NumberFormatException e) {
                  JOptionPane.showMessageDialog(GUIChat.this, 
                        portString +"is not a legal port number.");
                  return;
               }
               connectButton.setEnabled(false);
               listenButton.setEnabled(false);
               closeButton.setEnabled(true);
               connection = new ConnectionHandler(port);
            }
         }
         else if (source == connectButton) {
            if (connection == null || 
                  connection.getConnectionState() == ConnectionState.CLOSED) {
               String portString = remotePortInput.getText();
               int port;
               try {
                  port = Integer.parseInt(portString);
                  if (port < 0 || port > 65535)
                     throw new NumberFormatException();
               }
               catch (NumberFormatException e) {
                  JOptionPane.showMessageDialog(GUIChat.this, 
                        portString +"is not a legal port number.");
                  return;
               }
               connectButton.setEnabled(false);
               listenButton.setEnabled(false);
               connection = new ConnectionHandler(remoteHostInput.getText(),port);
            }
         }
         else if (source == closeButton) {
            if (connection != null)
               connection.close();
         }
         else if (source == clearButton) {
            transcript.setText("");
         }
         else if (source == quitButton) {
            try {
               System.exit(0);
            }
            catch (SecurityException e) {
            }
         }
         else if (source == saveButton) {
            doSave();
         }
         else if (source == sendButton || source == messageInput) {
            if (connection != null && 
                  connection.getConnectionState() == ConnectionState.CONNECTED) {
               connection.send(messageInput.getText());
               messageInput.selectAll();
               messageInput.requestFocus();
            }
         }
      }
   }
   
   
   /**
    * Save the contents of the transcript area to a file selected by the user.
    */
   private void doSave() {
      JFileChooser fileDialog = new JFileChooser(); 
      File selectedFile;  //Initially selected file name in the dialog.
      selectedFile = new File("transcript.txt");
      fileDialog.setSelectedFile(selectedFile); 
      fileDialog.setDialogTitle("Select File to be Saved");
      int option = fileDialog.showSaveDialog(this);
      if (option != JFileChooser.APPROVE_OPTION)
         return;  // User canceled or clicked the dialog's close box.
      selectedFile = fileDialog.getSelectedFile();
      if (selectedFile.exists()) {  // Ask the user whether to replace the file.
         int response = JOptionPane.showConfirmDialog( this,
               "The file \"" + selectedFile.getName()
               + "\" already exists.\nDo you want to replace it?", 
               "Confirm Save",
               JOptionPane.YES_NO_OPTION, 
               JOptionPane.WARNING_MESSAGE );
         if (response != JOptionPane.YES_OPTION)
            return;  // User does not want to replace the file.
      }
      PrintWriter out; 
      try {
         FileWriter stream = new FileWriter(selectedFile); 
         out = new PrintWriter( stream );
      }
      catch (Exception e) {
         JOptionPane.showMessageDialog(this,
            "Sorry, but an error occurred while trying to open the file:\n" + e);
         return;
      }
      try {
         out.print(transcript.getText());  // Write text from the TextArea to the file.
         out.close();
         if (out.checkError())   // (need to check for errors in PrintWriter)
            throw new IOException("Error check failed.");
      }
      catch (Exception e) {
         JOptionPane.showMessageDialog(this,
            "Sorry, but an error occurred while trying to write the text:\n" + e);
      }   
   }
   
   
   /**
    * Add a line of text to the transcript area.
    * @param message text to be added; a line feed is added at the end.
    */
   private void postMessage(String message) {
      transcript.append(message + '\n');
         // The following line is a nasty kludge that was the only way I could find to force
         // the transcript to scroll so that the text that was just added is visible in
         // the window.  Without this, text can be added below the bottom of the visible area
         // of the transcript.
      transcript.setCaretPosition(transcript.getDocument().getLength());
   }
   
   
   /**
    * Defines the thread that handles the connection.  The thread is responsible
    * for opening the connection and for receiving messages.  This class contains
    * several methods that are called by the main class, and that are therefore
    * executed in a different thread.  Note that by using a thread to open the
    * connection, any blocking of the graphical user interface is avoided.  By
    * using a thread for reading messages sent from the other side, the messages
    * can be received and posted to the transcript asynchronously at the same
    * time as the user is typing and sending messages.
    */
   private class ConnectionHandler extends Thread {
      
      private volatile ConnectionState state;
      private String remoteHost;
      private int port;
      private ServerSocket listener;
      private Socket socket;
      private PrintWriter out;
      private BufferedReader in;
      
      /**
       * Listen for a connection on a specified port.  The constructor
       * does not perform any network operations; it just sets some
       * instance variables and starts the thread.  Note that the
       * thread will only listen for one connection, and then will
       * close its server socket.
       */
      ConnectionHandler(int port) {
         state = ConnectionState.LISTENING;
         this.port = port;
         postMessage("\nLISTENING ON PORT " + port + "\n");
         start();
      }
      
      /**
       * Open a connection to specified computer and port.  The constructor
       * does not perform any network operations; it just sets some
       * instance variables and starts the thread.
       */
      ConnectionHandler(String remoteHost, int port) {
         state = ConnectionState.CONNECTING;
         this.remoteHost = remoteHost;
         this.port = port;
         postMessage("\nCONNECTING TO " + remoteHost + " ON PORT " + port + "\n");
         start();
      }
      
      /**
       * Returns the current state of the connection.  
       */
      synchronized ConnectionState getConnectionState() {
         return state;
      }
      
      /**
       * Send a message to the other side of the connection, and post the
       * message to the transcript.  This should only be called when the
       * connection state is ConnectionState.CONNECTED; if it is called at
       * other times, it is ignored.
       */
      synchronized void send(String message) {
         if (state == ConnectionState.CONNECTED) {
            postMessage("SEND:  " + message);
            out.println(message);
            out.flush();
            if (out.checkError()) {
               postMessage("\nERROR OCCURRED WHILE TRYING TO SEND DATA.");
               close();
            }
         }
      }
      
      /**
       * Close the connection. If the server socket in non-null, the
       * server socket is closed, which will cause its accept() method to
       * fail with an error.  If the socket is non-null, then the socket
       * is closed, which will cause its input method to fail with an
       * error.  (However, these errors will not be reported to the user.)
       */
      synchronized void close() {
         state = ConnectionState.CLOSED;
         try {
            if (socket != null)
               socket.close();
            else if (listener != null)
               listener.close();
         }
         catch (IOException e) {
         }
      }
      
      /**
       * This is called by the run() method when a message is received from
       * the other side of the connection.  The message is posted to the
       * transcript, but only if the connection state is CONNECTED.  (This
       * is because a message might be received after the user has clicked
       * the "Disconnect" button; that message should not be seen by the
       * user.)
       */
      synchronized private void received(String message) {
         if (state == ConnectionState.CONNECTED)
            postMessage("RECEIVE:  " + message);
      }
      
      /**
       * This is called by the run() method when the connection has been
       * successfully opened.  It enables the correct buttons, writes a
       * message to the transcript, and sets the connected state to CONNECTED.
       */
      synchronized private void connectionOpened() throws IOException {
         listener = null;
         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         out = new PrintWriter(socket.getOutputStream());
         state = ConnectionState.CONNECTED;
         closeButton.setEnabled(true);
         sendButton.setEnabled(true);
         messageInput.setEditable(true);
         messageInput.setText("");
         messageInput.requestFocus();
         postMessage("CONNECTION ESTABLISHED\n");
      }
      
      /**
       * This is called by the run() method when the connection is closed
       * from the other side.  (This is detected when an end-of-stream is
       * encountered on the input stream.)  It posts a mesaage to the
       * transcript and sets the connection state to CLOSED.
       */
      synchronized private void connectionClosedFromOtherSide() {
         if (state == ConnectionState.CONNECTED) {
            postMessage("\nCONNECTION CLOSED FROM OTHER SIDE\n");
            state = ConnectionState.CLOSED;
         }
      }
      
      /**
       * Called from the finally clause of the run() method to clean up
       * after the network connection closes for any reason.
       */
      private void cleanUp() {
         state = ConnectionState.CLOSED;
         listenButton.setEnabled(true);
         connectButton.setEnabled(true);
         closeButton.setEnabled(false);
         sendButton.setEnabled(false);
         messageInput.setEditable(false);
         postMessage("\n*** CONNECTION CLOSED ***\n");
         if (socket != null && !socket.isClosed()) {
               // Make sure that the socket, if any, is closed.
            try {
               socket.close();
            }
            catch (IOException e) {
            }
         }
         socket = null;
         in = null;
         out = null;
         listener = null;
      }
      
      
      /**
       * The run() method that is executed by the thread.  It opens a
       * connection as a client or as a server (depending on which 
       * constructor was used).
       */
      public void run() {
         try {
            if (state == ConnectionState.LISTENING) {
                  // Open a connection as a server.
               listener = new ServerSocket(port);
               socket = listener.accept();
               listener.close();
            }
            else if (state == ConnectionState.CONNECTING) {
                  // Open a connection as a client.
               socket = new Socket(remoteHost,port);
            }
            connectionOpened();  // Set up to use the connection.
            while (state == ConnectionState.CONNECTED) {
                  // Read one line of text from the other side of
                  // the connection, and report it to the user.
               String input = in.readLine();
               if (input == null)
                  connectionClosedFromOtherSide();
               else
                  received(input);  // Report message to user.
            }
         }
         catch (Exception e) {
               // An error occurred.  Report it to the user, but not
               // if the connection has been closed (since the error
               // might be the expected error that is generated when
               // a socket is closed).
            if (state != ConnectionState.CLOSED)
               postMessage("\n\n ERROR:  " + e);
         }
         finally {  // Clean up before terminating the thread.
            cleanUp();
         }
      }
      
   } // end nested class ConnectionHandler

}
