/*
 * ForFunction.java - for loop
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either For 2
 * of the License, or any later For.
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

package org.jext.dawn.loop;

import java.io.*;
import java.util.*;
import org.jext.dawn.*;

/**
 * For loop.<br>
 * Usage:<br>
 * <code>start end variable for [code] next</code>
 * @author Romain Guy
 */

public class ForFunction extends Function
{
  public ForFunction()
  {
    super("for");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkArgsNumber(this, 3);

    String var = parser.popString();

    if (var.equals("needs") || var.equals("needsGlobal"))
      throw new DawnRuntimeException(this, parser, "you cannot use reserved keyword" +
                                     "\'needs\' or \'needsGlobal\'");
    boolean word = false;
    for (int i = 0; i < var.length(); i++)
    {
      if (Character.isDigit(var.charAt(i)) && !word)
      {
        throw new DawnRuntimeException(this, parser, "bad for-loop counter identifier:" + var);
      } else
        word = true;
    }

    if (parser.getVariables().get(var) != null)
    {
      throw new DawnRuntimeException(this, parser, "for-loop counter identifier already exists");
    }

    int end = (int) parser.popNumber();
    int start = (int) parser.popNumber();

    int innerLoop = 0;
    StreamTokenizer st = parser.getStream();
    StringBuffer buf = new StringBuffer();

    try // LONG LONG TRY
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
          throw new DawnRuntimeException(this, parser, "for without next");
        case StreamTokenizer.TT_WORD:
          if (st.sval.equals("for"))
            innerLoop++;
          else if (st.sval.equals("next"))
          {
            if (innerLoop > 0)
              innerLoop--;
            else
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

    if (start <= end)
    {
      for (int i = start; i < end; i++)
      {
        parser.getVariables().put(var, new Double(i));
        function.invoke(parser);
        parser.getVariables().remove(var);
      }
    } else {
      for (int i = start - 1; i >= end; i--)
      {
        parser.getVariables().put(var, new Double(i));
        function.invoke(parser);
        parser.getVariables().remove(var);
      }
    }

    } catch (IOException ioe) {
      throw new DawnRuntimeException(this, parser, "unexpected error occured during parsing");
    }
  }
}

// End of ForFunction.java
