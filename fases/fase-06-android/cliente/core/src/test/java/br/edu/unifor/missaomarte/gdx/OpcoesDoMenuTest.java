package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class OpcoesDoMenuTest {

    @Test
    void convidadoTemORankingLocalEAEntrada() {
        assertEquals(List.of("1  Jogar", "2  Ranking local", "3  Limpar ranking local", "4  Entrar ou criar conta",
                "5  Sair"), OpcoesDoMenu.para(false));
    }

    @Test
    void logadoTemORankingGlobalEOHistorico() {
        assertEquals(List.of("1  Jogar", "2  Ranking global", "3  Meu histórico", "4  Sair da conta", "5  Sair"),
                OpcoesDoMenu.para(true));
    }
}
