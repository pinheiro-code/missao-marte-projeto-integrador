# Evidências do servidor (fase 5)

Saídas reais, rodadas em 21/11/2026 no Git Bash do Windows 11, na pasta
`fases/fase-05-servidor-ranking/servidor`, com o JDK 17 em `JAVA_HOME` e no `PATH`:

```bash
export JAVA_HOME="/c/Users/Arthur Pinheiro/.jdks/jdk-17.0.21.1+2"
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS='-Djdk.net.unixdomain.tmpdir=C:/Users/ARTHUR~1/.gradle/udtmp'
```

## 1. Versão

```text
$ ./gradlew --version
Gradle 9.8.2
Build time:    2026-10-19 16:29:26 UTC
Launcher JVM:  17.0.21.1 (Eclipse Adoptium 17.0.21.1+2)
Daemon JVM:    C:\Users\Arthur Pinheiro\.jdks\jdk-17.0.21.1+2 (no Daemon JVM specified, using current Java home)
OS:            Windows 11 10.0 amd64
```

## 2. Testes

```text
$ ./gradlew clean test --console=plain
BUILD SUCCESSFUL in 16s

$ cat build/test-results/test/*.xml | grep -o 'tests="[0-9]*" skipped="[0-9]*" failures="[0-9]*" errors="[0-9]*"' \
    | awk -F'"' '{t+=$2;s+=$4;f+=$6;e+=$8} END {print t, s, f, e}'
49 0 0 0
```

49 testes, nenhum pulado, nenhuma falha, nenhum erro. Por classe: `CadastroApiTest` 13,
`LoginApiTest` 8, `PartidaApiTest` 14, `ProtecaoApiTest` 6, `RankingApiTest` 7 e
`ServidorApplicationTest` 1 (o contexto sobe). Todos usam MockMvc, H2 em memória e o segredo fixo de
`src/test/resources/application-test.properties`; cada teste é desfeito no fim (`@Transactional`).

### Tabela de testes do enunciado (5.7) → método

