package com.poquer.dto.request;

import com.poquer.model.Carta;
import com.poquer.model.Naipe;
import com.poquer.model.ValorCarta;
import jakarta.validation.constraints.NotNull;

public record CartaRequest(
    @NotNull(message = "O valor da carta é obrigatório.") ValorCarta valor,
    @NotNull(message = "O naipe da carta é obrigatório.") Naipe naipe
) {
    public Carta toCarta() {
        return new Carta(valor, naipe);
    }
}
