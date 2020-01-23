/*
 * 01/25/2003 - 14:02:55
 *
 * Console.java - A console (emulates system terminal)
 * Copyright (C) 1999-2003 Romain Guy
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

package org.jext.console;

import java.io.*;
import java.net.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.BadLocationException;

import org.jext.*;
import org.jext.console.commands.*;
import org.python.util.InteractiveInterpreter;
import org.jext.scripting.python.Run;

/**
 * An internal console which provide different kinds of
 * prompts and which allows to execute both internal and
 * external (OS specific) commands. The console is embedded
 * in a <code>JScrollPane</code> and handles it by itself.
 * @author Romain Guy
 */

public class Console extends JScrollPane
{
  /** DOS prompt: /export/home/guy &gt; */
  public static final int DOS_PROMPT = 0;
  /** Jext prompt: Gfx@/export/home/guy &gt; */
  public static final int JEXT_PROMPT = 1;
  /** Linux prompt: guy@csdlyon$ */
  public static final int LINUX_PROMPT = 2;
  /** SunOS prompt: csdlyon% */
  public static final int SUNOS_PROMPT = 3;

  /** Default prompt types: DOS, Jext, Linux and SunOS **/
  public static final String[] DEFAULT_PROMPTS = { "$p >", "$u@$p >", "$u@$h$$ ", "$h% " };

  // current separators used in command lines
  //private static final String COMPLETION_SEPARATORS = " \t;:/\\\"\'";
  private static final String COMPLETION_SEPARATORS = " \t;:\"\'";

  // commands
  private Command currentCmd, firstCmd;
  // parent
  private JextFrame parent;
  // processes specific
  private ConsoleProcess cProcess;

  /**This parser is used as interpreter for the Jython mode*/
  private InteractiveInterpreter parser;

  /**This buffer stores the incomplete Python command lines*/
  private StringBuffer pythonBuf = new StringBuffer();

  // private fields
  private String current;
  private Document outputDocument;
  private ConsoleTextPane textArea;
  private HistoryModel historyModel = new HistoryModel(25);
  /**This is the point from where starts the text the user can edit. The text 
   * before was either output or (from the user but accepted with &lt;Enter&gt;)*/
  private int userLimit = 0;
  /**This is where the user-typed text that hasn't still be accepted ends. If it's
   * before the document length, the user cannot type.*/
  private int typingLocation = 0;
  /**If the command is taken from the history, this is its position inside it:
   * 0 for the last command, 1 for the one before and so on; if it's -1, the command
   * doesn't come from the history.*/
  private int index = -1;

  // colors
  public Color errorColor = Color.red;
  public Color promptColor = Color.blue;
  public Color outputColor = Color.black;
  public Color infoColor = new Color(0, 165, 0);

  // prompt
  private boolean displayPath;
  private String prompt, hostName, oldPath = new String();
  private String promptPattern = DEFAULT_PROMPTS[JEXT_PROMPT];
  private boolean alwaysAllowType = false;

  private Command evalCom = null;
  
  /**
   * Instanciates a new console without displaying prompt.
   * @param parent <code>Jext</code> parent
   */

  public Console(JextFrame parent)
  {
    this(parent, false);
  }

  /**
   * Creates a new console, embedding it in a <code>JScrollPane</code>.
   * By default console help is displayed.
   * @param parent <code>Jext</code> parent
   * @param display If set on true, prompt is displayed
   */

  public Console(JextFrame parent, boolean display)
  {
    super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    this.parent = parent;

    // load commands from previous sessions
    load();

    textArea = new ConsoleTextPane(this);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
    outputDocument = textArea.getDocument();
    append(Jext.getProperty("console.welcome"), infoColor, false, true);

    if (display)
      displayPrompt();

    getViewport().setView(textArea);
    FontMetrics fm = getFontMetrics(textArea.getFont());
    setPreferredSize(new Dimension(40 * fm.charWidth('m'), 6 * fm.getHeight()));
    setMinimumSize(getPreferredSize());
    setMaximumSize(getPreferredSize());
    setBorder(null);

    initCommands();
  }

  /**
   * Returns this Console's parent.
   */

  public JextFrame getParentFrame()
  {
    return parent;
  }

  /**
   * Return the <code>Document</code> in which output is performed.
   */

