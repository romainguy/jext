/*
 * ASPPerlscriptTokenMarker.java - Perlscript token marker
 * Copyright (C) 1999 Andr? Kaplan
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
 * Original Perl token marker by Slava Pestov
 * Perlscript Token Marker
 *
 * @author  Andre Kaplan
 * @version 0.6
 */
public class ASPPerlscriptTokenMarker
	extends    TokenMarker
	implements TokenMarkerWithAddToken,
			   MultiModeTokenMarkerWithContext
{
	// public members
	public static final byte S_ONE = Token.INTERNAL_FIRST;
	public static final byte S_TWO = (byte)(Token.INTERNAL_FIRST + 1);
	public static final byte S_END = (byte)(Token.INTERNAL_FIRST + 2);

	public ASPPerlscriptTokenMarker()
	{
		this(getKeywords(), true);
	}

	public ASPPerlscriptTokenMarker(boolean standalone)
	{
		this(getKeywords(), standalone);
	}
	public ASPPerlscriptTokenMarker(KeywordMap keywords)
	{
		this(keywords, true);
	}

	public ASPPerlscriptTokenMarker(KeywordMap keywords, boolean standalone)
	{
		this.keywords   = keywords;
		this.standalone = standalone;
	}

	public void addToken(int length, byte id)
	{
		super.addToken(length, id);
	}

	protected byte markTokensImpl(byte _token, Segment line, int lineIndex)
	{
		TokenMarkerContext tokenContext = new TokenMarkerContext(line, lineIndex, this, this.lineInfo);
		
		MultiModeToken prevLineToken = MultiModeToken.NULL;

		if (    tokenContext.prevLineInfo != null 
			 && tokenContext.prevLineInfo.obj != null 
			 && tokenContext.prevLineInfo.obj instanceof MultiModeToken
		)
		{
			prevLineToken = (MultiModeToken)tokenContext.prevLineInfo.obj;
		}

		MultiModeToken res = this.markTokensImpl(prevLineToken, tokenContext);

		tokenContext.currLineInfo.obj = res;
		return res.token;
	}

	// **************
	// markTokensImpl
	// **************
	public MultiModeToken markTokensImpl(final MultiModeToken token, TokenMarkerContext tokenContext)
	{
		MultiModeToken res = new MultiModeToken(token);

		matchChar = '\0';
		matchCharBracket = false;
		matchSpacesAllowed = false;

		int debugPos   = -1;
		int debugCount = 0;

		if (    res.token == Token.LITERAL1 
			 &&	res.obj != null)
		{
			String str = (String)res.obj;
			if (   	str != null 
				 &&	str.length() == tokenContext.line.count
			 	 && tokenContext.regionMatches(false, str))
			{
				tokenContext.addTokenToEnd(res.token);
				res.token = Token.NULL;
				res.obj = null;
				return res;
			} else {
				tokenContext.addTokenToEnd(res.token);
				return res;
			}
		}

		boolean backslash = false;

loop:	for(this.debug.reset(); tokenContext.hasMoreChars(); )
		{
			char c = tokenContext.getChar();

			// Following is a way to detect whether tokenContext.pos is not 
			// correctly incremented. This is for debugging purposes
			if (!this.debug.isOK(tokenContext)) {
				// We got stuck here at some point
				// Log this and increment tokenContext.pos to escape this
				tokenContext.pos++;
			}
	
			if (c == '\\')
			{
				backslash = !backslash;
				tokenContext.pos++;
				continue;
			}

			switch (res.token)
			{
				case Token.NULL:
					if (!this.standalone) {
						if (res.mode == ASPMode.CSPS) {
							if (tokenContext.regionMatches(true, "<%"))
							{
								this.doKeywordToPos(res,tokenContext,c);
								// Just return, let parent mode decide what to do next
								return res;
							}
						}
	
						if (res.mode == ASPMode.ASP) {
							if (tokenContext.regionMatches(true, "%>"))
							{
								this.doKeywordToPos(res,tokenContext,c);
								// Just return, let parent mode decide what to do next
								return res;
							}
						}
	
						if (res.mode == ASPMode.CSPS || res.mode == ASPMode.SSPS) {
							if (tokenContext.regionMatches(true, "</script>"))
							{
								this.doKeywordToPos(res,tokenContext,c);
								// Just return, let parent mode decide what to do next
								return res;
							}
						}
					}
	
					switch(c)
					{
						case '#':
							if (this.doKeywordToPos(res,tokenContext,c))
								break;
							if (backslash) {
								backslash = false;
							} else {
								tokenContext.addTokenToPos(res.token);
								tokenContext.addTokenToEnd(Token.COMMENT1);
								break loop;
							}
							break;
				
							case '=':
								backslash = false;
								if (tokenContext.atFirst())
								{
									res.token = Token.COMMENT2;
									tokenContext.addTokenToEnd(res.token);
									break loop;
								}
								else
									this.doKeywordToPos(res,tokenContext,c);
								break;

							case '$': case '&': case '%': case '@':
								backslash = false;
								if(this.doKeywordToPos(res,tokenContext,c))
									break;
								if (tokenContext.remainingChars() > 0)
								{
									char c1 = tokenContext.getChar(1);
									if  (  (c == '&')
										&& (  (c1 == '&')
										   || (Character.isWhitespace(c1))
										   )
										)
									{
										tokenContext.pos++;
									} else {
										tokenContext.addTokenToPos(res.token);
										res.token = Token.KEYWORD2;
									}
								}
								break;

							case '"':
								if(this.doKeywordToPos(res,tokenContext,c))
									break;
								if(backslash) {
									backslash = false;
								} else {
									tokenContext.addTokenToPos(res.token);
									res.token = Token.LITERAL1;
									res.obj   = null;
								}
								break;

							case '\'':
								if(backslash) {
									backslash = false;
								} else {
									int oldLastKeyword = tokenContext.lastKeyword;
									if(this.doKeywordToPos(res,tokenContext,c))
										break;
									if (tokenContext.pos != oldLastKeyword)
										break;
									tokenContext.addTokenToPos(res.token);
									res.token = Token.LITERAL2;
								}
								break;

							case '`':
								if(this.doKeywordToPos(res,tokenContext,c))
									break;
								if(backslash) {
									backslash = false;
								} else {
									tokenContext.addTokenToPos(res.token);
									res.token = Token.OPERATOR;
								}
								break;

							case '<':
								if(this.doKeywordToPos(res,tokenContext,c))
									break;
								if(backslash)
									backslash = false;
								else
								{
									if (  (tokenContext.remainingChars() > 1)
									   && (tokenContext.getChar(1) == '<')
									   && (!Character.isWhitespace(
									   			tokenContext.getChar(2)))
									   )
									{
										tokenContext.addTokenToPos(res.token);
										res.token = Token.LITERAL1;
										int len = tokenContext.remainingChars() - 1;
										if(tokenContext.lastChar() == ';')
											len--;
										String readin = 
											createReadinString(
												tokenContext.array,
												tokenContext.pos + 2,
												len
											);
										// Log.log(Log.DEBUG, this, "Readin: [" + readin + "]");
										res.obj = readin;
										tokenContext.addTokenToEnd(res.token);
										break loop;
									}
								}
								break;

							case ':':
								backslash = false;
								if(this.doKeywordToPos(res,tokenContext,c))
									break;
								// Doesn't pick up all labels,
								// but at least doesn't mess up
								// XXX::YYY
								if(tokenContext.lastKeyword != 0)
									break;
								tokenContext.pos++;
								tokenContext.addTokenToPos(Token.LABEL);
								continue;

							case '-':
								backslash = false;
								if(this.doKeywordToPos(res,tokenContext,c))
									break;
								if(  (tokenContext.pos != tokenContext.lastKeyword)
								  || (tokenContext.remainingChars() < 1)
								  )
									break;
								switch(tokenContext.getChar(1))
								{
									case 'r': case 'w': case 'x':
									case 'o': case 'R': case 'W':
									case 'X': case 'O': case 'e':
									case 'z': case 's': case 'f':
									case 'd': case 'l': case 'p':
									case 'S': case 'b': case 'c':
									case 't': case 'u': case 'g':
									case 'k': case 'T': case 'B':
									case 'M': case 'A': case 'C':
										tokenContext.addTokenToPos(res.token);
										tokenContext.pos++;
										tokenContext.addTokenToPos(Token.KEYWORD3);
										tokenContext.pos++;
										continue;
								}
								break;

							case '/': case '?':
								if(this.doKeywordToPos(res,tokenContext,c))
									break;
								if(tokenContext.remainingChars() > 0)
								{
									backslash = false;
									char ch = tokenContext.getChar(1);
									if(Character.isWhitespace(ch))
										break;
									matchChar = c;
									matchSpacesAllowed = false;
									tokenContext.addTokenToPos(res.token);
									res.token = S_ONE;
								}
								break;

							default:
								backslash = false;
								if(!Character.isLetterOrDigit(c)
									&& c != '_')
									this.doKeywordToPos(res,tokenContext,c);
								break;
						}
				break;

				case Token.KEYWORD2:
					backslash = false;
					// This test checks for an end-of-variable
					// condition
					if(!Character.isLetterOrDigit(c) && c != '_'
						&& c != '#' && c != '\'' && c != ':'
						&& c != '&')
					{
						// If this is the first character
						// of the variable name ($'aaa)
						// ignore it
						if (!tokenContext.atFirst() && tokenContext.getChar(-1) == '$')
						{
							tokenContext.pos++;
							tokenContext.addTokenToPos(res.token);
							continue;
						}
						// Otherwise, end of variable...
						else
						{
							tokenContext.addTokenToPos(res.token);
							// Wind back so that stuff
							// like $hello$fred is picked
							// up
							// Change the token KEYWORD2 -> NULL
							// And continue at the same position
							res.token = Token.NULL;
							continue;
						}
					}
				break;

				case S_ONE: case S_TWO:
					if(backslash)
						backslash = false;
					else
					{
						if(matchChar == '\0')
						{
							if(Character.isWhitespace(matchChar)
								&& !matchSpacesAllowed)
								break;
							else
								matchChar = c;
						}
						else
						{
							switch(matchChar)
							{
							case '(':
								matchChar = ')';
								matchCharBracket = true;
								break;
							case '[':
								matchChar = ']';
								matchCharBracket = true;
								break;
							case '{':
								matchChar = '}';
								matchCharBracket = true;
								break;
							case '<':
								matchChar = '>';
								matchCharBracket = true;
								break;
							default:
								matchCharBracket = false;
								break;
							}
							if(c != matchChar)
								break;
							if (res.token == S_TWO)
							{
								res.token = S_ONE;
								if(matchCharBracket)
									matchChar = '\0';
							}
							else
							{
								res.token = S_END;
								tokenContext.pos++;
								tokenContext.addTokenToPos(Token.LITERAL2);
								continue;
							}
						}
					}
				break;

				case S_END:
					backslash = false;
					if(!Character.isLetterOrDigit(c)
						&& c != '_')
						this.doKeywordToPos(res,tokenContext,c);
				break;

				case Token.COMMENT2:
					backslash = false;
					if (tokenContext.atFirst())
					{
						if (tokenContext.regionMatches(false, "=cut"))
							res.token = Token.NULL;
						tokenContext.addTokenToEnd(Token.COMMENT2);
						break loop;
					}
				break;

				case Token.LITERAL1:
					if(backslash)
						backslash = false;
					/* else if(c == '$')
						backslash = true; */
					else if (c == '"')
					{
						tokenContext.pos++;
						tokenContext.addTokenToPos(res.token);
						res.token = Token.NULL;
						continue;
					}
				break;

				case Token.LITERAL2:
					if(backslash)
						backslash = false;
					/* else if(c == '$')
						backslash = true; */
					else if(c == '\'')
					{
						tokenContext.pos++;
						tokenContext.addTokenToPos(Token.LITERAL1);
						res.token = Token.NULL;
						continue;
					}
				break;

				case Token.OPERATOR:
					if(backslash)
						backslash = false;
					else if (c == '`')
					{
						tokenContext.pos++;
						tokenContext.addTokenToPos(res.token);
						res.token = Token.NULL;
						continue;
					}
				break;

				default:
					throw new InternalError("Invalid state: "
						+ res.token);
			}

			tokenContext.pos++;
		}

		if(res.token == Token.NULL)
			this.doKeywordToEnd(res, tokenContext, '\0');

		switch (res.token)
		{
			case Token.KEYWORD2:
				tokenContext.addTokenToEnd(res.token);
			break;
	
			case Token.LITERAL2:
				tokenContext.addTokenToEnd(Token.LITERAL1);
			break;

			case S_END:
				tokenContext.addTokenToEnd(Token.LITERAL2);
				res.token = Token.NULL;
			break;

			case S_ONE: case S_TWO:
				tokenContext.addTokenToEnd(Token.INVALID); // XXX
				res.token = Token.NULL;
			break;

			default:
				tokenContext.addTokenToEnd(res.token);
			break;
		}
		return res;
	}

	// private members
	private KeywordMap keywords;
	private boolean    standalone;

	private char    matchChar;
	private boolean matchCharBracket;
	private boolean matchSpacesAllowed;

	private TokenMarkerDebugger debug = new TokenMarkerDebugger();

	// **************
	// doKeywordToEnd
	// **************
	private boolean doKeywordToEnd(MultiModeToken token,
							       TokenMarkerContext tokenContext,
							       char c)
	{
		return doKeyword(token, tokenContext, tokenContext.length, c);
	}

	// **************
	// doKeywordToPos
	// **************
	private boolean doKeywordToPos(MultiModeToken token,
							       TokenMarkerContext tokenContext,
							       char c)
	{
		return doKeyword(token, tokenContext, tokenContext.pos, c);
	}

	// *********
	// doKeyword
	// *********
	private boolean doKeyword(MultiModeToken token,
							  TokenMarkerContext tokenContext,
							  int i,
							  char c)
	{
		int i1 = i + 1;

		if (token.token == S_END)
		{
			tokenContext.addTokenToPos(i, Token.LITERAL2);
			token.token = Token.NULL;
			tokenContext.lastKeyword = i1;
			return false;
		}

		int len = i - tokenContext.lastKeyword;
		byte id = keywords.lookup(tokenContext.line,tokenContext.lastKeyword,len);
		if (id == S_ONE || id == S_TWO) 
		{
			tokenContext.addTokenToPos(tokenContext.lastKeyword, Token.NULL);
			tokenContext.addTokenToPos(i, Token.LITERAL2);
			tokenContext.lastKeyword = i1;
			if(Character.isWhitespace(c))
				matchChar = '\0';
			else
				matchChar = c;
			matchSpacesAllowed = true;
			token.token = id;
			return true;
		}
		else if (id != Token.NULL)
		{
			tokenContext.addTokenToPos(tokenContext.lastKeyword, Token.NULL);
			tokenContext.addTokenToPos(i, id);
		}
		tokenContext.lastKeyword = i1;
		return false;
	}

	// Converts < EOF >, < 'EOF' >, etc to <EOF>
	private String createReadinString(char[] array, int start, int len)
	{
		int idx1 = start;
		int idx2 = start + len - 1;
		while ((idx1 <= idx2) && (!Character.isLetterOrDigit(array[idx1]))) {
			idx1++;
		}

		while((idx1 <= idx2) && (!Character.isLetterOrDigit(array[idx2]))) {
			idx2--;
		}
		
		return new String(array, idx1, idx2 - idx1 + 1);
	}

	private static KeywordMap perlKeywords;

	private static KeywordMap getKeywords()
	{
		if(perlKeywords == null)
		{
			perlKeywords = new KeywordMap(false);
			perlKeywords.add("my",Token.KEYWORD1);
			perlKeywords.add("our",Token.KEYWORD1);
			perlKeywords.add("local",Token.KEYWORD1);
			perlKeywords.add("new",Token.KEYWORD1);
			perlKeywords.add("if",Token.KEYWORD1);
			perlKeywords.add("until",Token.KEYWORD1);
			perlKeywords.add("while",Token.KEYWORD1);
			perlKeywords.add("elsif",Token.KEYWORD1);
			perlKeywords.add("else",Token.KEYWORD1);
			perlKeywords.add("eval",Token.KEYWORD1);
			perlKeywords.add("unless",Token.KEYWORD1);
			perlKeywords.add("foreach",Token.KEYWORD1);
			perlKeywords.add("continue",Token.KEYWORD1);
			perlKeywords.add("exit",Token.KEYWORD1);
			perlKeywords.add("die",Token.KEYWORD1);
			perlKeywords.add("last",Token.KEYWORD1);
			perlKeywords.add("goto",Token.KEYWORD1);
			perlKeywords.add("next",Token.KEYWORD1);
			perlKeywords.add("redo",Token.KEYWORD1);
			perlKeywords.add("goto",Token.KEYWORD1);
			perlKeywords.add("return",Token.KEYWORD1);
			perlKeywords.add("do",Token.KEYWORD1);
			perlKeywords.add("sub",Token.KEYWORD1);
			perlKeywords.add("use",Token.KEYWORD1);
			perlKeywords.add("require",Token.KEYWORD1);
			perlKeywords.add("package",Token.KEYWORD1);
			perlKeywords.add("BEGIN",Token.KEYWORD1);
			perlKeywords.add("END",Token.KEYWORD1);
			perlKeywords.add("eq",Token.OPERATOR);
			perlKeywords.add("ne",Token.OPERATOR);
			perlKeywords.add("gt",Token.OPERATOR);
			perlKeywords.add("lt",Token.OPERATOR);
			perlKeywords.add("le",Token.OPERATOR);
			perlKeywords.add("ge",Token.OPERATOR);
			perlKeywords.add("not",Token.OPERATOR);
			perlKeywords.add("and",Token.OPERATOR);
			perlKeywords.add("or",Token.OPERATOR);
			perlKeywords.add("cmp",Token.OPERATOR);
			perlKeywords.add("xor",Token.OPERATOR);

			perlKeywords.add("abs",Token.KEYWORD3);
			perlKeywords.add("accept",Token.KEYWORD3);
			perlKeywords.add("alarm",Token.KEYWORD3);
			perlKeywords.add("atan2",Token.KEYWORD3);
			perlKeywords.add("bind",Token.KEYWORD3);
			perlKeywords.add("binmode",Token.KEYWORD3);
			perlKeywords.add("bless",Token.KEYWORD3);
			perlKeywords.add("caller",Token.KEYWORD3);
			perlKeywords.add("chdir",Token.KEYWORD3);
			perlKeywords.add("chmod",Token.KEYWORD3);
			perlKeywords.add("chomp",Token.KEYWORD3);
			perlKeywords.add("chr",Token.KEYWORD3);
			perlKeywords.add("chroot",Token.KEYWORD3);
			perlKeywords.add("chown",Token.KEYWORD3);
			perlKeywords.add("closedir",Token.KEYWORD3);
			perlKeywords.add("close",Token.KEYWORD3);
			perlKeywords.add("connect",Token.KEYWORD3);
			perlKeywords.add("cos",Token.KEYWORD3);
			perlKeywords.add("crypt",Token.KEYWORD3);
			perlKeywords.add("dbmclose",Token.KEYWORD3);
			perlKeywords.add("dbmopen",Token.KEYWORD3);
			perlKeywords.add("defined",Token.KEYWORD3);
			perlKeywords.add("delete",Token.KEYWORD3);
			perlKeywords.add("die",Token.KEYWORD3);
			perlKeywords.add("dump",Token.KEYWORD3);
			perlKeywords.add("each",Token.KEYWORD3);
			perlKeywords.add("endgrent",Token.KEYWORD3);
			perlKeywords.add("endhostent",Token.KEYWORD3);
			perlKeywords.add("endnetent",Token.KEYWORD3);
			perlKeywords.add("endprotoent",Token.KEYWORD3);
			perlKeywords.add("endpwent",Token.KEYWORD3);
			perlKeywords.add("endservent",Token.KEYWORD3);
			perlKeywords.add("eof",Token.KEYWORD3);
			perlKeywords.add("exec",Token.KEYWORD3);
			perlKeywords.add("exists",Token.KEYWORD3);
			perlKeywords.add("exp",Token.KEYWORD3);
			perlKeywords.add("fctnl",Token.KEYWORD3);
			perlKeywords.add("fileno",Token.KEYWORD3);
			perlKeywords.add("flock",Token.KEYWORD3);
			perlKeywords.add("fork",Token.KEYWORD3);
			perlKeywords.add("format",Token.KEYWORD3);
			perlKeywords.add("formline",Token.KEYWORD3);
			perlKeywords.add("getc",Token.KEYWORD3);
			perlKeywords.add("getgrent",Token.KEYWORD3);
			perlKeywords.add("getgrgid",Token.KEYWORD3);
			perlKeywords.add("getgrnam",Token.KEYWORD3);
			perlKeywords.add("gethostbyaddr",Token.KEYWORD3);
			perlKeywords.add("gethostbyname",Token.KEYWORD3);
			perlKeywords.add("gethostent",Token.KEYWORD3);
			perlKeywords.add("getlogin",Token.KEYWORD3);
			perlKeywords.add("getnetbyaddr",Token.KEYWORD3);
			perlKeywords.add("getnetbyname",Token.KEYWORD3);
			perlKeywords.add("getnetent",Token.KEYWORD3);
			perlKeywords.add("getpeername",Token.KEYWORD3);
			perlKeywords.add("getpgrp",Token.KEYWORD3);
			perlKeywords.add("getppid",Token.KEYWORD3);
			perlKeywords.add("getpriority",Token.KEYWORD3);
			perlKeywords.add("getprotobyname",Token.KEYWORD3);
			perlKeywords.add("getprotobynumber",Token.KEYWORD3);
			perlKeywords.add("getprotoent",Token.KEYWORD3);
			perlKeywords.add("getpwent",Token.KEYWORD3);
			perlKeywords.add("getpwnam",Token.KEYWORD3);
			perlKeywords.add("getpwuid",Token.KEYWORD3);
			perlKeywords.add("getservbyname",Token.KEYWORD3);
			perlKeywords.add("getservbyport",Token.KEYWORD3);
			perlKeywords.add("getservent",Token.KEYWORD3);
			perlKeywords.add("getsockname",Token.KEYWORD3);
			perlKeywords.add("getsockopt",Token.KEYWORD3);
			perlKeywords.add("glob",Token.KEYWORD3);
			perlKeywords.add("gmtime",Token.KEYWORD3);
			perlKeywords.add("grep",Token.KEYWORD3);
			perlKeywords.add("hex",Token.KEYWORD3);
			perlKeywords.add("import",Token.KEYWORD3);
			perlKeywords.add("index",Token.KEYWORD3);
			perlKeywords.add("int",Token.KEYWORD3);
			perlKeywords.add("ioctl",Token.KEYWORD3);
			perlKeywords.add("join",Token.KEYWORD3);
			perlKeywords.add("keys",Token.KEYWORD3);
			perlKeywords.add("kill",Token.KEYWORD3);
			perlKeywords.add("lcfirst",Token.KEYWORD3);
			perlKeywords.add("lc",Token.KEYWORD3);
			perlKeywords.add("length",Token.KEYWORD3);
			perlKeywords.add("link",Token.KEYWORD3);
			perlKeywords.add("listen",Token.KEYWORD3);
			perlKeywords.add("log",Token.KEYWORD3);
			perlKeywords.add("localtime",Token.KEYWORD3);
			perlKeywords.add("lstat",Token.KEYWORD3);
			perlKeywords.add("map",Token.KEYWORD3);
			perlKeywords.add("mkdir",Token.KEYWORD3);
			perlKeywords.add("msgctl",Token.KEYWORD3);
			perlKeywords.add("msgget",Token.KEYWORD3);
			perlKeywords.add("msgrcv",Token.KEYWORD3);
			perlKeywords.add("no",Token.KEYWORD3);
			perlKeywords.add("oct",Token.KEYWORD3);
			perlKeywords.add("opendir",Token.KEYWORD3);
			perlKeywords.add("open",Token.KEYWORD3);
			perlKeywords.add("ord",Token.KEYWORD3);
			perlKeywords.add("pack",Token.KEYWORD3);
			perlKeywords.add("pipe",Token.KEYWORD3);
			perlKeywords.add("pop",Token.KEYWORD3);
			perlKeywords.add("pos",Token.KEYWORD3);
			perlKeywords.add("printf",Token.KEYWORD3);
			perlKeywords.add("print",Token.KEYWORD3);
			perlKeywords.add("push",Token.KEYWORD3);
			perlKeywords.add("quotemeta",Token.KEYWORD3);
			perlKeywords.add("rand",Token.KEYWORD3);
			perlKeywords.add("readdir",Token.KEYWORD3);
			perlKeywords.add("read",Token.KEYWORD3);
			perlKeywords.add("readlink",Token.KEYWORD3);
			perlKeywords.add("recv",Token.KEYWORD3);
			perlKeywords.add("ref",Token.KEYWORD3);
			perlKeywords.add("rename",Token.KEYWORD3);
			perlKeywords.add("reset",Token.KEYWORD3);
			perlKeywords.add("reverse",Token.KEYWORD3);
			perlKeywords.add("rewinddir",Token.KEYWORD3);
			perlKeywords.add("rindex",Token.KEYWORD3);
			perlKeywords.add("rmdir",Token.KEYWORD3);
			perlKeywords.add("scalar",Token.KEYWORD3);
			perlKeywords.add("seekdir",Token.KEYWORD3);
			perlKeywords.add("seek",Token.KEYWORD3);
			perlKeywords.add("select",Token.KEYWORD3);
			perlKeywords.add("semctl",Token.KEYWORD3);
			perlKeywords.add("semget",Token.KEYWORD3);
			perlKeywords.add("semop",Token.KEYWORD3);
			perlKeywords.add("send",Token.KEYWORD3);
			perlKeywords.add("setgrent",Token.KEYWORD3);
			perlKeywords.add("sethostent",Token.KEYWORD3);
			perlKeywords.add("setnetent",Token.KEYWORD3);
			perlKeywords.add("setpgrp",Token.KEYWORD3);
			perlKeywords.add("setpriority",Token.KEYWORD3);
			perlKeywords.add("setprotoent",Token.KEYWORD3);
			perlKeywords.add("setpwent",Token.KEYWORD3);
			perlKeywords.add("setsockopt",Token.KEYWORD3);
			perlKeywords.add("shift",Token.KEYWORD3);
			perlKeywords.add("shmctl",Token.KEYWORD3);
			perlKeywords.add("shmget",Token.KEYWORD3);
			perlKeywords.add("shmread",Token.KEYWORD3);
			perlKeywords.add("shmwrite",Token.KEYWORD3);
			perlKeywords.add("shutdown",Token.KEYWORD3);
			perlKeywords.add("sin",Token.KEYWORD3);
			perlKeywords.add("sleep",Token.KEYWORD3);
			perlKeywords.add("socket",Token.KEYWORD3);
			perlKeywords.add("socketpair",Token.KEYWORD3);
			perlKeywords.add("sort",Token.KEYWORD3);
			perlKeywords.add("splice",Token.KEYWORD3);
			perlKeywords.add("split",Token.KEYWORD3);
			perlKeywords.add("sprintf",Token.KEYWORD3);
			perlKeywords.add("sqrt",Token.KEYWORD3);
			perlKeywords.add("srand",Token.KEYWORD3);
			perlKeywords.add("stat",Token.KEYWORD3);
			perlKeywords.add("study",Token.KEYWORD3);
			perlKeywords.add("substr",Token.KEYWORD3);
			perlKeywords.add("symlink",Token.KEYWORD3);
			perlKeywords.add("syscall",Token.KEYWORD3);
			perlKeywords.add("sysopen",Token.KEYWORD3);
			perlKeywords.add("sysread",Token.KEYWORD3);
			perlKeywords.add("syswrite",Token.KEYWORD3);
			perlKeywords.add("telldir",Token.KEYWORD3);
			perlKeywords.add("tell",Token.KEYWORD3);
			perlKeywords.add("tie",Token.KEYWORD3);
			perlKeywords.add("tied",Token.KEYWORD3);
			perlKeywords.add("time",Token.KEYWORD3);
			perlKeywords.add("times",Token.KEYWORD3);
			perlKeywords.add("truncate",Token.KEYWORD3);
			perlKeywords.add("uc",Token.KEYWORD3);
			perlKeywords.add("ucfirst",Token.KEYWORD3);
			perlKeywords.add("umask",Token.KEYWORD3);
			perlKeywords.add("undef",Token.KEYWORD3);
			perlKeywords.add("unlink",Token.KEYWORD3);
			perlKeywords.add("unpack",Token.KEYWORD3);
			perlKeywords.add("unshift",Token.KEYWORD3);
			perlKeywords.add("untie",Token.KEYWORD3);
			perlKeywords.add("utime",Token.KEYWORD3);
			perlKeywords.add("values",Token.KEYWORD3);
			perlKeywords.add("vec",Token.KEYWORD3);
			perlKeywords.add("wait",Token.KEYWORD3);
			perlKeywords.add("waitpid",Token.KEYWORD3);
			perlKeywords.add("wantarray",Token.KEYWORD3);
			perlKeywords.add("warn",Token.KEYWORD3);
			perlKeywords.add("write",Token.KEYWORD3);

			perlKeywords.add("m",S_ONE);
			perlKeywords.add("q",S_ONE);
			perlKeywords.add("qq",S_ONE);
			perlKeywords.add("qw",S_ONE);
			perlKeywords.add("qx",S_ONE);
			perlKeywords.add("s",S_TWO);
			perlKeywords.add("tr",S_TWO);
			perlKeywords.add("y",S_TWO);
		}
		return perlKeywords;
	}	
}

