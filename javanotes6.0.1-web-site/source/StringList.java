
/**
 * An object of type StringList represents a list of strings.  Methods
 * are provided to insert a string into the list, to delete a string
 * from the list, and to check whether a given string occurs in the list.
 * (For testing purposes, a method is also provided that will return an
 * array containing all the strings in the list.)
 *    Strings that are inserted into the list must be non-null.  Inserting
 * a null string will cause NullPointerExceptions when the list is used
 * in subsequent operations.
 *    Note that this class is certainly NOT meant to be a full-featured
 * List class.  It is for demonstration only.
 */
public class StringList {


   /**
    * Internally, the list of strings is represented as a linked list of 
    * nodes belonging to the nested class Node.  The strings in the list 
    * are stored in increasing order (using the order given by the 
    * compareTo() method from the string class, which is the same as 
    * alphabetical order if all the strings are made up of lower 
    * case letters).
    */
   private static class Node {
      String item;   // One of the items in the list
      Node next;     // Pointer to the node containing the next item.
                     //   In the last node of the list, next is null.
   }


   private Node head;  // A pointer to the first node in the linked list.
                       // If the list is empty, the value is null.


   /**
    * Searches the list for a specified item.  (Note: for demonstration
    * purposes, this method does not use the fact that the items in the
    * list are ordered.)
    * @param searchItem the item that is to be searched for
    * @return true if searchItem is one of the items in the list or false if
    *    searchItem does not occur in the list.
    */
   public boolean find(String searchItem) {

      Node runner;    // A pointer for traversing the list.

      runner = head;  // Start by looking at the head of the list.
      
      while ( runner != null ) {
            // Go through the list looking at the string in each
            // node.  If the string is the one we are looking for,
            // return true, since the string has been found in the list.
            // (Note:  Since the list is ordered, if we find an item
            // that is greater than searchItem, we could immediately
            // return false.)
         if ( runner.item.equals(searchItem) )
            return true;
         runner = runner.next;  // Move on to the next node.
      }

      // At this point, we have looked at all the items in the list
      // without finding searchItem.  Return false to indicate that
      // the item does not exist in the list.

      return false;

   } // end find()


   /**
    * Delete a specified item from the list, if that item is present.
    * If multiple copies of the item are present in the list, only
    * the one that comes first in the list one is deleted.
    * @param deleteItem the item to be deleted
    * @return true if the item was found and deleted, or false if the item
    *    was not in the list.
    */
   public boolean delete(String deleteItem) {

      if ( head == null ) {
             // The list is empty, so it certainly doesn't contain deleteString.
         return false;
      }
      else if ( head.item.equals(deleteItem) ) {
              // The string is the first item of the list.  Remove it.
         head = head.next;
         return true;
      }
      else {
             // The string, if it occurs at all, is somewhere beyond the 
             // first element of the list.  Search the list.
         Node runner;     // A node for traversing the list.
         Node previous;   // Always points to the node preceding runner.
         runner = head.next;   // Start by looking at the SECOND list node.
         previous = head;
         while ( runner != null && runner.item.compareTo(deleteItem) < 0 ) {
                // Move previous and runner along the list until runner
                // falls off the end or hits a list element that is
                // greater than or equal to deleteItem.  When this 
                // loop ends, runner indicates the position where
                // deleteItem must be, if it is in the list.
            previous = runner;
            runner = runner.next;
         }
         if ( runner != null && runner.item.equals(deleteItem) ) {
                // Runner points to the node that is to be deleted.
                // Remove it by changing the pointer in the previous node.
            previous.next = runner.next;
            return true;
         }
         else {
                // The item does not exist in the list.
            return false;
         }
      }

   } // end delete()


   /**
    * Insert a specified item to the list, keeping the list in order.
    * @param insertItem the item that is to be inserted.
    */
   public void insert(String insertItem) {

      Node newNode;          // A Node to contain the new item.
      newNode = new Node();
      newNode.item = insertItem;  // (N.B.  newNode.next is null.)

      if ( head == null ) {
             // The new item is the first (and only) one in the list.
             // Set head to point to it.
         head = newNode;
      }
      else if ( head.item.compareTo(insertItem) >= 0 ) {
             // The new item is less than the first item in the list,
             // so it has to be inserted at the head of the list.
         newNode.next = head;
         head = newNode;
      }
      else {
             // The new item belongs somewhere after the first item
             // in the list.  Search for its proper position and insert it.
         Node runner;     // A node for traversing the list.
         Node previous;   // Always points to the node preceding runner.
         runner = head.next;   // Start by looking at the SECOND position.
         previous = head;
         while ( runner != null && runner.item.compareTo(insertItem) < 0 ) {
                // Move previous and runner along the list until runner
                // falls off the end or hits a list element that is
                // greater than or equal to insertItem.  When this 
                // loop ends, previous indicates the position where
                // insertItem must be inserted.
            previous = runner;
            runner = runner.next;
         }
         newNode.next = runner;     // Insert newNode after previous.
         previous.next = newNode;
      }

   }  // end insert()


   /**
    * Returns an array that contains all the elements in the list.
    * If the list is empty, the return value is an array of length zero.
    */
   public String[] getElements() {

      int count;          // For counting elements in the list.
      Node runner;        // For traversing the list.
      String[] elements;  // An array to hold the list elements.

      // First, go through the list and count the number
      // of elements that it contains.

      count = 0;
      runner = head;
      while (runner != null) {
         count++;
         runner = runner.next;
      }

      // Create an array just large enough to hold all the
      // list elements.  Go through the list again and
      // fill the array with elements from the list.

      elements = new String[count];
      runner = head;
      count = 0;
      while (runner != null) {
         elements[count] = runner.item;
         count++;
         runner = runner.next;
      }

      // Return the array that has been filled with the list elements.

      return elements;

   } // end getElements()


} // end class StringList
