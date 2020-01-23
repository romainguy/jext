/*
 * ASPMode.java 
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
 * ASP Mode constants
 *
 * @author Andre Kaplan
 * @version 0.6
 */
class ASPMode
{
	public static final byte HTML         = 0;
	public static final byte HTML_COMMENT = 1;
	public static final byte HTML_ENTITY  = 2;
	public static final byte HTML_TAG     = 3;
	public static final byte HTML_SCRIPT  = 4;
	public static final byte SSI          = 5;
	public static final byte ASP          = 6;
	public static final byte ASP_CFG      = 7;  // <%@ %> like tags (useful for <%@ LANGUAGE="Javascript" %>
	public static final byte SSVB         = 8;  // Server-side VB
	public static final byte CSVB         = 9;  // Client-side VB
	public static final byte SSJS         = 10; // Server-side Javascript
	public static final byte CSJS         = 11; // Client-side Javascript
	public static final byte SSPS         = 12; // Server-side Perlscript
	public static final byte CSPS         = 13; // Client-side Perlscript
}