| linha do enunciado | o que verificar | teste |
|---|---|---|
| Cadastro | 201 sem senha na resposta | `CadastroApiTest.cadastroValidoDevolve201ComIdENomeSemSenha` |
| Cadastro | hash BCrypt no banco, diferente da senha | `CadastroApiTest.bancoGuardaHashBcryptDiferenteDaSenha` |
| Cadastro | sal diferente | `CadastroApiTest.mesmaSenhaGeraHashesDiferentes` |
| Cadastro | nome repetido com outra caixa (409) | `CadastroApiTest.nomeRepetidoComOutraCaixaDa409`, `CadastroApiTest.nomeUnicoSemCaixaGarantidoPeloBanco` (pelo banco) |
| Cadastro | nome e senha inválidos (400) | `CadastroApiTest.nomeCurtoDa400ComOCampo`, `nomeComCaractereProibidoOuLongoDemaisDa400`, `senhaCurtaDa400`, `camposAusentesDao400OrdenadosPeloNome` |
| Cadastro | limite de 72 bytes | `CadastroApiTest.senhaDe72BytesPassaE73BytesDa400`, `LoginApiTest.senhaAcimaDe72BytesNoLoginDa400` |
| Login | sucesso | `LoginApiTest.loginCorretoDevolveTokenBearerEExpiracao`, `loginNaoDiferenciaCaixaDoNome` |
| Login | mesma resposta para senha errada e usuário inexistente | `LoginApiTest.senhaErradaEUsuarioInexistenteDaoAMesmaResposta`, `nomeForaDoFormatoDoCadastroDa401Generico` |
| Login | senha ausente da resposta | `LoginApiTest.respostaDoLoginNaoTrazSenha` |
| Proteção | sem token (401) | `ProtecaoApiTest.semTokenDa401ComProblemDetailEWwwAuthenticate`, `rotaQueNaoEstaNaListaPublicaExigeToken`, `PartidaApiTest.partidaSemTokenDa401` |
| Proteção | token inválido (401) | `ProtecaoApiTest.tokenInvalidoDa401`, `tokenAssinadoPorUmFalsificadorDa401` |
| Proteção | token expirado (401) | `ProtecaoApiTest.tokenExpiradoDa401` |
| Proteção | token válido (200) | `ProtecaoApiTest.tokenValidoDa200` |
| Partidas | registro | `PartidaApiTest.registraPartidaDevolve201ComDataSemFracao`, `derrotaComPontuacaoNegativaTambemERegistrada` |
| Partidas | usuário do corpo ignorado | `PartidaApiTest.usuarioNoCorpoEIgnoradoEODonoVemDoToken` |
| Partidas | enum inválido e valor fora da faixa (400) | `PartidaApiTest.enumInvalidoDa400ComOCampo`, `enumMandadoComoNumeroDa400`, `tipoErradoNumCampoDa400ComOCampo`, `numeroDecimalTextoOuGrandeDemaisNoLugarDeInteiroDa400`, `valorForaDaFaixaDa400`, `camposAusentesDa400ComTodosOsCampos` |
| Partidas | histórico só do dono, na ordem certa e paginado | `PartidaApiTest.historicoSoDoDonoDaMaisRecenteParaAMaisAntigaEPaginado`, `tamanhoAcimaDe50Da400`, `paginaNegativaOuTamanhoZeroDa400`, `paginaMuitoDistanteDevolveListaVazia` |
| Ranking | vazio | `RankingApiTest.rankingVazio` |
| Ranking | uma linha por jogador com a melhor pontuação | `RankingApiTest.umaLinhaPorJogadorComAMelhorVitoria`, `duasVitoriasIguaisDoMesmoJogadorDaoUmaLinhaSo` |
| Ranking | derrotas e pontuação não positiva fora | `RankingApiTest.derrotasEPontuacaoNaoPositivaFicamFora` |
| Ranking | desempate por duração e depois por antiguidade | `RankingApiTest.empateDesfeitoPelaMenorDuracaoDepoisPelaPartidaMaisAntiga` |
| Ranking | limite de 10 | `RankingApiTest.limiteDe10Linhas` |
| Ranking | sem dados sensíveis | `RankingApiTest.semDadosSensiveis` |

Além da tabela: `toStringDoCadastroNaoMostraASenha`, `toStringDoLoginNaoMostraASenha` (senha fora do
`toString`), `jsonQuebradoDa400SemMensagemInterna` e `jsonCortadoNoMeioDeUmCampoTambemDaODetailGenerico`
(JSON quebrado sem mensagem interna). O formato das datas sem fração é conferido em
`registraPartidaDevolve201ComDataSemFracao`, `loginCorretoDevolveTokenBearerEExpiracao` e
`historicoSoDoDonoDaMaisRecenteParaAMaisAntigaEPaginado`.

## 3. Subida do servidor

```text
$ rm -rf /tmp/mm-dados; MISSAOMARTE_DADOS=/tmp/mm-dados ./gradlew bootRun --console=plain > /tmp/bootrun.log 2>&1 &
$ grep -E "Tomcat started|Started ServidorApplication|MISSAOMARTE_JWT" /tmp/bootrun.log
WARN ... SegurancaConfig : MISSAOMARTE_JWT_SEGREDO não definida: usando um segredo aleatório. Os tokens deixam de valer quando o servidor reinicia.
INFO ... TomcatWebServer : Tomcat started on port 8080 (http) with context path '/'
INFO ... ServidorApplication : Started ServidorApplication in 4.574 seconds (process running for 4.931)
```

O banco foi criado em `jdbc:h2:file:C:/Users/ARTHUR~1/AppData/Local/Temp/mm-dados/missaomarte` (o Git
Bash traduz `/tmp`). No console do Windows os acentos do aviso aparecem trocados (o log sai no encoding
do console, cp1252); o texto no código está certo.

## 4. Roteiro da API (passo 4 do roteiro da fase)

Com o servidor no ar:

