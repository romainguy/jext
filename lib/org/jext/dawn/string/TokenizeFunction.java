/*
 * TokenizeFunction.java - tokenizer
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

package org.jext.dawn.string;

import org.jext.dawn.*;

/**
 * Tokenizes a string using a set of default delimiters: "\n \r\f\t"<br>
 * Usage:<br>
 * <code>string tokenize</code><br>
 * It then returns a string per token and the number of tokens on the top
 * of the stack.
 * @author Romain Guy
 */

public class TokenizeFunction extends Function
{
  public TokenizeFunction()
  {
    super("tokenize");
  }

  public void invoke(DawnParser parser) throws DawnRuntimeException
  {
    parser.checkEmpty(this);
    java.util.StringTokenizer token = new java.util.StringTokenizer(parser.popString());
    int tokenCount = token.countTokens();

    for ( ; token.hasMoreTokens(); )
      parser.pushString(token.nextToken());

    parser.pushNumber(tokenCount);
  }
}

// End of TokenizeFunction.java
