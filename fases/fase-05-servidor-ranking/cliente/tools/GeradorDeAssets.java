import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Gera todas as imagens e sons do jogo em core/assets. Rode na pasta da fase:
 *
 *     java tools/GeradorDeAssets.java
 *
 * Fica fora do build. Sobrescreve core/assets inteiro, então nenhum asset é editado à mão:
 * para mudar um desenho ou um som, mude este arquivo e rode de novo. O resultado é sempre
 * o mesmo (o sorteio das estrelas usa semente fixa), para o git status ficar limpo.
 *
 * Tamanhos e quantidade de quadros precisam bater com o enum Imagem do pacote gdx.
 */
public class GeradorDeAssets {

    private static final Path IMAGENS = Path.of("core", "assets", "imagens");
    private static final Path SONS = Path.of("core", "assets", "sons");
    private static final int Q = 32;
    private static final int TAXA = 22050;

    public static void main(String[] args) throws IOException {
        Files.createDirectories(IMAGENS);
        Files.createDirectories(SONS);

        salvar("fundo", fundo());
        salvar("terreno", faixa(1, Q, (g, i) -> terreno(g)));
        salvar("nave", faixa(1, Q, (g, i) -> nave(g)));
        salvar("propulsor", faixa(3, Q, GeradorDeAssets::propulsor));
        salvar("base", faixa(2, Q, GeradorDeAssets::base));
        salvar("asteroide", faixa(1, Q, (g, i) -> asteroide(g)));
        salvar("inimigo", faixa(2, Q, GeradorDeAssets::inimigo));
        salvar("passageiro_professor", faixa(1, Q, (g, i) -> passageiro(g, new Color(230, 190, 40), "P")));
        salvar("passageiro_engenheiro", faixa(1, Q, (g, i) -> passageiro(g, new Color(70, 110, 230), "E")));
        salvar("passageiro_astronauta", faixa(1, Q, (g, i) -> passageiro(g, new Color(235, 235, 235), "T")));
        salvar("desconhecido", faixa(1, Q, (g, i) -> desconhecido(g)));
        salvar("explosao", faixa(5, Q, GeradorDeAssets::explosao));
        salvar("brilho", faixa(4, Q, GeradorDeAssets::brilho));
        salvar("icone_vida", faixa(1, 16, (g, i) -> coracao(g, true)));
        salvar("icone_vida_vazia", faixa(1, 16, (g, i) -> coracao(g, false)));
        salvar("icone_vaga", faixa(1, 16, (g, i) -> vaga(g, true)));
        salvar("icone_vaga_livre", faixa(1, 16, (g, i) -> vaga(g, false)));

        wav("mover", tom(660, 0.06, 0.25));
        wav("selecionar", juntar(tom(880, 0.05, 0.3), tom(1320, 0.07, 0.3)));
        wav("embarcar", juntar(tom(523, 0.08, 0.35), tom(659, 0.08, 0.35), tom(784, 0.12, 0.35)));
        wav("entregar", juntar(tom(784, 0.08, 0.35), tom(988, 0.08, 0.35), tom(1175, 0.16, 0.35)));
        wav("colisao", ruido(0.35, 0.5));
        wav("vitoria", juntar(tom(523, 0.15, 0.35), tom(659, 0.15, 0.35), tom(784, 0.15, 0.35), tom(1047, 0.4, 0.35)));
        wav("derrota", juntar(tom(392, 0.2, 0.35), tom(330, 0.2, 0.35), tom(262, 0.5, 0.35)));
        wav("musica", musica());
        System.out.println("Assets gerados em core/assets: 17 imagens e 8 sons.");
    }

    // ---------- imagens ----------

    private interface Quadro {
        void desenhar(Graphics2D g, int indice);
    }

    /** Faixa horizontal: os quadros lado a lado, todos do mesmo tamanho. */
    private static BufferedImage faixa(int quadros, int lado, Quadro quadro) {
        BufferedImage imagem = new BufferedImage(quadros * lado, lado, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < quadros; i++) {
            Graphics2D g = imagem.createGraphics();
            g.translate(i * lado, 0);
            g.setClip(0, 0, lado, lado);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            quadro.desenhar(g, i);
            g.dispose();
        }
        return imagem;
    }

