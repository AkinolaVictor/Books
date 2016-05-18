import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * This program demonstrates the effect of using different Strokes when
 * drawning lines and rectangles.  Fifteen small panels are shown, in three
 * rows and five columns.  Each panel uses a different stroke.  The stroke
 * widths are 1, 2, 5, 10, or 20, depending on the column.  The first row
 * uses the default values of the stroke for cap and join.  The second row
 * uses BasicStroke.CAP_ROUND and BasicStroke.JOIN_ROUND.  The third row
 * uses BasicStroke.CAP_BUTT and BasicStroke.JOIN_BEVEL.  In addition the
 * third row uses a dash pattern.  The program illustrates antialiasing,
 * which is off in the first row and on in the second and third rows.
 * This class has a main() routine and so can be run as an application.
 * The static nested class StrokeDemo.Applet runs the program as an applet.
 */
public class StrokeDemo extends JPanel {
   
   /**
    * The main routine simply opens a window that shows a StrokeDemo panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Click and Drag; Right-click for Rectangles");
      StrokeDemo content = new StrokeDemo();
      window.setContentPane(content);
      window.pack();  
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth())/2,
            (screenSize.height - window.getHeight())/2 );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
   
   /**
    * The public static class StrokeDemo$Applet represents this program
    * as an applet.  The applet's init() method simply sets the content 
    * pane of the applet to be a StrokeDemo.  To use the applet on
    * a web page, use code="StrokeDemo$Applet.class" as the name of
    * the class.  A reasonable size for the applet is 518 by 312.
    */
   public static class Applet extends JApplet {
      public void init() {
         StrokeDemo content = new StrokeDemo();
         setContentPane( content );
      }
   }
   
   
   /**
    * Each of the 15 panels in the program is an object of type Display.
    */
   private class Display extends JPanel {
      Stroke stroke;  // The stroke used for drawing in this panel.
      boolean antialiased;   // Should antialiasing be used?
      boolean drawLine = true;   // Should a line be drawn, or a rectangle?
      int x1, y1, x2, y2;  // Endpoints of line or corners of rectangle.
      Display(Stroke s, boolean a) {
         stroke = s;
         antialiased = a;
         x1 = y1 = 15;
         x2 = 80;
         y2 = 85;
         setPreferredSize(new Dimension(100,100));
         setBackground(Color.WHITE);
      }
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g;
         g2.setStroke(stroke);
         if (antialiased) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                  RenderingHints.VALUE_ANTIALIAS_ON);
         }
         if (drawLine)
            g2.drawLine(x1,y1,x2,y2);
         else {
            int a = Math.min(x1,x2);
            int b = Math.min(y1,y2);
            int w = Math.abs(x1 - x2);
            int h = Math.abs(y1 - y2);
            g2.drawRect(a,b,w,h);
         }
      }
   }
   
   
   /**
    * An object of type MouseHandler is used as a MouseListener and MouseMotionListener
    * on each of the Display panels in the applet.  When the user clicks and drags
    * on ANY panel, ALL panels are repainted to show a line or rectangle between the 
    * start point of the mouse drag operation and the current point.  If a right-mouse 
    * click starts the draw operation (Command-click on Mac), a rectangle is drawn;
    * otherwise a line is drawn.
    */
   private class MouseHandler implements MouseListener, MouseMotionListener {
      public void mousePressed(MouseEvent e) {
         for (int i = 0; i < 5; i++)
            for (int j = 0; j < 3; j++) {
               displays[i][j].x1 = e.getX();
               displays[i][j].x2 = e.getX();
               displays[i][j].y1 = e.getY();
               displays[i][j].y2 = e.getY();
               displays[i][j].drawLine = ! e.isMetaDown();
               displays[i][j].repaint();
            }
      }
      public void mouseDragged(MouseEvent e) {
         for (int i = 0; i < 5; i++)
            for (int j = 0; j < 3; j++) {
               displays[i][j].x2 = e.getX();
               displays[i][j].y2 = e.getY();
               displays[i][j].repaint();
            }
      }
      public void mouseReleased(MouseEvent e) { }
      public void mouseMoved(MouseEvent e) { }
      public void mouseClicked(MouseEvent e) { }
      public void mouseEntered(MouseEvent e) { }
      public void mouseExited(MouseEvent e) { }
   }
   
   
   /**
    * The arrays of Displays that represent the 15 small panels in the program.
    */
   private Display[][] displays = new Display[5][3];
   
   
   /**
    * The constructor creates and lays out the 15 display panels in a grid.
    * Each panel is created with a different Stroke.  The first row of panels
    * are not antialiased, but the other two rows are.
    */
   public StrokeDemo() {
      setLayout(new GridLayout(3,5,3,3));
      setBorder(BorderFactory.createLineBorder(Color.GRAY,3));
      setBackground(Color.GRAY);
      displays[0][0] = new Display(new BasicStroke(1), false);
      displays[1][0] = new Display(new BasicStroke(2), false);
      displays[2][0] = new Display(new BasicStroke(5), false);
      displays[3][0] = new Display(new BasicStroke(10), false);
      displays[4][0] = new Display(new BasicStroke(20), false);
      displays[0][1] = new Display(new BasicStroke(1, 
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), true);
      displays[1][1] = new Display(new BasicStroke(2, 
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), true);
      displays[2][1] = new Display(new BasicStroke(5, 
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), true);
      displays[3][1] = new Display(new BasicStroke(10, 
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), true);
      displays[4][1] = new Display(new BasicStroke(20, 
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), true);
      displays[0][2] = new Display(new BasicStroke(1, BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_BEVEL, 10, new float[] {5,5}, 0), true);
      displays[1][2] = new Display(new BasicStroke(2, BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_BEVEL, 10, new float[] {5,5}, 0), true);
      displays[2][2] = new Display(new BasicStroke(5, BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_BEVEL, 10, new float[] {5,5}, 0), true);
      displays[3][2] = new Display(new BasicStroke(10, BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_BEVEL, 10, new float[] {5,5}, 0), true);
      displays[4][2] = new Display(new BasicStroke(20, BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_BEVEL, 10, new float[] {5,5}, 0), true);
      MouseHandler listener = new MouseHandler();
      for (int row = 0; row < 3; row++)
         for (int col = 0; col < 5; col++) {
            add(displays[col][row]);
            displays[col][row].addMouseListener(listener);
            displays[col][row].addMouseMotionListener(listener);
         }
   }

}
