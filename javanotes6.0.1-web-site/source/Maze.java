/*********************************************************************************

   Creates a random maze, then solves it by finding a path from the
   upper left corner to the lower right corner.  (After doing
   one maze, it waits a while then starts over by creating a
   new random maze.)
   
   This applet is customizable using the following parameters in
   its applet tag:
   
        name            default value    meaning
       ------           ---------------  --------------------------------------
        rows                     21         number of rows in maze (must be odd)
        columns                  21         number of columns in maze (must be odd)
        border                    0         width of colored border around maze
                                              (extra space after making equal-sized
                                               rows and columns is also part of the border)
        sleepTime              5000         pause, in milliseconds between
                                               solving one maze and creating
                                               another
        speed                     3         integer between 1 and 5 specifying
                                               how fast the a maze is created
                                               and solved.  (1 is fastest.)
        wallColor             black  
        emptyColor      128 128 255
        pathColor           200 0 0      
        visitedColor  same as empty
        borderColor           white
        
    Parameter names are case sensitive.  Parameters that specify
    colors can be given by one of the predefined color names
    (black, white, red, blue, green, cyan, magenta, yellow, pink
    orange, gray, lightGray, darkGray) or as an RGB color specified
    by three integers between 0 and 255.  Color names are NOT
    case sensitive.

BY:  David Eck
     Department of Mathematics and Computer Science
     Hobart and William Smith Colleges
     Geneva, NY   14456
     
     E-mail:  eck@hws.edu
     
     Modified March 7, 2000, to avoid using Thread.suspend, Thread.resume,
     and Thread.stop.
     
     Modified November 2006 to avoid holding on a graphics context for
     too long and to replace some deprecated methods with modern alternatives.
     

NOTE:  YOU CAN DO ANYTHING YOU WANT WITH THIS CODE AND APPLET, EXCEPT
       CLAIM CREDIT FOR THEM (such as by trying to copyright the code
       your self).

**************************************************************************/


import java.awt.*;

public class Maze extends java.applet.Applet implements Runnable {

    int[][] maze;  // Description of state of maze.  The value of maze[i][j]
                   // is one of the constants wallCode, pathcode, emptyCode,
                   // or visitedCode.  (Value can also be negative, temporarily,
                   // inside createMaze().)
                   //    A maze is made up of walls and corridors.  maze[i][j]
                   // is either part of a wall or part of a corridor.  A cell
                   // cell that is part of a corridor is represented by pathCode
                   // if it is part of the current path through the maze, by
                   // visitedCode if it has already been explored without finding
                   // a solution, and by emptyCode if it has not yet been explored.

    final static int backgroundCode = 0;
    final static int wallCode = 1;
    final static int pathCode = 2;
    final static int emptyCode = 3;
    final static int visitedCode = 4;
    
       // the next six items are set up in init(), and can be specified 
       // using applet parameters

    Color[] color = new Color[5];  // colors associated with the preceding 5 constants;
    int rows = 21;          // number of rows of cells in maze, including a wall around edges
    int columns = 21;       // number of columns of cells in maze, including a wall around edges
    int border = 0;         // minimum number of pixels between maze and edge of applet
    int sleepTime = 5000;   // wait time after solving one maze before making another
    int speedSleep = 50;    // short delay between steps in making and solving maze
    
    Thread mazeThread;   // thread for creating and solving maze
    
    int width = -1;   // width of applet, to be set by checkSize()
    int height = -1;  // height of applet, to be set by checkSize()

    int totalWidth;   // width of applet, minus border area (set in checkSize())
    int totalHeight;  // height of applet, minus border area (set in checkSize())
    int left;         // left edge of maze, allowing for border (set in checkSize())
    int top;          // top edge of maze, allowing for border (set in checkSize())
    
    boolean mazeExists = false;  // set to true when maze[][] is valid; used in
                                 // redrawMaze(); set to true in createMaze(), and
                                 // reset to false in run()


    int status = 0;  // A variable used by the applet to control the mazeThread.

    final static int GO = 0,         // Some constants to be uses as values for status.
                     SUSPEND = 1,
                     TERMINATE = 2;

