/*
 * 22:11:47 24/05/00
 *
 * HTMLIndenter.java
 * Copyright (C) 1999 Romain Guy
 *
 * This	free software; you can redistribute it and/or
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

import org.jext.*;

import javax.swing.text.Element;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;

public class HTMLIndenter extends MenuAction
{
  public HTMLIndenter()
  {
    super("indent_html");
  }

  public void actionPerformed(ActionEvent evt)
  {
    JextTextArea textArea = getTextArea(evt);
    textArea.beginCompoundEdit();
    indentSize = textArea.getTabSize();

    indent = 0;
    char c = '\0';
    lastWasTag = false;
    boolean space = false;

    StringBuffer _buf = new StringBuffer(textArea.getLength());

    String  _tmp = textArea.getText(0, textArea.getLength());
    for (int j = 0; j < _tmp.length(); j++)
    {
      switch (c =_tmp.charAt(j))
      {
        case '\r': case '\n': case ' ': case '\t':
          space = true;
          break;
        default:
          if (space)
          {
            space = false;
            _buf.append(' ');
          }
          _buf.append(c);
      }
    }

    _tmp = null;
    parse(_buf.toString(), textArea);
    textArea.endCompoundEdit();
  }

  public static String[] INDENT_ON_TAG =
  {
    "HTML", "BODY", "HEAD", "TABLE", "TR", "TD", "DIV", "UL", "OL", "FORM", "CENTER",
    "FRAMESET", "NOFRAMES", "SCRIPT"
  };

  private int indent;
  private int indentSize;
  private boolean lastWasTag;

  public static int MAX_LINE_WIDTH;

  private static final int TAG = 1;
  private static final int NULL = -1;
  private static final int CLOSING_TAG = 3;

  private int indentTag(String tag)
  {
    tag = tag.trim();

    for (int i = 0; i < INDENT_ON_TAG.length; i++)
    {
      if (INDENT_ON_TAG[i].equalsIgnoreCase(tag))
      {
        indent++;
        return TAG;
      } else if (tag.equalsIgnoreCase("/" + INDENT_ON_TAG[i])) {
        indent--;
        return CLOSING_TAG;
      }
    }
    return NULL;
  }

  private StringBuffer createIndent(int indent)
  {
    StringBuffer _buf = new StringBuffer();
    for (int i = 0; i < indent; i++)
    {
      for (int j = 0; j < indentSize; j++)
      {
        _buf.append(' ');
      }
    }
    return _buf;
  }

  private void parse(String html, JextTextArea textArea)
  {
    char c = '\0';
    int charCount = 0;
    boolean tag = false;
    StringBuffer buf = new StringBuffer();
    StringBuffer _buf = new StringBuffer();

    for (int i = 0; i < html.length(); i++)
    {
      charCount++;
      switch (c = html.charAt(i))
      {
        case '<':
          tag = true;
          _buf.append(c);
          break;
        case '>':
          if (tag)
          {
            _buf.append('>');
            String _tag = _buf.toString();
            int _name = _tag.indexOf(' ');
            if (_name == -1)
              _name = _tag.length() - 1;
            String tagName = _tag.substring(1, _name);
            switch (indentTag(tagName))
            {
              case TAG:
                buf.append('\n').append(createIndent(indent - 1)).append(_tag);
                charCount = 0;
                lastWasTag = false;
                break;
              case CLOSING_TAG:
                buf.append('\n').append(createIndent(indent)).append(_tag);
                charCount = 0;
                lastWasTag = false;
                break;
              default:
                if (!lastWasTag)
                  buf.append('\n').append(createIndent(indent));
                buf.append(_tag);
                lastWasTag = true;
                break;
            }
            _buf.delete(0, _buf.length());
            tag = false;
          } else
            buf.append(c);
          break;
        case ' ': case '\t':
          if (charCount >= MAX_LINE_WIDTH)
          {
            if (tag)
              _buf.append('\n').append(createIndent(indent));
            else
              buf.append('\n').append(createIndent(indent));
            charCount = 0;
          }
        default:
          if (tag)
            _buf.append(c);
          else
          {
            if (!lastWasTag)
            {
              buf.append('\n').append(createIndent(indent));
              lastWasTag = true;
            }
            buf.append(c);
          }
          break;
      }
    }

    html = null;
    textArea.setText(buf.toString());
  }
}

// End of IndentHTML.java
