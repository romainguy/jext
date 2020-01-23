/*
 * 10/21/2001 - 15:41:12
 * ProjectManagement.java
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


/**
 * The interface which should be implemented by all Jext project management
 * plugins.  This has been made separate from <CODE>ProjectManager</CODE> so
 * that the plugin class itself does not have to implement the much heavier
 * <CODE>ProjectManager</CODE> interface.
 *
 * @author <a href="mailto:orangeherbert@users.sourceforge.net">Matt Benson</a>
 */
public interface ProjectManagement
{
	
/**
 * Returns the <CODE>String</CODE> to use as a description of this
 * <CODE>ProjectManagement</CODE> in a list.
 */
	public String getLabel();
	
/**
 * Returns the <CODE>ProjectManager</CODE> for this
 * <CODE>ProjectManagement</CODE>.
 */
	public ProjectManager getProjectManager();

}//end class ProjectManagement
