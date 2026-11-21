package br.edu.unifor.missaomarte.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import br.edu.unifor.missaomarte.servidor.dto.LoginRequest;

class LoginApiTest extends ApiTeste {

    private static final String DATA_SEM_FRACAO = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}";

    @Test
    void loginCorretoDevolveTokenBearerEExpiracao() throws Exception {
        cadastrar("lara");
        String resposta = corpo(postar("/api/auth/login", credenciais("lara", SENHA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.tipo").value("Bearer")));
        String expiraEm = com.jayway.jsonpath.JsonPath.read(resposta, "$.expiraEm");
        assertTrue(expiraEm.matches(DATA_SEM_FRACAO), expiraEm);
    }

    @Test
    void loginNaoDiferenciaCaixaDoNome() throws Exception {
        cadastrar("Marta");
        postar("/api/auth/login", credenciais("MARTA", SENHA)).andExpect(status().isOk());
    }

    @Test
    void senhaErradaEUsuarioInexistenteDaoAMesmaResposta() throws Exception {
        cadastrar("nina");
        String senhaErrada = corpo(postar("/api/auth/login", credenciais("nina", "senha-errada-1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Usuário ou senha inválidos.")));
        String inexistente = corpo(postar("/api/auth/login", credenciais("ninguem", SENHA))
                .andExpect(status().isUnauthorized()));
        assertEquals(senhaErrada, inexistente);
    }

    @Test
    void nomeForaDoFormatoDoCadastroDa401Generico() throws Exception {
        postar("/api/auth/login", credenciais("ab", SENHA))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Usuário ou senha inválidos."));
    }

    @Test
    void respostaDoLoginNaoTrazSenha() throws Exception {
        cadastrar("otavio");
        String resposta = corpo(postar("/api/auth/login", credenciais("otavio", SENHA)).andExpect(status().isOk()));
        assertFalse(resposta.contains(SENHA));
        assertFalse(resposta.contains("senha"));
    }

    @Test
    void senhaAcimaDe72BytesNoLoginDa400() throws Exception {
        postar("/api/auth/login", credenciais("paula", "a".repeat(73)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("senha"));
    }

    @Test
    void campoAusenteNoLoginDa400() throws Exception {
        postar("/api/auth/login", "{\"nome\":\"quim\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("senha"));
    }

    @Test
    void toStringDoLoginNaoMostraASenha() {
        String texto = new LoginRequest("rita", "senha-secreta-9").toString();
        assertTrue(texto.contains("rita"));
        assertFalse(texto.contains("senha-secreta-9"));
    }
}
