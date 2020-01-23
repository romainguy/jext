/*
 * PikeTokenMarker.java - Java token marker
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
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.gjt.sp.jedit.syntax;

import javax.swing.text.Segment;

/**
 * Pike token marker.
 *
 * @author Romain Guy
 */

public class PikeTokenMarker extends CTokenMarker
{
	public PikeTokenMarker()
	{
		super(true, false, getKeywords());
	}

	public static KeywordMap getKeywords()
	{
		if (pikeKeywords == null)
		{
			pikeKeywords = new KeywordMap(false);
			pikeKeywords.add("array", Token.KEYWORD3);
			pikeKeywords.add("break", Token.KEYWORD1);
			pikeKeywords.add("case", Token.KEYWORD1);
			pikeKeywords.add("catch", Token.KEYWORD1);
			pikeKeywords.add("continue", Token.KEYWORD1);
			pikeKeywords.add("default", Token.KEYWORD1);
			pikeKeywords.add("do", Token.KEYWORD1);
			pikeKeywords.add("else", Token.KEYWORD1);
			pikeKeywords.add("float", Token.KEYWORD3);
			pikeKeywords.add("for", Token.KEYWORD1);
			pikeKeywords.add("foreach", Token.KEYWORD1);
			pikeKeywords.add("function", Token.KEYWORD1);
			pikeKeywords.add("gauge", Token.KEYWORD1);
			pikeKeywords.add("if", Token.KEYWORD1);
			pikeKeywords.add("inherit", Token.KEYWORD1);
			pikeKeywords.add("inline", Token.KEYWORD1);
			pikeKeywords.add("int", Token.KEYWORD3);
			pikeKeywords.add("lambda", Token.KEYWORD1);
			pikeKeywords.add("mapping", Token.KEYWORD1);
			pikeKeywords.add("mixed", Token.KEYWORD3);
			pikeKeywords.add("multiset", Token.KEYWORD1);
			pikeKeywords.add("nomask", Token.KEYWORD1);
			pikeKeywords.add("object", Token.KEYWORD3);
			pikeKeywords.add("predef", Token.KEYWORD1);
			pikeKeywords.add("private", Token.KEYWORD1);
			pikeKeywords.add("program", Token.KEYWORD1);
			pikeKeywords.add("protected", Token.KEYWORD1);
			pikeKeywords.add("public", Token.KEYWORD1);
			pikeKeywords.add("return", Token.KEYWORD1);
			pikeKeywords.add("sscanf", Token.KEYWORD1);
			pikeKeywords.add("static", Token.KEYWORD1);
			pikeKeywords.add("string", Token.KEYWORD3);
			pikeKeywords.add("switch", Token.KEYWORD1);
			pikeKeywords.add("typeof", Token.KEYWORD1);
			pikeKeywords.add("varargs", Token.KEYWORD1);
			pikeKeywords.add("void", Token.KEYWORD3);
			pikeKeywords.add("while", Token.KEYWORD1);
		}
		return pikeKeywords;
	}

	// private members
	private static KeywordMap pikeKeywords;
}

