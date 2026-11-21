package br.edu.unifor.missaomarte.gdx;

/** Tremor da tela na colisão: começa forte e diminui até zero. Sem libGDX, para testar. */
public class Tremor {

    private float intensidade;
    private float duracao;
    private float restante;

    /** Intensidade em pixels, duração em segundos. */
    public void iniciar(float intensidade, float duracao) {
        this.intensidade = intensidade;
        this.duracao = duracao;
        this.restante = duracao;
    }

    public void atualizar(float delta) {
        restante = Math.max(0, restante - delta);
    }

    /** Cai em linha reta da intensidade até zero. */
    public float amplitude() {
        return duracao <= 0 ? 0 : intensidade * restante / duracao;
    }

    public boolean ativo() {
        return restante > 0;
    }

    /** Deslocamento da câmera: oscila rápido dentro da amplitude. */
    public float deslocamentoX() {
        return amplitude() * (float) Math.sin(restante * 90);
    }

    public float deslocamentoY() {
        return amplitude() * (float) Math.cos(restante * 70);
    }
}
