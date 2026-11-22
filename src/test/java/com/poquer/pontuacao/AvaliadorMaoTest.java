package com.poquer.pontuacao;

import com.poquer.exception.BusinessException;
import com.poquer.model.Carta;
import com.poquer.model.Naipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.poquer.pontuacao.MaosDeTeste.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvaliadorMaoTest {
    private final AvaliadorMao avaliador = avaliador();

    static Stream<Arguments> combinacoes() {
        return Stream.of(
                Arguments.of(TipoMao.CARTA_ALTA, "AE KC QO 8P 2E", List.of(14, 13, 12, 8, 2)),
                Arguments.of(TipoMao.UM_PAR, "AE AC KO QP 2E", List.of(14, 13, 12, 2)),
                Arguments.of(TipoMao.DOIS_PARES, "AE AC KO KP 2E", List.of(14, 13, 2)),
                Arguments.of(TipoMao.TRINCA, "AE AC AO KP 2E", List.of(14, 13, 2)),
                Arguments.of(TipoMao.SEQUENCIA, "9E 8C 7O 6P 5E", List.of(9)),
                Arguments.of(TipoMao.FLUSH, "AE KE QE 8E 2E", List.of(14, 13, 12, 8, 2)),
                Arguments.of(TipoMao.FULL_HOUSE, "AE AC AO KP KE", List.of(14, 13)),
                Arguments.of(TipoMao.QUADRA, "AE AC AO AP KE", List.of(14, 13)),
                Arguments.of(TipoMao.STRAIGHT_FLUSH, "9E 8E 7E 6E 5E", List.of(9)),
                Arguments.of(TipoMao.ROYAL_FLUSH, "AE KE QE JE TE", List.of(14)));
    }

    @ParameterizedTest
    @MethodSource("combinacoes")
    void devePontuarCadaCombinacaoComSuaEstrategia(TipoMao tipo, String texto, List<Integer> desempate) {
        PontuacaoStrategy estrategia = estrategias().stream().filter(item -> item.tipo() == tipo).findFirst().orElseThrow();
        MaoCincoCartas mao = new MaoCincoCartas(cartas(texto));
        assertThat(estrategia.aplicavel(mao)).isTrue();
        assertThat(estrategia.calcular(mao)).isEqualTo(new PontuacaoMao(tipo, desempate));
        assertThat(avaliador.avaliar(mao.cartas())).isEqualTo(estrategia.calcular(mao));
    }

    @Test
    void estrategiasEspecificasNaoDevemReconhecerUmaMaoSemCombinacao() {
        MaoCincoCartas mao = new MaoCincoCartas(cartas("AE KC QO 8P 2E"));
        estrategias().stream().filter(item -> item.tipo() != TipoMao.CARTA_ALTA)
                .forEach(item -> assertThat(item.aplicavel(mao)).as(item.tipo().name()).isFalse());
    }

    @Test
    void deveRespeitarHierarquiaCompletaIndependentementeDosValoresDasCartas() {
        List<String> maos = List.of("AE KC QO 8P 2E", "2E 2C 3O 4P 7E", "2E 2C 3O 3P 7E",
                "2E 2C 2O 3P 7E", "AE 2C 3O 4P 5E", "2E 4E 6E 8E TE",
                "2E 2C 2O 3P 3E", "2E 2C 2O 2P 3E", "AE 2E 3E 4E 5E", "AE KE QE JE TE");
        for (int i = 1; i < maos.size(); i++) {
            assertThat(avaliador.avaliar(cartas(maos.get(i))))
                    .isGreaterThan(avaliador.avaliar(cartas(maos.get(i - 1))));
        }
    }

    static Stream<Arguments> desempates() {
        return Stream.of(
                Arguments.of("AE KC QO 8P 3E", "AE KC QO 8P 2E"),
                Arguments.of("AE AC KO QP 3E", "AE AC KO QP 2E"),
                Arguments.of("AE AC KO KP 3E", "AE AC KO KP 2E"),
                Arguments.of("AE AC QO QP 2E", "KE KC QO QP AE"),
                Arguments.of("AE AC AO KP 3E", "AE AC AO KP 2E"),
                Arguments.of("6E 5C 4O 3P 2E", "AE 5C 4O 3P 2E"),
                Arguments.of("AE KE QE 8E 3E", "AE KE QE 8E 2E"),
                Arguments.of("AE AC AO KP KE", "AE AC AO QP QE"),
                Arguments.of("KE KC KO AP AE", "QE QC QO AP AE"),
                Arguments.of("AE AC AO AP KE", "AE AC AO AP QE"),
                Arguments.of("6E 5E 4E 3E 2E", "AE 5E 4E 3E 2E"));
    }

    @ParameterizedTest
    @MethodSource("desempates")
    void deveDesempatarPelaPrimeiraCartaRelevante(String maior, String menor) {
        assertThat(avaliador.avaliar(cartas(maior))).isGreaterThan(avaliador.avaliar(cartas(menor)));
    }

    @ParameterizedTest
    @MethodSource("combinacoes")
    void naipeNaoDeveDesempatar(TipoMao tipo, String texto, List<Integer> ignorado) {
        List<Carta> mao = cartas(texto);
        List<Carta> outrosNaipes = mao.stream().map(carta -> new Carta(carta.valor(),
                Naipe.values()[(carta.naipe().ordinal() + 1) % Naipe.values().length])).toList();
        assertThat(avaliador.avaliar(mao).compareTo(avaliador.avaliar(outrosNaipes))).isZero();
    }

    @Test
    void deveEscolherMelhorMaoEntreSeteCartasSemDependerDaOrdem() {
        List<Carta> sete = new ArrayList<>(cartas("2C AE KE QE JE TE 3O"));
        PontuacaoMao esperada = new PontuacaoMao(TipoMao.ROYAL_FLUSH, List.of(14));
        assertThat(avaliador.avaliar(sete)).isEqualTo(esperada);
        Collections.reverse(sete);
        assertThat(avaliador.avaliar(sete)).isEqualTo(esperada);
    }

    @Test
    void deveUsarMaiorTrincaNoFullHouseEDoisMaioresParesEntreSeteCartas() {
        assertThat(avaliador.avaliar(cartas("AE AC AO KE KC KO 2P")))
                .isEqualTo(new PontuacaoMao(TipoMao.FULL_HOUSE, List.of(14, 13)));
        assertThat(avaliador.avaliar(cartas("AE AC KE KC QE QC 2P")))
                .isEqualTo(new PontuacaoMao(TipoMao.DOIS_PARES, List.of(14, 13, 12)));
        assertThat(avaliador.avaliar(cartas("AE 2C 3O 4P 5E 6C 7O")))
                .isEqualTo(new PontuacaoMao(TipoMao.SEQUENCIA, List.of(7)));
    }

    @Test
    void asNaoDeveConectarReiEDois() {
        assertThat(avaliador.avaliar(cartas("QE KC AO 2P 3E")).tipo()).isEqualTo(TipoMao.CARTA_ALTA);
    }

    @ParameterizedTest
    @ValueSource(strings = {"AE KC QO 8P", "AE AC QO 8P 2E 3C 4P 5O", "AE AE QO 8P 2E"})
    void deveRecusarQuantidadeInvalidaOuCartasRepetidas(String texto) {
        assertThatThrownBy(() -> avaliador.avaliar(cartas(texto))).isInstanceOf(BusinessException.class);
    }

    @Test
    void deveRecusarCartasAusentesOuIncompletas() {
        assertThatThrownBy(() -> avaliador.avaliar(null)).isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> avaliador.avaliar(Arrays.asList(null, null, null, null, null)))
                .isInstanceOf(BusinessException.class);
        List<Carta> mao = new ArrayList<>(cartas("AE KC QO 8P 2E"));
        mao.set(0, new Carta(null, Naipe.COPAS));
        assertThatThrownBy(() -> avaliador.avaliar(mao)).isInstanceOf(BusinessException.class);
    }

    @Test
    void contextoDevePermitirSubstituirAlgoritmoPelaInterface() {
        PontuacaoStrategy substituta = mock(PontuacaoStrategy.class);
        when(substituta.tipo()).thenReturn(TipoMao.CARTA_ALTA);
        when(substituta.aplicavel(any())).thenReturn(true);
        PontuacaoMao resultado = new PontuacaoMao(TipoMao.CARTA_ALTA, List.of(10));
        when(substituta.calcular(any())).thenReturn(resultado);
        AvaliadorMao contexto = new AvaliadorMao(List.of(substituta));
        assertThat(contexto.avaliar(cartas("AE KC QO 8P 2E"))).isSameAs(resultado);
        verify(substituta).calcular(any(MaoCincoCartas.class));
    }

    @Test
    void deveRecusarConfiguracaoSemEstrategiasOuSemAlgoritmoAplicavel() {
        assertThatThrownBy(() -> new AvaliadorMao(List.of())).isInstanceOf(IllegalArgumentException.class);
        AvaliadorMao contexto = new AvaliadorMao(List.of(new com.poquer.pontuacao.strategy.RoyalFlushStrategy()));
        assertThatThrownBy(() -> contexto.avaliar(cartas("AE KC QO 8P 2E"))).isInstanceOf(IllegalStateException.class);
    }
}
