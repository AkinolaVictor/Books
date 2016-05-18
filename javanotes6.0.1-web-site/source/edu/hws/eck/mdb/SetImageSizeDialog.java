package edu.hws.eck.mdb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class defines a custom dialog box that asks the user to enter
 * a new size for the Mandelbrot image.  The dialog box has two input
 * boxes where the user can enter integers.
 */
public class SetImageSizeDialog extends JDialog {

   /**
    * This static convenience method will show a dialog of type
    * SetImageSizeDialog.  It will not return until the user dismisses
    * the dialog, by clicking "OK" or "Cancel" or by clicking the
    * dialog's close box.  The user is forced to either enter legal
    * inputs or to cancel the dialog.  Legal input values are integers
    * in the range 10 to 5000.  NOTE:  If you use this method, you
    * don't have to worry about anything else in the class.
    * @param frame The "parent" of the dialog box.  This frame is "blocked"
    *   until the user closed the dialog box.
    * @param oldSize If non-null, then the values of oldSize.width and
    *    oldSize.height are put in the input boxes.  If null, the initial
    *    values in the input boxes are set to 800 and 600.
    * @return null, if the user cancels the dialog or clicks OK without changing
    *    the values in the input boxes; if the user changes the values and clicks
    *    OK, then the return value is a Dimension object whose width
    *    and height give the user's inputs.
    */
   static Dimension showDialog(JFrame frame, Dimension oldSize) {
      SetImageSizeDialog dialog = new SetImageSizeDialog(frame,oldSize);
      dialog.setVisible(true);  // does not return until the dialog box is dismissed
      Dimension newSize = dialog.getInputSize();
      if (newSize == null || newSize.equals(oldSize))
         return null;
      return newSize;
   }
   
   private Dimension inputSize;  // This will be set to the user's input,
                                 // when the user clicks OK and the input
                                 // is legal.

   private JButton cancelButton;
   private JButton okButton;
   private JTextField widthInput;
   private JTextField heightInput;
   
   
   /**
    * This constructor creates the dialog's user interface and sets up
    * listeners to detect clicks on the buttons or on the dialog box's
    * close box.  When the user clicks OK, the input boxes are checked.
    * If the input is legal, the dialog box is closed; if not, an error
    * message is displayed and the dialog stays up.  Note that this constructor
    * does not make the dialog box visible.  Note also that the dialog box
    * is set to DISPOSE itself when it is close and so cannot be reused.
    * (For a reusable dialog, it should be "hidden" rather than disposed.)
    * @param frame the parent of this dialog box, which is blocked until
    *    the dialog box is closed.
    * @param oldSize initial values for the input box
    */
   public SetImageSizeDialog(JFrame frame, Dimension oldSize) {
      super(frame,I18n.tr("imagesizedialog.title"),true); 
              // "The true" makes this a modal dialog that blocks its parent frame.
      JPanel content = new JPanel();
      content.setLayout(new BorderLayout(10,10));
      setContentPane(content);
      content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
      if (oldSize == null)
         oldSize = new Dimension(800,600);
      widthInput = new JTextField(""+oldSize.width, 4);
      heightInput = new JTextField(""+oldSize.height, 4);
      JPanel input = new JPanel();
      input.add(Box.createHorizontalStrut(10));
      input.add(new JLabel(I18n.tr("imagesizedialog.widthequals")));
      input.add(widthInput);
      input.add(Box.createHorizontalStrut(10));
      input.add(new JLabel(I18n.tr("imagesizedialog.widthequals")));
      input.add(heightInput);
      cancelButton = new JButton(I18n.tr("button.cancel"));
      okButton = new JButton(I18n.tr("button.ok"));
      getRootPane().setDefaultButton(okButton);  // This means that the OK button can be invoked by pressing return.
      JPanel buttons = new JPanel();
      buttons.add(cancelButton);
      buttons.add(okButton);
      content.add( new JLabel(I18n.tr("imagesizedialog.question")), BorderLayout.NORTH );  // Message to user.
      content.add( input, BorderLayout.CENTER );
      content.add( buttons, BorderLayout.SOUTH );
      setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Close window when user clicks its close box.
      pack();
      setLocation(frame.getX()+50, frame.getY()+75); // Position near top left of frame.
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      int x = getX();  // The next six lines ensure that the dialog box is actually on the screen!
      int y = getY();
      if (x + getWidth() > screensize.width)
         x = screensize.width - getWidth() - 30;  // Move left to bring dialog onto screen.
      if (y + getHeight() > screensize.height)
         y = screensize.height - getHeight() - 30;  // Move up to bring dialog onto screen.
      setLocation(x,y);
      cancelButton.addActionListener( new ActionListener() {
            // When the user clicks Cancel, dispose the window.
         public void actionPerformed(ActionEvent evt) {
            dispose();
         }
      });
      okButton.addActionListener( new ActionListener() {
            // When the user clicks OK, check the input; close the window only if the input is legal.
         public void actionPerformed(ActionEvent evt) {
            if (checkInput())
               dispose();
         }
      });
   }
   
   /**
    * After the dialog box has closed, this can be called to get
    * the user's response.
    * @return null, if dialog was canceled, or the width/height
    *   entered by the user if not.
    */
   public Dimension getInputSize() {
      return inputSize;
   }
   
   /**
    * This method is called when the user clicks OK.  It gets the
    * user's input and checks whether it is legal
    * @return true if the input is legal, false if not.
    */
   private boolean checkInput() {
      inputSize = null;
      int width, height;
      try {
         width = Integer.parseInt(widthInput.getText());
      }
      catch (NumberFormatException e) {
         JOptionPane.showMessageDialog(this,
               I18n.tr("imagesizedialog.error.widthnotanumber", widthInput.getText()));
         widthInput.selectAll();
         widthInput.requestFocus();
         return false;
      }
      try {
         height = Integer.parseInt(heightInput.getText());
      }
      catch (NumberFormatException e) {
         JOptionPane.showMessageDialog(this,
               I18n.tr("imagesizedialog.error.heightnotanumber", heightInput.getText())); 
         heightInput.selectAll();
         heightInput.requestFocus();
         return false;
      }
      if (width < 10 || width > 5000) {
         JOptionPane.showMessageDialog(this,
               I18n.tr("imagesizedialog.error.badwidth", ""+width)); 
         widthInput.selectAll();
         widthInput.requestFocus();
         return false;
      }
      else if (height < 10 || height > 5000) {
         JOptionPane.showMessageDialog(this,
               I18n.tr("imagesizedialog.error.badheight", ""+width)); 
         heightInput.selectAll();
         heightInput.requestFocus();
         return false;
      }
      inputSize = new Dimension(width,height);
      return true;
   }
   

}
