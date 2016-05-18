
/*
   This code is totally uncommented.  Sorry.          
 */

import java.awt.*;

public class SimpleCA extends java.applet.Applet implements Runnable {

   private CACanvas canvas;
   private Thread runner = null;
   private volatile boolean run;

   public void init() {
      setLayout(new BorderLayout());
      canvas = new CACanvas();
      add("Center",canvas);
      setBackground(Color.black);
   }

   public Insets insets() {
      return new Insets(2,2,2,2);
   }

   public void run() {
      canvas.properties(2,3,null,null,true);
      canvas.set(null);
      while (true) {
         synchronized(runner) {
            while (run == false) {
               try {
                  runner.wait();
               }
               catch (InterruptedException e) {
               }
            }
         }
         canvas.next();
         try { 
            Thread.sleep(30);
         }
         catch (InterruptedException e) {
         }
      }
   }

   public void start() {
      run = true;
      if (runner == null) {
         runner = new Thread(this);
         runner.start();
      }
      synchronized(runner) {
         runner.notify();
      }
   }

   public void stop() {
      run = false;
   }
   
   public boolean mouseDown(Event evt, int x, int y) {
      if (evt.shiftDown())
         canvas.reset();
      canvas.next();
      return true;
   }

}

