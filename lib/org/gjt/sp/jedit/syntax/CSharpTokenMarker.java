/*
 * CSharpTokenMarker.java - C# token marker
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

public class CSharpTokenMarker extends TokenMarker
{
  public static final byte VERBATIM_STRING = Token.INTERNAL_FIRST + 1;

  public CSharpTokenMarker()
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
    lastWhitespace = offset - 1;
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
                if (doKeyword(line, i, c))
                  break;
                addToken(i - lastOffset, token);
                addToken(length - i, Token.KEYWORD2);
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
            case '@':
              if (length - i > 1 && array[i1] == '"')
              {
                addToken(i - lastOffset, token);
                token = VERBATIM_STRING; //Token.LITERAL1;
                lastOffset = lastKeyword = i;
                i++;
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
            case ':':
              if (lastKeyword == offset)
              {
                if (doKeyword(line, i, c))
                  break;
                backslash = false;
                addToken(i1 - lastOffset, Token.LABEL);
                lastOffset = lastKeyword = i1;
              }
              else if (doKeyword(line, i, c))
                break;
              break;
            case '/':
              backslash = false;
              doKeyword(line, i, c);
              if (length - i > 1)
              {
                switch (array[i1])
                {
                  case '*':
                    addToken(i - lastOffset, token);
                    lastOffset = lastKeyword = i;
                    if (length - i > 2 && array[i + 2] == '*')
                      token = Token.COMMENT2;
                    else
                      token = Token.COMMENT1;
                    break;
                  case '/':
                    addToken(i - lastOffset, token);
                    if (length - i > 2)
                      addToken(length - i, Token.COMMENT2);
                    else
                      addToken(length - i, Token.COMMENT1);
                    lastOffset = lastKeyword = length;
                    break loop;
                }
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
        case Token.COMMENT1:
        case Token.COMMENT2:
          backslash = false;
          if (c == '*' && length - i > 1)
          {
            if (array[i1] == '/')
            {
              i++;
              addToken((i + 1) - lastOffset, token);
              token = Token.NULL;
              lastOffset = lastKeyword = i + 1;
              lastWhitespace = i;
            }
          }
          break;
        case VERBATIM_STRING:
          if (backslash)
            backslash = false;
          else if (c == '"')
          {
            addToken(i1 - lastOffset, Token.LITERAL1);
            token = Token.NULL;
            lastOffset = lastKeyword = i1;
            lastWhitespace = i;
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
            lastWhitespace = i;
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
            lastWhitespace = i;
          }
          break;
        default:
          throw new InternalError("Invalid state: " + token);
      }
    }

    if (token == Token.NULL)
      doKeyword(line, length, '\0');

    switch (token)
    {
      case VERBATIM_STRING:
        addToken(length - lastOffset, Token.LITERAL1);
        break;
      case Token.LITERAL1:
      case Token.LITERAL2:
        addToken(length - lastOffset, Token.INVALID);
        token = Token.NULL;
        break;
      case Token.KEYWORD2:
        addToken(length - lastOffset, token);
        if (!backslash)
          token = Token.NULL;
      default:
        addToken(length - lastOffset, token);
        break;
    }

    return token;
  }

  public static KeywordMap getKeywords()
  {
    if (cKeywords == null)
    {
      cKeywords = new KeywordMap(false);
      cKeywords.add("abstract", Token.KEYWORD1);
      cKeywords.add("as", Token.KEYWORD1);
      cKeywords.add("base", Token.KEYWORD1);
      cKeywords.add("break", Token.KEYWORD1);
      cKeywords.add("case", Token.KEYWORD1);
      cKeywords.add("catch", Token.KEYWORD1);
      cKeywords.add("checked", Token.KEYWORD1);
      cKeywords.add("const", Token.KEYWORD1);
      cKeywords.add("continue", Token.KEYWORD1);
      cKeywords.add("decimal", Token.KEYWORD1);
      cKeywords.add("default", Token.KEYWORD1);
      cKeywords.add("delegate", Token.KEYWORD1);
      cKeywords.add("do", Token.KEYWORD1);
      cKeywords.add("else", Token.KEYWORD1);
      cKeywords.add("explicit", Token.KEYWORD1);
      cKeywords.add("extern", Token.KEYWORD1);
      cKeywords.add("finally", Token.KEYWORD1);
      cKeywords.add("fixed", Token.KEYWORD1);
      cKeywords.add("for", Token.KEYWORD1);
      cKeywords.add("foreach", Token.KEYWORD1);
      cKeywords.add("get", Token.KEYWORD1);
      cKeywords.add("goto", Token.KEYWORD1);
      cKeywords.add("if", Token.KEYWORD1);
      cKeywords.add("implicit", Token.KEYWORD1);
      cKeywords.add("in", Token.KEYWORD1);
      cKeywords.add("internal", Token.KEYWORD1);
      cKeywords.add("is", Token.KEYWORD1);
      cKeywords.add("lock", Token.KEYWORD1);
      cKeywords.add("new", Token.KEYWORD1);
      cKeywords.add("operator", Token.KEYWORD1);
      cKeywords.add("out", Token.KEYWORD1);
      cKeywords.add("override", Token.KEYWORD1);
      cKeywords.add("params", Token.KEYWORD1);
      cKeywords.add("private", Token.KEYWORD1);
      cKeywords.add("protected", Token.KEYWORD1);
      cKeywords.add("public", Token.KEYWORD1);
      cKeywords.add("readonly", Token.KEYWORD1);
      cKeywords.add("ref", Token.KEYWORD1);
      cKeywords.add("return", Token.KEYWORD1);
      cKeywords.add("sealed", Token.KEYWORD1);
      cKeywords.add("set", Token.KEYWORD1);
      cKeywords.add("sizeof", Token.KEYWORD1);
      cKeywords.add("stackalloc", Token.KEYWORD1);
      cKeywords.add("static", Token.KEYWORD1);
      cKeywords.add("switch", Token.KEYWORD1);
      cKeywords.add("throw", Token.KEYWORD1);
      cKeywords.add("try", Token.KEYWORD1);
      cKeywords.add("typeof", Token.KEYWORD1);
      cKeywords.add("unchecked", Token.KEYWORD1);
      cKeywords.add("unsafe", Token.KEYWORD1);
      cKeywords.add("virtual", Token.KEYWORD1);
      cKeywords.add("while", Token.KEYWORD1);

      cKeywords.add("using", Token.KEYWORD2);
      cKeywords.add("namespace", Token.KEYWORD2);

      cKeywords.add("bool", Token.KEYWORD3);
      cKeywords.add("byte", Token.KEYWORD3);
      cKeywords.add("char", Token.KEYWORD3);
      cKeywords.add("class", Token.KEYWORD3);
      cKeywords.add("double", Token.KEYWORD3);
      cKeywords.add("enum", Token.KEYWORD3);
      cKeywords.add("event", Token.KEYWORD3);
      cKeywords.add("float", Token.KEYWORD3);
      cKeywords.add("int", Token.KEYWORD3);
      cKeywords.add("interface", Token.KEYWORD3);
      cKeywords.add("long", Token.KEYWORD3);
      cKeywords.add("object", Token.KEYWORD3);
      cKeywords.add("sbyte", Token.KEYWORD3);
      cKeywords.add("short", Token.KEYWORD3);
      cKeywords.add("string", Token.KEYWORD3);
      cKeywords.add("struct", Token.KEYWORD3);
      cKeywords.add("uint", Token.KEYWORD3);
      cKeywords.add("ulong", Token.KEYWORD3);
      cKeywords.add("ushort", Token.KEYWORD3);
      cKeywords.add("void", Token.KEYWORD3);

      cKeywords.add("false", Token.LITERAL2);
      cKeywords.add("null", Token.LITERAL2);
      cKeywords.add("this", Token.LITERAL2);
      cKeywords.add("true", Token.LITERAL2);
    }
    return cKeywords;
  }

  // private members
  private static KeywordMap cKeywords;

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
      lastKeyword = i1;
      lastWhitespace = i;
      return true;
    }
    lastKeyword = i1;
    return false;
  }
}

// End of CSharpTokenMarker.jav
