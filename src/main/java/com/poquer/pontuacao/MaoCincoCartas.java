package com.poquer.pontuacao;

import com.poquer.model.Carta;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Dados comuns às estratégias, sem decidir qual combinação vence. */
public record MaoCincoCartas(List<Carta> cartas) {

    public MaoCincoCartas {
        if (cartas == null || cartas.size() != 5 || cartas.stream().anyMatch(carta ->
                carta == null || carta.valor() == null || carta.naipe() == null)
                || new HashSet<>(cartas).size() != 5) {
            throw new IllegalArgumentException("Uma mão deve conter cinco cartas válidas e distintas.");
        }
        cartas = List.copyOf(cartas);
    }

    public List<Integer> valoresDecrescentes() {
        return cartas.stream().map(carta -> carta.valor().getValor())
                .sorted(Comparator.reverseOrder()).toList();
    }

    public List<Integer> valoresComQuantidade(int quantidade) {
        Map<Integer, Long> contagem = cartas.stream().collect(Collectors.groupingBy(
                carta -> carta.valor().getValor(), Collectors.counting()));
        return contagem.entrySet().stream().filter(item -> item.getValue() == quantidade)
                .map(Map.Entry::getKey).sorted(Comparator.reverseOrder()).toList();
    }

    public boolean mesmoNaipe() {
        return cartas.stream().map(Carta::naipe).distinct().count() == 1;
    }

    public List<Integer> valoresPorGrupos(int... quantidades) {
        List<Integer> valores = new ArrayList<>();
        for (int quantidade : quantidades) {
            valores.addAll(valoresComQuantidade(quantidade));
        }
        return List.copyOf(valores);
    }

    /** Zero indica ausência de sequência. A-2-3-4-5 tem cinco como carta mais alta. */
    public int maiorDaSequencia() {
        List<Integer> valores = valoresDecrescentes();
        if (valores.equals(List.of(14, 5, 4, 3, 2))) {
            return 5;
        }
        if (valores.stream().distinct().count() == 5 && valores.get(0) - valores.get(4) == 4) {
            return valores.get(0);
        }
        return 0;
    }
}
