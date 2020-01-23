/*
 * 11:58:05 07/08/00
 *
 * DawnParser.java - Dawn is a RPN based scripting language
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * www.jext.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License,  or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not,  write to the Free Software
 * Foundation,  Inc.,  59 Temple Place - Suite 330,  Boston,  MA  02111-1307,  USA.
 */

package org.jext.dawn;

import java.io.*;

import java.util.Stack;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * <code>DawnParser</code> is the Dawn scripting language interpreter.
 * Dawn is a language based on RPN. Dawn is also a very modulary language.
 * Basic usage of Dawn is:<br>
 * <pre>
 * DawnParser.init();
 * // code is a String containing the script
 * DawnParser parser = new DawnParser(new StringReader(code));
 * try
 * {
 *   parser.exec();
 * } catch (DawnRuntimeException dre) {
 *   System.err.println(dre.getMessage());
 * }
 * </pre><p>
 * Note the call to <code>init()</code>. You may not want to call this method,
 * but if you don't, then Dawn will provide NO FUNCTION AT ALL. Even basic ones,
 * like + - * drop sto rcl, won't work !! This is due to the fact Dawn can be
 * entirely customized.<br>
 * In fact, <code>init()</code> simply install basic packages (loop, test,
 * util, stack, math, err, io, string, naming). But you can load only one, or
 * many, of them and also install your own packages to replace default ones.<br>
 * You may also load extra packages with the: <code>installPackage()</code> method.<p>
 * Read the documentation for further informations.
 * @author Romain Guy
 * @version 1.1.1
 */

public class DawnParser
{
  /** Gives Dawn interpreter version numbering */
  public static final String DAWN_VERSION = "Dawn v1.1.1 final [$12:12:55 07/08/00]";

  /** Identifier for a stack element containing a numeric value */
  public static final int DAWN_NUMERIC_TYPE = 0;
  /** Identifier for a stack element containing a string */
  public static final int DAWN_STRING_TYPE = 1;
  /** Identifier for a stack element defining a literal (variable name) */
  public static final int DAWN_LITERAL_TYPE = 2;
  /** Identifier for a stack element defining an array */
  public static final int DAWN_ARRAY_TYPE = 3;

  // global functions loaded from packages
  private static Hashtable functions = new Hashtable(200);
  // global variables
  private static Hashtable variables = new Hashtable();
  // installed packages
  private static Vector installedPackages = new Vector();
  private static Vector installedRuntimePackages = new Vector();
  // init flag
  private static boolean isInited = false;

  // it true, the parser stops
  private boolean stopped = false;
  // properties set
  private Hashtable properties = new Hashtable();
  // stream tokenizer: this is Dawn parser engine
  private StreamTokenizer st;
  // the stack where datas are put
  private Stack stack;
  // functions created on runtime
  private Hashtable runtimeFunctions;
  // variables created on runtime
  private Hashtable runtimeVariables;
  // line number in the script
  public int lineno = 1;

  // standard streams
  public PrintStream out = System.out;
  public PrintStream err = System.err;
  public InputStream in  = System.in ;

  /**
   * Initializes Dawn default packages. This is strongly recommended
   * to call this method before any use of the parser.
   */

  public static void init()
  {
    System.out.println(DAWN_VERSION);

    installPackage("dawn.array");
    installPackage("dawn.err");
    installPackage("dawn.io");
    installPackage("dawn.javaccess");
    installPackage("dawn.loop");
    installPackage("dawn.math");
    installPackage("dawn.naming");
    installPackage("dawn.stack");
    installPackage("dawn.string");
    installPackage("dawn.test");
    installPackage("dawn.util");

    System.out.println();
    isInited = true;
  }

  /**
   * Returns true if the parser has already been initialized.
   * Dawn is considered initialized when a call to <code>init()</code>
   * has been made.
   */

  public static boolean isInitialized()
  {
    return isInited;
  }

  /**
   * Installs a package from Dawn archive.
   * @param packageName The package to load
   */

  public static void installPackage(String packageName)
  {
    installPackage(DawnParser.class, packageName, null);
  }

  /**
   * Installs a package specific to a given class. The class will give infos
   * to both load the package file and the package classes.
   * @param loader The <code>Class</code> which calls this, if the class is
   *               not part of Dawn standard package
   * @param packageName The package to load
   */

