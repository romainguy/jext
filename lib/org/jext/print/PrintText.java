/*
 * PrintText.java - Short Description
 * Copyright (C) 1999 Scot Bellamy, ACTS, Inc.
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

package org.jext.print;

import java.util.*;
import java.awt.print.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import javax.swing.*;
import javax.swing.text.*;


/**
 * A simple printing class to handle basic text printing.
 * Accepts an array of Strings or a PlainDocument and
 * prints all the lines contained there in.  Each String
 * in the array is assumed to be a separate line.
*/
public class PrintText
{

  private int numberOfpages_ = 0; // The number of pages
  private Book pages_ = new Book(); // This holds each page
  private int wrapOffset_ = 0; // Used to determine where to begin a wrapped line.
  private String docTitle_; // Used for document title (i.e. file name) when including the page header
  private String[] text_; // Text to print.
  private PrintingOptions printOptions_; // Print options (i.e. font, print header, etc.)
  private boolean softTabs_ = true; // Indicates whether soft or hard tabs are used.
  private int tabSize_ = 4; // Tab stop if hard tabs are used.

  /**
   * Constructor - Accepts a plain document and uses default font.
   * No header information will be printed.
  */
  public PrintText(PlainDocument document)
  {
    this(document, "", new PrintingOptions(), false, 4);
  }

  /**
   * Constructor - Accepts a plain document as well as  other print options,
   * including font, page title, and header indicator (true if printing header, false otherwise).
  */
  public PrintText(PlainDocument document, String docTitle, PrintingOptions printOptions, boolean softTabs, int tabSize)
  {
    printOptions_ = printOptions;
    softTabs_ = softTabs;
    tabSize_ = tabSize;

    if (docTitle != null)
    {
      docTitle_ = docTitle;
    } else {
      // If a new doc and no title, set docTitle to "New Document"
      docTitle_ = "New Document";
    }

    //  Get Root element of the document
    Element root = document.getDefaultRootElement();

    //get the number of lines (i.e. child elements)
    int count = root.getElementCount();

    //Allocate the array
    String lines[] = new String[count];

    Segment segment = new Segment();

    // Get each line element, get its text and put it in the string array
    for (int i = 0; i < count; i++)
    {
      Element lineElement = (Element) root.getElement(i);
      try
      {
        document.getText(lineElement.getStartOffset(), lineElement.getEndOffset() - lineElement.getStartOffset(),
                segment);
        lines[i] = segment.toString();
      }
      catch (BadLocationException ble)
      {
      } // Nothing gets added to the Array if there is a bad location
    }
    text_ = lines;
    printTextArray();
  }


  /**
   * Constructor - accepts an array of Strings, uses the default font, no header.
  */
  PrintText(String[] text)
  {
    printOptions_ = new PrintingOptions();
    text_ = text;
    printTextArray();
  }

  /**
   * Constructor - accepts an array of Strings and a font, no header.
  */
  PrintText(String[] text, Font font)
  {
    printOptions_ = new PrintingOptions();
    text_ = text;
    printTextArray();
  }


  /**
   * Where the print processing begins.
  */
  void printTextArray()
  {
    PageFormat pgfmt = printOptions_.getPageFormat();
    Font pageFont = printOptions_.getPageFont();
    try
    {
      PrinterJob job = PrinterJob.getPrinterJob(); // create a printjob
      //            pgfmt = job.pageDialog(pgfmt);                  // set a page format. Comment this if you do not want this to show
      //            pgfmt = job.validatePage(pgfmt);                // make sure the pageformat is ok

      text_ = removeEOLChar();

      if (printOptions_.getPrintLineNumbers() == true)
      {
        text_ = addLineNumbers();
      }
      if (printOptions_.getWrapText() == true)
      {
        text_ = wrapText();
      }

      pages_ = pageinateText(); // do the pagination

      try
      {
        job.setPageable(pages_); // set the book pageable so the printjob knows we are printing more than one page (maybe)
        if (job.printDialog())
        {
          job.print(); // print.  This calls each Page object's print method
        }
      }

      // catch any errors and be as ambiguous about them as possible :)
      catch (Exception e)
      {
        JOptionPane.showMessageDialog(null, "Printer Error", "Error", JOptionPane.OK_OPTION);
      }
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(null, "Printer Error", "Error", JOptionPane.OK_OPTION);
    }
  }

