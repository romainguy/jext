/*
 * $Id: PHPQuickOpen.java,v 1.1.1.1 2004/10/19 16:17:06 gfx Exp $
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
import gnu.regexp.*;
import java.io.File;


import java.awt.event.ActionEvent;

public class PHPQuickOpen extends MenuAction
{
    public PHPQuickOpen()
    {
	super("phpquickopen");
    }

    public void actionPerformed(ActionEvent evt)
    {
	JextFrame parent = getJextParent(evt);
	JextTextArea textArea = getTextArea(evt);
	String currentLine = textArea.getLineText(textArea.getCaretLine());
	String currentFileName = textArea.getCurrentFile();
	String currentPathName = currentFileName.substring(0,currentFileName.lastIndexOf(File.separator));
	String foundFileName = null;
	StringBuffer filename;


	// On cherche le nom du fichier
	try {
	    REMatchEnumeration ren;
	    REMatch rem;
	    RE re = new RE("(include_once|require_once|include|require)[^'\"]*.([^'\"]+)");

	    ren = re.getMatchEnumeration(currentLine);

	    while (ren.hasMoreMatches()) {
		rem = ren.nextMatch();
		foundFileName = rem.toString(2);
	    }
	} catch(Exception e) {
	    System.err.println(e);
	}

	if (foundFileName != null) {
	    if (foundFileName.length() > 0 && foundFileName.charAt(0) == '.') {
		// Chemin relatif
		    filename = new StringBuffer(currentPathName);
		    filename.append(File.separator);
		    filename.append(foundFileName);
	    }
	    else if (foundFileName.indexOf(File.separator) == -1) {
		// Chemin relatif
		    filename = new StringBuffer(currentPathName);
		    filename.append(File.separator);
		    filename.append(foundFileName);
	    }
	    else {
		// Chemin absolue
		filename = new StringBuffer(foundFileName);
	    }
	    // On ouvre le fichier
	    parent.open(filename.toString());
	}
    }
}


