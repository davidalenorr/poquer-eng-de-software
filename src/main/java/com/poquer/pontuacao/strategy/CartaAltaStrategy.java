package com.poquer.pontuacao.strategy;

import com.poquer.pontuacao.MaoCincoCartas;
import com.poquer.pontuacao.PontuacaoMao;
import com.poquer.pontuacao.PontuacaoStrategy;
import com.poquer.pontuacao.TipoMao;
import org.springframework.stereotype.Component;

@Component
public class CartaAltaStrategy implements PontuacaoStrategy {

    @Override
    public TipoMao tipo() {
        return TipoMao.CARTA_ALTA;
    }

    @Override
    public boolean aplicavel(MaoCincoCartas mao) {
        return true;
    }

    @Override
    public PontuacaoMao calcular(MaoCincoCartas mao) {
        return new PontuacaoMao(tipo(), mao.valoresDecrescentes());
    }
}
