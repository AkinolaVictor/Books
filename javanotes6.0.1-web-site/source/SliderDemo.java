
// A little applet that demonstrates JSliders.

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class SliderDemo extends JApplet implements ChangeListener {

   JSlider slider1, slider2, slider3;  // The sliders.
   
   JLabel label; // A label for reporting changes in the sliders' values.
   
   public void init() {
   
      getContentPane().setLayout(new GridLayout(4,1));
      
      label = new JLabel("Try dragging the sliders!", JLabel.CENTER);
      getContentPane().add(label);
      
      slider1 = new JSlider(0,10,0);
      slider1.addChangeListener(this);
      getContentPane().add(slider1);
      
      slider2 = new JSlider();
      slider2.addChangeListener(this);
      slider2.setMajorTickSpacing(25);
      slider2.setMinorTickSpacing(5);
      slider2.setPaintTicks(true);
      getContentPane().add(slider2);
      
      slider3 = new JSlider(2000,2100,2006);
      slider3.addChangeListener(this);
      slider3.setLabelTable(slider3.createStandardLabels(50));
      slider3.setPaintLabels(true);
      getContentPane().add(slider3);
      
   }  // end init()
   
   public void stateChanged(ChangeEvent evt) {
      if (evt.getSource() == slider1)
         label.setText("Slider one changed to " + slider1.getValue());
      else if (evt.getSource() == slider2)
         label.setText("Slider two changed to " + slider2.getValue());
      else if (evt.getSource() == slider3)
         label.setText("Slider three changed to " + slider3.getValue());
   }

}
