package edu.hws.eck.mdb;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.StringTokenizer;
import org.w3c.dom.*;
import java.io.*;

/**
 * This class defines a JMenuBar for use with a MandelbrotPanel.  This is a large
 * and complex class because it includes many nested classes and Actions that
 * do the work of carrying out menu commands.  However, all the complexity is in
 * the private part of the class, and the class has only a very small public interface
 * consisting of just the constructor and two methods that are used in implementing
 * "preferences" in Main.java.
 */
public class Menus extends JMenuBar {
   
   /* Another bug in makeAccelerator() was fixed on January 9, 2011.  The modifier
    * key for non-Mac platforms was specified as "cntr " rather than "ctrl ".  So, all
    * this time, there were no accelerator keys, except under Mac OS!
    */
   
   /* This file was modified on December 2, 2007 to fix a bug in the "LauncherApplet"
    * version of the program.  A SecurityException was thrown on non-Mac-OS platforms
    * by the call to System.getProperty() in the makeAccelerator() method.  This 
    * prevented the LauncherApplet from opening a window (except under Mac OS).
    */
   
   /**
    * Constructor creates the menu bar containing commands that apply to a
    * MandelbrotPanel.  The configuration of the menu bar can be slightly
    * different, depending on the parameters to the constructor.  The menu
    * bar always contains MaxIterations, Palette, and PaletteLength menus
    * that control the corresponding properties of the display.  There is
    * always a Control menu; its contents can vary a little, but it always
    * contains:  Restore All Defaults; Restore Default Limits; Restore Previous Limits;
    * and Set Limits.  A File menu might or might not be included.
    * @param owner  The MandelbrotPanel that will be managed by this menu bar.  This
    *   variable is also used for access to the MandelbrotDisplay that is
    *   contained in the panel.  This parameter cannot be null.
    * @param frame the frame, if any, that contains the MandelbrotPanel.  For a
    *    MandelbrotPanel in an applet on a web page, this will be null.  If frame
    *    is non-null, then accelerators are added to some of the menu commands.
    *    Also, a "Set Image Size" command is added to the Control menu; this command
    *    requires the frame to change size and so cannot be carried out if there
    *    is no frame.
    * @param runningAsApplet true if the program is running as a standalone application,
    *    false if not.  (Note that the frame can be non-null even for an applet, if
    *    LauncherApplet is used.)  For an applet, there is no File menu.  For a standalone
    *    application, there is a file menu that contains a Save Params, Open Params,
    *    Save Image, and Quit command.  If the program is running as an applet, but
    *    with a frame, then a Close command is added to the control menu.
    */
   public Menus(MandelbrotPanel owner, MandelbrotFrame frame, boolean runningAsApplet) {
      this.owner = owner;
      this.frame = frame;
      paletteManager = new PaletteManager();              // Manages Palette menu.
      paletteLengthManager = new PaletteLengthManager();  // Manages PaletteLength menu.
      maxIterationsManager = new MaxIterationsManager();  // Manages MaxIterations menu.
          // (Note that the Actions that carry out commands in other menus are
          //  defined as instance variables that are initialized as part of their
          //  declarations later in this class.)
      if (frame != null) {
             // Add accelerator keys.
         saveParams.putValue(Action.ACCELERATOR_KEY, makeAccelerator("S"));
         saveImage.putValue(Action.ACCELERATOR_KEY, makeAccelerator("shift S"));
         openParams.putValue(Action.ACCELERATOR_KEY, makeAccelerator("O"));
         if (runningAsApplet) {
               // "Close" and "Quit" are actually implemented in the same way;
               // both of them just dispose of the frame.  The name "Quit" is
               // used in standalone applications; "Close" is used in applets.
            close.putValue(Action.ACCELERATOR_KEY, makeAccelerator("W"));
         }
         else {
            close.putValue(Action.NAME, "Quit");
            close.putValue(Action.ACCELERATOR_KEY, makeAccelerator("Q"));
         }
         defaultLimits.putValue(Action.ACCELERATOR_KEY, makeAccelerator("R"));
         allDefaults.putValue(Action.ACCELERATOR_KEY, makeAccelerator("shift R"));
         undoChangeOfLimits.putValue(Action.ACCELERATOR_KEY, makeAccelerator("U"));
         showLimits.putValue(Action.ACCELERATOR_KEY, makeAccelerator("L"));
         setLimits.putValue(Action.ACCELERATOR_KEY, makeAccelerator("shift L"));
         setImageSize.putValue(Action.ACCELERATOR_KEY, makeAccelerator("shift I"));
         paletteManager.items[0].setAccelerator(makeAccelerator("T"));
         paletteLengthManager.items[0].setAccelerator(makeAccelerator("M"));
      }
      
      if (!runningAsApplet) {
            // Add a File menu, and add items to the menu.
         JMenu fileMenu = new JMenu(I18n.tr("menu.file"));
         add(fileMenu);
         fileMenu.add(saveParams);
         fileMenu.add(openParams);
         fileMenu.addSeparator();
         fileMenu.add(saveImage);
         fileMenu.addSeparator();
         fileMenu.add(close);
      }
      
      // Create the other menus and add them to this menu bar.
      
      JMenu controlMenu = new JMenu(I18n.tr("menu.control"));
      add(controlMenu);
      JMenu maxIterationsMenu = new JMenu(I18n.tr("menu.maxIterations"));
      add(maxIterationsMenu);
      JMenu paletteMenu = new JMenu(I18n.tr("menu.palette"));
      add(paletteMenu);
      JMenu paletteLengthMenu = new JMenu(I18n.tr("menu.paletteLength"));
      add(paletteLengthMenu);
      
      // Add items to the Control menu.
      
      controlMenu.add(allDefaults);
      controlMenu.addSeparator();
      controlMenu.add(defaultLimits);
      controlMenu.add(undoChangeOfLimits);
      undoChangeOfLimits.setEnabled(false);
      controlMenu.add(showLimits);
      controlMenu.add(setLimits);
      if (frame != null) {
         controlMenu.addSeparator();
         controlMenu.add(setImageSize);
      }
      if (frame != null && runningAsApplet) {
         controlMenu.addSeparator();
         controlMenu.add(close);
      }
      
      // Add items to the other three menus.  These are created by the "manager" objects.
      
      for (JMenuItem item : paletteManager.items)
         paletteMenu.add(item);

      for (JMenuItem item : paletteLengthManager.items)
         paletteLengthMenu.add(item);
      
      for (JMenuItem item : maxIterationsManager.items)
         maxIterationsMenu.add(item);
      
      // Add property change listeners to the MandebrotDisplay, so that this
      // menu bar can be notified when certain changes occur in the display.
      
      owner.getDisplay().addPropertyChangeListener(MandelbrotDisplay.LIMITS_PROPERTY, 
            new PropertyChangeListener() {
                     // This listener responds when the limits are changed in the
                     // display.  The only action is to save the old values of
                     // the limits, so they they can be used in the Restore Previous Limits
                     // command.  (Also, the command is enabled because a previous set
                     // set of limits is now available.)
               public void propertyChange(PropertyChangeEvent e) {
                  if (e.getPropertyName() == MandelbrotDisplay.LIMITS_PROPERTY) {
                     previousLimits = (double[])e.getOldValue();
                     undoChangeOfLimits.setEnabled(true);
                  }
               }
            });
      owner.getDisplay().addPropertyChangeListener(MandelbrotDisplay.STATUS_PROPERTY, 
            new PropertyChangeListener() {
                     // This listener responds when the "status" of the display
                     // changes.  The status is used, in the newDisplayStatus method,
                     // to enable and disable menu commands that should only be
                     // be available when the display is in a certain state.
               public void propertyChange(PropertyChangeEvent e) {
                  if (e.getPropertyName() == MandelbrotDisplay.STATUS_PROPERTY) {
                     newDisplayStatus(e.getNewValue());
                  }
               }
            });
      newDisplayStatus(owner.getDisplay().getStatus());  // Enable/disable commands based on initial display status.
      
   } // end constructor
   
   
   /**
    * If the fileDialog has been used to carry out one of the save/open commands,
    * then some directory will be selected in the dialog box.  This method returns
    * an absolute path name for that selected directory.  This method is used
    * by Main.java to find out the selected directory when the program ends.
    * The directory is saved in user preferences and is restored the next time
    * the program is run.
    */
   public String getSelectedDirectoryInFileChooser() {
      if (fileDialog == null)
         return null;
      else {
         File dir = fileDialog.getCurrentDirectory();
         if (dir == null)
            return null;
         else
            return dir.getAbsolutePath();
      }
   }
   
   
   /**
    * This sets the selected directory in the file dialog.  This method
    * is called by Main.java when the program starts to restore the directory
    * that was saved the last time the program was run (by the same user).
    * @param path absolute path name to the directory; if this is
    *   not the path name of an actual directory, then the dialog's
    *   selected directory is not set.
    */
   public void setSelectedDirectoryInFileChooser(String path) {
      File dir = new File(path);
      if (dir.isDirectory()) {
         if (fileDialog == null)
            fileDialog = new JFileChooser();
         fileDialog.setCurrentDirectory(dir);
      }
   }
   
   
   /**
    * Produces an XML representation of the current settings.
    * This is used by the Open Params action to restore the setting of the program based
    * on the contents of an XML file.  It is also used by the MandelbrotApplet class
    * to implement an Examples menu.  Currently, the image size is NOT adjusted to
    * the value in the file; the same picture that was saved is shown, but possibly at
    * a different size.  (Note:  This method is defective because I was lazy.  If an
    * error occurs in the middle of processing the data, some of the setting will be
    * applied but not others.  This ignores the guideline that the file should be
    * completely read and checked before any changes are made to the state of the
    * program.)
    */
   public void retrieveSettingsFromXML(Document xmlDoc) {
      Element docElement = xmlDoc.getDocumentElement();
      String docName = docElement.getTagName();
      if (! docName.equalsIgnoreCase("mandelbrot_settings"))
         throw new IllegalArgumentException(I18n.tr("xml.error.wrongType",docName));
      String version = docElement.getAttribute("version");
      if ( ! version.equalsIgnoreCase("edu.hws.eck.mdb/1.0"))
         throw new IllegalArgumentException(I18n.tr("xml.error.wrongSettingsVersion"));
      NodeList nodes = docElement.getChildNodes();
      int ct = nodes.getLength();
      for (int i = 0; i < ct; i++) {
         Node node = nodes.item(i);
         if (node instanceof Element) {
            String name = ((Element)node).getTagName();
            String value = ((Element)node).getAttribute("value");
            try {
               if (name.equalsIgnoreCase("palettetype"))
                  paletteManager.setValueFromString(value);
               else if (name.equalsIgnoreCase("palettelength"))
                  paletteLengthManager.setValueFromString(value);
               else if (name.equalsIgnoreCase("maxiterations"))
                  maxIterationsManager.setValueFromString(value);
               else if (name.equalsIgnoreCase("limits")) {
                  String[] limitStrings = explode(value,",");
                  double xmin = Double.parseDouble(limitStrings[0]);
                  double xmax = Double.parseDouble(limitStrings[1]);
                  double ymin = Double.parseDouble(limitStrings[2]);
                  double ymax = Double.parseDouble(limitStrings[3]);
                  owner.getDisplay().setLimits(xmin,xmax,ymin,ymax);
               }
            }
            catch (Exception e) {
               throw new IllegalArgumentException(I18n.tr("xml.error.illegalSettingsValue",name,value));
            }
         }
      }
   }
   

