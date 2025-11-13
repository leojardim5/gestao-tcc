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
     * Pasta onde os documentos serão criados. Quando vazio, o arquivo fica no drive raiz da service account.
     */
    private String parentFolderId;

    /**
     * Usuário a ser impersonado (delegação) quando necessário.
     */
    private String impersonatedUser;

    /**
     * E-mail de fallback para receber acesso caso algum endereço fornecido não seja válido.
     */
    private String fallbackEditorEmail;

    /**
     * OAuth Client ID para autenticação baseada em usuário (quando não se usa service account).
     */
    private String oauthClientId;

    /**
     * OAuth Client Secret para autenticação baseada em usuário.
     */
    private String oauthClientSecret;

    /**
     * Refresh token obtido para o usuário que concedeu acesso ao Drive.
     */
    private String oauthRefreshToken;
}