  public Document getOutputDocument()
  {
    return outputDocument;
  }

  /**
   * Adds a command to the linked list of commands.
   */

  public void addCommand(Command command)
  {
    if (command == null)
      return;

    currentCmd.next = command;
    currentCmd = command;
  }

  /**
   * Return true if command is built-in. If command is built-in,
   * it is also executed.
   * @param command Command to check and execute
   */

  private boolean builtInCommand(String command)
  {
    boolean ret = false;
    Command _currentCmd = firstCmd;

    while (_currentCmd != null)
    {
      if (_currentCmd.handleCommand(this, command))
      {
        ret = true;
        break;
      }
      _currentCmd = _currentCmd.next;
    }

    return ret;
  }

  // inits commands list

  private void initCommands()
  {
    firstCmd = currentCmd = new ClearCommand();
    addCommand(new JythonCommand());
    addCommand(new ChangeDirCommand());
    addCommand(new ExitCommand());
    addCommand(new FileCommand());
    addCommand(new HomeCommand());
    addCommand(new HttpCommand());
    addCommand(new HelpCommand());
    addCommand(new ListCommand());
    addCommand(new PwdCommand());
    addCommand(new RunCommand());
    addCommand(evalCom = new EvalCommand());
  }

  /**
   * Set console background color.
   * @param color <code>Color</code> to be used
   */

  public void setBgColor(Color color)
  {
    textArea.setBackground(color);
  }

  /**
   * Set console error color.
   * @param color <code>Color</code> to be used
   */

  public void setErrorColor(Color color)
  {
    errorColor = color;
  }

  /**
   * Set console prompt color.
   * @param color <code>Color</code> to be used
   */

  public void setPromptColor(Color color)
  {
    promptColor = color;
  }

  /**
   * Set console output color.
   * @param color <code>Color</code> to be used
   */

  public void setOutputColor(Color color)
  {
    outputColor = color;
    textArea.setForeground(color);
    textArea.setCaretColor(color);
  }

  /**
   * Set console info color.
   * @param color <code>Color</code> to be used
   */

  public void setInfoColor(Color color)
  {
    infoColor = color;
  }

  /**
   * Set console selection color.
   * @param color <code>Color</code> to be used
   */

  public void setSelectionColor(Color color)
  {
    textArea.setSelectionColor(color);
  }

  /**
   * Save the history.
   */

  public void save()
  {
    for (int i = 0; i < historyModel.getSize(); i++)
      Jext.setProperty("console.history." + i, historyModel.getItem(i));
  }

  /**
   * Load the last saved history.
   */

  public void load()
  {
    String s;

    for (int i = 24 ; i >= 0; i--)
    {
      s = Jext.getProperty("console.history." + i);
      if (s != null)
        historyModel.addItem(s);
    }
  }

  /**
   * Set the prompt pattern.
   * @param type The prompt pattern
   */

  public void setPromptPattern(String prompt)
  {
    if (prompt == null)
      return;

    promptPattern = prompt;
    displayPath = false;
    buildPrompt();
  }

  /**
   * Get prompt pattern.
   */

  public String getPromptPattern()
  {
    return promptPattern;
  }

  /**
   * Displays the prompt according to the current selected
   * prompt type.
   */

  public void displayPrompt()
  {
    if (prompt == null || displayPath)
      buildPrompt();

    if (Jext.getBooleanProperty("console.jythonMode"))
      //append('\n' + "[python] " + prompt, promptColor);
      append("[python] " + prompt, promptColor);
    else
      //append('\n' + prompt, promptColor);
      append(prompt, promptColor);
    typingLocation = userLimit = outputDocument.getLength();
  }

  // builds the prompt according to the prompt pattern

  private void buildPrompt()
  {
    if (displayPath && oldPath.equals(System.getProperty("user.dir")))
      return;

    displayPath = false;
    StringBuffer buf = new StringBuffer();

    if (hostName == null)
    {
      try
      {
        hostName = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException uhe) { }
    }

    for (int i = 0; i < promptPattern.length(); i++)
    {
      char c = promptPattern.charAt(i);

      switch(c)
      {
        case '$':
          if (i == promptPattern.length() - 1)
            buf.append(c);
          else
          {
            switch (promptPattern.charAt(++i))
            {
              case 'p':                    // current path
                buf.append(oldPath = System.getProperty("user.dir"));
                displayPath = true;
                break;
              case 'u':                    // user name
                buf.append(System.getProperty("user.name"));
                break;
              case 'h':                    // host name
                buf.append(hostName);
                break;
              case '$':
                buf.append('$');
                break;
            }
          }
          break;
        default:
          buf.append(c);
      }
    }

    prompt = buf.toString();
  }

