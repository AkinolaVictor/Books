
/**
 * This program evaluates postfix expressions entered by the
 * user.  The expressions can use non-negative real numbers and
 * the operators +, -, *, /, and ^ (where ^ represents exponentiation).
 * Numbers must begin with a digit.  That is, you have to say
 * 0.7 rather than just .7.  The value of an expression might
 * be given as infinity or NaN.
 * 
 * A stack is used to evaluate the expression.  The stack is represented
 * by an object of type StackOfDouble.  This program depends on the non-standard
 * classes StackOfDouble and TextIO.
 * 
 * For demonstration purposes, the stack operations are reported as
 * they are performed.
 */
public class PostfixEval {

   public static void main(String[] args) {

      TextIO.putln("This program can evaluate postfix expressions such as\n");
      TextIO.putln("        2 2 +");
      TextIO.putln("or");
      TextIO.putln("        7.3 89.2 + 9 1.83 * 2 + /\n");
      TextIO.putln("The operators +, -, *, /, and ^ can be used.");

      while (true) {
             // Get and process one line of input from the user.
         TextIO.putln("\n\n\nEnter a postfix expression or press return to end:\n");
         TextIO.put("?  ");
         TextIO.skipBlanks();
         if (TextIO.peek() == '\n') {
                // If the line is empty (except for spaces), we are done.
            break;
         }
         readAndEvaluate();  // Process the input.
         TextIO.getln();  // Discard the end-of-line.
      }

      TextIO.putln("\n\nExiting program.");

   } // end main();



   /**
    *  Read one line of input and process it as a postfix expression.
    *  If the input is not a legal postfix expression, then an error
    *  message is displayed.  Otherwise, the value of the expression
    *  is displayed.  It is assumed that the first character on
    *  the input line is a non-blank.  (This is checked in the
    *  main() routine.)
    */
   private static void readAndEvaluate() {

      StackOfDouble stack;  // For evaluating the expression.

      stack = new StackOfDouble();  // Make a new, empty stack.

      TextIO.putln();

      while (TextIO.peek() != '\n') {

         if ( Character.isDigit(TextIO.peek()) ) {
                // The next item in input is a number.  Read it and
                // save it on the stack.
            double num = TextIO.getDouble();
            stack.push(num);
            TextIO.putln("   Pushed constant " + num);
         }
         else {
                // Since the next item is not a number, the only thing
                // it can legally be is an operator.  Get the operator
                // and perform the operation.
            char op;  // The operator, which must be +, -, *, /, or ^.
            double x,y;     // The operands, from the stack, for the operation.
            double answer;  // The result, to be pushed onto the stack.
            op = TextIO.getChar();
            if (op != '+' && op != '-' && op != '*' && op != '/' && op != '^') {
                   // The character is not one of the acceptable operations.
               TextIO.putln("\nIllegal operator found in input: " + op);
               return;
            }
            if (stack.isEmpty()) {
               TextIO.putln("   Stack is empty while trying to evaluate " + op);
               TextIO.putln("\nNot enough numbers in expression!");
               return;
            }
            y = stack.pop();
            if (stack.isEmpty()) {
               TextIO.putln("   Stack is empty while trying to evaluate " + op);
               TextIO.putln("\nNot enough numbers in expression!");
               return;
            }
            x = stack.pop();
            switch (op) {
            case '+':  
               answer = x + y; 
               break;
            case '-':  
               answer = x - y;  
               break;
            case '*':  
               answer = x * y;  
               break;
            case '/':  
               answer = x / y;  
               break;
            default:   
               answer = Math.pow(x,y);  // (op must be '^'.)
            }
            stack.push(answer);
            TextIO.putln("   Evaluated " + op + " and pushed " + answer);
         }

         TextIO.skipBlanks();

      }  // end while

      // If we get to this point, the input has been read successfully.
      // If the expression was legal, then the value of the expression is
      // on the stack, and it is the only thing on the stack.

      if (stack.isEmpty()) {  // Impossible if the input is really non-empty.
         TextIO.putln("No expression provided.");
         return;
      }

      double value = stack.pop();  // Value of the expression.
      TextIO.putln("   Popped " + value + " at end of expression.");

      if (stack.isEmpty() == false) {
         TextIO.putln("   Stack is not empty.");
         TextIO.putln("\nNot enough operators for all the numbers!");
         return;
      }

      TextIO.putln("\nValue = " + value);


   } // end readAndEvaluate()


} // end class PostfixEval

