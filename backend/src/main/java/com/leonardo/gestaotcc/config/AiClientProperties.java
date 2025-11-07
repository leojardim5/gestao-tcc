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

    private boolean enabled = false;
    private String apiKey;
    private String apiUrl = "https://api.cursor.sh/v1/chat/completions";
    private String model = "cursor-small-latest";
    private double temperature = 0.2;
    private int maxTokens = 800;
}

