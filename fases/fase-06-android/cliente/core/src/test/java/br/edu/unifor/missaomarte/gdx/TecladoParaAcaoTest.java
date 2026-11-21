package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.domain.Acao;
import com.badlogic.gdx.Input.Keys;
import org.junit.jupiter.api.Test;

/** Keys são só constantes inteiras: o teste não abre janela. */
class TecladoParaAcaoTest {

    private final TecladoParaAcao teclado = new TecladoParaAcao();

    @Test
    void letrasMovem() {
        assertEquals(Acao.CIMA, teclado.converter(Keys.W));
        assertEquals(Acao.BAIXO, teclado.converter(Keys.S));
        assertEquals(Acao.ESQUERDA, teclado.converter(Keys.A));
        assertEquals(Acao.DIREITA, teclado.converter(Keys.D));
    }

    @Test
    void setasMovem() {
        assertEquals(Acao.CIMA, teclado.converter(Keys.UP));
        assertEquals(Acao.BAIXO, teclado.converter(Keys.DOWN));
        assertEquals(Acao.ESQUERDA, teclado.converter(Keys.LEFT));
        assertEquals(Acao.DIREITA, teclado.converter(Keys.RIGHT));
    }

    @Test
    void eEmbarcaQEEscSaem() {
        assertEquals(Acao.EMBARCAR, teclado.converter(Keys.E));
        assertEquals(Acao.SAIR, teclado.converter(Keys.Q));
        assertEquals(Acao.SAIR, teclado.converter(Keys.ESCAPE));
    }

    @Test
    void teclaSemAcaoEInvalida() {
        assertEquals(Acao.INVALIDA, teclado.converter(Keys.X));
        assertEquals(Acao.INVALIDA, teclado.converter(Keys.C), "o 'c' do console não embarca aqui");
        assertEquals(Acao.INVALIDA, teclado.converter(Keys.SPACE));
    }
}
