import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This program demonstrates the "alpha" component of colors by drawing
 * a triangle, a circle, a rectangle, and some text in colors that are
 * partly transparent.  The degrees of transparency of are controlled by
 * the user using four sliders.  The program also demonstrates using
 * FontMetrics by centering the text in the display panel.  This class
 * has a main() routine so it can be run as a stand-alone application.  
 * The nested static class TransparencyDemo.Applet runs the program as 
 * an applet.
 */
public class TransparencyDemo extends JPanel {
   
   /**
    * The main routine simply opens a window that shows a TransparencyDemo panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("TransparencyDemo");
      TransparencyDemo content = new TransparencyDemo();
      window.setContentPane(content);
      window.pack();  
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth())/2,
            (screenSize.height - window.getHeight())/2 );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
   
   /**
    * The public static class TransparencyDemo$Applet represents this program
    * as an applet.  The applet's init() method simply sets the content 
    * pane of the applet to be a TransparencyDemo.  To use the applet on
    * a web page, use code="TransparencyDemo$Applet.class" as the name of
    * the class.  A reasonable size for the applet is 375 by 425.
    */
   public static class Applet extends JApplet {
      public void init() {
         TransparencyDemo content = new TransparencyDemo();
         setContentPane( content );
      }
   }
   
   
   /**
    * The program uses four sliders to control the degree of
    * transparency of the four things that are drawn in the
    * display area.
    */
   private JSlider triangleTransparency;
   private JSlider ovalTransparency;
   private JSlider rectangleTransparency;
   private JSlider textTransparency;
   
   
   /**
    * The font that is used for displaying the message "Transparency
    * Demo" in the display area.
    */
   private Font textFont = new Font("Serif",Font.BOLD,42);
   
   
   /**
    * The display area of the program shows a red triangle, a green circle, a
    * blue rectangle and some black text.  Each figure is drawn with an alpha
    * component that is determined by the value of one of the sliders.
    */
   private JPanel display = new JPanel() {
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         Color triangleColor = new Color(255,0,0,triangleTransparency.getValue());
         Color ovalColor = new Color(0,255,0,ovalTransparency.getValue());
         Color rectangleColor = new Color(0,0,255,rectangleTransparency.getValue());
         Color textColor = new Color(0,0,0,textTransparency.getValue());
         g.setColor(triangleColor);
         g.fillPolygon(new int[] { scaleX(0.25), scaleX(0.7), scaleX(0.1) },
               new int[] { scaleY(0.1), scaleY(0.7), scaleY(0.7) },
               3);
         g.setColor(ovalColor);
         g.fillOval(scaleX(0.3),scaleY(0.45),getWidth()/2,getHeight()/2);
         g.setColor(rectangleColor);
         g.fillRect(scaleX(0.4),scaleY(0.15),getWidth()/2,getHeight()/2);
         FontMetrics metrics = g.getFontMetrics(textFont);
         int lineWidth1 = metrics.stringWidth("Transparency");
         int lineWidth2 = metrics.stringWidth("Demo");
         int textHeight = metrics.getHeight() + metrics.getAscent();
         int topSkip = (getHeight() - textHeight) / 2;
         int leftSkip1 = (getWidth() - lineWidth1) / 2;
         int leftSkip2 = (getWidth() - lineWidth2) / 2;
         g.setColor(textColor);
         g.setFont(textFont);
         g.drawString("Transparency", leftSkip1, topSkip + metrics.getAscent());
         g.drawString("Demo", leftSkip2, topSkip + metrics.getAscent() + metrics.getHeight());
      }
      private int scaleX(double x) {
         return (int)(x * getWidth());
      }
      private int scaleY(double y) {
         return (int)(y * getHeight());
      }
   };

   
   /**
    * The listener that responds when the user changes the value of
    * one of the sliders.  The response is simply to repaint the display.
    */
   private ChangeListener listener = new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
         display.repaint();
      }
   };
   

   /**
    * Constructor sets up the layout of the panel.
    */
   public TransparencyDemo() {
      setLayout(new BorderLayout(3,3));
      setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
      setBackground(Color.GRAY);
      display.setBackground(Color.WHITE);
      display.setPreferredSize(new Dimension(400,300));
      add(display,BorderLayout.CENTER);
      JPanel bottom = new JPanel();
      bottom.setLayout(new GridLayout(4,2,3,3));
      add(bottom,BorderLayout.SOUTH);
      bottom.add( new JLabel("  Triangle Transparency:") );
      triangleTransparency = new JSlider(0,255);
      triangleTransparency.addChangeListener(listener);
      bottom.add( triangleTransparency );
      bottom.add( new JLabel("  Oval Transparency:") );
      ovalTransparency = new JSlider(0,255);
      ovalTransparency.addChangeListener(listener);
      bottom.add( ovalTransparency );
      bottom.add( new JLabel("  Rectangle Transparency:") );
      rectangleTransparency = new JSlider(0,255);
      rectangleTransparency.addChangeListener(listener);
      bottom.add( rectangleTransparency );
      bottom.add( new JLabel("  Text Transparency:") );
      textTransparency = new JSlider(0,255);
      textTransparency.addChangeListener(listener);
      bottom.add( textTransparency );
   }

}
