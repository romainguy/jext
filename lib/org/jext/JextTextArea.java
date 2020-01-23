/*
 * 03/13/2003 - 17:27:08
 *
 * JextTextArea.java - An extended JEditTextArea
 * Copyright (C) 1998-2003 Romain Guy
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

import gnu.regexp.*;

import java.lang.reflect.Method;
import java.io.*;

import java.util.StringTokenizer;
import java.util.zip.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import javax.swing.undo.*;
import javax.swing.event.*;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.Position;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;

import org.jext.event.JextEvent;
import org.jext.misc.Workspaces;
import org.jext.misc.ZipExplorer;
import org.jext.search.*;
import org.jext.xml.XPopupReader;

import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;

/**
 * Extending JEditTextArea allow us to support syntax colorization. We also implement
 * some listeners: for the caret, for the undos and redos, for the keys (indent)
 * and for the modifications which can occures in the text. This component provides
 * its own methods to read and save files (even to zip them).
 * @author Romain Guy
 * @see Jext
 */

public class JextTextArea extends JEditTextArea implements UndoableEditListener, DocumentListener
{
  // static fields
  private static JPopupMenu popupMenu;

  // private fields
  private JextFrame parent;

  // misc properties
  private String mode;
  private long modTime;
  private Position anchor;
  private int fontSize, fontStyle;
  private String fontName, currentFile;

  /*friendly*/ JComboBox lineTermSelector;

  // undo
  private boolean undoing;
  private UndoManager undo = new UndoManager();
  private CompoundEdit compoundEdit, currentEdit = new CompoundEdit();
  private boolean dirty, newf, operation, protectedCompoundEdit;
  //newf says whether this is a new file or not

  // highlighters
  private SearchHighlight searchHighlight;

  /** This constant defines the size of the buffer used to read files */
  public static final int BUFFER_SIZE = 32768;

  public static final int DOS_LINE_END   = 0;
  public static final int MACOS_LINE_END = 1;
  public static final int UNIX_LINE_END  = 2;

  //let's remember the line end of the original file(as of Jext3.2pre3)
  private String myLineTerm = "\n", origLineTerm = "\n";

  /**
   * The constructor add the necessary listeners, set some stuffs
   * (caret color, borers, fonts...).
   * @param parent <code>JextTextArea</code> needs a <code>Jext</code> parent
   * because it provides a lot of 'vital' methods
   */

  public JextTextArea(JextFrame parent)
  {
    super(parent);

    addCaretListener(new CaretHandler());
    addFocusListener(new FocusHandler());
    setMouseWheel();

    undo.setLimit(1000);

    setBorder(null);
    getPainter().setInvalidLinesPainted(false);

    this.parent = parent;
    Font defaultFont = new Font("Monospaced", Font.PLAIN, 12);
    fontName = defaultFont.getName();
    fontSize = defaultFont.getSize();
    fontStyle = defaultFont.getStyle();
    setFont(defaultFont);

    modTime = -1;

    if (popupMenu == null)
    {
      new JextTextAreaPopupMenu(this);
    } else
      setRightClickPopup(popupMenu);

    newf = true;
    setTabSize(8);
    resetLineTerm();

    FontMetrics fm = getFontMetrics(getFont());
    setMinimumSize(new Dimension(40 * fm.charWidth('m'), 5 * fm.getHeight()));
    setPreferredSize(new Dimension(80 * fm.charWidth('m'), 15 * fm.getHeight()));

    add(LEFT_OF_SCROLLBAR, lineTermSelector = new JComboBox(new String[] { "DOS", "Mac", "UNIX" }));
    lineTermSelector.setSelectedItem(getLineTermName());
    lineTermSelector.addActionListener(new ActionListener()
    {
      JextFrame parent = JextTextArea.this.getJextParent();
      public void actionPerformed(ActionEvent evt)
      {
        int idx = lineTermSelector.getSelectedIndex();
        setLineTerm(idx);
        parent.updateStatus(JextTextArea.this);
        parent.setLineTerm(JextTextArea.this, idx);  //this updates the other TextArea if
        //the JextFrame is splitted
        if (JextTextArea.this.isDirty())
          parent.getTabbedPane().setDirtyIcon(JextTextArea.this);
        else
          parent.getTabbedPane().setCleanIcon(JextTextArea.this);
        parent.setStatus(JextTextArea.this);
      }
    });

    mode = ""; //Jext.getProperty("editor.colorize.mode");
  }

  private void setLineTerm(String newLineTerm)
  {//for now it's private so nothing changes.
    myLineTerm = newLineTerm;
  }

  private String getLineTerm()
  {
    return myLineTerm;
  }

  private void resetLineTerm()
  {
    myLineTerm = Jext.getProperty("editor.newLine");
    if (myLineTerm == null || "".equals(myLineTerm)) { //happens on first run.
      //And made 3.2pre3 crash the day before official release, while trying built version.
      myLineTerm = System.getProperty("line.separator");
      Jext.setProperty("editor.newLine", myLineTerm);
    }
    storeOrigLineTerm();
  }

