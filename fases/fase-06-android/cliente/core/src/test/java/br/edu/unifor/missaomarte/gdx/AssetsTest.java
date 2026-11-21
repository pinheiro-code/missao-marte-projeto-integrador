package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import org.junit.jupiter.api.Test;

/**
 * Confere os arquivos de core/assets contra o catálogo, sem OpenGL nem áudio:
 * a imagem é lida pelo ImageIO e o som só tem o formato lido pelo javax.sound (não toca).
 * O Gradle roda os testes com a pasta core como diretório de trabalho.
 */
class AssetsTest {

    private static final Path ASSETS = Path.of("assets");

    @Test
    void cadaImagemExisteComALarguraEAAlturaDoCatalogo() throws IOException {
        for (Imagem imagem : Imagem.values()) {
            Path arquivo = ASSETS.resolve(imagem.caminho());
            assertTrue(Files.exists(arquivo), "falta " + arquivo);
            BufferedImage png = ImageIO.read(arquivo.toFile());
            assertEquals(imagem.larguraQuadro() * imagem.quadros(), png.getWidth(), imagem + ": largura");
            assertEquals(imagem.alturaQuadro(), png.getHeight(), imagem + ": altura");
        }
    }

    @Test
    void cadaSomExisteEEUmAudioValido() {
        for (Som som : Som.values()) {
            Path arquivo = ASSETS.resolve(som.caminho());
            assertTrue(Files.exists(arquivo), "falta " + arquivo);
            assertDoesNotThrow(() -> AudioSystem.getAudioFileFormat(arquivo.toFile()), som + " não é áudio válido");
        }
    }

    @Test
    void existeUmaUnicaMusica() {
        assertEquals(1, Arrays.stream(Som.values()).filter(Som::musica).count());
    }

    @Test
    void animacoesTemOsQuadrosMinimosDoEnunciado() {
        assertTrue(Imagem.PROPULSOR.quadros() >= 2);
        assertTrue(Imagem.BASE.quadros() >= 2);
        assertTrue(Imagem.INIMIGO.quadros() >= 2);
        assertTrue(Imagem.EXPLOSAO.quadros() >= 4);
        assertTrue(Imagem.BRILHO.quadros() >= 3);
        assertEquals(800, Imagem.FUNDO.larguraQuadro());
        assertEquals(600, Imagem.FUNDO.alturaQuadro());
    }
}
