package br.edu.unifor.missaomarte.servidor.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.unifor.missaomarte.servidor.dto.CadastroRequest;
import br.edu.unifor.missaomarte.servidor.dto.UsuarioResponse;
import br.edu.unifor.missaomarte.servidor.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
public class UsuarioController {

    private final UsuarioService usuarios;

    public UsuarioController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @PostMapping("/api/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@Valid @RequestBody CadastroRequest pedido) {
        return usuarios.cadastrar(pedido);
    }
}
