/*
 * 10/24/2001 - 12:31:05
 * DefaultProjectManagement.java
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


import org.jext.Jext;
import org.jext.JextFrame;


/**
 * Default <CODE>ProjectManagement</CODE> implementation.
 */
	public class DefaultProjectManagement
	 implements ProjectManagement
	{
		
		private JextFrame parent;
		private ProjectManager pm;
		
		
/**
 * Constructs a new <CODE>DefaultProjectManagement</CODE>.
 * @param parent   the <CODE>JextFrame</CODE> to which this
 * <CODE>DefaultProjectManagement</CODE> belongs.
 */
		public DefaultProjectManagement(JextFrame parent)
		{
			this.parent = parent;
		}//end constructor
		
		
/**
 * @see ProjectManagement#getLabel()
 */
		public String getLabel()
		{
			return Jext.getProperty("defaultProjectManager.label");
		}//end getLabel

		
/**
 * @see ProjectManagement#getProjectManager()
 */
		public ProjectManager getProjectManager()
		{
			pm = ((pm == null) ? new DefaultProjectManager(parent) : pm);
			return pm;
		}//end getProjectManager
		
	}//end class DefaultProjectManagement
