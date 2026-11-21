package br.edu.unifor.missaomarte.gdx;

/**
 * Segurar uma tecla de movimento repete a ação: espera o atraso e depois repete a cada intervalo.
 * Cada repetição é um turno inteiro, como se a tecla fosse apertada de novo. Sem libGDX, para testar.
 */
public class RepeticaoDeTecla {

    public static final float ATRASO = 0.28f;
    public static final float INTERVALO = 0.14f;
    public static final int NENHUMA = -1;

    private int tecla = NENHUMA;
    private float espera;

    /** A tecla acabou de ser apertada (o primeiro turno quem chamou já jogou). */
    public void pressionar(int tecla) {
        this.tecla = tecla;
        this.espera = ATRASO;
    }

    /** Solta só se for a tecla que está repetindo. */
    public void soltar(int tecla) {
        if (this.tecla == tecla) {
            parar();
        }
    }

    public void parar() {
        tecla = NENHUMA;
    }

    /**
     * Avança o tempo e devolve a tecla a repetir agora, ou NENHUMA.
     * No máximo uma repetição por chamada, mesmo num quadro longo.
     */
    public int atualizar(float delta) {
        if (tecla == NENHUMA) {
            return NENHUMA;
        }
        espera -= delta;
        if (espera > 0) {
            return NENHUMA;
        }
        espera = Math.max(espera + INTERVALO, 0);
        return tecla;
    }
}
