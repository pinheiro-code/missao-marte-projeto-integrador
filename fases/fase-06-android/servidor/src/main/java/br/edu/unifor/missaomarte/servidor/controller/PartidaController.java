package br.edu.unifor.missaomarte.servidor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.unifor.missaomarte.servidor.dto.PartidaRequest;
import br.edu.unifor.missaomarte.servidor.dto.PartidaResponse;
import br.edu.unifor.missaomarte.servidor.service.PartidaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

// Sem @Validated de propósito: assim o @Min/@Max dos parâmetros vira HandlerMethodValidationException (400).
@RestController
public class PartidaController {

    private final PartidaService partidas;

    public PartidaController(PartidaService partidas) {
        this.partidas = partidas;
    }

    // O dono vem do sub do token, nunca do corpo.
    @PostMapping("/api/partidas")
    @ResponseStatus(HttpStatus.CREATED)
    public PartidaResponse registrar(@AuthenticationPrincipal Jwt token, @Valid @RequestBody PartidaRequest pedido) {
        return partidas.registrar(Long.valueOf(token.getSubject()), pedido);
    }

    @GetMapping("/api/partidas/minhas")
    public List<PartidaResponse> minhas(@AuthenticationPrincipal Jwt token,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "A página deve ser 0 ou maior.") int pagina,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "O tamanho deve estar entre 1 e 50.")
            @Max(value = 50, message = "O tamanho deve estar entre 1 e 50.") int tamanho) {
        return partidas.historico(Long.valueOf(token.getSubject()), pagina, tamanho);
    }
}
