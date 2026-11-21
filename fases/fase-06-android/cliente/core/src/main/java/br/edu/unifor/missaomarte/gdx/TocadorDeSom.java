package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.audio.Music;

/**
 * Só toca sons e controla o mudo e a música. Não carrega nem libera arquivo (isso é do Recursos).
 * Com o áudio desativado, todo método vira nada.
 */
public class TocadorDeSom {

    private static final float VOLUME_EFEITO = 0.6f;
    private static final float VOLUME_MUSICA = 0.35f;

    private final Recursos recursos;
    private final boolean ativo;
    private boolean mudo;
    /** Pausada pelo fim da partida: desmutar não a religa até voltar ao menu. */
    private boolean musicaPausada;
    /** A música tocava quando o app foi para segundo plano. */
    private boolean tocavaAntesDaPausa;

    public TocadorDeSom(Recursos recursos, boolean ativo) {
        this.recursos = recursos;
        this.ativo = ativo;
    }

    public void tocar(Som som) {
        if (ativo && !mudo) {
            recursos.som(som).play(VOLUME_EFEITO);
        }
    }

    /** Liga a música em laço, ou retoma depois de uma pausa. */
    public void tocarMusica() {
        musicaPausada = false;
        if (!ativo) {
            return;
        }
        Music musica = recursos.musica();
        musica.setLooping(true);
        musica.setVolume(VOLUME_MUSICA);
        if (!mudo && !musica.isPlaying()) {
            musica.play();
        }
    }

    /** No fim da partida, para ouvir o som do resultado. */
    public void pausarMusica() {
        musicaPausada = true;
        if (ativo) {
            recursos.musica().pause();
        }
    }

    /** O app foi para segundo plano (pause do libGDX): pausa a música e lembra se ela tocava. */
    public void pausarPorSegundoPlano() {
        tocavaAntesDaPausa = ativo && recursos.musica().isPlaying();
        if (tocavaAntesDaPausa) {
            recursos.musica().pause();
        }
    }

    /** Voltou: retoma só se tocava antes e se ninguém desligou o som nem pausou a música no meio-tempo. */
    public void retomarDoSegundoPlano() {
        if (tocavaAntesDaPausa && !mudo && !musicaPausada) {
            recursos.musica().play();
        }
        tocavaAntesDaPausa = false;
    }

    public void alternarMudo() {
        mudo = !mudo;
        if (!ativo) {
            return;
        }
        if (mudo) {
            recursos.musica().pause();
        } else if (!musicaPausada) {
            recursos.musica().play();
        }
    }

    public boolean mudo() {
        return mudo;
    }

    public boolean ativo() {
        return ativo;
    }

    /** Texto para o HUD e as telas. */
    public String estado() {
        if (!ativo) {
            return "Som: desativado";
        }
        return mudo ? "Som: desligado (M)" : "Som: ligado (M)";
    }
}
