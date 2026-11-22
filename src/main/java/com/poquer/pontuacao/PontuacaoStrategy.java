package com.poquer.pontuacao;

/** Strategy (ESM, seção 6.8): contrato intercambiável para calcular a força de uma combinação. */
public interface PontuacaoStrategy {
    TipoMao tipo();

    boolean aplicavel(MaoCincoCartas mao);

    PontuacaoMao calcular(MaoCincoCartas mao);
}
