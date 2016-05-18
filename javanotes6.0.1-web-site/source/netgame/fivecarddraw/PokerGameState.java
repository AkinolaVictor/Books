package netgame.fivecarddraw;

import java.io.Serializable;

/**
 * Represents the state of a game of five-card-draw poker 
 * from one player's point of view.  The full state of a game
 * is kept by a PokerHub.  That hub sends messages of type
 * PokerGameState to each player whenever the state of 
 * the game changes.  Note that the two players receive
 * different messages, to reflect each player's view of the
 * status of the game.
 */
public class PokerGameState implements Serializable {
   
   //-------------------------------------------------------------
   // The eight following constants are the possible values of
   // status.  The status is the basic information that tells
   // a player what it should be doing at a given time.
   
   public final static int DEAL = 0;   // The player must click "DEAL" to start the game.
   public final static int BET_OR_FOLD = 1;  // The player must make the first bet in a betting round, or fold.
   public final static int RAISE_SEE_OR_FOLD_ROUND_1 = 2;   // During first round of betting, player must respond
                                                            // to pponent's bet by raising or matching the bet, or folding.
   public final static int RAISE_CALL_OR_FOLD_ROUND_2 = 3;  // During second round of betting, player must respond
                                                            // to pponent's bet by raising or matching the bet, or folding.
   public final static int DRAW = 4;  // The player must select cards to discard, and click "DRAW".
   
   public final static int WAIT_FOR_DEAL = 5;  // Wait for opposing player to start the game.
   public final static int WAIT_FOR_BET = 6;   // Wait for opposing player to BET (or fold).
   public final static int WAIT_FOR_DRAW = 7;  // Wait for opposing player to discard cards.
   
   //-------------------------------------------------------------
   
   
   public int status;         // Game status; one of the constants defined in this class.

   public final PokerCard[] hand;   // Player's hand; null before game starts.
   
   public int money;          // Amount of money that player has left.
   public int opponentMoney;  // Amount of money that the opposing player has left.
   public int pot;            // Amount of money that has been bet in the current game.
   
   public int amountToSee;    // When status is RAISE_SEE_OR_FOLD_ROUND_1 or RAISE_CALL_OR_FOLD_ROUND_2, 
                              // this is the opponent's bet amount which must be matched.

   /**
    * Create a PokerGameState object with amountToCall equal to 0  and with specified values for 
    * the other public variables in this class.
    */
   public PokerGameState(PokerCard[] hand, int status, int money, int opponentMoney, int pot) {
      this(hand,status,money,opponentMoney,pot,0);
   }

   /**
    * Create a PokerGameState object with specified values for all public variables in this class.
    */
   public PokerGameState(PokerCard[] hand, int status, int money, int opponentMoney, int pot, int amountToSee) {
      this.hand = hand;
      this.status = status;
      this.money = money;
      this.opponentMoney = opponentMoney;
      this.pot = pot;
      this.amountToSee = amountToSee;
   }

}
