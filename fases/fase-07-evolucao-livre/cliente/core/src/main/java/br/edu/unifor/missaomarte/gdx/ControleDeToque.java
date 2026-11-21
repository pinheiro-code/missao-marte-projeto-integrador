package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.domain.Acao;

/**
 * Botão da partida para Acao do jogo: a mesma Acao das teclas, então a GameSession não sabe de onde
 * ela veio. O Som não é Acao (vira INVALIDA; a tela trata). Só as setas repetem. Sem libGDX.
 */
public class ControleDeToque {

    public Acao acao(String botao) {
        switch (botao) {
            case LayoutDosControles.CIMA:
                return Acao.CIMA;
            case LayoutDosControles.BAIXO:
                return Acao.BAIXO;
            case LayoutDosControles.ESQUERDA:
                return Acao.ESQUERDA;
            case LayoutDosControles.DIREITA:
                return Acao.DIREITA;
            case LayoutDosControles.EMBARCAR:
                return Acao.EMBARCAR;
            case LayoutDosControles.ABORTAR:
                return Acao.SAIR;
            default:
                return Acao.INVALIDA;
        }
    }

    public boolean repete(String botao) {
        Acao acao = acao(botao);
        return acao.dx() != 0 || acao.dy() != 0;
    }
}
