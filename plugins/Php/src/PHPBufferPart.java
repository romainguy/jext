/*
 * $Id: PHPBufferPart.java,v 1.1.1.1 2004/10/19 16:17:05 gfx Exp $
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
public class PHPBufferPart
{
    private int debut;
    private int fin;
    private String type;

    PHPBufferPart(int b, int e, String t) {
	this.debut = b;
	this.fin = e;
	this.type = t;
    }

    public int getStart()
    {
	return this.debut;
    }
    public int getEnd()
    {
	return this.fin;
    }
    public int getSize()
    {
	return (this.fin - this.debut);
    }
    public String getType()
    {
	return this.type;
    }

}


