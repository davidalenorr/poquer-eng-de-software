package com.poquer.dto.response;

import com.poquer.model.StatusPartida;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resultado do início de uma partida")
public record InicioPartidaResponse(
    @Schema(description = "Indica se a partida foi iniciada com sucesso", example = "true")
    boolean sucesso,

    @Schema(description = "Mensagem com o resultado da operação", example = "Partida iniciada com sucesso.")
    String mensagem,

    @Schema(description = "Identificador único da partida", example = "1")
    Long idPartida,

    @Schema(description = "Status atual da partida após o início", example = "EM_ANDAMENTO")
    StatusPartida status,

    @Schema(description = "Quantidade de jogadores participantes na partida", example = "2")
    int numeroJogadores,

    @Schema(description = "Data e hora de início da partida")
    LocalDateTime dataInicio
) {}
