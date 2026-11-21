# Fase 1: refatoração em camadas

O jogo de console da fase 0, reorganizado em camadas sem mudar as regras (as mudanças intencionais estão no tutorial).

| Pacote | Papel |
|---|---|
| `domain` | Entidades e regras: nave, passageiros, perigos, base, missão, posição, dificuldade, ação |
| `application` | Partida (`GameSession`), criação do cenário (`MissaoFactory`) e regras do ranking |
| `console` | Menu, leitura do teclado e desenho do mapa |
| `infrastructure` | Ranking gravado em `ranking.json` |

## Requisitos

JDK 17 ou mais novo (o script compila com `--release 17`).

## Compilar e rodar

PowerShell, nesta pasta:

```powershell
.\build.ps1          # compila em out/
.\build.ps1 -Run     # compila e joga
```

Sem o script, no Git Bash:

```bash
javac --release 17 -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out br.edu.unifor.missaomarte.Main
```

Para repetir o mesmo cenário, passe uma semente: `java -Dmissaomarte.semente=4 -cp out br.edu.unifor.missaomarte.Main`.

O `ranking.json` é gravado na pasta de onde o jogo foi iniciado (ignorado pelo Git).

## Documentação

- [Tutorial](docs/tutorial.md): o que mudou, SOLID, GRASP, padrões, diagramas, limitações e o roteiro de testes com a saída real.
- `docs/entrada-*.txt`: entradas usadas no roteiro; `docs/evidencias/`: saídas completas.
