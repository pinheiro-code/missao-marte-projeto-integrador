package br.edu.unifor.missaomarte.application;

import java.util.List;

/** O que aconteceu num turno, na ordem em que aconteceu. */
public record ResultadoTurno(List<String> mensagens) {
}
