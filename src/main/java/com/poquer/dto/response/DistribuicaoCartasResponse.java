package com.poquer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Resultado da distribuição de cartas da rodada")
public record DistribuicaoCartasResponse(
    @Schema(description = "Indica se a distribuição ocorreu com sucesso", example = "true")
    boolean sucesso,

    @Schema(description = "Mensagem com o resultado da operação", example = "Cartas distribuídas com sucesso.")
    String mensagem,

    @Schema(description = "Identificador único da partida", example = "1")
    Long idPartida,

    @Schema(description = "Quantidade de jogadores que receberam cartas", example = "2")
    int totalJogadores,

    @Schema(description = "Quantidade de cartas restantes no baralho após a distribuição", example = "48")
    int cartasRestantesNoBaralho,

    @Schema(description = "Lista contendo as cartas distribuídas para cada jogador")
    List<CartasJogadorResponse> distribuicao
) {}