  private class Appender implements Runnable {
    private String text;
    private Color color;
    private boolean italic, bold;

    Appender(String _text, Color _color, boolean _italic, boolean _bold) {
      text = _text;
      color = _color;
      italic = _italic;
      bold = _bold;
    }

    public void run() {
      SimpleAttributeSet style = new SimpleAttributeSet();
      if (color != null)
	style.addAttribute(StyleConstants.Foreground, color);
      StyleConstants.setBold(style, bold);
      StyleConstants.setItalic(style, italic);

      try
      {
	outputDocument.insertString(outputDocument.getLength(), text, style);
      } catch(BadLocationException bl) { }

      textArea.setCaretPosition(outputDocument.getLength());
    }
  }

  /**
   * This method appends text in the text area.
   * @param text The text to append
   * @param color The color of the text
   * @param italic Set to true will append italic text
   * @param bold Set to true will append bold text
   */

  private void append(String text, Color color, boolean italic, boolean bold)
  {
    Runnable appender = new Appender(text, color, italic, bold);
    if (SwingUtilities.isEventDispatchThread()) {
      appender.run();
    } else {
      SwingUtilities.invokeLater(appender);
    }
  }

  /**
   * This method appends text in the text area.
   * @param text The text to append in the text area
   * @apram color The color of the text to append
   */

  public void append(String text, Color color)
  {
    append(text, color, false, false);
  }

  /**
   * Adds a command to the history.
   * @param command Command to add in the history
   */

  public void addHistory(String command)
  {
    historyModel.addItem(command);
    index = -1;
  }

  /**
   * Remove a char from current command line.
   * Stands for BACKSPACE action.
   */

  public void removeChar()
  {
    try
    {
      //if (typingLocation != userLimit)
      //  outputDocument.remove(--typingLocation, 1);
      int pos = textArea.getCaretPosition();
      if (pos <= typingLocation && pos > userLimit)
      {
        outputDocument.remove(pos - 1, 1);
        typingLocation--;
      }
    } catch (BadLocationException ble) { }
  }

  /**
   * Delete a char from command line.
   * Stands for DELETE action.
   */

  public void deleteChar()
  {
    try
    {
      int pos = textArea.getCaretPosition();
      if (pos == outputDocument.getLength()) return;
      if (pos < typingLocation && pos >= userLimit)
      {
        outputDocument.remove(pos, 1);
        typingLocation--;
      }
    } catch (BadLocationException ble) { }
  }

  /**
   * Adds a <code>String</code> to the current command line.
   * @param add <code>String</code> to be added
   */

  public void add(String add)
  {
    try
    {
      int pos = textArea.getCaretPosition();
      if (pos <= typingLocation && pos >= userLimit)
        outputDocument.insertString(pos, add, null);
      typingLocation += add.length();
    } catch (BadLocationException ble) { }
  }

  /**
   * Returns the position in characters at which
   * user is allowed to type his commands.
   * @return Beginning of user typing space
   */

  public int getUserLimit()
  {
    return userLimit;
  }

  /**
   * Returns the position of the end of the console prompt.
   */

  public int getTypingLocation()
  {
    return typingLocation;
  }

  //TODO: this method, in case of ambiguity, should print completions as bash.
  //And should also expand the ~(not by calling constructPath, which is normally used
  //to do this: this expansion should go, probably, inside parseCommand().
  /**
   * Completes current filename if possible.
   */

