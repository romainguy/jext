/*
 * VerilogTokenMarker.java - Verilog token marker
 * Copyright (C) 2000 Bogdan Mitu
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package org.gjt.sp.jedit.syntax;

import javax.swing.text.Segment;

/**
 * Verilog token marker.
 *
 * @author Bogdan Mitu
 * @version $Id: VerilogTokenMarker.java,v 1.1.1.1 2004/10/19 16:16:21 gfx Exp $
 */
public class VerilogTokenMarker extends TokenMarker
{
    static final int NORMAL = 0;
    static final int SIMPLE_QUOTE = 1;
    static final int BACK_ACCENT = 2;
    static final int DOLLAR = 3;
    
    int env;
    
	public VerilogTokenMarker()
	{
		this( getKeywords());
	}

    public VerilogTokenMarker( KeywordMap keywords)
    {
        this.keywords = keywords;
    }

	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		char[] array = line.array;
		int offset = line.offset;
		lastOffset = offset;
		lastKeyword = offset;
		int length = line.count + offset;
		boolean backslash = false;
		boolean keyChar = false;
		env = NORMAL;

loop:	for(int i = offset; i < length; i++)
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
                    else
                    {
                        doKeyword(line,i,c);
                        addToken( i - lastOffset,token);
                        addToken( 1, Token.KEYWORD1);
                        lastOffset = lastKeyword = i+1;
                    }
                    break;
                case '@':
                    if(backslash)
                        backslash = false;
                    else
                    {
                        keyChar = true;
                        doKeyword(line,i,c);
                        addToken(i - lastOffset,token);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i;
                    }
                    break;
                case '$':
                    doKeyword(line,i,c);
                    if(backslash)
                        backslash = false;
                    else
                    {
                        env = DOLLAR;
                        addToken(i - lastOffset,token);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i;
                    }
                    break;
                case '`':
                    doKeyword(line,i,c);
                    if(backslash)
                        backslash = false;
                    else
                    {
                        env = BACK_ACCENT;
                        addToken(i - lastOffset,token);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i;
                    }
                    break;
                case '\'':
                    doKeyword(line,i,c);
                    if(backslash)
                        backslash = false;
                    else
                    {
                        env = SIMPLE_QUOTE;
                        addToken(i - lastOffset,token);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i;
                    }
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
				case '/':
					backslash = false;
					doKeyword(line,i,c);
					if(length - i > 1)
					{
						switch(array[i1])
						{
						case '*':
							addToken(i - lastOffset,token);
							lastOffset = lastKeyword = i;
							if(length - i > 2 && array[i+2] == '*')
								token = Token.COMMENT2;
							else
								token = Token.COMMENT1;
							break;
						case '/':
							addToken(i - lastOffset,token);
							addToken(length - i,Token.COMMENT1);
							lastOffset = lastKeyword = length;
							break loop;
						}
					}
					break;
				default:
					backslash = false;
                    if( keyChar) {
                        keyChar = false;
                        doKeyword(line,i,c);
                        addToken(i1 - lastOffset,token);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i1;
                    }
   					else if(!Character.isLetterOrDigit(c)
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
                    env = NORMAL;
				}
				break;
            case Token.LITERAL2:
                if(!Character.isLetterOrDigit(c) 
                    && c != '_')
                {
                    addToken(i1 - lastOffset, Token.LITERAL2);
                    token = Token.NULL;
                    lastOffset = lastKeyword = i1;
                    env = NORMAL;
                }
                break;
 
            case Token.KEYWORD2:
                if(!Character.isLetterOrDigit(c) 
                    && c != '_')
                {
                    addToken(i1 - lastOffset,Token.KEYWORD2);
                    token = Token.NULL;
                    lastOffset = lastKeyword = i1;
                    env = NORMAL;
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
		case Token.LITERAL2:
		case Token.KEYWORD2:
			addToken(length - lastOffset,token);
			if(!backslash)
				token = Token.NULL;
            break;
		default:
			addToken(length - lastOffset,token);
			break;
		}

