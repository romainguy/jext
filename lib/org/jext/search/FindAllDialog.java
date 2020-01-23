/*
 * 06/08/2001 - 23:35:37
 *
 * FindAllDialog.java - Find all occurences of a pattern in current text area
 * Copyright (C) 2000 Romain Guy
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

package org.jext.search;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.util.ArrayList;

import gnu.regexp.*;

import org.jext.*;
import org.jext.gui.*;

public class FindAllDialog extends JFrame implements ActionListener
{
  private JList results;
  private JextFrame parent;
  private JComboBox fieldSearch;
  private DefaultListModel resultModel;
  private JTextField fieldSearchEditor;
  private JextHighlightButton find, cancel;
  private JCheckBox useRegexp, ignoreCase, highlight;
  
  public FindAllDialog(JextFrame parent)
  {
    super(Jext.getProperty("find.all.title"));
    this.parent = parent;
    getContentPane().setLayout(new BorderLayout());

    fieldSearch = new JComboBox();
    fieldSearch.setRenderer(new ModifiedCellRenderer());
    fieldSearch.setEditable(true);
    fieldSearchEditor = (JTextField) fieldSearch.getEditor().getEditorComponent();
    fieldSearchEditor.addKeyListener(new KeyHandler());

    JPanel pane = new JPanel();
    pane.add(new JLabel(Jext.getProperty("find.all.label")));
    pane.add(fieldSearch);
    getContentPane().add(pane, BorderLayout.NORTH);

    JPanel pane2 = new JPanel();
    pane2.add(ignoreCase = new JCheckBox(Jext.getProperty("find.ignorecase.label"),
                           Jext.getBooleanProperty("ignorecase.all")));

    pane2.add(useRegexp = new JCheckBox(Jext.getProperty("find.useregexp.label"),
                          Jext.getBooleanProperty("useregexp.all")));

    pane2.add(highlight = new JCheckBox(Jext.getProperty("find.all.highlight.label"),
                          Jext.getBooleanProperty("highlight.all")));

    pane2.add(find = new JextHighlightButton(Jext.getProperty("find.all.button")));
    find.setMnemonic(Jext.getProperty("find.all.mnemonic").charAt(0));
    find.setToolTipText(Jext.getProperty("find.all.tip"));

    pane2.add(cancel = new JextHighlightButton(Jext.getProperty("general.cancel.button")));
    cancel.setMnemonic(Jext.getProperty("general.cancel.mnemonic").charAt(0));

    getContentPane().add(pane2, BorderLayout.CENTER);

    resultModel = new DefaultListModel();
    results = new JList();
    results.setCellRenderer(new ModifiedCellRenderer());
    results.setVisibleRowCount(10);
    FontMetrics fm = getFontMetrics(results.getFont());
//    results.setMaximumSize(new Dimension(40 * fm.charWidth('m'),
//                                         10 * results.getFixedCellHeight()));
    results.addListSelectionListener(new ListHandler());
    results.setModel(resultModel);

    JScrollPane scroller = new JScrollPane(results, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//    scroller.getViewport().setPreferredSize(results.getMaximumSize());
    getContentPane().add(scroller, BorderLayout.SOUTH);

    find.addActionListener(this);
    cancel.addActionListener(this);

    fm = getFontMetrics(fieldSearch.getFont());
    fieldSearch.setPreferredSize(new Dimension(30 * fm.charWidth('m'),
                                 (int) fieldSearch.getPreferredSize().height));

    String s;
    for (int i = 0; i < 25; i++)
    {
      s = Jext.getProperty("search.all.history." + i);
      if (s != null)
        fieldSearch.addItem(s);
      else
        break;
    }

    JextTextArea textArea = parent.getTextArea();
    if (!Jext.getBooleanProperty("use.selection"))
    {
      s = Jext.getProperty("find.all");
      addSearchHistory(s);
      fieldSearch.setSelectedItem(s);
    } else if (textArea.getSelectedText() != null) {
      s = textArea.getSelectedText();
      addSearchHistory(s);
      fieldSearch.setSelectedItem(s);
    }

    getRootPane().setDefaultButton(find);
    addKeyListener(new AbstractDisposer(this));

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent evt)
      {
        exit();
      }
    });

    setIconImage(GUIUtilities.getJextIconImage());
    pack();
    setResizable(false);
    org.jext.Utilities.centerComponent(this);
    setVisible(true);
  }

  private void exit()
  {
    Jext.setProperty("find.all", fieldSearchEditor.getText());

    for (int i = 0; i < fieldSearch.getItemCount(); i++)
      Jext.setProperty("search.all.history." + i, (String) fieldSearch.getItemAt(i));

    for (int i = fieldSearch.getItemCount(); i < 25; i++)
      Jext.unsetProperty("search.all.history." + i);

    Jext.setProperty("useregexp.all", (useRegexp.isSelected() ? "on" : "off"));
    Jext.setProperty("ignorecase.all", (ignoreCase.isSelected() ? "on" : "off"));
    Jext.setProperty("highlight.all", (highlight.isSelected() ? "on" : "off"));

    JextTextArea[] areas = parent.getTextAreas();
    for (int i = 0; i < areas.length; i++)
    {
      SearchHighlight h = areas[i].getSearchHighlight();
      if (h != null)
        h.disable();
    }

    parent.getTextArea().repaint();

    dispose();
  }

  private void addSearchHistory()
  {
    addSearchHistory(fieldSearchEditor.getText());
  }

  private void addSearchHistory(String c)
  {
    if (c == null)
      return;

    for (int i = 0; i < fieldSearch.getItemCount(); i++)
      if (((String) fieldSearch.getItemAt(i)).equals(c)) return;

    fieldSearch.insertItemAt(c, 0);

    if (fieldSearch.getItemCount() > 25)
    {
      for (int i = 24; i < fieldSearch.getItemCount(); i++)
        fieldSearch.removeItemAt(i);
    }
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object o = evt.getSource();
    if (o == cancel)
      exit();
    else if (o == find)
      findAll();
  }

  private void findAll()
  {
    String searchStr = fieldSearchEditor.getText();
    if (searchStr == null || searchStr.length() == 0)
      return;

    org.jext.Utilities.setCursorOnWait(this, true);

    addSearchHistory();
    resultModel.removeAllElements();
    JextTextArea textArea = parent.getTextArea();

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

    pack();
    textArea.repaint();
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

  class ListHandler implements ListSelectionListener
  {
    public void valueChanged(ListSelectionEvent evt)
    {
      if (results.isSelectionEmpty() || evt.getValueIsAdjusting())
        return;
      SearchResult result = (SearchResult) results.getSelectedValue();
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
        case KeyEvent.VK_ESCAPE:
          exit();
      }
    }
  }
  

}

// End of FindAllDialog.java