package netgame.fivecarddraw;

import java.util.ArrayList;


/**
 *  This is a utility class that can be used to assign ranks
 *  to poker hands containing up to five cards.  It does not
 *  handle jokers or hands of more than five cards, but a rank
 *  can be computed for a hand with fewer than five cards.
 *  A numerical rank is assigned to a hand.  One poker hand
 *  beats another if and only if the rank for the first hand
 *  is greater than the rank for the second hand.  If the
 *  ranks are equal, then two hands are tied.  Note that a hand
 *  with fewer than five cards is never considered to be
 *  a straight or flush.
 *  <p>Once cards have been added to a PokerRank object, the
 *  numerical rank can be obtained by calling the getRank()
 *  method.  Call getDescription() to get a verbal description 
 *  of the hand that is good enough for most purposes (such as
 *  "Pair of Kings"), but that does not include enough information
 *  to fully rank the hand. Call getLongDescription() to get a
 *  verbal description with enough detail to fully rank the hand.
 */
public class PokerRank {

  /* This main routine is meant only to test this class. 
   * this will only do anything if the output statements at
   * the end of computeRank() are uncommented. 
   */
   public static void main(String[] args) {
      System.out.println("(No cards)");
      System.out.println(new PokerRank().getRank());
      System.out.println(new PokerRank().getDescription());
      System.out.println(new PokerRank().getLongDescription());
      System.out.println("\n");
      new PokerRank(new PokerCard(12,3)).getRank();
      new PokerRank(new PokerCard(2,2), new PokerCard(12, 1)).getRank();
      new PokerRank(new PokerCard(12,3), new PokerCard(12, 1)).getRank();
      new PokerRank(new PokerCard(12,3), new PokerCard(2,2), new PokerCard(12, 1)).getRank();
      new PokerRank(new PokerCard(12,3), new PokerCard(12,2), new PokerCard(12, 1)).getRank();
      new PokerRank(new PokerCard(12,3), new PokerCard(2,2), new PokerCard(2,3), new PokerCard(12, 1)).getRank();
      new PokerRank(new PokerCard(12,3), new PokerCard(2,2), new PokerCard(12, 1), new PokerCard(12,2)).getRank();
      new PokerRank(new PokerCard(12,3), new PokerCard(2,2), new PokerCard(12, 1), new PokerCard(12,2)).getRank();
      new PokerRank(new PokerCard(12,3), new PokerCard(12, 1), new PokerCard(12,2), new PokerCard(2,3)).getRank();
      new PokerRank(new PokerCard(12,3), new PokerCard(2,2), new PokerCard(12, 1), new PokerCard(12,2), new PokerCard(12,0)).getRank();
      new PokerRank(new PokerCard(3,1), new PokerCard(2,1), new PokerCard(5, 1), new PokerCard(6,2), new PokerCard(4,3)).getRank();
      new PokerRank(new PokerCard(3,1), new PokerCard(2,1), new PokerCard(5, 1), new PokerCard(14,2), new PokerCard(4,3)).getRank();
      new PokerRank(new PokerCard(3,1), new PokerCard(2,1), new PokerCard(5, 1), new PokerCard(14,1), new PokerCard(4,1)).getRank();
      new PokerRank(new PokerCard(12,1), new PokerCard(10,1), new PokerCard(13, 1), new PokerCard(14,2), new PokerCard(11,3)).getRank();
      new PokerRank(new PokerCard(12,1), new PokerCard(10,1), new PokerCard(13, 1), new PokerCard(14,1), new PokerCard(11,1)).getRank();
      new PokerRank(new PokerCard(12,1), new PokerCard(10,1), new PokerCard(13, 1), new PokerCard(4,1), new PokerCard(11,1)).getRank();
      new PokerRank(new PokerCard(12,1), new PokerCard(10,2), new PokerCard(13, 1), new PokerCard(4,1), new PokerCard(11,1)).getRank();
      new PokerRank(new PokerCard(10,2), new PokerCard(10,1), new PokerCard(13, 1), new PokerCard(14,1), new PokerCard(13,3)).getRank();
      new PokerRank(new PokerCard(10,2), new PokerCard(10,1), new PokerCard(13, 1), new PokerCard(13,1), new PokerCard(10,3)).getRank();
   }
   
   
   public static final int NOTHING = 0;    // Codes for the basic types of poker hand.
   public static final int PAIR = 1;
   public static final int TWO_PAIR = 2;
   public static final int TRIPLE = 3;
   public static final int STRAIGHT = 4;
   public static final int FLUSH = 5;
   public static final int FULL_HOUSE = 6;
   public static final int FOUR_OF_A_KIND = 7;
   public static final int STRAIGHT_FLUSH =  8;
   public static final int ROYAL_FLUSH =  9;

