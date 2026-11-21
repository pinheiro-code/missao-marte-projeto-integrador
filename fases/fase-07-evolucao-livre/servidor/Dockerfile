# Etapa 1: compila o JAR com o JDK 17 e o wrapper do projeto.
FROM eclipse-temurin:17-jdk AS build
WORKDIR /fonte
COPY . .
# O gradlew está no git sem o bit de execução; chamado pelo sh, não precisa dele.
RUN sh gradlew bootJar --no-daemon

# Etapa 2: só o JRE 17 e o JAR, rodando com um usuário sem privilégio.
FROM eclipse-temurin:17-jre
RUN groupadd --system missaomarte && useradd --system --gid missaomarte missaomarte \
    && mkdir /dados && chown missaomarte:missaomarte /dados
WORKDIR /app
COPY --from=build /fonte/build/libs/app.jar app.jar
# Escuta em todas as interfaces dentro do contêiner; o banco H2 fica no volume.
# O segredo do token só entra na execução (variável de ambiente), nunca na imagem.
ENV MISSAOMARTE_ENDERECO=0.0.0.0 \
    MISSAOMARTE_DADOS=/dados
USER missaomarte
VOLUME /dados
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
