/*
 * TeXTokenMarker.java - TeX/LaTeX/AMS-TeX token marker
 * Copyright (C) 1998 Slava Pestov
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
 * TeX token marker.
 *
 * @author Slava Pestov
 * @version $Id: TeXTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:21 gfx Exp $
 */
public class TeXTokenMarker extends TokenMarker
{
	// public members
	public static final byte BDFORMULA = Token.INTERNAL_FIRST;
	public static final byte EDFORMULA = (byte)(Token.INTERNAL_FIRST + 1);
	
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
			// if a backslash is followed immediately
			// by a non-alpha character, the command at
			// the non-alpha char. If we have a backslash,
			// some text, and then a non-alpha char,
			// the command ends before the non-alpha char.
			if(Character.isLetter(c))
			{
				backslash = false;
			}
			else
			{
				if(backslash)
				{
					// \<non alpha>
					// we skip over this character,
					// hence the `continue'
					backslash = false;
					if(token == Token.KEYWORD2 || token == EDFORMULA)
						token = Token.KEYWORD2;
					addToken(i1 - lastOffset,token);
					lastOffset = i1;
					if(token == Token.KEYWORD1)
						token = Token.NULL;
					continue;
				}
				else
				{
					//\blah<non alpha>
					// we leave the character in
					// the stream, and it's not
					// part of the command token
					if(token == BDFORMULA || token == EDFORMULA)
						token = Token.KEYWORD2;
					addToken(i - lastOffset,token);
					if(token == Token.KEYWORD1)
						token = Token.NULL;
					lastOffset = i;
				}
			}
			switch(c)
			{
			case '%':
				if(backslash)
				{
					backslash = false;
					break;
				}
				addToken(i - lastOffset,token);
				addToken(length - i,Token.COMMENT1);
				lastOffset = length;
				break loop;
			case '\\':
				backslash = true;
				if(token == Token.NULL)
				{
					token = Token.KEYWORD1;
					addToken(i - lastOffset,Token.NULL);
					lastOffset = i;
				}
				break;
			case '$':
				backslash = false;
				if(token == Token.NULL) // singe $
				{
					token = Token.KEYWORD2;
					addToken(i - lastOffset,Token.NULL);
					lastOffset = i;
				}
				else if(token == Token.KEYWORD1) // \...$
				{
					token = Token.KEYWORD2;
					addToken(i - lastOffset,Token.KEYWORD1);
					lastOffset = i;
				}
				else if(token == Token.KEYWORD2) // $$aaa
				{
					if(i - lastOffset == 1 && array[i-1] == '$')
					{
						token = BDFORMULA;
						break;
					}
					token = Token.NULL;
					addToken(i1 - lastOffset,Token.KEYWORD2);
					lastOffset = i1;
				}
				else if(token == BDFORMULA) // $$aaa$
				{
					token = EDFORMULA;
				}
				else if(token == EDFORMULA) // $$aaa$$
				{
					token = Token.NULL;
					addToken(i1 - lastOffset,Token.KEYWORD2);
					lastOffset = i1;
				}
				break;
			}
		}
		if(lastOffset != length)
			addToken(length - lastOffset,token == BDFORMULA
				|| token == EDFORMULA ? Token.KEYWORD2 :
				token);
		return (token != Token.KEYWORD1 ? token : Token.NULL);
	}
}

/*
 * ChangeLog:
 * $Log: TeXTokenMarker.java,v $
 * Revision 1.1.1.1  2004/10/19 16:16:21  gfx
 * no message
 *
 * Revision 1.1.1.1  2001/08/20 22:31:47  gfx
 * Jext 3.0pre5
 *
 * Revision 1.1.1.1  2001/04/11 14:22:36  gfx
 *
 * Jext 2.11: GUI customization, bug fixes
 *
 * Revision 1.17  2000/03/20 03:42:55  sp
 * Smoother syntax package, opening an already open file will ask if it should be
 * reloaded, maybe some other changes
 *
 * Revision 1.16  1999/12/13 03:40:30  sp
 * Bug fixes, syntax is now mostly GPL'd
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
 * Revision 1.13  1999/04/19 05:38:20  sp
 * Syntax API changes
 *
 * Revision 1.12  1999/04/01 04:13:00  sp
 * Bug fixing for 1.5final
 *
 * Revision 1.11  1999/03/27 00:44:15  sp
 * Documentation updates, various bug fixes
 *
 * Revision 1.10  1999/03/13 00:09:07  sp
 * Console updates, uncomment removed cos it's too buggy, cvs log tags added
 *
 * Revision 1.9  1999/03/12 23:51:00  sp
 * Console updates, uncomment removed cos it's too buggy, cvs log tags added
 *
 */