   /**
    * This is used by the Save Params action to create an XML representation of 
    * the current settings.  (It is not currently used outside this class in the Mandelbrot
    * Viewer program.)
    */
   public String currentSettingsAsXML() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<?xml version='1.0'?>\n");
      buffer.append("<mandelbrot_settings version='edu.hws.eck.mdb/1.0'>\n");
      double[] limits = owner.getDisplay().getLimits();
      String limitString = limits[0] + "," + limits[1] + "," + limits[2] + "," + limits[3];
      buffer.append("<limits value='"+ limitString + "'/>\n");
      String sizeString = owner.getDisplay().getWidth() + "," + owner.getDisplay().getHeight();
      buffer.append("<imagesize value='"+ sizeString + "'/>\n");
      buffer.append("<maxiterations value='" + maxIterationsManager.valueAsString() + "'/>\n");
      buffer.append("<palettetype value='" + paletteManager.valueAsString() + "'/>\n");
      buffer.append("<palettelength value='" + paletteLengthManager.valueAsString() + "'/>\n");
      buffer.append("</mandelbrot_settings>\n");
      return buffer.toString();
   }
   
   

   //------------------ Everything after this point is private --------------------------

   
   private MandelbrotPanel owner;  // From the parameter to the constructor.
   private MandelbrotFrame frame;  // From the parameter to the constructor,
   
   private PaletteManager paletteManager;              // Manages Palette menu; defined by nested class below.
   private PaletteLengthManager paletteLengthManager;  // Manages PaletteLength menu; defined by nested class below.
   private MaxIterationsManager maxIterationsManager;  // Manages MaxIterations menu; defined by nested class below.

   private JFileChooser fileDialog;  // File dialog for open and save commands.
   private double[] previousLimits;  // For the Restore Previous Limits command.
   private String commandKey; // "ctrl " or "meta ", depending on platform; used only in makeAccelerator()

   
   /**
    * Makes an accelerator keystroke from description, with "ctrl " or "meta "
    * tacked onto the front.  Used only in the constructor.
    */
   private KeyStroke makeAccelerator(String description) {
      if (commandKey == null) {
         commandKey = "ctrl ";  // Fixed bug in January 2011.  Previously, given as "cntr ".
         try {  // try..catch added December 2007 to fix bug noted at top of this file
            if (System.getProperty("mrj.version") != null)
               commandKey = "meta ";
         }
         catch (SecurityException e) {
         }
      }
      return KeyStroke.getKeyStroke(commandKey + description);
   }
   
   
   /**
    * A convenience method that breaks up a string into tokens, where
    * the tokens are substrings separated by specified delimiters.
    * For example, explode("ab,cde,f,ghij", ",") produces an array
    * of the four substrings "ab"  "cde"  "f"  "ghi".
    */
   private String[] explode(String str, String separators) {
      StringTokenizer tokenizer = new StringTokenizer(str, separators);
      int ct = tokenizer.countTokens();
      String[] tokens = new String[ct];
      for (int i = 0; i < ct; i++)
         tokens[i] = tokenizer.nextToken();
      return tokens;
   }
   
   
   /**
    * Enables/Disables some menu items, based on the status of the MandelbrotDisplay.
    * This method is called by a listener that listens for property change events
    * from the display.
    * @param status is probably either "working" or "ready".  Working means that
    *   a computation is in progress in the display.  The save commands, the
    *   entire MaxIterations menu,and the Set Limits and Set Image Size commands
    *   are disabled during computations.  There is a small possibility that the 
    *   program will not have enough memory to create the image; Set Image Size
    *   would be enabled in this case because changing the image size might fix
    *   the problem.
    */
   private void newDisplayStatus(Object status) {
      boolean ready = status.equals(MandelbrotDisplay.STATUS_READY);
      boolean outofmem = status.equals(MandelbrotDisplay.STATUS_OUT_OF_MEMORY);
      saveImage.setEnabled(ready);
      saveParams.setEnabled(ready);
      openParams.setEnabled(ready);
      setLimits.setEnabled(ready);
      setImageSize.setEnabled(ready || outofmem);
      maxIterationsManager.setEnabled(ready || outofmem);
   }
   
   
   /**
    * A little utility method that makes strings out of the xy-limits on the display,
    * where the lengths of the strings is adjusted depending on the distance between
    * xmax and xmin.  The idea is to try to avoid more digits after the decimal
    * points than makes sense.  If it succeeds the coordinates that are shown for xmin
    * and xmax should differ only in their last four or five digits and the same should
    * also be true for ymin and ymax.
    * @return An array of 4 strings representing the values of xmin, xmax, ymin, ymax.
    */
   private String[] makeScaledLimitStrings() {
      double xmin = owner.getDisplay().getXmin();
      double xmax = owner.getDisplay().getXmax();
      double ymin = owner.getDisplay().getYmin();
      double ymax = owner.getDisplay().getYmax();
      double diff = xmax - xmin;
      if (diff == 0)
         return new String[] { ""+xmin, ""+xmax, ""+ymin, ""+ymax };
      int scale = 4;
      if (diff > 0) {
         while (diff < 1) {
            scale++;
            diff *= 10;
         }
      }
      String fmt = "%1." + scale + "f";
      String[] str = new String[4];
      str[0] = String.format(fmt,xmin);
      str[1] = String.format(fmt,xmax);
      str[2] = String.format(fmt,ymin);
      str[3] = String.format(fmt,ymax);
      return str;
   }
   
   // ------ The rest of the file defines Actions and nested classes that implement commands -------
   
   
   private Action saveParams = new AbstractAction(I18n.tr("command.save")) {
         // Saves current setting in an XML format file.
      public void actionPerformed(ActionEvent evt) {
         if (fileDialog == null)
            fileDialog = new JFileChooser();
         File selectedFile = new File(I18n.tr("files.saveparams.defaultFileName"));
         fileDialog.setSelectedFile(selectedFile); 
         fileDialog.setDialogTitle(I18n.tr("files.saveparams.title"));
         int option = fileDialog.showSaveDialog(owner);
         if (option != JFileChooser.APPROVE_OPTION)
            return;  // User canceled or clicked the dialog's close box.
         selectedFile = fileDialog.getSelectedFile();
         if (selectedFile.exists()) {  // Ask the user whether to replace the file.
            int response = JOptionPane.showConfirmDialog( owner,
                  I18n.tr("files.fileexists",selectedFile.getName()),
                  I18n.tr("files.confirmsave.title"),
                  JOptionPane.YES_NO_OPTION, 
                  JOptionPane.WARNING_MESSAGE );
            if (response != JOptionPane.YES_OPTION)
               return;  // User does not want to replace the file.
         }
         PrintWriter out; 
         try {
            FileOutputStream stream = new FileOutputStream(selectedFile); 
            out = new PrintWriter( stream );
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(owner,
                  I18n.tr("files.saveparams.error.cannotOpen", 
                        selectedFile.getName(), e.toString()));
            return;
         }
         try {
            out.print(currentSettingsAsXML());
            out.close();
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(owner,
                  I18n.tr("files.saveparams.error.cannotWrite", 
                        selectedFile.getName(), e.toString()));
         }   
      }
   };
   
   
   private Action openParams = new AbstractAction(I18n.tr("command.open")) {
          // Loads settings from an XML file of the form that is saved by the Save Params command.
      public void actionPerformed(ActionEvent evt) {
         if (fileDialog == null)
            fileDialog = new JFileChooser();
         fileDialog.setDialogTitle(I18n.tr("files.openparams.title"));
         fileDialog.setSelectedFile(null);  // No file is initially selected.
         int option = fileDialog.showOpenDialog(owner);
         if (option != JFileChooser.APPROVE_OPTION)
            return;  // User canceled or clicked the dialog's close box.
         File selectedFile = fileDialog.getSelectedFile();
         Document xmldoc;
         try {
            DocumentBuilder docReader = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            xmldoc = docReader.parse(selectedFile);
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(owner,
                  I18n.tr("files.openparams.error.notXML", 
                        selectedFile.getName(), e.toString()));
            return;
         }
         try {
            retrieveSettingsFromXML(xmldoc);
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(owner,
                  I18n.tr("files.openparams.error.notParamsFile", 
                        selectedFile.getName(), e.getMessage()));
         }   
      }
   };
   
   
   private Action saveImage = new AbstractAction(I18n.tr("command.saveImage")) {
         // Saves the current image as a file in PNG format.
      public void actionPerformed(ActionEvent evt) {
         if (fileDialog == null)      
            fileDialog = new JFileChooser();
         BufferedImage image;  // A copy of the image from the MandelbrotDisplay
         image = owner.getDisplay().getImage();
         if (image == null) {
            JOptionPane.showMessageDialog(owner,I18n.tr("files.saveimage.noImage"));
            return;
         }
         fileDialog.setSelectedFile(new File(I18n.tr("files.saveimage.defaultFileName"))); 
         fileDialog.setDialogTitle(I18n.tr("files.saveimage.title"));
         int option = fileDialog.showSaveDialog(owner);
         if (option != JFileChooser.APPROVE_OPTION)
            return;  // User canceled or clicked the dialog's close box.
         File selectedFile = fileDialog.getSelectedFile();
         if (selectedFile.exists()) {  // Ask the user whether to replace the file.
            int response = JOptionPane.showConfirmDialog( owner,
                  I18n.tr("files.fileexists",selectedFile.getName()),
                  I18n.tr("files.confirmsave.title"),
                  JOptionPane.YES_NO_OPTION, 
                  JOptionPane.WARNING_MESSAGE );
            if (response != JOptionPane.YES_OPTION)
               return;  // User does not want to replace the file.
         }
         try {
            boolean hasPNG = ImageIO.write(image,"PNG",selectedFile);
            if ( ! hasPNG )
               throw new Exception(I18n.tr("files.saveimage.noPNG"));
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(owner,
                  I18n.tr("files.saveimage.cantwrite", 
                        selectedFile.getName(), e.toString()));
         }   
      }
   };
   
   
   private Action close = new AbstractAction(I18n.tr("command.close")) {
         // Implement the Close or Quit command by disposing the frame.
      public void actionPerformed(ActionEvent evt) {
         frame.dispose();
      }
   };
   
   
   private Action defaultLimits = new AbstractAction(I18n.tr("command.defaultLimits")) {
         // Restores the default xy-limits on the MandelbrotDisplay.
      public void actionPerformed(ActionEvent evt) {
         owner.getDisplay().setLimits(-2.5,1.1,-1.35,1.35);
      }
   };
   
   
   private Action allDefaults = new AbstractAction(I18n.tr("command.restoreAllDefaults")) {
         // Restores default settings for limits, palette type, palette length, and maxIterations.
      public void actionPerformed(ActionEvent evt) {
         owner.getDisplay().setLimits(-2.5,1.1,-1.35,1.35);
         paletteManager.setDefault();
         paletteLengthManager.setDefault();
         maxIterationsManager.setDefault();
      }
   };
   
   
   private Action undoChangeOfLimits = new AbstractAction(I18n.tr("command.undoChangeOfLimits")) {
          // Restores previous xy-limits on MandelbrotPanel.  The previous limits are
          // obtained from a property change event that is emitted by the display whenever
          // the limits change.  The listener stores the old limits in the previousLimits
          // instance variable.
      public void actionPerformed(ActionEvent evt) {
         if (previousLimits != null)
            owner.getDisplay().setLimits(previousLimits[0],previousLimits[1],
                                           previousLimits[2],previousLimits[3]);
      }
   };
   
   
   private Action showLimits = new AbstractAction(I18n.tr("command.showLimits")) {
          // Puts up a message dialog that contains the current range of xy-values
          // that is shown in the MandelbrotDisplay.
      public void actionPerformed(ActionEvent evt) {
         String[] limits = makeScaledLimitStrings();
         JOptionPane.showMessageDialog(owner, 
               I18n.tr("dialog.showLimits",limits[0],limits[1],limits[2],limits[3]));
      }
   };
   
   
   private Action setImageSize = new AbstractAction(I18n.tr("command.enterImageSize")) {
          // Puts up a dialog box of type SetImageSizeDialog (another class defined in
          // this package).  The dialog box lets the user enter new values for the
          // width and height of the image.  If the user does not cancel, then the
          // new width and height are applied to the image.  The frame changes size
          // to match the new size.  However, the size is not allowed to grow bigger
          // than will fit on the screen.
      public void actionPerformed(ActionEvent evt) {
         Dimension oldSize = owner.getDisplay().getSize();
         Dimension newSize = SetImageSizeDialog.showDialog(frame, oldSize);
         if (newSize == null)
            return;
         owner.getDisplay().setPreferredSize(newSize);  // Change the size that the display wants to be.
         frame.pack();  // Sizes the window to accommodate the preferred size.
         frame.adjustToScreenIfNecessary();  // Make sure frame fits on the screen.
      }
   };
   
    
   private Action setLimits= new AbstractAction(I18n.tr("command.enterLimits")) {
         // Puts up a dialog box of type SetLimitsDialog (another class defined in
         // this package.  The dialog box lets the user enter new values for xmin,
         // xmax, ymin, and ymax (the limits of the range of xy-values shown in the
         // MandelbrotDisplay).  If the user does not cancel, the new limits are
         // applied to the display.
      public void actionPerformed(ActionEvent evt) {
         String[] limits = makeScaledLimitStrings();
         double[] newLimits = SetLimitsDialog.showDialog(frame, limits);
         if (newLimits != null)
            owner.getDisplay().setLimits(newLimits[0],newLimits[1],newLimits[2],newLimits[3]);
      }
   };
    
   
   
   /**
    * Defines the object that manages the Palette menu.
    */
   private class PaletteManager implements ActionListener{
      
      JRadioButtonMenuItem[] items;  // Array contains all the items that are in the Palette menu.
      int selectedItem = 0;  // Index in the items array of the item that is currently selected.
      private String[] valueStrings = {"Spectrum","PaleSpectrum","Grayscale","ReverseGrayscale",
                       "BlackToRed","RedToCyan","OrangeToBlue"};  // Names for commands in XML settings file.
      
      PaletteManager() {
            // Constructor creates the items and adds them to a ButtonGroup.  Also this
            // object adds itself as an ActionListener to each item so it can carry out
            // the command when the user selects one of the items.
         items = new JRadioButtonMenuItem[8];
         items[0] = new JRadioButtonMenuItem(I18n.tr("command.palette.spectrum"));
         items[1] = new JRadioButtonMenuItem(I18n.tr("command.palette.paleSpectrum"));
         items[2] = new JRadioButtonMenuItem(I18n.tr("command.palette.grayscale"));
         items[3] = new JRadioButtonMenuItem(I18n.tr("command.palette.reverseGrayscale"));
         items[4] = new JRadioButtonMenuItem(I18n.tr("command.palette.gradientBlackToRed"));
         items[5] = new JRadioButtonMenuItem(I18n.tr("command.palette.gradientRedToCyan"));
         items[6] = new JRadioButtonMenuItem(I18n.tr("command.palette.gradientOrangeToBlue"));
         items[7] = new JRadioButtonMenuItem(I18n.tr("command.palette.customGradient"));
         items[selectedItem].setSelected(true);
         ButtonGroup grp = new ButtonGroup();
         for (int i = 0; i < items.length; i++) {
            grp.add(items[i]);
            items[i].addActionListener(this);
         }
      }
      
      void setDefault() {
            // Selects the default item (item 0) and sets the state of the
            // MandelbrotDisplay to match; this is used by the Restore All Defaults command.
         items[0].setSelected(true);
         selectedItem = 0;
         owner.getDisplay().setPaletteType(MandelbrotDisplay.PALETTE_SPECTRUM);
      }
      
      String valueAsString() {
             // Converts the setting of this menu to a string that can be saved
             // in an XML file.  This is used by the currentSettingAsXML() method,
             // which is used in turn by the Save Params command.
         if (selectedItem < valueStrings.length)
            return valueStrings[selectedItem];
         else {
            Color c1 = owner.getDisplay().getGradientPaletteColor1();
            Color c2 = owner.getDisplay().getGradientPaletteColor2();
            if (c1 == null || c2 == null)
               return valueStrings[0];  // Should not happen!
            return "Custom/" + c1.getRed() + "," + c1.getGreen() + "," + c1.getBlue() + "/"
                                     + c2.getRed() + "," + c2.getGreen() + "," + c2.getBlue();
         }
      }
      
      void setValueFromString(String str) {
             // Takes a string from an XML file (which originally came from the
             // previous method when the file was saved) and restores the setting
             // represented by that string.  This is called by the retrieveSettingsFromXML()
             // method, which is called in turn by the Open Params command
         for (int i = 0; i < valueStrings.length; i++) {
            if (valueStrings[i].equalsIgnoreCase(str)) {
               items[i].setSelected(true);
               applySelection();
               return;
            }
         }
         String[] tokens = explode(str,"/,");
         if ( ! tokens[0].equalsIgnoreCase("custom"))
            throw new IllegalArgumentException();
         Color c1 = new Color( Integer.parseInt(tokens[1]), 
               Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]) );
         Color c2 = new Color( Integer.parseInt(tokens[4]), 
               Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]) );
         owner.getDisplay().setGradientPalette(c1, c2);
         items[7].setSelected(true);
         selectedItem = 7;
      }
      
      public void actionPerformed(ActionEvent evt) {
            // Responds to a command by calling the applySelection() method.
            // The applySelection() method has been factored out of the actionPerformed()
            // method because it is also used in the setValueFromString() method.
         applySelection();
      }

      private void applySelection() {
            // Sets the palette selected in the MandelbrotDisplay to match the
            // currently selected item in the menu.
         if (items[0].isSelected()) {
            owner.getDisplay().setPaletteType(MandelbrotDisplay.PALETTE_SPECTRUM);
            selectedItem = 0;
         }
         else if (items[1].isSelected()) {
            owner.getDisplay().setPaletteType(MandelbrotDisplay.PALETTE_PALE_SPECTRUM);
            selectedItem = 1;
         }
         else if (items[2].isSelected()) {
            owner.getDisplay().setPaletteType(MandelbrotDisplay.PALETTE_GRAYSCALE);
            selectedItem = 2;
         }
         else if (items[3].isSelected()) {
            owner.getDisplay().setPaletteType(MandelbrotDisplay.PALETTE_REVERSE_GRAYSCALE);
            selectedItem = 3;
         }
         else if (items[4].isSelected()) {
            owner.getDisplay().setGradientPalette(Color.BLACK, Color.RED);
            selectedItem = 4;
         }
         else if (items[5].isSelected()) {
            owner.getDisplay().setGradientPalette(Color.RED, Color.CYAN);
            selectedItem = 5;
         }
         else if (items[6].isSelected()) {
            owner.getDisplay().setGradientPalette( new Color(255,130,20), new Color(0,0,255));
            selectedItem = 6;
         }
         else {  // The setting is for a custom gradient.  NOTE that this case never occurs when
                // this method is called from the setValueFromString() method; it only occurs
                // when called from actionPerformed() in response to a user action.  The
                // command is "Custom gradient", and the response is to show two Color
                // dialog boxes where the user can pick the start and end color for the
                // gradient.  Note that if the user CANCELS, then the state of the
                // MandelbrotDisplay is not changed, and the menu must be reset to show
                // the selection that was in place before the user action so that the
                // menu will properly reflect the state of the display.  This is the
                // main reason why I keep the current selectedItem in an instance variable.
            Color c1 = Color.BLACK;
            Color c2 = Color.WHITE;
            if (owner.getDisplay().getPaletteType() == MandelbrotDisplay.PALETTE_GRADIENT) {
                   // If the display is already using a gradient palette, then the colors
                   // from that gradient will be used as the initially selected colors in
                   // the color chooser dialog boxes.
               c1 = owner.getDisplay().getGradientPaletteColor1();
               c2 = owner.getDisplay().getGradientPaletteColor2();
            }
            c1 = JColorChooser.showDialog(owner, "Select Gradient Start Color", c1);
            if (c1 == null) {
               items[selectedItem].setSelected(true);  // Restore previous selection in menu.
               return;                                 // (The menu selection has been changed by the user
            }                                           // action, but the value of selectedItem has not changed.
            c2 = JColorChooser.showDialog(owner, "Select Gradient End Color", c2);
            if (c2 == null) {
               items[selectedItem].setSelected(true);
               return;
            }
            owner.getDisplay().setGradientPalette(c1, c2);
            selectedItem = 7;
         }
      }
   } // end nested class PaletteManager
   
   
   /**
    * Defines the object that manages the PaletteLength menu.  Similar in
    * structure to PaletteManager; see above.
    */
   private class PaletteLengthManager implements ActionListener{
      int[] standardLengths = { 25, 50, 100, 250, 500, 1000, 2000, 5000, 10000 };
      int selectedItem = 0;
      JRadioButtonMenuItem[] items;
      PaletteLengthManager() {
         items = new JRadioButtonMenuItem[ 2 + standardLengths.length ];
         items[0] = new JRadioButtonMenuItem(I18n.tr("command.pallete.lengthTracksMaxIterations"));
         for (int i = 0; i < standardLengths.length; i++)
            items[i+1] = new JRadioButtonMenuItem(
                  I18n.tr("command.palette.length", ""+standardLengths[i]));
         items[items.length-1] = new JRadioButtonMenuItem(I18n.tr("command.palette.customLength"));
         items[selectedItem].setSelected(true);
         ButtonGroup grp = new ButtonGroup();
         for (int i = 0; i < items.length; i++) {
            grp.add(items[i]);
            items[i].addActionListener(this);
         }
      }
      void setDefault() {
         selectedItem = 0;
         owner.getDisplay().setPaletteLength(0);
         items[0].setSelected(true);
      }
      String valueAsString() {
         return "" + owner.getDisplay().getPaletteLength();
      }
      void setValueFromString(String str) {
         int length = Integer.parseInt(str);
         if (length < 0 || length > 500000)
            throw new IllegalArgumentException();
         if (length == 0) {
            selectedItem = 0;
            owner.getDisplay().setPaletteLength(0);
            items[0].setSelected(true);
            return;
         }
         for (int i = 0; i < standardLengths.length; i++) {
            if (length == standardLengths[i]) {
               selectedItem = i+1;
               owner.getDisplay().setPaletteLength(standardLengths[i]);
               items[i+1].setSelected(true);
               return;
            }
         }
         items[items.length-1].setSelected(true);
         owner.getDisplay().setPaletteLength(length);
      }
      public void actionPerformed(ActionEvent evt) {
         if (items[0].isSelected()) {
            owner.getDisplay().setPaletteLength(0);
            selectedItem = 0;
         }
         else if (items[items.length-1].isSelected()) {
            String lengthStr = JOptionPane.showInputDialog(
                  I18n.tr("command.palette.customLengthQuestion"),
                  owner.getDisplay().getPaletteLength());
            if (lengthStr == null || lengthStr.trim().length() == 0) {
               items[selectedItem].setSelected(true);
               return;
            }
            try {
               int length = Integer.parseInt(lengthStr);
               if (length < 0)
                  throw new NumberFormatException();
               if (length > 500000)
                  throw new NumberFormatException();
               owner.getDisplay().setPaletteLength(length);
               selectedItem = items.length - 1;
            }
            catch (NumberFormatException e) {
               JOptionPane.showMessageDialog(
                     owner,I18n.tr("command.palette.customLengthError",lengthStr));
               items[selectedItem].setSelected(true);
               return;
            }
         }
         else {
            for (int i = 0; i < standardLengths.length; i++) {
               if (items[i+1].isSelected()) {
                  owner.getDisplay().setPaletteLength(standardLengths[i]);
                  selectedItem = i+1;
                  break;
               }
            }
         }
      }
   } // end nested class PaletteLengthManager


   /**
    * Defines the object that manages the MaxIterations menu.  Similar in
    * structure to PaletteManager; see above.
    */
   private class MaxIterationsManager implements ActionListener{
      int[] standardValues = { 50, 100, 250, 500, 1000, 2000, 5000, 20000, 50000, 100000 };
      int selectedItem = 0;
      JRadioButtonMenuItem[] items;
      MaxIterationsManager() {
         items = new JRadioButtonMenuItem[ 1 + standardValues.length ];
         for (int i = 0; i < standardValues.length; i++)
            items[i] = new JRadioButtonMenuItem(
                  I18n.tr("command.maxiterations", ""+standardValues[i]));
         items[items.length-1] = new JRadioButtonMenuItem(I18n.tr("command.maxiterations.custom"));
         items[selectedItem].setSelected(true);
         ButtonGroup grp = new ButtonGroup();
         for (int i = 0; i < items.length; i++) {
            grp.add(items[i]);
            items[i].addActionListener(this);
         }
      }
      void setEnabled(boolean enable) {
         for (JRadioButtonMenuItem item : items)
            item.setEnabled(enable);
      }
      void setDefault() {
         selectedItem = 0;
         owner.getDisplay().setMaxIterations(standardValues[0]);
         items[0].setSelected(true);
      }
      String valueAsString() {
         return "" + owner.getDisplay().getMaxIterations();
      }
      void setValueFromString(String str) {
         int length = Integer.parseInt(str);
         if (length < 0 || length > 500000)
            throw new IllegalArgumentException();
         for (int i = 0; i < standardValues.length; i++) {
            if (length == standardValues[i]) {
               selectedItem = i;
               owner.getDisplay().setMaxIterations(standardValues[i]);
               items[i].setSelected(true);
               return;
            }
         }
         items[items.length-1].setSelected(true);
         owner.getDisplay().setMaxIterations(length);
      }
      public void actionPerformed(ActionEvent evt) {
         if (items[items.length-1].isSelected()) {
            String valueStr = JOptionPane.showInputDialog(
                  I18n.tr("command.maxiterations.customQuestion"),
                  owner.getDisplay().getMaxIterations());
            if (valueStr == null || valueStr.trim().length() == 0) {
               items[selectedItem].setSelected(true);
               return;
            }
            try {
               int value = Integer.parseInt(valueStr);
               if (value < 0)
                  throw new NumberFormatException();
               if (value > 500000)
                  throw new NumberFormatException();
               owner.getDisplay().setMaxIterations(value);
               selectedItem = items.length - 1;
            }
            catch (NumberFormatException e) {
               JOptionPane.showMessageDialog(
                     owner,I18n.tr("command.maxiterations.customError",valueStr));
               items[selectedItem].setSelected(true);
               return;
            }
         }
         else {
            for (int i = 0; i < standardValues.length; i++) {
               if (items[i].isSelected()) {
                  owner.getDisplay().setMaxIterations(standardValues[i]);
                  selectedItem = i;
                  break;
               }
            }
         }
      }
   } // end nested class MaxIterationsManager

   
} // end class Menus
