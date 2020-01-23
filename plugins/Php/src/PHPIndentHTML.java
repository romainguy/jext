/*
 * $Id: PHPIndentHTML.java,v 1.1.1.1 2004/10/19 16:17:06 gfx Exp $
 *
 * PHP Plugin for Jext
 *
 * Copyright (C) 2002 Nicolas Thouvenin
 * touv at yahoo dot fr
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
public class PHPIndentHTML
{
    String buffer;
    public StringBuffer newbuf;

    PHPIndentHTML(String s)
    {
	this.buffer = s;
	this.newbuf = new StringBuffer();
    }

    public int INDENT_SIZE = 2;
    public int indent = 0;

    public void setIndentSize(int i)
    {
	this.INDENT_SIZE = i;
    }
    public String get()
    {
	return this.newbuf.toString();
    }

    public int MAX_LINE_WIDTH = 80;

    public void setMaxLineWidth(int i)
    {
	this.MAX_LINE_WIDTH = i;
    }

    public void indent()
    {
	parse(onOneLine());
    }

    private String onOneLine()
    {
	char c = '\0';
	boolean space = false;

	StringBuffer _buf = new StringBuffer(this.buffer.length());
	for (int j = 0; j < this.buffer.length(); j++)  {
	    switch (c = this.buffer.charAt(j))   {
	    case '\r': case '\n': case ' ': case '\t':
		space = true;
		break;
	    default:
		if (space) {
		    space = false;
		    _buf.append(' ');
		}
		_buf.append(c);
	    }
	}
	return _buf.toString();
    }


    private int indentTag(String tag)
    {
	tag = tag.trim();

	for (int i = 0; i < INDENT_ON_TAG.length; i++)  {
	    if (INDENT_ON_TAG[i].equalsIgnoreCase(tag))   {
		indent++;
		return TAG;
	    }
	    else if (tag.equalsIgnoreCase("/" + INDENT_ON_TAG[i])) {
		indent--;
		return CLOSING_TAG;
	    }
	}
	return NULL;
    }


    private void parse(String html)
    {
	char c = '\0';
	int charCount = 0;
	boolean tag = false;
	StringBuffer _buf = new StringBuffer();

	for (int i = 0; i < html.length(); i++) {
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
					this.newbuf.append('\n').append(PHPIndent.createIndent(indent - 1, INDENT_SIZE)).append(_tag);
					charCount = 0;
					lastWasTag = false;
					break;
				    case CLOSING_TAG:
					this.newbuf.append('\n').append(PHPIndent.createIndent(indent, INDENT_SIZE)).append(_tag);
					charCount = 0;
					lastWasTag = false;
					break;
				    default:
					if (!lastWasTag)
					    this.newbuf.append('\n').append(PHPIndent.createIndent(indent, INDENT_SIZE));
					this.newbuf.append(_tag);
					lastWasTag = true;
					break;
				    }
				_buf.delete(0, _buf.length());
				tag = false;
			    } else
				this.newbuf.append(c);
			break;
		    case ' ': case '\t':
			if (charCount >= MAX_LINE_WIDTH)
			    {
				if (tag)
				    _buf.append('\n').append(PHPIndent.createIndent(indent, INDENT_SIZE));
				else
				    this.newbuf.append('\n').append(PHPIndent.createIndent(indent, INDENT_SIZE));
				charCount = 0;
			    }
		    default:
			if (tag)
			    _buf.append(c);
			else
			    {
				if (!lastWasTag)   {
				    this.newbuf.append('\n').append(PHPIndent.createIndent(indent, INDENT_SIZE));
				    lastWasTag = true;
				}
				this.newbuf.append(c);
			    }
			break;
		    }
	    }
	html = null;
    }



    public static String[] INDENT_ON_TAG =
    {
	"HTML", "BODY", "HEAD", "TABLE", "TR", "TD", "DIV", "UL", "OL", "FORM", "CENTER",
	"FRAMESET", "NOFRAMES", "SCRIPT"
    };



    private boolean lastWasTag = false;


    private static final int TAG = 1;
    private static final int NULL = -1;
    private static final int CLOSING_TAG = 3;

}
