/*
 * $Id: PHPBrowseTab.java,v 1.1.1.1 2004/10/19 16:17:05 gfx Exp $
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

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.jext.*;
import org.jext.event.*;
import org.jext.gui.JextCheckBox;
import org.jext.gui.JextHighlightButton;



public class PHPBrowseTab extends JPanel implements TreeSelectionListener, ActionListener, Runnable, JextListener
{
    private JextFrame parent;
    private JTree browserTree;
    private DefaultTreeModel browserTreeModel;
    private PHPBrowseTabNode root;

    private Thread thread;

    public PHPBrowseTab(JextFrame parent)
    {

	super();
	this.parent = parent;
	parent.addJextListener(this);

	setLayout(new BorderLayout());


	this.root = new PHPBrowseTabNode("PHP");
	this.browserTreeModel = new DefaultTreeModel(root);
	this.browserTree = new JTree(this.browserTreeModel);

	// Look
	DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	renderer.setOpenIcon(Utilities.getIcon("images/tree_open.gif", Jext.class));
	renderer.setLeafIcon(Utilities.getIcon("images/tree_leaf.gif", Jext.class));
	renderer.setClosedIcon(Utilities.getIcon("images/tree_close.gif", Jext.class));
	renderer.setTextSelectionColor(GUIUtilities.parseColor(Jext.getProperty("vf.selectionColor")));
	renderer.setBackgroundSelectionColor(this.browserTree.getBackground());
	renderer.setBorderSelectionColor(this.browserTree.getBackground());

	this.browserTree.setCellRenderer(renderer);

	DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
	selectionModel.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
	this.browserTree.setSelectionModel(selectionModel);


	// Divers propriétés
	this.browserTree.putClientProperty("JTree.lineStyle", "Angled");
	this.browserTree.setScrollsOnExpand(true);
	this.browserTree.expandRow(1);
	this.browserTree.setRootVisible(true);
	this.browserTree.setShowsRootHandles(true);
	this.browserTree.addTreeSelectionListener(this);

	// Ajout du treeview dans panneau
	this.add(new JScrollPane(this.browserTree), BorderLayout.CENTER);


	// On "thread" cette classe
	this.thread = new Thread(this);
	// On lance l'analyse dans le thread
	this.thread.start();
    }
    public void jextEventFired(JextEvent evt)
    {
	int what = evt.getWhat();

	// Le textarea change ou on en  ouvre/selectionne un nouveau
	if (what == JextEvent.CHANGED_UPDATE ||
	    what == JextEvent.REMOVE_UPDATE ||
	    what == JextEvent.INSERT_UPDATE ||
	    what == JextEvent.TEXT_AREA_SELECTED ||
	    what == JextEvent.TEXT_AREA_OPENED) {
	    if (this.thread.isAlive()) {
		// il ya un thread actif donc on l'arrete
		this.thread.interrupt();
		this.thread = null;
	    }
	    // On lance l'execution d'un nouveau Thread - implicitement on execute la méthode run()
	    this.thread = new Thread(this);
	    this.thread.start();
	}
    }

    /*
     * Lancement du traitement du thread
     */
    public void run()
    {
	try {
	    Thread.sleep(400);
	}
	catch(InterruptedException i) {
	    return;
	}


	this.load(this.parent.getTextAreas());
    }

    public void load(JextTextArea[] textAreas)
    {

	PHPBrowseTabNode newroot = new PHPBrowseTabNode("PHP");
	DefaultTreeModel newbrowserTreeModel = new DefaultTreeModel(newroot);

	for (int i = 0; i < textAreas.length; i++) {

	    if (textAreas[i].getColorizingMode().equals("php3")) {
		PHPBrowseTabNode truc1 = new PHPBrowseTabNode(textAreas[i].getName(), textAreas[i].getFile(), newroot);
		PHPBrowseTabParser p  = new PHPBrowseTabParser(truc1, textAreas[i]);
		p.parse();

		newroot.add(truc1);
	    }
	}
	this.browserTree.setModel(newbrowserTreeModel);
    }
    public void valueChanged(TreeSelectionEvent tse)
    {
	JTree source = (JTree) tse.getSource();
	if (source.isSelectionEmpty()) {
	    return;
	}
	PHPBrowseTabNode node = (PHPBrowseTabNode) source.getSelectionPath().getLastPathComponent();
	JextTextArea textArea = this.parent.getTextArea();
	int no = node.getLine();
	File fi = node.getFile();
	if (no != -1) {
	    if (!fi.equals(textArea.getFile())) {
		textArea = null;
		// Le fichier n'est pas le Fichier courant, donc on le cherche...
		JextTextArea[] openAreas = this.parent.getTextAreas();
		for (int i=0; i < openAreas.length; i++) {
		    if (fi.equals(openAreas[i].getFile())) {
			parent.getTabbedPane().setSelectedComponent(textArea=openAreas[i]);
			break;
		    }
		}
	    }
	    // On verifie que l'on a bien trouvé un fichier
	    if (textArea != null) {
		Element lelement = textArea.getDocument().getDefaultRootElement().getElement(no - 1);

		textArea.select(lelement.getStartOffset(), lelement.getEndOffset() - 1);
	    }
	}
    }
    public void actionPerformed(ActionEvent evt)
    {
    }
}


