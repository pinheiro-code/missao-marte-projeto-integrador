# Tutorial da fase 2: migração para o Gradle (MM-03)

O jogo da fase 1, sem nenhuma linha alterada, agora é compilado, testado e executado pelo Gradle Wrapper 8.7, com testes JUnit 5.

## 6.1 Estado de origem e destino

**Antes (fase 1):** `build.ps1` chamava o `javac` em todos os `.java` de `src/` e gravava em `out/`. Sem dependências declaradas, sem testes automatizados; a validação era o roteiro manual de 8 passos.

**Agora (fase 2):**

```text
fase-02-gradle/
├── settings.gradle              nome do projeto
├── build.gradle                 plugin application, Java 17, UTF-8, JUnit 5
├── gradlew, gradlew.bat         scripts do wrapper
├── gradle/wrapper/              gradle-wrapper.jar e gradle-wrapper.properties (8.7)
├── src/main/java/br/edu/unifor/missaomarte/...   23 classes, iguais às da fase 1
├── src/test/java/br/edu/unifor/missaomarte/...   6 classes de teste, 24 testes
├── docs/
├── .gitignore                   build/, .gradle/, out/, ranking.json
└── .gitattributes               gradlew com LF, .bat com CRLF
```

| Fase 1 | Fase 2 (Gradle) |
|---|---|
| `.\build.ps1` | `.\gradlew build` |
| `.\build.ps1 -Run` | `.\gradlew run --console=plain` |
| pasta `out/` | pasta `build/` (`build/classes/java/main`) |
| `src/` | `src/main/java/` |
| sem testes | `src/test/java/` e `.\gradlew test` |
| `javac --release 17 -encoding UTF-8` | `sourceCompatibility`/`targetCompatibility` 17 e `options.encoding = 'UTF-8'` |
| `java -cp out br.edu.unifor.missaomarte.Main` | `application { mainClass = 'br.edu.unifor.missaomarte.Main' }`, ou o script de `build/install/missao-marte/bin` |

## 6.2 Conceitos de Gradle aplicados

**Tasks e o DAG.** Uma task é uma unidade de trabalho (compilar, testar, empacotar). Cada task declara de quais outras depende, e o Gradle monta um grafo sem ciclos (DAG) e roda cada uma uma vez, na ordem certa. A saída real do nosso `clean build` mostra a cadeia:

```text
:clean
:compileJava → :processResources → :classes → :jar → :startScripts → :distTar, :distZip → :assemble
:compileTestJava → :processTestResources → :testClasses → :test → :check
:assemble + :check → :build
```

Na segunda execução, o que não mudou aparece como `UP-TO-DATE` e não roda de novo (o `gradlew test` logo depois do build mostrou `Task :test UP-TO-DATE`).

**Plugin `application`.** Uma linha no `build.gradle` (`id 'application'`) traz o plugin `java` (compilar, testar, `jar`) e acrescenta `run`, `startScripts`, `installDist`, `distZip` e `distTar`. O `mainClass` diz qual classe o `run` e os scripts chamam. O `gradlew tasks --group application` do projeto lista o `run`.

**Escopos de dependência.**

| Escopo | Para quê | No nosso `build.gradle` |
|---|---|---|
| `implementation` | o jogo precisa para compilar e rodar | nenhuma: o jogo não tem dependência (`gradlew dependencies --configuration runtimeClasspath` responde `No dependencies`) |
| `testImplementation` | só os testes compilam com ela | `platform('org.junit:junit-bom:5.10.2')` e `org.junit.jupiter:junit-jupiter` |
| `testRuntimeOnly` | só existe quando os testes rodam | `org.junit.platform:junit-platform-launcher` (o Gradle 9 passa a exigir) |

O BOM fixa a versão de toda a família JUnit num lugar só: `junit-jupiter` vira 5.10.2 e o `junit-platform-launcher` vira 1.10.2.

**Wrapper.** O `gradlew` baixa e usa a versão escrita em `gradle/wrapper/gradle-wrapper.properties` (`gradle-8.7-bin.zip`). Ele é versionado para que todo mundo rode o mesmo Gradle, sem instalar nada além do JDK.

**Gradle instalado x Gradle do wrapper.** O instalado é o que estiver no PATH da máquina, de qualquer versão; só usamos uma vez, para gerar o wrapper (`gradle wrapper --gradle-version 8.7`). O do wrapper é sempre o 8.7, igual para a equipe e para o professor.

**Ciclo de vida.**

1. Inicialização: lê o `settings.gradle` e descobre quais projetos existem (aqui, um só: `missao-marte`).
2. Configuração: executa o `build.gradle` e monta o grafo de tasks.
3. Execução: roda só as tasks pedidas e as que elas dependem.

## 6.3 Passo a passo da migração

