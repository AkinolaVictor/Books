
/**
 *  A ColorChooserApplet shows six slider bars that the user can manipulate
 *  to set the red, green, blue, hue, brightness, and saturation components
 *  of a color.  A color patch shows the selected color, and there are
 *  six labels that show the numerical values of all the components.
 *  RGB components are specified as integers in the range 0 to 255.
 *  HSB components are specified as float values in the range 0.0F to 1.0F.
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class ColorChooserApplet extends JApplet implements ChangeListener {

   private float[] hsb = new float[3];   // For holding HSB color components.
   
   private int r = 255, g = 0, b = 0;      // The RGB color components.
   
   private JSlider hueSlider, brightnessSlider, saturationSlider,  // Slider bars.
                     redSlider, greenSlider, blueSlider;
   
   private JLabel hueLabel, brightnessLabel, saturationLabel,  // Display component values.
                 redLabel, greenLabel, blueLabel;
                 
   private Canvas colorCanvas;  // Color patch for displaying the color.
                 
   /**
    * Sets up the applet content, layout, and event listening.
    */
   public void init() {
   
       Color.RGBtoHSB(r,g,b,hsb);  // Get HSB equivalent of RGB color
       
       /* Create JSliders with possible values from 0 to 255. */
       
       hueSlider = new JSlider(0,255,(int)(255*hsb[0]));
       saturationSlider = new JSlider(0,255,(int)(255*hsb[1]));
       brightnessSlider = new JSlider(0,255,(int)(255*hsb[2]));
       redSlider = new JSlider(0,255,255);
       greenSlider = new JSlider(0,255,0);
       blueSlider = new JSlider(0,255,0);
       
       /* Create Labels showing current RGB and HSB values. */
       
       hueLabel = new JLabel(String.format(" Hue = %1.5f", hsb[0]));
       saturationLabel = new JLabel(String.format(" Saturation = %1.5f", hsb[1]));
       brightnessLabel = new JLabel(String.format(" Brightness = %1.5f", hsb[2]));
       redLabel = new JLabel(" Red = " + r);
       greenLabel = new JLabel(" Green = " + g);
       blueLabel = new JLabel(" Blue = " + b);
       
       /* Set background colors for JLabels, and make them opaque so they don't
          inherit the gray background of the applet. */
       
       hueLabel.setBackground(Color.WHITE);
       saturationLabel.setBackground(Color.WHITE);
       brightnessLabel.setBackground(Color.WHITE);
       redLabel.setBackground(Color.WHITE);
       greenLabel.setBackground(Color.WHITE);
       blueLabel.setBackground(Color.WHITE);
       hueLabel.setOpaque(true);
       saturationLabel.setOpaque(true);
       brightnessLabel.setOpaque(true);
       redLabel.setOpaque(true);
       greenLabel.setOpaque(true);
       blueLabel.setOpaque(true);
       
       /* Set the applet to listen for changes to the slicer' values */
       
       hueSlider.addChangeListener(this);
       saturationSlider.addChangeListener(this);
       brightnessSlider.addChangeListener(this);
       redSlider.addChangeListener(this);
       greenSlider.addChangeListener(this);
       blueSlider.addChangeListener(this);
       
       /* Create a canvas whose background color will always be set to the
          currently selected color. */
       
       colorCanvas = new Canvas();
       colorCanvas.setBackground(Color.RED);
       
       /* Create the content pane, which contains of a row of
          three equal-sized regions holding the sliders,
          the Labels, and the color patch.  The background color
          of the applet is gray, which will show around the edges
          and between components. */
       
       JPanel content = new JPanel();
       setContentPane(content);
       
       content.setLayout(new GridLayout(1,3,3,3));
       content.setBackground(Color.DARK_GRAY);
       content.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
       JPanel sliders = new JPanel();
       JPanel labels = new JPanel();
       content.add(sliders);
       content.add(labels);
       content.add(colorCanvas);
       
       /* Add the Sliders and the Labels to their respective panels. */
       
       sliders.setLayout(new GridLayout(6,1,2,2));
       sliders.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
       sliders.add(redSlider);
       sliders.add(greenSlider);
       sliders.add(blueSlider);
       sliders.add(hueSlider);
       sliders.add(saturationSlider);
       sliders.add(brightnessSlider);
       
       labels.setLayout(new GridLayout(6,1,2,2));
       labels.setBackground(Color.DARK_GRAY);
       labels.add(redLabel);
       labels.add(greenLabel);
       labels.add(blueLabel);
       labels.add(hueLabel);
       labels.add(saturationLabel);
       labels.add(brightnessLabel);
       
   } // end init();
   
   
   /**
    * This is called when the user has changed the values on
    * one of the sliders.  All the sliders and labels
    * and the color patch are reset to correspond to the new color.
    */
   public void stateChanged(ChangeEvent evt) {
      JSlider source = (JSlider)evt.getSource();
      if ( ! source.getValueIsAdjusting() ) {
            // Ignore change events that are not produced by the user
            // adjusting the slider; such events are generated when the
            // change is made programmatically.
         return;
      }
       int r1, g1, b1;
       r1 = redSlider.getValue();
       g1 = greenSlider.getValue();
       b1 = blueSlider.getValue();
       if (r != r1 || g != g1 || b != b1) {  // One of the RGB components has changed.
          r = r1;
          g = g1;
          b = b1;
          Color.RGBtoHSB(r,g,b,hsb);
          hueSlider.setValue((int)(255*hsb[0]));
          saturationSlider.setValue((int)(255*hsb[1]));
          brightnessSlider.setValue((int)(255*hsb[2]));
       }
       else {  // One of the HSB components has changed.
           hsb[0] = hueSlider.getValue()/255.0F;
           hsb[1] = saturationSlider.getValue()/255.0F;
           hsb[2] = brightnessSlider.getValue()/255.0F;
           int rgb = Color.HSBtoRGB(hsb[0],hsb[1],hsb[2]);
           r = (rgb >> 16) & 0xFF;
           g = (rgb >> 8) & 0xFF;
           b = rgb & 0xFF;
           redSlider.setValue(r);
           greenSlider.setValue(g);
           blueSlider.setValue(b);
       }
       redLabel.setText(" Red = " + r);
       greenLabel.setText(" Green = " + g);
       blueLabel.setText(" Blue = " + b);
       hueLabel.setText(String.format(" Hue = %1.5f", hsb[0]));
       saturationLabel.setText(String.format(" Saturation = %1.5f", hsb[1]));
       brightnessLabel.setText(String.format(" Brightness = %1.5f", hsb[2]));
       colorCanvas.setBackground(new Color(r,g,b));
       colorCanvas.repaint();  // Tell the system to redraw the canvas in its new color.
   } // end stateChanged

   

}  // end class ColorChooserApplet
