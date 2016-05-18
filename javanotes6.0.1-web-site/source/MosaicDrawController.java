
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * This is the class that does most of the work in the MosaicDraw program.
 * It creates the two pieces of the program -- the panel and the menu bar -- and
 * makes them available in its getMosaicPanel() and getMenuBar() methods.
 * It attaches a mouse listener to the panel to respond to user mouse actions.
 * It attaches a listener to all the menu items to respond to menu commands from
 * the user.  It contains other instance methods and instance variables to 
 * implement the drawing and all the menu commands.  (Note that this class does
 * not itself represent a GUI component.)  This class depends on MosaicPanel.java.
 */
public class MosaicDrawController {
   
   private final static int DRAW_TOOL = 0;      // possible values for currentTool
   private final static int ERASE_TOOL = 1;
   private final static int DRAW_3x3_TOOL = 2;
   private final static int ERASE_3x3_TOOL = 3;

   private int currentTool;  // The current tool; this is changed when the
                             // user makes a selection from the Tools menu.

   private int currentRed, currentGreen, currentBlue;  // The current color.
                       // These change when the user selects from the Color menu
                       // They are uses whenever a square is painted.  (NOTE:
                       // I am using three integers to represent the color, rather
                       // than a variable of type Color, to make it easier to add
                       // in the random color variation.)

   private MosaicPanel mosaic;     // The panel where the drawing takes place.
   
   private boolean useRandomness;  // If true, then a small random variation is
                                   // added to the current color whenever a 
                                   // square is painted.  The value is controlled
                                   // by the "Use Randomness" option in the
                                   // Control menu.
   
   private boolean useSymmetry;   // If true, then whenever a square is painted
                                  // or erased, the three symmetrical squares
                                  // obtained by reflecting the square vertically
                                  // and horizontally are also painted or erased.
                                  // This is controlled by the "Use Symmetry"
                                  // option in the control menu.
   
   /**
    * Create a controller that uses a MosaicPanel with 40 rows and 40 columns,
    * and in which the preferred size of each square is 12 pixels.
    */
   public MosaicDrawController() {
      this(40,40,12);
   }
   
   /**
    * Create a controller that uses a MosaicPanel with a specified number of
    * rows and columns and in which the preferred size of each square is 12 pixels.
    */
   public MosaicDrawController(int rows, int columns) {
      this(rows,columns,12);
   }
   
   /**
    * Create a controller that uses a MosaicPanel with a specified number of
    * rows and columns and in which the preferred size of each square is also
    * given as a parameter.
    */
   public MosaicDrawController(int rows, int columns, int squareSize) {
      mosaic = new MosaicPanel(rows, columns, squareSize, squareSize);
      useRandomness = true;
      useSymmetry = false;
      currentRed = 150;
      currentGreen = 150;
      currentBlue = 225;
      MouseHandler listener = new MouseHandler();
      mosaic.addMouseListener(listener);
      mosaic.addMouseMotionListener(listener);
   }

   /**
    * Returns the MosaicPanel that is used by this controller, so that it
    * can, for example, be used as the content pane of a JFrame or JApplet.
    */
   public MosaicPanel getMosaicPanel() {
      return mosaic;
   }
   
   /**
    * Creates and returns a menu bar that contains options that affect the
    * drawing that is done on the MosaicPanel.
    */
   public JMenuBar getMenuBar() {
      JMenuBar menuBar = new JMenuBar();
      MenuHandler listener = new MenuHandler();
      JMenu controlMenu = new JMenu("Control");
      addMenuItem(controlMenu,"Fill",listener);
      addMenuItem(controlMenu,"Clear",listener);
      controlMenu.addSeparator();
      addToggleMenuItem(controlMenu,"Use Randomness",listener,true);
      addToggleMenuItem(controlMenu,"Use Symmetry",listener,false);
      addToggleMenuItem(controlMenu,"Show Grouting",listener,true);
      menuBar.add(controlMenu);
      JMenu colorMenu = new JMenu("Color");
      addMenuItem(colorMenu,"Red",listener);
      addMenuItem(colorMenu,"Green",listener);
      addMenuItem(colorMenu,"Blue",listener);
      addMenuItem(colorMenu,"Cyan",listener);
      addMenuItem(colorMenu,"Magenta",listener);
      addMenuItem(colorMenu,"Yellow",listener);
      addMenuItem(colorMenu,"Gray",listener);
      colorMenu.addSeparator();
      addMenuItem(colorMenu,"Custom Color...",listener);
      menuBar.add(colorMenu);
      JMenu toolMenu = new JMenu("Tools");
      addMenuItem(toolMenu,"Draw",listener);
      addMenuItem(toolMenu,"Erase",listener);
      addMenuItem(toolMenu,"Draw 3x3",listener);
      addMenuItem(toolMenu,"Erase 3x3",listener);
      menuBar.add(toolMenu);
      return menuBar;
   }
   