/**
 * ChangeLog:
 * $Log: ASPPerlscriptTokenMarker.java,v $
 * Revision 1.1.1.1  2004/10/19 16:16:18  gfx
 * no message
 *
 * Revision 1.1.1.1  2001/08/20 22:31:53  gfx
 * Jext 3.0pre5
 *
 * Revision 1.2  2001/06/03 09:45:04  gfx
 * Various bug fixes
 *
 * Revision 1.1.1.1  2001/04/11 14:22:26  gfx
 *
 * Jext 2.11: GUI customization, bug fixes
 *
 * Revision 1.9  1999/09/30 12:21:05  sp
 * No net access for a month... so here's one big jEdit 2.1pre1
 *
 * Revision 1.8  1999/06/28 09:17:20  sp
 * Perl mode javac compile fix, text area hacking
 *
 * Revision 1.7  1999/06/09 05:22:11  sp
 * Find next now supports multi-file searching, minor Perl mode tweak
 *
 * Revision 1.6  1999/06/07 06:36:32  sp
 * Syntax `styling' (bold/italic tokens) added,
 * plugin options dialog for plugin option panes
 *
 * Revision 1.5  1999/06/07 03:26:33  sp
 * Major Perl token marker updates
 *
 * Revision 1.4  1999/06/06 05:05:25  sp
 * Search and replace tweaks, Perl/Shell Script mode updates
 *
 * Revision 1.3  1999/06/05 00:22:58  sp
 * LGPL'd syntax package
 *
 * Revision 1.2  1999/06/03 08:24:14  sp
 * Fixing broken CVS
 *
 * Revision 1.3  1999/05/31 08:11:10  sp
 * Syntax coloring updates, expand abbrev bug fix
 *
 * Revision 1.2  1999/05/31 04:38:51  sp
 * Syntax optimizations, HyperSearch for Selection added (Mike Dillon)
 *
 * Revision 1.1  1999/05/30 04:57:15  sp
 * Perl mode started
 *
 */
