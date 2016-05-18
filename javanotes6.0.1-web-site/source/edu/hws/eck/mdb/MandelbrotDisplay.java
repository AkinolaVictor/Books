package edu.hws.eck.mdb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A MandelbrotDisplay is a panel that shows a region of the xy-plane that
 * is colored to show a visualization of the Mandelbrot set.  The Mandelbrot set
 * always appears in black.  Other points are colored based on the number of
 * iterations of the Mandelbrot formula that are needed to move the point
 * to a distance of more than sqrt(4.1) units away from (0,0).  Different
 * palettes of colors can be applied to the non-Mandelbrot points.  Furthermore,
 * the length of the palette can be adjusted.  (See the setPaletteLength() command.)
 * The setLimits() method can be used to change the range of x and y values that
 * are shown in the display, but the range will always be adjusted to fit the
 * shape of the display (so that units of measure in the x and y directions are
 * the same).
 * 
 * Because the computation of an image can take quite a while, the computation is
 * done in separate threads (one thread is used for each available processor).
 * 
 * The display is also capable of drawing a "zoom box" on top of the image.  This
 * is just a box whose position and location are given by the drawZoomBox() method.
 * The applyZoom() method makes the picture zoom into or out of the current zoom box,
 * but management of the box is left to some other class (the one that calls these
 * methods.)
 * 
 * Note:  The class uses a relatively large amount of memory.  When the large data
 * Structures are created, there is a small possibility that the program might not
 * have enough memory.  If this happens, the error is caught and the display will
 * show an error message instead of an image.
 */
public class MandelbrotDisplay extends JPanel {
   
   
   //------------------------- PUBLIC CONSTANTS ----------------------------------
   
   
   /**
    * Constant used to identify one of the types of color palettes that can
    * be used for coloring the points in the image.  See setPaletteType()
    * and setGradientPaletteType().
    */
   public final static int PALETTE_SPECTRUM = 0;
   public final static int PALETTE_PALE_SPECTRUM = 1;
   public final static int PALETTE_GRAYSCALE = 2;
   public final static int PALETTE_REVERSE_GRAYSCALE = 3;
   public final static int PALETTE_GRADIENT = 4;
   
   /**
    * The property name for the property change event that is generated
    * when the xy-limits on the display are changed.
    */
   public final static String LIMITS_PROPERTY = "MandelbrotLimits";
   
   /**
    * The property name for the property change that is generated when
    * the status of the display changes.  The possible status values are
    * STATUS_WORKING, meaning that a computation is in progress;
    * STATUS_READY, Which means that the image is complete and the
    * display is currently doing nothing; and STATUS_OUT_OF_MEMORY,
    * which will be the status in the unlikely event that there is not
    * enough memory available to create the data structures used by the
    * display. 
    */
   public final static String STATUS_PROPERTY = "MandelbrotStatus";
   
   /**
    * Constant representing a possible value of the STATUS property.
    */
   public final static String STATUS_WORKING = "working";
   public final static String STATUS_READY = "ready";
   public final static String STATUS_OUT_OF_MEMORY  = "out of memory";
   
   
   //------------------------- PRIVATE INSTANCE VARIABLES --------------------------
   
   private String status = STATUS_READY;  // Current value of the STATUS property.
   
   private BufferedImage OSC;             // The off-screen canvas in which the image is constructed.
   private int[][] iterationCounts;       // The iteration count for each pixel in the display;
                                          //    colors of pixels are set based on contents of this array.

   private int imageWidth;                // Number of columns in the image; same as OSC.getWidth();
   
   
   private int maxIterations = 50;        // Current maximum number of iterations that will be used
                                          //    in the Mandelbrot computation loop.  Pixels that require
                                          //    more iterations are colored black.
   
   private int paletteType;               // Current palette type; one of the constants like PALETTE_SPECTRUM.
   private Color gradientPaletteColor1;   // If palette type is PALETTE_GRADIENT, this is the gradient start color.
   private Color gradientPaletteColor2;   // If palette type is PALETTE_GRADIENT, this is the gradient end color.
   private int paletteLength;             // The number of colors in the palette.
   private int[] palette;                 // The colors in the palette, expressed as RGB color codes.
   
