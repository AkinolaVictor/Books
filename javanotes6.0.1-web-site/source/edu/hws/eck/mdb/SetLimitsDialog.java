package edu.hws.eck.mdb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class represents a dialog box where the user can enter new values
 * for xmin, xmax, ymin, and ymax.  These values specify the ranges of
 * x and y values that are shown in the Mandelbrot image.
 */
public class SetLimitsDialog extends JDialog {
   
   /**
    * This static convenience method shows a dialog of type SetLimitsDialog,
    * waits for the user to input a response or to dismiss the dialog, and
    * returns the user's response (or null if the user cancels).  Note that
    * if you use this method, then you don't have to do anything else with
    * this class.
    * @param frame The parent of this dialog, which is blocked while the
    *   dialog is on screen.
    * @param oldLimitStrings an array of 4 strings that are used as the
    *   initial content of the input boxes for xmin, xmax, ymin, ymax (in 
    *   that order). (Note that I pass strings rather than doubles because
    *   I had nicely formatted strings available in the class that calls this
    *   method.)
    * @return null, if the user cancels, or an array of four doubles, representing
    *   the inputs for xmin, xmax, ymin, and ymax.  It is guaranteed that
    *   xmin is strictly less than xmax and ymin is strictly less than ymax.
    *   (Actually, the return value will also be null when the user clicks OK
    *   without ever editing the initial values in the input boxes.  Only
    *   CHANGED values are returned.)
    */
   static double[] showDialog(JFrame frame, String[] oldLimitStrings) {
      SetLimitsDialog dialog = new SetLimitsDialog(frame,oldLimitStrings);
      dialog.setVisible(true);
      return dialog.getInputsIfChanged();
   }
   
   private double[] inputValues;  // Will contain the user's input after user clicks OK.
   boolean changed;  // Will be set to true if the user actually edited the input boxes.

   private JButton cancelButton;
   private JButton okButton;
   private JTextField[] inputBoxes;
   private String[] oldLimitStrings;
   
   private static final String[] names = { "limitsdialog.xmin", "limitsdialog.xmax", 
                                          "limitsdialog.ymin", "limitsdialog.ymax" };
   
   /**
    * Constructor builds the dialog box and adds listeners to the buttons.  This
    * does not make the dialog visible on the screen.  Note that the dialog is
    * disposed when it is closed, so it cannot be reused.  (For a reusable dialog,
    * it should be "hidden" rather than disposed.)
    * @param frame The parent of the dialog box, which is blocked while the dialog
    *   is on the screen.
    * @param oldLimitStrings Initial content of input boxes.  Must be an array of length four.
    */
   public SetLimitsDialog(JFrame frame, String[] oldLimitStrings) {
      super(frame,I18n.tr("limitsdialog.title"),true);  // "true" for a modal dialog.
      this.oldLimitStrings = oldLimitStrings;
      JPanel content = new JPanel();
      content.setLayout(new BorderLayout(10,10));
      setContentPane(content);
      content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
      JPanel input = new JPanel();
      input.setLayout(new GridLayout(4,2,5,5));
      inputBoxes = new JTextField[4];
      for (int i = 0; i < 4; i++) {
         inputBoxes[i] = new JTextField(oldLimitStrings[i]);
         input.add(new JLabel(I18n.tr(names[i])+":"));
         input.add(inputBoxes[i]);
      }
      cancelButton = new JButton(I18n.tr("button.cancel"));
      okButton = new JButton(I18n.tr("button.ok"));
      getRootPane().setDefaultButton(okButton);  // This means that the OK button can be invoked by pressing return.
      JPanel buttons = new JPanel();
      buttons.add(cancelButton);
      buttons.add(okButton);
      content.add( new JLabel(I18n.tr("limitsdialog.question")), BorderLayout.NORTH );
      content.add( input, BorderLayout.CENTER );
      content.add( buttons, BorderLayout.SOUTH );
      setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      pack();
      setLocation(frame.getX()+50, frame.getY()+75);  // Position near top-left corner of parent frame.
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      int x = getX();   // The next six lines ensure that the dialog is actually visible on the screen.
      int y = getY();
      if (x + getWidth() > screensize.width)
         x = screensize.width - getWidth() - 30;
      if (y + getHeight() > screensize.height)
         y = screensize.height - getHeight() - 30;
      setLocation(x,y);
      cancelButton.addActionListener( new ActionListener() {
             // Closes the dialog when the user clicks the cancel button.
         public void actionPerformed(ActionEvent evt) {
            dispose();
         }
      });
      okButton.addActionListener( new ActionListener() {
             // When the user clicks OK, close the dialog only if the input is legal.
         public void actionPerformed(ActionEvent evt) {
            if (checkInput())
               dispose();
         }
      });
   }
   
   /**
    * This is called when the user clicks the OK button, to get the user's responses
    * and make sure they are legal.  If not, the user is informed of the error and
    * the dialog will stay on the screen.
    * @return true if the input is legal.
    */
   private boolean checkInput() {
      changed = false;
      inputValues = null;
      String[] inputStrings = new String[4];
      for (int i = 0; i < 4; i++) {
         inputStrings[i] = inputBoxes[i].getText();
         if (!inputStrings[i].equals(oldLimitStrings[i]))
            changed = true;  // At least one of the input strings has been modified.
      }
      double[] values = new double[4];
      for (int i = 0; i < 4; i++) {
         try {
            values[i] = Double.parseDouble(inputStrings[i]);
         }
         catch (NumberFormatException e) {
            JOptionPane.showMessageDialog( this,
                  I18n.tr( "limitsdialog.error.NAN", inputStrings[i], I18n.tr(names[i]) ) );
            inputBoxes[i].selectAll();
            inputBoxes[i].requestFocus();
            return false;
         }
      }
      if (values[1] <= values[0]) {
         JOptionPane.showMessageDialog(this,
               I18n.tr("limitsdialog.error.xValuesOutOfOrder" ));
         inputBoxes[1].selectAll();
         inputBoxes[1].requestFocus();
         return false;
      }
      if (values[3] <= values[2]) {
         JOptionPane.showMessageDialog(this,
               I18n.tr("limitsdialog.error.yValuesOutOfOrder" ));
         inputBoxes[3].selectAll();
         inputBoxes[3].requestFocus();
         return false;
      }
      inputValues = values;
      return true;
   }
   
   /**
    * Can be called after the dialog box is closed to get the user's inputs.
    * @return an array containing the four values from the input box, if the 
    *   user dismissed the dialog box with the OK button, or null if the user
    *   canceled.
    */
   public double[] getInputs() {
      return inputValues;
   }

   /**
    * Can be called after the dialog box is closed to get the user's inputs.
    * (This is provided because the double values that are returned might not be
    * exactly the same as the original double values, even if the user has
    * not edited the values at all.  This is true because of round-off error
    * when doubles are converted to string form.  So, you can't check whether
    * the user edited the inputs just by checking whether the new values are
    * the same as the original values.  (This would probably be called a rather
    * minor point by most people.))
    * @return an array containing the four values from the input box, if the 
    *   user dismissed the dialog box with the OK button AND the user changed
    *   the initial values before clicking OK, or null if the user canceled OR
    *   if the user clicked OK but did not change the values..
    */
   public double[] getInputsIfChanged() {
      if (changed)
         return inputValues;
      else
         return null;
   }


}
