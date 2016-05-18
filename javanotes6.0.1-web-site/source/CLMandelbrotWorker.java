import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * This class is part of a part of a demonstration of distributed computing.
 * It is to be used with CLMandelbrotTask.java on the worker computer.
 * CLMandelbortWorker should be run as a command-line program on each
 * worker computer involved in the distributed computation.  When it is
 * running, it listens for a connection from the master computer.  The 
 * listening port number can be specified on the command line; if none is 
 * specified, a default port number is used.  It is possible to run several
 * copies of this program on the same computer, as long as they listen
 * on different ports.
 * 
 * After receiving a connection request from the master program, this
 * program expects to receive a sequence of tasks (of type CLMandelbrotTask)
 * from the master program.  It computes each task that it receives and
 * sends the results back to the master.  The process ends when this
 * program receives a CLOSE_CONNECTION_COMMAND from the master program.
 * It then goes back to listening for another connection.  (The connection
 * can also be terminated by a SHUT_DOWN_COMMAND.  When this command is 
 * received, this program shuts down.  However, this feature is not currently 
 * used.  Since graceful shutdown is not implemented, you can stop the
 * worker program using CONTROL-C.)
 * 
 * Note that data sent over the network is encoded as text.  The first
 * word on a line of text identifies the type of data.
 */
public class CLMandelbrotWorker {

   /**
    * Default listening port, if none is specified on the command line.
    */
   private static final int DEFAULT_PORT = 13572;
   
   /**
    * The first and only word on a message representing a close command.
    */
   private static final String CLOSE_CONNECTION_COMMAND = "close";

   /**
    * The first and only word on a message representing a shutdown command.
    */
   private static final String SHUT_DOWN_COMMAND = "shutdown";


   /**
    * The first word on a message representing a CLMandelbrotTask.  This
    * is followed by the incoming data (id, maxIterations, y, xmin, dx, and 
    * count) for the task. Items on the line are separated by spaces.
    */
   private static final String TASK_COMMAND = "task";

   /**
    * The first word on a message representing the results from a
    * CLMandelbrotTask.  This is followed by the task id, the number
    * of items in the results, and then the results.  Items on the
    * line are separated by spaces.
    */
   private static final String RESULT_COMMAND = "result";

   private static boolean shutdownCommandReceived;

   
   /**
    * The main program listens for connections from the master program
    * and does all the communication over the connection.  Note that this
    * worker program does not use threads (except for the thread in which
    * the main program runs).
    */
   public static void main(String[] args) {
      
      /* Get the port number from the command line, if present. */

      int port = DEFAULT_PORT;

      if (args.length > 0) {
         try {
            port = Integer.parseInt(args[0]);
            if (port < 0 || port > 65535)
               throw new NumberFormatException();
         }
         catch (NumberFormatException e) {
            port = DEFAULT_PORT;
         }
      }

      System.out.println("Starting with listening port number " + port);

      while (shutdownCommandReceived == false) {
         
         /* Listen for a connection request from the master program. */

         ServerSocket listener = null;
         try {
            listener = new ServerSocket(port);
         }
         catch (Exception e) {
            System.out.println("ERROR: Can't create listening socket on port " + port);
            System.exit(1);
         }
         
         /* Process the connection.   Note that the listener socket is closed as
            long as the connection remains open, since this program can only
            deal with one connection at a time.  A new listener is created
            after the connection closes. */
         
         try {
            Socket connection = listener.accept();
            listener.close();
            System.out.println("Accepted connection from " + connection.getInetAddress());
            handleConnection(connection);
         }
         catch (Exception e) {
            System.out.println("ERROR: Server shut down with error:");
            System.out.println(e);
            System.exit(2);
         }
      }

      System.out.println("Shutting down normally.");

   } // end main()
   
   
   /**
    * Decode a message that was received from the server and that represents
    * a CLMandelbrotTask.
    * @param taskData the message that represents the task.  It is already known
    * that the first word of the message is the TASK_COMMAND.
    * @return the task represented by this message.
    * @throws IOException if the data in the message is not in the correct form.
    */
   private static CLMandelbrotTask readTask(String taskData) throws IOException {
      try {
         Scanner scanner = new Scanner(taskData);
         CLMandelbrotTask task = new CLMandelbrotTask();
         scanner.next();  // skip the command at the start of the line.
         task.id = scanner.nextInt();
         task.maxIterations = scanner.nextInt();
         task.y = scanner.nextDouble();
         task.xmin = scanner.nextDouble();
         task.dx = scanner.nextDouble();
         task.count = scanner.nextInt();
         return task;
      }
      catch (Exception e) {
         throw new IOException("Illegal data found while reading task information.");
      }
   }
   
   /**
    * Encode the result of a task into String form, so that it can be sent
    * as a message back to the master program.
    * @param task the task to be encoded.  Its compute() method has already
    * been executed.
    */
   private static String writeResults(CLMandelbrotTask task) {
      StringBuffer buffer = new StringBuffer();
      buffer.append(RESULT_COMMAND);
      buffer.append(' ');
      buffer.append(task.id);
      buffer.append(' ');
      buffer.append(task.count);
      for (int i = 0; i < task.count; i++) {
         buffer.append(' ');
         buffer.append(task.results[i]);
      }
      return buffer.toString();
   }
   
   
   /**
    * Handle communication over a connection to the master program.  Accept and
    * process CLMandelbrotTasks until a close or shutdown message is received
    * (or an error occurs).
    * @param connection an already-connected socket for the connection.
    */
   private static void handleConnection(Socket connection) {
      try {
         BufferedReader in = new BufferedReader( new InputStreamReader(
                                             connection.getInputStream()) );
         PrintWriter out = new PrintWriter(connection.getOutputStream());
         while (true) {
            String line = in.readLine();  // Message from the master.
            if (line == null) {
                  // End-of-stream encountered -- should not happen.
               throw new Exception("Connection closed unexpectedly.");
            }
            if (line.startsWith(CLOSE_CONNECTION_COMMAND)) {
                  // Represents the normal termination of the connection.
               System.out.println("Received close command.");
               break;
            }
            else if (line.startsWith(SHUT_DOWN_COMMAND)) {
                  // Represents the normal termination of the connection
                  // and also tells this worker to shut down.
               System.out.println("Received shutdown command.");
               shutdownCommandReceived = true;
               break;
            }
            else if (line.startsWith(TASK_COMMAND)) {
                  // Represents a CLMandelbrotTask that this worker is
                  // supposed to perform.
               CLMandelbrotTask task = readTask(line);  // Decode the message.
               task.compute();  // Perform the task.
               out.println(writeResults(task));  //  Send back the results.
               out.flush();
            }
            else {
                  // No other messages are part of the protocol.
               throw new Exception("Illegal copmmand received.");
            }
         }
      }
      catch (Exception e) {
         System.out.println("Client connection closed with error " + e);
      }
      finally {
         try {
            connection.close();  // Make sure the socket is closed.
         }
         catch (Exception e) {
         }
      }
   }


}
