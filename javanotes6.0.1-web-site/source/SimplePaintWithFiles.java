
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * SimplePaintWithFiles is a drawing program in which the user can
 * sketch curves.  The user's work can be saved to a file which can later
 * be reopened and edited.  It is also possible to save the user's
 * picture as an image file.
 */
public class SimplePaintWithFiles extends JFrame {
   
   /**
    * main routine creates a frame of type SimplePaintWithFiles
    * and makes it visible on the screen.
    */
   public static void main(String[] args) {
      JFrame window = new SimplePaintWithFiles();
      window.setVisible(true);
   }
      
   
   /**
    * Constructor creates a window with a 600-by-600 pixel drawing area and
    * sets its location so that it is centered on the screen.  The window is
    * not resizable.  It is not made visible by this constructor.
    */
   public SimplePaintWithFiles() {
      super("SimplePaint: Untitled");
      SimplePaintPanel content = new SimplePaintPanel();
      setContentPane(content);
      setJMenuBar(content.createMenuBar());
      pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setLocation( (screenSize.width - getWidth())/2 , (screenSize.height - getHeight())/2 );
      setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      setResizable(false);  
   }
   
   
   
   /**
    * An object of type CurveData represents the data required to redraw one
    * of the curves that have been sketched by the user.  This class is
    * declared to implement the Serializable interface so that objects
    * belonging to this class can be saved to an ObjectOutputStream.
    */
   private static class CurveData implements Serializable {
      Color color;  // The color of the curve.
      boolean symmetric;  // Are horizontal and vertical reflections also drawn?
      ArrayList<Point> points;  // The points on the curve.
   }
   
   
   /**
    * An object of type SimplePaintPanel is used as the drawing area in
    * the window.  This class does all the work of the program.
    */
   private class SimplePaintPanel extends JPanel {

      private ArrayList<CurveData> curves;  // A list of all curves in the picture.

      private Color currentColor;   // When a curve is created, its color is taken
                                    //     from this variable.  The value is changed
                                    //     using commands in the "Color" menu.

      private boolean useSymmetry;  // When a curve is created, its "symmetric"
                                    // property is copied from this variable.  Its
                                    // value is set by the "Use Symmetry" command in
                                    // the "Control" menu.

      private File editFile;        // The file that is being edited, if any.
                                    // This is set when the user opens a file.
                                    // The name of the file appears in the
                                    // window's title bar.
      
      private JFileChooser fileDialog;   // The dialog box for all open/save commands.


      /**
       * Constructor.  Sets background color to white, adds a gray border, sets up
       * a listener for mouse and mouse motion events, and sets the preferred size
       * of the panel to be 600-by-600.
       */
      public SimplePaintPanel() {
         curves = new ArrayList<CurveData>();
         currentColor = Color.BLACK;
         setBackground(Color.WHITE);
         setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
         MouseHandler listener = new MouseHandler();
         addMouseListener(listener);
         addMouseMotionListener(listener);
         setPreferredSize( new Dimension(600,600) );
      }


      /**
       * This class defines the object that is used as a mouse listener and mouse
       * motion listener on this panel.  When the user presses the mouse, a new
       * CurveData object is created and is added to the ArrayList, curves. The
       * color of the curve is copied from currentColor, and the symmetric property
       * of the curve is copied from useSymmetry.  As the user drags the mouse, points
       * are added to the curve.  If the user doesn't move the mouse, there will only
       * be one point in the list of points; since this is not really a curve, the
       * CurveData objects is removed in this case from the curves list in the
       * mouseReleased method.
       */
      private class MouseHandler implements MouseListener, MouseMotionListener {
         CurveData currentCurve;
         boolean dragging;
         public void mousePressed(MouseEvent evt) {
            if (dragging)
               return;
            dragging = true;
            currentCurve = new CurveData();
            currentCurve.color = currentColor;
            currentCurve.symmetric = useSymmetry;
            currentCurve.points = new ArrayList<Point>();
            currentCurve.points.add( new Point(evt.getX(), evt.getY()) );
            curves.add(currentCurve);
         }
         public void mouseDragged(MouseEvent evt) {
            if (!dragging)
               return;
            currentCurve.points.add( new Point(evt.getX(),evt.getY()) );
            repaint();  // redraw panel with newly added point.
         }
         public void mouseReleased(MouseEvent evt) {
            if (!dragging)
               return;
            dragging = false;
            if (currentCurve.points.size() < 2)
               curves.remove(currentCurve);
            currentCurve = null;
         }
         public void mouseClicked(MouseEvent evt) { }
         public void mouseEntered(MouseEvent evt) { }
         public void mouseExited(MouseEvent evt) { }
         public void mouseMoved(MouseEvent evt) { }
      } // end nested class MouseHandler


