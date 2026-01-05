@echo off
cd /d "%~dp0"

set JAR=clientBR\target\clientBR-1.0.0-jar-with-dependencies.jar
set LIBDIR=clientBR\target\lib

if not exist "%JAR%" (
    echo Jar not found, building client...
    call mvn -q -DskipTests package -pl clientBR -am
    if errorlevel 1 (
        echo Maven build failed
        pause
        exit /b 1
    )
)

if not exist "%LIBDIR%" (
    echo Copying JavaFX libraries...
    call mvn -q -pl clientBR org.apache.maven.plugins:maven-dependency-plugin:3.6.0:copy-dependencies -DincludeGroupIds=org.openjfx -DoutputDirectory=%LIBDIR%
)

echo Starting client from %JAR%
java --module-path "%LIBDIR%" --add-modules javafx.controls,javafx.fxml -jar "%JAR%"
pause
