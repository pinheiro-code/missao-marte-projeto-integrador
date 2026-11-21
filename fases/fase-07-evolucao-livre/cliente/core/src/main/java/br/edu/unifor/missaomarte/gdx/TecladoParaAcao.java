package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.domain.Acao;
import com.badlogic.gdx.Input.Keys;

/**
 * Tecla do libGDX para Acao do jogo. Mapeia direto para o enum, sem o Acao.deTexto do console
 * (lá o embarque é 'c'; aqui é E). Tecla sem ação vira INVALIDA, que a tela ignora.
 * O controle Bluetooth chega como tecla: o direcional tem o mesmo código das setas, o A embarca e o B
 * aborta, como a tecla Voltar do Android.
 */
public class TecladoParaAcao {

    public Acao converter(int tecla) {
        return switch (tecla) {
            case Keys.W, Keys.UP -> Acao.CIMA;
            case Keys.S, Keys.DOWN -> Acao.BAIXO;
            case Keys.A, Keys.LEFT -> Acao.ESQUERDA;
            case Keys.D, Keys.RIGHT -> Acao.DIREITA;
            case Keys.E, Keys.BUTTON_A, Keys.DPAD_CENTER -> Acao.EMBARCAR;
            case Keys.Q, Keys.ESCAPE, Keys.BUTTON_B, Keys.BACK -> Acao.SAIR;
            default -> Acao.INVALIDA;
        };
    }
}
