package br.edu.unifor.missaomarte.domain;

/** Comando do jogador, já convertido do texto digitado. */
public enum Acao {

    CIMA('w', 0, -1),
    BAIXO('s', 0, 1),
    ESQUERDA('a', -1, 0),
    DIREITA('d', 1, 0),
    EMBARCAR('c', 0, 0),
    SAIR('q', 0, 0),
    INVALIDA(' ', 0, 0);

    private final char tecla;
    private final int dx;
    private final int dy;

    Acao(char tecla, int dx, int dy) {
        this.tecla = tecla;
        this.dx = dx;
        this.dy = dy;
    }

    /** Vale só o primeiro caractere, sem diferença entre maiúscula e minúscula. */
    public static Acao deTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return INVALIDA;
        }
        char tecla = Character.toLowerCase(texto.trim().charAt(0));
        for (Acao acao : values()) {
            if (acao.tecla == tecla) {
                return acao;
            }
        }
        return INVALIDA;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }
}