  /**
   * Eliminates end of line characters
  */
  private String[] removeEOLChar()
  {
    String temp1, temp2, temp3;
    int lineCount = text_.length;
    String [] newText = new String[lineCount];
    int offset = 0;

    for (int i = 0; i < lineCount; i++)
    {
      if (text_[i].length() == 1)
      {
        newText[i] = " ";
      }
      else
      {
        temp1 = text_[i].substring(text_[i].length() - 2, text_[i].length() - 1);
        temp2 = text_[i].substring(text_[i].length() - 1, text_[i].length());
        if (temp1.compareTo("\r") == 0 || temp1.compareTo("\n") == 0)
        {
          offset = 2;
        }
        else if (temp2.compareTo("\r") == 0 || temp2.compareTo("\n") == 0)
        {
          offset = 1;
        }
        else
        {
          offset = 0;
        }
        temp3 = text_[i].substring(0, text_[i].length() - offset);

        // Process tabs.  Assume tab stops.
        StringBuffer temp4 = new StringBuffer();
        int length = temp3.length();
        for (int j = 0; j < length; j++)
        {
          if ("\t".equals(temp3.substring(j, j + 1)) == true)
          {
            // Calcualte the numbe of spaces to the tab stop.
            int numSpaces = (temp4.length()) % tabSize_;

            if (numSpaces == 0)
            {
              numSpaces = tabSize_;
            }
            for (int x = 0; x < numSpaces; x++)
            {
              temp4.append(" ");
            }
          }
          else
          {
            temp4.append(temp3.substring(j, j + 1));
          }
        }
        newText[i] = temp4.toString();
      }
    }
    return newText;
  }



  /**
   * Addes line numbers to the beginning of each line.
  */
  private String[] addLineNumbers()
  {
    int numLines = text_.length;
    int totalNumSpaces = 0;
    String temp;
    String [] newText = new String[numLines];


    // Get the total number of digits in last line number
    // So that spacing and alignment can be done properly.
    Integer lines = new Integer(numLines);
    temp = lines.toString();
    totalNumSpaces = temp.length();

    // Set the wrap offset so that we can start wrapped lines in the proper place.
    wrapOffset_ = totalNumSpaces + 3;


    for (int i = 0; i < numLines; i++)
    {
      StringBuffer num = new StringBuffer();
      num.append(i + 1);
      int numLen = num.length();

      StringBuffer lineNum = new StringBuffer();

      for (int j = 0; j < (totalNumSpaces - numLen); j++)
      {
        lineNum.append(' ');
      }
      lineNum.append(num.toString());

      newText[i] = lineNum.toString() + ".  " + text_[i];
    }

    return newText;

  }

  /**
   * Creates a new array of lines that all fit the width of the page.
  */
  private String[] wrapText()
  {
    String currentLine = null;
    String tempString = null;
    Vector temp = new Vector();
    int lineCount = text_.length;
    int newLineCount = 0;
    StringBuffer wrapSpaces = new StringBuffer("");
    int i = 0;
    PageFormat pgfmt = printOptions_.getPageFormat();
    Font pageFont = printOptions_.getPageFont();
    double pageWidth = pgfmt.getImageableWidth();

    for (i = 0; i < wrapOffset_; i++)
    {
      wrapSpaces.append(' ');
    }

    for (i = 0; i < lineCount; i++)
    {
      currentLine = text_[i];
      while (pageFont.getStringBounds(currentLine,
              new FontRenderContext(pageFont.getTransform(), false, false)).getWidth() > pageWidth)
      {
        int numChars = (int)(currentLine.length() * pageWidth / pageFont.getStringBounds(currentLine,
                new FontRenderContext(pageFont.getTransform(), false, false)).getWidth());
        temp.add(currentLine.substring(0, numChars));
        currentLine = wrapSpaces.toString() + currentLine.substring(numChars, currentLine.length());
      }
      temp.add(currentLine);
    }

    newLineCount = temp.size();
    String [] newText = new String[newLineCount];

    for (int j = 0; j < newLineCount; j++)
    {
      newText[j] = (String) temp.get(j);
    }


    return newText;

  }



