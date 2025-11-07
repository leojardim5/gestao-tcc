package com.leonardo.gestaotcc.entity;

import com.leonardo.gestaotcc.enums.StatusTcc;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tccs")
public class Tcc {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String tema;

    @Column(nullable = false)
    private String curso;

    @Column(columnDefinition = "TEXT")
    private String resumo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "status_tcc")
    private StatusTcc status;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_entrega_prevista")
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
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}