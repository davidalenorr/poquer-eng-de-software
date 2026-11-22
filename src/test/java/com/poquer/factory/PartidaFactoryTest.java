package com.poquer.factory;

import com.poquer.exception.BusinessException;
import com.poquer.model.Jogador;
import com.poquer.model.Partida;
import com.poquer.model.StatusPartida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PartidaFactoryTest {

    private Jogador criador;

    @BeforeEach
    void prepararCriador() {
        criador = new Jogador("Ana", "ana@example.com", "senha123");
        criador.setId(1L);
    }

    @Test
    void deveCriarPartidaAbertaComCriadorComoPrimeiroParticipante() {
        Partida partida = PartidaFactory.criar("  Mesa dos amigos  ", 6, criador);

        assertEquals("Mesa dos amigos", partida.getNome());
        assertEquals(6, partida.getMaxJogadores());
        assertEquals(2, partida.getMinJogadores());
        assertEquals(StatusPartida.ABERTA, partida.getStatus());
        assertSame(criador, partida.getCriador());
        assertEquals(1, partida.getNumeroJogadoresPresentes());
        assertSame(criador, partida.getJogadores().get(0));
        assertNotNull(partida.getDataCriacao());
        assertNull(partida.getDataInicio());
        assertNull(partida.getId());
    }

    @Test
    void deveUsarOitoVagasQuandoCapacidadeNaoForInformada() {
        Partida partida = PartidaFactory.criar("Mesa", null, criador);

        assertEquals(8, partida.getMaxJogadores());
    }

    @Test
    void devePermitirMesaComDoisJogadores() {
        Partida partida = PartidaFactory.criar("Mesa", 2, criador);

        assertEquals(2, partida.getMaxJogadores());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void deveRecusarNomeAusenteOuEmBranco(String nome) {
        BusinessException erro = assertThrows(BusinessException.class,
                () -> PartidaFactory.criar(nome, 8, criador));

        assertEquals("O nome da partida é obrigatório.", erro.getMessage());
    }

    @Test
    void deveRecusarNomeMaiorQueOTamanhoDaColuna() {
        BusinessException erro = assertThrows(BusinessException.class,
                () -> PartidaFactory.criar("A".repeat(256), 8, criador));

        assertEquals("O nome da partida deve ter no máximo 255 caracteres.", erro.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1})
    void deveRecusarCapacidadeMenorQueDois(int capacidade) {
        BusinessException erro = assertThrows(BusinessException.class,
                () -> PartidaFactory.criar("Mesa", capacidade, criador));

        assertEquals("A partida deve permitir pelo menos dois jogadores.", erro.getMessage());
    }

    @Test
    void deveRecusarCriadorAusente() {
        BusinessException erro = assertThrows(BusinessException.class,
                () -> PartidaFactory.criar("Mesa", 8, null));

        assertEquals("O criador deve ser um jogador cadastrado.", erro.getMessage());
    }

    @Test
    void deveRecusarCriadorSemCadastro() {
        Jogador semCadastro = new Jogador("Bia", "bia@example.com", "senha123");

        assertThrows(BusinessException.class, () -> PartidaFactory.criar("Mesa", 8, semCadastro));
    }

    @Test
    void deveCriarPartidasComListasDeParticipantesIndependentes() {
        Partida primeira = PartidaFactory.criar("Mesa 1", 8, criador);
        Partida segunda = PartidaFactory.criar("Mesa 2", 8, criador);

        primeira.getJogadores().clear();

        assertEquals(1, segunda.getNumeroJogadoresPresentes());
    }
}
