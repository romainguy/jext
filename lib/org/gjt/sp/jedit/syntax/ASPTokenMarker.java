/*
 * ASPTokenMarker.java 
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

import java.util.Enumeration;
import java.util.Stack;
import javax.swing.text.Segment;

import gnu.regexp.*;


/**
 * An utility class to save some relevant infos (language and client/server side)
 * found in SCRIPT Tags or <%@ like Tags
 *
 * @author  Andre Kaplan
 * @version 0.6
 */
class ASPStateInfo
{
	ASPStateInfo() {}

	ASPStateInfo(boolean client, String language)
	{
		this.client   = client;
		this.language = language;
	}

	void init(boolean client, String language)
	{
		this.client   = client;
		this.language = language;
	}

	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof ASPStateInfo))
		{
			return false;
		}

		ASPStateInfo other = (ASPStateInfo)o;

		return ((this.client == other.client) && this.language.equals(other.language));
	}
 
	byte toASPMode()
	{
		for (int i = 0; i < modes.length; i++)
		{
			if (this.equals(modes[i][0]))
			{
				return ((Byte)modes[i][1]).byteValue();
			}
		}

		return ASPMode.HTML;
	}

	void display(java.io.PrintStream o)
	{
		o.println("LANGUAGE: [" + this.language + "]");
		o.println("CLIENT:   [" + this.client   + "]");
	}

	boolean client   = true;
	String  language = "javascript";

	private static Object[][] modes = new Object[][] {
		 new Object[] { new ASPStateInfo(true,  "html"),       new Byte(ASPMode.HTML) }
		,new Object[] { new ASPStateInfo(false, "html"),       new Byte(ASPMode.HTML) }
		,new Object[] { new ASPStateInfo(true,  "javascript"), new Byte(ASPMode.CSJS) }
		,new Object[] { new ASPStateInfo(true,  "jscript"),    new Byte(ASPMode.CSJS) }
		,new Object[] { new ASPStateInfo(false, "javascript"), new Byte(ASPMode.SSJS) }
		,new Object[] { new ASPStateInfo(false, "jscript"),    new Byte(ASPMode.SSJS) }
		,new Object[] { new ASPStateInfo(true,  "vbscript"),   new Byte(ASPMode.CSVB) }
		,new Object[] { new ASPStateInfo(false, "vbscript"),   new Byte(ASPMode.SSVB) }
		,new Object[] { new ASPStateInfo(true,  "perlscript"), new Byte(ASPMode.CSPS) }
		,new Object[] { new ASPStateInfo(false, "perlscript"), new Byte(ASPMode.SSPS) }
	};
}
	
/**
 * ASP Token Marker.
 * TO DO: CSS support, HTML Entities, HTML Attributes & Values colorizing
 *
 * @author Andre Kaplan
 * @version 0.6
 */
