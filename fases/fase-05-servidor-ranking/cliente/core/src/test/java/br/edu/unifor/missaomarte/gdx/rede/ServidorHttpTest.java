package br.edu.unifor.missaomarte.gdx.rede;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import br.edu.unifor.missaomarte.application.servidor.Sessao;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** O adaptador com um Net falso (respostas prontas, sem rede) e um executor que roda na hora. */
class ServidorHttpTest {

    private static final Sessao SESSAO = new Sessao("piloto_1", "tok123");
    private static final String PARTIDA_JSON = "{\"id\":3,\"dificuldade\":\"MEDIO\",\"resultado\":\"VITORIA\","
            + "\"pontuacao\":120,\"duracaoSegundos\":40,\"movimentos\":18,\"dataHora\":\"2026-11-21T14:03:05\"}";

    private final NetFalso net = new NetFalso();
    private final List<Runnable> executados = new ArrayList<>();
    private String endereco = "http://localhost:8080";
    private final ServidorHttp servidor = new ServidorHttp(net, r -> {
        executados.add(r);
        r.run();
    }, () -> endereco);

    private final List<Resposta<?>> recebidas = new ArrayList<>();

    @Test
    void entrarFazPostNoLoginEDevolveASessao() {
        net.responder(200, "{\"token\":\"eyJnovo\",\"tipo\":\"Bearer\",\"expiraEm\":\"2026-11-21T16:00:00\"}");

        servidor.entrar("piloto_1", "a\"b\\c", recebidas::add);

        HttpRequest pedido = net.pedidos.get(0);
        assertEquals("POST", pedido.getMethod());
        assertEquals("http://localhost:8080/api/auth/login", pedido.getUrl());
        assertEquals("application/json", pedido.getHeaders().get("Content-Type"));
        assertNull(pedido.getHeaders().get("Authorization"));
        assertEquals(ServidorHttp.TEMPO_LIMITE_MS, pedido.getTimeOut());
        assertEquals("{\"nome\":\"piloto_1\",\"senha\":\"a\\\"b\\\\c\"}", pedido.getContent());
        Sessao sessao = (Sessao) recebidas.get(0).valor();
        assertEquals("piloto_1", sessao.nome());
        assertEquals("eyJnovo", sessao.token());
        assertEquals(1, executados.size());
    }

    @Test
    void cadastrarCriaAContaEJaEntra() {
        net.responder(201, "{\"id\":1,\"nome\":\"piloto_1\"}");
        net.responder(200, "{\"token\":\"eyJnovo\",\"tipo\":\"Bearer\",\"expiraEm\":\"2026-11-21T16:00:00\"}");

        servidor.cadastrar("piloto_1", "segredo123", recebidas::add);

        assertEquals("http://localhost:8080/api/usuarios", net.pedidos.get(0).getUrl());
        assertEquals("POST", net.pedidos.get(0).getMethod());
        assertEquals("http://localhost:8080/api/auth/login", net.pedidos.get(1).getUrl());
        assertTrue(recebidas.get(0).ok());
        assertEquals("eyJnovo", ((Sessao) recebidas.get(0).valor()).token());
    }

    @Test
    void cadastroRecusadoNaoTentaEntrar() {
        net.responder(409, "{\"status\":409,\"detail\":\"Já existe um usuário com esse nome.\"}");

        servidor.cadastrar("piloto_1", "segredo123", recebidas::add);

        assertEquals(1, net.pedidos.size());
        assertEquals("Já existe um usuário com esse nome.", recebidas.get(0).erro());
    }

    @Test
    void erroDeCampoDoCadastroVaiParaATela() {
        net.responder(400, "{\"status\":400,\"detail\":\"Dados inválidos.\","
                + "\"erros\":[{\"campo\":\"nome\",\"mensagem\":\"O nome deve ter de 3 a 20 caracteres.\"}]}");

        servidor.cadastrar("ab", "segredo123", recebidas::add);

        assertEquals("O nome deve ter de 3 a 20 caracteres.", recebidas.get(0).erro());
        assertFalse(recebidas.get(0).naoAutorizado());
    }

