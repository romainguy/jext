/*
 * ASPJavascriptTokenMarker.java 
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
 * ASP Javascript token marker
 * 
 * @author  Andre Kaplan
 * @version 0.6
 */
public class ASPJavascriptTokenMarker
	extends    TokenMarker
	implements TokenMarkerWithAddToken,
			   MultiModeTokenMarkerWithContext
{
	public ASPJavascriptTokenMarker()
	{
		this(getKeywords(), true);

	}

	public ASPJavascriptTokenMarker(boolean standalone)
	{
		this(getKeywords(), standalone);

	}

	public ASPJavascriptTokenMarker(KeywordMap keywords)
	{
		this(keywords, true);

	}

	public ASPJavascriptTokenMarker(KeywordMap keywords, boolean standalone)
	{
		this.keywords   = keywords;
		this.standalone = standalone;
	}

	public void addToken(int length, byte id)
	{
		super.addToken(length, id);
	}

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

loop:	for(; tokenContext.hasMoreChars(); )
		{
			boolean backslash = false;
			char c = tokenContext.getChar();

			switch (res.token)
			{
				case Token.NULL:
					if (!this.standalone) {
						if (res.mode == ASPMode.CSJS) {
							if (tokenContext.regionMatches(true, "<%")) {
								tokenContext.doKeywordToPos(this.keywords);
								// Just return, let parent mode decide what to do next
								return res;
							}
						}
	
						if (res.mode == ASPMode.ASP) {
							if (tokenContext.regionMatches(true, "%>")) {
								tokenContext.doKeywordToPos(this.keywords);
								// Just return, let parent mode decide what to do next
								return res;
							}
						}
	
						if (res.mode == ASPMode.CSJS || res.mode == ASPMode.SSJS) {
							if (tokenContext.regionMatches(true, "</script>"))
							{
								tokenContext.doKeywordToPos(this.keywords);
								// Just return, let parent mode decide what to do next
								return res;
							}
						}
					}

					switch (c)
					{
						case '"':
							backslash = false;
							tokenContext.doKeywordToPos(this.keywords);
							tokenContext.addTokenToPos(res.token);
							res.token = Token.LITERAL1;
						break;

						case '\'':
							backslash = false;
							tokenContext.doKeywordToPos(this.keywords);
							tokenContext.addTokenToPos(res.token);
							res.token = Token.LITERAL2;
						break;

						case ':':
							if (tokenContext.lastKeyword == tokenContext.offset)
							{
								tokenContext.pos++;
								tokenContext.addTokenToPos(Token.LABEL);
								continue;
							}
						break;

						case '/':
							if (tokenContext.remainingChars() > 0)
							{
								switch (tokenContext.getChar(1))
								{
									case '*':
										tokenContext.doKeywordToPos(this.keywords);
										tokenContext.addTokenToPos(res.token);
										tokenContext.pos += 2;
										res.token = Token.COMMENT1;
									continue;

									case '/':
										tokenContext.doKeywordToPos(this.keywords);
										tokenContext.addTokenToPos(res.token);
										tokenContext.addTokenToEnd(Token.COMMENT1);
									break loop;
								}
							}

							tokenContext.doKeywordToPos(this.keywords);
							tokenContext.addTokenToPos(res.token);
							tokenContext.pos++;
							tokenContext.addTokenToPos(Token.OPERATOR);
						continue;

						// Operators
						// Unary:       ~ ++ --
						// Arithmetic:  + - * / %
						// Comparison:  == != < > <= >=
						// Boolean:     | & ^
						// Bit Shift:   << >> >>>
						// Logical:     ! || &&
						// Assignment:  = += -= *= /= %= |= &= ^= <<= >>= >>>= 
						// Conditional: ?:
						case '~':
						case '+': case '-': case '*': case '%':
						case '|': case '&': case '^':
						case '=': case '!': case '<': case '>':
							tokenContext.doKeywordToPos(this.keywords);
							tokenContext.addTokenToPos(res.token);
							tokenContext.pos++;
							tokenContext.addTokenToPos(Token.OPERATOR);
						continue;

						default:
							if (!Character.isLetterOrDigit(c)
								&& c != '_' && c != '$')
							{
								tokenContext.doKeywordToPos(this.keywords);
							}
						break;
					}
				break;

				case Token.COMMENT1:
				case Token.COMMENT2:
					if (tokenContext.regionMatches(true, "*/"))
					{
						tokenContext.pos += 2;
						tokenContext.addTokenToPos(res.token);
						res.token = Token.NULL;
						continue;
					}
				break;

				case Token.LITERAL1:
				{
					if (backslash)
					{
						backslash = false;
					}
					else if (c == '\\')
					{
						backslash = true;
					}
					else if (c == '"')
					{
						tokenContext.pos++;
						tokenContext.addTokenToPos(res.token);
						res.token = Token.NULL;
						continue;
					}
				}
				break;

				case Token.LITERAL2:
				{
					if (backslash)
					{
						backslash = false;
					}
					else if (c == '\\')
					{
						backslash = true;
					}
					else if (c == '\'')
					{
						tokenContext.pos++;
						tokenContext.addTokenToPos(tokenContext.pos, res.token);
						res.token = Token.NULL;
						continue;
					}
				}
				break;

				default:
					throw new InternalError("Invalid state: " + res.token);
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
			case Token.LITERAL2:
				tokenContext.addTokenToEnd(Token.INVALID);
				res.token = Token.NULL;
			break;

			default:
				tokenContext.addTokenToEnd(res.token);
			break;
		}

		return res;
	}

	public static KeywordMap getKeywords()
	{
		if(javaScriptKeywords == null)
		{
			javaScriptKeywords = new KeywordMap(false);

			// ECMAScript keywords
			javaScriptKeywords.add("break",    Token.KEYWORD1);
			javaScriptKeywords.add("continue", Token.KEYWORD1);
			javaScriptKeywords.add("delete",   Token.KEYWORD1);
			javaScriptKeywords.add("else",     Token.KEYWORD1);
			javaScriptKeywords.add("for",      Token.KEYWORD1);
			javaScriptKeywords.add("function", Token.KEYWORD1);
			javaScriptKeywords.add("if",       Token.KEYWORD1);
			javaScriptKeywords.add("in",       Token.KEYWORD1);
			javaScriptKeywords.add("new",      Token.KEYWORD1);
			javaScriptKeywords.add("return",   Token.KEYWORD1);
			javaScriptKeywords.add("this",     Token.LITERAL2);
			javaScriptKeywords.add("typeof",   Token.KEYWORD1);
			javaScriptKeywords.add("var",      Token.KEYWORD1);
			javaScriptKeywords.add("void",     Token.KEYWORD3);
			javaScriptKeywords.add("while",    Token.KEYWORD1);
			javaScriptKeywords.add("with",     Token.KEYWORD1);

			// ECMAScript keywords
			// Reserved for future use (some are already used in some Javascripts Engines)
			javaScriptKeywords.add("abstract", Token.KEYWORD1);
			javaScriptKeywords.add("boolean",  Token.KEYWORD3);
			javaScriptKeywords.add("byte",     Token.KEYWORD3);
			javaScriptKeywords.add("case",     Token.KEYWORD1);
			javaScriptKeywords.add("catch",    Token.KEYWORD1);
			javaScriptKeywords.add("char",     Token.KEYWORD3);
			javaScriptKeywords.add("class",    Token.KEYWORD1);
			javaScriptKeywords.add("const",    Token.KEYWORD1);
			javaScriptKeywords.add("debugger", Token.KEYWORD1);
			javaScriptKeywords.add("default",  Token.KEYWORD1);

			javaScriptKeywords.add("do",         Token.KEYWORD1);
			javaScriptKeywords.add("double",     Token.KEYWORD3);
			javaScriptKeywords.add("enum",       Token.KEYWORD1);
			javaScriptKeywords.add("export",     Token.KEYWORD2);
			javaScriptKeywords.add("extends",    Token.KEYWORD1);
			javaScriptKeywords.add("final",      Token.KEYWORD1);
			javaScriptKeywords.add("finally",    Token.KEYWORD1);
			javaScriptKeywords.add("float",      Token.KEYWORD3);
			javaScriptKeywords.add("goto",       Token.KEYWORD1);
			javaScriptKeywords.add("implements", Token.KEYWORD1);

			javaScriptKeywords.add("import",     Token.KEYWORD2);
			javaScriptKeywords.add("instanceof", Token.KEYWORD1);
			javaScriptKeywords.add("int",        Token.KEYWORD3);
			javaScriptKeywords.add("interface",  Token.KEYWORD1);
			javaScriptKeywords.add("long",       Token.KEYWORD3);
			javaScriptKeywords.add("native",     Token.KEYWORD1);
			javaScriptKeywords.add("package",    Token.KEYWORD2);
			javaScriptKeywords.add("private",    Token.KEYWORD1);
			javaScriptKeywords.add("protected",  Token.KEYWORD1);
			javaScriptKeywords.add("public",     Token.KEYWORD1);

			javaScriptKeywords.add("short",        Token.KEYWORD3);
			javaScriptKeywords.add("static",       Token.KEYWORD1);
			javaScriptKeywords.add("super",        Token.LITERAL2);
			javaScriptKeywords.add("switch",       Token.KEYWORD1);
			javaScriptKeywords.add("synchronized", Token.KEYWORD1);
			javaScriptKeywords.add("throw",        Token.KEYWORD1);
			javaScriptKeywords.add("throws",       Token.KEYWORD1);
			javaScriptKeywords.add("transient",    Token.KEYWORD1);
			javaScriptKeywords.add("try",          Token.KEYWORD1);
			javaScriptKeywords.add("volatile",     Token.KEYWORD1);

			// Intrinsic Objects (Good idea not to use these names!!)
			javaScriptKeywords.add("Array",    Token.KEYWORD3);
			javaScriptKeywords.add("Boolean",  Token.KEYWORD3);
			javaScriptKeywords.add("Date",     Token.KEYWORD3);
			javaScriptKeywords.add("Function", Token.KEYWORD3);
			javaScriptKeywords.add("Global",   Token.KEYWORD3);
			javaScriptKeywords.add("Math",     Token.KEYWORD3);
			javaScriptKeywords.add("Number",   Token.KEYWORD3);
			javaScriptKeywords.add("Object",   Token.KEYWORD3);
			javaScriptKeywords.add("RegExp",   Token.KEYWORD3); // NON-ECMA
			javaScriptKeywords.add("String",   Token.KEYWORD3);

			// Literals
			javaScriptKeywords.add("false",     Token.LITERAL2);
			javaScriptKeywords.add("null",      Token.LITERAL2);
			javaScriptKeywords.add("true",      Token.LITERAL2);

			javaScriptKeywords.add("NaN",       Token.LITERAL2);
			javaScriptKeywords.add("Infinity",  Token.LITERAL2);

			// Global functions
			javaScriptKeywords.add("eval",       Token.LITERAL2);
			javaScriptKeywords.add("parseInt",   Token.LITERAL2);
			javaScriptKeywords.add("parseFloat", Token.LITERAL2);
			javaScriptKeywords.add("escape",     Token.LITERAL2);
			javaScriptKeywords.add("unescape",   Token.LITERAL2);
			javaScriptKeywords.add("isNaN",      Token.LITERAL2);
			javaScriptKeywords.add("isFinite",   Token.LITERAL2);

			// Comment/Uncomment your favorite ADO Constants

			// ADO Constants

			//---- CursorTypeEnum Values ----
			javaScriptKeywords.add("adOpenForwardOnly", Token.LITERAL2);
			javaScriptKeywords.add("adOpenKeyset", Token.LITERAL2);
			javaScriptKeywords.add("adOpenDynamic", Token.LITERAL2);
			javaScriptKeywords.add("adOpenStatic", Token.LITERAL2);
			
			//---- CursorOptionEnum Values ----
			javaScriptKeywords.add("adHoldRecords", Token.LITERAL2);
			javaScriptKeywords.add("adMovePrevious", Token.LITERAL2);
			javaScriptKeywords.add("adAddNew", Token.LITERAL2);
			javaScriptKeywords.add("adDelete", Token.LITERAL2);
			javaScriptKeywords.add("adUpdate", Token.LITERAL2);
			javaScriptKeywords.add("adBookmark", Token.LITERAL2);
			javaScriptKeywords.add("adApproxPosition", Token.LITERAL2);
			javaScriptKeywords.add("adUpdateBatch", Token.LITERAL2);
			javaScriptKeywords.add("adResync", Token.LITERAL2);
			javaScriptKeywords.add("adNotify", Token.LITERAL2);
			javaScriptKeywords.add("adFind", Token.LITERAL2);
			javaScriptKeywords.add("adSeek", Token.LITERAL2);
			javaScriptKeywords.add("adIndex", Token.LITERAL2);
			
			//---- LockTypeEnum Values ----
			javaScriptKeywords.add("adLockReadOnly", Token.LITERAL2);
			javaScriptKeywords.add("adLockPessimistic", Token.LITERAL2);
			javaScriptKeywords.add("adLockOptimistic", Token.LITERAL2);
			javaScriptKeywords.add("adLockBatchOptimistic", Token.LITERAL2);
			
			//---- ExecuteOptionEnum Values ----
			javaScriptKeywords.add("adRunAsync", Token.LITERAL2);
			javaScriptKeywords.add("adAsyncExecute", Token.LITERAL2);
			javaScriptKeywords.add("adAsyncFetch", Token.LITERAL2);
			javaScriptKeywords.add("adAsyncFetchNonBlocking", Token.LITERAL2);
			javaScriptKeywords.add("adExecuteNoRecords", Token.LITERAL2);
			
			//---- ConnectOptionEnum Values ----
			javaScriptKeywords.add("adAsyncConnect", Token.LITERAL2);
			
			//---- ObjectStateEnum Values ----
			javaScriptKeywords.add("adStateClosed", Token.LITERAL2);
			javaScriptKeywords.add("adStateOpen", Token.LITERAL2);
			javaScriptKeywords.add("adStateConnecting", Token.LITERAL2);
			javaScriptKeywords.add("adStateExecuting", Token.LITERAL2);
			javaScriptKeywords.add("adStateFetching", Token.LITERAL2);
			
			//---- CursorLocationEnum Values ----
			javaScriptKeywords.add("adUseServer", Token.LITERAL2);
			javaScriptKeywords.add("adUseClient", Token.LITERAL2);
			
			//---- DataTypeEnum Values ----
			javaScriptKeywords.add("adEmpty", Token.LITERAL2);
			javaScriptKeywords.add("adTinyInt", Token.LITERAL2);
			javaScriptKeywords.add("adSmallInt", Token.LITERAL2);
			javaScriptKeywords.add("adInteger", Token.LITERAL2);
			javaScriptKeywords.add("adBigInt", Token.LITERAL2);
			javaScriptKeywords.add("adUnsignedTinyInt", Token.LITERAL2);
			javaScriptKeywords.add("adUnsignedSmallInt", Token.LITERAL2);
			javaScriptKeywords.add("adUnsignedInt", Token.LITERAL2);
			javaScriptKeywords.add("adUnsignedBigInt", Token.LITERAL2);
			javaScriptKeywords.add("adSingle", Token.LITERAL2);
			javaScriptKeywords.add("adDouble", Token.LITERAL2);
			javaScriptKeywords.add("adCurrency", Token.LITERAL2);
			javaScriptKeywords.add("adDecimal", Token.LITERAL2);
			javaScriptKeywords.add("adNumeric", Token.LITERAL2);
			javaScriptKeywords.add("adBoolean", Token.LITERAL2);
			javaScriptKeywords.add("adError", Token.LITERAL2);
			javaScriptKeywords.add("adUserDefined", Token.LITERAL2);
			javaScriptKeywords.add("adVariant", Token.LITERAL2);
			javaScriptKeywords.add("adIDispatch", Token.LITERAL2);
			javaScriptKeywords.add("adIUnknown", Token.LITERAL2);
			javaScriptKeywords.add("adGUID", Token.LITERAL2);
			javaScriptKeywords.add("adDate", Token.LITERAL2);
			javaScriptKeywords.add("adDBDate", Token.LITERAL2);
			javaScriptKeywords.add("adDBTime", Token.LITERAL2);
			javaScriptKeywords.add("adDBTimeStamp", Token.LITERAL2);
			javaScriptKeywords.add("adBSTR", Token.LITERAL2);
			javaScriptKeywords.add("adChar", Token.LITERAL2);
			javaScriptKeywords.add("adVarChar", Token.LITERAL2);
			javaScriptKeywords.add("adLongVarChar", Token.LITERAL2);
			javaScriptKeywords.add("adWChar", Token.LITERAL2);
			javaScriptKeywords.add("adVarWChar", Token.LITERAL2);
			javaScriptKeywords.add("adLongVarWChar", Token.LITERAL2);
			javaScriptKeywords.add("adBinary", Token.LITERAL2);
			javaScriptKeywords.add("adVarBinary", Token.LITERAL2);
			javaScriptKeywords.add("adLongVarBinary", Token.LITERAL2);
			javaScriptKeywords.add("adChapter", Token.LITERAL2);
			javaScriptKeywords.add("adFileTime", Token.LITERAL2);
			javaScriptKeywords.add("adDBFileTime", Token.LITERAL2);
			javaScriptKeywords.add("adPropVariant", Token.LITERAL2);
			javaScriptKeywords.add("adVarNumeric", Token.LITERAL2);

			/*			
			//---- FieldAttributeEnum Values ----
			javaScriptKeywords.add("adFldMayDefer", Token.LITERAL2);
			javaScriptKeywords.add("adFldUpdatable", Token.LITERAL2);
			javaScriptKeywords.add("adFldUnknownUpdatable", Token.LITERAL2);
			javaScriptKeywords.add("adFldFixed", Token.LITERAL2);
			javaScriptKeywords.add("adFldIsNullable", Token.LITERAL2);
			javaScriptKeywords.add("adFldMayBeNull", Token.LITERAL2);
			javaScriptKeywords.add("adFldLong", Token.LITERAL2);
			javaScriptKeywords.add("adFldRowID", Token.LITERAL2);
			javaScriptKeywords.add("adFldRowVersion", Token.LITERAL2);
			javaScriptKeywords.add("adFldCacheDeferred", Token.LITERAL2);
			javaScriptKeywords.add("adFldKeyColumn", Token.LITERAL2);
			
			//---- EditModeEnum Values ----
			javaScriptKeywords.add("adEditNone", Token.LITERAL2);
			javaScriptKeywords.add("adEditInProgress", Token.LITERAL2);
			javaScriptKeywords.add("adEditAdd", Token.LITERAL2);
			javaScriptKeywords.add("adEditDelete", Token.LITERAL2);
			
			//---- RecordStatusEnum Values ----
			javaScriptKeywords.add("adRecOK", Token.LITERAL2);
			javaScriptKeywords.add("adRecNew", Token.LITERAL2);
			javaScriptKeywords.add("adRecModified", Token.LITERAL2);
			javaScriptKeywords.add("adRecDeleted", Token.LITERAL2);
			javaScriptKeywords.add("adRecUnmodified", Token.LITERAL2);
			javaScriptKeywords.add("adRecInvalid", Token.LITERAL2);
			javaScriptKeywords.add("adRecMultipleChanges", Token.LITERAL2);
			javaScriptKeywords.add("adRecPendingChanges", Token.LITERAL2);
			javaScriptKeywords.add("adRecCanceled", Token.LITERAL2);
			javaScriptKeywords.add("adRecCantRelease", Token.LITERAL2);
			javaScriptKeywords.add("adRecConcurrencyViolation", Token.LITERAL2);
			javaScriptKeywords.add("adRecIntegrityViolation", Token.LITERAL2);
			javaScriptKeywords.add("adRecMaxChangesExceeded", Token.LITERAL2);
			javaScriptKeywords.add("adRecObjectOpen", Token.LITERAL2);
			javaScriptKeywords.add("adRecOutOfMemory", Token.LITERAL2);
			javaScriptKeywords.add("adRecPermissionDenied", Token.LITERAL2);
			javaScriptKeywords.add("adRecSchemaViolation", Token.LITERAL2);
			javaScriptKeywords.add("adRecDBDeleted", Token.LITERAL2);
			
			//---- GetRowsOptionEnum Values ----
			javaScriptKeywords.add("adGetRowsRest", Token.LITERAL2);
			
			//---- PositionEnum Values ----
			javaScriptKeywords.add("adPosUnknown", Token.LITERAL2);
			javaScriptKeywords.add("adPosBOF", Token.LITERAL2);
			javaScriptKeywords.add("adPosEOF", Token.LITERAL2);
			
			//---- enum Values ----
			javaScriptKeywords.add("adBookmarkCurrent", Token.LITERAL2);
			javaScriptKeywords.add("adBookmarkFirst", Token.LITERAL2);
			javaScriptKeywords.add("adBookmarkLast", Token.LITERAL2);
			
			//---- MarshalOptionsEnum Values ----
			javaScriptKeywords.add("adMarshalAll", Token.LITERAL2);
			javaScriptKeywords.add("adMarshalModifiedOnly", Token.LITERAL2);
			
			//---- AffectEnum Values ----
			javaScriptKeywords.add("adAffectCurrent", Token.LITERAL2);
			javaScriptKeywords.add("adAffectGroup", Token.LITERAL2);
			javaScriptKeywords.add("adAffectAll", Token.LITERAL2);
			javaScriptKeywords.add("adAffectAllChapters", Token.LITERAL2);
			
			//---- ResyncEnum Values ----
			javaScriptKeywords.add("adResyncUnderlyingValues", Token.LITERAL2);
			javaScriptKeywords.add("adResyncAllValues", Token.LITERAL2);
			
			//---- CompareEnum Values ----
			javaScriptKeywords.add("adCompareLessThan", Token.LITERAL2);
			javaScriptKeywords.add("adCompareEqual", Token.LITERAL2);
			javaScriptKeywords.add("adCompareGreaterThan", Token.LITERAL2);
			javaScriptKeywords.add("adCompareNotEqual", Token.LITERAL2);
			javaScriptKeywords.add("adCompareNotComparable", Token.LITERAL2);
			
			//---- FilterGroupEnum Values ----
			javaScriptKeywords.add("adFilterNone", Token.LITERAL2);
			javaScriptKeywords.add("adFilterPendingRecords", Token.LITERAL2);
			javaScriptKeywords.add("adFilterAffectedRecords", Token.LITERAL2);
			javaScriptKeywords.add("adFilterFetchedRecords", Token.LITERAL2);
			javaScriptKeywords.add("adFilterPredicate", Token.LITERAL2);
			javaScriptKeywords.add("adFilterConflictingRecords", Token.LITERAL2);
			
			//---- SearchDirectionEnum Values ----
			javaScriptKeywords.add("adSearchForward", Token.LITERAL2);
			javaScriptKeywords.add("adSearchBackward", Token.LITERAL2);
			
			//---- PersistFormatEnum Values ----
			javaScriptKeywords.add("adPersistADTG", Token.LITERAL2);
			javaScriptKeywords.add("adPersistXML", Token.LITERAL2);
			
			//---- StringFormatEnum Values ----
			javaScriptKeywords.add("adStringXML", Token.LITERAL2);
			javaScriptKeywords.add("adStringHTML", Token.LITERAL2);
			javaScriptKeywords.add("adClipString", Token.LITERAL2);
			
			//---- ConnectPromptEnum Values ----
			javaScriptKeywords.add("adPromptAlways", Token.LITERAL2);
			javaScriptKeywords.add("adPromptComplete", Token.LITERAL2);
			javaScriptKeywords.add("adPromptCompleteRequired", Token.LITERAL2);
			javaScriptKeywords.add("adPromptNever", Token.LITERAL2);
			
			//---- ConnectModeEnum Values ----
			javaScriptKeywords.add("adModeUnknown", Token.LITERAL2);
			javaScriptKeywords.add("adModeRead", Token.LITERAL2);
			javaScriptKeywords.add("adModeWrite", Token.LITERAL2);
			javaScriptKeywords.add("adModeReadWrite", Token.LITERAL2);
			javaScriptKeywords.add("adModeShareDenyRead", Token.LITERAL2);
			javaScriptKeywords.add("adModeShareDenyWrite", Token.LITERAL2);
			javaScriptKeywords.add("adModeShareExclusive", Token.LITERAL2);
			javaScriptKeywords.add("adModeShareDenyNone", Token.LITERAL2);
			
			//---- IsolationLevelEnum Values ----
			javaScriptKeywords.add("adXactUnspecified", Token.LITERAL2);
			javaScriptKeywords.add("adXactChaos", Token.LITERAL2);
			javaScriptKeywords.add("adXactReadUncommitted", Token.LITERAL2);
			javaScriptKeywords.add("adXactBrowse", Token.LITERAL2);
			javaScriptKeywords.add("adXactCursorStability", Token.LITERAL2);
			javaScriptKeywords.add("adXactReadCommitted", Token.LITERAL2);
			javaScriptKeywords.add("adXactRepeatableRead", Token.LITERAL2);
			javaScriptKeywords.add("adXactSerializable", Token.LITERAL2);
			javaScriptKeywords.add("adXactIsolated", Token.LITERAL2);
			
			//---- XactAttributeEnum Values ----
			javaScriptKeywords.add("adXactCommitRetaining", Token.LITERAL2);
			javaScriptKeywords.add("adXactAbortRetaining", Token.LITERAL2);
			
			//---- PropertyAttributesEnum Values ----
			javaScriptKeywords.add("adPropNotSupported", Token.LITERAL2);
			javaScriptKeywords.add("adPropRequired", Token.LITERAL2);
			javaScriptKeywords.add("adPropOptional", Token.LITERAL2);
			javaScriptKeywords.add("adPropRead", Token.LITERAL2);
			javaScriptKeywords.add("adPropWrite", Token.LITERAL2);
			
			//---- ErrorValueEnum Values ----
			javaScriptKeywords.add("adErrInvalidArgument", Token.LITERAL2);
			javaScriptKeywords.add("adErrNoCurrentRecord", Token.LITERAL2);
			javaScriptKeywords.add("adErrIllegalOperation", Token.LITERAL2);
			javaScriptKeywords.add("adErrInTransaction", Token.LITERAL2);
			javaScriptKeywords.add("adErrFeatureNotAvailable", Token.LITERAL2);
			javaScriptKeywords.add("adErrItemNotFound", Token.LITERAL2);
			javaScriptKeywords.add("adErrObjectInCollection", Token.LITERAL2);
			javaScriptKeywords.add("adErrObjectNotSet", Token.LITERAL2);
			javaScriptKeywords.add("adErrDataConversion", Token.LITERAL2);
			javaScriptKeywords.add("adErrObjectClosed", Token.LITERAL2);
			javaScriptKeywords.add("adErrObjectOpen", Token.LITERAL2);
			javaScriptKeywords.add("adErrProviderNotFound", Token.LITERAL2);
			javaScriptKeywords.add("adErrBoundToCommand", Token.LITERAL2);
			javaScriptKeywords.add("adErrInvalidParamInfo", Token.LITERAL2);
			javaScriptKeywords.add("adErrInvalidConnection", Token.LITERAL2);
			javaScriptKeywords.add("adErrNotReentrant", Token.LITERAL2);
			javaScriptKeywords.add("adErrStillExecuting", Token.LITERAL2);
			javaScriptKeywords.add("adErrOperationCancelled", Token.LITERAL2);
			javaScriptKeywords.add("adErrStillConnecting", Token.LITERAL2);
			javaScriptKeywords.add("adErrNotExecuting", Token.LITERAL2);
			javaScriptKeywords.add("adErrUnsafeOperation", Token.LITERAL2);
			
			//---- ParameterAttributesEnum Values ----
			javaScriptKeywords.add("adParamSigned", Token.LITERAL2);
			javaScriptKeywords.add("adParamNullable", Token.LITERAL2);
			javaScriptKeywords.add("adParamLong", Token.LITERAL2);
			
			//---- ParameterDirectionEnum Values ----
			javaScriptKeywords.add("adParamUnknown", Token.LITERAL2);
			javaScriptKeywords.add("adParamInput", Token.LITERAL2);
			javaScriptKeywords.add("adParamOutput", Token.LITERAL2);
			javaScriptKeywords.add("adParamInputOutput", Token.LITERAL2);
			javaScriptKeywords.add("adParamReturnValue", Token.LITERAL2);
			
			//---- CommandTypeEnum Values ----
			javaScriptKeywords.add("adCmdUnknown", Token.LITERAL2);
			javaScriptKeywords.add("adCmdText", Token.LITERAL2);
			javaScriptKeywords.add("adCmdTable", Token.LITERAL2);
			javaScriptKeywords.add("adCmdStoredProc", Token.LITERAL2);
			javaScriptKeywords.add("adCmdFile", Token.LITERAL2);
			javaScriptKeywords.add("adCmdTableDirect", Token.LITERAL2);
			
			//---- EventStatusEnum Values ----
			javaScriptKeywords.add("adStatusOK", Token.LITERAL2);
			javaScriptKeywords.add("adStatusErrorsOccurred", Token.LITERAL2);
			javaScriptKeywords.add("adStatusCantDeny", Token.LITERAL2);
			javaScriptKeywords.add("adStatusCancel", Token.LITERAL2);
			javaScriptKeywords.add("adStatusUnwantedEvent", Token.LITERAL2);
			
			//---- EventReasonEnum Values ----
			javaScriptKeywords.add("adRsnAddNew", Token.LITERAL2);
			javaScriptKeywords.add("adRsnDelete", Token.LITERAL2);
			javaScriptKeywords.add("adRsnUpdate", Token.LITERAL2);
			javaScriptKeywords.add("adRsnUndoUpdate", Token.LITERAL2);
			javaScriptKeywords.add("adRsnUndoAddNew", Token.LITERAL2);
			javaScriptKeywords.add("adRsnUndoDelete", Token.LITERAL2);
			javaScriptKeywords.add("adRsnRequery", Token.LITERAL2);
			javaScriptKeywords.add("adRsnResynch", Token.LITERAL2);
			javaScriptKeywords.add("adRsnClose", Token.LITERAL2);
			javaScriptKeywords.add("adRsnMove", Token.LITERAL2);
			javaScriptKeywords.add("adRsnFirstChange", Token.LITERAL2);
			javaScriptKeywords.add("adRsnMoveFirst", Token.LITERAL2);
			javaScriptKeywords.add("adRsnMoveNext", Token.LITERAL2);
			javaScriptKeywords.add("adRsnMovePrevious", Token.LITERAL2);
			javaScriptKeywords.add("adRsnMoveLast", Token.LITERAL2);
			
			//---- SchemaEnum Values ----
			javaScriptKeywords.add("adSchemaProviderSpecific", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaAsserts", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaCatalogs", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaCharacterSets", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaCollations", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaColumns", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaCheckConstraints", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaConstraintColumnUsage", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaConstraintTableUsage", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaKeyColumnUsage", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaReferentialConstraints", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaTableConstraints", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaColumnsDomainUsage", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaIndexes", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaColumnPrivileges", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaTablePrivileges", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaUsagePrivileges", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaProcedures", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaSchemata", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaSQLLanguages", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaStatistics", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaTables", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaTranslations", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaProviderTypes", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaViews", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaViewColumnUsage", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaViewTableUsage", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaProcedureParameters", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaForeignKeys", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaPrimaryKeys", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaProcedureColumns", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaDBInfoKeywords", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaDBInfoLiterals", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaCubes", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaDimensions", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaHierarchies", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaLevels", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaMeasures", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaProperties", Token.LITERAL2);
			javaScriptKeywords.add("adSchemaMembers", Token.LITERAL2);
			
			//---- SeekEnum Values ----
			javaScriptKeywords.add("adSeekFirstEQ", Token.LITERAL2);
			javaScriptKeywords.add("adSeekLastEQ", Token.LITERAL2);
			javaScriptKeywords.add("adSeekAfterEQ", Token.LITERAL2);
			javaScriptKeywords.add("adSeekAfter", Token.LITERAL2);
			javaScriptKeywords.add("adSeekBeforeEQ", Token.LITERAL2);
			javaScriptKeywords.add("adSeekBefore", Token.LITERAL2);
			
			//---- ADCPROP_UPDATECRITERIA_ENUM Values ----
			javaScriptKeywords.add("adCriteriaKey", Token.LITERAL2);
			javaScriptKeywords.add("adCriteriaAllCols", Token.LITERAL2);
			javaScriptKeywords.add("adCriteriaUpdCols", Token.LITERAL2);
			javaScriptKeywords.add("adCriteriaTimeStamp", Token.LITERAL2);
			
			//---- ADCPROP_ASYNCTHREADPRIORITY_ENUM Values ----
			javaScriptKeywords.add("adPriorityLowest", Token.LITERAL2);
			javaScriptKeywords.add("adPriorityBelowNormal", Token.LITERAL2);
			javaScriptKeywords.add("adPriorityNormal", Token.LITERAL2);
			javaScriptKeywords.add("adPriorityAboveNormal", Token.LITERAL2);
			javaScriptKeywords.add("adPriorityHighest", Token.LITERAL2);
			
			//---- CEResyncEnum Values ----
			javaScriptKeywords.add("adResyncNone", Token.LITERAL2);
			javaScriptKeywords.add("adResyncAutoIncrement", Token.LITERAL2);
			javaScriptKeywords.add("adResyncConflicts", Token.LITERAL2);
			javaScriptKeywords.add("adResyncUpdates", Token.LITERAL2);
			javaScriptKeywords.add("adResyncInserts", Token.LITERAL2);
			javaScriptKeywords.add("adResyncAll", Token.LITERAL2);
			
			//---- ADCPROP_AUTORECALC_ENUM Values ----
			javaScriptKeywords.add("adRecalcUpFront", Token.LITERAL2);
			javaScriptKeywords.add("adRecalcAlways", Token.LITERAL2);
			*/
		}
		return javaScriptKeywords;
	}

	// private members
	private static KeywordMap javaScriptKeywords;

	// private members
	private KeywordMap keywords;
	private boolean    standalone;
}
