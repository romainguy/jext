/*
 * 05/25/2002 - 22:10:17
 *
 * JextFrame.java - A text editor for Java
 * Copyright (C) 1999-2001 Romain Guy
 * Portions copyright (C) 1998-2000 Slava Pestov
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

package org.jext;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.HashMap;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;

import org.gjt.sp.jedit.gui.KeyEventWorkaround;
import org.gjt.sp.jedit.syntax.SyntaxDocument;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.textarea.*;

import org.jext.console.Console;
import org.jext.event.JextEvent;
import org.jext.event.JextListener;
import org.jext.gui.JextButton;
import org.jext.gui.JextHighlightButton;
import org.jext.gui.JextToggleButton;
import org.jext.gui.VoidComponent;
import org.jext.gui.Dockable;
import org.jext.menus.JextMenuBar;
import org.jext.menus.JextRecentMenu;
import org.jext.misc.AutoSave;
import org.jext.misc.FindAccessory;
import org.jext.misc.ProjectPanel;
import org.jext.misc.SaveDialog;
import org.jext.misc.VirtualFolders;
import org.jext.misc.Workspaces;
import org.jext.project.Project;
import org.jext.project.ProjectManager;
import org.jext.project.ProjectManagement;
import org.jext.project.DefaultProjectManager;
import org.jext.project.DefaultProjectManagement;
import org.jext.scripting.dawn.DawnLogWindow;
import org.jext.scripting.python.PythonLogWindow;
import org.jext.scripting.AbstractLogWindow;
import org.jext.search.Search;
import org.jext.toolbar.JextToolBar;
import org.jext.xinsert.XTree;
import org.jext.xml.XBarReader;
import org.jext.xml.XMenuReader;

import com.jgoodies.uif_lite.component.Factory;
import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

import com.jgoodies.plaf.plastic.theme.*;
import com.jgoodies.plaf.HeaderStyle;
import com.jgoodies.plaf.Options;

/**
 * Jext is a fully featured, 100% Pure Java, text editor. It
 * has been mainly designed for programmers, and provides also
 * very useful functions for them (syntax colorization, methods
 * seeker, auto indentation...).
 * @author Romain Guy
 */

public class JextFrame extends JFrame
{
  //////////////////////////////////////////////////////////////////////////////////////////////
  // LOCAL PRIVATE FIELDS
  //////////////////////////////////////////////////////////////////////////////////////////////

  // GUI private members
  private JextToolBar toolBar;
  private JMenu pluginsMenu;
  private JextRecentMenu menuRecent;
  private int _dividerSize;

  // GUI components
  private XTree xtree;
  private Console console;
  private AbstractLogWindow dawnLogWindow;
  private AbstractLogWindow pythonLogWindow;
  private Workspaces workspaces;
  private VirtualFolders virtualFolders;

  // misc
  private AutoSave auto;
  private JFileChooser chooser;
  private FindAccessory accessory;

  // gui
  private JPanel centerPane;
  private JextTabbedPane textAreasPane;
  private JSplitPane split;
  private JTabbedPane hTabbedPane, vTabbedPane;
  private JSplitPane splitter;
  private JSplitPane textAreaSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
  private SimpleInternalFrame rightFrame;
  private SimpleInternalFrame leftFrame;
  private SimpleInternalFrame consolesFrame;

  // splitted edition
  private JextTextArea splittedTextArea;

  // labels
  private JLabel message = new JLabel();
  private JLabel status = new JLabel("v" + Jext.RELEASE +
                                     " - (C)1999-2001 Romain Guy - www.jext.org");

  // misc datas
  private int waitCount, batchMode;
  private ArrayList jextListeners = new ArrayList();
  private ArrayList transientItems = new ArrayList();
  private boolean transientSwitch;
  private KeyListener keyEventInterceptor;
  private InputHandler inputHandler;
  private ProjectManager currentProjectMgr;
  private HashMap projectMgmts;
  private ProjectManagement defaultProjectMgmt;

  //////////////////////////////////////////////////////////////////////////////////////////////
  // PUBLIC AND PRIVATE NON-STATIC METHODS
  //////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the tabbed pane in which text areas are put.
   */

  public JextTabbedPane getTabbedPane()
  {
    return textAreasPane;
  }

  /**
   * Returns the XTree's tabbed pane.
   */

  public JTabbedPane getVerticalTabbedPane()
  {
    return vTabbedPane;
  }

  /**
   * Returns the console's tabbed pane.
   */

  public JTabbedPane getHorizontalTabbedPane()
  {
    return hTabbedPane;
  }

  /**
   * Some external functions may need to manipulate the tree.
   * The XTree itself needs to get other instances to reload'em.
   * @return Current <code>XTree</code> used by Jext
   */

  public XTree getXTree()
  {
    return xtree;
  }

  /**
   * Returns the Virtual Folders panel.
   */

  public VirtualFolders getVirtualFolders()
  {
    return virtualFolders;
  }

  /**
   * Returns the Workspaces panel.
   */

  public Workspaces getWorkspaces()
  {
    return workspaces;
  }

  /**
   * Some external functions may need to manipulate the console.
   * The Scripting system, for instance, need it to launch OS
   * specific commands.
   * @return Current <code>Console</code> used by Jext
   */

  public Console getConsole()
  {
    if (console == null)
    {
      Console c = new Console(this);
      c.setPromptPattern(Jext.getProperty("console.prompt"));
      c.displayPrompt();
      return c;
    }
    return console;
  }

  /**
   * The log window is used by scripting actions.
   * @return Current <code>DawnLogWindow</code> window used by Jext
   */

  public AbstractLogWindow getDawnLogWindow()
  {
    if (dawnLogWindow == null)
      //dawnLogWindow = new DawnLogWindow(this);
      dawnLogWindow = (AbstractLogWindow) DawnLogWindow.getInstance(this).getFrame();
    return dawnLogWindow;
  }

  /**
   * The log window is used by scripting actions.
   * @return Current <code>PythonLogWindow</code> window used by Jext
   */

  public AbstractLogWindow getPythonLogWindow()
  {
    if (pythonLogWindow == null)
      //pythonLogWindow = new PythonLogWindow(this);
      pythonLogWindow = (AbstractLogWindow) PythonLogWindow.getInstance(this).getFrame();
    return pythonLogWindow;
  }

  /**
   * Returns the dock holding the current Dawn LogWindow
   */
  public Dockable getDawnDock() {
    return getDawnLogWindow().getContainingDock();
  }

  /**
   * Returns the dock holding the current Python LogWindow
   */
  public Dockable getPythonDock() {
    return getPythonLogWindow().getContainingDock();
  }

  /**
   * Return the JFileChooser object used to open and save documents
   * By maintaining a single file chooser for each instance of Jext
   * instead of creating and disposing one each time a file is opened
   * or saved, we should see a drastic improvement in memory usage
   * @param  mode  the mode to open the dialog in
   * @return the file chooser
   * @author Ian D. Stewart <idstewart@softhome.net> &amp; Romain Guy
   */

