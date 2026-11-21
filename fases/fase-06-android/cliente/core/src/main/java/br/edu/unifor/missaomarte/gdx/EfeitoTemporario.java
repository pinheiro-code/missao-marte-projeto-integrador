package br.edu.unifor.missaomarte.gdx;

import java.util.List;

/**
 * Efeito de uma execução só (explosão, brilho, flash, nave piscando): guarda o próprio tempo,
 * espalha os quadros igualmente pela duração e termina sozinho. Sem libGDX, para testar.
 */
public class EfeitoTemporario {

    private final Imagem imagem;
    private final float x;
    private final float y;
    private final float duracao;
    private float tempo;

    /** imagem é null nos efeitos sem sprite (flash e nave piscando); x e y em células. */
    public EfeitoTemporario(Imagem imagem, float x, float y, float duracao) {
        this.imagem = imagem;
        this.x = x;
        this.y = y;
        this.duracao = duracao;
    }

    /** Avança todos e tira da lista os que terminaram. */
    public static void atualizarTodos(List<? extends EfeitoTemporario> efeitos, float delta) {
        for (EfeitoTemporario efeito : efeitos) {
            efeito.atualizar(delta);
        }
        efeitos.removeIf(EfeitoTemporario::terminou);
    }

    public void atualizar(float delta) {
        tempo += delta;
    }

    public boolean terminou() {
        return tempo >= duracao;
    }

    /** De 0 a 1. */
    public float progresso() {
        return Math.min(1, tempo / duracao);
    }

    /** Quadro da faixa para o tempo atual: cada quadro dura duracao / quadros. */
    public int quadro() {
        int quadros = imagem == null ? 1 : imagem.quadros();
        return Math.min(quadros - 1, (int) (progresso() * quadros));
    }

    /** Para a nave piscando: alterna aceso e apagado 10 vezes por segundo. */
    public boolean aceso() {
        return ((int) (tempo * 10)) % 2 == 0;
    }

    public Imagem imagem() {
        return imagem;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }
}
