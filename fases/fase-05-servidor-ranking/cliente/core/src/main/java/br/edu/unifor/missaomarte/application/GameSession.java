package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Acao;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Uma partida: recebe uma ação por turno e aplica as regras.
 * Não lê teclado nem escreve na tela, para servir a qualquer interface.
 */
public class GameSession {

    private final String piloto;
    private final Dificuldade dificuldade;
    private final Missao missao;
    private final Random random;
    private final Clock clock;
    private final Instant inicio;
    private Instant fim;
    private int pontos;
    private int movimentos;
    private boolean venceu;

    public GameSession(String piloto, Dificuldade dificuldade, Missao missao, Random random, Clock clock) {
        this.piloto = piloto;
        this.dificuldade = dificuldade;
        this.missao = missao;
        this.random = random;
        this.clock = clock;
        this.inicio = clock.instant();
        this.pontos = dificuldade.pontuacaoInicial();
        if (missao.todosEntregues()) {
            encerrar(true);
        }
    }

    public ResultadoTurno jogar(Acao acao) {
        if (acabou()) {
            throw new IllegalStateException("A partida já acabou.");
        }
        List<String> mensagens = new ArrayList<>();
        if (acao == Acao.SAIR) {
            mensagens.add("Missão abortada pelo piloto.");
            encerrar(false);
            return new ResultadoTurno(mensagens);
        }
        if (acao == Acao.INVALIDA) {
            mensagens.add("Comando inválido.");
            return new ResultadoTurno(mensagens);
        }

        // eventos: só registram o que as regras abaixo já decidem, comparando antes e depois
        List<EventoJogo> eventos = new ArrayList<>();
        Posicao antes = missao.nave().posicao();
        int noMapa = missao.restantes();
        int entreguesAntes = missao.entregues();
        int vidasAntes = missao.nave().vidas();

        if (acao == Acao.EMBARCAR) {
            embarcar(mensagens);
        } else {
            missao.moverNave(acao);
            pontos--;
            movimentos++;
        }
        if (!missao.nave().posicao().equals(antes)) {
            eventos.add(EventoJogo.MOVIMENTO);
        }
        if (missao.restantes() < noMapa) {
            eventos.add(EventoJogo.EMBARQUE);
        }
        desembarcar(mensagens);
        if (missao.entregues() > entreguesAntes) {
            eventos.add(EventoJogo.ENTREGA);
        }
        missao.moverInimigos(random);
        verificarColisao(mensagens);
        if (missao.nave().vidas() < vidasAntes) {
            eventos.add(EventoJogo.COLISAO);
        }
        verificarFim(mensagens);
        if (acabou()) {
            eventos.add(venceu ? EventoJogo.VITORIA : EventoJogo.DERROTA);
        }
        return new ResultadoTurno(mensagens, eventos);
    }

    private void embarcar(List<String> mensagens) {
        Passageiro passageiro = missao.passageiroNaNave();
        if (passageiro == null) {
            mensagens.add("Nenhum passageiro nesta posição.");
        } else if (missao.embarcar(passageiro)) {
            pontos += passageiro.pontuacao();
            mensagens.add(String.format("Passageiro %s embarcado com sucesso! +%d pontos!",
                    passageiro.nome(), passageiro.pontuacao()));
        } else {
            mensagens.add("Nave cheia! Não há espaço para mais passageiros.");
        }
    }

    private void desembarcar(List<String> mensagens) {
        int desceram = missao.desembarcarNaBase();
        if (desceram > 0) {
            mensagens.add(String.format("Passageiros entregues na base: %d. Total entregues: %d/%d.",
                    desceram, missao.entregues(), missao.total()));
        }
    }

    private void verificarColisao(List<String> mensagens) {
        if (!missao.naveColidiu()) {
            return;
        }
        Nave nave = missao.nave();
        nave.perderVida();
        if (nave.vidas() > 0) {
            mensagens.add(String.format("Alerta! Colisão detectada! Vidas restantes: %d", nave.vidas()));
        } else {
            mensagens.add("GAME OVER! A nave foi destruída.");
            encerrar(false);
        }
    }

    /** Ordem do original: colisão, depois pontos zerados, depois vitória. */
    private void verificarFim(List<String> mensagens) {
        if (pontos <= 0) {
            mensagens.add("Combustível/Pontuação zerada! Missão perdida.");
            encerrar(false);
        }
        if (acabou()) {
            return;
        }
        if (missao.todosEntregues()) {
            encerrar(true);
        } else if (missao.restantes() == 0) {
            mensagens.add("✨ ALERTA: Todos os passageiros resgatados! "
                    + "Retorne para a Plataforma de Pouso 'L' em (0,0) para completar a missão.");
        }
    }

    private void encerrar(boolean vitoria) {
        venceu = vitoria;
        fim = clock.instant();
    }

    public boolean acabou() {
        return fim != null;
    }

    public boolean venceu() {
        return venceu;
    }

    /** Segundos inteiros; para de contar quando a partida acaba. */
    public long duracaoSegundos() {
        Instant ate = acabou() ? fim : clock.instant();
        return Duration.between(inicio, ate).getSeconds();
    }

    public String piloto() {
        return piloto;
    }

    public Dificuldade dificuldade() {
        return dificuldade;
    }

    public int pontos() {
        return pontos;
    }

    public int movimentos() {
        return movimentos;
    }

    public int vidas() {
        return missao.nave().vidas();
    }

    public int aBordo() {
        return missao.nave().quantidadeABordo();
    }

    public int capacidade() {
        return missao.nave().capacidade();
    }

    public int entregues() {
        return missao.entregues();
    }

    public int restantes() {
        return missao.restantes();
    }

    public int total() {
        return missao.total();
    }
}
