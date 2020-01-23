/*
 * UMLTree.java
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

import java.util.*;
import java.awt.*;
import javax.swing.*;
//import java.awt.font.*; // JDK 1.2 specific, required for underline
                          // (but only for the elegant way that doesn't work in 1.2.1 anyway).
						  // therefore not currently implemented.
import javax.swing.tree.*;

import org.jext.Utilities;

//=============================================================================
public class UMLTree extends JTree
{

	//-------------------------------------------------------------------------
	/**
	 * Constructor for a UMLTree, automatically sets the new UMLTree's
	 * associated UMLModel to null.
	 */
	public UMLTree()
	{
		super.setModel(null);

		putClientProperty("JTree.lineStyle", "Angled");
		setVisibleRowCount(15);

	} // UMLTree(TreeModel): <init>


	//-------------------------------------------------------------------------
	/**
	 * This is the method that is called whenever the results of a new parse
	 * need to be displayed, or when filter options have changed on the
	 * currently parsed and displayed UMLTree.
	 */
	public void display(Model tm, Options options, JBrowseParser.Results results)
	{
		Options.DisplayIro displayOpt = options.getDisplayOptions();
		Options.FilterIro  filterOpt  =	options.getFilterOptions();

		setCellRenderer(new CellRenderer( displayOpt ) );

		tm.setFilterOptions(filterOpt);
		super.setModel(tm);
		tm.reload();

		expandRow(0);

		TreePath tp = results.getTopLevelPath();
		if (tp != null) {
			expandPath(tp);
		}

	} // display(TreeModel, Options, JBrowseParser.Results): void


