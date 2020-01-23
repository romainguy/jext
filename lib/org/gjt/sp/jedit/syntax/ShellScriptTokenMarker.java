/*
 * ShellScriptTokenMarker.java - Shell script token marker
 * Copyright (C) 1998, 1999 Slava Pestov
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
 * Shell script token marker.
 *
 * @author Slava Pestov
 * @version $Id: ShellScriptTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:20 gfx Exp $
 */
public class ShellScriptTokenMarker extends TokenMarker
{
	// public members
	public static final byte LVARIABLE = Token.INTERNAL_FIRST;

	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		char[] array = line.array;
		byte cmdState = 0; // 0 = space before command, 1 = inside
				// command, 2 = after command
		int offset = line.offset;
		int lastOffset = offset;
		int length = line.count + offset;

		if(token == Token.LITERAL1 && lineIndex != 0
			&& lineInfo[lineIndex - 1].obj != null)
		{
			String str = (String)lineInfo[lineIndex - 1].obj;
			if(str != null && str.length() == line.count
				&& SyntaxUtilities.regionMatches(false,line,
				offset,str))
			{
				addToken(line.count,Token.LITERAL1);
				return Token.NULL;
			}
			else
			{
				addToken(line.count,Token.LITERAL1);
				lineInfo[lineIndex].obj = str;
				return Token.LITERAL1;
			}
		}

		boolean backslash = false;
loop:		for(int i = offset; i < length; i++)
		{
			int i1 = (i+1);

			char c = array[i];

			if(c == '\\')
			{
				backslash = !backslash;
				continue;
			}

			switch(token)
			{
			case Token.NULL:
				switch(c)
				{
				case ' ': case '\t': case '(': case ')':
					backslash = false;
					if(cmdState == 1/*insideCmd*/)
					{
						addToken(i - lastOffset,Token.KEYWORD1);
						lastOffset = i;
						cmdState = 2; /*afterCmd*/
					}
					break;
				case '=':
					backslash = false;
					if(cmdState == 1/*insideCmd*/)
					{
						addToken(i - lastOffset,token);
						lastOffset = i;
						cmdState = 2; /*afterCmd*/
					}
					break;
				case '&': case '|': case ';':
					if(backslash)
						backslash = false;
					else
						cmdState = 0; /*beforeCmd*/
					break;
				case '#':
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						addToken(length - i,Token.COMMENT1);
						lastOffset = length;
						break loop;
					}
					break;
				case '$':
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						cmdState = 2; /*afterCmd*/
						lastOffset = i;
						if(length - i >= 2)
						{
							switch(array[i1])
							{
							case '(':
								continue;
							case '{':
								token = LVARIABLE;
								break;
							default:
								token = Token.KEYWORD2;
								break;
							}
						}
						else
							token = Token.KEYWORD2;
					}
					break;
				case '"':
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						token = Token.LITERAL1;
						lineInfo[lineIndex].obj = null;
						cmdState = 2; /*afterCmd*/
						lastOffset = i;
					}
					break;
				case '\'': case '`':
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						token = Token.LITERAL2;
						cmdState = 2; /*afterCmd*/
						lastOffset = i;
					}
					break;
				case '<':
					if(backslash)
						backslash = false;
					else
					{
						if(length - i > 1 && array[i1] == '<')
						{
							addToken(i - lastOffset,
								token);
							token = Token.LITERAL1;
							lastOffset = i;
							lineInfo[lineIndex].obj =
								new String(array,i + 2,
									length - (i+2));
						}
					}
					break;
				default:
					backslash = false;
					if(Character.isLetter(c))
					{
						if(cmdState == 0 /*beforeCmd*/)
						{
							addToken(i - lastOffset,token);
							lastOffset = i;
							cmdState++; /*insideCmd*/
						}
					}
					break;
				}
				break;
			case Token.KEYWORD2:
				backslash = false;
				if(!Character.isLetterOrDigit(c) && c != '_')
				{
					if(i != offset && array[i-1] == '$')
					{
						addToken(i1 - lastOffset,token);
						lastOffset = i1;
						token = Token.NULL;
						continue;
					}
					else
					{
						addToken(i - lastOffset,token);
						lastOffset = i;
						token = Token.NULL;
					}
				}
				break;
			case Token.LITERAL1:
				if(backslash)
					backslash = false;
				else if(c == '"')
				{
					addToken(i1 - lastOffset,token);
					cmdState = 2; /*afterCmd*/
					lastOffset = i1;
					token = Token.NULL;
				}
				else
					backslash = false;
				break;
			case Token.LITERAL2:
				if(backslash)
					backslash = false;
				else if(c == '\'' || c == '`')
				{
					addToken(i1 - lastOffset,Token.LITERAL1);
					cmdState = 2; /*afterCmd*/
					lastOffset = i1;
					token = Token.NULL;
				}
				else
					backslash = false;
				break;
			case LVARIABLE:
				backslash = false;
				if(c == '}')
				{
					addToken(i1 - lastOffset,Token.KEYWORD2);
					lastOffset = i1;
					token = Token.NULL;
				}
				break;
			default:
				throw new InternalError("Invalid state: " + token);
			}
		}

		switch(token)
		{
		case Token.NULL:
			if(cmdState == 1)
				addToken(length - lastOffset,Token.KEYWORD1);
			else
				addToken(length - lastOffset,token);
			break;
		case Token.LITERAL2:
			addToken(length - lastOffset,Token.LITERAL1);
			break;
		case Token.KEYWORD2:
			addToken(length - lastOffset,token);
			token = Token.NULL;
			break;
		case LVARIABLE:
			addToken(length - lastOffset,Token.INVALID);
			token = Token.NULL;
			break;
		default:
			addToken(length - lastOffset,token);
			break;
		}
		return token;
	}
}

