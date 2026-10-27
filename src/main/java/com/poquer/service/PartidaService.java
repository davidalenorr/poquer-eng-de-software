package com.poquer.service;

import com.poquer.dto.response.CartaResponse;
import com.poquer.dto.response.CartasJogadorResponse;
import com.poquer.dto.response.DistribuicaoCartasResponse;
import com.poquer.dto.response.EntradaPartidaResponse;
import com.poquer.dto.response.InicioPartidaResponse;
import com.poquer.dto.response.PartidaAbertaResponse;
import com.poquer.exception.BusinessException;
import com.poquer.exception.RecursoNaoEncontradoException;
import com.poquer.model.Baralho;
import com.poquer.model.Carta;
import com.poquer.model.CartasJogador;
import com.poquer.model.Jogador;
import com.poquer.model.Partida;
import com.poquer.model.StatusPartida;
import com.poquer.repository.CartasJogadorRepository;
import com.poquer.repository.PartidaRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final JogadorService jogadorService;
    private final CartasJogadorRepository cartasJogadorRepository;

    public PartidaService(
            PartidaRepository partidaRepository,
            JogadorService jogadorService,
            CartasJogadorRepository cartasJogadorRepository) {
        this.partidaRepository = partidaRepository;
        this.jogadorService = jogadorService;
        this.cartasJogadorRepository = cartasJogadorRepository;
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

        if (partida.isJogadorParticipando(idJogador)) {
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

    @Transactional
    public InicioPartidaResponse iniciar(Long idPartida, Long idJogador) {
        Partida partida = partidaRepository.buscarPorIdComBloqueio(idPartida)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        String.format("Partida com id %d não foi encontrada.", idPartida)));

        if (idJogador != null) {
            Jogador jogador = jogadorService.buscarPorId(idJogador);
            if (!partida.isJogadorParticipando(jogador.getId())) {
                throw new BusinessException("O jogador informado não está participando desta partida.");
            }
        }

        partida.iniciar();
        partidaRepository.save(partida);

        return new InicioPartidaResponse(
                true,
                "Partida iniciada com sucesso.",
                partida.getId(),
                partida.getStatus(),
                partida.getNumeroJogadoresPresentes(),
                partida.getDataInicio()
        );
    }

    @Transactional
    public InicioPartidaResponse iniciar(Long idPartida) {
        return iniciar(idPartida, null);
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

    @Transactional
    public DistribuicaoCartasResponse distribuirCartas(Long idPartida) {
        Partida partida = partidaRepository.buscarPorIdComBloqueio(idPartida)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        String.format("Partida com id %d não foi encontrada.", idPartida)));

        if (partida.getStatus() == StatusPartida.ABERTA) {
            throw new BusinessException("A partida precisa ser iniciada antes de distribuir as cartas.");
        }

        if (partida.getStatus() == StatusPartida.FINALIZADA) {
            throw new BusinessException("A partida já foi finalizada.");
        }

        if (partida.getNumeroJogadoresPresentes() < partida.getMinJogadores()) {
            throw new BusinessException("A partida não possui jogadores suficientes para a distribuição de cartas.");
        }

        Baralho baralho = new Baralho();
        baralho.embaralhar();

        cartasJogadorRepository.deleteByPartidaId(idPartida);

        List<CartasJogador> distribuicoes = new ArrayList<>();
        for (Jogador jogador : partida.getJogadores()) {
            Carta carta1 = baralho.retirarCarta();
            Carta carta2 = baralho.retirarCarta();
            distribuicoes.add(new CartasJogador(partida, jogador, carta1, carta2));
        }

        List<CartasJogador> salvas = cartasJogadorRepository.saveAll(distribuicoes);

        List<CartasJogadorResponse> responseItens = salvas.stream()
                .map(cj -> new CartasJogadorResponse(
                        cj.getJogador().getId(),
                        cj.getJogador().getNome(),
                        cj.getCartas().stream().map(CartaResponse::from).toList()
                ))
                .toList();

        return new DistribuicaoCartasResponse(
                true,
                "Cartas distribuídas com sucesso para todos os jogadores da partida.",
                partida.getId(),
                partida.getNumeroJogadoresPresentes(),
                baralho.quantidadeCartasRestantes(),
                responseItens
        );
    }
}
