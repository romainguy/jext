/*
 * Copyright (c) 1997, 1998 Tal Davidson. All rights reserved.
 *
 * JSBeautifier 1.1.1
 * by Tal Davidson (davidsont@bigfoot.com)
 *
 * JSBeautifier 1.1.1 is distributed under the "Artistic License" detailed below:
 *
 *
 *                           The ``Artistic License''
 *
 * Preamble
 * The intent of this document is to state the conditions under which a Package may
 * be copied, such that the Copyright Holder maintains some semblance of artistic
 * control over the development of the package, while giving the users of the
 * package the right to use and distribute the Package in a more-or-less customary
 * fashion, plus the right to make reasonable modifications.
 *
 * Definitions
 *     ``Package'' refers to the collection of files distributed by the Copyright
 *     Holder, and derivatives of that collection of files created through textual
 *     modification.
 *     ``Standard Version'' refers to such a Package if it has not been modified,
 *     or has been modified in accordance with the wishes of the Copyright Holder
 *     as specified below.
 *     ``Copyright Holder'' is whoever is named in the copyright or copyrights for
 *     the package.
 *     ``You'' is you, if you're thinking about copying or distributing this
 *     Package.
 *     ``Reasonable copying fee'' is whatever you can justify on the basis of media
 *     cost, duplication charges, time of people involved, and so on. (You will not
 *     be required to justify it to the Copyright Holder, but only to the computing
 *     community at large as a market that must bear the fee.)
 *     ``Freely Available'' means that no fee is charged for the item itself,
 *     though there may be fees involved in handling the item. It also means that
 *     recipients of the item may redistribute it under the same conditions they
 *     received it.
 *
 *     1. You may make and give away verbatim copies of the source form of the
 *        Standard Version of this Package without restriction, provided that you
 *        duplicate all of the original copyright notices and associated disclaimers.
 *     2. You may apply bug fixes, portability fixes and other modifications derived
 *        from the Public Domain or from the Copyright Holder. A Package modified in
 *        such a way shall still be considered the Standard Version.
 *     3. You may otherwise modify your copy of this Package in any way, provided that
 *        you insert a prominent notice in each changed file stating how and when you
 *        changed that file, and provided that you do at least ONE of the following:
 *         a. place your modifications in the Public Domain or otherwise make them
 *            Freely Available, such as by posting said modifications to Usenet or an
 *            equivalent medium, or placing the modifications on a major archive site
 *            such as uunet.uu.net, or by allowing the Copyright Holder to include
 *            your modifications in the Standard Version of the Package.
 *         b. use the modified Package only within your corporation or organization.
 *         c. rename any non-standard executables so the names do not conflict with
 *            standard executables, which must also be provided, and provide a
 *            separate manual page for each non-standard executable that clearly
 *            documents how it differs from the Standard Version.
 *         d. make other distribution arrangements with the Copyright Holder.
 *     4. You may distribute the programs of this Package in object code or executable
 *        form, provided that you do at least ONE of the following:
 *         a. distribute a Standard Version of the executables and library files,
 *            together with instructions (in the manual page or equivalent) on where
 *            to get the Standard Version.
 *         b. accompany the distribution with the machine-readable source of the
 *            Package with your modifications.
 *         c. give non-standard executables non-standard names, and clearly document
 *            the differences in manual pages (or equivalent), together with
 *            instructions on where to get the Standard Version.
 *         d. make other distribution arrangements with the Copyright Holder.
 *     5. You may charge a reasonable copying fee for any distribution of this
 *        Package. You may charge any fee you choose for support of this Package. You
 *        may not charge a fee for this Package itself. However, you may distribute
 *        this Package in aggregate with other (possibly commercial) programs as part
 *        of a larger (possibly commercial) software distribution provided that you do
 *        not advertise this Package as a product of your own. You may embed this
 *        Package's interpreter within an executable of yours (by linking); this shall
 *        be construed as a mere form of aggregation, provided that the complete
 *        Standard Version of the interpreter is so embedded.
 *     6. The scripts and library files supplied as input to or produced as output
 *        from the programs of this Package do not automatically fall under the
 *        copyright of this Package, but belong to whomever generated them, and may be
 *        sold commercially, and may be aggregated with this Package. If such scripts
 *        or library files are aggregated with this Package via the so-called "undump"
 *        or "unexec" methods of producing a binary executable image, then
 *        distribution of such an image shall neither be construed as a distribution
 *        of this Package nor shall it fall under the restrictions of Paragraphs 3 and
 *        4, provided that you do not represent such an executable image as a Standard
 *        Version of this Package.
 *     7. C subroutines (or comparably compiled subroutines in other languages)
 *        supplied by you and linked into this Package in order to emulate subroutines
 *        and variables of the language defined by this Package shall not be
 *        considered part of this Package, but are the equivalent of input as in
 *        Paragraph 6, provided these subroutines do not change the language in any
 *        way that would cause it to fail the regression tests for the language.
 *     8. Aggregation of this Package with a commercial distribution is always
 *        permitted provided that the use of this Package is embedded; that is, when
 *        no overt attempt is made to make this Package's interfaces visible to the
 *        end user of the commercial distribution. Such use shall not be construed as
 *        a distribution of this Package.
 *     9. The name of the Copyright Holder may not be used to endorse or promote
 *        products derived from this software without specific prior written
 *        permission.
 *        THIS PACKAGE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 *        WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *        MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * The End
 */
