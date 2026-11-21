# Publicar o servidor do Missão Marte (fase 6)

Roteiro para o jogo no celular alcançar o servidor. São dois caminhos: **(A) Wi-Fi local**, com o
servidor rodando no seu PC, e **(B) contêiner na nuvem**. Este é um trabalho acadêmico: o objetivo é
testar no celular, não manter um serviço no ar. Leia os avisos no fim antes de abrir o servidor.

Nenhuma credencial real aparece aqui: troque cada `<...>` pelo seu valor. O segredo do token é lido de um
arquivo ou variável fora do repositório e nunca é escrito no comando.

## Variáveis de ambiente

| variável | padrão | para que serve |
|---|---|---|
| `MISSAOMARTE_ENDERECO` | `127.0.0.1` | endereço de escuta. `127.0.0.1` só aceita a própria máquina; `0.0.0.0` aceita qualquer interface (rede local, contêiner) |
| `MISSAOMARTE_PORTA` | vazio | porta HTTP; quando definida, ganha da `PORT` |
| `PORT` | `8080` | porta que os provedores de nuvem informam ao contêiner; vale quando `MISSAOMARTE_PORTA` não existe |
| `MISSAOMARTE_DADOS` | `./dados` (no contêiner, `/dados`) | pasta do banco H2 em arquivo |
| `MISSAOMARTE_JWT_SEGREDO` | vazio | segredo que assina os tokens (32 bytes ou mais). Vazio: um aleatório a cada subida |

Verificação de saúde: `GET /api/saude` responde `200 {"status":"ok"}` sem token. É o que o botão
**Testar conexão** do jogo chama e o que o provedor de nuvem deve consultar.

O servidor já encerra de forma graciosa (padrão do Spring Boot 4): ao receber o sinal de parada, ele
termina as requisições em andamento antes de sair.

## Caminho A: Wi-Fi local

O celular e o PC precisam estar na **mesma rede Wi-Fi** (a de casa, por exemplo; rede de faculdade ou
de café costuma isolar os aparelhos).

1. Descubra o IP do PC na rede. No PowerShell, `ipconfig`, e procure o "Endereço IPv4" do adaptador
   Wi-Fi (algo como `192.168.0.15`).
2. Suba o servidor escutando em todas as interfaces. No Git Bash, na pasta `servidor`:

   ```bash
   # o segredo mora num arquivo fora do repositório (ex.: gerado uma vez com openssl rand -base64 48)
   export MISSAOMARTE_JWT_SEGREDO=$(cat ~/segredo-missaomarte.txt)
   MISSAOMARTE_ENDERECO=0.0.0.0 ./gradlew bootRun --console=plain
   ```

   No PowerShell:

   ```powershell
   $env:MISSAOMARTE_JWT_SEGREDO = Get-Content "$HOME\segredo-missaomarte.txt"
   $env:MISSAOMARTE_ENDERECO = '0.0.0.0'; .\gradlew bootRun --console=plain
   ```

   Sem `0.0.0.0` o servidor só escuta em `127.0.0.1` e **nenhum outro aparelho o alcança**, mesmo
   com o firewall liberado.
3. Firewall do Windows: na primeira vez, o Windows pergunta se o Java pode receber conexões. Marque
   **só "Redes privadas"** (nunca "Redes públicas"). Se a pergunta não aparecer, crie uma regra de
   entrada para a porta 8080 no perfil Privado (Painel de Controle, Windows Defender Firewall,
   Configurações avançadas, Regras de Entrada). Confira que a sua rede Wi-Fi está marcada como privada.
4. Teste pelo navegador do celular: `http://<IP do PC>:8080/api/saude` deve mostrar `{"status":"ok"}`.
5. No jogo, menu, opção **6 Servidor**: endereço `<IP do PC>`, porta `8080`, HTTPS desligado,
   **Testar conexão** e **Salvar**.

Alternativa sem Wi-Fi, com o celular no cabo USB e a depuração USB ligada:

```bash
adb reverse tcp:8080 tcp:8080
```

