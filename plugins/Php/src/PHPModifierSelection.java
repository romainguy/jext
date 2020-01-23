/*
 * $Id: PHPModifierSelection.java,v 1.1.1.1 2004/10/19 16:17:06 gfx Exp $
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

public class PHPModifierSelection extends PHPModifier
{
    PHPModifierSelection(String s)
    {
	super(s);
    }

    public void modifier(JextTextArea textArea)
    {
	String laselection = textArea.getSelectedText();
	boolean inhtml = false;

	if (laselection == null)
	    return;

	if (this.mode == 0)
	    return;

	textArea.beginCompoundEdit();

	PHPBufferPartList truc1 = new PHPBufferPartList(textArea.getText(0, textArea.getLength())); // Découpage Total
	truc1.parse();

	// On regarde si la selection commence sur du HTML ou du PHP
	inhtml = truc1.getPartOffset(textArea.getSelectionStart()).equalsIgnoreCase("HTML");

	PHPBufferPartList truc2 = new PHPBufferPartList(laselection); // Découpage de la selection
	if (!inhtml) {
	    truc2.beginWithPHP();
	}
	truc2.parse();

	PHPBufferPart machin[] = truc2.getPart();

	StringBuffer newstr = new StringBuffer();
	for (int j = 0; j < truc2.getNbPart(); j++)   {
	    if (machin[j].getType().equalsIgnoreCase("HTML")) {
		if (mode == 1 ) {
		    PHPIndentHTML z = new PHPIndentHTML(laselection.substring(machin[j].getStart(), machin[j].getEnd()));
		    z.setIndentSize(textArea.getTabSize());
		    z.indent();
		    newstr.append(z.get());
		}
		else if (mode == 2) {
		    newstr.append(laselection.substring(machin[j].getStart(), machin[j].getEnd()));
		}
	    }
	    else {
		if (mode == 1) {
		    PHPIndentPHP z = new PHPIndentPHP(laselection.substring(machin[j].getStart(), machin[j].getEnd()));
		    // On est déja dans du code PHP
		    if (j == 0 && !inhtml) {
			z.beginWithPHP();
		    }
		    z.setIndentSize(textArea.getTabSize());
		    z.indent();
		    newstr.append(z.get());
		}
		else if (mode == 2) {
		    PHPIndentPEAR z = new PHPIndentPEAR(laselection.substring(machin[j].getStart(), machin[j].getEnd()));
		    // On est déja dans du code PHP
		    if (j == 0 && !inhtml) {
			z.beginWithPHP();
		    }
		    z.setIndentSize(textArea.getTabSize());
		    z.indent();
		    newstr.append(z.get());
		}
	    }
	}

	textArea.setSelectedText(newstr.toString());
	textArea.endCompoundEdit();
    }
}


