# Fase 4: interface gráfica

O jogo da fase 3 com sprites, animações, movimento suave, repetição de tecla, efeitos de colisão, sons, música e HUD. As regras não mudaram: `domain` e `infrastructure` são cópias idênticas da fase 3, e `application` só ganhou, de forma aditiva, os eventos do turno (`EventoJogo` e `ResultadoTurno.eventos()`).

## Requisitos

- JDK 17 (`JAVA_HOME` apontando para ele).
- Placa de vídeo com OpenGL 2.0 ou superior.
- Dispositivo de som (opcional: dá para desativar, veja abaixo).
- Acesso ao Maven Central na primeira execução. Nenhuma dependência nova em relação à fase 3 (libGDX 1.12.1).

## Comandos

Nesta pasta. No PowerShell use `.\gradlew.bat`; no Git Bash, `./gradlew`.

```bash
./gradlew --version                                                  # Gradle 8.7 e JVM 17
./gradlew clean build                                                # compila e testa (74 testes)
./gradlew :lwjgl3:run --console=plain                                # abre a janela
./gradlew :lwjgl3:run --console=plain -Dmissaomarte.semente=4        # mesmo mapa e mesmos inimigos
./gradlew :lwjgl3:run --console=plain -Dmissaomarte.semaudio=true    # sem áudio (máquina sem som)
java tools/GeradorDeAssets.java                                      # regera core/assets (fora do build)
```

O `ranking.json` é gravado nesta pasta (ignorado pelo Git).

## Controles

| Tela | Teclas |
|---|---|
| Todas, menos nova partida | `M` liga e desliga o som |
| Menu | `1` jogar, `2` ver ranking, `3` limpar ranking, `4` sair |
| Nova partida | digitar o nome (aqui o `M` é letra), `Backspace` apaga, setas mudam a dificuldade, `Enter` decola, `Esc` volta |
| Partida | `W`/`A`/`S`/`D` ou setas movem (segurar repete: 0,28 s e depois a cada 0,14 s, um turno por repetição), `E` embarca, `Q` ou `Esc` aborta; outras teclas não gastam turno |
| Fim da partida | `Enter` vai ao ranking (vitória) ou ao menu (derrota) |
| Ranking | `Enter` ou `Esc` volta ao menu |

## Estrutura

```text
fase-04-interface-grafica/
├── settings.gradle, build.gradle, gradlew, gradle/wrapper/, .gitignore
├── core/
│   ├── assets/imagens, assets/sons, assets/CREDITOS.md   (gerados por tools/GeradorDeAssets.java)
│   ├── src/main/java/br/edu/unifor/missaomarte/{domain, application, infrastructure, gdx}
│   └── src/test/java/...      74 testes JUnit 5, sem janela, OpenGL nem áudio
├── lwjgl3/                    DesktopLauncher; core/assets entra no classpath por sourceSets
├── tools/GeradorDeAssets.java
└── docs/                      tutorial, capturas e evidências
```

## Documentação

- [Tutorial](docs/tutorial.md): de onde viemos, conceitos (assets, AssetManager, Sound x Music, posição visual, HUD, eventos), decisões, passo a passo com os problemas, testes, capturas e limitações.
- [Créditos dos assets](core/assets/CREDITOS.md).
