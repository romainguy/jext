/*
 * 10/23/2001 - 21:17:27
 *
 * OptionsDialog.java - Global options dialog
 * Copyright (C) 1998, 1999, 2000 Slava Pestov
 * Portions copyright (C) 1999 mike dillon
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

package org.jext.options;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.jext.*;
import org.jext.gui.*;

public class OptionsDialog extends JDialog implements ActionListener, TreeSelectionListener
{
  private JTree paneTree;
  //private Hashtable panes;//not any more needed
  private JPanel cardPanel;
  private JLabel currentLabel;
  private JextHighlightButton ok, cancel, apply;
  private OptionGroup jextGroup, pluginsGroup;

  private static OptionsDialog theInstance;
  private OptionTreeModel theTree;
  private boolean toReload = false, //if the user clicks cancel, options must be reloaded
                  isLoadingPlugs, isLoadingCore;
                  //when it's building the dialog the first time, it must now use it to
                  //select plugins which support the re-load()'ing of options.
  private String currPaneName;
  private Plugin currPlugin; //the plugin it's currently loading.
  private ArrayList cachPlugPanes,
          notCachPlugPanes, notCachPlugin;
  private JextFrame parent;//need to know this to show wait cursor after first load; commented out since it doesn't work
  //anyway
  
  //for UIOptions
  static OptionsDialog getInstance() {
    return theInstance;
  }

  /**Call this to show the dialog; every other method should not be called, except
   * (very rarely, however) by Jext kernel itself, with the only exceptions of
   * {@link #addOptionPane(OptionPane) addOptionPane} and
   * {@link #addOptionGroup(OptionGroup) addOptionGroup} methods.
   */
  public static void showOptionDialog(JextFrame parent)
  {
    if (theInstance == null)
      theInstance = new OptionsDialog(parent);
    else
      theInstance.reload();
    theInstance.setVisible(true);
  }

  private OptionsDialog(JextFrame _parent)
  {
    super(_parent, Jext.getProperty("options.title"), true);
    parent = _parent;

    parent.showWaitCursor();
    cachPlugPanes = new ArrayList(20);//number of elements. It should be more than needed one.
    notCachPlugPanes = new ArrayList(20);
    notCachPlugin = new ArrayList(20);
    getContentPane().setLayout(new BorderLayout());
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

    JPanel stage = new JPanel(new BorderLayout(4, 8));
    stage.setBorder(//BorderFactory.createCompoundBorder(
                    //new SoftBevelBorder(SoftBevelBorder.RAISED),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4));
    //new EtchedBorder(EtchedBorder.RAISED));
    getContentPane().add(stage, BorderLayout.CENTER);

    // currentLabel displays the path of the currently selected
    // OptionPane at the top of the stage area
    currentLabel = new JLabel();
    currentLabel.setHorizontalAlignment(JLabel.LEFT);
    currentLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
    stage.add(currentLabel, BorderLayout.NORTH);

    cardPanel = new JPanel(new CardLayout());
    stage.add(cardPanel, BorderLayout.CENTER);

    paneTree = new JTree(theTree = createOptionTreeModel());
    paneTree.setCellRenderer(new PaneNameRenderer());
    paneTree.putClientProperty("JTree.lineStyle", "Angled");
    paneTree.setShowsRootHandles(true);
    paneTree.setRootVisible(false);
    getContentPane().add(new JScrollPane(paneTree,
                         JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                         BorderLayout.WEST);

    JPanel buttons = new JPanel();

    ok = new JextHighlightButton(Jext.getProperty("options.set.button"));
    ok.setMnemonic(Jext.getProperty("options.set.mnemonic").charAt(0));
    ok.addActionListener(this);
    buttons.add(ok);
    getRootPane().setDefaultButton(ok);

    cancel = new JextHighlightButton(Jext.getProperty("general.cancel.button"));
    cancel.setMnemonic(Jext.getProperty("general.cancel.mnemonic").charAt(0));
    cancel.addActionListener(this);
    buttons.add(cancel);

    apply = new JextHighlightButton(Jext.getProperty("options.apply.button"));
    apply.setMnemonic(Jext.getProperty("options.apply.mnemonic").charAt(0));
    apply.addActionListener(this);
    buttons.add(apply);

    getContentPane().add(buttons, BorderLayout.SOUTH);

    addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent evt)
      {
        switch (evt.getKeyCode())
        {
          case KeyEvent.VK_ENTER:
            ok();
            break;
          case KeyEvent.VK_ESCAPE:
            cancel();
            break;
        }
      }
    });

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        cancel();
      }
    });

    // compute the Jext branch
    TreePath jextPath = new TreePath(new Object[] { theTree.getRoot(), jextGroup , jextGroup.getMember(0) });

    // register the Options dialog as a TreeSelectionListener.
    // this is done before the initial selection to ensure that the
    // first selected OptionPane is displayed on startup.
    paneTree.getSelectionModel().addTreeSelectionListener(this);

    // select the first member of the Jext group
    paneTree.setSelectionPath(jextPath);

    // register the MouseHandler to open and close branches
    paneTree.addMouseListener(new MouseHandler());

    pack();
    Utilities.centerComponent(this);
    parent.hideWaitCursor();
  }

  private void ok(boolean close)
  {
    OptionTreeModel m = (OptionTreeModel) paneTree.getModel();
    ((OptionGroup) m.getRoot()).save();

    Jext.propertiesChanged();
    if (close) setVisible(false);
  }

  private void ok()
  {
    ok(true);
  }

  private void cancel()
  {
    toReload = true;
    setVisible(false);
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object source = evt.getSource();

    if (source == ok)
    {
      ok();
    } else if(source == cancel) {
      cancel();
    } else if(source == apply) {
      ok(false);
    }
  }

  private void reload()
  {
    if (toReload) {
      parent.showWaitCursor();
      reloadStdPanes();
      reloadPluginPanes();
      toReload = false;
      parent.hideWaitCursor();
    }
  }

  private void reloadStdPanes()
  {
    ArrayList stdPanes = jextGroup.getMembers();
    for (int i = 0; i < stdPanes.size(); i++ )
      ((AbstractOptionPane) stdPanes.get(i)).load();
  }

  private void reloadPluginPanes()
  {
    ((CardLayout) cardPanel.getLayout()).show(cardPanel, ((OptionPane) (jextGroup.getMember(0))).getName());
    for (Iterator i = cachPlugPanes.iterator(); i.hasNext(); )
    {
      OptionPane op = null;
      try {
        ( op = (OptionPane) (i.next()) ).load();
      } catch(AbstractMethodError ame) {//This is when a plugin does not extends
        //AbstractOptionPane but implements directly the interface OptionPane, which has now new
        //methods
        ame.printStackTrace();
        Utilities.showError("The option pane of the plugin containing " + op.getClass().toString() +
        " is not supported, and you will not see it in the option dialog. This is related to new Jext " +
        "release(from 3.2pre3). You should make aware of this Romain Guy, the plugin's author or " +
        "Blaisorblade <blaisorblade_work (at) yahoo.it, who will provide an upgraded version " +
        "of the plugin.Thanks");
        //I hope this will never happen, but it has happened with the Java plugin(JBrowse option pane).
      } catch(Throwable t)  {
        t.printStackTrace();
      }
    }
    for (Iterator i = notCachPlugPanes.iterator(); i.hasNext(); )
      cardPanel.remove(( (OptionPane) i.next()).getComponent());
    for (Iterator i = notCachPlugin.iterator(); i.hasNext(); )
    {
      Plugin plug = null;
      try {
	( plug = (Plugin) (i.next()) ).createOptionPanes(this);
      } catch(AbstractMethodError ame) {//This is when a plugin does not extends
        //AbstractOptionPane but implements directly the interface OptionPane, which has now new
        //methods
        ame.printStackTrace();
        Utilities.showError("The option pane of the plugin containing " + plug.getClass().toString() +
        " is not supported, and you will not see it in the option dialog. This is related to new Jext " +
        "release(from 3.2pre3). You should make aware of this Romain Guy, the plugin's author or " +
        "Blaisorblade <blaisorblade_work (at) yahoo.it, who will provide an upgraded version " +
        "of the plugin.Thanks");
        //I hope this will never happen, but it has happened with the Java plugin(JBrowse option pane).
      } catch(Throwable t)  {
        t.printStackTrace();
      }
    }
 
    ((CardLayout) cardPanel.getLayout()).show(cardPanel, currPaneName);
  }

  /**Use this method or addOptionPane to add your option pane to Jext. You must use this one
   * and not anything else! See Jext Docs(the Plugin section is very good).
   * If you use OptionGroup.addOptionPane after adding the pane, it is a bug.
   * The pane must be added both to the tree and to a CardLayout to show it.
   * Also, it must be managed to be eventually cached.*/
  public void addOptionGroup(OptionGroup group)
  {
    addOptionGroup(group, pluginsGroup);
  }

  /**Use this method or addOptionGroup to add your option pane to Jext. You must use this one
   * and not anything else! See Jext Docs(the Plugin section is very good).
   * If you use OptionGroup.addOptionPane after adding the pane, it is a bug.
   * The pane must be added both to the tree and to a CardLayout to show it.
   * Also, it must be managed to be eventually cached.*/
  public void addOptionPane(OptionPane pane)
  {
    addOptionPane(pane, pluginsGroup);
  }

  private void addOptionGroup(OptionGroup child, OptionGroup parent)
  {
    ArrayList enum = child.getMembers();

    for (int i = 0; i < enum.size(); i++)
    {
      Object elem = enum.get(i);

      if (elem instanceof OptionPane)
      {
        addOptionPane((OptionPane) elem, child);
      } else if (elem instanceof OptionGroup) {
        addOptionGroup((OptionGroup) elem, child);
      }
    }

    parent.addOptionGroup(child);
  }

  private void addOptionPane(OptionPane pane, OptionGroup parent)
  {
    String name = pane.getName();
    cardPanel.add(pane.getComponent(), name);
    if (isLoadingPlugs || isLoadingCore)
      parent.addOptionPane(pane);
    //Let's trace reloadable panes and the ones I must rebuild.
    if (isLoadingPlugs) {
      if (pane.isCacheable() == true)
        cachPlugPanes.add(pane);
      else {
        notCachPlugPanes.add(pane);
        if (currPlugin != null)
        {
          notCachPlugin.add(currPlugin);//a value is given to currPlugin in createOptionTreeModel()
          currPlugin = null;//so every plugin is added to notCachPlugin only one time.
          //NOTE: when a single plugin has both reloadable and not reloadable plugin panes, all this code is likely to be
          //buggy.
        }
      }
    }
  }

  private OptionTreeModel createOptionTreeModel()
  {
    OptionTreeModel paneTreeModel = new OptionTreeModel();
    OptionGroup rootGroup = (OptionGroup) paneTreeModel.getRoot();

    //Either isLoadingCore or isLoadingPlugs must be true in order to make addOptionPane work.
    isLoadingCore = true;

    jextGroup = new OptionGroup("jext");

    addOptionPane(new GeneralOptions(),      jextGroup);
    addOptionPane(new LoadingOptions(),      jextGroup);
    addOptionPane(new UIOptions(),           jextGroup);
    addOptionPane(new EditorOptions(),       jextGroup);
    addOptionPane(new PrintOptions(),        jextGroup);
    addOptionPane(new GutterOptions(),       jextGroup);
    addOptionPane(new StylesOptions(),       jextGroup);
    //addOptionPane(new KeywordsOptions(),     jextGroup);
    addOptionPane(new KeyShortcutsOptions(), jextGroup);
    addOptionPane(new FileFiltersOptions(),  jextGroup);
    addOptionPane(new LangOptions(),         jextGroup);
    addOptionPane(new SecurityOptions(),     jextGroup);

    addOptionGroup(jextGroup, rootGroup);

    isLoadingCore = false;

    // initialize the Plugins branch of the options tree
    pluginsGroup = new OptionGroup("plugins");

    // Query plugins for option panes
    Plugin[] plugins = Jext.getPlugins();
    isLoadingPlugs = true;//so the added panes are tracked to know what ones must be rebuilt and what ones must be reloaded.
    for(int i = 0; i < plugins.length; i++)
    {
      currPlugin = plugins[i];
      try {
        currPlugin.createOptionPanes(this);
      } catch(AbstractMethodError ame) {//This is when a plugin does not extends
        //AbstractOptionPane but implements directly the interface OptionPane, which has now new
        //methods
        ame.printStackTrace();
        Utilities.showError("The option pane of the plugin containing " + plugins[i].getClass().toString() +
        " is not supported, and you will not see it in the option dialog. This is related to new Jext " +
        "release(from 3.2pre3). You should make aware of this Romain Guy, the plugin's author or " +
        "Blaisorblade <blaisorblade_work (at) yahoo.it, who will provide an upgraded version " +
        "of the plugin.Thanks");
        //I hope this will never happen, but it has happened with the Java plugin(JBrowse option pane).
      } catch(Throwable t)  {
        t.printStackTrace();
      }
    }
    isLoadingPlugs = false;

    // only add the Plugins branch if there are OptionPanes
    if (pluginsGroup.getMemberCount() > 0)
    {
      addOptionGroup(pluginsGroup, rootGroup);
    }

    return paneTreeModel;
  }

  public void valueChanged(TreeSelectionEvent evt)
  {
    TreePath path = evt.getPath();

    if (path == null || !(path.getLastPathComponent() instanceof OptionPane))
      return;

    Object[] nodes = path.getPath();
    StringBuffer buf = new StringBuffer();
    currPaneName = null;
    int lastIdx = nodes.length - 1;

    for (int i = paneTree.isRootVisible() ? 0 : 1; i <= lastIdx; i++)
    {
      if (nodes[i] instanceof OptionPane)
      {
        currPaneName = ((OptionPane)nodes[i]).getName();
      } else if (nodes[i] instanceof OptionGroup) {
        currPaneName = ((OptionGroup)nodes[i]).getName();
      } else {
        continue;
      }

      if (currPaneName != null)
      {
        String label = Jext.getProperty("options." + currPaneName + ".label");

        if (label == null)
        {
          buf.append(currPaneName);
        } else {
          buf.append(label);
        }
      }

      if (i != lastIdx) buf.append(": ");
    }

    currentLabel.setText(buf.toString());
    ((CardLayout) cardPanel.getLayout()).show(cardPanel, currPaneName);
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent evt)
    {
      TreePath path = paneTree.getPathForLocation(evt.getX(), evt.getY());

      if (path == null) return;

      Object node = path.getLastPathComponent();

      if (node instanceof OptionGroup)
      {
        if (paneTree.isCollapsed(path))
        {
          paneTree.expandPath(path);
        } else {
          paneTree.collapsePath(path);
        }
      }
    }
  }

  class PaneNameRenderer extends JLabel implements TreeCellRenderer
  {
    private Border noFocusBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    private Border focusBorder = BorderFactory.createLineBorder(UIManager.getColor("Tree.selectionBorderColor"));

    private Font paneFont;
    private Font groupFont;

    public PaneNameRenderer()
    {
      setOpaque(true);

      paneFont = UIManager.getFont("Tree.font");
      groupFont = new Font(paneFont.getName(), paneFont.getStyle() | Font.BOLD,
                           paneFont.getSize());
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus)
    {
      if (selected)
      {
        this.setBackground(UIManager.getColor("Tree.selectionBackground"));
        this.setForeground(UIManager.getColor("Tree.selectionForeground"));
      } else {
        this.setBackground(tree.getBackground());
        this.setForeground(tree.getForeground());
      }

      String name = null;

      if (value instanceof OptionGroup)
      {
        name = ((OptionGroup) value).getName();
        this.setFont(groupFont);
      } else if (value instanceof OptionPane) {
        name = ((OptionPane) value).getName();
        this.setFont(paneFont);
      }

      if (name == null)
      {
        setText(null);
      } else {

        String label = Jext.getProperty("options." + name + ".label");

        if (label == null)
        {
          setText(name);
        } else {
          setText(label);
        }
      }

      setBorder(hasFocus ? focusBorder : noFocusBorder);
      return this;
    }
  }

  class OptionTreeModel implements TreeModel
  {
    private OptionGroup root = new OptionGroup("root");
    private EventListenerList listenerList = new EventListenerList();

    public void addTreeModelListener(TreeModelListener l)
    {
      listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
      listenerList.remove(TreeModelListener.class, l);
    }

    public Object getChild(Object parent, int index)
    {
      if (parent instanceof OptionGroup)
      {
        return ((OptionGroup)parent).getMember(index);
      } else {
        return null;
      }
    }

    public int getChildCount(Object parent)
    {
      if (parent instanceof OptionGroup)
      {
        return ((OptionGroup)parent).getMemberCount();
      } else {
        return 0;
      }
    }

    public int getIndexOfChild(Object parent, Object child)
    {
      if (parent instanceof OptionGroup)
      {
        return ((OptionGroup) parent).getMemberIndex(child);
      } else {
        return -1;
      }
    }

    public Object getRoot()
    {
      return root;
    }

    public boolean isLeaf(Object node)
    {
      return node instanceof OptionPane;
    }

    public void valueForPathChanged(TreePath path, Object newValue)
    {
      // this model may not be changed by the TableCellEditor
    }

    protected void fireNodesChanged(Object source, Object[] path,
                                    int[] childIndices, Object[] children)
    {
      Object[] listeners = listenerList.getListenerList();

      TreeModelEvent modelEvent = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
        if (listeners[i] != TreeModelListener.class)
          continue;

        if (modelEvent == null)
        {
          modelEvent = new TreeModelEvent(source, path, childIndices, children);
        }

        ((TreeModelListener) listeners[i + 1]).treeNodesChanged(modelEvent);
      }
    }

    protected void fireNodesInserted(Object source, Object[] path,
                                     int[] childIndices, Object[] children)
    {
      Object[] listeners = listenerList.getListenerList();

      TreeModelEvent modelEvent = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
        if (listeners[i] != TreeModelListener.class)
          continue;

        if (modelEvent == null)
        {
          modelEvent = new TreeModelEvent(source, path, childIndices, children);
        }

        ((TreeModelListener)listeners[i + 1]).treeNodesInserted(modelEvent);
      }
    }

    protected void fireNodesRemoved(Object source, Object[] path,
                                    int[] childIndices, Object[] children)
    {
      Object[] listeners = listenerList.getListenerList();

      TreeModelEvent modelEvent = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
        if (listeners[i] != TreeModelListener.class)
          continue;

        if (modelEvent == null)
        {
          modelEvent = new TreeModelEvent(source, path, childIndices, children);
        }

        ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(modelEvent);
      }
    }

    protected void fireTreeStructureChanged(Object source, Object[] path,
                                            int[] childIndices, Object[] children)
    {
      Object[] listeners = listenerList.getListenerList();

      TreeModelEvent modelEvent = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
        if (listeners[i] != TreeModelListener.class)
          continue;

        if (modelEvent == null)
        {
          modelEvent = new TreeModelEvent(source, path, childIndices, children);
        }

        ((TreeModelListener) listeners[i + 1]).treeStructureChanged(modelEvent);
      }
    }
  }


}

// End of OptionsDialog.java