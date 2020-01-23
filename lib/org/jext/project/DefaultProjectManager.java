/*
 * 10/13/2001 - 08:27:02
 * DefaultProjectManager.java
 * Copyright (C) 2001 Matt Benson
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

package org.jext.project;


import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jext.Jext;
import org.jext.JextFrame;
import org.jext.JextTextArea;
import org.jext.JextTabbedPane;
import org.jext.event.JextEvent;
import org.jext.event.JextListener;
import org.jext.misc.Workspaces;
import org.jext.misc.ProjectPanel;


/**
 * A default <CODE>ProjectManager</CODE>.
 *
 * @author <a href="mailto:orangeherbert@users.sourceforge.net">Matt Benson</a>
 */
public class DefaultProjectManager
 extends AbstractProjectManager
 implements JextListener
{
	private ProjectPanel ui;
	private Vector projectNames;//for ordering
	private Hashtable projects;
	private Project currentProject;
	private JextFrame parent;

/**
 * Create a new <CODE>DefaultProjectManager</CODE>.
 * @param parent   the <CODE>JextFrame</CODE> for which this
 *                 <CODE>DefaultProjectManager</CODE> shall be an agent.
 */
	public DefaultProjectManager(JextFrame parent)
	{
		super();
		if (parent == null)
		{
			throw new IllegalArgumentException("Parent is null!");
		}//end if null parent supplied
		this.parent = parent;
		parent.addJextListener(this);
		ui = new ProjectPanel(parent);
		projectNames = new Vector();
		projects = new Hashtable();
		loadFromWorkspaces();

		parent.getWorkspaces().getList().addListDataListener(new ListDataListener()
		{

/**
 * @see ListDataListener#contentsChanged(ListDataEvent)
 */
			public void contentsChanged(ListDataEvent e)
			{
				loadFromWorkspaces();
			}//end contentsChanged

/**
 * @see ListDataListener#intervalAdded(ListDataEvent)
 */
			public void intervalAdded(ListDataEvent e)
			{
				for (int i = e.getIndex0(); i <= e.getIndex1() ; i++)
				{
					Workspaces.WorkspaceElement elm = (Workspaces.WorkspaceElement)
					 (DefaultProjectManager.this.parent.getWorkspaces().getList().get(i));
					projectNames.add(i, elm.getName());
					projects.put(elm.getName(), new DefaultProject(elm));
				}//end for i...
			}//end intervalAdded

/**
 * @see ListDataListener#intervalRemoved(ListDataEvent)
 */
			public void intervalRemoved(ListDataEvent e)
			{
				for (int i = e.getIndex0(); i <= e.getIndex1(); i++)
				{
					fireProjectEvent(new ProjectEvent(DefaultProjectManager.this,
					 (Project)(projects.remove(projectNames.remove(i))),
					 ProjectEvent.PROJECT_CLOSED));
				}//end for i...
			}//end intervalRemoved
		});//end anonymous ListDataListener

		setCurrentProjectFromWorkspace();
	}//end constructor
	
	
	private void loadFromWorkspaces()
	{
		DefaultListModel list = parent.getWorkspaces().getList();
		ArrayList tempNames = new ArrayList(list.size());
		HashMap tempProjects = new HashMap(list.size());
		for (int i = 0; i < list.size(); i++)
		{
			Workspaces.WorkspaceElement elm = (Workspaces.WorkspaceElement)(list.get(i));
			tempNames.add(elm.getName());
			tempProjects.put(elm.getName(), (projectNames.indexOf(elm.getName()) < 0)
			 ? new DefaultProject(elm) : projects.get(elm.getName()));
		}//end for i...
		projectNames.removeAll(tempNames); //all that's left are closed projects
		Iterator iter = projectNames.iterator();
		while (iter.hasNext())
		{
			fireProjectEvent(new ProjectEvent(this,
			 (Project)(projects.remove(projectNames.remove(projectNames.indexOf(
			 String.valueOf(iter.next()))))), ProjectEvent.PROJECT_CLOSED));
		}//end while more project names
		projects.clear();
		projectNames.addAll(tempNames);
		projects.putAll(tempProjects);
	}//end loadFromWorkspaces
	
	
/**
 * @see org.jext.event.JextListener#jextEventFired(org.jext.event.JextEvent)
 */
	public void jextEventFired(JextEvent evt)
	{
		if (parent.getProjectManager() == this)
		{
			switch (evt.getWhat())
			{
				case JextEvent.TEXT_AREA_SELECTED:
					if (currentProject == null ||
					 parent.getWorkspaces().getName() != currentProject.getName())
					{
						setCurrentProjectFromWorkspace();
					}//end if Project changed...
					fireProjectEvent(new ProjectEvent(this, ProjectEvent.FILE_SELECTED));
					break;
				case JextEvent.CHANGED_UPDATE:
				case JextEvent.REMOVE_UPDATE:
				case JextEvent.INSERT_UPDATE:
					fireProjectEvent(new ProjectEvent(this, ProjectEvent.FILE_CHANGED));
			}//end switch on event type
		}//end if we are the current ProjectManager
	}//end jextEventFired
	
	
	private void setCurrentProjectFromWorkspace()
	{
		currentProject = (Project)(projects.get(parent.getWorkspaces().getName()));
		fireProjectEvent(new ProjectEvent(this, ProjectEvent.PROJECT_SELECTED));
	}//end setCurrentProjectFromWorkspace
	
	
/**
 * @see ProjectManager#getProjects()
 */
	public Project[] getProjects()
	{
		//long implementation preserves ordering...
		Project[] result = new Project[projectNames.size()];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = (Project)(projects.get(projectNames.get(i)));
		}//end for i through the projects
		return result;
	}//end getProjects

	
