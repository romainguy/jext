/*
 * CreateGlobalFunction.java - creates a new global function
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

package org.jext.dawn.naming;

import java.io.*;
import org.jext.dawn.*;

/**
 * Creates a new global function.<br>
 * Usage:<br>
 * <code>string global [code] endGlobal</code><br>
 * Where 'strings' holds function name.
 * @author Romain Guy
 */

public class CreateGlobalFunction extends Function
{
  public CreateGlobalFunction()
  {
    super("global");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    String functionName = parser.popString();
    parser.checkVarName(this, functionName);

    StreamTokenizer st = parser.getStream();
    StringBuffer buf = new StringBuffer();

    try
    {
      int innerFunction = 0;

out:  for( ; ; )
      {
        switch(st.nextToken())
        {
          case StreamTokenizer.TT_EOL:
            //parser.lineno++;
            buf.append('\n');
            break;
          case StreamTokenizer.TT_EOF:
            throw new DawnRuntimeException(this, parser, "global without endGlobal");
          case StreamTokenizer.TT_WORD:
            if (st.sval.equals("global"))
              innerFunction++;
            else if (st.sval.equals("endGlobal"))
            {
              if (innerFunction > 0)
                innerFunction--;
              else
                break out;
            }
            buf.append(' ' + st.sval);
            break;
          case '"': case '\'':
            buf.append(" \"" + st.sval + "\"");
            break;
          case '-':
            buf.append(" -");
            break;
          case StreamTokenizer.TT_NUMBER:
            buf.append(" " + st.nval);
        }
      }

      parser.createGlobalFunction(functionName, buf.toString());
    } catch (IOException ioe) {
      throw new DawnRuntimeException(this, parser, "unexpected error occured during parsing");
    }
  }
}

// End of CreateGlobalFunction.java
