# Servidor do Missão Marte (fase 6)

Cópia do servidor da fase 5 com as mudanças mínimas para publicação (seção "Mudanças da fase 6").
As rotas e regras da fase 5 continuam iguais. Para publicar no Wi-Fi local ou num contêiner na nuvem,
veja o [DEPLOY.md](DEPLOY.md).

Servidor Spring Boot independente do jogo: cadastro, login por token (JWT), registro de partidas,
histórico do jogador e ranking global. O cliente libGDX conversa com ele pela API REST abaixo.
O servidor não importa nada do código do cliente (tem a própria cópia do enum de dificuldade).

## Requisitos

- JDK 17 em `JAVA_HOME` **e** no `PATH` (o primeiro `java` do `PATH` tem de ser o 17).
- Nada a instalar além disso: o Gradle 9.8.2 vem pelo wrapper e o banco é o H2 em arquivo.
- Acesso ao Maven Central e ao `services.gradle.org` na primeira execução.

## Comandos

Nesta pasta (`fases/fase-06-android/servidor`):

```powershell
.\gradlew --version                     # Gradle 9.8.2 e JVM 17
.\gradlew test
.\gradlew bootRun --console=plain       # http://localhost:8080
```

No Git Bash, os mesmos comandos com `./gradlew`. Para parar, `Ctrl+C` e depois `./gradlew --stop`:
dois servidores no mesmo arquivo de banco travam um ao outro.

Com o servidor no ar, `bash docs/roteiro-api.sh` chama a API passo a passo e confere cada código de
status (sai diferente de 0 se algum falhar). `requests.http` tem as mesmas chamadas para rodar uma a uma
no IntelliJ ou na extensão REST Client do VS Code.

## Variáveis de ambiente

| variável | padrão | para que serve |
|---|---|---|
| `MISSAOMARTE_JWT_SEGREDO` | vazio | segredo que assina os tokens, com pelo menos 32 bytes. Vazio: o servidor gera um aleatório a cada subida e avisa no log (os tokens deixam de valer quando ele reinicia). Com menos de 32 bytes o servidor não sobe |
| `MISSAOMARTE_DADOS` | `./dados` | pasta do arquivo do banco H2 (ignorada pelo Git) |
| `MISSAOMARTE_ENDERECO` | `127.0.0.1` | endereço em que o servidor escuta; por padrão só a própria máquina |
| `MISSAOMARTE_PORTA` | vazio | porta HTTP; quando definida, ganha da `PORT` |
| `PORT` | `8080` | porta informada pelos provedores de nuvem; vale quando `MISSAOMARTE_PORTA` não existe |

O segredo vem de variável de ambiente para não ficar no código nem no repositório: quem lê o
repositório não consegue assinar um token válido. O único segredo versionado é o dos testes, em
`src/test/resources/application-test.properties`.

## API

Corpo e resposta em JSON. Datas como texto `yyyy-MM-dd'T'HH:mm:ss` (hora local do servidor, sem fuso
e sem fração), por exemplo `2026-11-21T14:03:05`.

| método e rota | token | corpo | sucesso | erros |
|---|---|---|---|---|
| `POST /api/usuarios` | não | `{"nome","senha"}` | 201 `{"id","nome"}` | 400 inválido; 409 nome já existe (sem diferenciar maiúsculas) |
| `POST /api/auth/login` | não | `{"nome","senha"}` | 200 `{"token","tipo":"Bearer","expiraEm"}` | 401 "Usuário ou senha inválidos."; 400 campo ausente ou senha acima de 72 bytes |
| `POST /api/partidas` | sim | `{"dificuldade","resultado","pontuacao","duracaoSegundos","movimentos"}` | 201 partida | 400, 401 |
| `GET /api/partidas/minhas?pagina=0&tamanho=10` | sim | | 200 lista, da mais recente para a mais antiga | 400 (`pagina` < 0, `tamanho` fora de 1 a 50), 401 |
| `GET /api/ranking` | sim | | 200 até 10 linhas | 401 |
| `GET /api/saude` | não | | 200 `{"status":"ok"}` | |

Nas rotas com token, o cabeçalho é `Authorization: Bearer <token>`. O token vale 2 horas.

### Exemplos

Cadastro:

```http
POST /api/usuarios
{"nome": "ana_01", "senha": "senha-forte-123"}

201 {"id": 1, "nome": "ana_01"}
```

Login:

```http
POST /api/auth/login
{"nome": "ana_01", "senha": "senha-forte-123"}

200 {"token": "<token>", "tipo": "Bearer", "expiraEm": "2026-11-21T16:03:05"}
```

Registrar partida (o dono vem do token; um campo `usuario` no corpo é ignorado):

```http
POST /api/partidas
{"dificuldade": "MEDIO", "resultado": "VITORIA", "pontuacao": 150, "duracaoSegundos": 95, "movimentos": 40}

201 {"id": 7, "dificuldade": "MEDIO", "resultado": "VITORIA", "pontuacao": 150,
     "duracaoSegundos": 95, "movimentos": 40, "dataHora": "2026-11-21T14:05:10"}
```

Histórico (`GET /api/partidas/minhas?pagina=0&tamanho=10`): lista de partidas no formato acima.

Ranking (`GET /api/ranking`):

```json
[{"posicao": 1, "nome": "ana_01", "pontuacao": 150, "dificuldade": "MEDIO",
  "duracaoSegundos": 95, "movimentos": 40, "dataHora": "2026-11-21T14:05:10"}]
```

### Regras dos campos

