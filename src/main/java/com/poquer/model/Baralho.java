package com.poquer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baralho {

    private final List<Carta> cartas;

    public Baralho() {
        this.cartas = new ArrayList<>();
        reiniciar();
    }

    public final void reiniciar() {
        cartas.clear();
        for (Naipe naipe : Naipe.values()) {
            for (ValorCarta valor : ValorCarta.values()) {
                cartas.add(new Carta(valor, naipe));
            }
        }
    }

    public void embaralhar() {
        Collections.shuffle(cartas);
    }

    public Carta comprarCarta() {
        if (cartas.isEmpty()) {
            throw new IllegalStateException("O baralho não possui mais cartas.");
        }
        return cartas.remove(cartas.size() - 1);
    }

    public Carta retirarCarta() {
        return comprarCarta();
    }

    public int quantidadeCartasRestantes() {
        return cartas.size();
    }
}
