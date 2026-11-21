package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.ArmazemDeTexto;
import com.badlogic.gdx.Preferences;

/**
 * Adaptador da porta ArmazemDeTexto sobre as Preferences do libGDX ("missaomarte": no desktop,
 * ~/.prefs/missaomarte; no Android, as SharedPreferences do app). Criado no create(), quando as
 * Preferences já existem. Cada escrita vai para o disco na hora (flush).
 */
public class PreferenciasDoJogo implements ArmazemDeTexto {

    public static final String NOME = "missaomarte";

    private final Preferences preferencias;

    public PreferenciasDoJogo(Preferences preferencias) {
        this.preferencias = preferencias;
    }

    @Override
    public String ler(String chave) {
        return preferencias.contains(chave) ? preferencias.getString(chave) : null;
    }

    @Override
    public void gravar(String chave, String valor) {
        preferencias.putString(chave, valor);
        preferencias.flush();
    }

    @Override
    public void remover(String chave) {
        preferencias.remove(chave);
        preferencias.flush();
    }
}