  public static void installPackage(Class loader, String packageName)
  {
    installPackage(loader, packageName, null);
  }

  /**
   * Installs a package specific to a given class. The class will give infos
   * to both load the package file and the package classes.
   * @param loader The <code>Class</code> which calls this, if the class is
   *               not part of Dawn standard package
   * @param packageName The package to load
   * @param parser If this parameter is not set to null, the package is loaded
   *               as runtime package
   */

  public static void installPackage(Class loader, String packageName, DawnParser parser)
  {
    if (packageName == null || loader == null)
      return;

    // check first if the package is already installed
    // (case of packages dependencies)
    if (installedPackages.contains(packageName))
    {
      System.out.println("Dawn:<installPackage>:package " + packageName + " is already installed");
      return;
    }

    // get classes to be loaded
    String[] classes = getClasses(loader, packageName);
    if (classes == null)
    {
      System.out.println("Dawn:<installPackage:err>:couldn't install " + packageName);
      return;
    }

    Object obj = null;
    Class _class = null;
    String className = null;
    Function _function = null;
    CodeSnippet _codeFunction = null;
    // ClassLoader classLoader = loader.getClassLoader();

    try
    {
      // load classes
      for (int i = 0; i < classes.length; i++)
      {
        className = classes[i];
        _class = Class.forName(className); //, true, classLoader);
        if (_class == null)
        {
          // if class is null, we get rid of it
          System.out.println("Dawn:<installPackage:err>:couldn't find class " + className +
                             " in package " + packageName);
          continue;
        }

        // we create an instance of the class to check it
        obj = _class.newInstance();
        if (obj instanceof Function)
        {
          // if it is a function, then we add it to the list
          _function = (Function) obj;
          (parser == null ? functions : parser.getRuntimeFunctions()).put(_function.getName(),
                                                                          _function);
        } else if (obj instanceof CodeSnippet) {
          // it is a coded function, we build it
          _codeFunction = (CodeSnippet) obj;
          if (parser == null)
            createGlobalFunction(_codeFunction.getName(), _codeFunction.getCode());
          else
            parser.createRuntimeFunction(_codeFunction.getName(), _codeFunction.getCode());
        }
      }
    } catch(Exception e) {
      System.out.println("Dawn:<installPackage:err>:couldn't load class " + className +
                         " from package " + packageName);
      System.out.println("Dawn:<installPackage:err>:package " + packageName + " wasn't loaded");
      return;
    }

    System.out.println("Dawn:<installPackage>:\t" + packageName + (packageName.length() < 8 ? "\t\t" : "\t") +
                       "successfully installed");
    (parser == null ? installedPackages : installedRuntimePackages).addElement(packageName);
  }

  // reads a package file and get classes-to-be-loaded names. it also checks package
  // dependencies. if a dependency is found, requested package is loaded

  private static String[] getClasses(Class loader, String packageName)
  {
    Vector buf = new Vector();

    InputStream _in = loader.getResourceAsStream(packageName);
    if (_in == null)
      return null;
    BufferedReader in = new BufferedReader(new InputStreamReader(_in));

    String line;
    try
    {
      while ((line = in.readLine()) != null)
      {
        line = line.trim();
        if (line.length() == 0)
          continue;

        if (line.charAt(0) == '#')
          continue;
        else if (line.startsWith("needs"))
        {
          int index = line.indexOf(' ');
          if (index == -1 || index + 1 == line.length())
          {
            System.out.println("Dawn:<installPackage:err>:package " + packageName +
                               " contains a bad \'needs\' statement");
            continue;
          }

          installPackage(loader, line.substring(index + 1), null);
        } else
          buf.addElement(line);
      }
      in.close();
    } catch (IOException ioe) {
      return null;
    }

    if (buf.size() > 0)
    {
      String[] classes = new String[buf.size()];
      buf.copyInto(classes);
      buf = null;
      return classes;
    } else
      return null;
  }

  /**
   * Creates a new parser.
   * @param in A <code>Reader</code> which will deliver the script to the parser
   */

  public DawnParser(Reader in)
  {
    st = createTokenizer(in);

    stack = new Stack();
    runtimeFunctions = new Hashtable();
    runtimeVariables = new Hashtable();
  }

