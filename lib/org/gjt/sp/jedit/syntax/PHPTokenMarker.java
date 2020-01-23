/*
 * PHPTokenMarker.java - Token marker for PHP
 * Copyright (C) 1999 Clancy Malcolm
 * Copyright (C) 2001 Romain Guy
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
 * PHP token marker.
 *
 * @author Clancy Malcolm
 * @version $Id: PHPTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:21 gfx Exp $
 */
public class PHPTokenMarker extends TokenMarker
{
  public static final byte SCRIPT = Token.INTERNAL_FIRST + 1;
  public static final byte HTML_LITERAL_QUOTE = Token.INTERNAL_FIRST + 2;
  public static final byte HTML_LITERAL_NO_QUOTE = Token.INTERNAL_FIRST + 3;
  public static final byte INSIDE_TAG = Token.INTERNAL_FIRST + 4;
  public static final byte PHP_VARIABLE = Token.INTERNAL_FIRST + 5;

  public byte markTokensImpl(byte token, Segment line, int lineIndex)
  {
    char[] array = line.array;
    int offset = line.offset;
    lastOffset = offset;
    lastKeyword = offset;
    lastWhitespace = offset - 1;
    int length = line.count + offset;
    boolean backslash = false;

    loop:
    for (int i = offset; i < length; i++)
    {
      int i1 = (i + 1);

      char c = array[i];
      if (c == '\\')
      {
        backslash = !backslash;
        continue;
      }

      switch (token)
      {
        case Token.NULL: // HTML text
          backslash = false;
          switch (c)
          {
            case '<':
              addToken(i - lastOffset, token);
              lastOffset = lastKeyword = i;
              if (SyntaxUtilities.regionMatches(false, line, i1, "!--"))
              {
                i += 3;
                token = Token.COMMENT1;
              } else if (SyntaxUtilities.regionMatches(true, line, i1, "?php")) {
                addToken(1, Token.KEYWORD1, true);
                addToken(4, Token.LABEL, true);
                lastOffset = lastKeyword = (i += 4) + 1;
                lastWhitespace = lastOffset - 1;
                token = SCRIPT;
              } else if (SyntaxUtilities.regionMatches(true, line, i1, "?")) {
                addToken(1, Token.KEYWORD1, true);
                addToken(1, Token.LABEL, true);
                lastOffset = lastKeyword = (i += 1) + 1;
                lastWhitespace = lastOffset - 1;
                token = SCRIPT;
              } else if (SyntaxUtilities.regionMatches(true, line, i1, "script")) {
                //addToken(1, Token.KEYWORD1);
                //addToken(6, Token.LABEL);
                //addToken(1, Token.KEYWORD1);
                //lastOffset = lastKeyword = (i += 7) + 1;
                //token = SCRIPT;
                addToken(1, Token.KEYWORD1);
                lastOffset = lastKeyword = i1;
                token = Token.METHOD;//SCRIPT;
                script = true;
              } else {
                addToken(1, Token.KEYWORD1);
                lastOffset = lastKeyword = i1;
                token = Token.METHOD;
              }
              break;
            case '&':
              addToken(i - lastOffset, token);
              lastOffset = lastKeyword = i;
              token = Token.KEYWORD2;
              break;
          }
          break;
        case Token.OPERATOR:
          backslash = false;
          if (c != '<')
          {
            addToken(i1 - lastOffset, token);
            lastOffset = lastKeyword = i1;
            token = Token.NULL;
          }
          break;
        case Token.METHOD: // Inside a tag
          backslash = false;
          if (c == '>')
          {
            addToken(i - lastOffset, token);
            addToken(1, Token.KEYWORD1);
            lastOffset = lastKeyword = i1;
            if (!script)
              token = Token.NULL;
            else
            {
              script = false;
              lastWhitespace = i;
              token = SCRIPT;
            }
          } else if (c == ':') {
            addToken(i1 - lastOffset, Token.LITERAL2);
            lastOffset = lastKeyword = i1;
          } else if (c == ' ' || c == '\t') {
            addToken(i1 - lastOffset, token);
            lastOffset = lastKeyword = i1;
            token = INSIDE_TAG; //Token.KEYWORD3;
          }
          break;
        case INSIDE_TAG:
          if (c == '>')
          {
            addToken(i - lastOffset, Token.METHOD);
            addToken(1, Token.KEYWORD1);
            lastOffset = lastKeyword = i1;
            if (!script)
              token = Token.NULL;
            else
            {
              script = false;
              lastWhitespace = i;
              token = SCRIPT;
            }
          } else if (c == '/' || c == '?') {
            addToken(1, Token.METHOD);
            lastOffset = lastKeyword = i1;
            token = Token.METHOD;
          } else {//if (c != ' ' && c != '\t') {
            addToken(i - lastOffset, Token.NULL);
            lastOffset = lastKeyword = i;
            token = Token.KEYWORD3;
          }
          break;
        case Token.KEYWORD2: // Inside an entity
          backslash = false;
          if (c == ';')
          {
            addToken(i1 - lastOffset, token);
            lastOffset = lastKeyword = i1;
            token = Token.NULL;
          }
          break;
        case Token.KEYWORD3: // Inside an attribute
          if (c == '/' || c == '?')
          {
            addToken(i - lastOffset, token);
            addToken(1, Token.METHOD);
            lastOffset = lastKeyword = i1;
            //token = INSIDE_TAG;
          } else if (c == '=') {
            addToken(i - lastOffset, token);
            addToken(1, Token.LABEL);
            lastOffset = lastKeyword = i1;
            if (i1 < array.length && array[i1] == '"')
            {
              token = HTML_LITERAL_QUOTE;
              i++;
            } else {
              token = HTML_LITERAL_NO_QUOTE;
            }
          } else if (c == '>') {
            addToken(i - lastOffset, token);
            addToken(1, Token.KEYWORD1);
            lastOffset = lastKeyword = i1;
            token = Token.NULL;
          } else if (c == ' ' || c == '\t') {
            addToken(i1 - lastOffset, token);
            lastOffset = lastKeyword = i1;
            token = INSIDE_TAG;
          }
          break;
        case HTML_LITERAL_QUOTE:
          if (c == '"')
          {
            addToken(i1 - lastOffset, Token.LITERAL1);
            lastOffset = lastKeyword = i1;
            token = INSIDE_TAG;
          }
          break;
        case HTML_LITERAL_NO_QUOTE:
          if (c == ' ' || c == '\t')
          {
            addToken(i1 - lastOffset, Token.LITERAL1);
            lastOffset = lastKeyword = i1;
            token = INSIDE_TAG;
          } else if (c == '>') {
            addToken(i - lastOffset, Token.LITERAL1);
            addToken(1, Token.KEYWORD1);
            lastOffset = lastKeyword = i1;
            token = Token.NULL;
          }
          break;
        case Token.COMMENT1: // Inside a comment
          backslash = false;
          if (SyntaxUtilities.regionMatches(false, line, i, "-->"))
          {
            addToken((i + 3) - lastOffset, token);
            lastOffset = lastKeyword = i + 3;
            token = Token.NULL;
          }
          break;
        case SCRIPT: // Inside a JavaScript or PHP
          switch (c)
          {
            case '<':
              backslash = false;
              if (!doKeyword(line, i, c))
                addToken(i - lastOffset, token, true);
              if (SyntaxUtilities.regionMatches(true, line, i1, "/script>"))
              {
                addToken(1, Token.KEYWORD1);
                addToken(7, Token.METHOD);
                addToken(1, Token.KEYWORD1);
                //addToken(9, Token.LABEL);
                lastOffset = lastKeyword = (i += 8) + 1;
                token = Token.NULL;
              } else if (SyntaxUtilities.regionMatches(true, line, i1, "<<HERE")) {
                addToken(7, Token.COMMENT2);
                lastOffset = lastKeyword = (i += 6) + 1;
              } else {
                addToken(1, Token.OPERATOR, true);
                lastOffset = lastKeyword = i1;
              }
              break;
            case '?':
              backslash = false;
              if (!doKeyword(line, i, c))
                addToken(i - lastOffset, token, true);
              if (SyntaxUtilities.regionMatches(true, line, i1, ">"))
              {
                //Ending the script
                addToken(1, Token.LABEL, true);
                addToken(1, Token.KEYWORD1, true);
                lastOffset = lastKeyword = (++i) + 1;
                lastWhitespace = lastOffset - 1;
                token = Token.NULL;
              } else {
                //? operator
                addToken(1, Token.OPERATOR, true);
                lastOffset = lastKeyword = i1;
                lastWhitespace = i;
              }
              break;
            case '(':
              if (backslash)
              {
                doKeyword(line, i, c);
                backslash = false;
              } else {
                if (!doKeyword(line, i, c))
                {
                  addToken(lastWhitespace - lastOffset + 1, token, true);
                  addToken(i - lastWhitespace - 1, Token.METHOD, true);
                }
                addToken(1, Token.OPERATOR, true);
                token = SCRIPT;
                lastOffset = lastKeyword = i1;
                lastWhitespace = i;
              }
              break;
            case '"':
              doKeyword(line, i, c);
              if (backslash)
                backslash = false;
              else
              {
                addToken(i - lastOffset, token, true);
                lastOffset = lastKeyword = i;
                token = Token.LITERAL1;
              }
              break;
            case '\'':
              doKeyword(line, i, c);
              if (backslash)
                backslash = false;
              else
              {
                addToken(i - lastOffset, token, true);
                lastOffset = lastKeyword = i;
                token = Token.LITERAL2;
              }
              break;
            case '#':
              if (doKeyword(line, i, c))
                break;
              addToken(i - lastOffset, token, true);
              addToken(length - i, Token.COMMENT2, true);
              lastOffset = lastKeyword = length;
              break loop;
            case '/':
              backslash = false;
              doKeyword(line, i, c);
              if (length - i > 1)/*This is the same as if(length > i + 1) */

              {
                if (array[i1] == '/')
                {
                  addToken(i - lastOffset, token, true);
                  addToken(length - i, Token.COMMENT1, true);
                  lastOffset = lastKeyword = length;
                  break loop;
                } else if (array[i1] == '*') {
                  addToken(i - lastOffset, token, true);
                  lastOffset = lastKeyword = i;
                  token = Token.COMMENT2;
                } else {
                  // / operator
                  addToken(i - lastOffset, token, true);
                  addToken(1, Token.OPERATOR, true);
                  lastOffset = lastKeyword = i1;
                }
              } else {
                // / operator
                doKeyword(line, i, c);
                addToken(1, Token.OPERATOR, true);
                lastOffset = lastKeyword = i1;
              }
              break;
            case '$':
              doKeyword(line, i, c);
              if (backslash)
                backslash = false;
              else
              {
                addToken(i - lastOffset, token, true);
                token = PHP_VARIABLE;
                lastOffset = lastKeyword = i;
              }
              break;
            default:
              backslash = false;
              if (!Character.isLetterOrDigit(c) && c != '_')// && c != '$')
              {
                doKeyword(line, i, c);
                if (CTokenMarker.METHOD_DELIMITERS.indexOf(c) != -1)
                  lastWhitespace = i;

                if (c != ' ')
                {
                  addToken(i - lastOffset, token, true);
                  addToken(1, Token.OPERATOR, true);
                  lastOffset = lastKeyword = i1;
                }
              }
              break;
          }
          break;
        case PHP_VARIABLE:
          if (!Character.isLetterOrDigit(c) && c != '_')
          {
            addToken(i - lastOffset, Token.LITERAL2, true);
            addToken(1, Token.OPERATOR, true);
            lastOffset = lastKeyword = i1;
            lastWhitespace = i;
            token = SCRIPT;
          }
          break;
        case Token.LITERAL1: // Script "..."
          if (backslash)
            backslash = false;
          else if (c == '"')
          {
            addToken(i1 - lastOffset, Token.LITERAL1, true);
            lastOffset = lastKeyword = i1;
            lastWhitespace = i;
            token = SCRIPT;
          }
          break;
        case Token.LITERAL2: // Script '...'
          if (backslash)
            backslash = false;
          else if (c == '\'')
          {
            addToken(i1 - lastOffset, Token.LITERAL2, true);
            lastOffset = lastKeyword = i1;
            lastWhitespace = i;
            token = SCRIPT;
          }
          break;
        case Token.COMMENT2: // Inside a Script comment
          backslash = false;
          if (c == '*' && length - i > 1 && array[i1] == '/')
          {
            addToken(i + 2 - lastOffset, Token.COMMENT2, true);
            i += 1;
            lastOffset = lastKeyword = i + 1;
            lastWhitespace = i;
            token = SCRIPT;
          }
          break;
        default:
          throw new InternalError("Invalid state: " + token);
      }
    }

    switch (token)
    {
      case Token.LITERAL1:
        addToken(length - lastOffset, Token.LITERAL1);
        break;
      case Token.LITERAL2:
        addToken(length - lastOffset, Token.LITERAL2);
        break;
      case Token.KEYWORD2:
        addToken(length - lastOffset, Token.INVALID);
        token = Token.NULL;
        break;
      case SCRIPT:
        doKeyword(line, length, '\0');
        addToken(length - lastOffset, Token.NULL, true);
        break;
      case Token.COMMENT2:
        addToken(length - lastOffset, Token.COMMENT1);
        break;
      case INSIDE_TAG:
        break;
      case HTML_LITERAL_QUOTE: case HTML_LITERAL_NO_QUOTE:
        addToken(length - lastOffset, Token.LITERAL1);
        break;
      case PHP_VARIABLE:
        addToken(length - lastOffset, Token.KEYWORD3, true);
        token = SCRIPT;
        break;
      case Token.METHOD: case Token.OPERATOR:
        addToken(length - lastOffset, token, true);
        token = SCRIPT;
        break;
      default:
        addToken(length - lastOffset, token);
        break;
    }

    return token;
  }