		return token;
	}

	public static KeywordMap getKeywords()
	{
		if(vKeywords == null)
		{
			vKeywords = new KeywordMap(false);
			vKeywords.add("reg",Token.KEYWORD3);
			vKeywords.add("wire",Token.KEYWORD3);
			vKeywords.add("wand",Token.KEYWORD3);
			vKeywords.add("wor",Token.KEYWORD3);
			vKeywords.add("integer",Token.KEYWORD3);
			vKeywords.add("parameter",Token.KEYWORD3);
			vKeywords.add("integer",Token.KEYWORD3);
			vKeywords.add("real",Token.KEYWORD3);
			vKeywords.add("time",Token.KEYWORD3);
			vKeywords.add("realtime",Token.KEYWORD3);
			vKeywords.add("event",Token.KEYWORD3);
            
			vKeywords.add("input",Token.KEYWORD1);
			vKeywords.add("output",Token.KEYWORD1);
			vKeywords.add("inout",Token.KEYWORD1);
            
			vKeywords.add("module",Token.KEYWORD1);
			vKeywords.add("endmodule",Token.KEYWORD1);
            
			vKeywords.add("assign",Token.KEYWORD1);
			vKeywords.add("always",Token.KEYWORD1);
			vKeywords.add("posedge",Token.KEYWORD1);
			vKeywords.add("negedge",Token.KEYWORD1);
			vKeywords.add("initial",Token.KEYWORD1);
			vKeywords.add("forever",Token.KEYWORD1);
			vKeywords.add("while",Token.KEYWORD1);
			vKeywords.add("for",Token.KEYWORD1);
			vKeywords.add("if",Token.KEYWORD1);
			vKeywords.add("else",Token.KEYWORD1);
			vKeywords.add("case",Token.KEYWORD1);
			vKeywords.add("casex",Token.KEYWORD1);
			vKeywords.add("casez",Token.KEYWORD1);
			vKeywords.add("default",Token.KEYWORD1);
			vKeywords.add("endcase",Token.KEYWORD1);
			vKeywords.add("or",Token.KEYWORD1);
            
			vKeywords.add("#",Token.KEYWORD1);
			vKeywords.add("@",Token.KEYWORD1);
            
			vKeywords.add("begin",Token.KEYWORD1);
			vKeywords.add("end",Token.KEYWORD1);
			vKeywords.add("fork",Token.KEYWORD1);
			vKeywords.add("join",Token.KEYWORD1);
			vKeywords.add("wait",Token.KEYWORD1);

			vKeywords.add("function",Token.KEYWORD1);
			vKeywords.add("endfunction",Token.KEYWORD1);
			vKeywords.add("task",Token.KEYWORD1);
			vKeywords.add("endtask",Token.KEYWORD1);

			vKeywords.add("$display",Token.KEYWORD2);
			vKeywords.add("$write",Token.KEYWORD2);
			vKeywords.add("$time",Token.KEYWORD2);
			vKeywords.add("$monitor",Token.KEYWORD2);
			vKeywords.add("$finish",Token.KEYWORD2);
			vKeywords.add("$readmemb",Token.KEYWORD2);
			vKeywords.add("$readmemh",Token.KEYWORD2);
			vKeywords.add("$stop",Token.KEYWORD2);

			vKeywords.add("$define_group_waves",Token.KEYWORD2);
			vKeywords.add("$gr_waves_memsize",Token.KEYWORD2);
			vKeywords.add("$gr_waves",Token.KEYWORD2);
            
			vKeywords.add("`include",Token.KEYWORD2);
			vKeywords.add("`define",Token.KEYWORD2);
			vKeywords.add("`ifdef",Token.KEYWORD2);
			vKeywords.add("`define",Token.KEYWORD2);
		}
		return vKeywords;
	}

	// private members
	private static KeywordMap vKeywords;
	private boolean cpp;
	private KeywordMap keywords;
	private int lastOffset;
	private int lastKeyword;

	private boolean doKeyword(Segment line, int i, char c)
	{
		int i1 = i+1;
		int len = i - lastKeyword;
		byte id = keywords.lookup(line,lastKeyword,len);
       
		switch( env) 
		{
            case BACK_ACCENT:
                if(id == Token.NULL) 
                    id = Token.LITERAL2;
                break;
            case SIMPLE_QUOTE:
                id = Token.LITERAL2;
                lastKeyword = lastKeyword + 2;
                len = len - 2;
            default: 
        }
 
        if(id != Token.NULL)
        {
            if(lastKeyword != lastOffset)
                addToken(lastKeyword - lastOffset,Token.NULL);
            addToken(len,id);
            lastOffset = i;
        }
        lastKeyword = i1;
        env = NORMAL;
        return false;
	}
}

// End of VerilogTokenMarker.java
