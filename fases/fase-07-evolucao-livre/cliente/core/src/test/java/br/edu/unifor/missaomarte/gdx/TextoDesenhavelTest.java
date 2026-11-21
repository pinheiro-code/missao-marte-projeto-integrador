package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import org.junit.jupiter.api.Test;

/**
 * Usa os dados da fonte padrão do libGDX (a mesma do new BitmapFont() do jogo), lidos do jar sem OpenGL:
 * o BitmapFontData só lê o .fnt, a textura fica para a BitmapFont.
 */
class TextoDesenhavelTest {

    private static final String ALERTA = "✨ ALERTA: Todos os passageiros resgatados! "
            + "Retorne para a Plataforma de Pouso 'L' em (0,0) para completar a missão.";

    private final BitmapFontData fonte = new BitmapFontData(
            new FileHandle("com/badlogic/gdx/utils/lsans-15.fnt", FileType.Classpath) { }, false);

    @Test
    void aFontePadraoNaoTemOEmojiDoAlerta() {
        assertNull(fonte.getGlyph('✨'));
        assertNotNull(fonte.getGlyph('ã'));
    }

    @Test
    void tiraOEmojiEOEspacoQueSobrariaNoComeco() {
        assertEquals("ALERTA: Todos os passageiros resgatados! "
                + "Retorne para a Plataforma de Pouso 'L' em (0,0) para completar a missão.",
                TextoDesenhavel.de(ALERTA, fonte));
    }

    @Test
    void tudoQueSobraTemGlifoNaFonte() {
        TextoDesenhavel.de(ALERTA, fonte).chars()
                .filter(c -> c != ' ')
                .forEach(c -> assertNotNull(fonte.getGlyph((char) c), "sem glifo: " + (char) c));
    }

    @Test
    void caractereNoMeioNaoDeixaEspacoDuplo() {
        assertEquals("a b", TextoDesenhavel.de("a ✨ b", fonte));
    }

    @Test
    void emojiForaDoPlanoBasicoTambemSai() {
        assertEquals("Fim", TextoDesenhavel.de("Fim \uD83D\uDE80", fonte));
    }

    @Test
    void mensagemComAcentoEQuebraDeLinhaFicaIgual() {
        String texto = "Combustível/Pontuação zerada! Missão perdida.\nNenhum passageiro nesta posição.";
        assertEquals(texto, TextoDesenhavel.de(texto, fonte));
    }
}
