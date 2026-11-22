package com.poquer.pontuacao.strategy;

import com.poquer.pontuacao.MaoCincoCartas;
import com.poquer.pontuacao.PontuacaoMao;
import com.poquer.pontuacao.PontuacaoStrategy;
import com.poquer.pontuacao.TipoMao;
import org.springframework.stereotype.Component;

@Component
public class QuadraStrategy implements PontuacaoStrategy {

    @Override
    public TipoMao tipo() {
        return TipoMao.QUADRA;
    }

    @Override
    public boolean aplicavel(MaoCincoCartas mao) {
        return !mao.valoresComQuantidade(4).isEmpty();
    }

    @Override
    public PontuacaoMao calcular(MaoCincoCartas mao) {
        return new PontuacaoMao(tipo(), mao.valoresPorGrupos(4, 1));
    }
}
