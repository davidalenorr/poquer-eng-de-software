package com.poquer.repository;

import com.poquer.model.Jogador;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JogadorRepository extends JpaRepository<Jogador, Long> {

    boolean existsByEmail(String email);

    Optional<Jogador> findByEmail(String email);
}