      /**
       * Fills the panel with the current background color and draws all the
       * curves that have been sketched by the user.
       */
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         for ( CurveData curve : curves) {
            g.setColor(curve.color);
            for (int i = 1; i < curve.points.size(); i++) {
               // Draw a line segment from point number i-1 to point number i.
               int x1 = curve.points.get(i-1).x;
               int y1 = curve.points.get(i-1).y;
               int x2 = curve.points.get(i).x;
               int y2 = curve.points.get(i).y;
               g.drawLine(x1,y1,x2,y2);
               if (curve.symmetric) {
                  // Also draw the horizontal and vertical reflections
                  // of the line segment.
                  int w = getWidth();
                  int h = getHeight();
                  g.drawLine(w-x1,y1,w-x2,y2);
                  g.drawLine(x1,h-y1,x2,h-y2);
                  g.drawLine(w-x1,h-y1,w-x2,h-y2);
               }
            }
         }
      } // end paintComponent()


      /**
       * Creates a menu bar for use with this panel.  It contains
       * four menus: "File, "Control", "Color", and "BackgroundColor".
       */
      public JMenuBar createMenuBar() {

         /* Create the menu bar object */

         JMenuBar menuBar = new JMenuBar();

         /* Create the menu bar and add them to the menu bar. */

         JMenu fileMenu = new JMenu("File");
         JMenu controlMenu = new JMenu("Control");
         JMenu colorMenu = new JMenu("Color");
         JMenu bgColorMenu = new JMenu("BackgroundColor");
         menuBar.add(fileMenu);
         menuBar.add(controlMenu);
         menuBar.add(colorMenu);
         menuBar.add(bgColorMenu);
         
         /* Add commands to the "File" menu.  It contains two sets
          * of Open and Save commands, one for data saved in object
          * form and one for data saved in text form.  It also contains
          * a command for saving the user's picture as a PNG file and
          * a command for quitting the program.
          */
         
         JMenuItem newCommand = new JMenuItem("New");
         fileMenu.add(newCommand);
         newCommand.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               curves = new ArrayList<CurveData>();
               setBackground(Color.WHITE);
               useSymmetry = false;
               currentColor = Color.BLACK;
               setTitle("SimplePaint: Untitled");
               editFile = null;
               repaint();
            }
         });
         fileMenu.addSeparator();
         JMenuItem saveText = new JMenuItem("Save (text format)...");
         fileMenu.add(saveText);
         saveText.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               doSaveAsText();
            }
         });
         JMenuItem openText = new JMenuItem("Open (text format)...");
         fileMenu.add(openText);
         openText.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               doOpenAsText();
            }
         });
         fileMenu.addSeparator();
         JMenuItem saveObject = new JMenuItem("Save (binary format)...");
         fileMenu.add(saveObject);
         saveObject.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               doSaveAsBinary();
            }
         });
         JMenuItem openBinary = new JMenuItem("Open (binary format)...");
         fileMenu.add(openBinary);
         openBinary.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               doOpenAsBinary();
            }
         });
         fileMenu.addSeparator();
         JMenuItem saveImage = new JMenuItem("Save Image...");
         fileMenu.add(saveImage);
         saveImage.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               doSaveImage();
            }
         });
         fileMenu.addSeparator();
         JMenuItem quitCommand = new JMenuItem("Quit");
         fileMenu.add(quitCommand);
         quitCommand.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               System.exit(0);
            }
         });
         

         /* Add commands to the "Control" menu.  It contains an Undo
          * command that will remove the most recently drawn curve
          * from the list of curves; a "Clear" command that removes
          * all the curves that have been drawn; and a "Use Symmetry"
          * checkbox that determines whether symmetry should be used.
          */

         JMenuItem undo = new JMenuItem("Undo");
         controlMenu.add(undo);
         undo.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               if (curves.size() > 0) {
                  curves.remove( curves.size() - 1);
                  repaint();  // Redraw without the curve that has been removed.
               }
            }
         });
         JMenuItem clear = new JMenuItem("Clear");
         controlMenu.add(clear);
         clear.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               curves = new ArrayList<CurveData>();
               repaint();  // Redraw with no curves shown.
            }
         });
         JCheckBoxMenuItem sym = new JCheckBoxMenuItem("Use Symmetry");
         controlMenu.add(sym);
         sym.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               useSymmetry = ((JCheckBoxMenuItem)evt.getSource()).isSelected();
               // This does not affect the current drawing; it affects
               // curves that are drawn in the future.
            }
         });

         /**
          * Add commands to the "Color" menu.  The menu contains commands for
          * setting the current drawing color.  When the user chooses one of these
          * commands, it has not immediate effect on the drawing.  It just sets
          * the color that will be used for future drawing.
          */

         colorMenu.add(makeColorMenuItem("Black", Color.BLACK));
         colorMenu.add(makeColorMenuItem("White", Color.WHITE));
         colorMenu.add(makeColorMenuItem("Red", Color.RED));
         colorMenu.add(makeColorMenuItem("Green", Color.GREEN));
         colorMenu.add(makeColorMenuItem("Blue", Color.BLUE));
         colorMenu.add(makeColorMenuItem("Cyan", Color.CYAN));
         colorMenu.add(makeColorMenuItem("Magenta", Color.MAGENTA));
         colorMenu.add(makeColorMenuItem("Yellow", Color.YELLOW));
         JMenuItem customColor = new JMenuItem("Custom...");
         colorMenu.add(customColor);
         customColor.addActionListener( new ActionListener() { 
            // The "Custom..." color command lets the user select the current
            // drawing color using a JColorChoice dialog.
            public void actionPerformed(ActionEvent evt) {
               Color c = JColorChooser.showDialog(SimplePaintPanel.this,
                     "Select Drawing Color", currentColor);
               if (c != null)
                  currentColor = c;
            }
         });

         /**
          * Add commands to the "BackgourndColor" menu.  The menu contains commands
          * for setting the background color of the panel.  When the user chooses
          * one of these commands, the panel is immediately redrawn with the new
          * background color.  Any curves that have been drawn are still there.
          */

         bgColorMenu.add(makeBgColorMenuItem("Black", Color.BLACK));
         bgColorMenu.add(makeBgColorMenuItem("White", Color.WHITE));
         bgColorMenu.add(makeBgColorMenuItem("Red", Color.RED));
         bgColorMenu.add(makeBgColorMenuItem("Green", Color.GREEN));
         bgColorMenu.add(makeBgColorMenuItem("Blue", Color.BLUE));
         bgColorMenu.add(makeBgColorMenuItem("Cyan", Color.CYAN));
         bgColorMenu.add(makeBgColorMenuItem("Magenta", Color.MAGENTA));
         bgColorMenu.add(makeBgColorMenuItem("Yellow", Color.YELLOW));
         JMenuItem customBgColor = new JMenuItem("Custom...");
         bgColorMenu.add(customBgColor);
         customBgColor.addActionListener( new ActionListener() { 
            public void actionPerformed(ActionEvent evt) {
               Color c = JColorChooser.showDialog(SimplePaintPanel.this,
                     "Select Background Color", getBackground());
               if (c != null)
                  setBackground(c);
            }
         });

         /* Return the menu bar that has been constructed. */

         return menuBar;

      } // end createMenuBar


      /**
       * This utility method is used to create a JMenuItem that sets the
       * current drawing color.
       * @param command  the text that will appear in the menu
       * @param color  the drawing color that is selected by this command.  (Note that
       *    this parameter is "final" for a technical reason: This is a requirement for
       *    a local variable that is used in an anonymous inner class.)
       * @return  the JMenuItem that has been created.
       */
      private JMenuItem makeBgColorMenuItem(String command, final Color color) {
         JMenuItem item = new JMenuItem(command);
         item.addActionListener( new ActionListener()  {
            public void actionPerformed(ActionEvent evt) {
               setBackground(color);
            }
         });
         return item;
      }


      /**
       * This utility method is used to create a JMenuItem that sets the
       * background color of the panel.
       * @param command  the text that will appear in the menu
       * @param color  the background color that is selected by this command.
       * @return  the JMenuItem that has been created.
       */
      private JMenuItem makeColorMenuItem(String command, final Color color) {
         JMenuItem item = new JMenuItem(command);
         item.addActionListener( new ActionListener()  {
            public void actionPerformed(ActionEvent evt) {
               currentColor = color;
            }
         });
         return item;
      }
      
      
      /**
       * Save the user's image to a file in human-readable text format.
       * Files created by this method can be read back into the program
       * using the doOpenAsText() method.
       */
      private void doSaveAsText() {
         if (fileDialog == null)      
            fileDialog = new JFileChooser(); 
         File selectedFile;  //Initially selected file name in the dialog.
         if (editFile == null)
            selectedFile = new File("sketchData.text");
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
         PrintWriter out; 
         try {
            FileWriter stream = new FileWriter(selectedFile); 
            out = new PrintWriter( stream );
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(this,
               "Sorry, but an error occurred while trying to open the file:\n" + e);
            return;
         }
         try {
            out.println("SimplePaintWithFiles 1.0"); // Version number.
            Color bgColor = getBackground();
            out.println( "background " + bgColor.getRed() + " " +
                  bgColor.getGreen() + " " + bgColor.getBlue() );
            for ( CurveData curve : curves ) {
               out.println();
               out.println("startcurve");
               out.println("  color " + curve.color.getRed() + " " +
                     curve.color.getGreen() + " " + curve.color.getBlue() );
               out.println( "  symmetry " + curve.symmetric );
               for ( Point pt : curve.points )
                  out.println( "  coords " + pt.x + " " + pt.y );
               out.println("endcurve");
            }
            out.close();
            if (out.checkError())
               throw new IOException("Output error.");
            editFile = selectedFile;
            setTitle("SimplePaint: " + editFile.getName());
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(this,
               "Sorry, but an error occurred while trying to write the text:\n" + e);
         }   
      }
   
      
      /**
       * Read image data from a file into the drawing area.  The format
       * of the file must be the same as that used in the doSaveAsText()
       * method.
       */
      private void doOpenAsText() {
         if (fileDialog == null)
            fileDialog = new JFileChooser();
         fileDialog.setDialogTitle("Select File to be Opened");
         fileDialog.setSelectedFile(null);  // No file is initially selected.
         int option = fileDialog.showOpenDialog(this);
         if (option != JFileChooser.APPROVE_OPTION)
            return;  // User canceled or clicked the dialog's close box.
         File selectedFile = fileDialog.getSelectedFile();
         Scanner scanner;
         try {
            Reader stream = new BufferedReader(new FileReader(selectedFile));
            scanner = new Scanner( stream );
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                  "Sorry, but an error occurred while trying to open the file:\n" + e);
            return;
         }
         try {
            String programName = scanner.next();
            if ( ! programName.equals("SimplePaintWithFiles") )
               throw new IOException("File is not a SimplePaintWithFiles data file.");
            double version = scanner.nextDouble();
            if (version > 1.0)
               throw new IOException("File requires a newer version of SimplePaintWithFiles.");
            Color newBackgroundColor = Color.WHITE;
            ArrayList<CurveData> newCurves = new ArrayList<CurveData>();
            while (scanner.hasNext()) {
               String itemName = scanner.next();
               if (itemName.equalsIgnoreCase("background")) {
                  int red = scanner.nextInt();
                  int green = scanner.nextInt();
                  int blue = scanner.nextInt();
                  newBackgroundColor = new Color(red,green,blue);
               }
               else if (itemName.equalsIgnoreCase("startcurve")) {
                  CurveData curve = new CurveData();
                  curve.color = Color.BLACK;
                  curve.symmetric = false;
                  curve.points = new ArrayList<Point>();
                  itemName = scanner.next();
                  while ( ! itemName.equalsIgnoreCase("endcurve") ) {
                     if (itemName.equalsIgnoreCase("color")) {
                        int r = scanner.nextInt();
                        int g = scanner.nextInt();
                        int b = scanner.nextInt();
                        curve.color = new Color(r,g,b);
                     }
                     else if (itemName.equalsIgnoreCase("symmetry")) {
                        curve.symmetric = scanner.nextBoolean();
                     }
                     else if (itemName.equalsIgnoreCase("coords")) {
                        int x = scanner.nextInt();
                        int y = scanner.nextInt();
                        curve.points.add( new Point(x,y) );
                     }
                     else {
                        throw new Exception("Unknown term in input.");
                     }
                     itemName = scanner.next();
                  }
                  newCurves.add(curve);
               }
               else {
                  throw new Exception("Unknown term in input.");
               }
            }
            scanner.close();
            setBackground(newBackgroundColor);
            curves = newCurves;
            repaint();
            editFile = selectedFile;
            setTitle("SimplePaint: " + editFile.getName());
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                  "Sorry, but an error occurred while trying to read the data:\n" + e);
         }   
      }
      
   
      /**
       * Save the user's image to a file in binary form as serialized
       * objects, using an ObjectOutputStream.  Files created by this method 
       * can be read back into the program using the doOpenAsBinary() method.
       */
      private void doSaveAsBinary() {
         if (fileDialog == null)      
            fileDialog = new JFileChooser(); 
         File selectedFile;  //Initially selected file name in the dialog.
         if (editFile == null)
            selectedFile = new File("sketchData.binary");
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
         ObjectOutputStream out; 
         try {
            FileOutputStream stream = new FileOutputStream(selectedFile); 
            out = new ObjectOutputStream( stream );
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(this,
               "Sorry, but an error occurred while trying to open the file:\n" + e);
            return;
         }
         try {
            out.writeObject(getBackground());
            out.writeInt(curves.size());
            for ( CurveData curve : curves )
               out.writeObject(curve);
            out.close();
            editFile = selectedFile;
            setTitle("SimplePaint: " + editFile.getName());
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(this,
               "Sorry, but an error occurred while trying to write the text:\n" + e);
         }   
      }
   
      
      /**
       * Read image data from a file into the drawing area.  The format
       * of the file must be the same as that used in the doSaveAsBinary()
       * method.
       */
      private void doOpenAsBinary() {
         if (fileDialog == null)
            fileDialog = new JFileChooser();
         fileDialog.setDialogTitle("Select File to be Opened");
         fileDialog.setSelectedFile(null);  // No file is initially selected.
         int option = fileDialog.showOpenDialog(this);
         if (option != JFileChooser.APPROVE_OPTION)
            return;  // User canceled or clicked the dialog's close box.
         File selectedFile = fileDialog.getSelectedFile();
         ObjectInputStream in;
         try {
            FileInputStream stream = new FileInputStream(selectedFile);
            in = new ObjectInputStream( stream );
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                  "Sorry, but an error occurred while trying to open the file:\n" + e);
            return;
         }
         try {
            Color newBackgroundColor = (Color)in.readObject();
            int curveCount = in.readInt();
            ArrayList<CurveData> newCurves = new ArrayList<CurveData>();
            for (int i = 0; i < curveCount; i++)
               newCurves.add( (CurveData)in.readObject() );
            in.close();
            curves = newCurves;
            setBackground(newBackgroundColor);
            repaint();
            editFile = selectedFile;
            setTitle("SimplePaint: " + editFile.getName());
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                  "Sorry, but an error occurred while trying to read the data:\n" + e);
         }   
      }
      
      
      /**
       * Saves the user's sketch as an image file in PNG format.
       */
      private void doSaveImage() {
         if (fileDialog == null)      
            fileDialog = new JFileChooser(); 
         fileDialog.setSelectedFile(new File("sketch.png")); 
         fileDialog.setDialogTitle("Select File to be Saved");
         int option = fileDialog.showSaveDialog(this);
         if (option != JFileChooser.APPROVE_OPTION)
            return;  // User canceled or clicked the dialog's close box.
         File selectedFile = fileDialog.getSelectedFile();
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
         try {
            BufferedImage image;  // A copy of the sketch will be drawn here.
            image = new BufferedImage(600,600,BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();  // For drawing onto the image.
            paintComponent(g);
            g.dispose();
            boolean hasPNG = ImageIO.write(image,"PNG",selectedFile);
            if ( ! hasPNG )
               throw new Exception("PNG format not available.");
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(this,
               "Sorry, but an error occurred while trying to write the image:\n" + e);
         }   
      }
      
   
   }

}
