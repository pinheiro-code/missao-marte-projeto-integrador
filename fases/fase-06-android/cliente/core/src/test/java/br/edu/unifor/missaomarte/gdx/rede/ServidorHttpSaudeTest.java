package br.edu.unifor.missaomarte.gdx.rede;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.servidor.Resposta;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** O Testar conexão: GET /api/saude no endereço do formulário, com um Net falso (sem rede). */
class ServidorHttpSaudeTest {

    private final NetFalso net = new NetFalso();
    private final List<Runnable> executados = new ArrayList<>();
    private final ServidorHttp servidor = new ServidorHttp(net, r -> {
        executados.add(r);
        r.run();
    }, () -> "http://endereco-salvo:8080");
    private final List<Resposta<Boolean>> recebidas = new ArrayList<>();

    @Test
    void testaOEnderecoPedidoENaoOSalvo() {
        net.status = 200;
        net.corpo = "{\"status\":\"ok\"}";

        servidor.testarConexao("http://192.168.0.10:8080", recebidas::add);

        HttpRequest pedido = net.pedidos.get(0);
        assertEquals("GET", pedido.getMethod());
        assertEquals("http://192.168.0.10:8080/api/saude", pedido.getUrl());
        assertNull(pedido.getHeaders().get("Authorization"));
        assertEquals(ServidorHttp.TEMPO_LIMITE_MS, pedido.getTimeOut());
        assertTrue(recebidas.get(0).ok());
        assertEquals(1, executados.size());
    }

    @Test
    void outroProgramaNaPortaNaoPassa() {
        net.status = 200;
        net.corpo = "<html>roteador</html>";

        servidor.testarConexao("http://192.168.0.1:80", recebidas::add);

        assertEquals(JsonDoServidor.RESPOSTA_INVALIDA, recebidas.get(0).erro());
    }

    @Test
    void servidorDesligadoViraMensagemSemExcecao() {
        net.falhar = true;

        servidor.testarConexao("http://10.255.255.1:8080", recebidas::add);

        assertFalse(recebidas.get(0).ok());
        assertEquals(JsonDoServidor.SEM_CONEXAO, recebidas.get(0).erro());
        assertEquals(1, executados.size());
    }

    @Test
    void statusDeErroVaiParaATela() {
        net.status = 404;
        net.corpo = "";

        servidor.testarConexao("http://localhost:8080", recebidas::add);

        assertFalse(recebidas.get(0).ok());
    }

    private static final class NetFalso implements Net {

        final List<HttpRequest> pedidos = new ArrayList<>();
        int status;
        String corpo;
        boolean falhar;

        @Override
        public void sendHttpRequest(HttpRequest pedido, HttpResponseListener ouvinte) {
            pedidos.add(pedido);
            if (falhar) {
                ouvinte.failed(new java.net.ConnectException("Connection refused"));
            } else {
                ouvinte.handleHttpResponse(new RespostaFalsa(status, corpo));
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
            return Collections.emptyMap();
        }
    }
}