/**
 * @see ProjectManager#getCurrentProject()
 */
	public Project getCurrentProject()
	{
		return currentProject;
	}//end getCurrentProject

	
/**
 * @see ProjectManager#newProject()
 */
	public void newProject()
	{
    String response =
		 JOptionPane.showInputDialog(parent, Jext.getProperty("ws.new.msg"),
		 Jext.getProperty("ws.new.title"), JOptionPane.QUESTION_MESSAGE);
    if (response != null && response.length() > 0)
			openProject(response);
	}//end newProject

	
/**
 * @see ProjectManager#openProject(Object)
 */
	public void openProject(Object id)
	{
		parent.getWorkspaces().selectWorkspaceOfNameOrCreate(String.valueOf(id));
	}//end openProject
	
	
/**
 * @see ProjectManager#closeProject(Project)
 */
	public void closeProject(Project p)
	{
		DefaultListModel list = parent.getWorkspaces().getList();
		for (int i = 0; i < list.size(); i++)
		{
			if (((Workspaces.WorkspaceElement)(list.get(i))).getName().equals(p.getName()))
			{
				list.remove(i);
			}//end if this WorkspaceElement is the one specified...
		}//end for i...
	}//end closeProject
	
	
/**
 * @see ProjectManager#saveProject(Project)
 */
	public void saveProject(Project p)
	{
		//it takes a lot of work to save only one Workspace, implement better later
		parent.getWorkspaces().save();
	}//end saveProject
	
	
/**
 * @see ProjectManager#getUI()
 */
	public JComponent getUI()
	{
		return ui;
	}//end getUI
	
	
/**
 * Default <CODE>Project</CODE> implementation.
 */
	private class DefaultProject
	 extends AbstractProject
	{
		private Workspaces.WorkspaceElement ws;


/**
 * Construct a <CODE>DefaultProject</CODE>.
 * @param ws   the <CODE>Workspaces.WorkspaceElement</CODE> to which this
 *             project corresponds.
 */		
		public DefaultProject(Workspaces.WorkspaceElement ws)
		{
			super(ws.getName(), DefaultProjectManager.this);
			this.ws = ws;
			fireProjectEvent(ProjectEvent.PROJECT_OPENED);
		}//end constructor

		
// inherit doc		
		public synchronized File[] getFiles()
		{
			ArrayList list = new ArrayList(ws.contents.size());
			for (int i = 0; i < ws.contents.size(); i++)
			{
				if (ws.contents.get(i) instanceof JextTextArea)
				{
					File f = ((JextTextArea)(ws.contents.get(i))).getFile();
					if (f != null)
					{
						list.add(f);
					}//end if there is a file here
				}//end if this is a JextTextArea, WorkspaceElements may allow recursion in the future...
			}//end for i...
			File[] result;
			try
			{
				result = (File[])(list.toArray(new File[list.size()]));
			}//end try 
			catch (ArrayStoreException ayEssEx)
			{
				result = null;
				ayEssEx.printStackTrace(System.out);
				Iterator it = list.iterator();
				while (it.hasNext())
				{
					System.out.println(it.next());
				}//end while more contents of the list
			}//end catch ArrayStoreException
			return result;
		}//end getFiles
	
		
// inherit doc		
		public void openFile(File f)
		{
			if (!(parent.getWorkspaces().getName().equals(name)))
			{
				parent.getWorkspaces().selectWorkspaceOfName(name);
			}//end if this project is not selected
			parent.open(f.getAbsolutePath());
		}//end openFile
	
		
// inherit doc		
		public void closeFile(File f)
		{
			if (!(parent.getWorkspaces().getName().equals(name)))
			{
				parent.getWorkspaces().selectWorkspaceOfName(name);
			}//end if this project is not selected
			
			Iterator it = ws.contents.iterator();
			while (it.hasNext())
			{
				JextTextArea nextText = (JextTextArea)(it.next());
				if (nextText.getFile().equals(f))
				{
					parent.getWorkspaces().removeFile(nextText);
				}//end if this is the correct file
			}//end while this iterator has more
		}//end closeFile
	
		
// inherit doc		
		public void selectFile(File f)
		{
			int index = -1;
			for (int i = 0; i < ws.contents.size() && index < 0; i++)
			{
				if (((JextTextArea)(ws.contents.get(i))).getFile().equals(f))
				{
					index = i;
				}//end if this file matches
			}//end for i...
			if (index < 0)
			{
				openFile(f);
			}//end if not already open
			else
			{
				parent.getTabbedPane().setSelectedIndex(index);
				ws.setSelectedIndex(index);
			}//end else--already open
		}//end selectFile
		
		
// inherit doc		
		public File getSelectedFile()
		{
			return ((JextTextArea)(ws.contents.get(ws.getSelectedIndex()))).getFile();
		}//end getSelectedFile
	

// inherit doc		
		public boolean equals(Object o)
		{
			return (o instanceof DefaultProject &&
			 ((DefaultProject)o).name.equals(name));
		}//end equals


// inherit doc		
		public int hashCode()
		{
			return name.hashCode();
		}//end hashCode
		
		
// inherit doc		
		public String toString()
		{
			return new StringBuffer("DefaultProject ").append(name).toString();
		}//end toString
		
	}//end class DefaultProject
	
}//end class DefaultProjectManager
