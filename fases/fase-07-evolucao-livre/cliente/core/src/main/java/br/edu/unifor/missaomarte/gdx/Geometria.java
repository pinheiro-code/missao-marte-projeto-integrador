package br.edu.unifor.missaomarte.gdx;

/**
 * Converte célula do mapa em pixel da tela de 800x600.
 * No jogo o Y cresce para baixo (CIMA é y - 1); no libGDX cresce para cima.
 * Não usa libGDX, para ser testada sem janela.
 */
public class Geometria {

    public static final float CELULA = 48;
    public static final float MAPA_X = 16;
    public static final float MAPA_Y = 36;

    private final int tamanho;

    /** O mapa vai de -tamanho a +tamanho nos dois eixos. */
    public Geometria(int tamanho) {
        this.tamanho = tamanho;
    }

    /** Lado do mapa em pixels (11 células de 48 = 528). */
    public float lado() {
        return (2 * tamanho + 1) * CELULA;
    }

    /** X do canto esquerdo da célula. Aceita fração, para o desenho deslizar entre células. */
    public float esquerda(float x) {
        return MAPA_X + (x + tamanho) * CELULA;
    }

    /** Y do canto de baixo da célula, já com o eixo invertido: y = -tamanho fica no topo. */
    public float baixo(float y) {
        return MAPA_Y + (tamanho - y) * CELULA;
    }

    public float centroX(float x) {
        return esquerda(x) + CELULA / 2;
    }

    public float centroY(float y) {
        return baixo(y) + CELULA / 2;
    }
}
