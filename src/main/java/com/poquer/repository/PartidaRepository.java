package com.poquer.repository;

import com.poquer.model.Partida;
import com.poquer.model.StatusPartida;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {

    @EntityGraph(attributePaths = "jogadores")
    List<Partida> findByStatus(StatusPartida status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select partida from Partida partida where partida.id = :id")
    Optional<Partida> buscarPorIdComBloqueio(@Param("id") Long id);
}
