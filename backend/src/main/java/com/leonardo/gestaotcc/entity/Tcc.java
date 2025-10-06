package com.leonardo.gestaotcc.entity;

import com.leonardo.gestaotcc.enums.StatusTcc;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

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

    @Lob
    private String resumo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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