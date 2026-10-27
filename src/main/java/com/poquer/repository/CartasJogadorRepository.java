package com.poquer.repository;

import com.poquer.model.CartasJogador;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartasJogadorRepository extends JpaRepository<CartasJogador, Long> {

    @EntityGraph(attributePaths = {"jogador", "partida"})
    List<CartasJogador> findByPartidaId(Long partidaId);

    @EntityGraph(attributePaths = {"jogador", "partida"})
    Optional<CartasJogador> findByPartidaIdAndJogadorId(Long partidaId, Long jogadorId);

    void deleteByPartidaId(Long partidaId);
}
