package br.edu.unifor.missaomarte.application;

import java.util.List;

/** Onde o ranking fica guardado. Falha de gravação sai como UncheckedIOException. */
public interface RankingRepository {

    List<RankingEntry> carregar();

    void salvar(List<RankingEntry> ranking);

    void limpar();
}
