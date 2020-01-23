/*
 * 07/21/2001 - 13:14:28
 * Project.java
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
 * Defines a basic project created using Jext.
 *
 * @author <a href="mailto:orangeherbert@users.sourceforge.net">Matt Benson</a>
 */
public interface Project
{
	
/**
 * Returns the name of this <CODE>Project</CODE>.
 * @return <CODE>String</CODE>.
 */
	public abstract String getName();
	
	
/**
 * Returns the <CODE>File</CODE>s that compose this <CODE>Project</CODE>.
 * If any of these <CODE>File</CODE>s is a directory, it is understood that all
 * <CODE>File</CODE>s in and below this directory are part of this
 * <CODE>Project</CODE>.
 * @return <CODE>File[]</CODE>.
 */
	public abstract File[] getFiles();

	
/**
 * Open the specified <CODE>File</CODE> in this <CODE>Project</CODE>.  Although
 * it is an implementation decision, this act might also add the specified
 * <CODE>File</CODE> to this <CODE>Project</CODE>.
 * @param f   the <CODE>File</CODE> to close.
 */
	public abstract void openFile(File f);

	
/**
 * Close the specified <CODE>File</CODE>.
 * @param f   the <CODE>File</CODE> to close.
 */
	public abstract void closeFile(File f);

	
/**
 * Select the specified <CODE>File</CODE>.  It is recommended that this
 * method be implemented such that the <CODE>File</CODE> is opened if not
 * already open.
 * @param f   the <CODE>File</CODE> to select.
 * @see openFile(File)
 */	
	public abstract void selectFile(File f);
	
	
/**
 * Returns the currently selected <CODE>File</CODE> of this
 * <CODE>Project</CODE>.
 * @return <CODE>File</CODE>.
 */
	public abstract File getSelectedFile();
	

/**
 * Returns the value of the specified attribute for this <CODE>Project</CODE>.
 * @param key   the <CODE>String</CODE> key to which this attribute is tied.
 * @return <CODE>Object</CODE>
 */	
	public abstract Object getAttribute(String key);
	

/**
 * Returns the value of the specified attribute for this <CODE>Project</CODE>,
 * returning the specified default value if no such attribute exists.
 * @param key            the <CODE>String</CODE> key to which this attribute is tied.
 * @param defaultValue   the default <CODE>Object</CODE> to return if no such
 *                       attribute exists.
 * @return <CODE>Object</CODE>
 */	
	public abstract Object getAttribute(String key, Object defaultValue);
	

/**
 * Returns the <CODE>String</CODE> value of the specified attribute for this
 * <CODE>Project</CODE>.
 * @param key   the <CODE>String</CODE> key to which this attribute is tied.
 * @return <CODE>String</CODE>
 */	
	public abstract String getAttributeAsString(String key);
	
	
/**
 * Sets the specified attribute.
 * @param key     the <CODE>String</CODE> key of the attribute to be set.
 * @param value   the <CODE>Object</CODE> value to assign.
 */
	public abstract void setAttribute(String key, Object value);
	
}//end interface Project
