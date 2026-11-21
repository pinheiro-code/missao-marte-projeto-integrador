# Fase 3: libGDX

O jogo da fase 2 numa janela desktop com libGDX 1.12.1. As regras (`domain`, `application`) e o ranking em arquivo (`infrastructure`) são cópias idênticas da fase 2; o que mudou é a interface (pacote `gdx`) e a organização do projeto (módulos `core` e `lwjgl3`).

## Requisitos

- JDK 17 (`JAVA_HOME` apontando para ele). O Gradle 8.7 não roda no JDK 25.
- Placa de vídeo com OpenGL 2.0 ou superior.
- Acesso ao Maven Central na primeira execução (baixa o libGDX e o LWJGL).
- Não precisa instalar o Gradle: o `gradlew` baixa a versão 8.7.

## Comandos

Nesta pasta. No PowerShell use `.\gradlew.bat`; no Git Bash, `./gradlew`.

```bash
./gradlew --version                                  # Gradle 8.7 e JVM 17
./gradlew clean build                                # compila e testa core e lwjgl3 (38 testes)
./gradlew :lwjgl3:run --console=plain                # abre a janela
./gradlew :lwjgl3:run --console=plain -Dmissaomarte.semente=4   # mesmo mapa e mesmos inimigos a cada execução
```

O `ranking.json` é gravado nesta pasta (o `run` usa a raiz da fase como diretório de trabalho; o arquivo é ignorado pelo Git).

## Controles

| Tela | Teclas |
|---|---|
| Menu | `1` jogar, `2` ver ranking, `3` limpar ranking, `4` sair |
| Nova partida | digitar o nome, `Backspace` apaga, setas mudam a dificuldade, `Enter` decola, `Esc` volta |
| Partida | `W`/`A`/`S`/`D` ou setas movem, `E` embarca, `Q` ou `Esc` aborta e volta ao menu; outras teclas não gastam turno |
| Fim da partida | `Enter` vai ao ranking (vitória) ou ao menu (derrota) |
| Ranking | `Enter` ou `Esc` volta ao menu |

## Estrutura

```text
fase-03-libgdx/
├── settings.gradle            inclui core e lwjgl3
├── build.gradle               gdxVersion, Java 17, UTF-8, mavenCentral
├── gradlew, gradlew.bat, gradle/wrapper/   (8.7, copiado da fase 2)
├── core/                      o jogo, sem nada de plataforma
│   ├── src/main/java/br/edu/unifor/missaomarte/{domain, application, infrastructure, gdx}
│   └── src/test/java/...      38 testes JUnit 5, sem janela
├── lwjgl3/                    só o DesktopLauncher
└── docs/                      tutorial, capturas e evidências
```

O console (`console/` e `Main`) continua na fase 2; ele não faz parte desta fase.

## Documentação

- [Tutorial](docs/tutorial.md): de onde viemos, conceitos do libGDX com o log real, decisões, passo a passo com os problemas, testes, evidências e limitações.
