/*
 * $Id: PHPBrowseTabParser.java,v 1.1.1.1 2004/10/19 16:17:05 gfx Exp $
 *
 * PHP Plugin for Jext
 *
 * Copyright (C) 2002 Romain Guy rewrite in java by Nicolas Thouvenin
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

import org.jext.*;
import gnu.regexp.*;
import java.io.*;

public class PHPBrowseTabParser extends PHPParser
{
    private PHPBrowseTabNode parentNode;
    private PHPBrowseTabNode root;
    private StringBuffer uneLigne;
    private JextTextArea textArea;

    PHPBrowseTabParser(PHPBrowseTabNode root, JextTextArea textArea) {
	super(textArea.getText(0, textArea.getLength()));
	this.root = root;
	this.parentNode = this.root;
	this.uneLigne = new StringBuffer();
	this.textArea = textArea;
    }

    public  void P_AVANT() {};
    public  void P_DEBUT() {};
    public  void P_FIN_LIGNE() {
		parseLigne();
    };
    public  void P_DEBUT_BLOC_CODE() {};
    public  void P_FIN_BLOC_CODE() {};
    public  void P_DEBUT_BLOC_CONDITION() {};
    public  void P_FIN_BLOC_CONDITION() {};
    public  void P_SEPARATEUR() {};
    public  void P_ESPACE() {};
    public  void P_DEBUT_PHP() {};
    public  void P_FIN_PHP() {
	parseLigne();
    };
    public  void P_KEYWORD_A() {};
    public  void P_KEYWORD_B() {};
    public  void P_KEYWORD_C() {};
    public  void P_KEYWORD_D() {};
    public  void P_FIN() {
	if (testType(T_PHP) && !testType(T_COM1) && !testType(T_COM2)&& !testType(T_COM3) ) {
	    this.uneLigne.append(getStrLu());
	}
    };
    public  void P_APRES() {
	parseLigne();
    };


    void parseLigne()
    {
	String s = uneLigne.toString().trim();
	String name = new String();
	if (s.length() > 0) {
	    try {
		REMatchEnumeration ren;
		REMatch rem;
		RE re = new RE("(function|class)\\s+([^\\s\\(]+)");

		ren = re.getMatchEnumeration(s);

		while (ren.hasMoreMatches()) {
		    rem = ren.nextMatch();
		    name = rem.toString(2);
		}
	    } catch(Exception e) {
		System.err.println(e);
	    }
	    if (name.length() > 0) {

		PHPBrowseTabNode truc2 = new PHPBrowseTabNode(name, this.textArea.getLineOfOffset(getIndice()+1), this.textArea.getFile(), this.root);
		this.root.add(truc2);

		// System.out.println("[>"+name+"<]\n");
	    }
	    uneLigne = new StringBuffer();
	}
    }
}


