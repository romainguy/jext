/*
 * 03/13/2003 - 17:37:39
 *
 * JextTabbedPane.java - Jext tabbed pane
 * Copyright (C) 2003 Romain Guy
 * romain.guy@jext.org
 * www.jext.org
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

package org.jext;

import java.awt.Point;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Component;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jext.event.JextEvent;
import org.jext.xml.XPopupReader;

/**
 * A tabbed pane which can display indexed titles. As a matter of fact,
 * if a tab is added with a name which already exists, <code>JextTabbedPane</code>
 * will add an index under the form '(x)' at the end of the name. When all tabs
 * of the same name are closed, indexes are counted over from 0.
 * @author Romain Guy
 */

public class JextTabbedPane extends JTabbedPane implements ChangeListener
{
  // static fields
  private static JPopupMenu popupMenu;

  // private
  private JextFrame parent;
  // hold indexes
  private HashMap fileNames = new HashMap();
  // mouse listener
  private PopupMenu _mouseListener;

  // icon of a 'clean' text area
  private static final Icon CLEAN_ICON = Utilities.getIcon("images/tab_clean.gif", Jext.class);
  // icon of a 'dirty' text area
  private static final Icon DIRTY_ICON = Utilities.getIcon("images/tab_dirty.gif", Jext.class);
  
  /**
   * Creates a new tabbed pane and register a change listener
   * which will be used to update Jext infos concerning text
   * areas.
   * @param parent The parent window
   */

  public JextTabbedPane(JextFrame parent)
  {
    super();
    this.parent = parent;
    GUIUtilities.setScrollableTabbedPane(this);
    addMouseListener(_mouseListener = new PopupMenu());
    addChangeListener(this);
  }

  /**
   * Returns JextTabbedPane popup menu. This is needed to update look and feel when
   * user changed it.
   */

  public static JPopupMenu getPopupMenu()
  {
    return popupMenu;
  }

  // handles mouse right clicks to display a popup

  class PopupMenu extends MouseAdapter implements Runnable
  {
    // when we constructs this instance, we also
    // build a new popup menu from an external XML file

    PopupMenu()
    {
      if (popupMenu == null)
      {
        Thread t = new Thread(this);
        t.start();
      }
    }

    public void run()
    {
      popupMenu = XPopupReader.read(Jext.class.getResourceAsStream("jext.tabbedpane.popup.xml"),
                                    "jext.tabbedpane.popup.xml");
      if (Jext.getFlatMenus())
        popupMenu.setBorder(javax.swing.border.LineBorder.createBlackLineBorder());
    }

    // shows popup on right click
    public void mouseReleased(MouseEvent me)
    {
	    showPopupIfNeeded(me);
    }
    
    public void mousePressed(MouseEvent me)
    {
	    showPopupIfNeeded(me);
    }

    private void showPopupIfNeeded(MouseEvent me)
    {
      //if ((me.getModifiers() & me.BUTTON3_MASK) != 0 && popupMenu != null)
      if (me.isPopupTrigger() && popupMenu != null)
      {
        int x = me.getX();
        Dimension parentSize = parent.getSize();
        Point parentLocation = parent.getLocationOnScreen();
        Insets parentInsets  = parent.getInsets();

        Point tapLocation = JextTabbedPane.this.getLocationOnScreen();
        Dimension popupSize  = popupMenu.getSize();

        if ((tapLocation.x + x + popupSize.width) >
            (parentLocation.x + parentSize.width - parentInsets.right))
        {
          x -= popupSize.width;
        }

        popupMenu.show(JextTabbedPane.this, x, me.getY());
      }
    }
  }

  /**
   * When a text area is saved (or simply cleaned in case
   * of open or new), this method is used to display the 'clean'
   * icon in the tab which holds the text area.
   * @param textArea The text area which was cleaned
   */

  public void setCleanIcon(JextTextArea textArea)
  {
    int index = indexOfComponent(textArea);
    if (index == -1)
      return;

    setIconAt(index, CLEAN_ICON);
  }

  /**
   * When a text area is modified this method is used to display
   * the 'dirty' icon in the tab which holds the text area.
   * @param textArea The text area which was modified
   */

  public void setDirtyIcon(JextTextArea textArea)
  {
    int index = indexOfComponent(textArea);
    if (index == -1)
      return;

    setIconAt(index, DIRTY_ICON);
  }

  /**
   * Overrides default <code>addTab(String, Component)</code> method.
   * This method register the <code>title</code> if it hasn't been
   * displayed yet to be able to index next titles of same name. Then,
   * <code>super.addTab(String, Component)</code> is called.
   * @param title Title of the new tab
   * @param component Content of the new tab
   */

