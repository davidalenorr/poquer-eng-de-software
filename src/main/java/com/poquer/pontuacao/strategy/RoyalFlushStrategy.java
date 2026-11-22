package com.poquer.pontuacao.strategy;

import com.poquer.pontuacao.MaoCincoCartas;
import com.poquer.pontuacao.PontuacaoMao;
import com.poquer.pontuacao.PontuacaoStrategy;
import com.poquer.pontuacao.TipoMao;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RoyalFlushStrategy implements PontuacaoStrategy {

    @Override
    public TipoMao tipo() {
        return TipoMao.ROYAL_FLUSH;
    }

    @Override
    public boolean aplicavel(MaoCincoCartas mao) {
        return mao.mesmoNaipe() && mao.maiorDaSequencia() == 14;
    }

    @Override
    public PontuacaoMao calcular(MaoCincoCartas mao) {
        return new PontuacaoMao(tipo(), List.of(14));
    }
}
