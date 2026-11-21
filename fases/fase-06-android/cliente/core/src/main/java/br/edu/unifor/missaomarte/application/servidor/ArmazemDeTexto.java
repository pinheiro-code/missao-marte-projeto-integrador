package br.edu.unifor.missaomarte.application.servidor;

/**
 * Porta de um armazenamento de texto por chave, que sobrevive ao fechar o jogo.
 * No jogo é o Preferences do libGDX (PreferenciasDoJogo); nos testes, um mapa em memória.
 * Só guarda configuração: nunca senha, token ou nome da conta.
 */
public interface ArmazemDeTexto {

    /** null quando a chave não existe. */
    String ler(String chave);

    void gravar(String chave, String valor);

    void remover(String chave);
}