  public JFileChooser getFileChooser(int mode)
  {
    if (chooser == null)
    {
      chooser = new JFileChooser();
      accessory = new FindAccessory(chooser);
    }

    switch (mode)
    {
      case Utilities.OPEN: default:
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle(Jext.getProperty("filechooser.open.title"));

        if (chooser.getAccessory() == null)
          chooser.setAccessory(accessory);

        if (chooser.getChoosableFileFilters().length > 1)
          break;

        ModeFileFilter filter;
        ModeFileFilter selectedFilter = null;
        String _mode = getTextArea().getColorizingMode();

        for (int i = 0; i < Jext.modesFileFilters.size(); i++)
        {
          filter = (ModeFileFilter) Jext.modesFileFilters.get(i);
          chooser.addChoosableFileFilter(filter);
          if (selectedFilter == null && _mode.equals(filter.getModeName()))
            selectedFilter = filter;
        }

        chooser.setFileFilter(selectedFilter == null ? chooser.getAcceptAllFileFilter() :
                                                       selectedFilter);
        break;
      case Utilities.SCRIPT:
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle(Jext.getProperty("filechooser.script.title"));
        chooser.setAccessory(accessory);
        chooser.resetChoosableFileFilters();
        break;
      case Utilities.SAVE:
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle(Jext.getProperty("filechooser.save.title"));
        chooser.setAccessory(null);
        chooser.resetChoosableFileFilters();
        break;
    }

    chooser.setSelectedFile(new File(""));
    chooser.rescanCurrentDirectory();

    return chooser;
  }

  /**
   * Set batch mode state. Batch mode must be set when several operations are
   * made over many text areas. It avoids tools like JBrowse to perform tasks.
   */

  public void setBatchMode(boolean on)
  {
    if (on && batchMode == 0)
      fireJextEvent(JextEvent.BATCH_MODE_SET);
    else if (!on && batchMode == 1)
      fireJextEvent(JextEvent.BATCH_MODE_UNSET);

    batchMode += (on ? 1 : -1);
    if (batchMode < 0)
      batchMode = 0;
  }

  /**
   * Returns batch mode state.
   */

  public boolean getBatchMode()
  {
    return batchMode > 0;
  }

  /**
   * Returns the listener that will handle all key events in this
   * view, if any.
   */

  public final KeyListener getKeyEventInterceptor()
  {
    return keyEventInterceptor;
  }

  /**
   * Sets the listener that will handle all key events in this
   * view. For example, the complete word command uses this so
   * that all key events are passed to the word list popup while
   * it is visible.
   * @param comp The component
   */

  public void setKeyEventInterceptor(KeyListener listener)
  {
    this.keyEventInterceptor = listener;
  }

  /**
   * Records the componence of the GUI for later restauration.
   * Called after basic initialization of the GUI.
   */

  public void freeze()
  {
    // getJextMenuBar().freeze();
    transientSwitch = true;
    getJextToolBar().freeze();
  }

  /**
   * Called after adding a new item to the GUI
   */

  public void itemAdded(Component comp)
  {
    if (transientSwitch == true)
      transientItems.add(comp);
  }

  /**
   * Restores the basic GUI.
   */

  public void reset()
  {
    getJextToolBar().reset();
    for (int i = 0; i < transientItems.size(); i++)
    {
      Component comp = (Component) transientItems.get(i);
      if (comp != null)
      {
        Container parent = comp.getParent();
        if (parent != null)
          parent.remove(comp);
      }
    }

    if (getJextMenuBar() != null)
      getJextMenuBar().reset();
  }

  /**
   * Sets the menu to be used as 'plugins'.
   * @param menu The <code>JMenu</code> used as plugins menu
   */

  public void setPluginsMenu(JMenu menu)
  {
    pluginsMenu = menu;
  }

  /**
   * Fires a Jext event.
   * @param type Event type
   */

  public void fireJextEvent(int type)
  {
    JextEvent evt = new JextEvent(this, type);
    Iterator iterator = jextListeners.iterator();

    while (iterator.hasNext())
      ((JextListener) iterator.next()).jextEventFired(evt);
  }

  /**
   * Fires a Jext event.
   * @param textArea Related event text area
   * @param type Event type
   */

  public void fireJextEvent(JextTextArea textArea, int type)
  {
    JextEvent evt = new JextEvent(this, textArea, type);
    Iterator iterator = jextListeners.iterator();

    while (iterator.hasNext())
      try {
        ((JextListener) iterator.next()).jextEventFired(evt);
      } catch (Throwable t) {
	t.printStackTrace();
      }
  }

  /**
   * Removes all the listeners associated with this instance.
   */

  public void removeAllJextListeners()
  {
    jextListeners.clear();
  }

  /**
   * Adds a propertiesL listener to this class.
   * @param l The <code>JextListener</code> to add
   */

  public void addJextListener(JextListener l)
  {
    jextListeners.add(l);
  }

  /**
   * Remove a specified jext listener from the list.
   * @param l The listener to remove
   */

  public void removeJextListener(JextListener l)
  {
    jextListeners.remove(l);
  }

  /**
   * Load options states from the properties file.
   * Moreover, it sets up the corresponding internal
   * variables and menu items.
   */
  public void loadProperties()
  {
    loadProperties(true);
  }

  public void loadProperties(boolean triggerPanes)
  {
    if (Jext.getBooleanProperty("editor.autoSave"))
      startAutoSave();
    else
      stopAutoSave();

    if (Jext.getBooleanProperty("tips"))
    {
      String tip;
      try
      {
        tip = "Tip: " + Jext.getProperty("tip." + (Math.abs(new Random().nextInt()) %
                                         Integer.parseInt(Jext.getProperty("tip.count"))));
      } catch(Exception e) {
        tip = status.getText();
      }
      status.setText(' ' + tip);
    }

    if (triggerPanes)
      triggerTabbedPanes();
    splitEditor();

    loadButtonsProperties();
    loadConsoleProperties();
    loadTextAreaProperties();

    getTextArea().setParentTitle();
    fireJextEvent(JextEvent.PROPERTIES_CHANGED);
  }

  public void loadButtonsProperties()
  {
    toolBar.setGrayed(Jext.getBooleanProperty("toolbar.gray"));
    toolBar.setVisible(Jext.getBooleanProperty("toolbar"));
    // buttons colors
    JextButton.setHighlightColor(GUIUtilities.parseColor(Jext.getProperty("buttons.highlightColor")));
    JextHighlightButton.setHighlightColor(GUIUtilities.parseColor(Jext.getProperty("buttons.highlightColor")));
    JextToggleButton.setHighlightColor(GUIUtilities.parseColor(Jext.getProperty("buttons.highlightColor")));
  }

  /**
   * Hides or shows the left and top tabbed panes.
   */

  public void triggerTabbedPanes()
  {
    boolean leftShow = Jext.getBooleanProperty("leftPanel.show");

    if (leftShow && vTabbedPane.getTabCount() > 0)
    {
      //if (split.getDividerSize() == 0)
      if (split.getLeftComponent() instanceof VoidComponent)
      {
        split.setDividerSize(_dividerSize);
        split.setLeftComponent(leftFrame);
        split.resetToPreferredSizes();
      }
    } else {
      //if (split.getDividerSize() != 0)
      if (! (split.getLeftComponent() instanceof VoidComponent))
      {
        split.setDividerSize(0);
        split.setLeftComponent(new VoidComponent());
        split.resetToPreferredSizes();
      }
    }

    boolean topShow = Jext.getBooleanProperty("topPanel.show");

    if (topShow && hTabbedPane.getTabCount() > 0)
    {
      if (splitter.getDividerSize() == 0)
      {
        splitter.setDividerSize(_dividerSize);
        splitter.setTopComponent(consolesFrame);
        splitter.resetToPreferredSizes();
        splitter.setBottomComponent(split);

        // SPLITTER BUG FIX
        // If console enabled add splitter back to centerPane and make it visible.
        centerPane.add(BorderLayout.CENTER, splitter);
        centerPane.validate();
        splitter.setVisible(true);
      }
    } else {
      if (splitter.getDividerSize() != 0)
      {
        splitter.setDividerSize(0);
        splitter.setTopComponent(new VoidComponent());
        splitter.resetToPreferredSizes();

        // SPLITTER BUG FIX
        // Since the console isn't visible and setDividerSize isn't hiding the
        // splitter then remove the splitter and add split to the centerPane.
        centerPane.remove(splitter);
        centerPane.add(BorderLayout.CENTER, split);
        centerPane.validate();
      }
      topShow = false;
    }

    //if (leftShow)
    //  split.setBorder(topShow ? null : new MatteBorder(1, 0, 0, 0, Color.gray));
    //else
    //  split.setBorder(null);
  }


