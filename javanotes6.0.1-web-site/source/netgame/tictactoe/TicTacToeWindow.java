package netgame.tictactoe;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.IOException;
import netgame.common.*;

/**
 * This window represents one player in a two-player networked
 * game of TicTacToe.  The window is meant to be created by
 * the program netgame.tictactoe.Main.
 */
public class TicTacToeWindow extends JFrame {
   
   /**
    * The state of the game.  This state is a copy of the official
    * state, which is stored on the server.  When the state changes,
    * the state is sent as a message to this window.  (It is actually
    * sent to the TicTacToeClient object that represents the connection
    * to the server.)  When that happens, the state that was received
    * in the message replaces the value of this variable, and the
    * board and UI is updated to reflect the changed state.  This
    * is done in the newState() method, which is called by the
    * TicTacToeClent object.
    */
   private TicTacToeGameState state;
   
   
   private Board board;     // A panel that displays the board.  The user
                            // makes moves by clicking on this panel.

   private JLabel message;  // Displays messages to the user about the status of the game.
   
   private int myID;        // The ID number that identifies the player using this window.

   private TicTacToeClient connection;  // The Client object for sending and receiving 
                                        // network messages.
   
   /**
    * This class defines the client object that handles communication with the Hub.
    */
   private class TicTacToeClient extends Client {

      /**
       * Connect to the hub at a specified host name and port number.
       */
      public TicTacToeClient(String hubHostName,int hubPort) throws IOException {
         super(hubHostName, hubPort);
      }

      /**
       * Responds to a message received from the Hub.  The only messages that
       * are supported are TicTacToeGameState objects.  When one is received,
       * the newState() method in the TicTacToeWindow class is called.  To avoid
       * problems with synchronization, that method is called using
       * SwingUtilities.invokeLater() so that it will run in the GUI event thread.
       */
      protected void messageReceived(final Object message) {
         if (message instanceof TicTacToeGameState) {
            SwingUtilities.invokeLater(new Runnable(){
               public void run() {  // calls a method at the end of the TicTacToeWindow class
                  newState( (TicTacToeGameState)message ); 
               }
            });
         }
      }

