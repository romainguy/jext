/*
 * 05/25/2002 - 22:10:17
 *
 * Dockable.java - Class for dockable pane support
 * Copyright (C) 2003 Paolo Giarrusso
 * blaisorblade_work@yahoo.it
 *
 * Portions copyright (C) 2003 Romain Guy
 * romain.guy@jext.org
 * www.jext.org
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

package org.jext.gui;

import java.awt.*;
import javax.swing.*;
import org.jext.JextFrame;

/**FIXME: change this comment to reflect the correction of the API
 * This class can be used to turn a pane into a dockable.
 * Simply take your JPanel and pass it to the constructor.
 * Then call setDockingStatus to put it where you want. It's that.
 * NOTE: this is version 0 of the API. It's broken. Don't use it. 3.2pre5
 * (or 3.2, I don't know the future name) will have a corrected version of
 * the API, so plugins will be able to use it.
 * It's broken because from a design point of view we don't have an "is-a"
 * relation between this class and JFrame. If we had protected inheritance as
 * in C++, we should use it.
 * Specifically, a DockablePane is not a JFrame because it's not safe to call
 * DockablePane.show(). If a DockablePane is casted up to a JFrame and passed to
 * something which wants to show it, it won't check if it's docked. And if it's docked
 * and we show it, we get an empty JFrame in the screen. So, design is broken.
 * In the future(maybe), this class will support down and right panels. Maybe.
 * @since Jext3.2pre4
 * @version 0
 */
/*
 * Ok, that's the new API: DockablePane doesn't inherit from anything. You build
 * a JFrame, then put it into a DockablePane. So your code(which builds a JFrame)
 * doesn't change a lot: needing little changes was the reason I first thought that
 * "DockablePane extends JFrame". If the JFrame needs to access the DockablePane
 * instance, it can keep a reference to it, passed by the factory method. Yes, you
 * need a factory, because the external code must not be able to access the JFrame;
 * yet, the constructor can be public, so that external code can get a plain JFrame
 * without any docking allowed. However, remember that showing a JFrame which has
 * been docked it's not safe, so getting the JFrame from external code isn't either:
 * so, maybe getFrame() should remain private.
 */
public class Dockable {
  protected JextFrame parent; //it's protected only to ease transiction.
  private JFrame frame;
  private JPanel content;
  private String tabTitle;
  private JTabbedPane ownerPane;
  private DockChangeHandler handler;

  /** The current status */
  private int dockingStatus = HIDDEN;

  /**
   * Mask for the status: if getDockingStatus() &amp; DOCK_MASK != 0 then the panel is visible and not
   * floating.*/
  public static final int DOCK_MASK = 32;

  /* The things to care about are:
   * -the content JPanel
   * -the size of JPanel
   * -hiding/showing the window*/
  /** Constant for the status: hidden*/
  public static final int HIDDEN = 0;
  /** Constant for the status: floating window*/
  public static final int FLOATING = 1;
  /** Constant for the status: docked in the left pane*/
  public static final int DOCK_TO_LEFT_PANEL = DOCK_MASK | 1;
  /** Constant for the status: docked in the top pane*/
  public static final int DOCK_TO_UP_PANEL = DOCK_MASK | 2;
  //These two are not yet implemented.
  /** Constant for the status: docked in the right pane*/
  public static final int DOCK_TO_RIGHT_PANEL = DOCK_MASK | 4;
  /** Constant for the status: docked in the down pane*/
  public static final int DOCK_TO_DOWN_PANEL = DOCK_MASK | 8;

  private Dimension savedMinSize;

  private static final Dimension zeroDim = new Dimension(0, 0);

  /**
   * Returns the frame that will be used to display the panel when the status is FLOATING
   */
  public JFrame getFrame() {
    /*if (frame == null) {
      frame = new JFrame(title);
      if (where == FLOATING)
	System.err.println("We were floating but without a frame.");
    }
    return frame;
    return this;*/
    return frame;
  }

