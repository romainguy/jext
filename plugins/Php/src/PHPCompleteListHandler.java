/*
 * $Id: PHPCompleteListHandler.java,v 1.1.1.1 2004/10/19 16:17:05 gfx Exp $
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
import com.microstar.xml.*;
import java.util.Vector;

public class PHPCompleteListHandler extends HandlerBase
{
    private PHPFunction func;
    private String lastvalue;


    public void startElement(String name)
    {
	if (name.equalsIgnoreCase("Function")) {
	    this.func = new PHPFunction();

	}
    }

    public void charData(char[] str, int first, int last)
    {
	this.lastvalue = new String(str, first, last);
    }

    public void endElement(String name)
    {
	if (name.equalsIgnoreCase("Function")) {
	    PHPCompleteList.functionsList.add(func);
	    this.func = null;
	}
	else if (name.equalsIgnoreCase("name"))  {
	    this.func.name = lastvalue;
	}
	else if (name.equalsIgnoreCase("proto"))  {
	    this.func.proto = lastvalue;
	}
    }

    public void startDocument()
    {
	PHPCompleteList.functionsList = new Vector();
    }
}
