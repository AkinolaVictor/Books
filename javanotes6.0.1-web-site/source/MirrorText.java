import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;


/**
 * A component for displaying a mirror-reversed line of text.
 * The text will be centered in the available space.  This component
 * is defined as a subclass of JPanel.  It respects any background 
 * color, foreground color, and font that are set for the JPanel.
 * The setText(String) method can be used to change the displayed
 * text.  Changing the text will also call revalidate() on this
 * component.
 */
public class MirrorText extends JPanel {

   private String text; // The text displayed by this component.
                        // The value is non-null.  If a null value is
                        // specified, it is changed to an empty string.
   
   private BufferedImage OSC; // Holds an un-reversed picture of the text.

   /**
    * Construct a MirrorText component that will display the specified
    * text in mirror-reversed form.
    */
   public MirrorText(String text) {
      if (text == null)
         text = "";
      this.text = text;
   }

   
   /**
    * Change the text that is displayed on the label.
    * @param text the new text to display
    */
   public void setText(String text) {
      if (text == null)
         text = "";
      if ( ! text.equals(this.text) ) {
         this.text = text;  // Change the instance variable.
          revalidate();      // Tell container to recompute its layout.
          repaint();         // Make sure component is redrawn.
      }
   }

   
   /**
    * Return the text that is displayed on this component.
    * The return value is non-null but can be an empty string.
    */
   public String getText() {
      return text;
   }


   /**
    * The paintComponent method makes a new off-screen canvas, if necessary, writes
    * the text to the off-screen canvas, then copies the canvas onto the screen
    * in mirror-reversed form.
    */
   public void paintComponent(Graphics g) {
      int width = getWidth();
      int height = getHeight();
      if (OSC == null || width != OSC.getWidth() 
                          || height != OSC.getHeight()) {
         OSC = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
      }
      Graphics OSG = OSC.getGraphics();
      OSG.setColor(getBackground());
      OSG.fillRect(0, 0, width, height);
      OSG.setColor(getForeground()); 
      OSG.setFont(getFont());
      FontMetrics fm = OSG.getFontMetrics(getFont());
      int x = (width - fm.stringWidth(text)) / 2;
      int y = (height + fm.getAscent() - fm.getDescent()) / 2;
      OSG.drawString(text, x, y);
      OSG.dispose();
      g.drawImage(OSC, width, 0, 0, height, 0, 0, width, height, null);
   }

   
   /**
    * Compute a preferred size that includes the size of the text, plus
    * a boundary of 5 pixels on each edge.
    */
   public Dimension getPreferredSize() {
      FontMetrics fm = getFontMetrics(getFont());
      return new Dimension(fm.stringWidth(text) + 10, 
            fm.getAscent() + fm.getDescent() + 10);
   }

}  // end MirrorText
