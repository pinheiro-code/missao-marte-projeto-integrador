package br.edu.unifor.missaomarte.android;

import android.os.Bundle;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingService;
import br.edu.unifor.missaomarte.gdx.MissaoMarteGame;
import br.edu.unifor.missaomarte.gdx.Plataforma;
import br.edu.unifor.missaomarte.infrastructure.FileRankingRepository;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import java.io.File;
import java.time.Clock;
import java.util.Random;

/**
 * Composition root do Android: as mesmas peças do desktop, com o ranking na pasta privada do app,
 * o modo toque e a caixa de texto nativa. O servidor e as Preferences nascem no create() do jogo,
 * porque antes do initialize o Gdx.net, o Gdx.app e as Preferences ainda não existem.
 */
public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle estadoSalvo) {
        super.onCreate(estadoSalvo);
        Random random = new Random();
        Clock clock = Clock.systemDefaultZone();
        File arquivoDoRanking = new File(getFilesDir(), "ranking.json");
        RankingService ranking = new RankingService(new FileRankingRepository(arquivoDoRanking.toPath()), clock);
        MissaoMarteGame jogo = new MissaoMarteGame(new MissaoFactory(random), ranking, random, clock, true,
                new Plataforma(true, true), BuildConfig.SERVIDOR_PADRAO);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useImmersiveMode = true;
        initialize(jogo, config);
    }
}
