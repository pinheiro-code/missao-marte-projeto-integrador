import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * Gera o ícone do app Android em android/res/mipmap-* (48, 72, 96, 144 e 192 px). Rode na pasta cliente:
 *
 *     java tools/GeradorDeIcone.java
 *
 * Fica fora do build. Desenha por código a mesma nave do GeradorDeAssets (grade de 32x32) sobre um
 * planeta vermelho, sem imagem de terceiros. Sem sorteio nem data: rodar de novo dá os mesmos bytes.
 */
public class GeradorDeIcone {

    private static final String[] PASTAS = {"mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"};
    private static final int[] TAMANHOS = {48, 72, 96, 144, 192};

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < PASTAS.length; i++) {
            Path pasta = Path.of("android", "res", "mipmap-" + PASTAS[i]);
            Files.createDirectories(pasta);
            ImageIO.write(icone(TAMANHOS[i]), "png", pasta.resolve("ic_launcher.png").toFile());
        }
        System.out.println("Gerado o ic_launcher.png em " + PASTAS.length + " tamanhos (android/res/mipmap-*).");
    }

    private static BufferedImage icone(int lado) {
        BufferedImage imagem = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // tudo desenhado numa grade de 32 e escalado: os cinco tamanhos ficam iguais
        g.scale(lado / 32.0, lado / 32.0);
        planeta(g);
        nave(g);
        g.dispose();
        return imagem;
    }

    private static void planeta(Graphics2D g) {
        g.setColor(new Color(25, 12, 30));
        g.fillOval(0, 0, 32, 32);
        g.setColor(new Color(190, 75, 40));
        g.fillOval(2, 2, 28, 28);
        g.setColor(new Color(150, 55, 30));
        g.fillOval(6, 18, 7, 5);
        g.fillOval(20, 8, 5, 4);
    }

    /** A nave do jogo, apontando para cima. */
    private static void nave(Graphics2D g) {
        g.setColor(new Color(200, 210, 225));
        g.fillPolygon(new Polygon(new int[] {16, 24, 22, 10, 8}, new int[] {3, 20, 24, 24, 20}, 5));
        g.setColor(new Color(110, 120, 140));
        g.fillPolygon(new Polygon(new int[] {8, 2, 4, 10}, new int[] {16, 24, 26, 24}, 4));
        g.fillPolygon(new Polygon(new int[] {24, 30, 28, 22}, new int[] {16, 24, 26, 24}, 4));
        g.setColor(new Color(60, 180, 230));
        g.fillOval(13, 9, 6, 7);
        g.setColor(new Color(255, 170, 40));
        g.fillRect(12, 24, 8, 3);
    }
}
