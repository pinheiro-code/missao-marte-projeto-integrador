package br.edu.unifor.missaomarte.application;

/** O que aconteceu num turno, para a interface reagir (som, efeito) sem ler o texto das mensagens. */
public enum EventoJogo {
    MOVIMENTO,
    EMBARQUE,
    ENTREGA,
    COLISAO,
    VITORIA,
    DERROTA
}
