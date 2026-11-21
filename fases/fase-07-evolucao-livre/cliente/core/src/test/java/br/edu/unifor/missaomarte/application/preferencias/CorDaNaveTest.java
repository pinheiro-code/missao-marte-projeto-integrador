package br.edu.unifor.missaomarte.application.preferencias;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CorDaNaveTest {

    @Test
    void ordemDasCores() {
        assertArrayEquals(new CorDaNave[] {
                CorDaNave.ORIGINAL, CorDaNave.VERMELHA, CorDaNave.VERDE, CorDaNave.AZUL, CorDaNave.AMARELA,
                CorDaNave.ROSA}, CorDaNave.values());
    }

    @Test
    void aPrimeiraEAOriginalSemTinta() {
        CorDaNave primeira = CorDaNave.values()[0];

        assertEquals(CorDaNave.ORIGINAL, primeira);
        assertEquals(CorDaNave.ORIGINAL, CorDaNave.padrao());
        assertEquals(1f, primeira.vermelho());
        assertEquals(1f, primeira.verde());
        assertEquals(1f, primeira.azul());
    }

    @Test
    void proximaSegueAOrdemEDaAVolta() {
        assertEquals(CorDaNave.VERMELHA, CorDaNave.ORIGINAL.proxima());
        assertEquals(CorDaNave.AZUL, CorDaNave.VERDE.proxima());
        assertEquals(CorDaNave.ORIGINAL, CorDaNave.ROSA.proxima(), "depois da última vem a primeira");
    }

    @Test
    void percorrerTodasVoltaAoComeco() {
        CorDaNave cor = CorDaNave.ORIGINAL;
        for (int i = 0; i < CorDaNave.values().length; i++) {
            cor = cor.proxima();
        }
        assertEquals(CorDaNave.ORIGINAL, cor);
    }

    @Test
    void nomesEmPortugues() {
        assertEquals("Original", CorDaNave.ORIGINAL.nome());
        assertEquals("Vermelha", CorDaNave.VERMELHA.nome());
        assertEquals("Verde", CorDaNave.VERDE.nome());
        assertEquals("Azul", CorDaNave.AZUL.nome());
        assertEquals("Amarela", CorDaNave.AMARELA.nome());
        assertEquals("Rosa", CorDaNave.ROSA.nome());
    }

    @Test
    void componentesEntreZeroEUmESoAOriginalSemTinta() {
        for (CorDaNave cor : CorDaNave.values()) {
            for (float c : new float[] {cor.vermelho(), cor.verde(), cor.azul()}) {
                assertTrue(c >= 0f && c <= 1f, cor + " fora de 0..1: " + c);
            }
            boolean semTinta = cor.vermelho() == 1f && cor.verde() == 1f && cor.azul() == 1f;
            assertEquals(cor == CorDaNave.ORIGINAL, semTinta, cor + ": só a original fica sem tinta");
        }
    }
}
