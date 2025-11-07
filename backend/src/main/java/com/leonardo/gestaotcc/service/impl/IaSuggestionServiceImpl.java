package com.leonardo.gestaotcc.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardo.gestaotcc.config.AiClientProperties;
import com.leonardo.gestaotcc.dto.ia.AiSuggestionDto;
import com.leonardo.gestaotcc.entity.Usuario;
import com.leonardo.gestaotcc.enums.PapelUsuario;
import com.leonardo.gestaotcc.repository.UsuarioRepository;
import com.leonardo.gestaotcc.service.IaSuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestTemplateBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IaSuggestionServiceImpl implements IaSuggestionService {

    private final AiClientProperties properties;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplateBuilder restTemplateBuilder;

    @Override
    public AiSuggestionDto.SuggestionResponse sugerirOrientadores(AiSuggestionDto.SuggestionRequest request) {
        List<Usuario> orientadoresDisponiveis = usuarioRepository.findByPapelAndDisponivelParaOrientacaoTrue(
                PapelUsuario.ORIENTADOR
        );

        if (!properties.isEnabled() || properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            log.warn("IA desativada ou sem API key configurada. Utilizando fallback.");
            return buildFallbackResponse(orientadoresDisponiveis, "IA desativada. Retorno baseado em ordenação simples por nome.");
        }

        try {
            String prompt = buildPrompt(request, orientadoresDisponiveis);

            CursorRequest payload = CursorRequest.builder()
                    .model(properties.getModel())
                    .temperature(properties.getTemperature())
                    .maxTokens(properties.getMaxTokens())
                    .messages(List.of(
                            Map.of("role", "system", "content", buildSystemMessage()),
                            Map.of("role", "user", "content", prompt)
                    ))
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getApiKey());

            RestTemplate restTemplate = restTemplateBuilder
                    .setConnectTimeout(Duration.ofSeconds(10))
                    .setReadTimeout(Duration.ofSeconds(30))
                    .build();

            ResponseEntity<String> response = restTemplate.exchange(
                    properties.getApiUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(payload, headers),
                    String.class
            );

            if (response.getBody() == null) {
                log.warn("Resposta vazia da IA. Aplicando fallback.");
                return buildFallbackResponse(orientadoresDisponiveis, "IA retornou resposta vazia. Usando fallback.");
            }

            return parseAiResponse(response.getBody(), orientadoresDisponiveis);
        } catch (Exception ex) {
            log.error("Erro ao consultar IA para sugestões de orientadores", ex);
            return buildFallbackResponse(orientadoresDisponiveis, "Falha ao consultar IA. Resultado gerado localmente.");
        }
    }

    private AiSuggestionDto.SuggestionResponse parseAiResponse(String responseBody, List<Usuario> orientadoresDisponiveis) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                return buildFallbackResponse(orientadoresDisponiveis, "IA não retornou sugestões. Fallback aplicado.");
            }

            JsonNode firstMessage = choices.get(0).path("message").path("content");
            if (firstMessage.isMissingNode()) {
                return buildFallbackResponse(orientadoresDisponiveis, "Conteúdo de mensagem ausente na resposta da IA.");
            }

            String content = firstMessage.asText();
            // IA deve responder com JSON. Se vier texto normal, tentamos extrair bloco JSON
            String jsonPayload = extractJsonFromContent(content);

            List<AiSuggestionDto.AiSuggestion> sugestoes = objectMapper.readValue(
                    jsonPayload,
                    new TypeReference<List<AiSuggestionResponseItem>>() {
                    }
            ).stream()
                    .map(item -> mapToSuggestion(item, orientadoresDisponiveis))
                    .filter(s -> s.getOrientadorId() != null)
                    .sorted(Comparator.comparingDouble(AiSuggestionDto.AiSuggestion::getScore).reversed())
                    .collect(Collectors.toList());

            if (sugestoes.isEmpty()) {
                return buildFallbackResponse(orientadoresDisponiveis, "IA respondeu sem sugestões válidas. Fallback aplicado.");
            }

            return AiSuggestionDto.SuggestionResponse.builder()
                    .sugestoes(sugestoes)
                    .modelo(properties.getModel())
                    .mensagemSistema("Sugestões geradas pela IA do Cursor.")
                    .build();
        } catch (Exception ex) {
            log.error("Falha ao interpretar resposta da IA", ex);
            return buildFallbackResponse(orientadoresDisponiveis, "Falha ao interpretar resposta da IA. Resultado local.");
        }
    }

    private String extractJsonFromContent(String content) {
        int firstBrace = content.indexOf('[');
        int lastBrace = content.lastIndexOf(']');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return content.substring(firstBrace, lastBrace + 1);
        }
        return content;
    }

    private AiSuggestionDto.AiSuggestion mapToSuggestion(AiSuggestionResponseItem item, List<Usuario> orientadoresDisponiveis) {
        Usuario orientador = orientadoresDisponiveis.stream()
                .filter(o -> o.getId().toString().equalsIgnoreCase(item.orientadorId()) ||
                        normalizar(o.getNome()).equalsIgnoreCase(normalizar(item.orientadorNome())))
                .findFirst()
                .orElse(null);

        if (orientador == null) {
            return AiSuggestionDto.AiSuggestion.builder().build();
        }

        double score = item.score() != null ? item.score() : 0.0;

        return AiSuggestionDto.AiSuggestion.builder()
                .orientadorId(orientador.getId())
                .orientadorNome(orientador.getNome())
                .score(Math.max(0, Math.min(100, score)))
                .justificativa(item.justificativa() != null ? item.justificativa() : "Compatibilidade identificada pela IA.")
                .destaques(item.destaques())
                .build();
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.toLowerCase(Locale.ROOT).trim();
    }

    private AiSuggestionDto.SuggestionResponse buildFallbackResponse(List<Usuario> orientadores, String mensagem) {
        List<AiSuggestionDto.AiSuggestion> sugestoes = orientadores.stream()
                .sorted(Comparator.comparing(Usuario::getNome))
                .map(o -> AiSuggestionDto.AiSuggestion.builder()
                        .orientadorId(o.getId())
                        .orientadorNome(o.getNome())
                        .score(50.0)
                        .justificativa("Sugestão baseada na disponibilidade e nome (fallback).")
                        .destaques(List.of("Disponível para orientação"))
                        .build())
                .collect(Collectors.toList());

        return AiSuggestionDto.SuggestionResponse.builder()
                .sugestoes(sugestoes)
                .modelo(properties.getModel())
                .mensagemSistema(mensagem)
                .build();
    }

    private String buildSystemMessage() {
        return String.join("\n",
                "Você é um assistente especializado em combinar alunos e orientadores para projetos de TCC.",
                "Analise os perfis dos orientadores, suas áreas de atuação e a descrição do TCC do aluno.",
                "Responda estritamente em JSON. Estrutura esperada: uma lista com objetos contendo os campos:",
                "[{ \"orientadorId\": \"uuid\", \"orientadorNome\": \"string\", \"score\": 0-100, \"justificativa\": \"string\", \"destaques\": [\"string\"] }].",
                "Inclua no máximo 5 orientadores mais compatíveis.",
                "O campo orientadorId deve usar o UUID exato fornecido na lista de orientadores."
        );
    }

    private String buildPrompt(AiSuggestionDto.SuggestionRequest request, List<Usuario> orientadores) {
        StringBuilder builder = new StringBuilder();
        builder.append("Dados do aluno e TCC:\n");
        builder.append(String.format("Aluno: %s (%s)\n", request.getAlunoNome(), request.getAlunoId()));
        builder.append(String.format("Curso: %s\n", request.getCurso()));
        builder.append(String.format("Título: %s\n", request.getTitulo()));
        builder.append(String.format("Tema: %s\n", request.getTema()));
        if (request.getResumo() != null) {
            builder.append(String.format("Resumo: %s\n", request.getResumo()));
        }
        if (request.getMensagem() != null) {
            builder.append(String.format("Mensagem ao orientador: %s\n", request.getMensagem()));
        }
        if (!CollectionUtils.isEmpty(request.getPalavrasChave())) {
            builder.append(String.format("Palavras-chave: %s\n", String.join(", ", request.getPalavrasChave())));
        }

        builder.append("\nOrientadores disponíveis:\n");
        for (Usuario orientador : orientadores) {
            builder.append(String.format("- ID: %s\n", orientador.getId()));
            builder.append(String.format("  Nome: %s\n", orientador.getNome()));
            builder.append(String.format("  Email: %s\n", orientador.getEmail()));
            builder.append(String.format("  Perfil: %s\n", orientador.getPerfilOrientador() != null ? orientador.getPerfilOrientador() : "Sem descrição"));
            builder.append("  Áreas destacadas: ");
            builder.append(extrairAreas(orientador.getPerfilOrientador()));
            builder.append("\n");
        }

        builder.append("\nRetorne apenas JSON conforme especificado.");
        return builder.toString();
    }

    private String extrairAreas(String perfil) {
        if (perfil == null || perfil.isBlank()) {
            return "não informado";
        }
        String[] tokens = perfil.split("[,;\\n]");
        List<String> palavras = new ArrayList<>();
        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.length() > 2 && trimmed.length() <= 40) {
                palavras.add(trimmed);
            }
            if (palavras.size() >= 6) {
                break;
            }
        }
        return palavras.isEmpty() ? "não informado" : String.join(", ", palavras);
    }

    private record AiSuggestionResponseItem(String orientadorId,
                                            String orientadorNome,
                                            Double score,
                                            String justificativa,
                                            List<String> destaques) {
    }

    @lombok.Builder
    private record CursorRequest(String model,
                                 double temperature,
                                 @com.fasterxml.jackson.annotation.JsonProperty("max_tokens")
                                 int maxTokens,
                                 List<Map<String, Object>> messages) {
    }
}

