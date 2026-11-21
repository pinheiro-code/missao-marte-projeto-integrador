package br.edu.unifor.missaomarte.application.preferencias;

/**
 * As cores que o jogador pode dar à nave, na ordem em que o Tab (ou o botão Cor) as percorre.
 * Os componentes vão de 0 a 1 e multiplicam o sprite (tinta); a primeira é a do sprite, sem tinta.
 * Fica sem libGDX: a tela é que transforma os números na Color.
 */
public enum CorDaNave {

    ORIGINAL("Original", 1f, 1f, 1f),
    VERMELHA("Vermelha", 1f, 0.35f, 0.35f),
    VERDE("Verde", 0.4f, 1f, 0.45f),
    AZUL("Azul", 0.45f, 0.65f, 1f),
    AMARELA("Amarela", 1f, 0.9f, 0.3f),
    ROSA("Rosa", 1f, 0.5f, 0.85f);

    private final String nome;
    private final float vermelho;
    private final float verde;
    private final float azul;

    CorDaNave(String nome, float vermelho, float verde, float azul) {
        this.nome = nome;
        this.vermelho = vermelho;
        this.verde = verde;
        this.azul = azul;
    }

    /** A de quem nunca escolheu: o jogo fica igual à fase 6. */
    public static CorDaNave padrao() {
        return values()[0];
    }

    /** Depois da última vem a primeira. */
    public CorDaNave proxima() {
        CorDaNave[] todas = values();
        return todas[(ordinal() + 1) % todas.length];
    }

    public String nome() {
        return nome;
    }

    public float vermelho() {
        return vermelho;
    }

    public float verde() {
        return verde;
    }

    public float azul() {
        return azul;
    }
}