import java.util.*;
import java.io.*;

/**
 * <code> JSBeautifier </code> (formerly called Beautifier) is a filter for automatic
 * indentation of Java source code. Every line of the original source file should be
 * sent one after the other as a String to the beautify(lineString) method, which
 * returns an automatically indented version of the line it recieves, according to
 * the data in the past lines recieved.
 *
 * Every time a JSBeautifier instance is to be reused for a new file, a call must first
 * be made to its init() method.
 *
 * JSBeautifier can be used either as an object in a program or from the command line.
 *
 * When used from the command line, JSBeautifier can be used both as a filter
 * from standard-input to standard-output, i.e.:
 *     [/home/tald]$ java jstyle.JSBeautifier [flags] < sourceFile.java > resultingFile.java
 * or as a filter to specifically named files, i.e:
 *     [/home/tald]$ java jstyle.JSBeautifier [flags] File1.java File2.java File3.java
 * When Giving JSBeautifier the name of a specific source file, output will
 * be created to another file with the same name, BUT with an added suffix of ".js"
 * Thus, a file named "File1.java" will be renamed to "File1.java.js"
 *
 * Flag options:
 *     -t  (for tabs)
 *     -s# (for '#' spaces per indent, i.e.: -s2)
 *     -ib (add extra indentation to brackets)
 *     -fs (flush (i.e. don't add extra indentation to) switch statements - ala Java Code Convention).
 *     -h  (for help message)
 * The current default setup is 4 spaces per indent, 1 space before every comment line
 *
 *
 * Bug Reporting:
 * 1. If anyone corrects a found bug, please send me an example source-file
 *    that creates the bug, and the corrected version of this file, so that
 *    I can post it.
 * 2. Otherwise, please send me an example source-file cerating the bug, and
 *    the bug description, and i will do my best to correct the bug as soon
 *    as possible.
 *
 *
 * Acknowledgments:
 * - Thanks to Jim Watson for addition of the Help option !!!
 *
 *
 * @author	Tal Davidson <a href=mailto:davidsont@bigfoot.com>davidsont@bigfoot.com</a>
 * @version 1.1.1,  October 10th, 1998
 */
public class JSBeautifier
{
  // headers[] - an array of headers that require indentation
  private static String headers[] = { "if", "else", "for", "while", "do", "try",
                                      "catch", "synchronized", "switch", "case", "default", "static" };

  // nonParenHeaders[] - an array of headers that DONT require parenthesies after them
  private static String nonParenHeaders[] = {"else", "do", "try", "static"};

  // preBlockStatements[] - an array of headers that exist within statements immediately preceding blocks
  private static String preBlockStatements[] = {"class", "interface", "throws"};

  // assignmentOperators[] - an array of assignment operators
  private static String assignmentOperators[] = {"<<", ">>", "=", "+=", "-=", "*=", "/=", "%=", "|=", "&=", "return"};

  // nonAssignmentOperators[] - an array of non-assignment operators
  private static String nonAssignmentOperators[] = {"==", "++", "--", "!="};

  // headerStack - a stack of the headers responsible for indentations of the current char
  private Stack headerStack;

  // tempStacks - a stack of Stacks. Each inner stack holds the current header-list in a { } block.
  // The innermost { } block's stack sits at the top of the tempStacks.
  private Stack tempStacks;

  // blockParenDepthStack - stack of the number of parenthesies that are open when new NESTED BLOCKS are created.
  private Stack blockParenDepthStack;

  // blockStatementStack - stack of the states of 'isInStatement' when new NESTED BLOCKS are created.
  private Stack blockStatementStack;

  // parenStatementStack - stack of the states of 'isInStatement' when new NESTED PARENTHESIES are created.
  private Stack parenStatementStack;

  // inStatementIndentStack - stack of LOCATIONS of in-statement indents
  private Stack inStatementIndentStack;

  // inStatementIndentStackSizeStack - stack of SIZES of inStatementIndentStack stacks
  private Stack inStatementIndentStackSizeStack;

  // parenIndentStack - stack of LOCATIONS of '(' or '[' chars
  private Stack parenIndentStack;

  // bracketBlockStateStack - stack of types of nested '{' brackets.
  // Each element of the stack is either True (=the beginner of a block), or False (=the beginner of a
  // static array).
  private Stack bracketBlockStateStack;

  // isSpecialChar - true if a there exists a '\' preceding the current chararacter.
  //   i.e. \n, \t, \\, ...
  private boolean isSpecialChar;

  // isInQuote - true when the current character is part of a quote (i.e. 'g' or "ffff")
  private boolean isInQuote;

  // isInComment - true when current character is part of a /* */ comment
  private boolean isInComment;

  // isInCase - true if in middle of a case statement (inside a switch);
  private boolean isInCase;

  // isInQuestion - true if in the middle of a '? :' statement
  private boolean isInQuestion;

