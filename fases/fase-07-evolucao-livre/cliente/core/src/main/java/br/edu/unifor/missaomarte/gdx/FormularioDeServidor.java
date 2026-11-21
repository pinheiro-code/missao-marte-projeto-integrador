package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoDoServidor;

/**
 * O formulário da configuração do servidor, sem libGDX: endereço, porta e HTTPS, os caracteres
 * aceitos em cada campo e os limites. Quem decide se o endereço vale é o ConfiguracaoDoServidor.
 */
public class FormularioDeServidor {

    public enum Campo { ENDERECO, PORTA }

    static final int MAXIMO_ENDERECO = 253;
    static final int MAXIMO_PORTA = 5;

    /** O resultado do montar(): a configuração ou a mensagem de validação, nunca os dois. */
    public static final class Montagem {

        private final ConfiguracaoDoServidor configuracao;
        private final String erro;

        private Montagem(ConfiguracaoDoServidor configuracao, String erro) {
            this.configuracao = configuracao;
            this.erro = erro;
        }

        public boolean ok() {
            return configuracao != null;
        }

        public ConfiguracaoDoServidor configuracao() {
            return configuracao;
        }

        public String erro() {
            return erro;
        }
    }

    private final StringBuilder endereco = new StringBuilder();
    private final StringBuilder porta = new StringBuilder();
    private boolean https;
    private Campo campo = Campo.ENDERECO;

    /** Preenche com a configuração em uso, ao abrir a tela. */
    public void carregar(ConfiguracaoDoServidor configuracao) {
        endereco.setLength(0);
        endereco.append(configuracao.endereco());
        porta.setLength(0);
        porta.append(configuracao.porta());
        https = configuracao.https();
    }

    /** Endereço: letras, números, ponto e hífen. Porta: só dígitos. Devolve falso se recusou. */
    public boolean digitar(char caractere) {
        if (campo == Campo.ENDERECO) {
            if (!caractereDoEndereco(caractere) || endereco.length() >= MAXIMO_ENDERECO) {
                return false;
            }
            endereco.append(caractere);
            return true;
        }
        if (caractere < '0' || caractere > '9' || porta.length() >= MAXIMO_PORTA) {
            return false;
        }
        porta.append(caractere);
        return true;
    }

    private static boolean caractereDoEndereco(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '-';
    }

    /**
     * O texto da caixa nativa substitui o campo. Caractere fora da regra recusa o texto inteiro (o campo
     * fica como estava): filtrar faria de "servidor.com:8080" um "servidor.com8080" aceito sem aviso.
     * Devolve null quando aplicou, ou a mensagem para a tela.
     */
    public String definirTexto(Campo alvo, String texto) {
        campo = alvo;
        String limpo = texto == null ? "" : texto.trim();
        String erro = alvo == Campo.ENDERECO ? erroDoEndereco(limpo) : erroDaPorta(limpo);
        if (erro != null) {
            return erro;
        }
        StringBuilder atual = alvo == Campo.ENDERECO ? endereco : porta;
        atual.setLength(0);
        atual.append(limpo);
        return null;
    }

    private static String erroDoEndereco(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            if (!caractereDoEndereco(texto.charAt(i))) {
                try {
                    ConfiguracaoDoServidor.criar(texto, 1, false);
                } catch (IllegalArgumentException e) {
                    return e.getMessage();
                }
                return "Endereço inválido: use letras, números, hífen e ponto.";
            }
        }
        return texto.length() > MAXIMO_ENDERECO ? "O endereço é longo demais." : null;
    }

    private static String erroDaPorta(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) < '0' || texto.charAt(i) > '9') {
                return "A porta só aceita números.";
            }
        }
        return texto.length() > MAXIMO_PORTA ? "A porta deve ser de 1 a " + ConfiguracaoDoServidor.PORTA_MAXIMA + "." : null;
    }

    public void apagar() {
        StringBuilder atual = campo == Campo.ENDERECO ? endereco : porta;
        if (atual.length() > 0) {
            atual.deleteCharAt(atual.length() - 1);
        }
    }

    public void trocarCampo() {
        campo = campo == Campo.ENDERECO ? Campo.PORTA : Campo.ENDERECO;
    }

    public void selecionarCampo(Campo alvo) {
        campo = alvo;
    }

    public void alternarHttps() {
        https = !https;
    }

    public Montagem montar() {
        if (porta.length() == 0) {
            return new Montagem(null, "Digite a porta.");
        }
        try {
            return new Montagem(ConfiguracaoDoServidor.criar(endereco.toString(), Integer.parseInt(porta.toString()),
                    https), null);
        } catch (IllegalArgumentException e) {
            return new Montagem(null, e.getMessage());
        }
    }

    public String endereco() {
        return endereco.toString();
    }

    public String porta() {
        return porta.toString();
    }

    public boolean https() {
        return https;
    }

    public Campo campo() {
        return campo;
    }
}
