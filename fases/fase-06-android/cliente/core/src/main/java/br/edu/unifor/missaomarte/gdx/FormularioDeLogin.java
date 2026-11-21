package br.edu.unifor.missaomarte.gdx;

import java.nio.charset.StandardCharsets;

/**
 * O formulário de entrar ou criar conta, sem libGDX: campos, caracteres aceitos, limites,
 * máscara da senha e a mesma validação do servidor. Só o servidor é confiável; isto evita a viagem.
 */
public class FormularioDeLogin {

    public enum Campo { NOME, SENHA }

    static final int MINIMO_NOME = 3;
    static final int MAXIMO_NOME = 20;
    static final int MINIMO_SENHA = 8;
    static final int MAXIMO_SENHA_BYTES = 72;

    private final StringBuilder nome = new StringBuilder();
    private final StringBuilder senha = new StringBuilder();
    private Campo campo = Campo.NOME;
    private boolean criarConta;

    /** Devolve falso quando o caractere não é aceito no campo atual ou passaria do limite. */
    public boolean digitar(char caractere) {
        if (campo == Campo.NOME) {
            if (!caractereDoNome(caractere) || nome.length() >= MAXIMO_NOME) {
                return false;
            }
            nome.append(caractere);
            return true;
        }
        if (Character.isISOControl(caractere) || bytes(senha + String.valueOf(caractere)) > MAXIMO_SENHA_BYTES) {
            return false;
        }
        senha.append(caractere);
        return true;
    }

    private static boolean caractereDoNome(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '-';
    }

    private static int bytes(String texto) {
        return texto.getBytes(StandardCharsets.UTF_8).length;
    }

    public void apagar() {
        StringBuilder atual = campo == Campo.NOME ? nome : senha;
        if (atual.length() > 0) {
            atual.deleteCharAt(atual.length() - 1);
        }
    }

    public void trocarCampo() {
        campo = campo == Campo.NOME ? Campo.SENHA : Campo.NOME;
    }

    /** O toque num campo só o seleciona; a digitação continua pelo digitar ou pelo definirTexto. */
    public void selecionarCampo(Campo alvo) {
        campo = alvo;
    }

    /**
     * O texto da caixa nativa do celular: seleciona o campo, limpa e aplica as mesmas regras do
     * digitar a cada caractere (o que não é aceito ou passa do limite é descartado).
     */
    public void definirTexto(Campo alvo, String texto) {
        campo = alvo;
        (alvo == Campo.NOME ? nome : senha).setLength(0);
        if (texto == null) {
            return;
        }
        for (int i = 0; i < texto.length(); i++) {
            digitar(texto.charAt(i));
        }
    }

    public void alternarModo() {
        criarConta = !criarConta;
    }

    /**
     * null quando pode enviar; senão a mensagem para a tela. Como no servidor: entrar só exige os dois
     * campos (o resto vira o 401 genérico); criar conta exige nome de 3 a 20 e senha de 8 ou mais.
     */
    public String validar() {
        if (nome.length() == 0) {
            return "Digite o nome.";
        }
        if (senha.length() == 0) {
            return "Digite a senha.";
        }
        if (!criarConta) {
            return null;
        }
        if (nome.length() < MINIMO_NOME) {
            return "O nome deve ter de " + MINIMO_NOME + " a " + MAXIMO_NOME + " caracteres.";
        }
        if (senha.length() < MINIMO_SENHA) {
            return "A senha deve ter pelo menos " + MINIMO_SENHA + " caracteres.";
        }
        return null;
    }

    public String senhaMascarada() {
        StringBuilder mascara = new StringBuilder();
        for (int i = 0; i < senha.length(); i++) {
            mascara.append('*');
        }
        return mascara.toString();
    }

    public String nome() {
        return nome.toString();
    }

    public String senha() {
        return senha.toString();
    }

    public Campo campo() {
        return campo;
    }

    public boolean criarConta() {
        return criarConta;
    }

    @Override
    public String toString() {
        return "FormularioDeLogin[nome=" + nome + ", criarConta=" + criarConta + "]";
    }
}