  // isInStatement - true when current character is a part of an ongoing statement
  private boolean isInStatement;

  // isInClassHeader - true if inside a 'class' statement
  private boolean isInClassHeader;

  // isInClassHeaderTab - true if a special tab has been activated for the 'class statement'
  private boolean isInClassHeaderTab;

  // switchIndent - true if switch blocks should have an additional internal indent.
  private boolean switchIndent;

  // bracketIndent - true if brackets should have an added indent.
  private boolean bracketIndent;

  // quoteChar - the quote delimeter of a quote (' or ")
  private char quoteChar;

  // commmentIndent - the number of spaces to indent when in a comment
  private int commentIndent = 1;

  // parenDepth - the depth of parenthesies around the current character
  private int parenDepth;

  // indentString - the String to be used for every indentation
  // - either a "\t" or a String of n spaces.
  private String indentString;

  // indentLength - the length of one indent unit.
  private int indentLength;

  // blockTabCount - stores number of tabs to add to begining of line
  // due to statements with INNER blocks inside open parenthesies.
  private int blockTabCount;

  private int statementTabCount;

  private int leadingWhiteSpaces;

  private int maxInStatementIndent;

  private char prevNonSpaceCh;

  private char currentNonSpaceCh;

  private String currentHeader;

  private boolean isInHeader;

  private String immediatelyPreviousAssignmentOp;



  public static void main(String args[])
  {
    JSBeautifier beautifier = new JSBeautifier();
    Vector fileNameVector = new Vector();
    BufferedReader inReader = null;
    PrintWriter outWriter = null;
    boolean isHelpShown = false;

    // manage flags
    for (int i=0; i<args.length; i++)
    {
      String arg = args[i];
      if ("-t".equals(arg))
        beautifier.setTabIndentation();
      else if (arg.startsWith("-s"))
      {
        int spaceNum = 4;
        try {
          spaceNum = Integer.valueOf(arg.substring(2)).intValue();
        } catch (NumberFormatException e) {};
        beautifier.setSpaceIndentation(spaceNum);
      }
      else if (arg.startsWith("-m"))
      {
        int maxIndent = 4;
        try {
          maxIndent = Integer.valueOf(arg.substring(2)).intValue();
        } catch (NumberFormatException e) {};
        beautifier.setMaxInStatementIndetation(maxIndent);
      }
      else if ("-ib".equals(arg))
        beautifier.setBracketIndent(true);
      else if ("-fs".equals(arg))
        beautifier.setSwitchIndent(false);
      else if (arg.startsWith("-") && !isHelpShown)
      {
        isHelpShown = true;

        System.err.println("");
        System.err.println("JSBeautifier 1.1.0   (created by Tal Davidson, davidsont@bigfoot.com)");
        System.err.println("");
        System.err.println("Usage  : java jstyle.JSBeautifier [options] < Original.java > Beautified.java");
        System.err.println("         java jstyle.JSBeautifier [options] Foo.java Bar.java  [...]");
        System.err.println("");
        System.err.println("When given a specific file, JSBeautifier will create an output file with a");
        System.err.println("suffix of \".js\" added to the original filename, i.e: Foo.java --> Foo.java.js");
        System.err.println("");
        System.err.println("Options: -t   Indent using tab characters");
        System.err.println("         -s#  Indent using # spaces per indent (i.e. -s4)");
        System.err.println("         -m#  Indent a maximal # spaces in a continuous statement,");
        System.err.println("              relatively to the previous line(i.e. -m40)");
        System.err.println("         -ib  add extra indentation to brackets");
        System.err.println("         -fs  flush (i.e. don't indent) 'switch' blocks");
        System.err.println("         -h   Print this help message");
        System.exit(0);
      }
      else // file-name
        fileNameVector.addElement(arg);

    }

    if (fileNameVector.isEmpty())
    {
      inReader = new BufferedReader(new InputStreamReader(System.in));
      outWriter = new PrintWriter(System.out);
      try {
        beautifier.beautifyReader(inReader, outWriter);
      }
      catch (IOException e) {
        System.err.println("Error: " + e);
      }
      outWriter.close();
    }
    else
    {
      for (int i=0; i<fileNameVector.size(); i++)
      {
        beautifier.init();
        try {
          String fileName = (String) fileNameVector.elementAt(i);
          inReader = new BufferedReader(new FileReader(fileName));
          outWriter = new PrintWriter(new FileWriter(fileName+".js"), true);

          beautifier.beautifyReader(inReader, outWriter);
        }
        catch (IOException e) {
          System.err.println("Error: " + e);
        }

        outWriter.close();
        try {
          inReader.close();
        }
        catch (IOException e) {
          System.err.println("Error: " + e);
        }

      }
    }
  }

  /**
   * beautify input from inreader to outWriter
   *
   * @param      inReader     a BufferedReader from which to input original source code
   * @param      outWriter    a PrintWriter to output beutified source code to
   *
   * @exception  IOException
   */
  public void beautifyReader(BufferedReader inReader, PrintWriter outWriter) throws IOException
  {
    String line = null;

    // beautify source code lines
    try {
      while (true)
      {
        line = inReader.readLine();
        if (line == null)
          break;
        outWriter.println(beautify(line));
      }

    } catch (IOException e) {}
  }