  /*public void show() {
    int currState = getDockingStatus();
    if (currState == HIDDEN) {
      super.show();
      _setDockingStatus(FLOATING);
    } else if (currState != FLOATING) {
      System.err.println("");
    }
  }

  public void hide() {
    int currState = getDockingStatus();
    if (currState == FLOATING) {
      super.hide();
      _setDockingStatus(HIDDEN);
    } else if (currState != HIDDEN) {
      System.err.println("");
    }
  }*/

  /*public DockablePane(JextFrame parent, String title) {
    this(parent, title, title);
  }*/

  /**
   * The only constructor. You can set the parent later if you want, but it's up
   * to you and not guaranted to work.
   * At least, set the correct parent before docking.
   * And change it only when you are undocked.
   */
  public Dockable(JFrame frame, String tabTitle, JextFrame parent, DockChangeHandler handler) {
    this.frame = frame;
    this.parent = parent;
    this.tabTitle = tabTitle;
    this.handler = handler;
  }

  /** Sets the parent Jext window. We put ourself in it if docking and outside of it if not docking*/
  public void setParent(JextFrame parent) {
    this.parent = parent;
  }

  public boolean isDocked()
  {
    return (getDockingStatus() & DOCK_MASK) != 0;
  }

  public int getDockingStatus() {
    //MARK
    if (dockingStatus == FLOATING && !getFrame().isVisible())
      return HIDDEN;
    //MARK
    return dockingStatus;
  }

  /**
   * Sets the internal dockingStatus var to represent the <code>newWhere</code>
   * status.
   * It is for internal use, to incapsulate the dockingStatus logic
   * (i.e. how to set the HIDDEN status, see the 2 versions of the method)
   * from the rest of the code.
   * This method does not interact with the GUI*/
  private void _setDockingStatus(int newWhere) {
    //MARK
    dockingStatus = (newWhere == HIDDEN ? FLOATING: newWhere);
    //MARK
    //dockingStatus = newWhere;
  }

  /**
   * Changes the docking status.
   * @param newWhere the new docking status; must be one of the constants from this class.
   */

  public void setDockingStatus(int newWhere)
  {
    int where = getDockingStatus();

    if (newWhere != HIDDEN && newWhere != FLOATING &&
	newWhere != DOCK_TO_LEFT_PANEL && newWhere != DOCK_TO_UP_PANEL)
      return;
    if (where == newWhere) {
      if (where == FLOATING)
	getFrame().toFront();
      else
	return;
    }

    /* First we need to remove the pane from where it is.
     * If undocking, we restore minimum size.
     */

    boolean wasDocked = (where & DOCK_MASK) != 0;
    boolean goingToDock = (newWhere & DOCK_MASK) != 0;

    if (!(wasDocked || goingToDock)) {
      if (newWhere == FLOATING) {
	showFrame();
      } else if (newWhere == HIDDEN) {
	getFrame().dispose(); //maybe just hide?
      } else {
	(new Exception()).printStackTrace(); //FIXME:should never happen. Remove this.
      }
    } else if (wasDocked && goingToDock) {
      if (ownerPane != null) {
	ownerPane.remove(content);
      }
      putInPane(newWhere);
    } else if (wasDocked) {	//we must undock
      if (ownerPane != null)
	ownerPane.remove(content);

      content.setMinimumSize(savedMinSize);
      getFrame().setContentPane(content);

      if (newWhere == FLOATING) {
	showFrame();
      }
    } else {			//we must dock
      getFrame().dispose();
      content = (JPanel) getFrame().getContentPane();
      getFrame().setContentPane(new JPanel());

      savedMinSize = content.getMinimumSize();
      content.setMinimumSize(zeroDim);

      putInPane(newWhere);
    }

    if (handler != null)
      handler.dockChangeHandler(where, newWhere);

    _setDockingStatus(newWhere);
  }

  //These 2 are used to avoid code repetition.
  private void showFrame() {
    getFrame().pack();
    getFrame().setVisible(true);
    getFrame().toFront();
  }

  private void putInPane(int newWhere) {
    switch (newWhere) {
      case DOCK_TO_LEFT_PANEL:
	ownerPane = parent.getVerticalTabbedPane();
	break;
      case DOCK_TO_UP_PANEL:
	ownerPane = parent.getHorizontalTabbedPane();
	break;
    }
    ownerPane.add(tabTitle, content);
  }
}
