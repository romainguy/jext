/*
 * BatchFileTokenMarker.java - Batch file token marker
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
 * Batch file token marker.
 *
 * @author Slava Pestov
 * @version $Id: BatchFileTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:18 gfx Exp $
 */
public class BatchFileTokenMarker extends TokenMarker
{
	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		char[] array = line.array;
		int offset = line.offset;
		int lastOffset = offset;
		int length = line.count + offset;

		if(SyntaxUtilities.regionMatches(true,line,offset,"rem"))
		{
			addToken(line.count,Token.COMMENT1);
			return Token.NULL;
		}

loop:		for(int i = offset; i < length; i++)
		{
			int i1 = (i+1);

			switch(token)
			{
			case Token.NULL:
				switch(array[i])
				{
				case '%':
					addToken(i - lastOffset,token);
					lastOffset = i;
					if(length - i <= 3 || array[i+2] == ' ')
					{
						addToken(2,Token.KEYWORD2);
						i += 2;
						lastOffset = i;
					}
					else
						token = Token.KEYWORD2;
					break;
				case '"':
					addToken(i - lastOffset,token);
					token = Token.LITERAL1;
					lastOffset = i;
					break;
				case ':':
					if(i == offset)
					{
						addToken(line.count,Token.LABEL);
						lastOffset = length;
						break loop;
					}
					break;
				case ' ':
					if(lastOffset == offset)
					{
						addToken(i - lastOffset,Token.KEYWORD1);
						lastOffset = i;
					}
					break;
				}
				break;
			case Token.KEYWORD2:
				if(array[i] == '%')
				{
					addToken(i1 - lastOffset,token);
					token = Token.NULL;
					lastOffset = i1;
				}
				break;
			case Token.LITERAL1:
				if(array[i] == '"')
				{
					addToken(i1 - lastOffset,token);
					token = Token.NULL;
					lastOffset = i1;
				}
				break;
			default:
				throw new InternalError("Invalid state: " + token);
			}
		}

		if(lastOffset != length)
		{
			if(token != Token.NULL)
				token = Token.INVALID;
			else if(lastOffset == offset)
				token = Token.KEYWORD1;
			addToken(length - lastOffset,token);
		}
		return Token.NULL;
	}

	public boolean supportsMultilineTokens()
	{
		return false;
	}
}

/*
 * ChangeLog:
 * $Log: BatchFileTokenMarker.java,v $
 * Revision 1.1.1.1  2004/10/19 16:16:18  gfx
 * no message
 *
 * Revision 1.1.1.1  2001/08/20 22:32:04  gfx
 * Jext 3.0pre5
 *
 * Revision 1.1.1.1  2001/04/11 14:22:28  gfx
 *
 * Jext 2.11: GUI customization, bug fixes
 *
 * Revision 1.20  1999/12/13 03:40:29  sp
 * Bug fixes, syntax is now mostly GPL'd
 *
 * Revision 1.19  1999/07/16 23:45:49  sp
 * 1.7pre6 BugFree version
 *
 * Revision 1.18  1999/07/05 04:38:39  sp
 * Massive batch of changes... bug fixes, also new text component is in place.
 * Have fun
 *
 * Revision 1.17  1999/06/20 02:15:45  sp
 * Syntax coloring optimizations
 *
 * Revision 1.16  1999/06/05 00:22:58  sp
 * LGPL'd syntax package
 *
 * Revision 1.15  1999/06/03 08:24:13  sp
 * Fixing broken CVS
 *
 * Revision 1.16  1999/05/31 08:11:10  sp
 * Syntax coloring updates, expand abbrev bug fix
 *
 * Revision 1.15  1999/05/31 04:38:51  sp
 * Syntax optimizations, HyperSearch for Selection added (Mike Dillon)
 *
 * Revision 1.14  1999/04/19 05:38:20  sp
 * Syntax API changes
 *
 * Revision 1.13  1999/03/26 05:13:04  sp
 * Enhanced menu item updates
 *
 * Revision 1.12  1999/03/13 08:50:39  sp
 * Syntax colorizing updates and cleanups, general code reorganizations
 *
 * Revision 1.11  1999/03/13 00:09:07  sp
 * Console updates, uncomment removed cos it's too buggy, cvs log tags added
 *
 * Revision 1.10  1999/03/12 23:51:00  sp
 * Console updates, uncomment removed cos it's too buggy, cvs log tags added
 *
 */
