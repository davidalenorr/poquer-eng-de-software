package com.poquer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado da entrada de um jogador em uma partida")
public record EntradaPartidaResponse(
    @Schema(description = "Indica se o jogador entrou na partida", example = "true")
    boolean sucesso,

    @Schema(description = "Mensagem com o resultado da operação", example = "Jogador entrou na partida com sucesso.")
    String mensagem,

    @Schema(description = "Identificador da partida", example = "1")
    Long idPartida,

    @Schema(description = "Identificador do jogador", example = "2")
    Long idJogador
) {}
