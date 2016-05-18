
/*
    The class SimpleAnimationApplet provides a generic framework for applets
    that do simple animations.  This framework is appropriate for an animation
    that runs continuously as long as the applet is active.  It is assumed that
    each time a new frame of the animation is to be drawn, that frame will be
    drawn completely from scratch.
    
    To use this framework, define a subclass of SimpleAnimationApplet and
    override the drawFrame method.  This method is responsible for drawing
    one frame of the animation.  If you want to customize the animation, with
    calls to setFrameCount() and setMillisecondsPerFrame() for example, you
    can define a "public void init()" method in your applet.  Use the init()
    method to call the customization routines as well as to do any initialization
    required for your animation.  Note that you should not override the standard
    applet routines start(), stop(), and destroy(), without calling the versions
    defined below in the SimpleAnimationApplet class.
    
    (Alternatively, instead of defining a subclass, you could copy this
    file, change its name, and edit it.)
       
    David Eck
    Department of Mathematics and Computer Science
    Hobart and William Smith Colleges
    300 Pulteney Street
    Geneva, NY 14456
    eck@hws.edu
    
    July 18, 1998
    Modified January 28, 2000.
    Modified July 25, 2010 to remove references to deprecated method size() and
        to delete definitions of getWidth() and getHeight(), which are now already
        defined in the Component class.
*/

import java.awt.*;

public class SimpleAnimationApplet extends java.applet.Applet implements Runnable {

   public void drawFrame(Graphics g) {
         // This routine should be overridden in any subclass of AnimationApplet.
         // It is responsible for drawing one frame of the animation.  The frame
         // is drawn to the graphics context g.  The parameters width and height
         // give the size of the drawing area.  drawFrame() should begin by
         // filling the drawing area with a background color (as is done in this
         // version of drawFrame).  It should then draw the contents of the
         // frame.  The routine can call getFrameNumber() to determine which frame
         // it is supposed to draw.  It can call getElapsedTime() to find out
         // how long the animation has been running, in milliseconds.
         // The functions getWidth() and getHeight() tell the applet's size.
         // Note that this routine should not take a long time to execute!
      g.setColor(getBackground());
      g.fillRect(0,0,getWidth(),getHeight());
   }
   
   public int getFrameNumber() {
         // Get the current frame number.  The frame number will be incremented
         // each time a new frame is to be drawn.  The first frame number is 0.
         // (If frameCount is greater than zero, and if frameNumber is greater than
         // or equal to frameCount, then frameNumber returns to 0.)
      return frameNumber;
   }
   

   public void setFrameNumber(int frameNumber) {
           // Set the current frame number.  This is the value returned by getFrameNumber().
      if (frameNumber < 0)
         this.frameNumber = 0;
      else
         this.frameNumber = frameNumber;      
   }
      

   public long getElapsedTime() {
           // return the total number of milliseconds that the animation has been
           // running (not including the time when the applet is suspended by
           // the system)
      return elapsedTime;
   }
   

   public void setFrameCount(int max) {
           // If you want your animation to loop through a set of frames over
           // and over, you should call this routine to set the frameCount to 
           // the number of frames in the animation.  Frames will be numbered
           // from 0 to frameCount - 1.  If you specify a value <= 0, the
           // frameNumber will increase indefinitely without ever returning
           // to zero.  The default value of frameCount is -1, meaning that
           // by default frameNumber does NOT loop.
      if (max <= 0)
         this.frameCount = -1;
      else 
         frameCount = max;
   }
   

   public void setMillisecondsPerFrame(int time) {
           // Set the approximate number of milliseconds to be used for each frame.
           // For example, set time = 1000 if you want each frame to be displayed for
           // about a second.  The time is only approximate, and the actual display
           // time will probably be a bit longer.  The default value of 100 is
           // reasonable.  A value of 50 will give smoother animations on computers
           // that are fast enough to draw than many frames per second.
      millisecondsPerFrame = time;
   }
   

   public void setMinimumSleepTime(int time) {
           // An applet must allow some time for the computer to do other tasks.
           // In order to do this, the animation applet "sleeps" between frames.
           // This routine sets a minimum sleep time that will be applied even if
           // that will increase the display time for a frame above the value
           // specified in setMillisecondsPerFrame().  The parameter is given in
           // milliseconds.  The default value is 20.  You can set any non-negative
           // value, including zero.  (Even if you set a value of zero, the system
           // will still take some time away for the apple that it needs for doing
           // other tasks.)
      if (time < 0)
         minimumSleepTime = 0;
      else
         minimumSleepTime = time;
   }
   

