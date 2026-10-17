package com.poquer.model;

public record Carta(ValorCarta valor, Naipe naipe) {

    @Override
    public String toString() {
        return valor.getSimbolo() + naipe.getSimbolo();
    }
}
