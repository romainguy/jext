/*
 * 10/13/2001 - 08:10:55
 * AbstractProjectManager.java
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


import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;


/**
 * An abstract <CODE>ProjectManager</CODE>.
 *
 * @author <a href="mailto:orangeherbert@users.sourceforge.net">Matt Benson</a>
 */
public abstract class AbstractProjectManager
 implements ProjectManager
{

	protected Vector listeners;
	
	
/**
 * Creates a new <CODE>AbstractProjectManager</CODE>.
 */
	protected AbstractProjectManager()
	{
		listeners = new Vector();
	}//end constructor
	
	
/**
 * Adds a listener to the list that's notified each time a <CODE>Project</CODE>
 * change occurs.
 */
	public void addProjectListener(ProjectListener listener)
	{
		listeners.add(listener);
	}//end removeProjectListener

	
/**
 * Removes a listener from the list that's notified each time a
 * <CODE>Project</CODE> change occurs.
 */
	public void removeProjectListener(ProjectListener listener)
	{
		listeners.remove(listener);
	}//end removeProjectListener
	
	
/**
 * Fire the specified <CODE>ProjectEvent</CODE>.
 * @param e   the <CODE>ProjectEvent</CODE> to fire.
 */
	protected void fireProjectEvent(ProjectEvent e)
	{
		ArrayList doneListeners = new ArrayList(listeners.size());
		Iterator it = listeners.iterator();
		
		while (doneListeners.size() < listeners.size())
		{
			try
			{
				while (it.hasNext())
				{
					ProjectListener listener = (ProjectListener)(it.next());
					if (!(doneListeners.contains(listener)))
					{
						listener.projectEventFired(e);
						doneListeners.add(listener);
					}//end if this listener has not yet been done
				}//end while more listeners
			}//end try to notify listeners
			catch (ConcurrentModificationException seaEmEx)
			{//get a new Iterator
				it = listeners.iterator();
			}//end catch ConcurrentModificationException
		}//end while some listeners have not been notified
	}//end fireProjectEvent
	
}//end class AbstractProjectManager