```text
$ bash docs/roteiro-api.sh
Servidor: http://127.0.0.1:8080  jogador: piloto1795282171
ok     cadastro: 201
ok     mesmo nome com outra caixa: 409
ok     nome curto: 400
ok     login com senha errada: 401
ok     login com nome inexistente: 401
ok     mesma resposta para senha errada e nome inexistente: sim
ok     login correto: 200
ok     token recebido: sim
ok     ranking sem token: 401
ok     ranking com token inválido: 401
ok     registro de partida: 201
ok     partida com dificuldade inválida: 400
ok     histórico: 200
ok     partida no histórico: sim
ok     ranking: 200
ok     jogador no ranking: sim
Falhas: 0
$ echo $?
0
```

Respostas de erro vistas com `curl` na mesma subida:

```text
$ curl -s -i http://127.0.0.1:8080/api/ranking
HTTP/1.1 401
WWW-Authenticate: Bearer resource_metadata="http://127.0.0.1:8080/.well-known/oauth-protected-resource"
Content-Type: application/problem+json;charset=UTF-8

{"detail":"Token ausente, inválido ou expirado.","status":401,"title":"Não autorizado"}

$ curl -s -X POST -H 'Content-Type: application/json' --data '{"nome":"ab"}' http://127.0.0.1:8080/api/usuarios
{"detail":"Um ou mais campos são inválidos.","instance":"/api/usuarios","status":400,"title":"Requisição inválida","erros":[{"campo":"nome","mensagem":"O nome deve ter de 3 a 20 caracteres: letras, números, _ ou -."},{"campo":"senha","mensagem":"Informe a senha."}]}

$ curl -s -X POST -H 'Content-Type: application/json' --data '{"nome":"x","senha":"senha-errada-1"}' http://127.0.0.1:8080/api/auth/login
{"detail":"Usuário ou senha inválidos.","instance":"/api/auth/login","status":401,"title":"Unauthorized"}
```

Depois: `kill` do processo e `./gradlew --stop` (senão o H2 e a porta 8080 ficam presos).

## 5. Senha e token fora do log

```text
$ grep -cE "senha-do-roteiro-1|eyJ" /tmp/bootrun.log
0
$ grep -ln "String toString()" src/main/java/br/edu/unifor/missaomarte/servidor/dto/{CadastroRequest,LoginRequest}.java
src/main/java/br/edu/unifor/missaomarte/servidor/dto/CadastroRequest.java
src/main/java/br/edu/unifor/missaomarte/servidor/dto/LoginRequest.java
```

`eyJ` é o começo de todo JWT; `senha-do-roteiro-1` é a senha que o roteiro usa. Os testes
`toStringDoCadastroNaoMostraASenha` e `toStringDoLoginNaoMostraASenha` passam (seção 2). O log também
não traz a linha "Using generated security password" do Spring Security (0 ocorrências): com o resource
server, o usuário padrão do Boot não é criado.

## 6. Tudo protegido por padrão

```text
$ grep -rn "permitAll" src/main/java
src/main/java/br/edu/unifor/missaomarte/servidor/seguranca/SegurancaConfig.java:47:                        .requestMatchers(rotasPublicas()).permitAll()
$ grep -n -A8 "rotasPublicas()" src/main/java/br/edu/unifor/missaomarte/servidor/seguranca/SegurancaConfig.java
57:    static RequestMatcher[] rotasPublicas() {
58-        PathPatternRequestMatcher.Builder rota = PathPatternRequestMatcher.withDefaults();
59-        return new RequestMatcher[] {
60-                rota.matcher(HttpMethod.POST, "/api/usuarios"),
61-                rota.matcher(HttpMethod.POST, "/api/auth/login"),
62-                rota.matcher("/error")
63-        };
64-    }
$ grep -rn "anyRequest().authenticated()" src/main/java
src/main/java/br/edu/unifor/missaomarte/servidor/seguranca/SegurancaConfig.java:48:                        .anyRequest().authenticated())
$ grep -rn "servidor\.model" src/main/java/br/edu/unifor/missaomarte/servidor/controller | wc -l
0
```

