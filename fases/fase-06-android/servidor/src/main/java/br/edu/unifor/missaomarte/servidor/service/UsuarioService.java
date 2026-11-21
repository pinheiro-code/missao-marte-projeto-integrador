package br.edu.unifor.missaomarte.servidor.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.edu.unifor.missaomarte.servidor.dto.CadastroRequest;
import br.edu.unifor.missaomarte.servidor.dto.LoginRequest;
import br.edu.unifor.missaomarte.servidor.dto.TokenResponse;
import br.edu.unifor.missaomarte.servidor.dto.UsuarioResponse;
import br.edu.unifor.missaomarte.servidor.model.Usuario;
import br.edu.unifor.missaomarte.servidor.repository.UsuarioRepository;
import br.edu.unifor.missaomarte.servidor.seguranca.TokenService;

@Service
public class UsuarioService {

    private static final String LOGIN_INVALIDO = "Usuário ou senha inválidos.";

    private final UsuarioRepository usuarios;
    private final PasswordEncoder codificador;
    private final TokenService tokens;
    private final Clock relogio;
    // Nome inexistente confere a senha contra este hash: a resposta demora o mesmo que a senha errada.
    private final String hashFalso;

    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder codificador, TokenService tokens,
            Clock relogio) {
        this.usuarios = usuarios;
        this.codificador = codificador;
        this.tokens = tokens;
        this.relogio = relogio;
        this.hashFalso = codificador.encode("senha-que-nao-pertence-a-ninguem");
    }

    public UsuarioResponse cadastrar(CadastroRequest pedido) {
        String normalizado = normalizar(pedido.nome());
        if (usuarios.existsByNomeNormalizado(normalizado)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com esse nome.");
        }
        LocalDateTime agora = LocalDateTime.now(relogio).truncatedTo(ChronoUnit.SECONDS);
        Usuario usuario = usuarios.saveAndFlush(
                new Usuario(pedido.nome(), normalizado, codificador.encode(pedido.senha()), agora));
        return new UsuarioResponse(usuario.getId(), usuario.getNome());
    }

    public TokenResponse autenticar(LoginRequest pedido) {
        Optional<Usuario> encontrado = usuarios.findByNomeNormalizado(normalizar(pedido.nome()));
        String hash = encontrado.map(Usuario::getHashSenha).orElse(hashFalso);
        boolean senhaConfere = codificador.matches(pedido.senha(), hash);
        if (encontrado.isEmpty() || !senhaConfere) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, LOGIN_INVALIDO);
        }
        Usuario usuario = encontrado.get();
        return tokens.emitir(usuario.getId(), usuario.getNome());
    }

    private static String normalizar(String nome) {
        return nome.toLowerCase(Locale.ROOT);
    }
}