Assim o `localhost:8080` do celular vira o `localhost:8080` do PC, e o servidor pode continuar em
`127.0.0.1`. No jogo, endereço `localhost`, porta `8080`.

No **emulador** do Android, o PC é `10.0.2.2` (o `localhost` do emulador é o próprio emulador).
Para já gerar o APK com esse padrão: `./gradlew :android:assembleDebug -PservidorPadrao=http://10.0.2.2:8080`.

## Caminho B: contêiner na nuvem

O `Dockerfile` desta pasta tem duas etapas: a primeira compila o JAR com o JDK 17 e o wrapper; a
segunda leva só o JRE 17 e o `app.jar`, roda com um usuário sem privilégio e guarda o banco em `/dados`.

Para conferir a imagem na sua máquina (precisa do Docker):

```bash
docker build -t missao-marte-servidor .
# -e sem valor repassa a variável que já está no terminal; o segredo não aparece no comando
docker run --rm -p 8080:8080 -e MISSAOMARTE_JWT_SEGREDO -v missaomarte-dados:/dados missao-marte-servidor
curl http://localhost:8080/api/saude
```

Num provedor que roda contêineres a partir de um repositório ou de um `Dockerfile`:

1. Aponte o serviço para esta pasta (`fases/fase-06-android/servidor`) e para o `Dockerfile`.
2. Variáveis: `MISSAOMARTE_JWT_SEGREDO` como segredo do provedor (nunca no repositório). Não defina
   `MISSAOMARTE_PORTA`: o provedor informa a porta em `PORT` e o servidor a usa. `MISSAOMARTE_ENDERECO`
   já vem `0.0.0.0` da imagem.
3. Verificação de saúde (health check): caminho `/api/saude`, esperando 200.
4. Disco: se o provedor oferecer disco persistente, monte-o em `/dados`. Sem isso, veja o aviso
   sobre dados perdidos.
5. HTTPS: o provedor entrega o endereço público com HTTPS e repassa ao contêiner em HTTP. No jogo,
   endereço do serviço (sem `https://`), porta `443` e HTTPS ligado.

## Avisos obrigatórios

- **HTTP só em rede de confiança.** Sem HTTPS, a senha e o token viajam em texto claro: qualquer um
  na mesma rede pode lê-los. Use o caminho A só na sua rede de confiança (a de casa), e na nuvem
  só com o HTTPS do provedor. O jogo avisa quando o endereço usa HTTP fora da rede local, e o APK de
  release só aceita HTTPS.
- **Dados perdidos em plano gratuito.** Planos gratuitos costumam ter sistema de arquivos efêmero:
  o banco H2 em arquivo (contas, partidas, ranking) some a cada nova publicação ou reinício, a menos
  que exista um disco persistente montado em `/dados`.
- **Partida a frio.** Planos gratuitos desligam o serviço por inatividade. O primeiro acesso depois
  disso pode levar dezenas de segundos, mais que o tempo limite de 5 s do jogo: o Testar conexão ou o
  login falham com "Não foi possível conectar". Abra `/api/saude` no navegador, espere responder e
  tente de novo no jogo.
- **Segredo do token.** Gere um segredo aleatório com 32 bytes ou mais (por exemplo,
  `openssl rand -base64 48`), guarde-o no gerenciador de segredos do provedor ou num arquivo fora do
  repositório e nunca o coloque em commit, imagem, log ou captura de tela. Sem ele, o servidor gera
  um aleatório a cada subida e todo mundo precisa entrar de novo.
- **Swagger.** Este servidor não tem Swagger nem outra página de documentação da API: não há o que
  desligar. Se um dia for adicionado, deve ficar desligado em produção
  (`SPRINGDOC_SWAGGERUI_ENABLED=false`, porque o Spring lê a variável sem os hífens do nome).
- **Sem limite de tentativas de login.** Nada impede alguém de testar senhas sem parar (força bruta).
  Use senhas longas e não reaproveite a senha de outro serviço.
