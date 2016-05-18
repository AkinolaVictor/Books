import javax.swing.JApplet;

/**
 * A simple applet that can display the messages "Hello World"
 * and "Goodbye World".  The applet contains a button, and it
 * switches from one message to the other when the button is
 * clicked.  The content of the applet is an object of type
 * HelloWorldPanel.
 */

public class HelloWorldApplet2 extends JApplet {
   public void init() {  
      HelloWorldPanel content = new HelloWorldPanel();
      setContentPane(content);
   }
}