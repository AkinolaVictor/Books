import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.EnumSet;

/**
 * A simple paint program that is a first demonstration of using
 * an off-screen canvas.  This class can be run as a main program
 * and has a nested class PaintWithOffScreenCanvas.Applet that can be used as
 * an applet.  Note that the way that the off-screen canvas is used
 * in this class requires that the panel be non-resizable; this
 * is because the size of the off-screen canvas does not change
 * when the panel changes size.
 */
public class PaintWithOffScreenCanvas extends JPanel {
   
   /**
    * The main routine simply opens a window that shows a PaintWithOffScreenCanvas panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("PaintWithOffScreenCanvas");
      PaintWithOffScreenCanvas content = new PaintWithOffScreenCanvas();
      window.setContentPane(content);
      window.setJMenuBar(content.getMenuBar());
      window.pack();  
      window.setResizable(false); 
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth())/2,
            (screenSize.height - window.getHeight())/2 );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
   
   /**
    * The public static class PaintWithOffScreenCanvas$Applet represents this program
    * as an applet.  The applet's init() method simply sets the content 
    * pane of the applet to be a PaintWithOffScreenCanvas.  To use the applet on
    * a web page, use code="PaintWithOffScreenCanvas$Applet.class" as the name of
    * the class.  (This is a rather tricky way of defining an applet class to
    * run a program as an applet, instead of using a separate file to define the
    * applet.  It is not necessarily good style.)
    */
   public static class Applet extends JApplet {
      public void init() {
         PaintWithOffScreenCanvas content = new PaintWithOffScreenCanvas();
         content.setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
         setContentPane( content );
         setJMenuBar(content.getMenuBar());
      }
   }
   
   
   /**
    * The possible drawing tools in this program.  (The CURVE tool allows the
    * user to sketch a free-hand curve, while the LINE tool draws a line
    * between two points.  The SMUDGE tool lets the user "spread paint around"
    * with the mouse.  The ERASE tool erases with a 10-by-10 pixel rectangle.)
    */
   private enum Tool { CURVE, LINE, RECT, OVAL, FILLED_RECT, FILLED_OVAL, SMUDGE, ERASE }
   
   /**
    * The set of Tools that represent "shapes."  Shapes are handled differently
    * during dragging than other tools, since they are drawn "on top of" the
    * current picture during a mouse drag and are only added permanently to the
    * picture on mouse release.
    */
   private final static EnumSet<Tool> SHAPE_TOOLS = EnumSet.range(Tool.LINE, Tool.FILLED_OVAL);

   /**
    * The currently selected drawing tool.  Initially Tool.CURVE.  Can be
    * changed by the user with commands in the "Tool" menu.
    */
   private Tool currentTool = Tool.CURVE;
   
   /**
    * The current drawing color.  Initially Color.BLACK.  Can be changed
    * by the user with the "Select Drawing Color" command in the "Color" menu.
    */
   private Color currentColor = Color.BLACK;
   
   /**
    * The background color that is used to fill the off-screen canvas when
    * it is created.  If the user selects the "Fill With Color", the fill
    * color changes, and the canvas is filled with the new fill color,
    * erasing whatever was there before.
    */
   private Color fillColor = Color.WHITE;

   /**
    * The off-screen canvas.  This is not created until the first time
    * paintComponent() is called.  The size of the canvas will not change
    * after it is crearted, so this program has no support for resizing
    * the window.
    */
   private BufferedImage OSC;

   /**
    * This is set to true when the user is dragging the mouse.
    */
   private boolean dragging;
   
   /**
    * The start position of the mouse during a mouse drag.
    */
   private int startX, startY;
   
   /**
    * The current position of the mouse during a mouse drag.
    */
   private int currentX, currentY;
   
   
   /**
    * The constructor sets the preferred size of the panel to
    * 640-by-480.  It also sets up mouse listeners.
    */
   public PaintWithOffScreenCanvas() {
      setPreferredSize(new Dimension(640,480));
      MouseHandler mouseHandler = new MouseHandler();
      addMouseListener(mouseHandler);
      addMouseMotionListener(mouseHandler);
   }
   