  /**
   * Splits/unsplits editor according to the property "editor.splitted".
   */

  public void splitEditor()
  {
    if (Jext.getBooleanProperty("editor.splitted"))
    {
      //if (split.getRightComponent() != textAreaSplitter)
      if (rightFrame.getContent() != textAreaSplitter)
      {
        //split.remove(textAreasPane);
        textAreaSplitter.setTopComponent(textAreasPane);
        //split.setRightComponent(textAreaSplitter);
        rightFrame.setContent(textAreaSplitter);
        textAreaSplitter.setDividerLocation(0.5);
      }

      textAreaSplitter.setOrientation("Horizontal".equals(
                                      Jext.getProperty("editor.splitted.orientation")) ?
                                      JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);

      updateSplittedTextArea(getTextArea());
    } else {
      textAreaSplitter.remove(textAreasPane);
      rightFrame.setContent(textAreasPane);
      //split.setRightComponent(textAreasPane);
      rightFrame.validate();
      JextTextArea textArea = getTextArea();
      if (textArea != null)
      {
        textArea.grabFocus();
        textArea.requestFocus();
      }
    }
  }

  /**
   * Load console properties states from the properties file.
   * Should never been called directly.
   */

  public void loadConsoleProperties()
  {
    if (console != null)
    {
      String promptPattern = Jext.getProperty("console.prompt");
      if (promptPattern != null && !promptPattern.equals(console.getPromptPattern()))
      {
        console.setPromptPattern(promptPattern);
        console.displayPrompt();
      }

      console.setErrorColor(GUIUtilities.parseColor(Jext.getProperty("console.errorColor")));
      console.setPromptColor(GUIUtilities.parseColor(Jext.getProperty("console.promptColor")));
      console.setOutputColor(GUIUtilities.parseColor(Jext.getProperty("console.outputColor")));
      console.setInfoColor(GUIUtilities.parseColor(Jext.getProperty("console.infoColor")));
      console.setBgColor(GUIUtilities.parseColor(Jext.getProperty("console.bgColor")));
      console.setSelectionColor(GUIUtilities.parseColor(Jext.getProperty("console.selectionColor")));
    }
  }

  /**
   * Load text area properties states from the properties file.
   * Should never been called directly.
   */

  public void loadTextAreaProperties()
  {
    loadTextArea(splittedTextArea);
    splittedTextArea.setElectricScroll(0);
    workspaces.loadTextAreas();
  }

  /**
   * Load a given text area properties from the user settings.
   * @param textArea The text area which has to be set
   */

  public void loadTextArea(JextTextArea textArea)
  {
    try
    {
      textArea.setTabSize(Integer.parseInt(Jext.getProperty("editor.tabSize")));
    } catch (NumberFormatException nf) {
      textArea.setTabSize(8);
      Jext.setProperty("editor.tabSize", "8");
    }

    try
    {
      textArea.setElectricScroll(Integer.parseInt(Jext.getProperty("editor.autoScroll")));
    } catch (NumberFormatException nf) {
      textArea.setElectricScroll(0);
    }

    String newLine = Jext.getProperty("editor.newLine");
    if (newLine == null)
      Jext.setProperty("editor.newLine", System.getProperty("line.separator"));

    try
    {
      textArea.setFontSize(Integer.parseInt(Jext.getProperty("editor.fontSize")));
    } catch (NumberFormatException nf) {
      textArea.setFontSize(12);
      Jext.setProperty("editor.fontSize", "12");
    }

    try
    {
      textArea.setFontStyle(Integer.parseInt(Jext.getProperty("editor.fontStyle")));
    } catch (NumberFormatException nf) {
      textArea.setFontStyle(0);
      Jext.setProperty("editor.fontStyle", "0");
    }

    textArea.setFontName(Jext.getProperty("editor.font"));

    org.gjt.sp.jedit.textarea.TextAreaPainter painter = textArea.getPainter();

    try
    {
      painter.setLinesInterval(Integer.parseInt(Jext.getProperty("editor.linesInterval")));
    } catch (NumberFormatException nf) {
      painter.setLinesInterval(0);
    }

    try
    {
      painter.setWrapGuideOffset(Integer.parseInt(Jext.getProperty("editor.wrapGuideOffset")));
    } catch (NumberFormatException nf) {
      painter.setWrapGuideOffset(0);
    }

    painter.setAntiAliasingEnabled(Jext.getBooleanProperty("editor.antiAliasing"));

    painter.setLineHighlightEnabled(Jext.getBooleanProperty("editor.lineHighlight"));
    painter.setEOLMarkersPainted(Jext.getBooleanProperty("editor.eolMarkers"));
    painter.setBlockCaretEnabled(Jext.getBooleanProperty("editor.blockCaret"));
    painter.setLinesIntervalHighlightEnabled(Jext.getBooleanProperty("editor.linesIntervalEnabled"));
    painter.setWrapGuideEnabled(Jext.getBooleanProperty("editor.wrapGuideEnabled"));

    painter.setBracketHighlightColor(GUIUtilities.parseColor(Jext.getProperty("editor.bracketHighlightColor")));
    painter.setLineHighlightColor(GUIUtilities.parseColor(Jext.getProperty("editor.lineHighlightColor")));
    painter.setHighlightColor(GUIUtilities.parseColor(Jext.getProperty("editor.highlightColor")));
    painter.setEOLMarkerColor(GUIUtilities.parseColor(Jext.getProperty("editor.eolMarkerColor")));
    painter.setCaretColor(GUIUtilities.parseColor(Jext.getProperty("editor.caretColor")));
    painter.setSelectionColor(GUIUtilities.parseColor(Jext.getProperty("editor.selectionColor")));
    painter.setBackground(GUIUtilities.parseColor(Jext.getProperty("editor.bgColor")));
    painter.setForeground(GUIUtilities.parseColor(Jext.getProperty("editor.fgColor")));
    painter.setLinesIntervalHighlightColor(GUIUtilities.parseColor(Jext.getProperty("editor.linesHighlightColor")));
    painter.setWrapGuideColor(GUIUtilities.parseColor(Jext.getProperty("editor.wrapGuideColor")));

    loadGutter(textArea.getGutter());
    loadStyles(painter);

    if (textArea.isNew() && textArea.isEmpty())
      textArea.setColorizing(Jext.getProperty("editor.colorize.mode"));
    textArea.putClientProperty("InputHandler.homeEnd",
                               new Boolean(Jext.getBooleanProperty("editor.smartHomeEnd")));
    textArea.setCaretBlinkEnabled(Jext.getBooleanProperty("editor.blinkingCaret"));
    textArea.setParentTitle();
    textArea.repaint();
  }

  // loads the text area's gutter properties

