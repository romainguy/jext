/*
 * SAP2000TokenMarker.java - SAP2000 token marker
 * Copyright (C) 2002 Romain Guy
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

public class SAP2000TokenMarker extends TokenMarker
{
  // private members
  private static KeywordMap sapKeywords;
  private KeywordMap keywords;

  private int lastOffset;
  private int lastKeyword;

  public SAP2000TokenMarker()
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
            case ';':
              addToken(i - lastOffset, token);
              addToken(length - i, Token.COMMENT1);
              token = Token.NULL;
              lastOffset = lastKeyword = length;
              break loop;
            case ' ':
              doKeyword(line, i, c);
              break;
            default:
              if (!Character.isLetterOrDigit(c))
                doKeyword(line, i, c);
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
      //case Token.LITERAL1:
      //case Token.LITERAL2:
      //  addToken(length - lastOffset, Token.INVALID);
      //  token = Token.NULL;
      //  break;
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
    if (sapKeywords == null)
    {
      sapKeywords = new KeywordMap(true);
      sapKeywords.add("SYSTEM", Token.LABEL);
      sapKeywords.add("COORDINATE", Token.LABEL);
      sapKeywords.add("JOINT", Token.LABEL);
      sapKeywords.add("JOINTS", Token.LABEL);
      sapKeywords.add("LOCAL", Token.LABEL);
      sapKeywords.add("RESTRAINT", Token.LABEL);
      sapKeywords.add("RESTRAINTS", Token.LABEL);
      sapKeywords.add("CONSTRAINT", Token.LABEL);
      sapKeywords.add("CONSTRAINTS", Token.LABEL);
      sapKeywords.add("WELD", Token.LABEL);
      sapKeywords.add("PATTERN", Token.LABEL);
      sapKeywords.add("SPRING", Token.LABEL);
      sapKeywords.add("MASS", Token.LABEL);
      sapKeywords.add("MASSES", Token.LABEL);
      sapKeywords.add("MATERIAL", Token.LABEL);
      sapKeywords.add("FRAME", Token.LABEL);
      sapKeywords.add("FRAMES", Token.LABEL);
      sapKeywords.add("SHELL", Token.LABEL);
      sapKeywords.add("SECTION", Token.LABEL);
      sapKeywords.add("SECTIONS", Token.LABEL);
      sapKeywords.add("NLPROP", Token.LABEL);
      sapKeywords.add("FRAME", Token.LABEL);
      sapKeywords.add("SHELL", Token.LABEL);
      sapKeywords.add("PLANE", Token.LABEL);
      sapKeywords.add("ASOLID", Token.LABEL);
      sapKeywords.add("SOLID", Token.LABEL);
      sapKeywords.add("NLLINK", Token.LABEL);
      sapKeywords.add("MATTEMP", Token.LABEL);
      sapKeywords.add("REFTEMP", Token.LABEL);
      sapKeywords.add("PRESTRESS", Token.LABEL);
      sapKeywords.add("LOAD", Token.LABEL);
      sapKeywords.add("LOADS", Token.LABEL);
      sapKeywords.add("PDFORCE", Token.LABEL);
      sapKeywords.add("PDELTA", Token.LABEL);
      sapKeywords.add("MODES", Token.LABEL);
      sapKeywords.add("FUNCTION", Token.LABEL);
      sapKeywords.add("SPEC", Token.LABEL);
      sapKeywords.add("HISTORY", Token.LABEL);
      sapKeywords.add("LANE", Token.LABEL);
      sapKeywords.add("VEHICLE", Token.LABEL);
      sapKeywords.add("VEHICLE", Token.LABEL);
      sapKeywords.add("CLASS", Token.LABEL);
      sapKeywords.add("RESPONSE", Token.LABEL);
      sapKeywords.add("BRIDGE", Token.LABEL);
      sapKeywords.add("MOVING", Token.LABEL);
      sapKeywords.add("COMBO", Token.LABEL);
      sapKeywords.add("OUTPUT", Token.LABEL);
      sapKeywords.add("END", Token.LABEL);

      sapKeywords.add("NAME", Token.KEYWORD1);
      sapKeywords.add("TYPE", Token.KEYWORD1);
      sapKeywords.add("IDES", Token.KEYWORD1);
      sapKeywords.add("MAT", Token.KEYWORD1);
      sapKeywords.add("MATANG", Token.KEYWORD1);
      sapKeywords.add("TH", Token.KEYWORD1);
      sapKeywords.add("GEN", Token.KEYWORD1);
      sapKeywords.add("LGEN", Token.KEYWORD1);
      sapKeywords.add("FGEN", Token.KEYWORD1);
      sapKeywords.add("EGEN", Token.KEYWORD1);
      sapKeywords.add("CGEN", Token.KEYWORD1);
      sapKeywords.add("DEL", Token.KEYWORD1);
      sapKeywords.add("ADD", Token.KEYWORD1);
      sapKeywords.add("REM", Token.KEYWORD1);
      sapKeywords.add("ELEM", Token.KEYWORD1);
      sapKeywords.add("FACE", Token.KEYWORD1);
      sapKeywords.add("CSYS", Token.KEYWORD1);
      sapKeywords.add("AXDIR", Token.KEYWORD1);
      sapKeywords.add("PLDIR", Token.KEYWORD1);
      sapKeywords.add("LOCAL", Token.KEYWORD1);
      sapKeywords.add("SW", Token.KEYWORD1);

      sapKeywords.add("DOF", Token.KEYWORD2);
      sapKeywords.add("LENGTH", Token.KEYWORD2);
      sapKeywords.add("FORCE", Token.KEYWORD2);
      sapKeywords.add("UP", Token.KEYWORD2);
      sapKeywords.add("CYC", Token.KEYWORD2);
      sapKeywords.add("WARN", Token.KEYWORD2);
      sapKeywords.add("PAGE", Token.KEYWORD2);
      sapKeywords.add("LINES", Token.KEYWORD2);
      sapKeywords.add("LMAP", Token.KEYWORD2);
      sapKeywords.add("FMAP", Token.KEYWORD2);
      sapKeywords.add("NLP", Token.KEYWORD2);
      sapKeywords.add("AXVEC", Token.KEYWORD2);
      sapKeywords.add("PLVEC", Token.KEYWORD2);
      sapKeywords.add("ANG", Token.KEYWORD2);
      sapKeywords.add("ZERO", Token.KEYWORD2);
      sapKeywords.add("UX", Token.KEYWORD2);
      sapKeywords.add("UY", Token.KEYWORD2);
      sapKeywords.add("UZ", Token.KEYWORD2);
      sapKeywords.add("RX", Token.KEYWORD2);
      sapKeywords.add("RY	", Token.KEYWORD2);
      sapKeywords.add("RZ", Token.KEYWORD2);
      sapKeywords.add("U1", Token.KEYWORD2);
      sapKeywords.add("U2", Token.KEYWORD2);
      sapKeywords.add("U3", Token.KEYWORD2);
      sapKeywords.add("R1", Token.KEYWORD2);
      sapKeywords.add("R2", Token.KEYWORD2);
      sapKeywords.add("R3", Token.KEYWORD2);
      sapKeywords.add("RD", Token.KEYWORD2);
      sapKeywords.add("PAT", Token.KEYWORD2);

      sapKeywords.add("FORCE", Token.KEYWORD3);
      sapKeywords.add("RESTRAINT", Token.KEYWORD3);
      sapKeywords.add("SPRING", Token.KEYWORD3);
      sapKeywords.add("DISPLACEMENT", Token.KEYWORD3);
      sapKeywords.add("GRAVITY", Token.KEYWORD3);
      sapKeywords.add("CONCENTRATED", Token.KEYWORD3);
      sapKeywords.add("SPAN", Token.KEYWORD3);
      sapKeywords.add("DISTRIBUTED", Token.KEYWORD3);
      sapKeywords.add("PRESTRESS", Token.KEYWORD3);
      sapKeywords.add("UNIFORM", Token.KEYWORD3);
      sapKeywords.add("SURVACE", Token.KEYWORD3);
      sapKeywords.add("PORE", Token.KEYWORD3);
      sapKeywords.add("PRESSURE", Token.KEYWORD3);
      sapKeywords.add("TEMPERATURE", Token.KEYWORD3);
      sapKeywords.add("ROTATE", Token.KEYWORD3);
    }

    return sapKeywords;
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

// End of SAP2000TokenMarker.java
