@echo off
set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.11.9-hotspot"
.\mvnw.cmd clean spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.arguments="--spring.config.location=file:src/main/resources/"
