package br.edu.unifor.missaomarte.servidor.dto;

import br.edu.unifor.missaomarte.servidor.seguranca.Ate72Bytes;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CadastroRequest(
        @NotNull(message = "Informe o nome.")
        @Pattern(regexp = "[A-Za-z0-9_-]{3,20}", message = "O nome deve ter de 3 a 20 caracteres: letras, números, _ ou -.")
        String nome,
        @NotNull(message = "Informe a senha.")
        @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres.")
        @Ate72Bytes
        String senha) {

    @Override
    public String toString() {
        return "CadastroRequest[nome=" + nome + ", senha=***]";
    }
}
