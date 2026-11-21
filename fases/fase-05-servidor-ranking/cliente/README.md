# Fase 5: cliente

O jogo libGDX da fase 4 com dois modos: convidado (ranking local em `ranking.json`, como antes) e logado (conta, envio de partidas, ranking global e histórico pelo servidor em `../servidor`).

Comandos, opções e controles: [README da fase](../README.md). Tutorial, roteiro de validação e capturas: [docs/tutorial.md](../docs/tutorial.md).

## Estrutura

```text
cliente/
├── settings.gradle, build.gradle, gradlew, gradle/wrapper/ (Gradle 8.7), .gitignore
├── core/
│   ├── assets/                imagens, sons e CREDITOS.md (gerados por tools/GeradorDeAssets.java)
│   ├── src/main/java/br/edu/unifor/missaomarte/
│   │   ├── domain, infrastructure     idênticos à fase 4
│   │   ├── application                idêntico à fase 4, mais o pacote servidor (porta e tipos, sem libGDX)
│   │   └── gdx                        telas; gdx/rede tem o adaptador HTTP e o JSON
│   └── src/test/java/...      140 testes JUnit 5, sem janela, OpenGL nem rede
├── lwjgl3/                    DesktopLauncher (semente, áudio e endereço do servidor)
└── tools/GeradorDeAssets.java
```

O `ranking.json` do convidado é gravado nesta pasta (ignorado pelo Git). Senha e token nunca são gravados: a sessão vive só na memória.
