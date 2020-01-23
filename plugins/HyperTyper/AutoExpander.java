/*
 * AutoExpander.java - Auto expands an abbreviation
 * Copyright (C) 2000 Romain Guy
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

import java.awt.event.ActionEvent;

import org.jext.*;

import org.gjt.sp.jedit.textarea.InputHandler;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class AutoExpander extends MenuAction implements InputHandler.NonRepeatable
{
  public AutoExpander()
  {
    super("auto_expand");
  }

  public void actionPerformed(ActionEvent evt)
  {
    String str = evt.getActionCommand();
    char ch = str.charAt(0);

    if (HyperTyperPlugin.isAutoExpandOn() && !Character.isLetterOrDigit(ch)
        && HyperTyperAction.delimiters.indexOf(ch) != -1)
    {
      HyperTyperPlugin.htOMan.getAction().actionPerformed(evt);
    }

    JextTextArea textArea = getTextArea(evt);
    int repeatCount = textArea.getInputHandler().getRepeatCount();

    if (textArea.isEditable())
    {
      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < repeatCount; i++)
        buf.append(str);
      textArea.overwriteSetSelectedText(buf.toString());
    }
  }
}

// End of AutoExpander.java
