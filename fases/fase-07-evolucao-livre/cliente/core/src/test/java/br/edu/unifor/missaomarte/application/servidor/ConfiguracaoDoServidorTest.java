package br.edu.unifor.missaomarte.application.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConfiguracaoDoServidorTest {

    @Test
    void montaAUrlComEsquemaEnderecoEPorta() {
        assertEquals("http://192.168.0.10:8080", ConfiguracaoDoServidor.criar("192.168.0.10", 8080, false).url());
        assertEquals("https://jogo.exemplo.com:443", ConfiguracaoDoServidor.criar("jogo.exemplo.com", 443, true).url());
    }

    @Test
    void normalizaEspacosNasPontasEMaiusculas() {
        ConfiguracaoDoServidor c = ConfiguracaoDoServidor.criar("  Meu-PC.Local ", 8080, false);

        assertEquals("meu-pc.local", c.endereco());
        assertEquals("http://meu-pc.local:8080", c.url());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "256.1.1.1", "1.2.3", "1.2.3.4.5", "1..2", "-servidor", "servidor-",
            "meu servidor", "x/a@b", "a@b", "http://x", "servidor_1", "ç.com", "1234.1.1.1"})
    void recusaEnderecoInvalido(String endereco) {
        assertThrows(IllegalArgumentException.class, () -> ConfiguracaoDoServidor.criar(endereco, 8080, false));
    }

    @Test
    void recusaEnderecoNulo() {
        assertThrows(IllegalArgumentException.class, () -> ConfiguracaoDoServidor.criar(null, 8080, false));
    }

    @Test
    void ipInvalidoTemMensagemPropria() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> ConfiguracaoDoServidor.criar("256.1.1.1", 8080, false));
        assertTrue(e.getMessage().startsWith("IP inválido"));
    }

    @Test
    void aceitaIpNasBordasENomeDeMaquina() {
        assertEquals("0.0.0.0", ConfiguracaoDoServidor.criar("0.0.0.0", 1, false).endereco());
        assertEquals("255.255.255.255", ConfiguracaoDoServidor.criar("255.255.255.255", 65535, false).endereco());
        assertEquals("localhost", ConfiguracaoDoServidor.criar("localhost", 8080, false).endereco());
        assertEquals("a1-b2.c3", ConfiguracaoDoServidor.criar("a1-b2.c3", 8080, false).endereco());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 65536, 99999})
    void recusaPortaForaDaFaixa(int porta) {
        assertThrows(IllegalArgumentException.class, () -> ConfiguracaoDoServidor.criar("localhost", porta, false));
    }

    @Test
    void deUrlLeEsquemaEnderecoEPorta() {
        ConfiguracaoDoServidor c = ConfiguracaoDoServidor.deUrl("http://10.0.2.2:8080");

        assertEquals("10.0.2.2", c.endereco());
        assertEquals(8080, c.porta());
        assertFalse(c.https());
    }

    @Test
    void deUrlSemPortaUsaAPadraoDoEsquemaEAceitaBarraFinal() {
        assertEquals(443, ConfiguracaoDoServidor.deUrl("https://jogo.exemplo.com/").porta());
        assertEquals(80, ConfiguracaoDoServidor.deUrl("HTTP://jogo.exemplo.com").porta());
        assertTrue(ConfiguracaoDoServidor.deUrl("https://jogo.exemplo.com").https());
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://x/a@b", "http://x/api", "ftp://x:21", "localhost:8080", "http://", "http://x:",
            "http://x:99999", "http://x:8o80", "http://usuario@x:8080", "http://x :8080", ""})
    void deUrlRecusaEmVezDeInterpretar(String url) {
        assertThrows(IllegalArgumentException.class, () -> ConfiguracaoDoServidor.deUrl(url));
    }

    @Test
    void deUrlNuloERecusado() {
        assertThrows(IllegalArgumentException.class, () -> ConfiguracaoDoServidor.deUrl(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"localhost", "127.0.0.1", "127.5.5.5", "10.0.2.2", "172.16.0.1", "172.31.255.255",
            "192.168.0.10"})
    void redesPrivadasSaoRedeLocal(String endereco) {
        assertTrue(ConfiguracaoDoServidor.criar(endereco, 8080, false).redeLocal());
        assertFalse(ConfiguracaoDoServidor.criar(endereco, 8080, false).exigeAviso());
    }

    @ParameterizedTest
    @ValueSource(strings = {"172.15.0.1", "172.32.0.1", "8.8.8.8", "192.169.0.1", "jogo.exemplo.com", "meu-pc"})
    void foraDaRedeLocalComHttpExigeAviso(String endereco) {
        ConfiguracaoDoServidor c = ConfiguracaoDoServidor.criar(endereco, 8080, false);

        assertFalse(c.redeLocal());
        assertTrue(c.exigeAviso());
    }

    @Test
    void httpsNuncaExigeAviso() {
        assertFalse(ConfiguracaoDoServidor.criar("jogo.exemplo.com", 443, true).exigeAviso());
    }

    @Test
    void igualdadePorEnderecoPortaEHttps() {
        ConfiguracaoDoServidor a = ConfiguracaoDoServidor.criar("Servidor", 8080, false);
        ConfiguracaoDoServidor b = ConfiguracaoDoServidor.criar("servidor", 8080, false);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, ConfiguracaoDoServidor.criar("servidor", 8081, false));
        assertNotEquals(a, ConfiguracaoDoServidor.criar("servidor", 8080, true));
        assertNotEquals(a, ConfiguracaoDoServidor.criar("outro", 8080, false));
        assertEquals(a, ConfiguracaoDoServidor.deUrl("http://servidor:8080"));
    }
}