/*
 * ChangeLog:
 * $Log: ShellScriptTokenMarker.java,v $
 * Revision 1.1.1.1  2004/10/19 16:16:20  gfx
 * no message
 *
 * Revision 1.2  2001/11/28 21:25:29  gfx
 * Misc
 *
 * Revision 1.1.1.1  2001/08/20 22:31:41  gfx
 * Jext 3.0pre5
 *
 * Revision 1.1.1.1  2001/04/11 14:22:35  gfx
 *
 * Jext 2.11: GUI customization, bug fixes
 *
 * Revision 1.18  1999/12/13 03:40:30  sp
 * Bug fixes, syntax is now mostly GPL'd
 *
 * Revision 1.17  1999/06/20 02:15:45  sp
 * Syntax coloring optimizations
 *
 * Revision 1.16  1999/06/06 05:05:25  sp
 * Search and replace tweaks, Perl/Shell Script mode updates
 *
 * Revision 1.15  1999/06/05 00:22:58  sp
 * LGPL'd syntax package
 *
 * Revision 1.14  1999/06/03 08:24:14  sp
 * Fixing broken CVS
 *
 * Revision 1.15  1999/05/31 08:11:10  sp
 * Syntax coloring updates, expand abbrev bug fix
 *
 * Revision 1.14  1999/05/31 04:38:51  sp
 * Syntax optimizations, HyperSearch for Selection added (Mike Dillon)
 *
 * Revision 1.13  1999/05/30 04:57:15  sp
 * Perl mode started
 *
 * Revision 1.12  1999/05/03 04:28:01  sp
 * Syntax colorizing bug fixing, console bug fix for Swing 1.1.1
 *
 * Revision 1.11  1999/04/27 06:53:38  sp
 * JARClassLoader updates, shell script token marker update, token marker compiles
 * now
 *
 * Revision 1.10  1999/04/22 06:03:26  sp
 * Syntax colorizing change
 *
 * Revision 1.9  1999/04/19 05:38:20  sp
 * Syntax API changes
 *
 * Revision 1.8  1999/03/12 23:51:00  sp
 * Console updates, uncomment removed cos it's too buggy, cvs log tags added
 *
 */
