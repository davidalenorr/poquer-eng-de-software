package com.poquer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para criar uma mesa de pôquer")
public record CriacaoPartidaRequest(
    @Schema(description = "Nome da mesa", example = "Mesa 1")
    @NotBlank(message = "O nome da partida é obrigatório.")
    @Size(max = 255, message = "O nome da partida deve ter no máximo 255 caracteres.")
    String nome,

    @Schema(description = "Identificador do jogador que cria a mesa", example = "1")
    @NotNull(message = "O identificador do criador é obrigatório.")
    @Positive(message = "O identificador do criador deve ser positivo.")
    Long idCriador,

    @Schema(description = "Capacidade da mesa; quando omitida, assume 8 participantes", example = "8")
    @Min(value = 2, message = "A partida deve permitir pelo menos dois jogadores.")
    Integer maxJogadores
) {}
