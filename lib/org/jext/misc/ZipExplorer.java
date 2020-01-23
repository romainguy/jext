/*
 * 23:19:41 02/08/00
 *
 * ZipExplorer.java - Allows to open files from zips
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

package org.jext.misc;

import java.io.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.zip.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;

import org.jext.*;
import org.jext.gui.*;



public class ZipExplorer extends JDialog implements ActionListener
{
  // private members
  private JextFrame parent;
  private String zipName;
  private JTable zipTable;
  private ZipFile zipFile;
  private JextHighlightButton open, cancel;
  private JextTextArea textArea;
  private ZipTableModel zipModel;
  private Enumeration zipEntries;
  
  public ZipExplorer(JextFrame parent, JextTextArea textArea, String zipName)
  {
    super(parent, Jext.getProperty("zip.explorer.title"), true);
    this.parent = parent;
    this.textArea = textArea;
    readZip(zipName); 
    this.zipName = zipName;
    getContentPane().setLayout(new BorderLayout());

    JPanel btnPane = new JPanel();
    btnPane.add((open = new JextHighlightButton(Jext.getProperty("general.open.button"))));
    open.setToolTipText(Jext.getProperty("general.open.tip"));
    open.setMnemonic(Jext.getProperty("general.open.mnemonic").charAt(0));
    open.addActionListener(this);
    getRootPane().setDefaultButton(open);

    btnPane.add((cancel = new JextHighlightButton(Jext.getProperty("general.cancel.button"))));
    cancel.setMnemonic(Jext.getProperty("general.cancel.mnemonic").charAt(0));
    cancel.addActionListener(this);

    getContentPane().add(BorderLayout.CENTER, createZipTableScroller());
    getContentPane().add(BorderLayout.SOUTH, btnPane);

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addKeyListener(new AbstractDisposer(this));

    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent evt)
      {
        cancel();
      }
    });
    
    pack();
    Utilities.centerComponentChild(parent, this);
    setVisible(true);
  }

  private JScrollPane createZipTableScroller()
  {
    zipTable = new JTable(new ZipTableModel()); 
    zipTable.getTableHeader().setReorderingAllowed(false);
    zipTable.getColumnModel().getColumn(1).setCellRenderer(new DisabledCellRenderer());

    JScrollPane scroller = new JScrollPane(zipTable);
    scroller.getViewport().setPreferredSize(new Dimension(300, 200));
    return scroller;
  }

  private void readZip(String zipName)
  {
    if (zipName == null)
      return;

    try
    {
      File zipped = new File(zipName);
      if (!zipped.exists())
      {
        Utilities.showError(Jext.getProperty("textarea.file.notexists"));
        return;
      }

      zipFile = new ZipFile(zipped);
      zipEntries = zipFile.entries();
    } catch (ZipException ze) {
    } catch (IOException ioe) { }
  }

  private boolean readZipContent(String fileChosen)
  {
    if (zipFile == null || fileChosen == null)
      return false;

    try
    {
      ZipEntry entry = zipFile.getEntry(fileChosen);
      if (entry == null)
        return false;
      
      textArea.open(fileChosen, new InputStreamReader(zipFile.getInputStream(entry)),
                    (int) entry.getSize());
      parent.resetStatus(textArea);

    } catch (IOException ioe) {
      String[] args = { fileChosen };
      Utilities.showError(Jext.getProperty("zip.file.corrupted", args));
      return false;
    }
    return true;
  }

  public void actionPerformed(ActionEvent evt)
  {
    Object o = evt.getSource();
   
    if (o == open)
    {
      
      int zipIndex[] = zipTable.getSelectedRows();
      for(int i = 0; i < zipTable.getSelectedRowCount(); i++)
      {
        String file = (String) zipTable.getValueAt(zipIndex[i], 0);
        String path = (String) zipTable.getValueAt(zipIndex[i], 1);
        
        
        if(file.endsWith(".jar") || file.endsWith(".zip"))
        {
         Utilities.showError(Jext.getProperty("zip.file.corrupted"));
         return;  
        }
        
        // use the passed textArea, or else there would be a ghost textArea
        if(i != 0)
           textArea = parent.createFile(); 
           
        if (!path.equals("/"))
          file = path + "/" + file;
   
        if(!readZipContent(file))
        {
         cancel();
         return;
        }
        
      }

      parent.saveRecent(zipName);
      cancel(); 
      
    } else if (o == cancel)
      cancel();
  }

  private void cancel()
  {
    try
    {
      zipFile.close();
    } catch (IOException ioe) { }
    dispose();
  }

  class ZipTableModel extends AbstractTableModel
  {
     
    private ArrayList zipContents; 

    ZipTableModel()
    {
      zipContents = new ArrayList();
      for ( ; zipEntries.hasMoreElements(); )
      {
        ZipEntry name = (ZipEntry) zipEntries.nextElement();
        if (name == null)
          continue;
        if (!name.isDirectory())
          addZipEntry(name); 
      }
    }

    public int getColumnCount()
    {
      return 2;
    }

    public int getRowCount()
    {
      return zipContents.size();
    }

    public Object getValueAt(int row, int col)
    {
      String fileName, path;
      ZipEntry file = (ZipEntry) zipContents.get(row);
      String name = file.getName();
      int index = name.lastIndexOf('/');
      if (index == -1)
      {
        fileName = name;
        path = "/";
      } else {
        fileName = name.substring(index + 1);
        path = name.substring(0, index);
      }
      switch(col)
      {
        case 0:
          return fileName;
        case 1:
          return path;
        default:
          return null;
       }
     }

    public boolean isCellEditable(int row, int col)
    {
      return false;
    }

    public String getColumnName(int index)
    {
      switch(index)
      {
        case 0:
          return Jext.getProperty("zip.explorer.filenames");
        case 1:
          return Jext.getProperty("zip.explorer.directories");
        default:
          return null;
      }
    }

    private void addZipEntry(ZipEntry file)
    {
      for (int i = 0; i < zipContents.size(); i++)
      {
        ZipEntry z = (ZipEntry) zipContents.get(i);
        
        if (z.getName().compareTo(file.getName()) >= 0)
        {
          zipContents.add(i, file);
          return;
        }
        
      }
      zipContents.add(file);
    }
  }
  

}

// End of ZipExplorer.java