package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

/** Nome do piloto e dificuldade. Enter decola, Esc volta ao menu. */
public class NovaPartidaTela extends TelaBase {

    /** Mapa fixo de -5 a 5 (11x11), como pede a fase 3. */
    static final int TAMANHO_MAPA = 5;
    private static final int MAXIMO_NOME = 20;
    private static final String NOME_PADRAO = "Piloto Anônimo";

    private final StringBuilder nome = new StringBuilder();
    private Dificuldade dificuldade = Dificuldade.MEDIO;

    public NovaPartidaTela(MissaoMarteGame jogo) {
        super(jogo);
    }

    @Override
    protected void desenhar(float delta) {
        batch().begin();
        texto("NOVA MISSÃO", 330, 500, Color.ORANGE);
        texto("Nome do piloto:", 200, 420, Color.LIGHT_GRAY);
        texto(nome + "_", 340, 420, Color.WHITE);
        texto("Dificuldade:", 200, 370, Color.LIGHT_GRAY);
        texto("< " + dificuldade + " >", 340, 370, Color.WHITE);
        texto("Digite o nome (Backspace apaga). Setas mudam a dificuldade.", 200, 280, Color.GRAY);
        texto("Enter decola     Esc volta ao menu", 200, 250, Color.GRAY);
        batch().end();
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        switch (tecla) {
            case Keys.BACKSPACE -> {
                if (nome.length() > 0) {
                    nome.deleteCharAt(nome.length() - 1);
                }
            }
            case Keys.LEFT, Keys.UP -> dificuldade = vizinha(-1);
            case Keys.RIGHT, Keys.DOWN -> dificuldade = vizinha(1);
            case Keys.ENTER, Keys.NUMPAD_ENTER -> decolar();
            case Keys.ESCAPE -> jogo.trocarTela(new MenuTela(jogo));
            default -> {
                return false;
            }
        }
        return true;
    }

    /** Letras vêm aqui, já com acento e maiúscula; teclas de controle ficam no teclaApertada. */
    @Override
    protected boolean caractereDigitado(char caractere) {
        if (Character.isISOControl(caractere) || nome.length() >= MAXIMO_NOME) {
            return false;
        }
        if (Character.isLetterOrDigit(caractere) || caractere == ' ' || caractere == '.') {
            nome.append(caractere);
            return true;
        }
        return false;
    }

    private Dificuldade vizinha(int passo) {
        Dificuldade[] todas = Dificuldade.values();
        return todas[(dificuldade.ordinal() + passo + todas.length) % todas.length];
    }

    private void decolar() {
        String piloto = nome.toString().trim();
        if (piloto.isEmpty()) {
            piloto = NOME_PADRAO;
        }
        Missao missao = jogo.fabrica().criar(dificuldade, TAMANHO_MAPA);
        GameSession sessao = new GameSession(piloto, dificuldade, missao, jogo.random(), jogo.clock());
        jogo.trocarTela(new PartidaTela(jogo, sessao, missao));
    }
}
