package com.poquer.service;

import com.poquer.dto.response.PartidaAbertaResponse;
import com.poquer.dto.response.EntradaPartidaResponse;
import com.poquer.exception.BusinessException;
import com.poquer.exception.RecursoNaoEncontradoException;
import com.poquer.model.Jogador;
import com.poquer.model.Partida;
import com.poquer.model.StatusPartida;
import com.poquer.repository.PartidaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final JogadorService jogadorService;

    public PartidaService(PartidaRepository partidaRepository, JogadorService jogadorService) {
        this.partidaRepository = partidaRepository;
        this.jogadorService = jogadorService;
    }

    @Transactional
    public EntradaPartidaResponse entrar(Long idPartida, Long idJogador) {
        Partida partida = partidaRepository.buscarPorIdComBloqueio(idPartida)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        String.format("Partida com id %d não foi encontrada.", idPartida)));
        Jogador jogador = jogadorService.buscarPorId(idJogador);

        if (partida.getStatus() != StatusPartida.ABERTA) {
            throw new BusinessException("A partida não está aberta para entrada de jogadores.");
        }

        if (partida.getJogadores().stream().anyMatch(presente -> presente.getId().equals(idJogador))) {
            throw new BusinessException("O jogador já está participando desta partida.");
        }

        if (partida.getNumeroJogadoresPresentes() >= partida.getMaxJogadores()) {
            throw new BusinessException("A mesa está cheia.");
        }

        partida.getJogadores().add(jogador);
        partidaRepository.save(partida);

        return new EntradaPartidaResponse(
                true, "Jogador entrou na partida com sucesso.", idPartida, idJogador);
    }

    @Transactional(readOnly = true)
    public List<PartidaAbertaResponse> listarPartidasAbertas() {
        return partidaRepository.findByStatus(StatusPartida.ABERTA).stream()
                .filter(partida -> partida.getNumeroJogadoresPresentes() < partida.getMaxJogadores())
                .map(partida -> new PartidaAbertaResponse(
                        partida.getId(),
                        partida.getNome(),
                        partida.getNumeroJogadoresPresentes(),
                        partida.getMaxJogadores()))
                .toList();
    }
}
