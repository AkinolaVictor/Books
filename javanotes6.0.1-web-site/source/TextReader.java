

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The TextReader class provides methods for reading data expressed in human-readable
 * character format.  A TextReader can be used as a wrapper for any Reader or
 * InputStream to enable easy character-based input.
 * 
 * Note that all of the input methods in this class throw errors of type
 * IOException.  An IOException can occur when an attempt is made to read
 * data from the input source.  An error can occur if an attempt is made to
 * read past the end of the input source; the exception in this case is of
 * type TextReader.EndOfStreamError, which is a subclass of IOException.
 * An error can also occur if an attempt is made to read data of a particular
 * type from the input and the next item in input is not of the correct type;
 * in this case, the error is of type TextReader.BadDataException, which is
 * another subclass of IOException.
 * 
 * Once an input stream has been wrapped in a TextReader, data should only be
 * read from the stream using the wrapper.  This is because the TextReader reads
 * and buffers some data internally, and any data that has been buffered is not
 * available for reading except through the TextReader.
 */
public class TextReader {


   /**
    * The value returned by the peek() method when the input is at end-of-stream.
    * (The value of this constant is (char)0xFFFF.)
    */
   public final char EOF = (char)0xFFFF; 

   /**
    * The value returned by the peek() method when the input is at end-of-line.
    * The value of this constant is the character '\n'.
    */
   public final char EOLN = '\n'; 
   
   /**
    * Represents the error of trying to read past the end of the input source
    * of the TextReader.  Users of the class could catch this exception to
    * detect end-of-stream.  This is a subclass of IOException, so catching
    * IOException will also catch end-of-stream errors.
    */
   public static class EndOfStreamException extends IOException {
      public EndOfStreamException() {
         super("Attempt to read past end-of-stream.");
      }
   }
   
   /**
    * Represents the error that occurs when an attempt is made to read some type
    * of data, and the next item in the stream is not of the correct type.  
    * This is a subclass of IOException, so catching
    * IOException will also catch BadDataException.
    */
   public static class BadDataException extends IOException {
      public BadDataException(String errorMessage) {
         super(errorMessage);
      }
   }
   

   
    // ***************************** Constructors and closing *********************

   
   /**
    * Create a TextReader that will take its input from a specified Reader.
    * @s the non-null Reader from which the TextReader will read.
    * @throws NullPointerException if s is null.
    */
   public TextReader(Reader s) {
      if ( s == null )
         throw new NullPointerException("Can't create a TextReader for a null stream.");
      if (s instanceof BufferedReader)
         in = (BufferedReader)s;
      else
         in = new BufferedReader(s);
   }

   
   /**
    * Create a TextReader that will take its input from a specified InputStream.
    * (Internally, the InputStream is wrapped in a Reader of type InputStreamReader.)
    * @s the non-null InputStream from which the TextReader will read.
    * @throws NullPointerException if s is null.
    */
   public TextReader(InputStream s) {
      this( new InputStreamReader(s) );
   }
      
   
   /**
    * Closes the stream that is the input source for this TextReader by
    * calling its close() method.  Does not throw any exceptions; if
    * an exception occurs when the input source is closed, that exception
    * is ignored.
    */
   public void close()  {
      try {   
         in.close();
      }
      catch (IOException e) {
      }
   }
   

   // *************************** Input Methods *********************************
   
   /**
    * Test whether the next character in the input source is an end-of-line.  Note that
    * this method does NOT skip whitespace before testing for end-of-line -- if you want to do
    * that, call skipBlanks() first.
    */
   public boolean eoln() throws IOException { 
      return peek() == '\n'; 
   }

   /**
    * Test whether the next character in the input source is an end-of-file.  Note that
    * this method does NOT skip whitespace before testing for end-of-line -- if you want to do
    * that, call skipBlanks() or skipWhitespace() first.
    */
   public boolean eof() throws IOException  { 
      return peek() == EOF; 
   }
   
   /**
    * Reads the next character from the input source.  The character can be a whitespace
    * character; compare this to the getChar() method, which skips over whitespace and returns the
    * next non-whitespace character.  An end-of-line is always returned as the character '\n', even
    * when the actual end-of-line in the input source is something else, such as '\r' or "\r\n".
    */
   public char getAnyChar() throws IOException { 
      return readChar(); 
   }

   /**
    * Returns the next character in the input source, without actually removing that
    * character from the input.  The character can be a whitespace character and can be the
    * end-of-file character (specified by the constant TextIO.EOF). An end-of-line is always returned 
    * as the character '\n', even when the actual end-of-line in the input source is something else, 
    * such as '\r' or "\r\n". 
    */
   public char peek() throws IOException { 
      return lookChar();
   }
   
