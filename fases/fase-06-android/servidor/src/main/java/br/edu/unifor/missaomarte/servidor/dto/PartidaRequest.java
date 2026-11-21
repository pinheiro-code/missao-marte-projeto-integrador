package br.edu.unifor.missaomarte.servidor.dto;

import br.edu.unifor.missaomarte.servidor.model.Dificuldade;
import br.edu.unifor.missaomarte.servidor.model.Resultado;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Tipos que aceitam null: campo ausente vira 400, e não zero. Não há campo de usuário: o dono vem do token.
public record PartidaRequest(
        @NotNull(message = "Informe a dificuldade.")
        Dificuldade dificuldade,
        @NotNull(message = "Informe o resultado.")
        Resultado resultado,
        @NotNull(message = "Informe a pontuação.")
        @Min(value = -100000, message = "A pontuação deve estar entre -100000 e 100000.")
        @Max(value = 100000, message = "A pontuação deve estar entre -100000 e 100000.")
        Integer pontuacao,
        @NotNull(message = "Informe a duração.")
        @Min(value = 0, message = "A duração deve estar entre 0 e 86400 segundos.")
        @Max(value = 86400, message = "A duração deve estar entre 0 e 86400 segundos.")
        Long duracaoSegundos,
        @NotNull(message = "Informe os movimentos.")
        @Min(value = 0, message = "Os movimentos devem estar entre 0 e 100000.")
        @Max(value = 100000, message = "Os movimentos devem estar entre 0 e 100000.")
        Integer movimentos) {
}