  // private members
  private static KeywordMap keywords;
  private int lastOffset;
  private int lastKeyword;
  private int lastWhitespace;
  private boolean script = false;

  static
  {
    keywords = new KeywordMap(false);
    keywords.add("function", Token.KEYWORD2);
    keywords.add("class", Token.KEYWORD2);
    keywords.add("var", Token.KEYWORD2);
    keywords.add("global", Token.KEYWORD2);
    keywords.add("require", Token.KEYWORD2);
    keywords.add("require_once", Token.KEYWORD2);
    keywords.add("include", Token.KEYWORD2);
    keywords.add("include_once", Token.KEYWORD2);
    keywords.add("and", Token.KEYWORD1);
    keywords.add("or", Token.KEYWORD1);
    keywords.add("else", Token.KEYWORD1);
    keywords.add("elseif", Token.KEYWORD1);
    keywords.add("do", Token.KEYWORD1);
    keywords.add("as", Token.KEYWORD1);
    keywords.add("for", Token.KEYWORD1);
    keywords.add("foreach", Token.KEYWORD1);
    keywords.add("if", Token.KEYWORD1);
    keywords.add("endif", Token.KEYWORD1);
    keywords.add("in", Token.KEYWORD1);
    keywords.add("new", Token.KEYWORD1);
    keywords.add("return", Token.KEYWORD1);
    keywords.add("while", Token.KEYWORD1);
    keywords.add("endwhile", Token.KEYWORD1);
    keywords.add("with", Token.KEYWORD1);
    keywords.add("break", Token.KEYWORD1);
    keywords.add("switch", Token.KEYWORD1);
    keywords.add("case", Token.KEYWORD1);
    keywords.add("continue", Token.KEYWORD1);
    keywords.add("default", Token.KEYWORD1);
    keywords.add("echo", Token.KEYWORD1);
    keywords.add("false", Token.KEYWORD1);
    keywords.add("this", Token.KEYWORD1);
    keywords.add("true", Token.KEYWORD1);
    keywords.add("array", Token.KEYWORD1);
    keywords.add("extends", Token.KEYWORD1);
  }

