
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This applet demonstrates recursion by counting the number of
 * squares in a "blob".  The squares are arranged in a grid,
 * and each position in the grid can be either empty or filled.
 * A blob is defined to be a filled square and any square that
 * can be reached from that square by moving horizontally or
 * vertically to other filled squares.  This applet fills
 * the grid randomly.  If the user clicks on a filled square,
 * all the squares in the blob that contains that square are
 * colored red, and the number of squares in the blob is
 * reported.  The applet can also count and report the number
 * of blobs.  When the user clicks a "New Blobs" button,
 * the grid is randomly re-filled.
 * 
 * The applet should be at least 350-by-350 pixels.  Larger sizes 
 * will also work OK.  The number of squares in the grid depends
 * on the size of the applet.
 *
 * This class includes a main() routine, so that it can be run
 * as a stand-alone application.  The application version just
 * shows the same panel that is shown in the applet version.
 */
public class Blobs extends JApplet {
   
   
   /**
    * The init() method just sets the content pane of the applet
    * to be any panel of type BlobsPanel, which is a static nested
    * class in this class.
    */
   public void init() {
      setContentPane(new BlobsPanel(getWidth(),getHeight()));
   }
   
   
   /**
    * This main routine opens a window that displays a BlobsPanel,
    * which is a static nested class in this class.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("Recursive Blob Counting");
      window.setContentPane( new BlobsPanel(454,400) );
      window.pack();
      window.setResizable(false);
      window.setLocation(150,100);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.setVisible(true);
   }

   
   /**
    * This class defines the content pane of the applet (or the content of
    * the window when the class is run as a standalone application.  This class
    * does all the work of the program.
    */
   public static class BlobsPanel extends JPanel implements ActionListener, MouseListener {

      final static int SQUARE_SIZE = 9;  // Size of one square in the grid.

      JLabel message;       // For displaying information to the user.

      JComboBox percentFill;  // When the user clicks the "New Blobs" button
                              // to randomly fill the grid, this menu controls
                              // the probability that a given square in the grid
                              // is filled.

      int rows;     // Number of rows in the grid.  This depend on the size of the panel.
      int columns;  // Number of columns in the grid.  This depend on the size of the applet.

      boolean[][] filled;  // filled[r][c] is true if the square at row r, column c is filled.

      boolean[][] visited; // visited[r][c] is true if the square at row r, column c has
                           //   has already been visited by the getBlobSize() method.


      /**
       * Construct the panel.  Install a new content pane. Use a null layout and 
       * set the bounds of the components in the panel directly.  The panel
       *  listens for mouse clicks on itself.
       * @param width the width of the panel
       * @param height the height of the panel.  The width and height must be
       * known before the panel can be constructed, since they are used in the
       * constructor to lay out the panel and to decide on the number of rows
       * and columns in the grid.  The width and height are also used to set
       * a preferred size for the panel; this preferred size is used in the
       * application version when the window is "packed."
       */
      public BlobsPanel(int width, int height) {

         setLayout(null);
         setBackground(new Color(220,220,255));
         addMouseListener(this);
         setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.blue));
         setPreferredSize( new Dimension(width,height) );

         /* Determine the number of rows and columns and create the
               filled and visited arrays.  Fill the squares at random. */

         rows = (height - 120) / SQUARE_SIZE;
         columns = (width - 20) / SQUARE_SIZE;

         filled = new boolean[rows][columns];
         visited = new boolean[rows][columns];

         for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
               filled[r][c] = (Math.random() < 0.3);

         /* Create the components. */

         message = new JLabel("Click a square to get the blob size.", JLabel.CENTER);
         message.setForeground(Color.blue);
         message.setFont(new Font("Helvetica",Font.PLAIN,14));

         percentFill = new JComboBox();
         percentFill.addItem("10% fill");
         percentFill.addItem("20% fill");
         percentFill.addItem("30% fill");
         percentFill.addItem("40% fill");
         percentFill.addItem("50% fill");
         percentFill.addItem("60% fill");
         percentFill.addItem("70% fill");
         percentFill.addItem("80% fill");
         percentFill.addItem("90% fill");
         percentFill.setBackground(Color.white);
         percentFill.setSelectedIndex(2);

         JButton newButton = new JButton("New Blobs");
         newButton.addActionListener(this);
         newButton.setBackground(Color.lightGray);

         JButton countButton = new JButton("Count the Blobs");
         countButton.addActionListener(this);
         countButton.setBackground(Color.lightGray);

         /* Add the components to the panel and set their sizes and positions. */

         add(message);
         add(newButton);
         add(percentFill);
         add(countButton);

