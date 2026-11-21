package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Efeitos de uma execução e textos flutuantes, só com números. */
class EfeitosTest {

    private static final float DELTA = 1e-4f;

    @Test
    void quadrosSeEspalhamIgualmentePelaDuracao() {
        // explosão: 5 quadros em 1 s, 0,2 s cada
        EfeitoTemporario explosao = new EfeitoTemporario(Imagem.EXPLOSAO, 0, 0, 1f);
        assertEquals(0, explosao.quadro());
        explosao.atualizar(0.21f);
        assertEquals(1, explosao.quadro());
        explosao.atualizar(0.6f);
        assertEquals(4, explosao.quadro());
        explosao.atualizar(5f);
        assertEquals(4, explosao.quadro(), "depois do fim fica no último quadro");
    }

    @Test
    void efeitoQueTerminouSaiDaLista() {
        List<EfeitoTemporario> efeitos = new ArrayList<>();
        efeitos.add(new EfeitoTemporario(Imagem.BRILHO, 0, 0, 0.5f));
        efeitos.add(new EfeitoTemporario(Imagem.EXPLOSAO, 0, 0, 1f));

        EfeitoTemporario.atualizarTodos(efeitos, 0.6f);

        assertEquals(1, efeitos.size());
        assertEquals(Imagem.EXPLOSAO, efeitos.get(0).imagem());
        EfeitoTemporario.atualizarTodos(efeitos, 0.5f);
        assertTrue(efeitos.isEmpty());
    }

    @Test
    void navePiscaAlternandoAcesaEApagada() {
        EfeitoTemporario piscando = new EfeitoTemporario(null, 0, 0, 1f);
        assertTrue(piscando.aceso());
        piscando.atualizar(0.15f);
        assertFalse(piscando.aceso());
        piscando.atualizar(0.1f);
        assertTrue(piscando.aceso());
    }

    @Test
    void textoSobeEFicaTransparente() {
        TextoFlutuante texto = new TextoFlutuante("+10", 2, 3);
        assertEquals(3, texto.yAtual(), DELTA);
        assertEquals(1, texto.opacidade(), DELTA);

        texto.atualizar(TextoFlutuante.DURACAO / 2);

        assertEquals(3 - TextoFlutuante.SUBIDA / 2, texto.yAtual(), DELTA, "subir na tela é diminuir o Y do jogo");
        assertEquals(0.5f, texto.opacidade(), DELTA);
        texto.atualizar(TextoFlutuante.DURACAO);
        assertTrue(texto.terminou());
        assertEquals(0, texto.opacidade(), DELTA);
    }

    @Test
    void variacaoDeUmPontoNaoViraTexto() {
        assertFalse(TextoFlutuante.mostraVariacao(-1));
        assertFalse(TextoFlutuante.mostraVariacao(0));
        assertTrue(TextoFlutuante.mostraVariacao(10));
        assertTrue(TextoFlutuante.mostraVariacao(-6));
        assertEquals("+10", TextoFlutuante.formatarVariacao(10));
        assertEquals("-6", TextoFlutuante.formatarVariacao(-6));
    }
}
