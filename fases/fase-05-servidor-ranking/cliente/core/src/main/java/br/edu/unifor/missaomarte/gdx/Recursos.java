package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Carrega imagens e sons do catálogo uma vez só, pelo AssetManager, e entrega texturas,
 * quadros, animações e sons. O AssetManager é um campo comum (não estático): quem cria o
 * Recursos é o jogo, no create(), e quem libera é o jogo, no dispose().
 */
public class Recursos implements Disposable {

    private static final String TAG = "Recursos";
    /** Tempo de cada quadro das animações em laço (propulsor, base, inimigo). */
    private static final float QUADRO_LACO = 0.18f;

    private final AssetManager assets = new AssetManager();
    private final boolean comSom;
    private final Map<Imagem, TextureRegion[]> quadros = new EnumMap<>(Imagem.class);
    private final Map<Imagem, Animation<TextureRegion>> lacos = new EnumMap<>(Imagem.class);

    /** comSom falso (áudio desativado no launcher): os sons nem são carregados. */
    public Recursos(boolean comSom) {
        this.comSom = comSom;
    }

    public void carregar() {
        for (Imagem imagem : Imagem.values()) {
            assets.load(imagem.caminho(), Texture.class);
        }
        int sons = 0;
        if (comSom) {
            for (Som som : Som.values()) {
                if (som.musica()) {
                    assets.load(som.caminho(), Music.class);
                } else {
                    assets.load(som.caminho(), Sound.class);
                }
                sons++;
            }
        }
        // load() só enfileira; aqui é que os arquivos são lidos
        assets.finishLoading();

        for (Imagem imagem : Imagem.values()) {
            Texture textura = assets.get(imagem.caminho(), Texture.class);
            // pixel art pequena esticada na célula: Nearest não borra
            textura.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
            TextureRegion[] faixa = TextureRegion.split(textura, imagem.larguraQuadro(), imagem.alturaQuadro())[0];
            quadros.put(imagem, faixa);
            if (faixa.length > 1) {
                lacos.put(imagem, new Animation<>(QUADRO_LACO, faixa));
            }
        }
        Gdx.app.log(TAG, Imagem.values().length + " imagens e " + sons + " sons carregados");
    }

    public Texture textura(Imagem imagem) {
        return assets.get(imagem.caminho(), Texture.class);
    }

    public TextureRegion quadro(Imagem imagem, int indice) {
        return quadros.get(imagem)[indice];
    }

    /** Quadro de uma animação em laço; imagem de um quadro só devolve sempre o mesmo. */
    public TextureRegion quadroEmLaco(Imagem imagem, float tempo) {
        Animation<TextureRegion> animacao = lacos.get(imagem);
        return animacao == null ? quadro(imagem, 0) : animacao.getKeyFrame(tempo, true);
    }

    public Sound som(Som som) {
        return assets.get(som.caminho(), Sound.class);
    }

    public Music musica() {
        return assets.get(Som.MUSICA.caminho(), Music.class);
    }

    /** Libera texturas, sons e música de uma vez: nada é liberado à mão fora daqui. */
    @Override
    public void dispose() {
        assets.dispose();
    }
}
