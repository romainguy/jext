/*
 * 01/13/2001 - 13:55:49
 *
 * SaveDialog.java - Save dialog displayed when all files are about to be closed
 * Copyright (C) 2000 Romain Guy
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

package org.jext.misc;

import gnu.regexp.*;

import java.util.ArrayList;
import java.util.Vector;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import org.jext.*;
import org.jext.gui.*;

public class SaveDialog extends JDialog implements ActionListener
{
  public static final int CLOSE_WINDOW = 0;
  public static final int CLOSE_TEXT_AREAS_ONLY = 1;
  public static final int DO_NOTHING = 2;

  // private
  private int mode, dirty;
  private DirtyArea[] areas;
  private JextFrame parent;
  private JextHighlightButton all, none, cancel, ok;
  
  public SaveDialog(JextFrame parent, int mode)
  {
    super(parent,
          Jext.getProperty("save.dialog.title") /*+ " [" + parent.getWorkspaces().getName() + ']'*/,
          true);

    this.parent = parent;
    this.mode = mode;

    getContentPane().setLayout(new BorderLayout());

    Box boxer = Box.createVerticalBox();

    Object[] textAreas;
    if (mode == CLOSE_WINDOW)
      textAreas = createTextAreasArray();
    else
      textAreas = parent.getTextAreas();
    Vector _areas = new Vector(textAreas.length);
    boolean foundOne = false;

    boolean addedOne = false;
    for (int i = 0; i < textAreas.length; i++)
    {
      if (textAreas[i] instanceof JextTextArea)
      {
        JextTextArea textArea = (JextTextArea) textAreas[i];
        if (textArea.isDirty() && !textArea.isEmpty())
        {
          JextCheckBox box = new JextCheckBox(textArea.getName());
          box.setSelected(true);
          boxer.add(box);
          _areas.addElement(new DirtyArea(box, textArea));
          addedOne = foundOne = true;
          dirty++;
        }
      } else {
        if (i != 0)
        {
          if (!addedOne)
          {
            boxer.remove(boxer.getComponentCount() - 1);
            //if (boxer.getComponentCount() > 0)
            //  boxer.remove(boxer.getComponentCount() - 1);
          } else {
            JLabel label = new JLabel(" ");
            boxer.add(label);
          }
        }
        
        WorkspaceLabel label = new WorkspaceLabel(textAreas[i].toString());
        label.setFont(label.getFont().deriveFont(Font.ITALIC));
        boxer.add(label);

        addedOne = false;
      }
    }

    if (!foundOne)
    {
      exit();
      return;
    }

    if (!addedOne && mode == CLOSE_WINDOW)
    {
      boxer.remove(boxer.getComponentCount() - 1);
      if (boxer.getComponentCount() > 0)
        boxer.remove(boxer.getComponentCount() - 1);
    }

    getContentPane().add(new JLabel(Jext.getProperty("save.dialog.label")), BorderLayout.NORTH);

    areas = new DirtyArea[textAreas.length];
    _areas.copyInto(areas);
    _areas = null;

    JCheckBox box = areas[0].getCheckBox();
    JScrollPane scrollPane = new JScrollPane(boxer);
    scrollPane.getViewport().setPreferredSize(new Dimension(scrollPane.getViewport().getPreferredSize().width,
                                              6 * box.getPreferredSize().height));
    getContentPane().add(scrollPane, BorderLayout.CENTER);

    JPanel pane = new JPanel();
    pane.add(all = new JextHighlightButton(Jext.getProperty("save.dialog.all.button")));
    all.setMnemonic(Jext.getProperty("save.dialog.all.mnemonic").charAt(0));

    pane.add(none = new JextHighlightButton(Jext.getProperty("save.dialog.none.button")));
    none.setMnemonic(Jext.getProperty("save.dialog.none.mnemonic").charAt(0));

    pane.add(ok = new JextHighlightButton(Jext.getProperty("general.ok.button")));
    ok.setMnemonic(Jext.getProperty("general.ok.mnemonic").charAt(0));

    pane.add(cancel = new JextHighlightButton(Jext.getProperty("general.cancel.button")));
    cancel.setMnemonic(Jext.getProperty("general.cancel.mnemonic").charAt(0));
    getContentPane().add(pane, BorderLayout.SOUTH);

    all.addActionListener(this);
    none.addActionListener(this);
    ok.addActionListener(this);
    cancel.addActionListener(this);

    addKeyListener(new AbstractDisposer(this));

    getRootPane().setDefaultButton(ok);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    pack();
    setResizable(false);
    org.jext.Utilities.centerComponent(this);
    setVisible(true);
  }

  private Object[] createTextAreasArray()
  {
    ArrayList areas = new ArrayList();
    DefaultListModel model = parent.getWorkspaces().getList();

    for (int i = 0; i < model.size(); i++)
    {
      areas.add(((Workspaces.WorkspaceElement) model.get(i)).getName());
      ArrayList c = ((Workspaces.WorkspaceElement) model.get(i)).contents;
      for (int j = 0; j < c.size(); j++)
      {
        if (c.get(j) instanceof JextTextArea)
          areas.add(c.get(j));
      }
    }

    return areas.toArray();
  }

  private void save()
  {
    parent.setBatchMode(true);

    for (int i = 0; i < dirty; i++)
    {
      DirtyArea dirty = areas[i];
      JextTextArea textArea = dirty.getTextArea();

      if (dirty.isSelected())
        textArea.saveContent();

      if (mode == CLOSE_TEXT_AREAS_ONLY)
        parent.close(textArea, false);
    }

    parent.setBatchMode(false);
    exit();
  }

  private void exit()
  {
    if (mode == CLOSE_WINDOW)
    {
      parent.getWorkspaces().save();
      Jext.closeWindow(parent);
    } else if (mode == CLOSE_TEXT_AREAS_ONLY) {
      parent.setBatchMode(true);

      JextTextArea[] _areas = parent.getTextAreas();
      for (int i = 0; i < _areas.length; i++)
        parent.close(_areas[i], false);

      parent.setBatchMode(false);
    }

    dispose();
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object o = evt.getSource();
    if (o == cancel)
    {
      dispose();
    } else if (o == ok) {
      save();
    } else {
      for (int i = 0; i < dirty; i++)
        areas[i].setSelected(o == all);
    }
  }

  class WorkspaceLabel extends JLabel
  {
    WorkspaceLabel(String label)
    {
      super(label);
    }

    protected void paintComponent(Graphics g)
    {
      super.paintComponent(g);
      //int x = getIcon().getIconWidth() + Math.max(0, getIconTextGap() - 1);
      g.setColor(Color.black);
      g.drawLine(0, getHeight() - 2, getWidth() - 2, getHeight() - 2);
    }
  }

  class DirtyArea
  {
    // private fields
    private JCheckBox box;
    private JextTextArea area;

    DirtyArea(JCheckBox box, JextTextArea area)
    {
      this.box = box;
      this.area = area;
    }

    public JCheckBox getCheckBox()
    {
      return box;
    }

    public boolean isSelected()
    {
      return box.isSelected();
    }

    public void setSelected(boolean selected)
    {
      box.setSelected(selected);
    }

    public JextTextArea getTextArea()
    {
      return area;
    }
  }
  

}

// End of SaveDialog.java