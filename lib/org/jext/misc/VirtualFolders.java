/*
* 09/30/2001 - 01:37:11
*
* VirtualFolders.java - Virtual folders panel
* Copyright (C) 2000 Romain Guy
* Portions Copyright (C) 2001 Grant Stead
* guy.romain@bigfoot.com
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
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, 
USA.
*/

package org.jext.misc;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.io.*;

import java.util.*;

import org.jext.*;
import org.jext.event.*;
import org.jext.gui.*;

import com.microstar.xml.*;

/**
 * Virtual Folders are a kind of project manager. Virtual Folders allow to 
 *sort
 * documents into folder, categorizing them.
 * @author Romain Guy, Grant Stead
 * @version 2.0
 */

public class VirtualFolders extends JPanel implements ActionListener,
JextListener, TreeSelectionListener
{
  // miscallenaous
  private JextFrame parent;

  // the buttons panel
  private JextHighlightButton deleteItem, openFile, addFile, addAllFiles, newFolder;

  // the popup menu
  private JPopupMenu popup;
  private EnhancedMenuItem deleteM, openFileM, addFileM, addAllFilesM, newFolderM;

  // the tree
  private JTree tree;
  private DefaultTreeModel treeModel;
  private VirtualFolderNode root;

  public VirtualFolders(JextFrame parent)
  {
    super(new BorderLayout());
    this.parent = parent;
    parent.addJextListener(this);

    JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);

    ImageIcon icon = null;
    popup = new JPopupMenu();

    // Open
    popup.add(openFileM = new EnhancedMenuItem(Jext.getProperty("vf.open.label")));

    if (Jext.getProperty("vf.open.picture") != null)
    {
      icon = Utilities.getIcon(Jext.getProperty("vf.open.picture").concat(
                               Jext.getProperty("jext.look.icons")).concat(".gif"), Jext.class);
      if (icon != null)
        openFileM.setIcon(icon);
    }
    openFileM.addActionListener(this);
    toolbar.add(openFile = new JextHighlightButton(icon));
    openFile.setToolTipText(Jext.getProperty("vf.open.tooltip"));
    openFile.addActionListener(this);
    Dimension size = new Dimension(openFile.getMaximumSize().height,
    openFile.getMaximumSize().height);
    openFile.setMaximumSize(size);
    openFile.setEnabled(false);
    openFileM.setEnabled(false);

    // New Folder
    popup.add(newFolderM = new EnhancedMenuItem(Jext.getProperty("vf.new.label")));
    if (Jext.getProperty("vf.new.picture") != null)
    {
      icon = Utilities.getIcon(Jext.getProperty("vf.new.picture").concat(
                               Jext.getProperty("jext.look.icons")).concat(".gif"), Jext.class);
      if (icon != null)
        newFolderM.setIcon(icon);
    }
    newFolderM.addActionListener(this);
    toolbar.add(newFolder = new JextHighlightButton(icon));
    newFolder.setToolTipText(Jext.getProperty("vf.new.tooltip"));
    newFolder.addActionListener(this);
    newFolder.setMaximumSize(size);

    // Add
    popup.add(addFileM = new EnhancedMenuItem(Jext.getProperty("vf.add.label")));
    if (Jext.getProperty("vf.add.picture") != null)
    {
      icon = Utilities.getIcon( Jext.getProperty("vf.add.picture").concat(
              Jext.getProperty("jext.look.icons")).concat(".gif"), Jext.class);
      if (icon != null)
        addFileM.setIcon(icon);
    }
    addFileM.addActionListener(this);
    toolbar.add(addFile = new JextHighlightButton(icon));
    addFile.setToolTipText(Jext.getProperty("vf.add.tooltip"));
    addFile.addActionListener(this);
    addFile.setMaximumSize(size);

    // Add all
    popup.add(addAllFilesM = new EnhancedMenuItem(Jext.getProperty("vf.addall.label")));
    if (Jext.getProperty("vf.addall.picture") != null)
    {
      icon = Utilities.getIcon(Jext.getProperty("vf.addall.picture").concat(
                               Jext.getProperty("jext.look.icons")).concat(".gif"), Jext.class);
      if (icon != null)
        addAllFilesM.setIcon(icon);
    }
    addAllFilesM.addActionListener(this);
    toolbar.add(addAllFiles = new JextHighlightButton(icon));
    addAllFiles.setToolTipText(Jext.getProperty("vf.addall.tooltip"));
    addAllFiles.addActionListener(this);
    addAllFiles.setMaximumSize(size);

    // Delete
    popup.add(deleteM = new EnhancedMenuItem(Jext.getProperty("vf.delete.label")));
    if (Jext.getProperty("vf.delete.picture") != null)
    {
      icon = Utilities.getIcon(Jext.getProperty("vf.delete.picture").concat(
                               Jext.getProperty("jext.look.icons")).concat(".gif"), Jext.class);
      if (icon != null)
        deleteM.setIcon(icon);
    }
    deleteM.addActionListener(this);
    toolbar.add(deleteItem = new JextHighlightButton(icon));
    deleteItem.setToolTipText(Jext.getProperty("vf.delete.tooltip"));
    deleteItem.addActionListener(this);
    deleteItem.setMaximumSize(size);
    deleteItem.setEnabled(false);
    deleteM.setEnabled(false);

    toolbar.setMaximumSize(new Dimension(size.width * 5, size.height));
    add(toolbar, BorderLayout.NORTH);

    root = new VirtualFolderNode("VirtualFolders", false);
    treeModel = new DefaultTreeModel(root);
    tree = new JTree(treeModel);
    new DropTarget(tree, new DnDHandler());

    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setOpenIcon(Utilities.getIcon("images/tree_open.gif", Jext.class));
    renderer.setLeafIcon(Utilities.getIcon("images/tree_leaf.gif", Jext.class));
    renderer.setClosedIcon(Utilities.getIcon("images/tree_close.gif", Jext.class));

    
    renderer.setTextSelectionColor(GUIUtilities.parseColor(Jext.getProperty("vf.selectionColor")));
    renderer.setBackgroundSelectionColor(tree.getBackground());
    renderer.setBorderSelectionColor(tree.getBackground());

    tree.addMouseListener(new MouseHandler());
    tree.setCellRenderer(renderer);
    tree.putClientProperty("JTree.lineStyle", "Angled");
    tree.setScrollsOnExpand(true);

    DefaultTreeSelectionModel selectionModel = new 
    DefaultTreeSelectionModel();
    selectionModel.setSelectionMode(DefaultTreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    tree.setSelectionModel(selectionModel);

    load();
    tree.clearSelection();
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree.addKeyListener(new KeyHandler());
    tree.addTreeSelectionListener(this);

    fixVisible();
    tree.expandPath(new TreePath(root.getPath()));

    JScrollPane scroller = new JScrollPane(tree);
    scroller.setBorder(null);
    add(scroller, BorderLayout.CENTER);
  }

  public void jextEventFired(JextEvent evt)
  {
    if (evt.getWhat() == JextEvent.KILLING_JEXT)
      save();
  }

  private String toXML(VirtualFolderNode parent, int depth)
  {
    String crlf = System.getProperty("line.separator");
    StringBuffer ret = new StringBuffer();

    if (parent.isLeaf() && parent != root)
    {
      TreePath path = new TreePath(parent.getPath());
      String visible = tree.isVisible(path) ? " visible=\"yes\"" : "";
      ret.append(crlf).append(getIndentation(depth + 1)).append("<file path=\"" +
                              ((VirtualFolderNode) parent).filePath + "\"" + visible + " />");
    } else {
      if (parent != root)
      {
        ret.append(crlf).append(getIndentation(depth)).append("<folder name=\"" +
                parent.toString() + "\">");
      } else {
        ret.append(crlf).append("<folderlist>");
      }

      Enumeration enum = parent.children();
      while (enum.hasMoreElements())
      {
        VirtualFolderNode child = (VirtualFolderNode) enum.nextElement();
        ret.append(toXML(child, depth + 1));
      }

      if (parent != root)
      {
        ret.append(crlf).append(getIndentation(depth)).append("</folder>");
      } else {
        ret.append(crlf).append("</folderlist>");
      }
    }

    return ret.toString();
  }

  private String getIndentation(int depth)
  {
    return Utilities.createWhiteSpace(depth * 2);
  }

  private void save()
  {
    try
    {
      File vf = new File(Jext.SETTINGS_DIRECTORY + File.separator + ".vf.xml");
      BufferedWriter writer = new BufferedWriter(new FileWriter(vf));

      String xmlString = toXML(root, 1);
      if (xmlString.length() == 0)
      {
        xmlString = "<folderlist />";
      }
      xmlString = "<?xml version=\"1.0\"?>" + xmlString;

      writer.write(xmlString, 0, xmlString.length());
      writer.flush();
      writer.newLine();

      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void load()
  {
    try
    {
      File vf = new File(Jext.SETTINGS_DIRECTORY + File.separator + ".vf.xml");
      StringBuffer xmlString = new StringBuffer((int) vf.length());

      if (vf.exists() && (vf.length() > 0))
      {
        try
        {
          BufferedReader in = new BufferedReader(new FileReader(vf));
          String line = in.readLine();
          while (line != null)
          {
            xmlString.append(line);
            line = in.readLine();
          }
          in.close();
        } catch (Exception e) {
          xmlString = new StringBuffer("<?xml version=\"1.0\"?><folderlist />");
        }
      } else {
        xmlString = new StringBuffer("<?xml version=\"1.0\"?><folderlist />");
      }

      StringReader reader = new StringReader(xmlString.toString());
      XmlParser parser = new XmlParser();
      parser.setHandler(new VirtualFoldersHandler());

      parser.parse(null, null, reader);
    }
    catch (Exception e) { }
  }

  private void fixVisible()
  {
    Enumeration enum = root.depthFirstEnumeration();
    VirtualFolderNode node = null;
    while (enum.hasMoreElements())
    {

      node = (VirtualFolderNode)enum.nextElement();
      TreePath path = new TreePath(node.getPath());
      tree.collapsePath(path);
    }

    enum = root.depthFirstEnumeration();
    while (enum.hasMoreElements())
    {
      node = (VirtualFolderNode)enum.nextElement();
      if (node.shouldBeVisible())
      {
        TreePath path = new TreePath(((VirtualFolderNode)node.getParent()).getPath());
        tree.expandPath(path);
      }
    }
  }

  private VirtualFolderNode createFolder(String name)
  {
    return createFolder(name, false);
  }

  private VirtualFolderNode createFolder(String name, boolean expand)
  {
    return createFolder(name, expand, root);
  }

  private VirtualFolderNode createFolder(String name, boolean expand,
          VirtualFolderNode parent)
  {
    if (folderExists(parent, name))
      return null;

    VirtualFolderNode node = new VirtualFolderNode(name, false);

    treeModel.insertNodeInto(node, parent, parent.getChildCount());

    TreePath path = new TreePath(node.getPath());
    tree.setSelectionPath(path);

    if (expand)
    {
      tree.expandPath(path);
    } else {
      tree.collapsePath(path);
    }

    return node;
  }

  private VirtualFolderNode createLeaf(VirtualFolderNode parent, String content)
  {
    if (parent == null || content == null)
      return null;

    Enumeration e = parent.children();
    while (e.hasMoreElements())
    {
      if (((VirtualFolderNode) e.nextElement()).getFilePath().equalsIgnoreCase(content))
        return null;
    }

    VirtualFolderNode node = new VirtualFolderNode(content, true);
    treeModel.insertNodeInto(node, parent, parent.getChildCount());
    return node;
  }

  public static boolean folderExists(VirtualFolderNode parent, String name)
  {
    boolean exists = false;
    Enumeration enum = parent.children();
    while ((enum.hasMoreElements()) && !exists)
    {
      VirtualFolderNode child = (VirtualFolderNode) enum.nextElement();
      exists = child.toString().equals(name);
    }
    return exists;
  }

  private void newFolder()
  {
    TreePath[] paths = tree.getSelectionPaths();
    VirtualFolderNode parentNode = null;

    if ((paths == null) || (paths.length == 0))
    {
      parentNode = root;
    } else {
      parentNode = (VirtualFolderNode) paths[0].getLastPathComponent();
    }

    if (parentNode.isLeaf())
    {
      if (!parentNode.isRoot())
      {
        parentNode = (VirtualFolderNode) parentNode.getParent();
      }
    }
    newFolder(parentNode);
  }

  private void newFolder(VirtualFolderNode parentNode)
  {
    String response = JOptionPane.showInputDialog(parent, Jext.getProperty("vf.add.input.msg"),
                                                          Jext.getProperty("vf.add.input.title"),
                                                          JOptionPane.QUESTION_MESSAGE);
    if (response != null && response.length() > 0)
    {
      if (createFolder(response, true, parentNode) == null)
        GUIUtilities.message(parent, "vf.folder.exists", null);
    }
  }

  private void removeItem()
  {
    TreePath[] paths = tree.getSelectionPaths();
    if (paths != null)
    {
      for (int i = 0; i < paths.length; i++)
      {
        VirtualFolderNode node = (VirtualFolderNode) paths[i].getLastPathComponent();
        treeModel.removeNodeFromParent(node);
      }
    }

    int index = root.getChildCount() - 1;
    if (index >= 0)
    {
      VirtualFolderNode _node_ = (VirtualFolderNode) root.getChildAt(index);
      tree.setSelectionPath(new TreePath(_node_.getPath()));
    }
  }

  private void addFile()
  {
    JextTextArea textArea = parent.getNSTextArea();
    if (textArea.isNew())
      return;

    addFile(textArea.getCurrentFile());
  }

  private void addFile(String fileName)
  {
    TreePath selection = tree.getSelectionPath();
    VirtualFolderNode node = null;
    if (selection == null)
    {
      node = root;
    } else {
      node = (VirtualFolderNode) selection.getLastPathComponent();
      if (node.isLeaf())
        node = (VirtualFolderNode) node.getParent();
    }

    if (createLeaf(node, fileName) == null)
      GUIUtilities.message(parent, "vf.item.exists", null);
  }

  private void addAllFiles()
  {
    TreePath selection = tree.getSelectionPath();
    VirtualFolderNode node = null;
    if (selection == null)
    {
      node = root;
    } else {
      node = (VirtualFolderNode) selection.getLastPathComponent();
      if (node.isLeaf())
        node = (VirtualFolderNode) node.getParent();
    }

    JextTextArea[] textAreas = parent.getTextAreas();
    for (int i = 0; i < textAreas.length; i++)
    {
      if (textAreas[i].isNew())
        continue;

      if (createLeaf(node, textAreas[i].getCurrentFile()) == null)
        GUIUtilities.message(parent, "vf.item.exists", null);
    }
  }

  private void openSelection(boolean fromMenu)
  {
    TreePath[] paths = tree.getSelectionPaths();
    if (paths != null)
    {
      for (int i = 0; i < paths.length; i++)
      {
        VirtualFolderNode node = (VirtualFolderNode) paths[i].getLastPathComponent();
        openNode(node, fromMenu);
      }
    }
  }

  public void openNode(VirtualFolderNode node, boolean fromMenu)
  {
    if (node.isLeaf())
    {
      parent.open(node.getFilePath());
    } else {
      if (fromMenu)
      {
        Enumeration enum = node.children();
        while (enum.hasMoreElements())
        {
          VirtualFolderNode child = (VirtualFolderNode) enum.nextElement();
          openNode(child, fromMenu);
        }
      }
    }
  }

  public void notifyChanges()
  {
    ArrayList instances = Jext.getInstances();
    for (int i = 0; i < instances.size(); i++)
    {
      JextFrame instance = (JextFrame) instances.get(i);
      if (instance != parent)
        instance.getVirtualFolders().notify(treeModel);
    }
  }

  public void notify(DefaultTreeModel model)
  {
    this.treeModel = model;
    tree.setModel(treeModel);
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object o = evt.getSource();
    if ((o == newFolder) || (o == newFolderM))
    {
      newFolder();
      notifyChanges();
    } else if ((o == addFile) || (o == addFileM)) {
      addFile();
      notifyChanges();
    } else if ((o == addAllFiles) || (o == addAllFilesM)) {
      addAllFiles();
      notifyChanges();
    } else if ((o == deleteItem) || (o == deleteM)) {
      removeItem();
      notifyChanges();
    } else if ((o == openFile) || (o == openFileM)) {
      openSelection(true);
    }
  }

  public void valueChanged(TreeSelectionEvent e)
  {
    TreePath[] paths = tree.getSelectionPaths();
    boolean alsoFolder = false;
    if (paths != null)
    {
      openFileM.setEnabled(true);
      openFile.setEnabled(true);
      deleteM.setEnabled(true);
      deleteItem.setEnabled(true);

      for (int i = 0; i < paths.length; i++)
      {
        VirtualFolderNode node = (VirtualFolderNode) paths[i].getLastPathComponent();
        if (!node.isLeaf())
          alsoFolder = true;
      }
    } else {
      openFileM.setEnabled(false);
      openFile.setEnabled(false);
      deleteM.setEnabled(false);
      deleteItem.setEnabled(false);
    }
  }

  class MouseHandler extends MouseAdapter
  {
    public void mousePressed(MouseEvent e)
    {
      if (e.getModifiers() == e.BUTTON3_MASK)
      {
        popup.show(tree, e.getX(), e.getY());
      } else {
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        if (path == null)
          tree.clearSelection();

        if (e.getClickCount() == 2)
          openSelection(false);
      }
    }
  }

  class VirtualFoldersHandler extends HandlerBase
  {
    VirtualFolderNode parent = null;
    String folderName = null;
    String fileName = null;
    boolean isVisible = false;

    public void startElement(String elname) throws java.lang.Exception
    {
  	  if (parent == null)
        parent = root;
      if (elname.equalsIgnoreCase("folder"))
        parent = createFolder(folderName, false, parent);
	    if (elname.equalsIgnoreCase("file"))
      {
        VirtualFolderNode node = createLeaf(parent, fileName);
        if (isVisible) node.isVisible = isVisible;
        isVisible = false;
      }
    }

    public void endElement(String elname) throws java.lang.Exception
    {
      if (elname.equalsIgnoreCase("folder"))
      {
        if (parent != null)
          parent = (VirtualFolderNode) parent.getParent();
        if (parent == null)
          parent = root;
      }
    }

    public void attribute(String aname, String value, boolean isSpecified)
    {
      if (aname.equalsIgnoreCase("path"))
        fileName = value;
      if (aname.equalsIgnoreCase("name"))
        folderName = value;
      if (aname.equalsIgnoreCase("visible"))
        isVisible = value.equalsIgnoreCase("yes") ? true : false;
    }
  }

  class VirtualFolderNode extends DefaultMutableTreeNode
  {
    private boolean isLeaf;
    private String filePath, label;
    private boolean isVisible = false;

    VirtualFolderNode(String filePath)
    {
      this(filePath, true);
    }

    VirtualFolderNode(String filePath, boolean isLeaf)
    {
      super();
      this.filePath = filePath;
      this.isLeaf = isLeaf;

      int index = filePath.lastIndexOf(java.io.File.separator);
      if (index != -1)
        label = filePath.substring(index + 1);
      else
        label = filePath;
    }

    public void ensureVisible(boolean isVisible)
    {
      this.isVisible = isVisible;
    }

    public boolean shouldBeVisible()
    {
      return isVisible;
    }

    public String getFilePath()
    {
      return filePath;
    }

    public boolean isLeaf()
    {
      return isLeaf;
    }

    public String toString()
    {
      return label;
    }
  }

  class KeyHandler extends KeyAdapter
  {
    public void keyPressed(KeyEvent evt)
    {
      if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        openSelection(false);
    }
  }

  class DnDHandler implements DropTargetListener
  {
    public void dragEnter(DropTargetDragEvent evt)
    { }

    public void dragOver(DropTargetDragEvent evt)
    {
      Point p = evt.getLocation();
      TreePath path = tree.getPathForLocation(p.x, p.y);
      tree.setSelectionPath(path);
      tree.expandPath(path);
    }

    public void dragExit(DropTargetEvent evt) { }
    public void dragScroll(DropTargetDragEvent evt) { }
    public void dropActionChanged(DropTargetDragEvent evt) { }

    public void drop(DropTargetDropEvent evt)
    {
      DataFlavor[] flavors = evt.getCurrentDataFlavors();
      if (flavors == null)
        return;

      boolean dropCompleted = false;
      for (int i = flavors.length - 1; i >= 0; i--)
      {
        if (flavors[i].isFlavorJavaFileListType())
        {
          evt.acceptDrop(DnDConstants.ACTION_COPY);
          Transferable transferable = evt.getTransferable();
          try
          {
            Iterator iterator = ((java.util.List) transferable.getTransferData(flavors[i])).iterator();
            while (iterator.hasNext())
              addFile(((File) iterator.next()).getPath());
            dropCompleted = true;
          } catch (Exception e) { }
        }
      }
      evt.dropComplete(dropCompleted);
    }
  }


}

// End of VirtualFolders.java
