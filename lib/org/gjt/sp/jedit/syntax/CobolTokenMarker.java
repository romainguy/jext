/*
 * CobolTokenMarker.java - Dawn token marker
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.gjt.sp.jedit.syntax;

import javax.swing.text.Segment;

public class CobolTokenMarker extends TokenMarker
{
  // private members
  private static KeywordMap cobolKeywords;
  private KeywordMap keywords;

  private int lastOffset;
  private int lastKeyword;

  public CobolTokenMarker()
  {
    this.keywords = getKeywords();
  }

  public byte markTokensImpl(byte token, Segment line, int lineIndex)
  {
    char[] array = line.array;
    int offset = line.offset;
    lastOffset = offset;
    lastKeyword = offset;
    int length = line.count + offset;

loop: for(int i = offset; i < length; i++)
    {
      int i1 = (i+1);

      char c = array[i];

      switch(token)
      {
        case Token.NULL:
          switch(c)
          {
            case 'E':
              if (SyntaxUtilities.regionMatches(false, line, i1, "XEC SQL"))
              {
                doKeyword(line, i, c);
                i += 7;
                token = Token.KEYWORD3;
              }
              break;
            case '*':
              addToken(i - lastOffset, token);
              addToken(length - i, Token.COMMENT1);
              token = Token.NULL;
              lastOffset = lastKeyword = length;
              break loop;
            case '"':
              doKeyword(line, i, c);
              addToken(i - lastOffset,token);
              token = Token.LITERAL1;
              lastOffset = lastKeyword = i;
              break;
            case '\'':
              doKeyword(line, i, c);
              addToken(i - lastOffset,token);
              token = Token.LITERAL2;
              lastOffset = lastKeyword = i;
              break;
            case ' ':
              doKeyword(line, i, c);
          }
          break;
        case Token.KEYWORD3:
          if (c == 'E')
          {
            if (SyntaxUtilities.regionMatches(false, line, i1, "ND-EXEC"))
            {
              i += 8;
              addToken(i - lastOffset, token);
              token = Token.NULL;
              lastOffset = lastKeyword = i;
            }
          }
          break;
        case Token.LITERAL1:
          if(c == '"')
          {
            addToken(i1 - lastOffset, token);
            token = Token.NULL;
            lastOffset = lastKeyword = i1;
          }
          break;
        case Token.LITERAL2:
          if(c == '\'')
          {
            addToken(i1 - lastOffset, Token.LITERAL1);
            token = Token.NULL;
            lastOffset = lastKeyword = i1;
          }
          break;
        default:
          throw new InternalError("Invalid state: " + token);
      }
    }

    if (token == Token.NULL)
      doKeyword(line, length, '\0');

    switch(token)
    {
//      case Token.LITERAL1:
//      case Token.LITERAL2:
//        addToken(length - lastOffset, Token.INVALID);
//        token = Token.NULL;
//        break;
      //case Token.KEYWORD2:
      //  addToken(length - lastOffset, token);
      //  token = Token.NULL;
      default:
        addToken(length - lastOffset, token);
        break;
    }

    return token;
  }

  public static KeywordMap getKeywords()
  {
    if (cobolKeywords == null)
    {
      cobolKeywords = new KeywordMap(false);
      cobolKeywords.add("ACCEPT", Token.KEYWORD1);
      cobolKeywords.add("ACCESS", Token.KEYWORD1);
      cobolKeywords.add("ACTUAL", Token.KEYWORD1);
      cobolKeywords.add("ADD", Token.KEYWORD1);
      cobolKeywords.add("ADDRESS", Token.KEYWORD1);
      cobolKeywords.add("ADVANCING", Token.KEYWORD1);
      cobolKeywords.add("AFTER", Token.KEYWORD1);
      cobolKeywords.add("ALL", Token.KEYWORD1);
      cobolKeywords.add("ALPHABET", Token.KEYWORD1);
      cobolKeywords.add("ALPHABETIC", Token.KEYWORD1);
      cobolKeywords.add("ALPHABETIC-LOWER", Token.KEYWORD1);
      cobolKeywords.add("ALPHABETIC-UPPER", Token.KEYWORD1);
      cobolKeywords.add("ALPHANUMERIC", Token.KEYWORD1);
      cobolKeywords.add("ALPHANUMERIC-EDITED", Token.KEYWORD1);
      cobolKeywords.add("ALSO", Token.KEYWORD1);
      cobolKeywords.add("ALTER", Token.KEYWORD1);
      cobolKeywords.add("ALTERNATE", Token.KEYWORD1);
      cobolKeywords.add("AND", Token.KEYWORD1);
      cobolKeywords.add("ANY", Token.KEYWORD1);
      cobolKeywords.add("API", Token.KEYWORD1);
      cobolKeywords.add("APPLY", Token.KEYWORD1);
      cobolKeywords.add("ARE", Token.KEYWORD1);
      cobolKeywords.add("AREA", Token.KEYWORD1);
      cobolKeywords.add("AREAS", Token.KEYWORD1);
      cobolKeywords.add("ASCENDING", Token.KEYWORD1);
      cobolKeywords.add("ASSIGN", Token.KEYWORD1);
      cobolKeywords.add("AT", Token.KEYWORD1);
      cobolKeywords.add("AUTHOR", Token.KEYWORD1);
      cobolKeywords.add("AUTO", Token.KEYWORD1);
      cobolKeywords.add("AUTO-SKIP", Token.KEYWORD1);
      cobolKeywords.add("AUTOMATIC", Token.KEYWORD1);

      cobolKeywords.add("BACKGROUND-COLOR", Token.KEYWORD1);
      cobolKeywords.add("BACKGROUND-COLOUR", Token.KEYWORD1);
      cobolKeywords.add("BACKWARD", Token.KEYWORD1);
      cobolKeywords.add("BASIS", Token.KEYWORD1);
      cobolKeywords.add("BEEP", Token.KEYWORD1);
      cobolKeywords.add("BEFORE", Token.KEYWORD1);
      cobolKeywords.add("BEGINNING", Token.KEYWORD1);
      cobolKeywords.add("BELL", Token.KEYWORD1);
      cobolKeywords.add("BINARY", Token.KEYWORD1);
      cobolKeywords.add("BLANK", Token.KEYWORD1);
      cobolKeywords.add("BLINK", Token.KEYWORD1);
      cobolKeywords.add("BLOCK", Token.KEYWORD1);
      cobolKeywords.add("BOTTOM", Token.KEYWORD1);
      cobolKeywords.add("BY", Token.KEYWORD1);

      cobolKeywords.add("C01", Token.KEYWORD1);
      cobolKeywords.add("C02", Token.KEYWORD1);
      cobolKeywords.add("C03", Token.KEYWORD1);
      cobolKeywords.add("C04", Token.KEYWORD1);
      cobolKeywords.add("C05", Token.KEYWORD1);
      cobolKeywords.add("C06", Token.KEYWORD1);
      cobolKeywords.add("C07", Token.KEYWORD1);
      cobolKeywords.add("C08", Token.KEYWORD1);
      cobolKeywords.add("C09", Token.KEYWORD1);
      cobolKeywords.add("C10", Token.KEYWORD1);
      cobolKeywords.add("C11", Token.KEYWORD1);
      cobolKeywords.add("C12", Token.KEYWORD1);
      cobolKeywords.add("CALL", Token.KEYWORD1);
      cobolKeywords.add("CALL-CONVENTION", Token.KEYWORD1);
      cobolKeywords.add("CANCEL", Token.KEYWORD1);
      cobolKeywords.add("CBL", Token.KEYWORD1);
      cobolKeywords.add("CD", Token.KEYWORD1);
      cobolKeywords.add("CF", Token.KEYWORD1);
      cobolKeywords.add("CH", Token.KEYWORD1);
      cobolKeywords.add("CHAIN", Token.KEYWORD1);
      cobolKeywords.add("CHAINING", Token.KEYWORD1);
      cobolKeywords.add("CHANGED", Token.KEYWORD1);
      cobolKeywords.add("CHARACTER", Token.KEYWORD1);
      cobolKeywords.add("CHARACTERS", Token.KEYWORD1);
      cobolKeywords.add("CLASS", Token.KEYWORD1);
      cobolKeywords.add("CLOCK-UNITS", Token.KEYWORD1);
      cobolKeywords.add("CLOSE", Token.KEYWORD1);
      cobolKeywords.add("COBOL", Token.KEYWORD1);
      cobolKeywords.add("CODE", Token.KEYWORD1);
      cobolKeywords.add("CODE-SET", Token.KEYWORD1);
      cobolKeywords.add("COL", Token.KEYWORD1);
      cobolKeywords.add("COLLATING", Token.KEYWORD1);
      cobolKeywords.add("COLUMN", Token.KEYWORD1);
      cobolKeywords.add("COM-REG", Token.KEYWORD1);
      cobolKeywords.add("COMMA", Token.KEYWORD1);
      cobolKeywords.add("COMMIT", Token.KEYWORD1);
      cobolKeywords.add("COMMON", Token.KEYWORD1);
      cobolKeywords.add("COMMUNICATION", Token.KEYWORD1);
      cobolKeywords.add("COMP", Token.KEYWORD1);
      cobolKeywords.add("COMP-0", Token.KEYWORD1);
      cobolKeywords.add("COMP-1", Token.KEYWORD1);
      cobolKeywords.add("COMP-2", Token.KEYWORD1);
      cobolKeywords.add("COMP-3", Token.KEYWORD1);
      cobolKeywords.add("COMP-4", Token.KEYWORD1);
      cobolKeywords.add("COMP-5", Token.KEYWORD1);
      cobolKeywords.add("COMP-6", Token.KEYWORD1);
      cobolKeywords.add("COMP-X", Token.KEYWORD1);
      cobolKeywords.add("COMPUTATIONAL", Token.KEYWORD1);
      cobolKeywords.add("COMPUTATIONAL-0", Token.KEYWORD1);
      cobolKeywords.add("COMPUTATIONAL-1", Token.KEYWORD1);
      cobolKeywords.add("COMPUTATIONAL-2", Token.KEYWORD1);
      cobolKeywords.add("COMPUTATIONAL-3", Token.KEYWORD1);
      cobolKeywords.add("COMPUTATIONAL-4", Token.KEYWORD1);
      cobolKeywords.add("COMPUTATIONAL-5", Token.KEYWORD1);
      cobolKeywords.add("COMPUTATIONAL-6", Token.KEYWORD1);
      cobolKeywords.add("COMPUTATIONAL-X", Token.KEYWORD1);
      cobolKeywords.add("COMPUTE", Token.KEYWORD1);
      cobolKeywords.add("CONFIGURATION", Token.KEYWORD1);
      cobolKeywords.add("CONSOLE", Token.KEYWORD1);
      cobolKeywords.add("CONTAINS", Token.KEYWORD1);
      cobolKeywords.add("CONTENT", Token.KEYWORD1);
      cobolKeywords.add("CONTINUE", Token.KEYWORD1);
      cobolKeywords.add("CONTROL", Token.KEYWORD1);
      cobolKeywords.add("CONTROLS", Token.KEYWORD1);
      cobolKeywords.add("CONVERTING", Token.KEYWORD1);
      cobolKeywords.add("COPY", Token.KEYWORD1);
      cobolKeywords.add("CORE-INDEX", Token.KEYWORD1);
      cobolKeywords.add("CORR", Token.KEYWORD1);
      cobolKeywords.add("CORRESPONDING", Token.KEYWORD1);
      cobolKeywords.add("COUNT", Token.KEYWORD1);
      cobolKeywords.add("CRT", Token.KEYWORD1);
      cobolKeywords.add("CRT-UNDER", Token.KEYWORD1);
      cobolKeywords.add("CURRENCY", Token.KEYWORD1);
      cobolKeywords.add("CURRENT-DATE", Token.KEYWORD1);
      cobolKeywords.add("CURSOR", Token.KEYWORD1);
      cobolKeywords.add("CYCLE", Token.KEYWORD1);
      cobolKeywords.add("CYL-INDEX", Token.KEYWORD1);
      cobolKeywords.add("CYL-OVERFLOW", Token.KEYWORD1);

      cobolKeywords.add("DATA", Token.KEYWORD1);
      cobolKeywords.add("DATE", Token.KEYWORD1);
      cobolKeywords.add("DATE-COMPILED", Token.KEYWORD1);
      cobolKeywords.add("DATE-WRITTEN", Token.KEYWORD1);
      cobolKeywords.add("DAY", Token.KEYWORD1);
      cobolKeywords.add("DAY-OF-WEEK", Token.KEYWORD1);
      cobolKeywords.add("DBCS", Token.KEYWORD1);
      cobolKeywords.add("DE", Token.KEYWORD1);
      cobolKeywords.add("DEBUG", Token.KEYWORD1);
      cobolKeywords.add("DEBUG-CONTENTS", Token.KEYWORD1);
      cobolKeywords.add("DEBUG-ITEM", Token.KEYWORD1);
      cobolKeywords.add("DEBUG-LINE", Token.KEYWORD1);
      cobolKeywords.add("DEBUG-NAME", Token.KEYWORD1);
      cobolKeywords.add("DEBUG-SUB-1", Token.KEYWORD1);
      cobolKeywords.add("DEBUG-SUB-2", Token.KEYWORD1);
      cobolKeywords.add("DEBUG-SUB-3", Token.KEYWORD1);
      cobolKeywords.add("DEBUGGING", Token.KEYWORD1);
      cobolKeywords.add("DECIMAL-POINT", Token.KEYWORD1);
      cobolKeywords.add("DECLARATIVES", Token.KEYWORD1);
      cobolKeywords.add("DELETE", Token.KEYWORD1);
      cobolKeywords.add("DELIMITED", Token.KEYWORD1);
      cobolKeywords.add("DELIMITER", Token.KEYWORD1);
      cobolKeywords.add("DEPENDING", Token.KEYWORD1);
      cobolKeywords.add("DESCENDING", Token.KEYWORD1);
      cobolKeywords.add("DESTINATION", Token.KEYWORD1);
      cobolKeywords.add("DETAIL", Token.KEYWORD1);
      cobolKeywords.add("DISABLE", Token.KEYWORD1);
      cobolKeywords.add("DISK", Token.KEYWORD1);
      cobolKeywords.add("DISP", Token.KEYWORD1);
      cobolKeywords.add("DISPLAY", Token.KEYWORD1);
      cobolKeywords.add("DISPLAY-1", Token.KEYWORD1);
      cobolKeywords.add("DISPLAY-ST", Token.KEYWORD1);
      cobolKeywords.add("DIVIDE", Token.KEYWORD1);
      cobolKeywords.add("DIVISION", Token.KEYWORD1);
      cobolKeywords.add("DOWN", Token.KEYWORD1);
      cobolKeywords.add("DUPLICATES", Token.KEYWORD1);
      cobolKeywords.add("DYNAMIC", Token.KEYWORD1);

      cobolKeywords.add("ECHO", Token.KEYWORD1);
      cobolKeywords.add("EGCS", Token.KEYWORD1);
      cobolKeywords.add("EGI", Token.KEYWORD1);
      cobolKeywords.add("EJECT", Token.KEYWORD1);
      cobolKeywords.add("ELSE", Token.KEYWORD1);
      cobolKeywords.add("EMI", Token.KEYWORD1);
      cobolKeywords.add("EMPTY-CHECK", Token.KEYWORD1);
      cobolKeywords.add("ENABLE", Token.KEYWORD1);
      cobolKeywords.add("END", Token.KEYWORD1);
      cobolKeywords.add("END-ACCEPT", Token.KEYWORD1);
      cobolKeywords.add("END-ADD", Token.KEYWORD1);
      cobolKeywords.add("END-CALL", Token.KEYWORD1);
      cobolKeywords.add("END-CHAIN", Token.KEYWORD1);
      cobolKeywords.add("END-COMPUTE", Token.KEYWORD1);
      cobolKeywords.add("END-DELETE", Token.KEYWORD1);
      cobolKeywords.add("END-DISPLAY", Token.KEYWORD1);
      cobolKeywords.add("END-DIVIDE", Token.KEYWORD1);
      cobolKeywords.add("END-EVALUATE", Token.KEYWORD1);
      cobolKeywords.add("END-IF", Token.KEYWORD1);
      cobolKeywords.add("END-INVOKE", Token.KEYWORD1);
      cobolKeywords.add("END-MULTIPLY", Token.KEYWORD1);
      cobolKeywords.add("END-OF-PAGE", Token.KEYWORD1);
      cobolKeywords.add("END-PERFORM", Token.KEYWORD1);
      cobolKeywords.add("END-READ", Token.KEYWORD1);
      cobolKeywords.add("END-RECEIVE", Token.KEYWORD1);
      cobolKeywords.add("END-RETURN", Token.KEYWORD1);
      cobolKeywords.add("END-REWRITE", Token.KEYWORD1);
      cobolKeywords.add("END-SEARCH", Token.KEYWORD1);
      cobolKeywords.add("END-START", Token.KEYWORD1);
      cobolKeywords.add("END-STRING", Token.KEYWORD1);
      cobolKeywords.add("END-SUBTRACT", Token.KEYWORD1);
      cobolKeywords.add("END-UNSTRING", Token.KEYWORD1);
      cobolKeywords.add("END-WRITE", Token.KEYWORD1);
      cobolKeywords.add("ENDING", Token.KEYWORD1);
      cobolKeywords.add("ENTER", Token.KEYWORD1);
      cobolKeywords.add("ENTRY", Token.KEYWORD1);
      cobolKeywords.add("ENVIRONMENT", Token.KEYWORD1);
      cobolKeywords.add("EOL", Token.KEYWORD1);
      cobolKeywords.add("EOP", Token.KEYWORD1);
      cobolKeywords.add("EOS", Token.KEYWORD1);
      cobolKeywords.add("EQUAL", Token.KEYWORD1);
      cobolKeywords.add("EQUALS", Token.KEYWORD1);
      cobolKeywords.add("ERASE", Token.KEYWORD1);
      cobolKeywords.add("ERROR", Token.KEYWORD1);
      cobolKeywords.add("ESCAPE", Token.KEYWORD1);
      cobolKeywords.add("ESI", Token.KEYWORD1);
      cobolKeywords.add("EVALUATE", Token.KEYWORD1);
      cobolKeywords.add("EVERY", Token.KEYWORD1);
      cobolKeywords.add("EXAMINE", Token.KEYWORD1);
      cobolKeywords.add("EXCEEDS", Token.KEYWORD1);
      cobolKeywords.add("EXCEPTION", Token.KEYWORD1);
      cobolKeywords.add("EXCESS-3", Token.KEYWORD1);
      cobolKeywords.add("EXCLUSIVE", Token.KEYWORD1);
      cobolKeywords.add("EXEC", Token.KEYWORD1);
      cobolKeywords.add("EXECUTE", Token.KEYWORD1);
      cobolKeywords.add("EXHIBIT", Token.KEYWORD1);
      cobolKeywords.add("EXIT", Token.KEYWORD1);
      cobolKeywords.add("EXTEND", Token.KEYWORD1);
      cobolKeywords.add("EXTENDED-SEARCH", Token.KEYWORD1);
      cobolKeywords.add("EXTERNAL", Token.KEYWORD1);

      cobolKeywords.add("FACTORY", Token.KEYWORD1);
      cobolKeywords.add("FALSE", Token.KEYWORD1);
      cobolKeywords.add("FD", Token.KEYWORD1);
      cobolKeywords.add("FH-FCD", Token.KEYWORD1);
      cobolKeywords.add("FH-KEYDEF", Token.KEYWORD1);
      cobolKeywords.add("FILE", Token.KEYWORD1);
      cobolKeywords.add("FILE-CONTROL", Token.KEYWORD1);
      cobolKeywords.add("FILE-ID", Token.KEYWORD1);
      cobolKeywords.add("FILE-LIMIT", Token.KEYWORD1);
      cobolKeywords.add("FILE-LIMITS", Token.KEYWORD1);
      cobolKeywords.add("FILLER", Token.KEYWORD1);
      cobolKeywords.add("FINAL", Token.KEYWORD1);
      cobolKeywords.add("FIRST", Token.KEYWORD1);
      cobolKeywords.add("FIXED", Token.KEYWORD1);
      cobolKeywords.add("FOOTING", Token.KEYWORD1);
      cobolKeywords.add("FOR", Token.KEYWORD1);
      cobolKeywords.add("FOREGROUND-COLOR", Token.KEYWORD1);
      cobolKeywords.add("FOREGROUND-COLOUR", Token.KEYWORD1);
      cobolKeywords.add("FROM", Token.KEYWORD1);
      cobolKeywords.add("FULL", Token.KEYWORD1);
      cobolKeywords.add("FUNCTION", Token.KEYWORD1);

      cobolKeywords.add("GENERATE", Token.KEYWORD1);
      cobolKeywords.add("GIVING", Token.KEYWORD1);
      cobolKeywords.add("GLOBAL", Token.KEYWORD1);
      cobolKeywords.add("GO", Token.KEYWORD1);
      cobolKeywords.add("GOBACK", Token.KEYWORD1);
      cobolKeywords.add("GREATER", Token.KEYWORD1);
      cobolKeywords.add("GRID", Token.KEYWORD1);
      cobolKeywords.add("GROUP", Token.KEYWORD1);

      cobolKeywords.add("HEADING", Token.KEYWORD1);
      cobolKeywords.add("HIGH", Token.KEYWORD1);
      cobolKeywords.add("HIGH-VALUE", Token.KEYWORD1);
      cobolKeywords.add("HIGH-VALUES", Token.KEYWORD1);
      cobolKeywords.add("HIGHLIGHT", Token.KEYWORD1);

      cobolKeywords.add("I-O", Token.KEYWORD1);
      cobolKeywords.add("I-O-CONTROL", Token.KEYWORD1);
      cobolKeywords.add("ID", Token.KEYWORD1);
      cobolKeywords.add("IDENTIFICATION", Token.KEYWORD1);
      cobolKeywords.add("IF", Token.KEYWORD1);
      cobolKeywords.add("IGNORE", Token.KEYWORD1);
      cobolKeywords.add("IN", Token.KEYWORD1);
      cobolKeywords.add("INDEX", Token.KEYWORD1);
      cobolKeywords.add("INDEXED", Token.KEYWORD1);
      cobolKeywords.add("INDICATE", Token.KEYWORD1);
      cobolKeywords.add("INHERITING", Token.KEYWORD1);
      cobolKeywords.add("INITIAL", Token.KEYWORD1);
      cobolKeywords.add("INITIALIZE", Token.KEYWORD1);
      cobolKeywords.add("INITIATE", Token.KEYWORD1);
      cobolKeywords.add("INPUT", Token.KEYWORD1);
      cobolKeywords.add("INPUT-OUTPUT", Token.KEYWORD1);
      cobolKeywords.add("INSERT", Token.KEYWORD1);
      cobolKeywords.add("INSPECT", Token.KEYWORD1);
      cobolKeywords.add("INSTALLATION", Token.KEYWORD1);
      cobolKeywords.add("INTO", Token.KEYWORD1);
      cobolKeywords.add("INVALID", Token.KEYWORD1);
      cobolKeywords.add("INVOKE", Token.KEYWORD1);
      cobolKeywords.add("IS", Token.KEYWORD1);

      cobolKeywords.add("JAPANESE", Token.KEYWORD1);
      cobolKeywords.add("JUST", Token.KEYWORD1);
      cobolKeywords.add("JUSTIFIED", Token.KEYWORD1);

      cobolKeywords.add("KANJI", Token.KEYWORD1);
      cobolKeywords.add("KEPT", Token.KEYWORD1);
      cobolKeywords.add("KEY", Token.KEYWORD1);
      cobolKeywords.add("KEYBOARD", Token.KEYWORD1);

      cobolKeywords.add("LABEL", Token.KEYWORD1);
      cobolKeywords.add("LAST", Token.KEYWORD1);
      cobolKeywords.add("LEADING", Token.KEYWORD1);
      cobolKeywords.add("LEAVE", Token.KEYWORD1);
      cobolKeywords.add("LEFT", Token.KEYWORD1);
      cobolKeywords.add("LEFT-JUSTIFY", Token.KEYWORD1);
      cobolKeywords.add("LEFTLINE", Token.KEYWORD1);
      cobolKeywords.add("LENGTH", Token.KEYWORD1);
      cobolKeywords.add("LENGTH-CHECK", Token.KEYWORD1);
      cobolKeywords.add("LESS", Token.KEYWORD1);
      cobolKeywords.add("LIMIT", Token.KEYWORD1);
      cobolKeywords.add("LIMITS", Token.KEYWORD1);
      cobolKeywords.add("LIN", Token.KEYWORD1);
      cobolKeywords.add("LINAGE", Token.KEYWORD1);
      cobolKeywords.add("LINAGE-COUNTER", Token.KEYWORD1);
      cobolKeywords.add("LINE", Token.KEYWORD1);
      cobolKeywords.add("LINE-COUNTER", Token.KEYWORD1);
      cobolKeywords.add("LINES", Token.KEYWORD1);
      cobolKeywords.add("LINKAGE", Token.KEYWORD1);
      cobolKeywords.add("LOCAL-STORAGE", Token.KEYWORD1);
      cobolKeywords.add("LOCK", Token.KEYWORD1);
      cobolKeywords.add("LOCKING", Token.KEYWORD1);
      cobolKeywords.add("LOW", Token.KEYWORD1);
      cobolKeywords.add("LOW-VALUE", Token.KEYWORD1);
      cobolKeywords.add("LOW-VALUES", Token.KEYWORD1);
      cobolKeywords.add("LOWER", Token.KEYWORD1);
      cobolKeywords.add("LOWLIGHT", Token.KEYWORD1);

      cobolKeywords.add("MANUAL", Token.KEYWORD1);
      cobolKeywords.add("MASTER-INDEX", Token.KEYWORD1);
      cobolKeywords.add("MEMORY", Token.KEYWORD1);
      cobolKeywords.add("MERGE", Token.KEYWORD1);
      cobolKeywords.add("MESSAGE", Token.KEYWORD1);
      cobolKeywords.add("METHOD", Token.KEYWORD1);
      cobolKeywords.add("MODE", Token.KEYWORD1);
      cobolKeywords.add("MODULES", Token.KEYWORD1);
      cobolKeywords.add("MORE-LABELS", Token.KEYWORD1);
      cobolKeywords.add("MOVE", Token.KEYWORD1);
      cobolKeywords.add("MULTIPLE", Token.KEYWORD1);
      cobolKeywords.add("MULTIPLY", Token.KEYWORD1);

      cobolKeywords.add("NAME", Token.KEYWORD1);
      cobolKeywords.add("NAMED", Token.KEYWORD1);
      cobolKeywords.add("NATIONAL", Token.KEYWORD1);
      cobolKeywords.add("NATIONAL-EDITED", Token.KEYWORD1);
      cobolKeywords.add("NATIVE", Token.KEYWORD1);
      cobolKeywords.add("NCHAR", Token.KEYWORD1);
      cobolKeywords.add("NEGATIVE", Token.KEYWORD1);
      cobolKeywords.add("NEXT", Token.KEYWORD1);
      cobolKeywords.add("NO", Token.KEYWORD1);
      cobolKeywords.add("NO-ECHO", Token.KEYWORD1);
      cobolKeywords.add("NOMINAL", Token.KEYWORD1);
      cobolKeywords.add("NOT", Token.KEYWORD1);
      cobolKeywords.add("NOTE", Token.KEYWORD1);
      cobolKeywords.add("NSTD-REELS", Token.KEYWORD1);
      cobolKeywords.add("NULL", Token.KEYWORD1);
      cobolKeywords.add("NULLS", Token.KEYWORD1);
      cobolKeywords.add("NUMBER", Token.KEYWORD1);
      cobolKeywords.add("NUMERIC", Token.KEYWORD1);
      cobolKeywords.add("NUMERIC-EDITED", Token.KEYWORD1);

      cobolKeywords.add("OBJECT", Token.KEYWORD1);
      cobolKeywords.add("OBJECT-COMPUTER", Token.KEYWORD1);
      cobolKeywords.add("OBJECT-STORAGE", Token.KEYWORD1);
      cobolKeywords.add("OCCURS", Token.KEYWORD1);
      cobolKeywords.add("OF", Token.KEYWORD1);
      cobolKeywords.add("OFF", Token.KEYWORD1);
      cobolKeywords.add("OMITTED", Token.KEYWORD1);
      cobolKeywords.add("ON", Token.KEYWORD1);
      cobolKeywords.add("OOSTACKPTR", Token.KEYWORD1);
      cobolKeywords.add("OPEN", Token.KEYWORD1);
      cobolKeywords.add("OPTIONAL", Token.KEYWORD1);
      cobolKeywords.add("OR", Token.KEYWORD1);
      cobolKeywords.add("ORDER", Token.KEYWORD1);
      cobolKeywords.add("ORGANIZATION", Token.KEYWORD1);
      cobolKeywords.add("OTHER", Token.KEYWORD1);
      cobolKeywords.add("OTHERWISE", Token.KEYWORD1);
      cobolKeywords.add("OUTPUT", Token.KEYWORD1);
      cobolKeywords.add("OVERFLOW", Token.KEYWORD1);
      cobolKeywords.add("OVERLINE", Token.KEYWORD1);

      cobolKeywords.add("PACKED-DECIMAL", Token.KEYWORD1);
      cobolKeywords.add("PADDING", Token.KEYWORD1);
      cobolKeywords.add("PAGE", Token.KEYWORD1);
      cobolKeywords.add("PAGE-COUNTER", Token.KEYWORD1);
      cobolKeywords.add("PARAGRAPH", Token.KEYWORD1);
      cobolKeywords.add("PASSWORD", Token.KEYWORD1);
      cobolKeywords.add("PERFORM", Token.KEYWORD1);
      cobolKeywords.add("PF", Token.KEYWORD1);
      cobolKeywords.add("PH", Token.KEYWORD1);
      cobolKeywords.add("PIC", Token.KEYWORD1);
      cobolKeywords.add("PICTURE", Token.KEYWORD1);
      cobolKeywords.add("PLUS", Token.KEYWORD1);
      cobolKeywords.add("POINTER", Token.KEYWORD1);
      cobolKeywords.add("POS", Token.KEYWORD1);
      cobolKeywords.add("POSITION", Token.KEYWORD1);
      cobolKeywords.add("POSITIONING", Token.KEYWORD1);
      cobolKeywords.add("POSITIVE", Token.KEYWORD1);
      cobolKeywords.add("PREVIOUS", Token.KEYWORD1);
      cobolKeywords.add("PRINT", Token.KEYWORD1);
      cobolKeywords.add("PRINT-SWITCH", Token.KEYWORD1);
      cobolKeywords.add("PRINTER", Token.KEYWORD1);
      cobolKeywords.add("PRINTER-1", Token.KEYWORD1);
      cobolKeywords.add("PRINTING", Token.KEYWORD1);
      cobolKeywords.add("PRIVATE", Token.KEYWORD1);
      cobolKeywords.add("PROCEDURE", Token.KEYWORD1);
      cobolKeywords.add("PROCEDURE-POINTER", Token.KEYWORD1);
      cobolKeywords.add("PROCEDURES", Token.KEYWORD1);
      cobolKeywords.add("PROCEED", Token.KEYWORD1);
      cobolKeywords.add("PROCESSING", Token.KEYWORD1);
      cobolKeywords.add("PROGRAM", Token.KEYWORD1);
      cobolKeywords.add("PROGRAM-ID", Token.KEYWORD1);
      cobolKeywords.add("PROMPT", Token.KEYWORD1);
      cobolKeywords.add("PROTECTED", Token.KEYWORD1);
      cobolKeywords.add("PUBLIC", Token.KEYWORD1);
      cobolKeywords.add("PURGE", Token.KEYWORD1);

      cobolKeywords.add("QUEUE", Token.KEYWORD1);
      cobolKeywords.add("QUOTE", Token.KEYWORD1);
      cobolKeywords.add("QUOTES", Token.KEYWORD1);

      cobolKeywords.add("RANDOM", Token.KEYWORD1);
      cobolKeywords.add("RANGE", Token.KEYWORD1);
      cobolKeywords.add("RD", Token.KEYWORD1);
      cobolKeywords.add("READ", Token.KEYWORD1);
      cobolKeywords.add("READY", Token.KEYWORD1);
      cobolKeywords.add("RECEIVE", Token.KEYWORD1);
      cobolKeywords.add("RECORD", Token.KEYWORD1);
      cobolKeywords.add("RECORD-OVERFLOW", Token.KEYWORD1);
      cobolKeywords.add("RECORDING", Token.KEYWORD1);
      cobolKeywords.add("RECORDS", Token.KEYWORD1);
      cobolKeywords.add("REDEFINES", Token.KEYWORD1);
      cobolKeywords.add("REEL", Token.KEYWORD1);
      cobolKeywords.add("REFERENCE", Token.KEYWORD1);
      cobolKeywords.add("REFERENCES", Token.KEYWORD1);
      cobolKeywords.add("RELATIVE", Token.KEYWORD1);
      cobolKeywords.add("RELEASE", Token.KEYWORD1);
      cobolKeywords.add("RELOAD", Token.KEYWORD1);
      cobolKeywords.add("REMAINDER", Token.KEYWORD1);
      cobolKeywords.add("REMARKS", Token.KEYWORD1);
      cobolKeywords.add("REMOVAL", Token.KEYWORD1);
      cobolKeywords.add("RENAMES", Token.KEYWORD1);
      cobolKeywords.add("REORG-CRITERIA", Token.KEYWORD1);
      cobolKeywords.add("REPLACE", Token.KEYWORD1);
      cobolKeywords.add("REPLACING", Token.KEYWORD1);
      cobolKeywords.add("REPORT", Token.KEYWORD1);
      cobolKeywords.add("REPORTING", Token.KEYWORD1);
      cobolKeywords.add("REPORTS", Token.KEYWORD1);
      cobolKeywords.add("REQUIRED", Token.KEYWORD1);
      cobolKeywords.add("REREAD", Token.KEYWORD1);
      cobolKeywords.add("RERUN", Token.KEYWORD1);
      cobolKeywords.add("RESERVE", Token.KEYWORD1);
      cobolKeywords.add("RESET", Token.KEYWORD1);
      cobolKeywords.add("RETURN", Token.KEYWORD1);
      cobolKeywords.add("RETURN-CODE", Token.KEYWORD1);
      cobolKeywords.add("RETURNING", Token.KEYWORD1);
      cobolKeywords.add("REVERSE", Token.KEYWORD1);
      cobolKeywords.add("REVERSE-VIDEO", Token.KEYWORD1);
      cobolKeywords.add("REVERSED", Token.KEYWORD1);
      cobolKeywords.add("REWIND", Token.KEYWORD1);
      cobolKeywords.add("REWRITE", Token.KEYWORD1);
      cobolKeywords.add("RF", Token.KEYWORD1);
      cobolKeywords.add("RH", Token.KEYWORD1);
      cobolKeywords.add("RIGHT", Token.KEYWORD1);
      cobolKeywords.add("RIGHT-JUSTIFY", Token.KEYWORD1);
      cobolKeywords.add("ROLLBACK", Token.KEYWORD1);
      cobolKeywords.add("ROUNDED", Token.KEYWORD1);
      cobolKeywords.add("RUN", Token.KEYWORD1);

      cobolKeywords.add("S01", Token.KEYWORD1);
      cobolKeywords.add("S02", Token.KEYWORD1);
      cobolKeywords.add("S03", Token.KEYWORD1);
      cobolKeywords.add("S04", Token.KEYWORD1);
      cobolKeywords.add("S05", Token.KEYWORD1);
      cobolKeywords.add("SAME", Token.KEYWORD1);
      cobolKeywords.add("SCREEN", Token.KEYWORD1);
      cobolKeywords.add("SD", Token.KEYWORD1);
      cobolKeywords.add("SEARCH", Token.KEYWORD1);
      cobolKeywords.add("SECTION", Token.KEYWORD1);
      cobolKeywords.add("SECURE", Token.KEYWORD1);
      cobolKeywords.add("SECURITY", Token.KEYWORD1);
      cobolKeywords.add("SEEK", Token.KEYWORD1);
      cobolKeywords.add("SEGMENT", Token.KEYWORD1);
      cobolKeywords.add("SEGMENT-LIMIT", Token.KEYWORD1);
      cobolKeywords.add("SELECT", Token.KEYWORD1);
      cobolKeywords.add("SELECTIVE", Token.KEYWORD1);
      cobolKeywords.add("SEND", Token.KEYWORD1);
      cobolKeywords.add("SENTENCE", Token.KEYWORD1);
      cobolKeywords.add("SEPARATE", Token.KEYWORD1);
      cobolKeywords.add("SEQUENCE", Token.KEYWORD1);
      cobolKeywords.add("SEQUENTIAL", Token.KEYWORD1);
      cobolKeywords.add("SERVICE", Token.KEYWORD1);
      cobolKeywords.add("SET", Token.KEYWORD1);
      cobolKeywords.add("SHIFT-IN", Token.KEYWORD1);
      cobolKeywords.add("SHIFT-OUT", Token.KEYWORD1);
      cobolKeywords.add("SIGN", Token.KEYWORD1);
      cobolKeywords.add("SIZE", Token.KEYWORD1);
      cobolKeywords.add("SKIP1", Token.KEYWORD1);
      cobolKeywords.add("SKIP2", Token.KEYWORD1);
      cobolKeywords.add("SKIP3", Token.KEYWORD1);
      cobolKeywords.add("SORT", Token.KEYWORD1);
      cobolKeywords.add("SORT-CONTROL", Token.KEYWORD1);
      cobolKeywords.add("SORT-CORE-SIZE", Token.KEYWORD1);
      cobolKeywords.add("SORT-FILE-SIZE", Token.KEYWORD1);
      cobolKeywords.add("SORT-MERGE", Token.KEYWORD1);
      cobolKeywords.add("SORT-MESSAGE", Token.KEYWORD1);
      cobolKeywords.add("SORT-MODE-SIZE", Token.KEYWORD1);
      cobolKeywords.add("SORT-OPTION", Token.KEYWORD1);
      cobolKeywords.add("SORT-RETURN", Token.KEYWORD1);
      cobolKeywords.add("SOURCE", Token.KEYWORD1);
      cobolKeywords.add("SOURCE-COMPUTER", Token.KEYWORD1);
      cobolKeywords.add("SPACE", Token.KEYWORD1);
      cobolKeywords.add("SPACE-FILL", Token.KEYWORD1);
      cobolKeywords.add("SPACES", Token.KEYWORD1);
      cobolKeywords.add("SPECIAL-NAMES", Token.KEYWORD1);
      cobolKeywords.add("STANDARD", Token.KEYWORD1);
      cobolKeywords.add("STANDARD-1", Token.KEYWORD1);
      cobolKeywords.add("STANDARD-2", Token.KEYWORD1);
      cobolKeywords.add("START", Token.KEYWORD1);
      cobolKeywords.add("STATUS", Token.KEYWORD1);
      cobolKeywords.add("STOP", Token.KEYWORD1);
      cobolKeywords.add("STORE", Token.KEYWORD1);
      cobolKeywords.add("STRING", Token.KEYWORD1);
      cobolKeywords.add("SUB-QUEUE-1", Token.KEYWORD1);
      cobolKeywords.add("SUB-QUEUE-2", Token.KEYWORD1);
      cobolKeywords.add("SUB-QUEUE-3", Token.KEYWORD1);
      cobolKeywords.add("SUBTRACT", Token.KEYWORD1);
      cobolKeywords.add("SUM", Token.KEYWORD1);
      cobolKeywords.add("SUPER", Token.KEYWORD1);
      cobolKeywords.add("SUPPRESS", Token.KEYWORD1);
      cobolKeywords.add("SYMBOLIC", Token.KEYWORD1);
      cobolKeywords.add("SYNC", Token.KEYWORD1);
      cobolKeywords.add("SYNCHRONIZED", Token.KEYWORD1);
      cobolKeywords.add("SYSIN", Token.KEYWORD1);
      cobolKeywords.add("SYSIPT", Token.KEYWORD1);
      cobolKeywords.add("SYSLST", Token.KEYWORD1);
      cobolKeywords.add("SYSOUT", Token.KEYWORD1);
      cobolKeywords.add("SYSPCH", Token.KEYWORD1);
      cobolKeywords.add("SYSPUNCH", Token.KEYWORD1);

      cobolKeywords.add("TAB", Token.KEYWORD1);
      cobolKeywords.add("TABLE", Token.KEYWORD1);
      cobolKeywords.add("TALLY", Token.KEYWORD1);
      cobolKeywords.add("TALLYING", Token.KEYWORD1);
      cobolKeywords.add("TAPE", Token.KEYWORD1);
      cobolKeywords.add("TERMINAL", Token.KEYWORD1);
      cobolKeywords.add("TERMINATE", Token.KEYWORD1);
      cobolKeywords.add("TEST", Token.KEYWORD1);
      cobolKeywords.add("TEXT", Token.KEYWORD1);
      cobolKeywords.add("THAN", Token.KEYWORD1);
      cobolKeywords.add("THEN", Token.KEYWORD1);
      cobolKeywords.add("THROUGH", Token.KEYWORD1);
      cobolKeywords.add("THRU", Token.KEYWORD1);
      cobolKeywords.add("TIME", Token.KEYWORD1);
      cobolKeywords.add("TIME-OF-DAY", Token.KEYWORD1);
      cobolKeywords.add("TIME-OUT", Token.KEYWORD1);
      cobolKeywords.add("TIMEOUT", Token.KEYWORD1);
      cobolKeywords.add("TIMES", Token.KEYWORD1);
      cobolKeywords.add("TITLE", Token.KEYWORD1);
      cobolKeywords.add("TO", Token.KEYWORD1);
      cobolKeywords.add("TOP", Token.KEYWORD1);
      cobolKeywords.add("TOTALED", Token.KEYWORD1);
      cobolKeywords.add("TOTALING", Token.KEYWORD1);
      cobolKeywords.add("TRACE", Token.KEYWORD1);
      cobolKeywords.add("TRACK-AREA", Token.KEYWORD1);
      cobolKeywords.add("TRACK-LIMIT", Token.KEYWORD1);
      cobolKeywords.add("TRACKS", Token.KEYWORD1);
      cobolKeywords.add("TRAILING", Token.KEYWORD1);
      cobolKeywords.add("TRAILING-SIGN", Token.KEYWORD1);
      cobolKeywords.add("TRANSFORM", Token.KEYWORD1);
      cobolKeywords.add("TRUE", Token.KEYWORD1);
      cobolKeywords.add("TYPE", Token.KEYWORD1);
      cobolKeywords.add("TYPEDEF", Token.KEYWORD1);

      cobolKeywords.add("UNDERLINE", Token.KEYWORD1);
      cobolKeywords.add("UNEQUAL", Token.KEYWORD1);
      cobolKeywords.add("UNIT", Token.KEYWORD1);
      cobolKeywords.add("UNLOCK", Token.KEYWORD1);
      cobolKeywords.add("UNSTRING", Token.KEYWORD1);
      cobolKeywords.add("UNTIL", Token.KEYWORD1);
      cobolKeywords.add("UP", Token.KEYWORD1);
      cobolKeywords.add("UPDATE", Token.KEYWORD1);
      cobolKeywords.add("UPON", Token.KEYWORD1);
      cobolKeywords.add("UPPER", Token.KEYWORD1);
      cobolKeywords.add("UPSI-0", Token.KEYWORD1);
      cobolKeywords.add("UPSI-1", Token.KEYWORD1);
      cobolKeywords.add("UPSI-2", Token.KEYWORD1);
      cobolKeywords.add("UPSI-3", Token.KEYWORD1);
      cobolKeywords.add("UPSI-4", Token.KEYWORD1);
      cobolKeywords.add("UPSI-5", Token.KEYWORD1);
      cobolKeywords.add("UPSI-6", Token.KEYWORD1);
      cobolKeywords.add("UPSI-7", Token.KEYWORD1);
      cobolKeywords.add("USAGE", Token.KEYWORD1);
      cobolKeywords.add("USE", Token.KEYWORD1);
      cobolKeywords.add("USER", Token.KEYWORD1);
      cobolKeywords.add("USING", Token.KEYWORD1);

      cobolKeywords.add("VALUE", Token.KEYWORD1);
      cobolKeywords.add("VALUES", Token.KEYWORD1);
      cobolKeywords.add("VARIABLE", Token.KEYWORD1);
      cobolKeywords.add("VARYING", Token.KEYWORD1);

      cobolKeywords.add("WAIT", Token.KEYWORD1);
      cobolKeywords.add("WHEN", Token.KEYWORD1);
      cobolKeywords.add("WHEN-COMPILED", Token.KEYWORD1);
      cobolKeywords.add("WITH", Token.KEYWORD1);
      cobolKeywords.add("WORDS", Token.KEYWORD1);
      cobolKeywords.add("WORKING-STORAGE", Token.KEYWORD1);
      cobolKeywords.add("WRITE", Token.KEYWORD1);
      cobolKeywords.add("WRITE-ONLY", Token.KEYWORD1);
      cobolKeywords.add("WRITE-VERIFY", Token.KEYWORD1);

      cobolKeywords.add("ZERO", Token.KEYWORD1);
      cobolKeywords.add("ZERO-FILL", Token.KEYWORD1);
      cobolKeywords.add("ZEROES", Token.KEYWORD1);
      cobolKeywords.add("ZEROS", Token.KEYWORD1);

      cobolKeywords.add("ACOS", Token.KEYWORD2);
      cobolKeywords.add("ANNUITY", Token.KEYWORD2);
      cobolKeywords.add("ASIN", Token.KEYWORD2);
      cobolKeywords.add("ATAN", Token.KEYWORD2);
      cobolKeywords.add("CHAR", Token.KEYWORD2);
      cobolKeywords.add("COS", Token.KEYWORD2);
      cobolKeywords.add("CURRENT-DATE", Token.KEYWORD2);
      cobolKeywords.add("DATE-OF-INTEGER", Token.KEYWORD2);
      cobolKeywords.add("DAY-OF-INTEGER", Token.KEYWORD2);
      cobolKeywords.add("FACTORIAL", Token.KEYWORD2);
      cobolKeywords.add("INTEGER", Token.KEYWORD2);
      cobolKeywords.add("INTEGER-OF-DATE", Token.KEYWORD2);
      cobolKeywords.add("INTEGER-OF-DAY", Token.KEYWORD2);
      cobolKeywords.add("INTEGER-PART", Token.KEYWORD2);
      cobolKeywords.add("LOG", Token.KEYWORD2);
      cobolKeywords.add("LOG10", Token.KEYWORD2);
      cobolKeywords.add("LOWER-CASE", Token.KEYWORD2);
      cobolKeywords.add("MAX", Token.KEYWORD2);
      cobolKeywords.add("MEAN", Token.KEYWORD2);
      cobolKeywords.add("MEDIAN", Token.KEYWORD2);
      cobolKeywords.add("MIDRANGE", Token.KEYWORD2);
      cobolKeywords.add("MIN", Token.KEYWORD2);
      cobolKeywords.add("MOD", Token.KEYWORD2);
      cobolKeywords.add("NUMVAL", Token.KEYWORD2);
      cobolKeywords.add("NUMVAL-C", Token.KEYWORD2);
      cobolKeywords.add("ORD", Token.KEYWORD2);
      cobolKeywords.add("ORD-MAX", Token.KEYWORD2);
      cobolKeywords.add("ORD-MIN", Token.KEYWORD2);
      cobolKeywords.add("PRESENT-VALUE", Token.KEYWORD2);
      cobolKeywords.add("RANDOM", Token.KEYWORD2);
      cobolKeywords.add("RANGE", Token.KEYWORD2);
      cobolKeywords.add("REM", Token.KEYWORD2);
      cobolKeywords.add("REVERSE", Token.KEYWORD2);
      cobolKeywords.add("SIN", Token.KEYWORD2);
      cobolKeywords.add("SQRT", Token.KEYWORD2);
      cobolKeywords.add("STANDARD-DEVIATION", Token.KEYWORD2);
      cobolKeywords.add("SUM", Token.KEYWORD2);
      cobolKeywords.add("TAN", Token.KEYWORD2);
      cobolKeywords.add("UPPER-CASE", Token.KEYWORD2);
      cobolKeywords.add("VARIANCE", Token.KEYWORD2);
      cobolKeywords.add("WHEN-COMPILED", Token.KEYWORD2);

      cobolKeywords.add("[COPY-PREFIX]", Token.LITERAL2);
      cobolKeywords.add("[COUNT]", Token.LITERAL2);
      cobolKeywords.add("[DISPLAY]", Token.LITERAL2);
      cobolKeywords.add("[EXECUTE]", Token.LITERAL2);
      cobolKeywords.add("[PG]", Token.LITERAL2);
      cobolKeywords.add("[PREFIX]", Token.LITERAL2);
      cobolKeywords.add("[PROGRAM]", Token.LITERAL2);
      cobolKeywords.add("[SPECIAL-PREFIX]", Token.LITERAL2);
      cobolKeywords.add("[TESTCASE]", Token.LITERAL2);
    }

    return cobolKeywords;
  }

  private boolean doKeyword(Segment line, int i, char c)
  {
    int i1 = i + 1;

    int len = i - lastKeyword;
    byte id = keywords.lookup(line, lastKeyword, len);
    if (id != Token.NULL)
    {
      if(lastKeyword != lastOffset)
        addToken(lastKeyword - lastOffset, Token.NULL);
      addToken(len, id);
      lastOffset = i;
    }
    lastKeyword = i1;
    return false;
  }
}

// End of CobolTokenMarker.java