    @Test
    void enviarPartidaLevaOTokenEOCorpo() {
        net.responder(201, PARTIDA_JSON);

        servidor.enviarPartida(SESSAO, new PartidaConcluida(Dificuldade.MEDIO, true, 120, 40, 18), recebidas::add);

        HttpRequest pedido = net.pedidos.get(0);
        assertEquals("POST", pedido.getMethod());
        assertEquals("http://localhost:8080/api/partidas", pedido.getUrl());
        assertEquals("Bearer tok123", pedido.getHeaders().get("Authorization"));
        assertEquals("application/json", pedido.getHeaders().get("Content-Type"));
        assertTrue(pedido.getContent().contains("\"resultado\":\"VITORIA\""));
        assertEquals(3, ((PartidaRegistrada) recebidas.get(0).valor()).id());
    }

    @Test
    void carregarRankingFazGetComToken() {
        net.responder(200, "[{\"posicao\":1,\"nome\":\"piloto_1\",\"pontuacao\":120,\"dificuldade\":\"MEDIO\","
                + "\"duracaoSegundos\":40,\"movimentos\":18,\"dataHora\":\"2026-11-21T14:03:05\"}]");

        servidor.carregarRanking(SESSAO, recebidas::add);

        HttpRequest pedido = net.pedidos.get(0);
        assertEquals("GET", pedido.getMethod());
        assertEquals("http://localhost:8080/api/ranking", pedido.getUrl());
        assertEquals("Bearer tok123", pedido.getHeaders().get("Authorization"));
        assertNull(pedido.getHeaders().get("Content-Type"));
        assertNull(pedido.getContent());
        List<?> linhas = (List<?>) recebidas.get(0).valor();
        assertEquals(1, linhas.size());
        assertEquals("piloto_1", ((LinhaDoRanking) linhas.get(0)).nome());
    }

    @Test
    void carregarHistoricoPedeAsUltimasDez() {
        net.responder(200, "[]");

        servidor.carregarHistorico(SESSAO, recebidas::add);

        assertEquals("http://localhost:8080/api/partidas/minhas?pagina=0&tamanho=10", net.pedidos.get(0).getUrl());
        assertEquals("GET", net.pedidos.get(0).getMethod());
        assertTrue(((List<?>) recebidas.get(0).valor()).isEmpty());
    }

    @Test
    void naoAutorizadoGanhaAMarca() {
        net.responder(401, "{\"status\":401,\"detail\":\"Token ausente, inválido ou expirado.\"}");

        servidor.carregarRanking(SESSAO, recebidas::add);

        assertTrue(recebidas.get(0).naoAutorizado());
        assertEquals("Token ausente, inválido ou expirado.", recebidas.get(0).erro());
    }

    @Test
    void naoAutorizadoComCorpoVazioCaiNoTextoPorCodigo() {
        net.responder(401, "");

        servidor.carregarHistorico(SESSAO, recebidas::add);

        assertTrue(recebidas.get(0).naoAutorizado());
        assertEquals(JsonDoServidor.NAO_AUTORIZADO, recebidas.get(0).erro());
    }

    @Test
    void loginComSenhaErradaMostraODetail() {
        net.responder(401, "{\"status\":401,\"detail\":\"Usuário ou senha inválidos.\"}");

        servidor.entrar("piloto_1", "errada123", recebidas::add);

        assertEquals("Usuário ou senha inválidos.", recebidas.get(0).erro());
    }

    @Test
    void falhaDeConexaoViraMensagemSemExcecao() {
        net.falhar();

        servidor.entrar("piloto_1", "segredo123", recebidas::add);

        assertFalse(recebidas.get(0).ok());
        assertEquals("Não foi possível conectar ao servidor.", recebidas.get(0).erro());
        assertEquals(1, executados.size());
    }

