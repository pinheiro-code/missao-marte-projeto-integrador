# Fase 5: servidor e ranking global

O jogo da fase 4 ganhou contas de jogador e um ranking global. A fase tem dois projetos independentes:

| pasta | o que é | build |
|---|---|---|
| `servidor/` | API REST em Spring Boot 4: cadastro, login com token JWT, registro de partidas, histórico e ranking global, com banco H2 em arquivo | Gradle 9.8.2, wrapper próprio |
| `cliente/` | o jogo libGDX da fase 4, agora com modo convidado (como antes) e modo logado (usa o servidor) | Gradle 8.7, wrapper próprio |

O servidor guarda contas e resultados; ele **não executa a lógica do jogo** e não prova que a pontuação enviada é verdadeira (ver `docs/tutorial.md`, seção 8.3).

## Por que dois builds

O servidor precisa do Gradle 9 (o plugin do Spring Boot 4 não roda no 8.7) e o cliente usa o Gradle 8.7 do libGDX 1.12.1, que é o que as fases 3 e 4 já usam. Juntar os dois num build só obrigaria a mexer no cliente sem ganho nenhum. Separados, cada um compila, testa e roda sozinho, e o servidor não conhece nenhuma classe do cliente: tem a sua própria cópia do enum de dificuldade, e o único acordo entre os dois é o contrato da API.

## Requisitos

- JDK 17 no `JAVA_HOME` **e** na frente do `PATH` (o `java` do terminal vem do `PATH`).
- Para o cliente: placa de vídeo com OpenGL 2.0 ou superior.
- Acesso ao Maven Central na primeira execução de cada projeto.

## Comandos

No PowerShell use `.\gradlew.bat`; no Git Bash, `./gradlew`.

```powershell
# servidor (deixe este terminal aberto)
cd servidor
.\gradlew --version                     # Gradle 9.8.2 e JVM 17
.\gradlew test
.\gradlew bootRun --console=plain       # http://localhost:8080

# cliente (outro terminal)
cd cliente
.\gradlew --version                     # Gradle 8.7 e JVM 17
.\gradlew clean build                   # 140 testes
.\gradlew :lwjgl3:run --console=plain
.\gradlew :lwjgl3:run -Dmissaomarte.servidor=http://localhost:8080 --console=plain
```

Opções do cliente (todas com `-D` no `gradlew`, que repassa ao jogo):

| opção | padrão | para quê |
|---|---|---|
| `-Dmissaomarte.servidor=http://host:porta` | `http://localhost:8080` | endereço do servidor; vazio vale o padrão |
| `-Dmissaomarte.semente=4` | sorteio livre | mesmo mapa e mesmos inimigos na primeira partida da execução |
| `-Dmissaomarte.semaudio=true` | com som | para máquina sem dispositivo de som |

As variáveis do servidor (porta, pasta do banco e segredo do token) estão em `servidor/README.md`. Para o token continuar valendo depois de reiniciar o servidor, defina `MISSAOMARTE_JWT_SEGREDO` (32 bytes ou mais); sem ela, cada subida gera um segredo novo e quem estava logado recebe "Sessão expirada".

O cliente funciona sem o servidor: no modo convidado nada vai para a rede.

## Controles do cliente

| Tela | Convidado | Logado |
|---|---|---|
| Menu | `1` jogar, `2` ranking local, `3` limpar ranking local, `4` entrar ou criar conta, `5` sair | `1` jogar, `2` ranking global, `3` meu histórico, `4` sair da conta, `5` sair |
| Nova partida | digitar o nome (o `M` é letra), setas mudam a dificuldade, `Enter` decola, `Esc` volta | o nome é o da conta; setas mudam a dificuldade, `Enter` decola, `Esc` volta |
| Partida | `W`/`A`/`S`/`D` ou setas movem, `E` embarca, `Q` ou `Esc` aborta | igual |
| Fim da partida | vitória vai para o ranking local; `Enter` continua | vitória e derrota são enviadas; `Enter` só depois do envio; se falhar, `R` tenta de novo |
| Ranking | Top 5 local, `Enter` ou `Esc` volta | Top 10 global, diz se você está nele, `R` recarrega |
| Entrar ou criar conta | nome e senha (senha com `*`), `Tab` ou setas trocam o campo, `F2` alterna entrar e criar conta, `Enter` confirma, `Esc` volta; o `M` é letra | |
| Meu histórico | | últimas 10 partidas, `R` recarrega, `Enter` ou `Esc` volta |

Em toda tela que não digita texto, `M` liga e desliga o som. Abortar a partida não envia nada. Resposta 401 numa chamada do modo logado (com a tela que pediu ainda aberta) descarta a sessão, mostra "Sessão expirada" e volta ao menu do convidado. O nome mostrado no jogo é o digitado ao entrar; o ranking mostra o nome como foi cadastrado (o login não diferencia maiúscula).

## API (resumo)

| método e rota | token | sucesso |
|---|---|---|
| `POST /api/usuarios` | não | 201 `{"id","nome"}` |
| `POST /api/auth/login` | não | 200 `{"token","tipo":"Bearer","expiraEm"}` |
| `POST /api/partidas` | sim | 201 partida |
| `GET /api/partidas/minhas?pagina=0&tamanho=10` | sim | 200 lista de partidas |
| `GET /api/ranking` | sim | 200 até 10 linhas, uma por jogador |

Corpos, regras dos campos, formato do erro (`ProblemDetail`), segurança e limitações: `servidor/README.md`. Chamadas de exemplo: `servidor/requests.http`.

## Documentação

- `docs/tutorial.md`: estado de origem e destino, conceitos, segurança, decisões, passo a passo com os problemas encontrados, testes, roteiro de validação de 16 passos com capturas e limitações.
- `docs/capturas/`: telas do cliente; `docs/evidencias/`: saídas reais das execuções.
- `servidor/docs/evidencias.md`: saídas reais do servidor (testes, subida, roteiro da API).
