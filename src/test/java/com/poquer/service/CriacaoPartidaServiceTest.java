package com.poquer.service;

import com.poquer.dto.request.CriacaoPartidaRequest;
import com.poquer.dto.response.CriacaoPartidaResponse;
import com.poquer.exception.BusinessException;
import com.poquer.exception.RecursoNaoEncontradoException;
import com.poquer.model.Jogador;
import com.poquer.model.Partida;
import com.poquer.model.StatusPartida;
import com.poquer.repository.CartasJogadorRepository;
import com.poquer.repository.PartidaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriacaoPartidaServiceTest {

    @Mock
    private PartidaRepository partidaRepository;

    @Mock
    private JogadorService jogadorService;

    @Mock
    private CartasJogadorRepository cartasJogadorRepository;

    @InjectMocks
    private PartidaService partidaService;

    @Test
    void deveBuscarCriadorSalvarPartidaDaFabricaERetornarDadosSalvos() {
        CriacaoPartidaRequest request = new CriacaoPartidaRequest("  Mesa dos amigos  ", 1L, 6);
        Jogador criador = new Jogador("Ana", "ana@example.com", "senha123");
        criador.setId(1L);
        Partida partidaSalva = new Partida("Mesa dos amigos", 6, criador);
        partidaSalva.setId(10L);
        when(jogadorService.buscarPorId(1L)).thenReturn(criador);
        when(partidaRepository.save(any(Partida.class))).thenReturn(partidaSalva);

        CriacaoPartidaResponse response = partidaService.criar(request);

        ArgumentCaptor<Partida> captor = ArgumentCaptor.forClass(Partida.class);
        verify(partidaRepository).save(captor.capture());
        Partida partidaCriada = captor.getValue();
        assertEquals("Mesa dos amigos", partidaCriada.getNome());
        assertSame(criador, partidaCriada.getCriador());
        assertEquals(StatusPartida.ABERTA, partidaCriada.getStatus());
        assertEquals(1, partidaCriada.getNumeroJogadoresPresentes());
        assertEquals(6, partidaCriada.getMaxJogadores());
        assertEquals(new CriacaoPartidaResponse(10L, "Mesa dos amigos", 1L,
                StatusPartida.ABERTA, 1, 6), response);
        verify(jogadorService).buscarPorId(1L);
        verifyNoInteractions(cartasJogadorRepository);
    }

    @Test
    void naoDeveSalvarPartidaQuandoCriadorNaoExistir() {
        CriacaoPartidaRequest request = new CriacaoPartidaRequest("Mesa", 99L, 8);
        when(jogadorService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Jogador não encontrado."));

        assertThrows(RecursoNaoEncontradoException.class, () -> partidaService.criar(request));

        verifyNoInteractions(partidaRepository, cartasJogadorRepository);
    }

    @Test
    void naoDeveSalvarPartidaRecusadaPelaFabrica() {
        CriacaoPartidaRequest request = new CriacaoPartidaRequest("Mesa", 1L, 1);
        Jogador criador = new Jogador("Ana", "ana@example.com", "senha123");
        criador.setId(1L);
        when(jogadorService.buscarPorId(1L)).thenReturn(criador);

        assertThrows(BusinessException.class, () -> partidaService.criar(request));

        verify(partidaRepository, never()).save(any(Partida.class));
        verifyNoInteractions(cartasJogadorRepository);
    }
}
