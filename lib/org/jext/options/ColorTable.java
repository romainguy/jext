/*
 * ColorTable.java - Color selection table
 * Copyright (C) 2001 Matt Benson
 * Portions copyright (C) 1999 Slava Pestov
 * Portions copyright (C) 1999 mike dillon
 * Portions copyright (C) 1999-2001 Romain Guy
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

import java.awt.Color;
import java.awt.Component;

import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.JColorChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.border.EmptyBorder;

import org.jext.Jext;
import org.jext.GUIUtilities;
import org.jext.gui.DisabledCellRenderer;


/**
 * This has been extracted from StylesOptions for universal use, especially by
 * colorizing plugins.
 * @author Slava Pestov, Mike Dillon, Romain Guy, Matt Benson
 */
public class ColorTable
 extends JTable
{
	
/**
 * Construct a <CODE>ColorTable</CODE> with an empty model.
 */
	public ColorTable()
	{
		this(new ColorTableModel());
	}//end default constructor
	
	
/**
 * Construct a <CODE>ColorTable</CODE> with the specified model.
 * @param model   the <CODE>ColorTableModel</CODE> to use.
 */
	public ColorTable(ColorTableModel model)
	{
		super(model);
    getTableHeader().setReorderingAllowed(false);
    getSelectionModel().addListSelectionListener(new ListHandler());
    getColumnModel().getColumn(1).setCellRenderer(new ColorTableModel.ColorRenderer());
    getColumnModel().getColumn(0).setCellRenderer(new DisabledCellRenderer());
	}//end constructor(ColorTableModel)

  
  private class ListHandler
	 implements ListSelectionListener
  {

    public void valueChanged(ListSelectionEvent evt)
    {
      if (evt.getValueIsAdjusting())
        return;
			Color color = JColorChooser.showDialog(ColorTable.this,
							Jext.getProperty("colorChooser.title"),
							(Color) (dataModel.getValueAt(getSelectedRow(), 1)));
			if (color != null)
				dataModel.setValueAt(color, getSelectedRow(), 1);
    }
  }

	
/**
 * <CODE>TableModel</CODE> for the <CODE>ColorTable</CODE>.
 */
	public static class ColorTableModel
	 extends AbstractTableModel
	{
		private ArrayList colorChoices;
	
/**
 * Construct an empty <CODE>ColorTableModel</CODE>.
 */
		public ColorTableModel()
		{
			colorChoices = new ArrayList(24);
		}//end default constructor
		

/**
 * Construct a <CODE>ColorTableModel</CODE> and initialize it with the contents
 * of the specified <CODE>Map</CODE>.
 * @param choices   the <CODE>Map</CODE> containing the initial choices for this
 *                  <CODE>ColorTableModel</CODE>.  This should be a map of
 *                  <CODE>String</CODE> to <CODE>String</CODE>, so it would be
 *                  sensible to use a <CODE>Properties</CODE> object, but this
 *                  is not enforced beyond that the <CODE>String</CODE> forms of
 *                  the keys and values in the <CODE>Map</CODE> will be used.
 *                  Each map entry will be added to the
 *                  <CODE>ColorTableModel</CODE> as if by
 *                  <code>addColorChoice(String, String)</code>.
 * @see addColorChoice(String, String)
 */
		public ColorTableModel(Map choices)
		{
			this();
			Iterator it = choices.entrySet().iterator();
			Map.Entry entry = null;
			while (it.hasNext())
			{
				entry = (Map.Entry)(it.next());
				addColorChoice(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
			}//end while more keys
		}//end constructor(Map)
		
/**
 * @see TableModel#getColumnCount()
 */	
		public int getColumnCount()
		{
			return 2;
		}//end getColumnCount
	
/**
 * @see TableModel#getRowCount()
 */
		public int getRowCount()
		{
			return colorChoices.size();
		}//end getRowCount
	
/**
 * @see TableModel#getValueAt(int, int)
 */
		public Object getValueAt(int row, int col)
		{
			ColorChoice ch = (ColorChoice) colorChoices.get(row);
			switch (col)
			{
				case 0:
					return ch.label;
				case 1:
					return ch.color;
				default:
					return null;
			}//end switch on column number
		}//end getValueAt
	
/**
 * @see TableModel#setValueAt(Object, int, int)
 */
		public void setValueAt(Object value, int row, int col)
		{
			ColorChoice ch = (ColorChoice) colorChoices.get(row);
			if (col == 1)
				ch.color = (Color) value;
			fireTableRowsUpdated(row, row);
		}//end setValueAt
	
/**
 * @see TableModel#getColumnName(int)
 */
		public String getColumnName(int index)
		{
			switch (index)
			{
				case 0:
					return Jext.getProperty("options.styles.object");
				case 1:
					return Jext.getProperty("options.styles.color");
				default:
					return null;
			}//end switch on index
		}//end getColumnName
	
	
/**
 * Save the contents of this <CODE>ColorTableModel</CODE> to Jext properties.
 */
		public void save()
		{
			for (int i = 0; i < colorChoices.size(); i++)
			{
				ColorChoice ch = (ColorChoice) colorChoices.get(i);
				Jext.setProperty(ch.property, GUIUtilities.getColorHexString(ch.color));
			}//end for through color choices
		}//end save
/**
 * Reload all the colors of this <CODE>ColorTableModel</CODE> from Jext properties.
 * Added for the Option Dialog caching.
 */    
    public void load()
    {
			for (int i = 0; i < colorChoices.size(); i++)
				( (ColorChoice) colorChoices.get(i) ).resetColor();
      fireTableRowsUpdated(0, colorChoices.size() - 1);
    }//end load
	
/**
 * Add the specified color choice to this <CODE>ColorTableModel</CODE>.
 * @param label      <CODE>String</CODE> property name for the description.
 * @param property   <CODE>String</CODE> property name for the color.
 */
		public void addColorChoice(String label, String property)
		{
			colorChoices.add(new ColorChoice(Jext.getProperty(label), property));
							
		}//end addColorChoice
	
		private static class ColorChoice
		{
			String label;
			String property;
			Color color;
	
			ColorChoice(String label, String property/*, Color color*/)
			{
				this.label = label;
				this.property = property;
				this.color = GUIUtilities.parseColor(Jext.getProperty(property));
			}//end constructor
      
      public void resetColor()
      {
				this.color = GUIUtilities.parseColor(Jext.getProperty(property));
      }
		}//end class ColorChoice
	
		private static class ColorRenderer
		 extends JLabel
		 implements TableCellRenderer
		{
			public ColorRenderer()
			{
				setOpaque(true);
				setBorder(StylesOptions.noFocusBorder);
			}//end constructor
	
			// TableCellRenderer implementation
			public Component getTableCellRendererComponent(JTable table,
							Object value, boolean isSelected, boolean cellHasFocus,
							int row, int col)
			{
				if (isSelected)
				{
					setBackground(table.getSelectionBackground());
					setForeground(table.getSelectionForeground());
				}//end if selected
				else
				{
					setBackground(table.getBackground());
					setForeground(table.getForeground());
				}//end else, not selected
	
				if (value != null)
					setBackground((Color) value);
	
				setBorder((cellHasFocus) ?
								UIManager.getBorder("Table.focusCellHighlightBorder") :
								StylesOptions.noFocusBorder);
				return this;
			}//end getTableCellRendererComponent
		}//end class ColorRenderer
	}//end class ColorTableModel
}//end class ColorTable
