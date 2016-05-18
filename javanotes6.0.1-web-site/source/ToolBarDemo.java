import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;


/**
 * Demonstrates a toolbar and the use of image icons on buttons.  The toolbar
 * contains a radio group with three JRadioButtons and a button all of which
 * use custom icons.  The actual program just lets the user draw curves
 * in three different colors.  This class can be run as a stand-alone program.
 * The nested class ToolBarDemo.Applet can be used as an applet.
 */
public class ToolBarDemo extends JPanel {

   /**
    * The main routine simply opens a window that shows a ToolBarDemo panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("TooBarDemo");
      ToolBarDemo content = new ToolBarDemo();
      window.setContentPane(content);
      window.pack();  
      window.setResizable(false); 
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth())/2,
            (screenSize.height - window.getHeight())/2 );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
   
   /**
    * The public static class ToolBarDemo$Applet represents this program
    * as an applet.  The applet's init() method simply sets the content 
    * pane of the applet to be a ToolBarDemo.  To use the applet on
    * a web page, use code="ToolBarDemo$Aplt.class" as the name of
    * the class.  A reasonable size for the applet is 360 by 300
    */
   public static class Applet extends JApplet {
      public void init() {
         ToolBarDemo content = new ToolBarDemo();
         setContentPane( content );
      }
   }
   
   
   /**
    * Defines the display area of the program, where the user can draw curves
    * in various colors.  (This class demonstrates resizing an off-screen canvas
    * when the size of the display changes.  The size can change if the user
    * drags the toolbar out of the window or to a new position.)
    */
   private class Display extends JPanel implements MouseListener, MouseMotionListener {
      
      private BufferedImage OSC;  // Off-screen canvas.
      private Color currentColor = Color.RED;  // Current drawing color.
      private int prevX, prevY;  // Previous mouse position, during mouse drags.
      private BasicStroke stroke;  // Stroke used for drawing.
      
      Display() { // constructor.
         addMouseListener(this);
         addMouseMotionListener(this);
         setPreferredSize(new Dimension(300,300));
         stroke = new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
      }

      void setCurrentColor(Color c) {  // change current drawing color
         currentColor = c;
      }
      
      void clear() { // clear the drawing area by filling it with white
         if (OSC != null) {
            Graphics g = OSC.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0,0,getWidth(),getHeight());
            g.dispose();
            repaint();
         }
      }
      
      public void paintComponent(Graphics g) { // just copies OSC to screen
         checkImage();
         g.drawImage(OSC,0,0,null);
      }
      
      void checkImage() {  // create or resize OSC if necessary
         if (OSC == null) {
               // Create the OSC, with a size to match the size of the panel.
            OSC = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
            clear();
         }
         else if (OSC.getWidth() != getWidth() || OSC.getHeight() != getHeight()) {
               // OSC size does not match the panel's size, so create a new OSC and
               // copy the picture in the old OSC to the new one.  This will scale
               // the current image to fit the new size.
            BufferedImage newOSC;
            newOSC = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
            Graphics g = newOSC.getGraphics();
            g.drawImage(OSC,0,0,getWidth(),getHeight(),null);
            g.dispose();
            OSC = newOSC;
         }
      }
      