   /**
    * Utility method to create a menu item, add a listener to it, and add it to a menu.
    */
   private void addMenuItem(JMenu menu, String command, ActionListener listener) {
      JMenuItem menuItem = new JMenuItem(command);
      menuItem.addActionListener(listener);
      menu.add(menuItem);
   }

   /**
    * Utility method to create a checkbox menu item, add a listener to it, 
    * add it to a menu, and say whether it is initially selected or not.
    */
   private void addToggleMenuItem(JMenu menu, String command, 
         ActionListener listener, boolean selected) {
      JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(command);
      menuItem.setSelected(selected);
      menuItem.addActionListener(listener);
      menu.add(menuItem);
   }

   /**
    * Erases the square in a specified row and column.  If symmetry is turned
    * on, the three symmetrical squares are also erased.
    */
   private void eraseSquare(int row, int col) {
      mosaic.setColor(row, col, null);
      if (useSymmetry) {
         mosaic.setColor(mosaic.getRowCount() - 1 - row, col, null);
         mosaic.setColor(row, mosaic.getColumnCount() - 1 - col, null);
         mosaic.setColor(mosaic.getRowCount() - 1 - row, mosaic.getColumnCount() - 1 - col, null);
      }
   }

   /**
    * Applies the current drawing color to the square in a given row and column.
    * If randomness is turned on, a random amount is added to the red, green, and 
    * blue components of the drawing color.  If symmetry is turned on, then the
    * three symmetrical squares are also painted.
    */
   private void paintSquare(int row, int col) {
      int r = currentRed;
      int g = currentGreen;
      int b = currentBlue;
      if (useRandomness) {
         if (r < 60)
            r = (int)(60*Math.random());
         else if (r > 255-60)
            r = 255 - (int)(60*Math.random());
         else
            r = r + (int)(60*Math.random() - 30);
         if (g < 60)
            g = (int)(60*Math.random());
         else if (g > 255-60)
            g = 255 - (int)(60*Math.random());
         else
            g = g + (int)(60*Math.random() - 30);
         if (b < 60)
            b = (int)(60*Math.random());
         else if (b > 255-60)
            b = 255 - (int)(60*Math.random());
         else
            b = b + (int)(60*Math.random() - 30);
      }
      mosaic.setColor(row, col, r, g, b);
      if (useSymmetry) {
         mosaic.setColor(mosaic.getRowCount() - 1 - row, col, r, g, b);
         mosaic.setColor(row, mosaic.getColumnCount() - 1 - col, r, g, b);
         mosaic.setColor(mosaic.getRowCount() - 1 - row, mosaic.getColumnCount() - 1 - col, r, g, b);
      }
   }
   
   /**
    * This method is called when the user clicks the mouse or drags it over the
    * square in the specified row and column.  It takes the appropriate action,
    * depending on which drawing tool is currently selected.
    */
   private void applyCurrentTool(int row, int col) {
      int minrow, mincol, maxrow, maxcol;
      switch (currentTool) {
      case DRAW_TOOL:
         paintSquare(row,col);
         break;
      case ERASE_TOOL:
         eraseSquare(row,col);
         break;
      case DRAW_3x3_TOOL:
         minrow = Math.max(0, row-1);
         maxrow = Math.min(mosaic.getRowCount()-1, row+1);
         mincol = Math.max(0, col-1);
         maxcol = Math.min(mosaic.getColumnCount()-1, col+1);
         for (int i = minrow; i <= maxrow; i++)
            for (int j = mincol; j <= maxcol; j++)
               paintSquare(i,j);
         break;
      case ERASE_3x3_TOOL:
         minrow = Math.max(0, row-1);
         maxrow = Math.min(mosaic.getRowCount()-1, row+1);
         mincol = Math.max(0, col-1);
         maxcol = Math.min(mosaic.getColumnCount()-1, col+1);
         for (int i = minrow; i <= maxrow; i++)
            for (int j = mincol; j <= maxcol; j++)
               eraseSquare(i,j);
         break;
      }
   }

