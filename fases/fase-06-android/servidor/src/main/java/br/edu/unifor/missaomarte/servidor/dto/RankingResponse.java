package br.edu.unifor.missaomarte.servidor.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.edu.unifor.missaomarte.servidor.model.Dificuldade;

public record RankingResponse(
        int posicao,
        String nome,
        int pontuacao,
        Dificuldade dificuldade,
        long duracaoSegundos,
        int movimentos,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataHora) {
}
