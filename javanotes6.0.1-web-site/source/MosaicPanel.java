
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 *  A MosaicPanel object represents a grid containing rows
 *  and columns of colored rectangles.  There can be "grouting"
 *  between the rectangles.  (The grouting is just drawn as a 
 *  one-pixel outline around each rectangle.)  The rectangles
 *  are drawn as raised 3D-style rectangles.  Methods are 
 *  provided for getting and setting the colors of the rectangles.
 *  (Revision Spring 2006: added possibility of having a border.)
 *  (March 2006: added autopaint option for use in PentominosPanel).
 */
public class MosaicPanel extends JPanel {
   
   
   //------------------ private instance variables --------------------
   
   
   private int rows;       // The number of rows of rectangles in the grid.
   private int columns;    // The number of columns of rectangles in the grid.
   private Color defaultColor;   // Color used for any rectangle whose color
                        //    has not been set explicitly.  This
                        //    can never be null.
   private Color groutingColor;  // The color for "grouting" between 
                        //    rectangles.  If this is null, no
                        //    grouting is drawn.
   private boolean alwaysDrawGrouting;  // Grouting is drawn around default-
                              //    colored rects if this is true.
   private boolean autopaint = true;  // If true, then when a squar's color is set, repaint is called automatically.
   private Color[][] grid; // An array that contains the rectangles' colors.
                     //   If a null occurs in this array, the rectangle
                     //   is drawn in the default color, and "grouting"
                     //   will be drawn around that rectangle only if
                     //   alwaysDrawGrouting is true.  Also, the 
                     //   rectangle is drawn as a flat rectangle rather
                     //   than as a 3D rectangle.
   private BufferedImage OSI;  // The mosaic is actually drawn here, then the image 
                        //is copied to the screen
   private boolean needsRedraw;   // This is set to true when a change has occurred that
                         // changes the appearance of the mosaic.

   
   //------------------------ constructors -----------------------------
   
   
   /**
    *  Construct a MosaicPanel with 42 rows and 42 columns of rectangles,
    *  and with preferred rectangle height and width both set to 16.
    */
   public MosaicPanel() {
      this(42,42,16,16);
   }
   
   /**
    *  Construct a MosaicPanel with specified numbers of rows and columns of rectangles,
    *  and with preferred rectangle height and width both set to 16.
    */
   public MosaicPanel(int rows, int columns) {
      this(rows,columns,16,16);
   }
   
