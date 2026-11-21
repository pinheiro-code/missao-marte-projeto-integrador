package br.edu.unifor.missaomarte.servidor.dto;

import br.edu.unifor.missaomarte.servidor.seguranca.Ate72Bytes;
import jakarta.validation.constraints.NotNull;

// Só presença e 72 bytes: nome ou senha fora do formato do cadastro caem no 401 genérico,
// para o login não contar qual regra o nome quebrou.
public record LoginRequest(
        @NotNull(message = "Informe o nome.")
        String nome,
        @NotNull(message = "Informe a senha.")
        @Ate72Bytes
        String senha) {

    @Override
    public String toString() {
        return "LoginRequest[nome=" + nome + ", senha=***]";
    }
}
