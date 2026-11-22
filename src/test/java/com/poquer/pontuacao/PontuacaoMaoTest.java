package com.poquer.pontuacao;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class PontuacaoMaoTest {
    @Test
    void devePreservarDesempatesContraMutacoesExternas() {
        List<Integer> valores = new ArrayList<>(List.of(14, 13));
        PontuacaoMao pontuacao = new PontuacaoMao(TipoMao.QUADRA, valores);
        valores.clear();
        assertThat(pontuacao.desempate()).containsExactly(14, 13);
        assertThatThrownBy(() -> pontuacao.desempate().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void deveRecusarPontuacaoSemTipoOuDesempatesValidos() {
        assertThatThrownBy(() -> new PontuacaoMao(null, List.of(14))).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PontuacaoMao(TipoMao.UM_PAR, null)).isInstanceOf(NullPointerException.class);
        for (List<Integer> valores : List.of(List.<Integer>of(), List.of(1), List.of(15), List.of(2,3,4,5,6,7))) {
            assertThatThrownBy(() -> new PontuacaoMao(TipoMao.UM_PAR, valores)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void deveValidarECopiarUmaMaoDeCincoCartas() {
        List<com.poquer.model.Carta> cartas = new ArrayList<>(MaosDeTeste.cartas("AE KC QO 8P 2E"));
        MaoCincoCartas mao = new MaoCincoCartas(cartas);
        cartas.clear();
        assertThat(mao.cartas()).hasSize(5);
        assertThatThrownBy(() -> new MaoCincoCartas(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new MaoCincoCartas(List.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new MaoCincoCartas(MaosDeTeste.cartas("AE AE QO 8P 2E")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
