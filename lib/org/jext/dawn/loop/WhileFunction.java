/*
 * WhileFunction.java - while loop
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
 * While loop.<br>
 * Usage:<br>
 * <code>while expression repeat [code] wend</code>
 * @author Romain Guy
 */

public class WhileFunction extends Function
{
  public WhileFunction()
  {
    super("while");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    StreamTokenizer st = parser.getStream();
    StringBuffer buf = new StringBuffer();

    try // LONG LONG CATCH
    {

out: for( ; ; )
    {
      switch(st.nextToken())
      {
        case StreamTokenizer.TT_EOL:
          //parser.lineno++;
          buf.append('\n');
          break;
        case StreamTokenizer.TT_EOF:
          throw new DawnRuntimeException(this, parser, "while without repeat");
        case StreamTokenizer.TT_WORD:
          if (st.sval.equals("repeat"))
        break out;
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

    Function function = parser.createOnFlyFunction(buf.toString());
    function.invoke(parser);
    Function whileFunction = null;

    int innerLoop = 0;
    int bool = (int) parser.popNumber();
 
    while (bool >= 1)
    {
      if (whileFunction == null)
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
              throw new DawnRuntimeException(this, parser, "while without wend");
            case StreamTokenizer.TT_WORD:
              if (st.sval.equals("while"))
                innerLoop++;
              else if (st.sval.equals("wend"))
              {
                if (innerLoop > 0)
                  innerLoop--;
                else
                  break outWhile;
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
              break;
          }
        }
  
        whileFunction = parser.createOnFlyFunction(buf.toString());
      }

      whileFunction.invoke(parser);
      function.invoke(parser);
      bool = (int) parser.popNumber();
    }

    } catch (IOException ioe) {
      throw new DawnRuntimeException(this, parser, "unexpected error occured during parsing");
    }
  }
}

// End of WhileFunction.java
