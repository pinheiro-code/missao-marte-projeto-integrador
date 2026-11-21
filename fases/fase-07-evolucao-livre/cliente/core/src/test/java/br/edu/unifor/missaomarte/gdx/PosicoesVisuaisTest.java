package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.domain.Acao;
import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.List;
import org.junit.jupiter.api.Test;

/** A 12 células por segundo, um quadro de 1/24 s anda meia célula. */
class PosicoesVisuaisTest {

    private static final float MEIO_PASSO = 0.5f / PosicoesVisuais.VELOCIDADE;
    private static final float DELTA = 1e-4f;

    private final PosicoesVisuais posicoes = new PosicoesVisuais();

    @Test
    void objetoNovoComecaNaPosicaoLogicaSemDeslizar() {
        Asteroide asteroide = new Asteroide(new Posicao(3, -2));

        posicoes.atualizar(List.of(asteroide), 0.001f);

        assertEquals(3, posicoes.x(asteroide), DELTA);
        assertEquals(-2, posicoes.y(asteroide), DELTA);
    }

    @Test
    void deslizaAoMudarDeCelulaEChegaSemPassar() {
        Nave nave = new Nave(5);
        posicoes.atualizar(List.of(nave), 0);
        nave.mover(Acao.DIREITA, 5);

        posicoes.atualizar(List.of(nave), MEIO_PASSO);
        assertEquals(0.5f, posicoes.x(nave), DELTA);

        posicoes.atualizar(List.of(nave), 1f);
        assertEquals(1f, posicoes.x(nave), DELTA, "um quadro longo para no alvo, não passa");
    }

    @Test
    void naDiagonalAndaEmLinhaReta() {
        Nave nave = new Nave(5);
        posicoes.atualizar(List.of(nave), 0);
        nave.mover(Acao.DIREITA, 5);
        nave.mover(Acao.BAIXO, 5);

        posicoes.atualizar(List.of(nave), MEIO_PASSO);

        assertEquals(posicoes.x(nave), posicoes.y(nave), DELTA);
        float andou = (float) Math.hypot(posicoes.x(nave), posicoes.y(nave));
        assertEquals(0.5f, andou, DELTA);
    }

    @Test
    void esqueceQuemSumiuDoMapa() {
        Asteroide fica = new Asteroide(new Posicao(1, 1));
        Asteroide some = new Asteroide(new Posicao(1, 1));
        posicoes.atualizar(List.of(fica, some), 0);
        assertEquals(2, posicoes.quantidade(), "mesma célula, objetos diferentes: compara por identidade");

        posicoes.atualizar(List.of(fica), 0);

        assertEquals(1, posicoes.quantidade());
    }
}
