package br.edu.unifor.missaomarte.servidor;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.unifor.missaomarte.servidor.dto.CadastroRequest;
import br.edu.unifor.missaomarte.servidor.model.Usuario;
import br.edu.unifor.missaomarte.servidor.repository.UsuarioRepository;

class CadastroApiTest extends ApiTeste {

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private PasswordEncoder codificador;

    @Test
    void cadastroValidoDevolve201ComIdENomeSemSenha() throws Exception {
        String resposta = corpo(postar("/api/usuarios", credenciais("Ana_01", SENHA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Ana_01")));
        assertFalse(resposta.contains("senha"));
        assertFalse(resposta.contains(SENHA));
        assertFalse(resposta.contains("$2a$"));
    }

    @Test
    void bancoGuardaHashBcryptDiferenteDaSenha() throws Exception {
        cadastrar("bruno");
        Usuario usuario = usuarios.findByNomeNormalizado("bruno").orElseThrow();
        assertTrue(usuario.getHashSenha().startsWith("$2a$10$"));
        assertNotEquals(SENHA, usuario.getHashSenha());
        assertTrue(codificador.matches(SENHA, usuario.getHashSenha()));
    }

    @Test
    void mesmaSenhaGeraHashesDiferentes() throws Exception {
        cadastrar("carla");
        cadastrar("davi");
        String hashCarla = usuarios.findByNomeNormalizado("carla").orElseThrow().getHashSenha();
        String hashDavi = usuarios.findByNomeNormalizado("davi").orElseThrow().getHashSenha();
        assertNotEquals(hashCarla, hashDavi);
    }

    @Test
    void nomeRepetidoComOutraCaixaDa409() throws Exception {
        cadastrar("Elisa");
        postar("/api/usuarios", credenciais("ELISA", "outra-senha-1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Já existe um usuário com esse nome."));
    }

    @Test
    void nomeCurtoDa400ComOCampo() throws Exception {
        postar("/api/usuarios", credenciais("ab", SENHA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros", hasSize(1)))
                .andExpect(jsonPath("$.erros[0].campo").value("nome"));
    }

    @Test
    void nomeComCaractereProibidoOuLongoDemaisDa400() throws Exception {
        postar("/api/usuarios", credenciais("ana maria", SENHA)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("nome"));
        postar("/api/usuarios", credenciais("a".repeat(21), SENHA)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("nome"));
    }

    @Test
    void senhaCurtaDa400() throws Exception {
        postar("/api/usuarios", credenciais("fabio", "1234567"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("senha"))
                .andExpect(jsonPath("$.erros[0].mensagem").value("A senha deve ter pelo menos 8 caracteres."));
    }

    @Test
    void senhaDe72BytesPassaE73BytesDa400() throws Exception {
        String senha72 = "é".repeat(36);
        assertEquals(72, senha72.getBytes(StandardCharsets.UTF_8).length);
        postar("/api/usuarios", credenciais("gabi", senha72)).andExpect(status().isCreated());
        postar("/api/usuarios", credenciais("heitor", senha72 + "a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("senha"))
                .andExpect(jsonPath("$.erros[0].mensagem").value("A senha deve ter no máximo 72 bytes."));
    }

    @Test
    void camposAusentesDao400OrdenadosPeloNome() throws Exception {
        postar("/api/usuarios", "{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros", hasSize(2)))
                .andExpect(jsonPath("$.erros[0].campo").value("nome"))
                .andExpect(jsonPath("$.erros[1].campo").value("senha"));
    }

    @Test
    void jsonQuebradoDa400SemMensagemInterna() throws Exception {
        String resposta = corpo(postar("/api/usuarios", "{\"nome\":\"ivo\",")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("O corpo da requisição não é um JSON válido.")));
        assertFalse(resposta.contains("Jackson"));
        assertFalse(resposta.contains("Exception"));
    }

    @Test
    void jsonCortadoNoMeioDeUmCampoTambemDaODetailGenerico() throws Exception {
        postar("/api/usuarios", "{\"nome\":\"ab")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("O corpo da requisição não é um JSON válido."))
                .andExpect(jsonPath("$.erros").doesNotExist());
    }

    @Test
    void nomeUnicoSemCaixaGarantidoPeloBanco() {
        LocalDateTime agora = LocalDateTime.now();
        usuarios.saveAndFlush(new Usuario("Joao", "joao", "hash-qualquer", agora));
        assertThrows(DataIntegrityViolationException.class,
                () -> usuarios.saveAndFlush(new Usuario("JOAO", "joao", "outro-hash", agora)));
    }

    @Test
    void toStringDoCadastroNaoMostraASenha() {
        String texto = new CadastroRequest("kiko", "senha-secreta-9").toString();
        assertTrue(texto.contains("kiko"));
        assertFalse(texto.contains("senha-secreta-9"));
    }
}
