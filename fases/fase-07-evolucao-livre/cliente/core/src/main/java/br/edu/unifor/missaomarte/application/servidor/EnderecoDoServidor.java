package br.edu.unifor.missaomarte.application.servidor;

import java.util.function.Supplier;

/**
 * O servidor em uso. Precedência: o salvo pelo jogador, depois o padrão do lançador, depois
 * http://localhost:8080. O ServidorHttp lê a URL a cada chamada, então salvar vale na hora.
 * O armazenamento só é lido na primeira chamada (as Preferences só existem depois do create()),
 * e só guarda três chaves: endereço, porta e HTTPS. Valor salvo inválido é ignorado.
 */
public final class EnderecoDoServidor implements Supplier<String> {

    public static final String CHAVE_ENDERECO = "servidor.endereco";
    public static final String CHAVE_PORTA = "servidor.porta";
    public static final String CHAVE_HTTPS = "servidor.https";
    public static final String PADRAO_FIXO = "http://localhost:8080";

    private final ArmazemDeTexto armazem;
    private final ConfiguracaoDoServidor padrao;
    private final String avisoDoPadrao;
    private ConfiguracaoDoServidor atual;

    /** padraoDoLancador vazio ou null vale o PADRAO_FIXO; inválido também, com o aviso. */
    public EnderecoDoServidor(ArmazemDeTexto armazem, String padraoDoLancador) {
        this.armazem = armazem;
        ConfiguracaoDoServidor lido = null;
        String aviso = null;
        if (padraoDoLancador != null && !padraoDoLancador.trim().isEmpty()) {
            try {
                lido = ConfiguracaoDoServidor.deUrl(padraoDoLancador);
            } catch (IllegalArgumentException e) {
                aviso = "Padrão do servidor inválido (" + e.getMessage() + "): usando " + PADRAO_FIXO + ".";
            }
        }
        this.padrao = lido != null ? lido : ConfiguracaoDoServidor.deUrl(PADRAO_FIXO);
        this.avisoDoPadrao = aviso;
    }

    @Override
    public String get() {
        return atual().url();
    }

    public ConfiguracaoDoServidor atual() {
        if (atual == null) {
            atual = lerSalvo();
        }
        return atual;
    }

    private ConfiguracaoDoServidor lerSalvo() {
        String endereco = armazem.ler(CHAVE_ENDERECO);
        String porta = armazem.ler(CHAVE_PORTA);
        String https = armazem.ler(CHAVE_HTTPS);
        if (endereco == null || porta == null || !("true".equals(https) || "false".equals(https))) {
            return padrao;
        }
        try {
            return ConfiguracaoDoServidor.criar(endereco, Integer.parseInt(porta), Boolean.parseBoolean(https));
        } catch (IllegalArgumentException e) {
            // porta que não é número cai aqui também (NumberFormatException é IllegalArgumentException)
            return padrao;
        }
    }

    public ConfiguracaoDoServidor padrao() {
        return padrao;
    }

    /** Devolve verdadeiro quando o servidor mudou: quem chama encerra a sessão. */
    public boolean salvar(ConfiguracaoDoServidor nova) {
        boolean mudou = !nova.equals(atual());
        armazem.gravar(CHAVE_ENDERECO, nova.endereco());
        armazem.gravar(CHAVE_PORTA, String.valueOf(nova.porta()));
        armazem.gravar(CHAVE_HTTPS, String.valueOf(nova.https()));
        atual = nova;
        return mudou;
    }

    /** Apaga o salvo e volta ao padrão do lançador; verdadeiro quando o servidor mudou. */
    public boolean restaurarPadrao() {
        boolean mudou = !padrao.equals(atual());
        armazem.remover(CHAVE_ENDERECO);
        armazem.remover(CHAVE_PORTA);
        armazem.remover(CHAVE_HTTPS);
        atual = padrao;
        return mudou;
    }

    /** null quando o padrão do lançador era válido ou ausente; senão a mensagem para o log. */
    public String avisoDoPadrao() {
        return avisoDoPadrao;
    }
}
