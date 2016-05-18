
/* 
   A small applet that shows multiple copies of the string "Java!"
   in random colors, at random locations, and in a number of fonts
   on a black background.  Strings are added one-by-one, then removed
   in the reverse order.  Then the process is repeated.
   
   David Eck
   Deparment of Mathematics and Computer Science
   Hobart and William Smith Colleges
   300 Pulteney Street
   Geneva, NY 14456
   eck@hws.edu
   
   This version of the applet is based on a generic animation applet
   defined in SimpleAnimationApplet.java.  (An earlier version was not.)
*/

import java.awt.*;

public class JavaPops extends SimpleAnimationApplet {

   static final String str = "Java!";   // the string to display 
   static final int maxJavas = 20;      // number of instances of the string to be displayed
   
   Font[] fontList;          // various fonts to be used for displaying the string  (set up in init())
   FontMetrics[] fm;         // FontMetrics for each font in fontList
   
   int[] x, y;               // (x[i],y[i]) is position of i-th instance of string
   int[] font;               // font[i] is index in fontList[] of i-th instance of string
   Color[] color;            // color[i] is color used for i-th instance   
   
   public void init() {
   
      setFrameCount(2*maxJavas);      // set up SimpleAnimationApplet variables
      setMillisecondsPerFrame(200);
   
      setBackground(Color.black);

      x = new int[maxJavas];
      y = new int[maxJavas];
      font = new int[maxJavas];
      color = new Color[maxJavas];
      
      fontList = new Font[5];   // create fonts for displaying the string
      fontList[0] = new Font("TimesRoman",Font.BOLD,24);
      fontList[1] = new Font("TimesRoman",Font.BOLD,42);
      fontList[2] = new Font("TimesRoman",Font.BOLD,60);
      fontList[3] = new Font("TimesRoman",Font.BOLD+Font.ITALIC,24);
      fontList[4] = new Font("TimesRoman",Font.BOLD+Font.ITALIC,42);
            
      fm = new FontMetrics[fontList.length];
      for (int i=0; i<fontList.length; i++) {
         fm[i] = getFontMetrics(fontList[i]);
      }
          
   }


   public void drawFrame(Graphics g) {
         // A frame shows a number of randomly colored, sized, and positioned strings on
         // a black background.  For frame numbers 0 to maxJavas - 1, a new string is
         // added.  For frame numbers maxJavas to 2*maxJavas - 1, one of the strings is
         // removed.
      int width = getWidth();
      int height = getHeight();
      boolean ascending;  // true if a string is to be added in this frame
      ascending = getFrameNumber() < maxJavas; 
      int javaCt;    // number of strings in this frame
      javaCt = (ascending? getFrameNumber() : 2*maxJavas - getFrameNumber() - 1);
      if (ascending) {  // add an instance of the string with random color, location, font
         font[javaCt] = (int)(fontList.length * Math.random());
         int strlen = fm[font[javaCt]].stringWidth(str);
         int strasc = fm[font[javaCt]].getAscent();
         int minx = -(strlen / 2);
         int miny = strasc / 2;
         int maxx = width + minx;
         int maxy = height + miny;
         x[javaCt] = (int)(minx + Math.random()*(maxx-minx));
         y[javaCt] = (int)(miny + Math.random()*(maxy-miny));
         color[javaCt] = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
      }
      g.setColor(Color.black);  // start with a black background
      g.fillRect(0,0,width,height);
      for (int i = 0; i<javaCt; i++) {  // draw all the string instances
          g.setFont(fontList[font[i]]);
          g.setColor(color[i]);
          g.drawString(str,x[i],y[i]);
      }
   }

}  // end of applet