| # | O que foi feito | Problema e solução |
|---|---|---|
| 1 | Conferir o ambiente: `java -version` dava 25 | O Gradle 8.7 não roda no JDK 25. Instalamos o Temurin 17 (ZIP oficial em `~/.jdks`, porque o instalador MSI pedia administrador) e passamos `JAVA_HOME` só no comando, sem mudar o do sistema |
| 2 | Criar a pasta e copiar `fase-01-refatoracao/src` para `src/main/java` | Nenhum: cópia de arquivo, conferida por hash (6.5) |
| 3 | Escrever `settings.gradle` e `build.gradle` | |
| 4 | Gerar o wrapper com o Gradle 8.7 que já estava em `~/.gradle/wrapper/dists` | Falhou com `Unable to establish loopback connection` (`SocketException: Invalid argument: connect`). O Java 17 no Windows abre um socket local num arquivo da pasta temporária, e nesta máquina a pasta `AppData\Local\Temp` não aceita. Solução: apontar outra pasta pela propriedade `jdk.net.unixdomain.tmpdir`, passada em `JAVA_TOOL_OPTIONS` só no comando (vale para todos os processos Java que o Gradle abre). Caminho com espaço quebra a variável, então usamos o nome curto `C:/Users/ARTHUR~1/.gradle/udtmp` |
| 5 | `.gitignore` e `.gitattributes` | O `gradlew` é script de bash: com o `core.autocrlf` do Git para Windows ele iria com CRLF e quebraria no Linux. O `.gitattributes` fixa LF nele |
| 6 | Escrever os 6 testes e rodar `gradlew clean build` | Passou de primeira |
| 7 | `gradlew run` com a entrada por arquivo | A entrada chegou (o `standardInput = System.in` resolve), mas a partida não venceu: sem semente o mapa muda. A semente e o UTF-8 vão em `JAVA_TOOL_OPTIONS` (`-Dmissaomarte.semente=4 -Dstdout.encoding=UTF-8`) |
| 8 | Acentos quebrados na saída do `run` | O jogo escrevia em UTF-8, mas o Gradle relia a saída no charset padrão do Windows. `-Dfile.encoding=UTF-8` na mesma variável resolveu |

O comando completo que usamos no Git Bash, a partir desta pasta:

```bash
export JAVA_HOME="$HOME/.jdks/jdk-17.0.21.1+2"
export JAVA_TOOL_OPTIONS='-Djdk.net.unixdomain.tmpdir=C:/Users/ARTHUR~1/.gradle/udtmp -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dmissaomarte.semente=4'
./gradlew run --console=plain -q < docs/entrada-vitoria.txt
```

O `JAVA_TOOL_OPTIONS` é contorno desta máquina, não do projeto: ele não está no `build.gradle`. Para jogar no teclado basta `./gradlew run --console=plain`.

## 6.4 Testes

| Classe de teste | Testes | O que cobre |
|---|---|---|
| `domain.PosicaoTest` | 3 | `mover` devolve outra instância e não muda a original; `equals` e `hashCode` pelas coordenadas; limite do mapa |
| `domain.NaveTest` | 4 | embarque até a capacidade, nave cheia recusa; desembarque esvazia; não sai do mapa na borda; nasce na base com 3 vidas |
| `domain.AcaoTest` | 3 | as 6 teclas; só o 1º caractere e maiúscula vale; texto inválido, vazio e nulo viram `INVALIDA` |
| `application.MissaoFactoryTest` | 5 | quantidades por dificuldade; nada na mesma célula nem na base (50 cenários do Difícil em mapa 2); mapa abaixo do mínimo recusado; ajuste do tamanho; o menor mapa de cada dificuldade termina (o laço infinito da fase 0) |
| `application.GameSessionTest` | 6 | embarque soma o bônus; embarcar sem passageiro; desembarque na base libera a capacidade e fecha a conta a bordo + entregues + restantes = total; colisão tira vida e o movimento tira ponto (de novo parado no perigo); vitória só com todos entregues; sair e comando inválido |
| `infrastructure.FileRankingRepositoryTest` | 3 | grava e lê o mesmo ranking (nome com aspas); arquivo ausente vira lista vazia; limpar apaga o arquivo. Tudo em `@TempDir` |

Nenhum teste lê teclado, usa o relógio real (a `GameSession` recebe `Clock.fixed`) ou grava fora da pasta temporária. Os cenários da `GameSession` são montados à mão, sem inimigos, para não depender do sorteio.

Resultado (`gradlew cleanTest test`, 21/11/2026):

```text
> Task :test
BUILD SUCCESSFUL in 6s

$ grep -ho 'tests="[0-9]*"' build/test-results/test/*.xml
tests="6"   (GameSessionTest)
tests="5"   (MissaoFactoryTest)
tests="3"   (AcaoTest)
tests="4"   (NaveTest)
tests="3"   (PosicaoTest)
tests="3"   (FileRankingRepositoryTest)
total: 24, 0 falhas
```

O relatório HTML fica em `build/reports/tests/test/index.html`: abra no navegador depois do `gradlew test` para ver cada classe, cada teste e o tempo.

## 6.5 Evidências de validação

Git Bash, nesta pasta, com o `JAVA_HOME` e o `JAVA_TOOL_OPTIONS` da seção 6.3 (21/11/2026):