public class ASPTokenMarker 
	extends    TokenMarker
	implements TokenMarkerWithAddToken
{
	// Token to request next line parsing when the mode has changed
	public static final byte MODE_CHANGE = Token.INTERNAL_FIRST;

	public ASPTokenMarker()
	{
	}

	public void addToken(int length, byte id)
	{
		super.addToken(length, id);
	}

	// **************
	// markTokensImpl
	// **************
	protected byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		TokenMarkerContext tokenContext = new TokenMarkerContext(line, lineIndex, this, this.lineInfo);
		
		// We store the old defaultASPMode
		byte defaultASPMode = this.defaultASPMode;

		MultiModeToken prevLineToken = MultiModeToken.NULL;

		if (    tokenContext.prevLineInfo != null 
			 && tokenContext.prevLineInfo.obj != null 
			 && tokenContext.prevLineInfo.obj instanceof MultiModeToken
		)
		{
			prevLineToken = (MultiModeToken)tokenContext.prevLineInfo.obj;
		}

		MultiModeToken currLineToken = MultiModeToken.NULL;
		if (    tokenContext.currLineInfo != null 
			 && tokenContext.currLineInfo.obj != null 
			 && tokenContext.currLineInfo.obj instanceof MultiModeToken
		)
		{
			currLineToken = (MultiModeToken)tokenContext.currLineInfo.obj;
		}

		MultiModeToken res = this.markTokensImpl(prevLineToken, tokenContext);

		byte retval = res.token;

		// We check if mode has changed
		if (
				(defaultASPMode != this.defaultASPMode || currLineToken.mode != res.mode)
			 && (currLineToken.token == res.token)
		)
		{
			// We inform markTokens that the mode has changed
			retval = MODE_CHANGE;
		}

		tokenContext.currLineInfo.obj = res;

		return retval;
	}

	// **************
	// markTokensImpl
	// **************
	MultiModeToken markTokensImpl(final MultiModeToken token, TokenMarkerContext tokenContext)
	{
		MultiModeToken res = new MultiModeToken(token);

loop:   for (this.debug.reset(); tokenContext.hasMoreChars(); )
		{
			char c = tokenContext.getChar();

			if (!this.debug.isOK(tokenContext)) {
				// We got stuck here at some point
				// Log this and increment tokenContext.pos to escape this
				tokenContext.pos++;
			}

			// Switch to ASP Mode if <% found at pos
			// Except if already in a server-side mode
			if (	(res.mode != ASPMode.HTML_SCRIPT)
				 &&	(res.mode != ASPMode.SSI)
				 &&	(res.mode != ASPMode.ASP)
				 &&	(res.mode != ASPMode.ASP_CFG)
				 &&	(res.mode != ASPMode.SSVB)
				 &&	(res.mode != ASPMode.SSJS)
				 &&	(res.mode != ASPMode.SSPS)
			   )
			{
				if (this.doASP(res, tokenContext)) { 
					continue; }
			}

			// Switch to HTML_SCRIPT Mode if <script found at pos
			// Except if already in a server-side mode
			// or in a client-side script
			if (	(res.mode != ASPMode.HTML_SCRIPT)
				 &&	(res.mode != ASPMode.SSI)
				 &&	(res.mode != ASPMode.ASP)
				 &&	(res.mode != ASPMode.ASP_CFG)
				 &&	(res.mode != ASPMode.SSVB)
				 &&	(res.mode != ASPMode.SSJS)
				 &&	(res.mode != ASPMode.SSPS)
				 &&	(res.mode != ASPMode.CSVB)
				 &&	(res.mode != ASPMode.CSJS)
				 &&	(res.mode != ASPMode.CSPS)
			   )
			{
				if (this.doScript(res, tokenContext)) { 
					continue; 
				}
			}

			switch (res.mode)
			{
				case ASPMode.HTML: // HTML Text

					switch (c)
					{
						case '<':
							tokenContext.addTokenToPos(res.token);

							if (tokenContext.regionMatches(false, "<!--")) {
								if (tokenContext.regionMatches(false, "<!--#"))
								{
									// Server-Side Include comment
									tokenContext.pos += 5;
									res.mode  = ASPMode.SSI;
									res.token = Token.COMMENT2;
									continue;
								}
								else
								{
									tokenContext.pos += 4;
									res.mode  = ASPMode.HTML_COMMENT;
									res.token = Token.COMMENT1;
									continue;
								}
							}
							else if (tokenContext.regionMatches(true, "</"))
							{
								tokenContext.pos += 2;
								tokenContext.addTokenToPos(Token.KEYWORD1);
								res.mode  = ASPMode.HTML_TAG;
								res.token = Token.KEYWORD1;
								continue;
							}
							else
							{
								tokenContext.pos++;
								tokenContext.addTokenToPos(Token.KEYWORD1);
								res.mode  = ASPMode.HTML_TAG;
								res.token = Token.KEYWORD1;
								continue;
							}
						// break; // Not reached was already continued

						case '&':
							tokenContext.addTokenToPos(res.token);

							res.mode  = ASPMode.HTML_ENTITY;
							res.token = Token.KEYWORD2;
						break;
					}
				break;

				// ************
				// Inside a tag
				// ************
				case ASPMode.HTML_TAG:
					if (c == '>')
					{
						tokenContext.addTokenToPos(res.token);
						tokenContext.pos++;
						tokenContext.addTokenToPos(Token.KEYWORD1);
						res.mode  = ASPMode.HTML;
						res.token = Token.NULL;
						continue;
					}
				break;

				// ***************************
				// Inside an ASP CFG tag (<%@)
				// ***************************
				case ASPMode.ASP_CFG:
				{
					REMatch matchInfo;

					if (tokenContext.regionMatches(true, "%>"))
					{
						tokenContext.addTokenToPos(res.token);
						tokenContext.pos += 2;
						tokenContext.addTokenToPos(Token.LABEL);

						// System.out.println("ASP CFG");
						// this.stateInfo.display(System.out);

						this.defaultASPMode = this.stateInfo.toASPMode();

						res.reset();
						continue;
					}
					else if (tokenContext.regionMatches(true, "language"))
					{
						if (
								(language != null)
							&& ((matchInfo = tokenContext.RERegionMatches(language)) != null)
						)
						{
							this.stateInfo.language = matchInfo.toString(1).toLowerCase();
						}
						else
						{
							// the language was not recognized, default to html
							this.stateInfo.language = "html";
						}
					}
				}
				break;

				// *******************
				// Inside a script tag
				// *******************
				case ASPMode.HTML_SCRIPT:
				{
					REMatch matchInfo;

					if (c == '>')
					{
						tokenContext.pos++;	
						tokenContext.addTokenToPos(res.token);

						// this.stateInfo.display(System.out);

						// If this is a client script tag
						// we test if are inside an HTML COMMENT
						// and if it is, we just ignore the script tag and go 
						// back to normal
						if (	(this.stateInfo.client)
							 &&	(!this.modes.empty())
						   )
						{
							MultiModeToken mmt = (MultiModeToken)(this.modes.peek());
							if (mmt.mode == ASPMode.HTML_COMMENT)
							{
								res = (MultiModeToken)(this.modes.pop());
								continue;
							}
						}

						res.mode  = this.stateInfo.toASPMode();
						res.token = Token.NULL;

						continue;
					}
					else if (tokenContext.regionMatches(true, "language"))
					{
						if (
								(language != null)
							&& ((matchInfo = tokenContext.RERegionMatches(language)) != null)
						)
						{
							this.stateInfo.language = matchInfo.toString(1).toLowerCase();
						}
						else
						{
							// the language was not recognized, default to html
							this.stateInfo.language = "html";
						}
					}
					else if
					(
						    (runat != null)
						&& ((matchInfo = tokenContext.RERegionMatches(runat)) != null)
					)
					{
						this.stateInfo.client = false;
					}
				}
				break;

				// ****************
				// Inside an entity
				// ****************
				case ASPMode.HTML_ENTITY:
					if (c == ';')
					{
						tokenContext.pos++;
						tokenContext.addTokenToPos(res.token);
						res.reset();
						continue;
					}
				break;

				// *****************************
				// Inside an HTML comment or SSI
				// *****************************
				case ASPMode.HTML_COMMENT:
				case ASPMode.SSI:
					if (tokenContext.regionMatches(false, "-->"))
					{
						tokenContext.pos += 3;
						tokenContext.addTokenToPos(res.token);
						res.reset();
						continue;
					}
				break;

				// ********************
				// Inside an ASP script
				// ********************
				case ASPMode.ASP:
					if (this.defaultASPMode == ASPMode.SSJS) {
						res = js.markTokensImpl(res, tokenContext);
					} else if (this.defaultASPMode == ASPMode.SSPS) {
						res = ps.markTokensImpl(res, tokenContext);
					} else {
						res = vbs.markTokensImpl(res, tokenContext);
					}

					if (tokenContext.regionMatches(true, "%>"))
					{
						tokenContext.addTokenToPos(res.token);
						tokenContext.pos += 2;
						tokenContext.addTokenToPos(Token.LABEL);
						if (this.modes.empty())	{
							res.reset();
						} else {
							res = ((MultiModeToken)this.modes.pop());
						}
						continue;
					}
				break;

				// ***************
				// Inside a SCRIPT
				// ***************
				case ASPMode.SSJS:
				case ASPMode.CSJS:
					res = this.js.markTokensImpl(res, tokenContext);

					if (res.mode == ASPMode.CSJS)
					{
						if (this.doASP(res, tokenContext)) { continue; }
					} 

					if (this.doScriptClose(res, tokenContext)) { continue; }

					break;

				case ASPMode.CSVB:
				case ASPMode.SSVB:
					res = this.vbs.markTokensImpl(res, tokenContext);

					if (res.mode == ASPMode.CSVB)
					{
						if (this.doASP(res, tokenContext)) { continue; }
					} 

					if (this.doScriptClose(res, tokenContext)) { continue; }

					break;

				case ASPMode.SSPS:
				case ASPMode.CSPS:
					res = this.ps.markTokensImpl(res, tokenContext);

					if (res.mode == ASPMode.CSPS)
					{
						if (this.doASP(res, tokenContext)) { continue; }
					} 

					if (this.doScriptClose(res, tokenContext)) { continue; }

					break;

				default:
					break;
			}

			tokenContext.pos++;
		} // END LOOP

		if (res.mode == ASPMode.HTML_ENTITY) {
			tokenContext.addTokenToEnd(Token.INVALID);
			res.mode  = ASPMode.HTML;
			res.token = Token.NULL;
		} else {
			tokenContext.addTokenToEnd(res.token);
		}

		return res;
	}

	/**
     * Checks if ASP mode is entered at pos then updates Token and Context 
	 */
	private boolean doASP(MultiModeToken mmt,
						  TokenMarkerContext tokenContext)
	{
		if (tokenContext.regionMatches(false, "<%"))
		{
			tokenContext.addTokenToPos(mmt.token);

			if (tokenContext.regionMatches(false, "<%@"))
			{
				stateInfo.init(false, "vbscript");
				tokenContext.pos += 3;
				tokenContext.addTokenToPos(Token.LABEL);
				this.modes.push(new MultiModeToken(mmt));
				mmt.mode  = ASPMode.ASP_CFG;
				mmt.token = Token.KEYWORD2;
			}
			else 
			{
				tokenContext.pos += 2;
				tokenContext.addTokenToPos(Token.LABEL);
				// We save the current mode
				this.modes.push(new MultiModeToken(mmt));
				mmt.mode  = ASPMode.ASP;
				mmt.token = Token.NULL;
			}
			return true;
		}

		return false;
	}

	private boolean doScript(MultiModeToken mmt,
						     TokenMarkerContext tokenContext)
	{
		if (tokenContext.regionMatches(true, "<script"))
		{
			stateInfo.init(true, "javascript");
			tokenContext.addTokenToPos(mmt.token);
			tokenContext.pos += 7;
			// We save the current mode
			this.modes.push(new MultiModeToken(mmt));
			// If we are in a HTML Comment we can not decide if we tokenize
			// until we know that the script executes on the server
			if (mmt.mode == ASPMode.HTML_COMMENT)
			{
				mmt.mode  = ASPMode.HTML_SCRIPT;
				// mmt.token = Token.KEYWORD1;
			}
			else
			{
				mmt.mode  = ASPMode.HTML_SCRIPT;
				mmt.token = Token.KEYWORD1;
			}
			return true;
		}

		return false;
	}

	private boolean doScriptClose(MultiModeToken mmt,
						     	  TokenMarkerContext tokenContext)
	{
		if (tokenContext.regionMatches(true, "</script>"))
		{
			tokenContext.addTokenToPos(mmt.token);

			byte b = Token.KEYWORD1;

			if (this.modes.empty()) {
				mmt.reset();
			} else {
				mmt.assign((MultiModeToken)(this.modes.pop()));
				if (mmt.mode == ASPMode.HTML_COMMENT) {
					b = mmt.token;
				}
			}
			tokenContext.pos += 9;
			tokenContext.addTokenToPos(b);
				
			return true;
		}

		return false;
	}

	// private members
	private KeywordMap keywords;

	private ASPStateInfo stateInfo = new ASPStateInfo();
	private byte defaultASPMode    = ASPMode.SSVB;

	private TokenMarkerDebugger debug = new TokenMarkerDebugger();

	private MultiModeTokenMarkerWithContext vbs = new ASPVBScriptTokenMarker(false);
	private MultiModeTokenMarkerWithContext js  = new ASPJavascriptTokenMarker(false);
	private MultiModeTokenMarkerWithContext ps  = new ASPPerlscriptTokenMarker(false);

	private Stack modes = new Stack();

	private static RE language = null;
	private static RE runat    = null;

	static {
		try {
			language = new RE(
				"^language\\s*=\\s*[\"']?(jscript|javascript|perlscript|vbscript)([0-9]*|[0-9]+(?:\\.[0-9]+){0,2})[\"']?", RE.REG_ICASE);
		}
		catch (REException ree)
		{
		}
	
		try {
			runat = new RE("^runat\\s*=\\s*[\"']?(server)[\"']?", RE.REG_ICASE);
		}
		catch (REException ree)
		{
		}
	}
}
