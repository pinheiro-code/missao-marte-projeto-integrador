package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.domain.Acao;
import br.edu.unifor.missaomarte.domain.ElementoMapa;
import br.edu.unifor.missaomarte.domain.Missao;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;

/**
 * Desenha o mapa com sprites: terreno, depois base, perigos e passageiros, e a nave por último.
 * A imagem sai do símbolo de cada elemento (Imagem.doSimbolo), nunca de instanceof.
 * A posição vem das PosicoesVisuais, para o desenho deslizar entre as células.
 */
public class MundoRenderer {

    private static final float C = Geometria.CELULA;

    private final Recursos recursos;
    private final Geometria geometria;

    public MundoRenderer(Recursos recursos, Geometria geometria) {
        this.recursos = recursos;
        this.geometria = geometria;
    }

    /** Chamar com o batch aberto. */
    public void desenhar(SpriteBatch batch, Missao missao, PosicoesVisuais posicoes, float tempo,
                         Acao direcao, boolean naveAcesa, List<EfeitoTemporario> efeitos) {
        TextureRegion terreno = recursos.quadro(Imagem.TERRENO, 0);
        int t = missao.tamanho();
        for (int x = -t; x <= t; x++) {
            for (int y = -t; y <= t; y++) {
                batch.draw(terreno, geometria.esquerda(x), geometria.baixo(y), C, C);
            }
        }

        // de trás para frente: base primeiro, nave por último (por cima)
        List<ElementoMapa> elementos = missao.elementos();
        for (int i = elementos.size() - 1; i >= 0; i--) {
            ElementoMapa e = elementos.get(i);
            Imagem imagem = Imagem.doSimbolo(e.simbolo());
            float px = geometria.esquerda(posicoes.x(e));
            float py = geometria.baixo(posicoes.y(e));
            if (imagem == Imagem.NAVE) {
                if (naveAcesa) {
                    desenharNave(batch, px, py, tempo, direcao);
                }
            } else {
                batch.draw(recursos.quadroEmLaco(imagem, tempo), px, py, C, C);
            }
        }

        for (EfeitoTemporario efeito : efeitos) {
            batch.draw(recursos.quadro(efeito.imagem(), efeito.quadro()),
                    geometria.esquerda(efeito.x()), geometria.baixo(efeito.y()), C, C);
        }
    }

    /** O propulsor fica embaixo do desenho da nave, no mesmo quadro: girando os dois juntos, ele fica sempre atrás. */
    private void desenharNave(SpriteBatch batch, float x, float y, float tempo, Acao direcao) {
        float graus = rotacao(direcao);
        batch.draw(recursos.quadroEmLaco(Imagem.PROPULSOR, tempo), x, y, C / 2, C / 2, C, C, 1, 1, graus);
        batch.draw(recursos.quadro(Imagem.NAVE, 0), x, y, C / 2, C / 2, C, C, 1, 1, graus);
    }

    /** O sprite aponta para cima; o libGDX gira no sentido anti-horário. */
    static float rotacao(Acao direcao) {
        return switch (direcao) {
            case ESQUERDA -> 90;
            case BAIXO -> 180;
            case DIREITA -> 270;
            default -> 0;
        };
    }
}
