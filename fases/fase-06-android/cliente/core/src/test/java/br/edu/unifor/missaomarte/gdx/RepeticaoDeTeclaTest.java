package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.gdx.Input.Keys;
import org.junit.jupiter.api.Test;

/** Atraso de 0,28 s e depois uma repetição a cada 0,14 s. */
class RepeticaoDeTeclaTest {

    private static final int NADA = RepeticaoDeTecla.NENHUMA;

    private final RepeticaoDeTecla repeticao = new RepeticaoDeTecla();

    @Test
    void semTeclaNaoRepete() {
        assertEquals(NADA, repeticao.atualizar(1f));
    }

    @Test
    void esperaOAtrasoAntesDaPrimeiraRepeticao() {
        repeticao.pressionar(Keys.D);

        assertEquals(NADA, repeticao.atualizar(0.27f));
        assertEquals(Keys.D, repeticao.atualizar(0.02f));
    }

    @Test
    void depoisRepeteACadaIntervalo() {
        repeticao.pressionar(Keys.D);
        repeticao.atualizar(0.28f);

        assertEquals(NADA, repeticao.atualizar(0.13f));
        assertEquals(Keys.D, repeticao.atualizar(0.02f));
    }

    @Test
    void noMaximoUmaRepeticaoPorChamada() {
        repeticao.pressionar(Keys.D);

        assertEquals(Keys.D, repeticao.atualizar(2f), "um quadro de 2 s vale uma repetição só");
        assertEquals(Keys.D, repeticao.atualizar(0f), "o atraso acumulado não vira rajada: a próxima sai já");
        assertEquals(NADA, repeticao.atualizar(0.1f));
    }

    @Test
    void soltarParaERecomecaComAtraso() {
        repeticao.pressionar(Keys.D);
        repeticao.atualizar(0.3f);

        repeticao.soltar(Keys.A);
        assertEquals(Keys.D, repeticao.atualizar(0.14f), "soltar outra tecla não para a repetição");

        repeticao.soltar(Keys.D);
        assertEquals(NADA, repeticao.atualizar(1f));

        repeticao.pressionar(Keys.W);
        assertEquals(NADA, repeticao.atualizar(0.2f), "depois de parar, volta a esperar o atraso");
        assertEquals(Keys.W, repeticao.atualizar(0.1f));
    }
}
