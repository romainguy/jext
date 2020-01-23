/*
 * $Id: FunnyBrackets.java,v 1.1.1.1 2004/10/19 16:16:57 gfx Exp $
 *
 * Funny Brackets Plugin for Jext
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
import java.awt.event.ActionEvent;
import org.gjt.sp.jedit.textarea.TextUtilities;

public class FunnyBrackets extends MenuAction
{
    private boolean isRunning;
    public FunnyBrackets()
    {
	super("funnybrackets");
	// super("plugin.FunnyBracketsPlugin.hotkey.funnybrackets");
        this.isRunning = false;
    }

    public void setRunningMode(boolean b)
    {
        this.isRunning = b;
    }

    public void actionPerformed(ActionEvent evt)
    {
	if (this.isRunning) {
	    JextFrame parent = getJextParent(evt);
	    JextTextArea textArea = getTextArea(evt);

	    try {

		int cur = textArea.getCaretPosition();
		int tru = TextUtilities.findMatchingBracket(textArea.getDocument(), cur);

		// On est devant une braket inférieure
		if (tru != -1 && tru < cur) {
		    int pos = textArea.getLineOfOffset(tru);
		    int deb = textArea.getFirstLine();
		    int end = deb + textArea.getVisibleLines();

		    // Une popup uniquement si la brakete supérieure n'est pas visible
		    if ( ! (deb < pos && pos < end) ) {
			String s = textArea.getText(textArea.getLineStartOffset(pos), textArea.getLineLength(pos)).trim();
			if (s.length() <= 1) {
			    // ça veut dire que la ligne ne contient que ( ou {
			    // on prend donc la ligne du dessus
			    s = textArea.getText(textArea.getLineStartOffset(pos - 1), textArea.getLineLength(pos - 1)).trim();
			}
			if (s.length() > 0) {
			    // Une popup uniquement pour les lignes non vides
			    FunnyBracketsPopup popup = new FunnyBracketsPopup(parent, s);
			}
		    }
		}
	    }
	    catch(Exception e) {
		System.err.println(e);
	    }
	}
    }
}

