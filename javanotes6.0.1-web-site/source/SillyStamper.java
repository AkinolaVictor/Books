import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * An SillyStamper panel contains a JList of icons, a drawing area where 
 * the user can "stamp" images of the icons, and a few control buttons.
 * The user clicks an icon in the list to select it, then clicks on the drawing
 * area to place copies of the selected image.  This class can be run as a
 * main program, and it contains a static nested class that can be used to run
 * the program as an applet.
 * 
 * This program requires the icon image files from the stamper_icons directory.
 * (The images were taken from a KDE desktop icon collection.)
 */
public class SillyStamper extends JPanel {

   /**
    * The main routine simply opens a window that shows a SillyStamper panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Silly Stamper");
      SillyStamper content = new SillyStamper();
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
    * The public static class SillyStamper.Applet represents this program
    * as an applet.  The applet's init() method simply sets the content 
    * pane of the applet to be a SillyStamper.  To use the applet on
    * a web page, use code="SillyStamper$Applet.class" as the name of
    * the class. A reasonable size for the applet is 350 by 275.
    */
   public static class Applet extends JApplet {
      public void init() {
         SillyStamper content = new SillyStamper();
         setContentPane( content );
      }
   }
   
   
   /**
    * An object of type IconInfo stores the information needed to draw
    * one icon image on the display area.
    */
   private static class IconInfo {
      int iconNumber;  // an index into the iconImages array.
      int x, y;        // coords of the upper left corner of the image
   }

   /**
    * Contains info for all the icons that have been placed on the
    * display area.  Might contain more than have actually been shown,
    * because of the Undo command.  An icon that is removed from the
    * display area by an undo is not removed from this list.
    */
   private ArrayList<IconInfo> icons = new ArrayList<IconInfo>();

   private int iconsShown;  // Number of icons shown in the display area.
   private int iconsPlaced; // Number of icons that have been placed.  Can be
                            //  greater than iconsShown, because of undo/redo.
   
   private JList iconList;  // The JList from which the user selects the icon for stamping.

   private JButton undoButton;  // A button for removing the most recently added image.
   private JButton redoButton;  // A button for restoring the most recently removed image.

   private IconDisplayPanel displayPanel;  // The display panel.  The IconsDisplayPanel class is
                                            // a nested class, and is defined below.

   private Image[] iconImages;  // The little images that can be "stamped".




   /**
    * This class represents the drawing area where the user can stamp images.
    */
   private class IconDisplayPanel extends JPanel implements MouseListener {

      /**
       * Draws the display panel, based on the data about what icons are
       * to be displayed there and what their coordinates are.
       */
      public void paintComponent(Graphics g) {
         super.paintComponent(g); 
         if (iconImages == null) {
            g.drawString("Can't load icons.", 10, 30);
            return;
         }
         for (int i = 0; i < iconsShown; i++) {
            IconInfo info = icons.get(i);
            g.drawImage(iconImages[info.iconNumber], info.x, info.y, this);
         }
      }

      /**
       * When the user clicks the display panel, place a copy of the currently selected
       * icon image at the point where the user clicked.
       */
      public void mousePressed(MouseEvent e) { 
         IconInfo info  = new IconInfo();
         info.iconNumber = iconList.getSelectedIndex();
         info.x = e.getX() - 16;  // Offset x-coord, so center of icon is at the point that was clicked.
         info.y = e.getY() - 16;  // Offset y-coord too.
         if (iconsShown == icons.size())
            icons.add(info);
         else
            icons.set(iconsShown, info);
         iconsShown++;
         iconsPlaced = iconsShown;
         redoButton.setEnabled(false);
         undoButton.setEnabled(true);
         repaint();  // Tell system to redraw the image, with the new data
      }

      public void mouseClicked(MouseEvent e) { }   // Not used, but required by MouseListener interface.
      public void mouseEntered(MouseEvent e) { }
      public void mouseExited(MouseEvent e) { }
      public void mouseReleased(MouseEvent e) { }

   } // end nested class IconDisplayPanel


   /**
    * The constructor sets up a BorderLayout on the panel with a display panel
    * in the CENTER position, a list of icon images in the EAST position, and 
    * a JPanel in the SOUTH position that contains two control buttons.
    */
   public SillyStamper() {

      setLayout(new BorderLayout(2,2));   // Set basic properties of this panel.
      setBackground(Color.GRAY);
      setBorder(BorderFactory.createLineBorder(Color.GRAY,2));

      displayPanel = new IconDisplayPanel();   // Create and configure the display panel
      displayPanel.setPreferredSize(new Dimension(400,300));
      displayPanel.setBackground( new Color(220,220,255) );  // Very light blue.
      displayPanel.addMouseListener(displayPanel);
      add(displayPanel,BorderLayout.CENTER);  

      iconList = createIconList();  // Create the scrolling list of icons.
      if (iconList == null)
         return;
      add( new JScrollPane(iconList), BorderLayout.EAST );  

      Action undoAction = new AbstractAction("Undo") {
         public void actionPerformed(ActionEvent evt) {
            if (iconsShown > 0) {
               iconsShown--;
               redoButton.setEnabled(true);
               displayPanel.repaint();
            }
         }
      };

      Action redoAction = new AbstractAction("Redo") {
         public void actionPerformed(ActionEvent evt) {
            if (iconsShown < iconsPlaced) {
               iconsShown++;
               if (iconsShown == iconsPlaced)
                  redoButton.setEnabled(false);
               undoButton.setEnabled(true);
               displayPanel.repaint();
            }
         }
      };

      undoButton = new JButton(undoAction);
      redoButton = new JButton(redoAction);
      undoButton.setEnabled(false);
      redoButton.setEnabled(false);

      JPanel buttonPanel = new JPanel();  
      buttonPanel.add(undoButton);
      buttonPanel.add(redoButton);
      add(buttonPanel, BorderLayout.SOUTH);  

   }



   /**
    * Create a JList that contains all of the available icon images.
    */
   private JList createIconList() {
      String[] iconNames = new String[] {
         "icon5.png", "icon7.png", "icon8.png", "icon9.png", "icon10.png", "icon11.png", 
         "icon24.png", "icon25.png", "icon26.png", "icon31.png", "icon33.png", "icon34.png"

      };

      iconImages = new Image[iconNames.length];

      ClassLoader classLoader = getClass().getClassLoader();
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      try {
         for (int i = 0; i < iconNames.length; i++) {
            URL imageURL = classLoader.getResource("stamper_icons/" + iconNames[i]);
            if (imageURL == null)
               throw new Exception();
            iconImages[i] = toolkit.createImage(imageURL);
         }
      }
      catch (Exception e) {
         iconImages = null;
         return null;
      }

         // Create an array of objects of type ImageIcon.  This is required for
         // creating the JList.  It is easy to create a JList from an array
         // of ImageIcons -- just pass the array as a parameter to the constructor.
         // (You could do the same thing with an array of Strings, to get a list
         // of strings.  But JLists can't use other types so easily.)

      ImageIcon[] icons = new ImageIcon[iconImages.length];
      for (int i = 0; i < iconImages.length; i++)
         icons[i] = new ImageIcon(iconImages[i]);
      
      JList list = new JList(icons); // Makes a list containing the image icons from the array.
      
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          // (Note:  With the default selection mode, it would be possible for the user
          // to select several list items at the same time or to select no item at all.)
      
      list.setSelectedIndex(0);  // The first item in the list is currently selected.
      
      return list;
   }



} // end class SillyStamper

