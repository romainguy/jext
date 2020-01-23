/*
 * TryCatchFunction.java - try catch
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

package org.jext.dawn.err;

import java.io.*;
import java.util.*;
import org.jext.dawn.*;

/**
 * Try catch block: catches any exception in the block and void
 * the parser to stop.<br>
 * Usage:<br>
 * <code>try [code] catch [code] err</code>
 * @author Romain Guy
 */

public class TryCatchFunction extends Function
{
  public TryCatchFunction()
  {
    super("try");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    StreamTokenizer st = parser.getStream();
    StringBuffer buf = new StringBuffer();

    try // LONG LONG CATCH
    {

    int innerTry = 0;

out: for( ; ; )
    {
      switch(st.nextToken())
      {
        case StreamTokenizer.TT_EOL:
          parser.lineno++;
          break;
        case StreamTokenizer.TT_EOF:
          throw new DawnRuntimeException(this, parser, "try without catch");
        case StreamTokenizer.TT_WORD:
          if (st.sval.equals("try"))
            innerTry++;
          else if (st.sval.equals("err"))
          {
            if (innerTry > 0)
              innerTry--;
          } else if (st.sval.equals("catch")) {
            if (innerTry== 0)
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

    Function function = parser.createOnFlyFunction(buf.toString());
    Function errFunction = null;

    buf = new StringBuffer();
  
out2: for( ; ; )
    {
      switch(st.nextToken())
      {
        case StreamTokenizer.TT_EOL:
          //parser.lineno++;
          buf.append('\n');
          break;
        case StreamTokenizer.TT_EOF:
          throw new DawnRuntimeException(this, parser, "catch without err");
        case StreamTokenizer.TT_WORD:
          if (st.sval.equals("err"))
            break out2;
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
    errFunction = parser.createOnFlyFunction(buf.toString());

    try
    {
      function.invoke(parser);
    } catch (DawnRuntimeException dre) {
      ErrManager.setErr(parser, dre);
      errFunction.invoke(parser);
      parser.setStream(st);
      ///// OUPS /////
      // parser.exec();
    }

    } catch (IOException ioe) {
      throw new DawnRuntimeException(this, parser, "unexpected error occured during parsing");
    }
  }
}

// End of TryCatchFunction.java
