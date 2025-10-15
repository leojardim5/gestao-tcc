#!/bin/bash

# Script de Backup e Restore - Sistema de Gestão de TCCs
echo "================================================"
echo "   BACKUP E RESTORE - SISTEMA DE GESTÃO DE TCCs"
echo "================================================"

BACKUP_DIR="./backups"
DATE=$(date +"%Y%m%d_%H%M%S")

# Função para criar backup
create_backup() {
    echo "📦 Criando backup do banco de dados..."
    
    # Criar diretório de backup se não existir
    mkdir -p $BACKUP_DIR
    
    # Backup do banco de dados
    docker-compose exec -T postgres pg_dump -U postgres gestaotcc_db > "$BACKUP_DIR/gestaotcc_db_$DATE.sql"
    
    # Backup dos uploads
    docker-compose exec -T backend tar -czf - /app/uploads > "$BACKUP_DIR/uploads_$DATE.tar.gz"
    
    echo "✅ Backup criado com sucesso!"
    echo "📁 Arquivos salvos em: $BACKUP_DIR/"
    echo "   - gestaotcc_db_$DATE.sql"
    echo "   - uploads_$DATE.tar.gz"
}

# Função para restaurar backup
restore_backup() {
    if [ -z "$1" ]; then
        echo "❌ Por favor, especifique o arquivo de backup:"
        echo "   ./backup-restore.sh restore gestaotcc_db_YYYYMMDD_HHMMSS.sql"
        exit 1
    fi
    
    BACKUP_FILE="$1"
    
    if [ ! -f "$BACKUP_DIR/$BACKUP_FILE" ]; then
        echo "❌ Arquivo de backup não encontrado: $BACKUP_DIR/$BACKUP_FILE"
        exit 1
    fi
    
    echo "🔄 Restaurando backup: $BACKUP_FILE"
    
    # Restaurar banco de dados
    docker-compose exec -T postgres psql -U postgres -d gestaotcc_db -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
    docker-compose exec -T postgres psql -U postgres -d gestaotcc_db < "$BACKUP_DIR/$BACKUP_FILE"
    
    echo "✅ Backup restaurado com sucesso!"
}

# Função para listar backups
list_backups() {
    echo "📋 Backups disponíveis:"
    if [ -d "$BACKUP_DIR" ]; then
        ls -la "$BACKUP_DIR"/*.sql 2>/dev/null || echo "   Nenhum backup encontrado"
    else
        echo "   Diretório de backup não existe"
    fi
}

# Função para limpar backups antigos
cleanup_backups() {
    echo "🧹 Limpando backups antigos (mais de 30 dias)..."
    find "$BACKUP_DIR" -name "*.sql" -mtime +30 -delete
    find "$BACKUP_DIR" -name "*.tar.gz" -mtime +30 -delete
    echo "✅ Limpeza concluída!"
}

# Menu principal
case "$1" in
    "backup")
        create_backup
        ;;
    "restore")
        restore_backup "$2"
        ;;
    "list")
        list_backups
        ;;
    "cleanup")
        cleanup_backups
        ;;
    *)
        echo "Uso: $0 {backup|restore|list|cleanup}"
        echo ""
        echo "Comandos disponíveis:"
        echo "  backup   - Criar backup do banco de dados e uploads"
        echo "  restore  - Restaurar backup do banco de dados"
        echo "  list     - Listar backups disponíveis"
        echo "  cleanup  - Limpar backups antigos"
        echo ""
        echo "Exemplos:"
        echo "  $0 backup"
        echo "  $0 restore gestaotcc_db_20240101_120000.sql"
        echo "  $0 list"
        echo "  $0 cleanup"
        exit 1
        ;;
esac
