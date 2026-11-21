package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.domain.Acao;
import com.badlogic.gdx.Input.Keys;
import org.junit.jupiter.api.Test;

/** O controle Bluetooth e a tecla Voltar do Android chegam como teclas. */
class TecladoParaAcaoControleTest {

    private final TecladoParaAcao teclado = new TecladoParaAcao();

    @Test
    void direcionalTemOMesmoCodigoDasSetas() {
        assertEquals(Acao.CIMA, teclado.converter(Keys.DPAD_UP));
        assertEquals(Acao.BAIXO, teclado.converter(Keys.DPAD_DOWN));
        assertEquals(Acao.ESQUERDA, teclado.converter(Keys.DPAD_LEFT));
        assertEquals(Acao.DIREITA, teclado.converter(Keys.DPAD_RIGHT));
    }

    @Test
    void botaoAEOCentroDoDirecionalEmbarcam() {
        assertEquals(Acao.EMBARCAR, teclado.converter(Keys.BUTTON_A));
        assertEquals(Acao.EMBARCAR, teclado.converter(Keys.DPAD_CENTER));
    }

    @Test
    void botaoBEVoltarAbortam() {
        assertEquals(Acao.SAIR, teclado.converter(Keys.BUTTON_B));
        assertEquals(Acao.SAIR, teclado.converter(Keys.BACK));
    }
}