   /**
    *  Construct a MosaicPanel with the specified number of rows and
    *  columns of rectangles, and with a specified preferred size for the  
    *  rectangle.  The default rectangle color is black, the
    *  grouting color is gray, and alwaysDrawGrouting is set to false.
    *  @param rows the mosaic will have this many rows of rectangles.  This must be a positive number.
    *  @param columns the mosaic will have this many columns of rectangles.  This must be a positive number.
    *  @param preferredBlockWidth the preferred width of the mosaic will be set this value times the number of
    *  columns.  The actual width is set by the component that contains the mosaic, and so might not be
    *  equal to the preferred width.  Size is measured in pixels.  The value should not be less than about 5.
    *  @param preferredBlockHeight the preferred height of the mosaic will be set this value times the number of
    *  rows.  The actual height is set by the component that contains the mosaic, and so might not be
    *  equal to the preferred height.   Size is measured in pixels.  The value should not be less than about 5.
    */
   public MosaicPanel(int rows, int columns, int preferredBlockWidth, int preferredBlockHeight) {
      this(rows, columns, preferredBlockWidth, preferredBlockHeight, null, 0);
   }

   
   /**
    *  Construct a MosaicPanel with the specified number of rows and
    *  columns of rectangles, and with a specified preferred size for the  
    *  rectangle.  The default rectangle color is black, the
    *  grouting color is gray, and alwaysDrawGrouting is set to false.
    *  If a non-null border color is specified, then a border of that color is added
    *  to the panel, and its width is taken into account in the computation of the preferred
    *  size of the panel.
    *  @param rows the mosaic will have this many rows of rectangles.  This must be a positive number.
    *  @param columns the mosaic will have this many columns of rectangles.  This must be a positive number.
    *  @param preferredBlockWidth the preferred width of the mosaic will be set this value times the number of
    *  columns.  The actual width is set by the component that contains the mosaic, and so might not be
    *  equal to the preferred width.  Size is measured in pixels.  The value should not be less than about 5.
    *  @param preferredBlockHeight the preferred height of the mosaic will be set this value times the number of
    *  rows.  The actual height is set by the component that contains the mosaic, and so might not be
    *  equal to the preferred height.   Size is measured in pixels.  The value should not be less than about 5.
    *  @param borderColor if non-null, a border of this color is added to the panel.  The border is then
    *  taken into account in the computation of the panel's preferred size.
    *  @param borderWidth if borderColor is non-null, then this parameter gives the width of the border; any
    *  value less than 1 is treated as 1.
    */
   public MosaicPanel(int rows, int columns, int preferredBlockWidth, int preferredBlockHeight, Color borderColor, int borderWidth) {
      this.rows = rows;
      this.columns = columns;
      grid = new Color[rows][columns];
      defaultColor = Color.black;
      groutingColor = Color.gray;
      alwaysDrawGrouting = false;
      setBackground(defaultColor);
      setOpaque(true);
      setDoubleBuffered(false);
      if (borderColor != null) {
         if (borderWidth < 1)
            borderWidth = 1;
         setBorder(BorderFactory.createLineBorder(borderColor,borderWidth));
      }
      else
         borderWidth = 0;
      if (preferredBlockWidth > 0 && preferredBlockHeight > 0)
         setPreferredSize(new Dimension(preferredBlockWidth*columns + 2*borderWidth, preferredBlockHeight*rows + 2*borderWidth));
   }
   
   
   //--------- methods for getting and setting grid properties ----------
   
   
   /**
    *  Set the defaultColor.  If c is null, the color will be set to black.
    *  When a mosaic is first created, the defaultColor is black.
    *  This is the color that is used for rectangles whose color
    *  value is null.  Such rectangles are drawn as flat rather
    *  than 3D rectangles.
    */
   public void setDefaultColor(Color c) {
      if (c == null)
         c = Color.black;
      if (! c.equals(defaultColor)) {
         defaultColor = c;
         setBackground(c);
         forceRedraw();
      }
   }
   
   
   /**
    *  Return the defaultColor, which cannot be null.
    */
   public Color getDefaultColor() {
      return defaultColor;
   }
   
   
   /**
    *  Set the color of the "grouting" that is drawn between rectangles.
    *  If the value is null, no grouting is drawn and the rectangles
    *  fill the entire grid.   When a mosaic is first created, the
    *  groutingColor is gray.
    */
   public void setGroutingColor(Color c) {
      if (c == null || ! c.equals(groutingColor)) {
         groutingColor = c;
         forceRedraw();
      }
   }
   
   
   /**
    *  Get the current groutingColor, which can be null.
    */
   public Color getGroutingColor(Color c) {
      return groutingColor;
   }
   
   
   /**
    *  Set the value of alwaysDrawGrouting.  If this is false, then
    *  no grouting is drawn around rectangles whose color value is null.
    *  When a mosaic is first created, the value is false.
    */
   public void setAlwaysDrawGrouting(boolean always) {
      if (alwaysDrawGrouting != always) {
         alwaysDrawGrouting = always;
         forceRedraw();
      }
   }
   
   
   /**
    *  Get the value of the alwaysDrawGrouting property.
    */   
   public boolean getAlwaysDrawGrouting() {
      return alwaysDrawGrouting; 
   }
   
   
   /**
    *  Set the number of rows and columns in the grid.  If the value of
    *  the preserveData parameter is false, then the color values of all
    *  the rectangles in the new grid are set to null.  If it is true,
    *  then as much color data as will fit is copied from the old grid.
    */
   public void setGridSize(int rows, 
         int columns, boolean preserveData) {
      if (rows > 0 && columns > 0) {
         Color[][] newGrid = new Color[rows][columns];
         if (preserveData) {
            int rowMax = Math.min(rows,this.rows);
            int colMax = Math.min(columns,this.columns);
            for (int r = 0; r < rowMax; r++)
               for (int c = 0; c < colMax; c++)
                  newGrid[r][c] = grid[r][c];
         }
         grid = newGrid;
         this.rows = rows;
         this.columns = columns;
         forceRedraw();
      }
   }
   
   
   /**
    *  Return the number of rows of rectangles in the grid.
    */
   public int getRowCount() {
      return rows;
   }
   
   
   /**
    *  Return the number of columns of rectangles in the grid.
    */
   public int getColumnCount() {
      return columns;
   }   
   
   
   //------------------ other useful public methods ---------------------
   
   
   
