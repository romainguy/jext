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

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jext.*;
import org.jext.gui.*;
import org.jext.event.*;
import org.jext.search.*;

public class FindInFilesHelper {
  private static final boolean DEBUG=false;
  private static int tabSize;

  private static String[] getFileNames(File dir, String filter, boolean searchSubdirectories) throws Exception {
    if ( (filter==null) || (filter.trim().length()==0) || ("*".equals(filter.trim())) ) filter="*.*";
    if (!dir.exists()) throw new IllegalArgumentException(Jext.getProperty("findinfiles.msg.nodir")); //given a file
    if (!dir.isDirectory()) throw new IllegalArgumentException(Jext.getProperty("findinfiles.msg.nofilesallowed")); //given a file
    Vector files = new Vector();
    addFile2Vector(dir, Utilities.getWildCardMatches(dir.toString(),filter,true), files);
    if ( searchSubdirectories ) {
      String[] list = dir.list();
      java.util.List listList = Arrays.asList(list);
      Collections.sort(listList);
      for (Iterator itr=listList.iterator(); itr.hasNext();) {
        File aFile = new File(dir, (String)itr.next());
        if (aFile.isDirectory())
          addFile2Vector(getFileNames(aFile, filter, searchSubdirectories), files);
      }
    }
    String[] result = new String[files.size()];
    files.copyInto(result);
    return result;
  }

  private static void addFile2Vector(File dir, String[] strings, Vector vect)  {
    for (int i=0; i<strings.length; i++)
      vect.addElement((new File(dir,strings[i])).toString());
  }

  private static void addFile2Vector(String[] strings, Vector vect)  {
    for (int i=0; i<strings.length; i++)
      vect.addElement(strings[i]);
  }

  private static String[] vector2StringArray(Vector vect)  {
    String[] result = new String[vect.size()];
    vect.copyInto(result);
    return result;
  }

  public static Vector search(File dir, String filter, String pattern, boolean searchSubdirectories, boolean ignoreCase, boolean useRegExp) throws Exception {
    return search(getFileNames(dir, filter, searchSubdirectories), pattern, ignoreCase, useRegExp);
  }

  public static Vector search(String[] files, String pattern, boolean ignoreCase, boolean useRegExp) throws Exception {
    SearchMatcher matcher = null;
    if ( useRegExp ) {
      if (DEBUG) System.out.println("Glob if "+Jext.getProperty(FindInFilesPlugin.USE_GLOB_PROP, "off"));
      if ("on".equals(Jext.getProperty(FindInFilesPlugin.USE_GLOB_PROP, "off")))
        pattern=Utilities.globToRE(pattern);
      if (DEBUG) System.out.println("Pattern:"+pattern);
      matcher = new org.jext.search.RESearchMatcher(pattern,"",ignoreCase,false,null);
    } else{
      matcher = new BoyerMooreSearchMatcher(pattern,"",ignoreCase,false,false,null);
    }
    Vector allHits=new Vector();
    for (int i=0; i<files.length; i++)
      allHits.addAll(searchInFile(files[i], matcher));
    return allHits;
  }

  private static Vector searchInFile(String file, SearchMatcher matcher) throws Exception {
    String tabSpace=getTabSpace();
    Vector hits=new Vector();
    LineNumberReader reader=new LineNumberReader(new FileReader(file));
    String line=null;
    while((line=reader.readLine())!=null) {
      if ( matcher.nextMatch(new javax.swing.text.Segment(line.toCharArray(),0,line.length())) != null )
        hits.addElement(new FindInFilesMatch(file, replaceTabs(line, tabSpace), reader.getLineNumber()));
    }
    if ( hits.size() > 0 )
      hits.insertElementAt(new FindInFilesMatch(file,hits.size()),0);

    return hits;
  }

  private static final String replaceTabs(String text, String tabSpace) {
    String result="";
    int thisTabIndex=0,lastTabIndex=0;
    boolean repeat = true;
    while ( repeat ) {
      thisTabIndex=text.indexOf("\t",lastTabIndex);
      if ( thisTabIndex == -1 ) {
        repeat = false;
        thisTabIndex = text.length();
      }
      result+=text.substring(lastTabIndex,thisTabIndex);
      if (repeat) result+=tabSpace;
      lastTabIndex = thisTabIndex+1;
    }
    return result;
  }

  private static final String getTabSpace() {
    int tabSize = Integer.parseInt(Jext.getProperty("editor.tabSize","2"));
    String tabSpace = "";
    for ( int i=0; i<tabSize; i++) tabSpace+=" ";
    return tabSpace;
  }

}
