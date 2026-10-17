package com.poquer.model;

public enum Naipe {
    ESPADAS("♠", "Espadas"),
    COPAS("♥", "Copas"),
    OUROS("♦", "Ouros"),
    PAUS("♣", "Paus");

    private final String simbolo;
    private final String descricao;

    Naipe(String simbolo, String descricao) {
        this.simbolo = simbolo;
        this.descricao = descricao;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public String getDescricao() {
        return descricao;
    }
}