  /**
   * JSBeautifier's constructor.
   */
  public JSBeautifier()
  {
    init();
    setSpaceIndentation(4); // the default indentation of a JSBeautifier object is of 4 spaces per indent
    setMaxInStatementIndetation(40);
    setBracketIndent(false);
    setSwitchIndent(true);
  }

  /**
   * initiate the JSBeautifier.
   *
   * init() should be called every time a JSBeautifier object is to start
   * beautifying a NEW source file.
   */
  public void init()
  {
    headerStack = new Stack();
    tempStacks = new Stack();
    tempStacks.push(new Stack());

    blockParenDepthStack = new Stack();
    blockStatementStack = new Stack();
    parenStatementStack = new Stack();

    bracketBlockStateStack = new Stack();
    bracketBlockStateStack.push(new Boolean(true));

    inStatementIndentStack = new Stack();
    inStatementIndentStackSizeStack = new Stack();
    inStatementIndentStackSizeStack.push(new Integer(0));
    parenIndentStack = new Stack();

    isSpecialChar = false;
    isInQuote = false;
    isInComment = false;
    isInStatement = false;
    isInCase = false;
    isInQuestion = false;
    isInClassHeader = false;
    isInClassHeaderTab = false;
    isInHeader = false;

    immediatelyPreviousAssignmentOp = null;

    parenDepth=0;
    blockTabCount = 0;
    statementTabCount = -1;
    leadingWhiteSpaces = 0;

    prevNonSpaceCh = '{';
    currentNonSpaceCh = '{';
  }

  /**
   * ident using one tab per identation
   */
  public void setTabIndentation()
  {
    indentString = "\t";
    indentLength = 4;
  }

  /**
   * ident a number of spaces for each identation.
   *
   * @param   length     number of spaces per indent.
   */
  public void setSpaceIndentation(int length)
  {
    char spaces[] = new char[length];
    for (int i=0; i<length; i++)
      spaces[i] = ' ';
    indentString = new String(spaces);

    indentLength = length;
  }

  /**
   * set the maximum indentation between two lines in a multi-line statement.
   *
   * @param   max     maximum indentation length.
   */
  public void setMaxInStatementIndetation(int max)
  {
    maxInStatementIndent = max;
  }

  /**
   * set the state of the bracket indentation option. If true, brackets will
   * be indented one additional indent.
   *
   * @param   state             state of option.
   */
  public void setBracketIndent(boolean state)
  {
    bracketIndent = state;
  }

  /**
   * set the state of the switch indentation option. If true, blocks of 'switch'
   * statements will be indented one additional indent.
   *
   * @param   state             state of option.
   */
  public void setSwitchIndent(boolean state)
  {
    switchIndent = state;
  }

