
/**
 * This program lets the user play one or more guessing games.  In each
 * game, the computer selects a number in the range 1 to 100.  The use
 * tries to guess the numbers.  If the user's guess is right, the user
 * wins the game.  If the user makes six incorrect guesses, the user
 * loses the game.   The computer tells the user whether his guess is
 * high or low.  After each game, the computer asks the user whether
 * the user wants to play again.
 */
public class GuessingGame {

   public static void main(String[] args) {
      TextIO.putln("Let's play a game.  I'll pick a number between");
      TextIO.putln("1 and 100, and you try to guess it.");
      boolean playAgain;
      do {
         playGame();  // call subroutine to play one game
         TextIO.put("Would you like to play again? ");
         playAgain = TextIO.getlnBoolean();
      } while (playAgain);
      TextIO.putln("Thanks for playing.  Goodbye.");
   } // end of main()            
   

   /**
    * This subroutine lets the user play one guessing game and tells
    * the user whether he won or lost.
    */
   static void playGame() {
       int computersNumber; // A random number picked by the computer.
       int usersGuess;      // A number entered by user as a guess.
       int guessCount;      // Number of guesses the user has made.
       computersNumber = (int)(100 * Math.random()) + 1;
                // The value assigned to computersNumber is a randomly
                //    chosen integer between 1 and 100, inclusive.
       guessCount = 0;
       TextIO.putln();
       TextIO.put("What is your first guess? ");
       while (true) {
          usersGuess = TextIO.getInt();  // Get the user's guess.
          guessCount++;
          if (usersGuess == computersNumber) {
             TextIO.putln("You got it in " + guessCount
                     + " guesses!  My number was " + computersNumber);
             break;  // The game is over; the user has won.
          }
          if (guessCount == 6) {
             TextIO.putln("You didn't get the number in 6 guesses.");
             TextIO.putln("You lose.  My number was " + computersNumber);
             break;  // The game is over; the user has lost.
          }
          // If we get to this point, the game continues.
          // Tell the user if the guess was too high or too low.
          if (usersGuess < computersNumber)
             TextIO.put("That's too low.  Try again: ");
          else if (usersGuess > computersNumber)
             TextIO.put("That's too high.  Try again: ");
       }
       TextIO.putln();
   } // end of playGame()
               
} // end of class GuessingGame