   private ArrayList<PokerCard> cards = new ArrayList<PokerCard>();  // The cards in this hand.
   

   /**
    * Numerical rank of the hand consisting of cards that have
    * been added to this object.  -1 is a signal than the rank
    * has to be computed.
    */
   private int rank = -1;
   
   /**
    * The verbal description of the hand.  Computed at the same 
    * time the rank is computed.
    */
   private String description;
   
   /**
    * The full verbal description of the hand.  Computed at the same 
    * time the rank is computed.
    */
   private String longDescription;

   
   /**
    * Construct a PokerRank object from a list of zero or more
    * cards.  It is possible to add more cards later.
    * @throws IllegalArgumentException if any of the cards are
    *    null, or if any are jokers, or if the number of cards
    *    is greater than five.
    */
   public PokerRank(PokerCard... card) {
      if (card != null) {
         for (PokerCard c : card)
            add(c);
      }
   }

   /**
    * Construct a PokerRank object from a list of zero or more
    * cards.  It is possible to add more cards later.
    * @param cards the list of cards to be added.  A null value
    *     is OK and means that no cards are added initially.
    * @throws IllegalArgumentException if any cards in the list are
    *    null, or if any are jokers, or if the number of cards
    *    is greater than five.
    */
   public PokerRank(ArrayList<PokerCard> cards) {
      if (cards != null) {
         for (PokerCard c : cards)
            add(c);
      }
   }

   /**
    * Add a card to the hand.  This will change the ranking of the hand.
    * @throws IllegalArgumentException if the card is null or is a joker
    *    or if there were already five cards in the hand.
    */
   public void add(PokerCard card) {
      if (card == null)
         throw new IllegalArgumentException("Cards can't be null for class PokerRank");
      if (card.getSuit() == PokerCard.JOKER)
         throw new IllegalArgumentException("Class PokerRank does not support jokers.");
      if (cards.size() == 5)
         throw new IllegalArgumentException("PokerRank does not support hands with more than five cards.");
      cards.add(card);
      rank = -1;
   }


   /**
    * Get the numerical rank of the hand.  One hand beats another in poker 
    * if and only if it has a higher numerical rank.  (The rank is determined
    * as follows:  The basic type of hand is given by one of the constants
    * NOTHING, PAIR, TWO_PAIR, TRIPLE, STRAIGHT, FLUSH, FULL_HOUSE, FOUR_OF_A_KIND,
    * STRAIGHT_FLUSH, ROYAL_FLUSH.  These constants are integers in the range
    * 0 through 9.  That integer is stored in bits 20 through 23 of the numerical
    * rank (and all higher order bits are zero). So, if the basic type of one hand
    * is higher than the basic type of another hand then the rank of the first 
    * will be higher than the rank of the second (no matter what is in bits 0 -- 19).
    * If the basic types are the same, the comparison is based on bit positions
    * 0 through 19, which are used to record the values of the cards, with 4 bits per 
    * card in the order that the values would have to be considered to break ties.
    * The cards are ordered highest value to lowest value, except that, for example,
    * when there is a four-of-a-kind, the cards that make up the four-of-a-kind
    * come first even if their value is less than the value of the remaining
    * card.  This is because 5-5-5-5-2 beats 2-2-2-2-9.  Similarly, the cards
    * that make up a triple are moved to the front, since the value of the
    * triple has to be considered before the values of the other cards in the
    * hand.)
    */
   public int getRank() {
      if (rank == -1)
         computeRank();
      return rank;
   }
   
   /**
    * Returns a description of the rank of the hand, such as
    * "Pair of Threes", "High Card (Queen)", "Full House, Threes and Sevens",
    * "Seven-high Straight," or "Royal Flush".  The description does
    * not necessarily contain enough information to fully rank the
    * hand.
    */
   public String getDescription() {
      if (rank == -1)
         computeRank();
      return description;
   }

   /**
    * Returns a long description of the rank of the hand, containing
    * enough information to fully rank the hand.  For example:
    * "Pair of Threes (plus Ace, Ten, Seven)".
    */
   public String getLongDescription() {
      if (rank == -1)
         computeRank();
      return longDescription;
   }
   
