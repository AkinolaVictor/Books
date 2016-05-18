import java.awt.*;        
import java.awt.event.*;
import javax.swing.*;


/**
 * This panel implements a simple arcade game in which the user tries to blow
 * up a "submarine" (a black oval) by dropping "depth charges" (a red disk) from 
 * a "boat" (a blue roundrect).  The user moves the boat with the left- and 
 * right-arrow keys and drops the depth charge with the down-arrow key.
 * The sub moves left and right erratically along the bottom of the panel.
 */
public class SubKillerPanel extends JPanel {
   
   private Timer timer;        // Timer that drives the animation.

   private int width, height;  // The size of the panel -- the values are set
                               //    the first time the paintComponent() method
                               //    is called.  This class is not designed to
                               //    handle changes in size; once the width and
                               //    height have been set, they are not changed.
                               //    Note that width and height cannot be set
                               //    in the constructor because the width and
                               //    height of the panel have not been set at
                               //    the time that the constructor is called.

   private Boat boat;          // The boat, bomb, and sub objects are defined
   private Bomb bomb;          //    by nested classes Boat, Bomb, and Submarine,
   private Submarine sub;      //    which are defined later in this class.
                               //    Note that the objects are created in the
                               //    paintComponent() method, after the width
                               //    and height of the panel are known.
   
   
   /**
    * The constructor sets the background color of the panel, creates the
    * timer, and adds a KeyListener, FocusListener, and MouseListener to the
    * panel.  These listeners, as well as the ActionListener for the timer
    * are defined by anonymous inner classes.  The timer will run only
    * when the panel has the input focus.
    */
   public SubKillerPanel() {

      setBackground(Color.GREEN);

      ActionListener action = new ActionListener() {
               // Defines the action taken each time the timer fires.
         public void actionPerformed(ActionEvent evt) {
            if (boat != null) {
               boat.updateForNewFrame();
               bomb.updateForNewFrame();
               sub.updateForNewFrame();
            }
            repaint();
         }
      };
      timer = new Timer( 30, action );  // Fires every 30 milliseconds.

      addMouseListener( new MouseAdapter() {
              // The mouse listener simply requests focus when the user
              // clicks the panel.
         public void mousePressed(MouseEvent evt) {
            requestFocus();
         }
      } );

      addFocusListener( new FocusListener() {
             // The focus listener starts the timer when the panel gains
             // the input focus and stops the timer when the panel loses
             // the focus.  It also calls repaint() when these events occur.
         public void focusGained(FocusEvent evt) {
            timer.start();
            repaint();
         }
         public void focusLost(FocusEvent evt) {
            timer.stop();
            repaint();
         }
      } );

      addKeyListener( new KeyAdapter() {
             // The key listener responds to keyPressed events on the panel. Only
             // the left-, right-, and down-arrow keys have any effect.  The left- and
             // right-arrow keys move the boat while down-arrow releases the bomb.
         public void keyPressed(KeyEvent evt) {
            int code = evt.getKeyCode();  // Which key was pressed?
            if (code == KeyEvent.VK_LEFT) {
                 // Move the boat left.  (If this moves the boat out of the frame, its
                 // position will be adjusted in the boat.updateForNewFrame() method.)
               boat.centerX -= 15;
            }
            else if (code == KeyEvent.VK_RIGHT) {  
                 // Move the boat right.  (If this moves boat out of the frame, its
                 // position will be adjusted in the boat.updateForNewFrame() method.)
               boat.centerX += 15;
            }
            else if (code == KeyEvent.VK_DOWN) {
                  // Start the bomb falling, if it is not already falling.
               if ( bomb.isFalling == false )
                  bomb.isFalling = true;
            }
         }
      } );

   } // end constructor
   
   
   /**
    * The paintComponent() method draws the current state of the game.  It
    * draws a gray or cyan border around the panel to indicate whether or not
    * the panel has the input focus.  It draws the boat, sub, and bomb by
    * calling their respective draw() methods.
    */
   public void paintComponent(Graphics g) {
      
      super.paintComponent(g);  // Fill panel with background color, green.
      
      if (boat == null) {
            // The first time that paintComponent is called, it assigns
            // values to the instance variables.
         width = getWidth();
         height = getHeight();
         boat = new Boat();
         sub = new Submarine();
         bomb = new Bomb();
      }
      
      if (hasFocus())
         g.setColor(Color.CYAN);
      else {
         g.setColor(Color.RED);
         g.drawString("CLICK TO ACTIVATE", 20, 30);
         g.setColor(Color.GRAY);
      }
      g.drawRect(0,0,width-1,height-1);  // Draw a 3-pixel border.
      g.drawRect(1,1,width-3,height-3);
      g.drawRect(2,2,width-5,height-5);
      
      boat.draw(g);
      sub.draw(g);
      bomb.draw(g);
      
   } // end drawFrame()
   
   
   /**
    * This nested class defines the boat.  Note that its constructor cannot
    * be called until the width of the panel is known!
    */
   private class Boat {
      int centerX, centerY;  // Current position of the center of the boat.
      Boat() { // Constructor centers the boat horizontally, 80 pixels from top.
         centerX = width/2;
         centerY = 80;
      }
      void updateForNewFrame() { // Makes sure boat has not moved off screen.
         if (centerX < 0)
            centerX = 0;
         else if (centerX > width)
            centerX = width;
      }
      void draw(Graphics g) {  // Draws the boat at its current location.
         g.setColor(Color.BLUE);
         g.fillRoundRect(centerX - 40, centerY - 20, 80, 40, 20, 20);
      }
   } // end nested class Boat
   
   
   /**
    * This nested class defines the bomb. 
    */
   private class Bomb {
      int centerX, centerY; // Current position of the center of the bomb.
      boolean isFalling;    // If true, the bomb is falling; if false, it
                            // is attached to the boat.
      Bomb() { // Constructor creates a bomb that is initially attached to boat.
         isFalling = false;
      }
      void updateForNewFrame() {  // If bomb is falling, take appropriate action.
         if (isFalling) {
            if (centerY > height) {
                  // Bomb has missed the submarine.  It is returned to its
                  // initial state, with isFalling equal to false.
               isFalling = false;
            }
            else if (Math.abs(centerX - sub.centerX) <= 36 &&
                                 Math.abs(centerY - sub.centerY) <= 21) {
                  // Bomb has hit the submarine.  The submarine
                  // enters the "isExploding" state.
               sub.isExploding = true;
               sub.explosionFrameNumber = 1;
               isFalling = false;  // Bomb reappears on the boat.
            }
            else {
                 // If the bomb has not fallen off the panel or hit the
                 // sub, then it is moved down 10 pixels.
               centerY += 10;
            }
         }
      }
      void draw(Graphics g) {  // Draw the bomb.
         if ( ! isFalling ) {  // If not falling, set centerX and CenterY
                              // to show the bomb on the bottom of the boat.
            centerX = boat.centerX;
            centerY = boat.centerY + 23;
         }
         g.setColor(Color.RED);
         g.fillOval(centerX - 8, centerY - 8, 16, 16);
      }
   } // end nested class Bomb
   
   
   /**
    * This nested class defines the sub.  Note that its constructor cannot
    * be called until the width of the panel is known!
    */
   private class Submarine {
      int centerX, centerY; // Current position of the center of the sub.
      boolean isMovingLeft; // Tells whether the sub is moving left or right
      boolean isExploding;  // Set to true when the sub is hit by the bomb.
      int explosionFrameNumber;  // If the sub is exploding, this is the number
                                 //   of frames since the explosion started.
      Submarine() {  // Create the sub at a random location 40 pixels from bottom.
         centerX = (int)(width*Math.random());
         centerY = height - 40;
         isExploding = false;
         isMovingLeft = (Math.random() < 0.5);
      }
      void updateForNewFrame() { // Move sub or increase explosionFrameNumber.
         if (isExploding) {
               // If the sub is exploding, add 1 to explosionFrameNumber.
               // When the number reaches 15, the explosion ends and the
               // sub reappears in a random position.
            explosionFrameNumber++;
            if (explosionFrameNumber == 15) { 
               centerX = (int)(width*Math.random());
               centerY = height - 40;
               isExploding = false;
               isMovingLeft = (Math.random() < 0.5);
            }
         }
         else { // Move the sub.
            if (Math.random() < 0.04) {  
                  // In one frame out of every 25, on average, the sub
                  // reverses its direction of motion.
               isMovingLeft = ! isMovingLeft; 
            }
            if (isMovingLeft) { 
                  // Move the sub 5 pixels to the left.  If it moves off
                  // the left edge of the panel, move it back to the left
                  // edge and start it moving to the right.
               centerX -= 5;  
               if (centerX <= 0) {  
                  centerX = 0; 
                  isMovingLeft = false; 
               }
            }
            else {
                  // Move the sub 5 pixels to the right.  If it moves off
                  // the right edge of the panel, move it back to the right
                  // edge and start it moving to the left.
               centerX += 5;         
               if (centerX > width) {  
                  centerX = width;   
                  isMovingLeft = true; 
               }
            }
         }
      }
      void draw(Graphics g) {  // Draw sub and, if it is exploding, the explosion.
         g.setColor(Color.BLACK);
         g.fillOval(centerX - 30, centerY - 15, 60, 30);
         if (isExploding) {
                // Draw an "explosion" that grows in size as the number of
                // frames since the start of the explosion increases.
            g.setColor(Color.YELLOW);
            g.fillOval(centerX - 4*explosionFrameNumber,
                  centerY - 2*explosionFrameNumber,
                  8*explosionFrameNumber,
                  4*explosionFrameNumber);
            g.setColor(Color.RED);
            g.fillOval(centerX - 2*explosionFrameNumber,
                  centerY - explosionFrameNumber/2,
                  4*explosionFrameNumber,
                  explosionFrameNumber);
         }
      }
   } // end nested class Submarine   
   
   
} // end class SubKiller
