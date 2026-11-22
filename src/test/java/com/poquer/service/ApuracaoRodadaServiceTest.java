package com.poquer.service;

import com.poquer.dto.request.ApuracaoRodadaRequest;
import com.poquer.dto.request.CartaRequest;
import com.poquer.dto.response.ApuracaoRodadaResponse;
import com.poquer.exception.BusinessException;
import com.poquer.exception.RecursoNaoEncontradoException;
import com.poquer.model.*;
import com.poquer.repository.CartasJogadorRepository;
import com.poquer.repository.PartidaRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.poquer.pontuacao.MaosDeTeste.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApuracaoRodadaServiceTest {
    private final PartidaRepository partidas = mock(PartidaRepository.class);
    private final CartasJogadorRepository distribuicoes = mock(CartasJogadorRepository.class);
    private final ApuracaoRodadaService service = new ApuracaoRodadaService(partidas, distribuicoes, avaliador());
    private Partida partida;
    private Jogador ana;
    private Jogador bia;
    private CartasJogador cartasAna;
    private CartasJogador cartasBia;

    @BeforeEach
    void prepararRodada() {
        ana = new Jogador("Ana", "ana@example.com", "senha123");
        ana.setId(1L);
        bia = new Jogador("Bia", "bia@example.com", "senha123");
        bia.setId(2L);
        partida = new Partida("Mesa", 8, ana);
        partida.setId(1L);
        partida.getJogadores().add(bia);
        partida.setStatus(StatusPartida.EM_ANDAMENTO);
        cartasAna = distribuir(ana, "AC AO");
        cartasBia = distribuir(bia, "KC KO");
        when(partidas.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(partida));
        when(distribuicoes.findByPartidaId(1L)).thenReturn(List.of(cartasAna, cartasBia));
    }

    @Test
    void devePontuarCompararEInformarVencedorSemAlterarPartidaOuSaldos() {
        var saldoAna = ana.getSaldo();
        var saldoBia = bia.getSaldo();
        ApuracaoRodadaResponse resultado = service.apurar(1L, request("2E 3C 7O 8P 9E"));
        assertThat(resultado.idPartida()).isEqualTo(1L);
        assertThat(resultado.empate()).isFalse();
        assertThat(resultado.vencedores()).containsExactly(1L);
        assertThat(resultado.pontuacoes()).hasSize(2);
        assertThat(resultado.pontuacoes().get(0).pontos()).isGreaterThan(resultado.pontuacoes().get(1).pontos());
        assertThat(resultado.pontuacoes().get(0).nome()).isEqualTo("Ana");
        assertThat(resultado.pontuacoes().get(0).desempate()).containsExactly(14, 9, 8, 7);
        assertThat(ana.getSaldo()).isEqualTo(saldoAna);
        assertThat(bia.getSaldo()).isEqualTo(saldoBia);
        assertThat(partida.getStatus()).isEqualTo(StatusPartida.EM_ANDAMENTO);
        verify(partidas, never()).save(any());
        verify(distribuicoes, never()).save(any());
    }

    @Test
    void deveInformarTodosOsVencedoresQuandoAMelhorMaoEstaNaMesa() {
        ApuracaoRodadaResponse resultado = service.apurar(1L, request("AE KE QE JE TE"));
        assertThat(resultado.empate()).isTrue();
        assertThat(resultado.vencedores()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(resultado.pontuacoes()).allSatisfy(item ->
                assertThat(item.combinacao()).isEqualTo(com.poquer.pontuacao.TipoMao.ROYAL_FLUSH));
    }

    @Test
    void deveResolverDesempatePorKickerEntreTresParticipantes() {
        cartasAna.setCartas(cartas("AC QO").get(0), cartas("AC QO").get(1));
        cartasBia.setCartas(cartas("AO JO").get(0), cartas("AO JO").get(1));
        Jogador caio = new Jogador("Caio", "caio@example.com", "senha123");
        caio.setId(3L);
        partida.getJogadores().add(caio);
        when(distribuicoes.findByPartidaId(1L)).thenReturn(List.of(cartasBia, distribuir(caio, "9C 9O"), cartasAna));
        assertThat(service.apurar(1L, request("AE KC 7O 4P 2E")).vencedores()).containsExactly(1L);
    }

    @Test
    void deveRecusarPartidaInexistente() {
        when(partidas.buscarPorIdComBloqueio(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.apurar(1L, request("2E 3C 7O 8P 9E")))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verifyNoInteractions(distribuicoes);
    }

    @ParameterizedTest
    @EnumSource(value = StatusPartida.class, names = {"ABERTA", "FINALIZADA"})
    void deveRecusarPartidaForaDeAndamento(StatusPartida status) {
        partida.setStatus(status);
        assertThatThrownBy(() -> service.apurar(1L, request("2E 3C 7O 8P 9E")))
                .isInstanceOf(BusinessException.class).hasMessageContaining("andamento");
        verifyNoInteractions(distribuicoes);
    }

    @Test
    void deveRecusarMenosDeDoisParticipantes() {
        partida.getJogadores().remove(bia);
        assertThatThrownBy(() -> service.apurar(1L, request("2E 3C 7O 8P 9E")))
                .isInstanceOf(BusinessException.class).hasMessageContaining("dois jogadores");
    }

    @Test
    void deveRecusarDistribuicaoAusenteIncompletaRepetidaOuDeJogadorEstranho() {
        Jogador estranho = new Jogador();
        estranho.setId(99L);
        for (List<CartasJogador> dados : List.of(List.<CartasJogador>of(), List.of(cartasAna),
                List.of(cartasAna, cartasAna), List.of(cartasAna, cartasBia, cartasBia),
                List.of(cartasAna, distribuir(estranho, "QC QO")))) {
            when(distribuicoes.findByPartidaId(1L)).thenReturn(dados);
            assertThatThrownBy(() -> service.apurar(1L, request("2E 3C 7O 8P 9E")))
                    .isInstanceOf(BusinessException.class).hasMessageContaining("distribuição");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"2E 2E 7O 8P 9E", "AC 3C 7O 8P 9E"})
    void deveRecusarCartasDuplicadasNaMesaOuEntreMesaEMao(String mesa) {
        assertThatThrownBy(() -> service.apurar(1L, request(mesa)))
                .isInstanceOf(BusinessException.class).hasMessageContaining("mais de uma vez");
    }

    @Test
    void deveRecusarUmaCartaDistribuidaParaDoisJogadores() {
        cartasBia.setCartas(cartasAna.getCarta1(), cartasBia.getCarta2());
        assertThatThrownBy(() -> service.apurar(1L, request("2E 3C 7O 8P 9E")))
                .isInstanceOf(BusinessException.class).hasMessageContaining("mais de uma vez");
    }

    @Test
    void deveRecusarRequestIncompletoMesmoEmChamadasDiretasAoService() {
        for (ApuracaoRodadaRequest invalido : Arrays.asList(null, new ApuracaoRodadaRequest(null),
                request("2E 3C 7O 8P"), new ApuracaoRodadaRequest(Arrays.asList(null, null, null, null, null)),
                new ApuracaoRodadaRequest(List.of(new CartaRequest(null, Naipe.COPAS),
                        new CartaRequest(null, Naipe.COPAS), new CartaRequest(null, Naipe.COPAS),
                        new CartaRequest(null, Naipe.COPAS), new CartaRequest(null, Naipe.COPAS))))) {
            assertThatThrownBy(() -> service.apurar(1L, invalido)).isInstanceOf(BusinessException.class);
        }
    }

    private CartasJogador distribuir(Jogador jogador, String texto) {
        List<Carta> cartas = cartas(texto);
        return new CartasJogador(partida, jogador, cartas.get(0), cartas.get(1));
    }

    private ApuracaoRodadaRequest request(String texto) {
        return new ApuracaoRodadaRequest(cartas(texto).stream().map(carta -> new CartaRequest(carta.valor(), carta.naipe())).toList());
    }
}
