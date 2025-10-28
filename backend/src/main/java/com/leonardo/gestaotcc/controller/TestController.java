package com.leonardo.gestaotcc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/ping")
    public String ping() {
        return "Test Controller OK!";
    }

    @GetMapping("/fix-enum")
    public String fixEnum() {
        try {
            jdbcTemplate.execute("ALTER TYPE tipo_notificacao ADD VALUE 'CONVITE_ORIENTACAO';");
            return "Enum CONVITE_ORIENTACAO adicionado com sucesso!";
        } catch (Exception e) {
            return "Erro ou valor já existe: " + e.getMessage();
        }
    }

    @GetMapping("/check-enum")
    public String checkEnum() {
        try {
            var result = jdbcTemplate.queryForList("SELECT unnest(enum_range(NULL::tipo_notificacao)) as valores;");
            return "Valores do enum: " + result.toString();
        } catch (Exception e) {
            return "Erro ao verificar enum: " + e.getMessage();
        }
    }

    @GetMapping("/fix-status-enum")
    public String fixStatusEnum() {
        try {
            jdbcTemplate.execute("ALTER TYPE status_tcc ADD VALUE 'PENDENTE_APROVACAO';");
            return "Enum PENDENTE_APROVACAO adicionado ao status_tcc com sucesso!";
        } catch (Exception e) {
            return "Erro ou valor já existe: " + e.getMessage();
        }
    }

    @GetMapping("/check-status-enum")
    public String checkStatusEnum() {
        try {
            var result = jdbcTemplate.queryForList("SELECT unnest(enum_range(NULL::status_tcc)) as valores;");
            return "Valores do enum status_tcc: " + result.toString();
        } catch (Exception e) {
            return "Erro ao verificar enum: " + e.getMessage();
        }
    }
}
