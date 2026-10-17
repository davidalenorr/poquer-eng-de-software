package com.poquer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para realizar o cadastro de um jogador")
public record CadastroJogadorRequest(

    @Schema(description = "Nome completo ou apelido do jogador", example = "João Silva")
    @NotBlank(message = "O nome é obrigatório.")
    String nome,

    @Schema(description = "E-mail para login e notificações", example = "joao.silva@email.com")
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail informado possui formato inválido.")
    String email,

    @Schema(description = "Senha secreta para acesso à conta", example = "senhaForte123")
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres.")
    String senha
) {}
