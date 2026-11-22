package com.poquer.pontuacao.strategy;

import com.poquer.pontuacao.MaoCincoCartas;
import com.poquer.pontuacao.PontuacaoMao;
import com.poquer.pontuacao.PontuacaoStrategy;
import com.poquer.pontuacao.TipoMao;
import org.springframework.stereotype.Component;

@Component
public class DoisParesStrategy implements PontuacaoStrategy {

    @Override
    public TipoMao tipo() {
        return TipoMao.DOIS_PARES;
    }

    @Override
    public boolean aplicavel(MaoCincoCartas mao) {
        return mao.valoresComQuantidade(2).size() == 2;
    }

    @Override
    public PontuacaoMao calcular(MaoCincoCartas mao) {
        return new PontuacaoMao(tipo(), mao.valoresPorGrupos(2, 1));
    }
}
