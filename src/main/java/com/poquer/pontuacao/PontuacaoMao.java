package com.poquer.pontuacao;

import java.util.List;
import java.util.Objects;

/** A categoria precede os valores de desempate; naipes nunca desempatarão mãos. */
public record PontuacaoMao(TipoMao tipo, List<Integer> desempate) implements Comparable<PontuacaoMao> {

    public PontuacaoMao {
        Objects.requireNonNull(tipo);
        desempate = List.copyOf(desempate);
        if (desempate.isEmpty() || desempate.size() > 5
                || desempate.stream().anyMatch(valor -> valor < 2 || valor > 14)) {
            throw new IllegalArgumentException("Informe de um a cinco valores de desempate entre 2 e 14.");
        }
    }

    /** Base 15 reserva cinco posições para desempates sem sobrepor categorias. Não representa fichas. */
    public long pontos() {
        long pontos = tipo.ordinal();
        for (int i = 0; i < 5; i++) {
            pontos = pontos * 15 + (i < desempate.size() ? desempate.get(i) : 0);
        }
        return pontos;
    }

    @Override
    public int compareTo(PontuacaoMao outra) {
        return Long.compare(pontos(), outra.pontos());
    }
}