```text
$ ./gradlew --version
Gradle 8.7
JVM:          17.0.21.1 (Eclipse Adoptium 17.0.21.1+2)

$ rm -rf build .gradle && ./gradlew clean build
BUILD SUCCESSFUL in 8s
8 actionable tasks: 7 executed, 1 up-to-date

$ ./gradlew test
BUILD SUCCESSFUL               (24 testes, ver 6.4)

$ ./gradlew installDist
BUILD SUCCESSFUL in 951ms
$ ls build/install/missao-marte/bin
missao-marte  missao-marte.bat

$ ./gradlew run --console=plain -q < docs/entrada-vitoria.txt      (saída em docs/evidencias/saida-run-vitoria.txt)
Iniciando missão na dificuldade Fácil...
Comando (w/s/a/d/c/q): Passageiros entregues na base: 2. Total entregues: 4/4.
🚀 DECOLAGEM AUTORIZADA! Nave acoplada à plataforma em (0,0).
1. Ana - 70 pts | Dificuldade: Fácil | Coletados: 4 | Tempo: 0s | 2026-11-21 12:36:39
$ ls ranking.json
ranking.json
```

Comparação por hash (PowerShell, na pasta `fases`):

```powershell
$f1 = Get-ChildItem -Recurse fase-01-refatoracao\src -Filter *.java | ForEach-Object { [pscustomobject]@{ Arquivo = $_.FullName.Substring((Resolve-Path fase-01-refatoracao\src).Path.Length + 1); Hash = (Get-FileHash $_.FullName).Hash } }
$f2 = Get-ChildItem -Recurse fase-02-gradle\src\main\java -Filter *.java | ForEach-Object { [pscustomobject]@{ Arquivo = $_.FullName.Substring((Resolve-Path fase-02-gradle\src\main\java).Path.Length + 1); Hash = (Get-FileHash $_.FullName).Hash } }
$dif = Compare-Object $f1 $f2 -Property Arquivo, Hash
"Fase 1: $($f1.Count) arquivos | Fase 2: $($f2.Count) arquivos | Diferenças: $(@($dif).Count)"
```

```text
Fase 1: 23 arquivos | Fase 2: 23 arquivos | Diferenças: 0
```

No Git Bash, na raiz do repositório: `diff -r fases/fase-01-refatoracao/src fases/fase-02-gradle/src/main/java` não mostra nada.

## 6.6 Limitações e próximos passos

| Limitação | Relação com a fase 3 |
|---|---|
| Projeto de módulo único: domínio, console e infraestrutura no mesmo `jar` | O libGDX pede multi-módulo (`core` com as regras, `desktop` com o launcher). O `settings.gradle` passa a incluir os módulos, e o `domain`/`application` vão para o `core` |
| Cobertura parcial: `ConsoleGame`, `MapaRenderer` e `RankingService` não têm teste | Na fase 3 o console sai de cena; vale testar o `RankingService` (top 5) com um repositório em memória |
| Limitações herdadas da fase 1 (mensagens em português dentro da `GameSession`, regra de recorde no console, leitor de JSON feito à mão) | A tela do libGDX precisa de eventos, não de texto: a fase 4 acrescenta eventos ao `ResultadoTurno` |
| `JAVA_TOOL_OPTIONS` nesta máquina para o socket local | Não afeta outras máquinas; se aparecer em outra, o contorno é o mesmo (seção 6.3) |
| Versão do JUnit fixada à mão (5.10.2) | Com mais dependências (libGDX, cliente HTTP) vale um catálogo de versões (`gradle/libs.versions.toml`) |

## 7. Roteiro mínimo de validação

| # | Passo | Resultado |
|---|---|---|
| 1 | `gradlew --version` mostra Gradle 8.7 e JVM 17 | ok: `Gradle 8.7`, `JVM: 17.0.21.1` |
| 2 | `gradlew clean build` termina com `BUILD SUCCESSFUL` | ok |
| 3 | `gradlew test` executa todos os testes | ok: 24 testes, 0 falhas |
| 4 | `gradlew run --console=plain`: menu, partida no Fácil, no Médio e no Difícil, saída pelo menu | ok, com `docs/entrada-dificuldades.txt` (saída em `docs/evidencias/saida-run-dificuldades.txt`): `Pontos: 30 ... Total: 4`, `Pontos: 20 ... Total: 5`, `Pontos: 45 ... Total: 5`, `Obrigado por jogar a Missão Marte Unifor!` |
| 5 | Uma vitória grava o ranking e `ranking.json` aparece nesta pasta | ok (6.5) |
| 6 | Hash: os `.java` de `src/main/java` são idênticos aos da fase 1 | ok: 23 e 23, 0 diferenças |
| 7 | Build de um estado limpo (`rm -rf build .gradle` e o passo 2) | ok (6.5) |
| 8 | `fase-01-refatoracao` e a fase 0 não foram alteradas | ok: `git diff --stat mm-02-refatoracao HEAD -- fases/fase-01-refatoracao` e `git diff --stat 6236638 HEAD -- fases/fase-00-console/src` sem saída |
