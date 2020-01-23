/*
 * JBrowseLineParser.java - a JBrowseParser for Java Source Code via LineSource
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

import java.util.Stack; // will be required for inner-class support

//=============================================================================
/**
 * The class that implemnts a JBrowseParser for Java Source Code via a
 * JBrowseParser.LineSource.
 *
 * @author George Latkiewicz
 * @version 1.0.1 - Nov. 16, 1999
 */

public class JBrowseLineParser implements JBrowseParser
{

	/* public class attributes */
	public static final String VER_NUM = "1.0.1";

	/* public instance attributes */
	public JBrowseParser.Results results;

	/* private instance attributes */
	private JBrowseParser.LineSource ls;

	private	String fileName; // set on JBrowse.TreePane.init(),
	private	String expectedTopLevelName; // set on JBrowse.TreePane.init(),

	Stack nodeStack = new Stack();
	private UMLTree.Node root;

	private Options options;
	private Options.Filter  filterOpt;
	private Options.Display displayOpt;

	// Parse State
	ParseState parseState = null;
	ParseSubState parseSubState = null;

	// Counters
	int tokenCount = 0; // only count tokens returned by TokenSource
	int	curTokenLine  = -1;
	int	curTokenPos = -1;


	//-------------------------------------------------------------------------
	/**
	 * This method creates a new instance of JBrowse GUI and Parsing engine.
	 * @param ls JBrowseParser.LineSource that will provide the source to be parsed.
	 */
	public JBrowseLineParser(JBrowseParser.LineSource ls)
	{
		this.ls = ls;

		results = new JBrowseParser.Results();

	} // JBrowseLineParser(View): <init>

	/**
	 * Returns the String that represents the name associated with the current
	 * JBrowseParser.LineSource (e.g. the fileName associated with the current
	 * buffer), or null if the JBrowseParser.LineSource is not currently associated
	 * with a source.
	 */
	public String getSourceName()
	{
		if (ls != null) {
			return ls.getName();
		} else {
			return null;
		}
	}

	
/**
 * Returns <CODE>true</CODE> if the <CODE>JEditTextArea</CODE> to which this
 * <CODE>JBrowseLineParser</CODE> is tied uses a <CODE>JavaTokenMarker</CODE>.
 */
	public boolean usesJavaTokenMarker()
	{
		return (ls instanceof JEditLineSource) ? 
		 (((JEditLineSource)ls).getTextArea().getTokenMarker()
		 instanceof org.gjt.sp.jedit.syntax.JavaTokenMarker)
		 : false;
	}//end usesJavaTokenMarker
	

	public final void setOptions(Options options)
	{
		this.options = options;

		filterOpt  = options.getFilterOptions();
		displayOpt = options.getDisplayOptions();
	}


	public final void setRootNode(UMLTree.Node root)
	{
		this.root = root;
	}


	public boolean isReady()
	{
		boolean rVal = false;
		if ( options != null
				&& ls != null
				&& root != null ) {
			rVal = true;
		}
		return rVal;
	} // isReady(): boolean


