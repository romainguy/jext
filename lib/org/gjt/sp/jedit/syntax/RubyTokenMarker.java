/*
 * RubyTokenMarker.java - Python token marker
 * Copyright (C) 1999 Jonathan Revusky
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
 * Python token marker.
 *
 * @author Jonathan Revusky, Romain Guy
 * @version $Id: RubyTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:19 gfx Exp $
 */
public class RubyTokenMarker extends TokenMarker
{
  public RubyTokenMarker()
  {
    this.keywords = getKeywords();
  }

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
        case Token.NULL:
          switch (c)
          {
            case '(':
              if (backslash)
              {
                doKeyword(line, i, c);
                backslash = false;
              } else {
                if (doKeyword(line, i, c))
                  break;
                addToken(lastWhitespace - lastOffset + 1, token);
                addToken(i - lastWhitespace - 1, Token.METHOD);
                addToken(1, Token.NULL);
                token = Token.NULL;
                lastOffset = lastKeyword = i1;
                lastWhitespace = i;
              }
              break;
            case '#':
              if (backslash)
                backslash = false;
              else
              {
                doKeyword(line, i, c);
                addToken(i - lastOffset, token);
                addToken(length - i, Token.COMMENT1);
                lastOffset = lastKeyword = length;
                break loop;
              }
              break;
            case '"':
              doKeyword(line, i, c);
              if (backslash)
                backslash = false;
              else
              {
                addToken(i - lastOffset, token);
                token = Token.LITERAL1;
                lastOffset = lastKeyword = i;
              }
              break;
            case '\'':
              doKeyword(line, i, c);
              if (backslash)
                backslash = false;
              else
              {
                addToken(i - lastOffset, token);
                token = Token.LITERAL2;
                lastOffset = lastKeyword = i;
              }
              break;
            default:
              backslash = false;
              if (!Character.isLetterOrDigit(c) && c != '_')
                doKeyword(line, i, c);
              if (CTokenMarker.METHOD_DELIMITERS.indexOf(c) != -1)
              {
                lastWhitespace = i;
              }
              break;
          }
          break;
        case Token.LITERAL1:
          if (backslash)
            backslash = false;
          else if (c == '"')
          {
            addToken(i1 - lastOffset, token);
            token = Token.NULL;
            lastOffset = lastKeyword = i1;
          }
          break;
        case Token.LITERAL2:
          if (backslash)
            backslash = false;
          else if (c == '\'')
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

    switch (token)
    {
      case Token.LITERAL1:
      case Token.LITERAL2:
        addToken(length - lastOffset, Token.INVALID);
        token = Token.NULL;
        break;
      case Token.NULL:
        doKeyword(line, length, '\0');
      default:
        addToken(length - lastOffset, token);
        break;
    }

    return token;
  }

  public static KeywordMap getKeywords()
  {
    if (rubyKeywords == null)
    {
      rubyKeywords = new KeywordMap(false);
      rubyKeywords.add("__FILE__", Token.LABEL);
      rubyKeywords.add("and", Token.KEYWORD2);
      rubyKeywords.add("def", Token.KEYWORD1);
      rubyKeywords.add("end", Token.KEYWORD1);
      rubyKeywords.add("in", Token.KEYWORD1);
      rubyKeywords.add("or", Token.KEYWORD2);
      rubyKeywords.add("self", Token.LITERAL2);
      rubyKeywords.add("unless", Token.KEYWORD1);
      rubyKeywords.add("__LINE__", Token.LABEL);
      rubyKeywords.add("begin", Token.KEYWORD1);
      rubyKeywords.add("defined?", Token.KEYWORD1);
      rubyKeywords.add("ensure", Token.KEYWORD1);
      rubyKeywords.add("module", Token.KEYWORD3);
      rubyKeywords.add("require", Token.KEYWORD2);
      rubyKeywords.add("redo", Token.KEYWORD1);
      rubyKeywords.add("super", Token.LITERAL2);
      rubyKeywords.add("until", Token.KEYWORD1);
      rubyKeywords.add("BEGIN", Token.LABEL);
      rubyKeywords.add("break", Token.KEYWORD1);
      rubyKeywords.add("do", Token.KEYWORD1);
      rubyKeywords.add("false", Token.LITERAL2);
      rubyKeywords.add("next", Token.KEYWORD1);
      rubyKeywords.add("rescue", Token.KEYWORD1);
      rubyKeywords.add("then", Token.KEYWORD1);
      rubyKeywords.add("when", Token.KEYWORD1);
      rubyKeywords.add("END", Token.LABEL);
      rubyKeywords.add("case", Token.KEYWORD1);
      rubyKeywords.add("else", Token.KEYWORD1);
      rubyKeywords.add("for", Token.KEYWORD1);
      rubyKeywords.add("nil", Token.LITERAL2);
      rubyKeywords.add("retry", Token.KEYWORD1);
      rubyKeywords.add("true", Token.LITERAL2);
      rubyKeywords.add("while", Token.KEYWORD1);
      rubyKeywords.add("alias", Token.KEYWORD3);
      rubyKeywords.add("class", Token.KEYWORD3);
      rubyKeywords.add("elsif", Token.KEYWORD1);
      rubyKeywords.add("if", Token.KEYWORD1);
      rubyKeywords.add("not", Token.KEYWORD2);
      rubyKeywords.add("return", Token.KEYWORD1);
      rubyKeywords.add("undef", Token.KEYWORD1);
      rubyKeywords.add("yield", Token.KEYWORD1);
    }
    return rubyKeywords;
  }

  // private members
  private static KeywordMap rubyKeywords;

  private KeywordMap keywords;
  private int lastOffset;
  private int lastKeyword;
  private int lastWhitespace;

  private boolean doKeyword(Segment line, int i, char c)
  {
    int i1 = i + 1;

    int len = i - lastKeyword;
    byte id = keywords.lookup(line, lastKeyword, len);
    if (id != Token.NULL)
    {
      if (lastKeyword != lastOffset)
        addToken(lastKeyword - lastOffset, Token.NULL);
      addToken(len, id);
      lastOffset = i;
      lastWhitespace = i1;
      lastKeyword = i1;
      return true;
    }
    lastKeyword = i1;
    return false;
  }
}

/*
 * ChangeLog:
 * $Log: RubyTokenMarker.java,v $
 * Revision 1.1.1.1  2004/10/19 16:16:19  gfx
 * no message
 *
 * Revision 1.2  2001/11/16 21:00:46  gfx
 * Jext 3.0
 *
 * Revision 1.1  2001/11/16 18:12:45  gfx
 * Jext 3.0
 *
 * Revision 1.4  2001/09/29 11:45:28  gfx
 * Jext 3.0pre10 <Seldon>
 *
 * Revision 1.3  2001/09/26 22:11:43  gfx
 * Jext 3.0pre9
 */
