/*
 * RESearchMatcher.java - Regular expression matcher
 * Copyright (C) 1999, 2000, 2001 Slava Pestov
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

package org.jext.search;

import javax.swing.text.Segment;

import gnu.regexp.*;
import org.jext.scripting.python.Run;

/**
 * A regular expression string matcher.
 * @author Slava Pestov
 * @version $Id: RESearchMatcher.java,v 1.1.1.1 2004/10/19 16:16:54 gfx Exp $
 */
public class RESearchMatcher implements SearchMatcher
{
  /**
   * Perl5 syntax with character classes enabled.
   * @since jEdit 3.0pre5
   */
  public static final RESyntax RE_SYNTAX_JEXT =
          new RESyntax(RESyntax.RE_SYNTAX_PERL5).set(RESyntax.RE_CHAR_CLASSES).setLineSeparator("\n");

  // private members
  private String replace;
  private RE re;
  private boolean script;
  private String pythonScript;
  String[] replaceArgs;

  /**
   * Creates a new regular expression string matcher.
   */
  public RESearchMatcher(String search, String replace, boolean ignoreCase, boolean script,
                         String pythonScript) throws Exception
  {
    this.replace = replace;
    this.script = script;
    this.pythonScript = pythonScript;
    replaceArgs = new String[10];

    re = new RE(search, (ignoreCase ? RE.REG_ICASE : 0) | RE.REG_MULTILINE, RE_SYNTAX_JEXT);
  }

  /**
   * Returns the offset of the first match of the specified text
   * within this matcher.
   * @param text The text to search in
   * @return an array where the first element is the start offset
   * of the match, and the second element is the end offset of
   * the match
   */
  public int[] nextMatch(Segment text)
  {
    REMatch match = re.getMatch(text);
    if (match == null)
      return null;
    int[] result = { match.getStartIndex(), match.getEndIndex()};
    return result;
  }

  /**
   * Returns the specified text, with any substitution specified
   * within this matcher performed.
   * @param text The text
   */
  public String substitute(String text) throws Exception
  {
    REMatch match = re.getMatch(text);
    if (match == null)
      return null;

    if (script)
    {
      //Interpreter interp = BeanShell.getInterpreter();

      int count = re.getNumSubs();
      for (int i = 1; i <= count; i++)
        replaceArgs[i - 1] = match.toString(i);

      Object obj = Run.eval(pythonScript, "_m", replaceArgs, null);
      if (obj == null)
        return null;
      else
        return obj.toString();
    } else
      return match.substituteInto(replace);
  }
}
