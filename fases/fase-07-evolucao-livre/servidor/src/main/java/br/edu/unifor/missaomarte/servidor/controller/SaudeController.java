package br.edu.unifor.missaomarte.servidor.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Verificação de saúde: o jogo usa no "Testar conexão" e o provedor de nuvem, para saber se o serviço subiu.
@RestController
public class SaudeController {

    @GetMapping("/api/saude")
    public Map<String, String> saude() {
        return Map.of("status", "ok");
    }
}
