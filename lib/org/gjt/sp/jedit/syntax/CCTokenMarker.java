/*
 * CCTokenMarker.java - C++ token marker
 * Copyright (C) 1999 Slava Pestov
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
 * C++ token marker.
 *
 * @author Slava Pestov
 * @version $Id: CCTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:18 gfx Exp $
 */
public class CCTokenMarker extends CTokenMarker
{
	public CCTokenMarker()
	{
		super(true,false,getKeywords());
	}

	public static KeywordMap getKeywords()
	{
		if(ccKeywords == null)
		{
			ccKeywords = new KeywordMap(false);

			ccKeywords.add("and", Token.KEYWORD3);
			ccKeywords.add("and_eq", Token.KEYWORD3);
			ccKeywords.add("asm", Token.KEYWORD2);         //
			ccKeywords.add("auto", Token.KEYWORD1);        //
			ccKeywords.add("bitand", Token.KEYWORD3);
			ccKeywords.add("bitor", Token.KEYWORD3);
			ccKeywords.add("bool",Token.KEYWORD3);
			ccKeywords.add("break", Token.KEYWORD1);	   //
			ccKeywords.add("case", Token.KEYWORD1);		   //
			ccKeywords.add("catch", Token.KEYWORD1);
			ccKeywords.add("char", Token.KEYWORD3);		   //
			ccKeywords.add("class", Token.KEYWORD3);
			ccKeywords.add("compl", Token.KEYWORD3);
			ccKeywords.add("const", Token.KEYWORD1);	   //
			ccKeywords.add("const_cast", Token.KEYWORD3);
			ccKeywords.add("continue", Token.KEYWORD1);	   //
			ccKeywords.add("default", Token.KEYWORD1);	   //
			ccKeywords.add("delete", Token.KEYWORD1);
			ccKeywords.add("do",Token.KEYWORD1);           //
			ccKeywords.add("double" ,Token.KEYWORD3);	   //
			ccKeywords.add("dynamic_cast", Token.KEYWORD3);
			ccKeywords.add("else", 	Token.KEYWORD1);	   //
			ccKeywords.add("enum",  Token.KEYWORD3);	   //
			ccKeywords.add("explicit", Token.KEYWORD1);			
			ccKeywords.add("export", Token.KEYWORD2);
			ccKeywords.add("extern", Token.KEYWORD2);	   //
			ccKeywords.add("false", Token.LITERAL2);
			ccKeywords.add("float", Token.KEYWORD3);	   //
			ccKeywords.add("for", Token.KEYWORD1);		   //
			ccKeywords.add("friend", Token.KEYWORD1);			
			ccKeywords.add("goto", Token.KEYWORD1);        //
			ccKeywords.add("if", Token.KEYWORD1);		   //
			ccKeywords.add("inline", Token.KEYWORD1);
			ccKeywords.add("int", Token.KEYWORD3);		   //
			ccKeywords.add("long", Token.KEYWORD3);		   //
			ccKeywords.add("mutable", Token.KEYWORD3);
			ccKeywords.add("namespace", Token.KEYWORD2);
			ccKeywords.add("new", Token.KEYWORD1);
			ccKeywords.add("not", Token.KEYWORD3);
			ccKeywords.add("not_eq", Token.KEYWORD3);
			ccKeywords.add("operator", Token.KEYWORD3);
			ccKeywords.add("or", Token.KEYWORD3);
			ccKeywords.add("or_eq", Token.KEYWORD3);
			ccKeywords.add("private", Token.KEYWORD1);
			ccKeywords.add("protected", Token.KEYWORD1);
			ccKeywords.add("public", Token.KEYWORD1);
			ccKeywords.add("register", Token.KEYWORD1);
			ccKeywords.add("reinterpret_cast", Token.KEYWORD3);
			ccKeywords.add("return", Token.KEYWORD1);      //
			ccKeywords.add("short", Token.KEYWORD3);	   //
			ccKeywords.add("signed", Token.KEYWORD3);	   //
			ccKeywords.add("sizeof", Token.KEYWORD1);	   //
			ccKeywords.add("static", Token.KEYWORD1);	   //
			ccKeywords.add("static_cast", Token.KEYWORD3);
			ccKeywords.add("struct", Token.KEYWORD3);	   //
			ccKeywords.add("switch", Token.KEYWORD1);	   //
			ccKeywords.add("template", Token.KEYWORD3);
			ccKeywords.add("this", Token.LITERAL2);
			ccKeywords.add("throw", Token.KEYWORD1);
			ccKeywords.add("true", Token.LITERAL2);
			ccKeywords.add("try", Token.KEYWORD1);
			ccKeywords.add("typedef", Token.KEYWORD3);	   //
			ccKeywords.add("typeid", Token.KEYWORD3);
			ccKeywords.add("typename", Token.KEYWORD3);
			ccKeywords.add("union", Token.KEYWORD3);	   //
			ccKeywords.add("unsigned", Token.KEYWORD3);	   //
			ccKeywords.add("using", Token.KEYWORD2);
			ccKeywords.add("virtual", Token.KEYWORD1);
			ccKeywords.add("void", Token.KEYWORD1);		   //
			ccKeywords.add("volatile", Token.KEYWORD1);	   //
			ccKeywords.add("wchar_t", Token.KEYWORD3);
			ccKeywords.add("while", Token.KEYWORD1);	   //
			ccKeywords.add("xor", Token.KEYWORD3);
			ccKeywords.add("xor_eq", Token.KEYWORD3);            

			// non ANSI keywords
			ccKeywords.add("NULL", Token.LITERAL2);
		}
		return ccKeywords;
	}

	// private members
	private static KeywordMap ccKeywords;
}

/*
 * ChangeLog:
 * $Log: CCTokenMarker.java,v $
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
 * Revision 1.7  2000/01/29 10:12:43  sp
 * BeanShell edit mode, bug fixes
 *
 * Revision 1.6  1999/12/13 03:40:29  sp
 * Bug fixes, syntax is now mostly GPL'd
 *
 * Revision 1.5  1999/06/05 00:22:58  sp
 * LGPL'd syntax package
 *
 * Revision 1.4  1999/05/14 04:56:15  sp
 * Docs updated, default: fix in C/C++/Java mode, full path in title bar toggle
 *
 * Revision 1.3  1999/04/22 06:03:26  sp
 * Syntax colorizing change
 *
 * Revision 1.2  1999/04/21 05:00:46  sp
 * New splash screen!!!!!!!!!!! Also, Juha has sent in a new keyword set for C++
 *
 * Revision 1.1  1999/03/13 09:11:46  sp
 * Syntax code updates, code cleanups
 *
 */
