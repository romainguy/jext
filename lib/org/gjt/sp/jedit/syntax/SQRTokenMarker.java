/*
 * sqrTokenMarker.java - sqr token marker
 * By Richard Ashwell
 * Modified and Pieced together from TSQLTokenMaker.java
 * Copyright (C) 2001 Romain Guy
 *
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
 * sqr token marker.
 */
public class SQRTokenMarker extends TokenMarker
{
  boolean bracket = false;

  public SQRTokenMarker()
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

loop:   for(int i = offset; i < length; i++)
    {
      int i1 = (i+1);

      char c = array[i];

      switch(token)
      {
      case Token.NULL:
        switch(c)
        {
        case '[':
          bracket = true;
        case '"':
          doKeyword(line,i,c);
          addToken(i - lastOffset,token);
          token = Token.LITERAL1;
          lastOffset = lastKeyword = i;
          break;
        case '\'':
          doKeyword(line,i,c);
          addToken(i - lastOffset,token);
          token = Token.LITERAL2;
          lastOffset = lastKeyword = i;
          break;
        case ':':
          if(lastKeyword == offset)
          {
            if(doKeyword(line,i,c))
              break;
            addToken(i1 - lastOffset,Token.LABEL);
            lastOffset = lastKeyword = i1;
          }
          else if(doKeyword(line,i,c))
            break;
          break;
        case '!':  /* THIS IS SQR's Line Comment Indicator */
          doKeyword(line,i,c);
          if(length - i > 1)
          {
              addToken(i - lastOffset,token);
 	      addToken(length - i,Token.COMMENT1);
	      lastOffset = length;
	      break loop;
          }
          break;
        default:
          if(!Character.isLetterOrDigit(c) && c != '-' && c != '#')  /* SQR Allows some weird Characters in Keywords */
            doKeyword(line,i,c);
          break;
        }
        break;
      case Token.COMMENT1:
        break;
      case Token.COMMENT2:
        if(c == '*' && length - i > 1)
        {
          if(array[i1] == '/')
          {
            i++;
            addToken((i+1) - lastOffset,token);
            token = Token.NULL;
            lastOffset = lastKeyword = i+1;
          }
        }
        break;
      case Token.LITERAL1:
         if(c == '"' || c == ']')
        {
          addToken(i1 - lastOffset,token);
          token = Token.NULL;
          lastOffset = lastKeyword = i1;
          bracket = false;
        }
        break;
      case Token.LITERAL2:
        if(c == '\'')
        {
          addToken(i1 - lastOffset,Token.LITERAL1);
          token = Token.NULL;
          lastOffset = lastKeyword = i1;
        }
        break;
      default:
        throw new InternalError("Invalid state: "
          + token);
      }
    }

    if(token == Token.NULL)
      doKeyword(line,length,'\0');

    switch(token)
    {
    case Token.LITERAL1:
    case Token.LITERAL2:
      addToken(length - lastOffset, (bracket ? Token.LITERAL1 : Token.INVALID));
      token = (bracket ? Token.LITERAL1 : Token.NULL);
      break;
    case Token.KEYWORD2:
      addToken(length - lastOffset,token);
      token = Token.NULL;
      break;
    default:
      addToken(length - lastOffset,token);
      break;
    }

