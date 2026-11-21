package br.edu.unifor.missaomarte.servidor;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import br.edu.unifor.missaomarte.servidor.model.Dificuldade;
import br.edu.unifor.missaomarte.servidor.model.Partida;
import br.edu.unifor.missaomarte.servidor.model.Resultado;
import br.edu.unifor.missaomarte.servidor.model.Usuario;
import br.edu.unifor.missaomarte.servidor.repository.PartidaRepository;
import br.edu.unifor.missaomarte.servidor.repository.UsuarioRepository;

class PartidaApiTest extends ApiTeste {

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private PartidaRepository partidas;

    @Test
    void registraPartidaDevolve201ComDataSemFracao() throws Exception {
        String token = tokenDe("tiago");
        postarComToken("/api/partidas", token, partida("MEDIO", "VITORIA", 120, 95, 40))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.dificuldade").value("MEDIO"))
                .andExpect(jsonPath("$.resultado").value("VITORIA"))
                .andExpect(jsonPath("$.pontuacao").value(120))
                .andExpect(jsonPath("$.duracaoSegundos").value(95))
                .andExpect(jsonPath("$.movimentos").value(40))
                .andExpect(jsonPath("$.dataHora", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")));
    }

    @Test
    void derrotaComPontuacaoNegativaTambemERegistrada() throws Exception {
        String token = tokenDe("ursula");
        postarComToken("/api/partidas", token, partida("DIFICIL", "DERROTA", -30, 10, 5))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pontuacao").value(-30));
    }

    @Test
    void usuarioNoCorpoEIgnoradoEODonoVemDoToken() throws Exception {
        String tokenVera = tokenDe("vera");
        String tokenWill = tokenDe("will");
        String comUsuario = "{\"usuario\":\"will\",\"dificuldade\":\"FACIL\",\"resultado\":\"VITORIA\","
                + "\"pontuacao\":50,\"duracaoSegundos\":30,\"movimentos\":12}";
        postarComToken("/api/partidas", tokenVera, comUsuario).andExpect(status().isCreated());
        historico(tokenVera, "").andExpect(jsonPath("$", hasSize(1)));
        historico(tokenWill, "").andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void enumInvalidoDa400ComOCampo() throws Exception {
        String token = tokenDe("xande");
        postarComToken("/api/partidas", token, partida("IMPOSSIVEL", "VITORIA", 10, 10, 10))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("dificuldade"));
        postarComToken("/api/partidas", token, partida("FACIL", "EMPATE", 10, 10, 10))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("resultado"));
    }

    @Test
    void tipoErradoNumCampoDa400ComOCampo() throws Exception {
        String token = tokenDe("yara");
        String texto = "{\"dificuldade\":\"FACIL\",\"resultado\":\"VITORIA\",\"pontuacao\":\"muito\","
                + "\"duracaoSegundos\":30,\"movimentos\":12}";
        postarComToken("/api/partidas", token, texto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("pontuacao"));
    }

    @Test
    void enumMandadoComoNumeroDa400() throws Exception {
        String token = tokenDe("xuxa");
        String resultadoZero = "{\"dificuldade\":\"FACIL\",\"resultado\":0,\"pontuacao\":10,"
                + "\"duracaoSegundos\":10,\"movimentos\":10}";
        postarComToken("/api/partidas", token, resultadoZero)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("resultado"));
        postarComToken("/api/partidas", token, partida("2", "VITORIA", 10, 10, 10))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("dificuldade"));
    }

    @Test
    void numeroDecimalTextoOuGrandeDemaisNoLugarDeInteiroDa400() throws Exception {
        String token = tokenDe("yuri");
        for (String pontuacao : new String[] { "10.9", "\"120\"", "3000000000" }) {
            String corpo = "{\"dificuldade\":\"FACIL\",\"resultado\":\"VITORIA\",\"pontuacao\":" + pontuacao
                    + ",\"duracaoSegundos\":10,\"movimentos\":10}";
            postarComToken("/api/partidas", token, corpo)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.erros[0].campo").value("pontuacao"));
        }
    }

    @Test
    void paginaMuitoDistanteDevolveListaVazia() throws Exception {
        String token = tokenDe("zilda");
        historico(token, "?pagina=50000000&tamanho=50")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void valorForaDaFaixaDa400() throws Exception {
        String token = tokenDe("zeca");
        postarComToken("/api/partidas", token, partida("FACIL", "VITORIA", 100001, 10, 10))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("pontuacao"));
        postarComToken("/api/partidas", token, partida("FACIL", "VITORIA", 10, 86401, 10))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("duracaoSegundos"));
        postarComToken("/api/partidas", token, partida("FACIL", "VITORIA", 10, 10, -1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("movimentos"));
    }

    @Test
    void camposAusentesDa400ComTodosOsCampos() throws Exception {
        String token = tokenDe("alice");
        postarComToken("/api/partidas", token, "{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros", hasSize(5)))
                .andExpect(jsonPath("$.erros[0].campo").value("dificuldade"))
                .andExpect(jsonPath("$.erros[4].campo").value("resultado"));
    }

    @Test
    void historicoSoDoDonoDaMaisRecenteParaAMaisAntigaEPaginado() throws Exception {
        String token = tokenDe("beto");
        Usuario beto = usuarios.findByNomeNormalizado("beto").orElseThrow();
        Usuario outro = usuarios.save(new Usuario("caio", "caio", "hash", LocalDateTime.now()));
        LocalDateTime base = LocalDateTime.of(2026, 11, 1, 10, 0, 0);
        for (int i = 1; i <= 3; i++) {
            partidas.save(new Partida(beto, Dificuldade.FACIL, Resultado.VITORIA, i * 10, 60, 20, base.plusDays(i)));
        }
        partidas.save(new Partida(outro, Dificuldade.FACIL, Resultado.VITORIA, 999, 60, 20, base.plusDays(9)));

        historico(token, "")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].pontuacao").value(30))
                .andExpect(jsonPath("$[0].dataHora").value("2026-11-04T10:00:00"))
                .andExpect(jsonPath("$[2].pontuacao").value(10));
        historico(token, "?pagina=0&tamanho=2").andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].pontuacao").value(30));
        historico(token, "?pagina=1&tamanho=2").andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pontuacao").value(10));
    }

    @Test
    void tamanhoAcimaDe50Da400() throws Exception {
        String token = tokenDe("dora");
        historico(token, "?tamanho=51")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("tamanho"));
    }

    @Test
    void paginaNegativaOuTamanhoZeroDa400() throws Exception {
        String token = tokenDe("edu");
        historico(token, "?pagina=-1").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("pagina"));
        historico(token, "?tamanho=0").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("tamanho"));
    }

    @Test
    void partidaSemTokenDa401() throws Exception {
        postar("/api/partidas", partida("FACIL", "VITORIA", 10, 10, 10)).andExpect(status().isUnauthorized());
    }

    private ResultActions historico(String token, String consulta) throws Exception {
        return mvc.perform(get("/api/partidas/minhas" + consulta).header("Authorization", "Bearer " + token));
    }
}
