/*
 * Copyright (C) 2002 James Kolean
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

public class FindInFilesMatch {
  private boolean isHeader=false; //headers are really just place holders for labels
  private int matchCount; //only valid on headers
  private String filename;
  private String lineText; //only valid on non-headers
  private long lineNumber; //only valid on non-headers

  public FindInFilesMatch(String filename, int matchCount) {
    this(filename,null,0,true,matchCount);
  }
  public FindInFilesMatch(String filename, String lineText, long lineNumber) {
    this(filename,lineText,lineNumber,false,0);
  }
  private FindInFilesMatch(String filename, String lineText, long lineNumber, boolean isHeader, int matchCount) {
    this.filename=filename;
    this.lineText=lineText;
    this.lineNumber=lineNumber;
    this.isHeader=isHeader;
    this.matchCount=matchCount;
  }
  public String getFilename() {
    return filename;
  }
  public String getLineText() {
    return lineText;
  }
  public long getLineNumber() {
    if ( isHeader() ) return 1L;
    return lineNumber;
  }
  public boolean isHeader() {
    return isHeader;
  }
  public int getMatchCount() {
    return matchCount;
  }
}

