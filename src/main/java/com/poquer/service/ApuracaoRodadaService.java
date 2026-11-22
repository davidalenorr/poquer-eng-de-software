package com.poquer.service;

import com.poquer.dto.request.ApuracaoRodadaRequest;
import com.poquer.dto.request.CartaRequest;
import com.poquer.dto.response.ApuracaoRodadaResponse;
import com.poquer.dto.response.PontuacaoJogadorResponse;
import com.poquer.exception.BusinessException;
import com.poquer.exception.RecursoNaoEncontradoException;
import com.poquer.model.Carta;
import com.poquer.model.CartasJogador;
import com.poquer.model.Jogador;
import com.poquer.model.Partida;
import com.poquer.model.StatusPartida;
import com.poquer.pontuacao.AvaliadorMao;
import com.poquer.pontuacao.PontuacaoMao;
import com.poquer.repository.CartasJogadorRepository;
import com.poquer.repository.PartidaRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Fachada da apuração: US27 (melhor mão), US28 (comparação) e US29 (vencedores). */
@Service
public class ApuracaoRodadaService {

    private final PartidaRepository partidaRepository;
    private final CartasJogadorRepository cartasJogadorRepository;
    private final AvaliadorMao avaliadorMao;

    public ApuracaoRodadaService(PartidaRepository partidaRepository,
            CartasJogadorRepository cartasJogadorRepository, AvaliadorMao avaliadorMao) {
        this.partidaRepository = partidaRepository;
        this.cartasJogadorRepository = cartasJogadorRepository;
        this.avaliadorMao = avaliadorMao;
    }

    @Transactional
    public ApuracaoRodadaResponse apurar(Long idPartida, ApuracaoRodadaRequest request) {
        Partida partida = partidaRepository.buscarPorIdComBloqueio(idPartida)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        String.format("Partida com id %d não foi encontrada.", idPartida)));
        if (partida.getStatus() != StatusPartida.EM_ANDAMENTO) {
            throw new BusinessException("A partida precisa estar em andamento para apurar a rodada.");
        }
        if (partida.getNumeroJogadoresPresentes() < 2) {
            throw new BusinessException("A apuração precisa de pelo menos dois jogadores.");
        }
        List<CartasJogador> distribuicoes = cartasJogadorRepository.findByPartidaId(idPartida);
        Set<Long> participantes = partida.getJogadores().stream().map(Jogador::getId).collect(Collectors.toSet());
        Set<Long> jogadoresComCartas = distribuicoes.stream().map(item -> item.getJogador().getId())
                .collect(Collectors.toSet());
        if (distribuicoes.size() != participantes.size() || !jogadoresComCartas.equals(participantes)) {
            throw new BusinessException("Todos os participantes devem ter exatamente uma distribuição de cartas.");
        }

        if (request == null || request.cartasComunitarias() == null || request.cartasComunitarias().size() != 5
                || request.cartasComunitarias().stream().anyMatch(carta ->
                        carta == null || carta.valor() == null || carta.naipe() == null)) {
            throw new BusinessException("Informe exatamente cinco cartas comunitárias válidas.");
        }
        List<Carta> comunitarias = request.cartasComunitarias().stream().map(CartaRequest::toCarta).toList();
        List<Carta> todasAsCartas = new ArrayList<>(comunitarias);
        distribuicoes.forEach(item -> todasAsCartas.addAll(item.getCartas()));
        if (new HashSet<>(todasAsCartas).size() != todasAsCartas.size()) {
            throw new BusinessException("Uma carta não pode aparecer mais de uma vez na rodada.");
        }

        List<PontuacaoJogadorResponse> pontuacoes = new ArrayList<>();
        for (CartasJogador distribuicao : distribuicoes) {
            List<Carta> cartas = new ArrayList<>(comunitarias);
            cartas.addAll(distribuicao.getCartas());
            PontuacaoMao pontuacao = avaliadorMao.avaliar(cartas);
            pontuacoes.add(new PontuacaoJogadorResponse(distribuicao.getJogador().getId(),
                    distribuicao.getJogador().getNome(), pontuacao.tipo(), pontuacao.pontos(), pontuacao.desempate()));
        }
        long maiorPontuacao = pontuacoes.stream().mapToLong(PontuacaoJogadorResponse::pontos).max().orElseThrow();
        List<Long> vencedores = pontuacoes.stream().filter(item -> item.pontos() == maiorPontuacao)
                .map(PontuacaoJogadorResponse::idJogador).toList();
        return new ApuracaoRodadaResponse(idPartida, vencedores.size() > 1, vencedores, List.copyOf(pontuacoes));
    }
}