  private void loadGutter(Gutter gutter)
  {
    try
    {
      int width = Integer.parseInt(Jext.getProperty("textArea.gutter.width"));
      gutter.setGutterWidth(width);
    } catch(NumberFormatException nf) { }

    gutter.setCollapsed("yes".equals(Jext.getProperty("textArea.gutter.collapsed")));
    gutter.setLineNumberingEnabled(!"no".equals(Jext.getProperty("textArea.gutter.lineNumbers")));
    try
    {
      int interval = Integer.parseInt(Jext.getProperty("textArea.gutter.highlightInterval"));
      gutter.setHighlightInterval(interval);
    } catch(NumberFormatException nf) {}

    gutter.setAntiAliasingEnabled(Jext.getBooleanProperty("editor.antiAliasing"));

    gutter.setBackground(GUIUtilities.parseColor(Jext.getProperty("textArea.gutter.bgColor")));
    gutter.setForeground(GUIUtilities.parseColor(Jext.getProperty("textArea.gutter.fgColor")));
    gutter.setHighlightedForeground(GUIUtilities.parseColor(Jext.getProperty("textArea.gutter.highlightColor")));
    gutter.setCaretMark(GUIUtilities.parseColor(Jext.getProperty("textArea.gutter.caretMarkColor")));
    gutter.setAnchorMark(GUIUtilities.parseColor(Jext.getProperty("textArea.gutter.anchorMarkColor")));
    gutter.setSelectionMark(GUIUtilities.parseColor(Jext.getProperty("textArea.gutter.selectionMarkColor")));

    String alignment = Jext.getProperty("textArea.gutter.numberAlignment");
    if ("right".equals(alignment))
      gutter.setLineNumberAlignment(Gutter.RIGHT);
    else if ("center".equals(alignment))
      gutter.setLineNumberAlignment(Gutter.CENTER);
    else
      gutter.setLineNumberAlignment(Gutter.LEFT);

    try
    {
      int width = Integer.parseInt(Jext.getProperty("textArea.gutter.borderWidth"));
      gutter.setBorder(width, GUIUtilities.parseColor(Jext.getProperty("textArea.gutter.borderColor")));
    } catch(NumberFormatException nf) { }

    try
    {
      String fontname = Jext.getProperty("textArea.gutter.font");
      int fontsize = Integer.parseInt(Jext.getProperty("textArea.gutter.fontSize"));
      int fontstyle = Integer.parseInt(Jext.getProperty("textArea.gutter.fontStyle"));
      gutter.setFont(new Font(fontname, fontstyle, fontsize));
    } catch(NumberFormatException nf) { }
  }

  // loads the syntax colorizing styles properties. This method
  // is called by loadTextArea() and exists only to separate the
  // code because loadTextArea() was becoming confusing

  private void loadStyles(TextAreaPainter painter)
  {
    try
    {
      SyntaxStyle[] styles = new SyntaxStyle[Token.ID_COUNT];
      styles[Token.COMMENT1] = GUIUtilities.parseStyle(Jext.getProperty("editor.style.comment1"));
      styles[Token.COMMENT2] = GUIUtilities.parseStyle(Jext.getProperty("editor.style.comment2"));
      styles[Token.KEYWORD1] = GUIUtilities.parseStyle(Jext.getProperty("editor.style.keyword1"));
      styles[Token.KEYWORD2] = GUIUtilities.parseStyle(Jext.getProperty("editor.style.keyword2"));
      styles[Token.KEYWORD3] = GUIUtilities.parseStyle(Jext.getProperty("editor.style.keyword3"));
      styles[Token.LITERAL1] = GUIUtilities.parseStyle(Jext.getProperty("editor.style.literal1"));
      styles[Token.LITERAL2] = GUIUtilities.parseStyle(Jext.getProperty("editor.style.literal2"));
      styles[Token.OPERATOR] = GUIUtilities.parseStyle(Jext.getProperty("editor.style.operator"));
      styles[Token.INVALID] = GUIUtilities.parseStyle(Jext.getProperty("editor.style.invalid"));
      styles[Token.LABEL] = GUIUtilities.parseStyle(Jext.getProperty("editor.style.label"));
      styles[Token.METHOD] = GUIUtilities.parseStyle(Jext.getProperty("editor.style.method"));

      painter.setStyles(styles);
    } catch(Exception e) { }
  }


  private void registerPlugins()
  {
    Plugin[] plugins = Jext.getPlugins();
    for (int i = 0; i < plugins.length; i++)
    {
      if (plugins[i] instanceof RegisterablePlugin)
        try
        {
          ((RegisterablePlugin) plugins[i]).register(this);
        } catch (Throwable t) {
          System.err.println("#--Exception occurred while registering plugin:");
          t.printStackTrace();
        }
      if (plugins[i] instanceof ProjectManagement)
        addProjectManagement(((ProjectManagement)plugins[i]));
    }
  }

  /**
   * Recreates the plugins menu.
   */

  public void updatePluginsMenu()
  {
    if (pluginsMenu == null)
      return;
    if (pluginsMenu.getMenuComponentCount() != 0)
      pluginsMenu.removeAll();

    Plugin[] plugins = Jext.getPlugins();
    if (plugins.length == 0) //if (pluginArray.length == 0)
    {
      pluginsMenu.add(GUIUtilities.loadMenuItem(Jext.getProperty("no.plugins"), null,
                                                 null, false));
      return;
    }

    Vector _pluginsMenus = new Vector();
    Vector _pluginsMenuItems = new Vector();

    for (int i = 0; i < plugins.length; i++)
    {
      String pluginModes = Jext.getProperty("plugin." + plugins[i].getClass().getName() + ".modes");
      if (pluginModes == null)  // and therefore it is a permanent plugin
      {
        try
        {
          plugins[i].createMenuItems(this, _pluginsMenus, _pluginsMenuItems);
        } catch (Throwable t) {
          System.err.println("#--Exception while constructing menu items:");
          t.printStackTrace();
        }
      }
    }

    for (int i = 0; i < _pluginsMenus.size(); i++)
      pluginsMenu.add((JMenu) _pluginsMenus.elementAt(i));
    for (int i = 0; i < _pluginsMenuItems.size(); i++)
      pluginsMenu.add((JMenuItem) _pluginsMenuItems.elementAt(i));

    if (pluginsMenu.getItemCount() == 0)
      pluginsMenu.add(GUIUtilities.loadMenuItem(Jext.getProperty("no.plugins"), null,
                                                null, false));
    freeze();
  }


  /**
   * Start the auto saving function. If the <code>Thread</code>
   * used for the auto save is <code>null</code>, we need to
   * create it.
   */

  public void startAutoSave()
  {
    if (auto == null)
      auto = new AutoSave(this);
  }

  /**
   * Stop the auto saving function. We just interrupt the
   * <code>Thread</code> and then 'kill' it.
   */

  public void stopAutoSave()
  {
    if (auto != null)
    {
      auto.interrupt();
      auto = null;
    }
  }

  /**
   * Update status label which displays informations about caret's position.
   * @param textArea The text area which caret status has to be updated
   */

  public void updateStatus(JextTextArea textArea)
  {
    int off = textArea.getCaretPosition();

    Element map = textArea.getDocument().getDefaultRootElement();
    int currLine = map.getElementIndex(off);

    Element lineElement = map.getElement(currLine);
    int start = lineElement.getStartOffset();
    int end = lineElement.getEndOffset();
    int numLines = map.getElementCount();

    status.setText(new StringBuffer().append(' ').append(off - start + 1).append(':')
                   .append(end - start).append(" - ").append(currLine + 1).append('/')
                   .append(numLines).append(" - [ ").append(textArea.getLineTermName()).append(" ] - ")
                   .append(((currLine + 1) * 100) / numLines)
                   .append('%').toString());
  }

