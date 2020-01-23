/*
 * LATTokenMarker.java - LAT token marker
 * Copyright (C) 2001 Romain Guy
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

public class LATTokenMarker extends TokenMarker
{
  // private members
  private static KeywordMap latKeywords;
  private KeywordMap keywords;

  private int lastOffset;
  private int lastKeyword;

  public LATTokenMarker()
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

loop: for (int i = offset; i < length; i++)
    {
      int i1 = (i + 1);

      char c = array[i];

      switch(token)
      {
        case Token.NULL:
          switch(c)
          {
            case '{':
              addToken(i - lastOffset, token);
              token = Token.COMMENT2;
              lastOffset = lastKeyword = i;
              break;
            case '/':
              doKeyword(line, i, c);
              if (length - i > 1 && array[i1] == '/')
              {
                addToken(i - lastOffset, token);
                addToken(length - i, Token.COMMENT1);
                lastOffset = lastKeyword = length;
                break loop;
              }
              break;
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
            case '#':
             addToken(i - lastOffset, token);
             addToken(1, Token.KEYWORD2);
             lastOffset = lastKeyword = i1;
             break;
           default:
            if (!Character.isLetterOrDigit(c) && c != '_')
						  doKeyword(line, i, c);
          }
          break;
        case Token.COMMENT2:
          if (c == '}')
          {
            addToken(i1 - lastOffset, token);
            token = Token.NULL;
            lastOffset = lastKeyword = i1;
          }
          break;
        case Token.LITERAL1:
          if (c == '"')
          {
            addToken(i1 - lastOffset, token);
            token = Token.NULL;
            lastOffset = lastKeyword = i1;
          }
          break;
        case Token.LITERAL2:
          if (c == '\'')
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
        addToken(length - lastOffset, Token.INVALID);
        token = Token.NULL;
        break;
      default:
        addToken(length - lastOffset, token);
        break;
    }

    return token;
  }

  public static KeywordMap getKeywords()
  {
    if (latKeywords == null)
    {
      latKeywords = new KeywordMap(true);
      latKeywords.add("specification", Token.OPERATOR);
      latKeywords.add("realisation", Token.OPERATOR);
      latKeywords.add("constantes", Token.OPERATOR);
      latKeywords.add("types", Token.OPERATOR);

      latKeywords.add("<-", Token.KEYWORD2);
      latKeywords.add("alors", Token.KEYWORD1);
      latKeywords.add("autrement", Token.KEYWORD1);
      latKeywords.add("booleen", Token.KEYWORD3);
      latKeywords.add("boucle", Token.KEYWORD1);
      latKeywords.add("caractere", Token.KEYWORD3);
      latKeywords.add("cas", Token.KEYWORD1);
      latKeywords.add("chaine", Token.KEYWORD3);
      latKeywords.add("dans", Token.KEYWORD1);
      latKeywords.add("de", Token.KEYWORD1);
      latKeywords.add("div", Token.KEYWORD2);
      latKeywords.add("ecrire", Token.KEYWORD1);
      latKeywords.add("ecrire_ligne", Token.KEYWORD1);
      latKeywords.add("enregistrement", Token.KEYWORD3);
      latKeywords.add("ensemble", Token.KEYWORD3);
      latKeywords.add("entier", Token.KEYWORD3);
      latKeywords.add("et", Token.KEYWORD2);
      latKeywords.add("etpuis", Token.KEYWORD2);
      latKeywords.add("faire", Token.KEYWORD1);
      latKeywords.add("fait", Token.KEYWORD1);
      latKeywords.add("faux", Token.LITERAL2);
      latKeywords.add("fin", Token.KEYWORD1);
      latKeywords.add("finselon", Token.KEYWORD1);
      latKeywords.add("finsi", Token.KEYWORD1);
      latKeywords.add("jusqua", Token.KEYWORD1);
      latKeywords.add("lire", Token.KEYWORD1);
      latKeywords.add("lire_ligne", Token.KEYWORD1);
      latKeywords.add("liste", Token.KEYWORD3);
      latKeywords.add("mod", Token.KEYWORD2);
      latKeywords.add("non", Token.KEYWORD2);
      latKeywords.add("ou", Token.KEYWORD2);
      latKeywords.add("oubien", Token.KEYWORD2);
      latKeywords.add("pour", Token.KEYWORD1);
      latKeywords.add("reel", Token.KEYWORD3);
      latKeywords.add("repeter", Token.KEYWORD1);
      latKeywords.add("retour", Token.KEYWORD1);
      latKeywords.add("retourne", Token.KEYWORD1);
      latKeywords.add("selon", Token.KEYWORD1);
      latKeywords.add("si", Token.KEYWORD1);
      latKeywords.add("sinon", Token.KEYWORD1);
      latKeywords.add("sortirsi", Token.KEYWORD1);
      latKeywords.add("tableau", Token.KEYWORD3);
      latKeywords.add("tantque", Token.KEYWORD1);
      latKeywords.add("vrai", Token.LITERAL2);
    }

    return latKeywords;
  }

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
    }
    lastKeyword = i1;
    return false;
  }
}

// End of LATTokenMarker.java
