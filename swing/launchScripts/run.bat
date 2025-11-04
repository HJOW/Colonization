@echo off
setlocal

REM Check JAVA_HOME is declared and version is least 1.8
IF NOT DEFINED JAVA_HOME (
    echo [ERROR] There is no JAVA_HOME environment !
    goto :eof
)

IF NOT EXIST "%JAVA_HOME%\bin\java.exe" (
    echo [ERROR] Incorrect JAVA_HOME environment !
    goto :eof
)

"%JAVA_HOME%\bin\java.exe" -cp ".\colonization-swing-0.0.1.jar;%HOMEDRIVE%%HOMEPATH%\lib\*" org.duckdns.hjow.colonization.Colonization --updator Y %*

:EOF
endlocal
