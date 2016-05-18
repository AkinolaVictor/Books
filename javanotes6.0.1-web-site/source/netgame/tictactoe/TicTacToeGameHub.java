package netgame.tictactoe;

import java.io.IOException;

import netgame.common.Hub;

/**
 * A "Hub" for the network TicTacToe game.  There is only one Hub
 * for a game, and both network players connect to the same Hub.
 * Official information about the state of the game is maintained
 * on the Hub.  When the state changes, the Hub sends the new 
 * state to both players, ensuring that both players see the
 * same state.
 */
public class TicTacToeGameHub extends Hub {
   
   private TicTacToeGameState state;  // Records the state of the game.

   /**
    * Create a hub, listening on the specified port.  Note that this
    * method calls setAutoreset(true), which will cause the output stream
    * to each client to be reset before sending each message.  This is
    * essential since the same state object will be transmitted over and
    * over, with changes between each transmission.
    * @param port the port number on which the hub will listen.
    * @throws IOException if a listener cannot be opened on the specified port.
    */
   public TicTacToeGameHub(int port) throws IOException {
      super(port);
      state = new TicTacToeGameState();
      setAutoreset(true);
   }

   /**
    * Responds when a message is received from a client.  In this case,
    * the message is applied to the game state, by calling state.applyMessage().
    * Then the possibly changed state is transmitted to all connected players.
    */
   protected void messageReceived(int playerID, Object message) {
      state.applyMessage(playerID, message);
      sendToAll(state);
   }

   /**
    * This method is called when a player connects.  If that player
    * is the second player, then the server's listening socket is
    * shut down (because only two players are allowed), the 
    * first game is started, and the new state -- with the game
    * now in progress -- is transmitted to both players.
    */
   protected void playerConnected(int playerID) {
      if (getPlayerList().length == 2) {
         shutdownServerSocket();
         state.startFirstGame();
         sendToAll(state);
      }
   }

   /**
    * This method is called when a player disconnects.  This will
    * end the game and cause the other player to shut down as
    * well.  This is accomplished by setting state.playerDisconnected
    * to true and sending the new state to the remaining player, if 
    * there is one, to notify that player that the game is over.
    */
   protected void playerDisconnected(int playerID) {
      state.playerDisconnected = true;
      sendToAll(state);
   }
}
