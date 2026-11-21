# Missão Marte: Projeto Integrador

Projeto Integrador de Projeto e Arquitetura de Sistemas (Unifor, 2026.2). A equipe evoluiu o jogo **Missão Marte Unifor**, que nasceu como um programa Java de console, em fases: refatoração em camadas, build com Gradle, interface gráfica com libGDX, servidor de ranking com Spring Boot, versão Android com controles por toque e uma melhoria própria (cor da nave).

Cada fase fica numa pasta própria em `fases/`. Uma fase nova nunca altera as anteriores: começa como cópia da anterior, e a cópia é conferida por `diff -r` ou hash.

- Equipe: Arthur Pinheiro ([@pinheiro-code](https://github.com/pinheiro-code)) e Rafael David ([@rafaeldavidsantana](https://github.com/rafaeldavidsantana))
- Quadro Kanban: [Missão Marte - Kanban](https://github.com/users/pinheiro-code/projects/1)

## Fases

| Fase | Pasta | O que é | Sprint | Testes |
|---|---|---|---|---|
| 0 | `fases/fase-00-console` | Jogo de console original, sem alteração, e o diagnóstico | 1 | nenhum |
| 1 | `fases/fase-01-refatoracao` | Refatoração em camadas (domain, application, console, infrastructure) | 1 | nenhum (nascem na fase 2) |
| 2 | `fases/fase-02-gradle` | Build, testes e execução pelo Gradle Wrapper | 1 | 24 |
| 3 | `fases/fase-03-libgdx` | Jogo numa janela libGDX (módulos `core` e `lwjgl3`) | 2 | 38 |
| 4 | `fases/fase-04-interface-grafica` | Sprites, sons, animações, efeitos e HUD | 2 | 75 |
| 5 | `fases/fase-05-servidor-ranking` | Servidor de ranking (Spring Boot) e cliente libGDX com contas e ranking global | 3 | servidor 49, cliente 140 |
| 6 | `fases/fase-06-android` | Módulo Android, controles por toque, servidor configurável no jogo e servidor pronto para publicar | 4 | servidor 52, cliente 269 |
| 7 | `fases/fase-07-evolucao-livre` | Melhoria da equipe: cor da nave escolhida na Nova partida e guardada entre partidas | 4 | servidor 52, cliente 282 |

## Requisitos

- JDK 17 no `JAVA_HOME` e na frente do `PATH` (fases 1 em diante compilam para Java 17).
- Git Bash ou PowerShell. No PowerShell use `.\gradlew.bat` e ponha cada `-D...` entre aspas (PowerShell 5.1); no Git Bash, `./gradlew`.
- Placa de vídeo com OpenGL 2.0 para as fases 3 em diante.
- Acesso ao Maven Central na primeira execução de cada projeto Gradle (o `gradlew` baixa o próprio Gradle).
- Android SDK com a plataforma 35, só para gerar o APK das fases 6 e 7. Sem ele, o módulo `android` fica fora do build e o resto compila e testa.

## Como rodar

Cada pasta de fase, da 1 em diante, tem o próprio README com todos os comandos e opções (a fase 0 tem o `ORIGEM.md` e o diagnóstico em `docs/`). Os principais:

| Fase | Pasta | Comandos |
|---|---|---|
| 0 | `fases/fase-00-console` | `javac -encoding UTF-8 -d out src/*.java` e `java -cp out Main` |
| 1 | `fases/fase-01-refatoracao` | `.\build.ps1 -Run` (PowerShell) |
| 2 | `fases/fase-02-gradle` | `./gradlew build` e `./gradlew run --console=plain` |
| 3 e 4 | `fases/fase-03-libgdx`, `fases/fase-04-interface-grafica` | `./gradlew clean build` e `./gradlew :lwjgl3:run --console=plain` |
| 5 servidor | `fases/fase-05-servidor-ranking/servidor` | `./gradlew test` e `./gradlew bootRun --console=plain` (porta 8080) |
| 5 cliente | `fases/fase-05-servidor-ranking/cliente` | `./gradlew clean build` e `./gradlew :lwjgl3:run --console=plain -Dmissaomarte.servidor=http://localhost:8080` |
| 6 e 7 | `fases/fase-06-android`, `fases/fase-07-evolucao-livre` | servidor: `./gradlew test` e `./gradlew bootRun --console=plain`; cliente: `./gradlew clean build` e `./gradlew :lwjgl3:run --console=plain`; modo toque no desktop: acrescentar `-Dmissaomarte.toque=true -Dmissaomarte.janela=1200x540`; APK (só com o SDK): `./gradlew :android:assembleDebug` |

Opções comuns do jogo: `-Dmissaomarte.semente=4` repete o mesmo mapa e os mesmos inimigos; `-Dmissaomarte.semaudio=true` roda sem dispositivo de som. Em todas as fases o `ranking.json` local é criado na pasta de onde o jogo foi iniciado (ignorado pelo Git).

O cliente funciona sem o servidor: no modo convidado nada vai para a rede e o ranking é local.

## Servidor e API

As rotas, os corpos, o formato do erro, as variáveis de ambiente e a segurança estão em `fases/fase-05-servidor-ranking/servidor/README.md`; chamadas de exemplo em `requests.http` e um roteiro que confere a API em `docs/roteiro-api.sh`. A fase 6 acrescenta `GET /api/saude` e a publicação (`PORT`, `Dockerfile` e `DEPLOY.md` em `fases/fase-06-android/servidor`).

| Variável | Para quê |
|---|---|
| `MISSAOMARTE_JWT_SEGREDO` | segredo do token (32 bytes ou mais); sem ela, cada subida gera um novo e quem estava logado precisa entrar de novo |
| `MISSAOMARTE_DADOS` | pasta do banco H2 (padrão `./dados`) |
| `MISSAOMARTE_PORTA` | porta (padrão 8080); a partir da fase 6 também vale a `PORT` dos provedores de nuvem |
| `MISSAOMARTE_ENDERECO` | endereço de escuta (padrão `127.0.0.1`; `0.0.0.0` para a rede local) |

Nenhum segredo fica no repositório. Use só dados de teste.

## Limitações conhecidas

- O servidor não executa a lógica do jogo: grava a pontuação que o cliente manda e não prova que ela é verdadeira.
- Não há APK nem teste em aparelho: a máquina da equipe não tem Android SDK e a equipe não tem aparelho Android. A evidência é o modo toque no desktop; o que não foi verificado e os comandos para verificar estão em `fases/fase-06-android/docs/tutorial.md`, seção 8.7.
- Também não verificados: `docker build`, publicação em nuvem, controle Bluetooth e acesso de outro aparelho pelo Wi-Fi.

## Documentação

- Tutorial e evidências de cada fase: `fases/<fase>/docs/` (tutorial, capturas e saídas reais).

## Como a equipe trabalha

- Cada issue tem uma branch `mm-NN-<assunto>` e um pull request com `Closes #N`. Exceções registradas nos PRs: a fase 6 (#10, #11 e #12) e a fase 7 (#13 e #14) têm um PR para várias issues, porque são o mesmo build e dependem em cadeia.
- A issue vai para "Em revisão" quando o PR abre e para "Concluído" só quando outro integrante revisa e faz o merge.
- Merge só por merge commit (os PRs foram empilhados; squash ou rebase repetem commits nos PRs de cima).
- Commits pequenos, no formato `tipo(escopo): descrição`.

## Divergências entre as fontes

O enunciado do AVA, o arquivo de issues do professor (ISS) e os enunciados das fases nem sempre batem. Onde divergem, seguimos o AVA, que é o enunciado da disciplina:

| Assunto | O que diz cada fonte | O que seguimos |
|---|---|---|
| Colunas do quadro | ISS tem 5 (com "Pronto"); AVA tem 4 | 4 colunas: Backlog, Em andamento, Em revisão, Concluído |
| Milestones | ISS pede um por fase; AVA pede por calendário | um por sprint (Sprint 1 a Sprint 5) |
| Campo "Sprint" no quadro | ISS sugere um campo Iteration | não criamos: a sprint já está no milestone |
| Testes na fase 1 | ISS (MM-02) cobra testes automatizados; o enunciado da fase 2 diz que a fase 1 não tem testes | os testes nascem na fase 2, ainda na Sprint 1 |
| Orientação do celular | a issue MM-11 pede retrato e paisagem; o enunciado da fase 6 manda `sensorLandscape` | paisagem no aparelho; o layout cabe em retrato (registrado no PR da fase 6) |
