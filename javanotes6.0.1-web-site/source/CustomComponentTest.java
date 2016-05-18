
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Tests the custom component classes MirrorText and StopWatchLabel
 * by adding them both to a panel.  The panel also contains a button
 * that changes the text on the MirrorText component (and also on
 * the button itself.  This program also demonstrates how the layout
 * of the panel is recomputed when the components are changed.  
 * This class can be run as a standalone application and contains
 * a nested class that can be used to run the program as an applet.
 */
public class CustomComponentTest extends JPanel {

   /**
    * The main routine simply opens a window that shows a CustomComponentTest panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("CustomComponentText");
      CustomComponentTest content = new CustomComponentTest();
      window.setContentPane(content);
      window.setSize(420,150);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth())/2,
            (screenSize.height - window.getHeight())/2 );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }


   /**
    * For running the program as an applet, use code="CustomComponentTest$Applet.class"
    * A reasonable size for the applet is 425 by 125.
    */
   public static class Applet extends JApplet {
      public void init() {
         setContentPane( new CustomComponentTest() );
      }
   }



   private MirrorText greet;
   private StopWatchLabel timer;
   private JButton changeText;

   public CustomComponentTest() {

      setLayout(new FlowLayout());

      greet = new MirrorText("PLEASE LET ME OUT!");
      greet.setBackground(Color.black);
      greet.setForeground(Color.red);
      greet.setFont( new Font("SansSerif", Font.BOLD, 30) );
      add(greet);

      timer = new StopWatchLabel();
      timer.setBackground(Color.white);
      timer.setForeground(Color.blue);
      timer.setOpaque(true);
      timer.setFont(new Font("Serif", Font.PLAIN, 20));
      add( timer );

      changeText = new JButton("Change Text in this Program");
      add(changeText);

      changeText.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            if (greet.getText().equals("PLEASE LET ME OUT!"))
               greet.setText("Help!");
            else
               greet.setText("PLEASE LET ME OUT!");
            if (timer.isRunning() == false)
               timer.setText("Please click me.");
            if (changeText.getText().equals("Change Back"))
               changeText.setText("Change Text in this Program");
            else
               changeText.setText("Change Back");
         }
      } );

   } 

} 

