package com.leonardo.gestaotcc.dto.cronograma;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CronogramaResumoDto {
    private UUID tccId;
    private long total;
    private long pendentes; // Inclui PENDENTE e ATRASADA
    private long emAndamento; // Etapas em progresso
   	private long concluidas; // Etapas concluídas
}

