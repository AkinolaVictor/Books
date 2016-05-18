package edu.hws.eck.mdb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This applet appears on the screen as a single button.  When the user
 * clicks the button, a window of type MandelbrotFrame is opened.  Clicking
 * the button while the window is open will close the window.  After the
 * window has been closed, the button can be used to open a new one.
 * Obviously, the applet should be rather small, just large enough to
 * show one button.    Note that in an applet tag on a web page, this
 * class should be referred to as code="edu.hws.eck.mdb.LauncherApplet.class".
 */
public class LauncherApplet extends JApplet {
   
   private String launchCommand;  // Text for the button.
   private String closeCommand;   // Text for the button if the window is open.
   
   private JButton launchButton;
   private MandelbrotFrame frame;
   
   /**
    * Set up the applet to show just the launchButton, and add
    * an ActionListener to that button.
    */
   public void init() {
      launchCommand = I18n.tr("launcherApplet.launchCommand");
      closeCommand = I18n.tr("launcherApplet.closeCommand");
      launchButton = new JButton(launchCommand);
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(launchButton,BorderLayout.CENTER);
      launchButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            doClick();
         }
      } );
   }
   
   /**
    * Called by the ActionListener when the user clicks on the launcher
    * button.  If frame is null, a new frame is created and made visible.
    * If frame is not null, the frame is closed.  The launchButton is
    * disabled (briefly) while the frame is opening or closing. A
    * window listener on the frame re-enables the button and sets
    * its text when the window is finished opening or closing.  Also,
    * when the window closes, the frame variable is set to null which
    * will indicate to this routine that a new window can be opened.
    */
   private void doClick() {
      launchButton.setEnabled(false);
      if (frame == null) {
         frame = new MandelbrotFrame(true);
         frame.adjustToScreenIfNecessary();
         frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         frame.addWindowListener( new WindowAdapter() {
            public void windowOpened(WindowEvent evt) {
                  // Called when window opens; this only happens when
                  // the frame is created by the doClick() method.
               launchButton.setText(closeCommand);
               launchButton.setEnabled(true);
            }
            public void windowClosed(WindowEvent evt) {
                  // Called when the window closes for any reason.
                  // This can be because the user clicks the button
                  // again or because the user closes the frame with
                  // its "Close" command or by clicking its close box.
               launchButton.setText(launchCommand);
               launchButton.setEnabled(true);
               frame = null;
            }
         });
         frame.setVisible(true);
      }
      else {
         frame.dispose();
         frame.getMandelbrotPanel().getDisplay().shutDownThreads();
      }
   }
   
   /**
    * This is called when the applet is about to be destroyed.
    * Clean up by disposing the frame and making sure that the
    * worker threads in the MandebrotPanel get shut down. 
    */
   public void destroy() {
      if (frame != null) {
         frame.dispose();
         frame.getMandelbrotPanel().getDisplay().shutDownThreads();
         frame = null;
      }
   }

}