    private static BufferedImage fundo() {
        BufferedImage imagem = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        for (int y = 0; y < 600; y++) {
            float t = y / 599f;
            g.setColor(new Color((int) (12 + 70 * t), (int) (8 + 18 * t), (int) (28 + 4 * t)));
            g.fillRect(0, y, 800, 1);
        }
        Random sorteio = new Random(20261121L);
        for (int i = 0; i < 180; i++) {
            int x = sorteio.nextInt(800);
            int y = sorteio.nextInt(420);
            int brilho = 150 + sorteio.nextInt(106);
            g.setColor(new Color(brilho, brilho, brilho));
            g.fillRect(x, y, sorteio.nextInt(4) == 0 ? 2 : 1, 1);
        }
        g.setColor(new Color(150, 60, 30));
        g.fillOval(560, 60, 120, 120);
        g.setColor(new Color(120, 45, 25));
        g.fillOval(590, 90, 30, 20);
        g.fillOval(625, 130, 20, 14);
        g.setColor(new Color(95, 35, 20));
        g.fillPolygon(new int[] {0, 120, 260, 420, 560, 700, 800, 800, 0},
                new int[] {520, 470, 500, 455, 495, 465, 500, 600, 600}, 9);
        g.dispose();
        return imagem;
    }

    private static void terreno(Graphics2D g) {
        g.setColor(new Color(140, 62, 34));
        g.fillRect(0, 0, Q, Q);
        g.setColor(new Color(120, 50, 28));
        g.fillRect(4, 6, 3, 2);
        g.fillRect(20, 4, 2, 2);
        g.fillRect(12, 18, 4, 2);
        g.fillRect(25, 24, 3, 3);
        g.fillRect(5, 26, 2, 2);
        g.setColor(new Color(160, 76, 42));
        g.fillRect(16, 10, 2, 1);
        g.fillRect(8, 14, 1, 1);
        g.fillRect(28, 14, 1, 1);
        g.setColor(new Color(105, 44, 24));
        g.drawRect(0, 0, Q - 1, Q - 1);
    }

    /** Nave apontando para cima; o propulsor fica embaixo, no mesmo quadro. */
    private static void nave(Graphics2D g) {
        g.setColor(new Color(200, 210, 225));
        g.fillPolygon(new Polygon(new int[] {16, 24, 22, 10, 8}, new int[] {3, 20, 24, 24, 20}, 5));
        g.setColor(new Color(110, 120, 140));
        g.fillPolygon(new Polygon(new int[] {8, 2, 4, 10}, new int[] {16, 24, 26, 24}, 4));
        g.fillPolygon(new Polygon(new int[] {24, 30, 28, 22}, new int[] {16, 24, 26, 24}, 4));
        g.setColor(new Color(60, 180, 230));
        g.fillOval(13, 9, 6, 7);
        g.setColor(new Color(80, 85, 100));
        g.fillRect(12, 24, 8, 2);
    }

    private static void propulsor(Graphics2D g, int i) {
        int[] comprimento = {4, 6, 5};
        int fim = 26 + comprimento[i];
        g.setColor(new Color(255, 140, 20));
        g.fillPolygon(new Polygon(new int[] {12, 20, 16}, new int[] {26, 26, fim}, 3));
        g.setColor(new Color(255, 230, 90));
        g.fillPolygon(new Polygon(new int[] {14, 18, 16}, new int[] {26, 26, fim - 2}, 3));
    }

    private static void base(Graphics2D g, int i) {
        g.setColor(new Color(70, 75, 85));
        g.fillOval(2, 6, 28, 22);
        g.setColor(new Color(110, 115, 125));
        g.fillOval(5, 9, 22, 16);
        g.setColor(new Color(230, 230, 230));
        g.setStroke(new BasicStroke(2));
        g.drawLine(12, 11, 12, 22);
        g.drawLine(12, 22, 20, 22);
        g.setColor(i == 0 ? new Color(60, 230, 90) : new Color(20, 90, 35));
        g.fillRect(3, 15, 3, 3);
        g.fillRect(26, 15, 3, 3);
        g.setColor(i == 0 ? new Color(20, 90, 35) : new Color(60, 230, 90));
        g.fillRect(15, 6, 3, 3);
    }

