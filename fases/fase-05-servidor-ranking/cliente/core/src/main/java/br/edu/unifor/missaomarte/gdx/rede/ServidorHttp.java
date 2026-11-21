package br.edu.unifor.missaomarte.gdx.rede;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import br.edu.unifor.missaomarte.application.servidor.Retorno;
import br.edu.unifor.missaomarte.application.servidor.ServidorDoJogo;
import br.edu.unifor.missaomarte.application.servidor.Sessao;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Adaptador HTTP da porta ServidorDoJogo, com o Net do libGDX. O Net responde em outra thread:
 * todo desfecho (resposta, falha ou cancelamento) volta pelo executor, que no jogo é o postRunnable.
 * Nenhuma falha de rede sai daqui como exceção.
 */
public class ServidorHttp implements ServidorDoJogo {

    static final int TEMPO_LIMITE_MS = 5000;
    private static final int TAMANHO_DO_HISTORICO = 10;

    private final Net net;
    private final Executor naThreadDoJogo;
    private final Supplier<String> endereco;
    private final JsonDoServidor json = new JsonDoServidor();

    /** O endereço é lido a cada chamada, para dar para trocar de servidor sem recriar o adaptador. */
    public ServidorHttp(Net net, Executor naThreadDoJogo, Supplier<String> endereco) {
        this.net = net;
        this.naThreadDoJogo = naThreadDoJogo;
        this.endereco = endereco;
    }

    @Override
    public void cadastrar(String nome, String senha, Retorno<Sessao> retorno) {
        Retorno<Boolean> depoisDoCadastro = resposta -> {
            if (resposta.ok()) {
                entrar(nome, senha, retorno);
            } else {
                retorno.receber(resposta.mesmoErro());
            }
        };
        enviar(HttpMethods.POST, "/api/usuarios", json.credenciais(nome, senha), null,
                corpo -> Boolean.TRUE, depoisDoCadastro);
    }

    @Override
    public void entrar(String nome, String senha, Retorno<Sessao> retorno) {
        enviar(HttpMethods.POST, "/api/auth/login", json.credenciais(nome, senha), null,
                corpo -> new Sessao(nome, json.lerToken(corpo)), retorno);
    }

    @Override
    public void enviarPartida(Sessao sessao, PartidaConcluida partida, Retorno<PartidaRegistrada> retorno) {
        enviar(HttpMethods.POST, "/api/partidas", json.partida(partida), sessao, json::lerPartida, retorno);
    }

    @Override
    public void carregarRanking(Sessao sessao, Retorno<List<LinhaDoRanking>> retorno) {
        enviar(HttpMethods.GET, "/api/ranking", null, sessao, json::lerRanking, retorno);
    }

    @Override
    public void carregarHistorico(Sessao sessao, Retorno<List<PartidaRegistrada>> retorno) {
        enviar(HttpMethods.GET, "/api/partidas/minhas?pagina=0&tamanho=" + TAMANHO_DO_HISTORICO, null, sessao,
                json::lerHistorico, retorno);
    }

    /** sessao null: rota pública, sem Authorization. corpo null: sem conteúdo. */
    private <T> void enviar(String metodo, String caminho, String corpo, Sessao sessao,
                            Function<String, T> conversor, Retorno<T> retorno) {
        HttpRequest pedido = new HttpRequest(metodo);
        pedido.setUrl(endereco.get() + caminho);
        pedido.setTimeOut(TEMPO_LIMITE_MS);
        pedido.setHeader("Accept", "application/json");
        if (corpo != null) {
            pedido.setHeader("Content-Type", "application/json");
            pedido.setContent(corpo);
        }
        if (sessao != null) {
            pedido.setHeader("Authorization", "Bearer " + sessao.token());
        }
        net.sendHttpRequest(pedido, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse resposta) {
                // o corpo é lido aqui: a conexão fecha quando este método volta
                Resposta<T> pronta = converter(resposta.getStatus().getStatusCode(), resposta.getResultAsString(),
                        conversor);
                naThreadDoJogo.execute(() -> retorno.receber(pronta));
            }

            @Override
            public void failed(Throwable causa) {
                naThreadDoJogo.execute(() -> retorno.receber(Resposta.erro(JsonDoServidor.SEM_CONEXAO)));
            }

            @Override
            public void cancelled() {
                naThreadDoJogo.execute(() -> retorno.receber(Resposta.erro(JsonDoServidor.SEM_CONEXAO)));
            }
        });
    }

    private <T> Resposta<T> converter(int status, String corpo, Function<String, T> conversor) {
        if (status < 100) {
            // o Net do desktop entrega status -1, e não failed, quando o servidor está desligado
            return Resposta.erro(JsonDoServidor.SEM_CONEXAO);
        }
        if (status >= 200 && status < 300) {
            try {
                return Resposta.ok(conversor.apply(corpo));
            } catch (RuntimeException e) {
                // campo ausente, tipo trocado ou corpo que não é JSON: não derruba o jogo
                return Resposta.erro(JsonDoServidor.RESPOSTA_INVALIDA);
            }
        }
        String mensagem = json.mensagemDeErro(status, corpo);
        return status == 401 ? Resposta.naoAutorizado(mensagem) : Resposta.erro(mensagem);
    }
}
