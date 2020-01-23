/*
 * TextTokenMarker.java - Text token marker
 * Copyright (C) 2000 Romain Guy
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */

package org.gjt.sp.jedit.syntax;

import javax.swing.text.Segment;

/**
 * Text token marker.
 */
 
public class TextTokenMarker extends TokenMarker
{
  public TextTokenMarker()
  {
  }

  public byte markTokensImpl(byte token, Segment line, int lineIndex)
  {
    char[] array = line.array;
    int offset = line.offset;
    int length = line.count + offset;

    int textLength = 0;
    boolean dot = true;

    for (int i = offset; i < length; i++)
    {
      char c = array[i];

      switch(c)
      {
        case '.': case '!': case '?':
          if (textLength != 0)
          {
            addToken(textLength, Token.NULL);
            textLength = 0;
          }
          addToken(1, Token.KEYWORD3);
          dot = true;
          break;
        case ':': case ';': case ',':
          if (textLength != 0)
          {
            addToken(textLength, Token.NULL);
            textLength = 0;
          }
          addToken(1, Token.KEYWORD1);
          dot = false;
          break;
        case '\'': case '\"': case '(': case ')':
        case '{': case '}': case '[': case ']':
          if (textLength != 0)
          {
            addToken(textLength, Token.NULL);
            textLength = 0;
          }
          addToken(1, Token.LITERAL1);
          dot = false;
          break;
        case '/': case '\\': case '+': case '=': case '-':
        case '*': case '%': case '^':
          if (textLength != 0)
          {
            addToken(textLength, Token.NULL);
            textLength = 0;
          }
          addToken(1, Token.OPERATOR);
          dot = false;
          break;
        default:
          if (Character.isLetter(c) && Character.isUpperCase(c) && dot)
          {
            if (textLength != 0)
            {
              addToken(textLength, Token.NULL);
              textLength = 0;
            }
            addToken(1, Token.COMMENT1);
          } else if (Character.isDigit(c)) {
            if (textLength != 0)
            {
              addToken(textLength, Token.NULL);
              textLength = 0;
            }
            addToken(1, Token.LABEL);
          } else
            textLength++;
          if (!Character.isWhitespace(c))
            dot = false;
      }
    }

    if (textLength != 0)
      addToken(textLength, Token.NULL);   
    return token;
  }
}

// End of TextTokenMarker.java