   /**
    * An object of type MouseHandler is installed as a mouse listener and mouse
    * motion listener on the MosaicPanel.  It responds to a mousePressed or
    * mouseDragged event on the panel by calling the applyCurrentTool() method
    * for the square that contained the mouse.
    */
   private class MouseHandler implements MouseListener, MouseMotionListener {
      public void mousePressed(MouseEvent evt) {
         int row = mosaic.yCoordToRowNumber(evt.getY());
         int col = mosaic.xCoordToColumnNumber(evt.getX());
         if (row >= 0 && row < mosaic.getRowCount() && col >= 0 && col < mosaic.getColumnCount())
            applyCurrentTool(row,col);
      }
      public void mouseDragged(MouseEvent evt) {
         int row = mosaic.yCoordToRowNumber(evt.getY());
         int col = mosaic.xCoordToColumnNumber(evt.getX());
         if (row >= 0 && row < mosaic.getRowCount() && col >= 0 && col < mosaic.getColumnCount())
            applyCurrentTool(row,col);
      }
      public void mouseReleased(MouseEvent evt) { }
      public void mouseEntered(MouseEvent evt) { }
      public void mouseExited(MouseEvent evt) { }
      public void mouseMoved(MouseEvent evt) { }
      public void mouseClicked(MouseEvent evt) { }
   } // end class MouseHandler
   
   /**
    * An object of type MenuHandler is used as the action listener for all
    * the menu items in the menu.  It can tell which item was selected by
    * the user by looking at the action command associated with the ActionEvent.
    * The action command will be the text of the menu item that was selected.
    * (This requires, of course, that all the menu items have different names.)
    * This is a fairly simple way to handle menu items, but not the best or
    * most flexible.
    */
   private class MenuHandler implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         String command = evt.getActionCommand();
         if (command.equals("Fill")) {  // color every square
            mosaic.setAutopaint(false);
            for (int row = 0; row < mosaic.getRowCount(); row++)
               for (int col = 0; col < mosaic.getColumnCount(); col++)
                  paintSquare(row,col);
            mosaic.setAutopaint(true);
         }
         else if (command.equals("Clear")) { // clear by filling mosaic with null
            mosaic.fill(null);
         }
         else if (command.equals("Use Randomness")) {
                 // Set the value of useRandomness depending on the menu item's state.
            JCheckBoxMenuItem toggle = (JCheckBoxMenuItem)evt.getSource();
            useRandomness = toggle.isSelected();
         }
         else if (command.equals("Use Symmetry")) {
                 // Set the value of useSymmetry depending on the menu item's state.
            JCheckBoxMenuItem toggle = (JCheckBoxMenuItem)evt.getSource();
            useSymmetry = toggle.isSelected();
         }
         else if (command.equals("Show Grouting")) {
                // Turn grouting on or off, depending on the menu item's state.
            JCheckBoxMenuItem toggle = (JCheckBoxMenuItem)evt.getSource();
            if (toggle.isSelected())
               mosaic.setGroutingColor(Color.GRAY);
            else
               mosaic.setGroutingColor(null);  // Turns grouting off.
         }
         else if (command.equals("Red")) { 
            currentRed = 255;    // Set current drawing color.
            currentGreen = 0;
            currentBlue = 0;
         }
         else if (command.equals("Green")) {
            currentRed = 0;
            currentGreen = 255;
            currentBlue = 0;
         }
         else if (command.equals("Blue")) {
            currentRed = 0;
            currentGreen = 0;
            currentBlue = 255;
         }
         else if (command.equals("Cyan")) {
            currentRed = 0;
            currentGreen = 255;
            currentBlue = 255;
         }
         else if (command.equals("Magenta")) {
            currentRed = 255;
            currentGreen = 0;
            currentBlue = 255;
         }
         else if (command.equals("Yellow")) {
            currentRed = 255;
            currentGreen = 255;
            currentBlue = 0;
         }
         else if (command.equals("Gray")) {
            currentRed = 180;
            currentGreen = 180;
            currentBlue = 180;
         }
         else if (command.equals("Custom Color...")) {
                // Let the user select the current drawing color using a
                // standard color chooser dialog.  The color chooser
                // is initially set to the current drawing color.
            Color c = new Color(currentRed, currentGreen, currentBlue);
            c = JColorChooser.showDialog(mosaic, "Select Drawing Color", c);
                // If c comes back null, it means that the user canceled
                // the dialog, so the current drawing color should not change.
            if (c != null) {
               currentRed = c.getRed();
               currentGreen = c.getGreen();
               currentBlue = c.getBlue();
            }
         }
         else if (command.equals("Draw"))
            currentTool = DRAW_TOOL;
         else if (command.equals("Erase"))
            currentTool = ERASE_TOOL;
         else if (command.equals("Draw 3x3"))
            currentTool = DRAW_3x3_TOOL;
         else if (command.equals("Erase 3x3"))
            currentTool = ERASE_3x3_TOOL;
      }
   } // end class MenuHandler
   
} // end class MosaicDrawController