	//-------------------------------------------------------------------------
	/**
	 * The method that preforms the actual parsing. This is the method which builds
	 * the tree model that reflects the heirarchical structure of the Java code in
	 * the current LineSource.
	 */
	public JBrowseParser.Results parse()
	{


		results.reset(); // reset result counters to 0
		ls.reset(); // reset the LineSource to the beginning of the buffer

		if (!isReady()) {
			return results;
		}

		// Set initial Parse State
		parseState = ParseState.HEADER;
		parseSubState = null;

		// Set initial counts
		tokenCount = 0; // only count tokens returned by TokenSource
		curTokenLine  = -1;
		curTokenPos = -1;

		UML.Element currentElement = null;
		UML.Message messageElement = null;
		UML.Type eType;

		nodeStack.push(root);
		UMLTree.Node parentNode = root;

		int methodBraceCount = 0;
		int lastTokenLine = -1;

		int packageMemberStartPos = -1;
		int packageMemberLine     = -1;

		// Parsing Attributes
		int curElementStartPos = -1;

		String msgStr = null;
		boolean badFlag = false;
		boolean resetFlag = false;

		String lastToken = null;
		String token = null;
		char tokenStartChar;

		boolean exceptionThrown = false;

		// Get fileName and TokenSource
		fileName = ls.getName();
//		TokenSource ts = new TokenSource(ls, JBrowse.debugLevel );
		TokenSource ts = new TokenSource(ls);

		if (!fileName.toUpperCase().endsWith(".JAVA") ) {
			expectedTopLevelName = fileName;
			fileName += " (NON-java file?)";
		} else {
			expectedTopLevelName = fileName.substring(0, fileName.indexOf('.'));
		}
		root.setName(fileName);

		try {

			root.setPosition( ls.createPosition(0) );

			if (root.getChildCount() > 0 ) {
				root.removeAllChildren();
			}

			// parsing attributes
			int mod = 0;
			String className = null;

			String memberType = null;     // method return type or attribute type
			String memberName = null;

			String parmType = "";
			String parmName = "";

			int methodParmsCount = 0;

			boolean extendsFound    = false; // set when extends keyword found
			boolean implementsFound = false; // set when implements keyword found
			boolean extendsOK       = true;  // set when extends class/interface name found (but no comma)
			boolean implementsOK    = true;  // set when implements interface name found (but no comma)

			boolean throwsFound     = false; // set when throws keyword found
			boolean throwsOK        = true;  // set when throws class/interface name found (but no comma)

			boolean isBodyRequired  = false;


			do {

				tokenCount++;
				lastTokenLine  = curTokenLine;

				token = ts.getNextToken();

				curTokenLine = ts.getCurrentLineNum();
				curTokenPos  = ts.getCurrentPos();

				if (token == null) break; // may be necessary if we end in a comment

				tokenStartChar = token.charAt(0);


				// -----------------------
				// HEADER - <any substate>
				// -----------------------
				if ( parseState == ParseState.HEADER) {

					if (parseSubState == null) {
						if (token.equals("package") ) {
							parseSubState = ParseSubState.PACKAGE;
							continue;
						} else if (token.equals("import") ) {
							parseSubState = ParseSubState.IMPORT;
							continue;
						} else if (Character.isJavaIdentifierStart(tokenStartChar) ) {
							parseState = ParseState.POST_HEADER;
							parseSubState = ParseSubState.SCANNING;
							// fall into ParseState.POST_HEADER processing
						} else {

							String lastMsgStr = "Expecting package/import or package member definition.";

							if (!lastMsgStr.equals(msgStr) || lastTokenLine != curTokenLine ) {

								// i.e. log only if not already logged for this line.
								msgStr = lastMsgStr;

								// Create error node and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr, UML.Type.ERROR,
										parentNode.getElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, parentNode);
							} // suppress repeats

							continue;
						}

					} else if (parseSubState == ParseSubState.PACKAGE) {
						if (token.equals(";") ) {
							parseSubState = ParseSubState.POST_PACKAGE;
						}
						continue;

					} else if (parseSubState == ParseSubState.POST_PACKAGE) {
						if (token.equals("import") ) {
							parseSubState = ParseSubState.IMPORT;
							continue;
						} else if (Character.isJavaIdentifierStart(tokenStartChar) ) {
							parseState = ParseState.POST_HEADER;
							parseSubState = ParseSubState.SCANNING;
							// fall into ParseState.POST_HEADER processing

						} else {

							String lastMsgStr = "Expecting import or package member definition.";

							if (!lastMsgStr.equals(msgStr) || lastTokenLine != curTokenLine ) {

								// i.e. log only if not already logged for this line.
								msgStr = lastMsgStr;

								// Create error node and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr, UML.Type.ERROR,
										parentNode.getElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, parentNode);

							} // suppress repeats
							continue;
						}


					} else if (parseSubState == ParseSubState.IMPORT) {
						if (token.equals(";") ) {
							parseSubState = ParseSubState.POST_PACKAGE;
						}
						continue;

					} // if - else if for HEADER - <any substate>

				} // if (ParseState == ParseState.HEADER)


				// ----------------------------
				// POST_HEADER - <any substate>
				// ----------------------------
				if (parseState == ParseState.POST_HEADER) {

					// these tokens should put me in a class/interface and lead me to its body

					if (curElementStartPos == -1) {
						curElementStartPos = ls.getStartOffset() + curTokenPos;
						className = null;
						mod = 0;
						badFlag = false;
					}

					if ( Character.isJavaIdentifierStart(tokenStartChar) ) {

						if (className == null) {

							if (token.equals("abstract") ) {
								mod = RWModifier.setAbstract(mod);
							} else if (token.equals("final") ) {
								mod = RWModifier.setFinal(mod);
							} else if (token.equals("interface") ) {
								mod = RWModifier.setInterface(mod);
//							} else if (token.equals("native") ) {		// n/a for class/interface
//								mod = RWModifier.setNative(mod);
//							} else if (token.equals("private") ) {		// n/a for class/interface
//								mod = RWModifier.setPrivate(mod);
//							} else if (token.equals("protected") ) {	// n/a for class/interface
//								mod = RWModifier.setProtected(mod);
							} else if (token.equals("public") ) {
								mod = RWModifier.setPublic(mod);
//							} else if (token.equals("static") ) {       // n/a for class/interface
//								mod = RWModifier.setStatic(mod);
							} else if (token.equals("strictfp") ) {
								mod = RWModifier.setStrict(mod);
							} else if (token.equals("synchronized") ) {	// n/a for class/interface
								mod = RWModifier.setSynchronized(mod);
//							} else if (token.equals("transient") ) {	// n/a for class/interface
//								mod = RWModifier.setTransient(mod);
//							} else if (token.equals("volatile") ) {		// n/a for class/interface
//								mod = RWModifier.setVolatile(mod);
							} else if (token.equals("class") ) {
								mod = RWModifier.setClass(mod);

							} else if ( RWModifier.isClassOrInterface(mod) ) {

								className = token;

							} else {
								// identifier found (not modifier)
								// where "class"/"interface" not yet specified
								badFlag = true;
							}

						} // if (className == null)

					} else if (token.equals("{") ) {

						// non-identifier token where name not yet found
						if ( RWModifier.isClassOrInterface(mod) ) {
							className = MISSING_LABEL;
						} else {
							badFlag = true;
						}

					} else {

						// non-identifier token where name not yet found
						badFlag = true;

					} // i.e. isJavaIdentifierStart()

					if (className != null) {
						packageMemberStartPos = curElementStartPos;
						packageMemberLine     = curTokenLine;

						// Determine node type and insert
						if ( RWModifier.isInterface(mod) ) {
							eType = UML.Type.INTERFACE;
							results.incInterfaceCount();
						} else if (parentNode != root && !(RWModifier.isStatic(mod))) {
							eType = UML.Type.INNER_CLASS;
							results.incClassCount();
						} else {
							eType = UML.Type.CLASS;
							results.incClassCount();
						}

						// Create and insert class/interface node
						if (parentNode == root) {
							currentElement = new UML.PackageMember(className, eType, mod,
									packageMemberLine);
							nodeStack.push( parentNode = insertAsNode(
									currentElement, packageMemberStartPos, parentNode) );

							// Check if TopLevel is OK
							if ( RWModifier.isPublic(mod) ) {
								
								if (! className.equals(expectedTopLevelName) ) {

									msgStr = "This top-level public " + eType + " must be defined in file " + className + ".java";

									// Create error node and increment count
									results.incErrorCount();

									messageElement = new UML.Message(msgStr, UML.Type.ERROR,
											parentNode.getElement(), curTokenLine);
									insertAsNode(messageElement, curElementStartPos, parentNode);

									if (results.getTopLevelPath() == null ) {
										// assume first public found is the intended anyway
										results.setTopLevelPath(parentNode.getPathFrom(root));
									}
								} else if (results.getTopLevelPath() == null ) {
									results.setTopLevelPath(parentNode.getPathFrom(root));

								} else {
									msgStr = "Can only have one top-level public";

									// Create error node and increment count
									results.incErrorCount();

									messageElement = new UML.Message(msgStr, UML.Type.ERROR,
											parentNode.getElement(), curTokenLine);
									insertAsNode(messageElement, curElementStartPos, parentNode);
								}
							} // Check if TopLevel is OK

						} else {
							currentElement = new UML.NestedMember(className, eType, mod,
									parentNode.getElement(), packageMemberLine);
							nodeStack.push( parentNode = insertAsNode(
									currentElement, packageMemberStartPos, parentNode) );
						}

						// Setup for next state, if required
						if ( token.equals(className) ) {
							parseState    = ParseState.PACKAGE_MEMBER;
							parseSubState = ParseSubState.POST_NAME;

							extendsFound = false;
							implementsFound = false;

							extendsOK = true;
							implementsOK = true;

						} else {
							msgStr = "Missing class/interface name.";

							// Create error node and increment count
							results.incErrorCount();

							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, parentNode);

							// entering package member body
							parseState    = ParseState.PACKAGE_MEMBER;
							parseSubState = ParseSubState.BODY;
							curElementStartPos = -1;
							mod = 0;
							memberType = null;
							memberName = null;
						}
						continue;
					} // if (className != null)

					if (badFlag) {

						String lastMsgStr = "Expecting package member (class or interface).";

						if (!lastMsgStr.equals(msgStr) || lastTokenLine != curTokenLine ) {

							// i.e. log only if not already logged for this line.
							msgStr = lastMsgStr;

							// Create error node and increment count
							results.incErrorCount();

							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, parentNode);

						} // suppress repeats

						badFlag = false;

					}
					
