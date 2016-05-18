import java.awt.*;

import javax.swing.*;
import java.awt.geom.Line2D;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

/**
 * Demonstrates the use of a custom table model, and some other
 * configuration, for a JTable.  The program lets the user enter
 * (x,y) coordinates of some points, and it draws a simple
 * scatter plot of all the points in the table for which
 * both the x and the y coordinate is defined.  This class can
 * be run as a standalone application and has a nested class
 * that can be used to run the program as an applet.
 */
public class ScatterPlotTableDemo extends JPanel {
   

   /**
    * The main routine simply opens a window that shows a ScatterPlotTableDemo panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("ScatterPlotTableDemo");
      ScatterPlotTableDemo content = new ScatterPlotTableDemo();
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
    * The public static class ScatterPlotTableDemo.Applet represents this program
    * as an applet.  The applet's init() method simply sets the content 
    * pane of the applet to be a ScatterPlotTableDemo.  To use the applet on
    * a web page, use code="ScatterPlotTableDemo$Applet.class" as the name of
    * the class. A reasonable size for the applet is 580 by 300.
    */
   public static class Applet extends JApplet {
      public void init() {
         ScatterPlotTableDemo content = new ScatterPlotTableDemo();
         setContentPane( content );
      }
   }

   
   
   /**
    * This class defines the TableModel that is used for the JTable in this
    * program.  The table has three columns.  Column 0 simply holds the
    * row number of each row.  Column 1 holds the x-coordinates of the
    * points for the scatter plot, and Column 2 holds the y-coordinates.
    * The table has 25 rows.  No support is provided for adding more rows.
    */
   private class CoordInputTableModel extends AbstractTableModel {
      
      private Double[] xCoord = new Double[25];  // Data for Column 1, initially all null.
      private Double[] yCoord = new Double[25];  // Data for Column 2, initially all null.
      
      public int getColumnCount() {  // Tells caller how many columns there are.
         return 3;
      }

      public int getRowCount() {  // Tells caller how many rows there are.
         return xCoord.length;
      }

      public Object getValueAt(int row, int col) {  // Get the data for one cell.
         if (col == 0)
            return (row+1);   // Column 0 holds the row number.
         else if (col == 1)
            return xCoord[row];    // Column 1 holds the x-coordinates.
         else
            return yCoord[row];    // column 2 holds the y-coordinates.
      }

      public Class<?> getColumnClass(int col) {  // Get data type of column.
         if (col == 0)
            return Integer.class;
         else
            return Double.class;
      }

      public String getColumnName(int col) {  // Returns a name for column header.
         if (col == 0)
            return "Num";
         else if (col == 1)
            return "X";
         else
            return "Y";
      }

      public boolean isCellEditable(int row, int col) { // Can user edit cell?
         return col > 0;
      }
      
