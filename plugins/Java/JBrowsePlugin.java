/*
 * JBrowsePlugin.java - Java Browser Plugin, v1.0.1
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

import org.jext.*;
import org.jext.gui.*;
import org.jext.options.*;

import java.io.File;

import java.util.Properties;
import java.util.Vector;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Position;
import javax.swing.text.Element;
import javax.swing.text.BadLocationException;

//=============================================================================
/**
 * This class implements the JEdit's Plugin interface for the JBrowse plugin.
 */
public class JBrowsePlugin implements Plugin
{
	/* public class attributes */
	public static final String VER_NUM = "1.0.1";

	// Plugin interface

	//-------------------------------------------------------------------------
	public void start()
	{
	    Jext.addAction(new OpenAction());
	} // start(): void


	//-------------------------------------------------------------------------
	/**
	 * Newly created views call this method of each plugin in turn.
	 * The default implementation does nothing, but most plugins will
	 * want to override it to add instances of <code>javax.swing.JMenu</code>
	 * and <code>javax.swing.JMenuItem</code> to the appropriate vectors.
	 * The menus and menu items can be created dynamically by the plugin,
	 * but the preferred way is to use the methods of the
	 * <code>GUIUtilities</code> class.
	 */
	public void createMenuItems(JextFrame view, Vector menus, Vector menuItems)
	{
	}


	//-------------------------------------------------------------------------
	/**
	 * This allows plugins to add their own option pane to the
	 * <code>OptionsDialog</code>.
	 */
	public void createOptionPanes(OptionsDialog parent)
	{
         // GLK ! 800*600 screens won't like that !!
		 //parent.addOptionPane(new OptPane());
	}


	public void stop() {}
	public String getName() { return "JBrowse"; }