    private static void asteroide(Graphics2D g) {
        g.setColor(new Color(105, 95, 90));
        g.fillPolygon(new Polygon(new int[] {9, 21, 29, 27, 18, 6, 3}, new int[] {4, 3, 11, 24, 29, 26, 14}, 7));
        g.setColor(new Color(75, 68, 64));
        g.fillOval(10, 9, 6, 5);
        g.fillOval(18, 17, 7, 6);
        g.fillOval(8, 19, 4, 4);
        g.setColor(new Color(135, 125, 118));
        g.fillRect(19, 7, 3, 2);
    }

    private static void inimigo(Graphics2D g, int i) {
        g.setColor(new Color(200, 40, 50));
        g.fillOval(4, 10, 24, 12);
        g.setColor(new Color(240, 90, 90));
        g.fillOval(10, 5, 12, 10);
        g.setColor(new Color(20, 20, 30));
        g.fillRect(13, 9, 2, 2);
        g.fillRect(18, 9, 2, 2);
        g.setColor(i == 0 ? new Color(255, 230, 60) : new Color(120, 20, 25));
        g.fillRect(7, 15, 3, 3);
        g.fillRect(22, 15, 3, 3);
        g.setColor(i == 0 ? new Color(120, 20, 25) : new Color(255, 230, 60));
        g.fillRect(14, 16, 4, 3);
        g.setColor(new Color(150, 30, 40));
        int perna = i == 0 ? 0 : 2;
        g.fillRect(8 - perna, 22, 2, 5);
        g.fillRect(22 + perna, 22, 2, 5);
    }

    private static void passageiro(Graphics2D g, Color roupa, String letra) {
        g.setColor(new Color(240, 200, 160));
        g.fillOval(11, 3, 10, 10);
        g.setColor(roupa);
        g.fillRect(9, 13, 14, 11);
        g.fillRect(10, 24, 4, 6);
        g.fillRect(18, 24, 4, 6);
        g.setColor(new Color(40, 40, 50));
        letra(g, letra, 14, 15, 1);
    }

    private static void desconhecido(Graphics2D g) {
        g.setColor(new Color(200, 0, 200));
        g.fillRect(4, 4, 24, 24);
        g.setColor(Color.WHITE);
        letra(g, "?", 10, 8, 3);
    }

    /** Letras de 3x5 pixels desenhadas à mão: não dependem das fontes instaladas na máquina. */
    private static void letra(Graphics2D g, String letra, int x, int y, int escala) {
        String[] desenho = switch (letra) {
            case "P" -> new String[] {"###", "#.#", "###", "#..", "#.."};
            case "E" -> new String[] {"###", "#..", "##.", "#..", "###"};
            case "T" -> new String[] {"###", ".#.", ".#.", ".#.", ".#."};
            default -> new String[] {"###", "..#", ".##", "...", ".#."};
        };
        for (int linha = 0; linha < desenho.length; linha++) {
            for (int coluna = 0; coluna < 3; coluna++) {
                if (desenho[linha].charAt(coluna) == '#') {
                    g.fillRect(x + coluna * escala, y + linha * escala, escala, escala);
                }
            }
        }
    }

    private static void explosao(Graphics2D g, int i) {
        int r = 5 + i * 5;
        int c = 16;
        g.setColor(new Color(255, 80 + i * 30, 20, 255 - i * 40));
        g.fillOval(c - r, c - r, 2 * r, 2 * r);
        int ri = Math.max(2, r - 5);
        g.setColor(new Color(255, 240, 120, 255 - i * 45));
        g.fillOval(c - ri, c - ri, 2 * ri, 2 * ri);
    }

    private static void brilho(Graphics2D g, int i) {
        int r = 4 + i * 3;
        g.setColor(new Color(255, 255, 200, 230 - i * 50));
        g.setStroke(new BasicStroke(2));
        g.drawLine(16 - r, 16, 16 + r, 16);
        g.drawLine(16, 16 - r, 16, 16 + r);
        int d = (int) (r * 0.7);
        g.drawLine(16 - d, 16 - d, 16 + d, 16 + d);
        g.drawLine(16 - d, 16 + d, 16 + d, 16 - d);
    }

