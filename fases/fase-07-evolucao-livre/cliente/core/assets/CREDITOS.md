# Créditos dos assets

Todas as imagens (`imagens/*.png`) e todos os sons (`sons/*.wav`) desta pasta foram **gerados por código** pela equipe, com o programa `tools/GeradorDeAssets.java` da fase 4 (Java puro: `java.awt` para desenhar, `ImageIO` para gravar o PNG e um gravador de WAV PCM escrito no próprio arquivo).

- Autor: equipe do Projeto Integrador Missão Marte (Arthur Pinheiro e Rafael), Unifor, 2026.2.
- Origem: gerados, sem arte nem som de terceiros e sem copiar arquivos do referencial da disciplina.
- Licença: mesma do repositório do projeto.

Para mudar um asset, mude o gerador e rode, na pasta da fase:

```bash
java tools/GeradorDeAssets.java
```

O gerador sobrescreve todas as imagens e todos os sons desta pasta e sempre produz os mesmos bytes, então nenhum asset é editado à mão.
