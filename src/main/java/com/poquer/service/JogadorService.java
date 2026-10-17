package com.poquer.service;

import com.poquer.dto.request.CadastroJogadorRequest;
import com.poquer.dto.response.CadastroJogadorResponse;
import com.poquer.exception.EmailJaCadastradoException;
import com.poquer.exception.RecursoNaoEncontradoException;
import com.poquer.model.Jogador;
import com.poquer.repository.JogadorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JogadorService {

    private final JogadorRepository jogadorRepository;

    public JogadorService(JogadorRepository jogadorRepository) {
        this.jogadorRepository = jogadorRepository;
    }

    @Transactional
    public CadastroJogadorResponse cadastrar(CadastroJogadorRequest request) {
        String emailFormatado = request.email().trim().toLowerCase();

        if (jogadorRepository.existsByEmail(emailFormatado)) {
            throw new EmailJaCadastradoException(emailFormatado);
        }

        Jogador novoJogador = new Jogador(
                request.nome().trim(),
                emailFormatado,
                request.senha()
        );

        Jogador jogadorSalvo = jogadorRepository.save(novoJogador);
        return new CadastroJogadorResponse(jogadorSalvo.getId());
    }

    @Transactional(readOnly = true)
    public Jogador buscarPorId(Long id) {
        return jogadorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        String.format("Jogador com id %d não foi encontrado.", id)));
    }

    @Transactional(readOnly = true)
    public List<Jogador> listarTodos() {
        return jogadorRepository.findAll();
    }
}
