/*
 * 21:57:18 - 25/05/2002
 *
 * JextLauncher.cpp - A windows launcher for Jext or any Java app
 * Copyright (C) 2001-2002 Romain Guy
 * romain.guy@jext.org
 * Portions copyright (C) 2002-2003 Paolo Giarrusso
 * blaisorblade_work@yahoo.it
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

//////////////////////////////////////////////////////////////////
// INCLUDES
//////////////////////////////////////////////////////////////////

// registry and shell
#include <windows.h>
#include <assert.h>

#include <io.h>                   //for finding files
#include <direct.h>               //for getcwd and chdir

#include <vector>
#include <algorithm>              //for copy
using namespace std;

#ifdef _MSC_VER
#define fullpath _fullpath        //for compatibility with VC++
#endif

//////////////////////////////////////////////////////////////////
// HEADER
//////////////////////////////////////////////////////////////////

void          ExecuteJext(LPCSTR path,LPCSTR lpCmdLine);
char**        GetClassPath(int *length);
bool          IsJextInstalled();
bool          SearchJREAndRun(LPCSTR keys[], int nKeys, LPSTR lpCmdLine);
bool          GetBoolValue(LPCSTR lpSection, LPCSTR lpKey);
void          AddHomeSetting(LPSTR args);

void          FreeAll();

//Library functions (I want it to work on Win95).
bool          MyPathFileExists(LPCSTR fileName);
void          MyPathCombine(LPSTR dest, LPCSTR dir, LPCSTR file);

void          PutMyPath(LPSTR outStr);
const char *  GetMyPath();
//////////////////////////////////////////////////////////////////
// CLASSES
//////////////////////////////////////////////////////////////////

// reads informations from INI files

class INIReader
{
private:
  char iniPath[MAX_PATH];

public:
  INIReader(char fileName[])
  {
    // constructs path to INI file
    PutMyPath(iniPath);
    strcat(iniPath, fileName);
  };

  LPSTR GetKeyValue(LPCSTR lpSection, LPCSTR lpKey, LPCSTR lpDefault, LPSTR outStr,
    DWORD size);
};

LPSTR INIReader::GetKeyValue(LPCSTR lpSection, LPCSTR lpKey, LPCSTR lpDefault, LPSTR outStr,
                             DWORD size = (DWORD) MAX_PATH)
{
  GetPrivateProfileString(lpSection, lpKey, lpDefault, outStr, size, iniPath);
  return outStr;
};

// reads registry informations

class RegistryReader
{
public:
  // Retrieve a string value. If the given buffer for the string is too small (specified
  // by rdwSize), rdwSize is increased to the correct value. If the buffer is bigger than
  // the retrieved string, rdwSize is set to the length of the string (in TCHARs) including
  // the terminating null.
  static bool GetStringValue(HKEY hKeyRoot, LPCSTR pszSubKey, LPCSTR pszValue, LPSTR pszBuffer, DWORD& rdwSize);
};

bool RegistryReader::GetStringValue(HKEY hKeyRoot, LPCSTR pszSubKey, LPCSTR pszValue,
                                    LPSTR pszBuffer, DWORD& rdwSize)
{
  HKEY hKey;
  DWORD dwType = REG_SZ;
  LONG  lRes;

  if (!pszBuffer)
    return false;

  if ((lRes = RegOpenKeyEx(hKeyRoot, pszSubKey, 0, KEY_READ, &hKey))!=ERROR_SUCCESS)
  {
    SetLastError(lRes);
    return false;
  }
  lRes = RegQueryValueEx(hKey, pszValue, NULL, &dwType, (BYTE*) pszBuffer, &rdwSize);
  RegCloseKey(hKey);

  if (dwType != REG_SZ)
    return false;
  
  if (lRes != ERROR_SUCCESS)
  {
    SetLastError(lRes);
    return false;
  }

  return true;
};

//////////////////////////////////////////////////////////////////
// FIELDS
//////////////////////////////////////////////////////////////////
INIReader reader("jextlauncher.ini");
LPSTR * classPaths;
int numOfClPaths;
char myPath[MAX_PATH];

//////////////////////////////////////////////////////////////////
// METHODS AND FUNCTIONS
//////////////////////////////////////////////////////////////////

//finds if a file exists(replacement for Win95, which IS supported.).
bool MyPathFileExists(LPCSTR fileName) {
  _finddata_t data;
  long handle = _findfirst(fileName, &data);
  if (handle == -1)
    return false;
  else {
    _findclose(handle);
    return true;
  }
}

//gets the path of the folder of this executable,
//with the final slash included
void PutMyPath(LPSTR outStr) {
  GetModuleFileName(NULL, outStr, (DWORD) MAX_PATH);
  char *lastSlash = strrchr(outStr, '\\'); //find last slash.
  *(++lastSlash) = NULL; 
}

inline const char* GetMyPath() {
  return myPath;
}

void MyPathCombine(LPSTR dest, LPCSTR dir, LPCSTR file) {
  char savedPath[MAX_PATH];
  getcwd(savedPath, MAX_PATH);
  chdir(dir);
  fullpath(dest, file, MAX_PATH);
  chdir(savedPath);
}

bool GetBoolValue(LPCSTR lpSection, LPCSTR lpKey) {
  char tmp[6];
  reader.GetKeyValue(lpSection, lpKey, "", tmp, 6);
  return tolower(tmp[0]) == 'y';
}

void AddHomeSetting(LPSTR args) {
  char docsPath[MAX_PATH], homeOpt[MAX_PATH + 30];
  DWORD bufferSize = MAX_PATH;

  //maybe the key should end with Explorer\\User Shell Folders instead of
  //Explorer\\Shell Folders
  if (RegistryReader::GetStringValue(HKEY_CURRENT_USER,
      "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders",
      "Personal", docsPath, bufferSize)) {
    sprintf(homeOpt, "-Duser.home=\"%s\" ", docsPath);
    strcat(args, homeOpt);
  }
}

// execute Jext
void ExecuteJext(LPCSTR path, LPCSTR lpCmdLine) {
  // javaw arguments string
  char arguments[MAX_PATH * 20];
  // GetKeyValue output strings;
  char outStr[MAX_PATH];
  // flags
  reader.GetKeyValue("Launcher", "flags", "-Xms32m", arguments, MAX_PATH * 20);
  strcat(arguments, " ");

  if (strstr(arguments, "-Duser.home") == NULL && GetBoolValue("Launcher", "SetHome")) {
    //the user doesn't define its own Java home property setting, so we do it if specified.
    AddHomeSetting(arguments);
  }

  // classpath
  if (numOfClPaths > 0) {
    strcat(arguments, "-classpath ");
    for (int i = 0; i < numOfClPaths; i++) {
      // add the classpath[i] to the command line
      strcat(arguments, "\"");
      strcat(arguments, classPaths[i]);
      strcat(arguments, "\"");

      if (i != numOfClPaths - 1)
        strcat(arguments, ";");
    }
  }

  // main class
  strcat(arguments, " ");
  strcat(arguments, reader.GetKeyValue("Launcher", "main-class", "org.jext.Jext", outStr));

  // arguments
  strcat(arguments, " ");
  strcat(arguments, lpCmdLine);

  // execute

#ifdef _DEBUG
  char Message[2000];
  _snprintf(Message, 2000, "The command is: \"%s %s\"\nand it is executed from the path: %s",
    path, arguments, GetMyPath());
  int choice = MessageBox(NULL,
               Message,
               reader.GetKeyValue("Messages", "launcher.title", "Launcher", outStr),
               MB_ICONEXCLAMATION || MB_OKCANCEL);
  if (choice != IDCANCEL) //so in Debug mode ShellExecute is called only
      // if the user doesn't press Cancel
#endif
  ShellExecute(0, "open",
                  path,
                  arguments,
                  GetMyPath(),
                  SW_SHOWNORMAL);
}

// launch Jext using the <key> Java runtime

bool SearchJREAndRun(LPCSTR keys[], const int nKeys, LPSTR lpCmdLine) {
  // buffer
  char buffer[MAX_PATH];
  DWORD bufferSize = MAX_PATH;
  // misc buffers
  char pathBuffer[100];                     //Huge enough, actual maximum will be less than 50
  char readKeyName[10], keyName[10];

  // Read user supplied version; for each user supplied one, tries in every item of keys[]
  //it now works this way since otherwise it doesn't find the JDK 1.3 because
  //there is the JRE 1.4

  char javaVersion[20];

  for (int nJavaVersion = 0; nJavaVersion < 10; nJavaVersion++) {
    sprintf(javaVersion, "JavaVersion.%d", nJavaVersion);
    reader.GetKeyValue("Launcher", javaVersion, "", keyName, 10);

    if ( strcmp(keyName, "") != 0) {
      // creates new key path
      for (int currKeyI = 0; currKeyI < nKeys; currKeyI++) {
        LPCSTR key = keys[currKeyI];
        sprintf(pathBuffer, "Software\\JavaSoft\\%s%s", key, keyName);

        // find JRE home
        bufferSize = MAX_PATH;
        if (RegistryReader::GetStringValue(HKEY_LOCAL_MACHINE,
                                           pathBuffer, "JavaHome",
                                           buffer, bufferSize)) {
          // creates the full path
          if ( GetBoolValue("Launcher", "UseJavaInterp") )
            strcat(buffer, "\\bin\\java.exe");
          else
            strcat(buffer, "\\bin\\javaw.exe");

          if (MyPathFileExists(buffer)) {
            ExecuteJext(buffer, lpCmdLine);
            return true;
          }
        }
      }//for end
    }
    else
      break;
  }//loop over .INI JavaVersion items end

  //Version chosen in the .ini file invalid: search the latest, for each item of keys[]
  HKEY handle;
  for (int currKeyI = 0; currKeyI < nKeys; currKeyI++) {
    LPCSTR key = keys[currKeyI];
    sprintf(pathBuffer, "Software\\JavaSoft\\%s", key);
    strcpy(keyName, "0.0");

    // search the latest JDK/JRE version.
    RegOpenKeyEx(HKEY_LOCAL_MACHINE, pathBuffer, 0, KEY_READ, &handle);
    for (int i = 0;;i++) {
      if (RegEnumKey(handle, i, readKeyName, 10) != ERROR_SUCCESS)
        break;
      else if (strlen(readKeyName) == 3) { //main version key, not such as 1.3.1_01
        //compares version numbers
        //these are major & minor numbers
        int maj     = readKeyName[0] - '0', min     = readKeyName[2] - '0',
            bestMaj = keyName[0]     - '0', bestMin = keyName[2]     - '0';

        if (maj > bestMaj || (maj == bestMaj && min > bestMin))
          strcpy(keyName, readKeyName);
      }
    }
    RegCloseKey(handle);

    if ( strcmp(keyName, "0.0") != 0) {                   //found any JDK/JRE
      // creates new key path
      sprintf(pathBuffer, "Software\\JavaSoft\\%s%s", key, keyName);

      // find JRE home
      bufferSize = MAX_PATH;
      if (RegistryReader::GetStringValue(HKEY_LOCAL_MACHINE,
                                         pathBuffer, "JavaHome",
                                         buffer, bufferSize)) {
        // creates the full path
        if ( GetBoolValue("Launcher", "UseJavaInterp") )
          strcat(buffer, "\\bin\\java.exe");
        else
          strcat(buffer, "\\bin\\javaw.exe");
        if (MyPathFileExists(buffer)) {
          ExecuteJext(buffer, lpCmdLine);
          return true;
        }
      }
    }
  } //end of 2° for

  return false;
}

// returns the class path as a table of LPSTR. It reads the datas from INI file and 
// expands them fully(take cares of environment vars and wildcards) into absolute paths.

char** GetClassPath(int *length) {
  const int bufSize = MAX_PATH * 8;
  char classpath[20];
  vector<LPSTR> resPaths;
  resPaths.reserve(10);
  //here goes the string it reads, then it is split into lpPaths
  char _thisPath[bufSize];
  char * thisPath;

  for (int toRead = 0; toRead < 100; toRead++) {
    thisPath = _thisPath;  //since I move thisPath on the string, I need to do this

            // Get INI value
    sprintf(classpath, "classpath.%d", toRead);
    reader.GetKeyValue("Launcher", classpath, "", thisPath, MAX_PATH * 8);
    if ( ! thisPath[0] )
      break;

    bool isLiteral = true;
    //this is a literal value and must be added to the classpaths,
    //if this is true; else it derives from expansion and must not be added if not existing.

            // Expands env vars
    // checks if there are two '%' not next to each other
    // ("%%" is not considered an environment var)
    char * firstPerc = strchr(thisPath, '%');
    if ((firstPerc && firstPerc[1]) ? strchr(firstPerc + 2, '%') : 0) {
        //so if firstPerc is NULL, or if the string finishes before firstPerc + 2,
        //it doesn't call strchr on a undefined string. If firstPerc isn't 0 neither
        //firstPerc[0] is, since it is a return value from strchr.
      isLiteral = false;
      char envVarValue[bufSize];

      int nChars = ExpandEnvironmentStrings(thisPath, envVarValue, bufSize);

      if (nChars != 0 && strcmp(thisPath, envVarValue) != 0)
        strcpy(thisPath, envVarValue);
    }

    while (thisPath[0]) {  
            //Puts into singlePath one of the path items of thisPath.
      size_t index = strcspn(thisPath, ";");
      LPSTR singlePath = new char[MAX_PATH];

      strncpy(singlePath, thisPath, index);                  //it doesn't copy the ';'
      singlePath[index] = NULL;                              //this IS required

      thisPath += index;                                     //it now points to ';' or NULL
      if (thisPath[0])
        thisPath++;                                          //skips the ';'

            //Now we process singlePath.

      //This is used to remove from the classpath the "." elements, which come from
      //the %CLASSPATH% but are useless here.
      if (strcmp(singlePath, ".") == 0) {
        delete[] singlePath;
        continue;
      }

            //Expands relative path.
      MyPathCombine(singlePath, GetMyPath(), singlePath);

            //Expands wildcards
      char * wildCard = strpbrk(singlePath, "*?");
      if (wildCard != NULL) {
        _finddata_t data;
        
        long handle = _findfirst(singlePath, &data);
        if (handle == -1)
          continue;
        else {
          char dirName[MAX_PATH] = "";
          char *lastSlash = strrchr(singlePath, '\\'); //last backslash in the name
          
          if (lastSlash != NULL) {
            strncpy(dirName, singlePath, lastSlash - singlePath + 1); //including backslash.
            dirName[lastSlash - singlePath + 1] = NULL;               //required after strncpy
          }

          do {
            LPSTR file = new char[MAX_PATH];
            sprintf(file, "%s%s", dirName, data.name);
            resPaths.push_back(file);
          } while (_findnext(handle, &data) == 0);

          _findclose(handle);
          delete[] singlePath;
          continue;
        }
      } else if (isLiteral || MyPathFileExists(singlePath)) {
        //see isLiteral comment at its declaration for the above condition.
        resPaths.push_back(singlePath);
      } else {
        delete[] singlePath;
      }
    }
  } //end loop over input items.

  *length = resPaths.size();
  LPSTR *lpPaths = new LPSTR[*length];
  copy(resPaths.begin(), resPaths.end(), lpPaths);
  return lpPaths;
}


// returns true if Jext is correctly installed

bool IsJextInstalled() {
  bool installed = true;
  char outPath[MAX_PATH];

  for (int i = 0; i < numOfClPaths; i++) {
    MyPathCombine(outPath, GetMyPath(), classPaths[i]); //FIXME: now that GetClassPath expands ClassPaths, this
    //should go away.
    installed = installed && MyPathFileExists(outPath);
    if (!installed)
      break;
  }
  return (numOfClPaths == 0 ? false : installed);
}

void FreeAll() {
  for (int i = 0; i < numOfClPaths; i++)
    delete[] classPaths[i];
  delete[] classPaths;
}

//////////////////////////////////////////////////////////////////
// MAIN ENTRY POINT
//////////////////////////////////////////////////////////////////

int APIENTRY WinMain(HINSTANCE hInstance,
                     HINSTANCE hPrevInstance,
                     LPSTR    lpCmdLine,
                     int       nCmdShow) {
  // GetKeyValue output strings;
  char outStr1[MAX_PATH], outStr2[MAX_PATH];
  // initializes global myPath.
  PutMyPath(myPath);

  //reads the classpaths from the .ini file for use by functions
  classPaths = GetClassPath(&numOfClPaths);

  // test if Jext is well installed
  if (!IsJextInstalled()) {
    MessageBox(NULL,
               reader.GetKeyValue("Messages", "install.failed", "", outStr1),
               reader.GetKeyValue("Messages", "launcher.title", "Launcher", outStr2),
               MB_ICONEXCLAMATION);
    FreeAll();
    return 1;
  }

  // try to launch a JRE
  LPCSTR keys[] = {"Java Runtime Environment\\", "Java Development Kit\\"};

  if (!SearchJREAndRun(keys, 2, lpCmdLine)) {
      char jrePath[MAX_PATH];
      reader.GetKeyValue("Launcher", "jre", "", jrePath);

      // get current executing path
      MyPathCombine(jrePath, GetMyPath(), jrePath);

      // try to launch a local JRE
      if (!MyPathFileExists(jrePath)) {
        MessageBox(NULL,
                   reader.GetKeyValue("Messages", "no.jdk", "", outStr1),
                   reader.GetKeyValue("Messages", "launcher.title", "Launcher", outStr2),
                   MB_ICONEXCLAMATION);
        FreeAll();
        return 2;
      } else {
        // starts Jext with local JRE
        ExecuteJext(jrePath, lpCmdLine);
      }
  }

  FreeAll();
  return 0;
}

// End of JextLauncher.cpp
