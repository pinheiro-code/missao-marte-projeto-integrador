package br.edu.unifor.missaomarte.gdx;

import java.util.ArrayList;
import java.util.List;

/**
 * Onde ficam os botões da partida no modo toque. O jogo ocupa 0..800 x 0..600; o ExtendViewport
 * garante um mundo de pelo menos 1060x600, e a coluna de controles é o que sobra à direita
 * (x de 800 até a largura do mundo). Setas em cruz com o Embarcar no meio; Som e Abortar em cima.
 * Sem libGDX, para testar em qualquer proporção.
 */
public final class LayoutDosControles {

    public static final float LARGURA_DO_JOGO = 800;
    public static final float ALTURA_DO_JOGO = 600;
    public static final float LARGURA_MINIMA = 1060;
    public static final float ALTURA_MINIMA = 600;
    /** 80 unidades dão uns 50 dp num celular 2400x1080; 64 ficariam abaixo dos 48 dp recomendados. */
    public static final float BOTAO_MINIMO = 80;
    static final float BOTAO_MAXIMO = 120;
    static final float MARGEM = 5;
    static final float ESPACO = 5;

    public static final String CIMA = "cima";
    public static final String BAIXO = "baixo";
    public static final String ESQUERDA = "esquerda";
    public static final String DIREITA = "direita";
    public static final String EMBARCAR = "embarcar";
    public static final String SOM = "som";
    public static final String ABORTAR = "abortar";
    public static final String CONTINUAR = "continuar";
    public static final String REENVIAR = "reenviar";

    private final float coluna;
    private final float larguraDaColuna;
    private final float lado;

    /** Largura e altura visíveis do mundo; abaixo do mínimo do ExtendViewport valem o mínimo. */
    public LayoutDosControles(float larguraDoMundo, float alturaDoMundo) {
        float largura = Math.max(larguraDoMundo, LARGURA_MINIMA);
        float altura = Math.max(alturaDoMundo, ALTURA_MINIMA);
        this.coluna = LARGURA_DO_JOGO;
        this.larguraDaColuna = largura - LARGURA_DO_JOGO;
        float pelaLargura = (larguraDaColuna - 2 * MARGEM - 2 * ESPACO) / 3;
        float pelaAltura = (altura - 2 * MARGEM - 3 * ESPACO) / 4;
        this.lado = Math.min(BOTAO_MAXIMO, Math.min(pelaLargura, pelaAltura));
    }

    /** Os 7 botões da partida, de baixo para cima, centralizados na coluna. */
    public List<BotaoVirtual> botoesDaPartida() {
        float passo = lado + ESPACO;
        float x0 = coluna + (larguraDaColuna - (3 * lado + 2 * ESPACO)) / 2;
        float y0 = MARGEM;
        List<BotaoVirtual> botoes = new ArrayList<>();
        botoes.add(new BotaoVirtual(BAIXO, "v", x0 + passo, y0, lado, lado));
        botoes.add(new BotaoVirtual(ESQUERDA, "<", x0, y0 + passo, lado, lado));
        botoes.add(new BotaoVirtual(EMBARCAR, "Embarcar", x0 + passo, y0 + passo, lado, lado));
        botoes.add(new BotaoVirtual(DIREITA, ">", x0 + 2 * passo, y0 + passo, lado, lado));
        botoes.add(new BotaoVirtual(CIMA, "^", x0 + passo, y0 + 2 * passo, lado, lado));
        float metade = (3 * lado + ESPACO) / 2;
        botoes.add(new BotaoVirtual(SOM, "Som", x0, y0 + 3 * passo, metade, lado));
        botoes.add(new BotaoVirtual(ABORTAR, "Abortar", x0 + metade + ESPACO, y0 + 3 * passo, metade, lado));
        return botoes;
    }

    /** Fim de partida: Continuar e, se o envio falhou, Reenviar, no lugar dos controles. */
    public List<BotaoVirtual> botoesDoFim(boolean comReenviar) {
        float largura = 3 * lado + 2 * ESPACO;
        float x0 = coluna + (larguraDaColuna - largura) / 2;
        List<BotaoVirtual> botoes = new ArrayList<>();
        botoes.add(new BotaoVirtual(CONTINUAR, "Continuar", x0, MARGEM + lado + ESPACO, largura, lado));
        if (comReenviar) {
            botoes.add(new BotaoVirtual(REENVIAR, "Reenviar", x0, MARGEM, largura, lado));
        }
        return botoes;
    }

    public float lado() {
        return lado;
    }
}
