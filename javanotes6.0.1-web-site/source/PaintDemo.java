import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


/**
 * This program demonstrates GradientPaint and TexturePaint.
 * This class has a main() routine and so can be run as an application.
 * The static nested class PaintDemo.Applet runs the program as an applet.
 */
public class PaintDemo extends JPanel {
   
   /**
    * The main routine simply opens a window that shows a PaintDemo panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("PaintDemo - Drag the Vertices");
      PaintDemo content = new PaintDemo();
      window.setContentPane(content);
      window.pack();  
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth())/2,
            (screenSize.height - window.getHeight())/2 );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
   
   /**
    * The public static class PaintDemo$Applet represents this program
    * as an applet.  The applet's init() method simply sets the content 
    * pane of the applet to be a PaintDemo.  To use the applet on
    * a web page, use code="PaintDemo$Applet.class" as the name of
    * the class.  A reasonable size for the applet is 400 by 450.
    */
   public static class Applet extends JApplet {
      public void init() {
         PaintDemo content = new PaintDemo();
         setContentPane( content );
      }
   }
   
   
   /**
    * The display area of the program shows a filled polygon that can be filled
    * with various kinds of paint.  The vertices of the polygon can be dragged
    * by the user.
    */
   private class Display extends JPanel implements MouseListener, MouseMotionListener {
      int[] xcoord, ycoord;
      int draggedPoint = -1;
      Display() {
         setBackground(Color.WHITE);
         setPreferredSize(new Dimension(400,300));
         addMouseListener(this);
         addMouseMotionListener(this);
      }
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g;
         if (xcoord == null) {
            xcoord = new int[] { scaleX(0.2), scaleX(0.8), scaleX(0.5),
                               scaleX(0.95), scaleX(0.35), scaleX(0.1) };
            ycoord = new int[] { scaleY(0.15), scaleY(0.1), scaleY(0.5),
                               scaleY(0.45), scaleY(0.9), scaleY(0.7) };
         }
         g2.setPaint(paint);
         g2.fillPolygon(xcoord, ycoord, 6);
         g2.setColor(Color.BLACK);
         g2.setStroke(new BasicStroke(2));
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                   RenderingHints.VALUE_ANTIALIAS_ON);
         g2.drawPolygon(xcoord, ycoord, 6);
         for (int i = 0; i < 6; i++)
            g2.fillRect(xcoord[i] - 3, ycoord[i] - 3, 7, 7);
      }
      private int scaleX(double x) {
         return (int)(x * getWidth());
      }
      private int scaleY(double y) {
         return (int)(y * getHeight());
      }
      public void mousePressed(MouseEvent e) {
         draggedPoint = -1;
         for (int i = 0; i < 6; i++) {
            if (Math.abs(xcoord[i] - e.getX()) < 4 && Math.abs(ycoord[i] - e.getY()) < 4) {
               draggedPoint = i;
               break;
            }
         }
      }
      public void mouseDragged(MouseEvent e) {
         if (draggedPoint < 0)
            return;
         int x = Math.max(0, Math.min(e.getX(),getWidth()));
         int y = Math.max(0, Math.min(e.getY(),getHeight()));
         xcoord[draggedPoint] = x;
         ycoord[draggedPoint] = y;
         repaint();
      }
      public void mouseReleased(MouseEvent e) { }
      public void mouseMoved(MouseEvent e) { }
      public void mouseClicked(MouseEvent e) { }
      public void mouseEntered(MouseEvent e) { }
      public void mouseExited(MouseEvent e) { }
   }
   

   /**
    * Responds when a use clicks on the radio button to set up the labels and sliders
    * to correspond to the kind of paint that has been selected.  Calls setPaint()
    * to make the display use the new selected paint.
    */
   private ActionListener buttonlistener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         currentButton = (JRadioButton)e.getSource();
         slider1.removeChangeListener(sliderlistener);  //(Yuck! Had to do this to avoid notifying
         slider2.removeChangeListener(sliderlistener);  // slider listeners when changes are made.)
         if (currentButton == gradientButton1 || currentButton == gradientButton2) {
            label1.setText("  Gradient Angle:");
            label2.setText("  Gradient Width:");
            slider1.setMinimum(0);
            slider1.setMaximum(360);
            slider1.setValue(gradientAngle);
            slider2.setMinimum(10);
            slider2.setMaximum(300);
            slider2.setValue(gradientWidth);
         }
         else {
            label1.setText("  Texture Offset:");
            label2.setText("  Texture Scale:");
            slider1.setMinimum(0);
            slider1.setMaximum(100);
            slider1.setValue(textureOffset);
            slider2.setMinimum(25);
            slider2.setMaximum(200);
            slider2.setValue(textureScale);
         }
         slider1.addChangeListener(sliderlistener);
         slider2.addChangeListener(sliderlistener);
         setPaint();
      }
   };
   
   
   /**
    * When the user changes the value on one of the sliders, this 
    * ChangeListener responds by changing the Paint to reflect the
    * changed value.
    */
   private ChangeListener sliderlistener = new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
         setPaint();
      }
   };
   
   
   /**
    * Called when the type of paint or the values on the sliders are changed.
    * Creates the new Paint and redraws the display to show the change.
    */
   private void setPaint() {
      if (currentButton == gradientButton1 || currentButton == gradientButton2) {
         gradientAngle = slider1.getValue();
         gradientWidth = slider2.getValue();
         int x = getWidth()/2;
         int y = getHeight()/2;
         int dx = (int)( gradientWidth * Math.cos(gradientAngle/180.0 * Math.PI) );
         int dy = (int)( gradientWidth * Math.sin(gradientAngle/180.0 * Math.PI) );
         if (currentButton == gradientButton1)
            paint = new GradientPaint(x,y,Color.LIGHT_GRAY,x+dx,y+dy,Color.BLACK,true);
         else
            paint = new GradientPaint(x,y,Color.RED,x+dx,y+dy,Color.YELLOW,true);
      }
      else {
         textureOffset = slider1.getValue();
         textureScale = slider2.getValue();
         BufferedImage texture;
         if (currentButton == textureButton1)
            texture = smiley;
         else
            texture = queen;
         int width = texture.getWidth() * textureScale / 100;
         int height = texture.getHeight() * textureScale / 100;
         int offsetX = width * textureOffset / 100;
         int offsetY = height * textureOffset / 100;
         Rectangle2D anchor = new Rectangle2D.Double(offsetX,offsetY,width,height);
         paint = new TexturePaint(texture,anchor);
      }
      display.repaint();
   }
   
   
   private Display display = new Display();  // The display area where the polygon is drawn.
   
   private Paint paint;  // The paint that is used to fill the polygon in the display.
   
   private BufferedImage smiley, queen;  // Images for texture paint.

   private JRadioButton gradientButton1, gradientButton2;  // Select the type of paint.
   private JRadioButton textureButton1, textureButton2;

   private JRadioButton currentButton;   // The currently selected radio button.
   
   private int gradientAngle = 45, gradientWidth = 50;  // Settings that affect the paint.
   private int textureOffset = 0, textureScale = 100;
   
   private JSlider slider1, slider2;  // Sliders that control the settings;  Which 
                                      // setting is affect depends on current paint type.
   
   private JLabel label1 = new JLabel("  Gradient Angle:");  // Labels change, depending
   private JLabel label2 = new JLabel("  Gradient Width:");  //   on type of paint.
   
   
   /**
    * Constructor.
    */
   public PaintDemo() {
      setLayout(new BorderLayout(3,3));
      setBorder(BorderFactory.createLineBorder(Color.GRAY,3));
      setBackground(Color.GRAY);
      add(display, BorderLayout.CENTER);
      JPanel bottom = new JPanel();
      bottom.setLayout(new GridLayout(0,2,5,5));
      add(bottom, BorderLayout.SOUTH);
      slider1 = new JSlider(0,360,gradientAngle);
      slider1.addChangeListener(sliderlistener);
      slider2 = new JSlider(10,300,gradientWidth);
      slider2.addChangeListener(sliderlistener);
      bottom.add(label1);
      bottom.add(slider1);
      bottom.add(label2);
      bottom.add(slider2);
      ButtonGroup group = new ButtonGroup();
      gradientButton1 = new JRadioButton("Black/Gray Gradient");
      gradientButton1.addActionListener(buttonlistener);
      bottom.add(gradientButton1);
      group.add(gradientButton1);
      gradientButton1.setSelected(true);
      currentButton = gradientButton1;
      gradientButton2 = new JRadioButton("Red/Yellow Gradient");
      gradientButton2.addActionListener(buttonlistener);
      bottom.add(gradientButton2);
      group.add(gradientButton2);
      setPaint();
      try {
         ClassLoader cl = PaintDemo.class.getClassLoader();
         URL imageURL = cl.getResource("TinySmiley.png");
         if (imageURL != null)
            smiley = ImageIO.read(imageURL);
         imageURL = cl.getResource("QueenOfHearts.png");
            queen = ImageIO.read(imageURL);
      }
      catch (Exception e) {
         return;  // Can't load the images, so don't add the texture radio buttons.
      }
      textureButton1 = new JRadioButton("Smiley Face");
      textureButton1.addActionListener(buttonlistener);
      bottom.add(textureButton1);
      group.add(textureButton1);
      textureButton2 = new JRadioButton("Queen Of Hearts");
      textureButton2.addActionListener(buttonlistener);
      bottom.add(textureButton2);
      group.add(textureButton2);
   }

}
