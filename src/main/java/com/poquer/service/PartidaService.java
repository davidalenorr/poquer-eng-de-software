package com.poquer.service;

import com.poquer.dto.response.PartidaAbertaResponse;
import com.poquer.model.StatusPartida;
import com.poquer.repository.PartidaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;

    public PartidaService(PartidaRepository partidaRepository) {
        this.partidaRepository = partidaRepository;
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
