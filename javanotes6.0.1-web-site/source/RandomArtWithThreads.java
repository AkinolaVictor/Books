
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A panel that can shows randomly generated "art".  When the user
 * clicks a "Start" button, a new random artwork is generated every
 * two seconds.
 * <p>This program demonstrates using a thread for a very simple
 * animation.  (In fact, it would be more appropriate to use a
 * timer.)
 */
public class RandomArtWithThreads extends JPanel {
   
   /**
    * This main routine just shows a panel of type RandomArtWithThreads.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Demo: Animation with a Thread");
      RandomArtWithThreads content = new RandomArtWithThreads();
      window.setContentPane(content);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth()) / 2,
            (screenSize.height - window.getHeight()) / 2 );
      window.setVisible(true);
   }
   

   private Display display;  // A panel where the random "art" is drawn
   
   private JButton startButton;   // Button for starting/stoping the animation
   
   private Runner runner;  // The thread that drives the animation.

   private volatile boolean running;  // Set to false to stop the thread.
    
   
   /**
    * This class defines the threads that drive the animation.
    */
   private class Runner extends Thread {
      public void run() {
         while (running) {
            display.repaint();
            try {
               Thread.sleep(2000);  // Wait two seconds between repaints.
            }
            catch (InterruptedException e) {
            }
         }
      }
   }

   
   /**
    * A subpanel of type Display that draws the random "art".  (The
    * paintComponent method is taken from the class RandomArtPanel.)
    */
   private class Display extends JPanel {
      Display() {
         setPreferredSize(new Dimension(500,400));
         setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      }
      protected void paintComponent(Graphics g) {
         // Note:  Since the next three lines fill the entire panel with
         // gray, there is no need to call super.paintComponent(g), since
         // any drawing that it does will only be covered up anyway.
         
         Color randomGray = Color.getHSBColor( 1.0F, 0.0F, (float)Math.random() );
         g.setColor(randomGray);
         g.fillRect( 0, 0, getWidth(), getHeight() );
         
         int artType = (int)(4*Math.random());

         switch (artType) {
         case 0:
            for (int i = 0; i < 500; i++) {
               int x1 = (int)(getWidth() * Math.random());
               int y1 = (int)(getHeight() * Math.random());
               int x2 = (int)(getWidth() * Math.random());
               int y2 = (int)(getHeight() * Math.random());
               Color randomHue = Color.getHSBColor( (float)Math.random(), 1.0F, 1.0F);
               g.setColor(randomHue);
               g.drawLine(x1,y1,x2,y2);
            }
            break;
         case 1:
            for (int i = 0; i < 200; i++) {
               int centerX =  (int)(getWidth() * Math.random());
               int centerY = (int)(getHeight() * Math.random());
               Color randomHue = Color.getHSBColor( (float)Math.random(), 1.0F, 1.0F);
               g.setColor(randomHue);
               g.drawOval(centerX - 50, centerY - 50, 100, 100);
            }
            break;
         default:
            for (int i = 0; i < 25; i++) {
               int centerX =  (int)(getWidth() * Math.random());
               int centerY = (int)(getHeight() * Math.random());
               int size = 30 + (int)(170*Math.random());
               Color randomColor = new Color( (int)(256*Math.random()), 
                     (int)(256*Math.random()), (int)(256*Math.random()) );
               g.setColor(randomColor);
               g.fill3DRect(centerX - size/2, centerY - size/2, size, size, true);
            }
            break;
         }
      }
   }
   
   
   /**
    * The constructor sets up the panel, containing the Display and the
    * Start button below it.
    */
   public RandomArtWithThreads() {
      setLayout(new BorderLayout());
      display = new Display();
      add(display, BorderLayout.CENTER);
      startButton = new JButton("Start");
      JPanel bottom = new JPanel();
      bottom.add(startButton);
      bottom.setBackground(Color.WHITE);
      add(bottom,BorderLayout.SOUTH);
      startButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (running)
               stop();
            else
               start();
         }
      });
   }
   
   
   /**
    * This method is called when the user clicks the Start button,
    * while no thread is running.  It starts a new thread and
    * sets the signaling variable, running, to true;  Also changes
    * the text on the Start button to "Finish".
    */
   private void start() {
      startButton.setText("Finish");
      runner = new Runner();
      running = true;  // Set the signal before starting the thread!
      runner.start();
   }
   
   
   /**
    * This method is called when the user clicks the button while
    * a thread is running.  A signal is sent to the thread to terminate,
    * by setting the value of the signaling variable, running, to false.
    */
   private void stop() {

      /* Set the value of the signaling variable to false as a signal
       * to the thread to terminate.
       */

      running = false; 
      
      /* Wake the thread, in case it is sleeping, to get a more
       * immediate reaction to the signal.
       */
      
      runner.interrupt(); 
      
      /* Wait for the thread to stop before setting runner = null.
       * One second should be plenty of time for this to happen, but
       * in case something goes wrong, it's better not to 
       */
      
      try {
         runner.join(1000);  // Wait for thread to stop.  One second should be plenty of time.
      }
      catch (InterruptedException e) {
      }
      
      runner = null;
   }
   

} // end RandomArtWithThreads
