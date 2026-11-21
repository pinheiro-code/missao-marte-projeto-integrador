package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.ElementoMapa;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MissaoFactoryTest {

    private static long contar(Missao missao, char simbolo) {
        return missao.elementos().stream().filter(e -> e.simbolo() == simbolo).count();
    }

    @Test
    void quantidadesPorDificuldade() {
        MissaoFactory fabrica = new MissaoFactory(new Random(1));
        for (Dificuldade dificuldade : Dificuldade.values()) {
            Missao missao = fabrica.criar(dificuldade, 5);

            assertEquals(dificuldade.passageiros(), missao.total(), dificuldade.name());
            assertEquals(dificuldade.passageiros(), missao.restantes(), dificuldade.name());
            assertEquals(dificuldade.asteroides(), contar(missao, '#'), dificuldade.name());
            assertEquals(dificuldade.inimigos(), contar(missao, 'X'), dificuldade.name());
        }
        assertEquals(4, Dificuldade.FACIL.passageiros());
        assertEquals(5, Dificuldade.MEDIO.passageiros());
        assertEquals(5, Dificuldade.DIFICIL.passageiros());
    }

    /** A nave nasce na base; fora isso, cada objeto tem a própria célula. */
    @Test
    void nenhumObjetoNaMesmaCelulaNemNaBase() {
        MissaoFactory fabrica = new MissaoFactory(new Random(7));
        for (int repeticao = 0; repeticao < 50; repeticao++) {
            Missao missao = fabrica.criar(Dificuldade.DIFICIL, 2);
            List<ElementoMapa> elementos = missao.elementos();
            Set<Posicao> posicoes = new HashSet<>();
            elementos.forEach(e -> posicoes.add(e.posicao()));

            long naOrigem = elementos.stream().filter(e -> e.posicao().equals(Posicao.ORIGEM)).count();
            assertEquals(2, naOrigem, "só a nave e a base ficam em (0,0)");
            assertEquals(elementos.size() - 1, posicoes.size(), "duas coisas na mesma célula");
        }
    }

    @Test
    void mapaAbaixoDoMinimoERecusado() {
        MissaoFactory fabrica = new MissaoFactory(new Random(1));

        assertThrows(IllegalArgumentException.class, () -> fabrica.criar(Dificuldade.MEDIO, 1));
        assertThrows(IllegalArgumentException.class, () -> fabrica.criar(Dificuldade.DIFICIL, 1));
    }

    @Test
    void ajusteDoTamanhoSobeParaOMinimo() {
        assertEquals(1, Dificuldade.FACIL.ajustarTamanho(1));
        assertEquals(2, Dificuldade.MEDIO.ajustarTamanho(1));
        assertEquals(2, Dificuldade.DIFICIL.ajustarTamanho(1));
        assertEquals(5, Dificuldade.MEDIO.ajustarTamanho(5));
    }

    /** O menor mapa de cada dificuldade fica pronto (antes, mapa 1 no Médio travava). */
    @Test
    void menorMapaDeCadaDificuldadeTermina() {
        MissaoFactory fabrica = new MissaoFactory(new Random(3));
        for (Dificuldade dificuldade : Dificuldade.values()) {
            Missao missao = fabrica.criar(dificuldade, dificuldade.ajustarTamanho(1));

            assertEquals(dificuldade.passageiros(), missao.total());
        }
    }
}
