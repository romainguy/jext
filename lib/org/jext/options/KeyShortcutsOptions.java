/*
 * 04/24/2002 - 20:05:42
 *
 * KeyShortcutsOptions.java - The keys options pane
 * Copyright (C) 2001 Romain Guy
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

package org.jext.options;

import java.awt.*;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import org.jext.*;
import org.jext.gui.*;
import org.gjt.sp.jedit.textarea.DefaultInputHandler;

public class KeyShortcutsOptions extends AbstractOptionPane
{
  private JTable table;
  private String[] actions, labels, _keys;
  private KeysTableModel theTableModel;
  
  public KeyShortcutsOptions()
  {
    super("keyShortcuts");

    actions = new String[GUIUtilities.menuItemsActions.size()];
    labels = new String[actions.length];
    _keys = new String[actions.length];

    Enumeration e = GUIUtilities.menuItemsActions.keys();
    for (int i = 0; e.hasMoreElements(); i++)
      actions[i] = e.nextElement().toString();
    e = null;    

    Hashtable h = GUIUtilities.menuItemsActions;
    for (int i = 0; i < actions.length; i++)
      labels[i] = h.get(actions[i]).toString();
    h = null;

    sortStrings(labels, actions);

    setLayout(new GridLayout(1, 1));
    JPanel pane = new JPanel(new BorderLayout());
    pane.add(BorderLayout.NORTH, new JLabel(Jext.getProperty("options.keyShortcuts.title")));
    pane.add(BorderLayout.CENTER, createTableScroller());
    add(pane);
  }

  public void load()
  {
    theTableModel.load();
  }

  public void save()
  {
    String key;

    for (int i = 0; i < actions.length; i++)
    {
      key = _keys[i];
      if (key != null && key.length() != 0)
        Jext.setProperty(actions[i].toString().concat(".shortcut"), key);
    }
  }

  private JScrollPane createTableScroller()
  {
    table = new JTable(theTableModel = new KeysTableModel());
    table.getTableHeader().setReorderingAllowed(false);
    table.setCellSelectionEnabled(false);
    table.getColumnModel().getColumn(0).setCellRenderer(new DisabledCellRenderer());
    Dimension _dim = table.getPreferredSize();
    JScrollPane scroller = new JScrollPane(table);
    scroller.setPreferredSize(new Dimension((int) _dim.width, 250));
    return scroller;
  }

  /**
   * Quick sort an array of Strings.
   * @param string Strings to be sorted
   */

  public static void sortStrings(String[] strings, String[] aStrings)
  {
    sortStrings(strings, aStrings, 0, strings.length - 1);
  }

  /**
   * Quick sort an array of Strings.
   * @param a Strings to be sorted
   * @param lo0 Lower bound
   * @param hi0 Higher bound
   */

  public static void sortStrings(String a[], String b[], int lo0, int hi0)
  {
    int lo = lo0;
    int hi = hi0;
    String mid;

    if (hi0 > lo0)
    {
      mid = a[(lo0 + hi0) / 2];

      while (lo <= hi)
      {
        while (lo < hi0 && a[lo].compareTo(mid) < 0)
          ++lo;

        while (hi > lo0 && a[hi].compareTo(mid) > 0)
          --hi;

        if (lo <= hi)
        {
          swap(a, lo, hi);
          swap(b, lo, hi);
          ++lo;
          --hi;
        }
      }

      if (lo0 < hi)
        sortStrings(a, b, lo0, hi);

      if (lo < hi0)
        sortStrings(a, b, lo, hi0);
    }
  }

  /**
   * Swaps two Strings.
   * @param a The array to be swapped
   * @param i First String index
   * @param j Second String index
   */

  public static void swap(String a[], int i, int j)
  {
    String T;
    T = a[i];
    a[i] = a[j];
    a[j] = T;
  }

  class KeysTableModel extends AbstractTableModel
  {
    KeysTableModel()
    {
      load();
    }

    void load()
    {
      String key;
      for (int i = 0 ; i < actions.length; i++)
      {
        key = actions[i].toString();
        if (key != null)
          _keys[i] = Jext.getProperty(key.concat(".shortcut"));
      }
    }
    
    public int getColumnCount()
    {
      return 2;
    }

    public int getRowCount()
    {
      return _keys.length;
    }

    public Object getValueAt(int row, int col)
    {
      Object[] _v = null;
      if (col == 0)
        _v = labels;
      else if (col == 1)
        _v = _keys;

      if (_v == null)
        return null;

      return _v[row];
    }

    public boolean isCellEditable(int row, int col)
    {
      return (col == 1);
    }

    public String getColumnName(int index)
    {
      switch(index)
      {
        case 0:
          return Jext.getProperty("options.keyShortcuts.menu");
        case 1:
          return Jext.getProperty("options.keyShortcuts.keys");
        default:
          return null;
      }
    }

    public void setValueAt(Object value, int row, int col)
    {
      String val = value.toString();
      if (val.trim().length() == 0)
      {
        Jext.unsetProperty(actions[row].toString().concat(".shortcut"));
        _keys[row] = "";
        return;
      }

      boolean isValid = false;
      StringTokenizer st = new StringTokenizer(val);
      while (st.hasMoreTokens())
      {
        isValid = (DefaultInputHandler.parseKeyStroke(st.nextToken()) != null);
      }

      if (value == null || val.length() == 0 || isValid)
      {
        boolean found = false;
        int i = 0;

        if (val.length() != 0)
        {
          for ( ; i < _keys.length; i++)
          {
            if (val.equals(_keys[i]))
            {
              found = true;
              break;
            }
          }
        }

        if (!found || (found && row == i))
        {
          _keys[row] = val;
        } else {
          Utilities.showError(Jext.getProperty("options.keyShortcuts.errorMessage2"));
        }
      } else {
        Utilities.showError(Jext.getProperty("options.keyShortcuts.errorMessage"));
      }
    }
  }
  

}

// End of KeyShortcutsOptions.java