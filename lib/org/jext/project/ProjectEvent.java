/*
 * ProjectEvent.java - Jext project event model
 * 07/21/2001 - 13:43:53
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


/**
 * The event emitted by a Jext <CODE>ProjectManager</CODE> when a meaningful
 * change happens to one of its <CODE>Project</CODE>s.
 *
 * @author <a href="mailto:orangeherbert@users.sourceforge.net">Matt Benson</a>
 * @see org.jext.project.ProjectListener
 */
public class ProjectEvent
{
// project related events
	public static final int PROJECT_OPENED = 0;
	public static final int PROJECT_CLOSED = 1;
	public static final int PROJECT_SELECTED = 2;

// file related events
  public static final int FILE_ADDED = 100;
  public static final int FILE_REMOVED = 101;
  public static final int FILE_OPENED = 102;
  public static final int FILE_CLOSED = 103;
  public static final int FILE_SELECTED = 104;
  public static final int FILE_CHANGED = 105;
	
// attribute related events
	public static final int ATTRIBUTE_SET = 201;
	public static final int ATTRIBUTE_UNSET = 202;
	
	public static final int OTHER = Integer.MAX_VALUE;

	
  private int event;
	private ProjectManager projectManager;
  private Project project;
	private Object target;

	
/**
 * Creates a new ProjectEvent, registering the parent <CODE>ProjectManager</CODE>
 * of this event and the type of the event (the event is assumed to be specific
 * to the <CODE>ProjectManager</CODE>'s currently active <CODE>Project</CODE>.
 * @param projectManager   <code>ProjectManager</code> parent.
 * @param eventType        <CODE>int</CODE> value which specifies the nature of
 *                         the <CODE>ProjectEvent</CODE>.
 */
  public ProjectEvent(ProjectManager projectManager, int eventType)
  {
		this(projectManager, projectManager.getCurrentProject(), eventType);
  }//end constructor(ProjectManager, int)

	
/**
 * Creates a new ProjectEvent, registering the parent <CODE>ProjectManager</CODE>
 * of this event, the type of the event and the relevant <CODE>Project</CODE>.
 * @param projectManager   <code>ProjectManager</code> parent.
 * @param project          <CODE>Project</CODE> relevant to this
 *                         <CODE>ProjectEvent</CODE>.
 * @param eventType        <CODE>int</CODE> value which specifies the nature of
 *                         the <CODE>ProjectEvent</CODE>.
 */
  public ProjectEvent(ProjectManager projectManager,
	                    Project project, int eventType)
  {
		this(projectManager, project, eventType, null);
  }//end constructor(ProjectManager, Project, int)
	
	
	
/**
 * Creates a new ProjectEvent, registering the parent <CODE>ProjectManager</CODE>
 * of this event, the type of the event, the relevant <CODE>Project</CODE>, and
 * the target of the event.
 * @param projectManager   <code>ProjectManager</code> parent.
 * @param project          <CODE>Project</CODE> relevant to this
 *                         <CODE>ProjectEvent</CODE>.
 * @param eventType        <CODE>int</CODE> value which specifies the nature of
 *                         the <CODE>ProjectEvent</CODE>.
 * @param target           <CODE>Object</CODE> which, along with the
 *                         <CODE>Project</CODE>, is the target of the
 *                         <CODE>ProjectEvent</CODE>.
 */
	public ProjectEvent(ProjectManager projectManager,
	                    Project project, int eventType, Object target)
	{
		if (projectManager == null)
		{
			throw new IllegalArgumentException("ProjectEvent.<init>:  ProjectManager is null!");
		}//end if null projectManager
		if (project == null)
		{
			throw new IllegalArgumentException("ProjectEvent.<init>:  Project is null!");
		}//end if null project
    this.projectManager = projectManager;
    this.project = project;
    this.event = eventType;
		
		switch(eventType)
		{
			case PROJECT_OPENED:
			case PROJECT_CLOSED:
			case PROJECT_SELECTED:
				this.target = project;
				break;
				
			case FILE_ADDED:
			case FILE_REMOVED:
			case FILE_OPENED:
			case FILE_CLOSED:
			case FILE_SELECTED:
			case FILE_CHANGED:
				if (target instanceof File)
				{
					this.target = target;
				}//end if File
				else
				{
					this.target = this.project.getSelectedFile();
				}//end else
				break;
				
			case ATTRIBUTE_SET:
			case ATTRIBUTE_UNSET:
				if (target instanceof String)
				{
					this.target = target;
				}//end if String
				break;
		}//end switch event type
  }//end constructor(ProjectManager, Project, int)

	
/**
 * Returns the type of event.
 * @return <CODE>int</CODE>.
 */
  public int getWhat()
  {
    return event;
  }//end getWhat

	
/**
 * Returns the <code>ProjectManager</code> from which the event was fired.
 * @return <code>ProjectManager</code>.
 */
  public ProjectManager getProjectManager()
  {
    return projectManager;
  }//end getProjectManager


/**
 * Returns the <CODE>Project</CODE> for which the <CODE>ProjectEvent</CODE> was
 * generated.
 * @return <code>Project</code>.
 */
  public Project getProject()
  {
    return project;
  }//end getProject
	
	
/**
 * Returns the <CODE>Object</CODE> target of the event; if this is a "project"
 * event, then the result should be the same <CODE>Project</CODE> as returned by
 * <CODE>getProject</CODE>; if this is a "file" event, the result should be the
 * affected <CODE>File</CODE>; if this is an "attribute" event, the result
 * should be the affected <CODE>String</CODE> attribute name.
 * @return <CODE>Object</CODE>.
 */
	public Object getTarget()
	{
		return target;
	}//end getTarget
	
	
/**
 * @see java.lang.Object#toString()
 */
	public String toString()
	{
		return new StringBuffer("ProjectEvent:  ").append(
		 "projectManager=").append(String.valueOf(getProjectManager())).append(
		 ", ").append(
		 "project=").append(getProject().getName()).append(
		 ", ").append(
		 "what=").append(String.valueOf(getWhat())).append(
		 ", ").append(
		 "target=").append(String.valueOf(getTarget())).toString();
	}//end
	
}//end class ProjectEvent
