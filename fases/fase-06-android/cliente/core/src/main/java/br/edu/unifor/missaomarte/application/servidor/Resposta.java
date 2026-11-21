package br.edu.unifor.missaomarte.application.servidor;

/**
 * O desfecho de uma chamada ao servidor: um valor, ou uma mensagem de erro pronta para a tela.
 * A marca naoAutorizado vem do 401: numa rota protegida, quer dizer sessão expirada.
 */
public final class Resposta<T> {

    private final T valor;
    private final String erro;
    private final boolean naoAutorizado;

    private Resposta(T valor, String erro, boolean naoAutorizado) {
        this.valor = valor;
        this.erro = erro;
        this.naoAutorizado = naoAutorizado;
    }

    public static <T> Resposta<T> ok(T valor) {
        return new Resposta<>(valor, null, false);
    }

    public static <T> Resposta<T> erro(String mensagem) {
        return new Resposta<>(null, mensagem, false);
    }

    public static <T> Resposta<T> naoAutorizado(String mensagem) {
        return new Resposta<>(null, mensagem, true);
    }

    /** O mesmo erro com outro tipo de valor, para repassar a falha de uma etapa à seguinte. */
    public <U> Resposta<U> mesmoErro() {
        if (ok()) {
            throw new IllegalStateException("A resposta não é um erro.");
        }
        return new Resposta<>(null, erro, naoAutorizado);
    }

    public boolean ok() {
        return erro == null;
    }

    public T valor() {
        return valor;
    }

    public String erro() {
        return erro;
    }

    public boolean naoAutorizado() {
        return naoAutorizado;
    }
}
