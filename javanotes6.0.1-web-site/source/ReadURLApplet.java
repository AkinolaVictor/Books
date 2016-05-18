
/**
 * The applet URLExampleApplet demonstrates how to read data from
 * a url over the Internet using classes from the packages java.net
 * and java.io.  If the data from the url is of type text, then
 * the data is displayed. 
 * The user can specify the url to load by typing the url
 * into an input box.  An initial URL to load can also be specified
 * in an applet parameter with the name "URL".
 * 
 * (Note that this program uses a thread, although threads are not
 * covered until after this example has been given.)
 */


import java.awt.*;       // make graphical user interface classes available
import java.awt.event.*; // make event-handling classes available
import javax.swing.*;    // make Swing GUI classes available
import java.io.*;        // make I/O classes such as InputStream available
import java.net.*;       // make networking classes such as URL available

public class ReadURLApplet extends JApplet  implements Runnable, ActionListener {

   private JTextArea textDisplay;  // data loaded from url is displayed here
   private JTextField inputBox;    // user enters name of url to be loaded here
   private JButton loadButton;     // user clicks on this button to load the url

   private String urlName;  // the name of the url to be loaded by the run() method.
                            // Note:  this url can be specified relative to the location
                            //         of the HTML document that contains the applet.

   private Thread loader;   // thread for reading data from the url



   /**
    * Lays out the applet when it is first created. If there is an applet parameter 
    * named "URL", then that parameter is read and loaded into the input box.
    * the user can then load the url by clicking on load.
    */
   public void init() { 
   
      JPanel content = new JPanel();
      setContentPane(content);
      
      content.setBackground(Color.GRAY);
   
      textDisplay = new JTextArea();
      textDisplay.setEditable(false);
   
      loadButton = new JButton("Load");
      loadButton.addActionListener(this);
   
      String url = getParameter("URL");
      if (url == null) 
         inputBox = new JTextField();
      else
         inputBox = new JTextField(url);
      inputBox.addActionListener(this);
   
      JPanel bottom = new JPanel();
      bottom.setBackground(Color.GRAY);
      bottom.setLayout(new BorderLayout(3,3));
      bottom.add(inputBox, BorderLayout.CENTER);
      bottom.add(loadButton, BorderLayout.EAST);
      bottom.add(new JLabel("Enter URL:"), BorderLayout.WEST);
   
      content.setLayout(new BorderLayout(3,3));
      content.add(new JScrollPane(textDisplay), BorderLayout.CENTER);
      content.add(bottom, BorderLayout.SOUTH);
      
      content.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
   
      inputBox.requestFocus();
   
   }



   /**
    * This method is called by actionPerformed() to load a url.
    * The interface elements are "turned off" so they can't be used while the 
    * url is loading.  Then a separate thread is started to do the loading.  
    * The interface elements will be turned back on when that thread finishes 
    * its execution.
    * <p>NOTE:  I use a thread to do the loading so that the loading can take 
    * place asynchronously, while other things are going on in the applet.  
    * (For this simple example, there really isn't anything else to do, but 
    * the technique is useful to know.  Furthermore, it can take some time
    * to load the data -- or to discover an error -- and it's not a good idea
    * to block the graphical user interface completely while waiting.)
    * @param urlToLoad the url to be loaded, in text form.
    */
   private void doLoad(String urlToLoad) {
      inputBox.setEditable(false);
      loadButton.setEnabled(false);
      textDisplay.setText("Connecting...");
      urlName = urlToLoad;  // set the urlName which will be used by the thread
      loader = new Thread(this);
      loader.start();
   }


   /**
    * Responds when the user clicks on the load button or presses return in the input box.
    */
   public void actionPerformed(ActionEvent evt) {
      String urlToLoad = inputBox.getText().trim();
      if (urlToLoad.equals("")) {
         textDisplay.setText("No URL found in text-input box");
         textDisplay.requestFocus();
      }
      else
         doLoad(urlToLoad);
   }


   /**
    * Loads the data in the url specified by an instance variable named 
    * urlName.  The data is displayed in a TextArea named textDisplay.  
    * Exception handling is used to detect and respond to errors that might 
    * occur.  This method is run in a separate thread to avoid blocking the
    * graphical user interface.
    */
   public void run() {

      try {

         /* Create a URL object.  This can throw a MalformedURLException. */

         URL url = new URL(getDocumentBase(), urlName);

         /* Open a connection to the URL, and get an input stream
               for reading data from the URL. */

         URLConnection connection = url.openConnection();
         InputStream urlData = connection.getInputStream();

         /* Check that the content is some type of text. */

         String contentType = connection.getContentType();
         if (contentType == null || contentType.startsWith("text") == false)
            throw new IOException("URL does not refer to a text file.");

         /* Copy lines from the input stream until 10000 characters
               have been copied or end-of-file is encountered. */

         textDisplay.setText("Receiving Data...");

         BufferedReader in;  // For reading from the connection's input stream.
         in = new BufferedReader(new InputStreamReader(urlData));
         StringBuffer chars;  // Store chars here before adding to text area.
         chars = new StringBuffer(10000);

         while (true) {
            String line = in.readLine();
            if (line == null)
               break;
            chars.append(line);
            chars.append('\n');
            if ( chars.length() >= 10000) {
               chars.append("\n\nINPUT TRUNCATED TO " + chars.length()
                     + " CHARACTERS\n");
               break;
            }
         }
         in.close();

         /* Finally, put the text that has been received into the 
               text area. */

         textDisplay.setText(chars.toString()); 

      }
      catch (MalformedURLException e) {
            // Can be thrown when URL is created.
         textDisplay.setText(
             "\nERROR!  Improper syntax given for the URL to be loaded.");
      }
      catch (SecurityException e) {  
            // Can be thrown when the connection is created.
         textDisplay.setText("\nSECURITY ERROR!  Can't access that URL.");
      }
      catch (IOException e) {  
            // Can be thrown while data is being read.
         textDisplay.setText(
               "\nINPUT ERROR! Problem reading data from that URL:\n"
               + e.toString());
      }
      finally {  
            // This part is done, no matter what, before the thread ends.
            // Set up the user interface of the applet so the user can
            // enter another URL.
         loadButton.setEnabled(true);
         inputBox.setEditable(true);
         inputBox.selectAll();
         inputBox.requestFocus();
      }

   } // end of run() method
   
   
} // end class ReadURLApplet

