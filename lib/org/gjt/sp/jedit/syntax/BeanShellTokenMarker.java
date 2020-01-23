/*
 * BeanShellTokenMarker.java - BeanShell token marker
 * Copyright (C) 1999, 2000 Slava Pestov
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
 * BeanShell (www.beanshell.org) token marker.
 *
 * @author Slava Pestov
 * @version $Id: BeanShellTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:18 gfx Exp $
 */
public class BeanShellTokenMarker extends CTokenMarker
{
	public BeanShellTokenMarker()
	{
		super(false,false,getKeywords());
	}

	public static KeywordMap getKeywords()
	{
		if(bshKeywords == null)
		{
			bshKeywords = new KeywordMap(false);
			bshKeywords.add("import",Token.KEYWORD2);
			bshKeywords.add("byte",Token.KEYWORD3);
			bshKeywords.add("char",Token.KEYWORD3);
			bshKeywords.add("short",Token.KEYWORD3);
			bshKeywords.add("int",Token.KEYWORD3);
			bshKeywords.add("long",Token.KEYWORD3);
			bshKeywords.add("float",Token.KEYWORD3);
			bshKeywords.add("double",Token.KEYWORD3);
			bshKeywords.add("boolean",Token.KEYWORD3);
			bshKeywords.add("void",Token.KEYWORD3);
			bshKeywords.add("break",Token.KEYWORD1);
			bshKeywords.add("case",Token.KEYWORD1);
			bshKeywords.add("continue",Token.KEYWORD1);
			bshKeywords.add("default",Token.KEYWORD1);
			bshKeywords.add("do",Token.KEYWORD1);
			bshKeywords.add("else",Token.KEYWORD1);
			bshKeywords.add("for",Token.KEYWORD1);
			bshKeywords.add("if",Token.KEYWORD1);
			bshKeywords.add("instanceof",Token.KEYWORD1);
			bshKeywords.add("new",Token.KEYWORD1);
			bshKeywords.add("return",Token.KEYWORD1);
			bshKeywords.add("switch",Token.KEYWORD1);
			bshKeywords.add("while",Token.KEYWORD1);
			bshKeywords.add("throw",Token.KEYWORD1);
			bshKeywords.add("try",Token.KEYWORD1);
			bshKeywords.add("catch",Token.KEYWORD1);
			bshKeywords.add("finally",Token.KEYWORD1);
			bshKeywords.add("this",Token.LITERAL2);
			bshKeywords.add("null",Token.LITERAL2);
			bshKeywords.add("true",Token.LITERAL2);
			bshKeywords.add("false",Token.LITERAL2);
		}
		return bshKeywords;
	}

	// private members
	private static KeywordMap bshKeywords;
}

/*
 * ChangeLog:
 * $Log: BeanShellTokenMarker.java,v $
 * Revision 1.1.1.1  2004/10/19 16:16:18  gfx
 * no message
 *
 * Revision 1.1.1.1  2001/08/20 22:32:04  gfx
 * Jext 3.0pre5
 *
 * Revision 1.1.1.1  2001/04/11 14:22:29  gfx
 *
 * Jext 2.11: GUI customization, bug fixes
 *
 * Revision 1.1  2000/01/29 10:12:43  sp
 * BeanShell edit mode, bug fixes
 *
 */
