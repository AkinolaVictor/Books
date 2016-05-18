
/*
    This program can evaluate expressions that can include
    numbers, variables, parentheses, and the operators +,
    -, *, /, and ^ (where ^ indicates raising to a power).
    A variable name must consist of letters and digits,
    beginning with a letter.  Names are case-sensitive.
    This program accepts commands of two types from the user.
    For a command of the form  print <expression> , the expression
    is evaluated and the value is output.  For a command of
    the form  let <variable> = <expression> , the expression is
    evaluated and the value is assigned to the variable.
    If a variable is used in an expression before it has been
    assigned a value, an error occurs.  A number must begin with 
    a digit (i.e., not a decimal point).

    Commands are formally defined by the BNF rules:

            <command>  ::=  "print" <expression>
                               |  "let" <variable> "=" <expression>

            <expression>  ::=  [ "-" ] <term> [ [ "+" | "-" ] <term> ]...

            <term>  ::=  <factor> [ [ "*" | "/" ] <factor> ]...

            <factor>  ::=  <primary> [ "^" <primary> ]...

            <primary>  ::=  <number> | <variable> | "(" <expression> ")"

    A line of input must contain exactly one such command.  If extra
    data is found on a line after an expression has been read, it is
    considered an error.  The variables "pi" and "e" are defined
    when the program starts to represent the usual mathematical
    constants.

    This program demonstrates the use of a HashMap as a symbol
    table.

    SimpleInterpreter.java is based on the program SimpleParser2.java.
    It uses the non-standard class, TextIO.
 */

import java.util.HashMap;

public class SimpleInterpreter {

   /**
    * Represents a syntax error found in the user's input.
    */
   private static class ParseError extends Exception {
      ParseError(String message) {
         super(message);
      }
   } // end nested class ParseError


   /**
    * The symbolTable contains information about the
    * values of variables.  When a variable is assigned
    * a value, it is recorded in the symbol table.
    * The key is the name of the variable, and the 
    * value is an object of type Double that contains
    * the value of the variable.  (The wrapper class
    * Double is used, since a HashMap cannot contain
    * objects belonging to the primitive type double.)
    */
   private static HashMap<String,Double> symbolTable;


   public static void main(String[] args) {
      
      // Create the map that represents symbol table.
      
      symbolTable = new HashMap<String,Double>();

      // To start, add variables named "pi" and "e" to the symbol
      // table.  Their values are the usual mathematical constants.

      symbolTable.put("pi", Math.PI);
      symbolTable.put("e", Math.E);

      TextIO.putln("\n\nEnter commands; press return to end.");
      TextIO.putln("Commands must have the form:\n");
      TextIO.putln("      print <expression>");
      TextIO.putln("  or");
      TextIO.putln("      let <variable> = <expression>");

      while (true) {
         TextIO.put("\n?  ");
         TextIO.skipBlanks();
         if ( TextIO.peek() == '\n' ) {
            break;  // A blank input line ends the while loop and the program.
         }
         try {
            String command = TextIO.getWord();
            if (command.equalsIgnoreCase("print"))
               doPrintCommand();
            else if (command.equalsIgnoreCase("let"))
               doLetCommand();
            else
               throw new ParseError("Command must begin with 'print' or 'let'.");
            TextIO.getln();
         }
         catch (ParseError e) {
            TextIO.putln("\n*** Error in input:    " + e.getMessage());
            TextIO.putln("*** Discarding input:  " + TextIO.getln());
         }
      }

      TextIO.putln("\n\nDone.");

   } // end main()


   /**
    * Process a command of the form  let <variable> = <expression>.
    * When this method is called, the word "let" has already
    * been read.  Read the variable name and the expression, and
    * store the value of the variable in the symbol table.
    */
   private static void doLetCommand() throws ParseError {
      TextIO.skipBlanks();
      if ( ! Character.isLetter(TextIO.peek()) )
         throw new ParseError("Expected variable name after 'let'.");
      String varName = readWord();  // The name of the variable.
      TextIO.skipBlanks();
      if ( TextIO.peek() != '=' )
         throw new ParseError("Expected '=' operator for 'let' command.");
      TextIO.getChar();
      double val = expressionValue();  // The value of the variable.
      TextIO.skipBlanks();
      if ( TextIO.peek() != '\n' )
         throw new ParseError("Extra data after end of expression.");
      symbolTable.put( varName, val );  // Add to symbol table.
      TextIO.putln("ok");
   }


   /**
    * Process a command of the form  print <expression>.
    * When this method is called, the word "print" has already
    * been read.  Evaluate the expression and print the value.
    */
   private static void doPrintCommand() throws ParseError {
      double val = expressionValue();
      TextIO.skipBlanks();
      if ( TextIO.peek() != '\n' )
         throw new ParseError("Extra data after end of expression.");
      TextIO.putln("Value is " + val);
   }


   /**
    * Read an expression from the current line of input and return its value.
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
      val = termValue();  // An expression must start with a term.
      if (negative)
         val = -val; // Apply the leading minus sign
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
    */
   private static double termValue() throws ParseError {
      TextIO.skipBlanks();
      double val;  // The value of the term.
      val = factorValue();  // A term must start with a factor.
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
    */
   private static double factorValue() throws ParseError {
      TextIO.skipBlanks();
      double val;  // Value of the factor.
      val = primaryValue();  // A factor must start with a primary.
      TextIO.skipBlanks();
      while ( TextIO.peek() == '^' ) {
            // Read the next primary, and exponentiate
            // the value-so-far by the value of this primary.
         TextIO.getChar();
         double nextVal = primaryValue();
         val = Math.pow(val,nextVal);
         if (Double.isNaN(val))
            throw new ParseError("Illegal values for ^ operator.");
         TextIO.skipBlanks();
      }
      return val;
   } // end termValue()


   /**
    *  Read a primary from the current line of input and
    *  return its value.  A primary must be a number,
    *  a variable, or an expression enclosed in parentheses.
    */
   private static double primaryValue() throws ParseError {
      TextIO.skipBlanks();
      char ch = TextIO.peek();
      if ( Character.isDigit(ch) ) {
            // The factor is a number.  Read it and
            // return its value.
         return TextIO.getDouble();
      }
      else if ( Character.isLetter(ch) ) {
            // The factor is a variable.  Read its name and
            // look up its value in the symbol table.  If the
            // variable is not in the symbol table, an error
            // occurs.  (Note that the values in the symbol
            // table are objects of type Double.)
         String name = readWord();
         Double val = symbolTable.get(name);
         if (val == null)
            throw new ParseError("Unknown variable \"" + name + "\"");
         return val.doubleValue();
      }
      else if ( ch == '(' ) {
            // The factor is an expression in parentheses.
            // Return the value of the expression.
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
      else if ( ch == '+' || ch == '-' || ch == '*' || ch == '/')
         throw new ParseError("Misplaced operator.");
      else
         throw new ParseError("Unexpected character \"" + ch + "\" encountered.");
   }


   /**
    *  Reads a word from input.  A word is any sequence of
    *  letters and digits, starting with a letter.  When 
    *  this subroutine is called, it should already be
    *  known that the next character in the input is
    *  a letter.
    */
   private static String readWord() {
      String word = "";  // The word.
      char ch = TextIO.peek();
      while (Character.isLetter(ch) || Character.isDigit(ch)) {
         word += TextIO.getChar(); // Add the character to the word.
         ch = TextIO.peek();
      }
      return word;
   }

} // end class SimpleInterpreter