  /**
   * Display status of a given text area.
   * @param textArea The text area which status has to be displayed
   */

  public void setStatus(JextTextArea textArea) //this one changes the message label, not status one!
  {
    StringBuffer text = new StringBuffer();
    if (textArea.isEditable())
    {
      text.append(textArea.isDirty() ? Jext.getProperty("editor.modified") : "");
    } else
      text.append(Jext.getProperty("editor.readonly"));

    if (textArea.oneClick != null)
    {
      if (text.length() > 0)
        text.append(" : ");
      text.append("one click!");
    }

    String _text = text.toString();
    if (_text.length() > 0)
      message.setText('(' + _text + ')');
    else
      message.setText("");
  }

  /**
   * Makes the given text area being considered as non modified.
   */

  public void resetStatus(JextTextArea textArea)
  {
    textArea.clean();//TODO for line-end patch
    message.setText("");
    textAreasPane.setCleanIcon(textArea);
  }

  /**
   * When the user create a new file, we need to reset some stuffs
   * such as the bottom labels and the tab icon.
   * @param textArea The text area which was cleared
   */

  public void setNew(JextTextArea textArea)
  {
    message.setText(textArea.isEditable() ? "" : Jext.getProperty("editor.readonly"));
    textAreasPane.setCleanIcon(textArea);
    updateStatus(textArea);
  }

  /**
   * When the text change, we warn the user while displaying a text
   * in the lower right corner.
   * @param textArea The text area which was modified
   */

  public void setChanged(JextTextArea textArea)
  {
    /*if (!textArea.isDirty())
    {*/
      //textArea.setDirty(); //checks moved to JextTextArea
      textAreasPane.setDirtyIcon(textArea);
      setStatus(textArea);
    //}
  }

  /**
   * When the user saves its text, we have to reset modifications done
   * by <code>setChanged()</code>.
   */

  public void setSaved(JextTextArea textArea)
  {
    /*if (textArea.isDirty()) 
    {*/
      //textArea.clean()
      textAreasPane.setCleanIcon(textArea);
      message.setText("");
    //}
  }

  /**
   * Close current window after having checked dirty state
   * of each opened file. It should be called only by {@link Jext#closeToQuit(JextFrame)},
   * which also closes Jext if needed, handling all the background server related issues.
   */

  public void closeToQuit()
  {
    //new SaveDialog(this, SaveDialog.CLOSE_WINDOW);
    workspaces.closeAllWorkspaces();
    Iterator it = projectMgmts.values().iterator();
    while (it.hasNext())
    {
      ProjectManager pm =
       ((ProjectManagement)(it.next())).getProjectManager();
      Project[] project = pm.getProjects();
      for (int i = 0; i < project.length; i++)
      {
        pm.saveProject(project[i]);
      }//end for i...
    }//end while more project managements...
  }

  /**
   * Destroys current window and close JVM.
   */

  public void closeWindow()
  {
    closeWindow(true);
  }

  /**
   * Destroy current window and close JVM if necessary. However the correct way to close a JextFrame
   * (as of Jext3.2pre3) is to call {@link Jext#closeToQuit(JextFrame)}
   * @param jvm If true, we terminate the JVM
   * @deprecated Use closeWindow(), since the JVM is not closed anyway.
   */

  public void closeWindow(boolean jvm)
  {
    //fireJextEvent((Jext.getWindowsCount() == 1) ? JextEvent.KILLING_JEXT : JextEvent.CLOSING_WINDOW);
    //fireJextEvent(JextEvent.CLOSING_WINDOW);
    //events are handled by Jext.closeToQuit. But so they are dispatched before the Save on exit dialog.
    //Will work?

    if (console != null)
      console.stop();
    stopAutoSave();
    removeAllJextListeners();
    Jext.getInstances().remove(this);
    this.dispose();

    /*if (console != null && Jext.getBooleanProperty("console.save"))
      console.save();

    GUIUtilities.saveGeometry(this, "jext");
    Jext.saveXMLProps("Jext v" + Jext.RELEASE + " b" + Jext.BUILD);*/

    //cleanMemory();

    /* // real exit is done by the calling Jext.closeToQuit();
    
    // if all windows have been closed and we are not running the jext background server, and we
    // have been asked to do, then we actually close Jext.
    if (Jext.getWindowsCount() == 0 && ! Jext.isRunningBg() && jvm)
      Jext.finalCleanupBeforeExit();*/
  }
  /* so Jext.java can save the console */
  /*friendly*/ void saveConsole() {
    if (console != null && Jext.getBooleanProperty("console.save"))
      console.save();
  }

  // helps GC to clean up memory a bit

  /*friendly*/ void cleanMemory()
  {
    workspaces.clear();
    workspaces = null;
    transientItems.clear();
    toolBar = null;
    pluginsMenu = null;
    menuRecent = null;
    xtree = null;
    console = null;
    auto = null;
    chooser = null;
    accessory = null;
    centerPane = null;
    textAreaSplitter = split = splitter = null;
    vTabbedPane = hTabbedPane = null;
    textAreasPane = null;
    splittedTextArea = null;
    inputHandler = null;
    transientItems = jextListeners = null;
    keyEventInterceptor = null;

    System.gc();
  }

  /**
   * Check if content of text area has to be saved or not.
   * @return true if user want to close the area, false otherwise
   */

  public boolean checkContent(JextTextArea textArea)
  {
    if (textArea.isDirty() && !textArea.isEmpty())
    {
      textAreasPane.setSelectedComponent(textArea);
      String[] args = { textArea.getName() };
      int response = JOptionPane.showConfirmDialog(this,
                                                   Jext.getProperty("general.save.question", args),
                                                   Jext.getProperty("general.save.title"),
                                                   JOptionPane.YES_NO_CANCEL_OPTION,
                                                   JOptionPane.QUESTION_MESSAGE);
      switch (response)
      {
        case 0:
          textArea.saveContent();
          break;
        case 1:
          break;
        case 2:
          return false;
      }
    }
    return true;
  }

  /**
   * Sets the menu to be used as 'recent'.
   * @param menu The <code>JMenu</code> used as recent menu
   */

  public void setRecentMenu(JextRecentMenu menu)
  {
    menuRecent = menu;
    reloadRecent();
  }

  /**
   * Called by the RecentListener to reload the recent menu.
   */

  public void reloadRecent()
  {
    menuRecent.createRecent();
  }

  /**
   * Clears recent menu.
   */

  public void removeRecent()
  {
    menuRecent.removeRecent();
  }

  /**
   * Saves a file as a recent.
   */

  public void saveRecent(String file)
  {
    menuRecent.saveRecent(file);
  }

  /**
   * Shows the wait cursor.
   */

  public void showWaitCursor()
  {
    if (waitCount++ == 0)
    {
      Cursor cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
      setCursor(cursor);
      JextTextArea[] textAreas = getTextAreas();

      for (int i = 0; i < textAreas.length; i++)
        textAreas[i].getPainter().setCursor(cursor);
    }
  }

  /**
   * Hides the wait cursor.
   */

