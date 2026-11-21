package br.edu.unifor.missaomarte.servidor.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.edu.unifor.missaomarte.servidor.model.Dificuldade;
import br.edu.unifor.missaomarte.servidor.model.Resultado;

public record PartidaResponse(
        Long id,
        Dificuldade dificuldade,
        Resultado resultado,
        int pontuacao,
        long duracaoSegundos,
        int movimentos,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataHora) {
}
