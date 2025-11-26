package com.leonardo.gestaotcc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationData {
    private String tituloTcc;
    private String curso;
    private String tema;
    private String resumo;
    private String nomeAluno;
    private String nomeOrientador;
    private String emailAluno;
    private String emailOrientador;
    private LocalDateTime dataHora;
    private String mensagemPersonalizada;
    private String status;
    private Integer versao;
    private Map<String, Object> dadosExtras;
}

