package com.poquer.dto.response;

import com.poquer.pontuacao.TipoMao;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PontuacaoJogadorResponse(
    Long idJogador,
    String nome,
    TipoMao combinacao,
    @Schema(description = "Força comparável da mão; não representa fichas nem valor do pote") long pontos,
    @Schema(description = "Valores em ordem de prioridade para desempate") List<Integer> desempate
) {}
