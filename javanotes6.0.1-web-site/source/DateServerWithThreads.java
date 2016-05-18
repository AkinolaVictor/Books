import java.net.*;
import java.io.*;
import java.util.Date;

/**
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program sends the current time to
 * the connected socket.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example). 
 * 
 * This version of the program creates a new thread for
 * every connection request.
 */
public class DateServerWithThreads {

   public static final int LISTENING_PORT = 32007;

   public static void main(String[] args) {

      ServerSocket listener;  // Listens for incoming connections.
      Socket connection;      // For communication with the connecting program.

      /* Accept and process connections forever, or until some error occurs. */

      try {
         listener = new ServerSocket(LISTENING_PORT);
         System.out.println("Listening on port " + LISTENING_PORT);
         while (true) {
                // Accept next connection request and handle it.
            connection = listener.accept(); 
            ConnectionHandler handler = new ConnectionHandler(connection);
            handler.start();
         }
      }
      catch (Exception e) {
         System.out.println("Sorry, the server has shut down.");
         System.out.println("Error:  " + e);
         return;
      }

   }  // end main()


   /**
    *  Defines a thread that handles the connection with one
    *  client.
    */
   private static class ConnectionHandler extends Thread {
      Socket client;
      ConnectionHandler(Socket socket) {
         client = socket;
      }
      public void run() {
         String clientAddress = client.getInetAddress().toString();
         try {
            System.out.println("Connection from " + clientAddress );
            Date now = new Date();  // The current data and time.
            PrintWriter outgoing;   // Stream for sending data.
            outgoing = new PrintWriter( client.getOutputStream() );
            outgoing.println( now.toString() );
            outgoing.flush();  // Make sure the data is actually sent!
            client.close();
         }
         catch (Exception e){
            System.out.println("Error on connection with: " 
                  + clientAddress + ": " + e);
         }
      }
   }
   

} //end class DateServer
