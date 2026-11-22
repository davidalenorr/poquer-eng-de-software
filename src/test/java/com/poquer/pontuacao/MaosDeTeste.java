package com.poquer.pontuacao;

import com.poquer.model.Carta;
import com.poquer.model.Naipe;
import com.poquer.model.ValorCarta;
import com.poquer.pontuacao.strategy.*;
import java.util.Arrays;
import java.util.List;

public final class MaosDeTeste {
    private MaosDeTeste() {}

    public static List<Carta> cartas(String texto) {
        return Arrays.stream(texto.split(" ")).map(carta -> new Carta(
                switch (carta.charAt(0)) {
                    case 'T' -> ValorCarta.DEZ;
                    case 'J' -> ValorCarta.VALETE;
                    case 'Q' -> ValorCarta.DAMA;
                    case 'K' -> ValorCarta.REI;
                    case 'A' -> ValorCarta.AS;
                    default -> ValorCarta.values()[Character.digit(carta.charAt(0), 10) - 2];
                },
                switch (carta.charAt(1)) {
                    case 'E' -> Naipe.ESPADAS;
                    case 'C' -> Naipe.COPAS;
                    case 'O' -> Naipe.OUROS;
                    case 'P' -> Naipe.PAUS;
                    default -> throw new IllegalArgumentException("Naipe inválido no cenário.");
                })).toList();
    }

    public static List<PontuacaoStrategy> estrategias() {
        return List.of(new CartaAltaStrategy(), new UmParStrategy(), new DoisParesStrategy(),
                new TrincaStrategy(), new SequenciaStrategy(), new FlushStrategy(), new FullHouseStrategy(),
                new QuadraStrategy(), new StraightFlushStrategy(), new RoyalFlushStrategy());
    }

    public static AvaliadorMao avaliador() {
        return new AvaliadorMao(estrategias());
    }
}