   /**
    * Returns one of the constants NOTHING, PAIR, TWO_PAIR, TRIPLE, STRAIGHT, 
    * FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH, or ROYAL_FLUSH.
    * The constant represents the basic type of the hand, but without
    * the information about card values that is needed to compare
    * hands of the same type.
    */
   public int getHandType() {
      if (rank == -1)
         computeRank();
      return rank >> 20;
   }
   
   /**
    * Returns the type of hand as a string, such as "Two pairs", "Straight",
    * or "Nothing".  This is a string representation of the value returned
    * by getHandType().  This is the basic type of the hand, but without
    * the information about card values that is needed to compare
    * hands of the same type.
    */
   public String getHandTypeAsString() {
      if (cards.size() == 0)
         return "Empty Hand";
      int type = getHandType();
      if (type == PAIR)
         return "Pair";
      if (type == TWO_PAIR)
         return "Two pairs";
      if (type == TRIPLE)
            return "Triple";
      if (type == STRAIGHT)
            return "Straight";
      if (type == FLUSH)
            return "Flush";
      if (type == FULL_HOUSE)
            return "Full House";
      if (type == FOUR_OF_A_KIND)
         return "Four of a kind";
      if (type == STRAIGHT_FLUSH)
            return "Straight Flush";
      if (type == ROYAL_FLUSH)
            return "Royal Flush";
      return "Nothing";
   }
   
   
   /**
    * Returns the cards that have been added to this hand.
    * @return a newly created ArrayList containing the cards.
    * The list can be empty but will never be null.  Cards in
    * the list have been sorted into the order in which they
    * have to be considered when evaluating the hand.
    */
   public ArrayList<PokerCard> getCards() {
      if ( rank == -1)
         computeRank();
      return new ArrayList<PokerCard>(cards);
   }
   

   /**
    * Returns the same string as does the getDescription() method.
    * That is, the return value is a string that describes the hand
    * but does not contain all the information that is needed to
    * completely rank hands of the same type.  See also the
    * method getLongDescription().
    */
   public String toString() {
      return getDescription();
   }
      
   // --------------------- the private implementation section -------------------
   

   private String valueName(PokerCard c) {
      switch ( c.getValue() ) {
      case 2:   return "Two";
      case 3:   return "Three";
      case 4:   return "Four";
      case 5:   return "Five";
      case 6:   return "Six";
      case 7:   return "Seven";
      case 8:   return "Eight";
      case 9:   return "Nine";
      case 10:  return "Ten";
      case 11:  return "Jack";
      case 12:  return "Queen";
      case 13:  return "King";
      default: return "Ace";
      }
   }
      
   private String pluralValueName(PokerCard c) {
      if (c.getValue() == 6)
         return "Sixes";
      else
         return valueName(c) + "s";
   }
   
   private String cardValueNames() {
      StringBuffer s = new StringBuffer(valueName(cards.get(0)));
      for (int i = 1; i < cards.size(); i++) {
         s.append(',');
         s.append(valueName(cards.get(i)));
      }
      return s.toString();
   }

