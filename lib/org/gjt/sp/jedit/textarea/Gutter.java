/*
 * Gutter.java
 * Copyright (C) 1999, 2000 mike dillon
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

package org.gjt.sp.jedit.textarea;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.border.Border;

import org.jext.JextTextArea;

public class Gutter extends JComponent implements SwingConstants
{
  public Gutter(JEditTextArea textArea, TextAreaDefaults defaults)
  {
    this.textArea = textArea;

    setBackground(defaults.gutterBgColor);
    setForeground(defaults.gutterFgColor);
    setHighlightedForeground(defaults.gutterHighlightColor);
    setCaretMark(defaults.caretMarkColor);
    setAnchorMark(defaults.anchorMarkColor);
    setSelectionMark(defaults.selectionMarkColor);

    setFont(defaults.gutterFont);
    setBorder(defaults.gutterBorderWidth, defaults.gutterBorderColor);
    setLineNumberAlignment(defaults.gutterNumberAlignment);

    setGutterWidth(defaults.gutterWidth);
    setCollapsed(defaults.gutterCollapsed);

    GutterMouseListener ml = new GutterMouseListener();
    addMouseListener(ml);
    addMouseMotionListener(ml);
  }

  ///////////////////////////////////////////////////////////////////////////////
  // ANTI ALIASING
  ///////////////////////////////////////////////////////////////////////////////

  private boolean antiAliasing = false;
  private boolean wasAntiAliasing = false;

  public void setAntiAliasingEnabled(boolean on)
  {
    wasAntiAliasing = antiAliasing;
    antiAliasing = on;
  }

  private void setAntiAliasing(Graphics g)
  {
    if (antiAliasing)
      ((Graphics2D) g).setRenderingHints(TextAreaPainter.ANTI_ALIASED_RENDERING);
    else if (wasAntiAliasing != antiAliasing)
      ((Graphics2D) g).setRenderingHints(TextAreaPainter.DEFAULT_RENDERING);
  }

  public void paintComponent(Graphics gfx)
  {
    if (!collapsed)
    {
      setAntiAliasing(gfx);

      // fill the background
      Rectangle r = gfx.getClipBounds();
      gfx.setColor(getBackground());
      gfx.fillRect(r.x, r.y, r.width, r.height);

      // paint custom highlights, if there are any
      if (highlights != null)
        paintCustomHighlights(gfx);

      // paint line numbers, if they are enabled
      if (lineNumberingEnabled)
        paintLineNumbers(gfx);
    }
  }

  protected void paintLineNumbers(Graphics gfx)
  {
    FontMetrics pfm = textArea.getPainter().getFontMetrics();
    int lineHeight = pfm.getHeight();
    int baseline = (int) Math.round((this.baseline + lineHeight - pfm.getMaxDescent()) / 2.0);

    int firstLine = textArea.getFirstLine() + 1;
    int lastLine = firstLine + (getHeight() / lineHeight);

    int firstValidLine = (int) Math.max(1, firstLine);
    int lastValidLine = (int) Math.min(textArea.getLineCount(), lastLine);

    gfx.setFont(getFont());
    gfx.setColor(getForeground());

    String number;

    for (int line = firstLine; line <= lastLine; line++, baseline += lineHeight)
    {
      // only print numbers for valid lines
      if (line < firstValidLine || line > lastValidLine)
        continue;

      number = Integer.toString(line);
      int offset;

      switch (alignment)
      {
        case RIGHT:
          offset = gutterSize.width - collapsedSize.width - (fm.stringWidth(number) + 1);
          break;
        case CENTER:
          offset = ((gutterSize.width - collapsedSize.width) - fm.stringWidth(number)) / 2;
          break;
        case LEFT: default:
          offset = 1;
      }

      if (interval > 1 && line % interval == 0)
      {
        gfx.setColor(getHighlightedForeground());
        gfx.drawString(number, ileft + offset, baseline);
        gfx.setColor(getForeground());
      } else {
        gfx.drawString(number, ileft + offset, baseline);
      }

      if (line == textArea.getCaretLine() + 1)
      {
        gfx.setColor(caretMark);
        gfx.drawRect(ileft + offset - 8, baseline - 6, 4, 4);
      }

      int anchor = ((JextTextArea) textArea).getAnchorOffset();
      if (anchor != -1 && line == textArea.getLineOfOffset(anchor) + 1)
      {
        gfx.setColor(anchorMark);
        gfx.drawRect(ileft + offset - 8, baseline - 6, 4, 4);
      }

      if ( textArea.getSelectionStart() == textArea.getSelectionEnd())
      {
        gfx.setColor(getForeground());
        continue;
      }

      if (line >= textArea.getSelectionStartLine() + 1 && line <= textArea.getSelectionEndLine() + 1)
      {
        gfx.setColor(selectionMark);
        gfx.fillRect(ileft + offset - 7, baseline - 5, 3, 3);
      }

      gfx.setColor(getForeground());
    }
  }

  protected void paintCustomHighlights(Graphics gfx)
  {
    int lineHeight = textArea.getPainter().getFontMetrics() .getHeight();
    int firstLine = textArea.getFirstLine();
    int lastLine = firstLine + (getHeight() / lineHeight);

    int y = 0;

    for (int line = firstLine; line < lastLine; line++, y += lineHeight)
    {
      highlights.paintHighlight(gfx, line, y);
    }
  }

  /**
   * Adds a custom highlight painter.
   * @param highlight The highlight
   */
  public void addCustomHighlight(TextAreaHighlight highlight)
  {
    highlight.init(textArea, highlights);
    highlights = highlight;
  }

  /**
   * Convenience method for setting a default matte border on the right
   * with the specified border width and color
   * @param width The border width (in pixels)
   * @param color The border color
   */
  public void setBorder(int width, Color color)
  {
    setBorder(BorderFactory.createMatteBorder(0,0,0,width,color));
  }

  /*
   * JComponent.setBorder(Border) is overridden here to cache the left
   * inset of the border (if any) to avoid having to fetch it during every
   * repaint.
   */
  public void setBorder(Border border)
  {
    super.setBorder(border);

    if (border == null)
    {
      ileft = 0;
      collapsedSize.width = 0;
      collapsedSize.height = 0;
    }
    else
    {
      Insets insets = border.getBorderInsets(this);
      ileft = insets.left;
      collapsedSize.width = insets.left + insets.right;
      collapsedSize.height = insets.top + insets.bottom;
    }
  }

  /*
   * JComponent.setFont(Font) is overridden here to cache the baseline for
   * the font. This avoids having to get the font metrics during every
   * repaint.
   */
  public void setFont(Font font)
  {
    super.setFont(font);

    fm = getFontMetrics(font);
    baseline = fm.getHeight() - fm.getMaxDescent();
  }

  /**
   * Set the foreground color for highlighted line numbers
   * @param highlight The highlight color
   */
  public void setHighlightedForeground(Color highlight)
  {
    intervalHighlight = highlight;
  }

  /**
   * Get the foreground color for highlighted line numbers
   * @return The highlight color
   */
  public Color getHighlightedForeground()
  {
    return intervalHighlight;
  }

  public void setCaretMark(Color mark)
  {
    caretMark = mark;
  }

  public void setAnchorMark(Color mark)
  {
    anchorMark = mark;
  }

  public void setSelectionMark(Color mark)
  {
    selectionMark = mark;
  }

  /**
   * Set the width of the expanded gutter
   * @param width The gutter width
   */
  public void setGutterWidth(int width)
  {
    if (width < collapsedSize.width) width = collapsedSize.width;
    gutterSize.width = width;
    // if the gutter is expanded, ask the text area to revalidate
    // the layout to resize the gutter
    if (!collapsed) textArea.revalidate();
  }

  /**
   * Get the width of the expanded gutter
   * @return The gutter width
   */
  public int getGutterWidth()
  {
    return gutterSize.width;
  }

  /*
   * Component.getPreferredSize() is overridden here to support the
   * collapsing behavior.
   */
  public Dimension getPreferredSize()
  {
    if (collapsed)
    {
      return collapsedSize;
    } else {
      return gutterSize;
    }
  }

  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  public String getToolTipText(MouseEvent evt)
  {
    return (highlights == null) ? null :
      highlights.getToolTipText(evt);
  }

  /**
   * Identifies whether or not the line numbers are drawn in the gutter
   * @return true if the line numbers are drawn, false otherwise
   */
  public boolean isLineNumberingEnabled()
  {
    return lineNumberingEnabled;
  }

  /**
   * Turns the line numbering on or off and causes the gutter to be
   * repainted.
   * @param enabled true if line numbers are drawn, false otherwise
   */
  public void setLineNumberingEnabled(boolean enabled)
  {
    if (lineNumberingEnabled == enabled) return;

    lineNumberingEnabled = enabled;

    repaint();
  }

  /**
   * Identifies whether the horizontal alignment of the line numbers.
   * @return Gutter.RIGHT, Gutter.CENTER, Gutter.LEFT
   */
  public int getLineNumberAlignment()
  {
    return alignment;
  }

  /**
   * Sets the horizontal alignment of the line numbers.
   * @param alignment Gutter.RIGHT, Gutter.CENTER, Gutter.LEFT
   */
  public void setLineNumberAlignment(int alignment)
  {
    if (this.alignment == alignment) return;

    this.alignment = alignment;

    repaint();
  }

  /**
   * Identifies whether the gutter is collapsed or expanded.
   * @return true if the gutter is collapsed, false if it is expanded
   */
  public boolean isCollapsed()
  {
    return collapsed;
  }

  /**
   * Sets whether the gutter is collapsed or expanded and force the text
   * area to update its layout if there is a change.
   * @param collapsed true if the gutter is collapsed,
   *                   false if it is expanded
   */
  public void setCollapsed(boolean collapsed)
  {
    if (this.collapsed == collapsed) return;

    this.collapsed = collapsed;

    textArea.revalidate();
  }

  /**
   * Toggles whether the gutter is collapsed or expanded.
   */
  public void toggleCollapsed()
  {
    setCollapsed(!collapsed);
  }

  /**
   * Sets the number of lines between highlighted line numbers.
   * @return The number of lines between highlighted line numbers or
   *          zero if highlighting is disabled
   */
  public int getHighlightInterval()
  {
    return interval;
  }

  /**
   * Sets the number of lines between highlighted line numbers. Any value
   * less than or equal to one will result in highlighting being disabled.
   * @param interval The number of lines between highlighted line numbers
   */
  public void setHighlightInterval(int interval)
  {
    if (interval <= 1) interval = 0;
    this.interval = interval;
    repaint();
  }

  public JPopupMenu getContextMenu()
  {
    return context;
  }

  public void setContextMenu(JPopupMenu context)
  {
    this.context = context;
  }

  // private members
  // the JEditTextArea this gutter is attached to
  private JEditTextArea textArea;

  private JPopupMenu context;

  private TextAreaHighlight highlights;

  private int baseline = 0;
  private int ileft = 0;

  private Dimension gutterSize = new Dimension(0,0);
  private Dimension collapsedSize = new Dimension(0,0);

  private Color intervalHighlight;
  private Color caretMark;
  private Color anchorMark;
  private Color selectionMark;

  private FontMetrics fm;

  private int alignment;

  private int interval = 0;
  private boolean lineNumberingEnabled = true;
  private boolean collapsed = false;

  class GutterMouseListener extends MouseAdapter implements MouseMotionListener
  {
    public void mouseClicked(MouseEvent e)
    {
      int count = e.getClickCount();
      if (count == 1)
      {
        if (context == null || context.isVisible())
          return;

        if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
        {
          //XXX this is a hack to make sure the
          //XXX actions get the right text area
          textArea.requestFocus();
          context.show(Gutter.this, e.getX(), e.getY());
        }
      } else if (count >= 2) {
        toggleCollapsed();
      }
    }

    public void mousePressed(MouseEvent e)
    {
      dragStart = e.getPoint();
      startWidth = gutterSize.width;
    }

    public void mouseDragged(MouseEvent e)
    {
      if (dragStart == null) return;

      if (isCollapsed()) setCollapsed(false);

      Point p = e.getPoint();
      gutterSize.width = startWidth + p.x - dragStart.x;

      if (gutterSize.width < collapsedSize.width)
      {
        gutterSize.width = startWidth;
        setCollapsed(true);
      }

      SwingUtilities.invokeLater(new Runnable() {
        public void run()
        {
          textArea.revalidate();
        }
      });
    }

    public void mouseExited(MouseEvent e)
    {
      if (dragStart != null && dragStart.x > e.getPoint().x)
      {
        setCollapsed(true);
        gutterSize.width = startWidth;

        SwingUtilities.invokeLater(new Runnable() {
          public void run()
          {
            textArea.revalidate();
          }
        });
      }

      //dragStart = null;
    }

    public void mouseMoved(MouseEvent e) {}

    public void mouseReleased(MouseEvent e)
    {
      dragStart = null;
    }

    private Point dragStart = null;
    private int startWidth = 0;
  }
}
