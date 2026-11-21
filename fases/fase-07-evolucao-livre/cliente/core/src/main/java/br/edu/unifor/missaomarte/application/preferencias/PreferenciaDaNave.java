package br.edu.unifor.missaomarte.application.preferencias;

import br.edu.unifor.missaomarte.application.servidor.ArmazemDeTexto;

/**
 * A cor da nave escolhida pelo jogador, guardada na chave nave.cor do mesmo armazém do endereço do servidor
 * (no jogo, as Preferences). Grava o nome da constante (ex.: AZUL); valor ausente ou que não seja uma das
 * constantes vale a primeira cor, sem erro, para um arquivo editado à mão não travar o jogo.
 */
public final class PreferenciaDaNave {

    public static final String CHAVE = "nave.cor";

    private final ArmazemDeTexto armazem;

    public PreferenciaDaNave(ArmazemDeTexto armazem) {
        this.armazem = armazem;
    }

    public CorDaNave atual() {
        String valor = armazem.ler(CHAVE);
        if (valor != null) {
            for (CorDaNave cor : CorDaNave.values()) {
                if (cor.name().equals(valor)) {
                    return cor;
                }
            }
        }
        return CorDaNave.padrao();
    }

    public void salvar(CorDaNave cor) {
        armazem.gravar(CHAVE, cor.name());
    }
}
