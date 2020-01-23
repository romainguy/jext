/*
 * 01/25/2003 - 13:42:28
 *
 * PythonLogWindow.java - Scripts log window
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

package org.jext.scripting.python;

import java.awt.BorderLayout;
import java.awt.Container;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.*;
import javax.swing.text.Element;

import gnu.regexp.*;

import org.jext.*;
import org.jext.gui.*;
import org.jext.scripting.*;

public class PythonLogWindow extends AbstractLogWindow implements ActionListener
{
  private RE regexp;
  private boolean docked = false;
  private JextHighlightButton clear, dock;
  private JScrollPane textAreaScroller = null;

  private DockChangeHandler handler = new DockChangeHandler() {
    public void dockChangeHandler(int where, int newWhere) {
      if (newWhere == Dockable.FLOATING)
	dock.setLabel(Jext.getProperty("python.window.dock"));
      else if ((newWhere & Dockable.DOCK_MASK) != 0)
	dock.setLabel(Jext.getProperty("python.window.undock"));
    }
  };

  public static Dockable getInstance(JextFrame parent) {
    PythonLogWindow frame = new PythonLogWindow(parent);
    Dockable pane = new Dockable(frame, Jext.getProperty("python.window.tab"), parent, frame.handler);
    frame.contDock = pane;
    return pane;
  }

  private PythonLogWindow(JextFrame parent)
  {
    //super(parent, Jext.getProperty("python.window.title"), Jext.getProperty("python.window.tab"));
    super(parent, Jext.getProperty("python.window.title"));

    textArea.addMouseListener(new MouseHandler());

    JPanel pane = new JPanel(new BorderLayout());
    dock = new JextHighlightButton(Jext.getProperty("python.window.dock"));
    pane.add(BorderLayout.WEST, dock);
    pane.add(BorderLayout.CENTER, new JLabel(Jext.getProperty("python.window.advice")));
    clear = new JextHighlightButton(Jext.getProperty("python.window.clear"));
    pane.add(BorderLayout.EAST, clear);
    getContentPane().add(BorderLayout.SOUTH, pane);

    dock.addActionListener(this);
    clear.addActionListener(this);

    try
    {
      regexp = new RE("File \"([^\"]+)\", line (\\d+),.*");
    } catch (REException ree) {
      dispose();
    }

    pack();
    Utilities.centerComponent(this);
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object o = evt.getSource();
    if (o == clear)
      textArea.setText("");
    else if (o == dock)
    {
      toggleDocking();
    }
  }

  /** Switches the docking status between DOCK_TO_LEFT_PANEL and FLOATING*/
  private void toggleDocking() {
    int status = contDock.getDockingStatus();
    if (status == Dockable.FLOATING) {
      contDock.setDockingStatus(Dockable.DOCK_TO_LEFT_PANEL);
    } else if (status == Dockable.DOCK_TO_LEFT_PANEL) {
      contDock.setDockingStatus(Dockable.FLOATING);
    } else
      System.err.println("DockingStatus:" + status);
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent evt)
    {
      if (evt.getClickCount() == 2)
      {
        try
        {
          Element map = textArea.getDocument().getDefaultRootElement();
          Element line = map.getElement(map.getElementIndex(textArea.getCaretPosition()));
          int start = line.getStartOffset();
          REMatch match = regexp.getMatch(textArea.getText(start, line.getEndOffset() - 1 - start));

          if (match != null)
          {
            // file
            String file = match.toString(1);
            // line number
            int lineNo = 0;
            // how can an exception be thrown here ? jeeezz... :)
            lineNo = Integer.parseInt(match.toString(2));
            JextTextArea _textArea = null;

            // script is executed from a text area
            if (file.equals("<string>"))
            {
              _textArea = parent.getTextArea();
            } else {
            // from a file

              JextTextArea[] areas = parent.getTextAreas();
              for (int i = 0; i < areas.length; i++)
              {
                if (file.equals(areas[i].getCurrentFile()))
                {
                  _textArea = areas[i];
                  break;
                }
              }

              if (_textArea == null)
                _textArea = parent.open(file, false);
            }

            // select error line
            line = _textArea.getDocument().getDefaultRootElement().getElement(lineNo - 1);
            if (line != null)
              _textArea.select(line.getStartOffset(), line.getEndOffset() - 1);
          }
        } catch (Exception e ) { }
      }
    }
  }
}

// End of PythonLogWindow.java
