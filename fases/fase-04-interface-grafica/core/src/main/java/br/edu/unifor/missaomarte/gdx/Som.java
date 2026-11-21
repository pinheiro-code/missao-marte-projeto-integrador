package br.edu.unifor.missaomarte.gdx;

/** Catálogo dos sons em core/assets/sons. Só a música toca em laço (Music); o resto é efeito (Sound). */
public enum Som {

    MOVER("mover", false),
    SELECIONAR("selecionar", false),
    EMBARCAR("embarcar", false),
    ENTREGAR("entregar", false),
    COLISAO("colisao", false),
    VITORIA("vitoria", false),
    DERROTA("derrota", false),
    MUSICA("musica", true);

    private final String caminho;
    private final boolean musica;

    Som(String nome, boolean musica) {
        this.caminho = "sons/" + nome + ".wav";
        this.musica = musica;
    }

    /** Relativo a core/assets. */
    public String caminho() {
        return caminho;
    }

    public boolean musica() {
        return musica;
    }
}