  /**
   * beautify a line of source code.
   *
   * every line of source code in a java source code file should be sent
   * one after the other to the beautify method.
   */
  public String beautify(String line)
  {
    boolean isInLineComment = false; // true when the current character is in a // comment (such as this line ...)
    boolean isInSwitch = false;
    char ch = ' ';       // the current character
    char prevCh;         // previous char
    StringBuffer outBuffer = new StringBuffer(); // the newly idented line is bufferd here
    int tabCount = 0;  // number of indents before line
    String lastLineHeader = null; // last header found within line
    boolean closingBracketReached = false;
    int spaceTabCount = 0;
    boolean usePreviousTabCount = false;
    int previousTabCount = 0;

    int headerStackSize = headerStack.size();
    boolean isLineInStatement = isInStatement;
    boolean shouldIndentBrackettedLine = true;

    currentHeader = null;

    // handle and remove white spaces around the line:
    // If not in comment, first find out size of white space before line,
    // so that possible comments starting in the line continue in
    // relation to the preliminary white-space.
    if (!isInComment)
    {
      leadingWhiteSpaces = 0;
      while (leadingWhiteSpaces<line.length() &&
          (line.charAt(leadingWhiteSpaces)==' ' ||
           line.charAt(leadingWhiteSpaces)=='\t'))
        leadingWhiteSpaces++;

      line = line.trim();
    }
    else
    {
      int trimSize;
      for (trimSize=0;
          trimSize < line.length() && trimSize<leadingWhiteSpaces &&
          (line.charAt(trimSize) == ' ' || line.charAt(trimSize) == '\t');
          trimSize++)
        ;

      line = line.substring(trimSize);
    }


    if (line.length() == 0)
      return line;

    if (!inStatementIndentStack.isEmpty())
      spaceTabCount = ((Integer) inStatementIndentStack.peek()).intValue();

    // calculate preliminary indentation based on data from past lines
    for (int i=0; i<headerStackSize; i++)
    {
      if (!(i>0 && !"{".equals(headerStack.elementAt(i-1)) && "{".equals(headerStack.elementAt(i))))
        tabCount++;

      // is the switchIndent option is on, indent switch statements an additional indent.
      if (switchIndent && i > 1 &&
          "switch".equals(headerStack.elementAt(i-1)) &&
          "{".equals(headerStack.elementAt(i))
          )
      {
        tabCount++;
        isInSwitch = true;
      }
    }
    if (isInSwitch && switchIndent && headerStackSize >= 2 &&
        "switch".equals(headerStack.elementAt(headerStackSize-2)) &&
        "{".equals(headerStack.elementAt(headerStackSize-1)) && line.charAt(0) == '}')
      tabCount--;

    if (isInClassHeader)
    {
      isInClassHeaderTab = true;
      tabCount += 2;
    }

    //if (isInStatement)
    //    if (!headerStack.isEmpty() && !"{".equals(headerStack.lastElement()))
    //	tabCount--;



    // parse characters in the current line.
    for (int i=0; i<line.length(); i++)
    {
      prevCh = ch;
      ch = line.charAt(i);

      if (ch=='\n' || ch=='\r')
        continue;

      outBuffer.append(ch);

      if (ch==' ' || ch=='\t')
        continue;

      // handle special characters (i.e. backslash+character such as \n, \t, ...)
      if (isSpecialChar)
      {
        isSpecialChar = false;
        continue;
      }
      if (!(isInComment || isInLineComment) && line.regionMatches(false, i, "\\\\", 0, 2))
      {
        outBuffer.append('\\');
        i++;
        continue;
      }
      if (!(isInComment || isInLineComment) && ch=='\\')
      {
        isSpecialChar = true;
        continue;
      }

      // handle quotes (such as 'x' and "Hello Dolly")
      if (!(isInComment || isInLineComment) && (ch=='"' || ch=='\''))
        if (!isInQuote)
        {
          quoteChar = ch;
          isInQuote = true;
        }
        else if (quoteChar == ch)
        {
          isInQuote = false;
          isInStatement = true;
          continue;
        }
      if (isInQuote)
        continue;

      // handle comments
      if ( !(isInComment || isInLineComment) && line.regionMatches(false, i, "//", 0, 2) )
      {
        isInLineComment = true;
        outBuffer.append("/");
        i++;
        continue;
      }
      else if ( !(isInComment || isInLineComment) && line.regionMatches(false, i, "/*", 0, 2) )
      {
        isInComment = true;
        outBuffer.append("*");
        i++;
        continue;
      }
      else if ( (isInComment || isInLineComment) && line.regionMatches(false, i, "*/", 0, 2) )
      {
        isInComment = false;
        outBuffer.append("/");
        i++;
        continue;
      }

      if (isInComment||isInLineComment)
        continue;


      // if we have reached this far then we are NOT in a comment or string of special character...

      prevNonSpaceCh = currentNonSpaceCh;
      currentNonSpaceCh = ch;

      if (isInHeader)
      {
        isInHeader = false;
        currentHeader = (String) headerStack.peek();
      }
      else
        currentHeader = null;

      // handle parenthesies
      if (ch == '(' || ch == '[' || ch == ')' || ch == ']')
      {
        if (ch == '(' || ch == '[')
        {
          if (parenDepth == 0)
          {
            parenStatementStack.push(new Boolean(isInStatement));
            isInStatement = true;
          }
          parenDepth++;

          inStatementIndentStackSizeStack.push(new Integer(inStatementIndentStack.size()));
          if (currentHeader != null)
          {
            //spaceTabCount-=indentLength;
            inStatementIndentStack.push(new Integer(indentLength*2 + spaceTabCount));
            parenIndentStack.push(new Integer(indentLength*2 + spaceTabCount));

          }
          else
            registerInStatementIndent(line, i, spaceTabCount, isLineInStatement, true);
        }
        else if (ch == ')' || ch == ']')
        {
          parenDepth--;
          if (parenDepth == 0)
          {
            isInStatement = ((Boolean) parenStatementStack.pop()).booleanValue();
            ch = ' ';
          }

          if (!inStatementIndentStackSizeStack.isEmpty())
          {
            int previousIndentStackSize = ((Integer) inStatementIndentStackSizeStack.pop()).intValue();
            while (previousIndentStackSize < inStatementIndentStack.size())
              inStatementIndentStack.pop();

            if (!parenIndentStack.isEmpty())
            {
              Object poppedIndent = parenIndentStack.pop();
              if (i == 0)
                spaceTabCount = ((Integer) poppedIndent).intValue();
            }
          }
        }
        continue;
      }


      if (ch == '{')
      {
        boolean isBlockOpener = false;

        // first, check if '{' is a block-opener or an static-array opener
        isBlockOpener |= (prevNonSpaceCh == '{' &&
                          ((Boolean) bracketBlockStateStack.peek()).booleanValue());

        isBlockOpener |= (prevNonSpaceCh == ')' || prevNonSpaceCh == ';');

        isBlockOpener |= isInClassHeader;

        isInClassHeader = false;

        if (!isBlockOpener && currentHeader != null)
        {
          for (int n=0; n < nonParenHeaders.length; n++)
            if (currentHeader.equals(nonParenHeaders[n]))
            {
              isBlockOpener = true;
              break;
            }
        }

        bracketBlockStateStack.push(new Boolean(isBlockOpener));

        if (!isBlockOpener)
        {
          if (line.length() - i == getNextProgramCharDistance(line, i) &&
              immediatelyPreviousAssignmentOp != null) // && !inStatementIndentStack.isEmpty() - actually not needed
            inStatementIndentStack.pop();
          inStatementIndentStackSizeStack.push(new Integer(inStatementIndentStack.size()));
          registerInStatementIndent(line, i, spaceTabCount, isLineInStatement, true);
          //parenIndentStack.push(new Integer(i+spaceTabCount));
          parenDepth++;
          if (i == 0)
            shouldIndentBrackettedLine = false;

          continue;
        }

        if (isInClassHeader)
          isInClassHeader = false;
        if (isInClassHeaderTab)
        {
          isInClassHeaderTab = false;
          tabCount -= 2;
        }

        blockParenDepthStack.push(new Integer(parenDepth));
        blockStatementStack.push(new Boolean(isInStatement));

        inStatementIndentStackSizeStack.push(new Integer(inStatementIndentStack.size()));

        blockTabCount += isInStatement? 1 : 0;
        parenDepth = 0;
        isInStatement = false;

        tempStacks.push(new Stack());
        headerStack.push("{");
        lastLineHeader = "{";

        continue;
      }

      //check if a header has been reached
      if (prevCh == ' ')
      {
        boolean isDoubleHeader = false;
        int h = findLegalHeader(line, i, headers);
        if (h > -1)
        {
          // if we reached here, then this is a header...

          isInHeader = true;

          Stack lastTempStack = (Stack) tempStacks.peek();

          // if a new block is opened, push a new stack into tempStacks to hold the
          // future list of headers in the new block.
          //if ("{".equals(headers[h]))
          //    tempStacks.push(new Stack());

          // take care of the special case: 'else if (...)'
          if ("if".equals(headers[h]) && "else".equals(lastLineHeader))
            headerStack.pop();

          // take care of 'else'
          else if ("else".equals(headers[h]))
          {
            String header;
            if (lastTempStack != null)
            {
              int indexOfIf = lastTempStack.indexOf("if");
              if (indexOfIf != -1)
              {
                // recreate the header list in headerStack up to the previous 'if'
                // from the temporary snapshot stored in lastTempStack.
                int restackSize = lastTempStack.size() - indexOfIf - 1;
                for (int r=0; r<restackSize; r++)
                  headerStack.push(lastTempStack.pop());
                if (!closingBracketReached)
                  tabCount += restackSize;
              }
              /*
               * If the above if is not true, i.e. no 'if' before the 'else',
               * then nothing beautiful will come out of this...
               * I should think about inserting an Exception here to notify the caller of this...
               */

            }
          }

          // check if 'while' closes a previous 'do'
          else if ("while".equals(headers[h]))
          {
            String header;
            if (lastTempStack != null)
            {
              int indexOfDo = lastTempStack.indexOf("do");
              if (indexOfDo != -1)
              {
                // recreate the header list in headerStack up to the previous 'do'
                // from the temporary snapshot stored in lastTempStack.
                int restackSize = lastTempStack.size() - indexOfDo - 1;
                for (int r=0; r<restackSize; r++)
                  headerStack.push(lastTempStack.pop());
                if (!closingBracketReached)
                  tabCount += restackSize;
              }
            }
          }
          // check if 'catch' closes a previous 'try' or 'catch'
          else if ("catch".equals(headers[h]))
          {
            String header;
            if (lastTempStack != null)
            {
              int indexOfTry = lastTempStack.indexOf("try");
              if (indexOfTry == -1)
                indexOfTry = lastTempStack.indexOf("catch");
              if (indexOfTry != -1)
              {
                // recreate the header list in headerStack up to the previous 'do'
                // from the temporary snapshot stored in lastTempStack.
                int restackSize = lastTempStack.size() - indexOfTry - 1;
                for (int r=0; r<restackSize; r++)
                  headerStack.push(lastTempStack.pop());
                //lastTempStack.pop();
                //headerStack.push("try");

                if (!closingBracketReached)
                  tabCount += restackSize;
              }
            }
          }
          else if ("case".equals(headers[h]) || "default".equals(headers[h]) )
          {
            isInCase = true;
            --tabCount;
          }

          else if (("static".equals(headers[h]) || "synchronized".equals(headers[h])) &&
            !headerStack.isEmpty() &&
            ("static".equals(headerStack.lastElement()) || "synchronized".equals(headerStack.lastElement())))
            isDoubleHeader = true;

          if (!isDoubleHeader)
          {
            spaceTabCount-=indentLength;
            headerStack.push(headers[h]);
          }

          lastLineHeader = headers[h];

          outBuffer.append(headers[h].substring(1));
          i += headers[h].length() - 1;

          //if (parenDepth == 0)
          isInStatement = false;
        }
      }

      if (ch == '?')
        isInQuestion = true;

      // special handling of 'case' statements
      if (ch == ':')
      {
        if (isInQuestion)
        {
          isInQuestion = false;
        }
        else
        {
          currentNonSpaceCh = ';'; // so that brackets after the ':' will appear as block-openers
          if (isInCase)
          {
            isInCase = false;
            ch = ';'; // from here on, treat char as ';'
          }
        }
      }

      if ((ch == ';' || ch == ',') && !inStatementIndentStackSizeStack.isEmpty())
        while (((Integer) inStatementIndentStackSizeStack.peek()).intValue() + (parenDepth>0 ? 1 : 0)  < inStatementIndentStack.size())
          inStatementIndentStack.pop();


      // handle ends of statements
      if ( (ch == ';' && parenDepth == 0) || ch == '}' || (ch == ',' && parenDepth == 0))
      {
        if (ch == '}')
        {
          // first check if this '}' closes a previous block, or a static array...
          if (  !bracketBlockStateStack.isEmpty() && !((Boolean) bracketBlockStateStack.pop()).booleanValue() )
          {
            if (!inStatementIndentStackSizeStack.isEmpty())
            {
              int previousIndentStackSize = ((Integer) inStatementIndentStackSizeStack.pop()).intValue();
              while (previousIndentStackSize < inStatementIndentStack.size())
                inStatementIndentStack.pop();
              parenDepth--;
              if (i == 0)
                shouldIndentBrackettedLine = false;

              if (!parenIndentStack.isEmpty())
              {
                Object poppedIndent = parenIndentStack.pop();
                if (i == 0)
                  spaceTabCount = ((Integer) poppedIndent).intValue();
              }
            }
            continue;
          }


          if(!inStatementIndentStackSizeStack.isEmpty())
            inStatementIndentStackSizeStack.pop();

          if (!blockParenDepthStack.isEmpty())
          {
            parenDepth = ((Integer) blockParenDepthStack.pop()).intValue();
            isInStatement = ((Boolean) blockStatementStack.pop()).booleanValue();

            if (isInStatement)
              blockTabCount--;
          }

          closingBracketReached = true;
          int headerPlace = headerStack.search("{");
          if (headerPlace != -1)
          {
            while (!"{".equals(headerStack.pop()))
              ;
            if (!tempStacks.isEmpty())
              tempStacks.pop();
          }

          ch = ' '; // needed due to cases such as '}else{', so that headers ('else' tn tih case) will be identified...

        }

        //else if (ch == ';' /* parenDepth == 0*/)
        //    while (((Integer) inStatementIndentStackSizeStack.peek()).intValue() < inStatementIndentStack.size())
        //	inStatementIndentStack.pop();

        /*
         * Create a temporary snapshot of the current block's header-list in the
         * uppermost inner stack in tempStacks, and clear the headerStack up to
         * the begining of the block.
         * Thus, the next future statement will think it comes one indent past
         * the block's '{' unless it specifically checks for a companion-header
         * (such as a previous 'if' for an 'else' header) within the tempStacks,
         * and recreates the temporary snapshot by manipulating the tempStacks.
         */
        if (!((Stack) tempStacks.peek()).isEmpty())
          ((Stack) tempStacks.peek()).removeAllElements();
        while (!headerStack.isEmpty() && !"{".equals(headerStack.peek()))
          ((Stack) tempStacks.peek()).push(headerStack.pop());

        if (parenDepth == 0 && ch == ';')
          isInStatement=false;

        continue;
      }

      if (prevCh == ' ')
      {
        int headerNum = findLegalHeader(line, i, preBlockStatements);
        if (headerNum > -1)
        {
          isInClassHeader = true;
          outBuffer.append(preBlockStatements[headerNum].substring(1));
          i += preBlockStatements[headerNum].length() - 1;
        }
      }

      // PRECHECK if a '==' or '--' or '++' operator was reached.
      // If not, then register an indent IF an assignment operator was reached.
      // The precheck is important, so that statements such as 'i--==2' are not recognized
      // to have assignment operators (here, '-=') in them . . .
      immediatelyPreviousAssignmentOp = null;
      boolean isNonAssingmentOperator = false;
      for (int n = 0; n < nonAssignmentOperators.length; n++)
        if (line.regionMatches(false, i, nonAssignmentOperators[n], 0, nonAssignmentOperators[n].length()))
        {
          outBuffer.append(nonAssignmentOperators[n].substring(1));
          i++;
          /*  the above two lines do the same as the next since all listed  non Assignment operators are 2 chars long...
          if (nonAssignmentOperators[n].length() > 1)
        {
              outBuffer.append(nonAssignmentOperators[n].substring(1));
              i += nonAssignmentOperators[n].length() - 1;
        }
          */
          isNonAssingmentOperator = true;
          break;
        }
      if (!isNonAssingmentOperator)
      {
        for (int a = 0; a < assignmentOperators.length; a++)
          if (line.regionMatches(false, i, assignmentOperators[a], 0, assignmentOperators[a].length()))
          {
            if (assignmentOperators[a].length() > 1)
            {
              outBuffer.append(assignmentOperators[a].substring(1));
              i += assignmentOperators[a].length() - 1;
            }
            registerInStatementIndent(line, i, spaceTabCount, isLineInStatement, false);
            immediatelyPreviousAssignmentOp = assignmentOperators[a];
            break;
          }
      }



      if ( parenDepth > 0 || !(isLegalNameChar(ch) || ch == ':'))
        isInStatement = true;

    }


    // handle special cases of unindentation:

    /*
     * if '{' doesn't follow an immediately previous '{' in the headerStack
     * (but rather another header such as "for" or "if", then unindent it
     * by one indentation relative to its block.
     */
    if (outBuffer.length()>0 && outBuffer.charAt(0)=='{'
        &&  !(headerStack.size()>1 && "{".equals(headerStack.elementAt(headerStack.size()-2)))
        && shouldIndentBrackettedLine)
      tabCount--;

    else if (outBuffer.length()>0 && outBuffer.charAt(0)=='}' && shouldIndentBrackettedLine )
      tabCount--;

    if (tabCount < 0)
      tabCount = 0;

    // take care of extra bracket indentatation option...
    if (bracketIndent && outBuffer.length()>0 && shouldIndentBrackettedLine)
      if (outBuffer.charAt(0)=='{' || outBuffer.charAt(0)=='}')
        tabCount++;

    // finally, insert indentations into begining of line
    for (int i=0; i<tabCount; i++)
      outBuffer.insert(0, indentString);

    while ((spaceTabCount--) > 0)
      outBuffer.insert(0, ' ');

    if (!inStatementIndentStack.isEmpty())
    {
      if (statementTabCount < 0)
        statementTabCount = tabCount;
    }
    else
      statementTabCount = -1;

    return outBuffer.toString();
  }

