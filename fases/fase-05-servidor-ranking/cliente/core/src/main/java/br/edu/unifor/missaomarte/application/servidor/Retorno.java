package br.edu.unifor.missaomarte.application.servidor;

/** Callback de uma chamada ao servidor; o adaptador sempre chama na thread do jogo. */
public interface Retorno<T> {

    void receber(Resposta<T> resposta);
}