//	//-------------------------------------------------------------------------
//	/**
//	 * This updates only the nodes that are currently visible in the JTree.
//	 * This is the method that is called whenever the display options have
//	 * changed on the currently parsed and displayed UMLTree.
//	 */
//	public void updateVisible(Options options)
//	{
//		int lastRow = getRowCount() - 1;
//		Model tm = (Model) getModel();
//
//		if (lastRow > 200 ) {
////System.out.println("Too many rows to update individually (" + lastRow + ") will reload.");
//			display(tm, options);
//			return;
//		}
//
////System.out.println("getRowCount(): " + getRowCount() + ", getVisibleRowCount(): " + getVisibleRowCount());
////System.out.println("Will update " + lastRow + " rows");
//
//		Options.FilterIro  filterOpt  =	options.getFilterOptions();
//		TreeNode parent = null;
//		Object[] cChildren = new Object[1];
//		int[] childIndices = new int[1];
//
//		for (int i = 0; i <= lastRow; i++) {
//
//			Node node =  (Node) getPathForRow(i).getLastPathComponent();
//
//			parent = node.getParent();
//			int anIndex = node.getVisibleIndex(filterOpt);
//
//			if(anIndex != -1) {
//
//				// i.e. is a visible child
//				childIndices[0] = anIndex;
//				cChildren[0] = node;
//
//				tm.fireTreeNodesChanged(parent, childIndices, cChildren);
//
////				tm.nodesChanged(parent, childIndices);
//			}
//		}
//	} // updateVisible(Node): void


	//-------------------------------------------------------------------------
	public void updateVisibleToggled(Options options)
	{
		Model tm = (Model) getModel();
		Options.FilterIro  filterOpt = options.getFilterOptions();
		TreePath aPath;
		Node aNode;
		Object[] cChildrenObject;

		aPath = getPathForRow(0);
		Enumeration e = getDescendantToggledPaths(aPath);

		//System.out.println("updateVisibleToggled called for root path: " + aPath);		

		while ( e.hasMoreElements() ) {
			aPath = (TreePath) e.nextElement();
			//System.out.println( aPath );

			aNode = (Node) aPath.getLastPathComponent();
			
			cChildrenObject = aNode.getVisibleChildrenObject(filterOpt);

			if (cChildrenObject != null) {

				tm.fireTreeNodesChanged(aNode, (int[]) cChildrenObject[0],
						(Object[]) cChildrenObject[1] );
			}
		}

	} // updateVisibleToggled(Node): void


	//=========================================================================
	static class Model extends DefaultTreeModel
	{
		private Options.FilterIro filterOpt = null;

		// Overrides:
		//    getChild() & getChildCount()

		// Constructors

		public Model(TreeNode root)
		{
			super(root);
		}

		// Filter state accessors

		public void setFilterOptions(Options.FilterIro filterOpt)
		{
			this.filterOpt = filterOpt;
		}

		public Options.FilterIro getFilterOptions() { return filterOpt; }


		final void fireTreeNodesChanged(TreeNode parent, int[] childIndices, Object[] children)
		{
			super.fireTreeNodesChanged(this, getPathToRoot(parent), childIndices, children);
		}

		// Overridded methods to provide InvisibleTreeModel behaviour

		//---------------------------------------------------------------------
		public Object getChild(Object parent, int index)
		{
			if (filterOpt != null) {
					return ( (Node) parent).getChildAt(index, filterOpt);
			}
			return ( (Node) parent).getChildAt(index);
		}

		//---------------------------------------------------------------------
		public int getChildCount(Object parent)
		{
			if (filterOpt != null) {
					return ( (Node) parent).getChildCount(filterOpt);
			}
			return ( (Node) parent).getChildCount();
		}

		//---------------------------------------------------------------------
		public boolean isLeaf(Object node)
		{
			if (getChildCount(node) > 0 )
				return false;
			else
				return true;
		}


//		/**
//		 * This method updates the immediate children of the passed node. It works
//		 * well but is very slow on large trees, you should seriously
//		 * consider using the associated UMLTree's updateVisible() method
//		 * instead.
//		 */
//		public void updateVisibleChildren(Node parent)
//		{
//			for(int i = 0; i < parent.getChildCount(filterOpt); i++ ) {
//				Node child = parent.getChildAt(i, filterOpt);
//				nodeChanged(child);
//				if ( child.getChildCount(filterOpt) > 0 ) {
//					updateChildren(child);
//				}
//			}
//
//		} // updateVisibleChildren(Node): void
//
//
//		/**
//		 * This method updates all children of the passed node. That are not
//		 * invisible (although they may not be in the curently displayed portion
//		 * of the JTree.
//		 * It works well but is very slow on large trees, you should seriously
//		 * consider using the associated UMLTree's updateVisible() method
//		 * instead.
//		 */
//		public void updateVisibleChildren(Node parent)
//		{
//			for(int i = 0; i < parent.getChildCount(filterOpt); i++ ) {
//				Node child = parent.getChildAt(i, filterOpt);
//				nodeChanged(child);
//				if ( child.getChildCount(filterOpt) > 0 ) {
//					updateVisibleChildren(child);
//				}
//			}
//
//		} // updateVisibleChildren(Node): void


	} // public class InvisibleTreeModel extends DefaultTreeModel


	//=========================================================================
	static class Node extends DefaultMutableTreeNode
	{
		private Object pos = null;

		public Node(UML.Element userObject) {
			super(userObject);
		}

		public Node(String userObject) {
			super(userObject);
		}

		public final void setPosition(Object pos) {
			this.pos = pos;
		}
		public final Object getPosition() {
			return pos;
		}
		
		public final void alphaSort()
		{
			ArrayList sortedList = new ArrayList();
			UMLTree.Node node;
			UML.Element elm;
			if (children != null && children.size() > 0)
			{
				HashMap methods = new HashMap(children.size());
				for (int i = 0; i < children.size(); i++)
				{
					node = (Node)(children.get(i));
					elm = node.getElement();
					try
					{
						UML.Operation operation = (UML.Operation)elm;
						Options.Display display = new Options.Display();
						display.setShowArguments(true);
						
						String add = operation.getName() + "(" +
						 operation.listArgs((Options.DisplayIro)display) + ")";
						 
						methods.put(add, node);
					}//end try 
					catch(ClassCastException cce)
					{
						if (elm.isInterface() || elm.isClass())
						{
							node.alphaSort();
						}//end if class or interface
						sortedList.add(node);
					}//end catch
				}//end for i . . .
				 
				String[] methodNames =
				 (String[])(methods.keySet().toArray(new String[methods.size()]));
				Arrays.sort(methodNames);
				for (int j = 0; j < methodNames.length; j++)
				{
					sortedList.add(methods.get(methodNames[j]));
				}//end for j . . .
				
				children.removeAllElements();
				children.addAll(sortedList);
			}//end if children != null && children.size() > 0
		
		}//end alphaSort

		//---------------------------------------------------------------------
		/**
		 * Returns the TreePath from the specified ancestor Node to this
		 * node.
		 */
		public TreePath getPathFrom(Node ancestor)
		{
			Enumeration e = pathFromAncestorEnumeration(ancestor);
			Vector pathList = new Vector();
			int depth = 0;
			Node curNode;

			while (e.hasMoreElements()) {
				depth++;
				curNode = (Node) e.nextElement();
				pathList.addElement(curNode); //  patch for jdk1.1
				// pathList.add(curNode); // jdk1.2 only ???
			}

			// for JDK 1.1
			Node[] pathArray = new Node[pathList.size()];
			pathList.copyInto(pathArray);
			return new TreePath(pathArray);

			// for JDI 1.2
			//return new TreePath( pathList.toArray() );
		} // getPathFrom(Node): TreePath


		//---------------------------------------------------------------------
		public final boolean isVisible(Options.FilterIro filterOpt)
		{
			if (userObject instanceof UML.Element) {			
				return ( (UML.Element) getUserObject() ).isVisible(filterOpt);
			} else {
				return true;
			}
		}

		//---------------------------------------------------------------------
		public final UML.Element getElement() {
			if (userObject instanceof UML.Element) {
				return (UML.Element) userObject;
			} else {
				return null;
			}
		}


//		public String getName() {  // implement later
//			return userObject.toString();
//		}


		//---------------------------------------------------------------------
		public UML.Type getElementType() 
		{	
			if (userObject instanceof UML.Element) {
				return ( (UML.Element) userObject).getElementType();
			} else {
				return null;
			}
		}


		//---------------------------------------------------------------------
		public void setName(String name)
		{
			if (userObject instanceof UML.Element) {
				( (UML.Element) userObject).setName(name);
			} else {
				userObject = name;
			}
		}


		//---------------------------------------------------------------------
		/**
		 * Returns visible index of the current node, or -1 if it is not
		 * visible based on the specified filter options.
		 */
		public final int getVisibleIndex(Options.FilterIro filterOpt)
		{
			Node parent = (Node) this.getParent();
			if(parent == null) return -1;

			Vector children = parent.children;
			int visibleIndex = -1;

			for(int i = 0; i < children.size(); i++) {
				Node curNode = (Node) children.elementAt(i);
				Object nodeObject = curNode.userObject;
				if ( ( (UML.Element) nodeObject).isVisible(filterOpt) ) {
					visibleIndex++;
					if (curNode == this) return visibleIndex;
				}

				if (curNode == this) return -1;
			}

			throw new ArrayIndexOutOfBoundsException("index unmatched");

		} // getVisibleIndex(int, Options.FilterIro): int


//		//---------------------------------------------------------------------
//		public int getVisibleIndex(Options.FilterIro filterOpt)
//		{
//            TreeNode parent = this.getParent();
//            if(parent == null) return -1;
//
//			int visibleIndex = -1;
//			Enumeration enum = parent.children();
//
//			while (enum.hasMoreElements()) {
//				Node curNode = (Node) enum.nextElement();
//				Object nodeObject = curNode.userObject;
//				if ( ( (UML.Element) nodeObject).isVisible(filterOpt) ) {
//					visibleIndex++;
//					if (curNode == this) return visibleIndex;
//				}
//
//				if (curNode == this) return -1;
//			}
//
//			throw new ArrayIndexOutOfBoundsException("index unmatched");
//
//		} // getVisibleIndex(int, Options.FilterIro): int


		//---------------------------------------------------------------------
		/**
		 * Creates and returns a forward-order enumeration of this node's
		 * visible children.
		 *
		 * @return	an Enumeration of this node's visible children
		 */
		public Object[] getVisibleChildrenObject(Options.FilterIro filterOpt)
		{
			int count = getChildCount(filterOpt);
			if (count > 0) {
				Object[] visibleChildNodes = new Object[count];
				int[]    visibleChildIndxs = new int[count];
				int visibleIndex = -1;

				for(int i = 0; i < children.size(); i++) {
					Node curNode = (Node) children.elementAt(i);
					Object nodeObject = curNode.userObject;
					if ( ( (UML.Element) nodeObject).isVisible(filterOpt) ) {
						visibleIndex++;
						visibleChildIndxs[visibleIndex] = visibleIndex;
						visibleChildNodes[visibleIndex] = curNode;
					}
				}
				return new Object[] { visibleChildIndxs, visibleChildNodes };	
			} else {
				return null;
			}

		} // getVisibleChildrenObject(Options.FilterIro): Object[]


		//---------------------------------------------------------------------
		/**
		 * Returns the child of this node with the specified visible index
		 * based on the specified filter options.
		 */
		public final Node getChildAt(int index, Options.FilterIro filterOpt)
		{
			if (children == null) {
				throw new ArrayIndexOutOfBoundsException("node has no children");
			}

			int realIndex    = -1;
			int visibleIndex = -1;
			Enumeration enum = children.elements();
			while (enum.hasMoreElements()) {
				Object nodeObject = ( (Node) enum.nextElement()).userObject;

				if ( ( (UML.Element) nodeObject).isVisible(filterOpt) ) {
					visibleIndex++;
				}

				realIndex++;
				if (visibleIndex == index) {
					return (Node)children.elementAt(realIndex);
				}
			}

			throw new ArrayIndexOutOfBoundsException("index unmatched");
		}

		//---------------------------------------------------------------------
		/**
		 * Returns a count of the number of visible children of this node
		 * based on the specified filter options.
		 */
		public final int getChildCount(Options.FilterIro filterOpt)
		{
			if (children == null) {
				return 0;
			}

			int count = 0;
			Enumeration enum = children.elements();
			while (enum.hasMoreElements()) {
				Object nodeObject = ( (Node) enum.nextElement()).userObject;

				if ( ( (UML.Element) nodeObject).isVisible(filterOpt) ) {
					count++;
				}
			}
			return count;
		}

	} // class UMLTree.Node extends DefaultMutableTreeNode


	//=========================================================================
	static class CellRenderer extends DefaultTreeCellRenderer
	{
		private static Font standardFont = new Font("Helvetica", Font.PLAIN, 12);
		private static Font italicFont   = new Font("Helvetica", Font.ITALIC, 12);

		private boolean isUnderlined;
		private Options.DisplayIro options;
		private Options.DisplayIro inverseOptions;

		public CellRenderer(Options.DisplayIro options)
		{
			super();
			this.options = options;
		}

		//-------------------------------------------------------------------------
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel,
				boolean expanded,
				boolean leaf,
				int row,
				boolean hasFocus )
		{

			Component r = super.getTreeCellRendererComponent(tree, value, sel,
							expanded, leaf,
							row, hasFocus);

			isUnderlined = false;

			if (r instanceof JLabel) {
				JLabel lab = (JLabel) r;

				lab.setToolTipText(null);
				tree.setToolTipText(null);

				UMLTree.Node node = (UMLTree.Node) value;

				Object uObj = node.getUserObject();

				if ( uObj != null && uObj instanceof UML.Element ) {

					UML.Element e = (UML.Element) uObj;
					UML.Type type = e.getElementType();

					if (type != null) {

						// label
						lab.setText(e.toString(options));

						if ( options.getAbstractItalic() && e.isAbstract() ) {
							lab.setFont( italicFont );
						} else {
							lab.setFont( standardFont );
						}

						if (options.getStaticUlined() && e.isStatic() ) {
							isUnderlined = true;
						}

						// tips
						inverseOptions = options.getInverseOptions();

						lab.setToolTipText(e.toString(inverseOptions) + " ");
						tree.setToolTipText(e.toString(inverseOptions) + " ");

//						if ( options.getAbstractItalic() && e.isAbstract() ) {
//							lab.setFont( italicFont );
//							tree.setFont( italicFont );
//
//						} else {
//							lab.setFont( standardFont );
//							tree.setFont( standardFont );
//						}

						// icon
						Icon icon = (Icon) type.getIcon();
						if (icon != null) {
							lab.setIcon(icon) ;
						}
					}

				} else {

					// for strings (e.g. root)
					lab.setFont(standardFont);
				}
			}

			return r;

		} // getTreeCellRendererComponent(JTree, Object, boolean, boolean, boolean, int, boolean): Component


	 	//-------------------------------------------------------------------------
	 	protected void paintComponent(Graphics g)
	 	{
	 		super.paintComponent(g);
	 		if ( this.isUnderlined) {
		 		int x = getIcon().getIconWidth() + Math.max(0, getIconTextGap() - 1);
			    g.setColor(Color.black);
		 		g.drawLine(x, getHeight() - 2, getWidth() - 2, getHeight() - 2);
	 		}

	 	} // paintComponent(Graphics) // void

	} // class UMLTree.CellRenderer extends DefaultTreeCellRenderer

} // class UMLTree extends JTree