  private void registerInStatementIndent(String line, int i, int spaceTabCount, boolean isLineInStatement, boolean updateParenStack)
  {
    int inStatementIndent;
    int remainingCharNum = line.length() - i;
    int nextNonWSChar = 1;

    /*
    while (nextNonWSChar < remainingCharNum
           && (line.charAt(i+nextNonWSChar) == ' ' ||
    	   line.charAt(i+nextNonWSChar) == '\t') )
        nextNonWSChar++;
    */
    nextNonWSChar = getNextProgramCharDistance(line, i);

    // if indent is around the last char in the line, indent instead 2 spaces from the previous indent
    if (nextNonWSChar == remainingCharNum)
    {
      int previousIndent = spaceTabCount;
      if (!inStatementIndentStack.isEmpty())
        previousIndent = ((Integer) inStatementIndentStack.peek()).intValue();

      inStatementIndentStack.push(new Integer(2 /*indentLength*/ + previousIndent) ); //2
      if (updateParenStack)
        parenIndentStack.push( new Integer(previousIndent) );
      return;
    }

    if (updateParenStack)
      parenIndentStack.push(new Integer(i+spaceTabCount));

    inStatementIndent = i + nextNonWSChar + spaceTabCount;

    if (i + nextNonWSChar > maxInStatementIndent)
      inStatementIndent =  indentLength*2 + spaceTabCount;

    if (!inStatementIndentStack.isEmpty() &&
        inStatementIndent < ((Integer) inStatementIndentStack.peek()).intValue())
      inStatementIndent = ((Integer) inStatementIndentStack.peek()).intValue();

    //else if (!isLineInStatement && i + nextNonWSChar < 8)
    //    inStatementIndent =  8 + spaceTabCount;

    inStatementIndentStack.push(new Integer(inStatementIndent));
  }

