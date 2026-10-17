package com.poquer.exception;

public class EmailJaCadastradoException extends BusinessException {

    public EmailJaCadastradoException(String email) {
        super(String.format("Já existe um jogador cadastrado com o e-mail '%s'.", email));
    }
}
