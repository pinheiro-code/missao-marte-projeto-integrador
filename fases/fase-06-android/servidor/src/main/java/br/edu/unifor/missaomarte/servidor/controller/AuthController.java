package br.edu.unifor.missaomarte.servidor.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.edu.unifor.missaomarte.servidor.dto.LoginRequest;
import br.edu.unifor.missaomarte.servidor.dto.TokenResponse;
import br.edu.unifor.missaomarte.servidor.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
public class AuthController {

    private final UsuarioService usuarios;

    public AuthController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @PostMapping("/api/auth/login")
    public TokenResponse entrar(@Valid @RequestBody LoginRequest pedido) {
        return usuarios.autenticar(pedido);
    }
}
