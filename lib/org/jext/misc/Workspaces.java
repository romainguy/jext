/*
 * 03/13/2003 - 17:29:02
 *
 * Workspaces.java - Workspaces panel
 * Copyright (C) 2003 Romain Guy
 * Portions copyright (C) 2001 by Grant Stead
 * romain.guy@jext.org
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.misc;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.io.*;

import java.util.*;

import org.jext.*;
import org.jext.gui.*;
import org.jext.event.JextEvent;

import com.microstar.xml.*;

public class Workspaces extends JPanel implements ActionListener, ListSelectionListener
{
  // miscallenaous
  private JextFrame parent;
  private JList workspacesList;
  private DefaultListModel model;
  private WorkspaceElement currentWorkspace;

  private boolean loading = false;

  // the buttons panel
  private JextHighlightButton newWorkspace, removeWorkspace, switchWorkspace;

  public Workspaces(JextFrame parent)
  {
    super();
    setLayout(new BorderLayout());
    this.parent = parent;

    JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);

    toolbar.add(newWorkspace = new JextHighlightButton(
                Utilities.getIcon("images/menu_new" + Jext.getProperty("jext.look.icons") +
                                  ".gif", Jext.class)));
    newWorkspace.setToolTipText(Jext.getProperty("ws.new.tooltip"));
    newWorkspace.addActionListener(this);

    toolbar.add(removeWorkspace = new JextHighlightButton(
                Utilities.getIcon("images/button_remove" + Jext.getProperty("jext.look.icons") +
                                  ".gif", Jext.class)));
    removeWorkspace.setToolTipText(Jext.getProperty("ws.remove.tooltip"));
    removeWorkspace.addActionListener(this);

    toolbar.add(switchWorkspace = new JextHighlightButton(
                Utilities.getIcon("images/menu_goto" + Jext.getProperty("jext.look.icons") +
                                  ".gif", Jext.class)));
    switchWorkspace.setToolTipText(Jext.getProperty("ws.sendTo.tooltip"));
    switchWorkspace.addMouseListener(new WorkspaceSwitcher(parent));

    model = new DefaultListModel();
    workspacesList = new JList(model);
    workspacesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    workspacesList.setCellRenderer(new ModifiedCellRenderer());
    new DropTarget(workspacesList, new DnDHandler());

    add(toolbar, BorderLayout.NORTH);
    JScrollPane scroller = new JScrollPane(workspacesList);
    scroller.setBorder(null);
    add(scroller, BorderLayout.CENTER);
  }

  public void load()
  {
    workspacesList.addListSelectionListener(this);
    loading = true;

    WorkspacesHandler handler = null;
    try
    {
      String xmlString = "";
      File f = new File(Jext.SETTINGS_DIRECTORY + File.separator + ".workspaces.xml");
      if (f.exists() && (f.length() > 0) && Jext.getBooleanProperty("editor.saveSession"))
      {
        try
        {
          BufferedReader in = new BufferedReader(new FileReader(f));
          String line = in.readLine();
          while (line != null)
          {
            xmlString += line;
            line = in.readLine();
          }
          in.close();
        } catch (Exception e) {
          xmlString = "<?xml version=\"1.0\"?><workspaces><workspace name=\"" +
                      Jext.getProperty("ws.default") + "\"/></workspaces>";
        }
      } else {
        xmlString = "<?xml version=\"1.0\"?><workspaces><workspace name=\"" +
                    Jext.getProperty("ws.default") + "\"/></workspaces>";
      }

      StringReader reader = new StringReader(xmlString);
      XmlParser parser = new XmlParser();
      handler = new WorkspacesHandler();
      parser.setHandler(handler);

      parser.parse(null, null, reader);
    } catch (Exception e) { }

    loading = false;
    workspacesList.setSelectedIndex(0);
  }

  public void save()
  {
    if (Jext.getInstances().size() > 1 || !Jext.getBooleanProperty("editor.saveSession"))
      return;

    try
    {
      String output;
      File vf = new File(Jext.SETTINGS_DIRECTORY + File.separator + ".workspaces.xml");
      BufferedWriter writer = new BufferedWriter(new FileWriter(vf));

      writer.write("<?xml version=\"1.0\"?>");
      writer.newLine();
      writer.write("<workspaces>");
      writer.newLine();

      for (int i = 0; i < model.size(); i++)
      {
        WorkspaceElement e = (WorkspaceElement) model.get(i);
        writer.write("  <workspace name=\"" + convertToXML(e.toString()) + "\" selectedIndex=\"" +
                     e.getSelectedIndex() + "\">");
        writer.newLine();

        ArrayList list = e.contents;

        for (int j = 0; j < list.size(); j++)
        {
          JextTextArea area = (JextTextArea) list.get(j);
          if (area.isNew())
            continue;
          writer.write("    <file path=\"" + convertToXML(area.getCurrentFile()) + "\" caretPosition=\"" +
                  area.getCaretPosition() + "\" />");
          writer.newLine();
        }
        writer.write("  </workspace>");
        writer.newLine();
      }

      writer.write("</workspaces>");
      writer.flush();
      writer.close();
    } catch (Exception e) { }
  }

  public String convertToXML(String source)
  {
    char c;
    StringBuffer buf = new StringBuffer(source.length());
    for (int i = 0; i < source.length(); i++)
    {
      switch (c = source.charAt(i))
      {
        case '&':
          buf.append("&amp;");
          break;
        case '\'':
          buf.append("&apos;");
          break;
        case '"':
          buf.append("&quot;");
          break;
        default:
          buf.append(c);
      }
    }
    return buf.toString();
  }

  public DefaultListModel getList()
  {
    return model;
  }

  public String[] getWorkspacesNames()
  {
    String[] names = new String[model.size()];
    for (int i = 0; i < names.length; i++)
      names[i] = ((WorkspaceElement) model.get(i)).getName();
    return names;
  }

  public void addFile(JextTextArea textArea)
  {
    currentWorkspace.contents.add(textArea);
  }

  public void removeFile(JextTextArea textArea)
  {
    currentWorkspace.contents.remove(currentWorkspace.contents.indexOf(textArea));

    int size = currentWorkspace.contents.size();
    if (size == 0)
      currentWorkspace.setSelectedIndex(0);
    else if (size - 1 < currentWorkspace.getSelectedIndex())
      currentWorkspace.setSelectedIndex(size - 1);
  }

  private void newWorkspace()
  {
    String response =
            JOptionPane.showInputDialog(parent, Jext.getProperty("ws.new.msg"),
            Jext.getProperty("ws.new.title"), JOptionPane.QUESTION_MESSAGE);
    if (response != null && response.length() > 0)
      createWorkspace(response);
  }

  public WorkspaceElement createWorkspace(String name)
  {
    for (int i = 0; i < model.size(); i++)
    {
      if (name.equals(((WorkspaceElement) model.get(i)).getName()))
      {
        GUIUtilities.message(parent, "ws.exists", null);
        return null;
      }
    }

    WorkspaceElement elem = new WorkspaceElement(name);
    model.addElement(elem);
    workspacesList.setSelectedIndex(model.size() - 1);
    return elem;
  }

  public void clear()
  {
    parent.getTabbedPane().removeAll();
    for (int i = 0; i < model.size(); i++)
    {
      WorkspaceElement e = (WorkspaceElement) model.get(i);
      e.contents.clear();
      model.remove(i);
      e = null;
    }
  }

  public void closeAllWorkspaces()
  {
    new SaveDialog(parent, SaveDialog.CLOSE_WINDOW);
  }

  private void removeWorkspace()
  {
    parent.closeAll();
    int index = workspacesList.getSelectedIndex();
    model.remove(index);
    Object e = workspacesList.getSelectedValue();
    e = null;

    if (model.size() == 0)
      createWorkspace(Jext.getProperty("ws.default"));

    workspacesList.setSelectedIndex(index == 0 ? 0 : index - 1);
  }

  public void loadTextAreas()
  {
    parent.setBatchMode(true);

    for (int i = 0; i < model.size(); i++)
    {
      ArrayList a = ((WorkspaceElement) model.get(i)).contents;
      for (int j = 0; j < a.size(); j++)
        parent.loadTextArea((JextTextArea) a.get(j));
    }

    parent.setBatchMode(false);
  }

  public String getName()
  {
    if (currentWorkspace == null)
    {
      return Jext.getProperty("ws.default");
    } else {
      return currentWorkspace.toString();
    }
  }

  public void selectWorkspaceOfName(String name)
  {
    if (name == null)
      return;

    for (int i = 0; i < model.size(); i++)
    {
      if (name.equals(((WorkspaceElement) model.get(i)).getName()))
      {
        workspacesList.setSelectedIndex(i);
        return;
      }
    }
  }

  public void selectWorkspaceOfNameOrCreate(String name)
  {
    if (name == null)
      return;

    for (int i = 0; i < model.size(); i++)
    {
      if (name.equals(((WorkspaceElement) model.get(i)).getName()))
      {
        workspacesList.setSelectedIndex(i);
        return;
      }
    }

    currentWorkspace = createWorkspace(name);
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object o = evt.getSource();
    if (o == newWorkspace)
      newWorkspace();
    else if (o == removeWorkspace)
      removeWorkspace();
  }

  public void valueChanged(ListSelectionEvent e)
  {
    if (e.getValueIsAdjusting())
      return;

    parent.setBatchMode(true);

    if (currentWorkspace != null)
    {
      if (!currentWorkspace.first)
      {
        currentWorkspace.setSelectedIndex(parent.getTabbedPane().getSelectedIndex());
      } else
        currentWorkspace.first = false;
    }

    WorkspaceElement elem = (WorkspaceElement) workspacesList.getSelectedValue();
    if (elem == null)
		{
			parent.setBatchMode(false);
      return;
		}
    currentWorkspace = elem;

    JextTabbedPane pane = parent.getTabbedPane();
    pane.removeAll();

    if (elem.contents.size() == 0)
    {
      if (!loading)
        parent.createFile();
    } else {
      ArrayList list = elem.contents;
      for (int i = 0; i < list.size(); i++)
        pane.add((Component) list.get(i));

      pane.setSelectedIndex(currentWorkspace.getSelectedIndex());
    }

    parent.setBatchMode(false);
		parent.fireJextEvent(JextEvent.TEXT_AREA_SELECTED);
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JextTextArea textArea = parent.getTextArea();
        if (textArea != null)
        {
          textArea.grabFocus();
          textArea.requestFocus();
        }
      }
    });
  }

  class WorkspacesHandler extends HandlerBase
  {
    int caretPosition = 0;
    int selectedIndex = 0;

    String fileName = null;
    String workspaceName = null;
    String currentWorkspaceName = null;

    public void startElement(String elname) throws java.lang.Exception
    {
      if (elname.equalsIgnoreCase("workspace"))
      {
        currentWorkspace = createWorkspace(workspaceName);
        //currentWorkspace.setSelectedIndex(0);
      } else if (elname.equalsIgnoreCase("file")) {
        if (new File(fileName).exists())
        {
          JextTextArea area = parent.openForLoading(fileName);
          if (caretPosition < area.getLength())
            area.setCaretPosition(caretPosition);
        }
      }
    }

    public void endElement(String elname) throws java.lang.Exception
    {
      if (elname.equalsIgnoreCase("workspace"))
      {
        if (currentWorkspace != null)
        {
          if (currentWorkspace.contents.size() == 0)
            parent.createFile();
          else
            currentWorkspace.setSelectedIndex(selectedIndex);
        }
        selectedIndex = 0;
      } else
        caretPosition = 0;
    }

    public void attribute(String aname, String value, boolean isSpecified)
    {
      if (aname.equalsIgnoreCase("path"))
        fileName = value;
      else if (aname.equalsIgnoreCase("name"))
        workspaceName = value;
      else if (aname.equalsIgnoreCase("caretPosition"))
      {
        try
        {
          caretPosition = Integer.parseInt(value);
        } catch (Exception e) {
          caretPosition = 0;
        }
      } else if (aname.equalsIgnoreCase("selectedIndex")) {
        try
        {
          selectedIndex = Integer.parseInt(value);
        } catch (Exception e) {
          selectedIndex = 0;
        }
      }
    }
  }

  public static class WorkspaceElement
  {
    private String name;
    private int sIndex = 0;
    private boolean first = true;

    public ArrayList contents;

    WorkspaceElement(String name)
    {
      this.name = name;
      contents = new ArrayList();
    }

    public int getSelectedIndex()
    {
      return sIndex;
    }

    public String getName()
    {
      return name;
    }

    public void setSelectedIndex(int index)
    {
      if (index < contents.size())
        sIndex = index;
    }

    public String toString()
    {
      return name;
    }
  }

  class DnDHandler implements DropTargetListener
  {
    public void dragEnter(DropTargetDragEvent evt) { }

    public void dragOver(DropTargetDragEvent evt)
    {
      workspacesList.setSelectedIndex(workspacesList.locationToIndex(evt.getLocation()));
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
              parent.open(((File) iterator.next()).getPath());
            dropCompleted = true;
          }
          catch (Exception e)
          { }
        }
      }
      evt.dropComplete(dropCompleted);
    }
  }
}

// End of Workspaces.java