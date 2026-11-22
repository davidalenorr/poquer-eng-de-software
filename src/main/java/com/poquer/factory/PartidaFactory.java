package com.poquer.factory;

import com.poquer.exception.BusinessException;
import com.poquer.model.Jogador;
import com.poquer.model.Partida;

public final class PartidaFactory {

    private PartidaFactory() {}

    public static Partida criar(String nome, Integer maxJogadores, Jogador criador) {
        if (nome == null || nome.isBlank()) {
            throw new BusinessException("O nome da partida é obrigatório.");
        }
        if (nome.trim().length() > 255) {
            throw new BusinessException("O nome da partida deve ter no máximo 255 caracteres.");
        }
        if (maxJogadores != null && maxJogadores < 2) {
            throw new BusinessException("A partida deve permitir pelo menos dois jogadores.");
        }
        if (criador == null || criador.getId() == null) {
            throw new BusinessException("O criador deve ser um jogador cadastrado.");
        }

        return new Partida(nome.trim(), maxJogadores, criador);
    }
}
