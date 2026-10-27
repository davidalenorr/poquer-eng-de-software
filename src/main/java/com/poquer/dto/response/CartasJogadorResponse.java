package com.poquer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Cartas distribuídas para um jogador")
public record CartasJogadorResponse(
    @Schema(description = "Identificador único do jogador", example = "1")
    Long idJogador,

    @Schema(description = "Nome do jogador", example = "João Silva")
    String nomeJogador,

    @Schema(description = "Cartas recebidas pelo jogador na rodada")
    List<CartaResponse> cartas
) {}
