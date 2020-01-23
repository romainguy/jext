//////////////////////////////////////////////////////////
// jext.js - Script to start Jext under Windows 98
// 05/22/2001 - 18:16:06
// v1.5
//
// I know I shouldn't have made those ugly nested tries
//
// JScript for starting Jext on Win98 or Win95 + WSH
// This script first checks existance of an installed JRE,
// then it checks existance of Jext archive. If one is not
// found, we exit and please user to download either Jext
// either a JRE.
//
// www.jext.org
// romain.guy@jext.org

// Set objects
var fileSystem = WScript.createObject("Scripting.FileSystemObject");
var shell = WScript.createObject("WScript.Shell");

function noJDK()
{
  shell.popup("You have no valid JDK/JRE installed\nPlease download one at:\n" +
              "http://java.sun.com", 0, "Jext", 16);
  WScript.quit(1);
}

/////////////////////////////////////////////////////////////////////////////////////////////////
// EXISTANCE TEST
/////////////////////////////////////////////////////////////////////////////////////////////////

// Get JDK release
var javaVersion;

// trying with a JRE
try
{
  javaVersion = shell.regRead("HKLM\\Software\\JavaSoft\\Java Runtime Environment\\CurrentVersion");
} catch (Exception) {
  // trying with a JDK
  try
  {
    javaVersion = shell.regRead("HKLM\\Software\\JavaSoft\\Java Development Kit\\CurrentVersion");
  } catch (Exception) {
    noJDK();
  }
}

if (javaVersion.substring(0, 3) == "1.1")
{
  shell.popup("This script is targeted to be used\nwith a JDK 1.2 or greater only !",
              0, "Jext", 16);
  WScript.quit(1);
}

// Get path to JVM (here it is a JRE which is requested)
var javaPath;

try
{
  javaPath = shell.regRead("HKLM\\Software\\JavaSoft\\Java Runtime Environment\\" + javaVersion +
                           "\\JavaHome") + "\\bin\\javaw.exe";
} catch (Exception) {
  try
  {
    javaPath = shell.regRead("HKLM\\Software\\JavaSoft\\Java Development Kit\\" + javaVersion +
                             "\\JavaHome") + "\\bin\\javaw.exe";
  } catch (Exception) {
    noJDK();
  }
}

// Check existance of JRE
if (!fileSystem.fileExists(javaPath))
{
  noJDK();
}

/////////////////////////////////////////////////////////////////////////////////////////////////
// RUNNING STAGE
/////////////////////////////////////////////////////////////////////////////////////////////////

// We concat arguments
var args = WScript.arguments;
var argString = new String;
var i;

for (i = 0; i < args.count(); i++)
  argString = argString + ' ' + '\"' +  args.item(i) + '\"';

// Run Jext
shell.run('\"' + javaPath + '\"' + " -Dpython.path=..\\lib\\Lib -Xms48m -classpath ..\\lib\\looks-@LOOKS-VERSION@.jar;..\\lib\\jython-@JYTHON-VERSION@.jar;..\\lib\\dawn-@DAWN-VERSION@.jar;..\\lib\\jext-@VERSION@.jar org.jext.Jext" + argString);

// End of jext.js
