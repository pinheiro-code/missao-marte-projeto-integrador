package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NomeDoPilotoTest {

    private final NomeDoPiloto nome = new NomeDoPiloto();

    @Test
    void aceitaLetrasComAcentoNumerosEspacoEPonto() {
        assertTrue(nome.digitar('J'));
        for (char c : "oão 2.".toCharArray()) {
            nome.digitar(c);
        }

        assertEquals("João 2.", nome.texto());
    }

    @Test
    void recusaControleEPontuacao() {
        assertFalse(nome.digitar('\n'));
        assertFalse(nome.digitar('\b'));
        assertFalse(nome.digitar('@'));
        assertFalse(nome.digitar('-'));
        assertEquals("", nome.texto());
    }

    @Test
    void paraEmVinte() {
        for (int i = 0; i < 25; i++) {
            nome.digitar('a');
        }

        assertEquals(NomeDoPiloto.MAXIMO, nome.texto().length());
        assertFalse(nome.digitar('b'));
    }

    @Test
    void apagaOUltimoEIgnoraVazio() {
        nome.digitar('a');
        nome.digitar('b');
        nome.apagar();
        assertEquals("a", nome.texto());
        nome.apagar();
        nome.apagar();
        assertEquals("", nome.texto());
    }

    @Test
    void definirTextoTrocaTudoComAMesmaRegra() {
        nome.definirTexto("velho");
        nome.definirTexto("Ana-Maria @ Silva da Costa Pereira");

        assertEquals("AnaMaria  Silva da C", nome.texto());

        nome.definirTexto(null);
        assertEquals("", nome.texto());
    }
}
