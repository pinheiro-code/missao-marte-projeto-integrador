package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.domain.ElementoMapa;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Onde cada elemento está sendo desenhado, em células fracionárias. A regra move a nave
 * de uma vez; aqui o desenho persegue a posição lógica a uma velocidade fixa, em linha reta.
 * Compara por identidade (dois objetos iguais são dois objetos) e não muda o domínio.
 */
public class PosicoesVisuais {

    /** Células por segundo. */
    public static final float VELOCIDADE = 12f;

    private final Map<ElementoMapa, float[]> posicoes = new IdentityHashMap<>();

    /**
     * Uma vez por quadro, com os elementos que estão no mapa agora.
     * Quem sumiu (embarcou, por exemplo) é esquecido; quem é novo começa no lugar, sem deslizar.
     */
    public void atualizar(List<? extends ElementoMapa> elementos, float delta) {
        Set<ElementoMapa> presentes = Collections.newSetFromMap(new IdentityHashMap<>());
        presentes.addAll(elementos);
        posicoes.keySet().retainAll(presentes);
        float passo = VELOCIDADE * delta;
        for (ElementoMapa elemento : elementos) {
            float alvoX = elemento.posicao().x();
            float alvoY = elemento.posicao().y();
            float[] atual = posicoes.get(elemento);
            if (atual == null) {
                posicoes.put(elemento, new float[] {alvoX, alvoY});
                continue;
            }
            float dx = alvoX - atual[0];
            float dy = alvoY - atual[1];
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);
            if (distancia <= passo) {
                atual[0] = alvoX;
                atual[1] = alvoY;
            } else {
                atual[0] += dx / distancia * passo;
                atual[1] += dy / distancia * passo;
            }
        }
    }

    /** X visual; elemento ainda não visto fica na posição lógica. */
    public float x(ElementoMapa elemento) {
        float[] p = posicoes.get(elemento);
        return p == null ? elemento.posicao().x() : p[0];
    }

    public float y(ElementoMapa elemento) {
        float[] p = posicoes.get(elemento);
        return p == null ? elemento.posicao().y() : p[1];
    }

    public int quantidade() {
        return posicoes.size();
    }
}
