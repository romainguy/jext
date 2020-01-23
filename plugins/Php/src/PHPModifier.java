/*
 * $Id: PHPModifier.java,v 1.1.1.1 2004/10/19 16:17:06 gfx Exp $
 *
 * PHP Plugin for Jext
 *
 * Copyright (C) 2002 Nicolas Thouvenin
 * touv at yahoo dot fr
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

import org.jext.*;

public abstract class PHPModifier
{
    protected int mode;

    public PHPModifier(String s)
    {
	if (s.equalsIgnoreCase("PHP")) {
	    this.mode = 1;
	}
	else if (s.equalsIgnoreCase("PEAR")) {
	    this.mode = 2;
	}
	else {
	    this.mode = 0;
	}
    }

    public abstract void modifier(JextTextArea textArea);
}