  public void addTab(String title, Component component)
  {
    setIndexedTitle(title);
    super.addTab(getIndexedTitle(title), (component instanceof JextTextArea ?
                                          (((JextTextArea) component).isDirty() ? DIRTY_ICON : CLEAN_ICON)
                                          : null),
                 component);
  }

  /**
   * Overrides default <code>removeTabAt(int)</code> method.
   * This method checks if no other tab among the remaining ones
   * has got the same name. In this case, the title is unregistered
   * from the indexed titles list.
   * @param index Removes the tab at <code>index</code>
   */

  public void removeTabAt(int index)
  {
    if (index == -1)
      return;

    removeTitle(index, getComponentAt(index).getName());
    super.removeTabAt(index);

    // if (index == 0)
    stateChanged(new ChangeEvent(this));
  }

  /**
   * Overrides default <code>setTitleAt(int, String)</code> method.
   * Before setting the new title, the new title is registered in
   * the indexed titles list. Then, we call <code>super.setTitleAt(int, String)</code>
   * method but specifying an indexed title.
   * @param index The index of tab to be re-titled
   * @param title The new title
   */

  public void setTitleAt(int index, String title)
  {
    if (index == -1)
      return;

    removeTitle(index, getComponentAt(index).getName());
    setIndexedTitle(title);
    super.setTitleAt(index, getIndexedTitle(title));
  }

  // if specified title is title of another tab, we keep it in the list,
  // otherwise, it is removed

  private void removeTitle(int index, String title)
  {
    String _name;
    boolean more = false;

    for (int i = 0; i < getTabCount(); i++)
    {
      if (i != index && (_name = getComponentAt(i).getName()) != null && _name.equals(title))
      {
        more = true;
        break;
      }
    }

    if (!more)
      fileNames.remove(title);
  }

  // register a title in the list. If it hasn't been registered
  // yet, it is added to list, otherwise, the corresponding index
  // is increased by one.

  private void setIndexedTitle(String title)
  {
    if (title == null)
      title = Jext.getProperty("general.unknown");

    Integer _integer = (Integer) fileNames.get(title);
    if (_integer == null)
    {
      fileNames.put(title, new Integer(0));
    } else {
      fileNames.put(title, new Integer(_integer.intValue() + 1));
    }
  }

  // get a title from the indexed titles list. If the title is found in
  // the list, and if the index is != 0, we add "(index)" at the end of
  // the title.

  private String getIndexedTitle(String title)
  {
    if (title == null)
      return Jext.getProperty("general.unknown");

    int _val;
    Integer _integer = (Integer) fileNames.get(title);

    if (_integer != null && (_val = _integer.intValue()) != 0)
    {
      return (new StringBuffer(title)).append(" (").append(_val).append(')').toString();
    }

    return title;
  }

  /**
   * Selects the next tab in the list. If current selected tab
   * is the last one, then the first one is selected.
   */

  public void nextTab()
  {
    int selectedIndex = getSelectedIndex();
    if (++selectedIndex == getTabCount())
      selectedIndex = 0;
    setSelectedIndex(selectedIndex);
  }

  /**
   * Selects the previous tab in the list. If current selected tab
   * is the first one, then the last one is selected.
   */

  public void previousTab()
  {
    int selectedIndex = getSelectedIndex();
    if (selectedIndex == 0)
      selectedIndex = getTabCount() - 1;
    else
      selectedIndex--;
    setSelectedIndex(selectedIndex);
  }

  /**
   * Removes any installed component from this tabbed pane.
   * Also resets the titles list.
   */

  public void removeAll()
  {
    fileNames.clear();
    super.removeAll();
  }

  /**
   * When a tab is selected, and if content of the tab is a
   * <code>JextTextArea</code>, we update infos displayed by
   * Jext and notify Jext listeners.
   */

  public void stateChanged(ChangeEvent evt)
  {
    Component c = getSelectedComponent();

    if (!(c instanceof JextTextArea))
    {
      if (c != null)
      {
        parent.setTitle("Jext - " + getTitleAt(indexOfComponent(c)) +
                        " [" + parent.getWorkspaces().getName() + ']');
        parent.disableSplittedTextArea();
      }
      return;
    }

    JextTextArea textArea = (JextTextArea) c;

    textArea.setParentTitle();
    parent.updateStatus(textArea);
    parent.setStatus(textArea);
    parent.updateSplittedTextArea(textArea);
    parent.fireJextEvent(JextEvent.TEXT_AREA_SELECTED);

    textArea.grabFocus();
    textArea.requestFocus();
  }
  

}

// End of JextTabbedPane.java