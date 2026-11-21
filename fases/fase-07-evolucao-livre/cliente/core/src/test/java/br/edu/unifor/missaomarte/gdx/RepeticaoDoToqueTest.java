package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.edu.unifor.missaomarte.domain.Acao;
import org.junit.jupiter.api.Test;

class RepeticaoDoToqueTest {

    private final RepeticaoDoToque repeticao = new RepeticaoDoToque();

    @Test
    void esperaOAtrasoEDepoisRepeteAAcaoDoDono() {
        repeticao.pressionar(0, LayoutDosControles.CIMA, Acao.CIMA);

        assertNull(repeticao.atualizar(RepeticaoDeTecla.ATRASO - 0.01f));
        assertEquals(Acao.CIMA, repeticao.atualizar(0.02f));
        assertNull(repeticao.atualizar(RepeticaoDeTecla.INTERVALO - 0.02f));
        assertEquals(Acao.CIMA, repeticao.atualizar(0.03f));
        assertEquals(0, repeticao.dono());
        assertEquals(LayoutDosControles.CIMA, repeticao.botao());
    }

    @Test
    void soltarOutroPonteiroNaoPara() {
        repeticao.pressionar(0, LayoutDosControles.DIREITA, Acao.DIREITA);
        repeticao.soltar(1);

        assertEquals(Acao.DIREITA, repeticao.atualizar(1));
    }

    @Test
    void soltarODonoPara() {
        repeticao.pressionar(2, LayoutDosControles.BAIXO, Acao.BAIXO);
        repeticao.soltar(2);

        assertNull(repeticao.atualizar(1));
        assertEquals(RepeticaoDoToque.NINGUEM, repeticao.dono());
        assertNull(repeticao.botao());
    }

    @Test
    void segundoDedoEmOutraSetaViraONovoDono() {
        repeticao.pressionar(0, LayoutDosControles.CIMA, Acao.CIMA);
        repeticao.pressionar(1, LayoutDosControles.ESQUERDA, Acao.ESQUERDA);

        repeticao.soltar(0);
        assertEquals(Acao.ESQUERDA, repeticao.atualizar(1));
        repeticao.soltar(1);
        assertNull(repeticao.atualizar(1));
    }

    @Test
    void pararParaQualquerUm() {
        repeticao.pressionar(0, LayoutDosControles.CIMA, Acao.CIMA);
        repeticao.parar();

        assertNull(repeticao.atualizar(1));
    }

    @Test
    void semNinguemNaoRepete() {
        assertNull(repeticao.atualizar(5));
    }
}
