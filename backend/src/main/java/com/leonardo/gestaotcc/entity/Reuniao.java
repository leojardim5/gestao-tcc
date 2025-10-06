package com.leonardo.gestaotcc.entity;

import com.leonardo.gestaotcc.enums.TipoReuniao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reunioes")
public class Reuniao {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}