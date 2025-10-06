package com.leonardo.gestaotcc.entity;

import com.leonardo.gestaotcc.enums.TipoReuniao;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "reunioes")
public class Reuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tcc_id", nullable = false)
    private Tcc tcc;

    @Column(nullable = false)
    private Instant dataHora;

    @Column(nullable = false)
    private String tema;

    @Lob
    private String resumo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoReuniao tipo;

    public Reuniao() {
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

    public Instant getDataHora() {
        return dataHora;
    }

    public void setDataHora(Instant dataHora) {
        this.dataHora = dataHora;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public TipoReuniao getTipo() {
        return tipo;
    }

    public void setTipo(TipoReuniao tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reuniao reuniao = (Reuniao) o;
        return Objects.equals(id, reuniao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reuniao{" +
                "id=" + id +
                ", tcc=" + tcc.getId() +
                ", dataHora=" + dataHora +
                ", tema='" + tema + "'" +
                '}';
    }
}