  protected void addToken(int i, byte id)
  {
    addToken(i, id, false);
  }

  protected void addToken(int i, byte id, boolean highlighBackground)
  {
    if (id == SCRIPT)
      id = Token.NULL;
    super.addToken(i, id, highlighBackground);
  }
  
  private boolean doKeyword(Segment line, int i, char c)
  {
    int i1 = i + 1;

    int len = i - lastKeyword;
    byte id = keywords.lookup(line, lastKeyword, len);
    if (id != Token.NULL)
    {
      if (lastKeyword != lastOffset)
        addToken(lastKeyword - lastOffset, Token.NULL, true);
      addToken(len, id, true);
      lastOffset = i;
      lastKeyword = i1;
      lastWhitespace = i;
      return true;
    }
    lastKeyword = i1;
    return false;
  }
}

/*
  * ChangeLog:
  * $Log: PHPTokenMarker.java,v $
  * Revision 1.1.1.1  2004/10/19 16:16:21  gfx
  * no message
  *
  * Revision 1.11  2003/06/30 17:31:10  blaisorblade
  * Fix for line-ends.
  *
  * Revision 1.10  2003/06/29 13:37:27  gfx
  * Support of JDK 1.4.2
  *
  * Revision 1.9  2003/02/26 21:20:41  gfx
  * New PHP highlighting feature
  *
  * Revision 1.8  2002/05/13 17:37:21  gfx
  * *** empty log message ***
  *
  */