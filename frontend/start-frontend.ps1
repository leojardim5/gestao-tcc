# Script PowerShell para iniciar o Frontend
Write-Host "Iniciando Frontend - Sistema de Gestao de TCCs" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green

# Verificar se o Node.js esta instalado
try {
    $nodeVersion = node --version
    Write-Host "[OK] Node.js encontrado: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "ERRO: Node.js nao encontrado! Instale o Node.js 18 ou superior." -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

# Verificar se as dependencias estao instaladas
if (-not (Test-Path "node_modules")) {
    Write-Host "Instalando dependencias..." -ForegroundColor Yellow
    npm install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERRO: Falha ao instalar dependencias!" -ForegroundColor Red
        Read-Host "Pressione Enter para sair"
        exit 1
    }
}

Write-Host "Iniciando aplicacao Next.js..." -ForegroundColor Yellow
Write-Host "URL: http://localhost:3000" -ForegroundColor Cyan
Write-Host ""

# Tentar diferentes formas de executar o Next.js
if (Test-Path "node_modules\.bin\next.cmd") {
    Write-Host "Usando next.cmd..." -ForegroundColor Yellow
    & ".\node_modules\.bin\next.cmd" dev
} elseif (Test-Path "node_modules\.bin\next") {
    Write-Host "Usando next..." -ForegroundColor Yellow
    & ".\node_modules\.bin\next" dev
} else {
    Write-Host "Tentando npx..." -ForegroundColor Yellow
    npx next dev
}

Read-Host "Pressione Enter para sair"
