package br.edu.unifor.missaomarte.application.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Com um armazém em memória que conta as leituras e anota toda chave gravada. */
class EnderecoDoServidorTest {

    private final ArmazemEmMemoria armazem = new ArmazemEmMemoria();

    @Test
    void semNadaSalvoESemPadraoValeOLocalhost() {
        assertEquals("http://localhost:8080", new EnderecoDoServidor(armazem, null).get());
        assertEquals("http://localhost:8080", new EnderecoDoServidor(armazem, "  ").get());
    }

    @Test
    void semNadaSalvoValeOPadraoDoLancador() {
        EnderecoDoServidor endereco = new EnderecoDoServidor(armazem, "http://10.0.2.2:8080");

        assertEquals("http://10.0.2.2:8080", endereco.get());
        assertNull(endereco.avisoDoPadrao());
    }

    @Test
    void padraoInvalidoViraLocalhostComAviso() {
        EnderecoDoServidor endereco = new EnderecoDoServidor(armazem, "http://x:99999");

        assertEquals("http://localhost:8080", endereco.get());
        assertNotNull(endereco.avisoDoPadrao());
    }

    @Test
    void oSalvoGanhaDoPadraoDoLancador() {
        armazem.gravar(EnderecoDoServidor.CHAVE_ENDERECO, "192.168.0.10");
        armazem.gravar(EnderecoDoServidor.CHAVE_PORTA, "9090");
        armazem.gravar(EnderecoDoServidor.CHAVE_HTTPS, "false");

        assertEquals("http://192.168.0.10:9090", new EnderecoDoServidor(armazem, "http://10.0.2.2:8080").get());
    }

    @Test
    void soLeOArmazemNaPrimeiraChamadaEUmaVezSo() {
        EnderecoDoServidor endereco = new EnderecoDoServidor(armazem, null);
        assertEquals(0, armazem.leituras);

        endereco.get();
        int depoisDaPrimeira = armazem.leituras;
        endereco.get();
        endereco.atual();

        assertTrue(depoisDaPrimeira > 0);
        assertEquals(depoisDaPrimeira, armazem.leituras);
    }

    @Test
    void salvarValeNaHoraEDizSeMudou() {
        EnderecoDoServidor endereco = new EnderecoDoServidor(armazem, null);

        assertTrue(endereco.salvar(ConfiguracaoDoServidor.criar("192.168.0.10", 8080, false)));
        assertEquals("http://192.168.0.10:8080", endereco.get());
        assertFalse(endereco.salvar(ConfiguracaoDoServidor.criar("192.168.0.10", 8080, false)));
        assertEquals("192.168.0.10", armazem.ler(EnderecoDoServidor.CHAVE_ENDERECO));
        assertEquals("8080", armazem.ler(EnderecoDoServidor.CHAVE_PORTA));
        assertEquals("false", armazem.ler(EnderecoDoServidor.CHAVE_HTTPS));
    }

    @Test
    void oSalvoSobreviveAUmNovoEndereco() {
        new EnderecoDoServidor(armazem, null).salvar(ConfiguracaoDoServidor.criar("jogo.exemplo.com", 443, true));

        assertEquals("https://jogo.exemplo.com:443", new EnderecoDoServidor(armazem, null).get());
    }

    @Test
    void restaurarApagaOSalvoEVoltaAoPadrao() {
        EnderecoDoServidor endereco = new EnderecoDoServidor(armazem, "http://10.0.2.2:8080");
        endereco.salvar(ConfiguracaoDoServidor.criar("192.168.0.10", 8080, false));

        assertTrue(endereco.restaurarPadrao());
        assertEquals("http://10.0.2.2:8080", endereco.get());
        assertTrue(armazem.valores.isEmpty());
        assertFalse(endereco.restaurarPadrao());
    }

    @Test
    void valorSalvoInvalidoEIgnorado() {
        armazem.gravar(EnderecoDoServidor.CHAVE_ENDERECO, "256.1.1.1");
        armazem.gravar(EnderecoDoServidor.CHAVE_PORTA, "8080");
        armazem.gravar(EnderecoDoServidor.CHAVE_HTTPS, "false");
        assertEquals("http://localhost:8080", new EnderecoDoServidor(armazem, null).get());

        armazem.gravar(EnderecoDoServidor.CHAVE_ENDERECO, "192.168.0.10");
        armazem.gravar(EnderecoDoServidor.CHAVE_PORTA, "oito");
        assertEquals("http://localhost:8080", new EnderecoDoServidor(armazem, null).get());

        armazem.gravar(EnderecoDoServidor.CHAVE_PORTA, "8080");
        armazem.gravar(EnderecoDoServidor.CHAVE_HTTPS, "talvez");
        assertEquals("http://localhost:8080", new EnderecoDoServidor(armazem, null).get());
    }

    @Test
    void faltandoUmaChaveValeOPadrao() {
        armazem.gravar(EnderecoDoServidor.CHAVE_ENDERECO, "192.168.0.10");

        assertEquals("http://localhost:8080", new EnderecoDoServidor(armazem, null).get());
    }

    @Test
    void nenhumaChaveDeSenhaOuToken() {
        EnderecoDoServidor endereco = new EnderecoDoServidor(armazem, null);
        endereco.salvar(ConfiguracaoDoServidor.criar("192.168.0.10", 8080, false));
        endereco.restaurarPadrao();
        endereco.salvar(ConfiguracaoDoServidor.criar("jogo.exemplo.com", 443, true));

        assertEquals(Set.of(EnderecoDoServidor.CHAVE_ENDERECO, EnderecoDoServidor.CHAVE_PORTA,
                EnderecoDoServidor.CHAVE_HTTPS), armazem.gravadas);
        for (String chave : armazem.gravadas) {
            assertFalse(chave.contains("senha") || chave.contains("token"), chave);
        }
    }

    private static final class ArmazemEmMemoria implements ArmazemDeTexto {

        final Map<String, String> valores = new HashMap<>();
        final Set<String> gravadas = new HashSet<>();
        int leituras;

        @Override
        public String ler(String chave) {
            leituras++;
            return valores.get(chave);
        }

        @Override
        public void gravar(String chave, String valor) {
            gravadas.add(chave);
            valores.put(chave, valor);
        }

        @Override
        public void remover(String chave) {
            valores.remove(chave);
        }
    }
}
