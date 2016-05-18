
/*
    This program evaluates standard expressions typed in
    by the user.  The expressions can use positive real numbers and
    the binary operators +, -, *, and /.  The unary minus operation
    is supported.  The expressions are defined by the BNF rules:

            <expression>  ::=  [ "-" ] <term> [ [ "+" | "-" ] <term> ]...

            <term>  ::=  <factor> [ [ "*" | "/" ] <factor> ]...

            <factor>  ::=  <number>  |  "(" <expression> ")"

    A number must begin with a digit (i.e., not a decimal point).
    A line of input must contain exactly one such expression.  If extra
    data is found on a line after an expression has been read, it is
    considered an error.
 */

public class SimpleParser2 {


   /**
    * An object of type ParseError represents a syntax error found in 
    * the user's input.
    */
   private static class ParseError extends Exception {
      ParseError(String message) {
         super(message);
      }
   } // end nested class ParseError


   public static void main(String[] args) {

      while (true) {
         TextIO.putln("\n\nEnter an expression, or press return to end.");
         TextIO.put("\n?  ");
         TextIO.skipBlanks();
         if ( TextIO.peek() == '\n' )
            break;
         try {
            double val = expressionValue();
            TextIO.skipBlanks();
            if ( TextIO.peek() != '\n' )
               throw new ParseError("Extra data after end of expression.");
            TextIO.getln();
            TextIO.putln("\nValue is " + val);
         }
         catch (ParseError e) {
            TextIO.putln("\n*** Error in input:    " + e.getMessage());
            TextIO.putln("*** Discarding input:  " + TextIO.getln());
         }
      }

      TextIO.putln("\n\nDone.");

   } // end main()


   /**
    * Read an expression from the current line of input and return its value.
    * @throws ParseError if the input contains a syntax error
    */
   private static double expressionValue() throws ParseError {
      TextIO.skipBlanks();
      boolean negative;  // True if there is a leading minus sign.
      negative = false;
      if (TextIO.peek() == '-') {
         TextIO.getAnyChar();
         negative = true;
      }
      double val;  // Value of the expression.
      val = termValue();
      if (negative)
         val = -val;
      TextIO.skipBlanks();
      while ( TextIO.peek() == '+' || TextIO.peek() == '-' ) {
             // Read the next term and add it to or subtract it from
             // the value of previous terms in the expression.
         char op = TextIO.getAnyChar();
         double nextVal = termValue();
         if (op == '+')
            val += nextVal;
         else
            val -= nextVal;
         TextIO.skipBlanks();
      }
      return val;
   } // end expressionValue()

   
   /**
    * Read a term from the current line of input and return its value.
    * @throws ParseError if the input contains a syntax error
    */
   private static double termValue() throws ParseError {
      TextIO.skipBlanks();
      double val;
      val = factorValue();
      TextIO.skipBlanks();
      while ( TextIO.peek() == '*' || TextIO.peek() == '/' ) {
             // Read the next factor, and multiply or divide
             // the value-so-far by the value of this factor.
         char op = TextIO.getAnyChar();
         double nextVal = factorValue();
         if (op == '*')
            val *= nextVal;
         else
            val /= nextVal;
         TextIO.skipBlanks();
      }
      return val;
   } // end termValue()


   /**
    * Read a factor from the current line of input and return its value.
    * @throws ParseError if the input contains a syntax error
    */
   private static double factorValue() throws ParseError {
      TextIO.skipBlanks();
      char ch = TextIO.peek();
      if ( Character.isDigit(ch) ) {
             // The factor is a number.
         return TextIO.getDouble();
      }
      else if ( ch == '(' ) {
             // The factor is an expression in parentheses.
         TextIO.getAnyChar();  // Read the "("
         double val = expressionValue();
         TextIO.skipBlanks();
         if ( TextIO.peek() != ')' )
            throw new ParseError("Missing right parenthesis.");
         TextIO.getAnyChar();  // Read the ")"
         return val;
      }
      else if ( ch == '\n' )
         throw new ParseError("End-of-line encountered in the middle of an expression.");
      else if ( ch == ')' )
         throw new ParseError("Extra right parenthesis.");
      else if ( ch == '+' || ch == '-' || ch == '*' || ch == '/' )
         throw new ParseError("Misplaced operator.");
      else
         throw new ParseError("Unexpected character \"" + ch + "\" encountered.");
   }


} // end class SimpleParser2