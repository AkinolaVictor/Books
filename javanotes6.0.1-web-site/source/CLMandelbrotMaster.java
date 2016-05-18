import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.image.BufferedImage;


/**
 * This class is part of a part of a demonstration of distributed computing.
 * It is to be used with CLMandelbrotTask.java on the master computer.
 * Before starting CLMandelbrotMaster on the master computer, 
 * CLMandelbrotWorker should be started as a command-line program on each
 * worker computer involved in the distributed computation.  See
 * CLMandelbrotWorker.java for more information.  (It is possible to
 * run several copies of CLMandelbrotWorker on the same computer, as
 * long as each copy uses a different port number.  It is even possible
 * to run the master and one or more copies of the worker on the same
 * computer.  In that case, the host name for the workers would be
 * localhost.)
 * 
 * If CLMandelbrotMaster is run with no command line arguments, it will run
 * without using the network at all.  The complete computation will be
 * done locally (using just one processor.  You can do this to test how 
 * long the computation takes when distributed computing is not used.
 * 
 * Otherwise, the location of all the CLMandelbrotWorker programs must be
 * specified as command line arguments when CLMandelbrotMaster is run.  Each
 * command line argument gives the host name or IP address of a computer
 * on which a worker is running.  If that worker is not listening on the 
 * default port, then the listening port number must also be included.
 * The form for specifying a computer with a port number is, for example,
 * math.hws.edu:1501 or 127.0.0.1:18881, that is, the computer name or
 * IP, followed by a colon, followed by the port number, with NO SPACES.
 * 
 * When CLMandlebrotMaster runs, it creates a list of tasks (of type
 * CLMandelbrotTask) that have to be performed, and it creates a thread
 * for communicating with each copy of CLMandelbrotWorker.  Each thread
 * sends a sequence of tasks to the connected worker, which does the
 * actual work involved in performing the task and sends back the
 * results.
 * 
 * Although this program is meant as a demonstration of distributed
 * computing, it does compute an interesting picture.  If you want to
 * see that picture, uncomment the call to saveImage() at the end of
 * the main() routine.  The program computes the same picture every
 * time it is run.
 * 
 * Note that data sent over the network is encoded as text.  The first
 * word on a line of text identifies the type of data.
 */
public class CLMandelbrotMaster {

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
    * (This feature is currently unused.)
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

   /**
    * The list of tasks that must be performed to complete the computation.
    * This list is created by createJob().  The tasks are sent out to 
    * workers to be performed.  Each task represents the computation of
    * one row of an image of part of the Mandelbrot set.
    */
   private static ConcurrentLinkedQueue<CLMandelbrotTask> tasks;

   /**
    * The number of tasks that have been completed.  At the end of the
    * computation, this should be equal to the number of rows in the
    * image that is being computed.
    */
   private static int tasksCompleted;

   /**
    * Number of rows and columns in the image; set by computeJob().
    */
   private static int rows, columns;
   
   /**
    * Maximum number of iterations for the Mandelbrot computation;
    * set by computeJob().  For the purposes of this computation, it
    * is not necessary to understand this.
    */
   private static int maxIterations;

   /**
    * All the data for the image, collected from the results of all
    * the tasks.  mandelbrotData[c][r] is the data for row r,
    * column c in the image.
    */
   private static int[][] mandelbrotData;  
      


