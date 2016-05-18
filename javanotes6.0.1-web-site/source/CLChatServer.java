import java.net.*;
import java.io.*;

/**
 * This program is one end of a simple command-line interface chat program.
 * It acts as a server which waits for a connection from the CLChatClient 
 * program.  The port on which the server listens can be specified as a 
 * command-line argument.  If it is not, then the port specified by the
 * constant DEFAULT_PORT is used.  Note that if a port number of zero is 
 * specified, then the server will listen on any available port.
 * This program only supports one connection.  As soon as a connection is 
 * opened, the listening socket is closed down.  The two ends of the connection
 * each send a HANDSHAKE string to the other, so that both ends can verify
 * that the program on the other end is of the right type.  Then the connected 
 * programs alternate sending messages to each other.  The client always sends 
 * the first message.  The user on either end can close the connection by 
 * entering the string "quit" when prompted for a message.  Note that the first 
 * character of any string sent over the connection must be 0 or 1; this
 * character is interpreted as a command.
 */
public class CLChatServer {

   /**
    * Port to listen on, if none is specified on the command line.
    */
   static final int DEFAULT_PORT = 1728;

   /**
    * Handshake string. Each end of the connection sends this  string to the 
    * other just after the connection is opened.  This is done to confirm that 
    * the program on the other side of the connection is a CLChat program.
    */
   static final String HANDSHAKE = "CLChat";

   /**
    * This character is prepended to every message that is sent.
    */
   static final char MESSAGE = '0';

   /**
    * This character is sent to the connected program when the user quits.
    */
   static final char CLOSE = '1';

   
   public static void main(String[] args) {

      int port;   // The port on which the server listens.

      ServerSocket listener;  // Listens for a connection request.
      Socket connection;      // For communication with the client.

      BufferedReader incoming;  // Stream for receiving data from client.
      PrintWriter outgoing;     // Stream for sending data to client.
      String messageOut;        // A message to be sent to the client.
      String messageIn;         // A message received from the client.
      
      BufferedReader userInput; // A wrapper for System.in, for reading
                                // lines of input from the user.

      /* First, get the port number from the command line,
            or use the default port if none is specified. */

      if (args.length == 0) 
         port = DEFAULT_PORT;
      else {
         try {
            port= Integer.parseInt(args[0]);
            if (port < 0 || port > 65535)
               throw new NumberFormatException();
         }
         catch (NumberFormatException e) {
            System.out.println("Illegal port number, " + args[0]);
            return;
         }
      }

      /* Wait for a connection request.  When it arrives, close
           down the listener.  Create streams for communication
           and exchange the handshake. */

      try {
         listener = new ServerSocket(port);
         System.out.println("Listening on port " + listener.getLocalPort());
         connection = listener.accept();
         listener.close();  
         incoming = new BufferedReader( 
                        new InputStreamReader(connection.getInputStream()) );
         outgoing = new PrintWriter(connection.getOutputStream());
         outgoing.println(HANDSHAKE);  // Send handshake to client.
         outgoing.flush();
         messageIn = incoming.readLine();  // Receive handshake from client.
         if (! HANDSHAKE.equals(messageIn) ) {
            throw new Exception("Connected program is not a CLChat!");
         }
         System.out.println("Connected.  Waiting for the first message.");
      }
      catch (Exception e) {
         System.out.println("An error occurred while opening connection.");
         System.out.println(e.toString());
         return;
      }

      /* Exchange messages with the other end of the connection until one side
         or the other closes the connection.  This server program waits for 
         the first message from the client.  After that, messages alternate 
         strictly back and forth. */

      try {
         userInput = new BufferedReader(new InputStreamReader(System.in));
         System.out.println("NOTE: Enter 'quit' to end the program.\n");
         while (true) {
            System.out.println("WAITING...");
            messageIn = incoming.readLine();
            if (messageIn.length() > 0) {
                   // The first character of the message is a command. If 
                   // the command is CLOSE, then the connection is closed.  
                   // Otherwise, remove the command character from the 
                   // message and procede.
               if (messageIn.charAt(0) == CLOSE) {
                  System.out.println("Connection closed at other end.");
                  connection.close();
                  break;
               }
               messageIn = messageIn.substring(1);
            }
            System.out.println("RECEIVED:  " + messageIn);
            System.out.print("SEND:      ");
            messageOut = userInput.readLine();
            if (messageOut.equalsIgnoreCase("quit"))  {
                  // User wants to quit.  Inform the other side
                  // of the connection, then close the connection.
               outgoing.println(CLOSE);
               outgoing.flush();  // Make sure the data is sent!
               connection.close();
               System.out.println("Connection closed.");
               break;
            }
            outgoing.println(MESSAGE + messageOut);
            outgoing.flush(); // Make sure the data is sent!
            if (outgoing.checkError()) {
               throw new IOException("Error occurred while transmitting message.");
            }
         }
      }
      catch (Exception e) {
         System.out.println("Sorry, an error has occurred.  Connection lost.");
         System.out.println("Error:  " + e);
         System.exit(1);
      }

   }  // end main()



} //end class CLChatServer
