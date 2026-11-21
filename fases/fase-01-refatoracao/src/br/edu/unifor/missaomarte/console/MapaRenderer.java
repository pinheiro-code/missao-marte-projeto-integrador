package br.edu.unifor.missaomarte.console;

import br.edu.unifor.missaomarte.domain.ElementoMapa;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Desenha o mapa e a legenda a partir dos elementos da missão. */
public class MapaRenderer {

    private static final char VAZIO = '.';
    private static final String FIM_DE_LINHA = System.lineSeparator();

    public String desenhar(Missao missao) {
        int tamanho = missao.tamanho();
        List<ElementoMapa> elementos = missao.elementos();
        StringBuilder texto = new StringBuilder("    ");
        for (int x = -tamanho; x <= tamanho; x++) {
            texto.append(String.format(" %2d", x));
        }
        texto.append(FIM_DE_LINHA).append("    ");
        texto.append(" __".repeat(2 * tamanho + 1));
        texto.append(FIM_DE_LINHA);
        for (int y = -tamanho; y <= tamanho; y++) {
            texto.append(String.format("%3d|", y));
            for (int x = -tamanho; x <= tamanho; x++) {
                texto.append(String.format(" %2c", simboloEm(elementos, new Posicao(x, y))));
            }
            texto.append(FIM_DE_LINHA);
        }
        texto.append("Legenda: ").append(legenda(elementos));
        return texto.toString();
    }

    /** O primeiro elemento da lista ganha a célula (a nave vem antes de todos). */
    private char simboloEm(List<ElementoMapa> elementos, Posicao posicao) {
        for (ElementoMapa elemento : elementos) {
            if (elemento.posicao().equals(posicao)) {
                return elemento.simbolo();
            }
        }
        return VAZIO;
    }

    private String legenda(List<ElementoMapa> elementos) {
        Map<Character, String> itens = new LinkedHashMap<>();
        for (ElementoMapa elemento : elementos) {
            itens.putIfAbsent(elemento.simbolo(), elemento.descricao());
        }
        itens.put(VAZIO, "Vazio");
        StringBuilder texto = new StringBuilder();
        for (Map.Entry<Character, String> item : itens.entrySet()) {
            if (texto.length() > 0) {
                texto.append(", ");
            }
            texto.append(item.getKey()).append('=').append(item.getValue());
        }
        return texto.toString();
    }
}
