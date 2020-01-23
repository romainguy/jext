/*
 * MultiModeToken.java 
 * Copyright (c) 1999 André Kaplan
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
  
package org.gjt.sp.jedit.syntax;

/**
 * This class is useful to handle multiple modes.
 *
 * @author Andre Kaplan
 * @version 0.6
 */
class MultiModeToken
{
	public MultiModeToken() 
	{
		this.mode  = ASPMode.HTML;
		this.token = Token.NULL;
		this.obj   = null;
	}

	private MultiModeToken(byte mode, byte token)
	{
		this.mode  = mode;
		this.token = token;
		this.obj   = null;
	}

	public MultiModeToken(byte mode, byte token, Object obj)
	{
		this.mode  = mode;
		this.token = token;
		this.obj   = obj;
	}

	public MultiModeToken(MultiModeToken other) 
	{
		this.mode  = other.mode;
		this.token = other.token;
		this.obj   = other.obj;
	}

	public void reset()
	{
		this.mode  = ASPMode.HTML;
		this.token = Token.NULL;
	}

	public void assign(MultiModeToken other)
	{
		this.mode  = other.mode;
		this.token = other.token;
	}

	public byte mode;
	public byte token;
	public Object obj = null;

	public static final MultiModeToken NULL = new MultiModeToken();
}
