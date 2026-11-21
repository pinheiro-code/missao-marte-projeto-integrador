package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Acao;
import org.junit.jupiter.api.Test;

class ControleDeToqueTest {

    private final ControleDeToque controle = new ControleDeToque();
    private final TecladoParaAcao teclado = new TecladoParaAcao();

    @Test
    void cadaBotaoViraAMesmaAcaoDaTecla() {
        assertEquals(teclado.converter(com.badlogic.gdx.Input.Keys.UP), controle.acao(LayoutDosControles.CIMA));
        assertEquals(Acao.CIMA, controle.acao(LayoutDosControles.CIMA));
        assertEquals(Acao.BAIXO, controle.acao(LayoutDosControles.BAIXO));
        assertEquals(Acao.ESQUERDA, controle.acao(LayoutDosControles.ESQUERDA));
        assertEquals(Acao.DIREITA, controle.acao(LayoutDosControles.DIREITA));
        assertEquals(Acao.EMBARCAR, controle.acao(LayoutDosControles.EMBARCAR));
        assertEquals(Acao.SAIR, controle.acao(LayoutDosControles.ABORTAR));
    }

    @Test
    void somEBotaoDesconhecidoNaoSaoAcao() {
        assertEquals(Acao.INVALIDA, controle.acao(LayoutDosControles.SOM));
        assertEquals(Acao.INVALIDA, controle.acao("qualquer"));
    }

    @Test
    void soAsSetasRepetem() {
        assertTrue(controle.repete(LayoutDosControles.CIMA));
        assertTrue(controle.repete(LayoutDosControles.BAIXO));
        assertTrue(controle.repete(LayoutDosControles.ESQUERDA));
        assertTrue(controle.repete(LayoutDosControles.DIREITA));
        assertFalse(controle.repete(LayoutDosControles.EMBARCAR));
        assertFalse(controle.repete(LayoutDosControles.ABORTAR));
        assertFalse(controle.repete(LayoutDosControles.SOM));
    }
}
