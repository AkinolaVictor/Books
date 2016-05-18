import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 *  This program lets the user edit RTF text files in a window.  When a
 *  file is being edited, the name of the file is displayed in the window's
 *  title bar.  RTF text can include "styles" such as bold and italics.
 *  This class can be run as a standalone application.  (There is no
 *  applet version.)
 *  
 *  Note that the program can open files that were saved from the program,
 *  but can't open arbitrary text files correctly.  It should be able to open
 *  standard RTF files.
 */
public class SimpleRTFEdit extends JFrame {


   /**
    * The main program just opens a window belonging to this SimpleRTFEdit class. 
    * Then the window takes care of itself until the program is ended with the 
    * Quit command or when the user closes the window.
    */
   public static void main(String[] args) {
      JFrame window = new SimpleRTFEdit();
      window.setVisible(true);
   }


   private JEditorPane editPane;   // Holds the text that is being edited in the window.
   
   private JFileChooser fileDialog;  // File dialog for use in doOpen() an doSave().
   
   private File editFile;  // The file, if any that is currently being edited.

   
   /**
    * Create a SimpleRTFEdit window, with an edit pane where the user can
    * edit some text and with a menu bar.  The content type for the text
    * files that are edited by this program is "text/rtf".
    */
   public SimpleRTFEdit() {
      super("SimpleRTFEdit: Untitled");  // Specifies title of the window.
      editPane = new JEditorPane("text/rtf","");
      editPane.setMargin( new Insets(3,5,0,0) ); // Some space around the text.
      JScrollPane scroller = new JScrollPane(editPane);
      setContentPane(scroller);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setJMenuBar( makeMenus() );
      setSize(600,500);
      setLocation(50,50);
   }


   /**
    * Create and return a menu bar containing a File menu and an Edit
     * menu.  The File menu contains four commands, New, Open, Save,
     * and Quit.  The commands in the Edit menu are standard editing
     * actions defined in the classes DefaultEditorKit and
     * StyledEditorKit.
    */
   private JMenuBar makeMenus() {

      ActionListener listener = new ActionListener() {
            // An object that will serve as listener for File menu items.
         public void actionPerformed(ActionEvent evt) {
               // This will be called when the user makes a selection
               // from the File menu.  This routine just checks 
               // which command was selected and calls another 
               // routine to carry out the command.
            String cmd = evt.getActionCommand();
            if (cmd.equals("New"))
               doNew();
            else if (cmd.equals("Open..."))
               doOpen();
            else if (cmd.equals("Save..."))
               doSave();
            else if (cmd.equals("Quit"))
               doQuit();
         }
      };
      
      JMenuBar bar = new JMenuBar();

      JMenu fileMenu = new JMenu("File");

      JMenuItem newCmd = new JMenuItem("New");
      newCmd.setAccelerator(makeAccelerator("N"));
      newCmd.addActionListener(listener);
      fileMenu.add(newCmd);
      
      JMenuItem openCmd = new JMenuItem("Open...");
      openCmd.setAccelerator(makeAccelerator("O"));
      openCmd.addActionListener(listener);
      fileMenu.add(openCmd);
      
      JMenuItem saveCmd = new JMenuItem("Save...");
      saveCmd.setAccelerator(makeAccelerator("S"));
      saveCmd.addActionListener(listener);
      fileMenu.add(saveCmd);
      
      fileMenu.addSeparator();
      
      JMenuItem quitCmd = new JMenuItem("Quit");
      newCmd.setAccelerator(makeAccelerator("Q"));
      quitCmd.addActionListener(listener);
      fileMenu.add(quitCmd);

      bar.add(fileMenu);
      
      JMenu editMenu = new JMenu("Edit");
      
      Action cutAction = new DefaultEditorKit.CutAction();
      cutAction.putValue(Action.NAME, "Cut");
      cutAction.putValue(Action.ACCELERATOR_KEY, makeAccelerator("X"));
      editMenu.add(cutAction);
      
      Action copyAction = new DefaultEditorKit.CopyAction();
      copyAction.putValue(Action.NAME, "Copy");
      copyAction.putValue(Action.ACCELERATOR_KEY, makeAccelerator("C"));
      editMenu.add(copyAction);
      
      Action pasteAction = new DefaultEditorKit.PasteAction();
      pasteAction.putValue(Action.NAME, "Paste");
      pasteAction.putValue(Action.ACCELERATOR_KEY, makeAccelerator("V"));
      editMenu.add(pasteAction);
      
      editMenu.addSeparator();
      
      Action boldAction = new StyledEditorKit.BoldAction();
      boldAction.putValue(Action.NAME, "Bold");
      boldAction.putValue(Action.ACCELERATOR_KEY, makeAccelerator("B"));
      editMenu.add(boldAction);
      
      Action italicAction = new StyledEditorKit.ItalicAction();
      italicAction.putValue(Action.NAME, "Italic");
      italicAction.putValue(Action.ACCELERATOR_KEY, makeAccelerator("I"));
      editMenu.add(italicAction);
      
      Action underlineAction = new StyledEditorKit.UnderlineAction();
      underlineAction.putValue(Action.NAME, "Underlined");
      underlineAction.putValue(Action.ACCELERATOR_KEY, makeAccelerator("U"));
      editMenu.add(underlineAction);
            
      bar.add(editMenu);

      return bar;

   } // end makeMenus()
   
   
   /**
    * Create a KeyStroke that uses the meta key on Mac OS and
    * the control key on other operating systems.
    * @param description a string that describes the keystroke,
    *   without the "meta" or "ctrl"; for example, "S" or
    *   "shift Z" or "alt F1"
    * @return a keystroke created from the description string
    *   with either "ctrl " or "meta " prepended
    */
   private static KeyStroke makeAccelerator(String description) {
      String commandKey;
      if ( System.getProperty("mrj.version") == null )
         commandKey = "ctrl";
      else
         commandKey = "meta";
      return KeyStroke.getKeyStroke( commandKey + " " + description );
   }


