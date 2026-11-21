package br.edu.unifor.missaomarte.gdx.rede;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Só conversão entre objetos e o JSON do servidor, sem rede nem OpenGL.
 * O JSON de saída é sempre do JsonWriter, que escapa aspas e barras: nunca concatenação de texto.
 * Na leitura, resposta inválida vira IllegalArgumentException, que o adaptador transforma em mensagem.
 */
public class JsonDoServidor {

    static final String SEM_CONEXAO = "Não foi possível conectar ao servidor.";
    static final String RESPOSTA_INVALIDA = "Resposta inválida do servidor.";
    static final String NAO_AUTORIZADO = "Não autorizado. Entre de novo.";
    static final String ERRO_NO_SERVIDOR = "O servidor teve um erro. Tente de novo mais tarde.";
    static final String ERRO_INESPERADO = "O servidor recusou o pedido (código ";

    public String credenciais(String nome, String senha) {
        StringWriter texto = new StringWriter();
        try (JsonWriter json = novoEscritor(texto)) {
            json.object().set("nome", nome).set("senha", senha).pop();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return texto.toString();
    }

    public String partida(PartidaConcluida partida) {
        StringWriter texto = new StringWriter();
        try (JsonWriter json = novoEscritor(texto)) {
            json.object()
                    .set("dificuldade", partida.dificuldade().name())
                    .set("resultado", partida.vitoria() ? "VITORIA" : "DERROTA")
                    .set("pontuacao", partida.pontuacao())
                    .set("duracaoSegundos", partida.duracaoSegundos())
                    .set("movimentos", partida.movimentos())
                    .pop();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return texto.toString();
    }

    private static JsonWriter novoEscritor(StringWriter texto) {
        JsonWriter json = new JsonWriter(texto);
        json.setOutputType(JsonWriter.OutputType.json);
        return json;
    }

    public String lerToken(String corpo) {
        String token = objeto(corpo).getString("token", null);
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Resposta do login sem token.");
        }
        return token;
    }

    public PartidaRegistrada lerPartida(String corpo) {
        return partida(objeto(corpo));
    }

    public List<PartidaRegistrada> lerHistorico(String corpo) {
        List<PartidaRegistrada> partidas = new ArrayList<>();
        for (JsonValue item = lista(corpo).child; item != null; item = item.next) {
            partidas.add(partida(item));
        }
        return partidas;
    }

    public List<LinhaDoRanking> lerRanking(String corpo) {
        List<LinhaDoRanking> linhas = new ArrayList<>();
        for (JsonValue item = lista(corpo).child; item != null; item = item.next) {
            linhas.add(new LinhaDoRanking(item.getInt("posicao"), item.getString("nome"), item.getInt("pontuacao"),
                    Dificuldade.valueOf(item.getString("dificuldade")), item.getLong("duracaoSegundos"),
                    item.getInt("movimentos"), item.getString("dataHora")));
        }
        return linhas;
    }

    private static PartidaRegistrada partida(JsonValue item) {
        return new PartidaRegistrada(item.getLong("id"), Dificuldade.valueOf(item.getString("dificuldade")),
                "VITORIA".equals(item.getString("resultado")), item.getInt("pontuacao"),
                item.getLong("duracaoSegundos"), item.getInt("movimentos"), item.getString("dataHora"));
    }

    /**
     * A mensagem para o jogador: o primeiro erro de campo, senão o detail do ProblemDetail,
     * senão um texto pelo código. O corpo bruto nunca vai para a tela.
     */
    public String mensagemDeErro(int status, String corpo) {
        JsonValue raiz = ler(corpo);
        if (raiz != null && raiz.isObject()) {
            JsonValue erros = raiz.get("erros");
            if (erros != null && erros.isArray() && erros.child != null && erros.child.isObject()) {
                String mensagem = erros.child.getString("mensagem", null);
                if (mensagem != null && !mensagem.isEmpty()) {
                    return mensagem;
                }
            }
            String detail = raiz.getString("detail", null);
            if (detail != null && !detail.isEmpty()) {
                return detail;
            }
        }
        if (status == 401) {
            return NAO_AUTORIZADO;
        }
        if (status >= 500) {
            return ERRO_NO_SERVIDOR;
        }
        return ERRO_INESPERADO + status + ").";
    }

    private static JsonValue objeto(String corpo) {
        JsonValue raiz = ler(corpo);
        if (raiz == null || !raiz.isObject()) {
            throw new IllegalArgumentException(RESPOSTA_INVALIDA);
        }
        return raiz;
    }

    private static JsonValue lista(String corpo) {
        JsonValue raiz = ler(corpo);
        if (raiz == null || !raiz.isArray()) {
            throw new IllegalArgumentException(RESPOSTA_INVALIDA);
        }
        return raiz;
    }

    /** null para corpo vazio, cortado ou que não é JSON. */
    private static JsonValue ler(String corpo) {
        if (corpo == null || corpo.isEmpty()) {
            return null;
        }
        try {
            return new JsonReader().parse(corpo);
        } catch (SerializationException e) {
            return null;
        }
    }
}
