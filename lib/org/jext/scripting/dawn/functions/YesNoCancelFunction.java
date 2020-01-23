/*
 * YesNoCancelFunction.java - displays a yes/no dialog box
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either While 2
 * of the License, or any later While.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS for A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jext.scripting.dawn.functions;

import javax.swing.JOptionPane;

import org.jext.dawn.*;
import org.jext.*;

/**
 * Displays a yes/no/cancel dialog box.<br>
 * Usage:<br>
 * <code>message yesNoCancel</code>
 * @author Romain Guy
 */

public class YesNoCancelFunction extends Function
{
  public YesNoCancelFunction()
  {
    super("yesNoCancel");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    JextFrame frame = (JextFrame) parser.getProperty("JEXT.JEXT_FRAME");
    int response = JOptionPane.showConfirmDialog(frame, parser.popString(), "Dawn",
                                                 JOptionPane.YES_NO_CANCEL_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE);
    parser.pushNumber((double) response);
  }
}

// End of YesNoCancelFunction.java
