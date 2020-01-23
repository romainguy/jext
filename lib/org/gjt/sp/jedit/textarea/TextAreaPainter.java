/*
 * TextAreaPainter.java - Paints the text area
 * Copyright (C) 1999 Slava Pestov
 * Portions Copyright (C)2000-2001 Romain Guy
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

import javax.swing.ToolTipManager;
import javax.swing.text.*;
import javax.swing.JComponent;
import java.awt.event.MouseEvent;
import java.awt.*;

import org.gjt.sp.jedit.syntax.*;

/**
 * The text area repaint manager. It performs double buffering and paints
 * lines of text.
 * @author Slava Pestov
 * @version $Id: TextAreaPainter.java,v 1.1.1.1 2004/10/19 16:16:24 gfx Exp $
 */
public class TextAreaPainter extends JComponent implements TabExpander
{
  public static RenderingHints ANTI_ALIASED_RENDERING = null;
  public static RenderingHints DEFAULT_RENDERING = null;

  private static void initRenderings()
  {
    if (ANTI_ALIASED_RENDERING == null)
    {
      //ANTI_ALIASED_RENDERING = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
      //                                            RenderingHints.VALUE_ANTIALIAS_ON);
      ANTI_ALIASED_RENDERING = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                                                  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      //ANTI_ALIASED_RENDERING.put(RenderingHints.KEY_FRACTIONALMETRICS,
      //                           RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      //ANTI_ALIASED_RENDERING.put(RenderingHints.KEY_RENDERING,
      //                           RenderingHints.VALUE_RENDER_QUALITY);
    }

    if (DEFAULT_RENDERING == null)
    {
      DEFAULT_RENDERING = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                             RenderingHints.VALUE_ANTIALIAS_OFF);
    }
  }

  /**
    * Creates a new repaint manager. This should be not be called
    * directly.
    */
  public TextAreaPainter(JEditTextArea textArea, TextAreaDefaults defaults)
  {
    initRenderings();

    this.textArea = textArea;

    setAutoscrolls(true);
    setDoubleBuffered(true);
    setOpaque(true);

    ToolTipManager.sharedInstance().registerComponent(this);

    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    setFont(new Font("Monospaced",Font.PLAIN, 12));
    setForeground(Color.black);
    setBackground(Color.white);

    blockCaret = defaults.blockCaret;
    styles = defaults.styles;
    cols = defaults.cols;
    rows = defaults.rows;
    caretColor = defaults.caretColor;
    selectionColor = defaults.selectionColor;
    lineHighlightColor = defaults.lineHighlightColor;
    lineHighlight = defaults.lineHighlight;
    bracketHighlightColor = defaults.bracketHighlightColor;
    bracketHighlight = defaults.bracketHighlight;
    paintInvalid = defaults.paintInvalid;
    eolMarkerColor = defaults.eolMarkerColor;
    eolMarkers = defaults.eolMarkers;

    wrapGuide = defaults.wrapGuide;
    wrapGuideColor = defaults.wrapGuideColor;
    wrapGuideOffset = defaults.wrapGuideOffset;

    linesIntervalHighlight = defaults.linesIntervalHighlight;
    linesIntervalColor = defaults.linesIntervalColor;
    linesInterval = defaults.linesInterval;
  }

  ///////////////////////////////////////////////////////////////////////////////
  // ANTI ALIASING
  ///////////////////////////////////////////////////////////////////////////////

  private boolean antiAliasing = false;
  private boolean wasAntiAliasing = false;

  public boolean isAntiAliasingEnabled()
  {
    return antiAliasing;
  }

  public void setAntiAliasingEnabled(boolean on)
  {
    ///// TEMP FIX /////
    wasAntiAliasing = antiAliasing;
    //antiAliasing = on;
    antiAliasing = false;
  }

  private void setAntiAliasing(Graphics g)
  {
    if (antiAliasing)
      ((Graphics2D) g).setRenderingHints(ANTI_ALIASED_RENDERING);
    else if (wasAntiAliasing != antiAliasing)
      ((Graphics2D) g).setRenderingHints(DEFAULT_RENDERING);
  }

  ///////////////////////////////////////////////////////////////////////////////
  // WRAP GUIDE
  ///////////////////////////////////////////////////////////////////////////////

  private boolean wrapGuide;
  private Color wrapGuideColor;
  private int wrapGuideOffset;

  public void setWrapGuideEnabled(boolean enabled)
  {
    wrapGuide = enabled;
  }

  public void setWrapGuideOffset(int offset)
  {
    wrapGuideOffset = offset;
  }

  public void setWrapGuideColor(Color color)
  {
    wrapGuideColor = color;
  }

  ///////////////////////////////////////////////////////////////////////////////
  // INTERVAL HIGHLIGHTING
  ///////////////////////////////////////////////////////////////////////////////

  private boolean linesIntervalHighlight;
  private Color linesIntervalColor;
  private int linesInterval;

  public void setLinesIntervalHighlightEnabled(boolean enabled)
  {
    linesIntervalHighlight = enabled;
  }

  public void setLinesInterval(int offset)
  {
    linesInterval = offset;
  }

  public void setLinesIntervalHighlightColor(Color color)
  {
    linesIntervalColor = color;
  }

  /**
    * Returns if this component can be traversed by pressing the
    * Tab key. This returns false.
    */
  public final boolean isManagingFocus()
  {
    return false;
  }

  /**
    * Returns the syntax styles used to paint colorized text. Entry <i>n</i>
    * will be used to paint tokens with id = <i>n</i>.
    * @see org.gjt.sp.jedit.syntax.Token
    */
  public final SyntaxStyle[] getStyles()
  {
    return styles;
  }

  /**
    * Sets the syntax styles used to paint colorized text. Entry <i>n</i>
    * will be used to paint tokens with id = <i>n</i>.
    * @param styles The syntax styles
    * @see org.gjt.sp.jedit.syntax.Token
    */
  public final void setStyles(SyntaxStyle[] styles)
  {
    this.styles = styles;
    repaint();
  }

  /**
    * Returns the caret color.
    */
  public final Color getCaretColor()
  {
    return caretColor;
  }

  /**
    * Sets the caret color.
    * @param caretColor The caret color
    */
  public final void setCaretColor(Color caretColor)
  {
    this.caretColor = caretColor;
    invalidateSelectedLines();
  }

  /**
    * Returns the selection color.
    */
  public final Color getSelectionColor()
  {
    return selectionColor;
  }

  /**
    * Sets the selection color.
    * @param selectionColor The selection color
    */
  public final void setSelectionColor(Color selectionColor)
  {
    this.selectionColor = selectionColor;
    invalidateSelectedLines();
  }

  /**
    * Returns the highlight color.
    */
  public final Color getHighlightColor()
  {
    return lineHighlightColor;
  }

  /**
    * Sets the highlight color.
    * @param highlightColor The highlight color
    */
  public final void setHighlightColor(Color highlightColor)
  {
    this.highlightColor = highlightColor;
    repaint();
  }

  /**
    * Returns the line highlight color.
    */
  public final Color getLineHighlightColor()
  {
    return lineHighlightColor;
  }

  /**
    * Sets the line highlight color.
    * @param lineHighlightColor The line highlight color
    */
  public final void setLineHighlightColor(Color lineHighlightColor)
  {
    this.lineHighlightColor = lineHighlightColor;
    invalidateSelectedLines();
  }

  /**
    * Returns true if line highlight is enabled, false otherwise.
    */
  public final boolean isLineHighlightEnabled()
  {
    return lineHighlight;
  }

  /**
    * Enables or disables current line highlighting.
    * @param lineHighlight True if current line highlight should be enabled,
    * false otherwise
    */
  public final void setLineHighlightEnabled(boolean lineHighlight)
  {
    this.lineHighlight = lineHighlight;
    invalidateSelectedLines();
  }

  /**
    * Returns the bracket highlight color.
    */
  public final Color getBracketHighlightColor()
  {
    return bracketHighlightColor;
  }

  /**
    * Sets the bracket highlight color.
    * @param bracketHighlightColor The bracket highlight color
    */
  public final void setBracketHighlightColor(Color bracketHighlightColor)
  {
    this.bracketHighlightColor = bracketHighlightColor;
    invalidateLine(textArea.getBracketLine());
  }

  /**
    * Returns true if bracket highlighting is enabled, false otherwise.
    * When bracket highlighting is enabled, the bracket matching the
    * one before the caret (if any) is highlighted.
    */
  public final boolean isBracketHighlightEnabled()
  {
    return bracketHighlight;
  }

  /**
    * Enables or disables bracket highlighting.
    * When bracket highlighting is enabled, the bracket matching the
    * one before the caret (if any) is highlighted.
    * @param bracketHighlight True if bracket highlighting should be
    * enabled, false otherwise
    */
  public final void setBracketHighlightEnabled(boolean bracketHighlight)
  {
    this.bracketHighlight = bracketHighlight;
    invalidateLine(textArea.getBracketLine());
  }

  /**
    * Returns true if the caret should be drawn as a block, false otherwise.
    */
  public final boolean isBlockCaretEnabled()
  {
    return blockCaret;
  }

  /**
    * Sets if the caret should be drawn as a block, false otherwise.
    * @param blockCaret True if the caret should be drawn as a block,
    * false otherwise.
    */
  public final void setBlockCaretEnabled(boolean blockCaret)
  {
    this.blockCaret = blockCaret;
    invalidateSelectedLines();
  }

  /**
    * Returns the EOL marker color.
    */
  public final Color getEOLMarkerColor()
  {
    return eolMarkerColor;
  }

  /**
    * Sets the EOL marker color.
    * @param eolMarkerColor The EOL marker color
    */
  public final void setEOLMarkerColor(Color eolMarkerColor)
  {
    this.eolMarkerColor = eolMarkerColor;
    repaint();
  }

  /**
    * Returns true if EOL markers are drawn, false otherwise.
    */
  public final boolean getEOLMarkersPainted()
  {
    return eolMarkers;
  }

  /**
    * Sets if EOL markers are to be drawn.
    * @param eolMarkers True if EOL markers should be drawn, false otherwise
    */
  public final void setEOLMarkersPainted(boolean eolMarkers)
  {
    this.eolMarkers = eolMarkers;
    repaint();
  }

  /**
    * Returns true if invalid lines are painted as red tildes (~),
    * false otherwise.
    */
  public boolean getInvalidLinesPainted()
  {
    return paintInvalid;
  }

  /**
    * Sets if invalid lines are to be painted as red tildes.
    * @param paintInvalid True if invalid lines should be drawn, false otherwise
    */
  public void setInvalidLinesPainted(boolean paintInvalid)
  {
    this.paintInvalid = paintInvalid;
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
    * Adds a custom first priority highlight painter.
    * @param highlight The highlight
    */
  public void addCustomFirstPriorityHighlight(TextAreaHighlight highlight)
  {
    highlight.init(textArea, firstPriorityHighlights);
    firstPriorityHighlights = highlight;
  }


  /**
    * Returns the tool tip to display at the specified location.
    * @param evt The mouse event
    */
  public String getToolTipText(MouseEvent evt)
  {
    if (highlights != null)
      return highlights.getToolTipText(evt);
    else
      return null;
  }

  /**
    * Returns the font metrics used by this component.
    */
  public FontMetrics getFontMetrics()
  {
    return fm;
  }

  /**
    * Sets the font for this component. This is overridden to update the
    * cached font metrics and to recalculate which lines are visible.
    * @param font The font
    */
  public void setFont(Font font)
  {
    super.setFont(font);
    fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
    textArea.recalculateVisibleLines();
  }

  /**
    * Repaints the text.
    * @param g The graphics context
    */
  public void paint(Graphics gfx)
  {
    ///// TEMP FIX /////
    //setAntiAliasing(gfx);

    tabSize = fm.charWidth(' ') *
              ((Integer) textArea .getDocument().getProperty(PlainDocument.tabSizeAttribute)).intValue();

    Rectangle clipRect = gfx.getClipBounds();

    gfx.setColor(getBackground());
    gfx.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);

    // We don't use yToLine() here because that method doesn't
    // return lines past the end of the document
    int height = fm.getHeight();
    int firstLine = textArea.getFirstLine();
    int firstInvalid = firstLine + clipRect.y / height;
    // Because the clipRect's height is usually an even multiple
    // of the font height, we subtract 1 from it, otherwise one
    // too many lines will always be painted.
    int lastInvalid = firstLine + (clipRect.y + clipRect.height - 1) / height;
    int x = textArea.getHorizontalOffset();
    int lineCount = textArea.getLineCount();

    try
    {
      TokenMarker tokenMarker = textArea.getDocument().getTokenMarker();
      int maxWidth = textArea.maxHorizontalScrollWidth;

      boolean updateMaxHorizontalScrollWidth = false;
      for (int line = firstInvalid; line <= lastInvalid; line++)
      {
        boolean valid = line >= 0 && line < lineCount;

        int width = paintLine(gfx, tokenMarker, valid, line, x) - x + 5 /* Yay */;
        if (valid)
        {
          tokenMarker.setLineWidth(line, width);
          if (width > maxWidth)
            updateMaxHorizontalScrollWidth = true;
        }
      }

      if (tokenMarker.isNextLineRequested())
      {
        int h = clipRect.y + clipRect.height;
        repaint(0, h, getWidth(), getHeight() - h);
      }

      if (updateMaxHorizontalScrollWidth)
        textArea.updateMaxHorizontalScrollWidth();
    } catch (Exception e) {
      System.err.println("Error repainting line" + " range {" + firstInvalid + "," + lastInvalid + "}:");
      e.printStackTrace();
    }
  }

  /**
    * Marks a line as needing a repaint.
    * @param line The line to invalidate
    */
  public final void invalidateLine(int line)
  {
    repaint(0, textArea.lineToY(line) + fm.getMaxDescent() + fm.getLeading(), getWidth(), fm.getHeight());
  }

  /**
    * Marks a range of lines as needing a repaint.
    * @param firstLine The first line to invalidate
    * @param lastLine The last line to invalidate
    */
  public final void invalidateLineRange(int firstLine, int lastLine)
  {
    repaint(0, textArea.lineToY(firstLine) + fm.getMaxDescent() + fm.getLeading(), getWidth(),
            (lastLine - firstLine + 1) * fm.getHeight());
  }

  /**
    * Repaints the lines containing the selection.
    */
  public final void invalidateSelectedLines()
  {
    invalidateLineRange(textArea.getSelectionStartLine(), textArea.getSelectionEndLine());
  }

  /**
    * Implementation of TabExpander interface. Returns next tab stop after
    * a specified point.
    * @param x The x co-ordinate
    * @param tabOffset Ignored
    * @return The next tab stop after <i>x</i>
    */
  public float nextTabStop(float x, int tabOffset)
  {
    if (tabSize == 0)
    {
      tabSize = fm.charWidth(' ') *
              ((Integer) textArea.getDocument().getProperty(PlainDocument.tabSizeAttribute)).intValue();
    }

    int offset = textArea.getHorizontalOffset();
    int ntabs = ((int) x - offset) / tabSize;
    return (ntabs + 1) * tabSize + offset;
  }

  /**
    * Returns the painter's preferred size.
    */
  public Dimension getPreferredSize()
  {
    Dimension dim = new Dimension();
    dim.width = fm.charWidth('w') * cols;
    dim.height = fm.getHeight() * rows;
    return dim;
  }


  /**
    * Returns the painter's minimum size.
    */
  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  // protected members
  protected JEditTextArea textArea;

  protected SyntaxStyle[] styles;
  protected Color caretColor;
  protected Color selectionColor;
  protected Color lineHighlightColor;
  protected Color highlightColor;
  protected Color bracketHighlightColor;
  protected Color eolMarkerColor;

  protected boolean blockCaret;
  protected boolean lineHighlight;
  protected boolean bracketHighlight;
  protected boolean paintInvalid;
  protected boolean eolMarkers;
  protected int cols;
  protected int rows;

  protected int tabSize;
  protected FontMetrics fm;

  protected TextAreaHighlight highlights;
  protected TextAreaHighlight firstPriorityHighlights;

  protected int paintLine(Graphics gfx, TokenMarker tokenMarker, boolean valid, int line, int x)
  {
    Font defaultFont = getFont();
    Color defaultColor = getForeground();

    int y = textArea.lineToY(line);

    if (!valid)
    {
      if (paintInvalid)
      {
        paintHighlight(gfx, line, y);
        styles[Token.INVALID].setGraphicsFlags(gfx, defaultFont);
        gfx.drawString("~", 0, y + fm.getHeight());
      }
    } else {
      x = paintSyntaxLine(gfx, tokenMarker, line, defaultFont, defaultColor, x, y);
    }

    return x;
  }

  protected int paintSyntaxLine(Graphics gfx, TokenMarker tokenMarker, int line, Font defaultFont,
                                Color defaultColor, int x, int y)
  {
    // priority highlights first
    if (firstPriorityHighlights != null)
      firstPriorityHighlights.paintHighlight(gfx, line, y);

    Segment tempSeg = textArea.lineSegment;
    textArea.getLineText(line, textArea.lineSegment);

    Token tokens = tokenMarker.markTokens(textArea.lineSegment, line);
    Token first = tokens;

    int backY = y + fm.getLeading() + fm.getMaxDescent();
    int backHeight = fm.getHeight();
    int backWidth = fm.charWidth('w');
    int offset = x;
    int offsetShift = 0;
    int charOffset = 0;
    String textLine = textArea.lineSegment.toString();
    org.jext.JextTextArea jextArea = (org.jext.JextTextArea) textArea;

    while (tokens.id != Token.END)
    {
      offsetShift = backWidth * org.jext.Utilities.getRealLength(textLine.substring(charOffset, charOffset + tokens.length),
                                                                 jextArea.getTabSize());
      charOffset += tokens.length;

      if (tokens.highlightBackground)
      {
        gfx.setColor(highlightColor);
        gfx.fillRect(offset, backY, offsetShift, backHeight);
      }
      offset += offsetShift;
      tokens = tokens.next;
    }

    textArea.lineSegment = tempSeg;
    paintHighlight(gfx, line, y);
    textArea.getLineText(line, textArea.lineSegment);

    gfx.setFont(defaultFont);
    gfx.setColor(defaultColor);
    y += fm.getHeight();

    x = SyntaxUtilities.paintSyntaxLine(textArea.lineSegment, first,
                                        styles, this, gfx, x, y);

    if (eolMarkers)
    {
      gfx.setColor(eolMarkerColor);
      gfx.drawString(".", x, y);
    }

    return x;
  }

  protected void paintHighlight(Graphics gfx, int line, int y)
  {
    if (line >= textArea.getSelectionStartLine() && line <= textArea.getSelectionEndLine())
      paintLineHighlight(gfx, line, y);

    if (linesIntervalHighlight && linesInterval > 0)
      paintLinesInterval(gfx, line, y);

    if (wrapGuide && wrapGuideOffset > 0)
      paintWrapGuide(gfx, line, y);

    if (highlights != null)
      highlights.paintHighlight(gfx, line, y);

    if (bracketHighlight && line == textArea.getBracketLine())
      paintBracketHighlight(gfx, line, y);

    if (line == textArea.getCaretLine())
      paintCaret(gfx, line, y);

    if (line == textArea.getShadowCaretLine())
      paintShadowCaret(gfx, line, y);
  }

  protected void paintWrapGuide(Graphics gfx, int line, int y)
  {
    gfx.setColor(wrapGuideColor);

    int _offset = y + (fm.getLeading() + fm.getMaxDescent());
    int start = (line > 0) ? _offset : 0;
    int end = (line != textArea.getLineCount() - 1) ? _offset + fm.getHeight() : textArea.getHeight();

    int charWidth = fm.charWidth('m');

    int offset = textArea.getHorizontalOffset();
    int width = textArea.getWidth();
    int off = wrapGuideOffset * charWidth + offset;

    if (off >= 0 && off < width)
      gfx.drawLine(off, start, off, end);
  }

  protected void paintLinesInterval(Graphics gfx, int line, int y)
  {
    if (((line + 1) % linesInterval) == 0)
    {
      int height = fm.getHeight();
      int _offset = y + fm.getLeading() + fm.getMaxDescent();

      int selectionStart = textArea.getSelectionStart();
      int selectionEnd = textArea.getSelectionEnd();

      gfx.setColor(linesIntervalColor);
      gfx.fillRect(0, _offset, getWidth(), height);

      gfx.setColor(selectionColor);

      int selectionStartLine = textArea.getSelectionStartLine();
      int selectionEndLine = textArea.getSelectionEndLine();
      int lineStart = textArea.getLineStartOffset(line);

      int x1, x2;

      if (textArea.isSelectionRectangular())
      {
        int lineLen = textArea.getLineLength(line);
        x1 = textArea.offsetToX(line,
                Math.min(lineLen, selectionStart - textArea.getLineStartOffset(selectionStartLine)));
        x2 = textArea.offsetToX(line, Math.min(lineLen, selectionEnd - textArea.getLineStartOffset(selectionEndLine)));
        if (x1 == x2)
          x2++;

      } else if (selectionStartLine == selectionEndLine) {
        x1 = textArea.offsetToX(line, selectionStart - lineStart);
        x2 = textArea.offsetToX(line, selectionEnd - lineStart);
      } else if (line == selectionStartLine) {
        x1 = textArea.offsetToX(line, selectionStart - lineStart);
        x2 = textArea.offsetToX(line, textArea.getLineLength(line));
      } else if (line == selectionEndLine) {
        x1 = 0;
        x2 = textArea.offsetToX(line, selectionEnd - lineStart);
      } else {
        x1 = 0;
        x2 = textArea.offsetToX(line, textArea.getLineLength(line));
      }

      gfx.fillRect(x1 > x2 ? x2 : x1, _offset, x1 > x2 ? (x1 - x2) : (x2 - x1), height);
    }
  }

  protected void paintLineHighlight(Graphics gfx, int line, int y)
  {
    int height = fm.getHeight();
    y += fm.getLeading() + fm.getMaxDescent();

    int selectionStart = textArea.getSelectionStart();
    int selectionEnd = textArea.getSelectionEnd();

    if (selectionStart == selectionEnd)
    {
      if (lineHighlight)
      {
        gfx.setColor(lineHighlightColor);
        gfx.fillRect(0, y, getWidth(), height);
      }
    } else {
      gfx.setColor(selectionColor);

      int selectionStartLine = textArea.getSelectionStartLine();
      int selectionEndLine = textArea.getSelectionEndLine();
      int lineStart = textArea.getLineStartOffset(line);

      int x1, x2;
      if (textArea.isSelectionRectangular())
      {
        int lineLen = textArea.getLineLength(line);
        x1 = textArea.offsetToX(line,
                                Math.min(lineLen, selectionStart - textArea.getLineStartOffset(selectionStartLine)));
        x2 = textArea.offsetToX(line,
                                Math.min(lineLen, selectionEnd - textArea.getLineStartOffset(selectionEndLine)));
        if (x1 == x2)
          x2++;
      } else if (selectionStartLine == selectionEndLine) {
        x1 = textArea.offsetToX(line, selectionStart - lineStart);
        x2 = textArea.offsetToX(line, selectionEnd - lineStart);
      } else if (line == selectionStartLine) {
        x1 = textArea.offsetToX(line, selectionStart - lineStart);
        //x2 = getWidth();
        x2 = textArea.offsetToX(line, textArea.getLineLength(line));
      } else if (line == selectionEndLine) {
        x1 = 0;
        x2 = textArea.offsetToX(line, selectionEnd - lineStart);
      } else {
        x1 = 0;
        //x2 = getWidth();
        x2 = textArea.offsetToX(line, textArea.getLineLength(line));
      }

      int w = x1 > x2 ? (x1 - x2) : (x2 - x1);
      if (w == 0) w = 4;
      // "inlined" min/max()
      gfx.fillRect(x1 > x2 ? x2 : x1, y, w, height);
    }
  }

  protected void paintBracketHighlight(Graphics gfx, int line, int y)
  {
    int position = textArea.getBracketPosition();
    if (position == -1)
      return;
    y += fm.getLeading() + fm.getMaxDescent();
    int x = textArea.offsetToX(line, position);
    gfx.setColor(bracketHighlightColor);
    // Hack!!! Since there is no fast way to get the character
    // from the bracket matching routine, we use ( since all
    // brackets probably have the same width anyway
    gfx.fillRect(x, y, fm.charWidth('(') - 1, fm.getHeight() - 1);
  }

  protected void paintCaret(Graphics gfx, int line, int y)
  {
    if (textArea.isCaretVisible())
    {
      int offset = textArea.getCaretPosition() - textArea.getLineStartOffset(line);
      int caretX = textArea.offsetToX(line, offset);
      int caretWidth = ((blockCaret || textArea.isOverwriteEnabled()) ? fm.charWidth('w') : 1);
      y += fm.getLeading() + fm.getMaxDescent();
      int height = fm.getHeight();

      gfx.setColor(caretColor);

      if (textArea.isOverwriteEnabled())
      {
        gfx.fillRect(caretX, y + height - 1, caretWidth, 1);
      } else if (caretWidth > 1) {
        gfx.drawRect(caretX, y, caretWidth - 1, height - 1);
      } else {
        gfx.drawLine(caretX, y, caretX, y + height - 1);
      }
    }
  }

  protected void paintShadowCaret(Graphics gfx, int line, int y)
  {
    int offset = textArea.getShadowCaretPosition() - textArea.getLineStartOffset(line);
    int caretX = textArea.offsetToX(line, offset);
    y += fm.getLeading() + fm.getMaxDescent();
    int height = fm.getHeight();

    gfx.setColor(caretColor.darker());
    for (int i = 0; i < height; i += 3)
      gfx.drawLine(caretX, y + i, caretX, y + i + 1);
  }
}