  public void doCompletion()
  {
    int index = 0;
    int caret = textArea.getCaretPosition() - userLimit;

    String wholeText = getText();
    String text;
    String finalCompletion;

    if (Jext.getBooleanProperty("console.jythonMode") && !wholeText.startsWith("!"))
      return;

    try
    {
      text = outputDocument.getText(userLimit, caret);
    } catch (BadLocationException ble) { return; }

    for (int i = text.length() - 1; i >= 0; i--)
    {
      if (COMPLETION_SEPARATORS.indexOf(text.charAt(i)) != -1)
      {
        if (i == 0)
          return;
        index = i + 1;
        break;
      }
    }

    String current = text.substring(index);
    String path = "";
    int separatorIdx = current.lastIndexOf(File.separatorChar);
    if (separatorIdx != -1) {
      path = current.substring(0, separatorIdx + 1); //the slash is inside path.
      current = current.substring(separatorIdx + 1);
    }

    String[] files = Utilities.getWildCardMatches(path, current + "*", true);

    if (files == null || files.length == 0) {
      if (current.equals(".."))
        finalCompletion = current + File.separator;
      else
        return;
    } else if (files.length != 1) {
      int length = 0; //maximum length of a completion
      int mIndex = 0; //index of the longest completion(if more longest ones,
      //we take the index of the last one).

      for (int i = 0; i < files.length; i++)
      {
	int _length = files[i].length();
	length = length < _length ? _length : length;
	if (length == _length)
	  mIndex = i;
      }

      char c;
      int diffIndex = length; //source[0:diffIndex] is the completion common to
      //everything. Note that if there are, i.e., "file" and "fileLonger", the 
      //completion used to be fileLonger. Now we want to get, instead, file; but
      //modify below where MARKed to get old behaviour.

      String compare;
      String source = files[mIndex];

out:  for (int i = 0; i < length; i++)
      {
	c = source.charAt(i);

	for (int j = 0; j < files.length; j++)
	{
	  if (j == mIndex)
	    continue;

	  compare = files[j];

	  //MARK
	  /*if (i >= compare.length())
	    continue;*/

	  //if (compare.charAt(i) != c)
	  if (i >= compare.length() || compare.charAt(i) != c) {
	    diffIndex = i;
	    break out;
	  }
	}
      }
      finalCompletion = source.substring(0, diffIndex);
    } else {
      finalCompletion = files[0];

      File f = new File(path + finalCompletion);
      if (!f.isAbsolute())
	f = new File(Utilities.getUserDirectory(), path + finalCompletion);

      if (f.isDirectory())
	finalCompletion += File.separator;
    }

    String textToInsert = text.substring(0, index) + path + finalCompletion;
    setText(textToInsert + wholeText.substring(caret));
    textArea.setCaretPosition(userLimit + textToInsert.length());
  }

  /**
   * Search backward in the history for a matching command,
   * according to the command typed in the user typing space.
   */

  public void doBackwardSearch()
  {
    String text = getText();
    if (text == null)
    {
      historyPrevious();
      return;
    }

    for(int i = index + 1; i < historyModel.getSize(); i++)
    {
      String item = historyModel.getItem(i);
      if (item.startsWith(text))
      {
        setText(item);
        index = i;
        return;
      }
    }
  }

  /**
   * Get previous item in the history list.
   */

  public void historyPrevious()
  {
    if (index == historyModel.getSize() - 1)
      getToolkit().beep();
    else if (index == -1)
    {
      current = getText();
      setText(historyModel.getItem(0));
      index = 0;
    } else {
      int newIndex = index + 1;
      setText(historyModel.getItem(newIndex));
      index = newIndex;
    }
  }

  /**
   * Get next item in the history list.
   */

  public void historyNext()
  {
    if (index == -1)
      getToolkit().beep();
    else if (index == 0)
      setText(current);
    else
    {
      int newIndex = index - 1;
      setText(historyModel.getItem(newIndex));
      index = newIndex;
    }
  }

  /**
   * Set user's command line content.
   * @param text Text to be put on command line.
   */

  public void setText(String text)
  {
    try
    {
      outputDocument.remove(userLimit, typingLocation - userLimit);
      outputDocument.insertString(userLimit, text, null);
      typingLocation = outputDocument.getLength();
      index = -1;
    } catch (BadLocationException ble) { }
  }

  /**
   * Returns current command line.
   */

  public String getText()
  {
    try
    {
      return outputDocument.getText(userLimit, typingLocation - userLimit);
    } catch (BadLocationException ble) { }
    return null;
  }

  /**
   * Displays console help.
   */

