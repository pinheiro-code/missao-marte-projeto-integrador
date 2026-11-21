package br.edu.unifor.missaomarte.servidor;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import br.edu.unifor.missaomarte.servidor.model.Dificuldade;
import br.edu.unifor.missaomarte.servidor.model.Partida;
import br.edu.unifor.missaomarte.servidor.model.Resultado;
import br.edu.unifor.missaomarte.servidor.model.Usuario;
import br.edu.unifor.missaomarte.servidor.repository.PartidaRepository;
import br.edu.unifor.missaomarte.servidor.repository.UsuarioRepository;

class RankingApiTest extends ApiTeste {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 11, 1, 10, 0, 0);

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private PartidaRepository partidas;

    private String token;

    @BeforeEach
    void entrar() throws Exception {
        token = tokenDe("consulta");
    }

    @Test
    void rankingVazio() throws Exception {
        ranking().andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void umaLinhaPorJogadorComAMelhorVitoria() throws Exception {
        Usuario ana = jogador("ana");
        Usuario bia = jogador("bia");
        vitoria(ana, 50, 60, BASE);
        vitoria(ana, 80, 60, BASE.plusHours(1));
        vitoria(bia, 70, 60, BASE);
        ranking().andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].posicao").value(1))
                .andExpect(jsonPath("$[0].nome").value("ana"))
                .andExpect(jsonPath("$[0].pontuacao").value(80))
                .andExpect(jsonPath("$[1].posicao").value(2))
                .andExpect(jsonPath("$[1].nome").value("bia"))
                .andExpect(jsonPath("$[1].dataHora").value("2026-11-01T10:00:00"));
    }

    @Test
    void derrotasEPontuacaoNaoPositivaFicamFora() throws Exception {
        Usuario caio = jogador("caio");
        Usuario duda = jogador("duda");
        partidas.save(new Partida(caio, Dificuldade.FACIL, Resultado.DERROTA, 500, 60, 10, BASE));
        vitoria(duda, 0, 60, BASE);
        vitoria(duda, -10, 60, BASE);
        ranking().andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void empateDesfeitoPelaMenorDuracaoDepoisPelaPartidaMaisAntiga() throws Exception {
        Usuario lento = jogador("lento");
        Usuario rapido = jogador("rapido");
        Usuario novo = jogador("novo");
        Usuario antigo = jogador("antigo");
        vitoria(lento, 100, 90, BASE);
        vitoria(rapido, 100, 30, BASE.plusDays(5));
        vitoria(novo, 100, 60, BASE.plusDays(2));
        vitoria(antigo, 100, 60, BASE.plusDays(1));
        ranking().andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].nome").value("rapido"))
                .andExpect(jsonPath("$[1].nome").value("antigo"))
                .andExpect(jsonPath("$[2].nome").value("novo"))
                .andExpect(jsonPath("$[3].nome").value("lento"));
    }

    @Test
    void duasVitoriasIguaisDoMesmoJogadorDaoUmaLinhaSo() throws Exception {
        Usuario eva = jogador("eva");
        vitoria(eva, 90, 40, BASE);
        vitoria(eva, 90, 40, BASE);
        ranking().andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void limiteDe10Linhas() throws Exception {
        for (int i = 1; i <= 12; i++) {
            vitoria(jogador("piloto" + i), i * 10, 60, BASE);
        }
        ranking().andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].pontuacao").value(120))
                .andExpect(jsonPath("$[9].posicao").value(10))
                .andExpect(jsonPath("$[9].pontuacao").value(30));
    }

    @Test
    void semDadosSensiveis() throws Exception {
        vitoria(jogador("fabi"), 40, 60, BASE);
        String resposta = corpo(ranking().andExpect(jsonPath("$", hasSize(1))));
        assertFalse(resposta.contains("hash"));
        assertFalse(resposta.contains("senha"));
        assertFalse(resposta.contains("$2a$"));
        assertFalse(resposta.contains("\"id\""));
    }

    private Usuario jogador(String nome) {
        return usuarios.save(new Usuario(nome, nome, "$2a$10$hash-que-nao-pode-aparecer", BASE));
    }

    private void vitoria(Usuario jogador, int pontuacao, long duracao, LocalDateTime quando) {
        partidas.save(new Partida(jogador, Dificuldade.MEDIO, Resultado.VITORIA, pontuacao, duracao, 25, quando));
    }

    private ResultActions ranking() throws Exception {
        return mvc.perform(get("/api/ranking").header("Authorization", "Bearer " + token));
    }
}
