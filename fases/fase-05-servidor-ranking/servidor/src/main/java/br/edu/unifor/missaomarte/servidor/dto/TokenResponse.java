package br.edu.unifor.missaomarte.servidor.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record TokenResponse(
        String token,
        String tipo,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime expiraEm) {

    @Override
    public String toString() {
        return "TokenResponse[token=***, tipo=" + tipo + ", expiraEm=" + expiraEm + "]";
    }
}
