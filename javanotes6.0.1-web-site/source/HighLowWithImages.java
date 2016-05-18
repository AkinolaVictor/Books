
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

/**
 * This program is a simple card game.  The user sees a card and
 * tries to predict whether the next card will be higher or 
 * lower.  Aces are the lowest-valued cards.  If the user makes
 * three correct predictions, the user wins.  If not, the
 * user loses.
 * 
 * This class defines a panel, but it also contains a main()
 * routine that makes it possible to run the program as a
 * stand-alone application.
 * 
 * This program depends on several additional source code files:
 * Card.java, Hand.java, and Deck.java.  It also requires the image
 * file, cards.png, which contains the images of the playing cards.
 * (The file cards.png is taken from the Gnome Desktop.)
 */
public class HighLowWithImages extends JPanel {
   
   /**
    * The main routine simply opens a window that shows a HighLowWithImages panel.
    */
   public static void main(String[] args) {
      JFrame window = new JFrame("HighLowWithImages");
      HighLowWithImages content = new HighLowWithImages();
      window.setContentPane(content);
      window.pack();  // Set size of window to preferred size of its contents.
      window.setResizable(false);  // User can't change the window's size.
      window.setLocation(100,100);
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
   
      
   /**
    * The constructor lays out the panel.  A CardPanel occupies the CENTER 
    * position of the panel (where CardPanel is a subclass of JPanel that is 
    * defined below).  On the bottom is a panel that holds three buttons.  
    * The CardPanel listens for ActionEvents from the buttons and does all 
    * the real work of the program.
    */
   public HighLowWithImages() {
      
      setBackground( new Color(130,50,40) );
      
      setLayout( new BorderLayout(3,3) );
      
      CardPanel board = new CardPanel();
      add(board, BorderLayout.CENTER);
      
      JPanel buttonPanel = new JPanel();
      buttonPanel.setBackground( new Color(220,200,180) );
      add(buttonPanel, BorderLayout.SOUTH);
      
      JButton higher = new JButton( "Higher" );
      higher.addActionListener(board);
      buttonPanel.add(higher);
      
      JButton lower = new JButton( "Lower" );
      lower.addActionListener(board);
      buttonPanel.add(lower);
      
      JButton newGame = new JButton( "New Game" );
      newGame.addActionListener(board);
      buttonPanel.add(newGame);
      
      setBorder(BorderFactory.createLineBorder( new Color(130,50,40), 3) );
      
   }  // end constructor
   
   
   
   /**
    * A nested class that displays the cards and does all the work
    * of keeping track of the state and responding to user events.
    */
   private class CardPanel extends JPanel implements ActionListener {
      
      Deck deck;       // A deck of cards to be used in the game.
      Hand hand;       // The cards that have been dealt.
      String message;  // A message drawn on the canvas, which changes
                       //    to reflect the state of the game.
      
      boolean gameInProgress;  // Set to true when a game begins and to false
                               //   when the game ends.
      
      Font bigFont;      // Font that will be used to display the message.
      
      Image cardImages;  // Contains the image of all 52 cards 
      
      /**
       * Constructor creates fonts, sets the foreground and background
       * colors and starts the first game.  It also sets a "preferred
       * size" for the panel.  This size is respected when the program
       * is run as an application, since the pack() method is used to
       * set the size of the window.
       */
      CardPanel() {
         loadImage();
         setBackground( new Color(0,120,0) );
         setForeground( Color.GREEN );
         bigFont = new Font("Serif", Font.BOLD, 15);
         setPreferredSize( new Dimension(15+4*(15+79), 185));
         doNewGame();
      } // end constructor
      
      
      /**
       * Respond when the user clicks on a button by calling the appropriate 
       * method.  Note that the buttons are created and listening is set
       * up in the constructor of the HighLowPanel class.
       */
      public void actionPerformed(ActionEvent evt) {
         String command = evt.getActionCommand();
         if (command.equals("Higher"))
            doHigher();
         else if (command.equals("Lower"))
            doLower();
         else if (command.equals("New Game"))
            doNewGame();
      } // end actionPerformed()
      
      
      /**
       * Called by actionPerformmed() when user clicks "Higher" button.
       * Check the user's prediction.  Game ends if user guessed
       * wrong or if the user has made three correct predictions.
       */
      void doHigher() {
         if (gameInProgress == false) {
            // If the game has ended, it was an error to click "Higher",
            // So set up an error message and abort processing.
            message = "Click \"New Game\" to start a new game!";
            repaint();
            return;
         }
         hand.addCard( deck.dealCard() );     // Deal a card to the hand.
         int cardCt = hand.getCardCount();
         Card thisCard = hand.getCard( cardCt - 1 );  // Card just dealt.
         Card prevCard = hand.getCard( cardCt - 2 );  // The previous card.
         if ( thisCard.getValue() < prevCard.getValue() ) {
            gameInProgress = false;
            message = "Too bad! You lose.";
         }
         else if ( thisCard.getValue() == prevCard.getValue() ) {
            gameInProgress = false;
            message = "Too bad!  You lose on ties.";
         }
         else if ( cardCt == 4) {
            gameInProgress = false;
            message = "You win!  You made three correct guesses.";
         }
         else {
            message = "Got it right!  Try for " + cardCt + ".";
         }
         repaint();
      } // end doHigher()
      
      
      /**
       * Called by actionPerformmed() when user clicks "Lower" button.
       * Check the user's prediction.  Game ends if user guessed
       * wrong or if the user has made three correct predictions.
       */
      void doLower() {
         if (gameInProgress == false) {
               // If the game has ended, it was an error to click "Lower",
               // So set up an error message and abort processing.
            message = "Click \"New Game\" to start a new game!";
            repaint();
            return;
         }
         hand.addCard( deck.dealCard() );     // Deal a card to the hand.
         int cardCt = hand.getCardCount();
         Card thisCard = hand.getCard( cardCt - 1 );  // Card just dealt.
         Card prevCard = hand.getCard( cardCt - 2 );  // The previous card.
         if ( thisCard.getValue() > prevCard.getValue() ) {
            gameInProgress = false;
            message = "Too bad! You lose.";
         }
         else if ( thisCard.getValue() == prevCard.getValue() ) {
            gameInProgress = false;
            message = "Too bad!  You lose on ties.";
         }
         else if ( cardCt == 4) {
            gameInProgress = false;
            message = "You win!  You made three correct guesses.";
         }
         else {
            message = "Got it right!  Try for " + cardCt + ".";
         }
         repaint();
      } // end doLower()
      
      
      /**
       * Called by the constructor, and called by actionPerformed() if
       * the use clicks the "New Game" button.  Start a new game.
       */
      void doNewGame() {
         if (gameInProgress) {
               // If the current game is not over, it is an error to try
               // to start a new game.
            message = "You still have to finish this game!";
            repaint();
            return;
         }
         deck = new Deck();   // Create the deck and hand to use for this game.
         hand = new Hand();
         deck.shuffle();
         hand.addCard( deck.dealCard() );  // Deal the first card into the hand.
         message = "Is the next card higher or lower?";
         gameInProgress = true;
         repaint();
      } // end doNewGame()
      
      
      /**
       * This method draws the message at the bottom of the
       * panel, and it draws all of the dealt cards spread out
       * across the canvas.  If the game is in progress, an extra
       * card is drawn face down representing the card to be dealt next.
       */
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         if (cardImages == null) {
            g.drawString("Error: Can't get card images!", 10,30);
            return;
         }
         g.setFont(bigFont);
         g.drawString(message,15,168);
         int cardCt = hand.getCardCount();
         for (int i = 0; i < cardCt; i++)
            drawCard(g, hand.getCard(i), 15 + i * (15+79), 15);
         if (gameInProgress)
            drawCard(g, null, 15 + cardCt * (15+79), 15);
      } // end paintComponent()

      
      /**
       * Draws a card in a 79x123 pixel rectangle with its
       * upper left corner at a specified point (x,y).  Drawing the card 
       * requires the image file "cards.png".
       * @param g The non-null graphics context used for drawing the card.  If g is
       * null, a NullPointerException will be thrown.
       * @param card The card that is to be drawn.  If the value is null, then a
       * face-down card is drawn.
       * @param x the x-coord of the upper left corner of the card
       * @param y the y-coord of the upper left corner of the card
       */
      public void drawCard(Graphics g, Card card, int x, int y) {
         int cx;    // x-coord of upper left corner of the card inside cardsImage
         int cy;    // y-coord of upper left corner of the card inside cardsImage
         if (card == null) {
            cy = 4*123;   // coords for a face-down card.
            cx = 2*79;
         }
         else {
            cx = (card.getValue()-1)*79;
            switch (card.getSuit()) {
            case Card.CLUBS:    
               cy = 0; 
               break;
            case Card.DIAMONDS: 
               cy = 123; 
               break;
            case Card.HEARTS:   
               cy = 2*123; 
               break;
            default:  // spades   
               cy = 3*123; 
               break;
            }
         }
         g.drawImage(cardImages,x,y,x+79,y+123,cx,cy,cx+79,cy+123,this);
      }
      
      
      /**
       * Load the image from the file "cards.png", which must be somewhere
       * on the classpath for this program.  If the file is found, then
       * cardImages will refer to the Image.  If not, then cardImages
       * will be null.
       */
      private void loadImage() {
         ClassLoader cl = HighLowWithImages.class.getClassLoader();
         URL imageURL = cl.getResource("cards.png");
         if (imageURL != null)
            cardImages = Toolkit.getDefaultToolkit().createImage(imageURL);
      }


   } // end nested class CardPanel
   
   
} // end class HighLowWithImages
