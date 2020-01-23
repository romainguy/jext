/*
 * 03/27/2002 - 22:12:53
 *
 * TagsCompletion.java - Completes tags
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

import java.awt.event.*;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.gjt.sp.jedit.textarea.*;

import org.jext.*;
import org.jext.event.*;

public class TagsCompletion extends KeyAdapter implements JextListener
{
  private CompleteTagList popup;
  private ArrayList registeredAreas = new ArrayList();

  public static Vector tagsList, entitiesList;
  public static final String delimiters = " \t>;.,\"\'(){}[]";
  public static final String endDelimiters = "<&";

  public void unregisterTextArea(JextTextArea textArea)
  {
    if (registeredAreas.contains(textArea))
    {
      textArea.removeKeyListener(this);
      registeredAreas.remove(textArea);
    }
  }
        
  public void registerTextArea(JextTextArea textArea)
  {
    if (!registeredAreas.contains(textArea))
    {
      textArea.addKeyListener(this);
      registeredAreas.add(textArea);
    }
  }

  public void jextEventFired(JextEvent evt)
  {
    int what = evt.getWhat();
    JextTextArea textArea = evt.getTextArea();

    if (textArea == null)
      return;

    String mode = textArea.getColorizingMode();

    switch (what)
    {
      case JextEvent.TEXT_AREA_OPENED:
        if (mode.equals("html") || mode.equals("asp") || mode.equals("jsp") ||
            mode.equals("asp-vbscript") || mode.equals("php3"))
        {
          registerTextArea(textArea);
        }
        break;
      case JextEvent.SYNTAX_MODE_CHANGED:
        if (mode.equals("html") || mode.equals("asp") || mode.equals("jsp") ||
            mode.equals("asp-vbscript") || mode.equals("php3"))
        {
          registerTextArea(textArea);
        } else
          unregisterTextArea(textArea);
        break;
      case JextEvent.TEXT_AREA_CLOSED:
        unregisterTextArea(textArea);
        break;
    }
  }

  public void keyPressed(KeyEvent evt)
  {
    char c = evt.getKeyChar();
    if (c != '&' && c != '!' && (evt.getModifiers() != 0 || c == '>' || !Character.isLetterOrDigit(c)))
      return;

    final JextTextArea textArea = (JextTextArea) evt.getSource();
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        showCompleteTagList(textArea);
      }
    });
  }

  private String getWord(JextTextArea textArea)
  {
    Document doc = textArea.getDocument();
    Element map = doc.getDefaultRootElement();
    Element lineElement = map.getElement(map.getElementIndex(textArea.getCaretPosition()));

    int start = lineElement.getStartOffset();
    int end = lineElement.getEndOffset() - 1;
    int length = end - start;
    int startPos = textArea.getCaretPosition();

    if (startPos == end)
    {
      if (startPos == start)
        return null;
      else
        startPos--;
    }

    String _line = textArea.getText(start, length);
    int linePos = startPos - start;

    int wordStart = -1;
    int wordEnd = -1;

    if (endDelimiters.indexOf(_line.charAt(linePos)) != -1 &&
        linePos - 1 >= 0 &&
        delimiters.indexOf(_line.charAt(linePos - 1)) != -1)
    {
      return null;
    }

    if (delimiters.indexOf(_line.charAt(linePos)) != -1)
    {
      if (linePos > 0)
      {
        if (delimiters.indexOf(_line.charAt(linePos - 1)) != -1)
          return null;
        else
          wordEnd = linePos;
      }

      for (int i = linePos - 1; i >= 0; i--)
      {
        if (delimiters.indexOf(_line.charAt(i)) != -1)
        {
          wordStart = i + 1;
          break;
        } else if (endDelimiters.indexOf(_line.charAt(i)) != -1) {
          wordStart = i;
          break;
        }
      }
    } else {
      if (endDelimiters.indexOf(_line.charAt(linePos)) != -1)
      {
        wordEnd = linePos;
        if (linePos > 0)
          linePos--;
      } else {
        for (int i = linePos; i < length; i++)
        {
          if (delimiters.indexOf(_line.charAt(i)) != -1)
          {
            wordEnd = i;
            break;
          }
        }
      }

      for (int i = linePos; i >= 0; i--)
      {
        if (delimiters.indexOf(_line.charAt(i)) != -1)
        {
          wordStart = i + 1;
          break;
        } else if (endDelimiters.indexOf(_line.charAt(i)) != -1) {
          wordStart = i;
          break;
        }
      }
    }

    if (wordStart == -1)
      wordStart = 0;

    if (wordEnd == -1)
      wordEnd = length;

    return _line.substring(wordStart, wordEnd);
  }

  private void showCompleteTagList(JextTextArea textArea)
  {
    String word = getWord(textArea);

    if (word != null)
    {
      if (word.startsWith("<"))
      {
        Tag[] list = buildTagsList(word.substring(1));
        if (list.length > 0)
          popup = new CompleteTagList(textArea.getJextParent(), this, word.substring(1), list);
      } else if (word.startsWith("&")) {
        Entity[] list = buildEntitiesList(word.substring(1));
        if (list.length > 0)
          popup = new CompleteTagList(textArea.getJextParent(), this, word.substring(1), list);
      }
    }
  }

  private void loadEntitiesList()
  {
    EntitiesListReader.read(TagsCompletion.class.getResourceAsStream("default-entities-list.xml"),
                                                                     "default-entities-list.xml");
  }

  private void loadTagsList()
  {
    TagsListReader.read(TagsCompletion.class.getResourceAsStream("default-tags-list.xml"),
                                                                 "default-tags-list.xml");
  }

  /* friendly */ Entity[] buildEntitiesList(String entity)
  {
    if (entitiesList == null)
      loadEntitiesList();

    Entity e;
    int len = entitiesList.size();
    Vector myTags = new Vector();

    for (int i = 0; i < len; i++)
    {
      e = (Entity) entitiesList.get(i);
      if (e.toString().startsWith(entity))
        myTags.add(e);
    }

    Entity[] list = new Entity[myTags.size()];
    for (int i = 0; i < list.length; i++)
      list[i] = (Entity) myTags.get(i);

    myTags.clear();
    myTags = null;

    return list;
  }

  /* friendly */ Tag[] buildTagsList(String tag)
  {
    if (tagsList == null)
      loadTagsList();

    boolean addEmptyTags = true;
    if (tag.startsWith("/"))
    {
      tag = tag.substring(1);
      addEmptyTags = false;
    }

    Tag e;
    int len = tagsList.size();
    Vector myTags = new Vector();

    for (int i = 0; i < len; i++)
    {
      e = (Tag) tagsList.get(i);

      if (e.isEmpty() && !addEmptyTags)
        continue;

      if (e.toString().startsWith(tag))
        myTags.add(e);
    }

    Tag[] list = new Tag[myTags.size()];
    for (int i = 0; i < list.length; i++)
      list[i] = (Tag) myTags.get(i);

    myTags.clear();
    myTags = null;

    return list;
  }
}

// End of TagsCompletion.java