   private double xmin, xmax, ymin, ymax; // Ranges of xy values currently visible in the image.
   private double dx, dy;                 // Width and height of one pixel in xy-coords (should be the same).
   
   private double xmin_requested = -2.5;  // These are the values that were requested in the setLimits()
   private double xmax_requested = 1.1;   //   command.  They are adjusted in the checkAspect() method to
   private double ymin_requested = -1.35; //   fit the aspect ratio of the display, and the actual values
   private double ymax_requested = 1.35;  //   that are used for the image are stored in xmin, xmax, ymin,
                                          //   and ymax.  If the image changes size, the REQUESTED values
                                          //   are re-applied.
   
   private Rectangle zoomBox;             // If non-null, then this rectangle is drawn on top of the
                                          //   the image.  The image can be zoomed into or out of this box.
   
   private Timer delayedResizeTimer;      // This timer is used to avoid creating new BufferedImages
                                          //   continually as the display is resized.  The image will
                                          //   not be resized until 1/3 second after the last size
                                          //   change.
   

   private volatile boolean computing;    // True when a computation is underway.
   private ComputeThread[] workerThreads; // The threads that do the actual computing.  Worker threads
                                          //    perform the "jobs" that make up an image computation.
   private int jobs;                      // The number of jobs that make up a computation. Same as
                                          //    the height of the image, since each job consists of
                                          //    computing iteration counts for one row of pixels.
   private int jobsAssigned;              // The number of jobs that have been assigned to threads so far
                                          //    during the current computation.
   private int jobsCompleted;             // The number of jobs that have been completed so far.
   private LinkedList<Job> finishedJobs;  // When a job is completed, it is placed in this list, which
                                          //    is used as a queue.  A finished job contains data for
                                          //    part of the image.  Every so often, the main thread
                                          //    applies the data from any finished jobs to the image.
   private int computationNumber;         // Each time a computation is started, this variable is
                                          //    incremented.  Because computations are done by threads,
                                          //    when a computation is aborted and a new one started,
                                          //    it's possible that a thread might continue working on
                                          //    part of the previous computation.  When that happens,
                                          //    the result of that computation should be discarded and
                                          //    not applied to the current picture.  By associating each
                                          //    job with a computation number, out-of-date jobs can
                                          //    recognized.
   private boolean shutDown;              // Used to send a message to the threads to tell them to shut down.
   private int[] rgb;                     // Used for applying color to the BufferedImage; this could be 
                                          //    a local variable.
   
   private Timer applyJobsToImageTimer;   // A Timer that generates events every 1/2 second during a computation.
                                          //   These events wake up the main thread so it can apply completed
                                          //   jobs to the image.

   
   
   //-------------------------- PUBLIC CONSTRUCTOR AND METHODS ------------------------
   
