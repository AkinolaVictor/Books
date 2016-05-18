import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.net.*;
import java.io.*;

/**
 * Defines a simple web browser that can load web pages from
 * URLs specified by the user in a "Location" box.  Almost all
 * the functionality is provided automatically by the JEditorPane
 * class.  The program loads web pages asynchronously in a
 * thread.  See SimpleWebBrowser for a synchronous version.
 * This class can be run as a standalone application and has
 * a nested class that can be run as an applet.  The applet
 * version can probably only read pages from the server from which 
 * it was loaded.
 */
public class SimpleWebBrowserWithThread extends JPanel {

   /**
    * The main routine simply opens a window that shows a SimpleWebBrowserWithThread panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("SimpleWebBrowserWithThread");
      SimpleWebBrowserWithThread content = new SimpleWebBrowserWithThread();
      window.setContentPane(content);
      window.setSize(600,500);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth())/2,
            (screenSize.height - window.getHeight())/2 );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
   
   /**
    * The public static class SimpleWebBrowserWithThread.Applet represents this program
    * as an applet.  The applet's init() method simply sets the content 
    * pane of the applet to be a SimpleWebBrowserWithThread.  To use the applet on
    * a web page, use code="SimpleWebBrowserWithThread$Applet.class" as the name of
    * the class.  A reasonable size for the applet is 600 by 480 or larger.
    */
   public static class Applet extends JApplet {
      public void init() {
         SimpleWebBrowserWithThread content = new SimpleWebBrowserWithThread();
         setContentPane( content );
      }
   }
   
   
   /**
    * The pane in which documents are displayed.
    */
   private JEditorPane editPane;
   
   
   /**
    * An input box where the user enters the URL of a document
    * to be loaded into the edit pane.  A value URL string should
    * contain the substring "://".  If the string in the box
    * does not contain this substring, then "http://" is 
    * prepended to the string.
    */
   private JTextField locationInput;
   
   
   /**
    * A button that the user can click in order to load the url
    * that is specified in the location input box.
    */
   private JButton goButton;
   
   
   /**
    * Defines a listener that responds when the user clicks on
    * a link in the document.
    */
   private class LinkListener implements HyperlinkListener {
      public void hyperlinkUpdate(HyperlinkEvent evt) {
         if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            new LoaderThread(evt.getURL());
         }
      }
   }
   
   
   /**
    * Defines a listener that loads a new page when the user
    * clicks the "Go" button or presses return in the location
    * input box.
    */
   private class GoListener implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         URL url;
         try {
            String location = locationInput.getText().trim();
            if (location.length() == 0)
               throw new Exception();
            if (! location.contains("://"))
               location = "http://" + location;
            url = new URL(location);
         }
         catch (Exception e) {
            JOptionPane.showMessageDialog(SimpleWebBrowserWithThread.this, 
                  "The Location input box does not\nccontain a legal URL.");
            return;
         }
         new LoaderThread(url);
      }
   }
   
   
   /**
    * Defines the thread that actually does the loading of a document.
    * The thread is also responsible for enabling/disabling the go button
    * and the location input box.
    */
   private class LoaderThread extends Thread {
      private URL urlToLoad;  
      LoaderThread(URL url) {  // constructor starts the thread.
         urlToLoad = url;
         locationInput.setEnabled(false);
         goButton.setEnabled(false);
         start();
      }
      public void run() {
         InputStream in = null;
         try {
            editPane.setContentType("text/plain");
            editPane.setText("Loading URL " + urlToLoad + "...");
            
            /* Open a URL connection just for the purpose of reading
             * the content type.  I only want to show the document if
             * the content type is supported by JEditorPane. */
            
            URLConnection connection = urlToLoad.openConnection();
            in = connection.getInputStream();
            String contentType = connection.getContentType();
            if (contentType == null)
               throw new Exception("Can't determine content type of url.");
            if ( ! (contentType.startsWith("text/plain") 
                  || contentType.startsWith("text/html") 
                  || contentType.startsWith("text/rtf") ) )
               throw new Exception("Can't display content type " + contentType);
            editPane.setText("Retrieving document contents...");
            locationInput.setText(urlToLoad.toString());
            in.close();  // I don't want to actually read from the connection!
            in = null;
            editPane.setPage(urlToLoad);
         }
         catch (Exception e) {
            editPane.setContentType("text/plain");
            editPane.setText( "Sorry, the requested document was not found\n"
                  +"or cannot be displayed.\n\nError:" + e);
         }
         finally {
            goButton.setEnabled(true);
            locationInput.setEnabled(true);
            locationInput.selectAll();
            locationInput.requestFocus();
            if (in != null) {
               try {
                  in.close();
               }
               catch (Exception e) {
               }
            }
         }
      }
   }

   
   /**
    * Construct a panel that contains a JEditorPane in a JScrollPane,
    * with a tool bar that has a Location input box and a Go button.
    */
   public SimpleWebBrowserWithThread() {
      
      setBackground(Color.BLACK);
      setLayout(new BorderLayout(1,1));
      setBorder(BorderFactory.createLineBorder(Color.BLACK,1));

      editPane = new JEditorPane();
      editPane.setEditable(false);
      editPane.addHyperlinkListener(new LinkListener());
      add(new JScrollPane(editPane),BorderLayout.CENTER);
      
      JToolBar toolbar = new JToolBar();
      toolbar.setFloatable(false);
      add(toolbar,BorderLayout.NORTH);
      ActionListener goListener = new GoListener();
      locationInput = new JTextField("math.hws.edu/javanotes/index.html", 40);
      locationInput.addActionListener(goListener);
      goButton = new JButton(" Go ");
      goButton.addActionListener(goListener);
      toolbar.add( new JLabel(" Location: "));
      toolbar.add(locationInput);
      toolbar.addSeparator(new Dimension(5,0));
      toolbar.add(goButton);

   }
   
   
   
}
