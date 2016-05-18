
import java.awt.*;

public class RandomBrighten extends java.applet.Applet implements Runnable {

         /*
             Applet shows a mosaic of little colored
             squares.  Initially, all the squares are black.
             A "disturbance" moves randomly around
             in the window.  Each time it visits a square,
             the square gets a little brighter.  After 7500
             steps, the squares are reset to black.  Depends 
             on class MosaicCanvas.
         */
                
   int ROWS = 40;        // number of rows of squares; can be reset by applet param named "rows"
   int COLUMNS = 40;     // number of columns of squares; can be reset by applet param named "columns"
   int MAX_STEPS = 7500; // after the disturbance moves this many times, the applet is cleared and
                         //    the process starts again from a blank screen. 

   int currentRow;       // row currently containing "disturbance"
   int currentColumn;    // column currently containing "disturbance"
   MosaicCanvas mosaic;  // the actual mosaic of colored squares

   Thread runner = null; // thread for running the moving disturbance animation
   boolean suspended;    // set to true when applet is suspended
   boolean finished;     // set to true when applet is destroyed
   
   public void init() {
      setLayout(new BorderLayout());
      String param;
      param = getParameter("rows");
      if (param != null) {
         try {
            ROWS = Integer.parseInt(param);
         }
         catch (NumberFormatException e) {
         }
      }
      param = getParameter("columns");
      if (param != null) {
         try {
            COLUMNS = Integer.parseInt(param);
         }
         catch (NumberFormatException e) {
         }
      }
      param = getParameter("max_steps");
      if (param != null) {
         try {
            MAX_STEPS = Integer.parseInt(param);
         }
         catch (NumberFormatException e) {
         }
      }
      mosaic = new MosaicCanvas(ROWS,COLUMNS);
      add("Center",mosaic);
   }
   
   synchronized public void start() {
      suspended = false;
      if (runner == null || !runner.isAlive()) {
         runner = new Thread(this);
         runner.start();
      }
      else
         notify();
   }
   
   synchronized public void stop() {
      suspended = true;
   }
   
  synchronized public void destroy() {
      finished = true;
   }
   
   public void run() {
      currentRow = ROWS / 2;
      currentColumn = COLUMNS / 2;
      mosaic.fill(Color.black);
      int ct = 0;
      while (true) {
         synchronized(this) {
            while (suspended && ! finished)
               try {
                  wait();
               }
               catch (InterruptedException e) {
               }
            if (finished)
               break;
         }
         brighten(currentRow,currentColumn);
         randomMove();
         mosaic.delay(20);
         ct++;
         if (ct == MAX_STEPS) {
            mosaic.delay(3000);
            currentRow = ROWS / 2;
            currentColumn = COLUMNS / 2;
            mosaic.fill(Color.black);
            ct = 0;
         }
      }
   }
   
   
   void brighten(int row, int col) {
          // Increases the level of red in the current square.
          // (setting green and blue levels to zero).
       int level = mosaic.getGreen(currentRow,currentColumn);
       level += 25;
       if (level > 255)
          level = 255;
       mosaic.setColor(row,col,0,level,0);
   }
    

   void randomMove() {
        // randomly move the disturbance in one of
        // four possible directions: up, down, left, or right;
        // if this moves the disturbance outside the window,
        // then move it to the opposite edge of the window.
        int directionNum = (int)(4*Math.random());
             // directionNum is randomly set to 0, 1, 2, or 3
        switch (directionNum) {
           case 0:  // move up 
              currentRow--;
              if (currentRow < 0)
                  currentRow = ROWS - 1;
              break;
           case 1:  // move right
              currentColumn++;
              if (currentColumn >= COLUMNS)
                currentColumn = 0;
              break; 
           case 2:  // move down
              currentRow ++;
              if (currentRow >= ROWS)
                  currentRow = 0;
              break;
           case 3:  
              currentColumn--;
              if (currentColumn < 0)
                  currentColumn = COLUMNS - 1;
              break; 
        }
    }  // end of randomMove()
    
} // end of class
