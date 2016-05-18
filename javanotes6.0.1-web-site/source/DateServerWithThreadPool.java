import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program sends the current time to
 * the connected socket.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example). 
 * 
 * This version of the program uses a thread pool of worker
 * threads that handle the connections.
 */
public class DateServerWithThreadPool {

   public static final int LISTENING_PORT = 32007;
   
   private static final int THREAD_POOL_SIZE = 5;
   
   private static final int QUEUE_CAPACITY = 10;
   
   /**
    * The connectionQueue is used to send connected sockets from the
    * main program to the worker threads.  When a connection request
    * is received, the connected socket is placed into the queue.
    * Worker threads retrieve sockets from the queue as they become
    * available.  This is an ArrayBlockingQueue, with a limited
    * capacity, to prevent the number of clients who are waiting
    * for service in the queue from becoming too large.
    */
   private static ArrayBlockingQueue<Socket> connectionQueue;

   public static void main(String[] args) {

      ServerSocket listener;  // Listens for incoming connections.
      Socket connection;      // For communication with the connecting program.

      /* Create a listening socket, create the thread pool, then accept and 
       * process connection requests forever.  Note that the connection queue
       * MUST be created before the threads are created, since a thread tries
       * to use the queue as soon as it is started.  Once created, the thread
       * will immediately block until a socket becomes available in the queue.
       */

      try {
         
         listener = new ServerSocket(LISTENING_PORT);

         connectionQueue = new ArrayBlockingQueue<Socket>(QUEUE_CAPACITY);
         for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            new ConnectionHandler();  // Create the thread; it starts itself.
         }
         
         System.out.println("Listening on port " + LISTENING_PORT);
         while (true) {
                // Accept next connection request and put it in the queue.
            connection = listener.accept();
            try {
               connectionQueue.put(connection); // Blocks if queue is full.
            }
            catch (InterruptedException e) {
            }
         }
      }
      catch (Exception e) {
         System.out.println("Sorry, the server has shut down.");
         System.out.println("Error:  " + e);
         return;
      }

   }  // end main()


   /**
    *  Defines one of the threads in the thread pool.  Each thread runs
    *  in an infinite loop in which it takes a connection from the connection
    *  queue and handles communication with that client.  The thread starts
    *  itself in its constructor.  The constructor also sets the thread
    *  to be a daemon thread.  (A program will end if all remaining
    *  threads are daemon threads.)
    */
   private static class ConnectionHandler extends Thread {
      ConnectionHandler() {
         setDaemon(true);
         start();
      }
      public void run() {
         while (true) {
            Socket client;
            try {
               client = connectionQueue.take();
            }
            catch (InterruptedException e) {
               continue; // (If interrupted, just go back to start of while loop.)
            }
            String clientAddress = client.getInetAddress().toString();
            try {
               System.out.println("Connection from " + clientAddress );
               System.out.println("Handled by thread " + this);
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
   }
   

} //end class DateServerWithThreadPool
