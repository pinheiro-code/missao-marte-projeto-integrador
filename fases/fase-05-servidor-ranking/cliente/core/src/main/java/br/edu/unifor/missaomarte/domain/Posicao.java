package br.edu.unifor.missaomarte.domain;

/** Célula do mapa. Imutável: mover devolve outra posição. */
public record Posicao(int x, int y) {

    public static final Posicao ORIGEM = new Posicao(0, 0);

    public Posicao mover(int dx, int dy) {
        return new Posicao(x + dx, y + dy);
    }

    /** O mapa vai de -tamanho a +tamanho nos dois eixos. */
    public boolean dentroDoMapa(int tamanho) {
        return Math.abs(x) <= tamanho && Math.abs(y) <= tamanho;
    }
}
