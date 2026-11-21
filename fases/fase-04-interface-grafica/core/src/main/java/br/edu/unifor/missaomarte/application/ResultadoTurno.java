package br.edu.unifor.missaomarte.application;

import java.util.List;

/** O que aconteceu num turno, na ordem em que aconteceu. */
public record ResultadoTurno(List<String> mensagens, List<EventoJogo> eventos) {

    /** O construtor da fase 3 continua valendo: turno sem eventos. */
    public ResultadoTurno(List<String> mensagens) {
        this(mensagens, List.of());
    }
}
