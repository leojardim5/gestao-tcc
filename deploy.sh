#!/bin/bash

# Script de Deploy - Sistema de Gestão de TCCs
echo "================================================"
echo "   DEPLOY - SISTEMA DE GESTÃO DE TCCs"
echo "================================================"

# Verificar se o Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não encontrado! Instale o Docker primeiro."
    exit 1
fi

# Verificar se o Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose não encontrado! Instale o Docker Compose primeiro."
    exit 1
fi

echo "✅ Docker e Docker Compose encontrados"

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker-compose down

# Remover imagens antigas (opcional)
echo "🧹 Limpando imagens antigas..."
docker-compose down --rmi all

# Construir e iniciar os serviços
echo "🔨 Construindo e iniciando serviços..."
docker-compose up --build -d

# Aguardar os serviços ficarem prontos
echo "⏳ Aguardando serviços ficarem prontos..."
sleep 30

# Verificar status dos serviços
echo "📊 Verificando status dos serviços..."
docker-compose ps

# Verificar logs
echo "📋 Logs dos serviços:"
echo "Backend:"
docker-compose logs --tail=10 backend

echo "Frontend:"
docker-compose logs --tail=10 frontend

echo "PostgreSQL:"
docker-compose logs --tail=10 postgres

echo ""
echo "🎉 Deploy concluído!"
echo ""
echo "URLs de acesso:"
echo "- Frontend: http://localhost:3000"
echo "- Backend API: http://localhost:8081"
echo "- Swagger UI: http://localhost:8081/swagger-ui.html"
echo "- PostgreSQL: localhost:5432"
echo ""
echo "Para ver os logs em tempo real:"
echo "docker-compose logs -f"
echo ""
echo "Para parar os serviços:"
echo "docker-compose down"