  /**
   * The pagination method, Paginate the text onto Printable page objects
  */
  private Book pageinateText()
  {

    Book book = new Book();
    int linesPerPage = 0; // lines on one page
    int currentLine = 0; // line I am  currently reading
    int pageNum = 0; // page #
    PageFormat pgfmt = printOptions_.getPageFormat();
    Font pageFont = printOptions_.getPageFont();
    int height = (int) pgfmt.getImageableHeight(); // height of a page
    int pages = 0; // number of pages

    linesPerPage = height / (pageFont.getSize() + 2); // number of lines on a page
    pages = ((int) text_.length / linesPerPage); // set number of pages
    String[] pageText; // one page of text
    String readString; // a temporary string to read from master string

    convertUnprintables(); // method to keep out errors

    if (printOptions_.getPrintHeader() == true)
    {
      linesPerPage = linesPerPage - 2;
    }

    while (pageNum <= pages)
    {

      pageText = new String[linesPerPage]; // create a new page
      for (int x = 0; x < linesPerPage; x++)
      {
        try
        {
          readString = text_[currentLine]; // read the string
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
          readString = " ";
        }
        pageText[x] = readString; // add to the page

        currentLine++;
      }
      pageNum++; // increase the page number I am on
      book.append(new Page(pageText, pageNum), pgfmt); // create a new page object with the text and add it to the book

    }
    return book; // return the completed book

  }

  /**
   * Converts unprintable things to a space.  stops some errors.
  */
  private void convertUnprintables()
  {
    String tempString;
    int i = text_.length;
    while (i > 0)
    {
      i--;
      tempString = text_[i];
      if (tempString == null || "".equals(tempString))
      {
        text_[i] = " ";
      }
    }
  }

  /**
   * An inner class that defines one page of text based
   * on data about the PageFormat etc. from the book defined
   * in the parent class
  */
  class Page implements Printable
  {

    private String[] pageText_; // the text for the page
    private int pageNumber_ = 0;

    Page(String[] text, int pageNum)
    {
      this.pageText_ = text; // set the page's text
      this.pageNumber_ = pageNum; // set page number.
    }

    /**
     * Defines the Printable print method, for printing a Page
    */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException // the printing part

    {
      int pos;
      int posOffset = 1;
      double pageWidth = pageFormat.getImageableWidth();
      Font pageFont = printOptions_.getPageFont();

      if (printOptions_.getPrintHeader() == true)
      {
        StringBuffer header = new StringBuffer();
        StringBuffer pageNumText = new StringBuffer();
        int i = 0;
        int headerPos = 0;
        int numSpaces = 0;

        Calendar date = Calendar.getInstance();
        header.append(date.get(Calendar.DAY_OF_MONTH));
        header.append('/');
        header.append(date.get(Calendar.MONTH) + 1);
        header.append('/');
        header.append(date.get(Calendar.YEAR));

        pageNumText.append("Page ");
        pageNumText.append(pageNumber_);

        int xPos;
        double margin = (pageFormat.getWidth() - pageFormat.getImageableWidth()) / 2;
        graphics.setFont(printOptions_.getHeaderFont());
        graphics.setColor(Color.black);
        pos = (int) pageFormat.getImageableY() + (printOptions_.getHeaderFont().getSize() + 2);
        graphics.drawString(header.toString(), (int) pageFormat.getImageableX(), pos); // draw a line of text
        xPos = (int)((pageFormat.getWidth() / 2) - (graphics.getFontMetrics().stringWidth(docTitle_) / 2));
        graphics.drawString(docTitle_, xPos, pos);
        xPos = (int)(pageFormat.getWidth() - margin - graphics.getFontMetrics().stringWidth(pageNumText.toString()));
        graphics.drawString(pageNumText.toString(), xPos, pos);
        posOffset = 3;
      }

      graphics.setFont(pageFont); // Set the font
      graphics.setColor(Color.black); // set color

      for (int x = 0; x < (pageText_.length); x++)
      {
        pos = (int) pageFormat.getImageableY() + (pageFont.getSize() + 2) * (x + posOffset);
        graphics.drawString(this.pageText_[x], (int) pageFormat.getImageableX(), pos); // draw a line of text
      }

      return Printable.PAGE_EXISTS; // print the page
    }

  }

  /**
   * An inner class that defines one section of printable text.
   * This allows the flexability to assign different fonts to
   * individual words or phrases (i.e. for headers/footers or
   * Syntax highlighting (pretty print).
  */
  class PrintableText
  {
    private Font font_;
    private boolean newLine_ = true;
    private String text_;

    PrintableText()
    {
    }

    PrintableText(String text, Font font, boolean newLine)
    {
      text_ = text;
      font_ = font;
      newLine_ = newLine;
    }

    String getText()
    {
      return text_;
    }

    void setText(String text)
    {
      text_ = text;
    }

    Font getFont()
    {
      return font_;
    }

    void setFont(Font font)
    {
      font_ = font;
    }

    boolean isNewLine()
    {
      return newLine_;
    }

    void setNewLine(boolean newLine)
    {
      newLine_ = newLine;
    }
  }
}

// End of PrintText.java
