# Escopo da melhoria: cor da nave (fase 7)

Escrito antes de qualquer código da fase 7 (issue MM-13). A base é a fase 6 da equipe, copiada para
`fases/fase-07-evolucao-livre/` sem mudança.

## O que muda para o jogador

- Na **Nova partida**, o jogador escolhe a cor da nave: a tecla `Tab` passa para a próxima cor (as setas já
  mudam a dificuldade e as letras são o nome); no modo toque, o botão **Cor** faz o mesmo.
- A tela mostra uma nave de exemplo já pintada na cor escolhida e o nome da cor.
- Na partida, a nave aparece na cor escolhida. O propulsor, os passageiros, os perigos e a base não mudam.
- A escolha fica guardada: na próxima partida, e depois de fechar e abrir o jogo, a Nova partida já começa na
  última cor usada.
- A primeira cor da lista é a original do sprite (sem tinta): quem nunca escolheu vê o jogo igual à fase 6.

Nada de regra muda: pontuação, mapa, sorteios, inimigos, vidas, servidor e ranking ficam iguais.

## Camadas e classes afetadas

| camada | classe | novo ou alterado | o quê |
|---|---|---|---|
| application | `application/preferencias/CorDaNave` | novo | enum com as cores (nome em pt-BR e componentes RGB de 0 a 1); `proxima()` dá a volta; a primeira é a original |
| application | `application/preferencias/PreferenciaDaNave` | novo | lê e grava a chave `nave.cor` no `ArmazemDeTexto`; ausente ou inválida volta à primeira cor |
| gdx | `NovaPartidaTela` | alterado | tecla `Tab`, botão "Cor", amostra da nave, grava a escolha ao trocar |
| gdx | `MundoRenderer` | alterado | tinta só no sprite da nave (`batch.setColor` antes e a cor anterior do batch depois) |
| gdx | `PartidaTela` | alterado | lê a cor guardada ao começar a partida e a passa ao `MundoRenderer` |
| gdx | `MissaoMarteGame` | alterado | cria a `PreferenciaDaNave` no `create`, sobre o mesmo armazém do endereço do servidor, e a entrega às telas |
| domain | nenhuma | | a cor não é regra do jogo |
| infrastructure | nenhuma | | o armazenamento já existe (Preferences da fase 6) |

Sem libGDX em `application/preferencias` (a cor é guardada como números, e a `Color` do libGDX só aparece no
`gdx`). Sem assets novos: a tinta é aplicada por código sobre o sprite existente.

## Pontos de extensão usados

- **Porta `ArmazemDeTexto` (fase 6):** a preferência grava pela mesma porta que guarda o endereço do servidor,
  com o adaptador `PreferenciasDoJogo` já pronto; nos testes, um armazém em memória.
- **Desenho pelo símbolo no `MundoRenderer`:** a nave já é desenhada num método próprio (`desenharNave`), então a
  tinta entra num lugar só, sem tocar no `domain` nem no `getSimbolo()`.
- **Botões do modo toque (`LayoutDeOpcoes`, fase 6):** o botão "Cor" é mais uma linha da Nova partida.

Ponto que não serve como está: `ArmazemDeTexto` mora em `application/servidor`, um nome que diz "servidor".
A preferência da nave usa a porta mesmo assim (mover a interface mudaria arquivos da fase 6 sem ganho); o
acoplamento de nome fica registrado no tutorial.

## Critérios de aceite

1. `Tab` (teclado) e o botão "Cor" (toque) passam para a próxima cor; depois da última vem a primeira.
2. A amostra da Nova partida e a nave na partida usam a cor escolhida; nada mais muda de cor.
3. A cor escolhida volta na próxima partida e depois de fechar e abrir o jogo (chave `nave.cor` nas Preferences).
4. Valor ausente ou inválido em `nave.cor` vale a primeira cor (a original), sem erro.
5. O modo por turnos, a semente 4 e o layout 800x600 sem toque se comportam como na fase 6 (fora a linha nova
   da cor na Nova partida).
6. Os testes da fase 6 passam sem alteração; o servidor fica idêntico (mesmo conteúdo).
7. `domain` e `infrastructure` idênticos à fase 6; em `application` só o pacote novo; `com.badlogic` fora de
   `gdx`, `lwjgl3` e `android` continua vazio.

## Casos de teste

| teste | casos |
|---|---|
| `CorDaNaveTest` | ordem das cores; `proxima()` dá a volta; a primeira é a original (sem tinta, RGB 1,1,1); nomes em pt-BR; RGB entre 0 e 1 |
| `PreferenciaDaNaveTest` | sem valor gravado vale a primeira; salvar e ler; valor inválido (texto qualquer, vazio) volta à primeira; grava só a chave `nave.cor` |
| `PartidaSemente4Test` | compatibilidade: a sequência `wwdwdwewedsdseaasasasasasasedwdwdw` (F7, seção 7) com `Random(4)`, mapa 5 e fácil dá o mesmo resultado medido na base copiada, antes de qualquer mudança (escrito no commit seguinte a este, sem alteração depois) |

## Análise de impacto

- **Modo de jogo:** nenhum efeito. A cor não entra na `GameSession`, na `Missao` nem no `Random` (nenhum sorteio
  novo), então os cenários da mesma semente continuam iguais. O `PartidaSemente4Test` protege isso.
- **Testes existentes:** os 267 testes do cliente da fase 6 continuam sem alteração; a tela e o renderer não têm
  teste de unidade (usam libGDX), e a lógica nova fica em classes sem libGDX, com teste.
- **Arquitetura:** a regra da cor (ordem, volta, padrão, leitura e gravação) fica em `application`; a tela só
  traduz a tecla e o toque em `proxima()` e a cor em tinta. Nada de `com.badlogic` fora de `gdx`.
- **Servidor:** não muda. A cor é só visual e local; o ranking não sabe dela.
- **Preferences:** ganha a chave `nave.cor`; continuam sem senha, token ou nome de conta.
