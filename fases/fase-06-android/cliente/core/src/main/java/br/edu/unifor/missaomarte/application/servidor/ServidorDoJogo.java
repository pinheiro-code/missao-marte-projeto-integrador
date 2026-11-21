package br.edu.unifor.missaomarte.application.servidor;

import java.util.List;

/**
 * Porta do servidor do jogo. Toda operação é assíncrona: volta na hora e entrega o desfecho
 * depois, no Retorno, sem nunca lançar exceção por falha de rede.
 */
public interface ServidorDoJogo {

    /** Cria a conta e já entra com ela. */
    void cadastrar(String nome, String senha, Retorno<Sessao> retorno);

    void entrar(String nome, String senha, Retorno<Sessao> retorno);

    void enviarPartida(Sessao sessao, PartidaConcluida partida, Retorno<PartidaRegistrada> retorno);

    void carregarRanking(Sessao sessao, Retorno<List<LinhaDoRanking>> retorno);

    /** As 10 partidas mais recentes do jogador. */
    void carregarHistorico(Sessao sessao, Retorno<List<PartidaRegistrada>> retorno);

    /**
     * GET /api/saude num servidor que ainda não foi salvo; urlBase vem sempre de uma
     * ConfiguracaoDoServidor validada. O padrão responde erro, para quem não sabe testar (o falso dos
     * testes da fase 5) continuar valendo sem mudança; o ServidorHttp faz a chamada de verdade.
     */
    default void testarConexao(String urlBase, Retorno<Boolean> retorno) {
        retorno.receber(Resposta.erro("Este servidor não sabe testar a conexão."));
    }
}
