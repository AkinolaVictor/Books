
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/** 
 * This applet displays 25 copies of a message.  The color and 
 * position of each message is selected at random.  The font
 * of each message is randomly chosen from among five possible
 * fonts.  The messages are displayed on a black background.
 * When the user clicks on the applet, new random fonts, colors,
 * and positions are chosen.
 * 
 * Note:  The positions, colors, and fonts used for the strings 
 * are stored in an array so that the applet can be properly
 * redrawn whenever necessary.
 */
public class RandomStringsWithArray extends JApplet {
   
   private final static int MESSAGE_COUNT = 25;  // Number of copies of the message.
   
   private String message;  // The message to be displayed.  This can be set using
                            // an applet param with name "message".  If no value is
                            // provided in the applet tag, then the string "Java!" 
                            // is used as the default.
   
   private Font[] fonts;  // An array containing the five fonts.
                          //   (To be initialized in the constructor.)
   
   private StringData[] stringData;  // An array containing the font, color, and
                                     // and position of each copy of the message.
                                     // StringData is a nested class.
   
   
   /**
    * An object of this type holds the position, color, and font
    * of one copy of the string.
    */
   private static class StringData {
      int x, y;     // The coordinates of the left end of baseline of string.
      Color color;  // The color in which the string is drawn.
      Font font;    // The font that is used to draw the string.
   }
   

   /**
    * This class defines the drawing surface of the applet.  It displays
    * the string.  An object of this type is used as the content pane
    * of the applet.
    */
   private class Display extends JPanel {
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         for ( StringData data : stringData) {
                // Draw a copy of the message in the position, color, and font
                // stored in data.
            g.setColor( data.color );
            g.setFont( data.font );
            g.drawString( message, data.x, data.y );
         }
      }
   }
   
   
   /**
    * Initialize the applet.  Get the message from an applet parameter,
    * if it's there.  Create the five fonts.  Create the string data
    * array and fill it with random data.  Set up a MouseListener to
    * listen for clicks on the applet and respond by computing and
    * displaying new data.
    */
   public void init() {
      
      Display display = new Display(); // Drawing surface, belonging to 
                                       // nested class Display.
      display.setBackground(Color.BLACK);

      addMouseListener( new MouseAdapter() {
         public void mousePressed(MouseEvent evt) {
               // When user presses the mouse, create a new set of
               // random data for the strings, and repaint the applet.
            makeStringData();
            repaint();
         }
      });
      
      setContentPane(display);
      
      message = getParameter("message");  // Get message to be displayed.
      if (message == null)
         message = "Java!"; // The default, if there is no "message" param.
      
      fonts = new Font[5];  // Create the fonts.
      fonts[0] = new Font("Serif", Font.BOLD, 14);
      fonts[1] = new Font("SansSerif", Font.BOLD + Font.ITALIC, 24);
      fonts[2] = new Font("Monospaced", Font.PLAIN, 20);
      fonts[3] = new Font("Dialog", Font.PLAIN, 30);
      fonts[4] = new Font("Serif", Font.ITALIC, 36);
      
      stringData = new StringData[MESSAGE_COUNT];  // Create the array.
      for (int i = 0; i < MESSAGE_COUNT; i++)      // Fill array with objects.
         stringData[i] = new StringData();
      makeStringData();                            // Fill objects with data.
      
   } // end init()
   
   
   /**
    * Fill the StringData array with random position, font, and color values.
    * This method assumes that the array itself has already been created and
    * that each element of the array has been set to point to an object.  This
    * method does not create new objects; it just modifies the data stored in
    * the existing objects in the array.
    */
   private void makeStringData() {
      
      int width = getWidth();       // Get the applet's size
      int height = getHeight();
      
      for (StringData data : stringData) {
              // Set random values in data for the postion, color, and font.
         
         data.x = -50 + (int)(Math.random()*(width+40));
         data.y = (int)(Math.random()*(height+20));
         
         data.color = Color.getHSBColor( (float)Math.random(), 1.0F, 1.0F );
         
         int fontNum = (int)(Math.random()*fonts.length);  // A random index for
                                                           //  the fonts array.
         data.font = fonts[fontNum];
         
      }
      
   }  // end makeStringData()
   
   
}  // end class RandomStringsWithArray