         message.setBounds(15, height-100, width-30, 23);
         countButton.setBounds(15, height-70, width-30, 28);
         newButton.setBounds(15, height-35, (width-40)/2, 28);
         percentFill.setBounds(width/2 + 5, height-35, (width-40)/2, 28);

      } // end constructor


      /**
       * When the user clicks a button, call the appropriate method.
       */
      public void actionPerformed(ActionEvent evt) {
         String cmd = evt.getActionCommand();
         if (cmd.equals("New Blobs"))
            fillGrid();
         else if (cmd.equals("Count the Blobs"))
            countBlobs();
      }


      /**
       *  When the user clicks the "New Blobs" button, fill the grid of squares
       *  randomly.  The probability that a given square is filled is given by
       *  the percentFill Choice menu.  The probabilities corresponding to the
       *  items in that menu are 0.1, 0.2,... 0.9.  The visited array is cleared
       *  so there won't be any red-colored squares in the grid.
       */
      private void fillGrid() {
         double probability = (percentFill.getSelectedIndex() + 1) / 10.0;
         for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++) {
               filled[r][c] = (Math.random() < probability);
               visited[r][c] = false;
            }
         message.setText("Click a square to get the blob size.");
         repaint();
      }


      /**
       * When the use clicks the "Count the Blobs" button, find the number
       * of blobs in the grid and report the number in the message Label.
       */
      private void countBlobs() {

         int count = 0; // Number of blobs.

         /* First clear out the visited array. The getBlobSize() method will
               mark every filled square that it finds by setting the corresponding
               element of the array to true.  Once a square has been marked as
               visited, it will stay marked until all the blobs have been counted.
               This will prevent the same blob from being counted more than once. */

         for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
               visited[r][c] = false;

         /* For each position in the grid, call getBlobSize() to get the size
            of the blob at that position.  If the size is not zero, count a blob.
               Note that if we come to a position that was part of a previously
               counted square, getBlobSize() will return 0 and the blob will not
               be counted again. */

         for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++) {
               if (getBlobSize(r,c) > 0)
                  count++;
            }

         repaint();  // Note that all the filled squares will be red!

         message.setText("The number of blobs is " + count);

      } // end countBlobs()


      /**
       * Counts the squares in the blob at position (r,c) in the
        * grid.  Squares are only counted if they are filled and
       * unvisited.  If this routine is called for a position that
         * has been visited, the return value will be zero.
       */
      private int getBlobSize(int r, int c) {
         if (r < 0 || r >= rows || c < 0 || c >= columns) {
               // This position is not in the grid, so there is
               // no blob at this position.
            return 0;
         }
         if (filled[r][c] == false || visited[r][c] == true) {
               // This square is not part of a blob, or else it has
               // already been counted, so return zero.
            return 0;
         }
         visited[r][c] = true;   // Mark the square as visited so that
                                 //    we won't count it again during the
                                 //    following recursive calls to this method.
         int size = 1;  // Count the square at this position, then count the
                        //   the blobs that are connected to this square
                        //    horizontally or vertically.
         size += getBlobSize(r-1,c);
         size += getBlobSize(r+1,c);
         size += getBlobSize(r,c-1);
         size += getBlobSize(r,c+1);
         return size;
      }  // end getBlobSize()


      /**
       * The user has clicked the mouse on the panel.  If the
       * user has clicked on a position in the grid, count
       * the number of squares in the blob at that position.
       */
      public void mousePressed(MouseEvent evt) {
         int row = (evt.getY() - 10) / SQUARE_SIZE;
         int col = (evt.getX() - 10) / SQUARE_SIZE;
         if (row < 0 || row >= rows || col < 0 || col >= columns) {
            message.setText("Please click on a square!");
            return;
         }
         for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
               visited[r][c] = false;  // Clear visited array before counting.
         int size = getBlobSize(row,col);
         if (size == 0)
            message.setText("There is no blob at (" + row + "," + col + ").");
         else if (size == 1)
            message.setText("Blob at (" + row + "," + col + ") contains 1 square.");
         else
            message.setText("Blob at (" + row + "," + col + ") contains " + size + " squares.");
         repaint();
      }


      public void mouseReleased(MouseEvent e) { }  // Methods required by MouseListener interface
      public void mouseClicked(MouseEvent e) { }
      public void mouseEntered(MouseEvent e) { }
      public void mouseExited(MouseEvent e) { }


      /**
       * Paint the panel, showing the grid of squares.  (The other components 
       * in the panel draw themselves.)
       */
      public void paintComponent(Graphics g) {

         super.paintComponent(g);  // Fill with background color.

         /* Fill the area occupied by the grid with white, then draw
               black lines around this area and between the squares of
               the grid. */

         g.setColor(Color.white);
         g.fillRect(10, 10, columns*SQUARE_SIZE, rows*SQUARE_SIZE);

         g.setColor(Color.black);
         for (int i = 0; i <= rows; i++)
            g.drawLine(10, 10 + i*SQUARE_SIZE, columns*SQUARE_SIZE + 10, 10 + i*SQUARE_SIZE);
         for (int i = 0; i <= columns; i++)
            g.drawLine(10 + i*SQUARE_SIZE, 10, 10 + i*SQUARE_SIZE, rows*SQUARE_SIZE + 10);

         /* Fill "visited" squares with red and "filled" squares with gray.
               Other squares remain white.  */

         for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++) {
               if (visited[r][c]) {
                  g.setColor(Color.red);
                  g.fillRect(11 + c*SQUARE_SIZE, 11 + r*SQUARE_SIZE, SQUARE_SIZE - 1, SQUARE_SIZE - 1);
               }
               else if (filled[r][c]) {
                  g.setColor(Color.gray);
                  g.fillRect(11 + c*SQUARE_SIZE, 11 + r*SQUARE_SIZE, SQUARE_SIZE - 1, SQUARE_SIZE - 1);
               }
            }

      } // end paintComponent();

   } // end nested class BlobsPanel
   
} // end class Blobs