   /**
    * The main program opens network connection to CLMandelbrotWorker programs
    * and uses them to compute a visualization of a small piece of the famous
    * Mandelbrot set.  (For the purpose of this demonstration, it is not important
    * to know what that means.)
    * @param args the command line arguments of the program must be a list of all
    * the computers on which the CLMandelbrotWorker program is running, including
    * port numbers for the worker programs that are not listening on the default port.
    * (See the main comment on this class for more information.)
    */
   public static void main(String[] args) {
      
      long startTime = System.currentTimeMillis();
      
      createJob();  // Create the list of tasks that need to be computed.
      
      if (args.length == 0) { // Run non-distributed computation.
         
         System.out.println("Running on this computer only...");
         while (true) {
            CLMandelbrotTask task = tasks.poll();
            if (task == null)
               break;
            task.compute();
            finishTask(task);
         }
         
      }
      else {  // Run a distributed computation.
      
         WorkerConnection[] workers = new WorkerConnection[args.length];
         
         for (int i = 0; i < args.length; i++) {
               // Create the worker threads that communicate with the
               // CLMandelbrotWorker programs.  The threads start automatically
               // as soon as they are created.
            String host = args[i];
            int port = DEFAULT_PORT;
            int pos = host.indexOf(':');
            if (pos >= 0) {
                  // The host string contains a ":", which should be
                  // followed by the port number.
               String portString = host.substring(pos+1);
               host = host.substring(0,pos);  // Remove port from host string.
               try {
                  port = Integer.parseInt(portString);
               }
               catch (NumberFormatException e) {
               }
            }
            workers[i] = new WorkerConnection(i+1, host, port);
         }
         
         for (int i = 0; i < args.length; i++) {
                // Wait for all the threads to terminate.
            while (workers[i].isAlive()) {
               try {
                  workers[i].join();
               }
               catch (InterruptedException e) {
               }
            }
         }
   
         if (tasksCompleted != rows) {
               // Not all of the tasks were completed.  (Note: for a more robust
               // program, the remaining tasks could be executed here directly.)
            System.out.println("Something went wrong.  Only " + tasksCompleted);
            System.out.println("out of " + rows + " tasks were completed");
            System.exit(1);
         }
         
      }
      
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println("Finished in " + (elapsedTime/1000.0) + " seconds ");
      
      // saveImage();  // Uncomment this line if you would like to save the
                       // image that was computed by this program to a file.
   
   } // end main()
   
   
   /**
    * Creates the data needed for the computation and the list of tasks that
    * will perform parts of the computation.  For the purposes of this
    * computation, it is not necessary to understand the computation.
    */
   private static void createJob() {
      double xmin = -0.9548900066789311; // Region of xy-plane shown in the image.
      double xmax = -0.9548895970332226;
      double ymin = 0.2525416221154478;
      double ymax = 0.25254192934972913;
      maxIterations = 10000;
      rows = 768;
      columns = 1024;
      mandelbrotData = new int[rows][columns];
      double dx = (xmax - xmin)/(columns+1);
      double dy = (ymax - ymin)/(rows+1);
      tasks = new ConcurrentLinkedQueue<CLMandelbrotTask>();
      for (int j = 0; j < rows; j++) {  // Add tasks to the task list.
         CLMandelbrotTask task;
         task = new CLMandelbrotTask();
         task.id = j;
         task.maxIterations = maxIterations;
         task.y = ymax-j*dy;
         task.xmin = xmin;
         task.dx = dx;
         task.count = columns;
         tasks.add(task);
      }
   }
   
   
   /**
    * We allow for the possibility that a thread might fail while it is
    * performing a task.  When that happens, the thread drops the task
    * back into the list of tasks so that is can be assigned to another
    * worker.  That way, it is likely that all the tasks will be
    * completed even if some of the worker threads fail.  (However,
    * this is not completely foolproof -- if the failure occurs after
    * other worker threads have already terminated, there won't be
    * any threads left to execute the task.)
    */
   private static void reassignTask(CLMandelbrotTask task) {
      tasks.add(task);
   }
   
   
   /**
    * Add the data from a finished task to the array where the complete
    * set of data is collected.  Also increments tasksCompleted.  This
    * method is synchronized because of the race condition involved
    * in incrementing tasksCompleted.
    */
   synchronized private static void finishTask(CLMandelbrotTask task) {
      int row = task.id;
      System.arraycopy(task.results,0,mandelbrotData[row],0,columns);
      tasksCompleted++;
   }


