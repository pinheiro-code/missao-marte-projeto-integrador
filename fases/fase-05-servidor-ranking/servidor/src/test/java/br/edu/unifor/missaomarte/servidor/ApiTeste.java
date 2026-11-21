package br.edu.unifor.missaomarte.servidor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

// Base dos testes de API: H2 em memória, segredo de teste e cada teste desfeito no fim.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public abstract class ApiTeste {

    protected static final String SENHA = "senha-de-teste-1";

    @Autowired
    protected MockMvc mvc;

    protected ResultActions postar(String rota, String json) throws Exception {
        return mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    protected ResultActions postarComToken(String rota, String token, String json) throws Exception {
        return mvc.perform(post(rota).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    protected static String credenciais(String nome, String senha) {
        return "{\"nome\":\"" + nome + "\",\"senha\":\"" + senha + "\"}";
    }

    protected static String partida(String dificuldade, String resultado, int pontuacao, long duracao, int movimentos) {
        return "{\"dificuldade\":\"" + dificuldade + "\",\"resultado\":\"" + resultado + "\",\"pontuacao\":" + pontuacao
                + ",\"duracaoSegundos\":" + duracao + ",\"movimentos\":" + movimentos + "}";
    }

    protected static String corpo(ResultActions resposta) throws Exception {
        return resposta.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    protected void cadastrar(String nome) throws Exception {
        postar("/api/usuarios", credenciais(nome, SENHA)).andExpect(status().isCreated());
    }

    // Cadastra e entra pela API, como o cliente faz.
    protected String tokenDe(String nome) throws Exception {
        cadastrar(nome);
        String resposta = corpo(postar("/api/auth/login", credenciais(nome, SENHA)).andExpect(status().isOk()));
        return JsonPath.read(resposta, "$.token");
    }
}
