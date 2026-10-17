package com.poquer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Estrutura padrão de resposta de erro da API")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    @Schema(description = "Momento em que ocorreu o erro")
    LocalDateTime timestamp,

    @Schema(description = "Código de status HTTP", example = "400")
    int status,

    @Schema(description = "Título do erro", example = "Bad Request")
    String erro,

    @Schema(description = "Mensagem detalhada do erro", example = "E-mail já cadastrado no sistema.")
    String mensagem,

    @Schema(description = "Caminho da requisição", example = "/poker/cadastro")
    String caminho,

    @Schema(description = "Validações específicas de cada campo quando aplicável")
    Map<String, String> validacoes
) {
    public static ErrorResponse of(int status, String erro, String mensagem, String caminho) {
        return new ErrorResponse(LocalDateTime.now(), status, erro, mensagem, caminho, null);
    }

    public static ErrorResponse ofValidations(int status, String erro, String mensagem, String caminho, Map<String, String> validacoes) {
        return new ErrorResponse(LocalDateTime.now(), status, erro, mensagem, caminho, validacoes);
    }
}
