/*
 * DawnTokenMarker.java - Dawn token marker
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

public class DawnTokenMarker extends TokenMarker
{
  // private members
  private static KeywordMap dawnKeywords;
  private KeywordMap keywords;

  private int lastOffset;
  private int lastKeyword;

  public DawnTokenMarker()
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
            case '#':
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
      case Token.LITERAL1:
      case Token.LITERAL2:
        addToken(length - lastOffset, Token.INVALID);
        token = Token.NULL;
        break;
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
    if (dawnKeywords == null)
    {
      dawnKeywords = new KeywordMap(false);
      dawnKeywords.add("do", Token.KEYWORD1);
      dawnKeywords.add("loop", Token.KEYWORD1);
      dawnKeywords.add("until", Token.KEYWORD1);
      dawnKeywords.add("for", Token.KEYWORD1);
      dawnKeywords.add("next", Token.KEYWORD1);
      dawnKeywords.add("if", Token.KEYWORD1);
      dawnKeywords.add("then", Token.KEYWORD1);
      dawnKeywords.add("else", Token.KEYWORD1);
      dawnKeywords.add("end", Token.KEYWORD1);
      dawnKeywords.add("while", Token.KEYWORD1);
      dawnKeywords.add("repeat", Token.KEYWORD1);
      dawnKeywords.add("wend", Token.KEYWORD1);
      dawnKeywords.add("try", Token.KEYWORD1);
      dawnKeywords.add("catch", Token.KEYWORD1);
      dawnKeywords.add("err", Token.KEYWORD1);
      dawnKeywords.add("exit", Token.KEYWORD2);
      dawnKeywords.add("needs", Token.KEYWORD2);
      dawnKeywords.add("needsGlobal", Token.KEYWORD2);
      dawnKeywords.add("array", Token.KEYWORD3);
      dawnKeywords.add("->", Token.KEYWORD3);
      dawnKeywords.add("->lit", Token.KEYWORD3);
      dawnKeywords.add("lit->", Token.KEYWORD3);
      dawnKeywords.add("->str", Token.KEYWORD3);
      dawnKeywords.add("str->", Token.KEYWORD3);
      dawnKeywords.add("sto", Token.KEYWORD3);
      dawnKeywords.add("rcl", Token.KEYWORD3);
      dawnKeywords.add("function", Token.KEYWORD3);
      dawnKeywords.add("endFunction", Token.KEYWORD3);
      dawnKeywords.add("global", Token.KEYWORD3);
      dawnKeywords.add("endGlobal", Token.KEYWORD3);
      dawnKeywords.add("e", Token.LITERAL2);
      dawnKeywords.add("pi", Token.LITERAL2);
      dawnKeywords.add("null", Token.LITERAL2);
      dawnKeywords.add("and", Token.OPERATOR);
      dawnKeywords.add("&", Token.OPERATOR);
      dawnKeywords.add("or", Token.OPERATOR);
      dawnKeywords.add("xor", Token.OPERATOR);
      dawnKeywords.add("|", Token.OPERATOR);
    }

    return dawnKeywords;
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

// End of DawnTokenMarker.java
