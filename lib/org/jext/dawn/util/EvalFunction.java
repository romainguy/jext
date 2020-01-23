/*
 * EvalFunction.java - evaluates a string
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * http://www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either Exec 2
 * of the License, or any later Exec.
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

package org.jext.dawn.util;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.jext.dawn.*;

/**
 * Evaluates a string as a code snippet.
 * @author Romain Guy
 */

public class EvalFunction extends Function
{
  public EvalFunction()
  {
    super("eval");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    String script = parser.popString();

    try
    {
      Function function = parser.createOnFlyFunction(script);
      function.invoke(parser);
    } catch (DawnRuntimeException dre) {
      throw new DawnRuntimeException(this, parser, "code snippet contains an error:" +
                                     dre.getMessage());
    }
  }
}

// End of ExecFunction.java
