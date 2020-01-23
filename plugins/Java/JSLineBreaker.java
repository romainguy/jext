/*
 * Copyright (c) 1998 Tal Davidson. All rights reserved.
 *
 * JSLineBreaker 0.4.0
 * by Tal Davidson (davidsont@bigfoot.com)
 *
 * JSLineBreaker 0.4.0 is distributed under the "Artistic Licence" detailed below:
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

class JSLineBreaker
{
    private static final int BEFORE = 0;
    private static final int AFTER = 1;

    private String prefs[] = {
        "().", "()", ").", "+=", "-=", "*=", "/=", "%=", "^=", "||", "&&", "==", "!=", ">=", "<=",
        "(", ")", "[", "]", "?", ":", ",", ";", "=", "<", ">", "+", "-", "*", "/", "&", "|", /*"!",*/ "^"
    };
    private static Hashtable prefTable;
    static
    {
        prefTable = new Hashtable();
        prefTable.put("()", new Integer(80/*35*/)); // () appears here so that it will not be cut in the middle...

        prefTable.put("().", new Integer(90)); // () appears here so that it will not be cut in the middle...
        prefTable.put(").", new Integer(90)); // () appears here so that it will not be cut in the middle...

        prefTable.put("(", new Integer(80));//<--
        prefTable.put(")", new Integer(80));
        prefTable.put("[", new Integer(80));
        prefTable.put("]", new Integer(80));
        prefTable.put(",", new Integer(10));
        prefTable.put(";", new Integer(5));


        prefTable.put("=", new Integer(20));
        prefTable.put("+=", new Integer(20));
        prefTable.put("-=", new Integer(20));
        prefTable.put("*=", new Integer(20));
        prefTable.put("/=", new Integer(20));
        prefTable.put("|=", new Integer(20));
        prefTable.put("&=", new Integer(20));
        prefTable.put("^=", new Integer(20));

        prefTable.put("?", new Integer(25));
        prefTable.put(":", new Integer(25));

        prefTable.put("||", new Integer(30));
        prefTable.put("&&", new Integer(30));

        prefTable.put("==", new Integer(40));
        prefTable.put("!=", new Integer(40));
        prefTable.put(">=", new Integer(40));
        prefTable.put("<=", new Integer(40));
        prefTable.put(">", new Integer(40));
        prefTable.put("<", new Integer(40));

        prefTable.put("+", new Integer(50));
        prefTable.put("-", new Integer(50));
        prefTable.put("*", new Integer(60));
        prefTable.put("/", new Integer(60));
        prefTable.put("%", new Integer(60));
        //prefTable.put("!", new Integer(70));
        prefTable.put("&", new Integer(70));
        prefTable.put("|", new Integer(70));
        prefTable.put("^", new Integer(70));
    }


    private Vector brokenLineVector;
    private StringBuffer wsBuffer;
    private char quoteChar;
    private boolean isInQuote;
    private boolean isInComment;
    private boolean isNestedConnection = true;
    private boolean isCut;
    private boolean isLineComment; // true when the current character is in a // comment (such as this line ...)
    private int parenDepth;
    private int breakDepth;
    private int preferredLineLength = 70;
    private int lineLengthDeviation = 5;




    JSLineBreaker()
    {
        init();
    }

    void init()
    {
        brokenLineVector = new Vector();

        parenDepth = 0;
        breakDepth = 0; // <------ 2

        isInQuote = false;
        isInComment = false;
        isCut = false;
        isLineComment = false;
        wsBuffer = new StringBuffer();

    }

    void setPreferredLineLength(int length)
    {
        preferredLineLength = length;
    }

    void setLineLengthDeviation(int dev)
    {
        lineLengthDeviation = dev;
    }

    void setNestedConnection(boolean nest)
    {
        isNestedConnection = nest;
    }

    void breakLine(String line)
    {
        StringBuffer outBuffer = new StringBuffer();
        Stack lineBreakStack = new Stack();
        String previousAfterCut = "";
         boolean isSpecialChar = false;
        char ch = ' ';       // the current character
        char prevCh = 0;
        int i;
        int ws;
        int regBreak = 0;
        int wsBreak = 0;
        int chosenBreak = 0;
        int BufLength;
        int bufferStart = 0;

//System.out.println("line length: " + line.length() + " line: " + line );
        if ( line.trim().length() == 0)
        {
            brokenLineVector.addElement("");
            return;
        }

        ch = line.charAt(0);
        ws = 0;

        if (!isLineComment)
            isCut = false;
        isLineComment = false;

        if (!isCut)
        {
        wsBuffer = new StringBuffer();

        while ( (ch == ' ' || ch == '\t') && ws<line.length()-1)
        {
//System.out.println(ch + " " + ws);
            wsBuffer.append(ch);
            ch = line.charAt( ++ws );
        }
        }

//System.out.println(ws);
        // parse characters in the current line.
        for (i=ws; i<line.length(); i++)
        {
            if (ch != ' ' && ch != '\t')
                prevCh = ch;
            ch = line.charAt(i);

            // handle special characters (i.e. backslash+character such as \n, \t, ...)
            if (isSpecialChar)
            {
                outBuffer.append(ch);
                isSpecialChar = false;
                continue;
            }
            if (!(isInComment || isLineComment) && line.regionMatches(false, i, "\\\\", 0, 2))
            {
                outBuffer.append("\\\\");
                i++;
                continue;
            }
            if (!(isInComment || isLineComment) && ch=='\\')
            {
                outBuffer.append(ch);
                isSpecialChar = true;
                continue;
            }


            // handle comments
            if ( !isInQuote && !(isInComment || isLineComment) && line.regionMatches(false, i, "//", 0, 2) )
            {
                isLineComment = true;
                outBuffer.append("//");
                i++;
                continue;
            }
            else if ( !isInQuote && !(isInComment || isLineComment) && line.regionMatches(false, i, "/*", 0, 2) )
            {
                isInComment = true;
                outBuffer.append("/*");
                i++;
                continue;
            }
            else if ( !isInQuote && (isInComment || isLineComment) && line.regionMatches(false, i, "*/", 0, 2) )
            {
                isInComment = false;
                outBuffer.append("*/");
                i++;
                continue;
            }
            if (isInComment || isLineComment)
            {
                outBuffer.append(ch);
                continue;
            }

            // handle quotes (such as 'x' and "Hello Dolly")
            if (ch=='"' || ch=='\'')
                if (!isInQuote)
                {
                    quoteChar = ch;
                    isInQuote = true;
                }
                else if (quoteChar == ch)
                {
                    isInQuote = false;
                    outBuffer.append(ch);
                    continue;
                }
            if (isInQuote)
            {
                outBuffer.append(ch);
                continue;
            }

            outBuffer.append(ch);

            for (int p=0; p<prefs.length; p++)
            {
                String key = (String) prefs[p];
                if (line.regionMatches(false, i, key, 0, key.length()))
                {
                    int breakType = AFTER;
                    if (ch == '(' || ch == '[' || ch == ')' || ch == ']')
                    {
                        if ("(".equals(key) /*ch == '('*/ || ch == '[')
                            parenDepth++;
                        else if (/*")".equals(key)*/ ch == ')' || ch == ']')
                            parenDepth--;

                        breakDepth = parenDepth;
                        if (ch == ')' || ch == ']' || key.startsWith("()"))
                            breakDepth++;

                        if (ch == '(' || ch == '[')
                            if( (prevCh>='a' &&prevCh<='z')
                                    || (prevCh>='A' &&prevCh<='Z')
                                    || (prevCh>='0' &&prevCh<='9')
                                    || (prevCh=='.'))
                                breakType = AFTER;
                            else
                                breakType = BEFORE;
                        else
                            breakType = AFTER;
                    }

                    if (key.length() > 1)
                    {
                        outBuffer.append(key.substring(1));
                        i += key.length() - 1;
                    }
                    registerLineBreak(lineBreakStack,
                            new LineBreak(key, outBuffer.length()+bufferStart, breakDepth, breakType));

                    breakDepth = parenDepth;

                    break;
                }
            }


            int bufLength = outBuffer.length() + wsBuffer.length() + previousAfterCut.length() + (isCut ? 8 : 0);

            LineBreak curBreak = null;
            if (bufLength > preferredLineLength && i < line.length() - lineLengthDeviation)
            {
                while (!lineBreakStack.isEmpty())
                {
                    curBreak = (LineBreak) lineBreakStack.elementAt(0);
                    if (curBreak.breakWhere-bufferStart < 1) // <-----
                    {
                        curBreak = null;
                        lineBreakStack.removeElementAt(0);
                    }
                    else
                        break;
                }
                if (curBreak != null)
                    lineBreakStack.removeElementAt(0);
            }

            if (curBreak != null) // in future, think of: && line.length()>i+10) (that was used in the past...)
            {
                int cutWhere = curBreak.breakWhere - bufferStart - (curBreak.breakType == BEFORE ? curBreak.breakStr.length() : 0);
                if (cutWhere < 8)
                    continue;
                StringBuffer brokenLineBuffer = new StringBuffer();
                String outString = outBuffer.toString();
                String beforeCut = outString.substring(0, cutWhere);

                //brokenLineBuffer.append(wsBuffer);

                /*
                if (isCut)
                    brokenLineBuffer.append("        ");
                brokenLineBuffer.append(beforeCut);
                brokenLineVector.addElement(brokenLineBuffer.toString());
                */
                brokenLineBuffer.append(beforeCut);
                addBrokenLine(wsBuffer.toString(), brokenLineBuffer.toString(), curBreak, breakDepth, isCut);

                //previousAfterCut = outString.substring(cutWhere);
                bufferStart += cutWhere;
                outBuffer = new StringBuffer(outString.substring(cutWhere));

                //lineBreakStack = new Stack();
                isCut = true;
            }
        }

        // at end of line:
        StringBuffer brokenLineBuffer = new StringBuffer();
        //brokenLineBuffer.append(wsBuffer);
        /*
        if (isCut)
            brokenLineBuffer.append("        ");
        brokenLineBuffer.append(outBuffer);
        brokenLineVector.addElement(brokenLineBuffer.toString());
        */
        brokenLineBuffer.append(outBuffer);
        addBrokenLine(wsBuffer.toString(), brokenLineBuffer.toString(), null, breakDepth, isCut);

    }

    private void registerLineBreak(Stack lineBreakStack, LineBreak newBreak)
    {
        LineBreak lastBreak;
        while (!lineBreakStack.isEmpty())
        {
            lastBreak = (LineBreak) lineBreakStack.peek();
            if (compare(lastBreak, newBreak) < 0)
                lineBreakStack.pop();
            else
                break;
        }
        lineBreakStack.push(newBreak);
        //newBreak.dump();

 /*       System.out.println("for " + newBreak.breakStr);
        for (int s=0; s<lineBreakStack.size(); s++)
            ((LineBreak) lineBreakStack.elementAt(s)).dump();
        System.out.println();
  */
    }

    private LineBreak previousLineBreak = null;

    private void addBrokenLine(String whiteSpace, String brokenLine, LineBreak lineBreak, int breakDepth, boolean isCut)
    {
        boolean isLineAppended = false;

        brokenLine = brokenLine.trim();

        if (previousLineBreak != null) //&& brokenLine.length() > 0)
        {
            String previousBrokenLine = (String) brokenLineVector.lastElement();
            if (brokenLine.length() + previousBrokenLine.length() <= preferredLineLength + lineLengthDeviation
                    || brokenLine.startsWith("{"))
            {
                if (lineBreak == null ||
                        (isNestedConnection && !",".equals(previousLineBreak.breakStr)) ||
                        (lineBreak.breakDepth < previousLineBreak.breakDepth) || //dfds
                        (lineBreak.breakDepth == previousLineBreak.breakDepth &&
                        ((!isNestedConnection && !",".equals(previousLineBreak.breakStr)) ||
                             ",".equals(lineBreak.breakStr) || ";".equals(lineBreak.breakStr) ||
                             ")".equals(lineBreak.breakStr) || "]".equals(lineBreak.breakStr)))
                   )
                {
                    brokenLineVector.setElementAt( (previousBrokenLine + " " + brokenLine),
                            brokenLineVector.size()-1);
                    isLineAppended = true;
                }
            }
        }

        if (!isLineAppended)
        {
            if (isCut &&
                    !(previousLineBreak != null && ",".equals(previousLineBreak.breakStr) &&
                    previousLineBreak.breakDepth == 0))
                brokenLine = "        " + brokenLine;
            brokenLine = whiteSpace + brokenLine;
            brokenLineVector.addElement(brokenLine);
        }

        previousLineBreak = lineBreak;
    }

    private int compare(LineBreak br1, LineBreak br2)
    {
        if (br1.breakDepth < br2.breakDepth)
                return 1;
            else if (br1.breakDepth > br2.breakDepth)
                return -1;

        int ord1 = ((Integer) prefTable.get(br1.breakStr)).intValue();
        int ord2 = ((Integer) prefTable.get(br2.breakStr)).intValue();
        if (ord1 < ord2)
            return 1;
        else
            return -1;


    }

    boolean hasMoreBrokenLines()
    {
        return brokenLineVector.size() > 0;
    }

    String nextBrokenLine()
    {
        String nextLine;

        if (hasMoreBrokenLines())
        {
            nextLine = (String) brokenLineVector.firstElement();
            brokenLineVector.removeElementAt(0);
        }
        else
            return nextLine = "";

        return nextLine;
    }


    class LineBreak
    {
        String breakStr;
        int breakWhere;
        int breakDepth;
        int breakType;

        LineBreak(String str, int wh, int dp, int tp)
        {
            breakStr = str;
            breakWhere = wh;
            breakDepth = dp;
            breakType = tp;
        }

        void dump()
        {
            System.out.println("LB: str="+breakStr+" wh="+breakWhere+" dep="+breakDepth+" tp="+breakType);
        }
    }

}
