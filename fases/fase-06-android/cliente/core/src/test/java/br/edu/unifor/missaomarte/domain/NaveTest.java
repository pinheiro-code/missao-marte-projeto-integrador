package br.edu.unifor.missaomarte.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NaveTest {

    private static Passageiro passageiro() {
        return new Professor("Dr. Teste", new Posicao(1, 1));
    }

    @Test
    void embarcaAteACapacidadeEDepoisRecusa() {
        Nave nave = new Nave(2);

        assertTrue(nave.embarcar(passageiro()));
        assertFalse(nave.cheia());
        assertTrue(nave.embarcar(passageiro()));

        assertTrue(nave.cheia());
        assertFalse(nave.embarcar(passageiro()));
        assertEquals(2, nave.quantidadeABordo());
    }

    @Test
    void desembarcarEsvaziaENaoFicaMaisCheia() {
        Nave nave = new Nave(1);
        nave.embarcar(passageiro());

        assertEquals(1, nave.desembarcar());

        assertEquals(0, nave.quantidadeABordo());
        assertFalse(nave.cheia());
    }

    @Test
    void naoSaiDoMapaNaBorda() {
        Nave nave = new Nave(5);

        nave.mover(Acao.CIMA, 1);
        nave.mover(Acao.CIMA, 1);

        assertEquals(new Posicao(0, -1), nave.posicao());
    }

    @Test
    void nasceNaBaseComTresVidas() {
        Nave nave = new Nave(5);

        assertEquals(Posicao.ORIGEM, nave.posicao());
        assertEquals(3, nave.vidas());
    }
}
