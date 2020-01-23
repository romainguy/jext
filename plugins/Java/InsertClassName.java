/*
 * InsertClassName.java
 * Copyright (C) 2000 Matt Benson
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

import java.util.Vector;
import java.util.Enumeration;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import javax.swing.text.BadLocationException;
import org.jext.MenuAction;
import org.jext.JextFrame;
import org.jext.JextTextArea;
import org.gjt.sp.jedit.textarea.TextUtilities;


/**
 * InsertClassName inserts the name of the current class into a java source file.
 */
public class InsertClassName extends MenuAction
{

	UMLTree.Node root;
	JextFrame view;
	JEditLineSource ls;
	JBrowseLineParser parser;
	String fileName;
	JextTextArea textArea;

/**
 * default constructor
 */
  public InsertClassName()
  {
    super("insert_class_name");
  }//end constructor


/**
 * Performs the InsertClassName action.
 * @param evt   the <CODE>ActionEvent</CODE> that kicks off this shindig.
 */
  public void actionPerformed(ActionEvent evt)
  {
    textArea = getTextArea(evt);
		Clipboard clipboard = textArea.getToolkit().getSystemClipboard();
		String selection = new String();
		try
		{
			selection = ((String)clipboard
			 .getContents(this).getTransferData(
			 DataFlavor.stringFlavor))
			 .replace('\r','\n');
		}//end try
		catch(Exception e)
		{
		}//end catch

//I make no pretense--the next several lines were ripped directly from various
//portions of the JBrowse plugin so if you want comments look there!
		view = getJextParent(evt);
		ls = new JEditLineSource(view);
		parser = new JBrowseLineParser(ls);
		fileName = parser.getSourceName();

		if (parser.usesJavaTokenMarker())
		{
			root = new UMLTree.Node(fileName);
		}//end if parser uses JavaTokenMarker
		else
		{
			return;
		}//end else

		JBrowsePlugin.OpenAction openAction = new JBrowsePlugin.OpenAction();
		openAction.props = new JBrowsePlugin.PropAccessor();

		JBrowse.OptionDialog optionDialog = new JBrowse.OptionDialog(view,
		 new JBrowse(openAction), "I am a dummy!");
		Options options = optionDialog.getOptions();
		Options.Filter filterOpt  = options.getFilterOptions();
		Options.Display displayOpt = options.getDisplayOptions();
		parser.setOptions(options);
		parser.setRootNode(root);
		JBrowseParser.Results results = parser.parse();
		if ((results.getClassCount() + results.getInterfaceCount() == 0))
		{
			return;
		}//end if no classes or interfaces were found.

		String className = getClassName();
		if (className == null)
		{
			return;
		}//end if
		StringSelection stringSel = new StringSelection(className);
		clipboard.setContents(stringSel, stringSel);

    textArea.paste();

		stringSel = new StringSelection(selection);
		clipboard.setContents(stringSel, stringSel);

    textArea.grabFocus();
  }//end actionPerformed


/**
 * Returns the class name!
 * @return the class name.
 */
	public String getClassName()
	{
		BackStab[] classes = getClassArray();
		Arrays.sort(classes);//sort by line number
		int line = textArea.getCaretLine();
		int caretPos = textArea.getCaretPosition();
		String text = textArea.getText();
		String retName = null;
		int i;
//loop through found classes backward...
		for (i = classes.length - 1; i >= 0 && retName == null; i--)
		{
			int classLine = classes[i].getLine();
			String className = classes[i].getName();
//finding opening brace of class def so can find closing brace
			boolean foundOpenBrace = false;
			int startPos = 0;
			while (!foundOpenBrace)
			{
				startPos = text.indexOf(className,
				                        textArea.getLineStartOffset(classLine));
				startPos = TextUtilities.findWordEnd(text, startPos, className);
				int endComment = text.indexOf("*/", startPos);
				int beginComment = text.indexOf("/*", startPos);
				if (endComment >= beginComment)
				{
					startPos = text.indexOf('{', startPos);
					foundOpenBrace = true;
				}//end if endComment >= beginComment
			}//end while not found opening brace

			int endPos = startPos;
			try
			{
				endPos =
				 TextUtilities.findMatchingBracket(textArea.getDocument(), startPos);
			}//end try to assign endPos to ending brace
			catch(BadLocationException e)
			{
			}//end catch
// try to expand area if possible...

			if (startPos == text.indexOf('{'))
			{
				startPos = 0;
			}//end if opening brace is first in the document

			if (text.indexOf('}', endPos + 1) == -1)
			{
				endPos = text.length() - 1;
			}//end if closing brace is the last in the document

			if (endPos++ < text.length())
			{
				int endEndLine =
				 textArea.getLineEndOffset(textArea.getLineOfOffset(endPos));

				if (endEndLine > text.indexOf("//", endPos))
				{
					endPos = endEndLine;
				}//end if endEndLine > next line comment
			}//end if endPos < text.length()

			if (startPos > 0)
			{
				int lastJavadoc = text.lastIndexOf("/**", startPos);

				if (lastJavadoc > text.lastIndexOf('}', startPos) &&
				 lastJavadoc > text.lastIndexOf('{', startPos))
				{
					startPos = lastJavadoc;
				}//end if lastJavadoc > last closing brace
			}//end if startPos > 0

			if (startPos < caretPos && caretPos < endPos)
			{
				retName = className;
			}//end if startPos <= caretPos <= endPos
		}//end for i...
		return retName;
	}//end getClassName()


	private BackStab[] getClassArray()
	{
		Vector workVector = new Vector();
		Enumeration treeEnum = root.breadthFirstEnumeration();
		while (treeEnum.hasMoreElements())
		{
			Object ob = ((UMLTree.Node)treeEnum.nextElement()).getUserObject();
			if (ob instanceof UML.Element)
			{
				BackStab bs = BackStab.fromElement((UML.Element)ob);

				if (bs.isInterface() || bs.isClass())
				{
					workVector.add(bs);
				}//end if bs is a class or an interface
			}//end if
		}//end while treeEnum has more elements
		return (BackStab[])workVector.toArray(new BackStab[1]);
	}//end getClassArray()

}//end class InsertClassName
