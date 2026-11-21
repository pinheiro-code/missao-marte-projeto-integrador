package br.edu.unifor.missaomarte.servidor.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Dificuldade dificuldade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Resultado resultado;

    @Column(nullable = false)
    private int pontuacao;

    @Column(nullable = false)
    private long duracaoSegundos;

    @Column(nullable = false)
    private int movimentos;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected Partida() {
    }

    public Partida(Usuario usuario, Dificuldade dificuldade, Resultado resultado, int pontuacao,
            long duracaoSegundos, int movimentos, LocalDateTime dataHora) {
        this.usuario = usuario;
        this.dificuldade = dificuldade;
        this.resultado = resultado;
        this.pontuacao = pontuacao;
        this.duracaoSegundos = duracaoSegundos;
        this.movimentos = movimentos;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Dificuldade getDificuldade() {
        return dificuldade;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public long getDuracaoSegundos() {
        return duracaoSegundos;
    }

    public int getMovimentos() {
        return movimentos;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
