
/**
 * This program lists the steps in the solution of the TowersOfHanoi
 * problem.  The number of disks to be moved is specified by the user.
 * Warning:  The number of moves grows very quickly with the number of
 * disks!
 */
public class TowersOfHanoi {

   public static void main(String[] args) {

      int N;  // The number of disks in the original stack,
              //   as specified by the user.

      TextIO.putln("This applet will list the steps in the solution of");
      TextIO.putln("the Towers of Hanoi problem.  You can specify the");
      TextIO.putln("number of disks to be moved.  Try it for small numbers");
      TextIO.putln("of disks, like 1, 2, 3, and 4.");
      TextIO.putln();
      TextIO.putln("How many disks are to be moved from Stack 0 to Stack 1?");
      TextIO.putln();
      TextIO.put("? ");

      N = TextIO.getInt();

      TextIO.putln();
      TextIO.putln();

      towersOfHanoi(N, 0, 1, 2);  // Print the solution.

   }

   /**
    * Solve the problem of moving the number of disks specified
    * by the first parameter from the stack specified by the 
    * second parameter to the stack specified by the third 
    * parameter.  The stack specified by the fourth parameter 
    * is available for use as a spare.  Stacks are specified by
    * number: 0, 1, or 2.
    */
   static void towersOfHanoi(int disks, int from, int to, int spare) {
      if (disks == 1) {
            // There is only one disk to be moved.  Just move it.
         System.out.println("Move a disk from stack number "
                  + from + " to stack number " + to);
      }
      else {
            // Move all but one disk to the spare stack, then
            // move the bottom disk, then put all the other
            // disks on top of it.
         towersOfHanoi(disks-1, from, spare, to);
         System.out.println("Move a disk from stack number "
                  + from + " to stack number " + to);
         towersOfHanoi(disks-1, spare, to, from);
      }
   }

}