   // This rest of this file is private stuff that you don't have to know about
   // when you write your own animations.

   private int frameNumber = 0;
   private int frameCount = -1;
   private int millisecondsPerFrame = 100;
   private int minimumSleepTime = 10;
   
   private long startTime;
   private long oldElapsedTime;
   private long elapsedTime;
   
   private Thread runner;
   
   private Image OSC;
   private Graphics OSG;
   
   private final static int GO = 0, SUSPEND = 1, TERMINATE = 2;
   private int status = SUSPEND;
   
   private int width = -1;
   private int height = -1;

   synchronized public void start() {
           // Called by the system when the applet is first started 
           // or restarted after being stopped.  This routine creates
           // or restarts the thread that runs the animation.
      if (runner == null || !runner.isAlive()) {
         runner = new Thread(this);
         runner.start();
      }
      status = GO;
      startTime = -1;  // signal to run() to compute startTime
      notify();
   }
   
   synchronized public void stop() {
           // Called by the system to suspend the applet. Suspend the
           // animation thread by setting status to SUSPEND.
           // Also, update oldElapsedTime, which keeps track of the
           // total running time of the animation time between
           // calls to start() and stop(). 
      oldElapsedTime += (System.currentTimeMillis() - startTime);
      status = SUSPEND;
      notify();
   }
   
   public void destroy() {
           // Called by the system when the applet is being permanently
           // destroyed.  This tells the animation thread to stop by
           // setting status to TERMINATE.
      if (runner != null && runner.isAlive()) {
         synchronized(this) {
            status = TERMINATE;
            notify();
         }
      }
   }
   
   public void update(Graphics g) {
           // Called by system when applet needs to be redrawn.
      paint(g);
   }
   
   synchronized public void paint(Graphics g) {
           // Draw the current frame on the applet drawing area.
      if (width != getWidth() || height != getHeight()) { // if size has changed, recreate frame
         doSetup();
         if (OSC != null)
            drawFrame(OSG);
      }
      if (OSC == null)  // if there was not enougn memory for the OSC, draw frame directly on applet
         drawFrame(g);
      else  // copy the from from the off-screen canvas
         g.drawImage(OSC,0,0,this);
   }
   
   private void doSetup() {
           // creates OSC and graphics context for OSC
      width = getWidth();
      height = getHeight();
      OSC = null;  // free up any memory currently used by OSC before allocating new memory
      try {
         OSC = createImage(width,height);
         OSG = OSC.getGraphics();
      }
      catch (OutOfMemoryError e) {
         OSC = null;
         OSG = null;
      }
   }
   
   public void run() {
           // Runs the animation.  The animation thread executes this routine.
      long lastFrameTime = 0;
      while (true) {
         synchronized(this) {
            while (status == SUSPEND) {
               try {
                  wait();  // animation has been suspended; wait for it to be restarted
               }
               catch (InterruptedException e) {
               }
            }
            if (status == TERMINATE) {  // exit from run() routine and terminate animation thread
               return;
            }
            if (width != getWidth() || height != getHeight())  // check for applet size change
               doSetup();
            if (startTime == -1) {
               startTime = System.currentTimeMillis();
               elapsedTime = oldElapsedTime;
            }
            else
               elapsedTime = oldElapsedTime + (System.currentTimeMillis() - startTime);
            if (frameCount >= 0 && frameNumber >= frameCount)
               frameNumber = 0;
            if (OSC != null)
               drawFrame(OSG);   // draw current frame to OSC
            frameNumber++;
         }
         long time = System.currentTimeMillis();
         long sleepTime = (lastFrameTime + millisecondsPerFrame) - time;
         if (sleepTime < minimumSleepTime)
            sleepTime = minimumSleepTime;
         repaint();  // tell system to redraw the apple to display the new frame
         if (sleepTime <= 0)
            Thread.yield();
         else {
            try {
               synchronized(this) {
                 wait(sleepTime);
               }
            }
            catch (InterruptedException e) { }
         }
         lastFrameTime = System.currentTimeMillis();
      }
   }


} // end class SimpleAnimationApplet