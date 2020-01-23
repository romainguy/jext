/*
 * StyleTable.java - Color/style option pane
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
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.border.EmptyBorder;

import org.jext.Jext;
import org.jext.Utilities;
import org.jext.GUIUtilities;
import org.jext.gui.JextCheckBox;
import org.jext.gui.JextHighlightButton;
import org.jext.gui.DisabledCellRenderer;
import org.gjt.sp.jedit.syntax.SyntaxStyle;

/**
 * This has been extracted from StylesOptions for universal use.
 * @author Slava Pestov, Mike Dillon, Romain Guy, Matt Benson
 */
public class StyleTable
 extends JTable
{
	
	public StyleTable()
	{
		this(new StyleTableModel());
	}//end default constructor
	
	public StyleTable(StyleTableModel model)
	{
		super(model);
    getTableHeader().setReorderingAllowed(false);
    getSelectionModel().addListSelectionListener(new ListHandler());
    getColumnModel().getColumn(1).setCellRenderer(new StyleTableModel.StyleRenderer());
    getColumnModel().getColumn(0).setCellRenderer(new DisabledCellRenderer());
	}//end constructor(StyleTableModel)
	
  private class ListHandler
	 implements ListSelectionListener
  {
    public void valueChanged(ListSelectionEvent evt)
    {
      if (evt.getValueIsAdjusting())
        return;
			SyntaxStyle style = new StyleEditor(StyleTable.this,
			 (SyntaxStyle) dataModel.getValueAt(getSelectedRow(), 1)).getStyle();
			if (style != null)
				dataModel.setValueAt(style, getSelectedRow(), 1);
    }//end valueChanged
  }//end class ListHandler

/**
 * <CODE>TableModel</CODE> for the <CODE>StyleTable</CODE>.
 */
	public static class StyleTableModel
	 extends AbstractTableModel
	{
		private ArrayList styleChoices;
	
		public StyleTableModel()
		{
			styleChoices = new ArrayList(10);
		}//end default constructor
		
/**
 * Construct a <CODE>StyleTableModel</CODE> and initialize it with the contents
 * of the specified <CODE>Map</CODE>.
 * @param choices   the <CODE>Map</CODE> containing the initial choices for this
 *                  <CODE>StyleTableModel</CODE>.  This should be a map of
 *                  <CODE>String</CODE> to <CODE>String</CODE>, so it would be
 *                  sensible to use a <CODE>Properties</CODE> object, but this
 *                  is not enforced beyond that the <CODE>String</CODE> forms of
 *                  the keys and values in the <CODE>Map</CODE> will be used.
 *                  Each map entry will be added to the
 *                  <CODE>StyleTableModel</CODE> as if by
 *                  <code>addStyleChoice(String, String)</code>.
 * @see addStyleChoice(String, String)
 */
		public StyleTableModel(Map choices)
		{
			this();
			Iterator it = choices.entrySet().iterator();
			Map.Entry entry = null;
			while (it.hasNext())
			{
				entry = (Map.Entry)(it.next());
				addStyleChoice(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
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
			return styleChoices.size();
		}//end getRowCount
	
/**
 * @see TableModel#getValueAt(int, int)
 */
		public Object getValueAt(int row, int col)
		{
			StyleChoice ch = (StyleChoice) styleChoices.get(row);
			switch (col)
			{
				case 0:
					return ch.label;
				case 1:
					return ch.style;
				default:
					return null;
			}//end switch on column number
		}//end getValueAt
	
/**
 * @see TableModel#setValueAt(Object, int, int)
 */
		public void setValueAt(Object value, int row, int col)
		{
			StyleChoice ch = (StyleChoice) styleChoices.get(row);
			if (col == 1)
				ch.style = (SyntaxStyle) value;
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
					return Jext.getProperty("options.styles.style");
				default:
					return null;
			}//end switch on index
		}//end getColumnName
	
/**
 * Save the contents of this <CODE>StyleTableModel</CODE> to Jext properties.
 */
		public void save()
		{
			for (int i = 0; i < styleChoices.size(); i++)
			{
				StyleChoice ch = (StyleChoice) styleChoices.get(i);
				Jext.setProperty(ch.property, GUIUtilities.getStyleString(ch.style));
			}//end for through style choices
		}//end save
    
    public void load()
    {
			for (int i = 0; i < styleChoices.size(); i++)
        ( (StyleChoice) styleChoices.get(i) ).resetStyle();
      fireTableRowsUpdated(0, styleChoices.size() - 1);
    }
	
/**
 * Add the specified style choice to this <CODE>StyleTableModel</CODE>.
 * @param label      <CODE>String</CODE> property name for the description.
 * @param property   <CODE>String</CODE> property name for the style.
 */
		public void addStyleChoice(String label, String property)
		{
			styleChoices.add(new StyleChoice(Jext.getProperty(label), property));
		}//end addStyleChoice
	
		private static class StyleChoice
		{
			String label;
			String property;
			SyntaxStyle style;
	
			StyleChoice(String label, String property/*, SyntaxStyle style*/)
			{
				this.label = label;
				this.property = property;
				this.style = GUIUtilities.parseStyle(Jext.getProperty(property));
			}//end constructor

      public void resetStyle()
      {
        this.style = GUIUtilities.parseStyle(Jext.getProperty(property));
      }
		}//end class StyleChoice
	
		private static class StyleRenderer
		 extends JLabel
		 implements TableCellRenderer
		{
			public StyleRenderer()
			{
				setOpaque(true);
				setBorder(StylesOptions.noFocusBorder);
				setText("Hello World");
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
				} else {
					setBackground(table.getBackground());
					setForeground(table.getForeground());
				}//end else, not selected
	
				if (value != null)
				{
					SyntaxStyle style = (SyntaxStyle) value;
					setForeground(style.getColor());
					setFont(style.getStyledFont(getFont()));
				}//end if value not null
	
				setBorder((cellHasFocus) ?
								UIManager.getBorder("Table.focusCellHighlightBorder") :
								StylesOptions.noFocusBorder);
				return this;
			}//end getTableCellRendererComponent
		}//end class StyleRenderer
	}//end class StyleTableModel
	
	private static class StyleEditor
	 extends JDialog
	 implements ActionListener, KeyListener
	{
		// private members
		private boolean okClicked;
		private JextCheckBox bold, italics;
		private JextHighlightButton ok, cancel;
		private JButton color;
	
		StyleEditor(Component comp, SyntaxStyle style)
		{
			super(JOptionPane.getFrameForComponent(comp),
						Jext.getProperty("styleEditor.title"), true);
	
			getContentPane().setLayout(new BorderLayout());
	
			JPanel panel = new JPanel();
			panel.add(italics = new JextCheckBox(Jext.getProperty("styleEditor.italics")));
			italics.getModel().setSelected(style.isItalic());
			panel.add(bold = new JextCheckBox(Jext.getProperty("styleEditor.bold")));
			bold.getModel().setSelected(style.isBold());
			panel.add(new JLabel(Jext.getProperty("styleEditor.color")));
			panel.add(color = new JButton("    "));
			color.setBackground(style.getColor());
			color.setRequestFocusEnabled(false);
			color.addActionListener(this);
	
			getContentPane().add(BorderLayout.CENTER, panel);
	
			panel = new JPanel();
			panel.add(ok = new JextHighlightButton(Jext.getProperty("general.ok.button")));
			getRootPane().setDefaultButton(ok);
			ok.addActionListener(this);
			panel.add(cancel = new JextHighlightButton(Jext.getProperty("general.cancel.button")));
			cancel.addActionListener(this);
	
			getContentPane().add(BorderLayout.SOUTH, panel);
	
			addKeyListener(this);
	
//			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	
//			Dimension screen = getToolkit().getScreenSize();
			pack();
//			setLocation((screen.width - getSize().width) / 2,
//							(screen.height - getSize().height) / 2);
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			Utilities.centerComponent(this);
			show();
		}//end constructor
	
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if (source == ok)
			{
				okClicked = true;
				dispose();
			}//end if source is the ok button
			else if (source == cancel)
				dispose();
			else if (source == color)
			{
				Color c =
								JColorChooser.showDialog(this, Jext.getProperty("colorChooser.title"),
								color.getBackground());
				if (c != null)
					color.setBackground(c);
			}//end if source is the color button
		}//end actionPerformed
	
		public void keyPressed(KeyEvent evt)
		{
			switch (evt.getKeyCode())
			{
				case KeyEvent.VK_ENTER:
					okClicked = true;
					dispose();
					evt.consume();
					break;
				case KeyEvent.VK_ESCAPE:
					dispose();
					evt.consume();
					break;
			}//end switch on key code
		}//end keyPressed
	
		public void keyReleased(KeyEvent evt)
		{
		}//end keyReleased
		public void keyTyped(KeyEvent evt)
		{
		}//end keyTyped
	
		public SyntaxStyle getStyle()
		{
			if (!okClicked)
				return null;
			return new SyntaxStyle(color.getBackground(),
						 italics.getModel().isSelected(), bold.getModel().isSelected());
		}//end getStyle
	}//end class StyleEditor
}//end class StyleTable
