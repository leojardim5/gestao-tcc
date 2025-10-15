@echo off
echo ================================================
echo   SISTEMA DE GESTAO DE TCCs - EXECUTANDO TUDO!
echo ================================================
echo.

REM Verificar se estamos no diretorio correto
if not exist "backend" (
    echo ERRO: Execute este script no diretorio raiz do projeto!
    pause
    exit /b 1
)

if not exist "frontend" (
    echo ERRO: Execute este script no diretorio raiz do projeto!
    pause
    exit /b 1
)

echo 1. Verificando dependencias...
echo.

REM Verificar Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Java nao encontrado! Instale o Java 17+
    pause
    exit /b 1
) else (
    echo [OK] Java encontrado
)

REM Verificar Node.js
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Node.js nao encontrado! Instale o Node.js 18+
    pause
    exit /b 1
) else (
    echo [OK] Node.js encontrado
)

echo.
echo 2. Iniciando Backend...
echo ======================
start "Backend - Gestao TCC" cmd /k "cd backend && run-backend.bat"

echo Aguardando backend inicializar...
timeout /t 15 /nobreak >nul

echo.
echo 3. Iniciando Frontend...
echo =======================
start "Frontend - Gestao TCC" cmd /k "cd frontend && run-frontend.bat"

echo.
echo ================================================
echo   SISTEMA INICIADO COM SUCESSO! 🚀
echo ================================================
echo.
echo URLs:
echo - Frontend: http://localhost:3000
echo - Backend:  http://localhost:8081
echo - Swagger:  http://localhost:8081/swagger-ui.html
echo.
echo Pressione qualquer tecla para fechar esta janela...
pause >nul
