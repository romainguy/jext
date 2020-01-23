javac -classpath %JEXT_HOME%/build;. *.java
jar cf HTML.jar *.class *.xml *.dtd
move /Y HTML.jar %JEXT_HOME%/bin/plugins
