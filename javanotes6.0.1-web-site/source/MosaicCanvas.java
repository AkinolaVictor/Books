/**
 *  This MosaicCanvas is for use with MosaicFrame;
 *  see that class for details.
 */

import java.awt.*;

public class MosaicCanvas extends Canvas {

   protected int rows, columns;
   protected int blockWidth, blockHeight;

   protected Color defaultColor = Color.BLACK;

   private Color[][] grid;

   private Image OSC;
   private Graphics OSG;
   
   private boolean redrawCanvas;

   public MosaicCanvas(int rows, int columns) {
      this(rows, columns, 0, 0);
   }

   public MosaicCanvas(int rows, int columns, int blockWidth, int blockHeight) {
      this.rows = rows;
      this.columns = columns;
      this.blockWidth = blockWidth;
      this.blockHeight = blockHeight;
      grid = new Color[rows][columns];
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            grid[i][j] = defaultColor;
      setBackground(defaultColor);
   }

   public Dimension preferredSize() {
      if (blockWidth <= 0)
         blockWidth = 10;
      if (blockHeight <= 0)
         blockHeight = 10;
      return new Dimension(columns * blockWidth, rows * blockHeight);
   }

   public synchronized void paint(Graphics g) {
      if (OSC == null) {
         OSC = createImage(getWidth(), getHeight());
         OSG = OSC.getGraphics();
         redrawCanvas = true;
      }
      if ( redrawCanvas ) {
         double rowHeight = (double) getHeight() / rows;
         double colWidth = (double) getWidth() / columns;
         for (int i = 0; i < rows; i++) {
            int y = (int) Math.round(rowHeight * i);
            int h = (int) Math.round(rowHeight * (i + 1)) - y;
            for (int j = 0; j < columns; j++) {
               int x = (int) Math.round(colWidth * j);
               int w = (int) Math.round(colWidth * (j + 1)) - x;
               OSG.setColor(grid[i][j]);
               OSG.fillRect(x, y, w, h);
            }
         }
      }
      g.drawImage(OSC, 0, 0, getWidth(), getHeight(), null);
   }

   public synchronized void drawSquare(int row, int col) {
      double rowHeight = (double) getHeight() / rows;
      double colWidth = (double) getWidth() / columns;
      int y = (int) Math.round(rowHeight * row);
      int h = (int) Math.round(rowHeight * (row + 1)) - y;
      int x = (int) Math.round(colWidth * col);
      int w = (int) Math.round(colWidth * (col + 1)) - x;
      if (OSC == null)
         repaint();
      else {
         OSG.setColor(grid[row][col]);
         OSG.fillRect(x, y, w, h);
         repaint(x,y,w,h);
      }
   }

   public void update(Graphics g) {
      paint(g);
   }

   public Color getColor(int row, int col) {
      if (row >= 0 && row < rows && col >= 0 && col < columns)
         return grid[row][col];
      else
         throw new IllegalArgumentException("Row or column number out of range");
   }

   public int getRed(int row, int col) {
      return getColor(row, col).getRed();
   }

   public int getGreen(int row, int col) {
      return getColor(row, col).getGreen();
   }

   public int getBlue(int row, int col) {
      return getColor(row, col).getBlue();
   }

   public void setColor(int row, int col, Color c) {
      if (c == null)
         c = defaultColor;
      if (row >= 0 && row < rows && col >= 0 && col < columns && c != null) {
         grid[row][col] = c;
         drawSquare(row, col);
      }
      else
         throw new IllegalArgumentException("Row or column number out of range");
   }

   public void setColor(int row, int col, int red, int green, int blue) {
      if (row >= 0 && row < rows && col >= 0 && col < columns) {
         red = (red < 0) ? 0 : ((red > 255) ? 255 : red);
         green = (green < 0) ? 0 : ((green > 255) ? 255 : green);
         blue = (blue < 0) ? 0 : ((blue > 255) ? 255 : blue);
         grid[row][col] = new Color(red, green, blue);
         drawSquare(row, col);
      }
      else
         throw new IllegalArgumentException("Row or column number out of range");
   }

   public void fill(Color c) {
      if (c == null)
         c = defaultColor;
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            grid[i][j] = c;
      redrawCanvas = true;
      repaint();
   }

   public void fill(int red, int green, int blue) {
      red = (red < 0) ? 0 : ((red > 255) ? 255 : red);
      green = (green < 0) ? 0 : ((green > 255) ? 255 : green);
      blue = (blue < 0) ? 0 : ((blue > 255) ? 255 : blue);
      fill(new Color(red, green, blue));
   }

   public void fillRandomly() {
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++) {
            int r = (int) (256 * Math.random());
            int g = (int) (256 * Math.random());
            int b = (int) (256 * Math.random());
            grid[i][j] = new Color(r, g, b);
         }
      redrawCanvas = true;
      repaint();
   }

   public void delay(int milliseconds) {
      if (milliseconds > 0) {
         try {
            Thread.sleep(milliseconds);
         } 
         catch (InterruptedException e) {
         }
      }
   }

}