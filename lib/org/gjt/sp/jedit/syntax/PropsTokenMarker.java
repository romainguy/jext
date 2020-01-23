/*
 * PropsTokenMarker.java - Java props/DOS INI token marker
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
 * Java properties/DOS INI token marker.
 *
 * @author Slava Pestov
 * @version $Id: PropsTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:19 gfx Exp $
 */
public class PropsTokenMarker extends TokenMarker
{
	public static final byte VALUE = Token.INTERNAL_FIRST;

	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		char[] array = line.array;
		int offset = line.offset;
		int lastOffset = offset;
		int length = line.count + offset;
loop:		for(int i = offset; i < length; i++)
		{
			int i1 = (i+1);

			switch(token)
			{
			case Token.NULL:
				switch(array[i])
				{
				case '#': case ';':
					if(i == offset)
					{
						addToken(line.count,Token.COMMENT1);
						lastOffset = length;
						break loop;
					}
					break;
				case '[':
					if(i == offset)
					{
						addToken(i - lastOffset,token);
						token = Token.KEYWORD2;
						lastOffset = i;
					}
					break;
				case '=':
					addToken(i - lastOffset,Token.KEYWORD1);
					token = VALUE;
					lastOffset = i;
					break;
				}
				break;
			case Token.KEYWORD2:
				if(array[i] == ']')
				{
					addToken(i1 - lastOffset,token);
					token = Token.NULL;
					lastOffset = i1;
				}
				break;
			case VALUE:
				break;
			default:
				throw new InternalError("Invalid state: "
					+ token);
			}
		}
		if(lastOffset != length)
			addToken(length - lastOffset,Token.NULL);
		return Token.NULL;
	}

	public boolean supportsMultilineTokens()
	{
		return false;
	}
}

/*
 * ChangeLog:
 * $Log: PropsTokenMarker.java,v $
 * Revision 1.1.1.1  2004/10/19 16:16:19  gfx
 * no message
 *
 * Revision 1.1.1.1  2001/08/20 22:31:41  gfx
 * Jext 3.0pre5
 *
 * Revision 1.1.1.1  2001/04/11 14:22:34  gfx
 *
 * Jext 2.11: GUI customization, bug fixes
 *
 * Revision 1.9  1999/12/13 03:40:30  sp
 * Bug fixes, syntax is now mostly GPL'd
 *
 * Revision 1.8  1999/10/23 03:48:22  sp
 * Mode system overhaul, close all dialog box, misc other stuff
 *
 * Revision 1.7  1999/07/05 04:38:39  sp
 * Massive batch of changes... bug fixes, also new text component is in place.
 * Have fun
 *
 * Revision 1.6  1999/06/20 02:15:45  sp
 * Syntax coloring optimizations
 *
 * Revision 1.5  1999/06/05 00:22:58  sp
 * LGPL'd syntax package
 *
 * Revision 1.4  1999/06/03 08:24:14  sp
 * Fixing broken CVS
 *
 * Revision 1.5  1999/05/31 08:11:10  sp
 * Syntax coloring updates, expand abbrev bug fix
 *
 * Revision 1.4  1999/05/31 04:38:51  sp
 * Syntax optimizations, HyperSearch for Selection added (Mike Dillon)
 *
 * Revision 1.3  1999/04/19 05:38:20  sp
 * Syntax API changes
 *
 * Revision 1.2  1999/03/12 23:51:00  sp
 * Console updates, uncomment removed cos it's too buggy, cvs log tags added
 *
 */
