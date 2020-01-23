/*
 * LiteralSearchMatcher.java - String literal matcher
 * Copyright (C) 1999 Slava Pestov
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

public class LiteralSearchMatcher
{
  // private members
  private char[] search;
  private String replace;
  private boolean ignoreCase;

  /**
   * Creates a new string literal matcher.
   * @param search The search string
   * @param replace The replacement string
   * @param ignoreCase True if the matcher should be case insensitive,
   * false otherwise
   */

  public LiteralSearchMatcher(String search, String replace, boolean ignoreCase)
  {
    if (ignoreCase)
      this.search = search.toUpperCase().toCharArray();
    else
      this.search = search.toCharArray();
    this.replace = replace;
    this.ignoreCase = ignoreCase;
  }

  /**
   * Returns the offset of the first match of the specified text
   * within this matcher.
   * @param text The text to search in
   * @return an array where the first element is the start offset
   * of the match, and the second element is the end offset of
   * the match
   */

  public int[] nextMatch(String text)
  {
    return nextMatch(text, 0);
  }

  public int[] nextMatch(String text, int index)
  {
    char[] textChars = text.toCharArray();

    int searchLen = search.length;
    int len = textChars.length - searchLen + 1;
    if (index >= len)
      return null;

    int result = -1;

    if (ignoreCase)
    {
loop: for (int i = index; i < len; i++)
      {
        if (Character.toUpperCase(textChars[i]) == search[0])
        {
          for (int j = 1; j < searchLen; j++)
          {
            if (Character.toUpperCase(textChars[i + j]) != search[j])
            {
              i += j - 1;
              continue loop;
            }
          }

          result = i;
          break loop;
        }
      }
    } else {
loop: for (int i = index; i < len; i++)
      {
        if (textChars[i] == search[0])
        {
          for (int j = 1; j < searchLen; j++)
          {
            if (textChars[i + j] != search[j])
            {
              i += j - 1;
              continue loop;
            }
          }

          result = i;
          break loop;
        }
      }
    }

    if (result == -1)
      return null;
    else
    {
      int[] match = { result, result + searchLen };
      return match;
    }
  }

  /**
   * Returns the specified text, with any substitution specified
   * within this matcher performed.
   * @param text The text
   */

  public String substitute(String text)
  {
    StringBuffer buf = null;
    char[] textChars = text.toCharArray();
    int lastMatch = 0;
    int searchLen = search.length;
    int len = textChars.length - searchLen + 1;
    boolean matchFound = false;

    if (ignoreCase)
    {
loop: for (int i = 0; i < len;)
      {
        if (Character.toUpperCase(textChars[i]) == search[0])
        {
          for (int j = 1; j < searchLen; j++)
          {
            if (Character.toUpperCase(textChars[i + j]) != search[j])
            {
              i += j;
              continue loop;
            }
          }

          if (buf == null)
            buf = new StringBuffer();
          if (i != lastMatch)
            buf.append(textChars, lastMatch, i - lastMatch);
          buf.append(replace);
          i += searchLen;
          lastMatch = i;
          matchFound = true;
        } else
          i++;
      }
    } else {
loop: for (int i = 0; i < len;)
      {
        if (textChars[i] == search[0])
        {
          for (int j = 1; j < searchLen; j++)
          {
            if (textChars[i + j] != search[j])
            {
              i += j;
              continue loop;
            }
          }

          if (buf == null)
            buf = new StringBuffer();
          if (i != lastMatch)
            buf.append(textChars, lastMatch, i - lastMatch);
          buf.append(replace);
          i += searchLen;
          lastMatch = i;
          matchFound = true;
        } else
          i++;
      }
    }

    if (matchFound)
    {
      if (lastMatch != textChars.length)
        buf.append(textChars, lastMatch, textChars.length - lastMatch);
      return buf.toString();
    } else
      return null;
  }
}

// Enf of LiteralSearchMatcher.java
