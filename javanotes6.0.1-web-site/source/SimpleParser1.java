
/*
    This program evaluates fully parenthesized expressions typed in
    by the user.  The expressions can use positive real numbers and
    the binary operators +, -, *, and /.  The expressions are 
    defined by the BNF rules:

            <expression>  ::=  <number>  |
                                  "(" <expression> <operator> <expression> ")"
            <operator>  ::=  "+" | "-" | "*" | "/"

    A number must begin with a digit (i.e., not a decimal point).
    A line of input must contain exactly one such expression.  If extra
    data is found on a line after an expression has been read, it is
    considered an error.
 */

public class SimpleParser1 {


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
         TextIO.putln("\n\nEnter a fully parenthesized expression,");
         TextIO.putln("or press return to end.");
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
      if ( Character.isDigit(TextIO.peek()) ) {
             // The next item in input is a number, so the expression
             // must consist of just that number.  Read and return
             // the number.
         return TextIO.getDouble();
      }
      else if ( TextIO.peek() == '(' ) {
             // The expression must be of the form 
             //         "(" <expression> <operator> <expression> ")"
             // Read all these items, perform the operation, and
             // return the result.
         TextIO.getAnyChar();  // Read the "("
         double leftVal = expressionValue();  // Read and evaluate first operand.
         char op = getOperator();             // Read the operator.
         double rightVal = expressionValue(); // Read and evaluate second operand.
         TextIO.skipBlanks();
         if ( TextIO.peek() != ')' ) {
                // According to the rule, there must be a ")" here.
                // Since it's missing, throw a ParseError.
            throw new ParseError("Missing right parenthesis.");
         }
         TextIO.getAnyChar();  // Read the ")"
         switch (op) {   //  Apply the operator and return the result. 
         case '+':  return leftVal + rightVal;
         case '-':  return leftVal - rightVal;
         case '*':  return leftVal * rightVal;
         case '/':  return leftVal / rightVal;
         default:   return 0;  // Can't occur since op is one of the above.
                               // (But Java syntax requires a return value.)
         }
      }
      else {
         throw new ParseError("Encountered unexpected character, \"" + 
               TextIO.peek() + "\" in input.");
      }
   } // end expressionValue()


   /**
    * If the next character in input is one of the legal operators,
    * read it and return it.  Otherwise, throw a ParseError.
    */
   static char getOperator() throws ParseError {
      TextIO.skipBlanks();
      char op = TextIO.peek(); 
      if ( op == '+' || op == '-' || op == '*' || op == '/' ) {
         TextIO.getAnyChar();
         return op;
      }
      else if (op == '\n')
         throw new ParseError("Missing operator at end of line.");
      else
         throw new ParseError("Missing operator.  Found \"" +
               op + "\" instead of +, -, *, or /.");
   } // end getOperator()


} // end class SimpleParser1