/*
 * 07/21/2001 - 13:14:14
 * ProjectManager.java
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


import javax.swing.JComponent;


/**
 * A manager for Jext <CODE>Projects</CODE>.
 *
 * @author <a href="mailto:orangeherbert@users.sourceforge.net">Matt Benson</a>
 */
public interface ProjectManager
{
	
/**
 * Adds a listener to the list that's notified each time a <CODE>Project</CODE>
 * change occurs.
 */
	public abstract void addProjectListener(ProjectListener listener);

	
/**
 * Removes a listener from the list that's notified each time a
 * <CODE>Project</CODE> change occurs.
 */
	public abstract void removeProjectListener(ProjectListener listener);
	
	
/**
 * Returns all this <CODE>ProjectManager</CODE>'s <CODE>Project</CODE>s.
 * @return <CODE>Project[]</CODE>.
 */
	public abstract Project[] getProjects();

	
/**
 * Returns this <CODE>ProjectManager</CODE>'s currently active
 * <CODE>Project</CODE>.
 * @return <CODE>Project[]</CODE>.
 */
	public abstract Project getCurrentProject();

	
/**
 * Causes this <CODE>ProjectManager</CODE> to start a new <CODE>Project</CODE>.
 */
	public abstract void newProject();

	
/**
 * Causes this <CODE>ProjectManager</CODE> to open the specified <CODE>Project</CODE>.
 * @param id   the <CODE>Object</CODE> identifier of the <CODE>Project</CODE>.
 */
	public abstract void openProject(Object id);
	
	
/**
 * Causes this <CODE>ProjectManager</CODE> to close the specified <CODE>Project</CODE>.
 * @param p   the <CODE>Project</CODE> to close.
 */
	public abstract void closeProject(Project p);
	
	
/**
 * Causes this <CODE>ProjectManager</CODE> to save the specified <CODE>Project</CODE>,
 * @param p   the <CODE>Project</CODE> to save.
 */
	public abstract void saveProject(Project p);
	
	
/**
 * Returns a swing component that acts as the interface between the user and
 * this <CODE>ProjectManager</CODE>.
 */
	public abstract JComponent getUI();
	
}//end class ProjectManager
