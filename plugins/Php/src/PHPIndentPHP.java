/*
 * $Id: PHPIndentPHP.java,v 1.1.1.1 2004/10/19 16:17:06 gfx Exp $
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

public class PHPIndentPHP extends PHPParser
{
    private StringBuffer uneLigne;


    PHPIndentPHP(String s)
    {
	super(s);
	//P_AVANT();
    }

    public StringBuffer newbuf;
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

    private int countChar(String s, char c)
    {
	int i = 0;
	int cpt = 0;
	int ret;
	while (true) {
	    ret = s.indexOf(c,i);
	    if (ret < 0) return cpt;
	    else {
		cpt++;
		i = ret + 1;
	    }
	}
    }

    private void parseLigne()
    {
	String s = this.uneLigne.toString();

	if (s.length() != 0) {
	    int t = countChar(s, '{') - countChar(s, '}');

	    StringBuffer tmpstr = new StringBuffer();

	    if (this.newbuf.length() != 0) tmpstr.append('\n');

	    if (t < 0) {
		tmpstr.append(PHPIndent.createIndent(indent+t,INDENT_SIZE));
	    }
	    else {
		tmpstr.append(PHPIndent.createIndent(indent,INDENT_SIZE));
	    }
	    tmpstr.append(s.trim());

	    indent += t;

	    this.newbuf.append(tmpstr.toString());
	}
	this.uneLigne = new StringBuffer();
    }

    public void indent()
    {
	parse();
    }

    public  void P_AVANT()
    {
	this.uneLigne = new StringBuffer();
	this.newbuf = new StringBuffer();
    }

    public  void P_FIN_LIGNE()
    {
	parseLigne();
    }
    public  void P_FIN()
    {
	if (testType(T_PHP)) {
	    this.uneLigne.append(getStrLu());
	}
    }
    public  void P_APRES()
    {
	parseLigne();
    }


}
