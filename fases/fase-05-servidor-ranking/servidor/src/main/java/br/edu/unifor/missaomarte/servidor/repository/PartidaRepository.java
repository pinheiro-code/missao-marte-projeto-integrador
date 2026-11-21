package br.edu.unifor.missaomarte.servidor.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.edu.unifor.missaomarte.servidor.model.Partida;

public interface PartidaRepository extends JpaRepository<Partida, Long> {

    List<Partida> findByUsuarioIdOrderByDataHoraDescIdDesc(Long usuarioId, Pageable pagina);

    // Vitórias com pontuação positiva já na ordem do ranking; o serviço pega a primeira de cada jogador.
    // O id no fim desempata duas vitórias iguais do mesmo jogador.
    @Query("""
            select p from Partida p join fetch p.usuario
            where p.resultado = br.edu.unifor.missaomarte.servidor.model.Resultado.VITORIA
              and p.pontuacao > 0
            order by p.pontuacao desc, p.duracaoSegundos asc, p.dataHora asc, p.id asc
            """)
    List<Partida> vitoriasNaOrdemDoRanking();
}