  public void hideWaitCursor()
  {
    if (waitCount > 0)
      waitCount--;

    if (waitCount == 0)
    {
      Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
      setCursor(cursor);
      cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
      JextTextArea[] textAreas = getTextAreas();

      for(int i = 0; i < textAreas.length; i++)
        textAreas[i].getPainter().setCursor(cursor);
    }
  }

/**
 * Selects the specified form of project management; returns <CODE>true</CODE>
 * if successful.
 * @return <CODE>boolean</CODE>.
 */
  public boolean selectProjectManagement(String name)
  {
    boolean success
     = (projectMgmts.containsKey(name) && projectMgmts.get(name) != null);
    if (success)
    {
      ProjectManager newPM
       = ((ProjectManagement)(projectMgmts.get(name))).getProjectManager();
      if (success = (newPM != null))
      {
        if (currentProjectMgr != newPM)
        {
          if (currentProjectMgr != null)
          {
            vTabbedPane.remove(currentProjectMgr.getUI());
          }//end if there is an old ProjectManager
          currentProjectMgr = newPM;
          if (currentProjectMgr.getUI() != null)
          {
            vTabbedPane.add(Jext.getProperty("vTabbedPane.project"), newPM.getUI());
          }//end if the new ProjectManager has a UI
        }//end if the new one is different from the current one...
      }//end if still okay...
    }//end if such a name...
    return success;
  }//end selectProjectManagement

/**
 * Returns the current <CODE>ProjectManager</CODE>.
 * @return <CODE>ProjectManager</CODE>.
 */
  public ProjectManager getProjectManager()
  {
    return currentProjectMgr;
  }//end getProjectManager

  /**
   * Set Jext's toolbar. SHOULD NOT BE CALLED BY AN
   * EXTERNAL PLUGIN !
   * @param bar The new toolbar
   */

  public void setJextToolBar(JextToolBar bar)
  {
    bar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
    toolBar = bar;
  }

  /**
   * Get Jext's toolbar
   */

  public JextToolBar getJextToolBar()
  {
    return toolBar;
  }

  /**
   * Returns Jext menu bar.
   */

  public JextMenuBar getJextMenuBar()
  {
    return (JextMenuBar) getJMenuBar();
  }

  /**
   * Returns current selected text area.
   */

  public JextTextArea getTextArea()
  {
    if (splittedTextArea != null && splittedTextArea.hasFocus())
      return splittedTextArea;

    return getNSTextArea();
  }

  /**
   * Returns current selected text area, excluding the splitted area.
   */

  public JextTextArea getNSTextArea()
  {
    Component c = textAreasPane.getSelectedComponent();
    if (c instanceof JextTextArea)
    {
      return (JextTextArea) c;
    } else {
      for (int i = textAreasPane.getTabCount() - 1; i >= 0; i--)
      {
        if ((c = textAreasPane.getComponentAt(i)) instanceof JextTextArea)
          return (JextTextArea) c;
      }
    }

    return null;
  }

  /**
   * Returns an array containing all the text areas opened
   * in current window.
   */

  public JextTextArea[] getTextAreas()
  {
    Component c;
    Vector _v = new Vector(textAreasPane.getTabCount());

    for (int i = 0; i < textAreasPane.getTabCount(); i++)
    {
      if ((c = textAreasPane.getComponentAt(i)) instanceof JextTextArea)
        _v.addElement(c);
    }

    JextTextArea[] areas = new JextTextArea[_v.size()];
    _v.copyInto(areas);
    _v = null;

    return areas;
  }

  /**
   * Close a specified file and checks if file is dirty first.
   * @param textArea The file to close
   */

  public void close(JextTextArea textArea)
  {
    close(textArea, true);
  }

  /**
   * Closes a specified file.
   * @param textArea The file to close
   * @param checkContent If true, Jext check if text area is dirty before saving
   */

  public void close(JextTextArea textArea, boolean checkContent)
  {
    if (checkContent && !checkContent(textArea))
      return;

    int index = textAreasPane.indexOfComponent(textArea);
    if (index != -1)
    {
      workspaces.removeFile(textArea);
      textAreasPane.removeTabAt(index);
      textArea.getPainter().setDropTarget(null);

      fireJextEvent(textArea, JextEvent.TEXT_AREA_CLOSED);

      if (getTextAreas().length == 0)
        createFile();

      // saves memory and helps GC
      textArea = null;
    }
  }

  /**
   * Close all the opened files.
   */

  public void closeAll()
  {
    SaveDialog saveDialog = new SaveDialog(this, SaveDialog.CLOSE_TEXT_AREAS_ONLY);
    //////////////////////////////////////////////
    // JextTextArea[] textAreas = getTextAreas();
    // for (int i = 0; i < textAreas.length; i++)
    //   close(textAreas[i]);
  }

  /**
   * Opens a file in a new tabbed pane. In case it is already opened, we ask user if
   * he wants to reload it or open it in a new pane.
   */

  public JextTextArea open(String file)
  {
    return open(file, true);
  }

  /**
   * Opens a file in a new tabbed pane. In case it is already opened, we ask user if
   * he wants to reload it or open it in a new pane.
   * @param addToRecentList If false, the file name is not added to recent list
   */

