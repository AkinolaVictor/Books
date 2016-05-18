
/* 
    The ShapeDraw applet lets the user add small colored shapes to
    a drawing area and then drag them around.  The shapes are rectangles,
    ovals, and roundrects.  The user adds a shape to the canvas by
    clicking on a button.  The shape is added at the upper left corner
    of the canvas.  The color of the shape is given by the current
    setting of a pop-up menu.  The user can drag the shapes with the
    mouse.  Ordinarily, the shapes maintain a given back-to-front order.
    However, if the user shift-clicks on a shape, that shape will be
    brought to the front.
    
    This file defines the applet class plus several other classes used
    by the applet, namely:  ShapeCanvas, Shape, RectShape, OvalShape,
    and RoundRectShape.  These classes are nested inside the main class,
    so the compiled class files for these classes will have named like
    ShapeDraw$ShapeCanvas.class.  All the class files are necessary to
    run the applet.
    
    This applet requires Java version 1.2 or higher.
    
    David Eck
    Original version July 28,  1998
    Revised May 18, 2002 to use Swing and ArrayList and to use nested classes.
*/


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;


public class ShapeDraw extends JApplet {
   
   public void init() {  
        // Set up the applet's GUI.  It consists of a canvas, or drawing area,
        // plus a row of controls below the canvas.  The controls include three
        // buttons which are used to add shapes to the canvas and a Choice menu
        // that is used to select the color used for a shape when it is created.
        // The canvas is set as the "listener" for these controls so that it can
        // respond to the user's actions.
   
      setBackground(Color.lightGray);
      
      ShapeCanvas canvas = new ShapeCanvas();  // create the canvas

      JComboBox colorChoice = new JComboBox();  // color choice menu
      colorChoice.addItem("Red");
      colorChoice.addItem("Green");
      colorChoice.addItem("Blue");
      colorChoice.addItem("Cyan");
      colorChoice.addItem("Magenta");
      colorChoice.addItem("Yellow");
      colorChoice.addItem("Black");
      colorChoice.addItem("White");
      colorChoice.addActionListener(canvas);
      
      JButton rectButton = new JButton("Rect");    // buttons for adding shapes
      rectButton.addActionListener(canvas);

      JButton ovalButton = new JButton("Oval");
      ovalButton.addActionListener(canvas);

      JButton roundRectButton = new JButton("RoundRect");
      roundRectButton.addActionListener(canvas);
      
      JPanel bottom = new JPanel();   // a Panel to hold the control buttons
      bottom.setLayout(new GridLayout(1,4,3,3));
      bottom.add(rectButton);
      bottom.add(ovalButton);
      bottom.add(roundRectButton);
      bottom.add(colorChoice);
   
      getContentPane().setLayout(new BorderLayout(3,3));
      getContentPane().add("Center",canvas);              // add canvas and controls to the applet
      getContentPane().add("South",bottom);
      
   } // end init()
   
   public Insets getInsets() {
         // Allows 3 pixels of extra space around the edges of the applet
         // that will appear in the background color.
      return new Insets(3,3,3,3);
   } // end getInsets()
   
   
   //---- Nested class definitions ---
   //
   // The remainder of the ShapeDraw class consists of static nested class definitions.
   // These are just like regular classes, except that they are defined inside
   // another class (and hence have full names, when used outside this class, such
   // as ShapeDraw.ShapeCanvas).

