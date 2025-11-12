package com.leonardo.gestaotcc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "workspace.google")
public class GoogleWorkspaceProperties {
    /**
     * Habilita a integração com o Google Docs.
     */
    private boolean enabled = false;

    /**
     * Caminho para o arquivo JSON da service account.
     */
    private String credentialsPath;

    /**
     * Conteúdo do JSON da service account em formato string (útil para variáveis de ambiente).
     */
    private String credentialsJson;

    /**
     * Usuário a ser impersonado (delegação) quando necessário.
     */
    private String impersonatedUser;
}