   /**
    * Carry out the "New" command from the File menu by clearing all 
    * the text from the JTextArea.  Also sets the title bar of the
    * window to read "SimpleRTFEdit: Untitled".
    */
   private void doNew() {
      editPane.setText("");
      editFile = null;
      setTitle("SimpleRTFEdit: Untitled");
   }


   /**
    *  Carry out the Save command by letting the user specify an output file 
    *  and writing the text from the JTextArea to that file.
    */
   private void doSave() {
      if (fileDialog == null)      
         fileDialog = new JFileChooser(); 
      File selectedFile;  //Initially selected file name in the dialog.
      if (editFile == null)
         selectedFile = new File("filename.rtf");
      else
         selectedFile = new File(editFile.getName());
      fileDialog.setSelectedFile(selectedFile); 
      fileDialog.setDialogTitle("Select File to be Saved");
      int option = fileDialog.showSaveDialog(this);
      if (option != JFileChooser.APPROVE_OPTION)
         return;  // User canceled or clicked the dialog's close box.
      selectedFile = fileDialog.getSelectedFile();
      if (selectedFile.exists()) {  // Ask the user whether to replace the file.
         int response = JOptionPane.showConfirmDialog( this,
               "The file \"" + selectedFile.getName()
               + "\" already exists.\nDo you want to replace it?", 
               "Confirm Save",
               JOptionPane.YES_NO_OPTION, 
               JOptionPane.WARNING_MESSAGE );
         if (response != JOptionPane.YES_OPTION)
            return;  // User does not want to replace the file.
      }
      OutputStream out; 
      try {
         out = new FileOutputStream(selectedFile); 
      }
      catch (Exception e) {
         JOptionPane.showMessageDialog(this,
            "Sorry, but an error occurred while trying to open the file:\n" + e);
         return;
      }
      try {
         Document doc = editPane.getDocument();
         editPane.getEditorKit().write(out,doc,0,doc.getLength());
         editFile = selectedFile;
         setTitle("SimpleRTFEdit: " + editFile.getName());
      }
      catch (Exception e) {
         JOptionPane.showMessageDialog(this,
            "Sorry, but an error occurred while trying to write the text:\n" + e);
      }   
   }
   
   
   /**
    * Carry out the Open command by letting the user specify a file to be opened 
    * and reading up to 10000 characters from that file.  If the file is read 
    * successfully and is not too long, then the text from the file replaces the 
    * text in the JTextArea.
    */
   public void doOpen() {
      if (fileDialog == null)
         fileDialog = new JFileChooser();
      fileDialog.setDialogTitle("Select File to be Opened");
      fileDialog.setSelectedFile(null);  // No file is initially selected.
      int option = fileDialog.showOpenDialog(this);
      if (option != JFileChooser.APPROVE_OPTION)
         return;  // User canceled or clicked the dialog's close box.
      File selectedFile = fileDialog.getSelectedFile();
      InputStream in;
      try {
         in = new FileInputStream(selectedFile);
      }
      catch (Exception e) {
         JOptionPane.showMessageDialog(this,
               "Sorry, but an error occurred while trying to open the file:\n" + e);
         return;
      }
      try {
         Document doc = new DefaultStyledDocument();
         editPane.getEditorKit().read(in,doc,0);
         in.close();
         editPane.setDocument(doc);
         editFile = selectedFile;
         setTitle("SimpleRTFEdit: " + editFile.getName());
      }
      catch (Exception e) {
         JOptionPane.showMessageDialog(this,
               "Sorry, but an error occurred while trying to read the data:\n" + e);
      }   
   }


   /**
    * Carry out the Quit command by exiting the program.
    */
   private void doQuit() {
      System.exit(0);
   }


} // end class SimpleRTFEdit
