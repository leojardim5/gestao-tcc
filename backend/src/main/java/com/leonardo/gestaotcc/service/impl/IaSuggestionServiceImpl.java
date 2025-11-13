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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
            return buildFallbackResponse(orientadoresDisponiveis, "IA desativada. Sugerindo orientadores com base em aderência ao perfil.", request);
        }

        try {
            String prompt = buildPrompt(request, orientadoresDisponiveis);

            AiContentResult aiResult = switch (properties.getProvider()) {
                case GEMINI -> callGemini(prompt);
                case OPENAI -> callOpenAi(prompt);
            };

            if (aiResult == null || aiResult.content() == null || aiResult.content().isBlank()) {
                log.warn("Conteúdo vazio retornado pelo provedor IA. Aplicando fallback.");
                return buildFallbackResponse(orientadoresDisponiveis, "IA retornou resposta vazia. Utilizando heurística local.", request);
            }

            return parseAiContent(aiResult, orientadoresDisponiveis, request);
        } catch (Exception ex) {
            log.error("Erro ao consultar IA para sugestões de orientadores", ex);
            return buildFallbackResponse(orientadoresDisponiveis, "Falha ao consultar IA. Utilizando heurística local.", request);
        }
    }

    private AiContentResult callOpenAi(String prompt) throws Exception {
        String model = properties.getModel();
        OpenAiRequest payload = OpenAiRequest.builder()
                .model(model)
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
                properties.resolveApiUrl(),
                HttpMethod.POST,
                new HttpEntity<>(payload, headers),
                String.class
        );

        if (response.getBody() == null) {
            return null;
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            return null;
        }
        JsonNode firstMessage = choices.get(0).path("message").path("content");
        if (firstMessage.isMissingNode()) {
            return null;
        }
        String content = firstMessage.asText();
        String mensagem = "Sugestões geradas via OpenAI (" + model + ").";
        return new AiContentResult(content, mensagem, model);
    }

    private AiContentResult callGemini(String prompt) throws Exception {
        String baseUrl = properties.resolveApiUrl();
        String primaryModel = properties.getModel() != null ? properties.getModel() : "gemini-pro";
        return callGeminiWithModel(prompt, baseUrl, primaryModel, true);
    }

    private AiContentResult callGeminiWithModel(String prompt,
                                               String baseUrl,
                                               String model,
                                               boolean allowFallback) throws Exception {
        String url = buildGeminiUrl(baseUrl, model, properties.getApiKey());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("contents", List.of(
                Map.of("parts", List.of(
                        Map.of("text", buildSystemMessage() + "\n\n" + prompt)
                ))
        ));
        payload.put("generationConfig", Map.of(
                "temperature", properties.getTemperature(),
                "maxOutputTokens", properties.getMaxTokens(),
                "responseMimeType", "application/json"
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(payload, headers),
                    String.class
            );

            if (response.getBody() == null) {
                return null;
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                return null;
            }
            JsonNode firstCandidate = candidates.get(0).path("content").path("parts");
            if (!firstCandidate.isArray() || firstCandidate.isEmpty()) {
                return null;
            }

            StringBuilder builder = new StringBuilder();
            for (JsonNode part : firstCandidate) {
                String text = part.path("text").asText();
                if (text != null) {
                    builder.append(text);
                }
            }
            String mensagem = "Sugestões geradas via Gemini (" + model + ").";
            return new AiContentResult(builder.toString(), mensagem, model);
        } catch (HttpClientErrorException.NotFound notFound) {
            if (allowFallback) {
                String fallbackModel = "gemini-pro";
                log.warn("Modelo '{}' não encontrado na API Gemini. Tentando fallback para '{}'.", model, fallbackModel);
                return callGeminiWithModel(prompt, baseUrl, fallbackModel, false);
            }
            throw notFound;
        }
    }

    private String buildGeminiUrl(String baseUrl, String model, String apiKey) {
        String sanitizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        if (!sanitizedBase.endsWith("/models")) {
            sanitizedBase = sanitizedBase + "/models";
        }
        return UriComponentsBuilder.fromHttpUrl(sanitizedBase + "/" + model + ":generateContent")
                .queryParam("key", apiKey)
                .toUriString();
            }

    private AiSuggestionDto.SuggestionResponse parseAiContent(AiContentResult aiResult,
                                                              List<Usuario> orientadoresDisponiveis,
                                                              AiSuggestionDto.SuggestionRequest request) {
        try {
            String jsonPayload = extractJsonFromContent(aiResult.content());

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
                return buildFallbackResponse(orientadoresDisponiveis, "IA respondeu sem sugestões válidas. Utilizando heurística local.", request);
            }

            return AiSuggestionDto.SuggestionResponse.builder()
                    .sugestoes(sugestoes)
                    .modelo(aiResult.modelo())
                    .mensagemSistema(aiResult.mensagemSistema())
                    .build();
        } catch (Exception ex) {
            log.error("Falha ao interpretar resposta da IA", ex);
            return buildFallbackResponse(orientadoresDisponiveis, "Falha ao interpretar resposta da IA. Utilizando heurística local.", request);
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

    private AiSuggestionDto.SuggestionResponse buildFallbackResponse(List<Usuario> orientadores,
                                                                     String mensagem,
                                                                     AiSuggestionDto.SuggestionRequest request) {
        Map<String, String> tccKeywords = extractKeywordsFromRequest(request);

        List<AiSuggestionDto.AiSuggestion> sugestoes = orientadores.stream()
                .map(orientador -> buildHeuristicSuggestion(orientador, tccKeywords, request))
                .sorted(Comparator.comparingDouble(AiSuggestionDto.AiSuggestion::getScore).reversed())
                .collect(Collectors.toList());

        return AiSuggestionDto.SuggestionResponse.builder()
                .sugestoes(sugestoes)
                .modelo(properties.getModel())
                .mensagemSistema(mensagem)
                .build();
    }

    private AiSuggestionDto.AiSuggestion buildHeuristicSuggestion(Usuario orientador,
                                                                  Map<String, String> tccKeywords,
                                                                  AiSuggestionDto.SuggestionRequest request) {
        Set<String> orientadorTokens = tokenize(orientador.getPerfilOrientador());
        orientadorTokens.addAll(tokenize(orientador.getNome()));
        orientadorTokens.addAll(tokenize(orientador.getEmail()));

        List<String> matchedNormalized = new ArrayList<>();
        for (String keyword : tccKeywords.keySet()) {
            if (orientadorTokens.contains(keyword)) {
                matchedNormalized.add(keyword);
            }
        }

        int keywordCount = tccKeywords.size();
        double coverage = keywordCount == 0 ? 0 : (double) matchedNormalized.size() / keywordCount;

        double baseScore = 45.0;
        if (orientador.isDisponivelParaOrientacao()) {
            baseScore += 10;
        }
        if (orientador.getPerfilOrientador() != null && !orientador.getPerfilOrientador().isBlank()) {
            baseScore += 5;
        }

        double similarityBoost = coverage * 40; // até +40 pontos dependendo da cobertura
        double score = Math.min(95.0, Math.max(35.0, baseScore + similarityBoost));
        if (keywordCount == 0) {
            score = orientador.isDisponivelParaOrientacao() ? 65.0 : 55.0;
        }

        List<String> matchedTerms = matchedNormalized.stream()
                .map(tccKeywords::get)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        String justificativa = buildJustificativa(matchedTerms, keywordCount, orientador);
        List<String> destaques = buildHighlights(matchedTerms, orientador, request);

        return AiSuggestionDto.AiSuggestion.builder()
                .orientadorId(orientador.getId())
                .orientadorNome(orientador.getNome())
                .score(score)
                .justificativa(justificativa)
                .destaques(destaques)
                .build();
    }

    private String buildJustificativa(List<String> matchedTerms, int totalKeywords, Usuario orientador) {
        if (!matchedTerms.isEmpty()) {
            String termos = String.join(", ", matchedTerms);
            return String.format(
                    "Perfil do orientador menciona %s (%d de %d palavras-chave do TCC).",
                    termos,
                    matchedTerms.size(),
                    Math.max(totalKeywords, matchedTerms.size())
            );
        }
        if (orientador.getPerfilOrientador() != null && !orientador.getPerfilOrientador().isBlank()) {
            return "Perfil disponível, mas sem palavras-chave iguais ao TCC. Avalie se a experiência descrita se encaixa.";
        }
        return "Orientador sem perfil detalhado. Sugestão baseada na disponibilidade atual.";
    }

    private List<String> buildHighlights(List<String> matchedTerms,
                                         Usuario orientador,
                                         AiSuggestionDto.SuggestionRequest request) {
        List<String> destaques = new ArrayList<>();
        if (orientador.isDisponivelParaOrientacao()) {
            destaques.add("Disponível para orientação");
        }
        if (!matchedTerms.isEmpty()) {
            destaques.add("Palavras-chave em comum: " + String.join(", ", matchedTerms));
        }
        if (orientador.getPerfilOrientador() != null && !orientador.getPerfilOrientador().isBlank()) {
            destaques.add("Perfil: " + truncate(orientador.getPerfilOrientador(), 120));
        }
        if (request != null && request.getCurso() != null && !request.getCurso().isBlank()) {
            destaques.add("Curso do TCC: " + request.getCurso());
        }
        return destaques;
    }

    private String truncate(String value, int length) {
        if (value == null || value.length() <= length) {
            return value;
        }
        return value.substring(0, Math.max(0, length - 3)).trim() + "...";
    }

    private Map<String, String> extractKeywordsFromRequest(AiSuggestionDto.SuggestionRequest request) {
        Map<String, String> keywords = new LinkedHashMap<>();
        if (request == null) {
            return keywords;
        }

        addKeywordsFromText(keywords, request.getCurso());
        addKeywordsFromText(keywords, request.getTitulo());
        addKeywordsFromText(keywords, request.getTema());
        addKeywordsFromText(keywords, request.getResumo());
        addKeywordsFromText(keywords, request.getMensagem());

        if (request.getPalavrasChave() != null) {
            for (String palavra : request.getPalavrasChave()) {
                addKeyword(keywords, palavra);
            }
        }

        return keywords;
    }

    private void addKeywordsFromText(Map<String, String> keywords, String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        String[] tokens = text.split("[^\\p{L}\\p{N}]+");
        for (String token : tokens) {
            addKeyword(keywords, token);
        }
    }

    private void addKeyword(Map<String, String> keywords, String token) {
        if (token == null) {
            return;
        }
        String normalized = normalizeToken(token);
        if (normalized.length() < 3 || normalized.length() > 40) {
            return;
        }
        keywords.putIfAbsent(normalized, token.trim());
    }

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return new LinkedHashSet<>();
        }
        String[] tokens = text.split("[^\\p{L}\\p{N}]+");
        Set<String> result = new LinkedHashSet<>();
        for (String token : tokens) {
            String normalized = normalizeToken(token);
            if (normalized.length() >= 3 && normalized.length() <= 40) {
                result.add(normalized);
            }
        }
        return result;
    }

    private String normalizeToken(String token) {
        if (token == null) {
            return "";
        }
        String normalized = Normalizer.normalize(token, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();
        return normalized.replaceAll("[^\\p{L}\\p{N}]+", "");
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

    private record AiContentResult(String content, String mensagemSistema, String modelo) {
    }

    @lombok.Builder
    private record OpenAiRequest(String model,
                                 double temperature,
                                 @com.fasterxml.jackson.annotation.JsonProperty("max_tokens")
                                 int maxTokens,
                                 List<Map<String, Object>> messages) {
    }
}