  // get distance to the next non-white sspace, non-comment character in the line.
  // if no such character exists, return the length remaining to the end of the line.
  private int getNextProgramCharDistance(String line, int i)
  {
    int inStatementIndent;
    boolean inComment = false;
    int remainingCharNum = line.length() - i;
    int charDistance = 1;
    int ch;

    for (charDistance = 1; charDistance < remainingCharNum; charDistance++)
    {
      ch = line.charAt(i + charDistance);
      if (inComment)
      {
        if (line.regionMatches(false, i + charDistance, "*/", 0, 2))
        {
          charDistance++;
          inComment = false;
        }
        continue;
      }
      else if (ch  == ' ' || ch == '\t')
        continue;
      else if (ch == '/')
      {
        if ((line.regionMatches(false, i + charDistance, "//", 0, 2)))
          return remainingCharNum;
        else if ((line.regionMatches(false, i + charDistance, "/*", 0, 2)))
        {
          charDistance++;
          inComment = true;
        }
      }
      else
        return charDistance;
    }

    return charDistance;
  }

  private boolean isLegalNameChar(char ch)
  {
    return ((ch>='a' && ch<='z') || (ch>='A' && ch<='Z') || (ch>='0' && ch<='9') ||
            ch=='.' || ch=='_' || ch=='$');
  }

  private int findLegalHeader(String line, int i, String possibleHeaders[])
  {
    int maxHeaders = possibleHeaders.length;
    int p;

    for (p=0; p < maxHeaders; p++)
      if (line.regionMatches(false, i, possibleHeaders[p], 0, possibleHeaders[p].length()))
      {
        // first check that this is a header and not the begining of a longer word...
        int lineLength = line.length();
        int headerEnd = i + possibleHeaders[p].length();
        char endCh = 0;

        if ( headerEnd < lineLength )
          endCh = line.charAt(headerEnd);
        if (headerEnd >= lineLength || !isLegalNameChar(endCh))
          return p;
        else
          return -1;
      }

    return -1;
  }

}
