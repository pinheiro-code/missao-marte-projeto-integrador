package br.edu.unifor.missaomarte.application.servidor;

import java.util.Locale;
import java.util.Objects;

/**
 * Endereço, porta e HTTPS do servidor, validados na criação: um objeto que existe é válido.
 * O endereço é um IPv4 (4 números de 0 a 255) ou um nome de máquina (letras, números e hífen,
 * separados por ponto). Texto com esquema, espaço, barra ou @ é recusado, não interpretado.
 * Imutável; sem libGDX e sem as APIs do Java 11 (roda no Android 8).
 */
public final class ConfiguracaoDoServidor {

    public static final int PORTA_MAXIMA = 65535;
    static final int TAMANHO_MAXIMO = 253;

    private final String endereco;
    private final int porta;
    private final boolean https;

    private ConfiguracaoDoServidor(String endereco, int porta, boolean https) {
        this.endereco = endereco;
        this.porta = porta;
        this.https = https;
    }

    /** Lança IllegalArgumentException com a mensagem pronta para a tela. */
    public static ConfiguracaoDoServidor criar(String endereco, int porta, boolean https) {
        String normalizado = endereco == null ? "" : endereco.trim().toLowerCase(Locale.ROOT);
        validarEndereco(normalizado);
        if (porta < 1 || porta > PORTA_MAXIMA) {
            throw new IllegalArgumentException("A porta deve ser de 1 a " + PORTA_MAXIMA + ".");
        }
        return new ConfiguracaoDoServidor(normalizado, porta, https);
    }

    /**
     * Lê o padrão do lançador: http://endereco[:porta] ou https://endereco[:porta], com ou sem a barra
     * final. Sem porta, vale 80 no HTTP e 443 no HTTPS. Caminho, @ ou espaço são recusados.
     */
    public static ConfiguracaoDoServidor deUrl(String url) {
        String texto = url == null ? "" : url.trim();
        String minusculo = texto.toLowerCase(Locale.ROOT);
        boolean https;
        if (minusculo.startsWith("https://")) {
            https = true;
        } else if (minusculo.startsWith("http://")) {
            https = false;
        } else {
            throw new IllegalArgumentException("O endereço deve começar com http:// ou https://.");
        }
        String resto = texto.substring(https ? 8 : 7);
        if (resto.endsWith("/")) {
            resto = resto.substring(0, resto.length() - 1);
        }
        if (resto.indexOf(' ') >= 0) {
            // o criar() tira os espaços das pontas: aqui um espaço no meio da URL é erro, não ajuste
            throw new IllegalArgumentException("O endereço não pode ter espaço, barra nem @.");
        }
        int doisPontos = resto.lastIndexOf(':');
        if (doisPontos < 0) {
            return criar(resto, https ? 443 : 80, https);
        }
        String porta = resto.substring(doisPontos + 1);
        if (porta.length() > 5 || !soDigitos(porta)) {
            throw new IllegalArgumentException("Porta inválida: " + porta);
        }
        return criar(resto.substring(0, doisPontos), Integer.parseInt(porta), https);
    }

    private static void validarEndereco(String endereco) {
        if (endereco.isEmpty()) {
            throw new IllegalArgumentException("Digite o endereço do servidor.");
        }
        if (endereco.contains("://")) {
            throw new IllegalArgumentException("Digite só o endereço, sem http:// ou https://.");
        }
        if (endereco.contains(" ") || endereco.contains("/") || endereco.contains("@")) {
            throw new IllegalArgumentException("O endereço não pode ter espaço, barra nem @.");
        }
        if (endereco.length() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("O endereço é longo demais.");
        }
        String[] rotulos = endereco.split("\\.", -1);
        boolean todosNumericos = true;
        for (String rotulo : rotulos) {
            if (!rotuloValido(rotulo)) {
                throw new IllegalArgumentException("Endereço inválido: use letras, números, hífen e ponto.");
            }
            todosNumericos &= soDigitos(rotulo);
        }
        if (todosNumericos) {
            validarIpv4(rotulos);
        }
    }

    /** Um pedaço entre pontos: não vazio, até 63 caracteres, sem hífen nas pontas. */
    private static boolean rotuloValido(String rotulo) {
        if (rotulo.isEmpty() || rotulo.length() > 63 || rotulo.startsWith("-") || rotulo.endsWith("-")) {
            return false;
        }
        for (int i = 0; i < rotulo.length(); i++) {
            char c = rotulo.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-')) {
                return false;
            }
        }
        return true;
    }

    private static void validarIpv4(String[] octetos) {
        if (octetos.length != 4) {
            throw new IllegalArgumentException("IP inválido: use 4 números de 0 a 255 separados por ponto.");
        }
        for (String octeto : octetos) {
            if (octeto.length() > 3 || Integer.parseInt(octeto) > 255) {
                throw new IllegalArgumentException("IP inválido: cada número vai de 0 a 255.");
            }
        }
    }

    private static boolean soDigitos(String texto) {
        if (texto.isEmpty()) {
            return false;
        }
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) < '0' || texto.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    /** A base das rotas, sem barra no fim: http://192.168.0.10:8080. */
    public String url() {
        return (https ? "https" : "http") + "://" + endereco + ":" + porta;
    }

    /** localhost, 127.x, 10.x, 172.16 a 172.31 e 192.168.x: a própria máquina ou a rede de casa. */
    public boolean redeLocal() {
        if (endereco.equals("localhost")) {
            return true;
        }
        String[] partes = endereco.split("\\.");
        if (partes.length != 4 || !soDigitos(partes[0]) || !soDigitos(partes[1]) || !soDigitos(partes[3])) {
            return false;
        }
        int a = Integer.parseInt(partes[0]);
        int b = Integer.parseInt(partes[1]);
        return a == 127 || a == 10 || (a == 172 && b >= 16 && b <= 31) || (a == 192 && b == 168);
    }

    /** HTTP fora da rede local: a senha e o token iriam sem criptografia por uma rede desconhecida. */
    public boolean exigeAviso() {
        return !https && !redeLocal();
    }

    public String endereco() {
        return endereco;
    }

    public int porta() {
        return porta;
    }

    public boolean https() {
        return https;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof ConfiguracaoDoServidor)) {
            return false;
        }
        ConfiguracaoDoServidor c = (ConfiguracaoDoServidor) outro;
        return porta == c.porta && https == c.https && endereco.equals(c.endereco);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endereco, porta, https);
    }

    @Override
    public String toString() {
        return url();
    }
}
