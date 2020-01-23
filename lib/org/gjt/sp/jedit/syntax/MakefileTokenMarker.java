/*
 * MakefileTokenMarker.java - Makefile token marker
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
 * Makefile token marker.
 *
 * @author Slava Pestov
 * @version $Id: MakefileTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:19 gfx Exp $
 */
public class MakefileTokenMarker extends TokenMarker
{
	// public members
	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		char[] array = line.array;
		int offset = line.offset;
		int lastOffset = offset;
		int length = line.count + offset;
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
				case ':': case '=': case ' ': case '\t':
					backslash = false;
					if(lastOffset == offset)
					{
						addToken(i1 - lastOffset,Token.KEYWORD1);
						lastOffset = i1;
					}
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
					else if(lastOffset != offset)
					{
						addToken(i - lastOffset,token);
						lastOffset = i;
						if(length - i > 1)
						{
							char c1 = array[i1];
							if(c1 == '(' || c1 == '{')
								token = Token.KEYWORD2;
							else
							{
								addToken(2,Token.KEYWORD2);
								lastOffset += 2;
								i++;
							}
						}
					}
					break;
				case '"':
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						token = Token.LITERAL1;
						lastOffset = i;
					}
					break;
				case '\'':
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						token = Token.LITERAL2;
						lastOffset = i;
					}
					break;
				default:
					backslash = false;
					break;
				}
			case Token.KEYWORD2:
				backslash = false;
				if(c == ')' || c == '}')
				{
					addToken(i1 - lastOffset,token);
					token = Token.NULL;
					lastOffset = i1;
				}
				break;
			case Token.LITERAL1:
				if(backslash)
					backslash = false;
				else if(c == '"')
				{
					addToken(i1 - lastOffset,token);
					token = Token.NULL;
					lastOffset = i1;
				}
				else
					backslash = false;
				break;
			case Token.LITERAL2:
				if(backslash)
					backslash = false;
				else if(c == '\'')
				{
					addToken(i1 - lastOffset,Token.LITERAL1);
					token = Token.NULL;
					lastOffset = i1;
				}
				else
					backslash = false;
				break;
			}
		}
		switch(token)
		{
		case Token.KEYWORD2:
			addToken(length - lastOffset,Token.INVALID);
			token = Token.NULL;
			break;
		case Token.LITERAL2:
			addToken(length - lastOffset,Token.LITERAL1);
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
 * $Log: MakefileTokenMarker.java,v $
 * Revision 1.1.1.1  2004/10/19 16:16:19  gfx
 * no message
 *
 * Revision 1.1.1.1  2001/08/20 22:32:07  gfx
 * Jext 3.0pre5
 *
 * Revision 1.1.1.1  2001/04/11 14:22:32  gfx
 *
 * Jext 2.11: GUI customization, bug fixes
 *
 * Revision 1.18  1999/12/13 03:40:30  sp
 * Bug fixes, syntax is now mostly GPL'd
 *
 * Revision 1.17  1999/06/20 02:15:45  sp
 * Syntax coloring optimizations
 *
 * Revision 1.16  1999/06/05 00:22:58  sp
 * LGPL'd syntax package
 *
 * Revision 1.15  1999/06/03 08:24:14  sp
 * Fixing broken CVS
 *
 * Revision 1.16  1999/05/31 08:11:10  sp
 * Syntax coloring updates, expand abbrev bug fix
 *
 * Revision 1.15  1999/05/31 04:38:51  sp
 * Syntax optimizations, HyperSearch for Selection added (Mike Dillon)
 *
 * Revision 1.14  1999/05/22 08:33:53  sp
 * FAQ updates, mode selection tweak, patch mode update, javadoc updates, JDK 1.1.8 fix
 *
 * Revision 1.13  1999/04/22 06:03:26  sp
 * Syntax colorizing change
 *
 * Revision 1.12  1999/04/19 05:38:20  sp
 * Syntax API changes
 *
 * Revision 1.11  1999/03/13 00:09:07  sp
 * Console updates, uncomment removed cos it's too buggy, cvs log tags added
 *
 * Revision 1.10  1999/03/12 23:51:00  sp
 * Console updates, uncomment removed cos it's too buggy, cvs log tags added
 *
 */
