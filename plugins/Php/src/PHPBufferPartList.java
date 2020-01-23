/*
 * $Id: PHPBufferPartList.java,v 1.1.1.1 2004/10/19 16:17:05 gfx Exp $
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
import org.jext.*;

public class PHPBufferPartList  extends PHPParser
{
    private PHPBufferPart[] tabPart;
    private int nbPart;
    private int maxPart;
    private StringBuffer unBloc;

    private int debut = 0;

    PHPBufferPartList(String s) {
	super(s);
	//P_AVANT();
    }

    public String getPartOffset(int o)
    {
	for (int j = 0; j < this.getNbPart(); j++)   {
	    if (this.tabPart[j].getStart() <= o && o <= this.tabPart[j].getEnd() ) {
		return (this.tabPart[j].getType());
	    }
	}
	return "HTML";
    }

    public int getNbPart()
    {
	return this.nbPart;
    }

    public PHPBufferPart[] getPart()
    {
	return this.tabPart;
    }

    private void addPart(int b, int e, String t)
    {
	if (b != e) {
	    PHPBufferPart unePart = new PHPBufferPart(b, e, t);
	    if (this.nbPart >= this.maxPart) {
		System.err.println("Plus de 100 parties, c'est quoi ce fichier...");
	    }
	    else {
		this.tabPart[this.nbPart] = unePart;
		this.nbPart++;
	    }
	}
    }

    public  void P_AVANT()
    {
	this.tabPart = new PHPBufferPart[100];
	this.maxPart = 100;
	this.nbPart = 0;
    }

    public  void P_DEBUT_PHP()
    {
	int j = getIndice();
	if (j > debut) {
	    this.addPart(debut, j, "HTML");
	}
	debut = j;
    }

    public  void P_FIN_PHP() {
	int j = getIndice() + 2;
	if (j > debut) {
	    this.addPart(debut, j, "PHP");
	}
	debut = j;
    }

    public  void P_APRES()
    {
	int j = getIndice();
	if (testType(T_PHP)) {
	    if (j > debut) {
		this.addPart(debut, j, "PHP");
	    }
	}
	else {
	    if (j > debut) {
		this.addPart(debut, j, "HTML");
	    }
	}
    }
}


