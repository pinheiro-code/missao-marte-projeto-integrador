#!/usr/bin/env bash
# Roteiro da API (passo 4 do roteiro da fase 5): chama o servidor que JÁ está no ar e confere o status
# de cada passo. Não sobe nada e não imprime senha nem token. Sai com 1 se algum passo falhar.
# Uso: bash docs/roteiro-api.sh [endereço]   (padrão: http://127.0.0.1:8080)
set -u

API="${1:-http://127.0.0.1:8080}"
NOME="piloto$(date +%s)"
SENHA="${MISSAOMARTE_SENHA_ROTEIRO:-senha-do-roteiro-1}"
CORPO="$(mktemp)"
trap 'rm -f "$CORPO"' EXIT
FALHAS=0

# chamar MÉTODO ROTA [JSON] [TOKEN]: devolve o status HTTP; o corpo fica em $CORPO.
chamar() {
    local argumentos=(-s -o "$CORPO" -w '%{http_code}' -X "$1" "$API$2")
    if [ -n "${3:-}" ]; then
        argumentos+=(-H 'Content-Type: application/json' --data "$3")
    fi
    if [ -n "${4:-}" ]; then
        argumentos+=(-H "Authorization: Bearer $4")
    fi
    curl "${argumentos[@]}"
}

# conferir DESCRIÇÃO ESPERADO OBTIDO
conferir() {
    if [ "$2" = "$3" ]; then
        echo "ok     $1: $3"
    else
        echo "FALHA  $1: esperado $2, veio $3"
        FALHAS=$((FALHAS + 1))
    fi
}

credenciais() {
    echo "{\"nome\":\"$1\",\"senha\":\"$2\"}"
}

echo "Servidor: $API  jogador: $NOME"

conferir "cadastro" 201 "$(chamar POST /api/usuarios "$(credenciais "$NOME" "$SENHA")")"
conferir "mesmo nome com outra caixa" 409 "$(chamar POST /api/usuarios "$(credenciais "${NOME^^}" "$SENHA")")"
conferir "nome curto" 400 "$(chamar POST /api/usuarios "$(credenciais ab "$SENHA")")"

conferir "login com senha errada" 401 "$(chamar POST /api/auth/login "$(credenciais "$NOME" senha-errada-1)")"
SENHA_ERRADA="$(cat "$CORPO")"
conferir "login com nome inexistente" 401 "$(chamar POST /api/auth/login "$(credenciais "ninguem$(date +%s)" "$SENHA")")"
if [ "$SENHA_ERRADA" = "$(cat "$CORPO")" ]; then MESMO=sim; else MESMO=nao; fi
conferir "mesma resposta para senha errada e nome inexistente" sim "$MESMO"

conferir "login correto" 200 "$(chamar POST /api/auth/login "$(credenciais "$NOME" "$SENHA")")"
TOKEN="$(sed -n 's/.*"token":"\([^"]*\)".*/\1/p' "$CORPO")"
if [ -n "$TOKEN" ]; then RECEBIDO=sim; else RECEBIDO=nao; fi
conferir "token recebido" sim "$RECEBIDO"

conferir "ranking sem token" 401 "$(chamar GET /api/ranking)"
conferir "ranking com token inválido" 401 "$(chamar GET /api/ranking "" isto-nao-e-um-token)"

PARTIDA='{"dificuldade":"MEDIO","resultado":"VITORIA","pontuacao":150,"duracaoSegundos":95,"movimentos":40}'
conferir "registro de partida" 201 "$(chamar POST /api/partidas "$PARTIDA" "$TOKEN")"
INVALIDA='{"dificuldade":"IMPOSSIVEL","resultado":"VITORIA","pontuacao":150,"duracaoSegundos":95,"movimentos":40}'
conferir "partida com dificuldade inválida" 400 "$(chamar POST /api/partidas "$INVALIDA" "$TOKEN")"

conferir "histórico" 200 "$(chamar GET '/api/partidas/minhas?pagina=0&tamanho=10' "" "$TOKEN")"
if grep -q '"resultado":"VITORIA"' "$CORPO"; then NO_HISTORICO=sim; else NO_HISTORICO=nao; fi
conferir "partida no histórico" sim "$NO_HISTORICO"

conferir "ranking" 200 "$(chamar GET /api/ranking "" "$TOKEN")"
if grep -q "\"nome\":\"$NOME\"" "$CORPO"; then NO_RANKING=sim; else NO_RANKING=nao; fi
conferir "jogador no ranking" sim "$NO_RANKING"

echo "Falhas: $FALHAS"
[ "$FALHAS" -eq 0 ]