   static class ShapeCanvas extends JPanel
                     implements ActionListener, MouseListener, MouseMotionListener {

         // This class represents a "canvas" that can display colored shapes and
         // let the user drag them around.  It uses an off-screen images to 
         // make the dragging look as smooth as possible.

      ArrayList shapes = new ArrayList(); // holds a list of the shapes that are displayed on the canvas
      Color currentColor = Color.red;     // current color; when a shape is created, this is its color


      ShapeCanvas() {
           // Constructor: set background color to white set up listeners to respond to mouse actions
         setBackground(Color.white);
         addMouseListener(this);
         addMouseMotionListener(this);
      }   

      public void paintComponent(Graphics g) {
           // In the paint method, all the shapes in ArrayList are
           // copied onto the canvas.
         g.setColor(getBackground());
         g.fillRect(0,0,getSize().width,getSize().height);
         int top = shapes.size();
         for (int i = 0; i < top; i++) {
            Shape s = (Shape)shapes.get(i);
            s.draw(g);
         }
      }   

      public void actionPerformed(ActionEvent evt) {
             // Called to respond to action events.  The three shape-adding
             // buttons have been set up to send action events to this canvas.
             // Respond by adding the appropriate shape to the canvas.
         if (evt.getSource() instanceof JComboBox) {
            switch ( ((JComboBox)evt.getSource()).getSelectedIndex() ) {
               case 0: currentColor = Color.red;     break;
               case 1: currentColor = Color.green;   break;
               case 2: currentColor = Color.blue;    break;
               case 3: currentColor = Color.cyan;    break;
               case 4: currentColor = Color.magenta; break;
               case 5: currentColor = Color.yellow;  break;
               case 6: currentColor = Color.black;   break;
               case 7: currentColor = Color.white;   break;
            }
         }
         else {
            String command = evt.getActionCommand();
            if (command.equals("Rect"))
               addShape(new RectShape());
            else if (command.equals("Oval"))
               addShape(new OvalShape());
            else if (command.equals("RoundRect"))
               addShape(new RoundRectShape());
            }
      }

      void addShape(Shape shape) {
             // Add the shape to the canvas, and set its size/position and color.
             // The shape is added at the top-left corner, with size 80-by-50.
             // Then redraw the canvas to show the newly added shape.
         shape.setColor(currentColor);
         shape.reshape(3,3,80,50);
         shapes.add(shape);
         repaint();
      }


      // -------------------- This rest of this class implements dragging ----------------------

      Shape shapeBeingDragged = null;  // This is null unless a shape is being dragged.
                                       // A non-null value is used as a signal that dragging
                                       // is in progress, as well as indicating which shape
                                       // is being dragged.

      int prevDragX;  // During dragging, these record the x and y coordinates of the
      int prevDragY;  //    previous position of the mouse.

      public void mousePressed(MouseEvent evt) {
            // User has pressed the mouse.  Find the shape that the user has clicked on, if
            // any.  If there is a shape at the position when the mouse was clicked, then
            // start dragging it.  If the user was holding down the shift key, then bring
            // the dragged shape to the front, in front of all the other shapes.
         int x = evt.getX();  // x-coordinate of point where mouse was clicked
         int y = evt.getY();  // y-coordinate of point 
         for ( int i = shapes.size() - 1; i >= 0; i-- ) {  // check shapes from front to back
            Shape s = (Shape)shapes.get(i);
            if (s.containsPoint(x,y)) {
               shapeBeingDragged = s;
               prevDragX = x;
               prevDragY = y;
               if (evt.isShiftDown()) {  // Bring the shape to the front by moving it to
                  shapes.remove(s);      //       the end of the list of shapes.
                  shapes.add(s);
                  repaint();  // repaint canvas to show shape in front of other shapes
               }
               return;
            }
         }
      }

      public void mouseDragged(MouseEvent evt) {
             // User has moved the mouse.  Move the dragged shape by the same amount.
         int x = evt.getX();
         int y = evt.getY();
         if (shapeBeingDragged != null) {
            shapeBeingDragged.moveBy(x - prevDragX, y - prevDragY);
            prevDragX = x;
            prevDragY = y;
            repaint();      // redraw canvas to show shape in new position
         }
      }

      public void mouseReleased(MouseEvent evt) {
             // User has released the mouse.  Move the dragged shape, then set
             // shapeBeingDragged to null to indicate that dragging is over.
             // If the shape lies completely outside the canvas, remove it
             // from the list of shapes (since there is no way to ever move
             // it back onscreen).
         int x = evt.getX();
         int y = evt.getY();
         if (shapeBeingDragged != null) {
            shapeBeingDragged.moveBy(x - prevDragX, y - prevDragY);
            if ( shapeBeingDragged.left >= getSize().width || shapeBeingDragged.top >= getSize().height ||
                    shapeBeingDragged.left + shapeBeingDragged.width < 0 ||
                    shapeBeingDragged.top + shapeBeingDragged.height < 0 ) {  // shape is off-screen
               shapes.remove(shapeBeingDragged);  // remove shape from list of shapes
            }
            shapeBeingDragged = null;
            repaint();
         }
      }

      public void mouseEntered(MouseEvent evt) { }   // Other methods required for MouseListener and 
      public void mouseExited(MouseEvent evt) { }    //              MouseMotionListener interfaces.
      public void mouseMoved(MouseEvent evt) { }
      public void mouseClicked(MouseEvent evt) { }

   }  // end class ShapeCanvas