    Integer getIntParam(String paramName) {
          // Utility routine for reading an applet param which is an integer.
          // Returns null if there is no such param, or if the value is not
          // a legal integer.
       String param = getParameter(paramName);
       if (param == null)
          return null;
       int i;
       try {
          i = Integer.parseInt(param);
       }
       catch (NumberFormatException e) {
          return null;
       }
       return new Integer(i);
    }
    
    Color getColorParam(String paramName) {
          // Utility routine for reading an applet param which is a color.
          // Returns null if there is no such param, or if the value is not
          // a legal color.  Colors can be specified as three integers,
          // separated by spaces, giving RGB components in the range 0 to 255;
          // the standard Java color names are also acceptable.
       String param = getParameter(paramName);
       if (param == null || param.length() == 0)
          return null;
       if (Character.isDigit(param.charAt(0))) {  // try to parse RGB color
          int r=0,g=0,b=0;
          int pos=0;
          int d=0;
          int len=param.length();
          while (pos < len && Character.isDigit(param.charAt(pos)) && r < 255) {
              d = Character.digit(param.charAt(pos),10);
              r = 10*r + d;
              pos++;
          }
          if (r > 255)
             return null;
          while (pos < len && !Character.isDigit(param.charAt(pos)))
             pos++;
          if (pos >= len)
             return null;
          while (pos < len && Character.isDigit(param.charAt(pos)) && g < 255) {
              d = Character.digit(param.charAt(pos),10);
              g = 10*g + d;
              pos++;
          }
          if (g > 255)
             return null;
          while (pos < len && !Character.isDigit(param.charAt(pos)))
             pos++;
          if (pos >= len)
             return null;
          while (pos < len && Character.isDigit(param.charAt(pos)) && b < 255) {
              d = Character.digit(param.charAt(pos),10);
              b = 10*b + d;
              pos++;
          }
          if (b > 255)
             return null;
          return new Color(r,g,b);          
       }
       if (param.equalsIgnoreCase("black"))
          return Color.black;
       if (param.equalsIgnoreCase("white"))
          return Color.white;
       if (param.equalsIgnoreCase("red"))
          return Color.red;
       if (param.equalsIgnoreCase("green"))
          return Color.green;
       if (param.equalsIgnoreCase("blue"))
          return Color.blue;
       if (param.equalsIgnoreCase("yellow"))
          return Color.yellow;
       if (param.equalsIgnoreCase("cyan"))
          return Color.cyan;
       if (param.equalsIgnoreCase("magenta"))
          return Color.magenta;
       if (param.equalsIgnoreCase("pink"))
          return Color.pink;
       if (param.equalsIgnoreCase("orange"))
          return Color.orange;
       if (param.equalsIgnoreCase("gray"))
          return Color.gray;
       if (param.equalsIgnoreCase("darkgray"))
          return Color.darkGray;
       if (param.equalsIgnoreCase("lightgray"))
          return Color.lightGray;
       return null;  // param is not a legal color
    }

    public void init() {
         Integer parm;
         parm = getIntParam("rows");
         if (parm != null && parm.intValue() > 4 && parm.intValue() <= 500) {
            rows = parm.intValue();
            if (rows % 2 == 0)
               rows++;
         }
         parm = getIntParam("columns");
         if (parm != null && parm.intValue() > 4 && parm.intValue() <= 500) {
            columns = parm.intValue();
            if (columns % 2 == 0)
               columns++;
         }
         parm = getIntParam("border");
         if (parm != null && parm.intValue() > 0 && parm.intValue() <= 100)
            border = parm.intValue();
         parm = getIntParam("sleepTime");
         if (parm != null && parm.intValue() > 0)
            sleepTime = parm.intValue();
         parm = getIntParam("speed");
         if (parm != null && parm.intValue() > 0 && parm.intValue() < 6)
            switch (parm.intValue()) {
               case 1: speedSleep = 1; break;
               case 2: speedSleep = 25; break;
               case 3: speedSleep = 50; break;
               case 4: speedSleep = 100; break;
               case 5: speedSleep = 200; break;
            }
         color[wallCode] = getColorParam("wallColor");
         if (color[wallCode] == null) 
            color[wallCode] = Color.black;
         color[pathCode] = getColorParam("pathColor");
         if (color[pathCode] == null)
            color[pathCode] = new Color(200,0,0);
         color[emptyCode] = getColorParam("emptyColor");
         if (color[emptyCode] == null)
            color[emptyCode] = new Color(128,128,255);
         color[backgroundCode] = getColorParam("borderColor");
         if (color[backgroundCode] == null)
            color[backgroundCode] = Color.white;
         color[visitedCode] = getColorParam("visitedColor");
         if (color[visitedCode] == null)
            color[visitedCode] = color[emptyCode];
         setBackground(color[backgroundCode]);
    }
    
