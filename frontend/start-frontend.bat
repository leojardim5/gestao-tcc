@echo off
echo Iniciando Frontend - Sistema de Gestao de TCCs
echo ================================================

REM Verificar se o Node.js esta instalado
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Node.js nao encontrado! Instale o Node.js 18 ou superior.
    pause
    exit /b 1
)

REM Verificar se as dependencias estao instaladas
if not exist "node_modules" (
    echo Instalando dependencias...
    npm install
    if %errorlevel% neq 0 (
        echo ERRO: Falha ao instalar dependencias!
        pause
        exit /b 1
    )
)

echo Iniciando aplicacao Next.js...
echo URL: http://localhost:3000
echo.

REM Tentar diferentes formas de executar o Next.js
if exist "node_modules\.bin\next.cmd" (
    echo Usando next.cmd...
    call ".\node_modules\.bin\next.cmd" dev
) else if exist "node_modules\.bin\next" (
    echo Usando next...
    call ".\node_modules\.bin\next" dev
) else (
    echo Tentando npx...
    npx next dev
)

pause
