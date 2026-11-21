package br.edu.unifor.missaomarte.console;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingEntry;
import br.edu.unifor.missaomarte.application.RankingService;
import br.edu.unifor.missaomarte.domain.Acao;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/** Menu, leitura do teclado e textos da tela. As regras ficam na GameSession. */
public class ConsoleGame {

    private static final int TAMANHO_PADRAO = 5;
    private static final String LINHA = "================================================================";

    private final Scanner scanner;
    private final MissaoFactory missaoFactory;
    private final RankingService rankingService;
    private final MapaRenderer mapaRenderer;
    private final Random random;
    private final Clock clock;

    public ConsoleGame(Scanner scanner, MissaoFactory missaoFactory, RankingService rankingService,
                       MapaRenderer mapaRenderer, Random random, Clock clock) {
        this.scanner = scanner;
        this.missaoFactory = missaoFactory;
        this.rankingService = rankingService;
        this.mapaRenderer = mapaRenderer;
        this.random = random;
        this.clock = clock;
    }

    public void iniciar() {
        exibirBoasVindas();
        while (true) {
            if (!scanner.hasNextLine()) {
                System.out.println("\nEntrada encerrada. Obrigado por jogar!");
                return;
            }
            exibirMenu();
            String opcao = lerLinha("Escolha uma opção: ", "1");
            switch (opcao) {
                case "1" -> jogarPartida();
                case "2" -> exibirRanking();
                case "3" -> resetarRanking();
                case "4" -> {
                    System.out.println("\nObrigado por jogar a Missão Marte Unifor!");
                    return;
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void exibirBoasVindas() {
        System.out.println(LINHA);
        System.out.println("             MISSÃO MARTE UNIFOR - VERSÃO 10.0                  ");
        System.out.println(LINHA);
        System.out.println("  Pilote sua nave, salve os passageiros e desvie dos perigos!   ");
        System.out.println(LINHA);
    }

    private void exibirMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Iniciar Nova Missão");
        System.out.println("2. Visualizar Ranking Top 5");
        System.out.println("3. Resetar Histórico de Ranking");
        System.out.println("4. Sair do Jogo");
        System.out.println("----------------------");
    }

    private void jogarPartida() {
        String piloto = lerLinha("\nDigite o nome do piloto: ", "Piloto Anônimo");
        if (piloto.isEmpty()) {
            piloto = "Piloto Anônimo";
        }
        System.out.print("Escolha a Dificuldade (facil/medio/dificil): ");
        Dificuldade dificuldade = Dificuldade.deTexto(lerLinha("", "medio"));
        int tamanho = lerTamanhoMapa(dificuldade);

        System.out.println("\nIniciando missão na dificuldade " + dificuldade + "...");
        System.out.println("Pressione Enter para decolar!");
        if (!scanner.hasNextLine()) {
            System.out.println("Entrada encerrada. Missão não iniciada.");
            return;
        }
        scanner.nextLine();

        Missao missao = missaoFactory.criar(dificuldade, tamanho);
        GameSession sessao = new GameSession(piloto, dificuldade, missao, random, clock);
        while (!sessao.acabou()) {
            exibirTurno(missao, sessao);
            String comando = lerLinha("Comando (w/s/a/d/c/q): ", null);
            if (comando == null) {
                System.out.println("Entrada encerrada. Missão abortada.");
                return;
            }
            if (comando.isEmpty()) {
                continue;
            }
            sessao.jogar(Acao.deTexto(comando)).mensagens().forEach(System.out::println);
        }
        if (sessao.venceu()) {
            exibirVitoria(sessao);
        }
    }

    private int lerTamanhoMapa(Dificuldade dificuldade) {
        int tamanho;
        try {
            tamanho = Integer.parseInt(lerLinha("Tamanho do mapa (ex: 5 para mapa de -5 a +5): ", "5"));
            if (tamanho <= 0) {
                tamanho = TAMANHO_PADRAO;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida, usando tamanho padrão (5).");
            tamanho = TAMANHO_PADRAO;
        }
        int ajustado = dificuldade.ajustarTamanho(tamanho);
        if (ajustado != tamanho) {
            System.out.printf("Mapa pequeno demais para a dificuldade %s. Usando tamanho %d.%n", dificuldade, ajustado);
        }
        return ajustado;
    }

    private void exibirTurno(Missao missao, GameSession sessao) {
        System.out.println();
        System.out.printf("Mapa da Missão (Pontos: %d) - Piloto: %s%n", sessao.pontos(), sessao.piloto());
        System.out.println(mapaRenderer.desenhar(missao));
        System.out.println("Comandos: w/s/a/d (mover), c (embarcar), q (sair)");
        System.out.println("Passageiros na superfície marciana:");
        for (Passageiro p : missao.passageiros()) {
            System.out.printf(" - %s (%s) em (%d,%d)%n", p.nome(), p.descricao(), p.posicao().x(), p.posicao().y());
        }
        Posicao nave = missao.nave().posicao();
        System.out.printf("Nave em (%d,%d) | Pontos: %d | Vidas: %d | A bordo: %d/%d | Entregues: %d | Restantes: %d | Total: %d%n",
                nave.x(), nave.y(), sessao.pontos(), sessao.vidas(), sessao.aBordo(), sessao.capacidade(),
                sessao.entregues(), sessao.restantes(), sessao.total());
    }

    private void exibirVitoria(GameSession sessao) {
        List<RankingEntry> ranking = rankingService.listar();
        System.out.println("\n" + LINHA);
        System.out.println("🚀 DECOLAGEM AUTORIZADA! Nave acoplada à plataforma em (0,0).");
        System.out.println("Retornando à órbita marciana com todos os passageiros. Missão cumprida!");
        System.out.println(LINHA);
        System.out.println("Estatísticas da Partida:");
        System.out.printf(" - Pontuação Final: %d pontos%n", sessao.pontos());
        System.out.printf(" - Movimentos Efetuados: %d%n", sessao.movimentos());
        System.out.printf(" - Tempo de Jogo: %d segundos%n", sessao.duracaoSegundos());
        System.out.printf(" - Passageiros Resgatados: %d%n", sessao.entregues());
        int recorde = ranking.isEmpty() ? 0 : ranking.get(0).score();
        if (sessao.pontos() > recorde && recorde > 0) {
            System.out.println("🏆 Novo recorde absoluto do sistema!");
        } else if (recorde > 0) {
            System.out.printf(" - Recorde atual a ser batido: %d pontos (Piloto: %s)%n", recorde, ranking.get(0).name());
        }
        System.out.println(LINHA);

        try {
            if (rankingService.registrar(sessao.piloto(), sessao.pontos(), sessao.dificuldade(),
                    sessao.entregues(), sessao.duracaoSegundos())) {
                System.out.println("Parabéns! Você entrou para o Top 5 de pilotos!");
            }
        } catch (UncheckedIOException e) {
            System.out.println("Não foi possível salvar o ranking: " + e.getCause().getMessage());
        }
    }

    private void exibirRanking() {
        List<RankingEntry> ranking = rankingService.listar();
        System.out.println("\n====== RANKING TOP 5 PILOTOS ======");
        if (ranking.isEmpty()) {
            System.out.println(" - Nenhum registro encontrado. Seja o primeiro a jogar!");
        }
        int posicao = 1;
        for (RankingEntry e : ranking) {
            System.out.printf("%d. %s - %d pts | Dificuldade: %s | Coletados: %d | Tempo: %ds | %s%n",
                    posicao++, e.name(), e.score(), e.dificuldade(), e.passageirosColetados(), e.tempoJogo(), e.dataHora());
        }
        System.out.println("===================================");
    }

    private void resetarRanking() {
        System.out.print("Você realmente deseja limpar o histórico de ranking? (s/n): ");
        String confirmacao = lerLinha("", "n").toLowerCase();
        if (!confirmacao.equals("s") && !confirmacao.equals("sim")) {
            System.out.println("Operação cancelada.");
            return;
        }
        try {
            rankingService.limpar();
            System.out.println("Ranking resetado com sucesso!");
        } catch (UncheckedIOException e) {
            System.out.println("Erro ao resetar ranking: " + e.getCause().getMessage());
        }
    }

    /** Linha sem espaços nas pontas; no fim da entrada devolve o valor padrão. */
    private String lerLinha(String prompt, String padrao) {
        if (!prompt.isEmpty()) {
            System.out.print(prompt);
        }
        return scanner.hasNextLine() ? scanner.nextLine().trim() : padrao;
    }
}