  /**
   * Sets the parser print stream. Default packages may
   * pass informations through this stream (println function
   * for instance).
   * @param out The new <code>PrintStream</code>
   */

  public void setOut(PrintStream out)
  {
    this.out = out;
  }

  /**
   * Sets the parser error print stream. Default packages may
   * pass informations through this stream.
   * @param err The new <code>PrintStream</code> used for errors
   */

  public void setErr(PrintStream err)
  {
    this.err = err;
  }

  /**
   * Sets the parser input stream. Default packages may
   * pass informations through this stream (inputLine...)
   * @param out The new <code>OutputStream</code>
   */

  public void setIn(InputStream in)
  {
    this.in = in;
  }

  /**
   * Sets the <code>StreamTokenizer</code> used to execute a script.
   * It is HIGHLY recommended NOT TO CALL this without a very good
   * reason.
   * @param _st The new stream where to get the script from
   */

  public void setStream(StreamTokenizer _st)
  {
    st = _st;
  }

  /**
   * Returns current <code>StreamTokenizer</code>. It is mostly used
   * by functions to parse the script further. 'if' statement from
   * test package is a good example (see also for and while from the
   * loop package).
   */

  public StreamTokenizer getStream()
  {
    return st;
  }

  /**
   * Creates a new StreamTokenizer, setting its properties according to
   * the Dawn scripting language specifications. the stream is built
   * from a Reader which is most of the time a StringReader.
   * @param in The <code>Reader</code> which will deliver the script
   */

  public StreamTokenizer createTokenizer(Reader in)
  {
    StreamTokenizer st = new StreamTokenizer(in);
    st.resetSyntax();
    st.eolIsSignificant(true);
    st.whitespaceChars(0, ' ');
    st.wordChars(' ' + 1, 255);
    st.quoteChar('"');
    st.quoteChar('\'');
    st.commentChar('#');
    st.parseNumbers();
    st.eolIsSignificant(true);

    return st;
  }

  /**
   * Returns an Hashtable containing all the current global functions.
   */

  public static Hashtable getFunctions()
  {
    return functions;
  }

  /**
   * Returns the set of runtimes functions. This is needed by installPackage()
   * when the keyword 'needsGlobal' is used in a script.
   */

  public Hashtable getRuntimeFunctions()
  {
    return runtimeFunctions;
  }

  /**
   * Returns the stack which containes all the current availables datas.
   */

  public Stack getStack()
  {
    return stack;
  }

  /**
   * Checks if a given variable name is valid or not.
   * @parma function The <code>Function</code> which called this method
   * @param var The variable name to be tested
   */

  public void checkVarName(Function function, String var) throws DawnRuntimeException
  {
    if (var.equals("needs") || var.equals("needsGlobal"))
      throw new DawnRuntimeException(function, this, "you cannot use reserved keyword" +
                                     "\'needs\' or \'needsGlobal\'");

    boolean word = false;
    for (int i = 0; i < var.length(); i++)
    {
      if (Character.isDigit(var.charAt(i)) && !word)
      {
        throw new DawnRuntimeException(function, this, "bad variable/function name:" + var);
      } else
        word = true;
    }
  }

  /**
   * Checks if stack contains enough datas to feed a function.
   * @parma function The <code>Function</code> which called this method
   * @param nb The amount of arguments needed
   */

  public void checkArgsNumber(Function function, int nb) throws DawnRuntimeException
  {
    if (stack.size() < nb)
      throw new DawnRuntimeException(function, this, "bad arguments number, " + nb +
                                     " are required");
  }

  /**
   * Checks if the stack is empty.
   * @parma function The <code>Function</code> which called this method
   */

  public void checkEmpty(Function function) throws DawnRuntimeException
  {
    if (stack.isEmpty())
      throw new DawnRuntimeException(function, this, "empty stack");
  }

  /**
   * Checks if a given level is bound in the limits of the stack.
   * @parma function The <code>Function</code> which called this method
   * @param level The level to be tested
   */

  public void checkLevel(Function function, int level) throws DawnRuntimeException
  {
    if (level >= stack.size() || level < 0)
      throw new DawnRuntimeException(function, this, "stack level out of bounds:" + level);
  }

  /**
   * Sets a property in the parser. Properties are used by external functions
   * to store objects they may need later.
   * @param name An <code>Object</code> describing the property. It stands for the key
   * @param property The property value
   */

