package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.domain.Acao;

/**
 * Repetição de uma seta tocada: guarda o dedo dono (o ponteiro), o botão e a Acao, e deixa o tempo
 * com a RepeticaoDeTecla (mesmo atraso e intervalo do teclado). Soltar outro dedo não para; soltar
 * o dono para; um segundo dedo em outra seta vira o novo dono. Sem libGDX, para testar.
 */
public class RepeticaoDoToque {

    public static final int NINGUEM = -1;

    private final RepeticaoDeTecla tempo = new RepeticaoDeTecla();
    private int dono = NINGUEM;
    private String botao;
    private Acao acao;

    /** O dedo acabou de apoiar na seta (o primeiro turno quem chamou já jogou). */
    public void pressionar(int ponteiro, String botao, Acao acao) {
        this.dono = ponteiro;
        this.botao = botao;
        this.acao = acao;
        tempo.pressionar(acao.ordinal());
    }

    public void soltar(int ponteiro) {
        if (ponteiro == dono) {
            parar();
        }
    }

    public void parar() {
        dono = NINGUEM;
        botao = null;
        acao = null;
        tempo.parar();
    }

    /** A Acao a repetir agora, ou null. No máximo uma por chamada. */
    public Acao atualizar(float delta) {
        if (dono == NINGUEM) {
            return null;
        }
        return tempo.atualizar(delta) == RepeticaoDeTecla.NENHUMA ? null : acao;
    }

    public int dono() {
        return dono;
    }

    /** null quando ninguém está repetindo. */
    public String botao() {
        return botao;
    }
}
