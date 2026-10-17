package com.poquer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informações de uma partida disponível para entrada")
public record PartidaAbertaResponse(
    @Schema(description = "Identificador único da partida", example = "1")
    Long id,

    @Schema(description = "Nome da mesa de pôquer", example = "Mesa 1")
    String nome,

    @Schema(description = "Número de jogadores presentes na partida", example = "2")
    int numeroJogadoresPresentes,

    @Schema(description = "Número máximo de participantes", example = "8")
    int maxJogadores
) {}
