package br.edu.unifor.missaomarte.servidor.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String nome;

    // Nome em minúsculas com índice único: "Ana" e "ana" não convivem nem em cadastros simultâneos.
    @Column(nullable = false, unique = true, length = 20)
    private String nomeNormalizado;

    @Column(nullable = false, length = 60)
    private String hashSenha;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    protected Usuario() {
    }

    public Usuario(String nome, String nomeNormalizado, String hashSenha, LocalDateTime criadoEm) {
        this.nome = nome;
        this.nomeNormalizado = nomeNormalizado;
        this.hashSenha = hashSenha;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getHashSenha() {
        return hashSenha;
    }
}
