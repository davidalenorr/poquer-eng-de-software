package com.poquer.pontuacao.strategy;

import com.poquer.pontuacao.MaoCincoCartas;
import com.poquer.pontuacao.PontuacaoMao;
import com.poquer.pontuacao.PontuacaoStrategy;
import com.poquer.pontuacao.TipoMao;
import org.springframework.stereotype.Component;

@Component
public class FlushStrategy implements PontuacaoStrategy {

    @Override
    public TipoMao tipo() {
        return TipoMao.FLUSH;
    }

    @Override
    public boolean aplicavel(MaoCincoCartas mao) {
        return mao.mesmoNaipe();
    }

    @Override
    public PontuacaoMao calcular(MaoCincoCartas mao) {
        return new PontuacaoMao(tipo(), mao.valoresDecrescentes());
    }
}
