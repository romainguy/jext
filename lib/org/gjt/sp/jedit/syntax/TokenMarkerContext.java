/*
 * TokenMarkerContext.java
 * Copyright (c) 1999 André Kaplan
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.gjt.sp.jedit.syntax;

import javax.swing.text.Segment;

import gnu.regexp.*;

/**
 * This class contains the Context of a Token Marker that can be passed along 
 * nested Token Markers through a method like
 *   markTokensImpl(MyToken token, TokenMarkerContext ctx) : MyToken
 * where MyToken represents the token state at the end of the marked line.
 *
 * It contains useful infos such as last offset and last keyword and the pos in the marked line
 * It also provides utility functions that are often used in TokenMarkers such as
 * addTokenToPos, addTokenToEnd
 * doKeywordToPos, doKeywordToEnd
 *
 * @author  Andre Kaplan
 * @version 0.6
 */
public class TokenMarkerContext
{
	public TokenMarkerContext(Segment line,
							  int lineIndex,
							  TokenMarkerWithAddToken marker,
							  TokenMarker.LineInfo[] lineInfo)
	{
		this.line        = line;
		this.lineIndex   = lineIndex;
		this.marker      = marker;

		this.array       = line.array;
		this.offset      = line.offset;
		this.lastOffset  = line.offset;
		this.lastKeyword = line.offset;
		this.length      = line.offset + line.count;

		this.pos         = line.offset;

		if (lineInfo != null)
		{
			this.currLineInfo = lineInfo[lineIndex];
			this.prevLineInfo = ((lineIndex == 0) ? null : lineInfo[lineIndex - 1]);
		}
	}

	public TokenMarkerContext(Segment line,
							  int lineIndex,
							  TokenMarkerWithAddToken marker)
	{
		this(line, lineIndex, marker, null);
	}

	public boolean atFirst()
	{
		return (this.pos == line.offset);
	}

	public boolean hasMoreChars()
	{
		return (this.pos < this.length);
	}

	public int remainingChars()
	{
		return ((this.length - 1) - this.pos);
	}

	public char getChar()
	{
		return this.array[this.pos];
	}

	public char getChar(int inc)
	{
		return this.array[this.pos + inc];
	}

	public char lastChar()
	{
		return this.array[this.length - 1];
	}

	public void addToken(int length, byte id)
	{
		this.marker.addToken(length, id);
	}

	/**
	 * Adds a token to the position specified (position exclusive)
	 * The character at pos index is excluded
	 */
	public void addTokenToPos(byte id)
	{
		if (this.pos > this.lastOffset)
		{
			this.addToken(this.pos - this.lastOffset, id);
			this.lastOffset = this.lastKeyword = this.pos;
		}
	}

	public void addTokenToPos(int pos, byte id)
	{
		if (pos > this.lastOffset)
		{
			this.addToken(pos - this.lastOffset, id);
			this.lastOffset = this.lastKeyword = pos;
		}
	}

	// *************
	// addTokenToEnd
	// *************
	public void addTokenToEnd(byte id)
	{
		if (this.length > this.lastOffset)
		{
			this.addToken(this.length - this.lastOffset, id);
			this.pos = this.lastOffset = this.lastKeyword = this.length;
		}
	}

	// **************
	// doKeywordToPos
	// **************
	public byte doKeywordToPos(int pos, KeywordMap keywords)
	{
		int len = pos - this.lastKeyword;
		byte id = keywords.lookup(this.line, this.lastKeyword, len);

		if  (id != Token.NULL)
		{
			this.addTokenToPos(this.lastKeyword, Token.NULL);
			this.addTokenToPos(pos, id);
		}
		this.lastKeyword = pos + 1;
		return id;
	}

	// **************
	// doKeywordToPos
	// **************
	public byte doKeywordToPos(KeywordMap keywords)
	{
		int len = this.pos - this.lastKeyword;
		byte id = keywords.lookup(this.line, this.lastKeyword, len);

		if  (id != Token.NULL)
		{
			this.addTokenToPos(this.lastKeyword, Token.NULL);
			this.addTokenToPos(this.pos, id);
		}
		this.lastKeyword = this.pos + 1;
		return id;
	}

	// **************
	// doKeywordToEnd
	// **************
	public byte doKeywordToEnd(KeywordMap keywords)
	{
		return this.doKeywordToPos(this.length, keywords);
	}

	public boolean regionMatches(boolean ignoreCase, String match)
	{
		return SyntaxUtilities.regionMatches(ignoreCase, this.line, this.pos, match);
	}

	public REMatch RERegionMatches(RE match)
	{
		return RERegionMatches(this.line, this.pos, match);
	}

	/**
	 * Checks if a subregion of a <code>Segment</code> matches a regular expression.
     * The match-beginning operator (^) in the regular expression matches segment at position offset
	 * This function is intended to be part of SyntaxUtilities
	 * @param text The segment
	 * @param offset The offset into the segment
	 * @param match The regular expression to match
	 */
	private static REMatch RERegionMatches(Segment text, int offset, RE match)
	{
		try
		{
			String s = String.copyValueOf(text.array, offset, text.count - (offset - text.offset));
			return match.getMatch(s, 0, RE.REG_ANCHORINDEX);
			// BUG: The following may throw exceptions
			// I guess it's a thread safety issue since text.array may point to nowhere before getMatch has completed
			// If anyone knows about it...
			// char[] textArray = text.array;
			// return match.getMatch(textArray, offset, RE.REG_ANCHORINDEX);
		}
		catch (IllegalArgumentException iae)
		{
			return null;
		}
	}

	public String toString() {
		String res = 
			  "Line: " + (this.lineIndex + 1) /* Line numbers start from 1*/
			+ ", pos:" + (this.pos  - this.offset) + "\n";
		res += new String(line.array, line.offset, line.count);
		res += "\n";
		int spacerLen = this.pos - this.offset;
		StringBuffer spacer = new StringBuffer(spacerLen + 2);
		for (int i = 0; i < spacerLen; i++) {
			spacer.append('.');
		}
		spacer.append('^');
		spacer.append('\n');
		res += spacer.toString();
		return res;
	}

	public Segment line;
	public int     lineIndex;
	public char[]  array;
	public int     offset;
	public int     lastOffset;
	public int     lastKeyword;
	public int     length;

	public int     pos;

	private TokenMarkerWithAddToken marker;
	public TokenMarker.LineInfo prevLineInfo;
	public TokenMarker.LineInfo currLineInfo;
}

