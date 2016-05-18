package netgame.fivecarddraw;

import netgame.common.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;

/**
 * A window for one player in a two-player networked game of
 * Five Card Draw Poker.  The window is created by the main 
 * program, netgem.fivecarddraw.*.
 * <p>When a PokerWindow is opened, it establishes a
 * a connection to a PokerHub at a specified host and port.
 * Once two players have connected to the hub, the hub will
 * manage a game of poker between the two players.  Each
 * player is given $1000 to start.  One player acts a 
 * "dealer", and the dealer clicks a "DEAL" button to start
 * the game.  (The role of dealer alternates between players.)
 * After the cards are dealt, there is a first round of
 * betting.  The non-dealer player bets first.  When either
 * player matches the other's bet, the betting round ends and
 * each player has a chance to discard some cards and draw
 * new ones.  Then there is a second round of betting.  When
 * that round end, the players' hands are compared, and the
 * winner is announced.  (Players also have the choice of 
 * folding, instead of betting, which will end the game
 * immediately.)  Note that the player's amount of money
 * can become negative, and nothing is done when that
 * happens.  The game continues until one or the other
 * player quits.  The other player is notified so that
 * the second player's program can also be terminated.
 * <p>For details of the communication protocol that
 * is used between the PokerWindows and the PokerHub,
 * see the comments on the PokerHub class.
 */
