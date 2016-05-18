package netgame.fivecarddraw;

import java.io.IOException;

import netgame.common.*;

/**
 * A PokerHub manages a networked game of Five Card Draw Poker
 * between two players.  Each player is using a PokerWindow
 * as the GUI for the game.  The Hub keeps track of the full state
 * of the game, and it sends messages of type PokerGameState to
 * each player reflecting what that player needs to know about
 * the game state.  It also sends strings as messages.  These
 * strings are simply displayed by the PokerWindow to the
 * user.  Finally, when a game of poker ends because one player
 * "calls" the other, an array-of-PokerCard is sent to each player
 * with the contents of the opposing player's hand.  
 * <p>The first message is sent when the second player connects.  At 
 * that time, the Hub sends the initial PokerGameState to both players.
 * In this first message, the player's hand's are null.  They will
 * be set after the first player clicks "DEAL" to start the first game.
 * <p>As for incoming messages from the clients, the Hub recognizes:
 * (1) the string "deal" is sent when a player clicks "DEAL" to start
 * a game; (2) the string "fold" is sent when a player folds;
 * (3) a value of type Integer represents an amount that the player
 * wants to bet (the amount can be zero if the player sees or calls);
 * and (4) a value of type int[] when a player discards cards (the
 * values in the array are indices in the player's hand that the
 * player wants to discard).
 * <p>Programming note:  When a player's hand is sent as part of
 * a message, it is a cloned copy of the array that is sent.  This
 * is make sure that no object is sent more than once, after having
 * been changed in the meantime.  Because of this, it is unnecessary
 * to reset the ObjectOutputStreams that are used to send messages.
 * (See the reset() and setAutoreset() methods in the Hub class for
 * information about this issue.)
 */
public class PokerHub extends Hub {
   
   private PokerDeck deck = new PokerDeck();  // The deck of 52 playing cards.
   
   // The next five variables are possible values of the status variable.
   
   private final static int WAITING_FOR_DEAL = 0;        // Hub is waiting for a player to click "DEAL".
   private final static int WAITING_FOR_FIRST_BET = 1;   // Hub is waiting for the first bet (or fold) in a betting round.
   private final static int WAITING_FOR_BET_OR_SEE = 2;  // Hub is waiting for a later bet (or call/see/fold).
   private final static int WAITING_FOR_FIRST_DRAW = 3;  // Hub is waiting for the first player to discard cards.
   private final static int WAITING_FOR_SECOND_DRAW = 4; // Hub is waiting for the second player to discard cards.
   
   private int status; // The basic game status, one of the preceding 6 values, telling what message the hub is expecting.
   
   private int currentPlayer;  // The ID number (1 or 2) player who is to send the next message.
                               // Note that the ID number of the opposing player is always 3-currentPlayer.

   private int dealer; // The ID number (1 or 2) of the "dealer" for this game.  Player #1 deals the first hand,
                       // then the deal alternates between players.  The dealer's opponent is the first to bet
                       // in each betting round.
   
   private boolean firstBettingRound;  // If status is  
   
   private int amountNeededToSee;  // If status is WAITING_FOR_BET_OR_CALL, this is the amount
                                   // of money that is needed to "SEE" the previous bet.  It is
                                   // the minimum possible bet amount.  If the actual bet is equal
                                   // to this amount, then the current round of betting ends.
   
   private boolean previousGameTied = false;  // This is set to true at the end of a game in the
                                              // unlikely case that two player's hands are of exactly
                                              // the same value.  In that case, the "pot" stays on
                                              // the table for the next game.

   private PokerCard[][] hand = new PokerCard[2][5];  // The two player's hands.  hand[0] belongs
                                                      // to Player #1; hand[1] belongs to Player #2.
   
   private int[] money = new int[2];  // money[0] is the amount of money that Player #1 has left;
                                       // money[1] is the amount of money that Player #2 has left.
                                      // These values are initialized to 1000 at the start.  They
                                      // can become negative, and nothing is done about it if they do.
    
