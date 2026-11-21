package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.domain.Acao;
import com.badlogic.gdx.Input.Keys;

/**
 * Tecla do libGDX para Acao do jogo. Mapeia direto para o enum, sem o Acao.deTexto do console
 * (lá o embarque é 'c'; aqui é E). Tecla sem ação vira INVALIDA, que a tela ignora.
 */
public class TecladoParaAcao {

    public Acao converter(int tecla) {
        return switch (tecla) {
            case Keys.W, Keys.UP -> Acao.CIMA;
            case Keys.S, Keys.DOWN -> Acao.BAIXO;
            case Keys.A, Keys.LEFT -> Acao.ESQUERDA;
            case Keys.D, Keys.RIGHT -> Acao.DIREITA;
            case Keys.E -> Acao.EMBARCAR;
            case Keys.Q, Keys.ESCAPE -> Acao.SAIR;
            default -> Acao.INVALIDA;
        };
    }
}