      public void setValueAt(Object obj, int row, int col) { // Changes cell value.
            // (This method is called by the system if the value of the cell
            // needs to be changed because the user has edited the current value.
            // It can also be called to change the value programmatically.
            // In this case, only columns 1 and 2 can be modified, and the data
            // type for obj must be Double.  The method fireTableCellUpdated()
            // has to be called to send a TableModelEvent to registered listeners.)
         if (col == 1) 
            xCoord[row] = (Double)obj;
         else if (col == 2)
            yCoord[row] = (Double)obj;
         fireTableCellUpdated(row, col);
      }
      
   }  // end nested class CoordInputTableModel
   
   
   /**
    * Defines the display area where a scatter plot of the points
    * in the table is drawn.  The range of values shown in the plot
    * is adjusted to make sure that all the points are visible.
    * Note that only points for which both coordinates are
    * defined are drawn.
    */
   private class Display extends JPanel {
      public void paintComponent(Graphics g) {  
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g;

         double min = -0.5;  // Minimum of the range of values displayed.
         double max = 5;     // Maximum of the range of value displayed.
         int count = model.getRowCount();
         for (int i = 0; i < count; i++) {
            Double x = (Double)model.getValueAt(i, 1);  // (Return type of getValue() is Object.)
            Double y = (Double)model.getValueAt(i, 2);
            if (x != null && y != null) {
               // Adjust max and min to include x and y.
               if (x < min)
                  min = x - 0.5;
               if (x > max)
                  max = x + 0.5;
               if (y < min)
                  min = y - 0.5;
               if (y > max)
                  max = y + 0.5;
            }
         }

         /* Apply a translation so that the drawing coordinates on the display
               correspond to the range of values that I want to show. */

         g2.translate(getWidth()/2,getHeight()/2);
         g2.scale(getWidth()/(max-min), -getHeight()/(max-min));
         g2.translate(-(max+min)/2, -(max+min)/2);

         /* I want to be able to draw lines that are a certain number of pixels
               long.  Unfortunately, the unit of length is no longer equal to the
               size of a pixel, so I have to figure out how big a pixel is in the
               new coordinates.  Also, horizontal and vertical size can be different. */

         double pixelWidth = (max-min)/getWidth();    // Horizontal size of a pixel in new coords.
         double pixelHeight = (max-min)/getHeight();  // Vertical size of a pixel in new coord.

         /* When the thickness of a BasicStroke is set to 0, the actual width of
               the stroke will be as small as possible, that is, one pixel wide. */

         g2.setStroke(new BasicStroke(0));

         /* Draw x and y axes with tick marks to mark the integers (but don't draw
               the tick marks if there would be more than 100 of them. */

         g2.setColor(Color.BLUE);
         g2.draw( new Line2D.Double(min,0,max,0));
         g2.draw( new Line2D.Double(0,min,0,max));
         if (max - min < 100) {
            int tick = (int)min;
            while (tick <= max) {
               g2.draw(new Line2D.Double(tick,0,tick,3*pixelHeight));
               g2.draw(new Line2D.Double(0,tick,3*pixelWidth,tick));
               tick++;
            }
         }

         /* Draw a small crosshair at each point from the table. */

         g2.setColor(Color.RED);
         for (int i = 0; i < count; i++) {
            Double x = (Double)model.getValueAt(i, 1);
            Double y = (Double)model.getValueAt(i, 2);
            if (x != null && y != null) {
               g2.draw(new Line2D.Double(x-3*pixelWidth,y,x+3*pixelWidth,y));
               g2.draw(new Line2D.Double(x,y-3*pixelHeight,x,y+3*pixelHeight));
            }
         }

      }
   } // end nested class Display
   
   
   private JTable table;                // The JTable where the points are input.
   private CoordInputTableModel model;  // The TableModel for the table.
   private Display display;             // The panel where the scatter plot is drawn.

   
   /**
    * The constructor creates the model, table, and display.  The table and display
    * are added to this panel.  The table is configured to show grid lines between
    * cells, to disallow dragging of a column to a new position, to increase the
    * height of the cells, to set different preferred sizes for the columns, and
    * to set a preferred size for the viewport of the scrollpane that will display
    * the table.  A listener is set up on the model so that the display can be
    * changed whenever the data in the model changes.  (Also, some random data is
    * put in the table, as an example to help the user see what is going on -- this
    * is just done for demonstration purposes.)
    */
   public ScatterPlotTableDemo() {
      
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
      
      model = new CoordInputTableModel();
      table = new JTable(model);
      table.setRowHeight(25);
      table.setShowGrid(true);
      table.setGridColor(Color.BLACK);
      table.setPreferredScrollableViewportSize(new Dimension(250, 300));
      table.getColumnModel().getColumn(0).setPreferredWidth(50);
      table.getColumnModel().getColumn(1).setPreferredWidth(100);
      table.getColumnModel().getColumn(2).setPreferredWidth(100);
      table.getTableHeader().setReorderingAllowed(false);
      add(new JScrollPane(table), BorderLayout.WEST);
      
      for  (int i = 0; i < 6; i++) {  // Fill first 6 rows with random values.
         model.setValueAt( (int)(450*Math.random())/100.0, i, 1 );
         model.setValueAt( (int)(450*Math.random())/100.0, i, 2 );
      }
      
      display = new Display();
      display.setPreferredSize(new Dimension(300,300));
      display.setBackground(Color.WHITE);
      add(display, BorderLayout.CENTER);
      
      model.addTableModelListener(new TableModelListener() {
            // Install a TableModelListener that will respond to any
            // changes in the model's data by redrawing the display that
            // shows the scatter plot.
         public void tableChanged(TableModelEvent e) {
            display.repaint();
         }
      });
   }
   
}
