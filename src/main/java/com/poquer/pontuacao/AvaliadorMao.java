package com.poquer.pontuacao;

import com.poquer.exception.BusinessException;
import com.poquer.model.Carta;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;

/** Contexto do Strategy: seleciona a estratégia aplicável mais forte e delega o cálculo. */
@Component
public class AvaliadorMao {

    private final List<PontuacaoStrategy> estrategias;

    public AvaliadorMao(List<PontuacaoStrategy> estrategias) {
        if (estrategias.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos uma estratégia de pontuação.");
        }
        this.estrategias = estrategias.stream()
                .sorted(Comparator.comparing(PontuacaoStrategy::tipo).reversed()).toList();
    }

    /** US27: escolhe as melhores cinco cartas, inclusive quando a melhor mão está toda na mesa. */
    public PontuacaoMao avaliar(List<Carta> cartas) {
        if (cartas == null || cartas.size() < 5 || cartas.size() > 7
                || cartas.stream().anyMatch(carta -> carta == null || carta.valor() == null || carta.naipe() == null)
                || new HashSet<>(cartas).size() != cartas.size()) {
            throw new BusinessException("Informe de cinco a sete cartas válidas e distintas para avaliar a mão.");
        }
        List<PontuacaoMao> pontuacoes = new ArrayList<>();
        combinar(cartas, 0, new ArrayList<>(), pontuacoes);
        return pontuacoes.stream().max(Comparator.naturalOrder()).orElseThrow();
    }

    private void combinar(List<Carta> cartas, int inicio, List<Carta> escolhidas, List<PontuacaoMao> pontuacoes) {
        if (escolhidas.size() == 5) {
            MaoCincoCartas mao = new MaoCincoCartas(escolhidas);
            PontuacaoStrategy estrategia = estrategias.stream().filter(item -> item.aplicavel(mao))
                    .findFirst().orElseThrow(() -> new IllegalStateException("Nenhuma estratégia aplicável à mão."));
            pontuacoes.add(estrategia.calcular(mao));
            return;
        }
        for (int i = inicio; i <= cartas.size() - (5 - escolhidas.size()); i++) {
            escolhidas.add(cartas.get(i));
            combinar(cartas, i + 1, escolhidas, pontuacoes);
            escolhidas.remove(escolhidas.size() - 1);
        }
    }
}
