
import java.awt.*;

import javax.swing.*;


/**
 * Shows a JTable that contains a list of the states of the United States
 * and their capitals.  Since the default table model is used, the entries
 * in the table are editable, which is not really appropriate.
 * 
 * This class can be run as a stand-alone application.  It contains a static
 * nested class that can be used to run it as an applet.
 */
public class StatesAndCapitalsTableDemo extends JPanel {
   
   /**
    * The main routine simply opens a window that shows a StatesAndCapitalsTableDemo panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Trivial Table Demo");
      StatesAndCapitalsTableDemo content = new StatesAndCapitalsTableDemo();
      window.setContentPane(content);
      window.setSize( new Dimension(350,300) );
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth())/2,
            (screenSize.height - window.getHeight())/2 );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
   
   /**
    * The public static class StatesAndCapitalsTableDemo.Applet represents this program
    * as an applet.  The applet's init() method simply sets the content 
    * pane of the applet to be a StatesAndCapitalsTableDemo.  To use the applet on
    * a web page, use code="StatesAndCapitalsTableDemo$Applet.class" as the name of
    * the class.  A reasonable size for the applet is 350 by 200.
    */
   public static class Applet extends JApplet {
      public void init() {
         StatesAndCapitalsTableDemo content = new StatesAndCapitalsTableDemo();
         setContentPane( content );
      }
   }
   

   /**
    * Constructor just adds a table as the only component in this panel.
    */
   public StatesAndCapitalsTableDemo() {
      
      String[] columnHeads = new String[] { "State", "Capital City" };
      
      JTable table = new JTable(statesAndCapitals, columnHeads);
      
      setLayout(new BorderLayout());
      add( new JScrollPane(table), BorderLayout.CENTER );
      
   }

   /**
    * An alphabetical list of the states of the United States and
    * their capital cities.
    */
   private static String[][] statesAndCapitals = new String[][] {
           { "Alabama", "Montgomery" },
           { "Alaska", "Juneau" },
           { "Arizona", "Phoenix" },
           { "Arkansas", "Little Rock" },
           { "California", "Sacramento" },
           { "Colorado", "Denver" },
           { "Connecticut", "Hartford" },
           { "Delaware", "Dover" },
           { "Florida", "Tallahassee" },
           { "Georgia", "Atlanta" },
           { "Hawaii", "Honolulu" },
           { "Idaho", "Boise" },
           { "Illinois", "Springfield" },
           { "Indiana", "Indianapolis" },
           { "Iowa", "Des Moines" },
           { "Kansas", "Topeka" },
           { "Kentucky", "Frankfort" },
           { "Louisiana", "Baton Rouge" },
           { "Maine", "Augusta" },
           { "Maryland", "Annapolis" },
           { "Massachusetts", "Boston" },
           { "Michigan", "Lansing" },
           { "Minnesota", "St. Paul" },
           { "Mississippi", "Jackson" },
           { "Missouri", "Jefferson City" },
           { "Montana", "Helena" },
           { "Nebraska", "Lincoln" },
           { "Nevada", "Carson City" },
           { "New Hampshire", "Concord" },
           { "New Jersey", "Trenton" },
           { "New Mexico", "Santa Fe" },
           { "New York", "Albany" },
           { "North Carolina", "Raleigh" },
           { "North Dakota", "Bismarck" },
           { "Ohio", "Columbus" },
           { "Oklahoma", "Oklahoma City" },
           { "Oregon", "Salem" },
           { "Pennsylvania", "Harrisburg" },
           { "Rhode Island", "Providence" },
           { "South Carolina", "Columbia" },
           { "South Dakota", "Pierre" },
           { "Tennessee", "Nashville" },
           { "Texas", "Austin" },
           { "Utah", "Salt Lake City" },
           { "Vermont", "Montpelier" },
           { "Virginia", "Richmond" },
           { "Washington", "Olympia" },
           { "West Virginia", "Charleston" },
           { "Wisconsin", "Madison" },
           { "Wyoming", "Cheyenne" }
        };
   
}
