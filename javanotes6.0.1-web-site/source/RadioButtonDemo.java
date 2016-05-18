import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A small program that demonstrates JRadionButton with a group
 * of radio buttons that control the background color of a label.
 * This class includes a main() routine so it can be run as and
 * application and a nested subclass of JApplet that can be used
 * to run it as an applet.
 */
public class RadioButtonDemo extends JPanel implements ActionListener {

   /**
    * The main routine simply opens a window that shows a RadioButtonDemo panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("RadioButtonDemo");
      RadioButtonDemo content = new RadioButtonDemo();
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
    * The public static class RadioButtonDemo$Applet represents this program
    * as an applet.  The applet's init() method simply sets the content 
    * pane of the applet to be a RadioButtonDemo.  To use the applet on
    * a web page, use code="RadioButtonDemo$Applet.class" as the name of
    * the class.  A reasonable size for the applet is 202 by 202.
    */
   public static class Applet extends JApplet {
      public void init() {
         RadioButtonDemo content = new RadioButtonDemo();
         setContentPane( content );
      }
   }

   
   private JRadioButton redRadio, blueRadio, greenRadio, blackRadio;  // the buttons

   private JLabel label;  // Will show the background color specified by
                          // the selected radio button.

   public RadioButtonDemo() {

      setLayout( new GridLayout(5,1) );
      setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
      setPreferredSize(new Dimension(202,202));

      ButtonGroup colorGroup = new ButtonGroup();
         // A ButtonGroup is needed to synchronize the radio
         // buttons so that no more than one of them will
         // be selected at any given time.  Each button will be
         // added to this group.

      redRadio = new JRadioButton("Red");
      colorGroup.add(redRadio);
      redRadio.addActionListener(this);
      add(redRadio);

      blueRadio = new JRadioButton("Blue");
      colorGroup.add(blueRadio);
      blueRadio.addActionListener(this);
      add(blueRadio);

      greenRadio = new JRadioButton("Green");
      colorGroup.add(greenRadio);
      greenRadio.addActionListener(this);
      add(greenRadio);

      blackRadio = new JRadioButton("Black");
      colorGroup.add(blackRadio);
      blackRadio.addActionListener(this);
      add(blackRadio);

      redRadio.setSelected(true);  // Set an initial selection.

      label = new JLabel("Red is selected", JLabel.CENTER);
      label.setForeground(Color.white);
      label.setBackground(Color.red);
      label.setOpaque(true);
      add(label);

   } // end init()

   
   /**
    * Respond when the user clicks one of the radio button by
    * chaning the text and background color of the label.
    */
   public void actionPerformed(ActionEvent evt) {
      if ( redRadio.isSelected() ) {
         label.setBackground(Color.red);
         label.setText("Red is selected");
      }
      else if ( blueRadio.isSelected() ) {
         label.setBackground(Color.blue);
         label.setText("Blue is selected");
      }
      else if ( greenRadio.isSelected() ) {
         label.setBackground(Color.green);
         label.setText("Green is selected");
      }
      else if ( blackRadio.isSelected() ) {
         label.setBackground(Color.black);
         label.setText("Black is selected");
      }
   } // end actionPerformed()
   

} // end class RadioButtonDemo
