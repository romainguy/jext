/*
 * 08/15/2001 - 14:05:44
 *
 * XTree.java - A tree used for XInsert system
 * Copyright (C) 1999-2000 Romain Guy
 * Portion copyright (C) 2000 Richard Lowe
 * romain.guy@jext.org
 * http://www.jext.org
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

package org.jext.xinsert;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;

import org.jext.*;
import org.jext.dawn.*;
import org.jext.event.*;
import org.jext.scripting.dawn.Run;
import org.jext.gui.JextCheckBox;
import org.jext.gui.JextHighlightButton;
import org.jext.misc.Indent;
import org.jext.xml.XInsertReader;

public class XTree extends JPanel implements TreeSelectionListener, ActionListener, Runnable,
                                             JextListener
{
  private static Vector inserts;

  private String file, currentMode;

  private JTree tree;
  private JextFrame parent;

  /**
   * This contains the complete content of the files. It's never associated to the JTree:
   * associateXTreeToMode reads it and chooses the right nodes.
   * @see associateXTreeToMode
   */
  private DefaultTreeModel treeModel;
  private JextHighlightButton expand, collapse, reload;
  private JextCheckBox carriageReturn, executeScript, textSurrounding;

  // nested submenus
  private int rootIndex;
  private XTreeNode root;
  private Stack menuStack = null;
  private XTreeObject xtreeObj = null;
  
  public void addMenu(String nodeName, String modes)
  {
    xtreeObj = new XTreeObject(new XTreeNode(nodeName, modes), 0);

    if (menuStack.empty())
    {
      treeModel.insertNodeInto(xtreeObj.getXTreeNode(), root, rootIndex);
      rootIndex++;
    } else {
      XTreeObject obj = (XTreeObject) menuStack.peek();
      treeModel.insertNodeInto(xtreeObj.getXTreeNode(), obj.getXTreeNode(), obj.getIndex());
      obj.incrementIndex();
    }

    menuStack.push(xtreeObj);
  }

  public void closeMenu()
  {
    try
    {
      xtreeObj = (XTreeObject) menuStack.pop();
    } catch (Exception e) {
      xtreeObj = null;
    }
  }

  public void addInsert(String nodeName, String content, int type)
  {
    inserts.addElement(new XTreeItem(content, type));
    XTreeNode node = new XTreeNode(nodeName, null, inserts.size());

    if (xtreeObj == null)
    {
      treeModel.insertNodeInto(node, root, rootIndex);
      ++rootIndex;
    } else {
      XTreeObject obj = (XTreeObject) menuStack.peek();
      treeModel.insertNodeInto(node, obj.getXTreeNode(), obj.getIndex());
      obj.incrementIndex();
    }
  }

  public XTree(JextFrame parent, String file)
  {
    super();
    this.parent = parent;
    parent.addJextListener(this);

    setLayout(new BorderLayout());

    root = new XTreeNode("XInsert");
    treeModel = new DefaultTreeModel(root);
    tree = new JTree(treeModel);
    tree.addTreeSelectionListener(this);
    tree.putClientProperty("JTree.lineStyle", "Angled");

    if (!Jext.getBooleanProperty("useSkin"))
      tree.setCellRenderer(new XTreeCellRenderer());

    init(file);

    String icons = Jext.getProperty("jext.look.icons");

    JToolBar pane = new JToolBar();//new BorderLayout());
    pane.setFloatable(false);

    pane.add(collapse = new JextHighlightButton(Jext.getProperty("xtree.collapse.button"),
             Utilities.getIcon("images/button_collapse" + icons + ".gif", Jext.class)));//,
             //BorderLayout.NORTH);
    collapse.setMnemonic(Jext.getProperty("xtree.collapse.mnemonic").charAt(0));
    collapse.addActionListener(this);

    pane.add(expand = new JextHighlightButton(Jext.getProperty("xtree.expand.button"),
             Utilities.getIcon("images/button_expand" + icons + ".gif", Jext.class)));//,
             //BorderLayout.CENTER);
    expand.setMnemonic(Jext.getProperty("xtree.expand.mnemonic").charAt(0));
    expand.addActionListener(this);

    pane.add(reload = new JextHighlightButton(Jext.getProperty("xtree.reload.button"),
             Utilities.getIcon("images/menu_reload" + icons + ".gif", Jext.class)));//,
             //BorderLayout.SOUTH);
    reload.setMnemonic(Jext.getProperty("xtree.reload.mnemonic").charAt(0));
    reload.addActionListener(this);

    add(pane, BorderLayout.NORTH);
    JScrollPane s = new JScrollPane(tree);
    s.setBorder(null);
    add(s, BorderLayout.CENTER);

    JPanel optionPane = new JPanel(new BorderLayout());
    optionPane.add(carriageReturn = new JextCheckBox(Jext.getProperty("xtree.carriage.label")), BorderLayout.NORTH);
    carriageReturn.setSelected(Jext.getBooleanProperty("carriage"));
    carriageReturn.addActionListener(this);

    optionPane.add(executeScript = new JextCheckBox(Jext.getProperty("xtree.execute.label")), BorderLayout.CENTER);
    executeScript.setSelected(Jext.getBooleanProperty("execute"));
    if (Jext.getProperty("execute") == null)
      executeScript.setSelected(true);
    executeScript.addActionListener(this);

    optionPane.add(textSurrounding = new JextCheckBox(Jext.getProperty("xtree.surrounding.label")), BorderLayout.SOUTH);
    textSurrounding.setSelected(Jext.getBooleanProperty("surrounding"));
    if (Jext.getProperty("surrounding") == null)
      textSurrounding.setSelected(true);
    textSurrounding.addActionListener(this);

    add(optionPane, BorderLayout.SOUTH);
  }

  private void init(String file)
  {
    init(file, true);
  }

  private void init(String file, boolean useThread)
  {
    this.file = file;
    if (useThread)
    {
      Thread x = new Thread(this, "--XTree builder thread");
      x.start();
    } else
      run();
  }

  public void stop() { }

  //this loads the main file, while loadLocalFiles thinks to local files.
  public void run()
  {
    inserts = new Vector(200);
    menuStack = new Stack();
    rootIndex = 0;

    if (XInsertReader.read(this, Jext.class.getResourceAsStream(file), file)) {
      loadLocalFiles();
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          associateXTreeToMode(false);

	  tree.expandRow(0);
	  tree.setRootVisible(false);
	  tree.setShowsRootHandles(true);

	  file = null;
        }
      });
    }
  }

  private void loadLocalFiles()
  {
    String dir = Jext.SETTINGS_DIRECTORY + "xinsert" + File.separator;
    String inserts[] = Utilities.getWildCardMatches(dir, "*.insert.xml", false);
    if (inserts == null)
      return;

    try
    {
      String fileName;
      for (int i = 0; i < inserts.length; i++)
      {
        fileName = dir + inserts[i];
        if (XInsertReader.read(this, new FileInputStream(fileName), fileName))
        {
          String[] args = { inserts[i] };
          System.out.println(Jext.getProperty("xtree.loaded", args));
        }
      }
    } catch (FileNotFoundException fnfe) { }
  }

  public void jextEventFired(JextEvent evt)
  {
    int what = evt.getWhat();

    if (what == JextEvent.SYNTAX_MODE_CHANGED || what == JextEvent.TEXT_AREA_SELECTED ||
        what == JextEvent.OPENING_WINDOW)
    {
      associateXTreeToMode();
    }
  }

  private void associateXTreeToMode()
  {
    associateXTreeToMode(true);
  }

  /**
   * This method builds a copy of treeModel containing only nodes relevant to current
   * mode, and then uses the new TreeModel for the tree.
   * Must be called by the event-dispatching thread!
   */
  private void associateXTreeToMode(boolean checkColorizingMode)
  {
    JextTextArea textArea = parent.getTextArea();
    if (textArea == null)
      return;

    String mode = textArea.getColorizingMode();
    //if we must reload(at user's request or when loading is finished), we do this even when
    //mode has not changed.
    if (checkColorizingMode && mode.equals(currentMode)) 
      return;

    int index = 0;
    XTreeNode _root = new XTreeNode("XInsert");
    DefaultTreeModel _model = new DefaultTreeModel(_root);

    for (int i = 0; i < root.getChildCount(); i++)
    {
      XTreeNode child = (XTreeNode) root.getChildAt(i);

      if (child.isAssociatedToMode(mode))
      {
        child.setParent(null);
        if (child.isPermanent())
          _root.add(child);
        else
        {
          if (child.toString().equalsIgnoreCase(mode))
            _root.insert(child, 0);
          else
            _root.insert(child, index);
          index++;
        }
      }
    }

    tree.setModel(_model);      //so we need to be in the event-dispatching thread.
    tree.expandRow(0);
    currentMode = mode;
  }

  public void valueChanged(TreeSelectionEvent tse)
  {
    JTree source = (JTree) tse.getSource();
    if (source.isSelectionEmpty())
      return;

    XTreeNode node = (XTreeNode) source.getSelectionPath().getLastPathComponent();
    if (node.getIndex() == -1)
    {
      parent.getTextArea().grabFocus();
      return;
    }

    try
    {
      insert(node.getIndex() - 1);
    } catch (Exception e) { }

    source.setSelectionPath(source.getPathForRow(-1));
  }

  public void reload(DefaultTreeModel model)
  {
    // tree.setModel(model);
    this.treeModel = model;
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object o = evt.getSource();
    if (o == expand)
    {
      for (int i = 0; i < tree.getRowCount(); i++)
        tree.expandRow(i);
    } else if (o == collapse) {
      for (int i = tree.getRowCount(); i >= 0; i--)
        tree.collapseRow(i);
    } else if (o == reload) {
      root.removeAllChildren();
      treeModel.reload();

      init("jext.insert.xml", false);
      associateXTreeToMode(false);

      ArrayList  instances = Jext.getInstances();
      for (int i = 0; i < instances.size(); i++)
      {
        JextFrame instance = (JextFrame) instances.get(i);
        if (instance != parent)
        {
          instance.getXTree().reload(treeModel);
          instance.getXTree().associateXTreeToMode(false);
        }
      }
    } else if (o == carriageReturn)
      Jext.setProperty("carriage", carriageReturn.isSelected() ? "on" : "off");
    else if (o == executeScript)
      Jext.setProperty("execute", executeScript.isSelected() ? "on" : "off");
    else if (o == textSurrounding)
      Jext.setProperty("surrounding", textSurrounding.isSelected() ? "on" : "off");
  }

  private void insert(int index) throws BadLocationException
  {
    char c = '\0';

    XTreeItem item = (XTreeItem) inserts.elementAt(index);
    String data = item.getContent();
    boolean script = item.isScript();
    boolean mixed = item.isMixed();

    StringBuffer _buf = new StringBuffer(data.length());

    for (int i = 0; i < data.length(); i++)
    {
      if( (c = data.charAt(i)) == '\\' && i < data.length() - 1)
      {
	switch (data.charAt(i + 1))
	{
	  case 'n':
	    i++;
	    _buf.append('\n');
	    break;
	  case 't':
	    i++;
	    _buf.append('\t');
	    break;
	  case '\\':
	    i++;
	    _buf.append('\\');
	    break;
	}
      } else
	_buf.append(c);
    }

    data = _buf.toString();
    JextTextArea textArea = parent.getTextArea();

    if (script && executeScript.isSelected())
    {
      Run.execute(data, parent);
    } else {
      textArea.beginProtectedCompoundEdit();
      boolean indent = textArea.getEnterIndent();
      Document doc = textArea.getDocument();

      String surroundText = "";
      if (textArea.getSelectionStart() != textArea.getSelectionEnd())
      {
        surroundText = textArea.getSelectedText();
        textArea.setSelectedText("");
      }

      _buf = new StringBuffer(data.length());

      int caretState = 0;
      int insertPos = 0;
      int lastBreak = -1;
      int wordStart = textArea.getCaretPosition();
      int caret = data.length();

      StringBuffer mixedScript = new StringBuffer(30);
      boolean parsing = false;
      boolean onFirstLine = true, wasFirstLine = false;

out:  for (int i = 0 ; i < data.length(); i++)
      {
        switch (c = data.charAt(i))
        {
          case '|':
            if (parsing)
              mixedScript.append('|');
            else
            {
              if (i < data.length() - 1 && data.charAt(i + 1) == '|')
              {
                i++;
                _buf.append('|');
              } else {
                if (caretState == 0)
                {
                  caret = insertPos + _buf.length();
                  caretState = 1;
                  if (onFirstLine)
                    wasFirstLine = true;
                }
              }
            }
            break;
          case '\n':
            if (parsing)
            {
              mixedScript.append('\n');
              break;
            }

            if (indent && !onFirstLine)
            {
              doc.insertString(wordStart + insertPos, _buf.toString(), null);
              insertPos += _buf.length();
              _buf = new StringBuffer(data.length() - _buf.length());
              int tempLen = doc.getLength();
              Indent.indent(textArea, textArea.getCaretLine(), true, true);
              int indentLen = doc.getLength() - tempLen;

              if (caretState == 1)
              {
                if (!wasFirstLine)
                  caret += indentLen;
                caretState = 2;
              }

              insertPos += indentLen;
              wasFirstLine = false;
            }

            _buf.append('\n');
            onFirstLine = false;
            lastBreak = i;
            break;
          case '%':
            if (mixed)
            {
              if (i < data.length() - 1 && data.charAt(i + 1) == '%')
              {
                i++;
                (parsing ? mixedScript : _buf).append('%');
              } else if (parsing) {
                parsing = false;

                try
                {
                  if (!DawnParser.isInitialized())
                  {
                    DawnParser.init();
                    DawnParser.installPackage(Jext.class, "dawn-jext.scripting");
                  }

                  DawnParser parser = new DawnParser(new StringReader(mixedScript.toString()));
                  parser.setProperty("JEXT.JEXT_FRAME", parent);
                  parser.exec();

                  if (!parser.getStack().isEmpty())
                    _buf.append(parser.popString());
                } catch (DawnRuntimeException dre) {
                  JOptionPane.showMessageDialog(parent, dre.getMessage(),
                                                Jext.getProperty("dawn.script.error"),
                                                JOptionPane.ERROR_MESSAGE);
                }

                mixedScript = new StringBuffer(30);
              } else {
                parsing = true;
              }

              break;
            }
          default:
            (parsing ? mixedScript : _buf).append(c);
        }
      }

      doc.insertString(wordStart + insertPos, _buf.toString(), null);

      if (!onFirstLine)
      {
        int tempLen = doc.getLength();
        Indent.indent(textArea, textArea.getCaretLine(), true, true);
        if (lastBreak < caret && caretState <= 1)
          caret += doc.getLength() - tempLen;
      }

      int caretPos = wordStart + caret;
      int tempLen = doc.getLength();
      if (caretPos > tempLen)
        caretPos = tempLen;
      if (surroundText.length() > 0 && textSurrounding.isSelected())
        doc.insertString(caretPos, surroundText, null);
      textArea.setCaretPosition(caretPos);

      textArea.endProtectedCompoundEdit();
    }

    textArea.grabFocus();
  }

  private static final ImageIcon[] leaves =
  {
    Utilities.getIcon("images/tree_leaf.gif", Jext.class),
    Utilities.getIcon("images/tree_leaf_script.gif", Jext.class),
    Utilities.getIcon("images/tree_leaf_mixed.gif", Jext.class)
  };

  class XTreeCellRenderer extends DefaultTreeCellRenderer
  {
    XTreeCellRenderer()
    {
      super();

      openIcon = Utilities.getIcon("images/tree_open.gif", Jext.class);
      closedIcon = Utilities.getIcon("images/tree_close.gif", Jext.class);
      textSelectionColor = Color.red;
      borderSelectionColor = tree.getBackground();
      backgroundSelectionColor = tree.getBackground();
    }

    public Component getTreeCellRendererComponent(JTree source, Object value, boolean sel,
                                                  boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus)
    {
      if (leaf)
      {
        TreePath path = source.getPathForRow(row);
        if (path != null)
        {
          XTreeNode node = (XTreeNode) path.getLastPathComponent();
          int index = node.getIndex();

          if (index != -1)
          {
            leafIcon = leaves[((XTreeItem) inserts.elementAt(index - 1)).getType()];
          }
        }
      }

      return super.getTreeCellRendererComponent(source, value, sel, expanded, leaf, row, hasFocus);
    }
  }
}

// End of XTree.java