   /**
    * Skips over any whitespace characters, except for end-of-lines.  After this method is called,
    * the next input character is either an end-of-line, an end-of-file, or a non-whitespace character.
    */
   public void skipBlanks() throws IOException { 
      char ch=lookChar();
      while (ch != EOF && ch != '\n' && Character.isWhitespace(ch)) {
         readChar();
         ch = lookChar();
      }
   }

   /**
    * Skips over any whitespace characters, including for end-of-lines.  After this method is called,
    * the next input character is either an end-of-file or a non-whitespace character.
    */
   private void skipWhitespace()  throws IOException{
      char ch=lookChar();
      while (ch != EOF && Character.isWhitespace(ch)) {
         readChar();
         ch = lookChar();
      }
   }

   /**
    * Skips whitespace characters and then reads a value of type byte from input, 
    * discarding the rest of the current line of input (including the next end-of-line 
    * character, if any).  An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public byte getlnByte()  throws IOException{ 
      byte x=getByte(); 
      emptyBuffer(); 
      return x; 
   }
   
   /**
    * Skips whitespace characters and then reads a value of type short from input, 
    * discarding the rest of the current line of input (including the next end-of-line 
    * character, if any).  An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public short getlnShort() throws IOException {
      short x=getShort();
      emptyBuffer(); 
      return x; 
   }
   
   /**
    * Skips whitespace characters and then reads a value of type int from input, 
    * discarding the rest of the current line of input (including the next end-of-line 
    * character, if any).  An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public int getlnInt() throws IOException { 
      int x=getInt(); 
      emptyBuffer(); 
      return x; 
   }
   
   /**
    * Skips whitespace characters and then reads a value of type long from input, 
    * discarding the rest of the current line of input (including the next end-of-line 
    * character, if any).  An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public long getlnLong() throws IOException {
      long x=getLong(); 
      emptyBuffer(); 
      return x;
   }
   
   /**
    * Skips whitespace characters and then reads a value of type float from input, 
    * discarding the rest of the current line of input (including the next end-of-line 
    * character, if any).  An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public float getlnFloat() throws IOException {
      float x=getFloat(); 
      emptyBuffer(); 
      return x;
   }
   
   /**
    * Skips whitespace characters and then reads a value of type double from input, 
    * discarding the rest of the current line of input (including the next end-of-line 
    * character, if any).  An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public double getlnDouble() throws IOException { 
      double x=getDouble(); 
      emptyBuffer(); 
      return x; 
   }
   
   /**
    * Skips whitespace characters and then reads a value of type char from input, 
    * discarding the rest of the current line of input (including the next end-of-line 
    * character, if any).  An error occurs if an attempt is made to read past end-of-file
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source.
    */
   public char getlnChar() throws IOException {
      char x=getChar(); 
      emptyBuffer(); 
      return x;
   }
   
   /**
    * Skips whitespace characters and then reads a value of type double from input, 
    * discarding the rest of the current line of input (including the next end-of-line 
    * character, if any).  An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input. 
    * <p>Legal inputs for a boolean input are: true, t, yes, y, 1, false, f, no, n, 
    * and 0; letters can be either upper case or lower case. One "word" of input is read, 
    * using the getWord() method, and it must be one of these; note that the "word" 
    * must be terminated by a whitespace character (or end-of-file).
    */
   public boolean getlnBoolean() throws IOException { 
      boolean x=getBoolean(); 
      emptyBuffer();
      return x; 
   }
   
   /**
    * Skips whitespace characters and then reads one "word" from input, discarding the rest of 
    * the current line of input (including the next end-of-line character, if any).  A word is 
    * defined as a sequence of non-whitespace characters (not just letters!).  An error occurs 
    * if an attempt is made to read past end-of-file or if an IOException is thrown when an 
    * attempt is made to read data from the input source.
    */
   public String getlnWord() throws IOException {
      String x=getWord(); 
      emptyBuffer(); 
      return x; 
   }
   
   /**
    * This is identical to getln().
    */
   public String getlnString() throws IOException {
      return getln();
   } 
   
   /**
    * Reads all the characters from the input source, up to the next end-of-line.  The end-of-line
    * is read but is not included in the return value.  Any other whitespace characters on the line 
    * are retained, even if they occur at the start of input.  The return value will be an empty 
    * string if there are no characters before the end-of-line.  An error occurs if an attempt is 
    * made to read past end-of-file or if an IOException is thrown when an attempt is made to 
    * read data from the input source.
    */
   public String getln() throws IOException {
      StringBuffer s = new StringBuffer(100);
      char ch = readChar();
      while (ch != '\n') {
         s.append(ch);
         ch = readChar();
      }
      return s.toString();
   }
   
