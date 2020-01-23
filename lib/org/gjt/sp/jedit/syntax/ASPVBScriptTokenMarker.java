/*
 * ASPVBScriptTokenMarker - Token Marker for VBScript 5 in ASP Pages
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

/**
 * ASP VBScript token marker
 * 
 * @author  Andre Kaplan
 * @version 0.6
 */
public class ASPVBScriptTokenMarker
	extends TokenMarker
	implements 
		TokenMarkerWithAddToken,
		MultiModeTokenMarkerWithContext
{
	public ASPVBScriptTokenMarker()
	{
		this.keywords = getKeywords();
		this.standalone = true;
	}

	ASPVBScriptTokenMarker(boolean standalone)
	{
		this.keywords   = getKeywords();
		this.standalone = standalone;
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

loop:   for(; tokenContext.hasMoreChars(); )
		{
			char c = tokenContext.getChar();

			switch (res.token)
			{
				// NULL Token
				case Token.NULL:
					if (!this.standalone) {
						if (res.mode == ASPMode.CSVB) {
							if (tokenContext.regionMatches(true, "<%")) {
								this.doKeywordToPos(res, tokenContext);
								// Just return, let parent mode decide what to do next
								return res;
							}
						}
	
						if (res.mode == ASPMode.ASP) {
							if (tokenContext.regionMatches(true, "%>")) {
								this.doKeywordToPos(res, tokenContext);
								// Just return, let parent mode decide what to do next
								return res;
							}
						}
	
						if (res.mode == ASPMode.CSVB || res.mode == ASPMode.SSVB) {
							if (tokenContext.regionMatches(true, "</script>")) {
								this.doKeywordToPos(res, tokenContext);
								// Just return, let parent mode decide what to do next
								return res;
							}
						}
					}

					switch (c)
					{
						// Comments
						case '\'':
							this.doKeywordToPos(res, tokenContext);
							tokenContext.addTokenToPos(res.token);
							tokenContext.addTokenToEnd(Token.COMMENT1);
						break loop;

						// Literals
						case '\"':
							this.doKeywordToPos(res, tokenContext);
							tokenContext.addTokenToPos(res.token);
							res.token = Token.LITERAL1;
						break;

						// Single character operators
						case '<':
						case '>':
						case '=':
						case '+':
						case '-':
						case '*':
						case '/':
						case '\\': // Integer division
						case '^':  // Exponentiation
						case '&':  // String Concatenation
							this.doKeywordToPos(res, tokenContext);
							tokenContext.addTokenToPos(res.token);
							tokenContext.pos++;
							tokenContext.addTokenToPos(Token.OPERATOR);
						continue;

						default:
							if (!Character.isLetterOrDigit(c)
								&& c != '_')
							{
								this.doKeywordToPos(res, tokenContext);
							}
						break;
					}
				break;

				case Token.LITERAL1:
					if (c == '"')
					{
						tokenContext.pos++;
						tokenContext.addTokenToPos(res.token);
						res.token = Token.NULL;
						continue;
					}
				break;

				// Comment (Rem)
				case Token.COMMENT1:
					tokenContext.addTokenToEnd(Token.COMMENT1);
				break loop;

				default:
				break;
			}

			tokenContext.pos++;
		} // END LOOP

		if (res.token == Token.NULL)
		{
			tokenContext.doKeywordToEnd(this.keywords);
		}

		switch (res.token)
		{
			case Token.LITERAL1:
				tokenContext.addTokenToEnd(Token.INVALID);
				res.token = Token.NULL;
			break;

			case Token.COMMENT1:
				tokenContext.addTokenToEnd(Token.COMMENT1);
				res.token = Token.NULL;
			break;

			default:
				tokenContext.addTokenToEnd(res.token);
			break;
		}

		return res;
	}

	private byte doKeywordToPos(MultiModeToken mmt, TokenMarkerContext tokenContext)
	{
		byte id = tokenContext.doKeywordToPos(this.keywords);
		if (id == Token.COMMENT1) {
			mmt.token = Token.COMMENT1;
		}
		return id;
	}

	private KeywordMap keywords;
	private boolean    standalone;

	// Static members
	public static KeywordMap getKeywords()
	{
		if (vbScriptKeywords == null)
		{
			vbScriptKeywords = new KeywordMap(true);
			// Conditional Statements
			vbScriptKeywords.add("if",     Token.KEYWORD1);
			vbScriptKeywords.add("then",   Token.KEYWORD1);
			vbScriptKeywords.add("else",   Token.KEYWORD1);
			vbScriptKeywords.add("elseif", Token.KEYWORD1);
			vbScriptKeywords.add("select", Token.KEYWORD1);
			vbScriptKeywords.add("case",   Token.KEYWORD1);

			// Loop Statements
			// For..Next
			vbScriptKeywords.add("for",  Token.KEYWORD1);
			vbScriptKeywords.add("to",   Token.KEYWORD1);
			vbScriptKeywords.add("step", Token.KEYWORD1);
			vbScriptKeywords.add("next", Token.KEYWORD1);
			// For Each..Next
			vbScriptKeywords.add("each", Token.KEYWORD1);
			vbScriptKeywords.add("in",   Token.KEYWORD1);
			// Do..Loop
			vbScriptKeywords.add("do",    Token.KEYWORD1);
			vbScriptKeywords.add("while", Token.KEYWORD1);
			vbScriptKeywords.add("until", Token.KEYWORD1);
			vbScriptKeywords.add("loop",  Token.KEYWORD1);
			// While..Wend
			vbScriptKeywords.add("wend",  Token.KEYWORD1);

			// Closing, Exit Statements			
			vbScriptKeywords.add("exit", Token.KEYWORD1);
			vbScriptKeywords.add("end",  Token.KEYWORD1);

			// Methods, Functions, Class, Properties, Variables
			vbScriptKeywords.add("function", Token.KEYWORD1);
			vbScriptKeywords.add("sub", Token.KEYWORD1);
			vbScriptKeywords.add("class", Token.KEYWORD1);
			vbScriptKeywords.add("property", Token.KEYWORD1);
			vbScriptKeywords.add("get", Token.KEYWORD1);
			vbScriptKeywords.add("let", Token.KEYWORD1);
			// property set: vbScriptKeywords.add("set", Token.KEYWORD1);

			// Parameters passing
			vbScriptKeywords.add("byval", Token.KEYWORD1);
			vbScriptKeywords.add("byref", Token.KEYWORD1);

			// Declaration
			vbScriptKeywords.add("const",    Token.KEYWORD1);
			vbScriptKeywords.add("dim",      Token.KEYWORD1);
			vbScriptKeywords.add("redim",    Token.KEYWORD1);
			vbScriptKeywords.add("preserve", Token.KEYWORD1);

			// Object Assignement/Instantiation
			vbScriptKeywords.add("set",  Token.KEYWORD1);
			vbScriptKeywords.add("with", Token.KEYWORD1);
			vbScriptKeywords.add("new",  Token.KEYWORD1);

			// Visibility
			vbScriptKeywords.add("public",  Token.KEYWORD1);
			vbScriptKeywords.add("default", Token.KEYWORD1);
			vbScriptKeywords.add("private", Token.KEYWORD1);

			// Comments
			vbScriptKeywords.add("rem", Token.COMMENT1);

			// Function Call - Dynamic Evaluation
			vbScriptKeywords.add("call",    Token.KEYWORD1);
			vbScriptKeywords.add("execute", Token.KEYWORD1);
			vbScriptKeywords.add("eval",    Token.KEYWORD1);

			// Miscellaneous
			vbScriptKeywords.add("on",        Token.KEYWORD1);
			vbScriptKeywords.add("error",     Token.KEYWORD1);
			vbScriptKeywords.add("resume",    Token.KEYWORD1);
			vbScriptKeywords.add("option",    Token.KEYWORD1);
			vbScriptKeywords.add("explicit",  Token.KEYWORD1);
			vbScriptKeywords.add("erase",     Token.KEYWORD1);
			vbScriptKeywords.add("randomize", Token.KEYWORD1);

			// Operators
			// Comparison Operators
			vbScriptKeywords.add("is", Token.OPERATOR);
			// Arihtmetic Operators
			vbScriptKeywords.add("mod", Token.OPERATOR);
			// Logical Operators
			vbScriptKeywords.add("and", Token.OPERATOR);
			vbScriptKeywords.add("or",  Token.OPERATOR);
			vbScriptKeywords.add("not", Token.OPERATOR);
			vbScriptKeywords.add("xor", Token.OPERATOR);
			vbScriptKeywords.add("imp", Token.OPERATOR);

			// Datatypes Constants/Literals
			vbScriptKeywords.add("false",   Token.KEYWORD3);
			vbScriptKeywords.add("true",    Token.KEYWORD3);
			vbScriptKeywords.add("empty",   Token.KEYWORD3);
			vbScriptKeywords.add("nothing", Token.KEYWORD3);
			vbScriptKeywords.add("null",    Token.KEYWORD3);

			// String Constants
			vbScriptKeywords.add("vbcr",          Token.LITERAL2);
			vbScriptKeywords.add("vbcrlf",        Token.LITERAL2);
			vbScriptKeywords.add("vbformfeed",    Token.LITERAL2);
			vbScriptKeywords.add("vblf",          Token.LITERAL2);
			vbScriptKeywords.add("vbnewline",     Token.LITERAL2);
			vbScriptKeywords.add("vbnullchar",    Token.LITERAL2);
			vbScriptKeywords.add("vbnullstring",  Token.LITERAL2);
			vbScriptKeywords.add("vbtab",         Token.LITERAL2);
			vbScriptKeywords.add("vbverticaltab", Token.LITERAL2);
			// VarType Constants
			vbScriptKeywords.add("vbempty",      Token.LITERAL2);
			vbScriptKeywords.add("vbempty",      Token.LITERAL2);
			vbScriptKeywords.add("vbinteger",    Token.LITERAL2);
			vbScriptKeywords.add("vblong",       Token.LITERAL2);
			vbScriptKeywords.add("vbsingle",     Token.LITERAL2);
			vbScriptKeywords.add("vbdouble",     Token.LITERAL2);
			vbScriptKeywords.add("vbcurrency",   Token.LITERAL2);
			vbScriptKeywords.add("vbdate",       Token.LITERAL2);
			vbScriptKeywords.add("vbstring",     Token.LITERAL2);
			vbScriptKeywords.add("vbobject",     Token.LITERAL2);
			vbScriptKeywords.add("vberror",      Token.LITERAL2);
			vbScriptKeywords.add("vbboolean",    Token.LITERAL2);
			vbScriptKeywords.add("vbvariant",    Token.LITERAL2);
			vbScriptKeywords.add("vbdataobject", Token.LITERAL2);
			vbScriptKeywords.add("vbdecimal",    Token.LITERAL2);
			vbScriptKeywords.add("vbbyte",       Token.LITERAL2);
			vbScriptKeywords.add("vbarray",      Token.LITERAL2);

			// Built-in Functions
			// Array Handling
			vbScriptKeywords.add("array", Token.KEYWORD2);
			vbScriptKeywords.add("lbound", Token.KEYWORD2);
			vbScriptKeywords.add("ubound", Token.KEYWORD2);
			// Conversion Functions (Variant)
			vbScriptKeywords.add("cbool", Token.KEYWORD2);
			vbScriptKeywords.add("cbyte", Token.KEYWORD2);
			vbScriptKeywords.add("ccur", Token.KEYWORD2);
			vbScriptKeywords.add("cdate", Token.KEYWORD2);
			vbScriptKeywords.add("cdbl", Token.KEYWORD2);
			vbScriptKeywords.add("cint", Token.KEYWORD2);
			vbScriptKeywords.add("clng", Token.KEYWORD2);
			vbScriptKeywords.add("csng", Token.KEYWORD2);
			vbScriptKeywords.add("cstr", Token.KEYWORD2);
			// Conversion Functions (Radix)
			vbScriptKeywords.add("hex", Token.KEYWORD2);
			vbScriptKeywords.add("oct", Token.KEYWORD2);
			// Date/Time Functions
			vbScriptKeywords.add("date", Token.KEYWORD2);
			vbScriptKeywords.add("time", Token.KEYWORD2);
			vbScriptKeywords.add("dateadd", Token.KEYWORD2);
			vbScriptKeywords.add("datediff", Token.KEYWORD2);
			vbScriptKeywords.add("datepart", Token.KEYWORD2);
			vbScriptKeywords.add("dateserial", Token.KEYWORD2);
			vbScriptKeywords.add("datevalue", Token.KEYWORD2);
			vbScriptKeywords.add("day", Token.KEYWORD2);
			vbScriptKeywords.add("month", Token.KEYWORD2);
			vbScriptKeywords.add("monthname", Token.KEYWORD2);
			vbScriptKeywords.add("weekday", Token.KEYWORD2);
			vbScriptKeywords.add("weekdayname", Token.KEYWORD2);
			vbScriptKeywords.add("year", Token.KEYWORD2);
			vbScriptKeywords.add("hour", Token.KEYWORD2);
			vbScriptKeywords.add("minute", Token.KEYWORD2);
			vbScriptKeywords.add("second", Token.KEYWORD2);
			vbScriptKeywords.add("now", Token.KEYWORD2);
			vbScriptKeywords.add("timeserial", Token.KEYWORD2);
			vbScriptKeywords.add("timevalue", Token.KEYWORD2);
			// Formatting Strings
			vbScriptKeywords.add("formatcurrency", Token.KEYWORD2);
			vbScriptKeywords.add("formatdatetime", Token.KEYWORD2);
			vbScriptKeywords.add("formatnumber", Token.KEYWORD2);
			vbScriptKeywords.add("formatpercent", Token.KEYWORD2);
			// Input/Output
			vbScriptKeywords.add("inputbox", Token.KEYWORD2);
			vbScriptKeywords.add("loadpicture", Token.KEYWORD2);
			vbScriptKeywords.add("msgbox", Token.KEYWORD2);
			// Math Functions
			vbScriptKeywords.add("atn", Token.KEYWORD2);
			vbScriptKeywords.add("cos", Token.KEYWORD2);
			vbScriptKeywords.add("sin", Token.KEYWORD2);
			vbScriptKeywords.add("tan", Token.KEYWORD2);
			vbScriptKeywords.add("exp", Token.KEYWORD2);
			vbScriptKeywords.add("log", Token.KEYWORD2);
			vbScriptKeywords.add("sqr", Token.KEYWORD2);
			vbScriptKeywords.add("rnd", Token.KEYWORD2);
			// Miscellaneous
			vbScriptKeywords.add("rgb", Token.KEYWORD2);
			// COM Objects
			vbScriptKeywords.add("createobject", Token.KEYWORD2);
			vbScriptKeywords.add("getobject", Token.KEYWORD2);
			vbScriptKeywords.add("getref", Token.KEYWORD2);
			// Rounding
			vbScriptKeywords.add("abs", Token.KEYWORD2);
			vbScriptKeywords.add("int", Token.KEYWORD2);
			vbScriptKeywords.add("fix", Token.KEYWORD2);
			vbScriptKeywords.add("round", Token.KEYWORD2);
			vbScriptKeywords.add("sgn", Token.KEYWORD2);
			//  Script Engine ID
			vbScriptKeywords.add("scriptengine", Token.KEYWORD2);
			vbScriptKeywords.add("scriptenginebuildversion", Token.KEYWORD2);
			vbScriptKeywords.add("scriptenginemajorversion", Token.KEYWORD2);
			vbScriptKeywords.add("scriptengineminorversion", Token.KEYWORD2);
			// Strings
			vbScriptKeywords.add("asc", Token.KEYWORD2);
			vbScriptKeywords.add("ascb", Token.KEYWORD2);
			vbScriptKeywords.add("ascw", Token.KEYWORD2);
			vbScriptKeywords.add("chr", Token.KEYWORD2);
			vbScriptKeywords.add("chrb", Token.KEYWORD2);
			vbScriptKeywords.add("chrw", Token.KEYWORD2);
			vbScriptKeywords.add("filter", Token.KEYWORD2);
			vbScriptKeywords.add("instr", Token.KEYWORD2);
			vbScriptKeywords.add("instrb", Token.KEYWORD2);
			vbScriptKeywords.add("instrrev", Token.KEYWORD2);
			vbScriptKeywords.add("join", Token.KEYWORD2);
			vbScriptKeywords.add("len", Token.KEYWORD2);
			vbScriptKeywords.add("lenb", Token.KEYWORD2);
			vbScriptKeywords.add("lcase", Token.KEYWORD2);
			vbScriptKeywords.add("ucase", Token.KEYWORD2);
			vbScriptKeywords.add("left", Token.KEYWORD2);
			vbScriptKeywords.add("leftb", Token.KEYWORD2);
			vbScriptKeywords.add("mid", Token.KEYWORD2);
			vbScriptKeywords.add("midb", Token.KEYWORD2);
			vbScriptKeywords.add("right", Token.KEYWORD2);
			vbScriptKeywords.add("rightb", Token.KEYWORD2);
			vbScriptKeywords.add("replace", Token.KEYWORD2);
			vbScriptKeywords.add("space", Token.KEYWORD2);
			vbScriptKeywords.add("split", Token.KEYWORD2);
			vbScriptKeywords.add("strcomp", Token.KEYWORD2);
			vbScriptKeywords.add("string", Token.KEYWORD2);
			vbScriptKeywords.add("strreverse", Token.KEYWORD2);
			vbScriptKeywords.add("ltrim", Token.KEYWORD2);
			vbScriptKeywords.add("rtrim", Token.KEYWORD2);
			vbScriptKeywords.add("trim", Token.KEYWORD2);
			// Variants
			vbScriptKeywords.add("isarray", Token.KEYWORD2);
			vbScriptKeywords.add("isdate", Token.KEYWORD2);
			vbScriptKeywords.add("isempty", Token.KEYWORD2);
			vbScriptKeywords.add("isnull", Token.KEYWORD2);
			vbScriptKeywords.add("isnumeric", Token.KEYWORD2);
			vbScriptKeywords.add("isobject", Token.KEYWORD2);
			vbScriptKeywords.add("typename", Token.KEYWORD2);
			vbScriptKeywords.add("vartype", Token.KEYWORD2);

			// Uncomment your favorite keywords if you like
			// ADO Constants

			//---- CursorTypeEnum Values ----
			vbScriptKeywords.add("adOpenForwardOnly", Token.LITERAL2);
			vbScriptKeywords.add("adOpenKeyset", Token.LITERAL2);
			vbScriptKeywords.add("adOpenDynamic", Token.LITERAL2);
			vbScriptKeywords.add("adOpenStatic", Token.LITERAL2);
			
			//---- CursorOptionEnum Values ----
			vbScriptKeywords.add("adHoldRecords", Token.LITERAL2);
			vbScriptKeywords.add("adMovePrevious", Token.LITERAL2);
			vbScriptKeywords.add("adAddNew", Token.LITERAL2);
			vbScriptKeywords.add("adDelete", Token.LITERAL2);
			vbScriptKeywords.add("adUpdate", Token.LITERAL2);
			vbScriptKeywords.add("adBookmark", Token.LITERAL2);
			vbScriptKeywords.add("adApproxPosition", Token.LITERAL2);
			vbScriptKeywords.add("adUpdateBatch", Token.LITERAL2);
			vbScriptKeywords.add("adResync", Token.LITERAL2);
			vbScriptKeywords.add("adNotify", Token.LITERAL2);
			vbScriptKeywords.add("adFind", Token.LITERAL2);
			vbScriptKeywords.add("adSeek", Token.LITERAL2);
			vbScriptKeywords.add("adIndex", Token.LITERAL2);
			
			//---- LockTypeEnum Values ----
			vbScriptKeywords.add("adLockReadOnly", Token.LITERAL2);
			vbScriptKeywords.add("adLockPessimistic", Token.LITERAL2);
			vbScriptKeywords.add("adLockOptimistic", Token.LITERAL2);
			vbScriptKeywords.add("adLockBatchOptimistic", Token.LITERAL2);
			
			//---- ExecuteOptionEnum Values ----
			vbScriptKeywords.add("adRunAsync", Token.LITERAL2);
			vbScriptKeywords.add("adAsyncExecute", Token.LITERAL2);
			vbScriptKeywords.add("adAsyncFetch", Token.LITERAL2);
			vbScriptKeywords.add("adAsyncFetchNonBlocking", Token.LITERAL2);
			vbScriptKeywords.add("adExecuteNoRecords", Token.LITERAL2);
			
			//---- ConnectOptionEnum Values ----
			vbScriptKeywords.add("adAsyncConnect", Token.LITERAL2);
			
			//---- ObjectStateEnum Values ----
			vbScriptKeywords.add("adStateClosed", Token.LITERAL2);
			vbScriptKeywords.add("adStateOpen", Token.LITERAL2);
			vbScriptKeywords.add("adStateConnecting", Token.LITERAL2);
			vbScriptKeywords.add("adStateExecuting", Token.LITERAL2);
			vbScriptKeywords.add("adStateFetching", Token.LITERAL2);
			
			//---- CursorLocationEnum Values ----
			vbScriptKeywords.add("adUseServer", Token.LITERAL2);
			vbScriptKeywords.add("adUseClient", Token.LITERAL2);
			
			//---- DataTypeEnum Values ----
			vbScriptKeywords.add("adEmpty", Token.LITERAL2);
			vbScriptKeywords.add("adTinyInt", Token.LITERAL2);
			vbScriptKeywords.add("adSmallInt", Token.LITERAL2);
			vbScriptKeywords.add("adInteger", Token.LITERAL2);
			vbScriptKeywords.add("adBigInt", Token.LITERAL2);
			vbScriptKeywords.add("adUnsignedTinyInt", Token.LITERAL2);
			vbScriptKeywords.add("adUnsignedSmallInt", Token.LITERAL2);
			vbScriptKeywords.add("adUnsignedInt", Token.LITERAL2);
			vbScriptKeywords.add("adUnsignedBigInt", Token.LITERAL2);
			vbScriptKeywords.add("adSingle", Token.LITERAL2);
			vbScriptKeywords.add("adDouble", Token.LITERAL2);
			vbScriptKeywords.add("adCurrency", Token.LITERAL2);
			vbScriptKeywords.add("adDecimal", Token.LITERAL2);
			vbScriptKeywords.add("adNumeric", Token.LITERAL2);
			vbScriptKeywords.add("adBoolean", Token.LITERAL2);
			vbScriptKeywords.add("adError", Token.LITERAL2);
			vbScriptKeywords.add("adUserDefined", Token.LITERAL2);
			vbScriptKeywords.add("adVariant", Token.LITERAL2);
			vbScriptKeywords.add("adIDispatch", Token.LITERAL2);
			vbScriptKeywords.add("adIUnknown", Token.LITERAL2);
			vbScriptKeywords.add("adGUID", Token.LITERAL2);
			vbScriptKeywords.add("adDate", Token.LITERAL2);
			vbScriptKeywords.add("adDBDate", Token.LITERAL2);
			vbScriptKeywords.add("adDBTime", Token.LITERAL2);
			vbScriptKeywords.add("adDBTimeStamp", Token.LITERAL2);
			vbScriptKeywords.add("adBSTR", Token.LITERAL2);
			vbScriptKeywords.add("adChar", Token.LITERAL2);
			vbScriptKeywords.add("adVarChar", Token.LITERAL2);
			vbScriptKeywords.add("adLongVarChar", Token.LITERAL2);
			vbScriptKeywords.add("adWChar", Token.LITERAL2);
			vbScriptKeywords.add("adVarWChar", Token.LITERAL2);
			vbScriptKeywords.add("adLongVarWChar", Token.LITERAL2);
			vbScriptKeywords.add("adBinary", Token.LITERAL2);
			vbScriptKeywords.add("adVarBinary", Token.LITERAL2);
			vbScriptKeywords.add("adLongVarBinary", Token.LITERAL2);
			vbScriptKeywords.add("adChapter", Token.LITERAL2);
			vbScriptKeywords.add("adFileTime", Token.LITERAL2);
			vbScriptKeywords.add("adDBFileTime", Token.LITERAL2);
			vbScriptKeywords.add("adPropVariant", Token.LITERAL2);
			vbScriptKeywords.add("adVarNumeric", Token.LITERAL2);
		}
		return vbScriptKeywords;
	}

	// private members
	private static KeywordMap vbScriptKeywords;
}

