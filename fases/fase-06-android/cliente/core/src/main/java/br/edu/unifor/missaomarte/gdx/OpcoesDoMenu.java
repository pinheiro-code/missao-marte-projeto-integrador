package br.edu.unifor.missaomarte.gdx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Os textos do menu em cada modo; a tecla de cada opção é o número na frente. */
public final class OpcoesDoMenu {

    private static final List<String> CONVIDADO = Collections.unmodifiableList(Arrays.asList(
            "1  Jogar", "2  Ranking local", "3  Limpar ranking local", "4  Entrar ou criar conta", "5  Sair"));
    private static final List<String> LOGADO = Collections.unmodifiableList(Arrays.asList(
            "1  Jogar", "2  Ranking global", "3  Meu histórico", "4  Sair da conta", "5  Sair"));

    private OpcoesDoMenu() {
    }

    /** A opção da fase 6, igual nos dois modos; fica fora do para() para os textos da fase 5 não mudarem. */
    public static final String SERVIDOR = "6  Servidor";

    public static List<String> para(boolean logado) {
        return logado ? LOGADO : CONVIDADO;
    }

    /** As cinco opções da fase 5 e a do servidor. */
    public static List<String> todas(boolean logado) {
        List<String> todas = new ArrayList<>(para(logado));
        todas.add(SERVIDOR);
        return Collections.unmodifiableList(todas);
    }
}