  private void storeOrigLineTerm()
  {
    origLineTerm = myLineTerm;
  }

  public boolean isLineTermChanged()
  {
    if (myLineTerm != null)
      return !myLineTerm.equals(origLineTerm);
    else
      return false;
  }
  //this will be checked probably by internal JextTextArea methods; I think that JextTextArea will call
  //something as parent.setChanged, only a bit different.

  /*friendly*/ void setLineTerm(int lineTermConst)
  {
    switch(lineTermConst)
    {
      case UNIX_LINE_END:
        myLineTerm = "\n";
        break;
      case MACOS_LINE_END:
        myLineTerm = "\r";
        break;
      case DOS_LINE_END:
        myLineTerm = "\r\n";
        break;
    }
  }

  /*friendly*/ String getLineTermName()
  {
    if ("\r".equals(myLineTerm))
      return "Mac";
    if ("\n".equals(myLineTerm))
      return "UNIX";
    if ("\r\n".equals(myLineTerm))
      return "DOS";
    return "UNIX";
  }

  /*friendly*/ void rotateLineTerm()
  {
    if (myLineTerm.equals("\r"))
      myLineTerm = "\n";
    else if (myLineTerm.equals("\n"))
      myLineTerm = "\r\n";
    else if (myLineTerm.equals("\r\n"))
      myLineTerm = "\r";
    if (isLineTermChanged())
      parent.setChanged(this);
    else if (!isDirty())
      parent.setSaved(this);
    lineTermSelector.setSelectedItem(getLineTermName());
  }

  private void setMouseWheel()
  {
    if (Utilities.JDK_VERSION.charAt(2) >= '4')
    {
      try
      {
        Class cl = Class.forName("org.jext.JavaSupport");
        Method m = cl.getMethod("setMouseWheel", new Class[] { getClass() });
        if (m !=  null)
        m.invoke(null, new Object[] { this });
      } catch (Exception e) { }
    }
  }

  /**
   * Adds a search highlighter if none exists.
   */

  public void initSearchHighlight()
  {
    if (searchHighlight == null)
    {
      searchHighlight = new SearchHighlight();
      getPainter().addCustomHighlight(searchHighlight);
    }
  }

  /**
   * Returns the associated search highlighter.
   */

  public SearchHighlight getSearchHighlight()
  {
    return searchHighlight;
  }

  /**
   * Returns text area popup menu.
   */

  public static JPopupMenu getPopupMenu()
  {
    return popupMenu;
  }

  /**
   * Get property inherent to current syntax colorizing mode.
   */

  public String getProperty(String key)
  {
    return Jext.getProperty("mode." + mode + '.' + key);
  }

  /**
   * Set a new document
   */

  public void setDocument(org.gjt.sp.jedit.syntax.SyntaxDocument document)
  {
    document.removeUndoableEditListener(this);
    document.removeDocumentListener(this);
    super.setDocument(document);
    document.addDocumentListener(this);
    document.addUndoableEditListener(this);
  }

  /**
   * Return current font's name
   */

  public String getFontName()
  {
    return fontName;
  }

  /**
   * Return current font's size
   */

  public int getFontSize()
  {
    return fontSize;
  }

  /**
   * Return current font's style (bold, italic...)
   */

  public int getFontStyle()
  {
    return fontStyle;
  }

  /**
   * Set the font which has to be used.
   * @param name The name of the font
   */

  public void setFontName(String name)
  {
    fontName = name;
    changeFont();
  }

  /**
   * Set the size of the font.
   * @param size The new font's size
   */

  public void setFontSize(int size)
  {
    fontSize = size;
    changeFont();
    FontMetrics fm = getFontMetrics(getFont());
    setMinimumSize(new Dimension(80 * fm.charWidth('m'), 5 * fm.getHeight()));
    repaint();
  }

  /**
   * Set the style of the font.
   * @param style The new style to apply
   */

  public void setFontStyle(int style)
  {
    fontStyle = style;
    changeFont();
    repaint();
  }

  /**
   * Set the new font.
   */

  private void changeFont()
  {
    getPainter().setFont(new Font(fontName, fontStyle, fontSize));
  }

  /**
   * Show/hide waiting cursor
   */

  public void waitingCursor(boolean on)
  {
    if (on)
    {
      parent.showWaitCursor();
    } else {
      parent.hideWaitCursor();
    }
  }

  /**
   * This is necessary to determine if we have to indent on tab key press or not.
   */

  public static boolean getTabIndent()
  {
    return Jext.getBooleanProperty("editor.tabIndent");
  }

  /**
   * This is necessary to determine if we have to indent on enter key press or not.
   */

  public static boolean getEnterIndent()
  {
    return Jext.getBooleanProperty("editor.enterIndent");
  }

