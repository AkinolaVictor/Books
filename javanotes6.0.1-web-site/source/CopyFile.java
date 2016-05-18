import java.io.*;

/**
 *  Makes a copy of a file.  The original file and the name of the
 *  copy must be given as command-line arguments.  In addition, the
 *  first command-line argument can be "-f"; if present, the program
 *  will overwrite an existing file; if not, the program will report
 *  an error and end if the output file already exists.  The number
 *  of bytes that are copied is reported.
 */
public class CopyFile {

   public static void main(String[] args) {

      String sourceName;   // Name of the source file, 
                           //    as specified on the command line.
      String copyName;     // Name of the copy, 
                           //    as specified on the command line.
      InputStream source;  // Stream for reading from the source file.
      OutputStream copy;   // Stream for writing the copy.
      boolean force;  // This is set to true if the "-f" option
                      //    is specified on the command line.
      int byteCount;  // Number of bytes copied from the source file.

      /* Get file names from the command line and check for the 
            presence of the -f option.  If the command line is not one
            of the two possible legal forms, print an error message and 
            end this program. */

      if (args.length == 3 && args[0].equalsIgnoreCase("-f")) {
         sourceName = args[1];
         copyName = args[2];
         force = true;
      }
      else if (args.length == 2) {
         sourceName = args[0];
         copyName = args[1];
         force = false;
      }
      else {
         System.out.println(
               "Usage:  java CopyFile <source-file> <copy-name>");
         System.out.println(
               "    or  java CopyFile -f <source-file> <copy-name>");
         return;
      }

      /* Create the input stream.  If an error occurs, end the program. */

      try {
         source = new FileInputStream(sourceName);
      }
      catch (FileNotFoundException e) {
         System.out.println("Can't find file \"" + sourceName + "\".");
         return;
      }

      /* If the output file already exists and the -f option was not
            specified, print an error message and end the program. */

      File file = new File(copyName);
      if (file.exists() && force == false) {
         System.out.println(
               "Output file exists.  Use the -f option to replace it.");
         return;  
      }

      /* Create the output stream.  If an error occurs, end the program. */

      try {
         copy = new FileOutputStream(copyName);
      }
      catch (IOException e) {
         System.out.println("Can't open output file \"" + copyName + "\".");
         return;
      }

      /* Copy one byte at a time from the input stream to the output
            stream, ending when the read() method returns -1 (which is 
            the signal that the end of the stream has been reached).  If any 
            error occurs, print an error message.  Also print a message if 
            the file has been copied successfully.  */

      byteCount = 0;

      try {
         while (true) {
            int data = source.read();
            if (data < 0)
               break;
            copy.write(data);
            byteCount++;
         }
         source.close();
         copy.close();
         System.out.println("Successfully copied " + byteCount + " bytes.");
      }
      catch (Exception e) {
         System.out.println("Error occurred while copying.  "
               + byteCount + " bytes copied.");
         System.out.println(e.toString());
      }

   }  // end main()


} // end class CopyFile