    @Test
    void statusMenosUmDoDesktopViraFalhaDeConexao() {
        // medido na janela: com o servidor parado, o Net do LWJGL3 respondeu status -1 em vez de failed
        net.responder(-1, "");

        servidor.enviarPartida(SESSAO, new PartidaConcluida(Dificuldade.MEDIO, false, 10, 5, 3), recebidas::add);

        assertEquals("Não foi possível conectar ao servidor.", recebidas.get(0).erro());
        assertFalse(recebidas.get(0).naoAutorizado());
    }

    @Test
    void cancelamentoTambemVoltaPeloExecutor() {
        net.cancelar();

        servidor.carregarRanking(SESSAO, recebidas::add);

        assertEquals(JsonDoServidor.SEM_CONEXAO, recebidas.get(0).erro());
        assertEquals(1, executados.size());
    }

    @Test
    void sucessoComCorpoInvalidoNaoDerrubaOJogo() {
        net.responder(200, "<html>proxy</html>");

        servidor.carregarRanking(SESSAO, recebidas::add);

        assertEquals(JsonDoServidor.RESPOSTA_INVALIDA, recebidas.get(0).erro());
    }

    @Test
    void sucessoComCampoTrocadoNaoDerrubaOJogo() {
        net.responder(200, "[{\"posicao\":1,\"nome\":\"x\",\"pontuacao\":1,\"dificuldade\":\"IMPOSSIVEL\","
                + "\"duracaoSegundos\":1,\"movimentos\":1,\"dataHora\":\"2026-11-21T14:03:05\"}]");

        servidor.carregarRanking(SESSAO, recebidas::add);

        assertEquals(JsonDoServidor.RESPOSTA_INVALIDA, recebidas.get(0).erro());
    }

    @Test
    void enderecoELidoACadaChamada() {
        net.responder(200, "[]");
        net.responder(200, "[]");

        servidor.carregarRanking(SESSAO, recebidas::add);
        endereco = "http://192.168.0.10:8080";
        servidor.carregarRanking(SESSAO, recebidas::add);

        assertEquals("http://192.168.0.10:8080/api/ranking", net.pedidos.get(1).getUrl());
    }

    /** Responde na hora, na mesma thread, com o que o teste deixou na fila. */
    private static final class NetFalso implements Net {

        final List<HttpRequest> pedidos = new ArrayList<>();
        private final Deque<Object[]> respostas = new ArrayDeque<>();

        void responder(int status, String corpo) {
            respostas.add(new Object[] {status, corpo});
        }

        void falhar() {
            respostas.add(new Object[] {"falhar"});
        }

        void cancelar() {
            respostas.add(new Object[] {"cancelar"});
        }

        @Override
        public void sendHttpRequest(HttpRequest pedido, HttpResponseListener ouvinte) {
            pedidos.add(pedido);
            Object[] proxima = respostas.remove();
            if ("falhar".equals(proxima[0])) {
                ouvinte.failed(new java.net.ConnectException("Connection refused"));
            } else if ("cancelar".equals(proxima[0])) {
                ouvinte.cancelled();
            } else {
                ouvinte.handleHttpResponse(new RespostaFalsa((Integer) proxima[0], (String) proxima[1]));
            }
        }

        @Override
        public void cancelHttpRequest(HttpRequest pedido) {
        }

        @Override
        public ServerSocket newServerSocket(Protocol protocolo, String host, int porta, ServerSocketHints dicas) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ServerSocket newServerSocket(Protocol protocolo, int porta, ServerSocketHints dicas) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Socket newClientSocket(Protocol protocolo, String host, int porta, SocketHints dicas) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean openURI(String uri) {
            return false;
        }
    }

    private record RespostaFalsa(int status, String corpo) implements HttpResponse {

        @Override
        public byte[] getResult() {
            return corpo.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public String getResultAsString() {
            return corpo;
        }

        @Override
        public InputStream getResultAsStream() {
            return new ByteArrayInputStream(getResult());
        }

        @Override
        public HttpStatus getStatus() {
            return new HttpStatus(status);
        }

        @Override
        public String getHeader(String nome) {
            return null;
        }

        @Override
        public Map<String, List<String>> getHeaders() {
            return Map.of();
        }
    }
}
