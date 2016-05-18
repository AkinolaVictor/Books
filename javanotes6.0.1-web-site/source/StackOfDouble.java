
/**
 *  An object of type StackOfDouble is a stack of real numbers, with the 
 *  standard stack operations push(int N), pop(), and isEmpty().
 *  A makeEmpty() operation is also provided to remove all items from the stack.
 *  Internally, the stack is implemented as a linked list.
 */
public class StackOfDouble {


   /**
    * An object of type Node holds one of the items on the stack.
    */
   private static class Node {
      double item;    // One of the items in the list
      Node next;      // Pointer to the node that holds the next item.
   }

   
   private Node top;  // Pointer to the Node that is at the top of the stac,
                      //    If top == null, then the stack is empty.

   /**
    * Add N to the top of the stack.
    */
   public void push( double N ) {
      Node newTop = new Node();
      newTop.item = N;
      newTop.next = top;
      top = newTop;
   }

   /**
    * Remove the top item from the stack, and return it.
    * @return the item that was removed from the top of the stack
    * @throws IllegalStateException if the stack is emtpy when method is called.
    */
   public double pop() {
      if ( top == null )
         throw new IllegalStateException();
      double topItem = top.item;
      top = top.next;
      return topItem;
   }

   
   /**
    * Returns true if the stack is empty.  Returns false
    * if there are one or more items on the stack.
    */
   public boolean isEmpty() { 
      return top == null;
   }
   

} // end class StackOfDouble

