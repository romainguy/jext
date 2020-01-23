/*
 * $Id: PHPIndentPEAR.java,v 1.1.1.1 2004/10/19 16:17:06 gfx Exp $
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

public class PHPIndentPEAR extends PHPParser
{
    private int TAILLE_PILE = 20;                     // Nombre maximun de bloc de parenthese imbriquée

    private static int lastIndent = 0;
    private boolean nullblock = false;                  // On est dans block inutile ( commentaire ou chaine de caractères)
    private boolean amettre = true;                     // On ajoute le caractere courant au buffer de sortie
    private boolean addspace = false;                   // On ajoute un blanc aprés le carctere courant
    private boolean addnewline = false;                 // On ajoute un retour à la ligne avant un block {
    private int indentBis = 0;                          // On compte les sous blocs
    private static int lastIndentBis = 0;               // On mémorise le précédent nombre de sous blocs
    private int[] pileIndentBis= new int[TAILLE_PILE];  // Pile mémorisant l'emplacement de la premiere parenthése
    private StringBuffer uneLigne;

    PHPIndentPEAR(String s)
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

    private void parseLigne()
    {
	String laligne = this.uneLigne.toString().trim();

	//System.out.println("Type["+String.valueOf(getType())+"] Ligne["+this.uneLigne.toString()+"]\n");

	// On vient de commencer donc on ajoute rien
	if (newbuf.length() > 0) {
	    this.newbuf.append('\n');
	}


	if (laligne.length() > 0 && laligne.charAt(0) == '*') {
	    // Un commentaire PHPDOC commence forcement par *

	    if (testType(T_COM3)) {
		// On est bien dans commentaire PHPDOC, donc un met un petit blanc
		this.newbuf.append(' ');
	    }
	    else {
		// On aligne l'étoile de fin de commentaire avec l'etoile de début
		this.newbuf.append(' ');
	    }
	}

	// On indent des blocs de code
	if (this.lastIndent < indent) {
	    this.newbuf.append(PHPIndent.createIndent(this.lastIndent,INDENT_SIZE));
	}
	else {
	    this.newbuf.append(PHPIndent.createIndent(indent,INDENT_SIZE));
	}

	// On aligne le contenu des parenthéses
	if (this.indentBis > 0) {
	    if (this.lastIndentBis == this.indentBis) {
		for (int i = 1; i <= this.indentBis; i++)   {
		    this.newbuf.append(PHPIndent.createAlign(pileIndentBis[i]));
		}
	    }
	    else {
		for (int i = 1; i <= this.lastIndentBis; i++)   {
		    this.newbuf.append(PHPIndent.createAlign(pileIndentBis[i]));
		}
	    }
	}
	else if (this.indentBis < 0) {
	    this.indentBis = 0;
	}

	// On ajoute la ligne que l'on vient de traiter
	this.newbuf.append(laligne);

	// Sauvegarde les compteurs d'indentation
	this.lastIndent = indent;
	this.lastIndentBis = this.indentBis;

	// On n'a pas trouvé la déclaration d'une fonction ou d'une classe
	addnewline = false;

	this.uneLigne = new StringBuffer();
    }

    public void indent()
    {
	parse();
    }

    protected void P_AVANT()
    {
	this.uneLigne = new StringBuffer();
	this.newbuf = new StringBuffer();
    }

    protected void P_DEBUT()
    {
	this.amettre = true;
	this.addspace = false;
    }

    protected void P_FIN_LIGNE()
    {
	if (testType(T_PHP)) {
	    this.amettre = false;
	    parseLigne();
	}
    }

    protected void P_DEBUT_BLOC_CODE()
    {
	if (this.addnewline) {
	    // Si on vient de déclarer une fonction ou d'une classe on saute une ligne
	    parseLigne();
	}
	else {
	    // Sinon on met juste un espace devant
	    this.uneLigne.append(' ');
	}

	// On compte
	indent++;
    }

    protected void P_FIN_BLOC_CODE()
    {
	// On Décompte
	indent--;
    }

    protected void P_DEBUT_BLOC_CONDITION()
    {
	this.indentBis++;
	if (this.indentBis < TAILLE_PILE) {
	    pileIndentBis[this.indentBis] = this.uneLigne.length() + 1;
	}
	else this.indentBis--;
    }

    protected void P_FIN_BLOC_CONDITION()
    {
	this.indentBis--;
    }

    protected void P_SEPARATEUR()
    {
	// un espace derriere
	this.addspace = true;

	// On n'a pas trouvé la décaration d'une fonction ou d'une classe
	this.addnewline = false;

    }

    protected void P_ESPACE()
    {
	if (testType(T_PHP) ) {
	    this.amettre = false;
	}
    }

    protected void P_DEBUT_PHP() {
	this.addspace = true;
    }
    protected void P_KEYWORD_A()
    {
	this.addnewline = true;
	this.addspace = true;
    }

    protected void P_KEYWORD_B()
    {
	this.addspace = true;
    }

    protected void P_KEYWORD_C()
    {
	this.addspace = true;
    }

    protected void P_KEYWORD_D()
    {
	// Nous sommes sur un opérateur un seul espace devant
	// si et seulement si il n'y en a déjà pas
	int tmpint;
	if ( (tmpint = this.uneLigne.length() - 1) > 0 &&  this.uneLigne.charAt(tmpint) != ' ') {
	    this.uneLigne.append(' ');
	}

	// et un seul derrière
	this.addspace = true;

    }

    protected void P_FIN() {
	if (testType(T_PHP) && this.amettre ) {
	    this.uneLigne.append(getStrLu());
	    if (this.addspace) {
		this.uneLigne.append(' ');
	    }
	}
    }

    protected void P_APRES() {
	parseLigne();
    }

}