   private int pot;  // The total amount of money that has been bet in the current game (or in the
                     // game that has just finished.
   

   /**
    * Creates a PokerHub listening on a specified port.
    */
   public PokerHub(int port) throws IOException {
      super(port);
   }
   

   /**
    * When the second player connects, this method starts the game by
    * sending the initial game state to the two players.  At this time,
    * the players' hands are null.  The hands will be set when the
    * first hand is dealt.  This method also shuts down the Hub's 
    * ServerSocket so that no further players can connect.
    */
   protected void playerConnected(int playerID) {
      if (playerID == 2) {
         shutdownServerSocket();
         dealer = 1;
         currentPlayer = 1;
         money[0] = 1000;
         money[1] = 1000;
         sendToOne(1, new PokerGameState(null,PokerGameState.DEAL,1000,1000,0));
         sendToOne(2, new PokerGameState(null,PokerGameState.WAIT_FOR_DEAL,1000,1000,0));
         sendToAll("Ready to start the first game!");
      }
   }

   
   /**
    * If a player disconnects, the game ends.  This method shuts down
    * the Hub, which will send a signal to the remaining connected player,
    * if any, to let them know that their opponent has left the game.
    * The client will respond by terminating that player's program.
    */
   protected void playerDisconnected(int playerID) {
      shutDownHub();
   }


