/*
 * TokenSource.java - Token producer for a given JBrowseParser.LineSource
 *
 * Copyright (c) 1999 George Latkiewicz	(georgel@arvotek.net)
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

import java.util.StringTokenizer;

//=============================================================================
// This class implements a basic Java oriented tokenizer for the set of lines
// that are made available by a given JBrowseParser.LineSource object.

public class TokenSource {

	// Private Attributes

	private JBrowseParser.LineSource ls;

	private static final String DELIM = "\t\n\r\f " // whitespace
			+ "\"'*/{}()[],&|!~+-;:=\\";

	private int currentLineNum = -1;
	private int currentPos = 0;			// the current position within the tokenizer's line
										// (i.e. the position of the start of the current token.)

	private int tokenizerOffset = 0;	// offset of tokenizer's line start from source line's start.

	private String lineString;
	private String currentToken;  // set each time getNextToken() is called

	private StringTokenizer st;

	private int debugLevel;

	// Accessor methods

	public final int getCurrentLineNum() { return currentLineNum; }
	public final int getCurrentPos() { return currentPos + tokenizerOffset; }
	public final String getCurrentLine() { return lineString; }


	//-------------------------------------------------------------------------
	/**
	 * Construct a TokenSource (with debugging off)
	 */
	TokenSource(JBrowseParser.LineSource ls) {

		this(ls, 0);

	} // TokenSource(): <init>


	//-------------------------------------------------------------------------
	/**
	 * Construct a TokenSource (with the specified debugging level)
	 */
	TokenSource(JBrowseParser.LineSource ls, int debugLevel) {

		this.ls = ls;
		this.debugLevel = debugLevel;

		st = new StringTokenizer(getNextLine(), DELIM, true);

	} // TokenSource(): <init>

	//-------------------------------------------------------------------------
	private final String getNextLine() {

		lineString = ls.getLine(++currentLineNum);
		return lineString;

	} // getNextLine(): String


	//-------------------------------------------------------------------------
	// Returns next token, null if source is exhausted. Automatically skips
	// white space, single and multi-line comments and strings.

	public String getNextToken() throws Exception {

		int pos;

		while (true) {

			// update currentPos to after last token
			if ( currentToken != null) {
				currentPos += currentToken.length(); // assume it will start on same line
			}

			// skip to next line with tokens
			while ( !st.hasMoreTokens() && !ls.isExhausted() ) {
				currentPos = 0;
				tokenizerOffset = 0;
				try {
					st = new StringTokenizer(getNextLine(), DELIM, true);
				} catch (java.lang.Exception e) {
					System.out.println("Exception caught in TokenSource.getNextToken\n\t"
					+ "While trying to create a non-null StringTokenizer\n\t" + e.getMessage() );
					e.printStackTrace(System.out);
					return null;
				}
			}
			if ( !st.hasMoreTokens() ) {
				return null;
			}

			// handle the token
			try {
				currentToken = st.nextToken();
			} catch (java.lang.Exception e) {
				System.out.println("Exception caught in TokenSource.getNextToken\n\t"
				+ "While calling st.nextToken() immediately after verifying st.hasMoreTokens()!\n\t" + e.getMessage() );
				e.printStackTrace(System.out);
				return null;
			}

			pos = currentPos + tokenizerOffset;

//%			// if appropriate display each token at this point
//%			if (debugLevel >= 12) {
//%				System.out.println(">>> " + currentLineNum + "-" + currentPos
//%						+ " (+" + tokenizerOffset + ") :" + currentToken);
//%			}

			if ( currentToken.length() == 1 ) {

				char c = currentToken.charAt(0);
				boolean isEscaped = false;

				switch(c) {

				case ' ': case '\t': case '\f':
					continue;

				case '\'':
					// handle chars, find end of char

					// to be precise should check if we do in fact define a single char ???,
					// currently insures we have a valid string of chars.
					while (true) {

						if ( !st.hasMoreTokens() ) {
//%							System.out.println("ERROR: Line has unmatched ', will return ' and attempt to continue.");
							throw new Exception(Exception.MESSAGE_CHAR_EXPR);
						}

						currentToken += st.nextToken();

						if ( isEscaped ) {
							isEscaped = false;
						} else if ( currentToken.charAt(currentToken.length()-1) == '\'' ) {
							break;
						} else if ( currentToken.charAt(currentToken.length()-1) == '\\' ) {
							isEscaped = true;
						}

					} // while (true)

//%					if (debugLevel >= 12) {
//%						System.out.println("found string: " + currentToken);
//%					}

					continue;

				case '\"':
					// handle strings, find next unescaped double-quote (")

					while (true) {

						if (! st.hasMoreTokens() ) {
//%							System.out.println("ERROR: Line has unmatched \", will return \" and attempt to continue.");
							throw new Exception(Exception.MESSAGE_STRING_EXPR);
						}

						currentToken += st.nextToken();

						if ( isEscaped ) {
							isEscaped = false;
						} else if ( currentToken.charAt(currentToken.length()-1) == '\"' ) {
							break;
						} else if ( currentToken.charAt(currentToken.length()-1) == '\\' ) {
							isEscaped = true;
						}

					} // while (true)

//%					if (debugLevel >= 12) {
//%						System.out.println("found string: " + currentToken);
//%					}

					continue;

				case '/':

					if ( lineString.length() > pos+1 ) {

//%						if (debugLevel >= 12) {
//%							System.out.println("\tchar at currentPos+tokenizerOffset+1 (" + (pos+1) + ") "
//%									+ lineString.charAt(pos+1)); // debug
//%						}

						// check for '/' or '*' as next char on this line

						if ( '/' == lineString.charAt(pos+1) ) {

							// start of single line comment

//%							if (debugLevel >= 12) {
//%								System.out.println("found single-line comment: " + lineString);
//%							}
							skipRestOfLine();

							// prepare to get next token (on a subsequent line)
							currentToken = null;
							continue;

						} else if ( '*' == lineString.charAt(pos+1) ) {

							// start of mult-line comment

//%							if (debugLevel >= 12) {
//%								System.out.println("found multi-line comment: " + lineString);
//%							}

							// find terminating "*/"

							tokenizerOffset = lineString.indexOf("*/", pos+2);
							if (tokenizerOffset == -1 ) {
								// skip lines until found
								do {
									lineString = getNextLine();
									if (lineString == null) {
										// not very nice, this may happen if we end with
										// an unterminated comment.
										throw new Exception(Exception.MESSAGE_UNTERM_COMMENT);
									}
									tokenizerOffset = lineString.indexOf("*/");
								} while ( tokenizerOffset == -1) ;

								// will start tokenizer within this new line
							}

//%							if (debugLevel >= 12) {
//%								System.out.println("found end of multi-line comment.");
//%							}


							tokenizerOffset += 2;

							if (tokenizerOffset >= lineString.length() ) {
								// found at end of current line
								// will start tokenizer on next line
								lineString = getNextLine();
								if (lineString == null) {
									// not very nice, this may happen if we end with
									// a comment.
									// System.out.println("warning: file ends with a multi-line comment.");
									return null;
								}

								tokenizerOffset = 0;
							}

							// reconfigure tokenizer
							currentPos = 0;
							try {
								st = new StringTokenizer(lineString.substring(tokenizerOffset), DELIM, true);
								currentToken = null;

								continue;
							} catch (java.lang.Exception e) {
								System.out.println("Exception caught in TokenSource.getNextToken\n\t"
								+ "While trying to re-configure the Tokenizer\n\t" + e.getMessage() );
								e.printStackTrace(System.out);
								return null;
							}

						} // if

					} // if

				} //switch(c)

			} // if

			break;

		} // while (true)

//%	System.out.println("getNextToken() - returns: " + currentToken);
		return currentToken;

	} // getNextToken(): String


	//-------------------------------------------------------------------------
	public void skipRestOfLine() {

//%		if (debugLevel >= 12) {
//%			System.out.println("TokenSource.skipRestOfLine() called");
//%		}

		currentPos = 0;
		tokenizerOffset = 0;

		do {
			if ( ls.isExhausted() ) {

				// need to ensure that we have exhausted the last line
				while ( st.hasMoreTokens() ) {
//%					if (debugLevel >= 12) {
//%						System.out.println("Exhausting last line: "
//%							+ st.nextToken());
//%					} else {
						st.nextToken();
//%					}
				}

//%				if (debugLevel >= 12) {
//%					System.out.println("Finished exhausting last line.");
//%				}

				return;
			}

			st = new StringTokenizer(getNextLine(), DELIM, true);

		} while ( !st.hasMoreTokens() );

	} // skipRestOfLine(): void


	//-------------------------------------------------------------------------
	// Skip subsequent tokens until the specified token is found (while taking
	// into acount matching of parenthesis and braces) and then return true.
	// If the token is not found before the source is exhausted or unbalanced
	// braces or parentheses are encountered return false.

	public final boolean skipUntil(String skipToToken) throws Exception {

		String curToken = getNextToken();

		int braceCount = 0;
		int parenthCount = 0;

		while ( curToken != null ) {

			if ( skipToToken.equals(curToken)
					&& braceCount == 0 && parenthCount == 0 ) {

//%			if (debugLevel > 7) {
//%				System.out.println("Skipped to '" + skipToToken + "': "
//%						+ getCurrentLineNum() + "-" + getCurrentPos() );
//%			}

				return true;

			} else if ("{".equals(curToken) ) {
				braceCount++;
			} else if ("(".equals(curToken) ) {
				parenthCount++;
			} else if ("}".equals(curToken) ) {
				braceCount--;
			} else if (")".equals(curToken) ) {
				parenthCount--;
			}

			if ( braceCount < 0 || parenthCount < 0 ) {
				break;
			}

			curToken = getNextToken();

		} // while

//%		if (debugLevel > 0) {
//%			System.out.println("WARNING: failed to skip to '" + skipToToken
//%					+ "': " + getCurrentLineNum() + "-" + getCurrentPos() );
//%		}

		return false;

	} // skipUntil(String): void


	//-------------------------------------------------------------------------
	// Skip subsequent tokens until the a token is found that matches one of
	// the tokens in the argument array (while taking into acount matching of
	// parenthesis and braces) and then return the matched token.
	// If a match is not found before the source is exhausted or unbalanced
	// braces or parentheses are encountered return null. If the passed array
	// length == 0 also returns null.

	public final String skipUntil(char[] skipToChars) throws Exception {

		String skipCharStr = new String(skipToChars);

		String curToken = getNextToken();
		int braceCount = 0;
		int parenthCount = 0;

		boolean curInList;

		while ( curToken != null ) {

			if ( curToken.length() == 1
					&& ( skipCharStr.indexOf(curToken) != -1 )
					&& braceCount == 0 && parenthCount == 0 ) {

				// i.e. token found within balanced context
				return curToken;

			} else if ("{".equals(curToken) ) {
				braceCount++;
			} else if ("(".equals(curToken) ) {
				parenthCount++;
			} else if ("}".equals(curToken) ) {
				braceCount--;
			} else if (")".equals(curToken) ) {
				parenthCount--;
			}

			if ( braceCount < 0 || parenthCount < 0 ) {
				break;
			}

			curToken = getNextToken();

		} // while

		return null;

	} // skipUntil(Char[]): String


	//=========================================================================
	static class Exception extends java.lang.Exception {

		static final String MESSAGE_UNTERM_COMMENT = "Unterminated multi-line comment.";
		static final String MESSAGE_STRING_EXPR    = "Unterminated String expression.";
		static final String MESSAGE_CHAR_EXPR      = "Unterminated char expression.";

		Exception(String message) {
			super(message);
		}
	} // static class Exception

} // class TokenSource
