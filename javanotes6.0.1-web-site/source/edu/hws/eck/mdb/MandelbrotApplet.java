package edu.hws.eck.mdb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 * Runs the Mandelbrot viewer program as an applet.  The applet  sets
 * up a MandelbrotPanel in the content pane of the applet and adds an
 * appropriate menu bar.  Note that in an applet tag on a web page, this
 * class should be referred to as code="edu.hws.eck.mdb.MandelbrotApplet.class".
 * 
 * This applet version of the program can add an extra "Examples" menu to the
 * program.  The Examples menu contains a list of settings files.  These are
 * files in the format that are saved by the Save Params command in the 
 * stand-alone version of the program.  When the user selects one of the
 * items from the menu, the corresponding settings file is loaded and
 * applied to the display.  Since applets can't access the file system,
 * the files must be stored as resources along with the program, in directory
 * edu/hws/edu/mdb/examples, and this source code file must be modified by setting 
 * the SETTINGS_FILE_LIST constant to a list of the names of the file. This is
 * not a great way of doing things, but the program is not really meant to run
 * as an applet in any case.  (I've only done it this way so that when the applet
 * is used as a decoration for the end of Chapter 13 in "Introduction to 
 * Programming Using Java", there would be something interesting there for
 * readers to look at.)
 */
public class MandelbrotApplet extends JApplet {
   
   /**
    * If the value of SETTINGS_FILE_LIST is non-null and is not of length zero,
    * then an Examples menu is added to the applet's menu bar.  The items in the
    * menu are the strings from this array.  For a string str in the array,
    * a resource file name is constructed as:   "edu/hws/edu/mdb/examples" + str + ".mdb"
    * The resulting names must be names of resource files that are accessible 
    * to the applet.  The files should be settings files for the Mandelbrot
    * Viewer program.  When the user selects an item from the Examples menu,
    * the corresponding file is loaded and applied to the display.
    */
   private static final String[] SETTINGS_FILE_LIST =  {
      "settings1", "settings2", "settings3", "settings4", "settings5", 
      "settings6", "settings7", "settings8", "settings9", "settings10",
      "settings11", "settings12"
   };
   
   private MandelbrotPanel panel;
   private Menus menuBar;
   
   public void init() {  // Initialize the applet.
      panel = new MandelbrotPanel();
      JPanel content = new JPanel();
      content.setLayout(new BorderLayout()); // For adding a 1-pixel black border.
      content.add(panel);
      content.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
      setContentPane(content);
      JMenu exampleMenu = constructExampleMenu();
      menuBar = new Menus(panel,null,true);
      if (exampleMenu != null)
         menuBar.add(exampleMenu);
      setJMenuBar(menuBar);
   }
   
   private JMenu constructExampleMenu() {  // Constructs the Examples menu.
      if (SETTINGS_FILE_LIST == null || SETTINGS_FILE_LIST.length == 0)
         return null;
      JMenu menu = new JMenu("Examples");
      for (int i = 0; i < SETTINGS_FILE_LIST.length; i++) {
         final String str = SETTINGS_FILE_LIST[i];
         JMenuItem item = new JMenuItem(str);
         item.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               loadFile(str);
            }
         });
         menu.add(item);
      }
      return menu;
   }
   
   private void loadFile(String resourceName) {  // Tries to load one of the examples.
      resourceName = "edu/hws/eck/mdb/examples/" + resourceName + ".mdb";
      ClassLoader cl = MandelbrotApplet.class.getClassLoader();
      URL resourceURL = cl.getResource(resourceName);
      if (resourceURL != null) {
         try {
            InputStream stream = resourceURL.openStream();
            DocumentBuilder docReader = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmldoc = docReader.parse(stream);
            menuBar.retrieveSettingsFromXML(xmldoc);
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Internal Error.  Couldn't load example\n" + e);
         }
      }
      else
         JOptionPane.showMessageDialog(this,"Internal Error.  Couldn't find file.");
   }

   
   /**
    * This is called when the applet object is being discarded.  I want to make
    * sure that the worker threads in the MandelbrotDisplay are terminated.
    */
   public void destroy() {
      panel.getDisplay().shutDownThreads();
   }

}
