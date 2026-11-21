package br.edu.unifor.missaomarte.application;

import java.util.Collections;
import java.util.List;

/** O que aconteceu num turno, na ordem em que aconteceu. */
public record ResultadoTurno(List<String> mensagens, List<EventoJogo> eventos) {

    /** O construtor da fase 3 continua valendo: turno sem eventos (sem as fábricas de lista do Java 9, ausentes no Android 8). */
    public ResultadoTurno(List<String> mensagens) {
        this(mensagens, Collections.emptyList());
    }
}