Os controllers só conhecem DTOs e serviços; nenhuma entidade sai pela API.

## 7. Servidor independente do cliente

```text
$ grep -rnE "missaomarte\.(domain|application|infrastructure|gdx)" src | wc -l
0
$ cat settings.gradle
rootProject.name = 'missao-marte-servidor'
```

## 8. Problemas encontrados e como foram resolvidos

| problema | como apareceu | solução |
|---|---|---|
| O plugin do Spring Boot 4.1 não roda no Gradle 8.7 e não há `gradle` no `PATH` | enunciado (4) e cache local só com o 8.7 e o 9.8.2 | com o `settings.gradle` já escrito e antes do `build.gradle`, rodar o Gradle 8.7 do cache pelo caminho inteiro (`gradle wrapper --gradle-version 9.8.2`) e depois `./gradlew wrapper --gradle-version 9.8.2`, para o 9.8.2 regravar jar e scripts |
| O wrapper 9.8.2 e o Git do Windows trocam o fim de linha | `gradlew` e `docs/roteiro-api.sh` com CRLF quebram no `bash` | `.gitattributes` com `gradlew` e `*.sh` em LF e `*.bat` em CRLF |
| Starters do Boot 4 com nomes novos | enunciado (6) | `spring-boot-starter-webmvc` (não `-web`) e os starters de teste `-webmvc-test`, `-data-jpa-test`, `-security-test` |
| `@SpringBootTest` sozinho não configura o `MockMvc` no Boot 4 | enunciado (6) | `@AutoConfigureMockMvc`, que no Boot 4 mora em `org.springframework.boot.webmvc.test.autoconfigure` |
| O resource server só confia em RS256 | enunciado (6) | `JwtEncoder` e `JwtDecoder` declarados com a mesma chave HS256 |
| 401 do token com corpo vazio | padrão do `BearerTokenAuthenticationEntryPoint` | ponto de entrada próprio que chama o do Bearer (mantém o `WWW-Authenticate`) e escreve um `ProblemDetail` |
| JSON cortado no meio de um valor (`{"nome":"ab`) respondia como erro do campo `nome` | `curl` na primeira subida | o Jackson 3 põe o caminho do campo também no erro de leitura; só erro de conversão (`DatabindException`) ou número grande demais vira item de `erros`. Teste `jsonCortadoNoMeioDeUmCampoTambemDaODetailGenerico` |
| `"resultado":0` gravava `VITORIA`, `"dificuldade":"2"` gravava `DIFICIL`, `10.9` virava 10 e `"120"` virava 120 | revisão de contexto limpo, lendo o padrão do Jackson 3.1.7 | `application.properties` com `spring.jackson.datatype.enum.fail-on-numbers-for-enums=true`, `spring.jackson.deserialization.accept-float-as-int=false` e `spring.jackson.mapper.allow-coercion-of-scalars=false`. Testes `enumMandadoComoNumeroDa400` e `numeroDecimalTextoOuGrandeDemaisNoLugarDeInteiroDa400` |
| `?pagina=50000000&tamanho=50` dava 500 | revisão de contexto limpo | o Spring Data recusa deslocamento acima de `Integer.MAX_VALUE`; o serviço devolve lista vazia antes de consultar. Teste `paginaMuitoDistanteDevolveListaVazia` |
| Validação de `pagina` e `tamanho` com `@Validated` no controller cairia no 500 | spec | controller sem `@Validated`: o `@Min`/`@Max` dos parâmetros chega como `HandlerMethodValidationException`, tratada como 400 com `erros`. Teste `tamanhoAcimaDe50Da400` |
| Nome único sem caixa: o `save` dentro da transação do teste não chega ao banco | spec | o teste grava com `saveAndFlush` e espera `DataIntegrityViolationException` |
| Acentos trocados no log redirecionado | log da subida | encoding do console do Windows (cp1252); fica registrado, o código não muda |
