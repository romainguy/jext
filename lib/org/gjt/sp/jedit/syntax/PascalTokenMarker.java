/*
 * PascalTokenMarker.java - Pascal token marker
 * Copyright (C) 2002 Romain Guy
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
 * along with this program; if not,  write to the Free Software
 * Foundation,  Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.gjt.sp.jedit.syntax;

import javax.swing.text.Segment;

/**
 * Pascal token marker.
 */
public class PascalTokenMarker extends TokenMarker
{
  public static final String METHOD_DELIMITERS = " \t~!%^*()-+=|\\#/{}[]:;\"'<>,.?";

  public PascalTokenMarker()
  {
    this.keywords = getKeywords();
  }

  public byte markTokensImpl(byte token,  Segment line,  int lineIndex)
  {
    char[] array = line.array;
    int offset = line.offset;
    lastOffset = offset;
    lastKeyword = offset;
    lastWhitespace = offset - 1;
    int length = line.count + offset;
    boolean backslash = false;

loop:   for (int i = offset; i < length; i++)
    {
      int i1 = (i + 1);

      char c = array[i];
      if (c == '\\')
      {
        backslash = !backslash;
        continue;
      }

out:  switch(token)
      {
        case Token.NULL:
          switch(c)
          {
          case '(':
            if (backslash)
            {
              doKeyword(line, i, c);
              backslash = false;
            } else {
              boolean k = doKeyword(line, i, c);
              if(length - i > 1)
              {
                switch(array[i1])
                {
                  case '*':
                    addToken(i - lastOffset, token);
                    token = Token.COMMENT2;
                    lastOffset = lastKeyword = i;
                    break out;
                }
              }
              if (k)
                break;
              addToken(lastWhitespace - lastOffset + 1, token);
              addToken(i - lastWhitespace - 1, Token.METHOD);
              addToken(1, Token.NULL);
              token = Token.NULL;
              lastOffset = lastKeyword = i1;
              lastWhitespace = i;
            }
            break;
          case '{':
            backslash = false;
            doKeyword(line, i, c);
            addToken(i - lastOffset, token);
            token = Token.COMMENT1;
            lastOffset = lastKeyword = i1;
            break;
          case '\'':
            doKeyword(line, i, c);
            if(backslash)
              backslash = false;
            else
            {
              addToken(i - lastOffset, token);
              token = Token.LITERAL1;
              lastOffset = lastKeyword = i;
            }
            break;
          default:
            backslash = false;
            if (!Character.isLetterOrDigit(c) && c != '_' && c != '{' && c != '}')
              doKeyword(line, i, c);
            if (METHOD_DELIMITERS.indexOf(c) != -1)
            {
              lastWhitespace = i;
            }
            break;
          }
          break;
        case Token.COMMENT1:
          backslash = false;
          if (c == '}')
          {
            addToken((i + 1) - lastOffset, token);
            token = Token.NULL;
            lastOffset = lastKeyword = i;
            lastWhitespace = i;
          }
          break;
        case Token.COMMENT2:
          backslash = false;
          if (c == '*' && length - i > 1)
          {
            if (array[i1] == ')')
            {
              i++;
              addToken((i + 1) - lastOffset, token);
              token = Token.NULL;
              lastOffset = lastKeyword = i + 1;
              lastWhitespace = i;
            }
          }
          break;
        case Token.LITERAL1:
          if (backslash)
            backslash = false;
          else if (c == '\'')
          {
            addToken(i1 - lastOffset, token);
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
    if (pascalKeywords == null)
    {
      pascalKeywords = new KeywordMap(false);
      pascalKeywords.add("absolute", Token.KEYWORD1);
      pascalKeywords.add("and", Token.OPERATOR);
      pascalKeywords.add("array", Token.KEYWORD3);
      pascalKeywords.add("asm", Token.KEYWORD1);
      pascalKeywords.add("begin", Token.KEYWORD1);
      pascalKeywords.add("case", Token.KEYWORD1);
      pascalKeywords.add("const", Token.KEYWORD1);
      pascalKeywords.add("constructor", Token.KEYWORD1);
      pascalKeywords.add("destructor", Token.KEYWORD1);
      pascalKeywords.add("div", Token.OPERATOR);
      pascalKeywords.add("do", Token.KEYWORD1);
      pascalKeywords.add("downto", Token.KEYWORD1);
      pascalKeywords.add("else", Token.KEYWORD1);
      pascalKeywords.add("end", Token.KEYWORD1);
      pascalKeywords.add("external", Token.KEYWORD1);
      pascalKeywords.add("file", Token.KEYWORD1);
      pascalKeywords.add("for", Token.KEYWORD1);
      pascalKeywords.add("forward", Token.KEYWORD1);
      pascalKeywords.add("function", Token.KEYWORD1);
      pascalKeywords.add("goto", Token.KEYWORD1);
      pascalKeywords.add("if", Token.KEYWORD1);
      pascalKeywords.add("implementation", Token.KEYWORD2);
      pascalKeywords.add("in", Token.KEYWORD1);
      pascalKeywords.add("inherited", Token.KEYWORD1);
      pascalKeywords.add("inline", Token.KEYWORD1);
      pascalKeywords.add("interface", Token.KEYWORD1);
      pascalKeywords.add("interrupt", Token.KEYWORD1);
      pascalKeywords.add("label", Token.KEYWORD2);
      pascalKeywords.add("library", Token.KEYWORD2);
      pascalKeywords.add("mod", Token.OPERATOR);
      pascalKeywords.add("nil", Token.LABEL);
      pascalKeywords.add("not", Token.OPERATOR);
      pascalKeywords.add("object", Token.KEYWORD3);
      pascalKeywords.add("of", Token.KEYWORD1);
      pascalKeywords.add("on", Token.KEYWORD1);
      pascalKeywords.add("packed", Token.KEYWORD1);
      pascalKeywords.add("private", Token.KEYWORD1);
      pascalKeywords.add("procedure", Token.KEYWORD1);
      pascalKeywords.add("program", Token.KEYWORD1);
      pascalKeywords.add("public", Token.KEYWORD1);
      pascalKeywords.add("record", Token.KEYWORD3);
      pascalKeywords.add("repeat", Token.KEYWORD1);
      pascalKeywords.add("set", Token.KEYWORD3);
      pascalKeywords.add("shl", Token.KEYWORD1);
      pascalKeywords.add("shr", Token.KEYWORD1);
      pascalKeywords.add("string", Token.KEYWORD3);
      pascalKeywords.add("then", Token.KEYWORD1);
      pascalKeywords.add("to", Token.KEYWORD1);
      pascalKeywords.add("type", Token.KEYWORD1);
      pascalKeywords.add("unit", Token.KEYWORD1);
      pascalKeywords.add("until", Token.KEYWORD1);
      pascalKeywords.add("uses", Token.KEYWORD2);
      pascalKeywords.add("var", Token.KEYWORD1);
      pascalKeywords.add("virtual", Token.KEYWORD1);
      pascalKeywords.add("while", Token.KEYWORD1);
      pascalKeywords.add("with", Token.KEYWORD1);
      pascalKeywords.add("xor", Token.OPERATOR);
      pascalKeywords.add("true", Token.LABEL);
      pascalKeywords.add("false", Token.LABEL);
      pascalKeywords.add("maxint", Token.LABEL);
      pascalKeywords.add("maxlongint", Token.LABEL);
      pascalKeywords.add("boolean", Token.KEYWORD3);
      pascalKeywords.add("byte", Token.KEYWORD3);
      pascalKeywords.add("char", Token.KEYWORD3);
      pascalKeywords.add("extended", Token.KEYWORD3);
      pascalKeywords.add("longint", Token.KEYWORD3);
      pascalKeywords.add("integer", Token.KEYWORD3);
    }
    return pascalKeywords;
  }

  // private members
  private static KeywordMap pascalKeywords;

  private KeywordMap keywords;
  private int lastOffset;
  private int lastKeyword;
  private int lastWhitespace;

  private boolean doKeyword(Segment line,  int i,  char c)
  {
    int i1 = i+1;

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
