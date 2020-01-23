/*
 * 10/29/2001 - 22:23:08
 *
 * JextButton.java - A modified button
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

package org.jext.gui;

import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;

import java.awt.image.FilteredImageSource;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jext.EditAction;
import org.jext.Jext;
import org.jext.JextTextArea;
import org.jext.MenuAction;

public class JextButton extends JButton
{
  private MouseHandler _mouseListener;
  private ImageIcon grayedIcon, coloredIcon;
  private Color nColor;
  private static Color commonHighlightColor = new Color(192, 192, 210);
  private static boolean rollover = true;
  private static boolean blockHighlightChange = false;

  public static void setRollover(boolean enabled)
  {
    rollover = enabled;
  }

  public static void setHighlightColor(Color color)
  {
    if (!blockHighlightChange)
      commonHighlightColor = color;
  }

  public static Color getHighlightColor()
  {
    return commonHighlightColor;
  }

  public static void blockHighlightChange()
  {
    blockHighlightChange = true;
  }

  public static void unBlockHighlightChange()
  {
    blockHighlightChange = false;
  }

  private void init()
  {
    _mouseListener = new MouseHandler();
    if (rollover)
    {
      setBorderPainted(false);
      addMouseListener(_mouseListener);
    } else {
      if (Jext.getButtonsHighlight())
      {
        nColor = getBackground();
        addMouseListener(_mouseListener);
      }
    }
  }

  public JextButton()
  {
    super();
    init();
  }

  public JextButton(Icon icon)
  {
    super(icon);
    init();
  }

  public JextButton(String text)
  {
    super(text);
    init();
  }

  public JextButton(String text, Icon icon)
  {
    super(text, icon);
    init();
  }

  public void setGrayed(boolean on)
  {
    if (coloredIcon == null)
      coloredIcon = (ImageIcon) getIcon();

    if (on && getRolloverIcon() == null)
    {
      GrayFilter filter = new GrayFilter(true, 35);
      Image grayImage = Toolkit.getDefaultToolkit().createImage(
                        new FilteredImageSource(coloredIcon.getImage().getSource(), filter));
      grayedIcon = new ImageIcon(grayImage);
      setRolloverIcon(coloredIcon);
    }

    setIcon(on ? grayedIcon : coloredIcon);
    setRolloverEnabled(on);
  }

  protected void fireActionPerformed(ActionEvent event)
  {
    JextTextArea area = MenuAction.getTextArea(this);
    area.setOneClick(null);
    area.endCurrentEdit();

    Object[] listeners = listenerList.getListenerList();
    ActionEvent e = null;

    for (int i = listeners.length - 2; i >= 0; i -= 2)
    {
      if (listeners[i + 1] instanceof EditAction && !area.isEditable())
        continue;
  
      if (listeners[i] == ActionListener.class)
      {
        if (e == null)
        {
          String actionCommand = event.getActionCommand();
          if(actionCommand == null)
             actionCommand = getActionCommand();

          e = new ActionEvent(this,
                              ActionEvent.ACTION_PERFORMED,
                              actionCommand,
                              event.getModifiers());
        }
        ((ActionListener) listeners[i + 1]).actionPerformed(e);
      }
    }
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseEntered(MouseEvent me)
    {
      if (isEnabled())
      {
        if (rollover)
          setBorderPainted(true);
        else
          setBackground(commonHighlightColor);
      }
    }

    public void mouseExited(MouseEvent me)
    {
      if (isEnabled())
      {
        if (rollover)
          setBorderPainted(false);
        else
          setBackground(nColor);
      }
    }
  }
  


}

// End of JextButton.java