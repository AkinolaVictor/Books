

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * In this applet, the user can type in two real numbers.  The
 * user can click on buttons labeled +, - , *, and / to perform
 * basic arithmetic operations on the numbers.  When the user
 * clicks on a button the answer is displayed.  The applet
 * should be about 300 by 160 pixels.
 * Note that this class also contains a main() routine, so that
 * it can be run as a stand-alone application.  When it is run
 * in this way, it opens a window that shows that same panel that
 * is used in the applet version.
 */
public class SimpleCalc extends JApplet {
   
   /**
    * This main() routine makes it possible to run the SimpleCalc1 class
    * as a stand-alone application.  This routine just opens a window that
    * uses an object of type CalcPanel as its content pane, where CalcPanel
    * is the nested class defined below.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Simple Calculator");
      CalcPanel content = new CalcPanel();
      window.setContentPane(content);
      window.pack();  // Sizes window to preferred size of contents.
      window.setLocation(100,100);
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
   
   /**
    * The init() method for the applet just sets the content pane
    * of the applet to be an object of type CalcPanel, where CalcPanel
    * is the nested class defined below.
    */
   public void init() {
      setContentPane( new CalcPanel() );
   }
   
   
   public static class CalcPanel extends JPanel implements ActionListener {
      
      private JTextField xInput, yInput;  // Input boxes for the numbers.
      
        private JLabel answer;  // JLabel for displaying the answer, or an 
                              //    error message if appropriate.
      
      
      public CalcPanel() {
                  
         /* Assign a background color to the panel and its
          content panel.  This color will show through in the gaps
          between components. */
         
         setBackground(Color.GRAY);
         
         /* Add an empty border around the panel, which will also
          * appear in the gray background color. */
         
         setBorder( BorderFactory.createEmptyBorder(5,5,5,5) );
         
         /* Create the input boxes, and make sure that the background
          color is white.  (They are likely to be white by default.) */
         
         xInput = new JTextField("0", 10);
         xInput.setBackground(Color.WHITE);
         yInput = new JTextField("0", 10);
         yInput.setBackground(Color.WHITE);
         
         /* Create panels to hold the input boxes and labels "x =" and
          "y = ".  These panels use the default FlowLayout layout manager. */
         
         JPanel xPanel = new JPanel();
         xPanel.add( new JLabel(" x = "));
         xPanel.add(xInput);
         
         JPanel yPanel = new JPanel();
         yPanel.add( new JLabel(" y = "));
         yPanel.add(yInput);
         
         /* Create a panel to hold the four buttons for the four
          operations.  A GridLayout is used so that the buttons
          will all have the same size and will fill the panel. 
          The main panel servers as ActionListener for the buttons. */
         
         JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new GridLayout(1,4));
         
         JButton plus = new JButton("+");
         plus.addActionListener(this);
         buttonPanel.add(plus);
         
         JButton minus = new JButton("-");
         minus.addActionListener(this);
         buttonPanel.add(minus);
         
         JButton times = new JButton("*");
         times.addActionListener(this);
         buttonPanel.add(times);
         
         JButton divide = new JButton("/");
         divide.addActionListener(this);
         buttonPanel.add(divide);
         
         /* Create the label for displaying the answer in red
          on a white background.  The label is set to be
          "opaque" to make sure that the white background
          is painted. */
         
         answer = new JLabel("x + y = 0", JLabel.CENTER);
         answer.setForeground(Color.red);
         answer.setBackground(Color.white);
         answer.setOpaque(true);
         
         /* Set up the layout for the main panel, using a GridLayout,
          and add all the components that have been created. */
         
         setLayout(new GridLayout(4,1,3,3));
         add(xPanel);
         add(yPanel);
         add(buttonPanel);
         add(answer);
         
         /* Try to give the input focus to xInput, which is the natural
          place for the user to start. */
         
         xInput.requestFocus();
         
      }  // end constructor
      
            
      /**
       * When the user clicks a button, get the numbers from the input boxes 
       * and perform the operation indicated by the button.  Put the result in
       * the answer label.  If an error occurs, an error message is put in the label.
       */
      public void actionPerformed(ActionEvent evt) {
         
         double x, y;  // The numbers from the input boxes.
         
         /* Get a number from the xInput JTextField.  Use 
          xInput.getText() to get its contents as a String.
          Convert this String to a double.  The try...catch
          statement will check for errors in the String.  If 
          the string is not a legal number, the error message
          "Illegal data for x." is put into the answer and
          the actionPerformed() method ends. */
         
         try {
            String xStr = xInput.getText();
            x = Double.parseDouble(xStr);
         }
         catch (NumberFormatException e) {
            // The string xStr is not a legal number.
            answer.setText("Illegal data for x.");
            xInput.requestFocus();
            return;
         }
         
         /* Get a number from yInput in the same way. */
         
         try {
            String yStr = yInput.getText();
            y = Double.parseDouble(yStr);
         }
         catch (NumberFormatException e) {
            answer.setText("Illegal data for y.");
            yInput.requestFocus();
            return;
         }
         
         /* Perform the operation based on the action command
          from the button.  Note that division by zero produces
          an error message. */
         
         String op = evt.getActionCommand();
         if (op.equals("+"))
            answer.setText( "x + y = " + (x+y) );
         else if (op.equals("-"))
            answer.setText( "x - y = " + (x-y) );
         else if (op.equals("*"))
            answer.setText( "x * y = " + (x*y) );
         else if (op.equals("/")) {
            if (y == 0)
               answer.setText("Can't divide by zero!");
            else
               answer.setText( "x / y = " + (x/y) );
         }
         
      } // end actionPerformed()
      
   } // end nested class CalcPanel
   

}  // end class SimpleCalculator