  /**
   * Return the state of the softtab check menu item.
   * This is necessary to know if tabs have to be replaced
   * by whitespaces.
   */

  public static boolean getSoftTab()
  {
    return Jext.getBooleanProperty("editor.softTab");
  }

  /**
   * When an operation has began, setChanged() cannot be called.
   * This is very important when we need to insert or remove some
   * parts of the text without turning on the 'to_be_saved' flag.
   */

  public void beginOperation()
  {
    operation = true;
    waitingCursor(true);
  }

  /**
   * Calling this will allow the DocumentListener to use setChanged().
   */

  public void endOperation()
  {
    operation = false;
    waitingCursor(false);
  }

  /**
   * Return the parent of this component. Note that a LOT of
   * external functions need to call methods contained in the parent.
   */

  public JextFrame getJextParent()
  {
    return parent;
  }

  /**
   * Return true if we can use the setChanged() method,
   * false otherwise.
   */

  public boolean getOperation()
  {
    return operation;
  }

  /**
   * Return current opened file as a <code>File</code> object.
   */

  public File getFile()
  {
    return (currentFile == null ? null : new File(currentFile));
  }

  /**
   * Return the full path of the opened file.
   */

  public String getCurrentFile()
  {
    return currentFile;
  }

  /**
   * Set path of current opened file.
   */

  public void setCurrentFile(String path)
  {
    currentFile = path;
  }

  /**
   * Performs a 'filtered' paste. A filtered paste is a paste action performed after having
   * made some search and replace operations over the clipboard text.
   */

