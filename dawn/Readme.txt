Dawn - A dynamic scripting language for Java - 18:37:38 03/08/00
================================================================

Copyright (C) 2000 Romain Guy
Copyright (C) 2000 Guillaume Desnoix (JavaAccess package)
guy.romain@bigfoot.com
www.chez.com/powerteam

  To execute a script type (assuming danw.jar is declared in your
classpath):
java com.chez.powerteam.dawn.Dawn <script name>

  You can use the console instead:
java com.chez.powerteam.dawn.Dawn -console
or:
java com.chez.powerteam.dawn.Dawn -nativeConsole

  The -nativeConsole enables a console written in Java instead one
written in Dawn itself (Dawn-written one keeps stack between each
command but if an error is thrown. On the contrary, Java console
reset the parser between each command).

  While in the console type: "script" exec
to execute the script "script".
  You can also simply type: help
to get a full list of available functions

  Note that the doc contained in doc\ directory contains a
brief description of each function. If this directory does not exists,
you should unjar sources:
jar xf dawn_src.jar
And then generate documentation:
  make doc
or:
  mkdir doc
  javadoc -version -author -d doc @packages.doc
