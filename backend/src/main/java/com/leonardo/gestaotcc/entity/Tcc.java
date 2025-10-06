package com.leonardo.gestaotcc.entity;

import com.leonardo.gestaotcc.enums.StatusTcc;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tccs")
public class Tcc {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Lob
    private String resumo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTcc status;

    private LocalDate dataInicio;

    private LocalDate dataEntregaPrevista;

    @OneToOne
    @JoinColumn(name = "aluno_id", referencedColumnName = "id", unique = true)
    private Usuario aluno;

    @ManyToOne
    @JoinColumn(name = "orientador_id", referencedColumnName = "id")
    private Usuario orientador;

    @ManyToOne
    @JoinColumn(name = "coorientador_id", referencedColumnName = "id")
    private Usuario coorientador;

    @CreationTimestamp
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;

    public Tcc() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public StatusTcc getStatus() {
        return status;
    }

    public void setStatus(StatusTcc status) {
        this.status = status;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataEntregaPrevista() {
        return dataEntregaPrevista;
    }

    public void setDataEntregaPrevista(LocalDate dataEntregaPrevista) {
        this.dataEntregaPrevista = dataEntregaPrevista;
    }

    public Usuario getAluno() {
        return aluno;
    }

    public void setAluno(Usuario aluno) {
        this.aluno = aluno;
    }

    public Usuario getOrientador() {
        return orientador;
    }

    public void setOrientador(Usuario orientador) {
        this.orientador = orientador;
    }

    public Usuario getCoorientador() {
        return coorientador;
    }

    public void setCoorientador(Usuario coorientador) {
        this.coorientador = coorientador;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tcc tcc = (Tcc) o;
        return Objects.equals(id, tcc.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tcc{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", status=" + status +
                '}';
    }
}