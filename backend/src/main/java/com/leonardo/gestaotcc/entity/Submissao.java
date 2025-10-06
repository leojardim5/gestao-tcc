package com.leonardo.gestaotcc.entity;

import com.leonardo.gestaotcc.enums.StatusSubmissao;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "submissoes")
public class Submissao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tcc_id", nullable = false)
    private Tcc tcc;

    @Column(nullable = false)
    private Integer versao;

    @Column(nullable = false)
    private String arquivoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSubmissao status;

    @CreationTimestamp
    private LocalDateTime enviadoEm;

    public Submissao() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Tcc getTcc() {
        return tcc;
    }

    public void setTcc(Tcc tcc) {
        this.tcc = tcc;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public String getArquivoUrl() {
        return arquivoUrl;
    }

    public void setArquivoUrl(String arquivoUrl) {
        this.arquivoUrl = arquivoUrl;
    }

    public StatusSubmissao getStatus() {
        return status;
    }

    public void setStatus(StatusSubmissao status) {
        this.status = status;
    }

    public LocalDateTime getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(LocalDateTime enviadoEm) {
        this.enviadoEm = enviadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Submissao submissao = (Submissao) o;
        return Objects.equals(id, submissao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Submissao{" +
                "id=" + id +
                ", tcc=" + tcc.getId() +
                ", versao=" + versao +
                ", status=" + status +
                '}';
    }
}