    return token;
  }

  public static KeywordMap getKeywords()
  {
    if(sqrKeywords == null)
    {
      sqrKeywords = new KeywordMap(true);

      sqrKeywords.add("BEGIN-FOOTING", Token.KEYWORD1);
      sqrKeywords.add("BEGIN-HEADING", Token.KEYWORD1);
      sqrKeywords.add("BEGIN-PROCEDURE", Token.KEYWORD1);
      sqrKeywords.add("BEGIN-PROGRAM", Token.KEYWORD1);
      sqrKeywords.add("BEGIN-REPORT", Token.KEYWORD1);
      sqrKeywords.add("BEGIN-SELECT", Token.KEYWORD1);
      sqrKeywords.add("BEGIN-SETUP", Token.KEYWORD1);
      sqrKeywords.add("END-FOOTING", Token.KEYWORD1);
      sqrKeywords.add("END-HEADING", Token.KEYWORD1);
      sqrKeywords.add("END-PROCEDURE", Token.KEYWORD1);
      sqrKeywords.add("END-PROGRAM", Token.KEYWORD1);
      sqrKeywords.add("END-REPORT", Token.KEYWORD1);
      sqrKeywords.add("END-SETUP", Token.KEYWORD1);
      sqrKeywords.add("END-SELECT", Token.KEYWORD1);
      sqrKeywords.add("INPUT", Token.KEYWORD1);

      sqrKeywords.add("#include", Token.KEYWORD2);
      sqrKeywords.add("#debug", Token.KEYWORD2);
      sqrKeywords.add("#define", Token.KEYWORD2);
      sqrKeywords.add("#else", Token.KEYWORD2);
      sqrKeywords.add("#end-if", Token.KEYWORD2);
      sqrKeywords.add("#endif", Token.KEYWORD2);
      sqrKeywords.add("#if", Token.KEYWORD2);
      sqrKeywords.add("#ifdef", Token.KEYWORD2);
      sqrKeywords.add("#ifndef", Token.KEYWORD2);

      sqrKeywords.add("add", Token.KEYWORD3);
      sqrKeywords.add("array-add", Token.KEYWORD3);
      sqrKeywords.add("array-divide", Token.KEYWORD3);
      sqrKeywords.add("array-multiply", Token.KEYWORD3);
      sqrKeywords.add("array-subtract", Token.KEYWORD3);
      sqrKeywords.add("ask", Token.KEYWORD3);
      sqrKeywords.add("break", Token.KEYWORD3);
      sqrKeywords.add("call", Token.KEYWORD3);
      sqrKeywords.add("clear-array", Token.KEYWORD3);
      sqrKeywords.add("close", Token.KEYWORD3);
      sqrKeywords.add("columns", Token.KEYWORD3);
      sqrKeywords.add("commit", Token.KEYWORD3);
      sqrKeywords.add("concat", Token.KEYWORD3);
      sqrKeywords.add("connect", Token.KEYWORD3);
      sqrKeywords.add("create-array", Token.KEYWORD3);
      sqrKeywords.add("date-time", Token.KEYWORD3);
      sqrKeywords.add("display", Token.KEYWORD3);
      sqrKeywords.add("divide", Token.KEYWORD3);
      sqrKeywords.add("do", Token.KEYWORD3);
      sqrKeywords.add("dollar-symbol", Token.KEYWORD3);
      sqrKeywords.add("else", Token.KEYWORD3);
      sqrKeywords.add("encode", Token.KEYWORD3);
      sqrKeywords.add("end-evaluate", Token.KEYWORD3);
      sqrKeywords.add("end-if", Token.KEYWORD3);
      sqrKeywords.add("end-while", Token.KEYWORD3);
      sqrKeywords.add("evaluate", Token.KEYWORD3);
      sqrKeywords.add("execute", Token.KEYWORD3);
      sqrKeywords.add("extract", Token.KEYWORD3);
      sqrKeywords.add("find", Token.KEYWORD3);
      sqrKeywords.add("font", Token.KEYWORD3);
      sqrKeywords.add("get", Token.KEYWORD3);
      sqrKeywords.add("goto", Token.KEYWORD3);
      sqrKeywords.add("graphic", Token.KEYWORD3);
      sqrKeywords.add("if", Token.KEYWORD3);
      sqrKeywords.add("last-page", Token.KEYWORD3);
      sqrKeywords.add("let", Token.KEYWORD3);
      sqrKeywords.add("lookup", Token.KEYWORD3);
      sqrKeywords.add("lowercase", Token.KEYWORD3);
      sqrKeywords.add("money-symbol", Token.KEYWORD3);
      sqrKeywords.add("move", Token.KEYWORD3);
      sqrKeywords.add("multiply", Token.KEYWORD3);
      sqrKeywords.add("new-page", Token.KEYWORD3);
      sqrKeywords.add("new-report", Token.KEYWORD3);
      sqrKeywords.add("next-column", Token.KEYWORD3);
      sqrKeywords.add("next-listing", Token.KEYWORD3);
      sqrKeywords.add("no-formfeed", Token.KEYWORD3);
      sqrKeywords.add("open", Token.KEYWORD3);
      sqrKeywords.add("page-number", Token.KEYWORD3);
      sqrKeywords.add("page-size", Token.KEYWORD3);
      sqrKeywords.add("position", Token.KEYWORD3);
      sqrKeywords.add("print", Token.KEYWORD3);
      sqrKeywords.add("print-bar-code", Token.KEYWORD3);
      sqrKeywords.add("print-chart", Token.KEYWORD3);
      sqrKeywords.add("print-direct", Token.KEYWORD3);
      sqrKeywords.add("print-image", Token.KEYWORD3);
      sqrKeywords.add("printer-deinit", Token.KEYWORD3);
      sqrKeywords.add("printer-init", Token.KEYWORD3);
      sqrKeywords.add("put", Token.KEYWORD3);
      sqrKeywords.add("read", Token.KEYWORD3);
      sqrKeywords.add("rollback", Token.KEYWORD3);
      sqrKeywords.add("show", Token.KEYWORD3);
      sqrKeywords.add("stop", Token.KEYWORD3);
      sqrKeywords.add("string", Token.KEYWORD3);
      sqrKeywords.add("subtract", Token.KEYWORD3);
      sqrKeywords.add("unstring", Token.KEYWORD3);
      sqrKeywords.add("uppercase", Token.KEYWORD3);
      sqrKeywords.add("use", Token.KEYWORD3);
      sqrKeywords.add("use-column", Token.KEYWORD3);
      sqrKeywords.add("use-printer-type", Token.KEYWORD3);
      sqrKeywords.add("use-procedure", Token.KEYWORD3);
      sqrKeywords.add("use-report", Token.KEYWORD3);
      sqrKeywords.add("use-report", Token.KEYWORD3);
      sqrKeywords.add("while", Token.KEYWORD3);
      sqrKeywords.add("write", Token.KEYWORD3);
      sqrKeywords.add("from", Token.KEYWORD3);
      sqrKeywords.add("where", Token.KEYWORD3);
      sqrKeywords.add("order", Token.KEYWORD3);
      sqrKeywords.add("by", Token.KEYWORD3);
      sqrKeywords.add("in", Token.KEYWORD3);
      sqrKeywords.add("to", Token.KEYWORD3);
      sqrKeywords.add("between", Token.KEYWORD3);
      sqrKeywords.add("and", Token.KEYWORD3);
      sqrKeywords.add("or", Token.KEYWORD3);
      sqrKeywords.add("substr", Token.KEYWORD3);
      sqrKeywords.add("instr", Token.KEYWORD3);
      sqrKeywords.add("len", Token.KEYWORD3);

    }
    return sqrKeywords;
  }

  // private members
  private static KeywordMap sqrKeywords;

  private boolean cpp;
  private boolean javadoc;
  private KeywordMap keywords;
  private int lastOffset;
  private int lastKeyword;

  private boolean doKeyword(Segment line, int i, char c)
  {
    int i1 = i+1;

    int len = i - lastKeyword;
    byte id = keywords.lookup(line,lastKeyword,len);
    if(id != Token.NULL)
    {
      if(lastKeyword != lastOffset)
        addToken(lastKeyword - lastOffset,Token.NULL);
      addToken(len,id);
      lastOffset = i;
    }
    lastKeyword = i1;
    return false;
  }
}
