/*
 * $Id: PHPParser.java,v 1.1.1.1 2004/10/19 16:17:06 gfx Exp $
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
public abstract class PHPParser
{
    protected void P_AVANT() {};
    protected void P_DEBUT() {};
    protected void P_FIN_LIGNE() {};
    protected void P_DEBUT_BLOC_CODE() {};
    protected void P_FIN_BLOC_CODE() {};
    protected void P_DEBUT_BLOC_CONDITION() {};
    protected void P_FIN_BLOC_CONDITION() {};
    protected void P_SEPARATEUR() {};
    protected void P_ESPACE() {};
    protected void P_DEBUT_PHP() {};
    protected void P_FIN_PHP() {};
    protected void P_KEYWORD_A() {};
    protected void P_KEYWORD_B() {};
    protected void P_KEYWORD_C() {};
    protected void P_KEYWORD_D() {};
    protected void P_FIN() {};
    protected void P_APRES() {};


    protected final int T_COM1 = 4; // commentaire entre /* */
    protected final int T_COM2 = 8; // commentaire commencant par // ou #
    protected final int T_COM3 = 2; // commentaire type phpdoc /** */
    protected final int T_STR1 = 16; // Chaine de caracteres entre ""
    protected final int T_STR2 = 32; // Chaine de caracteres entre ''
    protected final int T_BLOC1 = 64; //  entre {}
    protected final int T_BLOC2 = 128; //  entre ()
    protected final int T_PHP = 256; // entre <?php ?>

    private String inputstr;
    private int typeblock = 0;
    private char c = '\0';                              // Caractére à analyser
    private int ind;                                      // Indice Courant
    private int dni;                                      // Indice Temporaire
    private int step;                                     // Nombre de carcteres Lu
    private int len;               // Longueur du buffer d'entree

    private boolean nullblock = false; // On est dans block inutile ( commentaire ou chaine de caractères PHP)

    PHPParser(String s)
    {
	this.set(s);
    }

    protected void parse()
    {
	int typetmp = 0; // en attend la fin du traitement pour changer de type
	P_AVANT();

	for (ind = 0; ind < len; ind++) {

	    P_DEBUT();

	    typetmp = 0;
	    dni = 0;
	    step = 0;
	    c = inputstr.charAt(ind);

	    if  ( ! nullblock && ( ( (dni = ind+5) < len && matchWord(ind, dni, "<?php") ) ||
				   ( (dni = ind+3) < len && matchWord(ind, dni, "<?=") ) ||
				   ( (dni = ind+2) < len && matchWord(ind, dni, "<?") )
				   )
		  ) {
		// Debut d'un bloc PHP
		setType(T_PHP);
		P_DEBUT_PHP();
	    }
	    else if (testType(T_PHP)) {
		// Analyse du code PHP

		dni = 0;

		switch (c) {
		case '/':
		    if (!nullblock) {
			// Debut de Commentaires ?
			if ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "//")) {
			    nullblock = true;
			    setType(T_COM2);
			}
			else if ( (dni = ind+3) < len &&  matchWord(ind, dni, "/**")) {
			    nullblock = true;
			    setType(T_COM3);
			}
			else if ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "/*")) {
			    nullblock = true;
			    setType(T_COM1);
			}
			else dni = 0;
		    }
		    break;
		case '#':
		    if (!nullblock) {
			nullblock = true;
			setType(T_COM2);
		    }
		    break;
		case '*':
		    if (nullblock) {
			if ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "*/")) {
			    // Fin de commentaire
			    nullblock = false;
			    if (testType(T_COM1)) {
				typetmp = T_COM1;
			    }
			    else if (testType(T_COM3)) {
				typetmp = T_COM3;
			    }
			}
			else dni = 0;
		    }
		    break;
		case '\n':
		    if (nullblock) {
			if (testType(T_COM2)) {
			    // Fin de commentaire
			    nullblock = false;
			    typetmp = T_COM2;
			}
		    }
		    if (!testType(T_STR1) && !testType(T_STR2)) {
			// On permet l'interception de fin de ligne partout (même dans les commentaires) sauf
			// dans les chaines de caractères
			P_FIN_LIGNE();
		    }

		    break;
		case '"':
		    if (!nullblock) {
			// Début d'une chaine de caractères
			nullblock = true;
			setType(T_STR1);
		    }
		    else if (nullblock && testType(T_STR1) ) {
			if (!inputstr.substring(ind-1, ind+1).equalsIgnoreCase("\\\"") ) {
			    // Fin d'une chaine de caractères
			    nullblock = false;
			    typetmp = T_STR1;
			}
		    }
		    break;
		case '\'':
		    if (!nullblock) {
			// Debut d'une chaine de caractères
			nullblock = true;
			setType(T_STR2);
		    }
		    else if (nullblock && testType(T_STR2) ) {
			if (!inputstr.substring(ind-1, ind).equalsIgnoreCase("\\'") ) {
			    // Fin d'une chaine de caractères
			    nullblock = false;
			    typetmp = T_STR2;
			}
		    }
		    break;
		case '{':
		    if (!nullblock) {
			// Debut d'un bloc de code
			setType(T_BLOC1);

			P_DEBUT_BLOC_CODE();

		    }
		    break;
		case '}':
		    if (!nullblock) {

			P_FIN_BLOC_CODE();

			// Fin d'un bloc de code
			typetmp = T_BLOC1;
		    }
		    break;
		case '(':
		    if (!nullblock) {
			// Debut d'une condition
			setType(T_BLOC2);

			P_DEBUT_BLOC_CONDITION();
		    }
		    break;
		case ')':
		    if (!nullblock) {
			// Fin d'une condition
			typetmp = T_BLOC2;

			P_FIN_BLOC_CONDITION();
		    }
		    break;
		case ',':
		case ';':
		    if (!nullblock) {
			P_SEPARATEUR();
		    }
		    break;
		case ' ':
		case '\t':
		    if (!nullblock) {
			P_ESPACE();
		    }
		    break;
		default:
		    if (!nullblock) {
			if ( (dni = ind+2) <= len &&  matchSeparator(ind, dni, "?>") )  {
			    typetmp = T_PHP;

			    P_FIN_PHP();
			}
			else if ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "->") )  {
			    // On ne fait rien
			}
			else if  (
				  ( (dni = ind+8) < len && matchWord(ind, dni, "function") ) ||
				  ( (dni = ind+8) < len && matchWord(ind, dni, "old_function") ) ||
				  ( (dni = ind+5) < len && matchWord(ind, dni, "class") )
				  ){

			    P_KEYWORD_A();

			}
			else if (
				 ( (dni = ind+4) < len &&  matchWord(ind, dni, "echo") ) ||
				 ( (dni = ind+5) < len &&  matchWord(ind, dni, "print") ) ||
				 ( (dni = ind+6) < len &&  matchWord(ind, dni, "return") ) ||
				 ( (dni = ind+12) < len &&  matchWord(ind, dni, "include_once") ) ||
				 ( (dni = ind+7) < len &&  matchWord(ind, dni, "include") ) ||
				 ( (dni = ind+12) < len &&  matchWord(ind, dni, "require_once") ) ||
				 ( (dni = ind+7) < len &&  matchWord(ind, dni, "require") )
				 ) {
			    // Pseudo Fonction (language construct)
			    // Peut-être utilisé avec des parenthéses à voir pour mieux faire ...

			    P_KEYWORD_B();

			}
			else if (
				 ( (dni = ind+2) < len &&  matchWord(ind, dni, "if") ) ||
				 ( (dni = ind+6) < len &&  matchWord(ind, dni, "elseif") ) ||
				 ( (dni = ind+5) < len &&  matchWord(ind, dni, "while") ) ||
				 ( (dni = ind+3) < len &&  matchWord(ind, dni, "for") ) ||
				 ( (dni = ind+7) < len &&  matchWord(ind, dni, "foreach") ) ||
				 ( (dni = ind+6) < len &&  matchWord(ind, dni, "switch") ) ||
				 ( (dni = ind+3) < len &&  matchWord(ind, dni, "new") ) ||
				 ( (dni = ind+7) < len &&  matchWord(ind, dni, "declare") ) ||
				 ( (dni = ind+4) < len &&  matchWord(ind, dni, "case") ) ||
				 ( (dni = ind+2) < len &&  matchWord(ind, dni, "do") ) ||
				 ( (dni = ind+6) < len &&  matchWord(ind, dni, "global") ) ||
				 ( (dni = ind+6) < len &&  matchWord(ind, dni, "static") ) ||
				 ( (dni = ind+3) < len &&  matchWord(ind, dni, "var") )
				 )  {

			    P_KEYWORD_C();

			}
			else if (
				 ( (dni = ind+7) < len &&  matchWord(ind, dni, "extends") ) ||
				 ( (dni = ind+3) < len &&  matchWord(ind, dni, "xor") ) ||
				 ( (dni = ind+3) < len &&  matchWord(ind, dni, "and") ) ||
				 ( (dni = ind+2) < len &&  matchWord(ind, dni, "as") ) ||
				 ( (dni = ind+2) < len &&  matchWord(ind, dni, "or") ) ||
				 ( (dni = ind+3) < len &&  matchWord(ind, dni, "not") ) ||
				 ( (dni = ind+3) < len &&  matchSeparator(ind, dni, "===") ) ||
				 ( (dni = ind+3) < len &&  matchSeparator(ind, dni, "!==") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "&&") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "||") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "==") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "<>") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "!=") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "<=") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "<<") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, ">>") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "=>") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, "<") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, ".=") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "+=") ) ||
				 ( (dni = ind+2) < len &&  matchSeparator(ind, dni, "-=") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, ">") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, "+") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, "-") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, "*") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, "/") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, "%") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, "=") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, "|") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, "&") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, ":") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, "?") ) ||
				 ( (dni = ind+1) < len &&  matchSeparator(ind, dni, ".") )
				 )
			    {

				P_KEYWORD_D();

			    }
			else  {
			    dni = 0;
			}
		    }
		}
	    }
	    else {
		dni = 0;
		// On est dans de l'HTML

		switch (c) {
		case '\n':

		    P_FIN_LIGNE();

		    break;
		case ' ':
		case '\t':

		    P_ESPACE();

		    break;
		}
	    }
	    step = dni - ind;

	    //System.out.println("c["+String.valueOf(c)+"] ind["+String.valueOf(ind)+"] dni["+String.valueOf(dni)+"] step["+String.valueOf(step)+"] len["+String.valueOf(len)+"]\n");

	    P_FIN();

	    // On change de type apres P_FIN afin de considérer les séparateurs suivants  } */ ) etc...
	    // comme faisant partie du type qui termine
	    if (typetmp != 0) unsetType(typetmp);


	    // Si nécessaire on avance un peu plus vite
	    // On retire 1 car le for ajoute 1
	    if (step > 1) ind += (step - 1);
	}

	P_APRES();

    }

    public void set(String s)
    {
	this.inputstr = s;
	this.len = this.inputstr.length();
    }

    public String get()
    {
	return this.inputstr;
    }

    protected int getType()
    {
	return this.typeblock;
    }

    protected boolean testType(int i)
    {
	return (typeblock & i) == i ? true : false;
    }

    private void setType(int i)
    {
	if ( ! testType(i)) {
	    this.typeblock += i;
	}
    }

    private void unsetType(int i)
    {
	if (testType(i)) {
	    this.typeblock -= i;
	}
    }

    protected char getCharLu()
    {
	return this.c;
    }
    protected int getNbCharLu()
    {
	return this.step;
    }

    protected String getStrLu()
    {

	if (this.step > 1) {
	    return this.inputstr.substring(this.ind, (this.ind+this.step));
	}
	else {
	    return String.valueOf(this.c);
	}
    }

    protected int getIndice()
    {
	return this.ind;
    }

    private boolean matchWord(int ind1, int ind2, String s)
    {
	char lechar;
	try {
	    lechar = this.inputstr.charAt(ind2);
	} catch(Exception e) {
	    lechar = '\0';
	}
	if (this.inputstr.substring(ind1, ind2).equalsIgnoreCase(s) && (lechar == ' ' || lechar == '\t' || lechar == '\n' || lechar == '\0') ) {
	    return true;
	}
	return false;
    }

    private boolean matchSeparator(int ind1, int ind2, String s)
    {
	if (this.inputstr.substring(ind1, ind2).equalsIgnoreCase(s)) {
	    return true;
	}
	return false;
    }

    public void beginWithPHP() {
	setType(T_PHP);
    }

    public void beginWithHTML()
    {
	unsetType(T_PHP);
    }

}
