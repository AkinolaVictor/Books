package edu.hws.eck.mdb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.StringTokenizer;
import java.util.prefs.*;

/**
 * This class is used to run the Mandelbrot viewer program as a standalone
 * application.  The main routine creates a window of type MandelbrotFrame.
 * and shows it on the screen.  This class also works with "preferences."
 * When the program exits, it saves the current window size and location to
 * the user's Java preferences.  It also saves the currently selected
 * directory in the JFileChooser that is used when the user saves and opens
 * files.   These values are restored from the user's preferences the next time 
 * the program starts.
 */
public class Main {

   public static void main(String[] args) {
      final MandelbrotFrame frame = new MandelbrotFrame();
      applyPreferences(frame);
      frame.adjustToScreenIfNecessary();
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.addWindowListener( new WindowAdapter() {
              // When the window is disposed, this event handler will
              // be notified.  It will save current settings to the
              // user's preferences and terminate the program.
         public void windowClosed(WindowEvent evt) {
            frame.getMandelbrotPanel().getDisplay().shutDownThreads();
            savePreferences(frame);
            System.exit(0);
         }
      });
      frame.setVisible(true);
   }

   
   /**
    * Called when the program ends to try to save window bounds and current
    * directory to user's preferences, so they can be restored the next
    * time the program is run.  Since this is not a critical function, errors
    * are ignored.  If an error occurs, the data simply won't be saved.
    */
   private static void savePreferences(MandelbrotFrame frame) {
      try {
         String pathName = "/edu/hws/eck/mdb";
            // The pathname uniquely identifies this program.  (It is unique
            // because it comes from the package name, which should not be used
            // for any other program under Java's package naming guidelines.
         Preferences root = Preferences.userRoot();
         Preferences node = root.node(pathName);
            // This "node" holds the preferences associated with the pathName.
            // Preferences take the form of key/value pairs, with put() and
            // get() operations for changing/reading values.
         Rectangle bounds = frame.getBounds();
         String boundsString = bounds.x + "," + bounds.y + "," 
                              + bounds.width + "," + bounds.height;
         node.put("mandelbrot.window.bounds", boundsString);  
         Menus menus = (Menus)frame.getJMenuBar();
         String currentDir = menus.getSelectedDirectoryInFileChooser();
         if (currentDir != null)
            node.put("mandelbort.filechooser.directory", currentDir); 
      }
      catch (Exception e) {
      }
   }
   
   
   /**
    * This method is called when the program starts to read and apply the
    * preferences that were saved the last time the program ran.  Since
    * this is not a critical function, errors are ignored.  If an error
    * occurs, preferences will simply not be restored.
    */
   private static void applyPreferences(MandelbrotFrame frame) {
      try {
         String pathName = "/edu/hws/eck/mdb";  // Identifies prefs for this program.
         Preferences root = Preferences.userRoot();
         if (! root.nodeExists(pathName) )
            return;  // There are no saved prefs for this program yet.
         Preferences node = root.node(pathName);
         String boundsString = node.get("mandelbrot.window.bounds",null);
         if (boundsString != null) {
               // Try to restore window bounds, ignoring any error.
            String[] bounds = explode(boundsString,",");
            try {
               int x = Integer.parseInt(bounds[0]);
               int y = Integer.parseInt(bounds[1]);
               int w = Integer.parseInt(bounds[2]);
               int h = Integer.parseInt(bounds[3]);
               if (w > 5000 || h > 5000)
                  throw new NumberFormatException();  // unreasonable values.
               frame.setBounds(x,y,w,h);
               frame.adjustToScreenIfNecessary();
            }
            catch (NumberFormatException e) {
            }
         }
         Menus menus = (Menus)frame.getJMenuBar();
         String directory = node.get("mandelbort.filechooser.directory", null);
         if (directory != null) {
               // Try to restore the current directory.
            menus.setSelectedDirectoryInFileChooser(directory);
         }
      }
      catch (Exception e) {
      }
   }
   
   
   /**
    * A convenience method that breaks up a string into tokens, where
    * the tokens are substrings separated by specified delimiters.
    * For example, explode("ab,cde,f,ghij", ",") produces an array
    * of the four substrings "ab"  "cde"  "f"  "ghi".
    */
   private static String[] explode(String str, String separators) {
      StringTokenizer tokenizer = new StringTokenizer(str, separators);
      int ct = tokenizer.countTokens();
      String[] tokens = new String[ct];
      for (int i = 0; i < ct; i++)
         tokens[i] = tokenizer.nextToken();
      return tokens;
   }
   
}