  public void setProperty(Object name, Object property)
  {
    if (name == null || property == null)
      return;
    properties.put(name, property);
  }

  /**
   * Returns a property according a given key.
   * @param name The property key
   */

  public Object getProperty(Object name)
  {
    if (name == null)
      return null;
    return properties.get(name);
  }

  /**
   * Unsets (remove) a given property.
   */

  public void unsetProperty(Object name)
  {
    properties.remove(name);
  }

  /**
   * Stops the parser.
   */

  public void stop()
  {
    stopped = true;
  }

  /**
   * Executes loaded script.
   */

  public void exec() throws DawnRuntimeException
  {
    if (st == null)
      throw new DawnRuntimeException(this, "parser cannot execute a non-existent script");

    try
    {
      for( ; ; )
      {
        if (stopped)
          return;

        switch(st.nextToken())
        {
          case StreamTokenizer.TT_EOL:
            lineno++;
            break;
          case StreamTokenizer.TT_EOF:
            // end of script
            return;
          case StreamTokenizer.TT_NUMBER:
            stack.push(new Double(st.nval));
            break;
          case StreamTokenizer.TT_WORD:
            if (st.sval.equals("needs") || st.sval.equals("needsGlobal"))
            {
              int keyWord = (st.sval.equals("needs") ? 0 : 1);
              if (st.nextToken() == StreamTokenizer.TT_WORD)
              {
                if (keyWord == 1)
                  installPackage(st.sval);
                else
                  installPackage(DawnParser.class, st.sval, this);
                break;
              } else {
                st.pushBack();
                throw new DawnRuntimeException(this, "bad usage of \'needs\' or \'needsGlobal\'" +
                                                     "reserved keyword");
              }
            }

            Function func = (Function) functions.get(st.sval);
            if (func != null)
              func.invoke(this);
            else
            {
              func = (Function) runtimeFunctions.get(st.sval);
              if (func != null)
                func.invoke(this);
              else
                stack.push(st.sval);
            }
            break;
          case '-':
            Function fc;
            if (st.nextToken() == StreamTokenizer.TT_WORD)
            {
              fc = (Function) functions.get('-' + st.sval);
              if (fc == null)
              {
                fc = (Function) runtimeFunctions.get('-' + st.sval);
                if (fc == null)
                {
                  st.pushBack();
                  fc = (Function) functions.get("-");
                }
              }
            } else {
              st.pushBack();
              fc = (Function) functions.get("-");
            }

            if (fc != null)
              fc.invoke(this);
            break;
          case '"': case '\'':
            pushString(st.sval);
            break;
        }
      }
    } catch (IOException ioe) {
      throw new DawnRuntimeException(this, "unexpected error occured during parsing");
    }
  }

  /**
   * Returns the <code>Hashtable</code> which contains the local variables.
   */

  public Hashtable getVariables()
  {
    return runtimeVariables;
  }

  /**
   * Returns the <code>Hashtable</code> which contains the global variables.
   */

  public Hashtable getGlobalVariables()
  {
    return variables;
  }

  /**
   * Returns the value of a given variable. Note that global variables got
   * priority on runtime ones.
   * @param var The variable to be recalled   
   */

  public Object getVariable(String var)
  {
    Object obj = variables.get(var);
    if (obj == null)
      obj = runtimeVariables.get(var);
    return obj;
  }

  /**
   * Sets a runtime variable. Runtime variables are stored temporarily. After
   * the execution of the script, they are flushed.
   * @param var The variable name
   * @param value An <code>Object</code> containg the variable value
   */

  public void setVariable(String var, Object value)
  {
    if (value == null)
      runtimeVariables.remove(var);
    else if (!functions.contains(var) && !runtimeFunctions.contains(var))
      runtimeVariables.put(var, value);
  }

  /**
   * Sets a global variable. Global variables are stored permanently, until the
   * JVM is killed or until the method <code>clearGlobalVariables()</code> is called.
   * @param var The variable name
   * @param value An <code>Object</code> containg the variable value
   */

  public static void setGlobalVariable(String var, Object value)
  {
    if (value == null)
      variables.remove(var);
    else if (!functions.contains(var))
      variables.put(var, value);
  }

  /**
   * Clears all the global variables.
   */

  public static void clearGlobalVariables()
  {
    variables.clear();
  }

