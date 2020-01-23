/*
 * DoLoopFunction.java - do loop
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

package org.jext.dawn.loop;

import java.io.*;
import java.util.*;
import org.jext.dawn.*;

/**
 * Do loop.<br>
 * Usage:<br>
 * <code>do [code] loop expression until</code>
 * @author Romain Guy
 */

public class DoLoopFunction extends Function
{
  public DoLoopFunction()
  {
    super("do");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    StreamTokenizer st = parser.getStream();
    StringBuffer buf = new StringBuffer();

    try // LONG LONG CATCH
    {

    int innerLoop = 0;

out: for( ; ; )
    {
      switch(st.nextToken())
      {
        case StreamTokenizer.TT_EOL:
          //parser.lineno++;
          buf.append('\n');
          break;
        case StreamTokenizer.TT_EOF:
          throw new DawnRuntimeException(this, parser, "do without loop");
        case StreamTokenizer.TT_WORD:
          if (st.sval.equals("do"))
            innerLoop++;
          else if (st.sval.equals("until"))
          {
            if (innerLoop > 0)
              innerLoop--;
          } else if (st.sval.equals("loop")) {
            if (innerLoop == 0)
              break out;
          }
          buf.append(' ' + st.sval);
          break;
        case '"': case '\'':
          buf.append(" \"" + DawnUtilities.unescape(st.sval) + "\"");
          break;
        case '-':
          buf.append(" -");
          break;
        case StreamTokenizer.TT_NUMBER:
          buf.append(" " + st.nval);
      }
    }

    String code = buf.toString();
    Function function = parser.createOnFlyFunction(code);
    Function untilFunction = null;

    int bool = 0;

    do
    {
      if (untilFunction == null)
      {
        buf = new StringBuffer();
  
outWhile: for( ; ; )
        {
          switch(st.nextToken())
          {
            case StreamTokenizer.TT_EOL:
              //parser.lineno++;
              buf.append('\n');
              break;
            case StreamTokenizer.TT_EOF:
              throw new DawnRuntimeException(this, parser, "loop without until");
            case StreamTokenizer.TT_WORD:
              if (st.sval.equals("until"))
                break outWhile;
              buf.append(' ' + st.sval);
              break;
            case '"': case '\'':
              buf.append(" \"" + DawnUtilities.unescape(st.sval) + "\"");
              break;
            case '-':
              buf.append(" -");
              break;
            case StreamTokenizer.TT_NUMBER:
              buf.append(" " + st.nval);
              break;
          }
        }
  
        code = buf.toString();
        untilFunction = parser.createOnFlyFunction(code);
      }

      function.invoke(parser);
      untilFunction.invoke(parser);
      bool = (int) parser.popNumber();
    } while (bool == 0);

    } catch (IOException ioe) {
      throw new DawnRuntimeException(this, parser, "unexpected error occured during parsing");
    }
  }
}

// End of DoLoopFunction.java
