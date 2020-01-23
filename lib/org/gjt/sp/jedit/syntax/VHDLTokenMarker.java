/*
 * VHDLTokenMarker.java - VHDL token marker
 * Copyright (C) 2000 Bogdan Mitu
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
 * VHDL token marker.
 *
 * @author Bogdan Mitu
 * @version $Id: VHDLTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:21 gfx Exp $
 */
public class VHDLTokenMarker extends TokenMarker
{
	public VHDLTokenMarker()
	{
		this.keywords = getKeywords();
	}

	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		char[] array = line.array;
		int offset = line.offset;
		lastOffset = offset;
		lastKeyword = offset;
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
				case '#':
					if(backslash)
						backslash = false;
					break;
				case '"':
					doKeyword(line,i,c);
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						token = Token.LITERAL1;
						lastOffset = lastKeyword = i;
					}
					break;
				case ':':
					if(lastKeyword == offset)
					{
						if(doKeyword(line,i,c))
							break;
						backslash = false;
						addToken(i1 - lastOffset,Token.LABEL);
						lastOffset = lastKeyword = i1;
					}
					else if(doKeyword(line,i,c))
						break;
					break;
				case '-':
					backslash = false;
					doKeyword(line,i,c);
					if(length - i > 1)
					{
						switch(array[i1])
						{
						case '*':
							addToken(i - lastOffset,token);
							lastOffset = lastKeyword = i;
							token = Token.COMMENT1;
							break;
						case '-':
							addToken(i - lastOffset,token);
							addToken(length - i,Token.COMMENT1);
							lastOffset = lastKeyword = length;
							break loop;
						}
					}
					break;
				default:
					backslash = false;
					if(!Character.isLetterOrDigit(c)
						&& c != '_')
						doKeyword(line,i,c);
					break;
				}
				break;
			case Token.COMMENT1:
			case Token.COMMENT2:
				backslash = false;
				if(c == '*' && length - i > 1)
				{
					if(array[i1] == '/')
					{
						i++;
						addToken((i+1) - lastOffset,token);
						token = Token.NULL;
						lastOffset = lastKeyword = i+1;
					}
				}
				break;
			case Token.LITERAL1:
				if(backslash)
					backslash = false;
				else if(c == '"')
				{
					addToken(i1 - lastOffset,token);
					token = Token.NULL;
					lastOffset = lastKeyword = i1;
				}
				break;
			case Token.LITERAL2:
				if(backslash)
					backslash = false;
				else if(c == '\'')
				{
					addToken(i1 - lastOffset,Token.LITERAL1);
					token = Token.NULL;
					lastOffset = lastKeyword = i1;
				}
				break;
			default:
				throw new InternalError("Invalid state: "
					+ token);
			}
		}

		if(token == Token.NULL)
			doKeyword(line,length,'\0');

		switch(token)
		{
		case Token.LITERAL1:
		case Token.LITERAL2:
			addToken(length - lastOffset,Token.INVALID);
			token = Token.NULL;
			break;
		case Token.KEYWORD2:
			addToken(length - lastOffset,token);
			if(!backslash)
				token = Token.NULL;
		default:
			addToken(length - lastOffset,token);
			break;
		}

		return token;
	}

	public static KeywordMap getKeywords()
	{
   		if(vhdlKeywords == null)
		{
            vhdlKeywords = new KeywordMap( true);
            vhdlKeywords.add("char",Token.KEYWORD3);
            vhdlKeywords.add("double",Token.KEYWORD3);
            vhdlKeywords.add("enum",Token.KEYWORD3);
            vhdlKeywords.add("real",Token.KEYWORD3);
            vhdlKeywords.add("integer",Token.KEYWORD3);
            vhdlKeywords.add("natural",Token.KEYWORD3);
            vhdlKeywords.add("text",Token.KEYWORD3);
            vhdlKeywords.add("boolean",Token.KEYWORD3);
            vhdlKeywords.add("line",Token.KEYWORD3);
            vhdlKeywords.add("string",Token.KEYWORD3);
            
            vhdlKeywords.add("bit",Token.KEYWORD3);
            vhdlKeywords.add("bit_vector",Token.KEYWORD3);
            vhdlKeywords.add("std_logic",Token.KEYWORD3);
            vhdlKeywords.add("std_logic_vector",Token.KEYWORD3);

            vhdlKeywords.add("if",Token.KEYWORD1);
            vhdlKeywords.add("then",Token.KEYWORD1);
            vhdlKeywords.add("elsif",Token.KEYWORD1);
            vhdlKeywords.add("else",Token.KEYWORD1);
            vhdlKeywords.add("begin",Token.KEYWORD1);
            vhdlKeywords.add("end",Token.KEYWORD1);
            vhdlKeywords.add("for",Token.KEYWORD1);
            vhdlKeywords.add("while",Token.KEYWORD1);
            vhdlKeywords.add("loop",Token.KEYWORD1);
            vhdlKeywords.add("when",Token.KEYWORD1);
            vhdlKeywords.add("after",Token.KEYWORD1);
            vhdlKeywords.add("wait",Token.KEYWORD1);
            vhdlKeywords.add("function",Token.KEYWORD1);
            vhdlKeywords.add("procedure",Token.KEYWORD1);
            vhdlKeywords.add("case",Token.KEYWORD1);
            vhdlKeywords.add("default",Token.KEYWORD1);
            vhdlKeywords.add("transport",Token.KEYWORD1);
            vhdlKeywords.add("and",Token.KEYWORD1);
            vhdlKeywords.add("or",Token.KEYWORD1);
            vhdlKeywords.add("not",Token.KEYWORD1);
            vhdlKeywords.add("xor",Token.KEYWORD1);
            vhdlKeywords.add("entity",Token.KEYWORD1);
            vhdlKeywords.add("architecture",Token.KEYWORD1);
            vhdlKeywords.add("port",Token.KEYWORD1);
            vhdlKeywords.add("in",Token.KEYWORD1);
            vhdlKeywords.add("out",Token.KEYWORD1);
            vhdlKeywords.add("inout",Token.KEYWORD1);
            vhdlKeywords.add("map",Token.KEYWORD1);
            vhdlKeywords.add("component",Token.KEYWORD1);
            vhdlKeywords.add("of",Token.KEYWORD1);
            vhdlKeywords.add("on",Token.KEYWORD1);
            vhdlKeywords.add("is",Token.KEYWORD1);
            vhdlKeywords.add("process",Token.KEYWORD1);
            vhdlKeywords.add("return",Token.KEYWORD1);
            vhdlKeywords.add("to",Token.KEYWORD1);
            vhdlKeywords.add("downto",Token.KEYWORD1);
            vhdlKeywords.add("alias",Token.KEYWORD1);
            vhdlKeywords.add("variable",Token.KEYWORD1);
            vhdlKeywords.add("signal",Token.KEYWORD1);
            vhdlKeywords.add("constant",Token.KEYWORD1);
            vhdlKeywords.add("generic",Token.KEYWORD1); 
            vhdlKeywords.add("range",Token.KEYWORD1); 
            vhdlKeywords.add("event",Token.KEYWORD1); 
            vhdlKeywords.add("file",Token.KEYWORD1);
            vhdlKeywords.add("time",Token.KEYWORD1);
            vhdlKeywords.add("all",Token.KEYWORD1);
            vhdlKeywords.add("package",Token.KEYWORD1);
            vhdlKeywords.add("use",Token.KEYWORD1);
            vhdlKeywords.add("library",Token.KEYWORD1);
            
            vhdlKeywords.add("true",Token.LITERAL2);
            vhdlKeywords.add("false",Token.LITERAL2);

            vhdlKeywords.add("NULL",Token.LITERAL2);
		}
		return vhdlKeywords;
	}

    public static final int AS_IS = 0;
    public static final int LOWER_CASE = 1;
    public static final int UPPER_CASE = 2;

	// private members
	private static KeywordMap vhdlKeywords;
	private KeywordMap keywords;
	private int lastOffset;
	private int lastKeyword;
	// to be used by a future plugin
	private int keywordCase = AS_IS;
	private boolean allLowerCase = false;

	private boolean doKeyword( Segment line, int i, char c)
	{
		int i1 = i+1;
		int len = i - lastKeyword;
        
		int txtOffset = lastKeyword;
		int n = i;
        
		byte id = keywords.lookup(line,lastKeyword,len);
		if(id != Token.NULL)
		{
			if(lastKeyword != lastOffset)
				addToken(lastKeyword - lastOffset,Token.NULL);
                              
			addToken(len,id);
			lastOffset = i;

            if( keywordCase == LOWER_CASE || allLowerCase == true ) 
            {
                char[] txt = line.array;
                for (int j = txtOffset; j < n; j++) 
                {
                    txt [j] = Character.toLowerCase( txt [j]); 
                }
            }
            else if( keywordCase == UPPER_CASE) 
            {
                char[] txt = line.array;
                for (int j = txtOffset; j < n; j++) 
                {
                    txt [j] = Character.toUpperCase( txt [j]); 
                }
            }
		}
		lastKeyword = i1;
		return false;
	}
	
	// to be used by a future plugin
	public void setKeywordCase( int c)
	{
		keywordCase = c;
	}
	
	// to be used by a future plugin
	public int getKeywordCase()
	{
		return keywordCase;
	}

	// to be used by a future plugin    
	public void setAllLowerCase( boolean b)
	{
		allLowerCase = b;
	}
    
	// to be used by a future plugin    
	public boolean getAllLowerCase()
	{
		return allLowerCase;
	}

}

// End of VHDLTokenMarker.java
