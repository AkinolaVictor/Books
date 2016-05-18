import java.awt.*;
import java.awt.event.*;
import java.io.StringReader;

import javax.swing.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

/**
 * A demonstration of simple XML parsing and processing using the Document Object
 * model.  XML can be entered into one text area (at the top).  When the user
 * clicks a "Parse XML Input", the input is parsed to give a DOM representation
 * of the XML.  If this succeeds, the DOM representation is traversed and information
 * is output about the nodes that are encountered.  Only Element, Text, and
 * Attribute nodes are understood by this program.  If the input is not well-formed
 * XML, an error message is output.  Output goes to a second text area in the
 * middle of the applet.  Initially, the input box contains a sample XML document.
 * 
 * This class has a main() routine, so it can be run as a stand-alone application.
 */
public class XMLDemo extends JPanel {

   public static void main(String[] args) {
      JFrame window = new JFrame("XMLDemo");
      window.setContentPane(new XMLDemo());
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.pack();
      window.setLocation(100,70);
      window.setVisible(true);
   }
   
   private final static String initialInput = 
               "<?xml version=\"1.0\"?>\n" +
               "<simplepaint version=\"1.0\">\n" +
               "   <background red='255' green='153' blue='51'/>\n" +
               "   <curve>\n" +
               "      <color red='0' green='0' blue='255'/>\n" +
               "      <symmetric>false</symmetric>\n" +
               "      <point x='83' y='96'/>\n" +
               "      <point x='116' y='149'/>\n" +
               "      <point x='159' y='215'/>\n" +
               "      <point x='216' y='294'/>\n" +
               "      <point x='264' y='359'/>\n" +
               "      <point x='309' y='418'/>\n" +
               "      <point x='371' y='499'/>\n" +
               "      <point x='400' y='543'/>\n" +
               "   </curve>\n" +
               "   <curve>\n" +
               "      <color red='255' green='255' blue='255'/>\n" +
               "      <symmetric>true</symmetric>\n" +
               "      <point x='54' y='305'/>\n" +
               "      <point x='79' y='289'/>\n" +
               "      <point x='128' y='262'/>\n" +
               "      <point x='190' y='236'/>\n" +
               "      <point x='253' y='209'/>\n" +
               "      <point x='341' y='158'/>\n" +
               "   </curve>\n" +
               "</simplepaint>\n";

   
   private JTextArea input;
   private JTextArea output;
   
   public XMLDemo() {
      setLayout(new BorderLayout(3,3));
      setPreferredSize(new Dimension(550,550));
      setBackground(Color.GRAY);
      setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
      JPanel buttonBar = new JPanel();
      add(buttonBar,BorderLayout.SOUTH);
      JPanel mainPanel = new JPanel();
      mainPanel.setBackground(Color.GRAY);
      mainPanel.setLayout(new GridLayout(2,1,3,3));
      add(mainPanel,BorderLayout.CENTER);
      input = new JTextArea(initialInput);
      input.setLineWrap(false);
      input.setMargin(new Insets(3,3,3,3));
      mainPanel.add(new JScrollPane(input));
      output = new JTextArea();
      output.setLineWrap(true);
      output.setWrapStyleWord(true);
      output.setMargin(new Insets(3,3,3,3));
      output.setEditable(false);
      mainPanel.add(new JScrollPane(output));
      JButton clearButton = new JButton("Clear");
      clearButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            input.setText("");
            output.setText("");
            input.requestFocus();
         }
      });
      buttonBar.add(clearButton);
      JButton restoreButton = new JButton("Restore Initial Input");
      restoreButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            input.setText(initialInput);
            input.setCaretPosition(0);
            input.requestFocus();
         }
      });
      buttonBar.add(restoreButton);
      JButton parseButton = new JButton("Parse XML Input");
      parseButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            doParse();
         }
      });
      buttonBar.add(parseButton);
   }

   
   /**
    * Attempt to parse the text from the input box as an XML document.
    * If an error occurs, display an error message.  If not, display
    * information about the nodes in the DOM representation of the document.
    */
   private void doParse() {
      output.setText("");
      String data = input.getText();
      if (data.trim().length() == 0) {
         output.setText("ERROR:  There is no text in the input box!\n");
         return;
      }
      Document xmldoc;
      try {
         DocumentBuilder docReader 
                   = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            xmldoc = docReader.parse(new InputSource(new StringReader(data)));
      }
      catch (Exception e) {
         output.setText("ERROR:  The input is not well-formed XML.\n\n" + e);
         return;
      }
      output.append("Input has been parsed successfully.\n");
      output.append("Nodes in the DOM representation:\n\n");
      Element root = xmldoc.getDocumentElement();
      listNodes(root,"",1);
   }
   
   
   /**
    * Display information about the Element root, its attributes, and any nested
    * element and text nodes.  This is a recursive routine.
    * @param node the Element whose name, attributes, and content will be displayed
    * @param indent spaces added at the beginning of each line of output.  This string
    *    of spaces grows with nested recursive call to this method.
    * @param level increases by 1 with each nested recursive call to this method.
    *    Gives the level of nesting of node within the root element of the document.
    */
   private void listNodes(Element node, String indent, int level) {
      output.append(indent + level + ". Element named:  " + node.getTagName() + '\n');
      NamedNodeMap attributes = node.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++) {
         Attr attribute = (Attr)attributes.item(i);
         output.append(indent + "         with attribute named: " + attribute.getName()
               + ",  value:  " + attribute.getValue() + '\n');
      }
      indent += "   ";
      level++;
      String prefix = indent + level + ". ";
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
         Node child = children.item(i);
         int nodeType = child.getNodeType();
         if (nodeType == Node.ELEMENT_NODE)
            listNodes( (Element)child, indent, level);
         else if (nodeType == Node.TEXT_NODE) {
            String text = child.getTextContent();
            text = text.trim();
            if (text.length() > 0)
               output.append(prefix + "Text node containing:  " + text + '\n');
         }
         else 
            output.append(prefix + "(Some other type of node.)\n");
      }
   }
   
}