  public JextTextArea open(String file, boolean addToRecentList)
  {
    if (file == null)
      return null;

    if (!(new File(file)).exists())
    {
      String[] args = { file };
      Utilities.showError(Jext.getProperty("textarea.file.notfound", args));
      return null;
    }

    String _file;
    JextTextArea textArea;
    JextTextArea[] areas = getTextAreas();

out:  for (int i = 0; i < areas.length; i++)
    {
      textArea = areas[i];
      if (textArea.isNew())
        continue;

      _file = textArea.getCurrentFile();
      if (_file != null && _file.equals(file))
      {
        int response = JOptionPane.showConfirmDialog(this,
                       Jext.getProperty("textarea.file.opened.msg", new Object[] { _file }),
                       Jext.getProperty("textarea.file.opened.title"),
                       JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        switch (response)
        {
          case 0:
            textArea.open(_file, addToRecentList);
            textAreasPane.setSelectedComponent(textArea);
            return textArea;
          case 1:
            break out;
          default:
            return null;
        }
      }
    }

    textArea = createTextArea();
    textArea.open(file, addToRecentList);
    addTextAreaInTabbedPane(textArea);
    JextTextArea firstTextArea = (JextTextArea) textAreasPane.getComponentAt(0);
    if (textAreasPane.getTabCount() == 2 && firstTextArea.isNew() && firstTextArea.getLength() == 0)
      close(firstTextArea);

    return textArea;
  }

  public JextTextArea openForLoading(String file)
  {
    if (file == null)
      return null;

    if (!(new File(file)).exists())
    {
      String[] args = { file };
      Utilities.showError(Jext.getProperty("textarea.file.notfound", args));
      return null;
    }

    JextTextArea textArea = new JextTextArea(this);
    new DropTarget(textArea.getPainter(), new DnDHandler());
    textArea.setDocument(new SyntaxDocument());
    textArea.open(file, false);
    addTextAreaInTabbedPane(textArea);

    return textArea;
  }

  // creates a new text area: it constructs it, gives it an
  // input handler, sets default document and finally loads
  // its properties from users settings

  private JextTextArea createTextArea()
  {
    JextTextArea textArea = new JextTextArea(this);
    new DropTarget(textArea.getPainter(), new DnDHandler());
    textArea.setDocument(new SyntaxDocument());
    loadTextArea(textArea);

    return textArea;
  }

  // internal use only: adds a text area in the tabbed pane
  // the area is added, then selected and finally an event
  // of kind TEXT_AREA_OPENED is fired

  private void addTextAreaInTabbedPane(JextTextArea textArea)
  {
    if (workspaces != null)
      workspaces.addFile(textArea);

    textAreasPane.add(textArea);
    fireJextEvent(textArea, JextEvent.TEXT_AREA_OPENED);
    textAreasPane.setSelectedComponent(textArea);
  }

// adds the specified ProjectManagement to the map...
  private void addProjectManagement(ProjectManagement projectMgmt)
  {
    projectMgmts = ((projectMgmts == null) ? new HashMap() : projectMgmts);
    projectMgmts.put(projectMgmt.getLabel(), projectMgmt);
  }//end addProjectManagement

  /**
   * Creates a new empty file.
   */

  public JextTextArea createFile()
  {
    JextTextArea textArea = createTextArea();
    addTextAreaInTabbedPane(textArea);
    return textArea;
  }

  /**
   * Change the name of specified text area.
   * @param textArea The area which name is to be changed
   * @param name The new title which will appear on the tab
   */

  public void setTextAreaName(JextTextArea textArea, String name)
  {
    textAreasPane.setTitleAt(textAreasPane.indexOfComponent(textArea), name);
  }

  /**
   * Updates splitted text area to make it edit the same thing as the
   * selected text area.
   * @param textArea The text area which has to be linked with
   */

  public void updateSplittedTextArea(JextTextArea textArea)
  {
    if (textAreaSplitter.getBottomComponent() == null || textArea == null)
      return;

    splittedTextArea.setDocument(textArea.getDocument());
    String mode = textArea.getColorizingMode();
    if (!mode.equals(splittedTextArea.getColorizingMode()))
      splittedTextArea.setColorizing(mode);
    splittedTextArea.discard();
    splittedTextArea.setEditable(textArea.isEditable());
    setLineTerm(textArea);
  }

  /**
   * If user selects a tab containing something different from a text area,
   * we disable 'splitted' one.
   */

  public void disableSplittedTextArea()
  {
    if (textAreaSplitter.getBottomComponent() == null)
      return;

    splittedTextArea.setDocument(new SyntaxDocument());
    splittedTextArea.setEditable(false);
  }

  /*friendly*/ void setLineTerm(JextTextArea jta) {
    setLineTerm(jta, jta.lineTermSelector.getSelectedIndex());
  }

  /** This method makes the two textareas show the same lineTerm in their ComboBoxes.
   * Needs to be called only when using one combobox to change the line end.
   */
  /*friendly*/ void setLineTerm(JextTextArea jta, int value) {
    JextTextArea toUpdate = null;
    if (splittedTextArea == null)
      return;
    if (jta == splittedTextArea) {
      toUpdate = getNSTextArea();
    } else if (jta == getNSTextArea()) {
      toUpdate = splittedTextArea;
    } else {
      return;
    }
    toUpdate.lineTermSelector.setSelectedIndex(value);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////
  // THE GUI
  //////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Create a new GUI.
   */

  public JextFrame()
  {
    this(null, true);
  }

  /**
   * Create a new GUI.
   * @param args Arguments (to open a file directly)
   */

  public JextFrame(String args[])
  {
    this(args, true);
  }

  JextFrame(String args[], boolean toShow)
  {
    super("Jext - Java Text Editor");
    getContentPane().setLayout(new BorderLayout());

    Jext.setSplashProgress(10);
    Jext.setSplashText(Jext.getProperty("startup.gui"));

    defaultProjectMgmt = new DefaultProjectManagement(this);
    addProjectManagement(defaultProjectMgmt);
    registerPlugins();
    setIconImage(GUIUtilities.getJextIconImage());
    XMenuReader.read(this, Jext.class.getResourceAsStream("jext.menu.xml"), "jext.menu.xml");
    getJMenuBar().putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);

    inputHandler = new DefaultInputHandler(Jext.getInputHandler());

    Jext.setSplashProgress(20);
    Jext.setSplashText(Jext.getProperty("startup.toolbar.build"));
    XBarReader.read(this, Jext.class.getResourceAsStream("jext.toolbar.xml"), "jext.toolbar.xml");

    // create the text areas pane
    splittedTextArea = createTextArea();
    textAreasPane = new JextTabbedPane(this);
    textAreasPane.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);
    textAreasPane.putClientProperty(Options.NO_CONTENT_BORDER_KEY, Boolean.TRUE);

    Jext.setSplashProgress(30);
    Jext.setSplashText(Jext.getProperty("startup.files"));

    workspaces = new Workspaces(this);
    workspaces.load();

    textAreaSplitter.setContinuousLayout(true);
    textAreaSplitter.setTopComponent(textAreasPane);
    textAreaSplitter.setBottomComponent(splittedTextArea);
    textAreaSplitter.setBorder(null);
    // textAreaSplitter.setResizeWeight(1.0);

    Jext.setSplashProgress(50);
    Jext.setSplashText(Jext.getProperty("startup.xinsert"));

    Jext.setSplashText(Jext.getProperty("startup.xinsert.build"));

    rightFrame = new SimpleInternalFrame(null, null, textAreaSplitter);

    vTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
    vTabbedPane.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);
    vTabbedPane.putClientProperty(Options.NO_CONTENT_BORDER_KEY, Boolean.TRUE);
    GUIUtilities.setScrollableTabbedPane(vTabbedPane);
    virtualFolders = new VirtualFolders(this);
    selectProjectManagement(Jext.getProperty("projectManagement.current",
     defaultProjectMgmt.getLabel()));
//    vTabbedPane.add(Jext.getProperty("vTabbedPane.project"), new ProjectPanel(this));

    if (Jext.getBooleanProperty("xtree.enabled"))
    {
      xtree = new XTree(this, "jext.insert.xml");
      vTabbedPane.add(Jext.getProperty("vTabbedPane.xinsert"), xtree);
    }
    //if (vTabbedPane.getTabCount() == 0)
    //  Jext.setProperty("leftPanel.show", "off");
    leftFrame = new SimpleInternalFrame("Tools");
    leftFrame.setContent(vTabbedPane);

