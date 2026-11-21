package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * No LWJGL3 o Tab também chega à Nova partida como caractere digitado ('\t'). Ele troca a cor no teclaApertada
 * e não pode entrar no nome do piloto.
 */
class TabNaNovaPartidaTest {

    @Test
    void tabNaoEntraNoNome() {
        NomeDoPiloto nome = new NomeDoPiloto();
        nome.digitar('A');

        assertFalse(nome.digitar('\t'));
        assertEquals("A", nome.texto());
    }
}
