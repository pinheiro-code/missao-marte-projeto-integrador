# Fase 2: Gradle

O jogo da fase 1, sem nenhuma linha alterada, compilado, testado e executado pelo Gradle Wrapper.

## Requisitos

- JDK 17 (`JAVA_HOME` apontando para ele). O Gradle 8.7 não roda no JDK 25.
- Não precisa instalar o Gradle: o `gradlew` baixa a versão 8.7 na primeira execução.

## Comandos

Nesta pasta. No PowerShell use `.\gradlew.bat`; no Git Bash, `./gradlew`.

```bash
./gradlew --version            # Gradle 8.7 e JVM 17
./gradlew build                # compila, testa e empacota
./gradlew test                 # só os testes (relatório em build/reports/tests/test/index.html)
./gradlew run --console=plain  # joga no terminal
./gradlew installDist          # script de execução em build/install/missao-marte/bin
./gradlew clean                # apaga build/
```

O `ranking.json` é gravado nesta pasta (ignorado pelo Git).

## Estrutura

```text
fase-02-gradle/
├── settings.gradle
├── build.gradle
├── gradlew, gradlew.bat, gradle/wrapper/
├── src/main/java/br/edu/unifor/missaomarte/...   jogo (igual à fase 1)
├── src/test/java/br/edu/unifor/missaomarte/...   testes JUnit 5
└── docs/
```

## Documentação

- [Tutorial](docs/tutorial.md): conceitos do Gradle, passo a passo com os problemas encontrados, testes e evidências.
