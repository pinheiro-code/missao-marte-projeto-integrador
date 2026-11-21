package br.edu.unifor.missaomarte.servidor.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.edu.unifor.missaomarte.servidor.dto.PartidaRequest;
import br.edu.unifor.missaomarte.servidor.dto.PartidaResponse;
import br.edu.unifor.missaomarte.servidor.dto.RankingResponse;
import br.edu.unifor.missaomarte.servidor.model.Partida;
import br.edu.unifor.missaomarte.servidor.model.Usuario;
import br.edu.unifor.missaomarte.servidor.repository.PartidaRepository;
import br.edu.unifor.missaomarte.servidor.repository.UsuarioRepository;

@Service
public class PartidaService {

    private static final int TAMANHO_DO_RANKING = 10;

    private final PartidaRepository partidas;
    private final UsuarioRepository usuarios;
    private final Clock relogio;

    public PartidaService(PartidaRepository partidas, UsuarioRepository usuarios, Clock relogio) {
        this.partidas = partidas;
        this.usuarios = usuarios;
        this.relogio = relogio;
    }

    public PartidaResponse registrar(Long usuarioId, PartidaRequest pedido) {
        // Token válido de um usuário que não existe mais (banco apagado com o mesmo segredo).
        Usuario dono = usuarios.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário do token não existe."));
        LocalDateTime agora = LocalDateTime.now(relogio).truncatedTo(ChronoUnit.SECONDS);
        Partida partida = partidas.save(new Partida(dono, pedido.dificuldade(), pedido.resultado(),
                pedido.pontuacao(), pedido.duracaoSegundos(), pedido.movimentos(), agora));
        return paraResposta(partida);
    }

    public List<PartidaResponse> historico(Long usuarioId, int pagina, int tamanho) {
        List<PartidaResponse> resposta = new ArrayList<>();
        for (Partida partida : partidas.findByUsuarioIdOrderByDataHoraDescIdDesc(usuarioId,
                PageRequest.of(pagina, tamanho))) {
            resposta.add(paraResposta(partida));
        }
        return resposta;
    }

    // A consulta já vem na ordem do ranking: a primeira vitória de cada jogador é a melhor dele.
    // Limitação: carrega todas as vitórias positivas, o que serve para o tamanho deste trabalho.
    public List<RankingResponse> ranking() {
        List<RankingResponse> linhas = new ArrayList<>();
        Set<Long> jogadoresNaLista = new HashSet<>();
        for (Partida partida : partidas.vitoriasNaOrdemDoRanking()) {
            if (linhas.size() == TAMANHO_DO_RANKING) {
                break;
            }
            Usuario jogador = partida.getUsuario();
            if (jogadoresNaLista.add(jogador.getId())) {
                linhas.add(new RankingResponse(linhas.size() + 1, jogador.getNome(), partida.getPontuacao(),
                        partida.getDificuldade(), partida.getDuracaoSegundos(), partida.getMovimentos(),
                        partida.getDataHora()));
            }
        }
        return linhas;
    }

    private static PartidaResponse paraResposta(Partida partida) {
        return new PartidaResponse(partida.getId(), partida.getDificuldade(), partida.getResultado(),
                partida.getPontuacao(), partida.getDuracaoSegundos(), partida.getMovimentos(),
                partida.getDataHora());
    }
}
