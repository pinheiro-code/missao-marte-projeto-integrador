# Missão Marte: Projeto Integrador

Projeto Integrador de Arquitetura de Sistemas (Unifor, 2026.2). A equipe evolui o jogo **Missão Marte Unifor**, que nasceu como um programa Java de console, em fases: refatoração em camadas, build com Gradle, interface gráfica com libGDX, servidor de ranking com Spring Boot e versão Android.

Cada fase fica numa pasta própria em `fases/`. Uma fase nova nunca altera as anteriores.

- Equipe: Arthur Pinheiro ([@pinheiro-code](https://github.com/pinheiro-code)) e Rafael ([@rafaeldavidsantana](https://github.com/rafaeldavidsantana))
- Quadro Kanban: GitHub Project "Missão Marte - Kanban" (aba Projects deste repositório)

## Fases

| Fase | Pasta | O que é | Sprint | Situação |
|---|---|---|---|---|
| 0 | `fases/fase-00-console` | Jogo de console original, sem alteração | 1 | pronta |
| 1 | `fases/fase-01-refatoracao` | Refatoração em camadas (domain, application, console, infrastructure) | 1 | em andamento |
| 2 | `fases/fase-02-gradle` | Build, testes e execução pelo Gradle Wrapper | 1 | em andamento |
| 3 e 4 | a definir | libGDX e interface gráfica | 2 | backlog |
| 5 | a definir | Servidor de ranking (Spring Boot) | 3 | backlog |
| 6 | a definir | Android | 4 | backlog |
| 7 | a definir | Melhoria escolhida pela equipe | 4 | backlog |

## Requisitos

- JDK 17 (fases 1 em diante compilam para Java 17).
- Git Bash ou PowerShell.

## Como rodar

### Fase 0

Na pasta `fases/fase-00-console`:

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

O `ranking.json` é criado na pasta de onde o jogo foi iniciado (ignorado pelo Git).

As outras fases têm o próprio README com os comandos.

## Como a equipe trabalha

- Cada issue tem uma branch `mm-NN-<assunto>` e um pull request com `Closes #N`.
- A issue vai para "Em revisão" quando o PR abre e para "Concluído" só quando outro integrante revisa e faz o merge.
- Commits pequenos, no formato `tipo(escopo): descrição`.

## Divergências entre as fontes

O enunciado do AVA, o arquivo de issues do professor (ISS) e os enunciados das fases nem sempre batem. Onde divergem, seguimos o AVA, que é o enunciado da disciplina:

| Assunto | O que diz cada fonte | O que seguimos |
|---|---|---|
| Colunas do quadro | ISS tem 5 (com "Pronto"); AVA tem 4 | 4 colunas: Backlog, Em andamento, Em revisão, Concluído |
| Milestones | ISS pede um por fase; AVA pede por calendário | um por sprint (Sprint 1 a Sprint 5) |
| Campo "Sprint" no quadro | ISS sugere um campo Iteration | não criamos: a sprint já está no milestone |
| Testes na fase 1 | ISS (MM-02) cobra testes automatizados; o enunciado da fase 2 diz que a fase 1 não tem testes | os testes nascem na fase 2, ainda na Sprint 1 |
