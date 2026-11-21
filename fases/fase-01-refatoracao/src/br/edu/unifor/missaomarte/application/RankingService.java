package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Regras do ranking: top 5 por pontos, maior primeiro. */
public class RankingService {

    private static final int TAMANHO = 5;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Comparator<RankingEntry> MAIOR_PRIMEIRO =
            Comparator.comparingInt(RankingEntry::score).reversed();

    private final RankingRepository repositorio;
    private final Clock clock;

    public RankingService(RankingRepository repositorio, Clock clock) {
        this.repositorio = repositorio;
        this.clock = clock;
    }

    public List<RankingEntry> listar() {
        List<RankingEntry> ranking = new ArrayList<>(repositorio.carregar());
        ranking.sort(MAIOR_PRIMEIRO);
        return ranking;
    }

    /**
     * Entra só com pontos acima de zero e, com a lista cheia, só se for maior que o último.
     * Devolve se entrou.
     */
    public boolean registrar(String piloto, int pontos, Dificuldade dificuldade, int passageiros, long tempoJogo) {
        List<RankingEntry> ranking = listar();
        boolean cabe = ranking.size() < TAMANHO || pontos > ranking.get(ranking.size() - 1).score();
        if (pontos <= 0 || !cabe) {
            return false;
        }
        String dataHora = LocalDateTime.now(clock).format(FORMATO);
        ranking.add(new RankingEntry(piloto, pontos, dificuldade, passageiros, dataHora, tempoJogo));
        ranking.sort(MAIOR_PRIMEIRO);
        repositorio.salvar(ranking.subList(0, Math.min(TAMANHO, ranking.size())));
        return true;
    }

    public void limpar() {
        repositorio.limpar();
    }
}
