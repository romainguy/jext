/*
 * FortranTokenMarker.java - Fortran token marker
 * by Carl Smotricz
 * carl@smotricz.com
 * www.smotricz.com
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

/**
 * Custom TokenMarker for UNISYS's <cite>ASCII FORTRAN 77</cite>.
 * Characteristics of this dialect are:<ul>
 *  <li>Fixed column format, with<ul>
 *   <li>comment character ( 'C'|'c'|'*' ) in column 1,</li>
 *   <li>labels (numeric) in column 1-5,</li>
 *   <li>continuation character ( any nonblank ) in column 6,</li>
 *   <li>logical end of line after column 72.</li>
 *   </ul></li>
 * <li>Nonstandard block comment character ( '@' ) in any column,</li>
 * <li>Some nonstandard functions: <code>BITS</code>, <code>BOOL</code>,
 *   <code>INDEX</code>, <code>TRMLEN</code></li>
 * </ul>
 * It should be easy enough to adapt this class for minor variations
 * in the dialect so long as the format is the classic fixed column format.
 * As this scanner is highly optimized for the fixed column format, it
 * is probably not readily adaptable for freeform FORTRAN code.
 */
 
public class FortranTokenMarker extends TokenMarker
{
  // private members

  private final static int MAYBE_KEYWORD_FIRST = Token.INTERNAL_FIRST;
  private final static int MAYBE_KEYWORD_MORE = 1 + MAYBE_KEYWORD_FIRST;
  private final static String S_E_P = "START EDIT PAGE";

  private static KeywordMap fortranKeywords;
  private KeywordMap keywords;

  private int lastOffset;

  /**
   * Constructor, with a wee bit of initialization.
   */
  public FortranTokenMarker()
  {
    this.keywords = getKeywords();
  }

  /**
   * Implementation of code to mark tokens.
   */
  public byte markTokensImpl(byte token, Segment line, int lineIndex)
  {
    byte lastLineToken = token;

    // --- Very quick check for empty line
    if (line.count < 1) return lastLineToken; // EXIT METHOD!

    char[] array = line.array;
    int offset = line.offset;
    char c = array[offset];

    // --- Very quick check for 'C' comment line
    if (c == 'C' || c == 'c' || c == '*') 
    { 
      addToken(line.count, Token.COMMENT1);
      return lastLineToken; // EXIT METHOD!
    }
    
    token = Token.NULL; // context usually ends on line boundary
    int lineEnd = offset + line.count;

    // --- Check for a label
    int limit = Math.min(lineEnd, offset + 5);
    int i;
    for (i=offset; i<limit; i++) 
    {
      c = array[i];
      if (c == '@') 
      { 
        // comment to end of line
        guardedAddToken(i - offset, token);
        addToken(lineEnd - i, Token.COMMENT2);
        return lastLineToken; // EXIT METHOD!
      } 
      else if (token == Token.NULL && '0' <= c && c <= '9') 
      {
        // numerics: Label.
        token = Token.LABEL;
      }
    }
    addToken(limit - offset, token);

    // --- End of line?
    if (limit == lineEnd) return Token.NULL; // EXIT METHOD!
      
    // --- Check for line continuation
    c = array[i];
    if (c == '@')
    {
      // comment to end of line
      addToken(lineEnd - i, Token.COMMENT2);
      return Token.NULL; // EXIT METHOD!
    } 
    else if (c == ' ') 
    {
      // just a plain old blank
      addToken(1, Token.NULL);
      token = Token.NULL;
    }
    else
    {
      // line continuation: mark it as a label to make it stand out
      addToken(1, Token.LABEL);
      token = lastLineToken;
    }

    // --- End of line?
    if (lineEnd == offset + 6) return Token.NULL; // EXIT METHOD!

    limit = Math.min(offset + 72, lineEnd);
    lastOffset = offset + 6;

    // --- Check for "START EDIT PAGE"
    if (checkStartEditPage(line)) {
      addToken(limit - lastOffset, Token.LABEL);
      return Token.NULL; // EXIT METHOD!
    }

    // --- 'normal' real, honest coding now
    int i1;
    for(i = lastOffset; i < limit; i++)
    {
      i1 = i + 1;
      c = array[i];

      if (token == Token.LITERAL1)
      {
        // ignore anything but the end of literal
        if (c == '\'')
        {
          addToken(i1 - lastOffset, Token.LITERAL1);
          token = Token.NULL;
          lastOffset = i1;
        }
      } 
      else if (c == '@')
      {
        // comment to end of line
        guardedAddToken(i - lastOffset, token);
        addToken(lineEnd - i, Token.COMMENT2);
        return token; // EXIT METHOD!
      }
      else if (token == Token.NULL) 
      {
        switch (c)
        {
          case '\'':
            guardedAddToken(i - lastOffset, token);
            token = Token.LITERAL1;
            lastOffset = i;
            break;
          case '+':
          case '-':
          case '*':
          case '/':
          case '(':
          case ')':
          case ',':
          case ':':
          case '=':
            guardedAddToken(i - lastOffset, token);
            addToken(1, Token.OPERATOR);
            lastOffset = i1;
            break;
          default:
            if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z'))
            {
              guardedAddToken(i - lastOffset, token);
              token = MAYBE_KEYWORD_FIRST;
              lastOffset = i;
              break;
            }
            else
            {
              // unknown special char - maintain state
            }
        }
      }
      else if (token == MAYBE_KEYWORD_FIRST ||
               token == MAYBE_KEYWORD_MORE)
      {
        if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || 
            ('0' <= c && c <= '9') || (c == '$'))
        {
          token = MAYBE_KEYWORD_MORE;
        }
        else
        {
          doKeyword(line, i);
          c = array[i];
          switch (c)
          {
            case '+':
            case '-':
            case '*':
            case '/':
            case '(':
            case ')':
            case ',':
            case ':':
            case '=':
              guardedAddToken(i - lastOffset, token);
              addToken(1, Token.OPERATOR);
              lastOffset = i1;
              break;
          }
          token = Token.NULL;
        }
      }
      else 
      {
        throw new InternalError("Invalid state: " + token);
      }  
    } // end for