   /**
    * Skips whitespace characters and then reads a value of type byte from input.
    * Any characters that remain on the line are saved for subsequent input operations.
    * An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public byte getByte() throws IOException   { 
      return (byte)readInteger(-128L,127L); 
   }

   /**
    * Skips whitespace characters and then reads a value of type short from input.
    * Any characters that remain on the line are saved for subsequent input operations.
    * An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public short getShort()  throws IOException{ 
      return (short)readInteger(-32768L,32767L);
   }   
   
   /**
    * Skips whitespace characters and then reads a value of type int from input.
    * Any characters that remain on the line are saved for subsequent input operations.
    * An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public int getInt()   throws IOException   { 
      return (int)readInteger(Integer.MIN_VALUE, Integer.MAX_VALUE);
   }
   
   /**
    * Skips whitespace characters and then reads a value of type long from input.
    * Any characters that remain on the line are saved for subsequent input operations.
    * An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public long getLong()  throws IOException  { 
      return readInteger(Long.MIN_VALUE, Long.MAX_VALUE); 
   }
   
   /**
    * Skips whitespace characters and then reads a value of type char from input.
    * Any characters that remain on the line are saved for subsequent input operations.
    * An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public char getChar() throws IOException { 
      skipWhitespace();
      return readChar();
   }
   
   /**
    * Skips whitespace characters and then reads a value of type float from input.
    * Any characters that remain on the line are saved for subsequent input operations.
    * An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public float getFloat() throws IOException {
      float x = 0.0F;
      while (true) {
         String str = readRealString();
         if (str == null) {
            errorMessage("Floating point number not found.",
                  "Real number in the range " + (-Float.MAX_VALUE) + " to " + Float.MAX_VALUE);
         }
         else {
            try { 
               x = Float.parseFloat(str); 
            }
            catch (NumberFormatException e) {
               errorMessage("Illegal floating point input, " + str + ".",
                     "Real number in the range " +  (-Float.MAX_VALUE) + " to " + Float.MAX_VALUE);
               continue;
            }
            if (Float.isInfinite(x)) {
               errorMessage("Floating point input outside of legal range, " + str + ".",
                     "Real number in the range " +  (-Float.MAX_VALUE) + " to " + Float.MAX_VALUE);
               continue;
            }
            break;
         }
      }
      return x;
   }
   
   /**
    * Skips whitespace characters and then reads a value of type double from input.
    * Any characters that remain on the line are saved for subsequent input operations.
    * An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    */
   public double getDouble() throws IOException {
      double x = 0.0;
      while (true) {
         String str = readRealString();
         if (str == null) {
            errorMessage("Floating point number not found.",
                  "Real number in the range " + (-Double.MAX_VALUE) + " to " + Double.MAX_VALUE);
         }
         else {
            try { 
               x = Double.parseDouble(str); 
            }
            catch (NumberFormatException e) {
               errorMessage("Illegal floating point input, " + str + ".",
                     "Real number in the range " + (-Double.MAX_VALUE) + " to " + Double.MAX_VALUE);
               continue;
            }
            if (Double.isInfinite(x)) {
               errorMessage("Floating point input outside of legal range, " + str + ".",
                     "Real number in the range " + (-Double.MAX_VALUE) + " to " + Double.MAX_VALUE);
               continue;
            }
            break;
         }
      }
      return x;
   }
   
