/*
 * $Id: PHPCompleteList.java,v 1.1.1.1 2004/10/19 16:17:05 gfx Exp $
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
import java.io.*;
import org.jext.Jext;
import com.microstar.xml.*;
import java.util.Vector;


public class PHPCompleteList
{
    public static Vector functionsList;

    void PHPCompleteList() {
	this.functionsList = null;
    }

    PHPFunction[] buildFunctionList(String s)
    {
	/* On charge le fichier XML en mémoire */
	if (functionsList == null) {
	    boolean ret = loadFunctionList();
	    if (!ret) System.out.println("ça merde lors du chargement...");
	}

	/* On ne prend que certaine fonctions */
	Vector listB = new Vector();
	for (int i=0; i< functionsList.size(); i++) {
	    PHPFunction r = (PHPFunction) functionsList.elementAt(i);
	    if (r.name.startsWith(s)) {
		listB.add(r);
	    }
	}

	/* On fabrique un tableau */
	PHPFunction[] list = new PHPFunction[listB.size()];
	for (int i = 0; i < list.length; i++) {
	    list[i] = (PHPFunction) listB.get(i);
	}

	return list;
    }

    private boolean loadFunctionList()
    {
	PHPCompleteListHandler xmh = new PHPCompleteListHandler();
	try {

	    XmlParser parser = new XmlParser();

	    InputStream myInput = PHPCompleteList.class.getResourceAsStream("phpfunctions.fr.xml");

	    parser.setHandler(xmh);

	    parser.parse(null, null, myInput, null);

	} catch (Exception se) {

	    se.printStackTrace();
	    return false;
	}

	return true;
    }

}