   /**
    * Encode a CLMandelbrotTask into a String that can be sent as a
    * message over the network to one of the worker programs.
    */
   private static String writeTask(CLMandelbrotTask task) {
      StringBuffer buffer = new StringBuffer();
      buffer.append(TASK_COMMAND);
      buffer.append(' ');
      buffer.append(task.id);
      buffer.append(' ');
      buffer.append(task.maxIterations);
      buffer.append(' ');
      buffer.append(task.y);
      buffer.append(' ');
      buffer.append(task.xmin);
      buffer.append(' ');
      buffer.append(task.dx);
      buffer.append(' ');
      buffer.append(task.count);
      buffer.append(' ');
      return buffer.toString();
   }
   
   
   /**
    * Decode a message received over the network from one of the worker threads.
    * The message contains the results from a task.
    * @param data the message that contains the results.  It is already known
    * that the first word of the message is RESULT_COMMAND.
    * @param task the task for which results are expected.  The task id in the
    * results message must match the id of this task.  The results are stored
    * are stored as the value of task.results.
    * @throws Exception if any error is found in the data.
    */
   private static void readResults(String data, CLMandelbrotTask task) throws Exception {
      Scanner scanner = new Scanner(data);
      scanner.next();  // read "results" at beginning of line
      int id = scanner.nextInt();
      if (id != task.id)
         throw new IOException("Wrong task ID in results returned by worker");
      int count = scanner.nextInt();
      if (count != task.count)
         throw new IOException("Wrong data count in results returned by worker");
      task.results = new int[count];
      for (int i = 0; i < count; i++)
         task.results[i] = scanner.nextInt();
   }

   
   /**
    * This class represents one worker thread.  The job of a worker threads
    * is to send out tasks to a CLMandelbrotWorker program over a network
    * connection, and to get back the results computed by that program.
    */
   private static class WorkerConnection extends Thread {
      
      int id;        // Identifies this thread in output statements.
      String host;   // The host to which this thread will connect.
      int port;      // The port number to which this thread will connect.
      
      /**
       * The constructor just sets the values of the instance
       * variables id, host, and port and starts the thread.
       */
      WorkerConnection(int id, String host, int port) {
         this.id = id;
         this.host = host;
         this.port = port;
         start();
      }
      
