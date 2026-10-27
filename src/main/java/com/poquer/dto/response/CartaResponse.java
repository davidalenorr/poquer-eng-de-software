package com.poquer.dto.response;

import com.poquer.model.Carta;
import com.poquer.model.Naipe;
import com.poquer.model.ValorCarta;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representação de uma carta de pôquer")
public record CartaResponse(
    @Schema(description = "Valor da carta", example = "AS")
    ValorCarta valor,

    @Schema(description = "Naipe da carta", example = "ESPADAS")
    Naipe naipe,

    @Schema(description = "Representação textual e simbólica da carta", example = "A♠")
    String representacao
) {
    public static CartaResponse from(Carta carta) {
        return new CartaResponse(carta.valor(), carta.naipe(), carta.toString());
    }
}