    private static void coracao(Graphics2D g, boolean cheio) {
        Polygon forma = new Polygon(new int[] {8, 1, 1, 4, 8, 12, 15, 15}, new int[] {15, 8, 4, 1, 4, 1, 4, 8}, 8);
        if (cheio) {
            g.setColor(new Color(230, 50, 60));
            g.fillPolygon(forma);
        } else {
            g.setColor(new Color(110, 110, 120));
            g.drawPolygon(forma);
        }
    }

    private static void vaga(Graphics2D g, boolean ocupada) {
        if (ocupada) {
            g.setColor(new Color(240, 200, 60));
            g.fillOval(2, 2, 12, 12);
        } else {
            g.setColor(new Color(150, 150, 160));
            g.drawOval(2, 2, 11, 11);
        }
    }

    private static void salvar(String nome, BufferedImage imagem) throws IOException {
        ImageIO.write(imagem, "png", IMAGENS.resolve(nome + ".png").toFile());
    }

    // ---------- sons (WAV PCM 16 bits, mono, 22050 Hz) ----------

    /** Onda quadrada suavizada, com volume caindo até zero no fim (sem estalo). */
    private static short[] tom(double frequencia, double segundos, double volume) {
        int n = (int) (segundos * TAXA);
        short[] amostras = new short[n];
        for (int i = 0; i < n; i++) {
            double t = (double) i / TAXA;
            double onda = Math.sin(2 * Math.PI * frequencia * t) + Math.sin(6 * Math.PI * frequencia * t) / 3;
            double envelope = Math.min(1, i / (TAXA * 0.005)) * (1 - (double) i / n);
            amostras[i] = (short) (onda * envelope * volume * 0.75 * Short.MAX_VALUE);
        }
        return amostras;
    }

    private static short[] ruido(double segundos, double volume) {
        Random sorteio = new Random(7L);
        int n = (int) (segundos * TAXA);
        short[] amostras = new short[n];
        double anterior = 0;
        for (int i = 0; i < n; i++) {
            anterior = anterior * 0.7 + (sorteio.nextDouble() * 2 - 1) * 0.3;
            double envelope = Math.pow(1 - (double) i / n, 2);
            amostras[i] = (short) (anterior * 3 * envelope * volume * Short.MAX_VALUE);
        }
        return amostras;
    }

    /** Oito compassos curtos que fecham no começo, para tocar em laço. */
    private static short[] musica() {
        double[] notas = {220, 262, 330, 262, 196, 247, 294, 247, 175, 220, 262, 220, 196, 247, 294, 330};
        short[][] partes = new short[notas.length][];
        for (int i = 0; i < notas.length; i++) {
            partes[i] = tom(notas[i], 0.25, 0.18);
        }
        return juntar(partes);
    }

    private static short[] juntar(short[]... partes) {
        int total = 0;
        for (short[] p : partes) {
            total += p.length;
        }
        short[] tudo = new short[total];
        int i = 0;
        for (short[] p : partes) {
            System.arraycopy(p, 0, tudo, i, p.length);
            i += p.length;
        }
        return tudo;
    }

    private static void wav(String nome, short[] amostras) throws IOException {
        ByteBuffer dados = ByteBuffer.allocate(44 + amostras.length * 2).order(ByteOrder.LITTLE_ENDIAN);
        dados.put("RIFF".getBytes()).putInt(36 + amostras.length * 2).put("WAVE".getBytes());
        dados.put("fmt ".getBytes()).putInt(16).putShort((short) 1).putShort((short) 1)
                .putInt(TAXA).putInt(TAXA * 2).putShort((short) 2).putShort((short) 16);
        dados.put("data".getBytes()).putInt(amostras.length * 2);
        for (short s : amostras) {
            dados.putShort(s);
        }
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        saida.write(dados.array());
        Files.write(SONS.resolve(nome + ".wav"), saida.toByteArray());
    }
}
