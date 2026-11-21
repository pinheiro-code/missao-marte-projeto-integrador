package br.edu.unifor.missaomarte.gdx;

/**
 * O nome do piloto na Nova partida, sem libGDX: letras (com acento), números, espaço e ponto,
 * até 20 caracteres. A mesma regra vale para a tecla e para o texto da caixa nativa do celular.
 */
public class NomeDoPiloto {

    static final int MAXIMO = 20;

    private final StringBuilder texto = new StringBuilder();

    /** Devolve falso quando o caractere não é aceito ou passaria do limite. */
    public boolean digitar(char caractere) {
        if (Character.isISOControl(caractere) || texto.length() >= MAXIMO) {
            return false;
        }
        if (Character.isLetterOrDigit(caractere) || caractere == ' ' || caractere == '.') {
            texto.append(caractere);
            return true;
        }
        return false;
    }

    public void apagar() {
        if (texto.length() > 0) {
            texto.deleteCharAt(texto.length() - 1);
        }
    }

    /** Troca o nome inteiro, descartando o que a regra não aceita. */
    public void definirTexto(String novo) {
        texto.setLength(0);
        if (novo == null) {
            return;
        }
        for (int i = 0; i < novo.length(); i++) {
            digitar(novo.charAt(i));
        }
    }

    public String texto() {
        return texto.toString();
    }
}
