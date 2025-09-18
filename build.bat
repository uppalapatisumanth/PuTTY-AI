@echo off
echo Building Putty AI Assistant using Ant...

rem Set Java 8 path - update this to your Java 8 installation path
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_301
set PATH=%JAVA_HOME%\bin;%PATH%

rem Use Ant to build the project
ant clean jar

pause