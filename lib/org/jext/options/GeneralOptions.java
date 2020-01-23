/*
 * 01/25/2003 - 14:04:26
 *
 * GeneralOptions.java - The interface options pane
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

package org.jext.options;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.File;

import org.jext.*;
import org.jext.gui.*;
import org.jext.console.*;

public class GeneralOptions extends AbstractOptionPane implements ActionListener
{
  private JComboBox prompt;
  private JTextField saveDelay, maxRecent, promptPattern, templatesDir;
  private JextCheckBox check, tips, console, fullFileName, autoSave, labeledSeparator,
                       saveSession, scriptingDebug, leftPanel, topPanel, newWindow,
                       scrollableTabbedPanes, jythonMode;
                       
  public GeneralOptions()
  {
    super("general");

    String prompts[] = { "DOS", "Jext", "Linux", "Solaris" };
    prompt = new JComboBox(prompts);
    prompt.setRenderer(new ModifiedCellRenderer());
    addComponent(Jext.getProperty("options.prompt.label"), prompt);

    addComponent(Jext.getProperty("options.pattern.label"), promptPattern = new JTextField(4));
    promptPattern.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    addComponent(Jext.getProperty("options.maxrecent.label"), maxRecent = new JTextField(4));
    maxRecent.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    addComponent(Jext.getProperty("options.delay.label"), saveDelay = new JTextField(4));
    saveDelay.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    addComponent(Jext.getProperty("options.templates.label"), templatesDir = new JTextField(10));
    templatesDir.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    addComponent(newWindow = new JextCheckBox(Jext.getProperty("options.newwindow.label")));
    addComponent(jythonMode = new JextCheckBox(Jext.getProperty("options.jythonmode.label")));
    addComponent(scriptingDebug = new JextCheckBox(Jext.getProperty("options.scriptingdebug.label")));
    addComponent(autoSave = new JextCheckBox(Jext.getProperty("options.autosave.label")));
    addComponent(saveSession = new JextCheckBox(Jext.getProperty("options.savesession.label")));
    addComponent(check = new JextCheckBox(Jext.getProperty("options.check.label")));
    addComponent(console = new JextCheckBox(Jext.getProperty("options.console.label")));
    addComponent(fullFileName = new JextCheckBox(Jext.getProperty("options.full.filename.label")));
    addComponent(tips = new JextCheckBox(Jext.getProperty("options.tips.label")));
    addComponent(scrollableTabbedPanes = new JextCheckBox(Jext.getProperty("options.scrollabletabbedpanes.label")));
    scrollableTabbedPanes.setEnabled(Utilities.JDK_VERSION.charAt(2) >= '4');
    addComponent(leftPanel = new JextCheckBox(Jext.getProperty("options.leftPanel.label")));
    addComponent(topPanel = new JextCheckBox(Jext.getProperty("options.topPanel.label")));
    load();
    prompt.addActionListener(this);//this must be done here, so the values loading doesn't trigger an actionPerformed
  }
  
  public void load()
  {
    String promptTxt = Jext.getProperty("console.prompt");
    promptPattern.setText(promptTxt);
    prompt.setSelectedIndex(-1);  //no item selected
    for (int i = 0; i < Console.DEFAULT_PROMPTS.length; i++) {
      if (promptTxt.equals(Console.DEFAULT_PROMPTS[i])) {
        prompt.setSelectedIndex(i);
        break;
      }
    }
    maxRecent.setText(Jext.getProperty("max.recent"));

    String svDelay = Jext.getProperty("editor.autoSaveDelay");
    if (svDelay == null)
      svDelay = "60";
    saveDelay.setText(svDelay);

    templatesDir.setText(Jext.getProperty("templates.directory",
                                          Jext.JEXT_HOME + File.separator + "templates"));
    newWindow.setSelected(Jext.getBooleanProperty("jextLoader.newWindow"));
    jythonMode.setSelected(Jext.getBooleanProperty("console.jythonMode"));
    scriptingDebug.setSelected(Jext.getBooleanProperty("dawn.scripting.debug"));
    autoSave.setSelected(Jext.getBooleanProperty("editor.autoSave"));
    saveSession.setSelected(Jext.getBooleanProperty("editor.saveSession"));
    check.setSelected(Jext.getBooleanProperty("check"));
    console.setSelected(Jext.getBooleanProperty("console.save", "on"));
    fullFileName.setSelected(Jext.getBooleanProperty("full.filename", "off"));
    tips.setSelected(Jext.getBooleanProperty("tips"));
    scrollableTabbedPanes.setSelected(Jext.getBooleanProperty("scrollableTabbedPanes"));
    leftPanel.setSelected(Jext.getBooleanProperty("leftPanel.show"));
    topPanel.setSelected(Jext.getBooleanProperty("topPanel.show"));
  }

  public Component getComponent()
  {
    JScrollPane scroller = new JScrollPane(this);
    Dimension _dim = this.getPreferredSize();
    scroller.setPreferredSize(new Dimension((int) _dim.width, 410));
    //scroller.setBorder(javax.swing.border.LineBorder.createBlackLineBorder());
    return scroller;
  }

  public void actionPerformed(ActionEvent evt)
  {
    if (evt.getSource() == prompt)
    {
      int idx = prompt.getSelectedIndex();
      if (idx != -1)
        promptPattern.setText(Console.DEFAULT_PROMPTS[idx]);
      //else nothing is selected and nothing must be done
    }
  }

  public void save()
  {
    Jext.setProperty("max.recent", maxRecent.getText());
    Jext.setProperty("templates.directory", templatesDir.getText());
    Jext.setProperty("check", check.isSelected() ? "on" : "off");
    Jext.setProperty("tips", tips.isSelected() ? "on" : "off");
    String _prompt = promptPattern.getText();
    Jext.setProperty("console.prompt", _prompt.length() == 0 ? "> " : _prompt);
    Jext.setProperty("console.save", console.isSelected() ? "on" : "off");
    Jext.setProperty("console.jythonMode", jythonMode.isSelected() ? "on" : "off");
    Jext.setProperty("full.filename", fullFileName.isSelected() ? "on" : "off");
    Jext.setProperty("editor.autoSave", autoSave.isSelected() ? "on" : "off");
    Jext.setProperty("editor.autoSaveDelay", saveDelay.getText());
    Jext.setProperty("editor.saveSession", saveSession.isSelected() ? "on" : "off");
    Jext.setProperty("dawn.scripting.debug", scriptingDebug.isSelected() ? "on" : "off");
    Jext.setProperty("leftPanel.show", leftPanel.isSelected() ? "on" : "off");
    Jext.setProperty("topPanel.show", topPanel.isSelected() ? "on" : "off");
    Jext.setProperty("jextLoader.newWindow", newWindow.isSelected() ? "on" : "off");
    Jext.setProperty("scrollableTabbedPanes", scrollableTabbedPanes.isSelected() ? "on" : "off");
  }
  

}

// End of GeneralOptions.java