   /**
    * Create a display with preferred size 800-by-600.
    */
   public MandelbrotDisplay() {
      setPreferredSize( new Dimension(800,600) );
      setBackground(Color.LIGHT_GRAY);
      addComponentListener( new ComponentAdapter() {
         public void componentResized(ComponentEvent e) {
                // Stops previous timer, if any, and starts a new timer
                // that will go off in 1/3 second.  The BufferedImage will
                // not be recreated until a timer actually has time to go off,
                // that is, 1/3 second after the last size change in a series.
            if (delayedResizeTimer != null) {
               delayedResizeTimer.stop();
               delayedResizeTimer = null;
            }
            if (OSC != null) {
               delayedResizeTimer = new Timer(100,new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     delayedResizeTimer = null;
                     repaint();
                  }
               });
               delayedResizeTimer.setInitialDelay(333);
               delayedResizeTimer.setRepeats(false);
               delayedResizeTimer.start();
            }
         }
      });
      applyJobsToImageTimer = new Timer(500, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            applyFinishedJobsToImage();
         }
      });
   }
   
   
   /**
    * Returns a reference to the off-screen image.  It is possible for the
    * value to be null.  (In the Mandelbrot Viewer program, this is used only
    * for implementing the Save Image command.)
    */
   public BufferedImage getImage() {
      return OSC;
   }
   
   
   /**
    * Returns the current value of the STATUS property.  The return value is one of
    * the constants MandelbrotDisplay.STATUS_READY, MandelbrotDisplay.STATUS_WORKING,
    * or MandelbrotDisplay.OUT_OF_MEMORY. 
    */
   public String getStatus() {
      return status;
   }
   
   
   /**
    * Set the desired range of xy-values to be visible in the image.  The values
    * might be adjusted to reflect the aspect ratio of the display.  When the
    * limits change, a PropertyChangeEvent with property name MandelbrotDisplay.STATUS_LIMITS
    * is generated; the values associated with the property change are arrays.  The
    * array is an array of double of length four containing xmin, xmax, ymin, and ymax.
    * Note that calling this method causes a new computation to begin, but only if the
    * limits are actually changed. The default values for the limits are -2.5, 1.1, -1.35, 1.35.
    */
   public void setLimits(double xmin, double xmax, double ymin, double ymax) {
      if (xmin == this.xmin && xmax == this.xmax && ymin == this.ymin && ymax == this.ymax)
         return;
      double[] oldLimits = { this.xmin, this.xmax, this.ymin, this.ymax };
      stopComputing();
      xmin_requested = xmin;
      xmax_requested = xmax;
      ymin_requested = ymin;
      ymax_requested = ymax;
      startComputing(); // Calls checkAspect, which sets new values for this.xmin, etc.
      repaint();
      double[] newLimits = { this.xmin, this.xmax, this.ymin, this.ymax };
      firePropertyChange(LIMITS_PROPERTY, oldLimits, newLimits);
   }
   
   
   /**
    * Return the current xy limits as an array of four doubles containing
    * xmin, xmax, ymin, and ymax.
    */
   public double[] getLimits() {
      return new double[] { xmin, xmax, ymin, ymax };
   }
   
   
   /**
    * Return the current value of xmin, the lower limit on the range of x values currently shown.
    */
   public double getXmin() {
      return xmin;
   }
   
   
   /**
    * Return the current value of xmax, the upper limit on the range of x values currently shown.
    */
   public double getXmax() {
      return xmax;
   }
   
   
   /**
    * Return the current value of ymin, the lower limit on the range of y values currently shown.
    */
   public double getYmin() {
      return ymin;
   }
   
   
   /**
    * Return the current value of ymax, the upper limit on the range of y values currently shown.
    */
   public double getYmax() {
      return ymax;
   }
   
   
   /**
    * Set the maximum number of iterations to be used in the Mandelbrot computation loop.
    * The default value is 50.  Calling this method causes a new computation to begin, if
    * the value of maxIterations is actually changed.
    */
   synchronized public void setMaxIterations( int max ) {
      if (max == maxIterations)
         return;
      stopComputing();
      maxIterations = max;
      if (paletteLength == 0)
         palette = null;
      startComputing();
   }
   
   
   /**
    * Returns the current maximum number of iterations to be used in the Mandelbrot computation loop.
    */
   public int getMaxIterations() {
      return maxIterations;
   }

   
   /**
    * Set the type of palette that is used to color the pixels to one of the constants
    * MandelbrotDispaly.PALETTE_SPECTRUM, MandelbrotDispaly.PALETTE_PALE_SPECTRUM,
    * MandelbrotDispaly.PALETTE_GRAYSCALE, or MandelbrotDispaly.PALETTE_REVERSE_GRAYSCALE.
    * Other values for the parameter will be ignored.  The new palette is applied
    * immediately.  If a computation is underway, the palette is applied to the part
    * of the image that has been computed and the computation continues.  Note that
    * this method CANNOT be used to set a gradient palette.
    */
   synchronized public void setPaletteType(int type) {
      if (type == paletteType)
         return;
      if (type != PALETTE_SPECTRUM && type != PALETTE_PALE_SPECTRUM
            && type != PALETTE_GRAYSCALE && type != PALETTE_REVERSE_GRAYSCALE)
         return;
      gradientPaletteColor1 = gradientPaletteColor2 = null;
      paletteType = type;
      palette = null;    // Forces computation of a new palette.
      recomputeColors(); // Applies new palette to the image, or the part that has been computed.
   }
   
   
   /**
    * Set the type of palette that is used to color the pixels to be a gradient palette
    * (paletteType = MandelbrotDisplay.PALETTE_GRADIENT), and specify the start and end
    * colors for the gradient. The new palette is applied
    * immediately.  If a computation is underway, the palette is applied to the part
    * of the image that has been computed and the computation continues.
    * @param color1  start color for gradient; if null, the call to this method is ignored.
    * @param color2  end color for gradient; if null, the call to this method is ignored.
    */
   synchronized public void setGradientPalette(Color color1, Color color2) {
      if (paletteType == PALETTE_GRADIENT && gradientPaletteColor1.equals(color1)
            && gradientPaletteColor2.equals(color2))
         return;
      if (color1 == null || color2 == null)
         return;
      paletteType = PALETTE_GRADIENT;
      gradientPaletteColor1 = color1;
      gradientPaletteColor2 = color2;
      palette = null;    // Forces computation of a new palette.
      recomputeColors(); // Applies new palette to the image, or the part that has been computed.
   }
   
   
   /**
    * Returns the current palette type, one of the constants MandelbrotDisplay.PALETTE_GRADIENT,
    * MandelbrotDisplay.PALETTE_SPECTRUM, MandelbrotDisplay.PALETTE_PALE_SPECTRUM,
    * MandelbrotDisplay.PALETTE_GRAYSCALE, or MandelbrotDisplay.PALETTE_REVERSE_GRAYSCALE.
    */
   public int getPaletteType() {
      return paletteType;
   }
   
   
   /**
    * If the current palette type is MandelbrotDispaly.PALETTE_GRADIENT, this returns
    * the start color of the gradient.  If one of the other types of palettes is being
    * used, the return value is null.
    */
   public Color getGradientPaletteColor1() {
      return gradientPaletteColor1;
   }
   
   
   /**
    * If the current palette type is MandelbrotDispaly.PALETTE_GRADIENT, this returns
    * the end color of the gradient.  If one of the other types of palettes is being
    * used, the return value is null.
    */
   public Color getGradientPaletteColor2() {
      return gradientPaletteColor2;
   }
   
   
   /**
    * Set the palette lengths.  If the length is set to 0, then the palette length will
    * always be set to be the same as maxIterations.  That is, there will be one color
    * for each possible value of the iteration count.  If it is set to some positive
    * value, that that number of colors is used.  When the number of colors in the
    * palette is smaller than maxIterations, the palette will be repeated as many times
    * as necessary to cover the full range of values; this makes the colors vary faster
    * as the iteration count changes, which can reveal a greater range of colors
    * in the image.  When the number of colors in the palette is larger than maxIterations, 
    * the effect is to make the colors vary more slowly.  If a computation is in progress
    * when this method is called, the change is applied immediately to the part of the
    * image that has been computed, and the computation continues.
    */
   synchronized void setPaletteLength(int length) {
      if (length <= 0)
         length = 0;
      if (length == paletteLength)
         return;
      paletteLength = length;
      palette = null;     // Force construction of new palette.
      recomputeColors();  // Apply new palette to image.
   }
   
   
   /**
    * Return the current value of paletteLength.
    */
   public int getPaletteLength() {
      return paletteLength;
   }
   
   
   /**
    * Used to draw a rectangle around a portion of the image.  If the parameter is null,
    * then nothing is drawn (and the rect that was there before, if any is removed).
    * Otherwise, the specified rectangle will be drawn on top of the image.
    */
   public boolean drawZoomBox(Rectangle rect) {
      if (zoomBox != null)
         repaint( zoomBox.x - 1, zoomBox.y - 1, zoomBox.width + 3, zoomBox.height + 3);
      if (OSC == null) {
         zoomBox = null;
         return false;
      }
      zoomBox = rect;
      if (zoomBox != null)
         repaint( zoomBox.x - 1, zoomBox.y - 1, zoomBox.width + 3, zoomBox.height + 3);
      return true;
   }
   
   
   /**
    * If a rectangle has been specified using the drawZoomBox, this method will zoom the
    * image into or out of the box.  The rectangle is then discarded.  If there is no
    * zoom box, nothing is done.
    * @param zoomOut if false, the part of the image inside the zoom rectangle is magnified
    *    to fill the entire image; if true, the entire image is shrunk down to fit inside
    *    the zoom box and new parts of the picture become visible.
    */
   public void applyZoom(boolean zoomOut) {
      if (zoomBox == null)
         return;
      if (zoomBox.width == 0 || zoomBox.height == 0)  {
         zoomBox = null;
         repaint();
         return;
      }
      double x1, x2, y1, y2;  // coordinates of corners of zoombox
      double cx, cy;   // coordinates of center of zoombox
      double newWidth, newHeight;
      x1 = xmin + ((double)zoomBox.x)/getWidth()*(xmax-xmin);
      x2 = xmin + ((double)(zoomBox.x+zoomBox.width))/getWidth()*(xmax-xmin);
      y1 = ymax - ((double)zoomBox.y+zoomBox.height)/getHeight()*(ymax-ymin);
      y2 = ymax - ((double)(zoomBox.y))/getHeight()*(ymax-ymin);
      cx = (x1+x2)/2;
      cy = (y1+y2)/2;
      if (zoomOut) {  // (some heavy math)
         double newXmin = xmin + (xmin-x1)/(x2-x1)*(xmax-xmin);
         double newXmax = xmin + (xmax-x1)/(x2-x1)*(xmax-xmin);
         double newYmin = ymin + (ymin-y1)/(y2-y1)*(ymax-ymin);
         double newYmax = ymin + (ymax-y1)/(y2-y1)*(ymax-ymin);
         setLimits(newXmin,newXmax,newYmin,newYmax);
      }
      else {
         newWidth = x2 - x1;
         newHeight = y2 - y1;
         setLimits( cx-newWidth/2, cx+newWidth/2, cy-newHeight/2, cy+newHeight/2 ); 
      }
      zoomBox = null;
   }
   
   
   /**
    * This method can be called to tell all the threads that are used by the display
    * to terminate cleanly.  This method should be called only when the display is
    * being discarded.  It is not usually necessary to call this method. It might
    * allow a cleaner shutdown if the program is being run as an applet.
    *
    */
   synchronized public void shutDownThreads() {
      shutDown = true;
      notifyAll();
   }
   
   
   /**
    * Draws the image onto the screen.  (This, of course, is meant to be called 
    * by the system, not by the user.)
    */
   protected void paintComponent(Graphics g) {
      if (delayedResizeTimer != null) {
             // If a resize timer is running, don't resize the image, just show the one we have, if any.
         super.paintComponent(g);
         if (OSC != null)
            g.drawImage(OSC, 0, 0, null);
         zoomBox = null;
      }
      else {
         checkOSC();
         if (OSC == null) {
                // Could not create the data structures; show an error message.
            super.paintComponent(g);
            g.setColor(Color.RED);
            g.drawString(I18n.tr("error.memory"),20,50);
            zoomBox = null;
         }
         else {
            g.drawImage(OSC, 0, 0, null);
            if (zoomBox != null) {
                  // A zoom box has been specified by the drawZoomBox() method, so draw it.
               g.setColor(Color.WHITE);   
               g.drawRect(zoomBox.x-1, zoomBox.y-1, zoomBox.width+2, zoomBox.height+2);
               g.setColor(Color.BLACK);   
               g.drawRect(zoomBox.x, zoomBox.y, zoomBox.width, zoomBox.height);
               g.setColor(Color.WHITE);   
               g.drawRect(zoomBox.x+1, zoomBox.y+1, zoomBox.width-2, zoomBox.height-2);
            }
         }
      }
      
   }
   
   
   
   //-------------------------- PRIVATE METHODS (and private nested classes) -----------------------
   
   
   /**
    * Used internally to set the current status.  Note that when the status is
    * changed, a PropertyChangeEvent with property name MandelbrotDisplay.STATUS_PROPERTY
    * is fired.
    */
   private void setStatus(String status) {
      if (status == this.status)
         return;
      String oldStatus = this.status;
      this.status = status;
      firePropertyChange(STATUS_PROPERTY, oldStatus, status);
   }

   
   /**
    * Called by the paintComponent method to check whether a new off-screen canvas
    * is needed.  (This is a separate method mainly because it needs to be synchronized).
    */
   synchronized private void checkOSC() {
      if (OSC == null || OSC.getWidth() != getWidth() || OSC.getHeight() != getHeight()) {
         stopComputing();  // Abort current computation -- it only applied to the old canvas.
         OSC = null;       // Free up memory currently used by OSC so it can be reused.
         iterationCounts = null;  // Free up memory currently used by iterationCounts, so it can be reused.
         try {
            int width = getWidth();
            int height = getHeight();
            OSC = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            iterationCounts = new int[height][width];  // Note that first index is pixel row number.
            rgb = new int[width];
            imageWidth = width;
            startComputing();
         }
         catch (OutOfMemoryError e) {
               // It was not possible to create all the memory that we need.
               // No image can be displayed.  The paintComponent method will show an error message.
            OSC = null;
            iterationCounts = null;
            setStatus(STATUS_OUT_OF_MEMORY);
         }
      }
   }
   

   /**
    * This is the method that is called periodically (in response to a timer event)
    * to check the queue of finished jobs.  All finished jobs are removed from the
    * queue and are applied to the image.
    */
   synchronized void applyFinishedJobsToImage() {
      ArrayList<Job> temp;
      synchronized(this) {
         if (finishedJobs == null)
            return;
         // First, get the jobs from the queue; has to be done in synchronized part of the method.
         temp = new ArrayList<Job>();
         while (!finishedJobs.isEmpty())
            temp.add(finishedJobs.removeFirst());
      }
      // Now apply the data from the jobs to the image; this doesn't have to be synchronized
      //   since this method is the only one that touches the image.
      for (Job job : temp) {
         iterationCounts[job.rowNumber] = job.iterationCounts;
         if (palette == null)
            createPalette();
         for (int i = 0; i < imageWidth; i++)
            rgb[i] = getColorForIterationCount(job.iterationCounts[i]);
         OSC.setRGB(0, job.rowNumber, imageWidth, 1, rgb, 0, imageWidth);
         repaint(0,job.rowNumber,imageWidth,1);
      }
   }
   
   
   /**
    * Get the correct color from the palette to color a point with the given
    * Iteration count.
    */
   private int getColorForIterationCount(int ct) {
      if (ct < 0)    // Only -1 is possible, representing the Mandelbrot set.
         return 0;  // RGB code for black
      else if (paletteLength == 0)
         return palette[ct];
      else {
         ct = ct % paletteLength;
         return palette[ct];
      }
   }
   
   
   /**
    * Applies current palette to the image, or to any part of the image
    * that has been completed, if a computation is in progress.
    */
   synchronized private void recomputeColors() {
      if (OSC == null)
         return;
      if (palette == null)
         createPalette();
      for (int i = 0; i < iterationCounts.length; i++) {
         if (iterationCounts[i] != null) {
            for (int j = 0; j < imageWidth; j++)
               rgb[j] = getColorForIterationCount(iterationCounts[i][j]);
            OSC.setRGB(0, i, imageWidth, 1, rgb, 0, imageWidth);
         }
      }
      repaint();
   }


   /**
    * This is called to abort the current computation, if any.  Note that this method
    * calls applyFinishedJobsToImage() to get the data from any outstanding finished
    * jobs and apply it to the image.
    */
   synchronized private void stopComputing() {
      if (!computing || OSC == null)
         return;
      applyJobsToImageTimer.stop();
      applyFinishedJobsToImage();
      finishedJobs = null;
      computing = false;
      setStatus(STATUS_READY);
   }
   
   
   /**
    * This is called to start a new computation.
    *
    */
   synchronized private void startComputing()  {
      if (OSC == null)
         return;
      stopComputing();
      Graphics g = OSC.getGraphics();
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(0,0,getWidth(),getHeight());
      g.dispose();
      repaint();
      int processCount = Runtime.getRuntime().availableProcessors();
      if (workerThreads == null) {
         System.out.println("Creating " + processCount + " threads.");
         workerThreads = new ComputeThread[processCount];
         int priority = Thread.currentThread().getPriority() - 1;
         for (int i = 0; i < processCount; i++) {
            workerThreads[i] = new ComputeThread();
            try {  // By setting the thread to be a "daemon" means that the
                  // program can terminate even if this thread is still running.
               workerThreads[i].setDaemon(true);
            }
            catch (Exception e) {
               System.out.println("Can't set thread to daemaon.");
            }
            try {  // By reducing the priority of the thread, we ensure that
                  // the user interface thread will be responsive.  Threads 
                  // of lower priority only run when no thread of higher
                  // priority wants to run.
               workerThreads[i].setPriority(priority);
            }
            catch (Exception e) {
               System.out.println("Can't reduce worker thread priority?");
            }
            workerThreads[i].start();
         }
      }
      checkAspect();
      computationNumber++;
      jobs = iterationCounts.length;
      jobsAssigned = 0;
      jobsCompleted = 0;
      computing = true;
      finishedJobs = new LinkedList<Job>();
      for (int i = 0; i < iterationCounts.length; i++)
         iterationCounts[i] = null;
      notifyAll();
      applyJobsToImageTimer.start();
      setStatus(STATUS_WORKING);
   }
   
   
   /**
    * Adjusts the xy limits to fit the aspect ratio of the display.  If the shape of
    * the requested region in the plane does not match the shape of the display,
    * then either the range of x values or the range of y values will be increased
    * to make the shapes match.  Note that the full requested ranges are always shown.
    * There just might be some extra parts of the plane visible on the top and bottom
    * or sides.
    */
   private void checkAspect() {
      xmin = xmin_requested;
      xmax = xmax_requested;
      if (xmax < xmin) {
         double temp = xmin;
         xmin = xmax;
         xmax = temp;
      }
      ymin = ymin_requested;
      ymax = ymax_requested;
      if (ymax < ymin) {
         double temp = ymax;
         ymax = ymin;
         ymin = temp;
      }
      double width = xmax - xmin;
      double height = ymax - ymin;
      double aspect = width/height;
      double windowAspect = (double)getWidth()/(double)getHeight();
      if (aspect < windowAspect) {
         double newWidth = width*windowAspect/aspect;
         double center = (xmax + xmin)/2;
         xmax = center + newWidth/2;
         xmin = center - newWidth/2;
      }
      else if (aspect > windowAspect) {
         double newHeight = height*aspect/windowAspect;
         double center = (ymax+ymin)/2;
         ymax = center + newHeight/2;
         ymin = center - newHeight/2;
      }
      dx = (xmax - xmin) / (getWidth() - 1);
      dy = (ymax - ymin) / (getHeight() - 1);
   }
   
   
   /**
    * Builds the array that holds the palette colors, based on current settings.
    */
   private void createPalette() {
      if (paletteLength == 0)
         palette = new int[maxIterations+1];
      else
         palette = new int[paletteLength];
      for (int i = 0; i < palette.length; i++) {
         float fraction = ((float)i)/(palette.length-1);
         Color color;
         switch (paletteType) {
         case PALETTE_GRADIENT:
            float r1 = gradientPaletteColor1.getRed()/255.0F;
            float r2 = gradientPaletteColor2.getRed()/255.0F;
            float r = Math.max(0,Math.min(1,r2*fraction + r1*(1-fraction)));
            float g1 = gradientPaletteColor1.getGreen()/255.0F;
            float g2 = gradientPaletteColor2.getGreen()/255.0F;
            float g = Math.max(0,Math.min(1,g2*fraction + g1*(1-fraction)));
            float b1 = gradientPaletteColor1.getBlue()/255.0F;
            float b2 = gradientPaletteColor2.getBlue()/255.0F;
            float b = Math.max(0,Math.min(1,b2*fraction + b1*(1-fraction)));
            color = new Color(r,g,b);
            break;
         case PALETTE_SPECTRUM:
            color = Color.getHSBColor(0.95F*fraction, 1, 1);
            break;
         case PALETTE_PALE_SPECTRUM:
            color = Color.getHSBColor(0.95F*fraction, 0.6F, 1);
            break;
         case PALETTE_GRAYSCALE:
            color = new Color(0.9F*fraction,0.9F*fraction,0.9F*fraction);
            break;
         default:
            color = new Color(1-0.9F*fraction,1-0.9F*fraction,1-0.9F*fraction);
            break;
         }
         palette[i] = color.getRGB();
      }
   }
   
   
   /**
    * Called by worker threads to get the next available job.  Computation of an
    * image is broken up into a set of jobs that are performed by worker threads.
    * This method is used to assign a new jobs to a thread each time it completes
    * a job.  When no jobs are available (between computations or before any computation
    * is begun), this method will block, which will keep the threads idle.
    * @return
    */
   synchronized private Job getNextJob() {
      while (!computing && !shutDown) {
         try {
            wait();
         }
         catch (InterruptedException e) {
         }
      }
      if (shutDown)
         return null;
      else if (jobsAssigned >= jobs)
         return null;
      else {
         Job job = new Job();
         job.rowNumber = jobsAssigned;
         job.xmin = xmin;
         job.dx = dx;
         job.y = ymax - jobsAssigned*dy;
         job.maxIterations = maxIterations;
         job.count = imageWidth;
         job.computationNumber = computationNumber;
         jobsAssigned++;
         return job;
      }
   }
   
   
   /**
    * This is called by a worker thread when it finishes a job.  The job is added to
    * the queue of finished jobs.  If all jobs have been completed, the stopComputing()
    * method is called.
    */
   synchronized private void finish(Job job) {
      if (job.computationNumber != computationNumber)
         return;
      finishedJobs.addLast(job);
      jobsCompleted++;
      if (jobsCompleted == jobs)
         stopComputing();
   }
   
   
   /**
    * This class represents one job, which consists of doing the Mandelbrot computation 
    * loop and counting the iterations for each pixel in one row of pixels.  All the
    * data necessary for the computation is stored in the job object.  The
    * computationNumber identifies which computation this job is part of.  The output
    * of the jobs, consisting of an array of iteration counts, is stored in the
    * iterationCounts instance variable when the job finishes.
    */
   private class Job {
      double xmin;
      double dx;
      double y;
      int count;
      int maxIterations;
      int rowNumber;
      int computationNumber;
      int[] iterationCounts;
      void compute() {
         iterationCounts = new int[count];
         for (int i = 0; i < count; i++) {
            double x0 = xmin + i * dx;
            double y0 = y;
            double a = x0;
            double b = y0;
            int ct = 0;
            while (a*a + b*b < 4.1) {
               ct++;
               if (ct > maxIterations) {
                  ct = -1;
                  break;
               }
               double newa = a*a - b*b + x0;
               b = 2*a*b + y0;
               a = newa;
            }
            iterationCounts[i] = ct;
         }
      }
   }
   
   
   /**
    * Class that defines the worker threads.  The thread is very simple.  It just
    * loops forever, getting jobs to do and carrying out each job. 
    */
   private class ComputeThread extends Thread {
      public void run() {
         while (true) {
            Job job = getNextJob();  // blocks until a job is available.
            if (shutDown)
               break;
            if (job != null) {
               job.compute();  // does the work.
               finish(job);
            }
         }
      }
   }
   

}
