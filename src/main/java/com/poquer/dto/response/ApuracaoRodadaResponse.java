package com.poquer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Pontuações e vencedores da rodada; não movimenta fichas nem encerra a partida")
public record ApuracaoRodadaResponse(
    Long idPartida,
    boolean empate,
    List<Long> vencedores,
    List<PontuacaoJogadorResponse> pontuacoes
) {}
