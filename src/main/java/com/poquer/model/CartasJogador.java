package com.poquer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "cartas_jogadores")
public class CartasJogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jogador_id", nullable = false)
    private Jogador jogador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValorCarta primeiroValor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Naipe primeiroNaipe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValorCarta segundoValor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Naipe segundoNaipe;

    public CartasJogador() {}

    public CartasJogador(Partida partida, Jogador jogador, Carta carta1, Carta carta2) {
        this.partida = partida;
        this.jogador = jogador;
        this.primeiroValor = carta1.valor();
        this.primeiroNaipe = carta1.naipe();
        this.segundoValor = carta2.valor();
        this.segundoNaipe = carta2.naipe();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public Jogador getJogador() {
        return jogador;
    }

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
    }

    public Carta getCarta1() {
        return new Carta(primeiroValor, primeiroNaipe);
    }

    public Carta getCarta2() {
        return new Carta(segundoValor, segundoNaipe);
    }

    public List<Carta> getCartas() {
        return List.of(getCarta1(), getCarta2());
    }

    public void setCartas(Carta carta1, Carta carta2) {
        this.primeiroValor = carta1.valor();
        this.primeiroNaipe = carta1.naipe();
        this.segundoValor = carta2.valor();
        this.segundoNaipe = carta2.naipe();
    }
}
