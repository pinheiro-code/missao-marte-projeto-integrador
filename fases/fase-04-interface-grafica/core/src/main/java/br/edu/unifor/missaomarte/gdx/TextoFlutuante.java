package br.edu.unifor.missaomarte.gdx;

/**
 * Texto que sobe, fica transparente e some ("+10", "Entregue!"). Posição em células:
 * no jogo o Y cresce para baixo, então subir na tela é diminuir o Y. Sem libGDX, para testar.
 */
public class TextoFlutuante extends EfeitoTemporario {

    public static final float DURACAO = 1.2f;
    /** Quanto sobe até sumir, em células. */
    public static final float SUBIDA = 1.0f;

    private final String texto;

    public TextoFlutuante(String texto, float x, float y) {
        super(null, x, y, DURACAO);
        this.texto = texto;
    }

    /** O "-1" de cada passo poluiria a tela: só variação maior que 1 em módulo vira texto. */
    public static boolean mostraVariacao(int variacao) {
        return Math.abs(variacao) > 1;
    }

    public static String formatarVariacao(int variacao) {
        return variacao > 0 ? "+" + variacao : String.valueOf(variacao);
    }

    public String texto() {
        return texto;
    }

    /** Y em células, já subindo. */
    public float yAtual() {
        return y() - SUBIDA * progresso();
    }

    /** 1 no começo, 0 no fim. */
    public float opacidade() {
        return 1 - progresso();
    }
}
