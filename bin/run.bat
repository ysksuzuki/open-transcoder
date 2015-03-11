@echo off

SETLOCAL

if NOT DEFINED JAVA_HOME goto err

REM JAVA options
REM You can set JVM additional options here if you want
if NOT DEFINED JVM_OPTS set JVM_OPTS=-Xverify:none -XX:+TieredCompilation -XX:+UseBiasedLocking -XX:+UseStringCache -XX:+UseParNewGC -XX:InitialCodeCacheSize=8m -XX:ReservedCodeCacheSize=32m -Dorg.terracotta.quartz.skipUpdateCheck=true
REM Set up security options
REM set SECURITY_OPTS=-Djava.security.debug=failure -Djava.security.manager"
set SECURITY_OPTS=-Djava.security.debug=failure
REM Combined java options
set JAVA_OPTS=%SECURITY_OPTS% %JAVA_OPTS% %JVM_OPTS%

set PRG_DIR=%~dp0

echo Starting open-transcoder
"%JAVA_HOME%\bin\java" %JAVA_OPTS% -jar %PRG_DIR%..\open-transcoder.jar %*
goto finally

:err
echo JAVA_HOME environment variable not set!
pause

:finally
ENDLOCAL