package br.edu.unifor.missaomarte.gdx;

/**
 * Um botão de toque: id (o que a tela faz com ele), rótulo e retângulo em coordenadas do mundo,
 * com (x, y) no canto de baixo à esquerda, como no libGDX. Sem libGDX, para testar.
 */
public final class BotaoVirtual {

    private final String id;
    private final String rotulo;
    private final float x;
    private final float y;
    private final float largura;
    private final float altura;

    public BotaoVirtual(String id, String rotulo, float x, float y, float largura, float altura) {
        this.id = id;
        this.rotulo = rotulo;
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
    }

    /** As bordas contam como dentro: um toque na linha do botão é dele. */
    public boolean contem(float px, float py) {
        return px >= x && px <= x + largura && py >= y && py <= y + altura;
    }

    /** Verdadeiro quando os dois retângulos têm área em comum (encostar na borda não conta). */
    public boolean sobrepoe(BotaoVirtual outro) {
        return x < outro.x + outro.largura && outro.x < x + largura && y < outro.y + outro.altura
                && outro.y < y + altura;
    }

    public String id() {
        return id;
    }

    public String rotulo() {
        return rotulo;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float largura() {
        return largura;
    }

    public float altura() {
        return altura;
    }

    @Override
    public String toString() {
        return id + "[" + x + ", " + y + ", " + largura + "x" + altura + "]";
    }
}