| campo | regra |
|---|---|
| `nome` (cadastro) | 3 a 20 caracteres: letras, números, `_` e `-` |
| `senha` (cadastro) | pelo menos 8 caracteres e no máximo 72 bytes em UTF-8 (o BCrypt ignora o resto) |
| `dificuldade` | `FACIL`, `MEDIO` ou `DIFICIL` |
| `resultado` | `VITORIA` ou `DERROTA` (partida abortada não é enviada) |
| `pontuacao` | de -100000 a 100000 |
| `duracaoSegundos` | de 0 a 86400 |
| `movimentos` | de 0 a 100000 |

Todo campo ausente é 400 (os tipos aceitam `null`, então um campo faltando não vira zero).

No login, nome ou senha fora do formato do cadastro não dão 400: seguem para a conferência e dão o
mesmo 401 de senha errada, para não revelar nada sobre o nome. A exceção é a senha acima de 72 bytes,
que dá 400 (o BCrypt compararia só o começo dela). Isso diverge um pouco do enunciado, que só cita o
401 para o login.

### Ranking

Uma linha por jogador, com a melhor vitória dele. Só entram vitórias com pontuação maior que zero.
Ordem: maior pontuação; no empate, menor duração; depois a partida mais antiga; por fim o id da partida
(assim duas vitórias iguais do mesmo jogador não viram duas linhas). No máximo 10 linhas.

O banco devolve as vitórias positivas já nessa ordem, e o serviço pega a primeira de cada jogador
até completar 10 (em Java, e não por subconsulta).

### Formato do erro

Todo erro sai como `ProblemDetail` (RFC 9457), com `Content-Type: application/problem+json`:

```json
{"status": 400, "title": "Requisição inválida", "detail": "Um ou mais campos são inválidos.",
 "instance": "/api/usuarios",
 "erros": [{"campo": "nome", "mensagem": "O nome deve ter de 3 a 20 caracteres: letras, números, _ ou -."}]}
```

- `erros` só aparece em erro de validação, ordenado pelo nome do campo. Enum desconhecido ou tipo errado
  num campo também entram em `erros`, com a mensagem "Valor inválido.".
- JSON quebrado: 400 com `detail` "O corpo da requisição não é um JSON válido.", sem a mensagem interna.
- Rota protegida sem token, com token inválido ou expirado: 401 com `detail` "Token ausente, inválido
  ou expirado." e o cabeçalho `WWW-Authenticate: Bearer`.
- Erro inesperado: 500 com `detail` genérico, sem stack trace na resposta.

## Segurança

- Senha guardada só como hash BCrypt de custo 10 (cada hash tem o próprio sal: a mesma senha gera
  hashes diferentes). Nenhuma resposta traz senha ou hash.
- Login com nome inexistente confere a senha contra um hash falso, para demorar o mesmo que a senha
  errada, e responde exatamente igual.
- Token JWT HS256 com o id do usuário em `sub`, o `nome`, a emissão e a expiração (2 h). Sem senha.
- Tudo exige token por padrão; as rotas públicas (`POST /api/usuarios`, `POST /api/auth/login`,
  `GET /api/saude` e `/error`) estão num método só, `rotasPublicas()`, em `SegurancaConfig`.
- API sem estado: sem sessão e sem cookie, o navegador não envia credencial sozinho, por isso o CSRF
  fica desligado.
- Os DTOs de cadastro e login não mostram a senha no `toString`; a resposta do login não mostra o token.

## Limitações

- O servidor **não executa a lógica do jogo**: ele recebe o resultado que o cliente envia e não prova
  que a pontuação é verdadeira. Os limites de campo só barram valores absurdos. Para confiar no
  resultado seria preciso o servidor rodar a partida (ou conferir a sequência de jogadas).
- Sem HTTPS: o token e a senha trafegam em texto claro se o servidor for aberto para a rede.
- Sem limite de tentativas de login (força bruta).
- Sem renovação nem revogação de token: um token vazado vale até expirar.
- `ddl-auto=update` deixa o Hibernate criar e alterar as tabelas sozinho. Serve para este trabalho,
  mas não para produção: não versiona as mudanças, não remove coluna nem migra dado. Em produção
  se usariam migrações versionadas (por exemplo, Flyway).
- O ranking carrega todas as vitórias positivas para escolher a melhor de cada jogador: serve para o
  tamanho deste trabalho, não para milhões de partidas.
- Sem Swagger UI (opcional no enunciado; fica como próximo passo).

## Mudanças da fase 6

| mudança | arquivo | para quê |
|---|---|---|
| `GET /api/saude` público, `{"status":"ok"}` | `SaudeController`, `SegurancaConfig.rotasPublicas()` | o jogo testa a conexão antes de salvar o endereço; o provedor de nuvem confere se o serviço subiu |
| porta por `MISSAOMARTE_PORTA`, depois `PORT`, depois 8080 | `application.properties` | provedores de nuvem informam a porta em `PORT` |
| JAR com nome fixo `app.jar` | `build.gradle` | o `Dockerfile` não muda a cada versão |
| encerramento gracioso | nenhum (já é o padrão do Spring Boot 4) | o contêiner para sem cortar requisições; só documentado |
| `Dockerfile` em duas etapas e `.dockerignore` | novos | publicar em provedores que rodam contêineres |
| `DEPLOY.md` | novo | roteiro de publicação: Wi-Fi local e nuvem, com os avisos |
| `SaudeApiTest` | novo | `/api/saude` responde 200 sem token e `/api/ranking` continua 401 |

O endereço de escuta (`MISSAOMARTE_ENDERECO`, padrão `127.0.0.1`) já vinha da fase 5.