    split = Factory.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftFrame, rightFrame, 0.00f);
    split.setContinuousLayout(true);
    _dividerSize = split.getDividerSize();

    Jext.setSplashProgress(60);

    hTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
    hTabbedPane.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);
    hTabbedPane.putClientProperty(Options.NO_CONTENT_BORDER_KEY, Boolean.TRUE);
    GUIUtilities.setScrollableTabbedPane(hTabbedPane);
    if (Jext.getBooleanProperty("console.enabled"))
    {
      // creates console
      console = new Console(this);
      console.setPromptPattern(Jext.getProperty("console.prompt"));
      console.displayPrompt();

      hTabbedPane.add(Jext.getProperty("hTabbedPane.console"), console);
      hTabbedPane.setPreferredSize(console.getPreferredSize());
    }

    consolesFrame = new SimpleInternalFrame("Consoles");
    consolesFrame.setContent(hTabbedPane);
    splitter = Factory.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, consolesFrame, split, 0.00f);
    splitter.setContinuousLayout(true);

    centerPane = new JPanel(new BorderLayout());
    centerPane.add(BorderLayout.CENTER, splitter);

    Jext.setSplashProgress(70);
    Jext.setSplashText(Jext.getProperty("startup.gui"));
    status.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent me) {
        //TODO: check if getNSTextArea() is the right thing.
        JextTextArea jta = getNSTextArea(), jtaSplitted = getTextArea();
        jta.rotateLineTerm();
        if (jtaSplitted != jta)
          jtaSplitted.rotateLineTerm();
        updateStatus(jta);//for the left status bar message
        //about isDirty: the JextFrame.setChanged/setSaved assume if the dirty flag
        //is in the wrong state(false for setChanged/true for setSaved), then the GUI hasn't been updated.
        //This is false here-rotateLineTerm changes actually the dirty flag but doesn't update the GUI.
        if (jta.isDirty())
          textAreasPane.setDirtyIcon(jta);
        else
          textAreasPane.setCleanIcon(jta);
        setStatus(jta);//for the right status bar message
      }
    });

    // we finally add the labels, used to display informations, and the toolbar
    JPanel pane = new JPanel(new BorderLayout());
    pane.add(BorderLayout.WEST, status);
    pane.add(BorderLayout.EAST, message);
    centerPane.add(BorderLayout.SOUTH, pane);
    centerPane.setBorder(new EmptyBorder(6, 0, 0, 0));
    getContentPane().add(BorderLayout.CENTER, centerPane);
    getContentPane().add(BorderLayout.NORTH, toolBar);

    // we load the user geometry
    Jext.setSplashProgress(80);
    Jext.setSplashText(Jext.getProperty("startup.props"));

    pack();
    GUIUtilities.loadGeometry(this, "jext");
    loadProperties(false);

    // here is the window listener which call exit on window closing
    addWindowListener(new WindowHandler());

    Jext.setSplashProgress(90);
    Jext.setSplashText(Jext.getProperty("startup.files"));

    if (args != null)
    {
      workspaces.selectWorkspaceOfNameOrCreate(Jext.getProperty("ws.default"));
      setBatchMode(true);

      for (int i = 0; i < args.length; i++)
      {
        if (args[i] != null)
          open(Utilities.constructPath(args[i]));
      }

      setBatchMode(false);
    }

    updateSplittedTextArea(getTextArea());
    Jext.setSplashProgress(95);
    Jext.setSplashText(Jext.getProperty("startup.plugins"));

    Jext.executeScripts(this);
    JARClassLoader.executeScripts(this);
    updatePluginsMenu();
    toolBar.addMisc(this);
    triggerTabbedPanes();

    Jext.setSplashProgress(100);

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addJextListener(new PluginHandler());
    addJextListener(new ModeHandler());
    // we notify listeners that a new Jext window is opened
    fireJextEvent(JextEvent.OPENING_WINDOW);

    getTextArea().setParentTitle();

    Jext.killSplashScreen();
    setVisible(toShow);

    getTextArea().grabFocus();
    getTextArea().requestFocus();
  }

  /**
   * Returns the input handler.
   */

  public final InputHandler getInputHandler()
  {
    return inputHandler;
  }

  /**
   * Sets the input handler.
   * @param inputHandler The new input handler
   */

  public void setInputHandler(InputHandler inputHandler)
  {
    this.inputHandler = inputHandler;
  }

  /**
   * Forwards key events directly to the input handler.
   * This is slightly faster than using a KeyListener
   * because some Swing overhead is avoided.
   */

  public void processKeyEvent(KeyEvent evt)
  {
    if (getFocusOwner() instanceof JComponent)
    {
      JComponent comp = (JComponent) getFocusOwner();
      InputMap map = comp.getInputMap();
      ActionMap am = comp.getActionMap();

      if (map != null && am != null && comp.isEnabled())
      {
        Object binding = map.get(KeyStroke.getKeyStrokeForEvent(evt));
        if (binding != null && am.get(binding) != null)
          return;
      }
    }

    if (getFocusOwner() instanceof JTextComponent)
    {
      if (evt.getID() == KeyEvent.KEY_PRESSED)
      {
        switch (evt.getKeyCode())
        {
          case KeyEvent.VK_BACK_SPACE:
          case KeyEvent.VK_TAB:
          case KeyEvent.VK_ENTER:
            return;
        }
      }

      Keymap keymap = ((JTextComponent) getFocusOwner()).getKeymap();
      if (keymap.getAction(KeyStroke.getKeyStrokeForEvent(evt)) != null)
        return;
    }

    if (evt.isConsumed())
      return;

    evt = KeyEventWorkaround.processKeyEvent(evt);
    if (evt == null)
      return;

    switch (evt.getID())
    {
      case KeyEvent.KEY_TYPED:
        // Handled in text area
        if (keyEventInterceptor != null)
          keyEventInterceptor.keyTyped(evt);
        else if (inputHandler.isRepeatEnabled())
          inputHandler.keyTyped(evt);
        break;
      case KeyEvent.KEY_PRESSED:
        if (keyEventInterceptor != null)
          keyEventInterceptor.keyPressed(evt);
        else
          inputHandler.keyPressed(evt);
        break;
      case KeyEvent.KEY_RELEASED:
        if (keyEventInterceptor != null)
          keyEventInterceptor.keyReleased(evt);
        else
          inputHandler.keyReleased(evt);
        break;
    }

    if (!evt.isConsumed())
      super.processKeyEvent(evt); 
  }

  //////////////////////////////////////////////////////////////////////////////////////////////
  // INTERNAL CLASSES
  //////////////////////////////////////////////////////////////////////////////////////////////

  class WindowHandler extends WindowAdapter
  {
    public void windowClosing(WindowEvent evt)
    {
      Jext.closeToQuit(JextFrame.this);
    }
  }

  class PluginHandler implements JextListener
  {
    public void jextEventFired(JextEvent evt)
    {
      int what = evt.getWhat();

      if (what == JextEvent.SYNTAX_MODE_CHANGED || what == JextEvent.TEXT_AREA_SELECTED ||
          what == JextEvent.OPENING_WINDOW)
      {
        reset();
        String modeName = evt.getTextArea().getColorizingMode();
        Mode mode = Jext.getMode(modeName);
        ArrayList plugins_ = mode.getPlugins();

        for (int i = 0; i < plugins_.size(); i++)
        {
          Plugin plugin = (Plugin) plugins_.get(i);
          if (plugin != null)
          {
            Vector pluginsMenus = new Vector();
            Vector pluginsMenuItems = new Vector();
            try
            {
              plugin.createMenuItems(JextFrame.this, pluginsMenus, pluginsMenuItems);
            } catch (Throwable t) {
              System.err.println("#--Exception while constructing menu items:");
              t.printStackTrace();
            }
          }
        }
      }
    }
  }

  class ModeHandler implements JextListener
  {
    public void jextEventFired(JextEvent evt)
    {
      if (evt.getWhat() == JextEvent.PROPERTIES_CHANGED)
      {
        for (int i = 0; i < Jext.modesFileFilters.size(); i++)
        {
          ((ModeFileFilter) Jext.modesFileFilters.get(i)).rebuildRegexp();
        }
      }
    }
  }

  class DnDHandler implements DropTargetListener
  {
    public void dragEnter(DropTargetDragEvent evt) { }
    public void dragOver(DropTargetDragEvent evt) { }
    public void dragExit(DropTargetEvent evt) { }
    public void dragScroll(DropTargetDragEvent evt) { }
    public void dropActionChanged(DropTargetDragEvent evt) { }

    public void drop(DropTargetDropEvent evt)
    {
      DataFlavor[] flavors = evt.getCurrentDataFlavors();
      if (flavors == null)
        return;

      boolean dropCompleted = false;
      for (int i = flavors.length - 1; i >= 0; i--)
      {
        if (flavors[i].isFlavorJavaFileListType())
        {
          evt.acceptDrop(DnDConstants.ACTION_COPY);
          Transferable transferable = evt.getTransferable();
          try
          {
            final Iterator iterator = ((List) transferable.getTransferData(flavors[i])).iterator();

            // what a fix !!!!! (JDK 1.4, JVM hanging on drag and drop if file was already opened)
            SwingUtilities.invokeLater(new Runnable()
            {
              public void run()
              {
                while (iterator.hasNext())
                {
                  open(((File) iterator.next()).getPath());
                }
              }
            });

            dropCompleted = true;

          } catch (Exception e) { }
        }
      }
      evt.dropComplete(dropCompleted);
    }
  }


}

// End of JextFrame.java 