      /**
       * If a shutdown message is received from the Hub, the user is notified
       * and the program ends.
       */
      protected void serverShutdown(String message) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               JOptionPane.showMessageDialog(TicTacToeWindow.this, 
                     "Your opponent has disconnected.\nThe game is ended.");
               System.exit(0);
            }
         });
      }
      
   }
   
   
   /**
    * A JPanel that draws the TicTacToe board.
    */
   private class Board extends JPanel {  // Defines the board object
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         if (state == null || state.board == null) {
            g.drawString("Starting up.", 20, 35);
            return;
         }
         ((Graphics2D)g).setStroke(new BasicStroke(10));
         ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g.drawLine(150,50,150,350);
         g.drawLine(250,50,250,350);
         g.drawLine(50,150,350,150);
         g.drawLine(50,250,350,250);
         for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
               if (state.board[row][col] == 'X') {
                  g.setColor(Color.RED);
                  g.drawLine(70+col*100, 70+row*100, 130+col*100, 130+row*100);
                  g.drawLine(70+col*100, 130+row*100, 130+col*100, 70+row*100);
               }
               else if (state.board[row][col] == 'O') {
                  g.setColor(Color.BLUE);
                  g.drawOval(65+col*100,65+row*100, 70, 70);
               }
            }
         }
      }
   }


   /**
    * Creates and configures the window, opens a connection to the server, and makes
    * the widow visible on the screen.  This constructor can block until the connection
    * is established.
    * @param hostName  the name or IP address of the host where the server is running.
    * @param serverPortNumber  the port number on the server computer when the Hub is listening for connections.
    * @throws IOException if some I/O error occurs while trying to open the connection.
    * @throws Client.DuplicatePlayerNameException  it playerName is already in use by another player in the game.
    */
   public TicTacToeWindow(String hostName, int serverPortNumber)  throws IOException {
      super("Net TicTacToe");
      connection = new TicTacToeClient(hostName, serverPortNumber);
      myID = connection.getID();
      board = new Board();
      message = new JLabel("Waiting for two players to connect.", JLabel.CENTER);
      board.setBackground(Color.WHITE);
      board.setPreferredSize(new Dimension(400,400));
      board.addMouseListener(new MouseAdapter() { // A mouse listener to respond to user's clicks.
         public void mousePressed(MouseEvent evt) {
            doMouseClick(evt.getX(), evt.getY());
         }
      });
      message.setBackground(Color.LIGHT_GRAY);
      message.setOpaque(true);
      JPanel content = new JPanel();
      content.setLayout(new BorderLayout(2,2));
      content.setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
      content.setBackground(Color.GRAY);
      content.add(board,BorderLayout.CENTER);
      content.add(message,BorderLayout.SOUTH);
      setContentPane(content);
      pack();
      setResizable(false);
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      addWindowListener(new WindowAdapter(){
             // When the user clicks the window's close box, this listener will
             // send a disconnect message to the Hub and will end the program.
             // The other player will then be notified that this player has disconnected.
         public void windowClosing(WindowEvent evt) {
            dispose();
            connection.disconnect();  // Send a disconnect message to the hub.
            try {
               Thread.sleep(333); // Wait one-half second to allow the message to be sent.
            }
            catch (InterruptedException e) {
            }
            System.exit(0);
         }
      });
      setLocation(200,100);
      setVisible(true);
   }
   
   
   /**
    * This method is called when the user clicks the tictactoe board.  If the
    * click represents a legal move at a legal time, then a message is sent
    * to the Hub to inform it of the move.  The Hub will change the game
    * state and send the new state to both players.  It is very important that
    * the game clients do not change the game state directly, since the
    * "official" game state is maintained by the Hub.  Doing things this
    * way guarantees that both players see the same board.
    */
   private void doMouseClick(int x, int y) {
      if (state == null || state.board == null)
         return;
      if (!state.gameInProgress) {
            // After a game ends, the winning player -- or either
            // player in the event of a tie -- can start a new
            // game by clicking the board.  When this happens,
             // the String "newgame" is sent as a message to Hub.
         if (state.gameEndedInTie || myID == state.winner)
            connection.send("newgame");  // Start a new game.
         return;
      }
      if (myID !=state.currentPlayer) {
         return;  // it's not this player's turn.
      }
      int row = (y-50) / 100;
      int col = (x-50) / 100;
      if (row >= 0 && row < 3 && col >= 0 && col < 3 && state.board[row][col] == ' ' ) {
            // User has clicked an empty square.  Send the move to the Hub
            // as an array of two ints containing the row number and column
            // number of the square where the user clicked.
         connection.send( new int[] { row, col } );
      }
   }
   
   
   /**
    * This method is called when a new game state is received from the hub.
    * It stores the new state in the instance variable that represents the
    * game state and updates the user interface to reflect the state.
    * Note that this method is called on the GUI event thread (using
    * SwingUtilitites.invokeLater()) to avoid synchronization problems.
    * (Synchronization is an issue when a method that manipulates the
    * GUI is called from a thread other than the GUI event thread.  In this
    * problem, there is also the problem that a message can actually be
    * received before the constructor has completed, which would lead to errors
    * in this method from uninitialized variables, if SwingUtilities.invokeLater()
    * were not used.)
    */
   private void newState(TicTacToeGameState state) {
      if ( state.playerDisconnected ) {
         JOptionPane.showMessageDialog(this, "Your opponent has disconnected.\nThe game is ended.");
         System.exit(0);
      }
      this.state = state;
      board.repaint();
      if ( state.board == null ) {
         return;  // haven't started yet -- waiting for 2nd player
      }
      else if ( ! state.gameInProgress ) {
         setTitle("Game Over");
         if ( state.gameEndedInTie )
            message.setText("Game ended in tie. Click to start again.");
         else if (myID == state.winner)
            message.setText("You won!  Click to start a new game.");
         else
            message.setText("You lost.  Waiting for new game...");
      }
      else {
         if (myID == state.playerPlayingX)
            setTitle("You are playing X's");
         else
            setTitle("You are playing O's");
         if (myID == state.currentPlayer)
            message.setText("Your move");
         else
            message.setText("Waiting for opponent's move");
      }
   }

}
