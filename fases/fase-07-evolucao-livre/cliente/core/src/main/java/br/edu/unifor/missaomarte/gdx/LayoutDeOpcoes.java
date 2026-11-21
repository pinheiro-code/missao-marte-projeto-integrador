package br.edu.unifor.missaomarte.gdx;

import java.util.ArrayList;
import java.util.List;

/**
 * Botões das telas fora da partida (menu e formulários) no modo toque, dentro do 800x600 do jogo.
 * Recebe uma região e as linhas de botões, de cima para baixo; cada linha divide a largura em
 * partes iguais. Botão com menos de 80 de altura ou de largura é erro de quem montou a tela.
 * Sem libGDX, para testar.
 *
 *     new LayoutDeOpcoes(40, 20, 720, 430).linha("ok", "Confirmar", "voltar", "Voltar").montar();
 */
public final class LayoutDeOpcoes {

    public static final float MINIMO = 80;
    static final float MAXIMO = 90;
    static final float ESPACO = 8;

    private final float x;
    private final float y;
    private final float largura;
    private final float altura;
    private final List<String[]> linhas = new ArrayList<>();

    /** Região com (x, y) no canto de baixo à esquerda; tem de caber no 800x600. */
    public LayoutDeOpcoes(float x, float y, float largura, float altura) {
        if (x < 0 || y < 0 || x + largura > LayoutDosControles.LARGURA_DO_JOGO
                || y + altura > LayoutDosControles.ALTURA_DO_JOGO) {
            throw new IllegalArgumentException("A região tem de caber no 800x600 do jogo.");
        }
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
    }

    /** Uma linha: pares id e rótulo, da esquerda para a direita. */
    public LayoutDeOpcoes linha(String... idsERotulos) {
        if (idsERotulos.length == 0 || idsERotulos.length % 2 != 0) {
            throw new IllegalArgumentException("Cada botão precisa de id e rótulo.");
        }
        linhas.add(idsERotulos);
        return this;
    }

    public List<BotaoVirtual> montar() {
        int n = linhas.size();
        float alturaDaLinha = Math.min(MAXIMO, (altura - (n - 1) * ESPACO) / n);
        if (alturaDaLinha < MINIMO) {
            throw new IllegalArgumentException("Linhas demais para a região: botão abaixo de " + MINIMO + ".");
        }
        List<BotaoVirtual> botoes = new ArrayList<>();
        float topo = y + altura;
        for (int i = 0; i < n; i++) {
            String[] linha = linhas.get(i);
            int k = linha.length / 2;
            float larguraDoBotao = (largura - (k - 1) * ESPACO) / k;
            if (larguraDoBotao < MINIMO) {
                throw new IllegalArgumentException("Botões demais na linha: largura abaixo de " + MINIMO + ".");
            }
            float by = topo - (i + 1) * alturaDaLinha - i * ESPACO;
            for (int j = 0; j < k; j++) {
                float bx = x + j * (larguraDoBotao + ESPACO);
                botoes.add(new BotaoVirtual(linha[2 * j], linha[2 * j + 1], bx, by, larguraDoBotao, alturaDaLinha));
            }
        }
        return botoes;
    }
}
