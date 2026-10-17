package com.poquer.controller;

import com.poquer.dto.request.CadastroJogadorRequest;
import com.poquer.dto.response.CadastroJogadorResponse;
import com.poquer.dto.response.ErrorResponse;
import com.poquer.dto.response.PartidaAbertaResponse;
import com.poquer.service.JogadorService;
import com.poquer.service.PartidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/poker")
@Tag(name = "Poker", description = "Endpoints para gerenciamento do jogo de pôquer")
public class PokerController {

    private final JogadorService jogadorService;
    private final PartidaService partidaService;

    public PokerController(JogadorService jogadorService, PartidaService partidaService) {
        this.jogadorService = jogadorService;
        this.partidaService = partidaService;
    }

    @Operation(
            summary = "US09 - Consulta de partidas disponíveis",
            description = "Lista as partidas abertas com vagas disponíveis para entrada. Retorna uma lista vazia quando não há partidas disponíveis."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Consulta de partidas disponíveis realizada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PartidaAbertaResponse.class)))
    )
    @GetMapping("/partidasAbertas")
    public ResponseEntity<List<PartidaAbertaResponse>> listarPartidasAbertas() {
        return ResponseEntity.ok(partidaService.listarPartidasAbertas());
    }

    @Operation(
            summary = "US01 - Cadastro de jogadores",
            description = "Permite que um visitante crie uma conta informando nome, e-mail e senha para participar das partidas."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Jogador cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = CadastroJogadorResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou e-mail já existente",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping("/cadastro")
    public ResponseEntity<CadastroJogadorResponse> cadastrarJogador(
            @Valid @RequestBody CadastroJogadorRequest request) {

        CadastroJogadorResponse response = jogadorService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
