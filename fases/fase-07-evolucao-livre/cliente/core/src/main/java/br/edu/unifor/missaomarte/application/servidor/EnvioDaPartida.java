package br.edu.unifor.missaomarte.application.servidor;

/**
 * O envio do resultado no fim de uma partida logada. Começa enviando; a tela só desenha o estado.
 * Enquanto envia, o jogador não continua; depois de uma falha, pode reenviar.
 */
public class EnvioDaPartida {

    public enum Estado { ENVIANDO, ENVIADO, FALHOU }

    private final ServidorDoJogo servidor;
    private final Sessao sessao;
    private final PartidaConcluida partida;
    private final Runnable aoExpirarSessao;
    private Estado estado;
    private String erro;

    public EnvioDaPartida(ServidorDoJogo servidor, Sessao sessao, PartidaConcluida partida, Runnable aoExpirarSessao) {
        this.servidor = servidor;
        this.sessao = sessao;
        this.partida = partida;
        this.aoExpirarSessao = aoExpirarSessao;
        enviar();
    }

    private void enviar() {
        estado = Estado.ENVIANDO;
        erro = null;
        servidor.enviarPartida(sessao, partida, this::receber);
    }

    private void receber(Resposta<PartidaRegistrada> resposta) {
        if (resposta.ok()) {
            estado = Estado.ENVIADO;
            return;
        }
        estado = Estado.FALHOU;
        erro = resposta.erro();
        if (resposta.naoAutorizado()) {
            aoExpirarSessao.run();
        }
    }

    /** Só depois de uma falha; devolve falso se não havia o que reenviar. */
    public boolean reenviar() {
        if (estado != Estado.FALHOU) {
            return false;
        }
        enviar();
        return true;
    }

    public boolean podeContinuar() {
        return estado != Estado.ENVIANDO;
    }

    public Estado estado() {
        return estado;
    }

    /** Motivo da última falha, ou null. */
    public String erro() {
        return erro;
    }
}
