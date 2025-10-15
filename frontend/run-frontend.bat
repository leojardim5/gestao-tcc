@echo off
echo ================================================
echo   INICIANDO FRONTEND - SISTEMA DE GESTAO TCCs
echo ================================================

REM Verificar se o Node.js esta instalado
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Node.js nao encontrado!
    pause
    exit /b 1
)

echo [OK] Node.js encontrado
echo.

REM Verificar se as dependencias estao instaladas
if not exist "node_modules" (
    echo Instalando dependencias...
    npm install
)

echo Iniciando Next.js...
echo URL: http://localhost:3000
echo.

REM Executar Next.js
npx next dev

pause
