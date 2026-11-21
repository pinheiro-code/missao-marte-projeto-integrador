# Fase 6: Android, toque e servidor publicável

O jogo da fase 5 ganhou um módulo Android, controles por toque, o endereço do servidor configurável dentro do
jogo e um servidor pronto para ser publicado (Wi-Fi local ou contêiner na nuvem). As regras do jogo não mudaram:
o pacote `domain` é idêntico ao da fase 5 (mesmo hash, `docs/tutorial.md` seção 8.6).

| pasta | o que é | build |
|---|---|---|
| `servidor/` | o servidor da fase 5 com `GET /api/saude`, porta pela variável `PORT`, `app.jar`, `Dockerfile` e `DEPLOY.md` | Gradle 9.8.2, wrapper próprio |
| `cliente/` | `core` (o jogo), `lwjgl3` (desktop), `android` (aparelho) e `tools` (gerador do ícone) | Gradle 8.7, wrapper próprio |
| `docs/` | `tutorial.md` (seções 8.1 a 8.8 e roteiro de 15 passos) e `capturas/` | |

## Requisitos

- JDK 17 no `JAVA_HOME` **e** na frente do `PATH`.
- Para o desktop: placa de vídeo com OpenGL 2.0 ou superior.
- Para o APK: Android SDK com a plataforma 35 (`platforms/android-35`) e as licenças aceitas por quem instala.
  O caminho vai no `cliente/local.properties` (`sdk.dir=C:/Users/<você>/AppData/Local/Android/Sdk`, com barras
  normais; o arquivo fica fora do Git) ou na variável `ANDROID_HOME`. **Sem o SDK**, o `settings.gradle` imprime um
  aviso e deixa o módulo `android` fora do build: `core` e `lwjgl3` compilam e testam normalmente, e
  `:android:assembleDebug` responde "project 'android' not found".

## Comandos

No PowerShell use `.\gradlew.bat`; no Git Bash, `./gradlew`. No PowerShell 5.1, ponha `-D...` e `-P...` entre aspas: sem elas o valor é cortado no primeiro ponto.

```powershell
# servidor (deixe este terminal aberto)
cd servidor
.\gradlew --version                     # Gradle 9.8.2 e JVM 17
.\gradlew test                          # 52 testes
.\gradlew bootRun --console=plain       # http://localhost:8080
.\gradlew bootJar                       # build/libs/app.jar

# cliente (outro terminal)
cd cliente
.\gradlew --version                     # Gradle 8.7 e JVM 17
.\gradlew clean build                   # 269 testes, sem precisar do SDK
.\gradlew :lwjgl3:run --console=plain   # desktop, igual à fase 5
.\gradlew :lwjgl3:run "-Dmissaomarte.toque=true" "-Dmissaomarte.janela=1200x540" --console=plain
.\gradlew :android:assembleDebug        # só com o SDK: android/build/outputs/apk/debug/android-debug.apk
.\gradlew :android:assembleDebug "-PservidorPadrao=http://192.168.0.10:8080"
```

Instalar no celular (com a depuração USB ligada): `adb install -r android\build\outputs\apk\debug\android-debug.apk`.
No emulador, o computador é `10.0.2.2`; com cabo, `adb reverse tcp:8080 tcp:8080` deixa o celular usar
`localhost:8080`. Detalhes de rede e de nuvem: [servidor/DEPLOY.md](servidor/DEPLOY.md).

## Opções

| opção | onde | padrão | para quê |
|---|---|---|---|
| `-Dmissaomarte.toque=true` | desktop | desligado | mostra os botões virtuais; o mouse faz o papel do dedo |
| `-Dmissaomarte.janela=LxA` | desktop | `800x600` | abre a janela no tamanho pedido (ex.: `1200x540` paisagem, `540x960` retrato, `800x600` 4:3); inválido vale 800x600 |
| `-Dmissaomarte.servidor=http://host:porta` | desktop | `http://localhost:8080` | padrão do servidor; o endereço salvo no jogo ganha dele |
| `-Dmissaomarte.semente=4` | desktop | sorteio livre | mesmo mapa e mesmos inimigos na primeira partida |
| `-Dmissaomarte.semaudio=true` | desktop | com som | máquina sem dispositivo de som |
| `-PservidorPadrao=http://host:porta` | build do APK | `http://localhost:8080` | padrão do servidor no APK (`BuildConfig.SERVIDOR_PADRAO`); a forma é validada na raiz do build e um valor inválido faz o build falhar, mesmo sem o SDK |