  public void help()
  {
    Command _current = firstCmd;
    StringBuffer buf = new StringBuffer();

    while (_current != null)
    {
      buf.append("   - ").append(_current.getCommandName());
      buf.append(Utilities.createWhiteSpace(30 - _current.getCommandName().length())).append('(');
      buf.append(_current.getCommandSummary()).append(')').append('\n');
      _current = _current.next;
    }
    buf.append('\n');

    help(Jext.getProperty("console.help", new String[] { buf.toString() }));
  }

  /**
   * Display a message using information color.
   * @since Jext3.2pre4
   * @param display <code>String</code> to be displayed
   */

  public void info(String display)
  {
    //append('\n' + display, infoColor, false, false);
    append(display + '\n', infoColor, false, false);
  }

  /**
   * Display a message using help color.
   * @param display <code>String</code> to be displayed
   */

  public void help(String display)
  {
    //append('\n' + display, infoColor, true, true);
    append(display + '\n', infoColor, true, true);
  }

  /**
   * Display a message using error color.
   * @param display <code>String</code> to be displayed
   */

  public void error(String display)
  {
    //append('\n' + display, errorColor, false, false);
    append(display + '\n', errorColor, false, false);
  }

  /**
   * Display a message using output color.
   * @param display <code>String</code> to be displayed
   */

  public void output(String display)
  {
    //append('\n' + display, outputColor, false, false);
    append(display + '\n', outputColor, false, false);
  }

  /**
   * Stops current task.
   */

  public void stop() {
    if (cProcess != null) {
      cProcess.stop();
      cProcess = null;
    }
  }

  /**
   * Parse a command. Replace internal variables by their
   * values.
   * @param command Command to be parsed
   */

