/*
 * IfFunction.java - if/else statement
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
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

package org.jext.dawn.test;

import java.io.IOException;
import java.io.StreamTokenizer;

import org.jext.dawn.*;

/**
 * This class provides an implementation of the if/else statement.<br>
 * Usage:<br>
 * <code>if [condition] then [code] else [code] end</code><br>
 * else is optional.
 * @author Romain Guy
 */

public class IfFunction extends Function
{
  public IfFunction()
  {
    super("if");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    StreamTokenizer st = parser.getStream();
    StringBuffer buf = new StringBuffer();

    try // LONG LONG TRY
    {

out: for( ; ; )
    {
      switch(st.nextToken())
      {
        case StreamTokenizer.TT_EOL:
          buf.append('\n');
          break;
        case StreamTokenizer.TT_EOF:
          throw new DawnRuntimeException(this, parser, "if without then");
        case StreamTokenizer.TT_WORD:
          if (st.sval.equals("then"))
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

    int innerTest = 0;
    boolean elseStatement = false;
    StringBuffer ifBuffer = new StringBuffer();
    StringBuffer elseBuffer = new StringBuffer();

out2: for( ; ; )
    {
      switch(st.nextToken())
      {
        case StreamTokenizer.TT_EOL:
          (elseStatement ? elseBuffer : ifBuffer).append('\n');
          break;
        case StreamTokenizer.TT_EOF:
          throw new DawnRuntimeException(this, parser, "if without else or end");
        case StreamTokenizer.TT_WORD:
          if (st.sval.equals("if"))
            innerTest++;
          else if (st.sval.equals("else"))
          {
            if (innerTest == 0)
            {
              elseStatement = true;
              break;
            }
          } else if (st.sval.equals("end")) {
            if (innerTest > 0)
              innerTest--;
            else
              break out2;
          }
          (elseStatement ? elseBuffer : ifBuffer).append(' ' + st.sval);
          break;
        case '"': case '\'':
          (elseStatement ? elseBuffer : ifBuffer).append(" \"" + DawnUtilities.unescape(st.sval) + "\"");
          break;
        case '-':
          (elseStatement ? elseBuffer : ifBuffer).append(" -");
          break;
        case StreamTokenizer.TT_NUMBER:
          (elseStatement ? elseBuffer : ifBuffer).append(" " + st.nval);
      }
    }

    Function function = parser.createOnFlyFunction(buf.toString());
    function.invoke(parser);

    int bool = (int) parser.popNumber();
    if (bool >= 1)
    {
      if (ifBuffer.length() != 0)
      {
        function = parser.createOnFlyFunction(ifBuffer.toString());
        function.invoke(parser);
      }
    } else {
      if (elseBuffer.length() != 0)
      {
        function = parser.createOnFlyFunction(elseBuffer.toString());
        function.invoke(parser);
      }
    }

    } catch (IOException ioe) {
      throw new DawnRuntimeException(this, parser, "unexpected error occured during parsing");
    }
  }
}

// End of IfFunction.java
