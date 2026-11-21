package br.edu.unifor.missaomarte.domain;

/** Os números de cada dificuldade ficam num lugar só. */
public enum Dificuldade {

    FACIL("Fácil", 30, 4, 1, 1, 1),
    MEDIO("Médio", 20, 5, 2, 2, 2),
    DIFICIL("Difícil", 45, 5, 3, 3, 2);

    private final String nome;
    private final int pontuacaoInicial;
    private final int passageiros;
    private final int asteroides;
    private final int inimigos;
    private final int tamanhoMinimo;

    Dificuldade(String nome, int pontuacaoInicial, int passageiros, int asteroides, int inimigos, int tamanhoMinimo) {
        this.nome = nome;
        this.pontuacaoInicial = pontuacaoInicial;
        this.passageiros = passageiros;
        this.asteroides = asteroides;
        this.inimigos = inimigos;
        this.tamanhoMinimo = tamanhoMinimo;
    }

    /** Texto desconhecido vira Médio, como no jogo original. */
    public static Dificuldade deTexto(String texto) {
        if (texto == null) {
            return MEDIO;
        }
        return switch (texto.trim().toLowerCase()) {
            case "facil", "fácil" -> FACIL;
            case "dificil", "difícil" -> DIFICIL;
            default -> MEDIO;
        };
    }

    /**
     * Abaixo do mínimo o mapa não tem células livres para todos os objetos
     * e o sorteio das posições nunca termina.
     */
    public int ajustarTamanho(int tamanho) {
        return Math.max(tamanho, tamanhoMinimo);
    }

    public int pontuacaoInicial() {
        return pontuacaoInicial;
    }

    public int passageiros() {
        return passageiros;
    }

    public int asteroides() {
        return asteroides;
    }

    public int inimigos() {
        return inimigos;
    }

    public int tamanhoMinimo() {
        return tamanhoMinimo;
    }

    @Override
    public String toString() {
        return nome;
    }
}