   // ------- Nested class definitions for the abstract Shape class and three -----
   // -------------------- concrete subclasses of Shape. --------------------------


   static abstract class Shape {

         // A class representing shapes that can be displayed on a ShapeCanvas.
         // The subclasses of this class represent particular types of shapes.
         // When a shape is first constructed, it has height and width zero
         // and a default color of white.

      int left, top;      // Position of top left corner of rectangle that bounds this shape.
      int width, height;  // Size of the bounding rectangle.
      Color color = Color.white;  // Color of this shape.

      void reshape(int left, int top, int width, int height) {
            // Set the position and size of this shape.
         this.left = left;
         this.top = top;
         this.width = width;
         this.height = height;
      }

      void moveBy(int dx, int dy) {
             // Move the shape by dx pixels horizontally and dy pixels vertically
             // (by changing the position of the top-left corner of the shape).
         left += dx;
         top += dy;
      }

      void setColor(Color color) {
             // Set the color of this shape
         this.color = color;
      }

      boolean containsPoint(int x, int y) {
            // Check whether the shape contains the point (x,y).
            // By default, this just checks whether (x,y) is inside the
            // rectangle that bounds the shape.  This method should be
            // overridden by a subclass if the default behavior is not
            // appropriate for the subclass.
         if (x >= left && x < left+width && y >= top && y < top+height)
            return true;
         else
            return false;
      }

      abstract void draw(Graphics g);  
            // Draw the shape in the graphics context g.
            // This must be overriden in any concrete subclass.

   }  // end of class Shape



   static class RectShape extends Shape {
         // This class represents rectangle shapes.
      void draw(Graphics g) {
         g.setColor(color);
         g.fillRect(left,top,width,height);
         g.setColor(Color.black);
         g.drawRect(left,top,width,height);
      }
   }


   static class OvalShape extends Shape {
          // This class represents oval shapes.
      void draw(Graphics g) {
         g.setColor(color);
         g.fillOval(left,top,width,height);
         g.setColor(Color.black);
         g.drawOval(left,top,width,height);
      }
      boolean containsPoint(int x, int y) {
            // Check whether (x,y) is inside this oval, using the
            // mathematical equation of an ellipse.
         double rx = width/2.0;   // horizontal radius of ellipse
         double ry = height/2.0;  // vertical radius of ellipse 
         double cx = left + rx;   // x-coord of center of ellipse
         double cy = top + ry;    // y-coord of center of ellipse
         if ( (ry*(x-cx))*(ry*(x-cx)) + (rx*(y-cy))*(rx*(y-cy)) <= rx*rx*ry*ry )
            return true;
         else
           return false;
      }
   }


   static class RoundRectShape extends Shape {
          // This class represents rectangle shapes with rounded corners.
          // (Note that it uses the inherited version of the 
          // containsPoint(x,y) method, even though that is not perfectly
          // accurate when (x,y) is near one of the corners.)
      void draw(Graphics g) {
         g.setColor(color);
         g.fillRoundRect(left,top,width,height,width/3,height/3);
         g.setColor(Color.black);
         g.drawRoundRect(left,top,width,height,width/3,height/3);
      }
   }


}  // end class ShapeDraw