public class PokerWindow extends JFrame {
   
   
   /**
    * The constructor sets up the window and makes it visible on the screen.  
    * It starts a thread that will open a connection to a PokerHub.
    * The window will become operational when the game stops, or it will be closed
    * and the program terminated if the connection attempt fails.
    * @param hubHostName the host name or IP address where the PokerHub is listening.
    * @param hubPort the port number where the PokerHub is listening.
    */
   public PokerWindow(final String hubHostName, final int hubPort) {
      super("NetPoker");
      ClassLoader cl = getClass().getClassLoader();
      URL imageURL = cl.getResource("netgame/fivecarddraw/cards.png");  // Image required for drawing cards.
      cardImages = Toolkit.getDefaultToolkit().createImage(imageURL);
      display = new Display();
      setContentPane(display);
      pack();
      setResizable(false);
      setLocation(200,100);
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      addWindowListener( new WindowAdapter() {  // A listener to end the program when the user closes the window.
         public void windowClosing(WindowEvent evt) {
            doQuit();
         }
      });
      display.addMouseListener( new MouseAdapter(){  // Respond to clicks on the display by calling the doClick() method.
         public void mousePressed(MouseEvent evt) {
            doClick(evt.getX(),evt.getY());
         }
      });
      setVisible(true);
      new Thread() {  // A thread to open the connection to the server.
         public void run() {
            try {
               final PokerClient c = new PokerClient(hubHostName,hubPort);
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     connection = c;
                     if (c.getID() == 1) { 
                           // This is Player #1.  Still have to wait for second player to
                           // connect.  Change the message display to reflect that fact.
                          messageFromServer.setText("Waiting for an opponent to connect...");
                     }
                  }
               });
            }
            catch (final IOException e) {
                  // Error while trying to connect to the server.  Tell the
                  // user, and end the program.  Use SwingUtilties.invokeLater()
                  // because this happens in a thread other than the GUI event thread.
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     dispose();
                     JOptionPane.showMessageDialog(null,"Could not connect to "
                           + hubHostName +".\nError:  " + e);
                     System.exit(0);
                  }
               });
            }
         }
      }.start();
   }
   
   
   // ---------------------- Private inner classes -----------------------------------
   
   
   /**
    * A PokerClient is a netgame client that handles communication
    * with the PokerHub.  It is used by the PokerWindow class to
    * send messages to the hub.  When messages are received from
    * the hub, it takes an appropriate action.
    */
   private class PokerClient extends Client {

      /**
       * Connect to a PokerHub at a specified hostname and port number.
       */
      public PokerClient(String hubHostName, int hubPort) throws IOException {
         super(hubHostName, hubPort);
      }

      /**
       * This method is called when a message from the hub is received 
       * by this client.  If the message is of type PokerGameState,
       * then the newState() method in the PokerWindow class is called
       * to handle the change in the state of the game.  If the message
       * is of type String, it represents a message that is to be
       * displayed to the user; the string is displayed in the JLabel
       * messageFromServer.  If the message is of type PokerCard[],
       * then it is the opponent's hand.  This had is sent when the
       * game has ended and the player gets to see the opponent's hand.
       * <p>Note that this method is called from a separate thread, not
       * from the GUI event thread.  In order to avoid synchronization
       * issues, this method uses SwingUtilties.invokeLater() to carry 
       * out its task in the GUI event thread.
       */
      protected void messageReceived(final Object message) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               if (message instanceof PokerGameState)
                  newState( (PokerGameState)message );
               else if (message instanceof String)
                  messageFromServer.setText( (String)message );
               else if (message instanceof PokerCard[]) {
                  opponentHand = (PokerCard[])message;
                  display.repaint();
               }
            }
         });
      }

      /**
       * This method is called when the hub shuts down.  That is a signal
       * that the oppsing player has quit the game.  The user is informed
       * of this, and the program is terminated.
       */
      protected void serverShutdown(String message) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               JOptionPane.showMessageDialog(PokerWindow.this,
                     "Your opponent has quit.\nThe game is over.");
               System.exit(0);
            }
         });
      }
      
   } // end nested class PokerClient
   

   
   /**
    * The display class defines a JPanel that is used as the content
    * pane for the PokerWindow.
    */
   private class Display extends JPanel {
      
      final Color brown = new Color(130,70,0);
      final Color green = new Color(0,100,0);
      
      /**
       * The constructor creates labels,  buttons, and a text field and adds
       * them to the panel.  An action listener of type ButtonHandler is created
       * and is added to all the buttons and the text field.
       */
      Display() {
         setLayout(null);  // Layout will be done by hand.
         setPreferredSize(new Dimension(675,585));
         setBackground(new Color(255,245,200));
         setBorder(BorderFactory.createLineBorder(brown, 3));
         opponentsMoney = makeLabel(100,10,400,30,24,green);
         messageFromServer = makeLabel(30,205,500,25,16,brown);
         message = makeLabel(30,325,500,25,16,brown);
         pot = makeLabel(150,255,300,45,38,green);
         money = makeLabel(100,535,400,30,24,green);
         messageFromServer.setText("WAITING FOR CONNECTION");
         ButtonHandler listener = new ButtonHandler();
         dealButton = makeButton("DEAL",575,60,listener);
         drawButton = makeButton("DRAW",575,120,listener);
         betButton = makeButton("BET",575,180,listener);
         passButton = makeButton("PASS",575,290,listener);
         callButton = makeButton("CALL",575,350,listener);
         foldButton = makeButton("FOLD",575,410,listener);
         quitButton = makeButton("QUIT",575,470,listener);
         quitButton.setEnabled(true);
         betInput = new JTextField();
         betInput.setMargin(new Insets(2,2,2,2));
         betInput.setEditable(false);
         add(betInput);
         betInput.setBounds(595,220,64,28);
         betInput.addActionListener(listener);
      }
      
      /**
       * Utility routine used by constructor to make a label and add it to the
       * panel.  The label has specified bounds, font size, and color, and its
       * text is initially empty.
       */
      JLabel makeLabel(int x, int y, int width, int height, int fontSize, Color color) {
         JLabel label = new JLabel();
         add(label);
         label.setBounds(x,y,width,height);
         label.setOpaque(false);
         label.setForeground(color);
         label.setFont(new Font("Serif", Font.BOLD, fontSize));
         return label;
      }
      
      /**
       * Utility routine used by the constructor to make a button and add it
       * to the panel. The button has a specified text and (x,y) position and
       * is 80-by-35 pixels.  An action listener is added to the button.
       */
      JButton makeButton(String text, int x, int y, ActionListener listener) {
         JButton button = new JButton(text);
         add(button);
         button.setEnabled(false);
         button.setBounds(x,y,80,35);
         setFont(new Font("SansSerif", Font.BOLD, 24));
         button.addActionListener(listener);
         return button;
      }
      
      /**
       * The paint component just draws the cards, when appropriate.  The remaining
       * content of the panel consists of sub-components (labels, buttons, text field).
       */
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         if (state == null) {
               // Still waiting for connections.  Don't draw anyting.
            return;
         }
         if (state.hand == null) {
               // This happens only while waiting for the first hand to be dealt.
               // Draw outlines of the card locations for this player's hand.
            g.setColor(brown);
            for (int x = 25; x < 500; x += 105)
               g.drawRect(x,380,80,124);
         }
         else {
               // Draw the cards in this player's hand.
            for (int i = 0; i < 5; i++) {
               if (discard != null && discard[i])
                  drawCard(g,null,25+i*105,380);
               else
                  drawCard(g,state.hand[i],25+i*105,380);
            }
         }
         if (state.hand == null) {
               // This happens only while waiting for the first hand to be dealt.
               // Draw outlines of the card locations for the opponent's hand.
            g.setColor(brown);
            for (int x = 25; x < 500; x += 105)
               g.drawRect(x,70,80,124);
         }
         else if (opponentHand == null) {
               // The opponent's hand exists but is unknown.  Draw it as face-down cards.
            for (int i = 0; i < 5; i++)
               drawCard(g,null,25+i*105,70);
         }
         else {
               // The opponent's hand is known.  Draw the cards.
            for (int i = 0; i < 5; i++)
               drawCard(g,opponentHand[i],25+i*105,70);
         }
      }
      
   } // end nested class Display
   
   
   
   /**
    * A class to define the action listener that responds when the user clicks
    * a button or presses return while typing in the text field.  Note that 
    * once an action is taken, the buttons that were enabled are disabled,
    * to prevent the user from generating extra messages while the hub is
    * processing the user's action.
    */
   private class ButtonHandler implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         Object src = evt.getSource();
         if (src == quitButton) {  // end the program
            doQuit();
         }
         else if (src == dealButton) { 
                // send "deal" as a message to the hub, which will start the next hand of the game
            dealButton.setEnabled(false);
            connection.send("deal");
         }
         else if (src == foldButton) { 
               // send "fold" as a message to the hub, which will end the game because this user folded
            foldButton.setEnabled(false);
            betButton.setEnabled(false);
            passButton.setEnabled(false);
            callButton.setEnabled(false);
            betInput.setEditable(false);
            betInput.setText("");
            connection.send("fold");
         }
         else if (src == passButton) { 
               // send the integer 0 as a message, indicating that the user places no bet;
               // this is only possible for the first bet in a betting round
            foldButton.setEnabled(false);
            betButton.setEnabled(false);
            passButton.setEnabled(false);
            callButton.setEnabled(false);
            betInput.setEditable(false);
            betInput.setText("");
            connection.send(new Integer(0));
         }
         else if (src == callButton) { 
               // send an integer equal to the minimum possible bet as a message to the hub;
               // this means "see" in the first betting round and "call" in the second, and
               // it will end the current betting round.
            foldButton.setEnabled(false);
            betButton.setEnabled(false);
            passButton.setEnabled(false);
            callButton.setEnabled(false);
            betInput.setEditable(false);
            betInput.setText("");
            connection.send(new Integer(state.amountToSee));
         }
         else if (src == drawButton) {
               // Send the list of cards that the user wants to discard as a message to
               // the hub.  The cards are recorded in the discard array.
            int ct = 0;
            for (int i = 0; i < 5; i++) {  // count the number of discarded cards.
               if (discard[i])
                  ct++;
            }
            if (ct == 0) {
               int resp = JOptionPane.showConfirmDialog(PokerWindow.this,
                     "Are you sure that you want to draw ZERO cards?\n"
                        +"If not, click 'No' and select the cards taht"
                        +"you want to discard.", 
                      "Discarding no cards?", 
                      JOptionPane.YES_NO_OPTION);
               if (resp != JOptionPane.YES_OPTION)
                  return;
            }
            int[] cardNums = new int[ct];
            int j = 0;
            for (int i = 0; i < 5; i++) {  // Put indices of discarded cards into an array to be send to the hub. 
               if (discard[i])
                  cardNums[j++] = i;
            }
            discard = null;  // We are finished with the discard array.
            drawButton.setEnabled(false);
            connection.send(cardNums);
         }
         else if (src == betButton || src == betInput) {
               // User wants to place a bet.  Check that the bet is legal.  If it is,
               // send the bet amount as a message to the hub.
            int amount;
            try {
               amount = Integer.parseInt(betInput.getText().trim());
               if (amount <= 0)
                  throw new NumberFormatException();
            }
            catch (NumberFormatException e) {
               JOptionPane.showMessageDialog(PokerWindow.this,
                     "The bet amount must be\na legal positive integer.");
               betInput.selectAll();
               betInput.requestFocus();
               return;
            }
            if ( (state.status == PokerGameState.RAISE_SEE_OR_FOLD_ROUND_1 ||
                  state.status == PokerGameState.RAISE_CALL_OR_FOLD_ROUND_2) && amount < state.amountToSee) {
               JOptionPane.showMessageDialog(PokerWindow.this,
                          "Your bet must be at least " + state.amountToSee
                          + "\n to match your opponent's bet.");
               betInput.selectAll();
               betInput.requestFocus();
               return;
            }
            betInput.setEditable(false);
            betInput.setText("");
            connection.send(new Integer(amount));
         }
      }
   } // end nested class ButtonHandler
   
   

   // ------------------- Private member variables and methods ---------------------------
   
   
   private PokerClient connection;   // Handles communication with the PokerHub; used to send messages to the hub.

   private PokerGameState state;     // Represents the state of the game, as seen by this player.  The state is
                                     //   received as a message from the hub whenever the state changes.  This
                                     //   variable changes only in the newState() method.
   
   private boolean[] discard;        // When the player is discarding cards, this array tells which cards the
                                     //   player wants to discard.  discard[i] is true if player is discarding 
                                     //   the i-th card in the hand.
   
   private PokerCard[] opponentHand; // The opponent's hand.  This variable is dull during the playing of a
                                     //   hand.  It becomes non-null if the opponent's hand is sent to this
                                     //   player at the end of one hand of poker.
   
   private Display display;          // The content pane of the window, defined by the inner class, Display.

   private Image cardImages;         // An image holding pictures of all the cards.  The Image is loaded
                                     // as a resource by the PokerWindow constructor from a resource file
                                     // "netgame/fivecarddraw/cards.png."   (The program will be non-functional
                                     // if that resource file is not there.)
   
   
   private JButton dealButton;   // User interface components that are be added to the display panel.
   private JButton drawButton;
   private JButton betButton;
   private JButton callButton;
   private JButton passButton;
   private JButton foldButton;
   private JButton quitButton;
   private JTextField betInput;
   private JLabel message;
   private JLabel messageFromServer;
   private JLabel money, opponentsMoney, pot;
   
   
   
   /**
    * This method is called when a new PokerGameState is received from the PokerHub.
    * It changes the GUI and the window's state to match the new game state.  The
    * new state is also stored in the instance variable named state.
    */
   private void newState(PokerGameState state) {
      
      this.state = state;
      
      // Set the enabled status of the buttons to enable actions that are appropriate in the current state.
      
      dealButton.setEnabled(state.status == PokerGameState.DEAL);
      drawButton.setEnabled(state.status == PokerGameState.DRAW);
      betButton.setEnabled(state.status == PokerGameState.BET_OR_FOLD
                    || state.status == PokerGameState.RAISE_SEE_OR_FOLD_ROUND_1
                    || state.status == PokerGameState.RAISE_CALL_OR_FOLD_ROUND_2);
      foldButton.setEnabled(state.status == PokerGameState.BET_OR_FOLD
                      || state.status == PokerGameState.RAISE_SEE_OR_FOLD_ROUND_1
                    || state.status == PokerGameState.RAISE_CALL_OR_FOLD_ROUND_2);
      passButton.setEnabled(state.status == PokerGameState.BET_OR_FOLD);
      callButton.setEnabled(state.status == PokerGameState.RAISE_SEE_OR_FOLD_ROUND_1
                      || state.status == PokerGameState.RAISE_CALL_OR_FOLD_ROUND_2);
      
      // Set the name of callButton to "CALL" during the second round of
      // betting and to "SEE" at other times.
      
      if (state.status == PokerGameState.RAISE_CALL_OR_FOLD_ROUND_2)
         callButton.setText("CALL");
      else
         callButton.setText("SEE");
      
      // When it's time for this player to make a bet, enable the betInput text field,
      // set its content to be the minimum possible bet plus $10, and select the
      // text input so that the user can simply type the bet and press return.  The
      // betInput is not editable except when its time for the user to place a bet.
      // Once the user places the bet (or sees, calls, passes, or folds), the
      // betInput is once again made empty and uneditable.
      
      if (state.status == PokerGameState.RAISE_SEE_OR_FOLD_ROUND_1 || 
            state.status == PokerGameState.RAISE_CALL_OR_FOLD_ROUND_2 ||
            state.status == PokerGameState.BET_OR_FOLD) {
         if ( ! betInput.isEditable() ) {
            int suggestedBet;
            if ( state.status == PokerGameState.BET_OR_FOLD)
               suggestedBet = 10;
            else
               suggestedBet = state.amountToSee + 10;
            betInput.setText("" + suggestedBet);
            betInput.setEditable(true);
            betInput.selectAll();
            betInput.requestFocus();
         }
      }
      
      // Show the current amounts of the user's mone, the opponent's money, and the pot.
      
      money.setText("You have $ " + state.money);
      opponentsMoney.setText("Your opponent has $ " + state.opponentMoney);
      if (state.status != PokerGameState.DEAL && state.status != PokerGameState.WAIT_FOR_DEAL)
         opponentHand = null;
      pot.setText("Pot:  $ " + state.pot);
      
      // If it's time for the user to select cards to be discarded, create the
      // discard array that will record which cards have been selected to be discarded.
      
      if (state.status == PokerGameState.DRAW && discard == null) {
         discard = new boolean[5];
      }
      
      // Set the JLable, message, to show instructions to the user that are appropriate for the state.
      
      switch (state.status) {
      case PokerGameState.DEAL:
         message.setText("Click the DEAL button to start the game.");
         break;
      case PokerGameState.DRAW:
         message.setText("Click the cards you want to discard, then click DRAW.");
         break;
      case PokerGameState.BET_OR_FOLD:
         message.setText("Place your BET, PASS, or FOLD.");
         break;
      case PokerGameState.RAISE_SEE_OR_FOLD_ROUND_1:
         message.setText("Place your BET, SEE, or FOLD.");
         break;
      case PokerGameState.RAISE_CALL_OR_FOLD_ROUND_2:
         message.setText("Place your BET, CALL, or FOLD.");
         break;
      case PokerGameState.WAIT_FOR_BET:
         message.setText("Waiting for opponent to bet.");
         break;
      case PokerGameState.WAIT_FOR_DEAL:
         message.setText("Waiting for opponent to deal.");
         break;
      case PokerGameState.WAIT_FOR_DRAW:
         message.setText("Waiting for opponent to draw.");
         break;
      }
      
      repaint();

   } // end newState()
   
      
   
   /**
    * This method is called when the user clicks the display at the
    * point (x,y).  Clicks are ignored except when the user is 
    * selecting cards to discard (that is when state.status is
    * PokerGameState.DRAW).  While discarding, if the user clicks
    * a card, that card is turned over.
    */
   private void doClick(int x, int y) {
      if (state == null || state.status != PokerGameState.DRAW)
         return;
      for (int i = 0; i < 5; i++) {
         if (y > 380 && y < 503 && x > 25+i*105 && x < 104+i*105) {
               // Clicked on card number i.  Toggle the value in
               // discard[i].  If the card is face up, it will be
               // selected for discarding and will now appear face down.
               // If the card is already selected for discarding, 
               // it will be de-selected.
            discard[i] = !discard[i];
            repaint();
            break;
         }
      }
   }
   
   
   /**
    * This method is called when the user clicks the "QUIT"
    * button or closed the window.  The client disconnects
    * from the server before terminating the program.  
    * This will be seen by the Hub, which will inform the 
    * other player's program (if any), so that that program 
    * can also terminate.
    */
   private void doQuit() {
      dispose(); // Close the window.
      if (connection != null) {
         connection.disconnect();
         try { // time for the disconnect message to be sent.
            Thread.sleep(500);
         }
         catch (InterruptedException e) {
         }
      }
      System.exit(0);
   }
   

   /**
    * Draws a card in a 79x123 pixel rectangle with its
    * upper left corner at a specified point (x,y).  Drawing the card 
    * requires the resource file "netgame/fivecarddraw/cards.png".
    * @param g The non-null graphics context used for drawing the card.
    * @param card The card that is to be drawn.  If the value is null, then a
    *     face-down card is drawn.
    * @param x the x-coord of the upper left corner of the card
    * @param y the y-coord of the upper left corner of the card
    */
   public void drawCard(Graphics g, PokerCard card, int x, int y) {
      int cx;    // x-coord of upper left corner of the card inside cardsImage
      int cy;    // y-coord of upper left corner of the card inside cardsImage
      if (card == null) {
         cy = 4*123;   // coords for a face-down card.
         cx = 2*79;
      }
      else {
         if (card.getValue() == PokerCard.ACE)
            cx = 0;
         else
            cx = (card.getValue()-1)*79;
         switch (card.getSuit()) {
         case PokerCard.CLUBS:    
            cy = 0; 
            break;
         case PokerCard.DIAMONDS: 
            cy = 123; 
            break;
         case PokerCard.HEARTS:   
            cy = 2*123; 
            break;
         default:  // spades   
            cy = 3*123; 
            break;
         }
      }
      g.drawImage(cardImages,x,y,x+79,y+123,cx,cy,cx+79,cy+123,this);
   }
   
   
} // end class PokerWindow

