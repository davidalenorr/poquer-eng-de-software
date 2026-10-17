package com.poquer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta contendo o identificador do jogador criado")
public record CadastroJogadorResponse(

    @Schema(description = "Identificador único (id) do jogador recém-criado", example = "1")
    Long id
) {}
