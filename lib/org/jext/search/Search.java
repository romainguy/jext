/*
 * 06/09/2001 - 12:07:56
 *
 * Search.java - Search methods
 * Copyright (C) 2000 Slava Pestov
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
import org.jext.*;
import org.gjt.sp.jedit.syntax.*;

public class Search
{
  public static SearchMatcher matcher;
  public static String replacePattern, findPattern, pythonScript;
  public static boolean useRegexp = false, ignoreCase = true, script = false, reverseSearch = false;

  public static void load()
  {
    findPattern = Jext.getProperty("find");
    replacePattern = Jext.getProperty("replace");
    useRegexp = Jext.getBooleanProperty("useregexp");
    ignoreCase = Jext.getBooleanProperty("ignorecase");
    script = Jext.getBooleanProperty("replacescript");
    pythonScript = Jext.getProperty("pythonscript");
  }

  public static void save()
  {
    Jext.setProperty("find", findPattern);
    Jext.setProperty("replace", replacePattern);
    Jext.setProperty("pythonscript", pythonScript);
    Jext.setProperty("ignorecase", ignoreCase ? "on" : "off");
    Jext.setProperty("useregexp", useRegexp ? "on" : "off");
    Jext.setProperty("replacescript", script ? "on" : "off");
  }

  public static String getPythonScriptString()
  {
    return pythonScript;
  }

  public static void setPythonScriptString(String pythonScript)
  {
    Search.pythonScript = pythonScript;
  }

  public static boolean getPythonScript()
  {
    return script;
  }

  public static void setPythonScript(boolean script)
  {
    Search.script = script;
  }

  public static boolean getRegexp()
  {
    return useRegexp;
  }

  public static void setRegexp(boolean useRegexp)
  {
    Search.useRegexp = useRegexp;
  }

  public static boolean getIgnoreCase()
  {
    return ignoreCase;
  }

  public static void setIgnoreCase(boolean icase)
  {
    Search.ignoreCase = icase;
  }

  public static void setFindPattern(String findPattern)
  {
    Search.findPattern = findPattern;
  }

  public static String getFindPattern()
  {
    return findPattern;
  }

  public static void setReplacePattern(String replacePattern)
  {
    Search.replacePattern = replacePattern;
  }

  public static String getReplacePattern()
  {
    return replacePattern;
  }

  public static SearchMatcher getSearchMatcher() throws Exception
  {
    return getSearchMatcher(true);
  }

  public static SearchMatcher getSearchMatcher(boolean reverseOK) throws Exception
  {
    //if (matcher != null && (reverseOK || !reverseSearch))
    //  return matcher;

    if (findPattern == null || "".equals(findPattern))
      return null;

    // replace must not be null
    String replace = (Search.replacePattern == null ? "" : Search.replacePattern);

    //String pythonScript = Search.pythonScript;
    //if (script && replace.length() != 0)
    //{
    //Interpreter interp = BeanShell.getInterpreter();
    //interp.eval("_replace(_0,_1,_2,_3,_4,_5,_6,_7,_8,_9)\n{\nreturn (" + replace + ");\n}");
    //replaceMethod = interp.getNameSpace().getMethod("_replace");
    //}

    if (useRegexp)
      matcher = new RESearchMatcher(findPattern, replace, ignoreCase, script, pythonScript);
    else {
      matcher = new BoyerMooreSearchMatcher(findPattern, replace, ignoreCase, reverseSearch && reverseOK,
                                            script, pythonScript);
    }

    return matcher;
  }

  public static boolean find(JextTextArea textArea, final int start) throws Exception
  {
    SearchMatcher matcher = getSearchMatcher(true);
    Segment text = new Segment();
    SyntaxDocument buffer = textArea.getDocument();
    buffer.getText(start, buffer.getLength() - start, text);

    int[] match = matcher.nextMatch(text);
    if (match != null)
    {
      textArea.select(start + match[0], start + match[1]);
      return true;
    } else
      return false;
  }

  public static boolean replace(JextTextArea textArea)
  {
    if(!textArea.isEditable())
    {
      Utilities.beep();
      return false;
    }

    // setSelectedText() clears these values, so save them
    int selStart = textArea.getSelectionStart();
    boolean rect = textArea.isSelectionRectangular();

    if (selStart == textArea.getSelectionEnd())
    {
      Utilities.beep();
      return false;
    }

    try
    {
      SearchMatcher matcher = getSearchMatcher(false);
      if (matcher == null)
      {
        Utilities.beep();
        return false;
      }

      String text = textArea.getSelectedText();
      String replacement = matcher.substitute(text);
      if (replacement == null || replacement.equals(text))
        return false;

      textArea.setSelectedText(replacement);
      //textArea.setSelectionStart(selStart);
      //textArea.setSelectionRectangular(rect);
      return true;
    } catch(Exception e) { }

    return false;
  }

  public static int replaceAll(JextTextArea textArea, int start, int end) throws Exception
  {
    if (!textArea.isEditable())
      return 0;

    SyntaxDocument buffer = textArea.getDocument();
    SearchMatcher matcher = getSearchMatcher(false);
    if (matcher == null)
      return 0;

    int occurCount = 0;
    Segment text = new Segment();
    int offset = start;

loop: for( ; ; )
    {
      buffer.getText(offset, end - offset, text);
      int[] occur = matcher.nextMatch(text);
      if (occur == null)
        break loop;
      int _start = occur[0] + offset;
      int _end = occur[1] - occur[0];
      String found = buffer.getText(_start, _end);
      String subst = matcher.substitute(found);

      end -= (found.length() - subst.length());

      if (subst != null)
      {
        buffer.remove(_start, _end);
        buffer.insertString(_start, subst, null);
        occurCount++;
        offset += occur[0] + found.length();
      } else
        offset += _end;
    }

    return occurCount;
  }
}

// End of Search.java