      public void mousePressed(MouseEvent e) {
         prevX = e.getX();
         prevY = e.getY();
      }
      public void mouseDragged(MouseEvent e) {
         Graphics2D g2 = (Graphics2D)OSC.getGraphics();
         g2.setColor(currentColor);
         g2.setStroke(stroke);
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.drawLine(prevX,prevY,e.getX(),e.getY());
         g2.dispose();
         repaint();
         prevX = e.getX();
         prevY = e.getY();
      }
      public void mouseReleased(MouseEvent e) { }
      public void mouseMoved(MouseEvent e) { }
      public void mouseClicked(MouseEvent e) { }
      public void mouseEntered(MouseEvent e) { }
      public void mouseExited(MouseEvent e) { }
   }

   
   private Display display;  // The display area of the program.
   
   
   /**
    * Constructor adds a display and a toolbar to the program.  The
    * advice for using a toolbar is to place the toolbar in one of
    * the edge positions of a BorderLayout, and to put nothing in any
    * of the other three edge positions.  The user might be able
    * to drag the toolbar from one edge position to another.
    */
   public ToolBarDemo() {
      
      setLayout(new BorderLayout(2,2));
      setBackground(Color.GRAY);
      setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
      
      display = new Display();
      add(display, BorderLayout.CENTER);
      
      JToolBar toolbar = new JToolBar();
      add(toolbar, BorderLayout.NORTH);
      
      ButtonGroup group = new ButtonGroup();
      toolbar.add( makeColorRadioButton(Color.RED,group,true) );
      toolbar.add( makeColorRadioButton(Color.GREEN,group,false) );
      toolbar.add( makeColorRadioButton(Color.BLUE,group,false) );
      toolbar.addSeparator(new Dimension(20,20));
      
      toolbar.add( makeClearButton() );
      
   }
   
   
   /**
    * Create a JRadioButton and add it to a specified button group.  The button
    * is meant for selecting a drawing color in the display.  The color is used to 
    * create two custom icons, one for the unselected state of the button and one
    * for the selected state.  These icons are used instead of the usual
    * radio button icons.
    * @param c the color of the button, and the color to be used for drawing.
    *    (Note that c has to be "final" since it is used in the anonymous inner
    *    class that defines the response to ActionEvents on the button.)
    * @param grp the ButtonGroup to which the radio button will be added.
    * @param selected if true, then the state of the button is set to selected.
    * @return the radio button that was just created.
    */
   private JRadioButton makeColorRadioButton(final Color c, ButtonGroup grp, boolean selected) {
      
      /* Create an ImageIcon for the normal, unselected state of the button,
         using a BufferedImage that is drawn here from scratch. */
      
      BufferedImage image = new BufferedImage(30,30,BufferedImage.TYPE_INT_RGB);
      Graphics g = image.getGraphics();
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(0,0,30,30);
      g.setColor(c);
      g.fill3DRect(1, 1, 24, 24, true);
      g.dispose();
      Icon unselectedIcon = new ImageIcon(image);
      
      /* Create an ImageIcon for the selected state of the button. */

      image = new BufferedImage(30,30,BufferedImage.TYPE_INT_RGB);
      g = image.getGraphics();
      g.setColor(Color.DARK_GRAY);
      g.fillRect(0,0,30,30);
      g.setColor(c);
      g.fill3DRect(3, 3, 24, 24, false);
      g.dispose();
      Icon selectedIcon = new ImageIcon(image);
      
      /* Create and configure the button. */

      JRadioButton button = new JRadioButton(unselectedIcon);
      button.setSelectedIcon(selectedIcon);
      button.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent e) {
              // The action for this button sets the current drawing color
              // in the display to c.
            display.setCurrentColor(c);
         }
      });
      grp.add(button);
      if (selected)
         button.setSelected(true);

      return button;
   } // end makeColorRadioButton

   
   /**
    * Create a JButton that can be use to clear the display.  The button has
    * no text and its icon is a custom icon that shows a big X.  The icon is
    * created by drawing it on a Buffered image and using that image to 
    * create an ImageIcon.  The button has tooltip text "Clear the Display".
    */
   private JButton makeClearButton() {
      BufferedImage image = new BufferedImage(24,24,BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = (Graphics2D)image.getGraphics();
      g2.setColor(Color.LIGHT_GRAY);
      g2.fillRect(0,0,24,24);
      g2.setStroke( new BasicStroke(3));
      g2.setColor(Color.BLACK);
      g2.drawLine(4,4,20,20);
      g2.drawLine(4,20,20,4);
      g2.dispose();
      Icon clearIcon = new ImageIcon(image);
      
      Action clearAction = new AbstractAction(null,clearIcon) {
         public void actionPerformed(ActionEvent evt) {
            display.clear();
         }
      };
      clearAction.putValue(Action.SHORT_DESCRIPTION, "Clear the Display");
      JButton button = new JButton(clearAction);
      
      return button;
   }

}