Precedência do endereço: o salvo no jogo (opção 6) ganha do padrão do lançador, que ganha de
`http://localhost:8080`. Variáveis do servidor (`MISSAOMARTE_ENDERECO`, `MISSAOMARTE_PORTA`, `PORT`,
`MISSAOMARTE_DADOS`, `MISSAOMARTE_JWT_SEGREDO`): [servidor/DEPLOY.md](servidor/DEPLOY.md).

## Controles

O teclado continua como na fase 5 (tabela no README da fase 5), com três novidades: a opção `6` (Servidor) nos
dois menus, `F3` na tela de entrar (abre a configuração do servidor) e o controle Bluetooth (o direcional move,
`A` embarca, `B` aborta; mapeado e testado por unidade, não verificado com controle real).

| tela | toque (Android ou `-Dmissaomarte.toque=true`) | teclado na configuração do servidor |
|---|---|---|
| Menu | um botão por opção, de 1 a 6 | `1` a `6` |
| Nova partida | Nome (abre a caixa de texto no celular), `< Dificuldade`, `Dificuldade >`, Decolar, Voltar | |
| Partida | setas em cruz com Embarcar no meio, Som e Abortar em cima, na coluna à direita do mapa; segurar uma seta repete o movimento | |
| Fim da partida | Continuar e, se o envio falhou, Reenviar | |
| Ranking e histórico | Recarregar e Voltar | |
| Entrar | Nome, Senha (caixa de senha no celular, sempre vazia), Criar conta ou Já tenho conta, Servidor, Entrar, Voltar | |
| Servidor | Endereço, Porta, HTTPS, Testar conexão, Salvar, Restaurar padrão, Voltar | `Tab` ou setas trocam o campo, `F2` HTTPS, `F5` testa, `Enter` salva, `F8` restaura, `Esc` volta |

Tecla **Voltar** do Android: no menu fecha o jogo; na partida aborta (o mesmo caminho do `Esc`); no fim da
partida faz o mesmo que Continuar (e espera o envio, como o `Enter`); nas outras telas faz o mesmo que o `Esc`.
No desktop o `Esc` não mudou.

Salvar ou restaurar um endereço diferente do atual encerra a sessão logada (o token vale só no servidor que o
emitiu). HTTP fora da rede local mostra um aviso. Só o endereço, a porta e o HTTPS são gravados
(`Preferences` com o nome `missaomarte`; no desktop, `~/.prefs/missaomarte`); senha, token e nome da conta
nunca são gravados.

## Limitações e divergências

- **Sem Android SDK nesta máquina:** o APK não foi gerado nem instalado; o que depende do SDK ou de aparelho está
  em `docs/tutorial.md` seção 8.7, com os comandos para verificar.
- **Orientação:** o aparelho fica em paisagem (`sensorLandscape`, obrigação do enunciado da fase 6). A issue #11
  pede "retrato e paisagem": o layout cabe em retrato (teste com 1060x1884 e janela 540x960 no desktop, captura
  `f6-19`), mas o aparelho não gira para retrato. Liberar é trocar uma linha do manifesto.
- **Mundo de 1060x600 no modo toque:** o enunciado dá 800x600 como exemplo; no modo toque o `ExtendViewport` usa
  1060x600 (800 do jogo mais 260 da coluna de controles) para os botões nunca cobrirem o mapa. Sem toque continua
  `FitViewport(800, 600)`, como na fase 5.
- Em retrato tudo fica pequeno (botões abaixo dos 48 dp recomendados num celular).
- O relógio da partida continua contando com o jogo em segundo plano.