	//=========================================================================
	public static class OpenAction
			extends MenuAction
			implements JBrowse.Activator
	{
        JBrowse b;
		JextFrame view;
		JBrowseParser parser;
		UMLTree umlTree;
		PropertyAccessor props;

		Cursor savedCursor;

		//---------------------------------------------------------------------
		public OpenAction()
		{
			// identify prefix to .label property for menu
			super("jbrowse.open");
		}

		//---------------------------------------------------------------------
		public void actionPerformed(ActionEvent evt)
		{
			try {

				// Loading properties
				props = new PropAccessor();

				// Obtain the view for this jEdit session
				view = getJextParent(evt);
                if (b != null && view.getVerticalTabbedPane().indexOfComponent(b) != -1)
                {
                  view.getVerticalTabbedPane().remove(b);
                  return;
                }
				// Set Wait Cursor
				savedCursor = view.getCursor();
				view.setCursor(new Cursor(Cursor.WAIT_CURSOR));

				// Build a JEditLineSource as the JBrowseParser.LineSource for this instance
				JEditLineSource ls = new JEditLineSource(view);

				// Build a parser for this JBrowseParser.LineSource
				parser = new JBrowseLineParser(ls);

				// Create a UMLTree for this Plug-in instance
				umlTree = new UMLTree();

				// Build and Add a TreeEventAdapter for this umlTree
				TreeEventAdapter tea = new TreeEventAdapter(umlTree, ls, view);
				umlTree.addTreeSelectionListener(tea);
				umlTree.addMouseListener(tea);

				// Build and Display a JBrowse GUI with this OpenAction as its Activator
                b = new JBrowse(this);
                view.addJextListener(b);
                view.getVerticalTabbedPane().add("JBrowse", b);
                view.getVerticalTabbedPane().setSelectedComponent(b);

			} catch (Exception e) {
			//	System.out.println(e.getMessage());
            //    System.out.println(e.toString());

			} finally {
				if (savedCursor != null) {
					view.setCursor(savedCursor);
				} else {
					view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}

		} // actionPerformed(ActionEvent): void

		// JBrowse.Activator interface
		public Frame getOwner() { return view; }
		public PropertyAccessor getPropertyAccessor() { return props; }
		public JBrowseParser getJBrowseParser() { return parser; }
		public UMLTree getUMLTree() { return umlTree; }

	} // static class JBrowsePlugin.OpenAction


	//=========================================================================
	public static class OptPane
			extends JBrowseOptionPane
			//implements OptionPane//it's now implemented in JBrowseOptionPane
	{
		public Component getComponent()
        {
            JScrollPane scroller = new JScrollPane(this);
            Dimension _dim = this.getPreferredSize();
            scroller.setPreferredSize(new Dimension((int) _dim.width, 250));
            return scroller;
        }
	}


	//=========================================================================
	public static class PropAccessor
		implements PropertyAccessor
	{
		public String getProperty(String name)
		{
			return Jext.getProperty(name);
		}

		public String getProperty(String name, String def)	{
			return Jext.getProperty(name, def);
		}


		public String getProperty(String name, Object[] args)	{
			return Jext.getProperty(name, args);
		}

		public Object /*void???*/ setProperty(String name, String value)	{
			Jext.setProperty(name, value);
			return null;
		}

	} // public static class JBrowsePlugin.PropAccessor


	//=========================================================================
	public static class TreeEventAdapter
			extends MouseAdapter implements TreeSelectionListener
	{
		UMLTree umlTree;
		JEditLineSource ls;
		JextFrame view;

		//---------------------------------------------------------------------
		TreeEventAdapter(UMLTree umlTree, JEditLineSource ls, JextFrame view)
		{
			this.umlTree = umlTree;
			this.ls = ls;
			this.view = view;

		} // TreeEventAdapter(UMLTree, JEditLineSource): <init>


		//---------------------------------------------------------------------
		public void mouseClicked(MouseEvent e)
		{
			int selRow = umlTree.getRowForLocation(e.getX(), e.getY());

			// Only required to supplement valueChanged(TreeSelectionEvent evt)
			// when mouse clicked on same node.
			if (selRow == umlTree.getMaxSelectionRow()
					&& e.getClickCount() == 1 ) {

				// Check if view buffer matches tree buffer
				if ( !verifyBuffer() ) {
					return;
				}

				UMLTree.Node selectedNode = (UMLTree.Node)
						umlTree.getLastSelectedPathComponent();

				setPosition(selectedNode);
			}

		} // mouseClicked(MouseEvent): void


		//---------------------------------------------------------------------
		public void valueChanged(TreeSelectionEvent evt)
		{
			UMLTree.Node selectedNode;

			if (umlTree.isSelectionEmpty() ) {
				return;
			}

			// Check if view buffer matches tree buffer
			if ( !verifyBuffer() ) {
				return;
			}

			selectedNode = (UMLTree.Node) umlTree.getSelectionPath().getLastPathComponent();

			setPosition(selectedNode);

		} // valueChanged(TreeSelectionEvent): void


		//---------------------------------------------------------------------
		private void setPosition(UMLTree.Node node)
		{
			Position pos = (Position) node.getPosition();
			if (pos == null) {
				return;
			}

			Element map = ls.getTextArea().getDocument().getDefaultRootElement();
			Element lineElement = map.getElement(map.getElementIndex(pos.getOffset()));
			if (lineElement == null) {
				return;
			}

			view.getTextArea().select(lineElement.getStartOffset(), lineElement.getEndOffset() - 1);
		}

		//---------------------------------------------------------------------
		private boolean verifyBuffer()
		{
			// Check if view buffer matches tree buffer
			if ( view.getTextArea().getDocument() != ls.getTextArea().getDocument() ) {
				//view.getToolkit().beep();
                File lsFile = ls.getTextArea().getFile();
                File viewFile = view.getTextArea().getFile();
                String bufferWant, bufferHave;
                if (lsFile != null)
				  bufferWant = lsFile.getPath();
                else
                  bufferWant = ls.getTextArea().getName();
                if (viewFile != null)
				  bufferHave = viewFile.getPath();
                else
                  bufferHave = view.getTextArea().getName();
				GUIUtilities.error(view, "jbrowse.msg.wrongBuffer", new Object[] { bufferWant, bufferHave });
				return false;
			} else {
				return true;
			}
		}

	} // static class TreeEventAdapter implements TreeSelectionListener

} // public class JBrowsePlugin


//=============================================================================
/**
 * Implements the functionality specified by the LineSource interface using
 * the set of lines made availble by a jEdit view and adds a method to return
 * the StartOffset.
 */
class JEditLineSource
		implements JBrowseParser.LineSource
{
	private JextFrame view; // jEdit specific
	private JextTextArea buffer; // jEdit specific
	private Element map, lineElement; // jEdit specific
	private String name;
	private int start;
	private int lastLine; // last line that was read


	//-------------------------------------------------------------------------
	JEditLineSource(JextFrame view)
	{
		this.view = view;
		reset();

	} // JEditLineSource(View): <init>


	//-------------------------------------------------------------------------
	/**
	 * Setup to become a newly initialized LineSource for the current buffer.
	 */
	public void reset()
	{
		buffer = view.getTextArea();
		map = buffer.getDocument().getDefaultRootElement();
		File temp = buffer.getFile();
		if (temp == null)
		{
			name = new String();
		}//end if File == null
		else
		{
			name = buffer.getFile().getName();
		}//end else
		start = 0;
		lastLine = -1;

	} // reset(): void


	//-------------------------------------------------------------------------
	public final String getName() { return name; }


	//-------------------------------------------------------------------------
	public final Object createPosition(int offs)
	{
		Position pos;

		try {
			pos = buffer.getDocument().createPosition(offs);
		} catch (BadLocationException e) {
//%			JBrowse.log(1, this, "BadLocationException thrown in exception handler of method createPosition().");
			pos = null;
		}
		return pos;
	}

	//-------------------------------------------------------------------------
	public final String getLine(int lineIndex)
	{
		// ??? Note this should be cleaned up. Currently rely on returning
		// null when source is exhausted. Should actually throw exception in
		// the second case. Probably should have an indexed line source vs. sequential
		// line source. The second would only allow calls to getNextLine and it
		// would keep track of the line number for the client.
		String lineString;

		// Sanity check
		if( lineIndex > map.getElementCount() - 1 ) {

//%			JBrowse.log(4, this, "Argument to getLine() is bad: " + lineIndex);
			System.out.println(this + " Argument to getLine() is bad: " + lineIndex);
			return null; // source has been exhaused
		}

		try	{
			lineElement = map.getElement(lineIndex);

			// Sanity check
			if( lineElement == null ) {
//%				JBrowse.log(1, this, "Element returned by getElement() is null");
				return null;
				//System.exit(0); // ??? should throw an exception here
			}
			lastLine = lineIndex;

			start = lineElement.getStartOffset();
			lineString = buffer.getDocument().getText(start, lineElement.getEndOffset() - start - 1);

		} catch (BadLocationException ble) {
//%			JBrowse.log(1, this, "BadLocationException thrown in getLine(int) method.");
			lineString = "";
		}
		return lineString;

	} // getLine(int): String


	//-------------------------------------------------------------------------
	public final boolean isExhausted()
	{
		return (lastLine >= map.getElementCount() - 1);
	}


	// this is specific to a JEditLineSource, should get rid of it ???
	public final int getStartOffset() { return start; }
	public final int getTotalLines() { return map.getElementCount(); }

	final JextTextArea getTextArea() { return buffer; }

} // class JEditLineSource

// End of JBrowsePlugin.java