					if ("{".equals(token) ) {
						ts.skipUntil( "}" );
						curElementStartPos = -1;
					} // if - else if for POST_HEADER - SCANNING


				// ----------------------------
				// PACKAGE MEMBER - POST_NAME
				// ----------------------------
				} else if ( parseSubState == ParseSubState.POST_NAME ) {

					badFlag = false;

					if (token.equals("{") ) {

						if (!extendsOK || !implementsOK) {

							curElementStartPos = ls.getStartOffset() + curTokenPos;
							curTokenLine = ts.getCurrentLineNum();

							msgStr = "Bad tokens between member name and '{'.";

							// Create error node and increment count
							results.incErrorCount();

							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, parentNode);

							// attempt to continue by falling into body
						}

						// entering package member body
						parseSubState = ParseSubState.BODY;
						curElementStartPos = -1;
						mod = 0;
						memberType = null;
						memberName = null;

					} else if ( Character.isJavaIdentifierStart(tokenStartChar) ) {

						if (token.equals("extends")) {

							if (extendsFound || implementsFound) {
								badFlag = true;
							} else {
								extendsFound = true;
								extendsOK = false;
							}

						} else if (token.equals("implements")) {

							if ( RWModifier.isInterface(mod)
									|| implementsFound || !extendsOK) {

								badFlag = true;
							} else {
								implementsFound = true;
								implementsOK = false;
							}

						} else {
							// implements/extends what indentifier
							curElementStartPos = ls.getStartOffset() + curTokenPos;
							curTokenLine = ts.getCurrentLineNum();

							if (!extendsOK) {

								extendsOK = true;
								currentElement = new UML.Generalization(token, UML.Type.EXTENDS,
										parentNode.getElement(), curTokenLine);
								insertAsNode(currentElement, curElementStartPos, parentNode);

							} else if (!implementsOK) {

								implementsOK = true;
								currentElement = new UML.Generalization(token, UML.Type.IMPLEMENTS,
										parentNode.getElement(), curTokenLine);
								insertAsNode(currentElement, curElementStartPos, parentNode);

							} else {
								badFlag = true;
							}
						}


					} else if (token.equals(",") ) {

						// expect more implements interfaces
						if (implementsFound && implementsOK
								&& RWModifier.isClass(mod) ) {

							implementsOK = false;

						} else if (extendsFound && extendsOK
								&& RWModifier.isInterface(mod) ) {

							extendsOK = false;

						} else {
							// phrase before ',' not OK
							badFlag = true;
						}

					} else {
						// unexpected token
						badFlag = true;

					} // if-else if by token


					if (badFlag) {

						msgStr = "Error in 'extends'/'implements' phrase.";

						// Create error node and increment count
						results.incErrorCount();

						messageElement = new UML.Message(msgStr, UML.Type.ERROR,
								parentNode.getElement(), curTokenLine);
						insertAsNode(messageElement, curElementStartPos, parentNode);

						// attempt to continue by skipping to beginning of body
						boolean skipSuccess = ts.skipUntil( "{" );

						if (skipSuccess) {

							// Determine node type and insert
							if ( RWModifier.isInterface(mod) ) {
								eType = UML.Type.INTERFACE;
								results.incInterfaceCount();
							} else if (parentNode != root && !(RWModifier.isStatic(mod))) {
								eType = UML.Type.INNER_CLASS;
								results.incClassCount();
							} else {
								eType = UML.Type.CLASS;
								results.incClassCount();
							}

							if (parentNode == root) {
								currentElement = new UML.PackageMember(className, eType, mod,
										packageMemberLine);
							} else {
								currentElement = new UML.NestedMember(className, eType, mod,
										parentNode.getElement(), packageMemberLine);
							}

							// Create and insert class/interface node
							nodeStack.push( parentNode = insertAsNode(
									currentElement, packageMemberStartPos, parentNode) );

							// entering package member body
							parseSubState = ParseSubState.BODY;
							curElementStartPos = -1;
							mod = 0;
							memberType = null;
							memberName = null;

						} else {

							msgStr = "Expected '{' not found.";

							// Create error node and increment count
							results.incErrorCount();

							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, parentNode);

							// skip and continue scanning
							curElementStartPos = -1;
							parseState    = ParseState.POST_HEADER;
							parseSubState = ParseSubState.SCANNING;
						}

					} // if (badFlag)

					// end of code block for PACKAGE MEMBER - POST_NAME


				// ------------------------------
				// PACKAGE_MEMBER - BODY
				// ------------------------------
				} else if ( parseSubState == ParseSubState.BODY ) {

					if (curElementStartPos == -1) {
						// n.b. cannot re-set the others here, may be a list of attributes.
						curElementStartPos = ls.getStartOffset() + curTokenPos;
						badFlag = false;
						resetFlag = false;
					}

					// these tokens should put me in a method and lead me to its parms

					if ( Character.isJavaIdentifierStart(tokenStartChar) ) {

						if (memberType == null) {

							if (token.equals("abstract") ) {
								mod = RWModifier.setAbstract(mod);
							} else if (token.equals("final") ) {
								mod = RWModifier.setFinal(mod);

							} else if ( token.equals("class") ) {
								mod = RWModifier.setClass(mod);
								parseState = ParseState.POST_HEADER;
								parseSubState = parseSubState.SCANNING;
								className = null;

							} else if ( token.equals("interface") ) {
								mod = RWModifier.setInterface(mod);
								parseState = ParseState.POST_HEADER;
								parseSubState = parseSubState.SCANNING;
								className = null;

							} else if (token.equals("native") ) {
								mod = RWModifier.setNative(mod);
							} else if (token.equals("private") ) {
								mod = RWModifier.setPrivate(mod);
							} else if (token.equals("protected") ) {
								mod = RWModifier.setProtected(mod);
							} else if (token.equals("public") ) {
								mod = RWModifier.setPublic(mod);
							} else if (token.equals("static") ) {
								mod = RWModifier.setStatic(mod);
							} else if (token.equals("strictfp") ) {
								mod = RWModifier.setStrict(mod);
							} else if (token.equals("synchronized") ) {
								mod = RWModifier.setSynchronized(mod);
							} else if (token.equals("transient") ) { // attribute only
								mod = RWModifier.setTransient(mod);
							} else if (token.equals("volatile") ) {	 // attribute only
								mod = RWModifier.setVolatile(mod);

							} else {
								memberType = token;
							}

						} else if (memberName == null) {

							memberName = token;

						} else {

							msgStr = "Unexpected identifier after member name '" + memberName +"'";

							// Create error node and increment count
							results.incErrorCount();

							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, parentNode);

							// attempt to continue by skipping to ";"
							boolean skipSuccess = ts.skipUntil( ";" );

							if (!skipSuccess) {

								msgStr = "Expected ';' not found.";

								// Create error node and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr, UML.Type.ERROR,
										parentNode.getElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, parentNode);

							}
							// Skip and continue
							curElementStartPos = -1;
							memberType = null;
							memberName = null;

						} // if (memberType == null) else if...


					} else if ( token.equals(";")
							||  token.equals("=")
							||  token.equals(",") ) {

						if ( memberType == null ) {

							if (token.equals(";") && mod == 0 ) {
								// assume empty statement
								curElementStartPos = -1;
								continue;
							}

							msgStr = "Expecting type & identifier for member.";


							// Create error node and increment count
							results.incErrorCount();

							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, parentNode);

							// attempt to continue
							if (token.equals(";") ) {
								curElementStartPos = -1;
							}

						} else {
						
							// assume this is an attribute

							if ( memberName == null ) {

								msgStr = "Expecting type & identifier for attribute.";

								// Create error node and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr, UML.Type.ERROR,
										parentNode.getElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, parentNode);

								// attempt to continue as if name was specified
								memberName = MISSING_LABEL;

							}

							// Create and Insert Attribute Node
							currentElement = new UML.Attribute(memberName, memberType, mod,
									parentNode.getElement(), curTokenLine);
							insertAsNode(currentElement, curElementStartPos, parentNode);


							if ( ( (UML.Attribute) currentElement).isPrimitive()) {
								results.incPrimAttrCount();
							} else {
								results.incObjAttrCount();
							}

							if ( token.equals(",") ) {
								// more should follow
								memberName = null;
								continue;

							} else if ( token.equals("=") ) {
								// more may follow, so skip until ";" or ","
								if ( ",".equals( ts.skipUntil( ",;".toCharArray() )) ) {
									// more should follow, so maintain type but clear name
									memberName = null;
									continue;
								}
								// ";" assume found so fall into prepare for next element
							}

							// prepare for next element
							curElementStartPos = -1;
							mod = 0;
							memberType = null;
							memberName = null;
						
						} // end of else if for attributes

					} else if ( token.equals("[") || token.equals("]") ) {
						if (memberName != null) {
							// attribute
							memberName += token;
						} else if (memberType != null) {
							// attribute or method
							memberType += token;
						} else {
							// unexpected
							badFlag = true;
							resetFlag = true;
							msgStr = null;
						}

					} else if (token.equals("(") ) {

						if (memberType == null) {

							// unexpected token
							badFlag = true;
							resetFlag = true;
							msgStr = null;

						} else if (memberName == null) {

							// handle constructors
							if (memberType.equals(parentNode.getElement().getName()) ) {
								memberName = memberType;
								memberType = null; // the expected value for constructors

								// Check if constructor is appropriate
								if ( parentNode.getElement().isInterface() ) {
									badFlag = true;
									msgStr = "Interfaces can't have constructors";
								} else if ( !RWModifier.isValidForConstructor(mod) ) {
									badFlag = true;
									msgStr = "Constructor can't be native/abstract/static/synchronized/final";
								}

							} else {

								// Report missing name or misspelled constructor
								badFlag = true;
								if ( parentNode.getElement().isInterface()
										|| !RWModifier.isValidForConstructor(mod) ) {
									memberName = MISSING_LABEL;
									msgStr = "Missing operation type or name";
								} else {
									memberName = memberType;
									memberType = null; // the expected value for constructors
									msgStr = "Misspelled constructor name?";
								}
							}
						}

						// Operation Node
						if ( !resetFlag ) {

							// Create Operation Node
							currentElement = new UML.Operation(memberName, memberType, mod,
									parentNode.getElement(), curTokenLine);
							if (memberType == null ) {
								( ( UML.Operation) currentElement).setConstructor(true);
							}
							isBodyRequired = ( (UML.Operation) currentElement).isBodyRequired();


							// Insert Operation Node
							nodeStack.push( parentNode = insertAsNode(
									currentElement, curElementStartPos, parentNode) );
							results.incMethodCount();

							if ( parentNode.getElement().getParentElement().isInterface() 
									&& !RWModifier.isValidInterfaceMethod(mod) ) {

								// Interface methods can't be native/static/synchronized/final/private/protected
								String msgStr2 = "Invalid modifiers for a method in an interface";

								// Create error node and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr2, UML.Type.ERROR,
										parentNode.getElement().getParentElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, 
										(UMLTree.Node) parentNode.getParent());

							} else if (RWModifier.isAbstract(mod) 
									&& !RWModifier.isValidAbstractMethod(mod)) {

								// Abstract methods can't be native/static/synchronized/final/private
								String msgStr2 = "Invalid modifiers for an abstract method";

								// Create error node and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr2, UML.Type.ERROR,
										parentNode.getElement().getParentElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, 
										(UMLTree.Node) parentNode.getParent());
							}

							// Setup for next state
							parseSubState = ParseSubState.OP_PARMS;
							lastToken = ""; // if there are any parameters signal that next token is start of first
							parmType = "";
							parmName = "";

							methodBraceCount = 0;
							methodParmsCount = 0;
						} // if ( !resetFlag )

				    } else if ( token.equals("{")
							&& ( mod == RWModifier.setStatic(0)
								|| mod == 0 ) ) {

				   		// static block or object block


						ts.skipUntil( "}" );
						curElementStartPos = -1;
						mod = 0;
						memberType = null;
						memberName = null;

					} else if (token.equals("}") ) {
						// pop parent from stack
						//nodeStack.pop();
						UMLTree.Node o = (UMLTree.Node) nodeStack.pop();
						parentNode = (UMLTree.Node) nodeStack.peek();
						if (parentNode == root ) {
							parseState = ParseState.POST_HEADER;
							parseSubState = ParseSubState.SCANNING;
						} else {
							parseState = ParseState.PACKAGE_MEMBER;
							parseSubState = ParseSubState.BODY;
							mod = 0;
							memberType = null;
							memberName = null;
						}
						curElementStartPos = -1;
					} else {

						// Unexpected token
						badFlag = true;
						resetFlag = true;

					} // if - else if for PACKAGE_MEMBER - BODY


					if (badFlag) {

						if (msgStr == null) {
							// Unexpected token
							msgStr = "Unexpected token: '" + token + "'";
						}

						// increment count
						results.incErrorCount();

						// attempt to continue by ignoring
						if (resetFlag) {

							// Create error node as daughter of parent
							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, parentNode);

							curElementStartPos = -1;
							memberType = null;
							memberName = null;
						} else {
							// Create error node as sister of parent
							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement().getParentElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, 
									(UMLTree.Node) parentNode.getParent());

							badFlag = false;
							resetFlag = false;
						}
					} // if (badFlag) for PACKAGE_MEMBER - BODY


				// ------------------------------
				// PACKAGE_MEMBER - OP_PARMS
				// ------------------------------
				} else if (parseSubState == ParseSubState.OP_PARMS) {

					if ( token.equals(")") || token.equals(",") ) {

						if (! "".equals(parmType) ) {

							// Attempt to add the parameter

							// insure parameter identifier not missing
							if ( "".equals(parmName) ) {

								msgStr = "Expecting type & identifier for method parameter.";

								// Create error node (as sister of parent) and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr, UML.Type.ERROR,
										parentNode.getElement().getParentElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, 
										(UMLTree.Node) parentNode.getParent());

								// attempt to continue as if an identifier for the parameter was found.
								parmName = MISSING_LABEL;
							}

							// insure closing ']' not missing
							if ( "[".equals(lastToken) ) {

								msgStr = "Missing ']' after '[' for method parameter.";

								// create error node (as sister of parent) and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr, UML.Type.ERROR,
										parentNode.getElement().getParentElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, 
										(UMLTree.Node) parentNode.getParent());

								// attempt to continue as if "]" was found.
								parmType += "[]";
							}

							// Add the parameter
							( (UML.Operation) currentElement).addArgument(parmType, parmName);
							methodParmsCount++;

						} else {

							// i.e. parmType not specified

							// insure ',' was not superfluous
							if ( token.equals(",") ) {
								msgStr = "Missing method parameter before ','.";

								// Create error node and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr, UML.Type.ERROR,
										parentNode.getElement().getParentElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, 
										(UMLTree.Node) parentNode.getParent());

								// attempt to continue as if superfluous "," was not there.
							}

						} // if (! "".equals(parmType) )


						// Handle for end or more parameters ( ')' vs. ',')
						if (token.equals(",") ) {

							// Prepare for next parameter
							parmType = "";  // if there are any more args signal that next token is type
							parmName = "";

						} else { // i.e. token is ")"

							// insure was not preceded by a superfluous ','
							if ( lastToken.equals(",") ) {

								msgStr = "Missing method parameter after ','.";

								// Create error node and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr, UML.Type.ERROR,
										parentNode.getElement().getParentElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, 
										(UMLTree.Node) parentNode.getParent());

								// attempt to continue as if superfluous "," was not there.
							}

							parseSubState = ParseSubState.OP_POST_PARMS;
							throwsFound = false;
							throwsOK    = true;

						} // ( ')' vs. ',')


					} else {

						// Assume Parameter type or formal name
						if ( Character.isJavaIdentifierStart(tokenStartChar) ) {

							if ( "".equals(parmType) ) {
								// type
								parmType = token;

							} else if ( "[".equals(lastToken) ) {

								msgStr = "Missing ']' after '[' for method parameter.";

								// create error node and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr, UML.Type.ERROR,
										parentNode.getElement().getParentElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, 
										(UMLTree.Node) parentNode.getParent());

								// attempt to continue as if "]" was found.
								parmType += "[]";
								parmName = token;

							} else {
								// formal name
								parmName = token;
							}


						} else if ("[".equals(token) ) {
							// then check that next token is "]"

						} else if ("]".equals(token) ) {
							if ( "[".equals(lastToken) ) {
								parmType += "[]";
							} else {

								msgStr = "Missing '[' before ']' for method parameter.";

								// Create error node and increment count
								results.incErrorCount();

								messageElement = new UML.Message(msgStr, UML.Type.ERROR,
										parentNode.getElement().getParentElement(), curTokenLine);
								insertAsNode(messageElement, curElementStartPos, 
										(UMLTree.Node) parentNode.getParent());

								// attempt to continue as if "]" was found.
								parmType += "[]";
							}

						} else {
							msgStr = "Unexpected token \"" + token + "\" in method parameter.";

							// Create error node and increment count
							results.incErrorCount();

							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement().getParentElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, 
									(UMLTree.Node) parentNode.getParent());

							// attempt to continue (i.e. ignore)

						}

					} // if - else if for PACKAGE_MEMBER - OP_PARMS

					lastToken = token;


				// ------------------------------
				// PACKAGE_MEMBER - OP_POST_PARMS
				// ------------------------------
				} else if (parseSubState == ParseSubState.OP_POST_PARMS) {

					// Parameter list finished, parse throws clause
					// and Determine Next State

					badFlag = false;

					if ( token.equals("{") || token.equals(";") ) {

						if (!throwsOK) {

							curElementStartPos = ls.getStartOffset() + curTokenPos;
							curTokenLine = ts.getCurrentLineNum();

							msgStr = "Missing type specification in throws clause";

							// Create error node and increment count
							results.incErrorCount();

							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement().getParentElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, 
									(UMLTree.Node) parentNode.getParent());

							// attempt to continue by falling into body
						}


						if ( isBodyRequired && token.equals("{") ) {

							parseSubState = ParseSubState.OP_BODY;
							methodBraceCount++;

						} else if ( !isBodyRequired && token.equals(";") ) {

							// handle method with no body
							UMLTree.Node o = (UMLTree.Node) nodeStack.pop();
							parentNode = (UMLTree.Node) nodeStack.peek();
							if (parentNode == root ) {
								parseState = ParseState.POST_HEADER;
								parseSubState = ParseSubState.SCANNING;
							} else {
								parseState = ParseState.PACKAGE_MEMBER;
								parseSubState = ParseSubState.BODY;
								mod = 0;
								memberType = null;
								memberName = null;
							}
							curElementStartPos = -1;

						} else {

							// error
							if (isBodyRequired) {
								msgStr = "Expecting body for the method.";
								parseSubState = ParseSubState.BODY;
								curElementStartPos = -1;
								mod = 0;
								memberType = null;
								memberName = null;

							} else {
								msgStr = "Expecting ';' after abstract or native method.";
								parseSubState = ParseSubState.OP_BODY;
								methodBraceCount++;
							}


							// pop off stack
							nodeStack.pop();
							parentNode = (UMLTree.Node) nodeStack.peek();

							// Create error node and increment count
							results.incErrorCount();

							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, parentNode);

							continue;
						} // if-else for "}" or ";"


					} else if (token.equals(",") ) {

						// expect more throws classes/interfaces
						if (throwsFound && throwsOK ) {

							throwsOK = false;

						} else {
							// phrase before ',' not OK
							badFlag = true;
						}

					} else if ( Character.isJavaIdentifierStart(tokenStartChar) ) {

						if (token.equals("throws")) {
							if (throwsFound) {
								badFlag = true;
							} else {
								throwsFound = true;
								throwsOK = false;
							}
							
						} else {
							// implements/extends what indentifier
							if (!throwsOK) {

								curElementStartPos = ls.getStartOffset() + curTokenPos;
								curTokenLine = ts.getCurrentLineNum();

								throwsOK = true;

								currentElement = new UML.Throws(token,
										parentNode.getElement(), curTokenLine);
								insertAsNode(currentElement, curElementStartPos, parentNode);

							} else {
								badFlag = true;
							}
						}

					} else {
						// unexpected token
						badFlag = true;

					} // if-else if by token for PACKAGE_MEMBER - OP_POST_PARMS


					if (badFlag) {

						msgStr = "Error in 'throws' phrase.";

						// Create error node and increment count
						results.incErrorCount();

						messageElement = new UML.Message(msgStr, UML.Type.ERROR,
								parentNode.getElement().getParentElement(), curTokenLine);
						insertAsNode(messageElement, curElementStartPos, 
								(UMLTree.Node) parentNode.getParent());

						// attempt to continue by skipping to beginning of body
						boolean skipSuccess = false;
						boolean popRequired = false;

						if ( isBodyRequired ) {

							skipSuccess = ts.skipUntil( "{" );

							// entering operation body
							parseSubState = ParseSubState.OP_BODY;
							methodBraceCount++;

						} else {
							popRequired = true;
							skipSuccess = ts.skipUntil( ";" );
						}

						if (!skipSuccess )  {

							if ( isBodyRequired ) {
								msgStr = "Expected '{' not found.";
							} else {
								msgStr = "Expected ';' not found.";
							}

							// Create error node and increment count
							results.incErrorCount();

							messageElement = new UML.Message(msgStr, UML.Type.ERROR,
									parentNode.getElement().getParentElement(), curTokenLine);
							insertAsNode(messageElement, curElementStartPos, 
									(UMLTree.Node) parentNode.getParent());

							popRequired = true;
						}

						if ( popRequired ) {							
							nodeStack.pop();
							parentNode = (UMLTree.Node) nodeStack.peek();

							// returning to package member body
							parseSubState = ParseSubState.BODY;
							curElementStartPos = -1;
							mod = 0;
							memberType = null;
							memberName = null;
						}

					} // if (badFlag)

					// end of code block for PACKAGE MEMBER - OP_POST_PARMS


				// ------------------------------
				// PACKAGE_MEMBER - OP_BODY
				// ------------------------------
				} else if (parseSubState == ParseSubState.OP_BODY) {

					if (token.equals("{") ) {
						methodBraceCount++;

					} else if (token.equals("}") ) {
						methodBraceCount--;

						if (methodBraceCount == 0) {

							nodeStack.pop();
							parentNode = (UMLTree.Node) nodeStack.peek();

							// return to body of this method's container
							parseSubState = ParseSubState.BODY;
							curElementStartPos = -1;
							mod = 0;
							memberType = null;
							memberName = null;
						}

					} // if - else if for PACKAGE_MEMBER - OP_BODY

				} else {

					msgStr = "Unexpected State/Sub-State.";

					// Create error node and increment count
					results.incErrorCount();

					messageElement = new UML.Message(msgStr, UML.Type.ERROR,
							parentNode.getElement().getParentElement(), curTokenLine);
					insertAsNode(messageElement, curElementStartPos, 
							(UMLTree.Node) parentNode.getParent());


				} // if...else if by parseState & parseSubState

				// log every token at end of loop!
