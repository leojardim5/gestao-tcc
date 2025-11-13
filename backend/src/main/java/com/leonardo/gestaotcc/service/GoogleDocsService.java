package com.leonardo.gestaotcc.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.About.StorageQuota;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.leonardo.gestaotcc.config.GoogleWorkspaceProperties;
import com.leonardo.gestaotcc.entity.Tcc;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.model.Comment;
import com.google.api.services.drive.model.CommentList;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import com.google.api.client.util.DateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDocsService {

    private static final String APPLICATION_NAME = "Gestao TCC Workspace";
    private static final String DOC_MIME_TYPE = "application/vnd.google-apps.document";
    private static final String DOC_VIEW_TEMPLATE = "https://docs.google.com/document/d/%s/view";
    private static final String DOC_EDIT_TEMPLATE = "https://docs.google.com/document/d/%s/edit";

    private final GoogleWorkspaceProperties properties;

    private Drive driveClient;

    @PostConstruct
    void init() {
        if (!properties.isEnabled()) {
            log.info("[GoogleDocs] Integração desabilitada.");
            return;
        }
        try {
            var transport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredentials credentials = loadCredentials();
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            if (credentials instanceof ServiceAccountCredentials sac) {
                log.info("[GoogleDocs] Usando service account {}", sac.getClientEmail());
            } else if (credentials instanceof UserCredentials uc) {
                log.info("[GoogleDocs] Usando OAuth com clientId {}", properties.getOauthClientId());
            }

            driveClient = new Drive.Builder(
                    transport,
                    GsonFactory.getDefaultInstance(),
                    requestInitializer
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            log.info("[GoogleDocs] Integração inicializada com sucesso.");
        } catch (IOException | GeneralSecurityException e) {
            log.error("[GoogleDocs] Falha ao inicializar integração com Google Docs: {}", e.getMessage(), e);
            driveClient = null;
        }
    }

    public Optional<GoogleDocMetadata> criarDocumento(String titulo, List<String> editores) {
        if (!properties.isEnabled() || driveClient == null) {
            return Optional.empty();
        }

        try {
            File fileMetadata = new File()
                    .setName(titulo)
                    .setMimeType(DOC_MIME_TYPE);

            if (StringUtils.hasText(properties.getParentFolderId())) {
                fileMetadata.setParents(List.of(properties.getParentFolderId()));
            }

            File createdFile = driveClient.files()
                    .create(fileMetadata)
                    .setFields("id, webViewLink")
                    .setSupportsAllDrives(true)
                    .execute();

            if (createdFile == null || !StringUtils.hasText(createdFile.getId())) {
                log.error("[GoogleDocs] Arquivo não criado para o título '{}'.", titulo);
                return Optional.empty();
            }

            String documentId = createdFile.getId();

            concederPermissoes(documentId, editores);

            String webView = StringUtils.hasText(createdFile.getWebViewLink())
                    ? createdFile.getWebViewLink()
                    : DOC_VIEW_TEMPLATE.formatted(documentId);
            String webEdit = DOC_EDIT_TEMPLATE.formatted(documentId);

            return Optional.of(new GoogleDocMetadata(documentId, webView, webEdit));
        } catch (GoogleJsonResponseException apiException) {
            log.error("[GoogleDocs] Erro do Google API ao criar documento '{}': {}", titulo,
                    apiException.getDetails() != null ? apiException.getDetails().getMessage() : apiException.getMessage());
            return Optional.empty();
        } catch (IOException e) {
            log.error("[GoogleDocs] Erro ao criar documento '{}': {}", titulo, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<GoogleDocMetadata> criarDocumentoParaTcc(Tcc tcc) {
        if (tcc == null || tcc.getAluno() == null || tcc.getOrientador() == null) {
            log.warn("[GoogleDocs] TCC, aluno ou orientador ausente. TCC: {}, Aluno: {}, Orientador: {}", 
                    tcc != null ? tcc.getId() : "null",
                    tcc != null && tcc.getAluno() != null ? tcc.getAluno().getEmail() : "null",
                    tcc != null && tcc.getOrientador() != null ? tcc.getOrientador().getEmail() : "null");
            return Optional.empty();
        }
        String titulo = String.format("TCC - %s - %s", tcc.getAluno().getNome(), tcc.getTitulo());
        List<String> editores = new java.util.ArrayList<>();

        if (tcc.getAluno() != null && StringUtils.hasText(tcc.getAluno().getEmail())) {
            editores.add(tcc.getAluno().getEmail());
            log.info("[GoogleDocs] Adicionando aluno '{}' como editor do documento.", tcc.getAluno().getEmail());
        }
        if (tcc.getOrientador() != null && StringUtils.hasText(tcc.getOrientador().getEmail())) {
            editores.add(tcc.getOrientador().getEmail());
            log.info("[GoogleDocs] Adicionando orientador '{}' como editor do documento.", tcc.getOrientador().getEmail());
        }
        if (tcc.getCoorientador() != null && StringUtils.hasText(tcc.getCoorientador().getEmail())) {
            editores.add(tcc.getCoorientador().getEmail());
            log.info("[GoogleDocs] Adicionando coorientador '{}' como editor do documento.", tcc.getCoorientador().getEmail());
        }

        if (editores.isEmpty()) {
            log.warn("[GoogleDocs] Nenhum editor encontrado para o documento '{}'.", titulo);
            return Optional.empty();
        }

        log.info("[GoogleDocs] Criando documento '{}' com {} editores: {}", titulo, editores.size(), editores);

        if (!possuiEspacoDisponivel()) {
            log.warn("[GoogleDocs] Quota de armazenamento excedida. Documento '{}' não será criado.", titulo);
            return Optional.empty();
        }

        return criarDocumento(titulo, editores);
    }

    private void concederPermissoes(String documentId, List<String> editores) {
        Set<String> destinatarios = new LinkedHashSet<>();

        if (editores != null) {
            editores.stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .forEach(destinatarios::add);
        }

        if (StringUtils.hasText(properties.getFallbackEditorEmail())) {
            destinatarios.add(properties.getFallbackEditorEmail().trim());
        }

        if (destinatarios.isEmpty()) {
            log.warn("[GoogleDocs] Nenhum destinatário informado para conceder permissão ao documento {}.", documentId);
            return;
        }

        for (String email : destinatarios) {
            try {
                Permission permission = new Permission()
                        .setType("user")
                        .setRole("writer")
                        .setEmailAddress(email);

                driveClient.permissions()
                        .create(documentId, permission)
                        .setSendNotificationEmail(false)
                        .setSupportsAllDrives(true)
                        .execute();

                log.info("[GoogleDocs] Permissão de edição concedida para '{}' no documento {}.", email, documentId);
            } catch (IOException permissionException) {
                log.warn("[GoogleDocs] Falha ao conceder acesso para '{}': {}", email, permissionException.getMessage());
            }
        }
    }

    private boolean possuiEspacoDisponivel() {
        if (!properties.isEnabled() || driveClient == null) {
            return false;
        }
        try {
            About about = driveClient.about().get().setFields("storageQuota").execute();
            StorageQuota quota = about.getStorageQuota();
            if (quota == null) {
                log.info("[GoogleDocs] Quota não informada pelo Drive, assumindo disponível.");
                return true;
            }
            Long limit = quota.getLimit();
            Long usage = quota.getUsage();
            Long usageInDrive = quota.getUsageInDrive();
            log.info("[GoogleDocs] Quota atual - limit: {} bytes, usage: {} bytes, usageInDrive: {} bytes", limit, usage, usageInDrive);
            if (limit == null || limit == 0L) {
                return true;
            }
            long used = usageInDrive != null ? usageInDrive : (usage != null ? usage : 0L);
            boolean disponivel = used < limit;
            if (!disponivel) {
                log.warn("[GoogleDocs] Quota excedida: usado={} / limite={}", used, limit);
            }
            return disponivel;
        } catch (IOException e) {
            log.warn("[GoogleDocs] Falha ao consultar quota: {}", e.getMessage());
            return false;
        }
    }

    public List<GoogleDocComment> listarComentarios(String fileId) {
        if (!properties.isEnabled() || driveClient == null || !StringUtils.hasText(fileId)) {
            return List.of();
        }

        try {
            CommentList response = driveClient.comments()
                    .list(fileId)
                    .setFields("comments(commentId,content,createdTime,modifiedTime,resolved,author/displayName,author/photoLink)")
                    .setIncludeDeleted(false)
                    .setPageSize(50)
                    .execute();

            if (response == null || response.getComments() == null) {
                return List.of();
            }

            List<Comment> comentarios = response.getComments();
            return comentarios.stream()
                    .map(comment -> new GoogleDocComment(
                            comment.getId(),
                            comment.getAuthor() != null ? comment.getAuthor().getDisplayName() : "",
                            comment.getAuthor() != null ? comment.getAuthor().getPhotoLink() : null,
                            comment.getContent(),
                            Boolean.TRUE.equals(comment.getResolved()),
                            parseDateTime(comment.getCreatedTime()),
                            parseDateTime(comment.getModifiedTime())
                    ))
                    .toList();
        } catch (IOException e) {
            log.error("[GoogleDocs] Erro ao listar comentários do arquivo {}: {}", fileId, e.getMessage(), e);
            return List.of();
        }
    }

    private LocalDateTime parseDateTime(DateTime value) {
        if (value == null) {
            return null;
        }
        return LocalDateTime.ofInstant(java.time.Instant.parse(value.toStringRfc3339()), ZoneOffset.UTC);
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

        if (inputStream != null) {
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

        if (StringUtils.hasText(properties.getOauthClientId())
                && StringUtils.hasText(properties.getOauthClientSecret())
                && StringUtils.hasText(properties.getOauthRefreshToken())) {
            return UserCredentials.newBuilder()
                    .setClientId(properties.getOauthClientId())
                    .setClientSecret(properties.getOauthClientSecret())
                    .setRefreshToken(properties.getOauthRefreshToken())
                    .build();
        }

        throw new IOException("Credenciais do Google não configuradas.");
    }

    public record GoogleDocMetadata(String fileId, String webViewLink, String webEditLink) {
    }

    public record GoogleDocComment(
            String id,
            String authorName,
            String authorPhotoUrl,
            String content,
            boolean resolved,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}

