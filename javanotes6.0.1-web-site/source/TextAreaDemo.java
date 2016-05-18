import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This applet simply demonstrates using a JTextArea in a JScrollPane..
 */
public class TextAreaDemo extends JApplet {
   

   public void init() {
      
      String text = "So, naturalists observe, a flea\n"
         + "Has smaller fleas that on him prey;\n"
         + "And these have smaller still to bite 'em;\n"
         + "And so proceed ad infinitum.\n\n"
         + "                              --Jonathan Swift";
      
      JTextArea textArea = new JTextArea();
      JScrollPane scrollPane = new JScrollPane(textArea);
      
      textArea.setText(text);
      textArea.setFont( new Font("Serif", Font.PLAIN, 24 ));
      textArea.setMargin( new Insets(7,7,7,7) );
      
      JPanel content = new JPanel();
      content.setLayout(new BorderLayout());
      content.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      content.add(scrollPane, BorderLayout.CENTER);
      
      setContentPane(content);

   }

}