//%				JBrowse.log(12, this, "(" + tokenCount + ") " + parseState + " "
//%						+ parseSubState + " " + curTokenLine + "-" + curTokenPos
//%						+ ":" + token);


			} while (token != null);



		} catch (TokenSource.Exception e) {

			exceptionThrown = true;

			curTokenLine = ts.getCurrentLineNum();
			curTokenPos  = ts.getCurrentPos();
			curElementStartPos = ls.getStartOffset() + curTokenPos;

			// Unterminated multi-line comment, String or char expression
			msgStr = e.getMessage();

			// Create error node and increment count
			results.incErrorCount();

			try {
				parentNode = (UMLTree.Node) nodeStack.peek();
				currentElement = parentNode.getElement();
			} catch (Exception ex) {
				parentNode = null;
				currentElement = null;
			}
			messageElement = new UML.Message(msgStr, UML.Type.ERROR,
					currentElement, curTokenLine);
			insertAsNode(messageElement, curElementStartPos, parentNode);


		} catch (java.util.EmptyStackException e) {

			exceptionThrown = true;

//%			msgStr = "Caught java.util.EmptyStackException: " + e.getMessage();

		} // end try-catch


		// Report Final Errors

		curTokenLine = ts.getCurrentLineNum();
		curTokenPos  = ts.getCurrentPos();
		curElementStartPos = ls.getStartOffset() + curTokenPos;


		if ( !exceptionThrown &&
				( methodBraceCount != 0 || parseState == ParseState.PACKAGE_MEMBER) ) {

			msgStr = "Unbalanced braces";

			// Create error node and increment count
			results.incErrorCount();

			try {
				parentNode = (UMLTree.Node) nodeStack.peek();
				currentElement = parentNode.getElement();
			} catch (Exception e) {
				parentNode = null;
				currentElement = null;
			}

			messageElement = new UML.Message(msgStr, UML.Type.ERROR,
					currentElement, curTokenLine);
			insertAsNode(messageElement, curElementStartPos, parentNode);
		}


		if (results.getClassCount() == 0
				&& results.getInterfaceCount() == 0) {

			msgStr = "No package members found!";

			// Show only this error
			root.removeAllChildren();

			// Create error node and increment count
			results.setErrorCount(1);

			messageElement = new UML.Message(msgStr, UML.Type.ERROR,
					null, curTokenLine);
			insertAsNode(messageElement, curElementStartPos, root);

		} // if no package members found

		if (displayOpt.getAlphaSort())
		{
			root.alphaSort();
		}//end if alpha sort methods
		
		return results;

	} // parse(): JBrowseParser.Results


	//-------------------------------------------------------------------------
	private final UMLTree.Node insertAsNode(UML.Element e, int pos,
			UMLTree.Node parentNode)
	{
//%		JBrowse.log(12, this, "Inserting node: " + e.toString(displayOpt)
//%				+ "\n\tParent Node: " + parentNode);

		// Insert Node
		UMLTree.Node node = new UMLTree.Node(e);
		node.setPosition( ls.createPosition(pos) );

		// line below is faster than: treeModel.insertNodeInto(node, parentNode, index);
		parentNode.insert(node, parentNode.getChildCount() );

//%		JBrowse.log(10, this, "Inserted node: " + e.toString(displayOpt) );

		return node;

	} // insertAsNode(UML.Element, int, UMLTree.Node, int): UMLTree.Node

} // class JBrowseLineParser implements JBrowseParser


