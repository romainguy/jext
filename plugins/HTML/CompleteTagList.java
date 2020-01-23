/*
 * 03/22/2002 - 23:33:29
 *
 * CompleteTagList.java - A complet tag selector
 * Copyright (C) 2001 Romain Guy
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
import javax.swing.border.*;
import javax.swing.event.*;

import org.jext.*;
import org.jext.gui.*;

public class CompleteTagList extends JWindow implements CaretListener
{
  private boolean tags, selfInput;

  private JList itemsList;

  //private Object[] list;
  private String word;

  private JextFrame parent;
  private JextTextArea textArea;
  private TagsCompletion completion;

  public CompleteTagList(JextFrame parent, TagsCompletion completion, String word, Tag[] list)
  {
    this(parent, completion, word, list, true);
  }

  public CompleteTagList(JextFrame parent, TagsCompletion completion, String word, Entity[] list)
  {
    this(parent, completion, word, list, false);
  }

  private CompleteTagList(JextFrame parent, TagsCompletion completion, String word,
                          Object[] list, boolean tags)
  {
    super(parent);

    this.parent = parent;
    this.completion = completion;
    this.textArea = parent.getTextArea();
    this.word = word;
    //this.list = list;
    this.tags = tags;

    JPanel pane = new JPanel();
    pane.setLayout(new BorderLayout());

    Font font = new Font("Monospaced", Font.PLAIN, 11);

    JLabel label = new JLabel(Jext.getProperty(tags ? "completeTag.list.title" : "completeEntity.list.title"));
    label.setFont(font);
    pane.add(label, BorderLayout.NORTH);

    itemsList = new JList(list);
    itemsList.setFont(font);
    itemsList.setVisibleRowCount(list.length < 5 ? list.length : 5);
    itemsList.setSelectedIndex(0);
    itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    itemsList.addMouseListener(new MouseHandler());
    if (tags)
      itemsList.setCellRenderer(new TagsCellRenderer());

    FontMetrics fm = getFontMetrics(font);
    itemsList.setPreferredSize(new Dimension(15 * fm.charWidth('m'),
                               (int) itemsList.getPreferredSize().height));


    JScrollPane scroll = new JScrollPane(itemsList);
    scroll.setBorder(null);
    pane.add(scroll, BorderLayout.SOUTH);
    pane.setBorder(LineBorder.createBlackLineBorder());
    getContentPane().add(pane);

    setBackground(Color.lightGray);

    GUIUtilities.requestFocus(this, itemsList);
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
    if ((x + popupSize.width) >
        (parentLocation.x + parentSize.width - parentInsets.right))
    {
      x -= popupSize.width;
    }

    setLocation(x, tapLocation.y + textArea.lineToY(line)
                                 + fm.getHeight() + fm.getDescent() + fm.getLeading());
    setVisible(true);

    KeyHandler handler = new KeyHandler();
    addKeyListener(handler);
    itemsList.addKeyListener(handler);
    parent.setKeyEventInterceptor(handler);
    textArea.addCaretListener(this);
  }

  public void caretUpdate(CaretEvent evt)
  {
    if (!selfInput)
      dispose();
    else
      selfInput = !selfInput;
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

  private void insertEntity()
  {
    textArea.setSelectedText(((Entity) itemsList.getSelectedValue()).getEntity().substring(word.length() + 1));
  }

  private void insertTag()
  {
    Tag __tag__ = (Tag) itemsList.getSelectedValue();
    String tag;

    if (word.charAt(0) == '/')
      tag = __tag__.getClosingTag();
    else
    {
      if ("on".equals(Jext.getProperty("html.completion.expandFullTag")))
        tag = __tag__.getFullTag();
      else
        tag = __tag__.getOpeningTag();
    }
    tag = tag.substring(word.length() + 1);

    int caretIndex = tag.indexOf('|');
    if (caretIndex != -1)
    {
      String endTag = tag.substring(caretIndex + 1);
      tag = tag.substring(0, caretIndex);
      textArea.setSelectedText(tag);
      int pos = textArea.getCaretPosition();
      textArea.setSelectedText(endTag);
      textArea.setCaretPosition(pos);
    } else
      textArea.setSelectedText(tag);
  }

  class KeyHandler extends KeyAdapter
  {
    public void keyTyped(KeyEvent evt)
    {
      char ch = evt.getKeyChar();
      if (ch != '\b')
      {
        selfInput = true;
        textArea.setSelectedText(String.valueOf(ch));
        word += ch;
        Object[] list = null;

        if (tags)
          list = completion.buildTagsList(word);
        else
          list = completion.buildEntitiesList(word);

        if (list.length == 0)
          dispose();
        else
        {
          itemsList.setListData(list);
          itemsList.setSelectedIndex(0);
        }
      }
    }

    public void keyPressed(KeyEvent evt)
    {
      switch (evt.getKeyCode())
      {
        case KeyEvent.VK_ENTER: //case KeyEvent.VK_SPACE:
          if (tags)
            insertTag();
          else
            insertEntity();
          evt.consume();
          dispose();
          break;
        case KeyEvent.VK_ESCAPE:
          dispose();
          evt.consume();
          break;
        case KeyEvent.VK_PAGE_UP:
          if (getFocusOwner() == itemsList)
            return;

          int selected = itemsList.getSelectedIndex();
          selected -= 5;
          if (selected < 0)
            selected = itemsList.getModel().getSize() - 1;
  
          itemsList.setSelectedIndex(selected);
          itemsList.ensureIndexIsVisible(selected);

          evt.consume();
          break;
        case KeyEvent.VK_PAGE_DOWN:
          if (getFocusOwner() == itemsList)
            return;

          selected = itemsList.getSelectedIndex();
          selected += 5;
          if (selected >= itemsList.getModel().getSize())
            selected = 0;
  
          itemsList.setSelectedIndex(selected);
          itemsList.ensureIndexIsVisible(selected);

          evt.consume();
          break;
        case KeyEvent.VK_UP:
          if (getFocusOwner() == itemsList)
            return;

          selected = itemsList.getSelectedIndex();
          if (selected == 0)
            selected = itemsList.getModel().getSize() - 1;
          else
            selected--;
  
          itemsList.setSelectedIndex(selected);
          itemsList.ensureIndexIsVisible(selected);

          evt.consume();
          break;
        case KeyEvent.VK_DOWN:
          if (getFocusOwner() == itemsList)
            return;

          selected = itemsList.getSelectedIndex();
          if (selected == itemsList.getModel().getSize() - 1)
            return;

          selected++;

          itemsList.setSelectedIndex(selected);
          itemsList.ensureIndexIsVisible(selected);

          evt.consume();
          break;
        default:
          if (evt.isActionKey())
          {
            dispose();
            parent.processKeyEvent(evt);
          }
          break;
      }
    }
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent me)
    {
      if (tags)
        insertTag();
      else
        insertEntity();
      dispose();
    }
  }

  class TagsCellRenderer extends DefaultListCellRenderer
  {
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) 
    {
      setComponentOrientation(list.getComponentOrientation());
      Tag t = (Tag) value;

      if (isSelected)
      {
        setBackground(list.getSelectionBackground());
        value = t.toString() + "(" + t.attributesCount() + ")";
      } else {
        setBackground(list.getBackground());
      }

      if (!t.isEmpty())
      {
        if (t.attributesCount() > 0)
          setForeground(Color.green.darker().darker());
        else
          setForeground(list.getForeground());
      } else {
        if (t.attributesCount() > 0)
          setForeground(Color.blue);
        else
          setForeground(Color.red);
      }
    
      setText((value == null) ? "" : value.toString());
      setOpaque(isSelected);
      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
    
      return this;
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
    
    itemsList = null;
    word = null;
    parent = null;
    textArea = null;
  }
  // End of patch
}

// End of CompleteTagList.java
