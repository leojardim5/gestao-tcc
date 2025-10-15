@echo off
echo ================================================
echo   INICIANDO BACKEND - SISTEMA DE GESTAO TCCs
echo ================================================

REM Verificar se o Java esta instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Java nao encontrado!
    pause
    exit /b 1
)

echo [OK] Java encontrado
echo.

REM Verificar se o JAR existe
if not exist "target\gestaotcc-backend-0.0.1-SNAPSHOT.jar" (
    echo ERRO: JAR nao encontrado! Compilando...
    echo Execute: mvn clean package
    pause
    exit /b 1
)

echo Iniciando Spring Boot...
echo URL: http://localhost:8081
echo Swagger: http://localhost:8081/swagger-ui.html
echo.

REM Executar aplicacao
java -jar target\gestaotcc-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

pause
