import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * A custom component that lets the user select a color.
 * The available colors are bright, saturated colors in
 * a full range of hues.  The possible colors are shown
 * in a strip.  The user clicks on this strip to select
 * a color.  The selected color is hilited with a white
 * border.  A program that uses this component can find
 * out which color is currently selected by calling 
 * the getSelectedColor() method.  The preferred size
 * of this component is 108-by-24.   There is a 
 * gray border around the component, two pixels wide
 * on top and bottom and four pixels on left and right.
 */
class RainbowPalette extends JPanel implements MouseListener {

   /* The currently selected color is stored in the variable
      selectedColor.  The hue of this color -- a float value
      in the range 0.0F to 1.0F is stored in selectedHue.
      The value of selectedHue is used to determine where
      to draw the hilite on the palette */

   private float selectedHue = 0;
   private Color selectedColor = Color.getHSBColor(0,1,1);

   /**
    * Constructor.  Set the component to listen for mouse clicks
    * on itself, and set the preferred size.  The gray background
    * color will show around the edges of the colored palette.
    */
   RainbowPalette() {
      addMouseListener(this);
      setPreferredSize( new Dimension(262, 24) );
      setBackground(Color.gray);
   }
   
   /**
    * Return the color that is currently selected in the palette.
    */
   public Color getSelectedColor() {
      return selectedColor;
   }

   /**
    * Draw the palette, and add a white rectangle to hilite the selected color.
    */
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      int width = getWidth();
      int height = getHeight();
      for (int i = 0; i < width - 8; i++) {
         float hue = (float)i / (width-8);
         g.setColor( Color.getHSBColor(hue, 1, 1) );
         g.drawLine(i+4,4,i+4,height-5);
      }
      int x = 4 + (int)(selectedHue*(width-8));  // x-coord of selected color.
      g.setColor(Color.white);
      g.drawRect(x-2,3,2,height-7);  // Draw the hilite.
      g.drawRect(x-3,2,4,height-5);
   }

   /**
    * When the user clicks on the component, select the
    * color that the user clicked.  But make sure that
    * the selectedHue is in the legal range, 0 to 1.
    */
   public void mousePressed(MouseEvent evt) {
       int x = evt.getX();
       selectedHue = (float)x / (getSize().width - 4);
       if (selectedHue < 0)
          selectedHue = 0;
       else if (selectedHue > 1)
          selectedHue = 1;
       selectedColor = Color.getHSBColor(selectedHue, 1, 1);
       repaint();
   }
   
   public void mouseReleased(MouseEvent evt) { }
   public void mouseClicked(MouseEvent evt) { }
   public void mouseEntered(MouseEvent evt) { }
   public void mouseExited(MouseEvent evt) { }

}  // end class RainbowPalette


