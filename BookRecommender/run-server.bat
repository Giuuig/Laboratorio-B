@echo off
cd /d "%~dp0"

set JAR=serverBR\target\serverBR-1.0.0-jar-with-dependencies.jar

if not exist "%JAR%" (
    echo Jar not found, building server...
    call mvn -q -DskipTests package -pl serverBR -am
    if errorlevel 1 (
        echo Maven build failed
        pause
        exit /b 1
    )
)

echo Starting server from %JAR%
java -jar "%JAR%"
pause
