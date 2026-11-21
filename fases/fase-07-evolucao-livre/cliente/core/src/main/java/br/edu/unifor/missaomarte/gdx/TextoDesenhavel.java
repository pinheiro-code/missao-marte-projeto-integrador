package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;

/**
 * Tira do texto os caracteres que a fonte não tem, antes de desenhar. Sem isso o libGDX troca cada um pelo
 * glifo de falta da fonte padrão (o quadrado), como no "✨ ALERTA" que vem da GameSession.
 * Não dá para usar hasGlyph: a fonte padrão tem glifo de falta, então ele responde sim para tudo.
 */
final class TextoDesenhavel {

    private TextoDesenhavel() {
    }

    static String de(String texto, BitmapFontData fonte) {
        StringBuilder saida = new StringBuilder(texto.length());
        boolean acabouDeTirar = false;
        for (int i = 0; i < texto.length(); ) {
            int c = texto.codePointAt(i);
            i += Character.charCount(c);
            if (!temGlifo(c, fonte)) {
                acabouDeTirar = true;
                continue;
            }
            // o espaço que separava o caractere tirado não pode virar espaço duplo nem espaço no começo
            boolean espacoSobrando = c == ' ' && acabouDeTirar
                    && (saida.length() == 0 || saida.charAt(saida.length() - 1) == ' ');
            if (!espacoSobrando) {
                saida.appendCodePoint(c);
            }
            acabouDeTirar = false;
        }
        if (acabouDeTirar && saida.length() > 0 && saida.charAt(saida.length() - 1) == ' ') {
            saida.setLength(saida.length() - 1);
        }
        return saida.toString();
    }

    private static boolean temGlifo(int c, BitmapFontData fonte) {
        return c == '\n' || (Character.isBmpCodePoint(c) && fonte.getGlyph((char) c) != null);
    }
}
