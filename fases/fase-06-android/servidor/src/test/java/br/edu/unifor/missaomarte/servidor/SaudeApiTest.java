package br.edu.unifor.missaomarte.servidor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

// A rota de saúde é pública e não abre as outras: a API continua exigindo token.
class SaudeApiTest extends ApiTeste {

    @Test
    void saudeResponde200SemToken() throws Exception {
        mvc.perform(get("/api/saude"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"ok\"}"));
    }

    @Test
    void rankingSemTokenContinua401() throws Exception {
        mvc.perform(get("/api/ranking")).andExpect(status().isUnauthorized());
    }

    @Test
    void saudeSoEPublicaNoGet() throws Exception {
        mvc.perform(post("/api/saude")).andExpect(status().isUnauthorized());
    }
}