      /**
       * The run() method of the thread opens a connection to the host and
       * port specified in the constructor, then sends tasks to the
       * CLMandelbrotWorker program on the other side of that connection.
       * If the thread terminates normally, it outputs  the number of tasks
       * that it processed.  If it terminates with an error, it outputs
       * an error message.
       */
      public void run() {
         
         int tasksCompleted = 0; // How many tasks has this thread handled.
         Socket socket;  // The socket for the connection.
         
         try {
            socket = new Socket(host,port);  // open the connection.
         }
         catch (Exception e) {
            System.out.println("Thread " + id + " could not open connection to " +
                  host + ":" + port);
            System.out.println("   Error: " + e);
            return;
         }
                  
         CLMandelbrotTask currentTask = null;
         CLMandelbrotTask nextTask = null;

         try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()) );
            currentTask = tasks.poll();
            if (currentTask != null) {
                  // Send first task to the worker program.
               String taskString = writeTask(currentTask);
               out.println(taskString);
               out.flush();
            }
            while (currentTask != null) {
               String resultString = in.readLine(); // Get results for currentTask.
               if (resultString == null)
                  throw new IOException("Connection closed unexpectedly.");
               if (! resultString.startsWith(RESULT_COMMAND))
                  throw new IOException("Illegal string received from worker.");
               nextTask = tasks.poll();  // Get next task and send it to worker.
               if (nextTask != null) {
                     // Send nextTask to worker before processing results for 
                     // currentTask, so that the worker can work on nextTask
                     // while the currentTask results are processed.
                  String taskString = writeTask(nextTask);
                  out.println(taskString);
                  out.flush();
               }
               readResults(resultString, currentTask); 
               finishTask(currentTask);  // Process results from currentTask.
               tasksCompleted++;
               currentTask = nextTask;   // We are finished with old currentTask.
               nextTask = null;
            }
            out.println(CLOSE_CONNECTION_COMMAND);  // Send close command to worker.
            out.flush();
         }
         catch (Exception e) {
            System.out.println("Thread " + id + " terminated because of an error");
            System.out.println("   Error: " + e);
            e.printStackTrace();
               // Put uncompleted tasks, if any, back into the task list.
            if (currentTask != null)
               reassignTask(currentTask);
            if (nextTask != null)
               reassignTask(nextTask);
         }
         finally {
            System.out.println("Thread " + id + " ending after completing " + 
                  tasksCompleted + " tasks");
            try {
               socket.close();
            }
            catch (Exception e) {
            }
         }
         
      } //end run()
      
   } // end nested class WorkerConnection

   
   /**
    * Although this program is meant as a demonstration of distributed computing,
    * it does compute something interesting.  This method will save the image
    * that was computed by the program to a file.  It first asks the user whether
    * the user wants to save the image.  If the answer is yes, a file dialog box
    * is put up where the user can specify the file in which the image is to
    * be saved.  NOTE that this method will not be called unless you uncomment
    * the call to this method at the end of the main program.
    */
   private static void saveImage() {
      Scanner in = new Scanner(System.in);
      System.out.println();
      while (true) {
         System.out.println("Computation complete.  Do you want to save the image?");
         String line = in.nextLine().trim().toLowerCase();
         if (line.equals("no") || line.equals("n"))
            break;
         else if (line.equals("yes") || line.equals("y")) {
            JFileChooser fileDialog = new JFileChooser();
            fileDialog.setSelectedFile(new File("CLMandelbrot_image.png")); 
            fileDialog.setDialogTitle("Select File to be Saved");
            int option = fileDialog.showSaveDialog(null);
            if (option != JFileChooser.APPROVE_OPTION)
               return;  // User canceled or clicked the dialog's close box.
            File selectedFile = fileDialog.getSelectedFile();
            if (selectedFile.exists()) {  // Ask the user whether to replace the file.
               int response = JOptionPane.showConfirmDialog( null,
                     "The file \"" + selectedFile.getName()
                     + "\" already exists.\nDo you want to replace it?", 
                     "Confirm Save",
                     JOptionPane.YES_NO_OPTION, 
                     JOptionPane.WARNING_MESSAGE );
               if (response != JOptionPane.YES_OPTION)
                  return;  // User does not want to replace the file.
            }
            try {
               int[] palette = new int[250];
               for (int i = 0; i < 250; i++) {
                  Color c = new Color(i,i,i);
                  palette[i] = c.getRGB();
               }
               BufferedImage OSI = new BufferedImage(columns,rows,BufferedImage.TYPE_INT_RGB);
               int[] rgb = new int[columns];
               for (int row = 0; row < rows; row++) {
                  for (int col = 0; col < columns; col++) {
                     if (mandelbrotData[row][col] == maxIterations)
                        rgb[col] = 0;
                     else
                        rgb[col] = palette[ (int)((mandelbrotData[row][col] * 250.0)/maxIterations) ];
                  }
                  OSI.setRGB(0,row,columns,1,rgb,0,1024);
               }
               boolean hasPNG = ImageIO.write(OSI,"PNG",selectedFile);
               if ( ! hasPNG )
                  throw new Exception("PNG format not available!!??"); // (It should always be.)
            }
            catch (Exception e) {
               System.out.println("Sorry, but an error occurred while trying to save the image.");
               e.printStackTrace();
            }
            break;
         }
         else
            System.out.println("Answer yes or no.");
      }
   }


} // end CLMandelbrotMaster
