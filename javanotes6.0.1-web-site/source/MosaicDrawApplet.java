import javax.swing.JApplet;

/**
 * A MosaicDrawApplet shows a MosaicPanel that the user can draw on,
 * along with a menu bar containing some options.  The number of rows
 * and columns in the mosaic will be 25 by default, but can be changed
 * using applet parameters.  The value of the applet param named "rows",
 * if it is presenta and is a legal integer, becomes the number of rows.  
 * The value of the applet param named "columns", if it present and is a
 * legal integer, becomes the number of columns.
 * Note that this class depends on MosaicPanel.java and MosaicDrawController.java.
 */
public class MosaicDrawApplet extends JApplet{

   public void init() {
      int rows = 25, cols = 25;
      try {
         rows = Integer.parseInt( getParameter("rows") );
      }
      catch (NumberFormatException e) {
      }
      try {
         cols = Integer.parseInt( getParameter("columns") );
      }
      catch (NumberFormatException e) {
      }
      MosaicDrawController controller = new MosaicDrawController(rows,cols);
      setContentPane(controller.getMosaicPanel());
      setJMenuBar(controller.getMenuBar());
   }

}
