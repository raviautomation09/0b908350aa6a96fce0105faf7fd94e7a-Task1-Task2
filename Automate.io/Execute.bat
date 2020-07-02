call Build.bat
TIMEOUT /T 10
SET CLASSPATH=./Library/*;./Library/jar_files/*;./Library/htmlReportLib/*;./CustomLib/*;./Library/Selenium/*;./Library/Selenium/lib/*;
java -classpath %CLASSPATH% -Xms128M -Xmx1024M -Dfile.encoding=UTF-8 com.maf.main.Executor %1
pause