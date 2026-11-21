package br.edu.unifor.missaomarte.gdx;

/**
 * Catálogo das imagens em core/assets/imagens. Cada arquivo é uma faixa horizontal de quadros
 * do mesmo tamanho. Não usa libGDX: o teste confere os arquivos sem abrir janela.
 */
public enum Imagem {

    FUNDO("fundo", 800, 600, 1),
    TERRENO("terreno", 32, 32, 1),
    NAVE("nave", 32, 32, 1),
    PROPULSOR("propulsor", 32, 32, 3),
    BASE("base", 32, 32, 2),
    ASTEROIDE("asteroide", 32, 32, 1),
    INIMIGO("inimigo", 32, 32, 2),
    PROFESSOR("passageiro_professor", 32, 32, 1),
    ENGENHEIRO("passageiro_engenheiro", 32, 32, 1),
    ASTRONAUTA("passageiro_astronauta", 32, 32, 1),
    DESCONHECIDO("desconhecido", 32, 32, 1),
    EXPLOSAO("explosao", 32, 32, 5),
    BRILHO("brilho", 32, 32, 4),
    VIDA("icone_vida", 16, 16, 1),
    VIDA_VAZIA("icone_vida_vazia", 16, 16, 1),
    VAGA("icone_vaga", 16, 16, 1),
    VAGA_LIVRE("icone_vaga_livre", 16, 16, 1);

    private final String caminho;
    private final int larguraQuadro;
    private final int alturaQuadro;
    private final int quadros;

    Imagem(String nome, int larguraQuadro, int alturaQuadro, int quadros) {
        this.caminho = "imagens/" + nome + ".png";
        this.larguraQuadro = larguraQuadro;
        this.alturaQuadro = alturaQuadro;
        this.quadros = quadros;
    }

    /**
     * A imagem de um elemento do mapa sai do símbolo que ele mesmo informa, sem instanceof.
     * Símbolo que ninguém conhece usa a imagem genérica.
     */
    public static Imagem doSimbolo(char simbolo) {
        return switch (simbolo) {
            case '@' -> NAVE;
            case 'L' -> BASE;
            case '#' -> ASTEROIDE;
            case 'X' -> INIMIGO;
            case 'P' -> PROFESSOR;
            case 'E' -> ENGENHEIRO;
            case 'T' -> ASTRONAUTA;
            default -> DESCONHECIDO;
        };
    }

    /** Relativo a core/assets. */
    public String caminho() {
        return caminho;
    }

    public int larguraQuadro() {
        return larguraQuadro;
    }

    public int alturaQuadro() {
        return alturaQuadro;
    }

    public int quadros() {
        return quadros;
    }
}
