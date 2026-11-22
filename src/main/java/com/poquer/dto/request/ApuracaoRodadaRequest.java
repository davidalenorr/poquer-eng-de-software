package com.poquer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "Cinco cartas comunitárias finais, enquanto flop, turn e river não estão implementados")
public record ApuracaoRodadaRequest(
    @NotNull(message = "As cartas comunitárias são obrigatórias.")
    @Size(min = 5, max = 5, message = "Informe exatamente cinco cartas comunitárias.")
    List<@NotNull @Valid CartaRequest> cartasComunitarias
) {}
