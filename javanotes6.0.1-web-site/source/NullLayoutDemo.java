
/**
 * This applet demonstrates how to do your own layout.  The layout
 * manager for the content pane is set to null.  The setBounds() method
 * of each component is called to set the size and position of the
 * component.
 *
 * It is assumed that this applet is 350 pixels wide and 240 pixels high!
 * If you want to deal with other sizes, you should implement the
 * ComponentEvent interface, and compute the sizes and positions of 
 * the components in terms of the actual width and height of the applet.
 * 
 * This class defines a main() routine and so can also be run as a stand-alone
 * application.  The main() routine just opens a window that shows the same
 * panel that is used in the applet.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NullLayoutDemo extends JApplet {
   
   /**
    * Main routine just opens a window that shows a NullLayoutPanel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Null Layout Demo");
      NullLayoutPanel content = new NullLayoutPanel();
      window.setContentPane(content);
      window.pack();  // User preferred size of content to set size of window.
      window.setResizable(false);  // User can't change the window's size.
      window.setLocation(100,100);
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
   /**
    * Applet's init() method just uses a NullLayoutPanel as the content
    * pane of the applet.
    */
   public void init() {
      NullLayoutPanel content = new NullLayoutPanel();
      setContentPane(content);
   }
   
   /**
    * This class defines a panel that uses a null layout.  Four components
    * are added to the panel, and their bounds are set explicitly.  It is
    * assumed that the panel has a fixed size of 350-by-240 pixels; the
    * preferred size of the panel is set to that width and height.
    */
   private static class NullLayoutPanel extends JPanel implements ActionListener {
      
      Checkerboard board;  // A checkerboard.  The Checkerboard class is 
                           //    nested inside the NullLayoutDemo class.
      
      JButton resignButton;      // Two buttons.
      JButton newGameButton;
      
      JLabel message;   // A label for displaying messages to the user.
      
      
      public NullLayoutPanel() {
         
         setLayout(null);  // I will do the layout myself!
         
         setBackground(new Color(0,150,0));  // A dark green background.
         
         setBorder( BorderFactory.createEtchedBorder() ); 
         
         setPreferredSize( new Dimension(350,240) );
         
         /* Create the components and add them to the content pane.  If you
          don't add them to the a container, they won't appear, even if
          you set their bounds! */
         
         board = new Checkerboard();
             // (Checkerborad is a subclass of JPanel, defined elsewhere.)
         add(board);
         
         newGameButton = new JButton("New Game");
         newGameButton.addActionListener(this);
         add(newGameButton);
         
         resignButton = new JButton("Resign");
         resignButton.addActionListener(this);
         add(resignButton);
         
         message = new JLabel("Click \"New Game\" to begin a game.");
         message.setForeground( new Color(100,255,100) ); // Light green.
         message.setFont(new Font("Serif", Font.BOLD, 14));
         add(message);
         
         /* Set the position and size of each component by calling
          its setBounds() method. */
         
         board.setBounds(20,20,164,164);
         newGameButton.setBounds(210, 60, 120, 30);
         resignButton.setBounds(210, 120, 120, 30);
         message.setBounds(20, 200, 330, 30);
                  
      }
      
      public void actionPerformed(ActionEvent evt) {
             // In this demo applet, clicking on a button just
             // changes the displayed message.
         message.setText(evt.getActionCommand() + " button was pressed.");
      }
      
   } // End nested class NullLayoutPanel

   
   /**
    * This canvas displays a 160-by-160 checkerboard pattern with
    * a 2-pixel black border.  It is assumed that the size of the
     * board is set to exactly 164-by-164 pixels.  This size is 
    *  set as the preferred size of the board.
    */
   private static class Checkerboard extends JPanel {
      
      public Checkerboard() {
         setBackground(Color.BLACK);
         setPreferredSize( new Dimension(164, 164) );
      }
      
      public void paintComponent(Graphics g) {
            // Draw a 2-pixel black border around the edges of the board.
            // (There is no need to call super.paintComponent() since
            // this method paints the entire surface of the component.)
         g.setColor(Color.BLACK);
         g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
         g.drawRect(1, 1, getSize().width - 3, getSize().height - 3);
         // Draw  checkerboard pattern in gray and lightGray.
         for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
               if ( row % 2 == col % 2 )
                  g.setColor(Color.LIGHT_GRAY);
               else
                  g.setColor(Color.GRAY);
               g.fillRect(2 + col*20, 2 + row*20, 20, 20);
            }
         }
      }
      
   }  // end nested class Checkerboard
   
   
} // end class NullLayoutDemo




