package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.OnscreenKeyboardType;
import com.badlogic.gdx.Input.TextInputListener;
import java.util.function.Consumer;

/**
 * O que muda entre desktop e celular, decidido pelo launcher: o modo toque (botões na tela) e a caixa
 * de texto nativa. No desktop a caixa fica desligada (o getTextInput do lwjgl3 1.12.1 só chama
 * canceled()): o toque num campo só o seleciona e o teclado físico digita.
 */
public class Plataforma {

    private final boolean modoToque;
    private final boolean caixaDeTextoNativa;

    public Plataforma(boolean modoToque, boolean caixaDeTextoNativa) {
        this.modoToque = modoToque;
        this.caixaDeTextoNativa = caixaDeTextoNativa;
    }

    /** Desktop comum: teclado, sem botões, igual à fase 5. */
    public static Plataforma desktop() {
        return new Plataforma(false, false);
    }

    public boolean modoToque() {
        return modoToque;
    }

    public boolean caixaDeTextoNativa() {
        return caixaDeTextoNativa;
    }

    /**
     * Abre a caixa de texto do sistema. A senha usa o teclado de senha e nunca recebe o valor atual.
     * A resposta chega em outra thread: volta para a do jogo pelo postRunnable antes de mexer na tela.
     * Cancelar não muda nada.
     */
    public void pedirTexto(String titulo, String textoAtual, boolean ehSenha, Consumer<String> aoResponder) {
        if (!caixaDeTextoNativa) {
            return;
        }
        TextInputListener ouvinte = new TextInputListener() {
            @Override
            public void input(String texto) {
                Gdx.app.postRunnable(() -> aoResponder.accept(texto));
            }

            @Override
            public void canceled() {
            }
        };
        Gdx.input.getTextInput(ouvinte, titulo, ehSenha ? "" : textoAtual, "",
                ehSenha ? OnscreenKeyboardType.Password : OnscreenKeyboardType.Default);
    }
}
