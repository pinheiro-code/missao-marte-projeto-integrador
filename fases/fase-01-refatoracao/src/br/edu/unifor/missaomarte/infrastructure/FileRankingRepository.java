package br.edu.unifor.missaomarte.infrastructure;

import br.edu.unifor.missaomarte.application.RankingEntry;
import br.edu.unifor.missaomarte.application.RankingRepository;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Ranking num arquivo JSON, no mesmo formato da fase 0:
 * [{"name":"Ana","score":42,"dificuldade":"FACIL","passageirosColetados":4,"dataHora":"...","tempoJogo":12}]
 */
public class FileRankingRepository implements RankingRepository {

    private final Path arquivo;

    public FileRankingRepository(Path arquivo) {
        this.arquivo = arquivo;
    }

    /** Arquivo ausente ou ilegível vira ranking vazio, como no original. */
    @Override
    public List<RankingEntry> carregar() {
        if (!Files.exists(arquivo)) {
            return new ArrayList<>();
        }
        try {
            return interpretar(Files.readString(arquivo, StandardCharsets.UTF_8).trim());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void salvar(List<RankingEntry> ranking) {
        List<String> objetos = new ArrayList<>();
        for (RankingEntry e : ranking) {
            objetos.add("{\"name\":\"" + e.name().replace("\"", "\\\"") + "\""
                    + ",\"score\":" + e.score()
                    + ",\"dificuldade\":\"" + e.dificuldade().name() + "\""
                    + ",\"passageirosColetados\":" + e.passageirosColetados()
                    + ",\"dataHora\":\"" + e.dataHora() + "\""
                    + ",\"tempoJogo\":" + e.tempoJogo() + "}");
        }
        try {
            Files.writeString(arquivo, "[" + String.join(",", objetos) + "]", StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void limpar() {
        try {
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Leitor simples para o formato acima; objeto sem nome ou pontos é ignorado. */
    private List<RankingEntry> interpretar(String json) {
        List<RankingEntry> ranking = new ArrayList<>();
        int indice = 0;
        while (true) {
            int inicio = json.indexOf('{', indice);
            int fim = json.indexOf('}', inicio + 1);
            if (inicio < 0 || fim < 0) {
                return ranking;
            }
            RankingEntry entrada = interpretarObjeto(json.substring(inicio + 1, fim));
            if (entrada != null) {
                ranking.add(entrada);
            }
            indice = fim + 1;
        }
    }

    private RankingEntry interpretarObjeto(String objeto) {
        String name = null;
        Integer score = null;
        Dificuldade dificuldade = Dificuldade.MEDIO;
        int passageiros = 0;
        String dataHora = "";
        long tempoJogo = 0;
        for (String par : objeto.split(",")) {
            String[] chaveValor = par.split(":", 2);
            if (chaveValor.length != 2) {
                continue;
            }
            String chave = chaveValor[0].trim().replace("\"", "");
            String valor = chaveValor[1].trim();
            try {
                switch (chave) {
                    case "name" -> name = texto(valor).replace("\\\"", "\"");
                    case "score" -> score = Integer.parseInt(valor);
                    case "dificuldade" -> dificuldade = Dificuldade.deTexto(texto(valor));
                    case "passageirosColetados" -> passageiros = Integer.parseInt(valor);
                    case "dataHora" -> dataHora = texto(valor);
                    case "tempoJogo" -> tempoJogo = Long.parseLong(valor);
                    default -> { }
                }
            } catch (NumberFormatException ignorado) {
                // valor numérico estragado: fica o padrão, como no original
            }
        }
        if (name == null || score == null) {
            return null;
        }
        return new RankingEntry(name, score, dificuldade, passageiros, dataHora, tempoJogo);
    }

    private static String texto(String valor) {
        if (valor.length() >= 2 && valor.startsWith("\"") && valor.endsWith("\"")) {
            return valor.substring(1, valor.length() - 1);
        }
        return valor;
    }
}
