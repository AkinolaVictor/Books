
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * This applet demonstrates Focus events and Key events.  A colored square
 * is drawn on the applet.  By pressing the arrow keys, the user can move
 * the square up, down, left, or right.  By pressing the keys
 * R, G, B, or K, the user can change the color of the square to red,
 * green, blue, or black, respectively.  Of course, none of the keys
 * will have any effect if the applet does not have the keyboard input
 * focus.  The applet changes appearance when it has the input focus.
 * A cyan-colored border is drawn around it.  When it does not have
 * the input focus, the message "Click to activate" is displayed
 * and the border is gray.
 * <p>This class contains a main() routine; when it is run as a stand-alone
 * application, the same JPanel that is used in the applet is shown in a
 * window instead.  The JPanel is defined using a nested subclass.
 */
public class KeyboardAndFocusDemo extends JApplet {

   /**
    * The main program just opens a window that shows an object of type
    * ContentPanel -- a nested class that is defined later in this class.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Keyboard and Focus Demo");
      window.setContentPane( new ContentPanel() );
      window.setSize(300,300);
      window.setLocation(100,100);
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
   /**
    * The init() method of the applet just sets the content pane
    * of the applet to be a panel of type ContentPane, a nested class
    * that is defined below, which does all the work.
    */
   public void init() {
      setContentPane( new ContentPanel() );
   }
   
   /**
    * Defines the content pane that is used in both the applet and
    * the stand-alone application version of this program.  The
    * panel displays a colored square that can be moved by the user,
    * by pressing the arrow keys.  The color of the square can be
    * changed by pressing the R, G, B, and K keys.  The panel also
    * displays a border and a message.
    */
   public static class ContentPanel extends JPanel
                  implements KeyListener, FocusListener, MouseListener {
      
         // (Note:  MouseListener is implemented only so that
         //         the applet can request the input focus when
         //         the user clicks on it.)
      
      private static final int SQUARE_SIZE = 40;  // Length of side of square.
      
      private Color squareColor;  // The color of the square.
      
      private int squareTop, squareLeft;  // Coordinates corner of square.

      /**
       * The constructor sets the initial position and color of the square
       * and registers itself to act as a listener for Key, Focus, and 
       * Mouse events.
       */
      public ContentPanel() {
         
         squareTop = 100;  // Initial position of top-left corner of square.
         squareLeft = 100;
         squareColor = Color.RED;  // Initial color of square.
         
         setBackground(Color.WHITE);

         addKeyListener(this);     // Set up event listening.
         addFocusListener(this);
         addMouseListener(this);
         
      } // end init();
      
      
      /**
       * Draws a border, square, and message in the panel.  The message and
       * the color of  the border depend on whether or not the pane has
       * the input focus.
       */
      public void paintComponent(Graphics g) {
         
         super.paintComponent(g);  // Fills the panel with its
                                   // background color, which is white.
         
         /* Draw a 3-pixel border, colored cyan if the applet has the
            keyboard focus, or in light gray if it does not. */
         
         if (hasFocus()) 
            g.setColor(Color.CYAN);
         else
            g.setColor(Color.LIGHT_GRAY);
         
         int width = getSize().width;  // Width of the applet.
         int height = getSize().height; // Height of the applet.
         g.drawRect(0,0,width-1,height-1);
         g.drawRect(1,1,width-3,height-3);
         g.drawRect(2,2,width-5,height-5);
         
         /* Draw the square. */
         
         g.setColor(squareColor);
         g.fillRect(squareLeft, squareTop, SQUARE_SIZE, SQUARE_SIZE);
         
         /* Print a message that depends on whether the panel has the focus. */
         
         g.setColor(Color.magenta);
         if (hasFocus()) {
            g.drawString("Arrow Keys Move Square",7,20);
            g.drawString("K, R, G, B Change Color",7,40);
         }
         else
            g.drawString("Click to activate",7,20);
         
      }  // end paintComponent()

      
      // ------------------- Event handling methods ----------------------

      /**
       * This will be called when the panel gains the input focus.  It just
       * calls repaint().  The panel will be redrawn with a cyan-colored border
       * and with a message about keyboard input.
       */
      public void focusGained(FocusEvent evt) {
         repaint();  // redraw with cyan border
      }
      
      
      /**
       * This will be called when the panel loses the input focus.  It just
       * calls repaint().  The panel will be redrawn with a gray-colored border
       * and with the message "Click to activate."
       */
      public void focusLost(FocusEvent evt) {
         repaint();  // redraw without cyan border
      }
      
      
      /**
       * This method is called when the user types a character on the keyboard
       * while the panel has the input focus.  If the character is R, G, B, or K
       * (or the corresponding lower case characters), then the color of the
       * square is changed to red, green, blue, or black, respectively.
       */
      public void keyTyped(KeyEvent evt) {
         
         char ch = evt.getKeyChar();  // The character typed.
         
         if (ch == 'B' || ch == 'b') {
            squareColor = Color.BLUE;
            repaint();   // Redraw panel with new color.
         }
         else if (ch == 'G' || ch == 'g') {
            squareColor = Color.GREEN;
            repaint();
         }
         else if (ch == 'R' || ch == 'r') {
            squareColor = Color.RED;
            repaint();
         }
         else if (ch == 'K' || ch == 'k') {
            squareColor = Color.BLACK;
            repaint();
         }
         
      }  // end keyTyped()
      
      
      /**
       * This is called each time the user presses a key while the panel has
       * the input focus.  If the key pressed was one of the arrow keys,
       * the square is moved (except that it is not allowed to move off the
       * edge of the panel).
       */
      public void keyPressed(KeyEvent evt) { 
         
         int key = evt.getKeyCode();  // keyboard code for the pressed key
         
         if (key == KeyEvent.VK_LEFT) {  // left arrow key
            squareLeft -= 8;
            if (squareLeft < 3)
               squareLeft = 3;
            repaint();
         }
         else if (key == KeyEvent.VK_RIGHT) {  // right arrow key
            squareLeft += 8;
            if (squareLeft > getWidth() - 3 - SQUARE_SIZE)
               squareLeft = getWidth() - 3 - SQUARE_SIZE;
            repaint();
         }
         else if (key == KeyEvent.VK_UP) {  // up arrow key
            squareTop -= 8;
            if (squareTop < 3)
               squareTop = 3;
            repaint();
         }
         else if (key == KeyEvent.VK_DOWN) {  // down arrow key
            squareTop += 8;
            if (squareTop > getHeight() - 3 - SQUARE_SIZE)
               squareTop = getHeight() - 3 - SQUARE_SIZE;
            repaint();
         }
         
      }  // end keyPressed()
      
      
      /**
       * This is called each time the user releases a key while the panel
       * has the input focus.  In this class, it does nothing, but it is
       * required to be here by the KeyListener interface.
       */
      public void keyReleased(KeyEvent evt) {
      }
      
      
      /**
       * This is called when the user clicks the panel with the mouse.
       * It just requests that the input focus be given to this panel.
       */
      public void mousePressed(MouseEvent evt) {
         requestFocus();
      }   
      
      
      public void mouseEntered(MouseEvent evt) { }  // Required by the
      public void mouseExited(MouseEvent evt) { }   //    MouseListener
      public void mouseReleased(MouseEvent evt) { } //       interface.
      public void mouseClicked(MouseEvent evt) { }
      
   } // end nested class ContentPanel

   
} // end class KeyboardAndFocusDemo
