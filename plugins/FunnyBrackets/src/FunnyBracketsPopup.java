/*
 * $Id: FunnyBracketsPopup.java,v 1.1.1.1 2004/10/19 16:16:57 gfx Exp $
 *
 * Funny Brackets Plugin for Jext
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
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.jext.*;
import org.jext.gui.*;


public class FunnyBracketsPopup extends JWindow implements CaretListener
{
    private JextFrame parent;
    private JextTextArea textArea;

    public FunnyBracketsPopup(JextFrame parent,  String word)
    {
	super(parent);

	this.parent = parent;
	this.textArea = parent.getTextArea();

	JPanel pane = new JPanel();
	pane.setLayout(new BorderLayout());

	Font font = new Font("Monospaced", Font.PLAIN, 11);

	JLabel label = new JLabel(word);
	label.setFont(font);
	label.setOpaque(true);
	label.setBackground(Color.yellow);
	pane.add(label, BorderLayout.CENTER);
	pane.setBorder(LineBorder.createBlackLineBorder());
	getContentPane().add(pane);

	GUIUtilities.requestFocus(this, label);
	pack();

	int offset = textArea.getCaretPosition();
	int line = textArea.getCaretLine();
	int x = textArea.offsetToX(line, offset-textArea.getLineStartOffset(line));

	Dimension parentSize = parent.getSize();
	Point parentLocation = parent.getLocationOnScreen();
	Insets parentInsets  = parent.getInsets();

	Point tapLocation = textArea.getLocationOnScreen();
	Dimension popupSize = getSize();

	x += tapLocation.x;
	if ((x + popupSize.width) >  (parentLocation.x + parentSize.width - parentInsets.right))  {
	    x -= popupSize.width;
	}

	FontMetrics fm = getFontMetrics(font);
	setLocation(x, tapLocation.y + textArea.lineToY(line) - fm.getHeight() - fm.getLeading());

	setVisible(true);

	MouseHandler mHandler = new MouseHandler();
	addMouseListener(mHandler);
	textArea.addMouseListener(mHandler);


	KeyHandler kHandler = new KeyHandler();
	addKeyListener(kHandler);
	parent.setKeyEventInterceptor(kHandler);


	textArea.addCaretListener(this);
    }


    public void dispose()
    {
	parent.setKeyEventInterceptor(null);
	textArea.removeCaretListener(this);
	super.dispose();
	SwingUtilities.invokeLater(new Runnable()
	    {
		public void run()
		{
		    textArea.requestFocus();
		}
	    });
    }

    public void caretUpdate(CaretEvent evt) {
	dispose();
    }

    class KeyHandler extends KeyAdapter
    {

	public void keyPressed(KeyEvent evt)
	{
	    // Dés que l'on appuie sur une touche on nique le popup
	    dispose();

	    // est fait suivre l'action ...
	    parent.processKeyEvent(evt);
	}
    }

    class MouseHandler extends MouseAdapter
    {
	public void mouseClicked(MouseEvent me)
	{
	    dispose();
	}
    }


    protected void finalize() throws Throwable
    {
	super.finalize();
	parent = null;
	textArea = null;
    }
}


