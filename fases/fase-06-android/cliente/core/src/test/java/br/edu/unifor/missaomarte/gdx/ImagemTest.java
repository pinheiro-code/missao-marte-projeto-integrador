package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Base;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import org.junit.jupiter.api.Test;

/** Símbolo para imagem, usando o símbolo que o próprio objeto do domínio informa. */
class ImagemTest {

    private static final Posicao P = new Posicao(1, 1);

    @Test
    void cadaSimboloDoDominioTemSuaImagem() {
        assertEquals(Imagem.NAVE, Imagem.doSimbolo(new Nave(5).simbolo()));
        assertEquals(Imagem.BASE, Imagem.doSimbolo(new Base().simbolo()));
        assertEquals(Imagem.ASTEROIDE, Imagem.doSimbolo(new Asteroide(P).simbolo()));
        assertEquals(Imagem.INIMIGO, Imagem.doSimbolo(new Inimigo(P).simbolo()));
        assertEquals(Imagem.PROFESSOR, Imagem.doSimbolo(new Professor("Dr.", P).simbolo()));
        assertEquals(Imagem.ENGENHEIRO, Imagem.doSimbolo(new Engenheiro("Eng.", P).simbolo()));
        assertEquals(Imagem.ASTRONAUTA, Imagem.doSimbolo(new Astronauta("Ast.", P).simbolo()));
    }

    @Test
    void simboloDesconhecidoUsaAImagemGenerica() {
        assertEquals(Imagem.DESCONHECIDO, Imagem.doSimbolo('?'));
        assertEquals(Imagem.DESCONHECIDO, Imagem.doSimbolo('z'));
    }
}
