@ECHO OFF
ECHO Executing maven commands
call mvn clean install
ECHO Starting Application
call mvn spring-boot:run -Dspring-boot.run.profiles=local