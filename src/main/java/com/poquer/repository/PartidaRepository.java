package com.poquer.repository;

import com.poquer.model.Partida;
import com.poquer.model.StatusPartida;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {

    @EntityGraph(attributePaths = "jogadores")
    List<Partida> findByStatus(StatusPartida status);
}
