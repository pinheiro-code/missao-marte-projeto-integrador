package br.edu.unifor.missaomarte.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** O cenário de uma partida: mapa, nave, base, passageiros e perigos. */
public class Missao {

    private final int tamanho;
    private final Nave nave;
    private final Base base = new Base();
    private final List<Passageiro> passageiros = new ArrayList<>();
    private final List<Asteroide> asteroides = new ArrayList<>();
    private final List<Inimigo> inimigos = new ArrayList<>();
    private int total;
    private int entregues;

    public Missao(int tamanho, Nave nave) {
        this.tamanho = tamanho;
        this.nave = nave;
    }

    public void adicionar(Passageiro passageiro) {
        passageiros.add(passageiro);
        total++;
    }

    public void adicionar(Asteroide asteroide) {
        asteroides.add(asteroide);
    }

    public void adicionar(Inimigo inimigo) {
        inimigos.add(inimigo);
    }

    /**
     * Ordem de desenho quando dois elementos dividem a célula:
     * nave, passageiro, asteroide, inimigo, base (igual ao jogo original).
     */
    public List<ElementoMapa> elementos() {
        List<ElementoMapa> elementos = new ArrayList<>();
        elementos.add(nave);
        elementos.addAll(passageiros);
        elementos.addAll(asteroides);
        elementos.addAll(inimigos);
        elementos.add(base);
        return elementos;
    }

    public boolean ocupada(Posicao posicao) {
        return elementos().stream().anyMatch(e -> e.posicao().equals(posicao));
    }

    public void moverNave(Acao acao) {
        nave.mover(acao, tamanho);
    }

    public void moverInimigos(Random random) {
        for (Inimigo inimigo : inimigos) {
            inimigo.mover(random, tamanho);
        }
    }

    /** Passageiro na célula da nave, ou null. */
    public Passageiro passageiroNaNave() {
        for (Passageiro passageiro : passageiros) {
            if (passageiro.posicao().equals(nave.posicao())) {
                return passageiro;
            }
        }
        return null;
    }

    /** Falso quando a nave está cheia: o passageiro continua no mapa. */
    public boolean embarcar(Passageiro passageiro) {
        if (!nave.embarcar(passageiro)) {
            return false;
        }
        passageiros.remove(passageiro);
        return true;
    }

    /** Na base, quem está a bordo desce e conta como entregue. Devolve quantos desceram. */
    public int desembarcarNaBase() {
        if (!nave.posicao().equals(base.posicao())) {
            return 0;
        }
        int desceram = nave.desembarcar();
        entregues += desceram;
        return desceram;
    }

    public boolean naveColidiu() {
        Posicao aqui = nave.posicao();
        return asteroides.stream().anyMatch(a -> a.posicao().equals(aqui))
                || inimigos.stream().anyMatch(i -> i.posicao().equals(aqui));
    }

    public boolean todosEntregues() {
        return entregues == total;
    }

    public int tamanho() {
        return tamanho;
    }

    public Nave nave() {
        return nave;
    }

    /** Passageiros que ainda estão no mapa. */
    public List<Passageiro> passageiros() {
        return Collections.unmodifiableList(passageiros);
    }

    public int total() {
        return total;
    }

    public int entregues() {
        return entregues;
    }

    public int restantes() {
        return passageiros.size();
    }
}
