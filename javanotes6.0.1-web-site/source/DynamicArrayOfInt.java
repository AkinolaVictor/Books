/**
 *  An object of type DynamicArrayOfInt acts like an array of int
 *  of unlimited size.  The notation A.get(i) must be used instead
 *  of A[i], and A.set(i,v) must be used instead of A[i] = v.
 */
public class DynamicArrayOfInt {

   private int[] data;  // An array to hold the data.

   /**
    * Constructor creates an array with an initial size of 1,
    * but the array size will be increased whenever a reference
    * is made to an array position that does not yet exist.
    */   
   public DynamicArrayOfInt() {
      data = new int[1];
   }
   
   /**
    *  Get the value from the specified position in the array.
    *  Since all array elements are initialized to zero, when the
    *  specified position lies outside the actual physical size
    *  of the data array, a value of 0 is returned.  Note that
    *  a negative value of position will still produce an
    *  ArrayIndexOutOfBoundsException.
    */
   public int get(int position) {
      if (position >= data.length)
         return 0;
      else
         return data[position];
   }
   
   /**
    *  Store the value in the specified position in the array.
    *  The data array will increase in size to include this
    *  position, if necessary.
    */
   public void put(int position, int value) {
      if (position >= data.length) {
             // The specified position is outside the actual size of
             // the data array.  Double the size, or if that still does
             // not include the specified position, set the new size
             // to 2*position. 
         int newSize = 2 * data.length;
         if (position >= newSize)
            newSize = 2 * position;
         int[] newData = new int[newSize];
         System.arraycopy(data, 0, newData, 0, data.length);
         data = newData;
             // The following line is for demonstration purposes only !!
         System.out.println("Size of dynamic array increased to " + newSize);
      }
      data[position] = value;
   }

} // end class DynamicArrayOfInt
