package com.poquer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "partidas")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer maxJogadores;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPartida status;

    @ManyToOne
    @JoinColumn(name = "criador_id", nullable = false)
    private Jogador criador;

    @ManyToMany
    @JoinTable(
            name = "partida_jogadores",
            joinColumns = @JoinColumn(name = "partida_id"),
            inverseJoinColumns = @JoinColumn(name = "jogador_id")
    )
    private List<Jogador> jogadores = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    public Partida() {
        this.status = StatusPartida.ABERTA;
        this.maxJogadores = 8;
        this.dataCriacao = LocalDateTime.now();
    }

    public Partida(String nome, Integer maxJogadores, Jogador criador) {
        this();
        this.nome = nome;
        this.maxJogadores = maxJogadores != null ? maxJogadores : 8;
        this.criador = criador;
        this.jogadores.add(criador);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getMaxJogadores() {
        return maxJogadores;
    }

    public void setMaxJogadores(Integer maxJogadores) {
        this.maxJogadores = maxJogadores;
    }

    public StatusPartida getStatus() {
        return status;
    }

    public void setStatus(StatusPartida status) {
        this.status = status;
    }

    public Jogador getCriador() {
        return criador;
    }

    public void setCriador(Jogador criador) {
        this.criador = criador;
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<Jogador> jogadores) {
        this.jogadores = jogadores;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getNumeroJogadoresPresentes() {
        return this.jogadores != null ? this.jogadores.size() : 0;
    }
}
