package com.poquer.dto.response;

import com.poquer.model.StatusPartida;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informações da mesa de pôquer criada")
public record CriacaoPartidaResponse(
    Long id,
    String nome,
    Long idCriador,
    StatusPartida status,
    int numeroJogadoresPresentes,
    int maxJogadores
) {}
