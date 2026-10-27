package com.poquer.model;

import com.poquer.exception.BusinessException;
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
    private Integer minJogadores;

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

    @Column
    private LocalDateTime dataInicio;

    public Partida() {
        this.status = StatusPartida.ABERTA;
        this.minJogadores = 2;
        this.maxJogadores = 8;
        this.dataCriacao = LocalDateTime.now();
    }

    public Partida(String nome, Integer maxJogadores, Jogador criador) {
        this();
        this.nome = nome;
        this.minJogadores = 2;
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

    public Integer getMinJogadores() {
        return minJogadores != null ? minJogadores : 2;
    }

    public void setMinJogadores(Integer minJogadores) {
        this.minJogadores = minJogadores;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public int getNumeroJogadoresPresentes() {
        return this.jogadores != null ? this.jogadores.size() : 0;
    }

    public boolean isJogadorParticipando(Long idJogador) {
        if (idJogador == null || this.jogadores == null) {
            return false;
        }
        return this.jogadores.stream().anyMatch(jogador -> jogador.getId().equals(idJogador));
    }

    public boolean podeIniciar() {
        return this.status == StatusPartida.ABERTA
                && getNumeroJogadoresPresentes() >= getMinJogadores();
    }

    public void iniciar() {
        if (this.status == StatusPartida.EM_ANDAMENTO) {
            throw new BusinessException("A partida já está em andamento.");
        }
        if (this.status == StatusPartida.FINALIZADA) {
            throw new BusinessException("A partida já foi finalizada.");
        }
        if (getNumeroJogadoresPresentes() < getMinJogadores()) {
            throw new BusinessException(String.format(
                    "A partida precisa de pelo menos %d jogadores para ser iniciada.", getMinJogadores()));
        }
        this.status = StatusPartida.EM_ANDAMENTO;
        this.dataInicio = LocalDateTime.now();
    }
}
