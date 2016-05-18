
import java.io.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.Scanner;

/**
 * This program lets the user keep a persistent "phone book" that
 * contains names and phone numbers.  The data for the phone book
 * is stored in a a file in the user's home directory.
 * 
 * The program is meant only as a demonstration of file use.  The phone
 * book data is stored is the form of Name/Number pairs, with
 * little error checking.  In particular, the "phone directory"
 * used in this program is not even close to what would be needed for
 * a real phone book application.
 */

public class PhoneDirectoryFileDemo {
   
   /**
    * The name of the file in which the phone book data is kept.  The
    * file is stored in the user's home directory.  The "." at the
    * beginning of the file name means that the file will be a
    * "hidden" file on Unix-based computers, including Linux and
    * Mac OS X.
    */
   private static String DATA_FILE_NAME = ".phone_book_demo";
   
   
   public static void main(String[] args) {
      
      String name, number;  // Name and number of an entry in the directory
                            // (used at various places in the program).

      TreeMap<String,String>  phoneBook;   // Phone directory data structure.
                                           // Entries are name/number pairs.
      
      phoneBook = new TreeMap<String,String>();
      
      
      /* Create a dataFile variable of type File to represent the
       * data file that is stored in the user's home directory.
       */
      
      File userHomeDirectory = new File( System.getProperty("user.home") );
      File dataFile = new File( userHomeDirectory, DATA_FILE_NAME );
      
      
      /* If the data file already exists, then the data in the file is
       * read and is used to initialize the phone directory.  The format
       * of the file must be as follows:  Each line of the file represents
       * one directory entry, with the name and the number for that entry
       * separated by the character '%'.  If a file exists but does not
       * have this format, then the program terminates; this is done to
       * avoid overwriting a file that is being used for another purpose.
       */
      
      if ( ! dataFile.exists() ) {
         System.out.println("No phone book data file found.");
         System.out.println("A new one will be created.");
         System.out.println("File name:  " + dataFile.getAbsolutePath());
      }
      else {
         System.out.println("Reading phone book data...");
         try {
            Scanner scanner = new Scanner( dataFile );
            while (scanner.hasNextLine()) {
               String phoneEntry = scanner.nextLine();
               int separatorPosition = phoneEntry.indexOf('%');
               if (separatorPosition == -1)
                  throw new IOException("File is not a phonebook data file.");
               name = phoneEntry.substring(0, separatorPosition);
               number = phoneEntry.substring(separatorPosition+1);
               phoneBook.put(name,number);
            }
         }
         catch (IOException e) {
            System.out.println("Error in phone book data file.");
            System.out.println("File name:  " + dataFile.getAbsolutePath());
            System.out.println("This program cannot continue.");
            System.exit(1);
         }
      }
      
      
      /* Read commands from the user and carry them out, until the
       * user gives the "Exit from program" command.
       */
      
      Scanner in = new Scanner( System.in );
      boolean changed = false;  // Have any changes been made to the directory?
      
      mainLoop: while (true) {
         System.out.println("\nSelect the action that you want to perform:");
         System.out.println("   1.  Look up a phone number.");
         System.out.println("   2.  Add or change a phone number.");
         System.out.println("   3.  Remove an entry from your phone directory.");
         System.out.println("   4.  List the entire phone directory.");
         System.out.println("   5.  Exit from the program.");
         System.out.println("Enter action number (1-5):  ");
         int command;
         if ( in.hasNextInt() ) {
            command = in.nextInt();
            in.nextLine();
         }
         else {
            System.out.println("\nILLEGAL RESPONSE.  YOU MUST ENTER A NUMBER.");
            in.nextLine();
            continue;
         }
         switch(command) {
         case 1:
            System.out.print("\nEnter the name whose number you want to look up: ");
            name = in.nextLine().trim().toLowerCase();
            number = phoneBook.get(name);
            if (number == null)
               System.out.println("\nSORRY, NO NUMBER FOUND FOR " + name);
            else
               System.out.println("\nNUMBER FOR " + name + ":  " + number);
            break;
         case 2:
            System.out.print("\nEnter the name: ");
            name = in.nextLine().trim().toLowerCase();
            if (name.length() == 0)
               System.out.println("\nNAME CANNOT BE BLANK.");
            else if (name.indexOf('%') >= 0)
               System.out.println("\nNAME CANNOT CONTAIN THE CHARACTER \"%\".");
            else { 
               System.out.print("Enter phone number: ");
               number = in.nextLine().trim();
               if (number.length() == 0)
                  System.out.println("\nPHONE NUMBER CANNOT BE BLANK.");
               else {
                  phoneBook.put(name,number);
                  changed = true;
               }
            }
            break;
         case 3:
            System.out.print("\nEnter the name whose entry you want to remove: ");
            name = in.nextLine().trim().toLowerCase();
            number = phoneBook.get(name);
            if (number == null)
               System.out.println("\nSORRY, THERE IS NO ENTRY FOR " + name);
            else {
               phoneBook.remove(name);
               changed = true;
               System.out.println("\nDIRECTORY ENTRY REMOVED FOR " + name);
            }
            break;
         case 4:
            System.out.println("\nLIST OF ENTRIES IN YOUR PHONE BOOK:\n");
            for ( Map.Entry<String,String> entry : phoneBook.entrySet() )
               System.out.println("   " + entry.getKey() + ": " + entry.getValue() );
            break;
         case 5:
            System.out.println("\nExiting program.");
            break mainLoop;
         default:
            System.out.println("\nILLEGAL ACTION NUMBER.");
         }
      }
      
      
      /* Before ending the program, write the current contents of the
       * phone directory, but only if some changes have been made to
       * the directory.
       */
      
      if (changed) {
         System.out.println("Saving phone directory changes to file " + 
               dataFile.getAbsolutePath() + " ...");
         PrintWriter out;
         try {
            out = new PrintWriter( new FileWriter(dataFile) );
         }
         catch (IOException e) {
            System.out.println("ERROR: Can't open data file for output.");
            return;
         }
         for ( Map.Entry<String,String> entry : phoneBook.entrySet() )
            out.println(entry.getKey() + "%" + entry.getValue() );
         out.close();
         if (out.checkError())
            System.out.println("ERROR: Some error occurred while writing data file.");
         else
            System.out.println("Done.");
      }
   
   }

}