   /**
    * Skips whitespace characters and then reads one "word" from input.  Any characters that
    * remain on the line are saved for subsequent input operations.  A word is 
    * defined as a sequence of non-whitespace characters (not just letters!).  An error occurs 
    * if an attempt is made to read past end-of-file or if an IOException is thrown when an 
    * attempt is made to read data from the input source.
    */
   public String getWord() throws IOException {
      skipWhitespace();
      StringBuffer str = new StringBuffer(50);
      char ch = lookChar();
      while (ch == EOF || !Character.isWhitespace(ch)) {
         str.append(readChar());
         ch = lookChar();
      }
      return str.toString();
   }
   
   
   /**
    * Skips whitespace characters and then reads a value of type boolean from input.
    * Any characters that remain on the line are saved for subsequent input operations.
    * An error occurs if an attempt is made to read past end-of-file,
    * or if an IOException is thrown when an attempt is made to read data from the
    * input source, or if a value of the correct type is not found in the input.
    * <p>Legal inputs for a boolean input are: true, t, yes, y, 1, false, f, no, n, 
    * and 0; letters can be either upper case or lower case. One "word" of input is 
    * read, using the getWord() method, and it must be one of these; note that the "word"  
    * must be terminated by a whitespace character (or end-of-file).
    */
   public boolean getBoolean() throws IOException {
      boolean ans = false;
      while (true) {
         String s = getWord();
         if ( s.equalsIgnoreCase("true") || s.equalsIgnoreCase("t") ||
               s.equalsIgnoreCase("yes")  || s.equalsIgnoreCase("y") ||
               s.equals("1") ) {
            ans = true;
            break;
         }
         else if ( s.equalsIgnoreCase("false") || s.equalsIgnoreCase("f") ||
               s.equalsIgnoreCase("no")  || s.equalsIgnoreCase("n") ||
               s.equals("0") ) {
            ans = false;
            break;
         }
         else
            errorMessage("Illegal boolean input value.",
            "one of:  true, false, t, f, yes, no, y, n, 0, or 1");
      }
      return ans;
   }
   
   // ***************** Everything beyond this point is private implementation detail *******************
   
   private BufferedReader in;  // The actual source of the input.
   
   private Matcher integerMatcher;  // Used for reading integer numbers; created from the integer Regex Pattern.
   private Matcher floatMatcher;   // Used for reading floating point numbers; created from the floatRegex Pattern.
   private final Pattern integerRegex = Pattern.compile("(\\+|-)?[0-9]+");
   private final Pattern floatRegex = Pattern.compile("(\\+|-)?(([0-9]+(\\.[0-9]*)?)|(\\.[0-9]+))((e|E)(\\+|-)?[0-9]+)?");
   
   private String buffer = null;  // One line read from input.
   private int pos = 0;           // Position of next char in input line that has not yet been processed.
   
   private String readRealString() throws IOException {   // read chars from input following syntax of real numbers
      skipWhitespace();
      if (lookChar() == EOF)
         return null;
      if (floatMatcher == null)
         floatMatcher = floatRegex.matcher(buffer);
      floatMatcher.region(pos,buffer.length());
      if (floatMatcher.lookingAt()) {
         String str = floatMatcher.group();
         pos = floatMatcher.end();
         return str;
      }
      else 
         return null;
   }
   
   private String readIntegerString() throws IOException {  // read chars from input following syntax of integers
      skipWhitespace();
      if (lookChar() == EOF)
         return null;
      if (integerMatcher == null)
         integerMatcher = integerRegex.matcher(buffer);
      integerMatcher.region(pos,buffer.length());
      if (integerMatcher.lookingAt()) {
         String str = integerMatcher.group();
         pos = integerMatcher.end();
         return str;
      }
      else 
         return null;
   }
   
   private long readInteger(long min, long max) throws IOException {  // read long integer, limited to specified range
      long x=0;
      while (true) {
         String s = readIntegerString();
         if (s == null){
            errorMessage("Integer value not found in input.",
                  "Integer in the range " + min + " to " + max);
         }
         else {
            String str = s.toString();
            try { 
               x = Long.parseLong(str);
            }
            catch (NumberFormatException e) {
               errorMessage("Illegal integer input, " + str + ".",
                     "Integer in the range " + min + " to " + max);
               continue;
            }
            if (x < min || x > max) {
               errorMessage("Integer input outside of legal range, " + str + ".",
                     "Integer in the range " + min + " to " + max);
               continue;
            }
            break;
         }
      }
      return x;
   }
   
   
   private void errorMessage(String message, String expecting) throws IOException {  // Report error on input.
         throw new BadDataException("Error in input:  " + message + 
               "; Expecting " + expecting);
   }
   
   private char lookChar() throws IOException {  // return next character from input
      if (buffer == null || pos > buffer.length())
         fillBuffer();
      if (buffer == null)
         return EOF;
      else if (pos == buffer.length())
         return '\n';
      else 
         return buffer.charAt(pos);
   }
   
   private char readChar() throws IOException {  // return and discard next character from input
      char ch = lookChar();
      if (buffer == null) {
         throw new EndOfStreamException();
      }
      pos++;
      return ch;
   }
      
   private void fillBuffer() throws IOException {    // Wait for user to type a line and press return,
      buffer = in.readLine();
      pos = 0;
      floatMatcher = null;
      integerMatcher = null;
   }
   
   private void emptyBuffer() {   // discard the rest of the current line of input
      buffer = null;
   }

   
      
} // end of class TextReader
