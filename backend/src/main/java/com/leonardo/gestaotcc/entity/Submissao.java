package com.leonardo.gestaotcc.entity;

import com.leonardo.gestaotcc.enums.StatusSubmissao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "submissoes")
public class Submissao {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
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
    @Column(name = "enviado_em", updatable = false)
    private LocalDateTime enviadoEm;
}