   /**
    * This is the method that responds to messages received from the
    * clients.  It handles all of the action of the game.  When a message
    * is received, this method will make any changes to the state of
    * the game that are triggered by the message.  It will then send
    * information about the new state to each player, and it will
    * generally send a string to each client as a message to be
    * displayed to that player.
    */
   protected void messageReceived(int playerID, Object message) {
      if (playerID != currentPlayer) {
            // This should not happen, assuming there are no bugs and the
            // connected clients are in fact PokerClients.  This test and
            // other tests in this method are included mostly for debugging.
         System.out.println("Error: message received from the wrong player.");
         return;
      }
      if (message.equals("deal")) {
         if (status != WAITING_FOR_DEAL) {
            System.out.println("Error: DEAL message received at incorrect time.");
            return;
         }
         // Suffle the deck, deal the cards, add a $5 ante from each player to the pot, and start the game.
         deck.shuffle();
         for (int i = 0; i < 5; i++) {
            hand[0][i] = deck.dealCard();
            hand[1][i] = deck.dealCard();
         }
         money[0] -= 5;
         money[1] -= 5;
         if (previousGameTied) {
            pot += 10;
            previousGameTied = false;
         }
         else
            pot = 10;
         currentPlayer = 3 - dealer;     // Dealer's opponent will bet first.
         status = WAITING_FOR_FIRST_BET; // Start the first round of betting.
         firstBettingRound = true;
         sendState(PokerGameState.BET_OR_FOLD, PokerGameState.WAIT_FOR_BET);
         sendToAll("Cards have been dealt.  $5 ante.");
      }
      else if (message.equals("fold")) {
         if (status != WAITING_FOR_FIRST_BET && status != WAITING_FOR_BET_OR_SEE) {
            System.out.println("Error: FOLD message received at incorrect time.");
            return;
         }
         // The game ends because the currentPlayer folded.  The opponent of the player who folded wins.
         gameOver(3-currentPlayer, "Your opponent has folded.","You folded");
      }
      else if (message instanceof Integer) {  // A bet.
         if (status != WAITING_FOR_FIRST_BET && status != WAITING_FOR_BET_OR_SEE) {
            System.out.println("Error: BET message received at incorrect time.");
            return;
         }
         // Apply a bet.  A bet of 0 indicates that the first player to bet has "PASSed".
         int bet = (Integer)message;
         if (bet < 0 || (status == WAITING_FOR_BET_OR_SEE && bet < amountNeededToSee)) {
            System.out.println("Error: Illegal bet amount received.");
            return;
         }
         // Move the bet amount from the player's money to the pot.  (No change if bet is 0.)
         pot += bet;
         money[currentPlayer-1] -= bet;
         // Update the state and inform the players.
         if (status == WAITING_FOR_FIRST_BET) {
               // This was the first bet in a betting round.  Betting continues.  The other
               // player must match the amount of the bet (or fold).
            sendToOne(currentPlayer, "You bet $" + bet);
            sendToOne(3-currentPlayer, "Your opponent bets $" + bet);
            currentPlayer = 3-currentPlayer;
            amountNeededToSee = bet;
            status = WAITING_FOR_BET_OR_SEE;
            if (firstBettingRound)
               sendState(PokerGameState.RAISE_SEE_OR_FOLD_ROUND_1,PokerGameState.WAIT_FOR_BET);
            else
               sendState(PokerGameState.RAISE_CALL_OR_FOLD_ROUND_2,PokerGameState.WAIT_FOR_BET);
         }
         else if (status == WAITING_FOR_BET_OR_SEE) {
               // This was not the first bet in a betting round.
            if (bet == amountNeededToSee) {
                  // If the bet is the amount needed to match the opponent's bet, 
                  // then the betting round ends.  (This is a "SEE" or "CALL".)
               if (firstBettingRound) {
                     // The first betting round has ended; proceed to the discarding of cards.
                  sendToOne(currentPlayer, "You see.  First round of betting ends.");
                  sendToOne(3-currentPlayer, "Your opponents sees.  First round of betting ends.");
                  currentPlayer = 3-dealer; // The same player who bet first, draws first.
                  status = WAITING_FOR_FIRST_DRAW;
                  sendState(PokerGameState.DRAW,PokerGameState.WAIT_FOR_DRAW);
               }
               else {
                    // The second betting round has ended; the game ends.
                  checkCardsAtEndOfGame();
               }
            }
            else {
                  // The player matches the opponent's bet and raises; the betting round ends.
                  // The opposing player must match the amount by which the player's bet
                  // exceeded the minimum that that player had to play to match the previous bet.
               amountNeededToSee = bet - amountNeededToSee;
               sendToOne(currentPlayer, "You see the bet and raise by $" + amountNeededToSee);
               sendToOne(3-currentPlayer, "Your opponent sees your bet and raises by $" + amountNeededToSee);
               currentPlayer = 3 - currentPlayer;
               if (firstBettingRound)
                  sendState(PokerGameState.RAISE_SEE_OR_FOLD_ROUND_1,PokerGameState.WAIT_FOR_BET);
               else
                  sendState(PokerGameState.RAISE_CALL_OR_FOLD_ROUND_2,PokerGameState.WAIT_FOR_BET);
            }
         }
      }
      else if (message instanceof int[]) {  // A list of card indices to be replaced
         if (status != WAITING_FOR_FIRST_DRAW && status != WAITING_FOR_SECOND_DRAW) {
            System.out.println("Error: DISCARD message received at incorrect time");
            return;
         }
         // Replace the cards that the player discarded.
         int[] cardNums = (int[])message;
         PokerCard[] currentPlayerHand = (currentPlayer == 1)? hand[0] : hand[1];
         for (int i = 0; i < cardNums.length; i++) {
            currentPlayerHand[cardNums[i]] = deck.dealCard();
         }
         sendToOne(currentPlayer,"You draw " + cardNums.length + " cards");
         sendToOne(3-currentPlayer,"Your opponent draws " + cardNums.length + " cards");
         if (status == WAITING_FOR_FIRST_DRAW) {
               // This was the first player to draw; proceed to the opposing player's draw.
            currentPlayer = 3 - currentPlayer;
            status = WAITING_FOR_SECOND_DRAW;
            sendState(PokerGameState.DRAW,PokerGameState.WAIT_FOR_DRAW);
         }
         else {
               // This was the second player's draw; proceed to the second round of betting.
            currentPlayer = 3-dealer;
            status = WAITING_FOR_FIRST_BET;
            firstBettingRound = false;
            sendState(PokerGameState.BET_OR_FOLD, PokerGameState.WAIT_FOR_BET);
         }
      }
   }
   
   
   // --- The remaining methods are called by messageReceived() to do some of its processing ---
   
   
   /**
    * When the game ends because one player has "called" the other, this method
    * will be called to compare the players' hands and determine the winner.
    * Objects of type PokerRank are used to assign rankings to each hand.
    */
   private void checkCardsAtEndOfGame() {
      PokerRank[] rank = new PokerRank[2];
      for (int i = 0; i < 2; i++) {
         rank[i] = new PokerRank();
         for (PokerCard c : hand[i])
            rank[i].add(c);
      }
      int winner; // 0 if Player #1 wins; 1 if Player #2 wins; -1 if they are tied.
      if (rank[0].getRank() > rank[1].getRank())
         winner = 0;  // Player #1's hand has higher rank.
      else if (rank[0].getRank() < rank[1].getRank())
         winner = 1;  // Player #2's hand has higher rank.
      else
         winner = -1; // Players' hands have the same rank.
      sendToOne(1,hand[1].clone());  // Send opponent's hand to Player #1.
      sendToOne(2,hand[0].clone());  // Send opponent's hand to Player #2.
      if (winner != -1) {  // One of the players won.
         gameOver(winner+1,rank[winner] + " beats " + rank[1-winner],
               rank[winner] + " beats " + rank[1-winner]);
      }
      else {  // Game was a tie.
         sendToAll("The result is a tie.  The pot stays on the table.");
         previousGameTied = true;
         dealer = 3-dealer;  // Dealer's opponent becomes dealer for next game.
         currentPlayer = dealer;
         status = WAITING_FOR_DEAL;
         sendState(PokerGameState.DEAL,PokerGameState.WAIT_FOR_DEAL);
      }
   }
   
   
   /**
    * This method is called when the game ends and there is a winner.  It gives the pot to the
    * winner, sends each player a message about the outcome of the game, changes the state
    * to get ready for the next game, and sends a state message to each player.
    * @param winner  The ID number -- 1 or 2 -- of the player who won the game.
    * @param winnerMessage A message to be sent to the winning player.
    * @param loserMessage A message to be sent to the losing player.
    */
   private void gameOver(int winner, String winnerMessage, String loserMessage) {
      sendToOne(winner, "You win. " + winnerMessage);
      sendToOne(3-winner, "You lose.  " + loserMessage); // "3-winner" is the ID number of the loser.
      money[winner-1] += pot; // The winner takes the pot; pot amount is added to winner's money.
      dealer = 3-dealer;      // Dealer's opponent becomes dealer for the next game.
      currentPlayer = dealer; // The dealer will have to start the next hand by clicking "DEAL".
      status = WAITING_FOR_DEAL;
      sendState(PokerGameState.DEAL,PokerGameState.WAIT_FOR_DEAL);
   }
   
   
   /**
    * This method is used by messageReceived() to send state messages to both
    * players.  
    * @param currentPlayerState  The state of the player who makes the next move.
    *    One of the status values from the PokerGameState class.
    * @param opponentState The state of the opposing player.
    *    One of the status values from the PokerGameState class.
    */
   private void sendState(int currentPlayerState, int opponentState) {
      int player1State, player2State;  // The states for Player #1 and #2.
      if (currentPlayer == 1) {
         player1State = currentPlayerState;
         player2State = opponentState;
      }
      else {
         player2State = currentPlayerState;
         player1State = opponentState;
      }
      if (status == WAITING_FOR_BET_OR_SEE) {  // Send a state message including an amount needed to see.
         sendToOne(1, new PokerGameState(hand[0].clone(),player1State,money[0],money[1],pot,amountNeededToSee));
         sendToOne(2, new PokerGameState(hand[1].clone(),player2State,money[1],money[0],pot,amountNeededToSee));
      }
      else { // Send a state message without an amount needed to see.
         sendToOne(1, new PokerGameState(hand[0].clone(),player1State,money[0],money[1],pot));
         sendToOne(2, new PokerGameState(hand[1].clone(),player2State,money[1],money[0],pot));
      }
   }
   
   
}
