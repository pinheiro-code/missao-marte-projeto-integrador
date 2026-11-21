package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

/** Em cada proporção: botões na coluna da direita, fora do jogo, sem sobreposição e grandes o bastante. */
class LayoutDosControlesTest {

    @ParameterizedTest(name = "{0}x{1}")
    @CsvSource({"1060, 600", "1060, 795", "1333, 600", "1600, 600", "1060, 1884"})
    void botoesDaPartidaCabemNaColunaSemSobreposicao(float largura, float altura) {
        List<BotaoVirtual> botoes = new LayoutDosControles(largura, altura).botoesDaPartida();

        assertEquals(7, botoes.size());
        conferir(botoes, largura, altura);
    }

    @ParameterizedTest(name = "{0}x{1}")
    @CsvSource({"1060, 600", "1060, 795", "1333, 600", "1600, 600", "1060, 1884"})
    void botoesDoFimCabemNaColunaSemSobreposicao(float largura, float altura) {
        LayoutDosControles layout = new LayoutDosControles(largura, altura);

        assertEquals(1, layout.botoesDoFim(false).size());
        conferir(layout.botoesDoFim(true), largura, altura);
    }

    private static void conferir(List<BotaoVirtual> botoes, float largura, float altura) {
        for (BotaoVirtual b : botoes) {
            assertTrue(b.x() >= LayoutDosControles.LARGURA_DO_JOGO, b + " invade o jogo");
            assertTrue(b.x() + b.largura() <= largura, b + " passa da direita");
            assertTrue(b.y() >= 0 && b.y() + b.altura() <= altura, b + " sai em cima ou embaixo");
            assertTrue(b.altura() >= LayoutDosControles.BOTAO_MINIMO, b + " baixo demais");
            assertTrue(b.largura() >= LayoutDosControles.BOTAO_MINIMO, b + " estreito demais");
        }
        for (int i = 0; i < botoes.size(); i++) {
            for (int j = i + 1; j < botoes.size(); j++) {
                assertFalse(botoes.get(i).sobrepoe(botoes.get(j)), botoes.get(i) + " x " + botoes.get(j));
            }
        }
    }

    @Test
    void temOsSeteBotoesDoEnunciado() {
        List<String> ids = new ArrayList<>();
        for (BotaoVirtual b : new LayoutDosControles(1060, 600).botoesDaPartida()) {
            ids.add(b.id());
        }

        assertEquals(List.of("baixo", "esquerda", "embarcar", "direita", "cima", "som", "abortar"), ids);
    }

    @Test
    void naLarguraMinimaOBotaoTemOMinimo() {
        assertEquals(LayoutDosControles.BOTAO_MINIMO, new LayoutDosControles(1060, 600).lado(), 0.001);
    }

    @Test
    void botoesCrescemComAColunaAteOMaximo() {
        assertTrue(new LayoutDosControles(1333, 600).lado() > LayoutDosControles.BOTAO_MINIMO);
        assertEquals(LayoutDosControles.BOTAO_MAXIMO, new LayoutDosControles(2000, 900).lado(), 0.001);
    }

    @Test
    void mundoMenorQueOMinimoValeOMinimo() {
        conferir(new LayoutDosControles(800, 400).botoesDaPartida(), 1060, 600);
    }

    @Test
    void oToqueNaBordaEntreDoisBotoesNaoCaiEmDois() {
        List<BotaoVirtual> botoes = new LayoutDosControles(1060, 600).botoesDaPartida();
        BotaoVirtual esquerda = botoes.get(1);
        float meioDoEspaco = esquerda.x() + esquerda.largura() + LayoutDosControles.ESPACO / 2;
        float y = esquerda.y() + esquerda.altura() / 2;

        int dentro = 0;
        for (BotaoVirtual b : botoes) {
            dentro += b.contem(meioDoEspaco, y) ? 1 : 0;
        }
        assertEquals(0, dentro);
    }
}