   /**
    * The paintComponent() method copies the off-screen canvas to the screen
    * (first creating it, if necessary).  If a mouse drag is is progress,
    * then the current tool is not Tool.CURVE, then the shape that the user
    * is drawing is drawn over the off-screen canvas.  (This is to avoid
    * making the shape a permanent part of the picture until after the user
    * releases the mouse.  The effect is a "rubber band cursor" in which
    * the shape changes as the user drags the mouse, but the picture under
    * the shape is not affected.)
    */
   public void paintComponent(Graphics g) {

      /* First create the off-screen canvas, if it does not already exist. */ 

      if (OSC == null)
         createOSC();

      /* Copy the off-screen canvas to the panel.  Since we know that the
            image is already completely available, the fourth "ImageObserver"
            parameter to g.drawImage() can be null.  Since the canvas completely
            fills the panel, there is no need to call super.paintComponent(g). */

      g.drawImage(OSC,0,0,null);

      /* If the user is currently dragging the mouse to draw a line, oval,
            or rectangle, draw the shape on top the image from the off-screen
            canvas, using the current drawing color.  (This is not done if the
            user is drawing a curve or using the smudge tool.) */

      if (dragging && SHAPE_TOOLS.contains(currentTool)) {
         g.setColor(currentColor);
         putCurrentShape(g);
      }

   }
   
   
   /**
    * This method creates the off-screen canvas and fills it with the current
    * fill color.
    */
   private void createOSC() {
      OSC = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
      Graphics osg = OSC.getGraphics();
      osg.setColor(fillColor);
      osg.fillRect(0,0,getWidth(),getHeight());
      osg.dispose();
   }
   
   
   /**
    * A utility method to draw the current shape in a given graphics context.
    * It draws the correct shape for the current tool in a rectangle whose
    * corners are given by the starting position of the mouse and the current
    * position of the mouse.  This is used by paintComponent() during a
    * mouse drag.  It is also used to make the shape a permanent part of the
    * off-screen canvas when the mouse is released.
    * This method is not used when the current tool is Tool.CURVE or Tool.SMUDGE.  
    */
   private void putCurrentShape(Graphics g) {
      switch (currentTool) {
      case LINE:
         g.drawLine(startX, startY, currentX, currentY);
         break;
      case OVAL:
         putOval(g,false,startX, startY, currentX, currentY);
         break;
      case RECT:
         putRect(g,false,startX, startY, currentX, currentY);
         break;
      case FILLED_OVAL:
         putOval(g,true,startX, startY, currentX, currentY);
         break;
      case FILLED_RECT:
         putRect(g,true,startX, startY, currentX, currentY);
         break;
      }
   }
   
   
   /**
    * Draws a filled or unfilled rectangle with corners at the points (x1,y1)
    * and (x2,y2).  Nothing is drawn if x1 == x2 or y1 == y2.
    * @param g the graphics context where the rectangle is drawn
    * @param filled tells whether to draw a filled or unfilled rectangle.
    */
   private void putRect(Graphics g, boolean filled, int x1, int y1, int x2, int y2) {
      if (x1 == x2 || y1 == y2)
         return;
      if (x2 < x1) {  // Swap x1,x2 if necessary to make x2 > x1.
         int temp = x1;
         x1 = x2;
         x2 = temp;
      }
      if (y2 < y1) {  // Swap y1,y2 if necessary to make y2 > y1.
         int temp = y1;
         y1 = y2;
         y2 = temp;
      }
      if (filled)
         g.fillRect(x1,y1,x2-x1,y2-y1);
      else
         g.drawRect(x1,y1,x2-x1,y2-y1);
   }
   
   
   /**
    * Draws a filled or unfilled oval in the rectangle with corners at the 
    * points (x1,y1) and (x2,y2).  Nothing is drawn if x1 == x2 or y1 == y2.
    * @param g the graphics context where the oval is drawn
    * @param filled tells whether to draw a filled or unfilled oval.
    */
   private void putOval(Graphics g, boolean filled, int x1, int y1, int x2, int y2) {
      if (x1 == x2 || y1 == y2)
         return;
      if (x2 < x1) {  // Swap x1,x2 if necessary to make x2 > x1.
         int temp = x1;
         x1 = x2;
         x2 = temp;
      }
      if (y2 < y1) {  // Swap y1,y2 if necessary to make y2 > y1.
         int temp = y1;
         y1 = y2;
         y2 = temp;
      }
      if (filled)
         g.fillOval(x1,y1,x2-x1,y2-y1);
      else
         g.drawOval(x1,y1,x2-x1,y2-y1);
   }

   
   /**
    * Calls the repaint() method of this panel for the rectangle with corners
    * at the points (x1,y1) and (x2,y2).  An extra one-pixel border is added
    * to the area that is repainted; this allows for the size of the "pen"
    * that is used to draw lines and unfilled ovals and rectangles.
    */
   private void repaintRect(int x1, int y1, int x2, int y2) {
      if (x2 < x1) {  // Swap x1,x2 if necessary to make x2 >= x1.
         int temp = x1;
         x1 = x2;
         x2 = temp;
      }
      if (y2 < y1) {  // Swap y1,y2 if necessary to make y2 >= y1.
         int temp = y1;
         y1 = y2;
         y2 = temp;
      }
      x1--;
      x2++;
      y1--;
      y2++;
      repaint(x1,y1,x2-x1,y2-y1);
   }
   
   
   /**
    * Creates a menu bar for use with this panel, with "Color"
    * and "Tool" menus.
    */
   public JMenuBar getMenuBar() {
      JMenuBar menubar = new JMenuBar();
      JMenu colorMenu = new JMenu("Color");
      JMenu toolMenu = new JMenu("Tool");
      menubar.add(colorMenu);
      menubar.add(toolMenu);
      ActionListener listener = new MenuHandler();
      JMenuItem item;
      item = new JMenuItem("Draw With Black");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Draw With White");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Draw With Red");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Draw With Green");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Draw With Blue");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Draw With Yellow");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Select Drawing Color...");
      item.addActionListener(listener);
      colorMenu.add(item);
      colorMenu.addSeparator();
      item = new JMenuItem("Fill With Color...");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Curve");
      item.addActionListener(listener);
      toolMenu.add(item);
      toolMenu.addSeparator();
      item = new JMenuItem("Line");
      item.addActionListener(listener);
      toolMenu.add(item);
      item = new JMenuItem("Rectangle");
      item.addActionListener(listener);
      toolMenu.add(item);
      item = new JMenuItem("Oval");
      item.addActionListener(listener);
      toolMenu.add(item);
      item = new JMenuItem("Filled Rectangle");
      item.addActionListener(listener);
      toolMenu.add(item);
      item = new JMenuItem("Filled Oval");
      item.addActionListener(listener);
      toolMenu.add(item);
      toolMenu.addSeparator();
      item = new JMenuItem("Smudge");
      item.addActionListener(listener);
      toolMenu.add(item);
      item = new JMenuItem("Erase");
      item.addActionListener(listener);
      toolMenu.add(item);
      return menubar;
   }
   
   
   /**
    * This nested class defines the ActionListener that responds when the
    * user selects a command from one of the menus.  It is used in the
    * getMenuBar() method.
    */
   private class MenuHandler implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         String command = evt.getActionCommand();
         if (command.equals("Select Drawing Color...")) {
            Color newColor = JColorChooser.showDialog(PaintWithOffScreenCanvas.this, 
                  "Select Drawing Color", currentColor);
            if (newColor != null)
               currentColor = newColor;
         }
         else if (command.equals("Fill With Color...")) {
            Color newColor = JColorChooser.showDialog(PaintWithOffScreenCanvas.this, 
                  "Select Fill Color", fillColor);
            if (newColor != null) {
               fillColor = newColor;
               Graphics osg = OSC.getGraphics();
               osg.setColor(fillColor);
               osg.fillRect(0,0,OSC.getWidth(),OSC.getHeight());
               osg.dispose();
               PaintWithOffScreenCanvas.this.repaint();
            }
         }
         else if (command.equals("Draw With Black"))
            currentColor = Color.BLACK;
         else if (command.equals("Draw With White"))
            currentColor = Color.WHITE;
         else if (command.equals("Draw With Red"))
            currentColor = Color.RED;
         else if (command.equals("Draw With Green"))
            currentColor = Color.GREEN;
         else if (command.equals("Draw With Blue"))
            currentColor = Color.BLUE;
         else if (command.equals("Draw With Yellow"))
            currentColor = Color.YELLOW;
         else if (command.equals("Curve"))
            currentTool = Tool.CURVE;
         else if (command.equals("Line"))
            currentTool = Tool.LINE;
         else if (command.equals("Rectangle"))
            currentTool = Tool.RECT;
         else if (command.equals("Oval"))
            currentTool = Tool.OVAL;
         else if (command.equals("Filled Rectangle"))
            currentTool = Tool.FILLED_RECT;
         else if (command.equals("Filled Oval"))
            currentTool = Tool.FILLED_OVAL;
         else if (command.equals("Smudge"))
            currentTool = Tool.SMUDGE;
         else if (command.equals("Erase"))
            currentTool = Tool.ERASE;
      }
   } // end nested class MenuHandler
   
   
   /**
    * This nested class defines the object that listens for mouse and
    * mouse motion events on the panel.  It is used in the constructor.
    */
   private class MouseHandler implements MouseListener, MouseMotionListener {
      
      int prevX, prevY;  // Previous position of mouse during a drag.
      
      double[][] smudgeRed, smudgeGreen, smudgeBlue;  // data for smudge tool
      
      /**
       * When the ERASE or SMUDGE tools are used and the mouse jumps
       * from (x1,y1) to (x2,y2), the tool has to be applied to a
       * line of pixel positions between the two points in order to
       * cover the entire line that the mouse moves along.  The change
       * is made to the off-screen canvas, and repaint() is called to
       * copy the changes to the screen.
       */
      void applyToolAlongLine(int x1, int y1, int x2, int y2) {
         Graphics g = OSC.getGraphics();
         g.setColor(fillColor);    // (for ERASE only)
         int w = OSC.getWidth();   // (for SMUDGE only)
         int h = OSC.getHeight();  // (for SMUDGE only)
         int dist = Math.max(Math.abs(x2-x1),Math.abs(y2-y1));
             // dist is the number of points along the line from
             // (x1,y1) to (x2,y2) at which the tool will be applied.
         double dx = (double)(x2-x1)/dist;
         double dy = (double)(y2-y1)/dist;
         for (int d = 1; d <= dist; d++) {
                // Apply the tool at one of the points (x,y) along the
                // line from (x1,y1) to (x2,y2).
            int x = (int)Math.round(x1 + dx*d);
            int y = (int)Math.round(y1 + dy*d);
            if (currentTool == Tool.ERASE) {
                   // Erase a 10-by-10 block of pixels around (x,y)
               g.fillRect(x-5,y-5,10,10);
               repaint(x-5,y-5,10,10);
            }
            else { 
                  // For the SMUDGE tool, blend some of the color from
                  // the smudgeRed, smudgeGreen, and smudgeBlue arrays
                  // into the pixels in a 7-by-7 block around (x,y), and
                  // vice versa.  The effect is to smear out the color
                  // of pixels that are visited by the tool.
               for (int i = 0; i < 7; i++)
                  for (int j = 0; j < 7; j++) {
                     int r = y + j - 3;
                     int c = x + i - 3;
                     if (!(r < 0 || r >= h || c < 0 || c >= w || smudgeRed[i][j] == -1)) {
                        int curCol = OSC.getRGB(c,r);
                        int curRed = (curCol >> 16) & 0xFF;
                        int curGreen = (curCol >> 8) & 0xFF;
                        int curBlue = curCol & 0xFF;
                        int newRed = (int)(curRed*0.7 + smudgeRed[i][j]*0.3);
                        int newGreen = (int)(curGreen*0.7 + smudgeGreen[i][j]*0.3);
                        int newBlue = (int)(curBlue*0.7 + smudgeBlue[i][j]*0.3);
                        int newCol = newRed << 16 | newGreen << 8 | newBlue;
                        OSC.setRGB(c,r,newCol);
                        smudgeRed[i][j] = curRed*0.3 + smudgeRed[i][j]*0.7;
                        smudgeGreen[i][j] = curGreen*0.3 + smudgeGreen[i][j]*0.7;
                        smudgeBlue[i][j] = curBlue*0.3 + smudgeBlue[i][j]*0.7;
                     }
                  }
               repaint(x-3,y-3,7,7);
            }
         }
         g.dispose();
      }

      /**
       * Start a drag operation.
       */
      public void mousePressed(MouseEvent evt) {
         startX = prevX = currentX = evt.getX();
         startY = prevY = currentY = evt.getY();
         dragging = true;
         if (currentTool == Tool.ERASE) {
               // Erase a 10-by-10 block around the starting mouse position.
            Graphics g = OSC.getGraphics();
            g.setColor(fillColor);
            g.fillRect(startX-5,startY-5,10,10);
            g.dispose();
            repaint(startX-5,startY-5,10,10);
         }
         else if (currentTool == Tool.SMUDGE) {
                // Record the colors in a 7-by-7 block of pixels around the
                // starting mouse position into the arrays smudgeRed, 
                // smudgeGreen, and smudgeBlue.  These arrays hold the
                // red, green, and blue components of the colors.
            if (smudgeRed == null) {
                  // Create the arrays, if they have not already been created.
               smudgeRed = new double[7][7];
               smudgeGreen = new double[7][7];
               smudgeBlue = new double[7][7];
            }
            int w = OSC.getWidth();
            int h = OSC.getHeight();
            int x = evt.getX();
            int y = evt.getY();
            for (int i = 0; i < 7; i++)
               for (int j = 0; j < 7; j++) {
                  int r = y + j - 3;
                  int c = x + i - 3;
                  if (r < 0 || r >= h || c < 0 || c >= w) {
                        // A -1 in the smudgeRed array indicates that the
                        // corresponding pixel was outside the canvas.
                     smudgeRed[i][j] = -1;
                  }
                  else {
                     int color = OSC.getRGB(c,r);
                     smudgeRed[i][j] = (color >> 16) & 0xFF;
                     smudgeGreen[i][j] = (color >> 8) & 0xFF;
                     smudgeBlue[i][j] = color & 0xFF;
                  }
               }
         }
      }
      
      /**
       * Continue a drag operation when the user drags the mouse.
       * For the CURVE tool, a line is drawn from the previous mouse
       * position to the current mouse position in the off-screen canvas,
       * and the repaint() method is called for a rectangle that contains
       * the line segment that was drawn.  For shape tools, the off-screen
       * canvas is not changed, but the repaint() method is called so
       * that the paintComponent() method can redraw the picture with
       * the user's shape in the new position.  For the SMUDGE and
       * ERASE tools, the tool is applied along a line from the previous
       * mouse position to the current position;
       */
      public void mouseDragged(MouseEvent evt) {
         currentX = evt.getX();
         currentY = evt.getY();
         if (currentTool == Tool.CURVE) {
            Graphics g = OSC.getGraphics();
            g.setColor(currentColor);
            g.drawLine(prevX,prevY,currentX,currentY);
            g.dispose();
            repaintRect(prevX,prevY,currentX,currentY);
         }
         else if (SHAPE_TOOLS.contains(currentTool)) {
                 // Repaint the rectangles occupied by the previous position of
                 // the shape and by its current position.
            repaintRect(startX,startY,prevX,prevY);
            repaintRect(startX,startY,currentX,currentY);
         }
         else {
               // Tool has to be ERASE or SMUDGE
            applyToolAlongLine(prevX,prevY,currentX,currentY);
         }
         prevX = currentX;
         prevY = currentY;
      }

      /**
       * Finish a mouse drag operation.  Nothing is done unless the current tool
       * is a shape tool.  For shape tools, the user's shape is drawn to the
       * off-screen canvas, making it a permanent part of the picture, and
       * then the repaint() method is called to show the modified picture
       * on the screen.
       */
      public void mouseReleased(MouseEvent evt) {
         dragging = false;
         if (SHAPE_TOOLS.contains(currentTool)) {
            Graphics g = OSC.getGraphics();
            g.setColor(currentColor);
            putCurrentShape(g);
            g.dispose();
            repaint();
         }
      }
      
      public void mouseMoved(MouseEvent evt) { }
      public void mouseClicked(MouseEvent evt) { }
      public void mouseEntered(MouseEvent evt) { }
      public void mouseExited(MouseEvent evt) { }
      
   } // end nested class MenuHandler


} // end class PaintWithOffScreenCanvas

