import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.AudioClip;
import java.net.URL;

/**
 * A demo program that can play a few sounds from resource files and
 * can show several different cursors, including a custom cursor that
 * is created from an image resource.  A main() routine allows this class
 * to be run as a stand-alone application.  The nested class
 * SoundAndCursorDemo.Aplt can be used to run it as an applet.
 * (The sound files used by this demo were taken from the KDE desktop.)
 */
public class SoundAndCursorDemo extends JPanel {
   
   /**
    * The main routine simply opens a window that shows a SoundAndCursorDemo panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("SoundAndCursorDemo");
      SoundAndCursorDemo content = new SoundAndCursorDemo();
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
    * The public static class SoundAndCursorDemo$Aplt represents this program
    * as an applet.  The applet's init() method simply sets the content 
    * pane of the applet to be a SoundAndCursorDemo.  To use the applet on
    * a web page, use code="SoundAndCursorDemo$Aplt.class" as the name of
    * the class.  (This is a tricky way of defining an applet class to run a
    * program as an applet, and is not necessarily good style.)  A reasonable size
    * for the applet is 480 by 150.
    */
   public static class Aplt extends JApplet {
      public void init() {
         SoundAndCursorDemo content = new SoundAndCursorDemo();
         setContentPane( content );
      }
   }
   

   public SoundAndCursorDemo() {
      setLayout(new GridLayout(4,2,3,3));
      setBackground(Color.GRAY);
      setBorder(BorderFactory.createLineBorder(Color.GRAY,3));
      JButton button;
      ButtonHandler listener = new ButtonHandler();
      button = new JButton("Play Sound #1");
      button.addActionListener(listener);
      add(button);
      button = new JButton("Use Crosshair Cursor");
      button.addActionListener(listener);
      add(button);
      button = new JButton("Play Sound #2");
      button.addActionListener(listener);
      add(button);
      button = new JButton("Use Wait Cursor");
      button.addActionListener(listener);
      add(button);
      button = new JButton("Play Sound #3");
      button.addActionListener(listener);
      add(button);
      button = new JButton("Use Move Cursor");
      button.addActionListener(listener);
      add(button);
      button = new JButton("Play Sound #4");
      button.addActionListener(listener);
      add(button);
      button = new JButton("Use Custom Cursor");
      button.addActionListener(listener);
      add(button);
   }
   
   
   private class ButtonHandler implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         String command = evt.getActionCommand();
         if (command.equals("Play Sound #1"))
            playAudioResource("snc_resources/KDE_Beep.wav");
         else if (command.equals("Play Sound #2"))
            playAudioResource("snc_resources/KDE_Beep_Lightning.wav");
         else if (command.equals("Play Sound #3"))
            playAudioResource("snc_resources/KDE_Window_Iconify.wav");
         else if (command.equals("Play Sound #4"))
            playAudioResource("snc_resources/KDE_Window_Sticky.wav");
         else if (command.equals("Use Wait Cursor"))
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         else if (command.equals("Use Move Cursor"))
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
         else if (command.equals("Use Crosshair Cursor"))
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
         else if (command.equals("Use Custom Cursor"))
            useCustomCursor("snc_resources/TinySmiley.png");
         if (command.startsWith("Play"))
            setCursor(Cursor.getDefaultCursor());
      }
   }
   
   
   /**
    * Plays the sound from an audio resource file.  If the resource can't
    * be found, nothing is done.  (If the sound is to replayed several
    * times, it would be more efficient to save the AudioClip in an
    * instance variable instead of loading it each time it is played.)
    */
   private void playAudioResource(String audioResourceName) {
      ClassLoader cl = SoundAndCursorDemo.class.getClassLoader();
      URL resourceURL = cl.getResource(audioResourceName);
      if (resourceURL != null) {
         AudioClip sound = JApplet.newAudioClip(resourceURL);
         sound.play();
      }
   }


   /**
    * Sets the current cursor for this component to be a custom
    * cursor defined by an image resource.  If the image resource
    * can't be found, then the component's cursor is not changed.
    * (For this demo, the hotspot of the cursor is always set to
    * be the point (7,7).)
    */
   private void useCustomCursor(String imageResourceName) {
      ClassLoader cl = SoundAndCursorDemo.class.getClassLoader();
      URL resourceURL = cl.getResource(imageResourceName);
      if (resourceURL != null) {
         Toolkit toolkit = Toolkit.getDefaultToolkit();
         Image image = toolkit.createImage(resourceURL);
         Point hotSpot = new Point(7,7);
         Cursor cursor = toolkit.createCustomCursor(image, hotSpot, "smiley");
         setCursor(cursor);
      }
   }

}
