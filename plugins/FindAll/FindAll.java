/*
* 08/30/2001 - 10:37:18
*
* FindAll.java - Find all
* Copyright (C) 2001 Romain Guy & Grant Stead
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.ArrayList;
import gnu.regexp.*;
import org.jext.*;
import org.jext.gui.*;
import org.jext.event.*;
import org.jext.search.*;

public class FindAll extends JPanel implements ActionListener, JextListener
{
  private JList results;
  private JextFrame parent;
  private JComboBox fieldSearch;
  private DefaultListModel resultModel;
  private JTextField fieldSearchEditor;
  private JextHighlightButton find, unHighlight;
  private JextCheckBox useRegexp, ignoreCase, highlight, allFiles;
  
  public FindAll(JextFrame parent)
  {
    this.parent = parent;
    parent.addJextListener(this);
    
    setLayout(new BorderLayout());

    fieldSearch = new JComboBox();
    fieldSearch.setRenderer(new ModifiedCellRenderer());
    fieldSearch.setEditable(true);
    fieldSearchEditor = (JTextField) fieldSearch.getEditor().getEditorComponent();
    fieldSearchEditor.addKeyListener(new KeyHandler());

    FontMetrics fm = getFontMetrics(fieldSearch.getFont());
    Dimension size; //= new Dimension(20 * fm.charWidth('m'), fieldSearch.getSize().height);
    //fieldSearch.setPreferredSize(size);

    add(fieldSearch, BorderLayout.NORTH);

    JPanel pane2 = new JPanel();
    pane2.setLayout(new GridLayout(5, 1));
    pane2.add(ignoreCase = new JextCheckBox(Jext.getProperty("find.ignorecase.label"),
                           "on".equals(Jext.getProperty("ignorecase.all"))));

    pane2.add(useRegexp = new JextCheckBox(Jext.getProperty("find.useregexp.label"),
                          "on".equals(Jext.getProperty("useregexp.all"))));

    pane2.add(highlight = new JextCheckBox(Jext.getProperty("find.all.highlight.label"),
                          "on".equals(Jext.getProperty("highlight.all"))));

    pane2.add(allFiles = new JextCheckBox(Jext.getProperty("find.allFiles.label"),
                         "on".equals(Jext.getProperty("allfiles.all"))));

    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.add(find = new JextHighlightButton(Jext.getProperty("find.all.button"),
                                               org.jext.Utilities.getIcon("images/menu_find" +
                                                                          Jext.getProperty("jext.look.icons") +
                                                                          ".gif", Jext.class)));
    toolBar.add(unHighlight = new JextHighlightButton(Jext.getProperty("find.all.unHighlight")));
    pane2.add(toolBar);
    
    find.setToolTipText(Jext.getProperty("find.all.tip"));
    find.addActionListener(this);
    unHighlight.addActionListener(this);

    add(pane2, BorderLayout.SOUTH);

    resultModel = new DefaultListModel();
    results = new JList();
    results.setCellRenderer(new ModifiedCellRenderer());
    results.addListSelectionListener(new ListHandler());
    results.setModel(resultModel);

    size = new Dimension(20 * fm.charWidth('m'), 10 * results.getFixedCellHeight());

    JScrollPane scroller = new JScrollPane(results, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scroller.setMaximumSize(size);
    scroller.setPreferredSize(size);

    add(scroller, BorderLayout.CENTER);

    String s;
    for (int i = 0; i < 25; i++)
    {
      s = Jext.getProperty("search.all.history." + i);
      if (s != null)
        fieldSearch.addItem(s);
      else
        break;
    }

    s = Jext.getProperty("find.all");
    addSearchHistory(s);
    fieldSearch.setSelectedItem(s);
  }

  public void exit()
  {
    Jext.setProperty("find.all", fieldSearchEditor.getText());

    for (int i = 0; i < fieldSearch.getItemCount(); i++)
      Jext.setProperty("search.all.history." + i, (String) fieldSearch.getItemAt(i));

    for (int i = fieldSearch.getItemCount(); i < 25; i++)
      Jext.unsetProperty("search.all.history." + i);

    Jext.setProperty("useregexp.all", (useRegexp.isSelected() ? "on" : "off"));
    Jext.setProperty("ignorecase.all", (ignoreCase.isSelected() ? "on" : "off"));
    Jext.setProperty("highlight.all", (highlight.isSelected() ? "on" : "off"));
    Jext.setProperty("allfiles.all", (allFiles.isSelected() ? "on" : "off"));

    removeHighlights();

    parent.getTextArea().repaint();
  }

  private void removeHighlights()
  {
    JextTextArea[] areas = parent.getTextAreas();
    for (int i = 0; i < areas.length; i++)
    {
      SearchHighlight h = areas[i].getSearchHighlight();
      if (h != null)
        h.disable();
    }
  }

  private void addSearchHistory()
  {
    addSearchHistory(fieldSearchEditor.getText());
  }

  private void addSearchHistory(String c)
  {
    if ((c == null) || (c.equals("")))
      return;

    for (int i = 0; i < fieldSearch.getItemCount(); i++)
    {
      if (((String) fieldSearch.getItemAt(i)).equals(c))
      {
        fieldSearch.setSelectedIndex(i);
        return;
      }
    }

    fieldSearch.insertItemAt(c, 0);
    fieldSearch.setSelectedIndex(0);
    
    if (fieldSearch.getItemCount() > 25)
    {
      for (int i = 24; i < fieldSearch.getItemCount(); i++)
        fieldSearch.removeItemAt(i);
    }
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (evt.getSource() == find)
      findAll();
    else if (evt.getSource() == unHighlight)
    {
      removeHighlights();
      parent.getTextArea().repaint();
    }
  }

  public void findText()
  {
    String text = parent.getTextArea().getSelectedText();
    fieldSearchEditor.setText(text);
    fieldSearchEditor.requestFocus();
	
    findAll();
    if ((text != null) && (text.length() > 0)) {
      ListModel model = results.getModel();
      for (int index=0; index<model.getSize(); index++)
      {
        SearchResult result = (SearchResult)model.getElementAt(index);
        int pos[] = result.getPos();        
        if ((parent.getTextArea().getSelectionStart() == pos[0]) && 
            (parent.getTextArea().getSelectionEnd() == pos[1]) &&
            (parent.getTextArea() == result.getTextArea())
           )
        {
          results.setSelectedIndex(index);
          index = model.getSize() + 10;
          results.requestFocus();
	    }
      }
    }
    
	parent.getVerticalTabbedPane().setSelectedComponent(this);
	if (!"on".equals(Jext.getProperty("leftPanel.show")))
	{
		Jext.setProperty("leftPanel.show", "on");
		parent.triggerTabbedPanes();
	}
  }
  
  private void findAll()
  {
    String searchStr = fieldSearchEditor.getText();
    if (searchStr == null || searchStr.length() == 0)
      return;

    org.jext.Utilities.setCursorOnWait(this, true);

    addSearchHistory();
    resultModel.removeAllElements();

    JextTextArea[] areas = null;
    if (allFiles.isSelected()) {
      areas = parent.getTextAreas();
    } else {
      areas = new JextTextArea[1];
      areas[0] = parent.getTextArea();
    }
           
    for (int aindex=0; aindex<areas.length; aindex++)
    {
      JextTextArea textArea = areas[aindex];
  
      ArrayList matches = new ArrayList();
      Document doc = textArea.getDocument();
      Element map = doc.getDefaultRootElement();
      int lines = map.getElementCount();
  
      boolean light = highlight.isSelected();
      boolean regexp = useRegexp.isSelected();
  
      LiteralSearchMatcher matcher = null;
      if (!regexp)
      {
        matcher = new LiteralSearchMatcher(searchStr, null, ignoreCase.isSelected());
      }
  
      try
      {
        for (int i = 1; i <= lines; i++)
        {
          Element lineElement = map.getElement(i - 1);
          int start = lineElement.getStartOffset();
          String lineString = doc.getText(start, lineElement.getEndOffset() - start - 1);
          int[] match;
          int index = 0;
  
          do
          {
            if (regexp)
              match = nextMatch(lineString, index);
            else
              match = matcher.nextMatch(lineString, index);
  
            if (match != null)
            {
              SearchResult result = new SearchResult(textArea,
                                                     doc.createPosition(start + match[0]),
                                                     doc.createPosition(start + match[1]));
              resultModel.addElement(result);
              if (light)
                matches.add(result);
  
              index = match[1];
            }
          } while (match != null);
        }
      } catch (BadLocationException ble) {
      } finally {
        org.jext.Utilities.setCursorOnWait(this, false);
      }
  
      if (resultModel.isEmpty())
        textArea.getToolkit().beep();
  
      results.setModel(resultModel);
  
      if (light)
      {
        textArea.initSearchHighlight();
        SearchHighlight h = textArea.getSearchHighlight();
        h.trigger(true);
        h.setMatches(matches);
      } else {
        SearchHighlight h = textArea.getSearchHighlight();
        if (h != null)
        {
          h.trigger(false);
          h.setMatches(null);
        }
      }
  
      textArea.repaint();
    }
  }

  private int[] nextMatch(String str, int index)
  {
    int[] res;

    try
    {
      if (str.equals("") || str == null)
        return null;

      RE regexp = new RE((String) fieldSearch.getSelectedItem(),
                         (ignoreCase.isSelected() ? RE.REG_ICASE : 0),
                         RESyntax.RE_SYNTAX_PERL5);
      if (regexp == null)
      {
        getToolkit().beep();
        return null;
      }

      REMatch match = regexp.getMatch(str, index);
      if (match != null)
      {
        res = new int[2];
        res[0] = match.getStartIndex();
        res[1] = match.getEndIndex();
        return res;
      }
    } catch(Exception e) { }

    return null;
  }

  public void jextEventFired(JextEvent evt)
  {
    if (evt.getWhat() == JextEvent.KILLING_JEXT)
    {
      exit();      
    }
  }
  
  class ListHandler implements ListSelectionListener
  {
    public void valueChanged(ListSelectionEvent evt)
    {
      if (results.isSelectionEmpty() || evt.getValueIsAdjusting())
        return;
      SearchResult result = (SearchResult) results.getSelectedValue();
      if (parent.getTextArea() != result.getTextArea()) {
        parent.getTabbedPane().setSelectedComponent(result.getTextArea());
        results.requestFocus();
      }
      int pos[] = result.getPos();
      result.getTextArea().select(pos[0], pos[1]);
    }
  }

  class KeyHandler extends KeyAdapter
  {
    public void keyPressed(KeyEvent evt)
    {
      switch (evt.getKeyCode())
      {
        case KeyEvent.VK_ENTER:
          findAll();
          break;
      }
    }
  }
  
  /***************************************************************************
  Patch
     -> Memory management improvements : it may help the garbage collector.
     -> Author : Julien Ponge (julien@izforge.com)
     -> Date : 23, May 2001
  ***************************************************************************/
  protected void finalize() throws Throwable
  {
    super.finalize();
    
    results = null;
    parent = null;
    fieldSearch = null;
    resultModel = null;
    fieldSearchEditor = null;
    find = null;
    useRegexp = null;
    ignoreCase = null;
    highlight = null;
    allFiles = null;
  }
  // End of patch
}

// End of FindAll.java
