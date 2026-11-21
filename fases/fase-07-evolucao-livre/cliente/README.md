# Fase 7: cliente

O cliente da fase 6 (desktop e, com o Android SDK, celular; toque e servidor configurável) com a melhoria da
fase 7: a cor da nave escolhida na Nova partida e guardada entre partidas.

Comandos, opções e controles: [README da fase](../README.md). Tutorial, roteiro de validação e capturas: [docs/tutorial.md](../docs/tutorial.md).

## Estrutura

```text
cliente/
├── settings.gradle            inclui android só se achar platforms/android-35 (sdk.dir ou ANDROID_HOME)
├── build.gradle               java 17 em core e lwjgl3; valida o -PservidorPadrao
├── gradlew, gradle/wrapper/ (Gradle 8.7), .gitignore (com local.properties)
├── core/
│   ├── assets/                imagens, sons e CREDITOS.md (gerados por tools/GeradorDeAssets.java)
│   ├── src/main/java/br/edu/unifor/missaomarte/
│   │   ├── domain             idêntico às fases 5 e 6
│   │   ├── application        o da fase 6; novo na fase 7: o pacote preferencias (CorDaNave, PreferenciaDaNave)
│   │   ├── infrastructure     o da fase 5 sem Files.readString/writeString
│   │   └── gdx                telas com toque, botões virtuais, tela do servidor; na fase 7, a cor na Nova partida e a tinta no MundoRenderer
│   └── src/test/java/...      281 testes JUnit 5 (269 da fase 6 e 12 novos), sem janela, OpenGL, rede ou SDK
├── lwjgl3/                    DesktopLauncher (-Dmissaomarte.toque, janela, servidor, semente, semaudio)
├── android/                   AndroidLauncher, AndroidManifest.xml, build.gradle e res/mipmap-* (ícone)
└── tools/                     GeradorDeAssets.java e GeradorDeIcone.java
```

O `ranking.json` do convidado é gravado nesta pasta no desktop (ignorado pelo Git) e na pasta privada do app no
Android. As `Preferences` (`missaomarte`) guardam só o endereço, a porta e o HTTPS do servidor e a cor da nave (`nave.cor`). Senha e token
nunca são gravados: a sessão vive só na memória.

O ícone é gerado por código: na pasta `cliente`, `java tools/GeradorDeIcone.java` grava os 5 tamanhos em
`android/res/mipmap-*` (sempre os mesmos bytes).
