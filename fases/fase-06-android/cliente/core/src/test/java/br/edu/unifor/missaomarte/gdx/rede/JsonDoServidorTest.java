package br.edu.unifor.missaomarte.gdx.rede;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonDoServidorTest {

    private final JsonDoServidor json = new JsonDoServidor();

    @Test
    void credenciaisViramJsonValido() {
        JsonValue lido = new JsonReader().parse(json.credenciais("piloto_1", "segredo123"));

        assertEquals("piloto_1", lido.getString("nome"));
        assertEquals("segredo123", lido.getString("senha"));
    }

    @Test
    void aspasEBarraNaSenhaNaoQuebramAEstrutura() {
        String texto = json.credenciais("piloto_1", "a\"b\\c");

        assertTrue(texto.contains("\"senha\":\"a\\\"b\\\\c\""), texto);
        JsonValue lido = new JsonReader().parse(texto);
        assertEquals("a\"b\\c", lido.getString("senha"));
        assertEquals(2, lido.size);
    }

    @Test
    void partidaViraJsonComNumerosSemAspas() {
        String texto = json.partida(new PartidaConcluida(Dificuldade.DIFICIL, false, -15, 42, 30));

        assertEquals("{\"dificuldade\":\"DIFICIL\",\"resultado\":\"DERROTA\",\"pontuacao\":-15,"
                + "\"duracaoSegundos\":42,\"movimentos\":30}", texto);
    }

    @Test
    void vitoriaViraVITORIA() {
        String texto = json.partida(new PartidaConcluida(Dificuldade.FACIL, true, 50, 10, 12));

        assertTrue(texto.contains("\"resultado\":\"VITORIA\""), texto);
    }

    @Test
    void leOToken() {
        String corpo = "{\"token\":\"eyJabc.def.ghi\",\"tipo\":\"Bearer\",\"expiraEm\":\"2026-11-21T16:03:05\"}";

        assertEquals("eyJabc.def.ghi", json.lerToken(corpo));
    }

    @Test
    void tokenAusenteEErro() {
        assertThrows(IllegalArgumentException.class, () -> json.lerToken("{\"tipo\":\"Bearer\"}"));
        assertThrows(IllegalArgumentException.class, () -> json.lerToken("{\"token\":\"\"}"));
        assertThrows(IllegalArgumentException.class, () -> json.lerToken(""));
    }

    @Test
    void leOHistorico() {
        String corpo = "[{\"id\":7,\"dificuldade\":\"MEDIO\",\"resultado\":\"DERROTA\",\"pontuacao\":-3,"
                + "\"duracaoSegundos\":25,\"movimentos\":23,\"dataHora\":\"2026-11-21T14:03:05\"},"
                + "{\"id\":6,\"dificuldade\":\"FACIL\",\"resultado\":\"VITORIA\",\"pontuacao\":48,"
                + "\"duracaoSegundos\":30,\"movimentos\":22,\"dataHora\":\"2026-11-21T13:59:00\"}]";

        List<PartidaRegistrada> partidas = json.lerHistorico(corpo);

        assertEquals(2, partidas.size());
        assertEquals(new PartidaRegistrada(7, Dificuldade.MEDIO, false, -3, 25, 23, "2026-11-21T14:03:05"),
                partidas.get(0));
        assertTrue(partidas.get(1).vitoria());
    }

    @Test
    void leORanking() {
        String corpo = "[{\"posicao\":1,\"nome\":\"piloto_1\",\"pontuacao\":120,\"dificuldade\":\"DIFICIL\","
                + "\"duracaoSegundos\":40,\"movimentos\":18,\"dataHora\":\"2026-11-21T14:03:05\"}]";

        List<LinhaDoRanking> linhas = json.lerRanking(corpo);

        assertEquals(List.of(new LinhaDoRanking(1, "piloto_1", 120, Dificuldade.DIFICIL, 40, 18,
                "2026-11-21T14:03:05")), linhas);
    }

    @Test
    void listasVaziasViramListaVazia() {
        assertTrue(json.lerRanking("[]").isEmpty());
        assertTrue(json.lerHistorico("[]").isEmpty());
    }

    @Test
    void leAPartidaRegistrada() {
        String corpo = "{\"id\":9,\"dificuldade\":\"FACIL\",\"resultado\":\"VITORIA\",\"pontuacao\":48,"
                + "\"duracaoSegundos\":30,\"movimentos\":22,\"dataHora\":\"2026-11-21T13:59:00\"}";

        assertEquals(9, json.lerPartida(corpo).id());
    }

    @Test
    void listaQueNaoELista() {
        assertThrows(IllegalArgumentException.class, () -> json.lerRanking("{\"posicao\":1}"));
        assertThrows(IllegalArgumentException.class, () -> json.lerHistorico("<html>erro</html>"));
    }

    @Test
    void mensagemDoPrimeiroErroDeCampo() {
        String corpo = "{\"status\":400,\"title\":\"Bad Request\",\"detail\":\"Dados inválidos.\","
                + "\"erros\":[{\"campo\":\"nome\",\"mensagem\":\"O nome deve ter de 3 a 20 caracteres.\"},"
                + "{\"campo\":\"senha\",\"mensagem\":\"Senha curta.\"}]}";

        assertEquals("O nome deve ter de 3 a 20 caracteres.", json.mensagemDeErro(400, corpo));
    }

    @Test
    void mensagemDoDetailSemErroDeCampo() {
        String corpo = "{\"status\":409,\"title\":\"Conflict\",\"detail\":\"Já existe um usuário com esse nome.\"}";

        assertEquals("Já existe um usuário com esse nome.", json.mensagemDeErro(409, corpo));
    }

    @Test
    void mensagemPorCodigoSemProblemDetail() {
        assertEquals(JsonDoServidor.NAO_AUTORIZADO, json.mensagemDeErro(401, ""));
        assertEquals(JsonDoServidor.ERRO_NO_SERVIDOR, json.mensagemDeErro(500, null));
        assertEquals(JsonDoServidor.ERRO_NO_SERVIDOR, json.mensagemDeErro(503, "{}"));
        assertEquals("O servidor recusou o pedido (código 404).", json.mensagemDeErro(404, "{\"erros\":[]}"));
    }

    @Test
    void corpoQueNaoEJsonNaoVaza() {
        String html = "<html><body>Whitelabel Error Page stack trace</body></html>";

        String mensagem = json.mensagemDeErro(502, html);

        assertEquals(JsonDoServidor.ERRO_NO_SERVIDOR, mensagem);
        assertFalse(mensagem.contains("Whitelabel"));
    }

    @Test
    void jsonCortadoNaoDerrubaECaiNoTextoPorCodigo() {
        assertEquals(JsonDoServidor.NAO_AUTORIZADO, json.mensagemDeErro(401, "{\"detail\":\"Usu"));
        assertThrows(IllegalArgumentException.class, () -> json.lerToken("{\"token\":\"eyJ"));
    }
}