//=============================================================================
class ParseState
{
	public static final ParseState
		HEADER         = new ParseState("*HEADER********"),
		POST_HEADER    = new ParseState("*POST_HEADER***"),
		PACKAGE_MEMBER = new ParseState("*PACKAGE_MEMBER");

	// use POSSIBLE_VALUES to build an iterator
	public static final ParseState[] POSSIBLE_VALUES = {
		HEADER, POST_HEADER, PACKAGE_MEMBER };

	protected String label = null;

	//-------------------------------------------------------------------------
	private ParseState(String label)
	{
		this.label = label;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof ParseState)) return false;
		return (this == o);
	}

	public String toString() { return label.toString(); }

} // class ParseState


//=============================================================================
class ParseSubState
{
	public static final ParseSubState
	// HEADER
		PACKAGE       = new ParseSubState("-PACKAGE-------", ParseState.HEADER),
		POST_PACKAGE  = new ParseSubState("-POST_PACKAGE--", ParseState.HEADER),
		IMPORT        = new ParseSubState("-IMPORT--------", ParseState.HEADER),
	// POST_HEADER
		SCANNING      = new ParseSubState("-SCANNING------", ParseState.POST_HEADER),
	// PACKAGE_MEMBER
		POST_NAME     = new ParseSubState("-POST_NAME-----", ParseState.PACKAGE_MEMBER),
		BODY          = new ParseSubState("-BODY----------", ParseState.PACKAGE_MEMBER),
		OP_PARMS      = new ParseSubState("-OP_PARMS------", ParseState.PACKAGE_MEMBER),
		OP_POST_PARMS = new ParseSubState("-OP_POST_PARMS-", ParseState.PACKAGE_MEMBER),
		OP_BODY       = new ParseSubState("-OP_BODY-------", ParseState.PACKAGE_MEMBER);

	// use POSSIBLE_VALUES to build an iterator
	public static final ParseSubState[] POSSIBLE_VALUES = {
			PACKAGE, POST_PACKAGE, IMPORT, SCANNING, POST_NAME, BODY, 
			OP_PARMS, OP_POST_PARMS, OP_BODY };

	protected String label = null;
	protected ParseState parentState = null;

	//-------------------------------------------------------------------------
	private ParseSubState(String label, ParseState parentState)
	{
		this.label = label;
		this.parentState = parentState;
	}

	public ParseState getParentState() { return parentState; }

	public boolean equals(Object o)
	{
		if (!(o instanceof ParseSubState)) return false;
		return (this == o);
	}

	public String toString() { return label.toString(); }

} // class ParseSubState
