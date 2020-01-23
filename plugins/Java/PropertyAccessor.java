/*
 * PropertyAccessor.java - Interface that defines a source of Property info.
 *
 * Copyright (c) 1999 George Latkiewicz	(georgel@arvotek.net)
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

//=============================================================================
/**
 * This is interface defines an implementation independant means of obtaining
 * property information. Any source of property pairs that implements this
 * interface can be used interchangeably by components to obtain read-only
 * access to these properties.
 */
import java.io.*;

//=========================================================================
public interface PropertyAccessor {

	/**
	 * Fetches a property, returning null if it's not defined.
	 * @param name The property
	 */
	public String getProperty(String name);

	/**
	 * Fetches a property, returning the default value if it's not
	 * defined.
	 * @param name The property
	 * @param def The default value
	 */
	public String getProperty(String name, String def);

	/**
	 * Returns the property with the specified name, formatting it with
	 * the <code>java.text.MessageFormat.format()</code> method.
	 * @param name The property
	 * @param args The positional parameters
	 */
	public String getProperty(String name, Object[] args);

	/**
	 * Sets a property to a new value.
	 * @param name The property
	 * @param value The new value
	 */
	public Object /*void???*/ setProperty(String name, String value);

	/**
	 * Reads a property list (key and element pairs) from the input stream.
	 */
	// public void load(InputStream inStream) throws IOException;

} // public static interface PropertyAccessor
