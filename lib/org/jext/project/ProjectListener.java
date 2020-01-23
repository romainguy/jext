/*
 * ProjectListener.java - Listens to Project events
 * 07/21/2001 - 13:19:35
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
 * The listener interface for receiving <CODE>Project</CODE> events.
 *
 * @author <a href="mailto:orangeherbert@users.sourceforge.net">Matt Benson</a>
 */
public interface ProjectListener
{

/**
 * A <CODE>ProjectManager</CODE> should invoke this method on all its
 * registered <CODE>ProjectListener</CODE>s when a significant
 * <CODE>Project</CODE> change occurs.
 * @param evt The received <code>ProjectEvent</code>.
 */
  public void projectEventFired(ProjectEvent evt);
	
}// end interface ProjectListener.java
