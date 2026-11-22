package com.poquer.controller;

import com.poquer.dto.request.ApuracaoRodadaRequest;
import com.poquer.dto.response.ApuracaoRodadaResponse;
import com.poquer.exception.BusinessException;
import com.poquer.exception.GlobalExceptionHandler;
import com.poquer.exception.RecursoNaoEncontradoException;
import com.poquer.service.ApuracaoRodadaService;
import com.poquer.service.JogadorService;
import com.poquer.service.PartidaService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ApuracaoRodadaControllerTest {
    private final ApuracaoRodadaService service = mock(ApuracaoRodadaService.class);
    private MockMvc mvc;
    private static final String JSON = """
            {"cartasComunitarias":[
              {"valor":"AS","naipe":"ESPADAS"}, {"valor":"REI","naipe":"ESPADAS"},
              {"valor":"DAMA","naipe":"ESPADAS"}, {"valor":"VALETE","naipe":"ESPADAS"},
              {"valor":"DEZ","naipe":"ESPADAS"}]}
            """;

    @BeforeEach
    void prepararController() {
        mvc = MockMvcBuilders.standaloneSetup(new PokerController(
                mock(JogadorService.class), mock(PartidaService.class), service))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void deveDelegarUmaUnicaOperacaoAoServiceERetornarOsVencedores() throws Exception {
        when(service.apurar(eq(1L), any())).thenReturn(new ApuracaoRodadaResponse(1L, true, List.of(1L, 2L), List.of()));
        mvc.perform(post("/poker/1/apurarRodada").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idPartida").value(1))
                .andExpect(jsonPath("$.empate").value(true)).andExpect(jsonPath("$.vencedores.length()").value(2));
        verify(service).apurar(eq(1L), any(ApuracaoRodadaRequest.class));
        verifyNoMoreInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"cartasComunitarias\":[]}", "{\"cartasComunitarias\":[null,null,null,null,null]}",
            "{\"cartasComunitarias\":[{},{},{},{},{}]}", "{", "", "{\"cartasComunitarias\":[{\"valor\":\"INVALIDO\"}]}"})
    void deveRecusarCorpoInvalidoAntesDeChamarService(String json) throws Exception {
        mvc.perform(post("/poker/1/apurarRodada").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(service);
    }

    @Test
    void deveRetornar404ParaPartidaInexistente() throws Exception {
        when(service.apurar(eq(99L), any())).thenThrow(new RecursoNaoEncontradoException("Partida não encontrada."));
        mvc.perform(post("/poker/99/apurarRodada").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar400ParaErroDeNegocio() throws Exception {
        when(service.apurar(eq(1L), any())).thenThrow(new BusinessException("Cartas repetidas."));
        mvc.perform(post("/poker/1/apurarRodada").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem").value("Cartas repetidas."));
    }
}
