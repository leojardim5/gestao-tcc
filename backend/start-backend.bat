@echo off
echo Iniciando Backend - Sistema de Gestao de TCCs
echo ================================================

REM Verificar se o Java esta instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Java nao encontrado! Instale o Java 17 ou superior.
    pause
    exit /b 1
)

REM Verificar se o JAR existe
if not exist "target\gestaotcc-backend-0.0.1-SNAPSHOT.jar" (
    echo ERRO: JAR nao encontrado! Execute 'mvn clean package' primeiro.
    pause
    exit /b 1
)

echo Iniciando aplicacao Spring Boot...
echo URL: http://localhost:8081
echo Swagger: http://localhost:8081/swagger-ui.html
echo.

java -jar target\gestaotcc-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

pause
