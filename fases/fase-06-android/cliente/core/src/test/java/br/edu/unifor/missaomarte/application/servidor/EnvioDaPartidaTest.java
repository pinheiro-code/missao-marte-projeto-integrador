package br.edu.unifor.missaomarte.application.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** O envio contra um servidor falso que guarda o retorno para o teste responder quando quiser. */
class EnvioDaPartidaTest {

    private static final Sessao SESSAO = new Sessao("piloto_1", "token");
    private static final PartidaConcluida PARTIDA = new PartidaConcluida(Dificuldade.MEDIO, true, 120, 40, 18);
    private static final PartidaRegistrada REGISTRADA =
            new PartidaRegistrada(1, Dificuldade.MEDIO, true, 120, 40, 18, "2026-11-21T14:03:05");

    private final ServidorFalso servidor = new ServidorFalso();
    private int expirou;

    private EnvioDaPartida novoEnvio() {
        return new EnvioDaPartida(servidor, SESSAO, PARTIDA, () -> expirou++);
    }

    @Test
    void comecaEnviandoAPartidaDaSessao() {
        EnvioDaPartida envio = novoEnvio();

        assertEquals(EnvioDaPartida.Estado.ENVIANDO, envio.estado());
        assertEquals(1, servidor.pendentes.size());
        assertSame(SESSAO, servidor.sessoes.get(0));
        assertSame(PARTIDA, servidor.partidas.get(0));
    }

    @Test
    void naoDeixaContinuarEnquantoEnvia() {
        EnvioDaPartida envio = novoEnvio();

        assertFalse(envio.podeContinuar());
        assertFalse(envio.reenviar());
        assertEquals(1, servidor.pendentes.size());
    }

    @Test
    void respostaOkViraEnviado() {
        EnvioDaPartida envio = novoEnvio();

        servidor.responder(Resposta.ok(REGISTRADA));

        assertEquals(EnvioDaPartida.Estado.ENVIADO, envio.estado());
        assertTrue(envio.podeContinuar());
        assertNull(envio.erro());
        assertFalse(envio.reenviar());
    }

    @Test
    void falhaGuardaOMotivoEPermiteReenviar() {
        EnvioDaPartida envio = novoEnvio();

        servidor.responder(Resposta.erro("Não foi possível conectar ao servidor."));

        assertEquals(EnvioDaPartida.Estado.FALHOU, envio.estado());
        assertEquals("Não foi possível conectar ao servidor.", envio.erro());
        assertTrue(envio.podeContinuar());
        assertEquals(0, expirou);
    }

    @Test
    void reenviarDepoisDaFalhaEnviaDeNovo() {
        EnvioDaPartida envio = novoEnvio();
        servidor.responder(Resposta.erro("Não foi possível conectar ao servidor."));

        assertTrue(envio.reenviar());

        assertEquals(EnvioDaPartida.Estado.ENVIANDO, envio.estado());
        assertNull(envio.erro());
        assertFalse(envio.podeContinuar());
        servidor.responder(Resposta.ok(REGISTRADA));
        assertEquals(EnvioDaPartida.Estado.ENVIADO, envio.estado());
        assertEquals(2, servidor.partidas.size());
    }

    @Test
    void naoAutorizadoAvisaSessaoExpirada() {
        EnvioDaPartida envio = novoEnvio();

        servidor.responder(Resposta.naoAutorizado("Token ausente, inválido ou expirado."));

        assertEquals(1, expirou);
        assertEquals(EnvioDaPartida.Estado.FALHOU, envio.estado());
    }

    private static final class ServidorFalso implements ServidorDoJogo {

        final List<Retorno<PartidaRegistrada>> pendentes = new ArrayList<>();
        final List<Sessao> sessoes = new ArrayList<>();
        final List<PartidaConcluida> partidas = new ArrayList<>();

        void responder(Resposta<PartidaRegistrada> resposta) {
            pendentes.remove(0).receber(resposta);
        }

        @Override
        public void enviarPartida(Sessao sessao, PartidaConcluida partida, Retorno<PartidaRegistrada> retorno) {
            sessoes.add(sessao);
            partidas.add(partida);
            pendentes.add(retorno);
        }

        @Override
        public void cadastrar(String nome, String senha, Retorno<Sessao> retorno) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void entrar(String nome, String senha, Retorno<Sessao> retorno) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void carregarRanking(Sessao sessao, Retorno<List<LinhaDoRanking>> retorno) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void carregarHistorico(Sessao sessao, Retorno<List<PartidaRegistrada>> retorno) {
            throw new UnsupportedOperationException();
        }
    }
}
