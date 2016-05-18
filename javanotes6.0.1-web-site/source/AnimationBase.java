
/*
    The class AnimationBase provides a generic framework for applets
    that do simple animations.  This framework is appropriate for an animation
    that runs continuously as long as the applet is active.  It is assumed that
    each time a new frame of the animation is to be drawn, that frame will be
    drawn completely from scratch.
    
    To use this framework, define a subclass of AnimationBase and
    override the drawFrame method.  This method is responsible for drawing
    one frame of the animation.  If you want to customize the animation, with
    calls to setFrameCount() and setMillisecondsPerFrame() for example, you
    can define a "public void init()" method in your applet.  Use the init()
    method to call the customization routines as well as to do any initialization
    required for your animation.  Note that you should not override the standard
    applet routines start(), stop(), and destroy(), without calling the versions
    defined below in the AnimationBase class.
    
    (Alternatively, instead of defining a subclass, you could copy this
    file, change its name, and edit it.)
    
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AnimationBase extends JApplet implements ActionListener {

   public void drawFrame(Graphics g) {
         // This routine should be overridden in any subclass of this class.
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
         // This version of this routine simple displays the frame number
         // and elapsed time in each frame.
      g.setColor(getBackground());
      g.fillRect(0,0,getWidth(),getHeight());
      g.setColor(getForeground());
      g.drawString("Frame Number " + getFrameNumber(),10,15);
      g.drawString("Elapsed Time " + getElapsedTime()/1000 + " seconds",10,30);
   }
   
   public int getFrameNumber() {
         // Get the current frame number.  The frame number will be incremented
         // each time a new frame is to be drawn.  The first frame number is 0.
         // (If frameCount is greater than zero, and if frameNumber is greater than
         // or equal to frameCount, then frameNumber returns to 0.)
      return frameNumber;
   }
   

   public void setFrameNumber(int frameNumber) {
           // Set the current frame number.  This is the value returned by 
           // getFrameNumber().  Any value less than 0 is changed to 0.
      if (frameNumber < 0)
         this.frameNumber = 0;
      else
         this.frameNumber = frameNumber;      
   }
   
   
   public int getWidth() {
           // Returns the width of the applet, in pixels.
       return display.getSize().width;
   }
   
   
   public int getHeight() {
           // Returns the height of the applet, in pixels.
       return display.getSize().height;
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
           // Set the APPROXIMATE number of milliseconds to be used for each frame.
           // For example, set time = 1000 if you want each frame to be displayed for
           // about a second.  The time is only approximate, and the actual display
           // time will probably be a bit longer.  The default value of 100 is
           // reasonable.  A value of 50 will give smoother animations on computers
           // that are fast enough to draw than many frames per second.  The
           // value of time must be 10 or greater.  If a value less than 10
           // is passed, it will be changed to 10.
           //    Do not put too much faith in the this value.  It is only
           // an approximation.  If you want to know exactly how long the
           // animation has been running, use getElapsedTime() instead of
           // getFrameNumber().
      if (time < 10)
         time = 10;
      timer.setDelay(time);
   }
   

   

   // This rest of this file is private stuff that you don't have to know about
   // when you write your own animations.

   private int frameNumber;
   private int frameCount = -1;
   
   private long startTime;
   private long oldElapsedTime;
   private long elapsedTime;
   
   private Timer timer = new Timer(100,this);
   private boolean firstTime = true;
   
   private JPanel display;
   
   public AnimationBase() {
      display = new JPanel() {
         public void paintComponent(Graphics g) {
            drawFrame(g);
         }
      };
      getContentPane().add(display);
   }
   
   public void start() {
           // Called by the system when the applet is first started 
           // or restarted after being stopped.  This routine starts
           // or restarts the timer that runs the animation.
       if (firstTime) {
          timer.setInitialDelay(500);  // Time before first frame.
          timer.start();
       }
       else {
          timer.restart();
       }
       startTime = System.currentTimeMillis() + 500;
   }
   
   public void stop() {
           // Called by the system to suspend the applet.  Stop the timer.
       oldElapsedTime += System.currentTimeMillis() - startTime;
       elapsedTime = oldElapsedTime;
       timer.stop();
   }
   
   public void actionPerformed(ActionEvent evt) {
       frameNumber++;
       if (frameCount >= 0 && frameNumber >= frameCount)
           frameNumber = 0;
       elapsedTime = oldElapsedTime + System.currentTimeMillis() - startTime;
       display.repaint();
   }
   

} // end class AnimationBase