  public void filteredPaste()
  {
    if (editable)
    {
      Clipboard clipboard = getToolkit().getSystemClipboard();
      try
      {
        // The MacOS MRJ doesn't convert \r to \n,
        // so do it here
        String selection =
               ((String) clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor)).replace('\r', '\n');
        String replaced = null;

        if (Search.getFindPattern().length() > 0)
        {
          if (Jext.getBooleanProperty("useregexp"))
          {
            RE regexp = new RE(Search.getFindPattern(),
                               (Jext.getBooleanProperty("ignorecase") == true ?
                               RE.REG_ICASE : 0) | RE.REG_MULTILINE,
                               RESyntax.RE_SYNTAX_PERL5);
            if (regexp == null)
              return;
            replaced = regexp.substituteAll(selection, Search.getReplacePattern());
          } else {
            LiteralSearchMatcher matcher = new LiteralSearchMatcher(Search.getFindPattern(),
                                                                    Search.getReplacePattern(),
                                                                    Jext.getBooleanProperty("ignorecase"));
            replaced = matcher.substitute(selection);
          }
        }

        if (replaced == null)
          replaced = selection;

        setSelectedText(replaced);

      } catch (Exception e) {
        getToolkit().beep();
      }
    }
  }

  /**
   * Set a new file. We first ask the user if he'd like to save its
   * changes (if some have been made).
   */

  public void newFile()
  {
    beginOperation();                            // we don't want to see a 'modified' message

    if (isDirty() && !isEmpty())
    {
      String[] args = { getName() };
      int response = JOptionPane.showConfirmDialog(parent,
                                                   Jext.getProperty("general.save.question", args),
                                                   Jext.getProperty("general.save.title"),
                                                   JOptionPane.YES_NO_CANCEL_OPTION,
                                                   JOptionPane.QUESTION_MESSAGE);
      switch (response)
      {
        case 0:
          saveContent();
          break;
        case 1:
          break;
        case 2:
          endOperation();
          return;
        default:
          endOperation();
          return;
      }
    }

    // we have to create a new document, so we remove
    // old listeners and create new ones below
    document.removeUndoableEditListener(this);
    document.removeDocumentListener(this);

    clean();
    discard();

    setEditable(true);
    setText("");

    anchor = null;
    modTime = -1;
    newf = true;
    resetLineTerm();
    currentFile = null;
    searchHighlight = null;

    document.addUndoableEditListener(this);
    document.addDocumentListener(this);

    parent.setNew(this);
    parent.setTextAreaName(this, Jext.getProperty("textarea.untitled"));
    parent.fireJextEvent(this, JextEvent.FILE_CLEARED);
    setParentTitle();

    endOperation();
  }

  /**
   * This is called by the AutoSave thread.
   */

  public void autoSave()
  {
    if (!isNew())
      saveContent();
  }

  /**
   * This overrides standard insert method. Indeed, we need
   * to update the label containing caret's position.
   * @param insert The string to insert
   * @param pos The offset of the text where to insert the string
   */

  public void insert(String insert, int pos)
  {
    setCaretPosition(pos);
    setSelectedText(insert);
  }

  public void userInput(char c)
  {
    String indentOpenBrackets = getProperty("indentOpenBrackets");
    String indentCloseBrackets = getProperty("indentCloseBrackets");

    if ((indentCloseBrackets != null && indentCloseBrackets.indexOf(c) != -1) ||
        (indentOpenBrackets != null && indentOpenBrackets.indexOf(c) != -1))
    {
      org.jext.misc.Indent.indent(this, getCaretLine(), false, true);
    }
  }

  /**
   * Because JEditorPane doesn't have any getTabSize() method,
   * we implement our own one.
   * @return Current tab size (in amount of spaces)
   */

  public int getTabSize()
  {
    String size = Jext.getProperty("editor.tabSize");
    if (size == null)
      return 8;

    Integer i = new Integer(size);
    if (i != null)
      return i.intValue();
    else
      return 8;
  }

  /**
   * See getTabSize().
   * @param size The new tab size (in amount of spaces)
   */

  public void setTabSize(int size)
  {
    //Jext.setProperty("editor.tabSize", String.valueOf(size));

    document.putProperty(PlainDocument.tabSizeAttribute,  new Integer(size));
  }

  /**
   * Set parent title according to the fullfilename flag
   * in the user properties.
   */

  public void setParentTitle()
  {
    if (currentFile == null)
    {
      Workspaces ws = parent.getWorkspaces();
      parent.setTitle("Jext - " + Jext.getProperty("textarea.untitled") +
                      (ws == null ? "" : " [" + ws.getName() + ']'));
      return;
    }

    String fName;
    if (Jext.getBooleanProperty("full.filename", "off"))
      fName = Utilities.getShortStringOf(currentFile, 80);
    else
      fName = getFileName(currentFile);

    Workspaces ws = parent.getWorkspaces();
    parent.setTitle("Jext - " + fName + (ws == null ? "" : " [" + ws.getName() + ']'));
  }

  // get the name of a file from its absolute path name

  private String getFileName(String file)
  {
    if (file == null)
      return Jext.getProperty("textarea.untitled");
    else
      return file.substring(file.lastIndexOf(File.separator) + 1);
  }

  /**
   * Get name of this text area. This name is made of the current opened
   * file name.
   */

  public String getName()
  {
    return getFileName(currentFile);
  }

  /**
   * Turn syntax colorization on or off.
   * @param mode Colorization mode
   */

  public void setColorizing(String mode)
  {
    enableColorizing(mode, Jext.getMode(mode).getTokenMarker());
  }

  public void setColorizing(Mode mode)
  {
    enableColorizing(mode.getModeName(), mode.getTokenMarker());
  }

  private void enableColorizing(String mode, TokenMarker token)
  {
    if (mode == null || token == null || mode.equals(this.mode))
      return;

    setTokenMarker(token);

    this.mode = mode;
    getPainter().setBracketHighlightEnabled("on".equals(getProperty("bracketHighlight")));

    Jext.setProperty("editor.colorize.mode", mode);
    parent.fireJextEvent(this, JextEvent.SYNTAX_MODE_CHANGED);

    repaint();
  }

  /**
   * Sets current colorizing mode.
   * @param mode The colorizing mode name
   */

  public void setColorizingMode(String mode)
  {
    this.mode = mode;
  }

  /**
   * Returns current syntax colorizing mode.
   */

  public String getColorizingMode()
  {
    return mode;
  }

  /**
   * Checks if holded file has been changed by an external program.
   */

  public void checkLastModificationTime()
  {
    if (modTime == -1)
      return;

    File file = getFile();
    if (file == null)
      return;

    long newModTime = file.lastModified();
    if (newModTime > modTime)
    {
      String prop = (isDirty() ? "textarea.filechanged.dirty.message" : "textarea.filechanged.focus.message");

      Object[] args = { currentFile };
      int result = JOptionPane.showConfirmDialog(parent,
                                                Jext.getProperty(prop, args),
                                                Jext.getProperty("textarea.filechanged.title"),
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE);
      if (result == JOptionPane.YES_OPTION)
        open(currentFile);
      else
        modTime = newModTime;
    }
  }

  /**
   * Called to save current content in specified zip file.
   * Call zip(String file) but asks user for overwriting if
   * file already exists.
   */

  public void zipContent()
  {
    if (getText().length() == 0)
      return;

    if (isNew())
    {
      Utilities.showMessage("Please save your file before zipping it !");
      return;
    }

    String zipFile = Utilities.chooseFile(parent, Utilities.SAVE);
    if (zipFile != null)
    {
      if (!zipFile.endsWith(".zip"))
        zipFile += ".zip";

      if (!(new File(zipFile)).exists())
        zip(zipFile);
      else
      {
        int response = JOptionPane.showConfirmDialog(parent,
                                                     Jext.getProperty("textarea.file.exists", new Object[] { zipFile }),
                                                     Jext.getProperty("general.save.title"),
                                                     JOptionPane.YES_NO_OPTION,
                                                     JOptionPane.QUESTION_MESSAGE);
        switch (response)
        {
          case 0:
            zip(zipFile);
            break;
          case 1:
            break;
          default:
            return;
        }
      }
    }
  }

  /**
   * Zip text area content into specified file.
   * @param zipFile The file name where to zip the text
   */

  public void zip(String zipFile)
  {
    waitingCursor(true);
    try
    {
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
      out.putNextEntry(new ZipEntry((new File(currentFile)).getName()));
      // we ensured we use system's carriage return char, as in save():
      //String newline = Jext.getProperty("editor.newLine");//System.getProperty("line.separator");
      //but now, as in save(), we use document's one.
      String newline = getLineTerm();
      Element map = document.getDefaultRootElement();
      // we zip the text line by line
      for (int i = 0; i < map.getElementCount() ; i++)
      {
        Element line = map.getElement(i);
        int start = line.getStartOffset();
        byte[] buf = (getText(start, line.getEndOffset() - start - 1) + newline).getBytes();
        out.write(buf, 0, buf.length);
      }
      out.closeEntry();
      out.close();
    } catch(IOException ioe) {
      Utilities.showError(Jext.getProperty("textarea.zip.error"));
    }
    waitingCursor(false);
  }


  /**
   * Called to save this component's content.
   * Call save(String file) but let the user choosing a file name
   * if the isNew() flag is true (int the case the user choosed
   * an existing file, we ask him if he really wants to overwrite it).
   */

  public void saveContent()
  {
    if (!isEditable())
      return;

    if (isNew())
    {
      String fileToSave = Utilities.chooseFile(parent, Utilities.SAVE);
      if (fileToSave != null)
      {
        if (!(new File(fileToSave)).exists())
          save(fileToSave);
        else
        {
          int response = JOptionPane.showConfirmDialog(parent,
                         Jext.getProperty("textarea.file.exists", new Object[] { fileToSave }),
                         Jext.getProperty("general.save.title"),
                         JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
          switch (response)
          {
            case 0:
              save(fileToSave);
              break;
            case 1:
              break;
            default:
              return;
          }
        }
      }
    } else {
      if (isDirty())
        save(currentFile);
    }
  }

  /**
   * Store the text in a specified file.
   * @param file The file in which we'll write the text
   */

  public void save(String file)
  {
    waitingCursor(true);
    try
    {
      File _file = new File(file);
      long newModTime = _file.lastModified();
      if (modTime != -1 && newModTime > modTime)
      {
        int result = JOptionPane.showConfirmDialog(parent,
                     Jext.getProperty("textarea.filechanged.save.message", new Object[] { file }),
                     Jext.getProperty("textarea.filechanged.title"),
                     JOptionPane.YES_NO_OPTION,
                     JOptionPane.WARNING_MESSAGE);
        if (result != JOptionPane.YES_OPTION)
        {
          waitingCursor(false);
          return;
        }
      }

      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                           new FileOutputStream(_file),
                           Jext.getProperty("editor.encoding", System.getProperty("file.encoding"))),
                           BUFFER_SIZE);
      Segment lineSegment = new Segment();
      //String newline = Jext.getProperty("editor.newLine");
      String newline = getLineTerm();
      Element map = document.getDefaultRootElement();

      // we save the text line by line
      for (int i = 0; i < map.getElementCount() - 1; i++)
      {
        Element line = map.getElement(i);
        int start = line.getStartOffset();
        document.getText(start, line.getEndOffset() - start - 1, lineSegment);
        out.write(lineSegment.array, lineSegment.offset, lineSegment.count);
        out.write(newline);
      }

      // avoids extra line feed at the end of the file
      Element line = map.getElement(map.getElementCount() - 1);
      int start = line.getStartOffset();
      document.getText(start, line.getEndOffset() - start - 1, lineSegment);
      out.write(lineSegment.array, lineSegment.offset, lineSegment.count);

      if (Jext.getBooleanProperty("editor.extra_line_feed"))
        out.write(newline);

      out.close();

      storeOrigLineTerm(); //so we can see if it has been changed.

      if (!file.equals(currentFile))
      {
        parent.setTextAreaName(this, getFileName(file));
        parent.saveRecent(file);
        currentFile = file;
        setParentTitle();
      }

      _file = new File(file);
      modTime = _file.lastModified();

      if (isNew())
        newf = false;

      clean(); //reset the dirty flag
      parent.setSaved(this);

    } catch(Exception e) {
      Utilities.showError(Jext.getProperty("textarea.save.error"));
    }

    waitingCursor(false);
  }

  /**
   * Called to load a new file in the text area.
   * Determines which line separator (\n, \r\n...) are used in the
   * file to open. Convert'em into Swing line separator (\n).
   * @param path The path of the file to be loaded
   */

  public void open(String path)
  {
    open(path, null, 0);
  }

  /**
   * Called to load a new file in the text area.
   * Determines which line separator (\n, \r\n...) are used in the
   * file to open. Convert'em into Swing line separator (\n).
   * @param path The path of the file to be loaded
   * @param addToRecentList If false, the file name is not added to recent list
   */

  public void open(String path, boolean addToRecentList)
  {
    open(path, null, 0, false, addToRecentList);
  }

  /**
   * Called to load a new file in the text area.
   * Determines which line separator (\n, \r\n...) are used in the
   * file to open. Convert'em into Swing line separator (\n).
   * @param path The path of the file to be loaded
   * @param _in You can specify an InputStreamReader (see ZipExplorer)
   * @param bufferSize Size of the StringBuffer, useful if _in != null
   */

  public void open(String path, InputStreamReader _in, int bufferSize)
  {
    open(path, _in, bufferSize, false, true);
  }

  /**
   * Called to load a new file in the text area.
   * Determines which line separator (\n, \r\n...) are used in the
   * file to open. Convert'em into Swing line separator (\n).
   * @param path The path of the file to be loaded
   * @param _in You can specify an InputStreamReader (see ZipExplorer);
   * if you do this the TextArea is marked as 'dirty'
   * @param bufferSize Size of the StringBuffer, useful if _in != null
   * @param web True if open an url
   * @param addToRecentList If false, the file name is not added to recent list
   */

  public void open(String path, InputStreamReader _in, int bufferSize,
                   boolean web, boolean addToRecentList)
  {
    beginOperation();

    if (path.endsWith(".zip") || path.endsWith(".jar"))
    {
      new ZipExplorer(parent, this, path);
      endOperation();
      return;
    }

    // we do the same thing as in newFile() for the listeners
    document.removeUndoableEditListener(this);
    document.removeDocumentListener(this);
    clean();
    discard();
    anchor = null;
    modTime = -1;

    try
    {
      StringBuffer buffer;
      InputStreamReader in;

      if (_in== null)
      {
        File toLoad = new File(path);
        // we check if the file is read only or not
        if (!toLoad.canWrite())
          setEditable(false);
        else if (!isEditable())
          setEditable(true);
        buffer = new StringBuffer((int) toLoad.length());
        in = new InputStreamReader(new FileInputStream(toLoad),
                                   Jext.getProperty("editor.encoding", System.getProperty("file.encoding")));
      } else {
        in = _in;
        if (bufferSize == 0)
          bufferSize = BUFFER_SIZE * 4;
        buffer = new StringBuffer(bufferSize);
      }

      char[] buf = new char[BUFFER_SIZE];
      int len;
      int lineCount = 0;
      boolean CRLF = false;
      boolean CROnly = false;
      boolean lastWasCR = false;

      // we read the file till its end (amazing, hu ?)
      while ((len = in.read(buf, 0, buf.length)) != -1)
      {
        int lastLine = 0;
        for (int i = 0; i < len; i++)
        {
          switch(buf[i])
          {
            // and we convert system's carriage return char into \n
            case '\r':
              if (lastWasCR)
              {
                CROnly = true;
                CRLF = false;
              } else
                lastWasCR = true;
              buffer.append(buf, lastLine, i - lastLine);
              buffer.append('\n');
              lastLine = i + 1;
              break;
            case '\n':
              if (lastWasCR)
              {
                CROnly = false;
                CRLF = true;
                lastWasCR = false;
                lastLine = i + 1;
              } else {
                CROnly = false;
                CRLF = false;
                buffer.append(buf, lastLine, i - lastLine);
                buffer.append('\n');
                lastLine = i + 1;
              }
              break;
            default:
              if (lastWasCR)
              {
                CROnly = true;
                CRLF = false;
                lastWasCR = false;
              }
              break;
          }
        }
        buffer.append(buf, lastLine, len - lastLine);
      }
      in.close();
      in = null;

      //handle line end choice
      resetLineTerm();
      if (Jext.getBooleanProperty("editor.line_end.preserve"))
        if(CROnly)
          setLineTerm("\r");
        else if (CRLF) //then CRLF is true
          setLineTerm("\r\n");
        else
          setLineTerm("\n");
      storeOrigLineTerm(); //so we can see if it has been changed.
      lineTermSelector.setSelectedItem(getLineTermName());
      getJextParent().setLineTerm(JextTextArea.this, lineTermSelector.getSelectedIndex());  //this updates the other TextArea if
      //the JextFrame is splitted

      if (buffer.length() != 0 && buffer.charAt(buffer.length() - 1) == '\n')
        buffer.setLength(buffer.length() - 1);

      // we clear the area
      document.remove(0, getLength());
      // we put the text in it
      document.insertString(0, buffer.toString(), null);
      buffer = null;

      setCaretPosition(0);
      parent.setNew(this);

      // we add the file into the recent menu
      if (_in == null)
      {
        parent.setTextAreaName(this, getFileName(path));
        if (addToRecentList)
          parent.saveRecent(path);
        currentFile = path;
        newf = false;
        modTime = getFile().lastModified();
      } else {
        if (!web)
          currentFile = (new File(path)).getName();
        else
          currentFile = path.substring(path.lastIndexOf('/') + 1);
        parent.setTextAreaName(this, currentFile);

        newf = true;
        setDirty();
        parent.setChanged(this);

        _in.close();
        _in = null;
      }

      setParentTitle();

      // and we choose the most appropriate syntax colorization mode
      String low = path.toLowerCase();
      String _mode;
      boolean modeSet = false;

      RE regexp;
      for (int i = Jext.modes.size() - 1; i >= 0; i--)
      {
        Mode modeClass = (Mode) Jext.modes.get(i);
        if (modeClass == null)
          continue;

       _mode = modeClass.getModeName();
        if (_mode.equals("plain"))
          continue;

        try
        {
          regexp = new RE(Utilities.globToRE(Jext.getProperty("mode." + _mode + ".fileFilter")),
                          RE.REG_ICASE);

          if (regexp.isMatch(low))
          {
            setColorizing(_mode);
            modeSet = true;
            break;
          }
        } catch (REException ree) { }
      }

      if (!modeSet)
        setColorizing("plain");

      document.addUndoableEditListener(this);
      document.addDocumentListener(this);

      parent.fireJextEvent(this, JextEvent.FILE_OPENED);

    } catch(BadLocationException bl) {
      bl.printStackTrace();
    } catch(FileNotFoundException fnf) {
      String[] args = { path };
      Utilities.showError(Jext.getProperty("textarea.file.notfound", args));
    } catch(IOException io) {
      Utilities.showError(io.toString());
    } finally {
      endOperation();
    }
  }

  /**
   * Set the new flag and resets the default line end separator.
   */

  public void setNewFlag(boolean newFlag)
  {
    newf = newFlag;
    //TODO: check if this is good.
    resetLineTerm();
    lineTermSelector.setSelectedItem(getLineTermName());
  }

  /**
   * Return true if current text is new, false otherwise.
   */

  public boolean isNew()
  {
    return newf;
  }

  /**
   * Return true if area is empty, false otherwise.
   */

  public boolean isEmpty()
  {
    if (getLength() == 0)
      return true;
    else
      return false;
  }

  /**
   * Return true if area content has changed, false otherwise.
   */

  public boolean isDirty()
  {
    //this is to mark changed a file when
    //its line end has been changed. But it's probably a bug - if you clean() a TextArea 
    //it can be still dirty.
    return dirty || isLineTermChanged();
  }

  /**
   * Called when the content of the area has changed.
   */

  private void setDirty()
  {
    dirty = true;
  }

  /**
   * Called after having saved or created a new document to ensure
   * the content isn't 'dirty'.
   */

  public void clean()
  {
    dirty = false;
  }

  /**
   * Discard all edits contained in the UndoManager.
   * Update the corresponding menu items.
   */

  public void discard()
  {
    undo.discardAllEdits();
  }

  /**
   * Set the anchor postion.
   */

  public void setAnchor()
  {
    try
    {
      anchor = document.createPosition(getCaretPosition());
    } catch (BadLocationException ble) { }
  }

  /**
   * Go to anchor position
   */

  public void gotoAnchor()
  {
    if (anchor == null)
      getToolkit().beep();
    else
      setCaretPosition(anchor.getOffset());
  }

  public int getAnchorOffset()
  {
    if (anchor == null)
      return -1;
    else
      return anchor.getOffset();
  }

  /**
   * Used by Jext to update its menu items.
   */

  public UndoManager getUndo()
  {
    return undo;
  }

  /**
   * Used for ReplaceAll.
   * This merges all text changes made between the beginCompoundEdit()
   * and the endCompoundEdit() calls into only one undo event.
   */

  public void beginCompoundEdit()
  {
    beginCompoundEdit(true);
  }

  public void beginCompoundEdit(boolean cursorHandle)
  {
    if (compoundEdit == null && !protectedCompoundEdit)
    {
      endCurrentEdit();
      compoundEdit = new CompoundEdit();

      if (cursorHandle)
        waitingCursor(true);
    }
  }

  /**
   * A protected compound edit is a compound edit which cannot be ended by
   * a normal call to endCompoundEdit().
   */

  public void beginProtectedCompoundEdit()
  {
    if (!protectedCompoundEdit)
    {
      beginCompoundEdit(true);
      protectedCompoundEdit = true;
    }
  }

  /**
   * See beginCompoundEdit().
   */

  public void endCompoundEdit()
  {
    endCompoundEdit(true);
  }

  public void endCompoundEdit(boolean cursorHandle)
  {
    if (compoundEdit != null && !protectedCompoundEdit)
    {
      compoundEdit.end();

      if (compoundEdit.canUndo())
        undo.addEdit(compoundEdit);

      compoundEdit = null;

      if (cursorHandle)
        waitingCursor(false);
    }
  }

  /**
   * This terminates a protected compound edit.
   */

  public void endProtectedCompoundEdit()
  {
    if (protectedCompoundEdit)
    {
      protectedCompoundEdit = false;
      endCompoundEdit(true);
    }
  }

  /**
   * Return the lentgh of the text in the area.
   */

  public int getLength()
  {
    return document.getLength();
  }

  /**
   * When an undoable event is fired, we add it to the undo/redo list.
   */

  public void undoableEditHappened(UndoableEditEvent e)
  {
    if (!getOperation())
    {
      if (compoundEdit == null)
      {
        currentEdit.addEdit(e.getEdit());
        //undo.addEdit(e.getEdit());
      } else
        compoundEdit.addEdit(e.getEdit());
    }
  }

  public void endCurrentEdit()
  {
    if (currentEdit.isSignificant())
    {
      currentEdit.end();
      if (currentEdit.canUndo())
        undo.addEdit(currentEdit);
      currentEdit = new CompoundEdit();
    }
  }

  public void setUndoing(boolean action)
  {
    undoing = action;
  }

  /**
   * When a modification is made in the text, we turn
   * the 'to_be_saved' flag to true.
   */

  public void changedUpdate(DocumentEvent e)
  {
    if (!getOperation())
    {
      boolean savedDirty = isDirty();
      setDirty();
      if (!savedDirty)
        parent.setChanged(this);  //it needs that the area is "dirty", so we have to call setDirty() before
      //isDirty can be true if the line end is changed or if dirty is true;
    }
    //setCaretPosition(e.getOffset() + e.getLength());
    parent.fireJextEvent(this, JextEvent.CHANGED_UPDATE);
  }

  /**
   * When a modification is made in the text, we turn
   * the 'to_be_saved' flag to true.
   */

  public void insertUpdate(DocumentEvent e)
  {
    if (!getOperation())
    {
      boolean savedDirty = isDirty();
      setDirty();
      if (!savedDirty)
        parent.setChanged(this);  //it needs that the area is "dirty", so we have to call setDirty() before
      //isDirty can be true if the line end is changed or if dirty is true;
    }
    if (undoing)
    {
      if (e.getLength() == 1)
        setCaretPosition(e.getOffset() + 1);
      else
        setCaretPosition(e.getOffset());
    }
    parent.fireJextEvent(this, JextEvent.INSERT_UPDATE);
  }

  /**
   * When a modification is made in the text, we turn
   * the 'to_be_saved' flag to true.
   */

  public void removeUpdate(DocumentEvent e)
  {
    parent.updateStatus(this);
    if (!getOperation())
    {
      boolean savedDirty = isDirty();
      setDirty();
      if (!savedDirty)
        parent.setChanged(this);  //it needs that the area is "dirty", so we have to call setDirty() before
      //isDirty can be true if the line end is changed or if dirty is true;
    }
    if (undoing)
      setCaretPosition(e.getOffset());
    parent.fireJextEvent(this, JextEvent.REMOVE_UPDATE);
  }

  /**
   * Return a String representation of this object.
   */

  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("JextTextArea: ");
    buf.append("[filename: " + getCurrentFile() + ";");
    buf.append(" filesize: " + getLength() + "] -");
    buf.append(" [is dirty: " + isDirty() + ";");
    buf.append(" is new: " + isNew() + ";");
    if (anchor != null)
      buf.append(" anchor: " + anchor.getOffset() + "] -");
    else
      buf.append(" anchor: not defined] -");
    buf.append(" [font-name: " + getFontName() + ";");
    buf.append(" font-style: " + getFontStyle() + ";");
    buf.append(" font-size: " + getFontSize() + "] -");
    buf.append(" [syntax mode: " + mode + "]");
    return buf.toString();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////
  // INTERNAL CLASSES
  //////////////////////////////////////////////////////////////////////////////////////////////

  class FocusHandler extends FocusAdapter
  {
    public void focusGained(FocusEvent evt)
    {
      if (!parent.getBatchMode())
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            checkLastModificationTime();
          }
        });
      }
    }
  }

  class CaretHandler implements CaretListener
  {
    public void caretUpdate(CaretEvent evt)
    {
      parent.updateStatus(JextTextArea.this);
    }
  }

  class JextTextAreaPopupMenu extends Thread
  {
    private JextTextArea area;

    JextTextAreaPopupMenu(JextTextArea area)
    {
      super("---Thread:JextTextArea Popup---");
      this.area = area;
      start();
    }

    public void run()
    {
      popupMenu = XPopupReader.read(Jext.class.getResourceAsStream("jext.textarea.popup.xml"),
                                    "jext.textarea.popup.xml");
      if (Jext.getFlatMenus())
        popupMenu.setBorder(javax.swing.border.LineBorder.createBlackLineBorder());
      area.setRightClickPopup(popupMenu);
    }
  }
  

}

// End of JextTextArea.java