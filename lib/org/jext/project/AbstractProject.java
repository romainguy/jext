/*
 * 10/13/2001 - 08:02:48
 * AbstractProject.java
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
import java.util.Map;
import java.util.Hashtable;


/**
 * Defines a basic project created using Jext.
 *
 * @author <a href="mailto:orangeherbert@users.sourceforge.net">Matt Benson</a>
 */
public abstract class AbstractProject
 implements Project
{
	protected final Map attributes;
	protected final AbstractProjectManager manager;
	protected final String name;
	
	
/**
 * Create a new <CODE>AbstractProject</CODE>.
 * @param name      the name of this <CODE>AbstractProject</CODE>.
 * @param manager   the <CODE>AbstractProjectManager</CODE> in charge of this
 *                  <CODE>AbstractProject</CODE>.
 */
	protected AbstractProject(String name, AbstractProjectManager manager)
	{
		if (manager == null)
		{
			throw new IllegalArgumentException("Cannot have a null ProjectManager!");
		}//end if null manager set
		this.manager = manager;
		this.name = name;
		attributes = new Hashtable();
	}//end constructor
	
	
/**
 * Returns the name of this <CODE>AbstractProject</CODE>.
 * @return <CODE>String</CODE>.
 */
	public String getName()
	{
		return name;
	}//end getName
	
	
/**
 * Returns the value of the specified attribute for this <CODE>AbstractProject</CODE>.
 * @param key   the <CODE>String</CODE> key to which this attribute is tied.
 * @return <CODE>Object</CODE>
 */	
	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}//end getAttribute(String)
	

/**
 * Returns the value of the specified attribute for this <CODE>AbstractProject</CODE>,
 * returning the specified default value if no such attribute exists.
 * @param key            the <CODE>String</CODE> key to which this attribute is tied.
 * @param defaultValue   the default <CODE>Object</CODE> to return if no such
 *                       attribute exists.
 * @return <CODE>Object</CODE>
 */	
	public Object getAttribute(String key, Object defaultValue)
	{
		return (getAttribute(key) == null) ? defaultValue : getAttribute(key);
	}//end getAttribute(String, Object)
	

/**
 * Returns the <CODE>String</CODE> value of the specified attribute for this
 * <CODE>AbstractProject</CODE>.
 * @param key   the <CODE>String</CODE> key to which this attribute is tied.
 * @return <CODE>String</CODE>
 */	
	public String getAttributeAsString(String key)
	{
		return String.valueOf(attributes.get(key));
	}//end getAttributeAsString
	
	
/**
 * Sets the specified attribute.
 * @param key     the <CODE>String</CODE> key of the attribute to be set.
 * @param value   the <CODE>Object</CODE> value to assign.
 */
	public void setAttribute(String key, Object value)
	{
		attributes.put(key, value);
		fireProjectEvent(ProjectEvent.ATTRIBUTE_SET, key);
	}//end setAttribute
	
	
/**
 * Fire the specified <CODE>ProjectEvent</CODE> for this <CODE>AbstractProject</CODE>.
 * This 
 * @param eventType   the <CODE>int</CODE> code for the type of event to fire.
 */
	protected void fireProjectEvent(int eventType)
	{
		manager.fireProjectEvent(
		 new ProjectEvent((ProjectManager)manager, (Project)this, eventType));
	}//end fireProjectEvent(int)
	
	
/**
 * Fire the specified <CODE>ProjectEvent</CODE> for this <CODE>AbstractProject</CODE>.
 * This 
 * @param eventType   the <CODE>int</CODE> code for the type of event to fire.
 * @param target      the <CODE>Object</CODE> target of the event to fire.
 */
	protected void fireProjectEvent(int eventType, Object target)
	{
		manager.fireProjectEvent(
		 new ProjectEvent((ProjectManager)manager, (Project)this, eventType, target));
	}//end fireProjectEvent(int, Object)
	
}//end class AbstractProject
