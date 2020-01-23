/*
 * $Id: PHPModifierFichier.java,v 1.1.1.1 2004/10/19 16:17:06 gfx Exp $
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

public class PHPModifierFichier extends PHPModifier
{
    PHPModifierFichier(String s)
    {
	super(s);
    }

    public void modifier(JextTextArea textArea)
    {
	textArea.beginCompoundEdit();

	PHPBufferPartList truc = new PHPBufferPartList(textArea.getText(0, textArea.getLength()));

	// On découpe en partie (HTML et PHP)
	truc.parse();

	PHPBufferPart machin[] = truc.getPart();

	StringBuffer newstr = new StringBuffer();

	// Pour chaque type de partie on implique une certaine méthode de découpage
	for (int j = 0; j < truc.getNbPart(); j++)   {
	    //System.out.println(machin[j].getType()+"[>"+textArea.getText(machin[j].getStart(), machin[j].getSize())+"<]\n");
	    if (machin[j].getType().equalsIgnoreCase("HTML")) {
		if (mode == 1 ) {
		    PHPIndentHTML z = new PHPIndentHTML(textArea.getText(machin[j].getStart(), machin[j].getSize()));
		    z.setIndentSize(textArea.getTabSize());
		    z.indent();
		    newstr.append(z.get());
		}
		else if (mode == 2) {
		    newstr.append(textArea.getText(machin[j].getStart(), machin[j].getSize()));
		}
	    }
	    else {
		if (mode == 1 ) {
		    PHPIndentPHP z = new PHPIndentPHP(textArea.getText(machin[j].getStart(), machin[j].getSize()));
		    z.setIndentSize(textArea.getTabSize());
		    z.indent();
		    newstr.append(z.get());
		}
		else if (mode == 2) {
		    PHPIndentPEAR z = new PHPIndentPEAR(textArea.getText(machin[j].getStart(), machin[j].getSize()));
		    z.setIndentSize(textArea.getTabSize());
		    z.indent();
		    newstr.append(z.get());
		}
	    }
	}

	textArea.setText(newstr.toString());

	textArea.endCompoundEdit();

    }
}


