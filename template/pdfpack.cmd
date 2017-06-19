@chcp 1251
@ECHO OFF

cd %~dp0

if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome

"%JAVA_HOME%\bin\java.exe"  -Dlog4j.configuration=file:.\jar\log4j.xml -Xms1000m -Xmx1900m -cp .\jar\commons-logging-1.1.1.jar;.\jar\db2jcc.jar;.\jar\db2jcc4.jar;.\jar\fontbox-1.8.13.jar;.\jar\jempbox-1.8.13.jar;.\jar\log4j-1.2.16.jar;.\jar\ojdbc6.jar;.\jar\orai18n.jar;.\jar\pdfbox-1.8.13.jar;.\jar\pdfpack.jar;.\jar\postgresql-9.4.1212.jre7.jar;.\jar\xdb6.jar com.rstyle.rsdoc.pdfpack.PDFPack %*

goto exit

:noJavaHome
echo ���������� ����� java.exe, ���������� JAVA_HOME goto exit

:exit
exit /b 1


