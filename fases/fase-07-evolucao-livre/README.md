# Fase 7: melhoria própria, cor da nave

A base é a fase 6 da equipe (Android, toque, servidor configurável), copiada sem mudança. A melhoria escolhida é
a **personalização da cor da nave**: na Nova partida o jogador escolhe a cor, vê uma nave de exemplo já pintada, e
a escolha fica guardada para as próximas partidas, inclusive depois de fechar o jogo. Nenhuma regra muda:
pontuação, mapa, sorteios, inimigos, vidas, servidor e ranking ficam como na fase 6.

| pasta | o que é | build |
|---|---|---|
| `servidor/` | o servidor da fase 6, idêntico (`diff -r` vazio) | Gradle 9.8.2, wrapper próprio |
| `cliente/` | a fase 6 com a melhoria: `CorDaNave` e `PreferenciaDaNave` em `application/preferencias`, e a tela, o desenho e o jogo em `gdx` | Gradle 8.7, wrapper próprio |
| `docs/` | `escopo-da-melhoria.md` (escrito antes do código), `tutorial.md` (seções 8.1 a 8.6 e roteiro de 11 passos) e `capturas/` | |

## Requisitos

Os mesmos da fase 6: JDK 17 no `JAVA_HOME` e na frente do `PATH`; OpenGL 2.0 para o desktop; Android SDK com a
plataforma 35 só para gerar o APK (sem o SDK, o módulo `android` fica fora do build e o resto compila e testa).

## Comandos

No PowerShell use `.\gradlew.bat`; no Git Bash, `./gradlew`. No PowerShell 5.1, ponha `-D...` entre aspas.

```powershell
# servidor (só para o modo logado e o ranking global; a cor da nave funciona sem ele)
cd servidor
.\gradlew test                          # 52 testes
.\gradlew bootRun --console=plain       # http://localhost:8080

# cliente
cd cliente
.\gradlew clean build                   # 281 testes, sem precisar do SDK
.\gradlew :lwjgl3:run --console=plain   # desktop 800x600, sem toque
.\gradlew :lwjgl3:run "-Dmissaomarte.toque=true" "-Dmissaomarte.janela=1200x540" --console=plain
```

As opções `-Dmissaomarte.*` e `-PservidorPadrao` são as da fase 6 (tabela no README da fase 6).

## Controles novos

| onde | teclado | toque (Android ou `-Dmissaomarte.toque=true`) |
|---|---|---|
| Nova partida | `Tab` passa para a próxima cor (as setas continuam mudando a dificuldade e as letras são o nome) | botão **Cor da nave: <cor>**, entre a dificuldade e o Decolar |

As cores, na ordem: Original (o sprite sem tinta), Vermelha, Verde, Azul, Amarela e Rosa; depois da Rosa volta a
Original. A cor é gravada a cada troca na chave `nave.cor` das Preferences `missaomarte` (no desktop,
`~/.prefs/missaomarte`), a mesma do endereço do servidor; nada de senha, token ou nome da conta. Um valor apagado
ou estragado vale a Original, sem erro. Na partida, só o desenho da nave muda de cor: o propulsor, os
passageiros, os perigos, a base e o painel ficam iguais.

O resto dos controles é o da fase 6.

## Limitações

- **Sem Android SDK nesta máquina:** o APK não foi gerado nem instalado (`docs/tutorial.md`, 8.5).
- A cor é só local e visual: o servidor e o ranking não sabem dela.
- A nave do menu principal continua com a cor original (a escolha aparece na Nova partida e na partida).
- Mais limitações e próximos passos em `docs/tutorial.md`, 8.6.
