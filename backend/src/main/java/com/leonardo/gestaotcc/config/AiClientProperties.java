package com.leonardo.gestaotcc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cursor.ai")
public class AiClientProperties {

    public enum Provider {
        OPENAI,
        GEMINI
    }

    private boolean enabled = false;
    private String apiKey;
    private String apiUrl = "https://api.openai.com/v1/chat/completions";
    private String model = "gpt-4o-mini";
    private double temperature = 0.2;
    private int maxTokens = 800;
    private Provider provider = Provider.OPENAI;

    public String resolveApiUrl() {
        if (provider == Provider.GEMINI) {
            if (apiUrl == null || apiUrl.contains("api.openai.com")) {
                return "https://generativelanguage.googleapis.com/v1beta/models";
            }
        }
        return apiUrl;
    }
}

