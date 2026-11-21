package br.edu.unifor.missaomarte.servidor.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.unifor.missaomarte.servidor.dto.RankingResponse;
import br.edu.unifor.missaomarte.servidor.service.PartidaService;

@RestController
public class RankingController {

    private final PartidaService partidas;

    public RankingController(PartidaService partidas) {
        this.partidas = partidas;
    }

    @GetMapping("/api/ranking")
    public List<RankingResponse> top10() {
        return partidas.ranking();
    }
}
