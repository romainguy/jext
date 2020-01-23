/*
 * 18:34:10 19/05/00
 *
 * Beautifier.java
 * Copyright (C) 1999 Romain Guy
 *
 * This	free software; you can redistribute it and/or
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

import javax.swing.text.Element;
import org.jext.*;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class Beautifier extends MenuAction {
	private int prefLineLength;
	public Beautifier() {
		super("beautifier");
	}

	public void actionPerformed(ActionEvent evt) {
		try {
			JextTextArea textArea = getTextArea(evt);
			textArea.beginCompoundEdit();
			Document doc = textArea.getDocument();

			JSFormatter format = new JSFormatter();
			format.setBracketBreak(Jext.getProperty("beautifier.breakBracket").equals("true"));
			format.setSwitchIndent(Jext.getProperty("beautifier.indentSwitch").equals("true"));

			try {
				prefLineLength = Integer.parseInt(Jext.getProperty("beautifier.preferredLineLength"));
				if (prefLineLength <= 0) {
					prefLineLength = 70;
					Jext.setProperty("beautifier.preferredLineLength", "70");
				}
			} catch (NumberFormatException nfe) {
				prefLineLength = 70;
				Jext.setProperty("beautifier.preferredLineLength", "70");
			}
			format.setPreferredLineLength(prefLineLength);

			if (Jext.getProperty("editor.softTab").equals("on"))
				format.beautifier.setSpaceIndentation(textArea.getTabSize());
			else
				format.beautifier.setTabIndentation();

			format.init();
			Element map = doc.getDefaultRootElement();

			String line;
			int i = 0, start, end;
			StringBuffer buf = new StringBuffer(doc.getLength());
			try {
				while (true) {
					while (!format.hasMoreFormattedLines()) {
						if (i > map.getElementCount())
							throw new NullPointerException();
						Element lineElement = map.getElement(i);
						start = lineElement.getStartOffset();
						end = lineElement.getEndOffset() - 1;
						end -= start;
						line = textArea.getText(start, end);
						if (line == null)
							throw new NullPointerException();
						format.formatLine(line);
						i++;
					}
					while (format.hasMoreFormattedLines())
						buf.append(format.nextFormattedLine() + '\n');
				}
			} catch (NullPointerException npe) { }

			doc.remove(0, doc.getLength());
			doc.insertString(0, buf.toString(), null);

			format.summarize();
			while (format.hasMoreFormattedLines())
				doc.insertString(doc.getLength(), format.nextFormattedLine() + '\n', null);
			textArea.endCompoundEdit();
		} catch (BadLocationException ble) { }
	}
}

// End of Beautifier.java