  /**
   * Returns current line number in the script.
   */

  public int lineno()
  {
    // return st.lineno();
    return lineno;
  }

  /**
   * Returns a <code>String</code> containing a simple description of the current stack
   * state. All the levels are shown, each labeled by its level number.
   */

  public String dump()
  {
    Object o;
    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < stack.size(); i++)
    {
      buf.append(stack.size() - 1 - i).append(':');
      o = stack.elementAt(i);
      if (o instanceof Vector)
        buf.append("array[").append(((Vector) o).size()).append(']');
      else
        buf.append(o);
      buf.append('\n');
    }
    return buf.toString();
  }

  /**
   * Get topmost element of the stack and return is as a double value
   * if it can. Otherwise, an exception is thrown. In any case, the
   * element is removed from the stack.
   */

  public double popNumber() throws DawnRuntimeException
  {
    checkEmpty(null);
    Object obj = stack.pop();
    if (!(obj instanceof Double))
    {
      throw new DawnRuntimeException(this, "bad argument type");
    }
    return ((Double) obj).doubleValue();
  }

  /**
   * Get topmost element of the stack and return is as a double value
   * if it can. Otherwise, an exception is thrown.
   */

  public double peekNumber() throws DawnRuntimeException
  {
    checkEmpty(null);
    Object obj = stack.peek();
    if (!(obj instanceof Double))
    {
      throw new DawnRuntimeException(this, "bad argument type");
    }
    return ((Double) obj).doubleValue();
  }

  /**
   * Pushes a number on top of the stack.
   * @param number The number to be put on the stack
   */

  public void pushNumber(double number)
  {
    stack.push(new Double(number));
  }

  /**
   * Get the topmost element of the stack and returns it as
   * a <code>String</code>. If the string is enclosed by " quote
   * characters, they are removed. The element is removed from the stack.
   */

  public String popString() throws DawnRuntimeException
  {
    checkEmpty(null);
    String str = stack.pop().toString();
    if (str.length() != 0 && str.startsWith("\"") && str.endsWith("\""))
      str = str.substring(1, str.length() - 1);
    return str;
  }

  /**
   * Get the topmost element of the stack and returns it as
   * a <code>String</code>. If the string is enclosed by " quote
   * characters, they are removed.
   */

  public String peekString() throws DawnRuntimeException
  {
    checkEmpty(null);
    String str = stack.peek().toString();
    if (str.length() != 0 && str.startsWith("\"") && str.endsWith("\""))
      str = str.substring(1, str.length() - 1);
    return str;
  }

  /**
   * Puts a <code>String</code> on top of the stack.
   * @param str The string to be put on the stack
   */

  public void pushString(String str)
  {
    if (str.length() == 2 && str.charAt(0) == '\"' && str.charAt(1) == '\"')
      stack.push("\"\"");
    else
      stack.push('"' + str + '"');
  }

  /**
   * Gets topmost stack element and returns it as a <code>Vector</code>
   * which is the Java object for Dawn arrays. The element is removed
   * from the stack.
   */

  public Vector popArray() throws DawnRuntimeException
  {
    checkEmpty(null);
    Object obj = stack.pop();
    if (!(obj instanceof Vector))
    {
      throw new DawnRuntimeException(this, "bad argument type");
    }
    return (Vector) obj;
  }

  /**
   * Gets topmost stack element and returns it as a <code>Vector</code>
   * which is the Java object for Dawn arrays.
   */

  public Vector peekArray() throws DawnRuntimeException
  {
    checkEmpty(null);
    Object obj = stack.peek();
    if (!(obj instanceof Vector))
    {
      throw new DawnRuntimeException(this, "bad argument type");
    }
    return (Vector) obj;
  }

  /**
   * Pushes an array on top of the stack.
   * @param array The array to be put on the stack
   */

  public void pushArray(Vector array)
  {
    stack.push(array);
  }

  /**
   * Returns topmost objet of the stack and remove it.
   */

  public Object pop() throws DawnRuntimeException
  {
    checkEmpty(null);
    return stack.pop();
  }

  /**
   * Returns topmost object of the stack.
   */

  public Object peek() throws DawnRuntimeException
  {
    checkEmpty(null);
    return stack.peek();
  }

  /**
   * Puts an object on the top of the stack.
   * @param obj The object to be put on the top
   */

  public void push(Object obj)
  {
    stack.push(obj);
  }

  /**
   * Tells wether topmost object is a numeric value or not.
   */

  public boolean isTopNumeric()
  {
    return stack.peek() instanceof Double;
  }

  /**
   * Tells wether topmost object is a string or not.
   */

  public boolean isTopString()
  {
    Object obj = stack.peek();
    if (obj instanceof String)
    {
      String str = (String) obj;
      if (str.startsWith("\"") && str.endsWith("\""))
        return true;
    }
    return false;
  }

  /**
   * Tells wether topmost object is an array or not.
   */

  public boolean isTopArray()
  {
    return stack.peek() instanceof Vector;
  }

  /**
   * Tells wether topmost object is a literal identifier or not.
   */

  public boolean isTopLiteral()
  {
    return !isTopString() && !isTopNumeric() && !isTopArray();
  }

  /**
   * Returns topmost stack element type.
   */

  public int getTopType()
  {
    if (isTopNumeric())
      return DAWN_NUMERIC_TYPE;
    else if (isTopString())
      return DAWN_STRING_TYPE;
    else if (isTopArray())
      return DAWN_ARRAY_TYPE;
    else
      return DAWN_LITERAL_TYPE;
  }

  /**
   * Adds given function to the global functions list.
   * @param function The <code>Function</code> to be added
   */

  public static void addGlobalFunction(Function function)
  {
    if (function == null)
      return;
    String name = function.getName();
    if (!name.equals("needs") && !name.equals("needsGlobal"))
      functions.put(name, function);
  }

  /**
   * Adds given function to the runtime functions list.
   * @param function The <code>Function</code> to be added
   */

  public void addRuntimeFunction(Function function)
  {
    if (function == null)
      return;
    String name = function.getName();
    if (!name.equals("needs") && !name.equals("needsGlobal"))
      runtimeFunctions.put(name, function);
  }

  /**
   * Creates dynamically a function which can execute the Dawn script
   * passed in parameter.
   * @param code The Dawn code which will be executed by the returned
   *             function on invoke() call
   */

  public Function createOnFlyFunction(final String code)
  {
    return new Function()
    {
      public void invoke(DawnParser parser) throws DawnRuntimeException
      {
        StreamTokenizer _st = st;
        Hashtable _variables = (Hashtable) runtimeVariables.clone();
        st = createTokenizer(new StringReader(code));
        exec();
        st = _st;

        // copy changed variables
        String _varName;
        for (Enumeration e = runtimeVariables.keys(); e.hasMoreElements(); )
        {
          _varName = (String) e.nextElement();
          if (_variables.get(_varName) != null)
          {
            _variables.put(_varName, runtimeVariables.get(_varName));
          }
        }
        runtimeVariables = (Hashtable) _variables.clone();
      }
    };
  }

  /**
   * Creates dynamically a function which can execute the Dawn script
   * passed in parameter. The function is added to the global functions
   * list and not returned.
   * @param name Function Dawn name
   * @param code The Dawn code which will be executed by function
   */

  public static void createGlobalFunction(String name, final String code)
  {
    if (name == null || name.length() == 0 || name.equals("needs") ||
        name.equals("needsGlobal") || code == null)
      return;

    functions.put(name, new Function(name)
    {
      public void invoke(DawnParser parser) throws DawnRuntimeException
      {
        StreamTokenizer _st = parser.getStream();
        parser.setStream(parser.createTokenizer(new StringReader(code)));
        parser.exec();
        parser.setStream(_st);
      }
    });
  }

  /**
   * Creates dynamically a function which can execute the Dawn script
   * passed in parameter. The function is added to the runtime functions
   * list and not returned.
   * @param name Function Dawn name
   * @param code The Dawn code which will be executed by function
   */

  public void createRuntimeFunction(String name, final String code)
  {
    if (name == null || name.length() == 0 || name.equals("needs") ||
        name.equals("needsGlobal") || code == null)
      return;

    runtimeFunctions.put(name, new Function(name)
    {
      public void invoke(DawnParser parser) throws DawnRuntimeException
      {
        StreamTokenizer _st = st;
        st = createTokenizer(new StringReader(code));
        exec();
        st = _st;
      }
    });
  }
}

// End of DawnParser.java