  public String parseCommand(String command)
  {
    String file;
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < command.length(); i++)
    {
      char c = command.charAt(i);
      switch(c)
      {
        case '$':
          if (i == command.length() - 1)
            buf.append(c);
          else
          {
            switch (command.charAt(++i))
            {
              case 'f':                    // current opened file (absolute path)
                file = parent.getTextArea().getCurrentFile();
                if (file != null)
                  buf.append(file);
                break;
              case 'd':                    // user directory
                buf.append(Utilities.getUserDirectory());
                break;
              case 'p':                    // current opened file name
                buf.append(parent.getTextArea().getName());
                break;
              case 'e':                    // current opened file name without extension
                file = parent.getTextArea().getName();
                int index = file.lastIndexOf('.');
                if (index != -1 && index + 1 < file.length())
                  buf.append(file.substring(0, index));
                else
                  buf.append(file);
                break;
              case 'n':                    // current opened file directory
                file = parent.getTextArea().getCurrentFile();
                if (file != null)
                  buf.append(file.substring(0, file.lastIndexOf(File.separator)));
                break;
              case 'h':                    // home dir
                buf.append(Utilities.getHomeDirectory());
                break;
              case 'j':                    // jext dir
                buf.append(Jext.getHomeDirectory());
                break;
              case 's':                    // selected text
                buf.append(parent.getTextArea().getSelectedText());
                break;
              case '$':
                buf.append('$');
                break;
            }
          }
          break;
        default:
          buf.append(c);
      }
    }
    return buf.toString();
  }

  /**
   * Execute command. First parse it then check if command
   * is built-in. At last, a process is created and threads
   * which handle output streams are started.
   * @param command Command to be execute
   */

  public void execute(String command)
  {
    if (command == null)
      return;
    stop();

    info("");

    //userLimit = typingLocation;//FIXME: this is part of saved positions refactoring?
    //However, it's needed to avoid that pressing Enter while a command executes 
    //makes Jext read the same command with getText() and execute it again.
    //But I've put it out, since with it, when I press Up, the command typed
    //remains there, since that line make Jext think it's part of the prompt.

    // check to see if in "jython" mode...
    boolean isJython = Jext.getBooleanProperty("console.jythonMode");

    // if in jython mode, look for '!' as first charater...means
    // treat it like a "normal console" command
    if (isJython)
    {
      if (!command.startsWith("!"))
      {
        if (command.startsWith("?"))
        {  // kludge shorthand for 'print'
          String ts = command.substring(1);
          command = "print " + ts;
        } else if (command.startsWith("exit")) {
          Jext.setProperty("console.jythonMode", "off");
          displayPrompt();
          return;
        }

        //evalCom.handleCommand(this, "eval:" + command);

	if (parser == null)
	{
	  parser = new InteractiveInterpreter();
	  Run.startupPythonInterpreter(parser);
	}
        Run.setupPythonInterpreter(parser, parent, this);

        if (pythonBuf.length() > 0)
          pythonBuf.append("\n");
        pythonBuf.append(command);
        if (!parser.runsource(pythonBuf.toString())) //FIXME: use return value to display
	  //secondary prompt.
          pythonBuf.setLength(0);

        displayPrompt();
        return;

      } else { //the command starts with !, so normal processing
        command = command.substring(1);
      }
    }

    command = command.trim();
    command = parseCommand(command);

    if (command == null || command.length() == 0 || builtInCommand(command))
    {
      displayPrompt();
      return;
    }

    cProcess = new ConsoleProcess(command);
    cProcess.execute();
 
    /*append("\n> " + command, infoColor);
    try
    {
      if (Utilities.JDK_VERSION.charAt(2) < '3')
        process = Runtime.getRuntime().exec(command);
      else
        process = Runtime.getRuntime().exec(command, null, new File(System.getProperty("user.dir")));
      process.getOutputStream().close();
    } catch (IOException ioe) {
      error(Jext.getProperty("console.error"));
      displayPrompt();
      return;
    }

    stdout = new StdoutThread();
    stderr = new StderrThread();

    if (process == null)
      displayPrompt();*/
  }

  class ConsoleProcess {
    private boolean executed;

    private Process process;
    private String command;

    /**Name of the command, here for being put inside messages*/
    private String processName;

    private int exitCode;
    private Object exitCodeLock = new Object();

    private StdoutThread stdout;
    private StderrThread stderr;
    private StdinThread stdin;

    private String stdinRedir, stdoutRedir;

    /**Synchronize on this obj(set to null) for event related to threading inside this object.*/
    private Object lockObj = new Object();

    /*
     * This should be used to notify stdin when the process actually started.
     * But this design is probably broken. Better for now just the sleep(500),
     * even if it's broken at all and it just has happened to always work.
     */
    /*private Object stdinCloseLockObj = new Object();
    private boolean processStarted = false;*/

    ConsoleProcess(String command) {
      this.command = handleRedirs(command);
      executed = false;
    }

    int getExitCode() {
      synchronized(exitCodeLock) {
	return exitCode;
      }
    }

    public void execute() {
      if (executed)
	return;
      executed = true;

      /*if (stdinRedir != null)
	alwaysAllowType = false; //we don't let 
      else
	alwaysAllowType = false; //since we forbid typing to stdin, then we set this to false anyway.*/

      int index = command.indexOf(' ');
      if (index != -1)
	processName = command.substring(0, index);
      else
	processName = command;

      info("> " + command);
      try {
	if (Utilities.JDK_VERSION.charAt(2) < '3')
	  process = Runtime.getRuntime().exec(command);
	else
	  process = Runtime.getRuntime().exec(command, null, new File(System.getProperty("user.dir")));
      } catch (IOException ioe) {
	error(Jext.getProperty("console.error"));
	displayPrompt();
	return;
      }

      stdout = new StdoutThread(stdoutRedir);
      stderr = new StderrThread();
      if (alwaysAllowType || stdinRedir != null)
	stdin = new StdinThread(stdinRedir);
      else
	stdin = new StdinThread(stdinRedir, true); //this thread will just
      //close the stream, in this case. It's needed for some problems on Unix(see below)

      synchronized(lockObj) {
        stdout.start();
        stderr.start();
	stdin.start();
      }
    }

    /**
     * This handles the parsing of I/O redirections, and sets stdinRedir
     * and stdoutRedir.
     */
    private String handleRedirs(String toParse) {
      int i, end;

      i = toParse.lastIndexOf('>');
      if ( i != -1 ) {
        while (toParse.charAt(++i) == ' '); //skips spaces after >

        end = toParse.indexOf('<', i); //the name of the file is from the > to the end or to the <
        if (end == -1)
          end = toParse.length();
        while (toParse.charAt(--end) == ' '); //skips spaces before <
	end++;

        stdoutRedir = toParse.substring(i, end);
      } else {
	stdoutRedir = null;
      }

      i = toParse.lastIndexOf('<');
      if ( i != -1 ) {
        while (toParse.charAt(++i) == ' '); //skips spaces after <

        end = toParse.indexOf('>', i); //the name of the file is from the < to the end or to the >
        if (end == -1)
          end = toParse.length();
        while (toParse.charAt(--end) == ' '); //skips spaces before <
	end++;

        stdinRedir = toParse.substring(i, end);
      } else {
	stdinRedir = null;
      }

      int lt = toParse.indexOf('<');
      if (lt == -1)
        lt = toParse.length();
      int gt = toParse.indexOf('>');
      if (gt == -1)
        gt = toParse.length();
      end = Math.min(lt,gt);
      return toParse.substring(0, end);
    }

    /**
     * Stops current task.
     */
    public void stop()
    {
      synchronized(lockObj) {
	if (stdout != null)
	{
	  stdout.interrupt();
	  stdout = null;
	}

	if (stderr != null)
	{
	  stderr.interrupt();
	  stderr = null;
	}
	
	if (stdin != null)
	{
	  stdin.interrupt();
	  stdin = null;
	}

	if (process != null)
	{
	  process.destroy();
	  Object[] args = { processName };
	  error(Jext.getProperty("console.killed", args));
	  process = null;
	}
      }
    }
    
    void sendToProcess(String toPrint) {
      if (stdin != null)
	stdin.print(toPrint);
    }

    class StdinThread extends Thread
    {
      StdinThread(String inFileName) {
	this(inFileName, false);
      }
      StdinThread(String inFileName, boolean justClose)
      {
	super("Console stdin");
	this.inFileName = inFileName;
	this.justClose = justClose;
      }

      private String toPrint, inFileName;
      private boolean justClose;

      /**
       * Must be called by the AWT-EventQueue thread, to make this one print some text
       * to the process's stdin.
       */
      synchronized void print(String toPrint) {
	this.toPrint = toPrint;
	this.notify();
      }
      
      public void run()
      {
	if (process == null)
	  return;

	PrintWriter out = new PrintWriter(process.getOutputStream());

	if (!justClose) {
	  System.out.println("StdinThread started running");
	  if (inFileName == null) {
	    try {
	      while(!isInterrupted()) {//this is needed to catch the interrupt when we are not
		//wait()'ing
		synchronized (this) {
		  this.wait();
		}
		if (toPrint != null) {
		  out.print(toPrint);
		  out.flush();
		  toPrint = null;
		}
	      }
	    } catch (NullPointerException npe) {
	      npe.printStackTrace();
	    } catch (InterruptedException ie) {
	      ie.printStackTrace();//FIXME: this happens often, so turn it off after debug.
	    }
	  } else {
	    File f = new File(inFileName);
	    if (f.exists()) {
	      FileReader in = null;
	      try {
		in = new FileReader(f);
		char[] buf = new char[256];
		int nRead;
		while ( ( nRead = in.read(buf)) != -1)
		  out.write(buf, 0, nRead);
	      } catch(IOException ioe) {
		ioe.printStackTrace();
	      } finally {
		try {
		  in.close();
		} catch (IOException ioe) {ioe.printStackTrace();}
	      }
	    } else {
	      error("Jext: file " + inFileName + "not found");
	    }
	  } //end if (inFileName != null)
	} //end if (!justClose)

	/*synchronized(stdinCloseLockObj) {
	  if (! processStarted)
	    stdinCloseLockObj.wait();
	}*/

	/* If the close happens when the native process has not yet started truly running,
	 * it's very easy for it to deadlock(at least on Linux). So I add these lines.*/
	try {
	  sleep(500);
	} catch (InterruptedException ie) {
	}
	out.close();
      }
    }

    class StdoutThread extends Thread
    {
      StdoutThread(String outFileName)
      {
	super("Console stdout");
	this.outFileName = outFileName;
      }
 
      private String outFileName;

      public void run()
      {
	BufferedReader in = null;
	try {
	  in = new BufferedReader(new InputStreamReader(process.getInputStream()));
	  System.out.println("StdoutThread started running");

	  if (outFileName == null) {
	    try {
	      String line;
	      /*synchronized(stdinCloseLockObj) {
		processStarted = true;
		stdinCloseLockObj.notify();
	      }*/
	      while((line = in.readLine()) != null)
		output(line);
 
	      /*int nRead;
	      char[] buf = new char[100];
	      boolean started = false;
	      while((nRead = in.read(buf)) != -1) {
		if (!started) {
		  started = true;
		  append("\n", outputColor);
		}
		append(new String(buf, 0, nRead), outputColor);
		//userLimit = outputDocument.getLength();//we display text that the user
		//can't delete and that getText mustn't read
	      }*/
	    } catch (IOException io) {}
	  } else {
	    BufferedWriter out = null;
	    try {
	      out = new BufferedWriter(new FileWriter(outFileName));
	      char[] buf = new char[256];
	      int nRead;
	      while ( ( nRead = in.read(buf)) != -1)
		out.write(buf, 0, nRead);
	    } catch(IOException ioe) {
	      ioe.printStackTrace();
	    } finally {
	      try {
		out.close();
	      } catch (IOException ioe) {ioe.printStackTrace();}
	    }
	  }

	  if (isInterrupted())
	    return; //this should remove some NPE throwns. If we have been interrupted
	  //(by stop()), the process var has been set to null
	  synchronized(lockObj) {
	    synchronized(exitCodeLock) { //this is safe because the other exitCode lock
	      //doesn't contain a lockObj lock
	      exitCode = process.waitFor();
	    }
	    Object[] args = { processName, new Integer(exitCode) };
	    info(Jext.getProperty("console.exited", args));//this instead must be audited, 
	    //since it calls SwingUtilities.invokeLater.
	  }

	  sleep(500);
	  synchronized(lockObj) {
	    process.destroy();
	    if (stdin != null)
	      stdin.interrupt();
	    process = null;
	    //cProcess = null;
	  }

	  SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
	      displayPrompt();
	    }
	  });
	} catch (NullPointerException npe) { npe.printStackTrace();
	} catch (InterruptedException ie) { ie.printStackTrace();
	} finally {
	  try {
	    in.close();
	  } catch (IOException ioe) {ioe.printStackTrace();}
	}
      }
    }

    class StderrThread extends Thread
    {
      StderrThread()
      {
	super("Console stderr");
      }

      public void run()
      {
	try
	{
	  if (process == null)
	    return;
	  BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));

	  String line;
	  while((line = in.readLine()) != null)
	    error(line);

	  //two versions of better code. I must first change
	  //the convention of messages starting with \n, instead of ending with
	  //\n. The second is older and slower, the first not tested.
	  /*char buf[] = new char[64];
	  int nRead = 0;
	  while(n != -1) {
	    int nRead = in.available();
	    if (nRead == 0)
	      nRead = in.read(buf); //here we block, not in any other case.
	    else {
	      nRead = Math.min(nRead, 64);
	      nRead = in.read(buf, 0, nRead);//here we're guaranted to read the maximum
	      //we can without blocking
	    }
	    append(new String(buf, 0, nRead), errorColor);
	    //userLimit = outputDocument.getLength();//we display text that the user
	    //can't delete and that getText mustn't read
	  }*/
	  /*int c;
	  while((c = in.read()) != -1) {
	    append("" + ((char)c), errorColor);
	  }*/
	  in.close();
	} catch(IOException io) {
	} catch (NullPointerException npe) { }
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////
  // NEEDED BY JavaScriptParser PLUGIN
  //////////////////////////////////////////////////////////////////////////////////////////////

  private Writer writerSTDOUT = new Writer()
  {
    public void close() { }

    public void flush()
    {
      repaint();
    }

    public void write( char cbuf[], int off, int len )
    {
      Console.this.append(new String(cbuf, off, len), outputColor);
    }
  };

  private Writer writeSTDERR = new Writer()
  {
    public void close() { }

    public void flush()
    {
      repaint();
    }

    public void write( char cbuf[], int off, int len )
    {
      Console.this.append(new String(cbuf, off, len), errorColor);
    }
  };

  /**
   * Returns a writer in which external classes can send
   * <code>String</code> to make them being displayed in the
   * console as standard output.
   */

  public Writer getStdOut()
  {
    return writerSTDOUT;
  }

  /**
   * Returns a writer in which external classes can send
   * <code>String</code> to make them being displayed in the
   * console as error output.
   */

  public Writer getStdErr()
  {
    return writeSTDERR;
  }
  

}

// End of Console.java