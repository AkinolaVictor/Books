/**
 * This program reads a line of text entered by the user.
 * It prints a list of the letters that occur in the text,
 * and it reports how many different letters were found.
 */

public class ListLetters {
   
   public static void main(String[] args) {
   
      String str;  // Line of text entered by the user.
      int count;   // Number of different letters found in str.
      char letter; // A letter of the alphabet.
      
      TextIO.putln("Please type in a line of text.");
      str = TextIO.getln();
      
      str = str.toUpperCase();
      
      count = 0;
      TextIO.putln("Your input contains the following letters:");
      TextIO.putln();
      TextIO.put("   ");
      for ( letter = 'A'; letter <= 'Z'; letter++ ) {
          int i;  // Position of a character in str.
          for ( i = 0; i < str.length(); i++ ) {
              if ( letter == str.charAt(i) ) {
                  TextIO.put(letter);
                  TextIO.put(' ');
                  count++;
                  break;
              }
          }
      }
      
      TextIO.putln();
      TextIO.putln();
      TextIO.putln("There were " + count + " different letters.");
   
   } // end main()
   
} // end class ListLetters
