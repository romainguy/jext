/*
 * 14:11:22 16/02/00
 *
 * FileFiltersOptions.java - The file filters options pane
 * Copyright (C) 1999 Romain Guy
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

import java.util.*;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import org.jext.*;
import org.jext.gui.*;

public class FileFiltersOptions extends AbstractOptionPane
{
  private JTable filtersTable;
  private ArrayList filters = new ArrayList(Jext.modes.size());
  private FiltersTableModel theTableModel;
  
  public FileFiltersOptions()
  {
    super("fileFilters");

    setLayout(new GridLayout(1, 1));
    JPanel pane = new JPanel(new BorderLayout());
    pane.add(BorderLayout.NORTH, new JLabel(Jext.getProperty("options.fileFilters.title")));
    pane.add(BorderLayout.CENTER, createTableScroller());
    add(pane);
  }

  public void save()
  {
    for (int i = 0; i < filters.size(); i++)
    {
      FileFilter filter = (FileFilter) filters.get(i);
      Jext.setProperty("mode." + filter.getMode() + ".fileFilter", filter.getFilter());
    }
  }
  public void load()
  {
    theTableModel.reload();
  }

  private JScrollPane createTableScroller()
  {
    filtersTable = new JTable(theTableModel = new FiltersTableModel());
    filtersTable.getTableHeader().setReorderingAllowed(false);
    filtersTable.setCellSelectionEnabled(false);
    filtersTable.getColumnModel().getColumn(0).setCellRenderer(new DisabledCellRenderer());
    Dimension _dim = filtersTable.getPreferredSize();
    JScrollPane scroller = new JScrollPane(filtersTable);
    scroller.setPreferredSize(new Dimension((int) _dim.width, 250));
    return scroller;
  }

  class FiltersTableModel extends AbstractTableModel
  {
    FiltersTableModel()
    {
      String name;
      ArrayList modes = Jext.modes;
      for (int i = 0; i < modes.size(); i++)
      {
        Mode mode = (Mode) modes.get(i);
        name = mode.getModeName();
        if (!name.equals("plain"))
          filters.add(new FileFilter(name, mode.getUserModeName(),
                                     Jext.getProperty("mode." + name + ".fileFilter")));
      }
    }
    
    void reload()
    {
      ArrayList modes = Jext.modes;
      int displacement = 0;
      for (int row = 0; row < modes.size(); row++)
      {
        String name = ((Mode) modes.get(row)).getModeName();
        if (!name.equals("plain"))
          ((FileFilter) filters.get(row - displacement)).setFilter(Jext.getProperty("mode." + name + ".fileFilter"));
        else
          displacement = 1;
          //Evil hack, I know. Don't blame me. The plain mode is not inserted as row, so when we meet it, all next rows have
          //an index littler by 1. So displacement is 1. Very evil, I know. But so goes the world.
      }
    }

    public int getColumnCount()
    {
      return 2;
    }

    public int getRowCount()
    {
      return filters.size();
    }

    public Object getValueAt(int row, int col)
    {
      FileFilter _filter = (FileFilter) filters.get(row);
      return (col == 0 ? _filter.getName() : _filter.getFilter());
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
          return Jext.getProperty("options.fileFilters.modeName");
        case 1:
          return Jext.getProperty("options.fileFilters.filter");
        default:
          return null;
      }
    }

    public void setValueAt(Object value, int row, int col)
    {
      ((FileFilter) filters.get(row)).setFilter((String) value);
    }
  }

  class FileFilter
  {
    private String _mode, _name, _filter;

    FileFilter(String mode, String name, String filter)
    {
      _mode = mode;
      _name = name;
      _filter = filter;
    }

    public String getMode()
    {
      return _mode;
    }

    public String getName()
    {
      return _name;
    }

    public String getFilter()
    {
      return _filter;
    }

    public void setFilter(String filter)
    {
      _filter = filter;
    }
  }


}

// End of FileFiltersOptions.java