package com.poquer.controller;

import com.poquer.dto.request.CriacaoPartidaRequest;
import com.poquer.dto.response.CriacaoPartidaResponse;
import com.poquer.exception.GlobalExceptionHandler;
import com.poquer.exception.RecursoNaoEncontradoException;
import com.poquer.model.StatusPartida;
import com.poquer.service.ApuracaoRodadaService;
import com.poquer.service.JogadorService;
import com.poquer.service.PartidaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CriacaoPartidaControllerTest {

    @Mock
    private PartidaService partidaService;

    @Mock
    private JogadorService jogadorService;

    @Mock
    private ApuracaoRodadaService apuracaoRodadaService;

    private MockMvc mvc;

    @BeforeEach
    void prepararController() {
        mvc = MockMvcBuilders.standaloneSetup(new PokerController(
                jogadorService, partidaService, apuracaoRodadaService))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void deveRetornar201EDelegarUmaUnicaOperacaoAoService() throws Exception {
        CriacaoPartidaRequest request = new CriacaoPartidaRequest("Mesa", 1L, 6);
        when(partidaService.criar(request)).thenReturn(new CriacaoPartidaResponse(
                10L, "Mesa", 1L, StatusPartida.ABERTA, 1, 6));

        mvc.perform(post("/poker/partidas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Mesa\",\"idCriador\":1,\"maxJogadores\":6}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nome").value("Mesa"))
                .andExpect(jsonPath("$.idCriador").value(1))
                .andExpect(jsonPath("$.status").value("ABERTA"))
                .andExpect(jsonPath("$.numeroJogadoresPresentes").value(1))
                .andExpect(jsonPath("$.maxJogadores").value(6));

        verify(partidaService).criar(request);
        verifyNoMoreInteractions(partidaService);
        verifyNoInteractions(jogadorService, apuracaoRodadaService);
    }

    @Test
    void deveAceitarRequisicaoSemCapacidadeExplicita() throws Exception {
        CriacaoPartidaRequest request = new CriacaoPartidaRequest("Mesa", 1L, null);
        when(partidaService.criar(request)).thenReturn(new CriacaoPartidaResponse(
                10L, "Mesa", 1L, StatusPartida.ABERTA, 1, 8));

        mvc.perform(post("/poker/partidas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Mesa\",\"idCriador\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maxJogadores").value(8));

        verify(partidaService).criar(request);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"nome\":\" \",\"idCriador\":1}",
            "{\"nome\":\"Mesa\"}",
            "{\"nome\":\"Mesa\",\"idCriador\":0}",
            "{\"nome\":\"Mesa\",\"idCriador\":-1}",
            "{\"nome\":\"Mesa\",\"idCriador\":1,\"maxJogadores\":1}",
            "{\"nome\":\"Mesa\",\"idCriador\":\"abc\"}",
            "{",
            ""
    })
    void deveRecusarDadosInvalidosSemChamarService(String json) throws Exception {
        mvc.perform(post("/poker/partidas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(partidaService);
    }

    @Test
    void deveRecusarNomeMaiorQue255Caracteres() throws Exception {
        String json = "{\"nome\":\"" + "A".repeat(256) + "\",\"idCriador\":1}";

        mvc.perform(post("/poker/partidas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validacoes.nome").exists());

        verifyNoInteractions(partidaService);
    }

    @Test
    void deveRetornar404QuandoCriadorNaoExistir() throws Exception {
        when(partidaService.criar(any(CriacaoPartidaRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("Jogador não encontrado."));

        mvc.perform(post("/poker/partidas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Mesa\",\"idCriador\":99}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Jogador não encontrado."));
    }
}
