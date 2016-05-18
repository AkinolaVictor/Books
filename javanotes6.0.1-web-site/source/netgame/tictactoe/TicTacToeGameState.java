package netgame.tictactoe;

import java.io.Serializable;



/**
 * This class holds all the necessary information to represent
 * the state of a game of networked TicTacToe.  It includes the 
 * method apply(hub,sender,message), which modifies the state to
 * reflect a message that was received from a player.  The protocol is
 * that two types of messages from client are understood.
 * One is that a TicTacToe client sends the String "newgame" as
 * a message when it wants to start a new game.  The other is that
 * when the user makes a mover into one of the squares on the board,
 * the client sends an array of two ints containing the row
 * and column where the user played.  Note that to keep things
 * simple, each time a game is started, this class decides at random 
 * which of the two players will play 'X' and which will play 'O'.  
 * X always makes the first move.
 */
public class TicTacToeGameState implements Serializable {
   
   //-------------- state variables recording the state of the game -------------------
   
   public boolean playerDisconnected;  // This is true if one of the two players has left the game
                                       // The new state, with this value set to true, is sent to
                                       // the other player as a signal that the game is over.  That
                                       // client will respond by ending the program.

   public char[][] board;  // The contents of the board. Values are ' ', 'X', or 'O'
                           // This variable is null before the first game starts.
   
   public boolean gameInProgress;  // True while a game is being played; 
                                   // false before first game and between games.

   // The next three variables are meant for use while a game is in progress.
   // Note that the ID numbers of the players will always be 1 and 2.
   
   public int playerPlayingX;   // The ID of the player who is playing X.
   public int playerPlayingO;   // The ID of the player who is playing O.
   public int currentPlayer;    // The ID of the player who is to make the next move.
   
   // The next two variables are meant for use between games.
   
   public boolean gameEndedInTie; // Tells whether the game ended in a tie.
   public int winner;   // The name of winner of the game that just ended, if it was not a tie.
   
   
   
   //----------- the method that is called by the Hub to react to messages from the players -----------
   
   /**
    *  Respond to a message that was sent by one of the players to the hub.
    *  Note that illegal messages (of the wrong type or coming at an illegal
    *  time) are simply ignored.  The messages that are understood are
    *  the string "newgame" for starting a new game and an array of two
    *  ints giving the row and column of a move that the user wants to make.
    *  @param sender the ID number of the player who sent the message.
    *  @param message the message that was received from that player.
    */
   public void applyMessage(int sender, Object message) {
      if (gameInProgress && message instanceof int[] && sender == currentPlayer) {
             // The message represents a move by the current player.
         int[] move = (int[])message;
         if (move == null || move.length != 2)
            return;
         int row = move[0];
         int col = move[1];
         if (row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != ' ')
            return;
         board[row][col] = (currentPlayer == playerPlayingX)? 'X' : 'O'; // Make the move.
         if (winner()) { // CurrentPlayer has won.
            gameInProgress = false;
            winner = currentPlayer;
         }
         else if (tie()) { // The board is full but there is no winner; game ends in a tie.
            gameInProgress = false;
            gameEndedInTie = true;
         }
         else {  // It's the other player's turn now.
            currentPlayer = (currentPlayer == playerPlayingX)? playerPlayingO : playerPlayingX;
         }
      }
      else if (!gameInProgress && message.equals("newgame")) {
         startGame();
      }
   }
   
   /**
    * This package private method is called by the hub when the second player
    * connects.  It's purpose is to start the first game.
    */
   void startFirstGame() {
      startGame();
   }
   
   
   //------------------- Some private utility methods used by the apply() method ---------------
   
   /**
    * Start a game.  Board is initialized to empty.  Players are
    * randomly assigned to play 'X' or 'O'. 
    */
   private void startGame() {
      board = new char[3][3];
      for (int i = 0; i < 3; i++)
         for (int j = 0; j < 3; j++) {
            board[i][j] = ' ';
         }
      int xPlr = (Math.random() < 0.5)? 1 : 2;
      playerPlayingX = xPlr;  // Will be 1 or 2.
      playerPlayingO = 3 - xPlr;  // The other player ( 3 - 1 = 2, and 3 - 2 = 1 )
      currentPlayer = playerPlayingX;
      gameEndedInTie = false;
      winner = -1;
      gameInProgress = true;
   }
   
   /**
    * Check if there is a winner, i.e. three pieces of the same kind in a row.
    */
   private boolean winner() {
      if (board[0][0] != ' ' && 
            (board[0][0] == board[1][1]&& board[1][1] == board[2][2]))
         return true;
      if (board[0][2] != ' ' && 
            (board[0][2] == board[1][1]&& board[1][1] == board[2][0]))
         return true;
      for (int row = 0; row < 3; row++) {
         if (board[row][0] != ' ' &&
               (board[row][0] == board[row][1] && board[row][1] == board[row][2]))
            return true;
      }
      for (int col = 0; col < 3; col++) {
         if (board[0][col] != ' ' &&
               (board[0][col] == board[1][col] && board[1][col] == board[2][col]))
            return true;
      }
      return false;
   }
   
   /**
    * Check if the board is full.  (This is called after the winner method
    * has returned false, so a full board means that the game is a tie.)
    */
   private boolean tie() {
      for (int i = 0; i < 3; i++)
         for (int j = 0; j < 3; j++)
            if (board[i][j] == ' ')
               return false;
      return true;
   }
   
}