    void checkSize() {
          // Called every time something is about to be drawn to
          // check the applet size and adjust variables that depend
          // on the size. 
       if (getWidth() != width || getHeight() != height) {
          width  = getWidth();
          height = getHeight();
          int w = (width - 2*border) / columns;
          int h = (height - 2*border) / rows;
          left = (width - w*columns) / 2;
          top = (height - h*rows) / 2;
          totalWidth = w*columns;
          totalHeight = h*rows; 
       }
    }

    synchronized public void start() {
        status = GO;
        if (mazeThread == null || ! mazeThread.isAlive()) {
          mazeThread = new Thread(this);
          mazeThread.start();
        }
        else
           notify();
    }

    synchronized public void stop() {
        if (mazeThread != null) {
            status = SUSPEND;
            notify();
        }
    }
    
    synchronized public void destroy() {
       if (mazeThread != null) {
          status = TERMINATE;
          notify();
       }
    }
    
    synchronized int checkStatus() {
       while (status == SUSPEND) {
          try { wait(); }
          catch (InterruptedException e) { }
       }
       return status;
    }

    public void paint(Graphics g) {
        checkSize();
        redrawMaze(g);
    }
    
    public void update(Graphics g) {  // don't bother filling with background color
        paint(g);                     //   because redrawMaze() does that anyway
    }
    
    synchronized void redrawMaze(Graphics g) {
          // draws the entire maze
        g.setColor(color[backgroundCode]);
        g.fillRect(0,0,width,height);
        if (mazeExists) {
           int w = totalWidth / columns;  // width of each cell
           int h = totalHeight / rows;    // height of each cell
           for (int j=0; j<columns; j++)
               for (int i=0; i<rows; i++) {
                   if (maze[i][j] < 0)
                      g.setColor(color[emptyCode]);
                   else
                      g.setColor(color[maze[i][j]]);
                   g.fillRect( (j * w) + left, (i * h) + top, w, h );
               }
         }
    }

    synchronized void putSquare(int row, int col, int colorNum) {
           // draw one cell of the maze, to the graphics context "me"
        checkSize();
        int w = totalWidth / columns;  // width of each cell
        int h = totalHeight / rows;    // height of each cell
        Graphics me = getGraphics();
        me.setColor(color[colorNum]);
        me.fillRect( (col * w) + left, (row * h) + top, w, h );
        me.dispose();
    }

    public void run() {
           // run method for thread repeatedly makes a maze and then solves it
       try { Thread.sleep(2000); } // wait a bit before starting
       catch (InterruptedException e) { }
       while (true) {
          if (checkStatus() == TERMINATE)
             break;
          makeMaze();
          if (checkStatus() == TERMINATE)
             break;
          solveMaze(1,1);
          if (checkStatus() == TERMINATE)
             break;
          synchronized(this) {
              try { wait(sleepTime); }
              catch (InterruptedException e) { }
          }
          if (checkStatus() == TERMINATE)
             break;
          mazeExists = false;
          checkSize();
          Graphics me = getGraphics();
          redrawMaze(me);   // erase old maze
          me.dispose();
       }
    }