    // --- Finish up the coding part of the line
    if (token == MAYBE_KEYWORD_FIRST || token == MAYBE_KEYWORD_MORE)
    {
      doKeyword(line, i);
      token = Token.NULL;
    }
    else
    {
      guardedAddToken(i - lastOffset, token);
    }

    // --- End of line?
    if (limit == lineEnd) return token; // EXIT METHOD!

    // --- Anything beyond column 72 is comment
    guardedAddToken(lineEnd - i, Token.COMMENT2);
    //
    return token;
  }

  private boolean checkStartEditPage(Segment line)
  {
    if (line.count < 6+15) return false;
    int limit = line.offset + Math.min(line.count, 72);
    int i;
    for (i=line.offset+6; i<limit-15; i++) if (line.array[i] != ' ') break;
    if (!SyntaxUtilities.regionMatches(false, line, i, S_E_P)) return false;
    for (i+=15; i<limit; i++) if (line.array[i] != ' ') return false;
    return true;
  }

  /**
   * Add the latest token to the current list.
   * Process 'START' as a special case.
   */        
  private void doKeyword(Segment line, int keywordEnd)
  {
    int len = keywordEnd - lastOffset;
    if (len > 0)
    {
      byte id = keywords.lookup(line, lastOffset, len);
      addToken(len, id);
      lastOffset = keywordEnd;
    }
  }

  /**
   * Call addToken only if the length of the token is not 0.
   */
  private void guardedAddToken(int len, byte token)
  {
    if (len > 0) addToken(len, token);
  }
  
  /**
   * Return the keyword map.
   * It's lazily initialized on the first call.
   */
  public static KeywordMap getKeywords()
  {
    if (fortranKeywords == null)
    {
      fortranKeywords = new KeywordMap(false);
      
      // === Commands ===        
      fortranKeywords.add("CALL", Token.KEYWORD1);
      fortranKeywords.add("CLOSE", Token.KEYWORD1);
      fortranKeywords.add("CONTINUE", Token.KEYWORD1);
      fortranKeywords.add("DO", Token.KEYWORD1);
      fortranKeywords.add("ELSE", Token.KEYWORD1);
      fortranKeywords.add("ELSEIF", Token.KEYWORD1);
      fortranKeywords.add("ENDIF", Token.KEYWORD1);
      fortranKeywords.add("GOTO", Token.KEYWORD1);
      fortranKeywords.add("GO TO", Token.KEYWORD1);
      fortranKeywords.add("IF", Token.KEYWORD1);
      fortranKeywords.add("INDEX", Token.KEYWORD1);
      fortranKeywords.add("INQUIRE", Token.KEYWORD1);
      fortranKeywords.add("OPEN", Token.KEYWORD1);
      fortranKeywords.add("PRINT", Token.KEYWORD1);
      fortranKeywords.add("READ", Token.KEYWORD1);
      fortranKeywords.add("RETURN", Token.KEYWORD1);
      fortranKeywords.add("THEN", Token.KEYWORD1);
      fortranKeywords.add("WRITE", Token.KEYWORD1);

      // === Compiler directives ===
      fortranKeywords.add("BLOCK DATA", Token.KEYWORD2);
      fortranKeywords.add("COMPILER", Token.KEYWORD2);
      fortranKeywords.add("END", Token.KEYWORD2);
      fortranKeywords.add("ENTRY", Token.KEYWORD2);
      fortranKeywords.add("FUNCTION", Token.KEYWORD2);
      fortranKeywords.add("INCLUDE", Token.KEYWORD2);
      fortranKeywords.add("SUBROUTINE", Token.KEYWORD2);

      // === Data types (etc.) ===
      fortranKeywords.add("CHARACTER", Token.KEYWORD3);
      fortranKeywords.add("DATA", Token.KEYWORD3);
      fortranKeywords.add("DEFINE", Token.KEYWORD3);
      fortranKeywords.add("EQUIVALENCE", Token.KEYWORD3);
      fortranKeywords.add("IMPLICIT", Token.KEYWORD3);
      fortranKeywords.add("INTEGER", Token.KEYWORD3);
      fortranKeywords.add("LOGICAL", Token.KEYWORD3);
      fortranKeywords.add("PARAMETER", Token.KEYWORD3);
      fortranKeywords.add("REAL", Token.KEYWORD3);

      // === Operators ===        
      fortranKeywords.add(".AND.", Token.OPERATOR);
      fortranKeywords.add(".EQ.", Token.OPERATOR);
      fortranKeywords.add(".NE.", Token.OPERATOR);
      fortranKeywords.add(".NOT.", Token.OPERATOR);
      fortranKeywords.add(".OR.", Token.OPERATOR);
      fortranKeywords.add("+", Token.OPERATOR);
      fortranKeywords.add("-", Token.OPERATOR);
      fortranKeywords.add("*", Token.OPERATOR);
      fortranKeywords.add("**", Token.OPERATOR);
      fortranKeywords.add("/", Token.OPERATOR);

      // === Literals ===
      fortranKeywords.add(".FALSE.", Token.LITERAL2);
      fortranKeywords.add(".TRUE.", Token.LITERAL2);

    }
    return fortranKeywords;
  }
}

// End of FortranTokenMarker.java