   /**
    * Computes the rank, description, and longDescription of the hand.
    * We know that there are 0 to 5 cards, that none of them are null
    * and that none of them are jokers.
    */
   private void computeRank() {
      if (cards.size() == 0) {
         rank = 0;
         description = longDescription = "Empty Hand";
         return;
      }
      
      /* Sort the cards by value.  Within the same value, sort them by
       * suit just to be neat; the suit order has no effect on the rank. */
      
      ArrayList<PokerCard> newCards = new ArrayList<PokerCard>();
      while (cards.size() > 0) {
         PokerCard maxCard = cards.get(0);
         for (int i = 1; i < cards.size(); i++)
            if (cards.get(i).getValue() > maxCard.getValue() ||
                  cards.get(i).getValue() == maxCard.getValue() && cards.get(i).getSuit() > maxCard.getSuit())
               maxCard = cards.get(i);
         cards.remove(maxCard);
         newCards.add(maxCard);
      }
      cards = newCards;
      
      /* Cards are now sorted by value.  They might have to be rearranged.  Use
       * a try..finally statement here to add the values of the cards in bit positions
       * 0 through 19.  That has to be done last, not first, because it has be
       * done after the cards have been, possibly, rearranged.  
       */
      
      try {
         
         /* Check if the card is a straight and/or flush.  A partial hand,
          * with fewer than five cards, can never be considered to be a
          * straight or a flush.
          */
         
         boolean isFlush = false;
         if (cards.size() == 5) {
            isFlush = cards.get(0).getSuit() == cards.get(1).getSuit() 
                        && cards.get(1).getSuit() == cards.get(2).getSuit()
                        && cards.get(1).getSuit() == cards.get(3).getSuit() 
                        && cards.get(1).getSuit() == cards.get(4).getSuit();
         }
         boolean isStraight = false;
         if (cards.size() == 5) {
                // Handle the case of a 5-4-3-2-A straight.  This hand would currently
                // be in the order A-5-4-3-2, but must be rearranged, since the Ace
                // counts as 1 in this case.
            if (cards.get(0).getValue() == PokerCard.ACE && cards.get(1).getValue() == 5
                  && cards.get(2).getValue() == 4 && cards.get(3).getValue() == 3
                  && cards.get(4).getValue() == 2 ) {
               isStraight = true;
               cards.add(cards.remove(0));  // Move the ace to the end, by removing it then adding it.
            }
            else {  // An ordinary straight.
               isStraight = cards.get(0).getValue() == cards.get(1).getValue() + 1
                        && cards.get(1).getValue() == cards.get(2).getValue() + 1
                        && cards.get(2).getValue() == cards.get(3).getValue() + 1
                        && cards.get(3).getValue() == cards.get(4).getValue() + 1;
            }
         }
         if (isFlush) {
            if (isStraight) {
               if (cards.get(0).getValue() == PokerCard.ACE) {
                  rank = ROYAL_FLUSH;
                  description = longDescription = "Royal Flush";
               }
               else {
                  rank = STRAIGHT_FLUSH;
                  description = longDescription = valueName(cards.get(0)) + "-high Straight Flush";
               }
            }
            else { 
               rank = FLUSH;
               description = "Flush";
               longDescription = "Flush (" + cardValueNames() + ")";
            }
            return;
         }
         if (isStraight)  {
            rank = STRAIGHT;
            description = longDescription = valueName(cards.get(0)) + "-high Straight";
            return;
         }
         
         /* Check for four-of-a-kind, first one in which the four-of-a-kind
          * occurs in the first four cards, then in the case where the first
          * card is not part of the four-of-a-kind.  In the latter case, the
          * first card has to be moved to the end.
          */
         
         if (cards.size() >= 4) {
            if (cards.get(0).getValue() == cards.get(1).getValue()
                     && cards.get(1).getValue() == cards.get(2).getValue()
                     && cards.get(2).getValue() == cards.get(3).getValue()) {
               rank = FOUR_OF_A_KIND;
               description = longDescription = "Four " + pluralValueName(cards.get(0));
               if (cards.size() == 5)
                  longDescription = description + " (plus " + valueName(cards.get(4)) + ")";
               return;
            }
         }
         if (cards.size() == 5 
               && cards.get(1).getValue() == cards.get(2).getValue()
               && cards.get(2).getValue() == cards.get(3).getValue()
               && cards.get(3).getValue() == cards.get(4).getValue()) {
            cards.add(cards.remove(0));  // Move first card -- not part of the Quad -- to the end.
            rank = FOUR_OF_A_KIND;
            description = "Four " + pluralValueName(cards.get(0));
            longDescription = description + " (plus " + valueName(cards.get(4)) + ")";
            return;
         }
         
         /* Check for triples and pairs. */
         
         int tripleValue = 0;     // If greater than 0, then there is a triple with this value.
         int tripleLocation = -1; // If tripleValue is greater than 0, this gives the index in cards of the first card in the triple.
         for (int i = 0; i <= cards.size() - 3; i++) {
            if (cards.get(i).getValue() == cards.get(i+1).getValue()
                  && cards.get(i+1).getValue() == cards.get(i+2).getValue()) {
               tripleLocation = i;
               tripleValue = cards.get(i).getValue();
               break;
            }
         }
         int pairValue1 = 0; // If greater than 0, then there is a pair with this value.  If two pairs, this is the first (so highest) value.
         int pairLoc1 = -1;  // If pairValue1 is greater than 0, then this is the index in cards of the first card in the pair.
         int pairValue2 = 0; // If greater than 0, there are two pairs and this is the value of the cards in the second pair.
         int pairLoc2 = -1;  // If pairValue2 is greater than 0, this is the index in cards of the first card in the second pair.
         for (int i = 0; i <= cards.size() - 2; i++) {
                  // Look for a pair at position i.  Be careful not to count two cards that
                  // are part of a triple as being a pair
            if (cards.get(i).getValue() == cards.get(i+1).getValue() && cards.get(i).getValue() != tripleValue) {
                   // Found a pair at position i.  Record it and look a second pair later in the hand.
               pairValue1 = cards.get(i).getValue();
               pairLoc1 = i;
               for (int j = i+2; j <= cards.size() -2; j++) {
                       // Found a second pair.
                  if (cards.get(j).getValue() == cards.get(j+1).getValue() && cards.get(j).getValue() != tripleValue) {
                     pairValue2 = cards.get(j).getValue();
                     pairLoc2 = j;
                     break;
                  }
               }
               break;
            }
         }
         
         if (tripleValue == 0 && pairValue1 == 0) {
               // No triple or pair in the hand.  The hand is ranked primarily on its high card.
            rank = NOTHING;
            description = "High Card (" + valueName(cards.get(0)) + ")";
            longDescription = "High Card (" + cardValueNames() + ")";
            return;
         }
         
         if (tripleValue > 0) { 
                // There is a triple.
            for (int i = 0; i < tripleLocation; i++) {
                  // Move the cards that precede the triple to the end of the hand.
                  // by rotating the first card to last position for the number of
                  // times given by the original location of the triple in the hand.
               cards.add(cards.remove(0));
            }
            if (pairValue1 > 0) {
                   // There is also a pair, so the hand is a full house.  The pair
                   // has been moved to the end of the hand, if it wasn't there in
                   // the first place.
               rank = FULL_HOUSE;
               description = longDescription = "Full House, " + pluralValueName(cards.get(0))
                    + " and " + pluralValueName(cards.get(4));
               return;
            }
            else {
               rank = TRIPLE;
               description = longDescription = "Three " + pluralValueName(cards.get(0));
               if (cards.size() == 4)
                  longDescription = description + " (plus " + valueName(cards.get(3)) + ")";
               else if (cards.size() == 5)
                  longDescription = description + " (plus " + valueName(cards.get(3)) 
                        + " and " + valueName(cards.get(4)) + ")";
               return;
            }
         }
         
         if (pairLoc1 > 0) {
               // The first pair that was found is not at the start of the hand,
               // so move it there by removing the cards that make up the pair
               // from the hand and then adding them back into the hand at
               // the start.
            PokerCard p2 = cards.remove(pairLoc1+1);
            PokerCard p1 = cards.remove(pairLoc1);
            cards.add(0,p2);
            cards.add(0,p1);
         }
         
         if (pairValue2 == 0) {  //  There was only one pair.
            rank = PAIR;
            description = longDescription = "Pair of " + pluralValueName(cards.get(0));
            if (cards.size() == 5)
               longDescription = description + " (plus " + valueName(cards.get(2)) + ","
                     + valueName(cards.get(3)) + "," + valueName(cards.get(4)) + ")";
            else if (cards.size() == 4)
               longDescription = description + " (plus " + valueName(cards.get(2)) + ","
                     + valueName(cards.get(3)) + ")";
            else if (cards.size() == 3)
               longDescription = description + " (plus " + valueName(cards.get(2)) + ")";
            return;
         }
         
         // If we reach this point, there are two pairs.
         
         if (pairLoc2 > 2) {
                // The second pair should at position 2.  If not, move the second
                // pair to that position by removing the cards that make up the
                // pair from the hand and then adding them back at position 2.
            PokerCard p2 = cards.remove(pairLoc2+1);
            PokerCard p1 = cards.remove(pairLoc2);
            cards.add(2,p2);
            cards.add(2,p1);
         }
         
         rank = TWO_PAIR;
         description = longDescription = "Two Pairs, " + pluralValueName(cards.get(0)) + " and "
                + pluralValueName(cards.get(2));
         if (cards.size() == 5)
            longDescription = description + " (plus " + valueName(cards.get(4)) + ")";
         
      }
      finally {
            // The finally clause adds the values of the cards to the rank, in
            // bit positions 19 down to 0.  The current value of rank contains the
            // basic hand type in bits 0-4.  This value is first moved into
            // bits 20-23, then the card values are added.  The first card goes in bits 
            // 19-16, the second in bits 15-12, and so on.  (This is true even if there
            // are fewer than five cards; in that case, lower order bits will
            // be zero.
         
         rank <<= 20;
         for (int i = 0; i < cards.size(); i++) {
            rank |= cards.get(i).getValue() << 4*(4-i);
         }
         
         // For testing, print out the cards, the hand type, the rank, and the descriptions.
         // The main() routine in this class is meant for testing, but only works if the
         // following lines are uncommented.
         /*
         for (PokerCard c : cards)
            System.out.println(c);
         System.out.println("Hand Type: " + (rank >> 20));
         System.out.println(description);
         System.out.println(longDescription);
         System.out.printf("Rank: %X\n\n", rank);
         */
      }
      
   }


}