    void makeMaze() {
            // Create a random maze.  The strategy is to start with
            // a grid of disconnected "rooms" separated by walls.
            // then look at each of the separating walls, in a random
            // order.  If tearing down a wall would not create a loop
            // in the maze, then tear it down.  Otherwise, leave it in place.
        if (maze == null)
           maze = new int[rows][columns];
        int i,j;
        int emptyCt = 0; // number of rooms
        int wallCt = 0;  // number of walls
        int[] wallrow = new int[(rows*columns)/2];  // position of walls between rooms
        int[] wallcol = new int[(rows*columns)/2];
        for (i = 0; i<rows; i++)  // start with everything being a wall
            for (j = 0; j < columns; j++)
                maze[i][j] = wallCode;
        for (i = 1; i<rows-1; i += 2)  // make a grid of empty rooms
            for (j = 1; j<columns-1; j += 2) {
                emptyCt++;
                maze[i][j] = -emptyCt;  // each room is represented by a different negative number
                if (i < rows-2) {  // record info about wall below this room
                    wallrow[wallCt] = i+1;
                    wallcol[wallCt] = j;
                    wallCt++;
                }
                if (j < columns-2) {  // record info about wall to right of this room
                    wallrow[wallCt] = i;
                    wallcol[wallCt] = j+1;
                    wallCt++;
                }
             }
        mazeExists = true;
        checkSize();
        if (checkStatus() == TERMINATE)
           return;
        Graphics me = getGraphics();
        redrawMaze(me);  // show the maze
        me.dispose();
        int r;
        for (i=wallCt-1; i>0; i--) {
            r = (int)(Math.random() * i);  // choose a wall randomly and maybe tear it down
            if (checkStatus() == TERMINATE)
               return;
            tearDown(wallrow[r],wallcol[r]);
            wallrow[r] = wallrow[i];
            wallcol[r] = wallcol[i];
        }
        for (i=1; i<rows-1; i++)  // replace negative values in maze[][] with emptyCode
           for (j=1; j<columns-1; j++)
              if (maze[i][j] < 0)
                  maze[i][j] = emptyCode;
    }

    synchronized void tearDown(int row, int col) {
       // Tear down a wall, unless doing so will form a loop.  Tearing down a wall
       // joins two "rooms" into one "room".  (Rooms begin to look like corridors
       // as they grow.)  When a wall is torn down, the room codes on one side are
       // converted to match those on the other side, so all the cells in a room
       // have the same code.   Note that if the room codes on both sides of a
       // wall already have the same code, then tearing down that wall would 
       // create a loop, so the wall is left in place.
            if (row % 2 == 1 && maze[row][col-1] != maze[row][col+1]) {
                       // row is odd; wall separates rooms horizontally
                fill(row, col-1, maze[row][col-1], maze[row][col+1]);
                maze[row][col] = maze[row][col+1];
                putSquare(row,col,emptyCode);
                try { wait(speedSleep); }
                catch (InterruptedException e) { }
             }
            else if (row % 2 == 0 && maze[row-1][col] != maze[row+1][col]) {
                      // row is even; wall separates rooms vertically
                fill(row-1, col, maze[row-1][col], maze[row+1][col]);
                maze[row][col] = maze[row+1][col];
                putSquare(row,col,emptyCode);
                try { wait(speedSleep); }
                catch (InterruptedException e) { }
             }
    }

    void fill(int row, int col, int replace, int replaceWith) {
           // called by tearDown() to change "room codes".
        if (maze[row][col] == replace) {
            maze[row][col] = replaceWith;
            fill(row+1,col,replace,replaceWith);
            fill(row-1,col,replace,replaceWith);
            fill(row,col+1,replace,replaceWith);
            fill(row,col-1,replace,replaceWith);
        }
    }

    boolean solveMaze(int row, int col) {
               // Try to solve the maze by continuing current path from position
               // (row,col).  Return true if a solution is found.  The maze is
               // considered to be solved if the path reaches the lower right cell.
         if (maze[row][col] == emptyCode) {
             maze[row][col] = pathCode;      // add this cell to the path
             if (checkStatus() == TERMINATE)
                return false;
             putSquare(row,col,pathCode);
             if (row == rows-2 && col == columns-2)
                 return true;  // path has reached goal
             try { Thread.sleep(speedSleep); }
             catch (InterruptedException e) { }
             if ( (solveMaze(row-1,col) && checkStatus() != TERMINATE)  ||     // try to solve maze by extending path
                  (solveMaze(row,col-1) && checkStatus() != TERMINATE)  ||     //    in each possible direction
                  (solveMaze(row+1,col) && checkStatus() != TERMINATE)  ||
                  solveMaze(row,col+1) )
                return true;
             if (checkStatus() == TERMINATE)
                return false;
             // maze can't be solved from this cell, so backtrack out of the cell
             maze[row][col] = visitedCode;   // mark cell as having been visited
             putSquare(row,col,visitedCode);
             synchronized(this) {
               try { wait(speedSleep); }
               catch (InterruptedException e) { }
             }
             if (checkStatus() == TERMINATE)
                return false;
          }
          return false;
    }

}
