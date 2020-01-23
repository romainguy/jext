/*
 * 03/30/2002 - 15:27:39
 *
 * TagsCompletionOptionPane.java - Options
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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.jext.*;
import org.jext.gui.*;

public class TagsCompletionOptionPane extends AbstractOptionPane implements ActionListener
{
  private JextCheckBox expandFullTag, activateTool, xhtml;

  public TagsCompletionOptionPane()
  {
    super("html.completion");

    addComponent(activateTool = new JextCheckBox(Jext.getProperty("html.completion.activateTool.label")));
    activateTool.setSelected(Jext.getBooleanProperty("html.completion.activateTool"));
    activateTool.addActionListener(this);

    addComponent(expandFullTag = new JextCheckBox(Jext.getProperty("html.completion.expandFullTag.label")));
    expandFullTag.setSelected(Jext.getBooleanProperty("html.completion.expandFullTag"));
    expandFullTag.setEnabled(activateTool.isSelected());

    addComponent(xhtml = new JextCheckBox(Jext.getProperty("html.completion.xhtmlCompliance.label")));
    xhtml.setSelected(Jext.getBooleanProperty("html.completion.xhtmlCompliance"));
    xhtml.setEnabled(activateTool.isSelected());
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (evt.getSource() == activateTool)
    {
      expandFullTag.setEnabled(activateTool.isSelected());
      xhtml.setEnabled(activateTool.isSelected());
    }
  }

  public void save()
  {
    Jext.setProperty("html.completion.activateTool", activateTool.isSelected() ? "on" : "off");
    Jext.setProperty("html.completion.expandFullTag", expandFullTag.isSelected() ? "on" : "off");
    Jext.setProperty("html.completion.xhtmlCompliance", xhtml.isSelected() ? "on" : "off");
  }  
}

// End of TagsCompletionOptionPane.java
