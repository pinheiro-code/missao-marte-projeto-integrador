package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LayoutDeOpcoesTest {

    /** De 2 a 7 opções em duas colunas, numa região do tamanho das telas do jogo. */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 7})
    void opcoesCabemNaRegiaoSemSobreposicaoEComOMinimo(int quantidade) {
        LayoutDeOpcoes layout = new LayoutDeOpcoes(40, 20, 720, 430);
        for (int i = 0; i < quantidade; i += 2) {
            if (i + 1 < quantidade) {
                layout.linha("o" + i, "Opção " + i, "o" + (i + 1), "Opção " + (i + 1));
            } else {
                layout.linha("o" + i, "Opção " + i);
            }
        }

        List<BotaoVirtual> botoes = layout.montar();

        assertEquals(quantidade, botoes.size());
        for (BotaoVirtual b : botoes) {
            assertTrue(b.x() >= 40 && b.x() + b.largura() <= 760.001, b.toString());
            assertTrue(b.y() >= 20 - 0.001 && b.y() + b.altura() <= 450.001, b.toString());
            assertTrue(b.altura() >= LayoutDeOpcoes.MINIMO, b.toString());
            assertTrue(b.largura() >= LayoutDeOpcoes.MINIMO, b.toString());
        }
        for (int i = 0; i < botoes.size(); i++) {
            for (int j = i + 1; j < botoes.size(); j++) {
                assertFalse(botoes.get(i).sobrepoe(botoes.get(j)), botoes.get(i) + " x " + botoes.get(j));
            }
        }
    }

    @Test
    void primeiraLinhaFicaEmCimaEOsBotoesDaLinhaDividemALargura() {
        List<BotaoVirtual> botoes = new LayoutDeOpcoes(0, 0, 400, 200)
                .linha("a", "A")
                .linha("b", "B", "c", "C")
                .montar();

        assertEquals(LayoutDeOpcoes.MAXIMO, botoes.get(0).altura(), 0.001);
        assertEquals(200 - LayoutDeOpcoes.MAXIMO, botoes.get(0).y(), 0.001);
        assertEquals(400, botoes.get(0).largura(), 0.001);
        assertTrue(botoes.get(1).y() < botoes.get(0).y());
        assertEquals(botoes.get(1).y(), botoes.get(2).y(), 0.001);
        assertEquals((400 - LayoutDeOpcoes.ESPACO) / 2, botoes.get(1).largura(), 0.001);
        assertEquals("C", botoes.get(2).rotulo());
    }

    @Test
    void seteLinhasNumaColunaSoNaoCabemNos600() {
        LayoutDeOpcoes layout = new LayoutDeOpcoes(0, 0, 800, 600);
        for (int i = 0; i < 7; i++) {
            layout.linha("o" + i, "Opção");
        }

        assertThrows(IllegalArgumentException.class, layout::montar);
    }

    @Test
    void recusaRegiaoForaDoJogoEBotaoEstreito() {
        assertThrows(IllegalArgumentException.class, () -> new LayoutDeOpcoes(700, 0, 200, 100));
        assertThrows(IllegalArgumentException.class, () -> new LayoutDeOpcoes(0, 550, 100, 100));
        assertThrows(IllegalArgumentException.class,
                () -> new LayoutDeOpcoes(0, 0, 200, 100).linha("a", "A", "b", "B", "c", "C").montar());
        assertThrows(IllegalArgumentException.class, () -> new LayoutDeOpcoes(0, 0, 200, 100).linha("a"));
    }
}
