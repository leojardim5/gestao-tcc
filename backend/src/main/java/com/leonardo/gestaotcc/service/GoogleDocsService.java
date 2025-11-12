package com.leonardo.gestaotcc.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.leonardo.gestaotcc.config.GoogleWorkspaceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDocsService {

    private static final String APPLICATION_NAME = "Gestao TCC Workspace";

    private final GoogleWorkspaceProperties properties;

    private Docs docsClient;
    private Drive driveClient;

    @PostConstruct
    void init() {
        if (!properties.isEnabled()) {
            log.info("[GoogleDocs] Integração desabilitada.");
            return;
        }
        try {
            GoogleCredentials credentials = loadCredentials();
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            docsClient = new Docs.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            driveClient = new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            log.info("[GoogleDocs] Integração inicializada com sucesso.");
        } catch (IOException | GeneralSecurityException e) {
            log.error("[GoogleDocs] Falha ao inicializar integração com Google Docs: {}", e.getMessage(), e);
            docsClient = null;
            driveClient = null;
        }
    }

    public Optional<GoogleDocMetadata> criarDocumento(String titulo, List<String> editores) {
        if (!properties.isEnabled() || docsClient == null || driveClient == null) {
            return Optional.empty();
        }

        try {
            Document documento = docsClient.documents()
                    .create(new Document().setTitle(titulo))
                    .execute();

            String documentId = documento.getDocumentId();

            if (StringUtils.hasText(documentId) && editores != null) {
                for (String email : editores) {
                    if (!StringUtils.hasText(email)) {
                        continue;
                    }
                    Permission permission = new Permission()
                            .setType("user")
                            .setRole("writer")
                            .setEmailAddress(email);
                    driveClient.permissions()
                            .create(documentId, permission)
                            .setSendNotificationEmail(false)
                            .execute();
                }
            }

            String webView = String.format("https://docs.google.com/document/d/%s/view", documentId);
            String webEdit = String.format("https://docs.google.com/document/d/%s/edit", documentId);

            return Optional.of(new GoogleDocMetadata(documentId, webView, webEdit));
        } catch (IOException e) {
            log.error("[GoogleDocs] Erro ao criar documento '{}': {}", titulo, e.getMessage(), e);
            return Optional.empty();
        }
    }

    private GoogleCredentials loadCredentials() throws IOException {
        InputStream inputStream = null;
        if (StringUtils.hasText(properties.getCredentialsPath())) {
            inputStream = new FileInputStream(properties.getCredentialsPath());
        } else if (StringUtils.hasText(properties.getCredentialsJson())) {
            String json = properties.getCredentialsJson().trim();
            if (!json.startsWith("{")) {
                byte[] decoded = Base64.getDecoder().decode(json);
                inputStream = new ByteArrayInputStream(decoded);
            } else {
                inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            }
        }

        if (inputStream == null) {
            throw new IOException("Credenciais do Google não configuradas.");
        }

        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(inputStream)
                .createScoped(List.of(
                        "https://www.googleapis.com/auth/drive",
                        "https://www.googleapis.com/auth/drive.file",
                        "https://www.googleapis.com/auth/documents"
                ));

        if (credentials instanceof ServiceAccountCredentials sac && StringUtils.hasText(properties.getImpersonatedUser())) {
            return sac.createDelegated(properties.getImpersonatedUser());
        }
        return credentials;
    }

    public record GoogleDocMetadata(String fileId, String webViewLink, String webEditLink) {
    }
}

