/*
 * JavaTokenMarker.java - Java token marker
 * Copyright (C) 1999 Slava Pestov
 * Portions copyright (C)2002-2003 Romain GUY
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
 * Java token marker.
 *
 * @author Slava Pestov
 * @version $Id: JavaTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:21 gfx Exp $
 */
public class JavaTokenMarker extends CTokenMarker
{
  public static final byte META_DATA = Token.INTERNAL_FIRST + 1;

	public JavaTokenMarker()
	{
		super(false, true, getKeywords());
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

loop: for (int i = offset; i < length; i++)
    {
      int i1 = (i + 1);

      char c = array[i];
      if (c == '\\')
      {
        backslash = !backslash;
        continue;
      }

      switch(token)
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
          case '@':
            doKeyword(line, i, c);
            if(backslash)
              backslash = false;
            else
            {
              addToken(i - lastOffset, token);
              token = META_DATA;
              lastOffset = lastKeyword = i;
            }
            break;
          case '"':
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
              else if (i1 < array.length && array[i1] == ':')
                addToken(i1 - lastOffset, Token.NULL);
              else
                addToken(i1 - lastOffset, Token.LABEL);
              lastOffset = lastKeyword = i1;
              lastWhitespace = i1;
              backslash = false;
            } else if (doKeyword(line, i, c))
              break;
            break;
          case '/':
            backslash = false;
            doKeyword(line, i, c);
            if(length - i > 1)
            {
              switch(array[i1])
              {
                case '*':
                  addToken(i - lastOffset, token);
                  lastOffset = lastKeyword = i;
                  if(javadoc && length - i > 2 && array[i+2] == '*')
                    token = Token.COMMENT2;
                  else
                    token = Token.COMMENT1;
                  break;
                case '/':
                  addToken(i - lastOffset, token);
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
            if (METHOD_DELIMITERS.indexOf(c) != -1)
            {
              lastWhitespace = i;
            }
            break;
          }
          break;
        case META_DATA:
          if (!Character.isLetterOrDigit(c) && c != '_')
          {
            addToken(i - lastOffset, Token.LABEL);
            token = Token.NULL;
            lastOffset = lastKeyword = i;
            lastWhitespace = i;
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
      case META_DATA:
        addToken(length - lastOffset, Token.LABEL);
        token = Token.NULL;
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
		if (javaKeywords == null)
		{
			javaKeywords = new KeywordMap(false);
       javaKeywords.add("goto", Token.INVALID); 
      javaKeywords.add("const", Token.INVALID);
			javaKeywords.add("package", Token.KEYWORD2);
			javaKeywords.add("import", Token.KEYWORD2);
			javaKeywords.add("byte", Token.KEYWORD3);
			javaKeywords.add("char", Token.KEYWORD3);
			javaKeywords.add("short", Token.KEYWORD3);
			javaKeywords.add("int", Token.KEYWORD3);
			javaKeywords.add("long", Token.KEYWORD3);
			javaKeywords.add("float", Token.KEYWORD3);
			javaKeywords.add("double", Token.KEYWORD3);
			javaKeywords.add("boolean", Token.KEYWORD3);
			javaKeywords.add("void", Token.KEYWORD3);
			javaKeywords.add("enum", Token.KEYWORD3);
			javaKeywords.add("class", Token.KEYWORD3);
			javaKeywords.add("interface", Token.KEYWORD3);
			javaKeywords.add("abstract", Token.KEYWORD1);
			javaKeywords.add("assert", Token.KEYWORD1);
      javaKeywords.add("final", Token.KEYWORD1);
      javaKeywords.add("strictfp", Token.KEYWORD1); 
			javaKeywords.add("private", Token.KEYWORD1);
			javaKeywords.add("protected", Token.KEYWORD1);
			javaKeywords.add("public", Token.KEYWORD1);
			javaKeywords.add("static", Token.KEYWORD1);
			javaKeywords.add("synchronized", Token.KEYWORD1);
			javaKeywords.add("native", Token.KEYWORD1);
			javaKeywords.add("volatile", Token.KEYWORD1);
			javaKeywords.add("transient", Token.KEYWORD1);
			javaKeywords.add("break", Token.KEYWORD1);
			javaKeywords.add("case", Token.KEYWORD1);
			javaKeywords.add("continue", Token.KEYWORD1);
			javaKeywords.add("default", Token.KEYWORD1);
			javaKeywords.add("do", Token.KEYWORD1);
			javaKeywords.add("else", Token.KEYWORD1);
			javaKeywords.add("for", Token.KEYWORD1);
			javaKeywords.add("if", Token.KEYWORD1);
			javaKeywords.add("instanceof", Token.KEYWORD1);
			javaKeywords.add("new", Token.KEYWORD1);
			javaKeywords.add("return", Token.KEYWORD1);
			javaKeywords.add("switch", Token.KEYWORD1);
			javaKeywords.add("while", Token.KEYWORD1);
			javaKeywords.add("throw", Token.KEYWORD1);
			javaKeywords.add("try", Token.KEYWORD1);
			javaKeywords.add("catch", Token.KEYWORD1);
			javaKeywords.add("extends", Token.KEYWORD1);
			javaKeywords.add("finally", Token.KEYWORD1);
			javaKeywords.add("implements", Token.KEYWORD1);
			javaKeywords.add("throws", Token.KEYWORD1);
			javaKeywords.add("this", Token.LITERAL2);
			javaKeywords.add("null", Token.LITERAL2);
			javaKeywords.add("super", Token.LITERAL2);
			javaKeywords.add("true", Token.LITERAL2);
			javaKeywords.add("false", Token.LITERAL2);
		}
		return javaKeywords;
	}

	// private members
	private static KeywordMap javaKeywords;
}

/*
 * ChangeLog:
 * $Log: JavaTokenMarker.java,v $
 * Revision 1.1.1.1  2004/10/19 16:16:21  gfx
 * no message
 *
 * Revision 1.6  2003/06/30 17:31:10  blaisorblade
 * Fix for line-ends.
 *
 * Revision 1.5  2003/06/29 13:37:27  gfx
 * Support of JDK 1.4.2
 *
 * Revision 1.4  2003/03/13 22:52:48  gfx
 * Improved focus gain
 *
 * Revision 1.3  2003/01/05 00:09:37  gfx
 * New strictfp keyword
 *
 * Revision 1.2  2001/12/01 18:54:31  gfx
 * Various bug fixes
 *
 * Revision 1.1.1.1  2001/08/20 22:31:56  gfx
 * Jext 3.0pre5
 *
 * Revision 1.2  2001/08/04 22:11:45  gfx
 * Methods colorizing, new Python 2.2 keyword
 *
 * Revision 1.1.1.1  2001/04/11 14:22:32  gfx
 *
 * Jext 2.11: GUI customization, bug fixes
 *
 * Revision 1.6  2000/01/29 10:12:43  sp
 * BeanShell edit mode, bug fixes
 *
 * Revision 1.5  1999/12/13 03:40:30  sp
 * Bug fixes, syntax is now mostly GPL'd
 *
 * Revision 1.4  1999/11/09 10:14:34  sp
 * Macro code cleanups, menu item and tool bar clicks are recorded now, delete
 * word commands, check box menu item support
 *
 * Revision 1.3  1999/06/05 00:22:58  sp
 * LGPL'd syntax package
 *
 * Revision 1.2  1999/04/22 06:03:26  sp
 * Syntax colorizing change
 *
 * Revision 1.1  1999/03/13 09:11:46  sp
 * Syntax code updates, code cleanups
 *
 */