   /**
    *  Get the color has been set for the rectangle in the specified
    *  row and column of the grid.  This value can be null if no
    *  color has been set for that rectangle.  (Such rectangles are
    *  actually displayed using the defaultColor.)  If the specified
    *  rectangle is outside the grid, then null is returned.
    */
   public Color getColor(int row, int col) {
      if (row >=0 && row < rows && col >= 0 && col < columns)
         return grid[row][col];
      else
         return null;
   }
   
   
   /**
    *  Return the red component of color of the rectangle in the
    *  specified row and column.  If that rectangle lies outside 
    *  the grid or if no color has been specified for the rectangle,
    *  then the red component of the defaultColor is returned.
    */
   public int getRed(int row, int col) {
      if (row >=0 && row < rows && col >= 0 && col < columns && grid[row][col] != null)
         return grid[row][col].getRed();
      else
         return defaultColor.getRed();
   }
   
   
   /**
    *  Return the green component of color of the rectangle in the
    *  specified row and column.  If that rectangle lies outside 
    *  the grid or if no color has been specified for the rectangle,
    *  then the green component of the defaultColor is returned.
    */
   public int getGreen(int row, int col) {
      if (row >=0 && row < rows && col >= 0 && col < columns && grid[row][col] != null)
         return grid[row][col].getGreen();
      else
         return defaultColor.getGreen();
   }
   
   
   /**
    *  Return the blue component of color of the rectangle in the
    *  specified row and column.  If that rectangle lies outside 
    *  the grid or if no color has been specified for the rectangle,
    *  then the blue component of the defaultColor is returned.
    */
   public int getBlue(int row, int col) {
      if (row >=0 && row < rows && col >= 0 && col < columns && grid[row][col] != null)
         return grid[row][col].getBlue();
      else
         return defaultColor.getBlue();
   }
   
   
   /**
    *  Set the color of the rectangle in the specified row and column.
    *  If the rectangle lies outside the grid, this is simply ignored.
    *  The color can be null.  Rectangles for which the color is null
    *  will be displayed in the defaultColor, and they will be shown
    *  as flat rather than 3D rects.
    */
   public void setColor(int row, int col, Color c) {
      if (row >=0 && row < rows && col >= 0 && col < columns) {
         grid[row][col] = c;
         drawSquare(row,col);
      }
   }
   
   
   /**
    *  Set the color of the rectangle in the specified row and column.
    *  The color is specified by giving red, green, and blue components
    *  of the color.  These values should be in the range from 0 to
    *  255, inclusive, and they are clamped to lie in that range.
    *  If the rectangle lies outside the grid, this is simply ignored.
    */
   public void setColor(int row, int col, int red, int green, int blue) {
      if (row >=0 && row < rows && col >= 0 && col < columns) {
         red = (red < 0)? 0 : ( (red > 255)? 255 : red);
         green = (green < 0)? 0 : ( (green > 255)? 255 : green);
         blue = (blue < 0)? 0 : ( (blue > 255)? 255 : blue);
         grid[row][col] = new Color(red,green,blue);
         drawSquare(row,col);
      }
   }
   
   
   /**
    *  Set the color of the rectangle in the specified row and column.
    *  The color is specified by giving hue, saturation, and brightness
    *  components of the color.  These values should be in the range from 
    *  0.0 to 1.0, inclusive, and they are clamped to lie in that range.
    *  If the rectangle lies outside the grid, this is simply ignored.
    */
   public void setHSBColor(int row, int col, 
         double hue, double saturation, double brightness) {
      if (row >=0 && row < rows && col >= 0 && col < columns) {
         grid[row][col] = makeHSBColor(hue,saturation,brightness);
         drawSquare(row,col);
      }
   }
   
   
   /**
    *  A little utility routine that is provided for making a color
    *  from hue, saturation, and brightness values.  These values should
    *  be in the range from 0.0 to 1.0, inclusive, and they are clamped
    *  to lie in that range.  (This method is more convenient than
    *  Color.getHSBColor() since it use double values rather than float.)
    */
   public static Color makeHSBColor(
         double hue, double saturation, double brightness) {
      float h = (float)hue;
      float s = (float)saturation;
      float b = (float)brightness;
      h = (h < 0)? 0 : ( (h > 1)? 1 : h );
      s = (s < 0)? 0 : ( (s > 1)? 1 : s );
      b = (b < 0)? 0 : ( (b > 1)? 1 : b );
      return Color.getHSBColor(h,s,b);
   }
   
   
   /**
    *  Set all rectangles of the grid to have the specified color.
    *  The color can be null.  In that case, the rectangles are
    *  drawn as flat rather than 3D rects in the defaultColor.
    */
   public void fill(Color c) {
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            grid[i][j] = c;
      forceRedraw();      
   }
   
   
   /**
    *  Set all rectangles of the grid to have the color specified by
    *  the given red, green, and blue components.  These components 
    *  should be integers in the range 0 to 255 and are clamped to lie
    *  in that range.
    */
   public void fill(int red, int green, int blue) {
      red = (red < 0)? 0 : ( (red > 255)? 255 : red);
      green = (green < 0)? 0 : ( (green > 255)? 255 : green);
      blue = (blue < 0)? 0 : ( (blue > 255)? 255 : blue);
      fill(new Color(red,green,blue));
   }
   
   
   /**
    *  Fill all the rectangles with randomly selected colors.
    */
   public void fillRandomly() {
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++) {
            int r = (int)(256*Math.random());
            int g = (int)(256*Math.random());
            int b = (int)(256*Math.random());
            grid[i][j] = new Color(r,g,b);
         }
      forceRedraw();
   }
   
   
   /**
    *   Clear the mosaic by setting all the colors to null.
    */
   public void clear() {
      fill(null);
   }
   

   /**
    * Returns the current value of the autopaint property.
    * @see #setAutopaint(boolean)
    */
   public boolean getAutopaint() {
      return autopaint;
   }

   /**
    * Sets the value of the autopaint property.  When this property is true,
    * then every call to one of the setColor methods automatically results in
    * repainting that square on the screen.  When it is desired to avoid this
    * immediate repaint -- for example, during a long sequence of setColors
    * that will all show up at once -- then the value of the autopaint property
    * can be set to false.  When the value is false, color changes are recorded
    * in the data for the mosaic but are not made on the screen.  When the
    * autopaint property is reset to true, the changes are applied and the
    * entire mosaic is repainted.  The default value of this property is
    * true.  
    * <p>Note that clearing or filling the mosaic will cause an immediate 
    * screen update, even if atutopaint is false.
    */
   public void setAutopaint(boolean autopaint) {
      if (this.autopaint == autopaint)
         return;
      this.autopaint = autopaint;
      if (autopaint) 
         forceRedraw();
   }

   /**
    * This method can be called to force redrawing of the entire mosaic.  The only
    * time it might be necessary for users of this class to recall this method is
    * while the autopaint property is set to false, and it is desired to show
    * all the changes that have been made to the mosaic, without resetting
    * the autopaint property to true.
    *@see #setAutopaint(boolean)
    */
   final public void forceRedraw() {
      needsRedraw = true;
      repaint();
   }

   /**
    *   Return an object that contains the color data that
    *   is needed to redraw the mosaic.  This includes the 
    *   defaultColor, the groutingColor, the number of rows and
    *   columns, the color of each rectangle, and the
    *   value of alwaysDrawGrouting.
    */
   public Object copyColorData() {
      // Note:  This is a fudge.  The data about defaultColor,
      // groutingColor, and alwaysDrawGrouting is added to the
      // last row of the grid.  If alwaysDrawGrouting is true,
      // this is recorded by adding an extra empty space to
      // that row.
      Color[][] copy = new Color[rows][columns];
      // Replace the last row with a longer row.
      if (alwaysDrawGrouting)
         copy[rows-1] = new Color[columns+3];
      else
         copy[rows-1] = new Color[columns+2];
      for (int r = 0; r < rows; r++)
         for (int c = 0; c < columns; c++)
            copy[r][c] = grid[r][c];
      copy[rows-1][columns] = defaultColor;
      copy[rows-1][columns+1] = groutingColor;
      return copy;
   }
   
   
   /**
    *  The parameter to this method should be an Object that
    *  was created by the copyColorData() method.  This method
    *  will restore the data in the object to the grid.  This
    *  can change the size of the grid, the colors in the grid,
    *  the defaultColor, the groutingColor, and the value of
    *  alwaysDrawGrouting.  If the object is of the proper
    *  form, then the return value is true.  If not, the return
    *  value is false and no changes are made to the current data.
    */
   public boolean restoreColorData(Object data) {
      if (data == null || !(data instanceof Color[][]))
         return false;
      Color[][] newGrid = (Color[][])data;
      int newRows = newGrid.length;
      if (newRows == 0 || newGrid[0].length == 0)
         return false;
      int newColumns = newGrid[0].length;
      for (int r = 1; r < newRows-1; r++)
         if (newGrid[r].length != newColumns)
            return false;
      if (newGrid[newRows-1].length != newColumns+2
            && newGrid[newRows-1].length != newColumns+3)
         return false;
      if (newGrid[newRows-1][newColumns] == null)
         return false;
      rows = newRows;
      columns = newColumns;
      grid = new Color[rows][columns];
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            grid[i][j] = newGrid[i][j];
      defaultColor = newGrid[newRows-1][newColumns];
      setBackground(defaultColor);
      groutingColor = newGrid[newRows-1][newColumns+1];
      alwaysDrawGrouting = newGrid[newRows-1].length == 3;
      forceRedraw();
      return true;
   }
   
   /**
    * Given an x-coordinate of a pixel in the MosaicPanel, this method returns
    * the row number of the mosaic rectangle that contains that pixel.  If
    * the x-coordinate does not lie within the bounds of the mosaic, the return
    * value is -1 or is equal to the number of columns, depending on whether
    * x is to the left or to the right of the mosaic.
    */
   public int xCoordToColumnNumber(int x) {
      Insets insets = getInsets();
      if (x < insets.left)
         return -1;
      double colWidth = (double)(getWidth()-insets.left-insets.right) / columns;
      int col = (int)( (x-insets.left) / colWidth);
      if (col >= columns)
         return columns;
      else
         return col;
   }
   
   /**
    * Given a y-coordinate of a pixel in the MosaicPanel, this method returns
    * the column number of the mosaic rectangle that contains that pixel.  If
    * the y-coordinate does not lie within the bounds of the mosaic, the return
    * value is -1  or is equal to the number of rows, depending on whether
    * y is above or below the mosaic.
    */
   public int yCoordToRowNumber(int y) {
      Insets insets = getInsets();
      if (y < insets.top)
         return -1;
      double rowHeight = (double)(getHeight()-insets.top-insets.bottom) / rows;
      int row = (int)( (y-insets.top) / rowHeight);
      if (row >= rows)
         return rows;
      else
         return row;
   }
   
   /**
    *  Returns the BufferedImage that contains the actual image of the mosaic.
    *  If this is called before the mosaic has been drawn on screen, the return value will be null.
    */
   public BufferedImage getImage() {
      return OSI;
   }
   
   //--------------- implementation details ------------------------
   //---------- (routines called internally or by the system) ------
   
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if ( (OSI == null) || OSI.getWidth() != getWidth() || OSI.getHeight() != getHeight() ) {
         OSI = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
         needsRedraw = true;
      }
      if (needsRedraw) {
         Graphics OSG = OSI.getGraphics();
         for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
               drawSquare(OSG,r,c,false);
         OSG.dispose();
         needsRedraw = false;
      }
      g.drawImage(OSI,0,0,null);
   }
   
   private void drawSquare(Graphics g, int row, int col, boolean callRepaint) {
      // Draw one of the rectangles in a specified graphics 
      // context.  g must be non-null and (row,col) must be
      // in the grid.
      if (callRepaint && !autopaint)
         return;
      Insets insets = getInsets();
      double rowHeight = (double)(getHeight()-insets.left-insets.right) / rows;
      double colWidth = (double)(getWidth()-insets.top-insets.bottom) / columns;
      int xOffset = insets.left;
      int yOffset = insets.top; 
      int y = yOffset + (int)Math.round(rowHeight*row);
      int h = Math.max(1, (int)Math.round(rowHeight*(row+1))+yOffset - y);
      int x = xOffset + (int)Math.round(colWidth*col);
      int w = Math.max(1, (int)Math.round(colWidth*(col+1))+xOffset - x);
      Color c = grid[row][col];
      g.setColor( (c == null)? defaultColor : c );
      if (groutingColor == null || (c == null && !alwaysDrawGrouting)) {
         if (c == null)
            g.fillRect(x,y,w,h);
         else
            g.fill3DRect(x,y,w,h,true);
      }
      else {
         if (c == null)
            g.fillRect(x+1,y+1,w-2,h-2);
         else
            g.fill3DRect(x+1,y+1,w-2,h-2,true);
         g.setColor(groutingColor);
         g.drawRect(x,y,w-1,h-1);
      }
      if (callRepaint)
         repaint(x,y,w,h);
   }
   
   private void drawSquare(int row, int col) {
      // Draw a specified rectangle directly on the applet in
      // the off-screen image, and call repaint to copy that
      // square to the screen.  (row,col) must be
      // within the grid.
      if (OSI == null)
         repaint();
      else {
         Graphics g = OSI.getGraphics();
         drawSquare(g,row,col,true);
         g.dispose();
      }
   }
   
   
} // end class MosaicPanel
