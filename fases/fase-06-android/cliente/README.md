# Fase 6: cliente

O jogo libGDX da fase 5 (convidado e logado) rodando no desktop e, com o Android SDK, também no celular, com
controles por toque e o endereço do servidor configurável dentro do jogo.

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
│   │   ├── domain             idêntico à fase 5 (mesmo hash)
│   │   ├── application        o da fase 5 sem API do Java 9+, e no pacote servidor a configuração e o endereço do servidor
│   │   ├── infrastructure     o da fase 5 sem Files.readString/writeString
│   │   └── gdx                telas com toque, botões virtuais, tela do servidor; gdx/rede tem o adaptador HTTP
│   └── src/test/java/...      269 testes JUnit 5, sem janela, OpenGL, rede ou SDK
├── lwjgl3/                    DesktopLauncher (-Dmissaomarte.toque, janela, servidor, semente, semaudio)
├── android/                   AndroidLauncher, AndroidManifest.xml, build.gradle e res/mipmap-* (ícone)
└── tools/                     GeradorDeAssets.java e GeradorDeIcone.java
```

O `ranking.json` do convidado é gravado nesta pasta no desktop (ignorado pelo Git) e na pasta privada do app no
Android. As `Preferences` (`missaomarte`) guardam só o endereço, a porta e o HTTPS do servidor. Senha e token
nunca são gravados: a sessão vive só na memória.

O ícone é gerado por código: na pasta `cliente`, `java tools/GeradorDeIcone.java` grava os 5 tamanhos em
`android/res/mipmap-*` (sempre os mesmos bytes).
