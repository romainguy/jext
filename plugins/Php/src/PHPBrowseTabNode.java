/*
 * $Id: PHPBrowseTabNode.java,v 1.1.1.1 2004/10/19 16:17:05 gfx Exp $
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

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;

public class PHPBrowseTabNode extends DefaultMutableTreeNode
{
    private String name;
    private File file = null;
    private int line = -1;
    private PHPBrowseTabNode parentNode = null;

    // Constructeur pour le Noeud racine
    public PHPBrowseTabNode(String name)
    {
	super(name);
	this.name = name;
    }
    // Constructeur pour une feuille
    public PHPBrowseTabNode(String name, int line, File file, PHPBrowseTabNode parentNode)
    {
	super(name);
	this.name = name;
	this.parentNode = parentNode;
	this.line = line;
	this.file = file;
    }
    // Constructeur pour une branche
    public PHPBrowseTabNode(String name, File file, PHPBrowseTabNode parentNode)
    {
	super(name);
	this.name = name;
	this.parentNode = parentNode;
	this.file = file;
    }

    public int getLine() {
	return this.line;
    }
    public String getName() {
	return this.name;
    }
    public File getFile() {
	return this.file;
    }
}


