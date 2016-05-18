
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This applet appears on a web page as a single button.  Clicking that
 * button will open a MosaicDrawFrame as a separate window.
 * Note that this class depends on MosaicPanel.java, MosaicDrawController.java.
 * and MosaicDrawFrame.java.
 */
public class MosaicDrawLauncherApplet extends JApplet {

   private MosaicDrawFrame window;  // This is null if no window has been opened;
                                    // otherwise, it points to the window.

   private JButton launchButton;  // The button that appears on the web page.
   
   public void init() {
      launchButton = new JButton("Launch MosaicDraw");
      getContentPane().add(launchButton, BorderLayout.CENTER);
      launchButton.addActionListener(new ActionListener() {
             // When the button is clicked, a window is opened if it does not
             // yet exist, or is closed if it does exist.
         public void actionPerformed(ActionEvent evt) {
            launchButton.setEnabled(false);
            if (window == null)
               openWindow();
            else
               window.dispose();
         }
      });
   }

   private void openWindow() {
      JFrame frame = new MosaicDrawFrame();
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.addWindowListener( new WindowAdapter() {
             // When the window opens, the instance variable "window" is set
             // to point to it, and the button is changed to read "Close MosaicDraw".
             // When the window closes, either because the button has been pressed
             // again or because the user has clicked the window's close box, the
             // instance variable window is set to null and the text of the button
             // is set back to "Launch MosaicDraw".
         public void windowOpened(WindowEvent evt) {
            window = (MosaicDrawFrame)evt.getSource();
            launchButton.setText("Close MosaicDraw");
            launchButton.setEnabled(true);
         }
         public void windowClosed(WindowEvent evt) {
            window = null;
            launchButton.setText("Launch MosaicDraw");
            launchButton.setEnabled(true);
         }
      });
      frame.setVisible(true);
   }
   

}
